package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;

/**
 * Created by Ray on 2017/8/25.
 */

public class SendWorldMessageProtocol {
    /**
     * 发送世界消息接口
     */
    private static String SEND_WORLD_MESSAGE = "/v1/chatbar/word/send";
    public static SendWorldMessageProtocol instance;

    public static SendWorldMessageProtocol getInstance() {
        if (instance == null) {
            instance = new SendWorldMessageProtocol();
        }
        return instance;
    }

    public static long getData(Context context, String url,
                               LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPostNoEncode(url, map,
                ConnectorManage.HTTP_CHATBAR, callback);


    }

    /**
     * 发送世界消息
     * @param context
     * @param groupId  int
     * @param content  string
     * @param type
     *              根据type值展示不同的数据类型   礼物信息，技能信息，招募信息，世界消息
     * @param callBack
     * @return
     */

    public long getSendWorldMessageData(Context context, int groupId, String content, int type,HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
//        String contentTransType = "011";
//        params.put("contentTransType", contentTransType);
        params.put("groupid", groupId);
        params.put("content", content);
        params.put("type", type);
        return getData(context, SEND_WORLD_MESSAGE, params, callBack);
    }

    /**
     *
     * @param context
     * @param groupId
     * @param content
     * @param callBack
     * @return
     */
    public long getSendWorldMessageData(Context context, int groupId, String content, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
//        String contentTransType = "011";
        params.put("type", "30");
        params.put("groupid", groupId);
        params.put("content", content);
        return getData(context, SEND_WORLD_MESSAGE, params, callBack);
    }
}
