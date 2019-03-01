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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lyh on 2017/8/25.
 */

public class StatisticsHttpProtocol {
    public static StatisticsHttpProtocol protocol = null;

    public static StatisticsHttpProtocol getInstance() {
        if (protocol == null) {
            synchronized (StatisticsHttpProtocol.class) {
                if(protocol == null)
                    protocol = new StatisticsHttpProtocol();
            }
        }
        return protocol;
    }

    //返回值 是否继续发送统计消息 服务器返回 非 200 表示停止发送统计消息
    public int syncSendStatistics(String json){
        int state = 0;
        String path = Constants.ACTION_LOG_IP + "/data/";
        StringBuilder sb = new StringBuilder();
        sb.append(path).append("?");
        HttpURLConnection conn = null;
        try {
            sb.append("data").append("=").append(json);
            String urlString = sb.toString();
            CommonFunction.log("StatisticsHttpProtocol", "syncSendStatistics() url===" + urlString);
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 403) {
//                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//                StringBuilder sd = new StringBuilder();
//                String output;
//                while ((output = br.readLine()) != null) {
//                    sd.append(output);
//                    sd.toString();
//                }
//                success = output;
                state = 1;
            }
            //conn.disconnect();
        }catch (Exception e){
            state = -1;
            CommonFunction.log("StatisticsHttpProtocol", "syncSendStatistics() exception=" + e.toString());
        }finally {
            if(null!=conn) {
                conn.disconnect();
            }
        }

        return state;
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
     * part               登录/注册 类型  0-0 激活 1 登录  2 注册 3 授权成功 4授权失败 5 登录按钮（包括授权成功，失败，网络异常）6 注册按钮（包括授权成功，失败，网络异常）7-7 启动 10 统计？
     * type               1 QQ ,2 微博,4 facebook,7， 微信
     * count              统计的数量
     */
    public String syncGetStatistics(Context context, int part, String user) {
        String phoneBrandsNew = PhoneInfoUtil.getInstance(context).getDevice();
        String phoneIdNew = PhoneInfoUtil.getInstance(context).getDeviceId();
        String appVersionNew = Config.APP_VERSION;
        String phoneType = "" + Build.MODEL;
        String phoneSystemVersion = "" + Build.VERSION.RELEASE;
//        String combinationType = "";
//        if (Integer.parseInt(enterType) > 0) {
//            combinationType = "1-" + part + "-" + type + "-" + enterType;
//        } else {
//            combinationType = "1-" + part + "-" + type ;
//        }
        String combinationType = "1-" + part + "-" + user ;
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
        params.put("count", String.valueOf(1));

        return sendGETRequest(params);
    }

    private String sendGETRequest(HashMap<String, String> params) {
        String success = null;
        String path = Constants.ACTION_LOG_IP;
        StringBuilder sb = new StringBuilder();
        sb.append(path).append("?");
        HttpURLConnection conn = null;
        try {
            if (params != null && params.size() != 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                    sb.append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            String urlString = sb.toString();
            CommonFunction.log("StatisticsHttpProtocol", "sendGETRequest() url===" + urlString);
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder sd = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sd.append(output);
                }
                success = sd.toString();
            }
//            conn.disconnect();
        }catch (Exception e){
            CommonFunction.log("StatisticsHttpProtocol", "sendGETRequest() exception=" + e.toString());
        }finally {
            if(null!=conn){
                conn.disconnect();
            }
        }
        return success;
    }

}
