package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.UploadFileManager;
import net.iaround.connector.UploadImageCallback;
import net.iaround.entity.type.ReportTargetType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class: 动态接口
 * Author：gh
 * Date: 2016/12/2 16:09
 * Emial：jt_gaohang@163.com
 */
public class DynamicHttpProtocol {

    private static String DYNAMIC = "/dynamic/nearDyList";
    private static String PERSONAL_DYNAMIC = "/dynamic/personalDyList";
    private static String FRIEND_DYNAMIC = "/dynamic/friendDyList";
    private static String ADVERT = "/dynamic/advert";
    private static String ADD = "/dynamic/add";
    private static String LOVEADD = "/dynamic/love";
    private static String REVIEWSADD = "/dynamic/comment";
    private static String DYNAMIC_DETAILS = "/dynamic/dynamicDetails";
    private static String REPORT = "/dynamic/report";
    private static String BLACK_HANDLE = "/user/blackhandle";
    private static String DYNAMIC_DELETE = "/dynamic/del";
    private static String DYNAMIC_COMMENTLIST = "/dynamic/commentList";
    private static String DYNAMIC_LOVELIST = "/dynamic/loveList";
    private static String DYNAMIC_FRIEND = "2";
    private static String DYNAMIC_DETAILS_NEW = "/dynamic/get_7_0";


    /**
     * 业务请求
     *
     * @param url
     * @param map
     * @return 返回-1时，表示请求失败
     */
    public static long dynamicPost(Context context, String url,
                                   LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_DYNAMIC, callback);
    }

    /**
     * 获取动态详情改版测试的
     */
    public static long getDynamicDetail(Context context, long dynamicid,
                                        HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        return dynamicPost(context, "/user/dynamic/get_7_0", entity, callback);
    }

    /**
     * 获取用户动态列表
     * */
    public static long getUserDynamicList(Context context, long userid,
                                          int pageno, int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", userid);
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        return dynamicPost(context, "/user/dynamic/list_7_0", entity, callback);
    }

    /**
     * 动态举报
     *
     * @param context
     * @return
     */
    public static long doReport(Context context, String type, String dynid, HttpCallBack callback) {
        return UserHttpProtocol.systemReport(context,
                Integer.valueOf(type), ReportTargetType.DYNAMIC,
                String.valueOf(dynid), "",
                callback);
    }

    /**
     * 动态设置
     *
     * @param context
     * @param uid
     * @param type     1:黑名单 2:不让Ta看我动态 3: 我不看Ta动态
     * @param callback
     */
    public static long doBlackHandle(Context context, String uid, String type, HttpCallBack callback) {

        return BusinessHttpProtocol.updateInvisibilitySettingInfo(context,Long.valueOf(uid),Integer.valueOf(type),"n",callback);
    }

    public static void uploadPic(Map<String, File> picFileMap, UploadImageCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(FileUploadType.PIC_FACE));
        params.put("key", "" + TimeFormat.getCurrentTimeMillis());
        UploadFileManager.getInstance().requestHttpPost("http://upi.iaround.com:8080/file/uploadByType", params, picFileMap, "application/octet-stream", "file", callback);
    }

    /**
     * 获取更多评论
     *
     * @param context
     * @return
     */
    public static long doCommentList(Context context, long dynamicid, int pageno,
                                     int pagesize, HttpCallBack callback) {
//        HashMap<String, String> entity = new HashMap<String, String>();
//        entity.put("dynamicid", String.valueOf(dynamicid));
//        entity.put("reviewid", String.valueOf(reviewid));
//        post(122, context, DYNAMIC_COMMENTLIST, entity, callback);
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        return dynamicPost(context,"/user/dynamic/reviews/list_7_0",entity,callback);
    }

    /**
     * 获取贴吧话题称赞用户列表
     *
     * @param postbarid
     *            贴吧id
     * @param topicid
     *            话题id
     * @param pageno
     *            第几页（>=1）
     * @param pagesize
     *            每页显示的条数（>=1）
     * */
    public static long getPostbarTopicLikeList( Context context , long postbarid ,
                                                long topicid , int pageno , int pagesize , HttpCallBack callback )
    {
        LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
        entity.put( "postbarid" , postbarid );
        entity.put( "topicid" , topicid );
        entity.put( "pageno" , pageno );
        entity.put( "pagesize" , pagesize );
        return ConnectorManage.getInstance( context ).asynPost( "/postbar/topic/like/list" , entity ,
                ConnectorManage.HTTP_POSTBAR , callback );
    }

    /**
     * 获取用户动态赞列表
     * */
    public static long getUserDynamicGreetList(Context context, long dynamicid,
                                               int pageno, int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        return dynamicPost(context, "/user/dynamic/love/list_5_6", entity,
                callback);
    }

    /**
     * 称赞用户动态
     */
    public static long greetUserDynamic(Context context, long dynamicid,
                                        HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        return dynamicPost(context, "/user/dynamic/love/add", entity, callback);
    }

    /**
     * 取消称赞用户动态
     */
    public static long greetCancelUserDynamic(Context context, long dynamicid,
                                              HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        return dynamicPost(context, "/user/dynamic/love/cancel", entity,
                callback);
    }

    /**
     * 获取用户动态列表
     *
     * @param context
     * @param lat      当前纬度
     * @param lng      当前经度
     * @param pagesize 每页显示页数
     * @param last     最后一条记录的时间戳
     * @param type     请求的类型,0-全部,1-附近,2-已关注,3-热门(如果选择2个,用','隔开,如选择了附近和热门的:"1,3")
     * @param vipquery vip查询 0-否,1-是
     * @param gender   性别:f-女,m-男,all-全部
     * @param callback 回调接口
     * @return
     */
    public static long getRecommendDynamicList(Context context, int lat, int lng,
                                               int pagesize, long last, String type, int vipquery, String gender,
                                               HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("lat", lat);
        entity.put("lng", lng);
        entity.put("pagesize", pagesize);
        entity.put("last", last);
        entity.put("type", type);
        entity.put("vipquery", vipquery);
        entity.put("gender", gender);
//        return dynamicPost(context, "/dynamic/list_6_0", entity, callback);
        return dynamicPost(context, "/dynamic/list_7_0", entity, callback);
    }

    /**
     * 发布动态 photos 照片url（多张用,号隔开）线上的
     */
    public static long publishNewDynamic(Context context, int type,
                                         String title, String content, String photos, String address,
                                         String shortAddress, int lat, int lng, String url, String sync,
                                         String syncvalue, String sharesource, String sharevalue,
                                         HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("type", type);
        entity.put("title", title);
        entity.put("content", content);
        entity.put("photos", photos);
        entity.put("address", address);
        entity.put("lat", lat);
        entity.put("lng", lng);
        entity.put("url", url);
        entity.put("sync", sync);
        entity.put("syncvalue", syncvalue);
        entity.put("shortaddress", shortAddress);
        entity.put("sharesource", sharesource);
        entity.put("sharevalue", sharevalue);
        return dynamicPost(context, "/user/dynamic/add_5_6", entity, callback);
    }

    /**
     * 获取用户未读点赞历史列表
     */
    public static long getUnreadGreeterList(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        return dynamicPost(context, "/user/dynamic/message/mylikelist", entity,
                callback);
    }

    /**
     * 获取用户点赞历史消息列表
     */
    public static long getGreeterHistoryList(Context context, int pageno,
                                             int pagesize, long time, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        entity.put("time", time);
        return dynamicPost(context, "/user/dynamic/message/historylike", entity,
                callback);
    }

    /**
     * 获取用户未读评论
     */
    public static long getUserUnreadCommentList(Context context,
                                                HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        return dynamicPost(context, "/user/dynamic/message/mylist", entity,
                callback);
    }

    /**
     * 获取用户历史评论
     */
    public static long getUserHistoryCommentList(Context context, int pageno,
                                                 int pagesize, long lastTime, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        entity.put("time", lastTime);
        return dynamicPost(context, "/user/dynamic/message/history", entity,
                callback);
    }

    /**
     * 清空历史评论
     */
    public static long delAllCommentList(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        return dynamicPost(context, "/user/dynamic/message/delhistory", entity,
                callback);
    }

    /**
     * 评论用户动态
     * */
    public static long commentUserDynamic(Context context, long dynamicid,
                                          String content, long userid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        entity.put("content", content);
        entity.put("userid", userid);
        return dynamicPost(context, "/user/dynamic/reviews/add", entity,
                callback);
    }

    /**
     * 删除动态
     * */
    public static long deleteDynamic(Context context, long dynamicid,
                                     HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        return dynamicPost(context, "/user/dynamic/del", entity, callback);
    }

    /**
     * 删除动态评论
     * */
    public static long deleteDynamicComment(Context context, long dynamicid,
                                            long reviewid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("dynamicid", dynamicid);
        entity.put("reviewid", reviewid);
        return dynamicPost(context, "/user/dynamic/reviews/del", entity,
                callback);
    }
}
