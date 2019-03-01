package net.iaround.ui.friend.bean;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/21.
 */

public class VideoChatBean extends BaseServerBean
{
    public long datetime;
    public NewFansUserInfoBean userinfo;
    public String talkTime;
    public int videoState;



    public int pageno;
    public int pagesize;
    public int amount;

    public List<VideoChatItem> list;

    public class VideoChatItem {

        public long otheruid;//对方ID
        public String nickname; //昵称
        public String icon;//头像
        public int vip; //vip标识
        public int viplevel; //vip等级
        public int svip;//终身vip 0:不是 1:终身
        public String note;//备注名称

        public int is_anchor;//是否是主播
        public int video_finish_type;//视频聊天结束类型
        public long video_time;//视频聊天持续时长
        public long end_time;//视频聊天结束时间

    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public NewFansUserInfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(NewFansUserInfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public String getTalkTime() {
        return talkTime;
    }

    public void setTalkTime(String talkTime) {
        this.talkTime = talkTime;
    }

    public int getVideoState() {
        return videoState;
    }

    public void setVideoState(int videoState) {
        this.videoState = videoState;
    }
}