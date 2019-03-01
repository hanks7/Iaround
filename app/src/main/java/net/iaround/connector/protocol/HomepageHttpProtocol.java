
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


/**
 * 与首页有关的协议接口
 */
public class HomepageHttpProtocol extends HttpProtocol {

    /**
     * 业务请求
     *
     * @param url
     * @param map
     * @返回-1时，表示请求失败
     */
    public static long post(Context context, String url,
                            LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map, ConnectorManage.HTTP_HOME_PAGE,callback);
    }


    /**
     * 获取首页陪玩列表信息
     */
    public static long getGameChatInfo(Context context, long lat,long lng,HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("lat",lat);//纬度  lat 39924953  lng 116560674
        map.put("lng",lng);//经度
        return post(context, "/v1/gamechat/getgamechatinfo", map, callback);
    }

    /**
     * 获取首页语音列表信息
     */
    public static long getGameVoiceInfo(Context context, int mPageNo, int mPageSize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pageno", mPageNo);
        map.put("pagesize", mPageSize);
        return post(context, "/v1/voicechat/getvoiceanchorlist", map, callback);
    }

    /**
     * 获取语音聊天列表信息
     */
    public static long getVoiceListInfo(Context context, int mPageNo, int mPageSize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pageno", mPageNo);
        map.put("pagesize", mPageSize);
        return post(context, "/v1/voicechat/getvoicechatrecord", map, callback);
    }

    /**
     * 删除语音聊天记录列表的item
     */
    public static long deteleVoiceListItemInfo(Context context, long VoiceRoomListID, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("voiceRoomListID", VoiceRoomListID);
        return post(context, "/v1/voicechat/delvoicechatearn", map, callback);
    }

}
