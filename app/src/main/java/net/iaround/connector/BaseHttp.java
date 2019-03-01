
package net.iaround.connector;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import net.iaround.conf.ErrorCode;
import net.iaround.tools.CommonFunction;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;


/**
 * http连接
 *
 * @author linyg
 */
public class BaseHttp {
    protected int connectTimeout = 10000;
    protected int readTimeout = 300000;
    protected HttpURLConnection connection = null;

    public static final String CTWAP = "ctwap";
    public static final String CMWAP = "cmwap";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";

    /**
     * 网络不可用
     */
    public static final int TYPE_NET_WORK_DISABLED = 0;
    public static final int TYPE_WIFI = 1;
    /**
     * 移动联通wap10.0.0.172
     */
    public static final int TYPE_CM_CU_WAP = 4;
    /**
     * 电信wap 10.0.0.200
     */
    public static final int TYPE_CT_WAP = 5;
    /**
     * 电信,移动,联通
     */
    public static final int TYPE_OTHER_NET = 6;

    public static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    public String url;

    protected Context context;

    protected BaseHttp(Context context, String url) throws ConnectionException {
        this.url = url;
        this.context = context.getApplicationContext();
        try {
            URL uri = null;
            uri = new URL(url);
            int type = checkNetworkType(context);
            try {
                connection = (HttpURLConnection) uri.openConnection();
            } catch (Exception e) {
                if (type == TYPE_CM_CU_WAP) {
                    // "10.0.0.172", 80
                    connection = (HttpURLConnection) uri.openConnection();
                    try {
                        java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                                new InetSocketAddress("10.0.0.172", 80));
                        connection = (HttpURLConnection) uri.openConnection(p);
                    } catch (Exception e1) {
                        connection = (HttpURLConnection) uri.openConnection();
                    }
                } else if (type == TYPE_CT_WAP) {
                    // "10.0.0.200", 80
                    try {
                        java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                                new InetSocketAddress("10.0.0.200", 80));
                        connection = (HttpURLConnection) uri.openConnection(p);
                    } catch (Exception e2) {
                        connection = (HttpURLConnection) uri.openConnection();
                    }
                }
            }
        } catch (Exception e) {
            CommonFunction.log("basehttp", e.getMessage());
            throw new ConnectionException(ErrorCode.E_101, e);
        }
    }

    /**
     * 获取Network具体类型
     *
     * @param mContext
     * @return
     */
    public static int checkNetworkType(Context mContext) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {
                return TYPE_NET_WORK_DISABLED;
            } else {
                int netType = networkInfo.getType();
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    return TYPE_WIFI;
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    // 电信wap
                    final Cursor c = mContext.getContentResolver().query(PREFERRED_APN_URI,
                            null, null, null, null);
                    try {
                        if (c != null) {
                            c.moveToFirst();
                            final String user = c.getString(c.getColumnIndex("user"));
                            if (!TextUtils.isEmpty(user)) {
                                if (user.startsWith(CTWAP)) {
                                    return TYPE_CT_WAP;
                                }
                            }
                        }
                        String netMode = networkInfo.getExtraInfo();
                        if (netMode != null) {
                            netMode = netMode.toLowerCase();
                            if (netMode.equals(CMWAP) || netMode.equals(WAP_3G)
                                    || netMode.equals(UNIWAP)) {
                                return TYPE_CM_CU_WAP;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            CommonFunction.log("basehttp", ex.getMessage());
            return TYPE_OTHER_NET;
        }
        return TYPE_OTHER_NET;
    }

    /**
     * 设置连接超时
     *
     * @param timeout
     */
    protected void setConnectTimeOut(int timeout) {
        this.connectTimeout = timeout;
    }

    /**
     * 读数据超时
     *
     * @param timeout
     */
    protected void setReadTimeOut(int timeout) {
        this.readTimeout = timeout;
    }

    protected HttpURLConnection getConnection() {
        return connection;
    }

    /**
     * 关闭连接
     */
    protected void close() {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Exception e) {
                CommonFunction.log("basehttp", e.getMessage());
            }
        }
    }
}
