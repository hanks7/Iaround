
package net.iaround.ui.datamodel;

import net.iaround.BaseApplication;
import net.iaround.tools.SharedPreferenceUtil;

import java.io.Serializable;


/**
 * @author tanzy
 * @Description: 大部分协议下发的User实体为此类型
 * 当此类有新增字段时，请在FriendModle的convertBaseUserToUser方法中添加相应的赋值语句
 * @date 2015-4-1
 */
public class BaseUserInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4997105832513474804L;
    public long userid;
    public String nickname;//用户昵称
    public String notename;//用户备注
    public String icon;
    public int vip;
    public int svip;//大于0为包月vip
    public int level; //user level
    public int lng;
    public int lat;
    public String selftext; //个人介绍
    public String isonline;
    public long lastonlinetime;
    public String weibo;
    public String notes;
    public int forbid;// 禁设备 0有效1被禁
    public int photonum;
    public int distance;// 距离 单位 m
    public int todayphotos;
    public int photouploadleft;
    public int todayphotostotal;
    public int photouploadtotal;
    public int occupation = -1; //职业
    public int viplevel = 0;//Vip等级，0：非Vip
    public int age = 18;//年龄
    /**
     * 性别（f:女；m:男)
     */
    public String gender;
    /**
     * 0:自己 ，1：好友 ，2：陌生人 3、关注 4、粉丝 5推荐
     */
    public int relation;
    public int contact;//类型 人脉关系 @ChatFromType
    public int charmnum; //魅力值 ，用于计算魅力等级， 6.5.0 版本，在user里面增加魅力值项
    public long expire; //会员到期时间
    public String bindphone;
    /**
     * 动态用户信息新增字段
     */
    public int horoscope;//星座
    public long birthday;//生日

    /**聊吧新增字段*/
    /**
     * 好友
     */
    public int cat;
    public int top;
    public int type;

    /**
     * 自己
     */
    public int mCat;
    public int mTop;
    public int mType;

    public int group_role;


    /**
     * 是否被选中
     */
    public boolean isSelect = false;

    public String verifyicon;//审核头像Url

    public int userType;//用户角色 0,普通用户 1,视屏主播
    public int notDisturb;//视屏主播免打扰状态
    public int voiceNotDisturb;//语音主播免打扰状态//0-正常，1-勿扰
    public int voiceUserType;//是否为语音主播 0-不是 1-是
    public int gameUserType;//是否为游戏主播 0-不是 1-是
    public int verifyPersion;//是否实名认证 0-没有 1-有
    public String verifyAlipay;//支付宝账户 如果已经绑定支付宝则不为空
    public String secret;//语音聊天密钥

    public User convertBaseToUser() {
        User user = new User();
        user.setUid(userid);
        user.setIcon(icon);
        user.setNickname(nickname);
        user.setNoteName(notename);
        user.setSex("m".equals(gender) ? 1 : 2);
        user.setAge(age);
        user.setLat(lat);
        user.setLng(lng);
        user.setViplevel(viplevel);
        user.setSVip(svip);
        user.setLevel(level);
        user.setForbid(forbid != 0);
        user.setPhotoNum(photonum);
        user.setJob(occupation);
        user.setLastLoginTime(lastonlinetime);
        user.setPhotouploadtotal(photouploadtotal);
        user.setPhotouploadleft(photouploadleft);
        user.setTodayphotos(todayphotos);
        user.setDistance(distance);
        user.setWeibo(weibo);
        user.setHoroscope(horoscope);
        user.setBindphone(bindphone);


        if (charmnum > 0) {
            user.setCharisma(charmnum);
        }
        if (expire > 0) {
            user.setExpire(expire);
        }

        /**
         * 聊吧新增字段
         * cat 1 魅力  2 财富
         * top 排名
         * type 本周 上周 本月 上月
         */
        user.setCat(cat);
        user.setTop(top);
        user.setDatatype(type);
        user.setVerifyicon(verifyicon);
        user.setUserType(userType);
        user.setVoiceUserType(voiceUserType);

        //陪玩新增字段
        user.setGameUserType(SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(userid + "game_user_type"));
        user.setVerifyPersion(verifyPersion);
        user.setVerifyAlipay(verifyAlipay);

        user.setSecret(secret);
        return user;
    }

    public void convertUserToBaseUserInfor(User user) {
        this.userid = user.getUid();
        this.icon = user.getIcon();
        this.nickname = user.getNickname();
        this.notename = user.getNoteName(false);
        this.age = user.getAge();
        this.gender = user.getGender();
        this.lat = user.getLat();
        this.lng = user.getLng();
        this.viplevel = user.getViplevel();
        this.svip = user.getSVip();
        this.level = user.getLevel();
        this.forbid = !user.isForbid() ? 0 : 1;
        this.photonum = user.getPhotoNum();
        this.occupation = user.getJob();
        this.lastonlinetime = user.getLastLoginTime();
        this.photouploadtotal = user.getPhotouploadtotal();
        this.photouploadleft = user.getPhotouploadleft();
        this.todayphotos = user.getTodayphotos();
        this.distance = user.getDistance();
        this.weibo = user.getWeiboString();
        this.charmnum = user.getCharisma();
        this.verifyicon = user.getVerifyicon();
        this.userType = user.getUserType();
        this.voiceUserType = user.getVoiceUserType();
        this.gameUserType = user.getGameUserType();
        this.verifyAlipay = user.getVerifyAlipay();
        this.verifyPersion = user.getVerifyPersion();
        this.secret = user.getSecret();
    }


    public static BaseUserInfo convertUserToBase(User user) {
        BaseUserInfo base = new BaseUserInfo();
        base.userid = user.getUid();
        base.icon = user.getIcon();
        base.nickname = user.getNickname();
        base.notename = user.getNoteName(false);
        base.age = user.getAge();
        base.gender = user.getGender();
        base.lat = user.getLat();
        base.lng = user.getLng();
        base.viplevel = user.getViplevel();
        base.svip = user.getSVip();
        base.level = user.getLevel();
        base.forbid = !user.isForbid() ? 0 : 1;
        base.photonum = user.getPhotoNum();
        base.occupation = user.getJob();
        base.lastonlinetime = user.getLastLoginTime();
        base.photouploadtotal = user.getPhotouploadtotal();
        base.photouploadleft = user.getPhotouploadleft();
        base.todayphotos = user.getTodayphotos();
        base.distance = user.getDistance();
        base.weibo = user.getWeiboString();
        base.verifyicon = user.getVerifyicon();
        base.userType = user.getUserType();
        base.voiceUserType = user.getVoiceUserType();
        base.gameUserType = user.getGameUserType();
        base.verifyAlipay = user.getVerifyAlipay();
        base.verifyPersion = user.getVerifyPersion();
        base.secret = user.getSecret();
        return base;
    }


}
