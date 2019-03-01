
package net.iaround.tools;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.GoogleLocationUtil.GoogleCallBack;
import net.iaround.tools.AMapLocationUtil.AMapCallBack;

import net.iaround.R;

import org.json.JSONObject;


/**
 * 采用异步获取位置信息,在主线程调用，已经包含baidu,google的获取
 *
 * @author linyg@iaround.net
 * @date 2012-3-2 上午11:42:52
 */
public class LocationUtil implements GoogleCallBack, AMapCallBack {
    private Context context;
    private static int LoadBaiduLocation = -1;
    private static GeoData geoData = null;
    private static int privLat = 0, privLng = 0;

    private int geoType;
    private MLocationListener listener;
    // 地球半径（单位：米）
    private static final double EARTH_RADIUS = 6378137;

    public final int GOOGLE_CALLBACK = 1002;
    public final int START_LOCATION = 1003;
    public final int MSG_SEND_LOCATION = 1004;
    public final int AMAP_CALLBACK = 1005;

    private GoogleLocationUtil googleUtil;
    private AMapLocationUtil amapUtil;
    private MHandler mHandler;

    private static boolean listenning = false; // 是否正在获取
    private static long privListenTime = 0;// 上次获取的时间

    public LocationUtil(Context context) {
        this.context = context;
        googleUtil = GoogleLocationUtil.getInstance(context);
        amapUtil = AMapLocationUtil.getInstance(context);
        mHandler = new MHandler(Looper.getMainLooper());
    }

    /**
     * 启动经纬读获取，并静听返回结果
     *
     * @param mLocationListener
     * @param type              1为获取经纬度以及地址信息，0为仅获取经纬度
     * @return void
     */
    public void startListener(MLocationListener mLocationListener, int type) {
        CommonFunction.log("LocationUtil", "#################startListener###################");
        this.listener = mLocationListener;
        this.geoType = type;

        if (LocationUtil.geoData != null && (System.currentTimeMillis() - LocationUtil.geoData.getCurrentTime()) < 1 * 1000) {

            CommonFunction.log("LocationUtil", "updateLocation ");
            updateLocation(1, LocationUtil.geoData.getLat(),
                    LocationUtil.geoData.getLng(), LocationUtil.geoData.getAddress(),
                    LocationUtil.geoData.getSimpleAddress());
            return;
        }

        if (listenning && System.currentTimeMillis() - privListenTime < 1 * 1000) {
            googleUtil.setCallBack(this);
            amapUtil.setCallBack(this);
            CommonFunction.log("LocationUtil", "listenning...");
            return;
        } else {
            privListenTime = System.currentTimeMillis();
            listenning = true;
            // 开启经纬度读取
            Message msg = new Message();
            msg.what = START_LOCATION;
            msg.arg1 = type;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 获取当前缓存的经纬
     *
     * @return GeoData
     */
    public static GeoData getCurrentGeo(Context context) {

        // 若当前未获取到经纬度，则首先使用历史
        if (LocationUtil.geoData == null
                || (LocationUtil.geoData.getLat() == 0 && LocationUtil.geoData.getLng() == 0)) {
            int lat = SharedPreferenceUtil.getInstance(context).getInt(
                    SharedPreferenceUtil.GEO_LAT);
            int lng = SharedPreferenceUtil.getInstance(context).getInt(
                    SharedPreferenceUtil.GEO_LNG);
            String address = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.GEO_ADDRESS);
            if (lat != 0 && lng != 0) {

                LocationUtil.geoData = new GeoData();
                LocationUtil.geoData.setLat(lat);
                LocationUtil.geoData.setLng(lng);
                LocationUtil.geoData.setAddress(address);
                return LocationUtil.geoData;
            } else {
                CommonFunction.log("LocationUtil", "------获取不到当前的地理位置-------");

                LocationUtil.geoData = new GeoData();
                LocationUtil.geoData.setLat(0);
                LocationUtil.geoData.setLng(0);
                LocationUtil.geoData.setAddress(context.getResources().getString(R.string.address_x));
                return LocationUtil.geoData;
            }
        }


        return LocationUtil.geoData;
    }

    /**
     * 设置当前经纬度
     *
     * @param lat
     * @param lng
     * @param address
     */
    public static void setGeo(Context context, int lat, int lng, String address) {
        if (LocationUtil.geoData == null) {
            LocationUtil.geoData = new GeoData();
        }
        if (lat != 0 && lng != 0) {
            LocationUtil.geoData.setLat(lat);
            LocationUtil.geoData.setLng(lng);
            SharedPreferenceUtil.getInstance(context).putInt(SharedPreferenceUtil.GEO_LAT,
                    lat);
            SharedPreferenceUtil.getInstance(context).putInt(SharedPreferenceUtil.GEO_LNG,
                    lng);
            if (!CommonFunction.isEmptyOrNullStr(LocationUtil.geoData.getAddress())) {
                LocationUtil.geoData.setAddress(address);
                SharedPreferenceUtil.getInstance(context).putString(SharedPreferenceUtil.GEO_ADDRESS, address);
            }
        }
    }

    public static void setGeo(String province, String city, String region, String country) {
        if (LocationUtil.geoData == null) {
            LocationUtil.geoData = new GeoData();
        }
        LocationUtil.geoData.setCountry(country);
        LocationUtil.geoData.setProvince(province);
        LocationUtil.geoData.setCity(city);
        LocationUtil.geoData.setRegion(region);
        CommonFunction.log("LocationUtil", "LocationUtil.setGeo===city=" + LocationUtil.geoData.getCity() + ";strprovince=" + LocationUtil.geoData.getProvince());
    }

    /**
     * 返回获取经纬度的情况
     *
     * @param type    0为获取失败，1为获取到经纬度和地址，2获取到经纬，3获取到地址信息
     * @param lat
     * @param lng
     * @param address
     * @return void
     */
    private void callBack(int type, int lat, int lng, String address, String simpleAddress, String city) {
        //修改定位信息
//        lat = 22330309;//纬度
//        lng = 114221090;//经度
//        address = "香港特别行政區觀塘區";
//        simpleAddress = "香港特别行政區觀塘區";
//        city = "香港特别行政區";

        listenning = false;
        // 获取经纬度不为空，则保存本地
        if (lat != 0 && lng != 0) {
            if (LocationUtil.geoData == null) {
                LocationUtil.geoData = new GeoData();
            }
            LocationUtil.geoData.setLat(lat);
            LocationUtil.geoData.setLng(lng);

            SharedPreferenceUtil.getInstance(context).putInt(SharedPreferenceUtil.GEO_LAT,
                    lat);
            SharedPreferenceUtil.getInstance(context).putInt(SharedPreferenceUtil.GEO_LNG,
                    lng);

            // 获取的是经纬度和地址信息
            if (!CommonFunction.isEmptyOrNullStr(address)) {
                LocationUtil.geoData.setAddress(address);
                SharedPreferenceUtil.getInstance(context).putString(
                        SharedPreferenceUtil.GEO_ADDRESS, address);
            }

            if (!CommonFunction.isEmptyOrNullStr(simpleAddress)) {
                LocationUtil.geoData.setSimpleAddress(simpleAddress);
                SharedPreferenceUtil.getInstance(context).putString(
                        SharedPreferenceUtil.GEO_SIMPLE_ADDRESS, simpleAddress);
            }

            // 比较是否超过200米，超过则发送给服务端
            int distance = calculateDistance(lng, lat, privLng, privLat);
            if (distance > 200 || distance == 0) {
                sendAddressByHttp(lat, lng, address, city);
            }
        }

        if (LocationUtil.geoData != null && LocationUtil.geoData.getLat() != 0
                && LocationUtil.geoData.getLng() != 0) {
            lat = LocationUtil.geoData.getLat();
            lng = LocationUtil.geoData.getLng();
            address = LocationUtil.geoData.getAddress();
            simpleAddress = LocationUtil.geoData.getSimpleAddress();
        } else {
            // 无法获取到经纬度时，将使用本地的磁盘缓存的经纬度
            lat = SharedPreferenceUtil.getInstance(context).getInt(
                    SharedPreferenceUtil.GEO_LAT);
            lng = SharedPreferenceUtil.getInstance(context).getInt(
                    SharedPreferenceUtil.GEO_LNG);
        }

        if (CommonFunction.isEmptyOrNullStr(address)) {
            address = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.GEO_ADDRESS);
        }

        if (CommonFunction.isEmptyOrNullStr(simpleAddress)) {
            simpleAddress = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.GEO_SIMPLE_ADDRESS);
        }
        updateLocation(1, lat, lng, address, simpleAddress);
    }

    // 向上层报告获取到的经纬读
    private void updateLocation(int type, int lat, int lng, String address,
                                String simpleAddress) {
        //CommonFunction.log( "LocationUtil" , "updateLocation" );
        if (this.listener != null) {
            //CommonFunction.log( "LocationUtil" , "updateLocation2" );
            this.listener.updateLocation(type, lat, lng, address, simpleAddress);
            this.listener = null; // 向上层报告之后，后面获取的经纬度将不再向上层报告
        }
    }

    /**
     * 获取经纬度回调接口
     */
    public interface MLocationListener {
        /**
         * 获取回调
         *
         * @param type    0为失败，1为经纬度和地址信息，2为经纬度
         * @param lat
         * @param lng
         * @param address
         * @return void
         */
        void updateLocation(int type, int lat, int lng, String address,
                            String simpleAddress);
    }

    /**
     * 通过google获取到经纬度的返回
     */
    @Override
    public void updateGoogle(int type, int lat, int lng, String address,
                             String simpleAddress, String city) {
        CommonFunction.log("LocationUtil", "updateGoogle() type:" + type + " lat:" + lat + " lng:" + lng + " address:" + address);
        Message msg = new Message();
        LocationItem item = new LocationItem();
        item.setType(type);
        item.setLat(lat);
        item.setLng(lng);
        item.setAddress(address);
        item.setSimpleAddress(simpleAddress);
        item.setCity(city);
        msg.obj = item;
        msg.what = GOOGLE_CALLBACK;
        mHandler.sendMessage(msg);
    }

    @Override
    public void updateAmap(int type, int lat, int lng, String address, String simple, String city) {
        // TODO Auto-generated method stub
        {
            CommonFunction.log("LocationUtil", "updateAmap() type:" + type + " lat:" + lat + " lng:"
                    + lng + " address:" + address);
            Message msg = new Message();
            LocationItem item = new LocationItem();
            item.setType(type);
            item.setLat(lat);
            item.setLng(lng);
            item.setAddress(address);
            item.setSimpleAddress(simple);
            item.setCity(city);
            msg.obj = item;
            msg.what = AMAP_CALLBACK;
            mHandler.sendMessage(msg);
        }
    }

    // 消息处理
    private class MHandler extends Handler {
        private MHandler() {
            super();
        }

        private MHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_LOCATION: {
                    // 根据运营商，若为中国大陆则优先使用baidu获取，若为非中国大陆则使用google优先获取
                    if (PhoneInfoUtil.getInstance(context).isChinaCarrier()
                            && isLoadBaidu()) {
                        CommonFunction.log("LocationUtil", "baidu");
                        amapUtil.startAMapListener(LocationUtil.this, 1);
                    } else {
                        CommonFunction.log("LocationUtil", "google");
                        googleUtil.startGoogleListener(LocationUtil.this, 1);
                    }
                }
                break;
                // 通过google获取到经纬度的回调
                case GOOGLE_CALLBACK: {
                    googleUtil.removeLister();
                    LocationItem googleItem = (LocationItem) msg.obj;
                    callBack(googleItem.getType(), googleItem.getLat(),
                            googleItem.getLng(), googleItem.getAddress(),
                            googleItem.getSimpleAddress(), googleItem.getCity());
                    // 当获取返回是失败
                    if (googleItem.getType() == 0) {
                        // 若为非中国大陆运营商，则首选取经纬度用的是google,则google获取返回失败，那么就开启baidu去获取
                        if (!PhoneInfoUtil.getInstance(context).isChinaCarrier()) {
                            amapUtil.startAMapListener(LocationUtil.this, 1);
                        }
                    }
                }
                break;
                case AMAP_CALLBACK: {
                    amapUtil.removeLister();
                    LocationItem amapItem = (LocationItem) msg.obj;
                    callBack(amapItem.getType(), amapItem.getLat(),
                            amapItem.getLng(), amapItem.getAddress(),
                            amapItem.getSimpleAddress(), amapItem.getCity());
                    // 当获取返回是失败
                    if (amapItem.getType() == 0) {
                        // 若为中国大陆运营商，则首选取经纬度用的是amap,则amap获取返回失败，那么就开启google去获取
                        if (PhoneInfoUtil.getInstance(context).isChinaCarrier()) {
                            googleUtil.startGoogleListener(LocationUtil.this, geoType);
                        }
                    }
                }
                break;
                case MSG_SEND_LOCATION: { // 发送经纬度返回
                    String result = String.valueOf(msg.obj);
                    if (!CommonFunction.isEmptyOrNullStr(result) && !"null".equals(result)) {
                        JSONObject json;
                        try {
                            json = new JSONObject(result);
                            if (json.optInt("status") == 200) {
                                privLat = msg.arg1;
                                privLng = msg.arg2;
                                if (LocationUtil.geoData != null) {
                                    LocationUtil.geoData.setCurrentTime(System.currentTimeMillis());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
    }

    class LocationItem {
        private int type;
        private int lat;
        private int lng;
        private String address;
        private String simpleAddress;
        private String city;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getLat() {
            return lat;
        }

        public void setLat(int lat) {
            this.lat = lat;
        }

        public int getLng() {
            return lng;
        }

        public void setLng(int lng) {
            this.lng = lng;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSimpleAddress() {
            return simpleAddress;
        }

        public void setSimpleAddress(String simpleAddress) {
            this.simpleAddress = simpleAddress;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    /**
     * 计算当前经纬度到目标经纬度的距离
     */
    public static int calculateDistance(Context context, long lat, long lng) {
        GeoData geo = getCurrentGeo(context);
        return calculateDistance(lng, lat, geo.getLng(), geo.getLat());
    }

    /**
     * 球地球表面两坐标的距离
     *
     * @param intlng1
     * @param intlat1
     * @param intlng2
     * @param intlat2
     * @return
     */
    public static int calculateDistance(long intlng1, long intlat1, long intlng2, long intlat2) {
        if (Math.abs(intlat1) > 90000000 || Math.abs(intlng1) > 180000000
                || Math.abs(intlat2) > 90000000 || Math.abs(intlng2) > 180000000) {
            return 0;
        }
        try {
            double lng1 = intlng1 / 1000000d;
            double lat1 = intlat1 / 1000000d;
            double lng2 = intlng2 / 1000000d;
            double lat2 = intlat2 / 1000000d;
            double radLat1 = rad(lat1);
            double radLat2 = rad(lat2);
            double a = radLat1 - radLat2;
            double b = rad(lng1) - rad(lng2);

            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                    + Math.cos(radLat1) * Math.cos(radLat2)
                    * Math.pow(Math.sin(b / 2), 2)));
            s = s * EARTH_RADIUS;
            s = Math.round(s * 10000) / 10000;
            return (int) s;
        } catch (Exception e) {
            CommonFunction.log(e);
            return 0;
        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    /**
     * 根据经纬度获取地址信息
     *
     * @param context
     * @param lat
     * @param lng
     * @param listener
     * @return void
     */
    public static void getAddressByLatLng(final Context context, final int lat,
                                          final int lng, final ListenerAddress listener) {
        if (lat == 0 && lng == 0) {
            if (listener != null) {
                listener.callbackAddress("");
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String address = GoogleLocationUtil.googleAddress(lat, lng);
                if (listener != null) {
                    listener.callbackAddress(address);
                }
            }
        }).start();
    }

    /**
     * 通过http发送经纬度
     *
     * @param lat
     * @param lng
     * @param address
     */
    public void sendAddressByHttp(final int lat, final int lng, final String address, final String city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = BusinessHttpProtocol.sendLoaction(context, lat, lng,
                        address, city);
                Message msg = new Message();
                msg.what = MSG_SEND_LOCATION;
                msg.arg1 = lat;
                msg.arg2 = lng;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    public interface ListenerAddress {
        void callbackAddress(String address);
    }

    /**
     * 停止监听
     */
    public static void stop(Context context) {
//		BaiduLocationUtil.getInstance( context ).removeLister( );
        AMapLocationUtil.getInstance(context).removeListener();
        GoogleLocationUtil.getInstance(context).removeLister();
    }

    // 判断是否可以加载百度定位so文件
    private static boolean isLoadBaidu() {
        if (LoadBaiduLocation == -1) {
            /*
             * try { System.loadLibrary("locSDK3"); } catch (Throwable e) {
             * e.printStackTrace(); LoadBaiduLocation = 0; return false; }
             */
            LoadBaiduLocation = 1;
            return true;
        } else {
            return LoadBaiduLocation == 1;
        }
    }

    /**
     * 获取当前的位置信息
     *
     * @return
     */
    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(privLat);
        location.setLongitude(privLng);
        return location;
    }

}
