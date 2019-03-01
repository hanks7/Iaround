package net.iaround.statistics;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

import net.iaround.BuildConfig;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.tools.CommonFunction;

import java.util.Random;

/**
 * Created by liangyuanhuan on 22/08/2017.
 * 统计日志格式
 */

public class StatisticsFormat {
    public static final  String  DEV_ACTIVATE = "devactivate";		//设备激活 		1000  （设备第一次启动遇见，以前未使用过）
    public static final  String  APP_STARTUP = "appstartup"; 			//app启动 		1001 （app启动过“遇见”的次数。）
    public static final  String  PAGE_NEARBY = "pgnearby";      //附近页面  	1002 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_DYNAMIC = "pgdynamic";	    //动态页面  	1003 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_CHATHOT = "pgchathot";     //聊吧热门页面  1004 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_CHATMINE = "pgchatmine";	//		聊吧我的页面  1005 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_MESSAGE = "pgmessage" ;    //			消息页面  	1006 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_RANK_CHARM = "pgrankcharm";   //			排行魅力页面  1007 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_RANK_RICH = "pgrankrich";   //			排行富豪页面  1008 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_USER = "pguser";        //			个人页面  	1009 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_MEMBER_PAY = "pgmemberpay";     //			进入会员支付页面     1010
    public static final  String  PAGE_DIAMOND_PAY = "pgdiamondpay";        //		进入钻石充值页面		1011
    public static final  String  MEMBER_PAY_SUCCESS = "memberpay";     //			会员支付成功     1012
    public static final  String  DIAMOND_PAY_SUCCESS = "diamondpay";        //		钻石充值成功		1013
    public static final  String  PAGE_RANK_SKILL = "pgrankskill";   //			排行技能页面  1014 （用户进入和退出页面一次的时间）
    public static final  String  PAGE_INDEX_ADD = "indexad";   //首页广告		1015

    /*devactivate 		设备激活 		1000  （设备第一次启动遇见，以前未使用过）
		array(
				'uid'=>'设备ID',   //uid写设备id
				'action'=>'devactivate',
				'target'=>'-',
				'timstamp'=>1502423230,
				'client'=>['ip'=>'128.0.0.1','version'=>'5.6.7','protocol'=>15],
		);
    * */
    public static String getDeviceActivateJson(Context context, String user){
        JSONObject json = new JSONObject();

        json.put("action",DEV_ACTIVATE);

        appendExtra(context, json);

        json.put("target",'-');
        json.put("uid",user);

        long current = System.currentTimeMillis();
        json.put("timstamp",current);

        return json.toJSONString();
    }

    /*  appstartup 			app启动 		1001 （app启动过“遇见”的次数。）
		array(
				'uid'=>61001269,   //用户id
				'action'=>'appstartup',
				'target'=>‘-’,
				'timstamp'=>1502423230,
				'client'=>['ip'=>'128.0.0.1','version'=>'5.6.7','protocol'=>15],
		);
    * */
    public static String getAppStartupJson(Context context, String user){
        JSONObject json = new JSONObject();

        json.put("action",APP_STARTUP);

        appendExtra(context,json);

        json.put("target",'-');
        json.put("uid",user);

        long current = System.currentTimeMillis();
        json.put("timstamp",current);

        return json.toJSONString();
    }

    /*
    *pgnearby			附近页面  	1002 （用户进入和退出页面一次的时间）
		array(
				'uid'=>61001269,   //用户id
				'action'=>'pgnearby',
				'target'=>‘300’,   //time 停留时间seconds
				'timstamp'=>1502423230,
				'client'=>['ip'=>'128.0.0.1','version'=>'5.6.7','protocol'=>15],
		);
    * */
    public static String getPageTimeJson(Context context, String user, int page, long time){
        JSONObject json = new JSONObject();


        if(Statistics.PAGE_NEARBY == page) {
            json.put("action", StatisticsFormat.PAGE_NEARBY);
        }else if(Statistics.PAGE_DYNAMIC == page) {
            json.put("action", StatisticsFormat.PAGE_DYNAMIC);
        }else if(Statistics.PAGE_CHAT_HOT == page) {
            json.put("action", StatisticsFormat.PAGE_CHATHOT);
        }else if(Statistics.PAGE_CHAT_MINE == page) {
            json.put("action", StatisticsFormat.PAGE_CHATMINE);
        }else if(Statistics.PAGE_MESSAGE == page) {
            json.put("action", StatisticsFormat.PAGE_MESSAGE);
        }else if(Statistics.PAGE_RANKING_CHARM == page) {
            json.put("action", StatisticsFormat.PAGE_RANK_CHARM);
        }else if(Statistics.PAGE_RANKING_RICH == page) {
            json.put("action", StatisticsFormat.PAGE_RANK_RICH);
        }else if(Statistics.PAGE_USER == page) {
            json.put("action", StatisticsFormat.PAGE_USER);
        }else if(Statistics.PAGE_MEMBER_PAY == page) {
            json.put("action", StatisticsFormat.PAGE_MEMBER_PAY);
        }else if(Statistics.PAGE_DIAMOND_PAY == page) {
            json.put("action", StatisticsFormat.PAGE_DIAMOND_PAY);
        }else if(Statistics.PAGE_RANKING_SKILL == page) {
            json.put("action", StatisticsFormat.PAGE_RANK_SKILL);
        }else if(Statistics.PAGE_INDEX_AD == page) {
            json.put("action", StatisticsFormat.PAGE_INDEX_ADD);
        }else{
            return null;
        }


        appendExtra(context,json);

        if(Statistics.PAGE_INDEX_AD == page) {
            JSONObject client = json.getJSONObject("client");
            if(client!=null) {
                client.put("type", "index");
            }
        }
        if(0 == time){
            json.put("target", '-');
        }else {
            json.put("target", String.valueOf(time));
        }
        json.put("uid",user);

        long current = System.currentTimeMillis();
        json.put("timstamp",current);

        return json.toJSONString();
    }

    /*
    * memberpay			会员支付     1010
		array(
				'uid'=>61001269,   //用户id
				'action'=>'memberpay',
				'target'=>'99.99',   // target 订单id? ;  money 支付金额
				'timstamp'=>1502423230,
				'client'=>['ip'=>'128.0.0.1','version'=>'5.6.7','protocol'=>15,'order'=>'99999']
		);
    * */
    public static String getMemberPayJson(Context context, String user,float money){
        JSONObject json = new JSONObject();


        json.put("action",StatisticsFormat.MEMBER_PAY_SUCCESS);


        appendExtra(context,json);

        json.put("target",String.valueOf(money));
        json.put("uid",user);

        long current = System.currentTimeMillis();
        json.put("timstamp",current);

        return json.toJSONString();
    }

    public static String getDiamondPayJson(Context context, String user,float money){
        JSONObject json = new JSONObject();


        json.put("action",StatisticsFormat.DIAMOND_PAY_SUCCESS);

        appendExtra(context,json);

        json.put("target",String.valueOf(money));
        json.put("uid",user);

        long current = System.currentTimeMillis();
        json.put("timstamp",current);

        return json.toJSONString();
    }

    private static void appendExtra(Context context,JSONObject json){
        JSONObject client = new JSONObject();
        client.put("plat",""+Config.PLAT);
        client.put("version", android.os.Build.VERSION.RELEASE);
        client.put("protocol","1");
        client.put("key",ConnectorManage.getInstance(context).getKey());
        client.put("packageid",BuildConfig.packageID);
        client.put("appversion", Config.APP_VERSION);
        client.put("type","0");
        json.put("client",client);
    }
}
