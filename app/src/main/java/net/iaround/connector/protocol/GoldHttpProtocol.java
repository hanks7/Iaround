
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.BuildConfig;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PhoneInfoUtil;

import java.util.LinkedHashMap;


/**
 * 支付模块协议接口
 *
 * @author linyg
 * @date 2012-08-25
 */
/*
v5.0
钻石兑换金币
/goods/diamond/goldlist
点击钻石兑换金币
/goods/diamond/goldlist

获取用户当前金币数
/user/gold/get

v5.0购买钻石
/goods/diamond/list

* VIP商品列表
* /gold/vip/list
 */


public class GoldHttpProtocol extends HttpProtocol {


    public static long goldPost(Context context, String url,
                                LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_GOLD, callback);
    }

    public static long miguPost(Context context, String url,
                                LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_BUSINESS, callback);
    }

    //php域名
    public static long phpPost(Context context, String url,
                                LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_HOME_PAGE, callback);
    }


    public static long diamondForGold(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        return goldPost(context, "/goods/diamond/goldlist", map, callback);
    }




    /**
     * 点击钻石兑换金币
     *
     * @param context
     * @param callback
     * @return
     * @version v5.0
     */
    public static long onclickDiamondForGold(Context context, String goodsid,
                                             HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("goodsid", goodsid);
        return goldPost(context, "/goods/diamond/exchange", map, callback);
    }


    /**
     * 获取用户当前金币数
     *
     * @return
     */
    public static long userGoldGet(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        return goldPost(context, "/user/gold/get", map, callback);
    }


    /**
     * 购买钻石
     *
     * @param context
     * @param paywayid 渠道ID
     * @return
     */
    public static long goodsList(Context context, int paywayid, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("paywayid", paywayid);
        return goldPost(context, "/goods/diamond/list", map, callback);
    }




    /**
     * @param context 平台id (1:android;2:iphone) 是否越狱（针对IOS，y是，n否） 包ID 应用版本号 地区1-大陆
     *                2-港澳台 +-海外
     * @return
     * @Title: getMywallet
     * @Description: 我的钱包
     */
    public static long getMywallet(Context context, HttpCallBack callback) {

        boolean isChina = PhoneInfoUtil.getInstance(context).isChinaCarrier();

        String area = isChina ? "1" : "2";

        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("plat", Config.PLAT);
        map.put("crack", "n");
        map.put("packageid", CommonFunction.getPackageMetaData(context));
        map.put("appversion", Config.APP_VERSION);
        map.put("area", area);
        map.put("isanchor", Common.getInstance().loginUser.getUserType());//是否是主播1:是 0或者不传不是
        return goldPost(context, "/user/mywallet", map, callback);
    }


    /**
     * 获取vip特权列表
     *
     * @param context
     * @param callback
     * @return
     */
    public static long getMemberPrivilegeList(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        return goldPost(context, "/user/member", map, callback);
    }


    /**
     * 获取钻石详情列表
     *
     * @param context
     * @param callback
     * @return
     */
    public static long getBrillantDetailList(Context context, int pageNo, int pageSize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageNo);
        map.put("pagesize", pageSize);
        return goldPost(context, "/diamond/consumelist", map, callback);
    }


    /**
     * 获取VIP购买会员列表
     * @param context
     * @param callback
     * @return
     */
    public static long getBuyMemberList( Context context , String area , HttpCallBack callback ){

        LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
        map.put( "plat" , Config.PLAT );
        map.put( "crack" , "n" );
        map.put( "area" , area );
        map.put( "packageid" , CommonFunction.getPackageMetaData( context ) );
        map.put( "appversion" , Config.APP_VERSION );
        return goldPost( context , "/user/buymemberlist" , map , callback );
    }



    /**
     * 获取vip商品列表 http://[host]:<port>/user/vipgoodsgetlist
     */
    public static long getVipGoodsList(Context context, String payWayId,
                                       HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("paywayid", payWayId);
        return goldPost(context, "/user/vipgoodsgetlist", map, callback);
    }

    /**
     * 我的钱包（6.2新接口） /user/mywallet_6_2
     */
    public static long getNewMyWallet(Context context, long userid, int plat, String version, int type, String dpi, String logincode, String appversion, String crack, String packageid, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("userid", userid);
        map.put("plat", plat);
        map.put("version", version);
        map.put("type", type);
        map.put("packageid", packageid);
        map.put("dpi", dpi);
        map.put("logincode", logincode);
        map.put("appversion", appversion);
        map.put("crack", crack);

        return goldPost(context, "v1/wallet/index", map, callback);
    }

    /**
     * 用户登录兑吧积分商城（6.2新接口）
     * </br>redirect-指定页面，可以为空
     */
    public static long autoLoginDuiba(Context context, String redirect, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        boolean isChina = PhoneInfoUtil.getInstance(context).isChinaCarrier();
        String area = isChina ? "1" : "2";
        map.put("redirect", redirect);
        map.put("area", area);
        return goldPost( context, "/credits/duiba/autologin", map, callback);
    }

    /**
     * 钱包积分来源（6.2新接口）
     */
    public static long getCreditsHistory(Context context, int pageno,
                                         int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        return goldPost(context, "/credits/gethistory", map, callback);
    }

    /**
     * 请求咪咕订购状态
     */
    public static long getMiguStatus(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("quDaoCode", BuildConfig.packageID);
        return miguPost(context, "/pay/migu", map, callback);
    }
    /**
     * 请求咪咕动漫联合会员订购状态
     */
    public static long getMiguManLianStatus(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        return miguPost(context, "/pay/manlian", map, callback);
    }
    /**
     * 设置咪咕动漫联合会员订购状态
     */
    public static long setMiguManLianStatus(Context context,int manlianstatus, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("manlianstatus", manlianstatus);
        return miguPost(context, "/pay/setmanlian", map, callback);
    }

    /**
     * 咪咕退订
     * @param context
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public static long requestWebsite(Context context , String url ,String params ,
                                                      HttpCallBack callback )
    {
        long flag = -1;
        try
        {
            flag = ConnectorManage.getInstance( context ).simplePost( url , params , callback );
        }
        catch ( Exception e )
        {
            e.printStackTrace( );
        }
        return flag;
    }

    /**
     * @param context 平台id (1:android;2:iphone) 是否越狱（针对IOS，y是，n否） 包ID 应用版本号 地区1-大陆
     *                2-港澳台 +-海外
     * @return
     * @Title: getMywallet
     * @Description: 星星充值页面
     */
    public static long getStar(Context context, HttpCallBack callback) {
        boolean isChina = PhoneInfoUtil.getInstance(context).isChinaCarrier();
        String area = isChina ? "1" : "2";
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("plat", Config.PLAT);
        map.put("area", area);
        return phpPost(context, "/v1/voicechat/rechargestar", map, callback);
    }


    /**
     * 钱包明细
     * @return
     * @Title: getWalletDetails
     */
    public static long getWalletDetails(Context context,int pageno,int pagesize,int type, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        map.put("type", type);
        return phpPost(context, "/v1/anchor/getwalletdetail", map, callback);
    }
}
