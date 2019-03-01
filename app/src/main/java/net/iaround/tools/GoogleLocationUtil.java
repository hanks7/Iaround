package net.iaround.tools;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.BaseApplication;
import net.iaround.model.entity.GeoData;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * google获取经纬度
 *
 * @author linyg@iaround.net
 * @ClassName: GoogleUtil
 * @date 2012-3-2 上午10:46:35
 */
public class GoogleLocationUtil {
    private final String TAG = "GoogleLocationUtil";

    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA： Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;

    private static GoogleLocationUtil googleUtil;
    private LocationManager locationManager;
    private LocationProvider gpsProvider;
    private LocationListener locationListener;
    private CopyOnWriteArrayList<GoogleCallBack> callBacks;
    public final String offset_url = "/user/lnglatset";
    private Timer timer;
    private boolean isListener = false; // 是否正在获取

    private int lattoOffset;
    private int lngtoOffset;
    private int dLatitude;
    private int dLongitude;
    private static String address;
    private static String simpleAddress;
    private static String cityAddrass;

    private GoogleLocationUtil(final Context context) {
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gpsProvider = locationManager.getProvider("gps");
        callBacks = new CopyOnWriteArrayList<GoogleCallBack>();
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (location != null) {
                    CommonFunction.log("GoogleLocationUtil", "google location : " + location);
                    dLatitude = (int) (location.getLatitude() * 1e6);
                    dLongitude = (int) (location.getLongitude() * 1e6);
                    if (callBacks != null && callBacks.size() > 0) {
                        getAddress();
                    }

                    LocationUtil.setGeo(context, dLatitude, dLongitude, address);

                    locationManager.removeUpdates(locationListener);
                }
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
    }

    public static GoogleLocationUtil getInstance(Context context) {
        if (googleUtil == null) {
            googleUtil = new GoogleLocationUtil(context);
        }
        return googleUtil;
    }

    // 设置回调
    public void setCallBack(GoogleCallBack googleCallBack) {
        callBacks.add(googleCallBack);
    }

    /**
     * 开始监听
     *
     * @param type 1为获取经纬度以及地址信息,0为仅获取经纬度
     * @return void
     */
    public void startGoogleListener(GoogleCallBack googleCallBack, final int type) {
        setCallBack(googleCallBack);
        CommonFunction.log(TAG + "startGoogleListener", "isListener:" + isListener);
        if (!isListener) {
            removeLister();
            isListener = true;
            timer = new Timer(true);
            try {
                locationManager.requestLocationUpdates(gpsProvider.getName(), 0, 0,
                        locationListener);
                if (ActivityCompat.checkSelfPermission(BaseApplication.appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(BaseApplication.appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                        0, locationListener);
            } catch (Exception e) {
                // TODO: handle exception
            }
            // 获取超时
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (callBacks != null && callBacks.size() > 0) {
                        update(0);
                    }
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    CommonFunction.log(TAG + "_timer", dLatitude + "," + dLongitude);
                }
            }, 10 * 1000, 11 * 1000);
        }
    }

    // 根据经纬度从google获取地址
    public static String googleAddress(int lat, int lng) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(BaseApplication.appContext) != ConnectionResult.SUCCESS) {
            return "";
        }

        if (!isNetworkAvailable(BaseApplication.appContext)) {
            return "";
        }
        String country = "", province = "", city = "", area = "", row = "", street = "";
        String language = Locale.getDefault().getLanguage();
        try {
            String url = String.format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&language=%s&sensor=false", (lat) / 1000000d, (lng) / 1000000d, language);
            CommonFunction.log("GoogleLocationUtil", "request url=" + url);
            HttpGet httpRequest = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            // 请求成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 取的返回的字符串
                String data = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                CommonFunction.log("GoogleLocationUtil", "response data=" + data);
                JSONObject json = new JSONObject(data);
                if (json != null && json.has("results")) {
                    JSONArray results = json.optJSONArray("results");
                    if (results != null && results.length() > 0) {
                        JSONObject result = results.optJSONObject(0);
                        JSONArray components = result.optJSONArray("address_components");
                        if (components != null && components.length() > 0) {
                            try {
                                for (int i = 0; i < components.length(); i++) {
                                    JSONObject component = components.optJSONObject(i);
                                    String type = "";
                                    JSONArray types = component.optJSONArray("types");
                                    if (types != null && types.length() > 0) {
                                        type = types.optString(0);
                                    }
                                    String long_name = CommonFunction.jsonOptString(component, "long_name");
                                    if (type.equals("country")) {
                                        country = long_name;
                                    } else if (type.equals("administrative_area_level_1")) {
                                        province = long_name;
                                    } else if (type.equals("locality")) {
                                        city = long_name;
                                    } else if (type.equals("sublocality")) {
                                        area = long_name;
                                    } else if (type.equals("route")) {
                                        row = long_name;
                                    } else if (type.equals("street_number")) {
                                        street = long_name;
                                    }
                                }
                            } catch (Exception e) {
                                CommonFunction.log("GoogleLocationUtil", "parse data error, error=" + e.getMessage());
                            }
                        }
                    }
                }
                if (CommonFunction.isEmptyOrNullStr(country)) {
                    country = "";
                }

                address = country + "," + province + "," + city + "," + area + row;
                simpleAddress = country + "," + province + "," + city + "," + area;
                cityAddrass = city;

                LocationUtil.setGeo(province, city, area, country);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CommonFunction.log("GoogleLocationUtil", "googleAddress() 获取地址失败, error=" + e.toString());
        }
        return address;
    }

    /**
     * 获取地址
     *
     * @return void
     */
    private void getAddress() {
        if (dLatitude == 0 && dLongitude == 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//					getOffset( ); // 经纬度矫正
                    address = googleAddress(dLatitude, dLongitude);
                } catch (Exception e) {

                }
                update(1);
            }
        }).start();
    }

    // 回调
    private void update(int type) {
        removeLister();
        if (callBacks != null && callBacks.size() > 0) {
            for (GoogleCallBack item : callBacks) {
                if (item != null) {
                    if (dLatitude != 0 && dLongitude != 0
                            && !CommonFunction.isEmptyOrNullStr(address)) {
                        type = 1;
                    } else {
                        type = 0;
                    }
                    item.updateGoogle(type, dLatitude, dLongitude, address, simpleAddress, cityAddrass);

                }
            }
            callBacks.clear();
        }
    }

    /**
     * 移除监听
     *
     * @return void
     */
    public void removeLister() {
        isListener = false;
        locationManager.removeUpdates(locationListener);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // 回调
    protected interface GoogleCallBack {
        /**
         * 经纬度回调
         *
         * @param type    0为获取失败,1为获取到经纬度和地址,2获取到经纬,3获取到地址信息
         * @param lat
         * @param lng
         * @param address
         * @return void
         */
        void updateGoogle(int type, int lat, int lng, String address,
                          String simpleAddress, String city);
    }


    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager manger = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manger.getActiveNetworkInfo();
            //return (info!=null && info.isConnected());//
            if (info != null) {
                return info.isConnected();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
