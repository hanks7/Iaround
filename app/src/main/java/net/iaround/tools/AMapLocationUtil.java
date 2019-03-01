
package net.iaround.tools;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @ClassName: AMapLocationUtil
 * @Description: 高德地图定位工具
 * @date 2014-7-21 下午2:56:33
 */
public class AMapLocationUtil {
    // 日志的tag标记
    private final String TAG = AMapLocationUtil.class.getName() + "_";
    // 静态单例对象
    private static AMapLocationUtil mAmapLocationUtil;
    // 是否监听
    private boolean mIsListener = false;
    // 超时检测的timer
    private Timer mTimer;
    // 上下文对象
    private Context mContext;
    // 是否第一次获取
    private boolean mIsFirst = false;
    // 定位管理的代理类
    private LocationManagerProxy mAMapLocManager = null;
    // 定位的监听器
    private AMapLocationListener mAmapLocationListener;

    // 定位到的纬度
    private int mLat;
    // 定位到的经度
    private int mLng;
    // 定位到的地址
    private String mAddress;

    /**
     * 定位回调接口列表
     */
    private List<AMapCallBack> mCallBacks;


    /**
     * 构造函数 （对参数及变量进行初始化）
     *
     * @param context
     */
    public AMapLocationUtil(Context context) {
        this.mContext = context.getApplicationContext();
        mAMapLocManager = LocationManagerProxy.getInstance(mContext);

        // 支持首选gps定位
        mAMapLocManager.setGpsEnable(true);
        mCallBacks = new ArrayList<AMapCallBack>();
        // 初始化监听器
        initAMapLocationListener();
    }

    /**
     * @param context
     * @return AMapLocationUtil
     * @Title: getInstance
     * @Description: 获取AMap定位工具的实例
     */
    public static AMapLocationUtil getInstance(Context context) {
        if (mAmapLocationUtil == null) {
            mAmapLocationUtil = new AMapLocationUtil(context);
        }
        return mAmapLocationUtil;
    }

    /**
     * @param aMapCallBack
     * @Title: setCallBack
     * @Description: 设置AMap的定位监听回调
     */
    public void setCallBack(AMapCallBack aMapCallBack) {
        mCallBacks.add(aMapCallBack);
    }

    /**
     * @Title: initAMapLocationListener
     * @Description: 初始化定位的监听器
     */
    private void initAMapLocationListener() {
        mAmapLocationListener = new AMapLocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // 暂时无用
                CommonFunction.log("AMapLocationUtil", "onStatusChanged() provider=" + provider + ", status=" + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                // 暂时无用
                CommonFunction.log("AMapLocationUtil", "onProviderEnabled() provider=" + provider);

            }

            @Override
            public void onProviderDisabled(String provider) {
                // 暂时无用
                CommonFunction.log("AMapLocationUtil", "onProviderDisabled() provider=" + provider);

            }

            @Override
            public void onLocationChanged(Location location) {
                // 暂时无用

            }

            @Override
            public void onLocationChanged(AMapLocation location) {
                if (location != null) {
                    mLat = (int) (location.getLatitude() * 1E6);
                    mLng = (int) (location.getLongitude() * 1E6);

                    final String strCity = location.getCity();
                    final String strprovince = location.getProvince();
                    final String strRegion = location.getDistrict();

                    Bundle locBundle = location.getExtras();
                    CommonFunction.log("AMapLocationUtil", "onLocationChanged() city=" + location.getCity() + ";getDistrict=" + location.getDistrict() + ";getFloor" + location.getFloor() + ";AdCode" + location.getAdCode());
                    mAddress = locBundle.getString("desc");
                    if (mCallBacks != null && mCallBacks.size() > 0) {
                        if (locBundle != null) {
                            // 如果未获取到地址，则去google再取一次
                            if (CommonFunction.isEmptyOrNullStr(mAddress)) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        String strAddr = GoogleLocationUtil.googleAddress(mLat, mLng);
                                        LocationUtil.setGeo(mContext, mLat, mLng, strAddr);

                                    }
                                }).start();
                            }
                            /*
                             * else { mIsFirst = true; mAddress =
                             * ","+location.getProvider
                             * ()+","+location.getCity()+
                             * ","+location.getDistrict(); }
                             */

                            int type = 1;
                            if (mLat == 0 && mLng == 0) {
                                type = 0;
                            }
                            String simple = location.getCity() + " " + location.getDistrict();

                            update(type, mLat, mLng, mAddress, simple, strCity);

                        }
                    }
                    // 定位成功后移除监听器
                    LocationUtil.setGeo(mContext, mLat, mLng, mAddress);
                    LocationUtil.setGeo(strprovince, strCity, strRegion, location.getCountry());
                    mAMapLocManager.removeUpdates(mAmapLocationListener);

                }
            }
        };
    }

    /**
     * @param type
     * @Title: update
     * @Description: 更新回调
     */
    private void update(int type, int lat, int lng, String address, String simpleAddress, String city) {
        removeListener();
        if (mCallBacks != null && mCallBacks.size() > 0) {
            for (AMapCallBack callback : mCallBacks) {
                if (callback != null) {
                    CommonFunction.log("AMapLocationUtil", "get location info latlng:" + lat + "," + lng + " address:" + address);
                    callback.updateAmap(type, lat, lng, address, simpleAddress, city);
                }
            }
            mCallBacks.clear();
        }
    }

    /**
     * @param aMapCallback 定位回调接口
     * @param type
     * @Title: startAMapListener
     * @Description: 开始定位
     */
    public void startAMapListener(AMapCallBack aMapCallback, final int type) {
        setCallBack(aMapCallback);
        CommonFunction.log(TAG, "start amap listener");
        if (!mIsListener) {
            // 首先移除监听器
            removeListener();
            mIsListener = true;
            // 初始化超时检测的timer
            try {
                /**
                 * 开启定位请求 参数说明： 1：定位的provider 2：定位最短时间（毫秒） 3：定位距离间隔单位（米）
                 */
                mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 300 * 1000, 0, mAmapLocationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mTimer = new Timer(true);
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    update(0, 0, 0, "", "", "");
                }
            }, 10 * 1000, 9 * 1000);
        }
    }

    /**
     * @Title: removeListener
     * @Description: 移除定位监听
     */
    public void removeListener() {
        mIsListener = false;
        mAMapLocManager.removeUpdates(mAmapLocationListener);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    /**
     * @author zhonglong kylin17@foxmail.com
     * @ClassName: AMapCallBack
     * @Description: 定位监听的接口回调
     * @date 2013-10-16 下午3:18:50
     */
    protected interface AMapCallBack {

        /**
         * @param type    0为获取失败,1为获取成功
         * @param lat     获取到的纬度
         * @param lng     获取到的经度
         * @param address 获取到地址信息
         * @Title: updateAmap
         * @Description: 定位回调
         */
        void updateAmap(int type, int lat, int lng, String address, String simpleAddress, String city);
    }

    /**
     * 移除监听
     *
     * @return void
     */
    public void removeLister() {
//		isListener = false;
//		locationManager.removeUpdates( locationListener );
//		if ( timer != null )
//		{
//			timer.cancel( );
//			timer = null;
//		}
    }

}
