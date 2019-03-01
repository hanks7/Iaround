package net.iaround.connector.protocol;

import android.content.Context;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.database.ReportLog;

import java.util.LinkedHashMap;

/**
 * @author：liush on 2016/12/9 17:13
 */
public class UserHttp2Protocol {

    private static String initUrl = "/user/personal";

    public static long userPost(Context context, String url,
                                LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_USER, callback);
    }

    /**
     * 举报
     *
     * @param type       @ReportType
     * @param targettype @ReportTargetType
     * @param targetid   被举报目标ID,如果类型是动态就是动态的id
     * @param content    举报内容（文本或者照片路径） 消息类型 （1：文本，2：图片，3：声音，4：视频，5.位置）
     *                   如果举报的是聊天内容，则内容为json串，内容样例：
     *                   {contents:[{type:1,content:"靠"}，{type
     *                   :2,content:"http://234.jpg"}]} 如果type=5，则按照“纬度,经度”进行构造
     *                   targetid为动态的时候,content为"";
     * @return
     */
    public static long systemReport(Context context, int type, int targettype, String targetid,
                                    String content, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("type", type);
        entity.put("targettype", targettype);
        entity.put("targetid", targetid);
        entity.put("content", content);

        if (ReportLog.getInstance().checkReport(entity)) {
            Toast.makeText(context, R.string.report_repeated, Toast.LENGTH_SHORT).show();
            return 1;
        } else {
            ReportLog.getInstance().logReport(entity);
            return userPost(context, "/system/report", entity, callback);
        }
    }

}
