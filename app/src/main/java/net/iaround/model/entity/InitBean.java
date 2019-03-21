package net.iaround.model.entity;

import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * Created by gaohang on 2017/3/14.
 */

public class InitBean extends BaseServerBean{

    public  int flag; //短信注册通道
    public  int available; //广告平台推广是否可用
    public  int gamecenter;//游戏中心是否可用
    public  int recommended;//推荐应用是否可用
    public String stat;  //一级统计事件
    public String statsub; ////二级统计事件
    public Commentinfo commentinfo;//渠道评论
    public int showcredits; //积分兑换中心是否可用
    public int showchatbar ;//在发现列表是否显示聊吧
    public int showthirdad ;//启动页是否显示第三方广告
    public int showregister;//是否显示注册按钮
    public String flagNet;//通信协议等级
    public int googleApp;//通信协议等级
    public int validWay;  //注册方式
    public String validkey;
    public String validUrl;
    public String advertInfo;
    public int showMigu;//是否显示乐游会员入口
    public int showManlian;//是否显示咪咕动漫联合会员入口
    public int giftDiamondMinNum;//发送礼物超过多少钻石数

    /*
    login3rd
    ALL_CLOSED(0), //第3方登录全关闭
    QQ_OPEN(1), //QQ
    WECHAT_OPEN(2), //微信
    V_OPEN(4), //微博
    Facebook(8),
    Twitter(16);
     */
    public int login3rd; //第三方登录按钮的开关
    public int qqReg; //QQ登录按钮的开关

    public ActiveUrl address;//所有动态地址

    public int defaultTopShow = 1;//新首页页面默认显示那个功能，1 陪玩；2 语音
    public int accompanyIsShow; //首页内容 0显示附近的人和语聊 1显示陪玩和语聊
    public int isShowVoice; //首页是否显示语聊，0-不显示 1-显示

    public class Commentinfo
    {
        public  String commenturl; //渠道评论的URL
        public  String packagename; //渠道评论的应用商城

    }
    public String url;//广告图\
    public String link;
    public String openurl;
    public int bannerid;

    public int[] loginflag;
    public int[] registerflag;

    public int defaultTab = 2;//首页默认显示页面
    public int relationswitch;//阻断状态0：不处理阻断,1：处理阻断
    public int defaultTopOption = 1;//附近默认显示页

    public int geetestSwitch;//极验验证开关 1 ：打开，0：关闭

    public GiftUpdateList giftUpdateList;

    public int bindPhoneSwitch;//登陆时绑定手机号开关 1 ：打开，0：关闭


    public static class GiftUpdateList{
        public String giftLastUpdate;//礼物最后更新时间
        public List<GiftPackage> list;//更新的礼物信息集合
    }

    public static class GiftPackage{
        public String animationId;
        public String downloadUrl;


        public GiftPackage() {
        }

        public GiftPackage(String animationId, String downloadUrl) {
            this.animationId = animationId;
            this.downloadUrl = downloadUrl;
        }
    }

    private class ActiveUrl{
        public String task;
        public String commonProblem;
        public String groupOwnerHelp;
        public String charmInstruction;
        public String iaroundProtocol;


    }

    /**
     * 设置动态地址
     */
    public void setActiveUrl(){
        Config.sSkillTask = address.task;
        Constants.iAroundFAQUrl = address.commonProblem;
        Constants.GroupHelpUrl = address.groupOwnerHelp;
        Config.sRankingHelp = address.charmInstruction;
        Config.USER_AGREEMENT_URL = address.iaroundProtocol;
    }

}
