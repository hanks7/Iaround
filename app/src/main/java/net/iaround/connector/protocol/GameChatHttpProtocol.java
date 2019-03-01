
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.conf.Common;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


/**
 * 与陪玩游戏有关的协议接口
 */
public class GameChatHttpProtocol extends HttpProtocol {

    /**
     * 业务请求
     *
     * @param url
     * @param map
     * @返回-1时，表示请求失败
     */
    public static long post(Context context, String url,
                            LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map, ConnectorManage.HTTP_HOME_PAGE, callback);
    }


    /****与主播资质认证有关的协议接口***/

    /**
     * 获取资质认证信息接口
     */
    public static long getQualification(Context context, long gameId, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("gameId", gameId);
        return post(context, "/v1/gamechat/qualification", map, callback);
    }

    /**
     * 申请资质认证选项列表接口
     */
    public static long applyQualification(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        return post(context, "/v1/gamechat/applyqualification", map, callback);
    }

    /**
     * 提交资质认证信息接口
     */
    public static long putQualification(Context context, long gameId, String photos, String voice, int gameLevel, String description, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("gameId", gameId);
        map.put("photos", photos);
        map.put("voice", voice);
        map.put("gameLevel", gameLevel);
        map.put("description", description);
        return post(context, "/v1/gamechat/putqualification", map, callback);
    }


    /****接单设置相关接口***/

    /**
     * 获取接单设置接口
     */
    public static long getGameOrderSet(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        return post(context, "/v1/gamechat/gameorderset", map, callback);
    }

    /**
     * 保存接单设置接口
     */
    public static long saveGameOrderSet(Context context, String games, String beginTime, String endTime, String repeat, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("games", games);// 如果没有折扣discount为0  gameId1:price1:discount1, gameId2:price2:discount1
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("repeat", repeat);
        return post(context, "/v1/gamechat/savegameorderset", map, callback);
    }


    /***与实名认证有关的协议接口-以下***/

    /**
     * 获取实名认证信息接口
     */
    public static long getAuthentication(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        return post(context, "/v1/gamechat/authentication", map, callback);
    }

    /**
     * 提交实名认证信息接口
     */
    public static long putAuthInfo(Context context, String realName, String phone, String frontPhoto, String backPhoto, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("realName", realName);
        map.put("phone", phone);
        map.put("frontPhoto", frontPhoto);
        map.put("backPhoto", backPhoto);
        return post(context, "/v1/gamechat/putauthinfo", map, callback);
    }


    /***主播明细相关接口***/

    /**
     * 获取主播每日明细接口
     */
    public static long getDailyEarn(Context context, int mPageNo, int mPageSize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pagenum", mPageNo);
        map.put("pagecount", mPageSize);
        return post(context, "/v1/gamechat/getdailyearn", map, callback);
    }

    /**
     * 获取主播今日总收入接口
     */
    public static long getEarnDetail(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        return post(context, "/v1/gamechat/getearndetail", map, callback);
    }

    /**
     * 获取主播提现明细接口
     */
    public static long getWithDrawList(Context context,int mPageNo, int mPageSize,String mDay, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pageno", mPageNo);
        map.put("pagesize", mPageSize);
        if(mDay != null){
            map.put("day",mDay);
        }
        return post(context, "/v1/gamechat/getwithdrawlist", map, callback);
    }


    /***关于订单相关接口***/

    /**
     * 获取订单中心列表接口
     */
    public static long getOrderList(Context context, int pageNum, int pageCount, int status, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pagenum", pageNum);
        map.put("pagecount", pageCount);
        map.put("status", status);
        return post(context, "/v1/orders/orderlist", map, callback);
    }


    /**
     * 获取游戏列表接口（陪玩分类）
     */
    public static long getGameList(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        return post(context, "/v1/games/gamelist", map, callback);
    }

    /**
     * 获取游戏列表接口（陪玩分类详情 游戏类型下附近的人）
     */
    public static long getPlayMatesList(Context context, int pageno, int pagesize, long lat, long lng, String gender, String price, String level, long gameid, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        map.put("lat", lat);
        map.put("lng", lng);
        map.put("gender", gender);
        map.put("price", price);
        map.put("level", level);
        map.put("gameid", gameid);
        return post(context, "/v1/near/playmateslist", map, callback);
    }

    /**
     * 同意或拒绝游戏订单
     *
     * @param context
     * @param agree       1-同意 2-拒绝
     * @param orderInfoId
     * @param msgId
     * @param callback
     * @return
     */
    public static long manageOrderResult(Context context, int agree, long orderInfoId, long msgId, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("agree", agree);
        map.put("orderInfoId", orderInfoId);
        map.put("msgid", msgId);
        return post(context, "/v1/gamechat/manageorderresult", map, callback);
    }


    /**
     * 主播发送服务邀请
     */
    public static long anchorSendServerMsg(Context context, long orderInfoId, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("orderInfoId", orderInfoId);
        return post(context, "/v1/gamechat/anchorsendservermsg", map, callback);
    }

    /**
     * 用户同意主播服务
     */
    public static long userAgreeServerMsg(Context context, int agree, long orderInfoId, long msgId, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("agree", agree);
        map.put("orderInfoId", orderInfoId);
        map.put("msgid", msgId);
        return post(context, "/v1/gamechat/useragreeservermsg", map, callback);
    }

    /**
     * 用户完成主播服务
     */
    public static long finishServerMsg(Context context, long orderInfoId, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("orderInfoId", orderInfoId);
        return post(context, "/v1/gamechat/finishservermsg", map, callback);
    }

    /**
     * 用户申请退款
     */
    public static long refundCheck(Context context, long orderInfoId, String reason, String other, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("orderInfoId", orderInfoId);
        map.put("reason", reason);
        map.put("other", other);
        return post(context, "/v1/gamechat/refundcheck", map, callback);
    }

    /**
     * 主播同意或者拒绝退款
     */
    public static long agreeRefund(Context context, int agree, long orderInfoId, long msgId, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("agree", agree);
        map.put("orderInfoId", orderInfoId);
        map.put("msgid", msgId);
        return post(context, "/v1/gamechat/agreerefund", map, callback);
    }

    /**
     * 主播申请提现接口
     */
    public static long applyWithdrawal(Context context, double sum, String account, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("sum", sum);
        map.put("account", account);
        return post(context, "/v1/gamechat/applywithdrawal", map, callback);
    }

    /**
     * 获取语音明细列表信息
     */
    public static long getVoiceDetailListInfo(Context context, int mPageNo, int mPageSize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pageno", mPageNo);
        map.put("pagesize", mPageSize);
        return post(context, "/v1/voicechat/getvoicechatearn", map, callback);
    }

    /**
     * 设置主播状态 勿扰
     */
    public static long setvoiceanchorstatus(Context context, int status, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("DisturbStatus", status);
        return post(context, "/v1/voicechat/setvoiceanchorstatus", map, callback);
    }


}
