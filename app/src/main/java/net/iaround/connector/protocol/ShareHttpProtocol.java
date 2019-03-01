package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;

/**
 * 作者：zx on 2017/8/14 14:46
 */
public class ShareHttpProtocol {

    public static final String sShareChatbarSuccessUrl = "/v1/chatbar/setshare";

    /*成功分享的类型  shareType 1 "Wechat" ; 2 "WZone" ; 3 "Weibo"; 4 "QQ" ; 5 "QZone"
    * */
    public static long shareSuccessPost(Context context, int shareType,final HttpCallBack callback) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("shareType", shareType);
        return ConnectorManage.getInstance(context).asynPost(sShareChatbarSuccessUrl, params, ConnectorManage.HTTP_CHATBAR, callback);
    }


}
