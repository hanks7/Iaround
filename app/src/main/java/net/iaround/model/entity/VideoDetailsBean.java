package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.User;

/**
 * 主播视频详情
 * Created by Administrator on 2017/12/13.
 */

public class VideoDetailsBean extends BaseServerBean {

    public VideoDetails info;

    public class VideoDetails {

        public int love;//爱心总量
        public int totallove;//爱心总量

        public long uid;
        public String nickName;
        public String gender;
        public String moodText;
        public int age;
        public int status; // 主播状态 1空闲  2热聊中  3勿扰
        public String notes;
        public String pic;
        public String video;
        public String firstvideopic;

        public int relation;
        public long lat;//经度
        public long lng;//维度
        public String icon;//头像
        public String landmarkname;//所在地
        public int sVip; // 包月会员等级 0为非vip  大于0-svip
        public int viplevel; // vip会员等级 0为非vip
        public int follow;// 请求接口的用户有没有关注此主播
        public int anchor_follow;// 主播有没有关注请求接口的用户

    }


}
