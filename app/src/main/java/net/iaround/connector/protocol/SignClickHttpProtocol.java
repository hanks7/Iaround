package net.iaround.connector.protocol;

import android.content.Context;
import android.os.Build;

import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.ThreadPoolUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Ray on 2017/6/6.
 */

public class SignClickHttpProtocol {
    private static StringBuffer sb = null;
    public static SignClickHttpProtocol protocol;

    public static SignClickHttpProtocol getInstance() {
        if (protocol == null) {
            protocol = new SignClickHttpProtocol();
        }
        return protocol;
    }

    public static String loginPost(Context context, String url,
                                   String params, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).syncGet(url, params);
    }

    /**
     * 统计模块
     * <p>
     * phoneBrands        手机品牌
     * phoneType          手机型号
     * phoneSystemVersion 手机系统版本
     * appVersion         app版本
     * packageId          包id
     * phoneId            手机唯一标识
     * part               登录/注册 类型  0-0 激活 1 登录  2 注册 3 授权成功 4授权失败 5 登录按钮（包括授权成功，失败，网络异常）6 注册按钮（包括授权成功，失败，网络异常）7-7 启动
     * type               1 QQ ,2 微博,4 facebook,7， 微信
     * count              统计的数量
     */
    public HashMap<String, String> syncGetStatistics(Context context, String part, String type, String count, String enterType, HttpCallBack httpCallBack) {
        String phoneBrandsNew = PhoneInfoUtil.getInstance(context).getDevice();
        String phoneIdNew = PhoneInfoUtil.getInstance(context).getDeviceId();
        String appVersionNew = Config.APP_VERSION;
        String phoneType = "" + Build.MODEL;
        String phoneSystemVersion = "" + Build.VERSION.RELEASE;
        String combinationType = "";
        if (Integer.parseInt(enterType) > 0) {
            combinationType = "1-" + part + "-" + type + "-" + enterType;
        } else {
            combinationType = "1-" + part + "-" + type ;
        }
        String packageIdNew = CommonFunction.getPackageMetaData(context);
        HashMap<String, String> params = new HashMap<>();
        params.put("from", "iaround");
        params.put("phoneBrands", phoneBrandsNew);
        params.put("phoneType", phoneType);
        params.put("phoneSystemVersion", phoneSystemVersion);
        params.put("appVersion", appVersionNew);
        params.put("packageId", packageIdNew);
        params.put("phoneId", phoneIdNew);
        params.put("plat", Config.PLAT + "");
        params.put("type", combinationType);
        params.put("count", count);
//        getUrlWithoutUrl(context, params, httpCallBack);
        doAsynRequest(params, httpCallBack);
//        try {
//            sendGETRequest(params, httpCallBack);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return params;
    }


    private static String getUrl(HashMap<String, String> params) {
        String url = "actionlog.iaround.com";
        // 添加url参数
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        return url;
    }

    public static String getUrlWithoutUrl(Context context, HashMap<String, String> params, HttpCallBack httpCallBack) {
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
        }
        loginPost(context, Constants.ACTION_LOG_IP, sb.toString(), httpCallBack);
        return sb.toString();
    }

    public boolean sendGETRequest(HashMap<String, String> params, HttpCallBack httpCallBack) throws Exception {
        boolean success = false;
        String path = Constants.ACTION_LOG_IP;
        // StringBuilder是用来组拼请求地址和参数
        StringBuilder sb = new StringBuilder();
        sb.append(path).append("?");
        if (params != null && params.size() != 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // 如果请求参数中有中文，需要进行URLEncoder编码 gbk/utf8
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        CommonFunction.log("Other", "url===" + path + "/" + params);
        URL url = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(20000);
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            success = true;
        }
        if (conn != null)
            conn.disconnect();
        return success;
    }


//    public static void requestByGet(Context context,
//                                    int url, Map<String, String> map,
//                                    boolean isCache, boolean isShowDialog) {
//        //组织URL 及判断网络处理 省略 。。。。
//        // 异步请求数据
//        doAsynRequest(GET, null, context, requestUrl, isCache, isShowDialog, url);
//    }


    private void doAsynRequest(final HashMap<String, String> params, final HttpCallBack httpCallBack) {

        // 请求
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendGETRequest(params, httpCallBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
