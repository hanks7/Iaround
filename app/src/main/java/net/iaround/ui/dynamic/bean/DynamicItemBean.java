package net.iaround.ui.dynamic.bean;

import android.text.TextUtils;

import net.iaround.conf.Common;
import net.iaround.model.entity.InitBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.DynamicDetailBaseBean;
import net.iaround.model.im.DynamicLoveInfo;
import net.iaround.model.im.Me;
import net.iaround.model.im.SyncInfo;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.PostbarUserInfo;
import net.iaround.ui.postbar.bean.PostbarTopicDetailInfo;

import java.io.Serializable;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-13 上午11:32:37
 * @Description: 单条动态的实体
 */
public class DynamicItemBean extends BaseServerBean implements Serializable, Comparable<DynamicItemBean>, DynamicDetailBaseBean {


    /**
     *
     */
    private static final long serialVersionUID = -6457778577098838550L;
    public static final byte SUCCESS = 0;
    public static final byte SENDDING = 1;
    public static final byte FAIL = 2;

    private byte sendStatus;//动态的发送状态 0:表示发送成功 1:表示发送中 2:表示发送失败

    public long curSendSucessTime; //动态发布成功的时间，
    public long dynamicUserid;//动态发布者ID；


    private DynamicUserBean user;//动态发表用户信息，note：我的动态中，user显示为null
    private DynamicInfo dynamic;//动态内容
    private DynamicLoveInfo loveinfo;//点赞实体
    private DynamicReviewInfo reviewinfo;//评论实体
    private SyncInfo syncinfo;//同步来自或者到哪里
    private ShareInfo shareinfo;//分享来自哪里

    private DynamicPublishBean publishInfo;//用来保存发布动态时候的信息,以备重发

    //在动态详情和个人动态中才有点赞实体和评论实体，动态中心中只显示点赞数和评论数
    public boolean isCurrentHanleView = false;//是否当前点赞或取消点赞的View
    public byte curruserlove;//当前用户是否点赞（0-否，1-是）
    public int likecount;//点赞数
    public int reviewcount;//评论数


    public void setPublishInfo(DynamicPublishBean value) {
        publishInfo = value;
    }

    public DynamicPublishBean getPublishInfo() {
        return publishInfo;
    }

    /**
     * 设置动态用户
     */
    public void setDynamicUser(DynamicUserBean value) {
        user = new DynamicUserBean();
        user.userid = value.userid;
        user.nickname = value.getNickName();
        user.age = value.age;
        user.icon = value.getIcon();
        user.viplevel = value.viplevel;
        user.svip = value.svip;
        user.gender = value.getGender();
        user.contact = value.contact;
        user.relation = value.relation;
        user.charmnum = value.charmnum;
        /**
         * 添加字段
         * 星座
         */
        user.horoscope = value.horoscope;
        user.birthday = value.birthday;
    }
    /**
     * 设置动态用户
     */
    public void setDynamicUser()
    {
        user = new DynamicUserBean();
        Me me = Common.getInstance().loginUser;
        user.userid = me.getUid();
        user.nickname = me.getNickname();
        user.age = me.getAge();
        user.icon = me.getIcon();
        user.viplevel = me.getViplevel();
        user.svip = me.getSVip();
        user.gender = me.getGender();
        /**
         * 添加字段
         * 星座
         */
        user.horoscope = me.getHoroscopeIndex();
    }
    /**
     * 设置动态用户
     */
    public void setDynamicUser(User me)
    {
        user = new DynamicUserBean();
        user.userid = me.getUid();
        user.nickname = me.getNickname();
        user.age = me.getAge();
        user.icon = me.getIcon();
        user.viplevel = me.getViplevel();
        user.svip = me.getSVip();
        user.gender = me.getGender();
        user.distance = me.getDistance();
        /**
         * 添加字段
         * 星座
         */
        user.horoscope = me.getHoroscopeIndex();
    }
    /**
     * 获取动态用户,如果user为空,返回null
     */
    public DynamicUserBean getDynamicUser() {
        return user;
    }

    /**
     * 设置动态信息
     */
    public void setDynamicInfo(DynamicInfo value) {
        if (dynamic == null) {
            dynamic = new DynamicInfo();
        }

        dynamic.dynamicid = value.dynamicid;
        dynamic.userid = value.userid;
        dynamic.setTitle(value.getTitle());
        dynamic.setContent(value.getContent());
        dynamic.type = value.type;
        dynamic.setPhotoList(value.getPhotoList());
        dynamic.distance = value.distance;
        dynamic.setAddress(value.getAddress());
        dynamic.datetime = value.datetime;
        dynamic.setUrl(value.getUrl());
        dynamic.ishot = value.ishot;
        dynamic.synctype = value.synctype;
        dynamic.sharesource = value.sharesource;
        dynamic.dynamiccategory = value.dynamiccategory;
        dynamic.dynamicsource = value.dynamicsource;
        dynamic.parentid = value.parentid;
        dynamic.setshareusername(value.getshareusername());
    }

    /**
     * 获取动态信息
     */
    public DynamicInfo getDynamicInfo() {
        return dynamic == null ? new DynamicInfo() : dynamic;
    }

    /**
     * 设置点赞实体
     */
    public void setDynamicReviewInfo(DynamicReviewInfo value) {
        if (value == null) {
            reviewinfo = null;
            return;
        }
        if (reviewinfo == null) {
            reviewinfo = new DynamicReviewInfo();
        }

        reviewinfo.total = value.total;
        reviewinfo.reviews.clear();
        if (value.reviews != null) {
            reviewinfo.reviews.addAll(value.reviews);
        }

    }

    /**
     * 获取评论实体,如果为空就返回null
     */
    public DynamicReviewInfo getDynamicReviewInfo() {
        return reviewinfo;
    }

    /**
     * 设置点赞实体
     */
    public void setDynamicLoveInfo(DynamicLoveInfo value) {
        if (value == null) {
            loveinfo = null;
            return;
        }

        if (loveinfo == null) {
            loveinfo = new DynamicLoveInfo();
        }

        loveinfo.total = value.total;
        loveinfo.curruserlove = value.curruserlove;
        loveinfo.loveusers.clear();
        if (value.loveusers != null) {
            loveinfo.loveusers.addAll(value.loveusers);
        }

    }

    /**
     * 获取点赞实体,如果为空就返回null
     */
    public DynamicLoveInfo getDynamicLoveInfo() {
        return loveinfo;
    }


    //初始化动态的用户id和昵称
    public void initDynamicUser(User sourceUser) {
        user = new DynamicUserBean();
        user.userid = sourceUser.getUid();
        user.nickname = sourceUser.getNickname();
        user.setNotename(sourceUser.getNoteName(false));
        user.age = sourceUser.getAge();
        user.icon = sourceUser.getIcon();
        user.viplevel = sourceUser.getViplevel();
        user.svip = sourceUser.getSVip();
        user.gender = sourceUser.getGender();
        user.relation = sourceUser.getRelationship();
        user.charmnum = sourceUser.getCharisma();

    }

    //动态中的用户信息
    public class DynamicUserBean extends BaseUserInfo implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = -8112781283521258513L;
//		public long userid;//用户id
//		private String nickname;//用户昵称
//		private String notename;//用户备注
//		private String icon;//用户头像
//		public int viplevel;//Vip等级，0：非Vip
//		public int svip;//sVip标识，0：非Vip
//		public int age;//年龄
//		private String gender;//性别（f:女；m:男)
//		public int contact;//类型 人脉关系 @ChatFromType
//		public int relation;//@User 0:自己 ，1：好友 ，2：陌生人 3、关注 4、粉丝 5推荐
        /**
         * 动态用户添加字段
         *
         * @return
         */
        public int horoscope;//星座
        public int birthday;//生日

        /**
         * 星座
         */
        public int getHoroscope() {
            return horoscope;
        }

        public void setHoroscope(int horoscope) {
            this.horoscope = horoscope;
        }

        /**
         * 生日
         */
        public int getBirthday() {
            return birthday;
        }

        public void setBirthday(int birthday) {
            this.birthday = birthday;
        }

        /**
         * 设置用户昵称
         */
        public void setNickName(String value) {
            nickname = value;
        }

        /**
         * 获取用户昵称
         */
        public String getNickName() {
            return nickname == null ? "" : nickname;
        }

        /**
         * 设置用户头像
         */
        public void setIcon(String value) {
            icon = value;
        }

        /**
         * 获取用户头像
         */
        public String getIcon() {
            return icon == null ? "" : icon;
        }

        /**
         * 设置用户性别
         */
        public void setGender(String value) {
            gender = value;
        }

        /**
         * 获取用户性别
         */
        public String getGender() {
            return gender == null ? "" : gender;
        }

        /**
         * 判断是否男性,如果gender字段为null默认为女性
         */
        public boolean isMale() {
            return !(gender == null || gender.equals("f"));
        }

        public String getNotename() {
            if (TextUtils.isEmpty(notename)) {
                return getNickName();
            }
            return notename;
        }

        public void setNotename(String notename) {
            this.notename = notename;
        }
    }

    /**
     * 获取同步信息
     */
    public SyncInfo getSyncinfo() {
        return syncinfo;
    }

    /**
     * 获取分享信息
     */
    public ShareInfo getShareinfo() {
        return shareinfo;
    }


    /**
     * 判断当前动态是否已经点赞
     */
    public boolean isCurrentLove() {
        return curruserlove == 1;
    }

    /**
     * 判断动态发送是否失败
     */
    public boolean isSendFail() {
        return sendStatus == FAIL;
    }

    /**
     * 获取动态的状态
     */
    public byte getSendStatus() {
        return sendStatus;
    }

    /**
     * 设置动态的状态
     */
    public void setSendStatus(byte value) {
        sendStatus = value;
    }

    @Override
    public int compareTo(DynamicItemBean arg0) {
        //倒序排列
        return -Long.valueOf(getDynamicInfo().datetime).compareTo(Long.valueOf(arg0.getDynamicInfo().datetime));
    }

    public ThemeTopicExtendInfo converTopicExtendInfo(DynamicItemBean bean) {
        ThemeTopicExtendInfo topicBean = new ThemeTopicExtendInfo();
        topicBean.curruserlike = bean.curruserlove;
        topicBean.likecount = bean.likecount;
        topicBean.reviewcount = bean.reviewcount;
        topicBean.user = new PostbarUserInfo();
        topicBean.user.userid = bean.getDynamicUser().userid;
        topicBean.user.nickname = bean.getDynamicUser().nickname;
        topicBean.user.age = bean.getDynamicUser().age;
        topicBean.user.icon = bean.getDynamicUser().getIcon();
        topicBean.user.viplevel = bean.getDynamicUser().viplevel;
        topicBean.user.gender = bean.getDynamicUser().getGender();
        topicBean.user.svip = bean.getDynamicUser().svip;
        topicBean.topic = new PostbarTopicDetailInfo();
        topicBean.topic.content = bean.getDynamicInfo().getContent();
        topicBean.topic.address = bean.getDynamicInfo().getAddress();
        topicBean.topic.datetime = bean.getDynamicInfo().datetime;
        topicBean.topic.distance = bean.getDynamicInfo().distance;
        topicBean.topic.ishot = bean.getDynamicInfo().ishot;
        topicBean.topic.postbarid = bean.getDynamicInfo().parentid;
        topicBean.topic.topicid = bean.getDynamicInfo().dynamicid;
        topicBean.topic.userid = bean.getDynamicInfo().userid;
        topicBean.topic.photos = bean.getDynamicInfo().getPhotoList();
        topicBean.topic.sync = String.valueOf(bean.getDynamicInfo().synctype);
        topicBean.topic.type = bean.getDynamicInfo().type;
        topicBean.topic.label = bean.getDynamicInfo().getLabel();
        topicBean.topic.labelurl = bean.getDynamicInfo().getLabelurl();

        return topicBean;
    }
}
