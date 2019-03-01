
package net.iaround.conf;


import net.iaround.BaseApplication;
import net.iaround.BuildConfig;
import net.iaround.tools.SharedPreferenceUtil;

/**
 * 系统配置d
 *
 * @author linyg
 */
public class Config {
    public static boolean DEBUG = BuildConfig.DEBUG;

    //发布新包时需要修改
    public final static String APP_VERSION = "7.6.5"; // 软件版本号
    public static boolean isShowGoogleApp = false;   //是否显示Google支付

    public final static int PLAT = 1; // android平台
    public final static long CUSTOM_SERVICE_UID = 114; // 客服id
    public final static long SYSTEM_UID = 1000; // 系统占用的id

    public final static long CREATE_DATETIME = 1452161413000L; //1452161413000L; // // 2016/1/7 18:10:13
    public final static boolean SHOW_UPDATE_INSTALL = true; // 是否进行升级检测
    public final static boolean BACK_SERVICE_ENABLE = true; // 是否启动后台服务
    public static String sGameCenter = "";//游戏中心的业务地址

    public static String iAroundPayFAQUrl = "http://www.iaround.com/m/newshelp/help_buy.html"; //遇见充值帮助地址

    public static String USER_AGREEMENT_URL = "http://www.iaround.com/m/protocol.html"; //遇见用户协议

    public static int MOBILE_TYPE = 0; // 手机类型 0为正规机，3为3码机，5为5码机

    // 广点通appid
    public static String GDT_APPID = "1106089046";
    // 开屏广告id
    public static String GDT_SPLASH_POST_ID = "2000027178337696";
    public static String GDT_NATIVE_POST_ID = "2090022189135476";

    //InMobi
    public static String INMOBI_APPID = "b371b709a08341c98724dff817a526d6";
    public static long INMOBI_SPLASH_PLACEMENTID = 1486822237801L;
    public static long INMOBI_NEARBY_PLACEMENTID = 1490240363447L;
    public static long INMOBI_DYNAMIC_PLACEMENTID = 1487971969831L;

    public static boolean isTestServer = false;//加密协议密钥，切勿打开

    public static final int TEST_SERVER = 3; // 1测试服debug,2测试服正式打包，3正式服服debug包，4正式服正式打包

    public static boolean isShowThridSplashAd = false; //system load 接口里的 广告 url，遇见自己开发的广告URL

    public final static String[] loginUrls = new String[]{"http://portal.iaround.com", "http://portal.iaround.net"};

    // 登陆服务请求地址
    public static String loginUrl = "http://portal.iaround.com";  // 外网

    public static String pushUrl = "http://push.iaround.com"; // 推送地址
    public static String sMonthlyPay = "https://g.10086.cn:5443/h5pay/api/ygPay?";
    public static String sMiguWebsite = "http://112.4.3.52:8310/SubscribeManageService/services/SubscribeManage/unSubscribeProduct";// 咪咕退订
    public static String sSkillTask = "http://notice.iaround.com/active_2017_08_14/index.html";// 技能任务
    public static String sRankingHelp = "http://www.iaround.com/m/rank_help_4/rank_help.html";// 排行榜帮助

    // 以下地址为动态获取
    public static String sPictureHost = "http://upi.iaround.com:8080/file/uploadByType"; // 图片上传地址
    public static String sPayHost = ""; // 支付地址
    public static String sPhotoHost = ""; // 照片接口地址
    public static String sNearHost = ""; // 附近有关
    public static String sDynamic = "";//动态的业务地址

    public static String sPictureUrlBak = "";// 作为域名解析有误，实行的替换方案

    public static String sFaceHost = "http://p1.iaround.com/face/";
    public static String sVideoHost = ""; // 视频上传地址
    public static String sBusinessHost = "";// 业务请求地址
    public static String sGroupHost = ""; // group请求地址
    public static String sFriendHost = ""; // 好友关系
    public static String sGameHost = ""; // 游戏地址
    public static String sSocialgame = "";//搭讪游戏的业务地址
    public static String sRoombiz = "";//聊天室业务地址
    public static String sChatBar = "";//聊吧业务地址
    public static String sGoldHost = ""; // 金币模块
    public static String sRelaction = "";//关系的业务地址
    public static String sMicL = "";//聊吧拉流业务地址
    public static String sMicT = "";//聊吧推流业务地址


    public static String sGetGameChatInfo = "http://portal.iaround.com";//首页地址（陪玩业务 域名）
//    public static String sGetGameChatInfo = "http://192.168.2.204";//首页地址 测试接口
//    public static String sAudioHost = "";//上传音频地址

    public static String sServerMsgHost = "http://fail.iaround.com/index.html"; // 服务器维护地址

    public static final String VIP_AGREEMENT_URL = "http://www.iaround.com/m/vipdetail/us-en/word.html|"
            + "http://www.iaround.com/m/vipdetail/zh-cn/word.html|"
            + "http://www.iaround.com/m/vipdetail/zh-tw/word.html";

    public static String IM_HOST_NAME =  "vchat.iaround.com"; //"192.168.100.5"; //视频服务器地址
    public static int IM_HOST_PORT =     8101;  //视频服务器端口

    public static String WS_AUDIO_CHAT_URL = "voice.iaround.com:9501";//语音聊天webSocket地址

    //魅力等级说明
    public static String getlevelDescUrl(int language) {
        String a = "http://www.iaround.com/m/levelDesc-";
        String c = ".html";
        String b = "en";
        if (language == 2)
            b = "cn";
        else if (language == 3)
            b = "tw";
        return a + b + c;
    }

    //金币获取说明
    public static String getGoldDescUrl(int language)
    {
        String a = "http://notice.iaround.com/voice/index";
        String c = ".html#1F";
        return a + c;
    }
    //金币获取说明
    public static String getGoldDescUrlNew(int language)
    {
//        String a = "http://notice.iaround.com/voice_2017_07_03/index";
        String a = "http://notice.iaround.com/voice_2017_07_03/index";
        String c = ".html#footer";
        return a + c;
    }


    //一对一视频总开关 1开 0关
    public static int getVideoChatOpen(){
        synchronized (Config.class){
            int video = 0;
            try {
                video = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt("IAROUND_VIDEO_CHAT_OPEN", 0);
            }catch (Exception e){
                e.printStackTrace();
            }
            return video;
        }
    }

    public static void setVideoChatOpen(int video){
        synchronized (Config.class){
            try {
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt("IAROUND_VIDEO_CHAT_OPEN", video);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //主播版本开关 1开 0关
    public static int getAnchorVersionOpen(){
        synchronized (Config.class){
            int video = 0;
            try {
                video = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt("IAROUND_ANCHOR_VERSION_OPEN", 0);
            }catch (Exception e){
                e.printStackTrace();
            }
            return video;
        }
    }

    public static void setAnchorVersionOpen(int open){
        synchronized (Config.class){
            try {
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt("IAROUND_ANCHOR_VERSION_OPEN", open);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
