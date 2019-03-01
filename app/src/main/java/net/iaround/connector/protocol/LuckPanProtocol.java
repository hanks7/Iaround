
package net.iaround.connector.protocol;


import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import net.android.volley.ConnectorConfig;
import net.iaround.BaseApplication;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 抽奖协议接口
 *
 * 抽奖接口:task.iaround.com/v1/task/getlottery
	 返回数据格式:{
	 "propId": 3,
	 "count": {
	 	"free": "9",
	 	"diamond": "10"
	 },
	 "msg": "ok",
	 "status": 200
	 }

 * @author lyh
 * 
 */
public class LuckPanProtocol extends HttpProtocol
{
	private static AtomicLong sHttpFlag = new AtomicLong(0);
	private static String sUserSessionkey = null;
	//抽奖接口（每次抽奖，发送此接口给服务器，服务器返回中奖奖品ID等）
	public static String sLuckPanProtocolUrl = "http://task.iaround.com/v1/task/getlottery";
	//免费抽奖次数
	public static String sLuckPanLottreyNumUrl = "http://task.iaround.com/v1/task/getlotterynum";

	/***
	 *
	 * 获取用户的中奖信息
	 * @param context
	 * @param callBack
     * @return
     */
	public static long getUserLuckPanInfo(Context context,String url,HttpCallBack callBack)
	{
		sUserSessionkey = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.IAROUND_SESSIONKEY);
		return luckpanPost(url, callBack);
	}

	/***
	 *
	 * 获取用户的免费中奖次数
	 * @param context
	 * @param callBack
	 * @return
	 */
	public static long getUserFreeLotteryInfo(Context context,String url,HttpCallBack callBack)
	{
		sUserSessionkey = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.IAROUND_SESSIONKEY);
		return luckpanPost(url, callBack);
	}

	private static long luckpanPost(String url,HttpCallBack callback )
	{
		long flag = sHttpFlag.incrementAndGet();
		Thread task = new HttpRequestThread(callback, flag, url);
		task.start();
		return flag;
	}


	static class HttpRequestThread extends Thread{
		private WeakReference<HttpCallBack>  callBack;
		private long flag;
		private String url;

		public HttpRequestThread(HttpCallBack callBack, long flag, String url){
			this.callBack = new WeakReference<HttpCallBack>(callBack);
			this.flag = flag;
			this.url = url;
		}

		@Override
		public void run() {
			String response = null;
			for(int i=0;i<3;i++) {
				response = sendPostRequest(ConnectorConfig.getSigniture(null, sUserSessionkey), url);
				if(null!=response){
					break;
				}
			}
			final String result = response;
			CommonFunction.log("LuckPanProtocol", "luckpanPost() response==" + result);
			final HttpCallBack httpCallBack = this.callBack.get();
			if(null!=httpCallBack){
				if (result == null || TextUtils.isEmpty(result)) {
					Handler h = new Handler(BaseApplication.appContext.getMainLooper());
					h.post(new Runnable() {
						@Override
						public void run() {
							httpCallBack.onGeneralError(ErrorCode.E_107,flag);
						}
					});
				}else{
					Handler h = new Handler(BaseApplication.appContext.getMainLooper());
					h.post(new Runnable() {
						@Override
						public void run() {
							httpCallBack.onGeneralSuccess(result, flag);
						}
					});
				}
			}else{
				CommonFunction.log("LuckPanProtocol", "callback null");
			}
		}
	}

	private static String sendPostRequest(String params, String request) {
		String success = null;
		String path = request; //sLuckPanProtocolUrl;
		try {
			CommonFunction.log("LuckPanProtocol", "sendPostRequest() url===" + path + ", params===" + params);
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
			printWriter.write(params);//post的参数 xx=xx&yy=yy
			printWriter.flush();
			int code = conn.getResponseCode();
			if (code == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				StringBuilder sd = new StringBuilder();
				String output;
				while ((output = br.readLine()) != null) {
					sd.append(output);
				}
				success = sd.toString();
            }
			printWriter.close();
			conn.disconnect();
		}catch (Exception e){
			CommonFunction.log("LuckPanProtocol", "sendPostRequest() exception=" + e.toString());
		}
		return success;
	}


}
