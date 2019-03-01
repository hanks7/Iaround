package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;

/**
 * @ClassName PushHttpProtocol.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-21 下午5:05:31
 * @Description: 推送业务的Protocol类
 */

public class PushHttpProtocol extends HttpProtocol
{
	public static long pushPost(Context context , String url ,
                                LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url , map ,
				ConnectorManage.HTTP_PUSH , callback );
	}
	
	/**
	 * 推送确认
	 * @param context
	 * @param map
	 * @param callback
	 * @return
	 */
	public static long pushNotify(Context context , LinkedHashMap< String , Object > map ,
                                  HttpCallBack callback )
	{
		return pushPost( context , "/user/pushnotify_1_3" , map , callback );
	}

	/**
	 * 推送设备信息确认
	 * @param context
	 * @param token
	 * @param type
	 * @param callback
	 * @return
	 */
	public static long pushDeviceType(Context context, String token, String type,
									  HttpCallBack callback) {
		LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("pushtype", type);
		params.put("pushtoken", token);
		return ConnectorManage.getInstance(context).asynPost("/device/deviceuploadforandroid", params,
				ConnectorManage.HTTP_GOLD, callback);
	}


}
