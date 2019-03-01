package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;

/**
 * 主播模块
 * Created by Administrator on 2017/12/8.
 */

public class AnchorsCertificationProtocol {

    public static final String getAnchorList = "/v1/anchor/getanchorlist";//获取主播列表
    public static final String setAnchorDisturbStatus = "/v1/anchor/setanchorstatus";//主播勿扰
    public static final String setAnchorDisturbAnchorPic = "/v1/anchor/setanchorpic";//设置主播认证
    public static final String getAnchorDisturbAnchorStata = "/v1/anchor/getanchorstatus";//获取主播认证
    public static final String getVideoDetails = "/v1/anchor/getanchorinfo";//获取主播视频详情
    public static final String getVideoHistoryList = "/v1/anchor/getconnectrecord";//获取视频历史
    public static final String getSpendRecord = "/v1/anchor/getspendrecord";//获取自己的礼物收益列表
    public static final String getGiftLoveRecord = "/v1/anchor/getgiftloverecord";//获取主播的礼物收益列表
    public static final String getAnchorEarn = "/v1/anchor/getanchorearn";//获取主播每日明细列表
    public static final String getAnchorAudioDetails = "/v1/voicechat/getvoiceanchordetail";//获取语音主播详情


    public static long groupPost(Context context, String url, LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map, ConnectorManage.HTTP_LOGIN, callback);
    }

    /**
     * 获取主播列表
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     * @return
     */
    public static long getAnchorList(Context context, int pageno, int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        return groupPost(context, getAnchorList, map, callback);
    }

    /**
     * 主播打扰状态
     *
     * @param context
     * @param status
     * @param callback
     * @return
     */
    public static long setAnchorDisturbStatus(Context context, int status, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("DisturbStatus", status);
        return groupPost(context, setAnchorDisturbStatus, map, callback);
    }

    /**
     * 主播认证
     *
     * @param context
     * @param pic
     * @param video
     * @param callback
     * @return
     */
    public static long setAnchorDisturbAnchorPic(Context context, String pic, String video, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("Pic", pic);
        map.put("Video", video);
        return groupPost(context, setAnchorDisturbAnchorPic, map, callback);
    }

    /**
     * 获取主播认证
     *
     * @param context
     * @param callback
     * @return
     */
    public static long getAnchorDisturbAnchor(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        return groupPost(context, getAnchorDisturbAnchorStata, map, callback);
    }

    /**
     * 获取主播视频详情
     *
     * @param uid
     * @param context
     * @param callback
     * @return
     */
    public static long getAnchorVideoDetails(Context context, long uid, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("targetUid", uid);
        return groupPost(context, getVideoDetails, map, callback);
    }


    /**
     * 获取视频记录
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     * @return
     */
    public static long getVideoHistoryList(Context context, int pageno, int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        return groupPost(context, getVideoHistoryList, map, callback);
    }

    /**
     * 获取自己的礼物收益列表
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     * @return
     */
    public static long getSpendRecord(Context context, int pageno, int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        return groupPost(context, getSpendRecord, map, callback);
    }

    /**
     * 获取主播的礼物收益列表
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     * @return
     */
    public static long getGiftLoveRecord(Context context, int pageno, int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        return groupPost(context, getGiftLoveRecord, map, callback);
    }

    /**
     * 获取主播每日明细列表
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     * @return
     */
    public static long getgetAnchorEarn(Context context, int pageno, int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pageno", pageno);
        map.put("pagesize", pagesize);
        return groupPost(context, getAnchorEarn, map, callback);
    }

    /**
     * 获取主播语音详情
     *
     * @param uid
     * @param context
     * @param callback
     * @return
     */
    public static long getAnchorAudioDetails(Context context, long uid, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("targetUid", uid);
        return groupPost(context, getAnchorAudioDetails, map, callback);
    }

}
