
package net.iaround.model.login.bean;

import net.iaround.conf.Config;
import net.iaround.connector.ConnectGroup;
import net.iaround.connector.ConnectSession;
import net.iaround.tools.CommonFunction;

public class UrlInfo {
    public String business; // 业务服地址
    public String picture; // 图片上传地址
    public String video; // 视频上传地址
    public String sound; // 声音上传地址
    public String pay; // 支付地址
    public String sms;
    public String group; // group请求地址
    public String gold; // gold请求地址
    public String imgurlbak;// 图片备用域名
    public String photo; // photo请求地址
    public String friend;//friend请求地址
    public String near;//附近用户
    public String session;
    public String groupsession;
    public String recommend;
    public String game;
    //v5.4
    public String socialgame;//socialgame请求地址
    //V5.5
    public String dynamic;//动态
    public String relation;//人脉关系
    //5.6
    public String postbar;//贴吧
    public String gamecenter;//游戏中心
    //6.4
    public String chatbar;//聊吧session
    public String chatbarbiz;//聊吧biz
    public String roombiz;

    //7.0.3
    public String push_stream;
    public String pull_stream;

    //7.2.1
    public String fiximgurl;// 图片域名替换配置

    public String videochat;// 通讯服务

    public void setUrl() {
        Config.sBusinessHost = business;
        Config.sPictureHost = picture + "/file/uploadByType";
        Config.sVideoHost = video + "/file/uploadByType";
        Config.sPayHost = pay;  //"http://dev.pay.iaround.com";
        Config.sGroupHost = group;
        Config.sGoldHost = gold;
        Config.sPhotoHost = photo;
        Config.sFriendHost = friend;
        Config.sNearHost = near;
        Config.sGameHost = game;
        Config.sDynamic = dynamic;
        Config.sRelaction = relation;
        Config.sGameCenter = gamecenter;
        Config.sRoombiz = roombiz;
        Config.sChatBar = chatbarbiz;
        Config.sPictureUrlBak =  fiximgurl;//imgurlbak;
        Config.sMicT = push_stream;
        Config.sMicL = pull_stream;

        if(CommonFunction.isEmptyOrNullStr(videochat) == false) {
            String[] strs = videochat.split(":");
            if(strs!=null && strs.length==2){
                Config.IM_HOST_NAME = strs[0];
                Config.IM_HOST_PORT =  Integer.valueOf(strs[1]);
//                Config.IM_HOST_NAME = "192.168.100.5";
//                Config.IM_HOST_PORT = 8101;
            }
        }
        ConnectSession.setSessionAddress(session);
        ConnectGroup.setGroupAddress(groupsession);
    }

}
