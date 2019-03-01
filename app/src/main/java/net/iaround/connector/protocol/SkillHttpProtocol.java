package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;

/**
 * 作者：zx on 2017/8/14 14:46
 */
public class SkillHttpProtocol {

    public static long skillPost(Context context, String url, LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map, ConnectorManage.HTTP_GROUP, callback);
    }


}
