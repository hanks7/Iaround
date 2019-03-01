package net.iaround.conf;

/**
 * @author：liush on 2016/12/7 21:36
 */
public class Constants {

    public static final int ACCOUNT_TYPE = 8974;
    public static final int WALLET_TYPE = 1;
    //sdk appid 由腾讯分配
    public static final int SDK_APPID = 1400018953;

    /**
     * 用户的登录方式
     */
    public static final int ACCOUNT_LOGIN = 6;//用户以账号密码的方式进行登录的

    /**
     * 用户信息部分
     */
    public static String NICKNAME = "nickname";
    public static String SECRET = "secret"; //通过Intent将用户信息从UserInfoActivity传递到SecretActitivy
    public static String USERINFO = "userinfo"; //通过Intent将用户信息从UserInfoActivity传递到EditUserInfoActitivy
    public static String PHONE_FLAG = "phone_flag";
    public static String LOCATION_FLAG = "location_flag";
    public static String PHONE_NUM = "phone_num";
    public static String UID = "uid";

    public static int SEX_FEMALE = 0;                       //性别-女
    public static int SEX_MALE = 1;                         //性别-男
    public static int SEX_ALL = 2;                         //性别-男和女

    public static int USER_SVIP = 2;                        //用户超级VIP
    public static int USER_VIP = 1;                         //用户VIP
    public static int USER_DEFAULT = 0;                     //用户

    public static final int MY_DYNAMIC_RESTRICTION = 2;//TA看不到我的动态
    public static final int OTHERS_DYNAMIC_RESTRICTION = 3;//不看TA的动态

    public final static int UNKONW = 0;// 未知，或者不需要的时候用这个变量
    public final static int DynamicComment = 36; //动态评论

    public static int windowWidth = 0; // 屏幕宽度
    public static int windowHeight = 0; // 屏幕高度


    /**
     * 开启资料编辑界面所需要的传递的参数
     */
    public static String INFO_TITLE = "info_title";
    public static String INFO_CONTENT = "info_content";
    public static String EDIT_RETURN_INFO = "edit_return_info";
    public static String AUTHEN_PIC_STATUS = "authen_pic_status";
    public static String EDIT_BIRTHDAT_AGE = "edit_birth_age";

    /**
     * 跳转至验证密码界面时，用来指定意图
     */
    public static final String USER_INTENT = "user_intent";
    public static final int BIND_PHONE = 1;
    public static final int RELEASE_BIND_PHONE = 2;
    /**
     * 认证信息状态
     */
    public static final int PIC_AUTHEN_SUC = 1;
    public static final int PIC_AUTHEN_FAIL = 2;
    public static final int PIC_AUTHEN_NO = 3;
    public static final int PIC_AUTHEN_ING = 4;
    public static final String PHONE_AUTHEN_STATUS = "phone_authen_status";

    /**
     * 手机认证信息，0是未认证 1、已经认证
     */
    public static final int PHONE_AUTHEN_NO = 0;
    public static final int PHONE_AUTHEN_SUC = 1;
    public static final String HAVE_PASSWORD = "have_password";

    /**
     * 跳转至会员中心界面传递参数
     */
    public static final String USER_HEAD_URL = "head_url";
    public static final String USER_VIP_STATUS = "user_vip_status";

    /**
     * 跳转至充值界面
     * 1、代表我的钱包界面
     * 2、代表充值界面
     */
    public static final String USER_WALLET_PAGE_TYPE = "user_wallet_page_type";
    public static final int USER_WALLET_PAGE_WALLET = 1;
    public static final int USER_WALLET_PAGE_RECHARGE = 2;

    /**
     * 传递头像列表所需要的数据
     */
    public static final String HEAD_PIC_THUM = "head_pic_thum";
    public static final String HEAD_PIC = "head_pic";

    /**
     * 传递礼物列表所需要的数据
     */
    public static final String GIFT_LIST = "gift_list";

    /**
     * 恋爱状态
     * 1、一个人，单身
     * 11、两个人，恋爱中
     * 111、三个人，已婚
     * 0、保密
     */
    public static final int love_status_1 = 1;
    public static final int love_status_11 = 2;
    public static final int love_status_111 = 3;
    public static final int love_status_0 = 4;

    /**
     * 开关状态
     */
    public static final int LOACTION_FLAG_CLOSE = 0;
    public static final int LOACTION_FLAG_OPEN = 1;

    public static final int SHOW_PHONE_FLAG_CLOSE = 0;
    public static final int SHOW_PHONE_FLAG_OPEN = 1;

    /**
     * 数据库版本
     */
    public static final int LABLE_VERSION_VALUE = 10;
    public static final String LABLE_VERSION = "lable_version_value";
    public static final int LABLE_SPORT_START_INDEX = 0;
    public static final int LABLE_SPORT_END_INDEX = 199;
    public static final int LABLE_TRAVEL_START_INDEX = 200;
    public static final int LABLE_TRAVEL_END_INDEX = 399;
    public static final int LABLE_ART_START_INDEX = 400;
    public static final int LABLE_ART_END_INDEX = 599;
    public static final int LABLE_FOOD_START_INDEX = 600;
    public static final int LABLE_FOOD_END_INDEX = 799;
    public static final int LABLE_FUN_START_INDEX = 800;
    public static final int LABLE_FUN_END_INDEX = 999;
    public static final int LABLE_REST_START_INDEX = 1000;
    public static final int LABLE_REST_END_INDEX = 1199;
    public static final int LABLE_LABLE_START_INDEX = 1200;
    public static final int LABLE_LABLE_END_INDEX = 1399;
    public static final String LABLE_IDS = "lableId";

    public static final int LABLE_SPORT_POSITION = 0;
    public static final int LABLE_TRAVEL_POSITION = 1;
    public static final int LABLE_ART_POSITION = 2;
    public static final int LABLE_FOOD_POSITION = 3;
    public static final int LABLE_FUN_POSITION = 4;
    public static final int LABLE_REST_POSITION = 5;
    public static final int LABLE_MYLABLE_POSITION = 6;
    public static final String LABLE_SELECTED_POSITION = "lable_selected_position";

    public static final String LABLE_SELECTED_IDS = "lable_selected_ids";

    /**
     * webview Url
     */
    public static String iAroundFAQUrl = "http://www.iaround.com/m/newhelp/help_query.html";
    public static final String IAROUND_WEBSITE = "http://www.iaround.com/m/index.html?android=true";
    public static String GroupHelpUrl = "http://www.iaround.com/m/newhelp/help_chat.html";
    /**
     * 客服email地址
     */
    public static final String SERVICE_EMAIL = "cs@iaround.com";

    /**
     * 通知设置需要的参数
     */
//    public static final String HIDE_TALK_MSG_TEXT = "hide_talk_msg_text";
//    public static final String MESSAGE_VOICE = "message_voice";
//    public static final String MESSAGE_VIBRATION = "message_vibration";
//    public static final String ACTION_COMMENT = "action_comment";
//    public static final String RECEIVE_ACCOST = "receive_accost";
    public static final String LOGIN_OUT = "login_out";
    public static final int FROM_LOGOUT = 999;
    public static int START_MAINACTIVITY_TYPE = 0;

    /**
     * 隐私设置需要的参数
     */
    public static final String SECRET_SET_TYPE = "secret_set_type";
    public static final String SECRET_SELECT_TYPE = "secret_select_type";
//    /**
//     * 新版接口中：
//     * 1、黑名单
//     * 2、不看他动态
//     * 3、不让他看我动态
//     * 4、关注
//     * 5、粉丝
//     * 6、好友列表
//     */
//    public static final int SECRET_SET_BLACK_LIST = 1;
//    public static final int SECRET_SET_INVISIABLE_OTHER_ACTION = 2;
//    public static final int SECRET_SET_INVISIABLE_MYSELF_ACTION = 3;
//    public static final int SECRET_SET_FOCUS_LIST = 4;
//    public static final int SECRET_SET_FANS_LIST = 5;
//    public static final int SECRET_SET_FRIENDS_LIST = 6;
//    public static final int MATCHES_LIKE = 7;
//    public static final int MATCHES_DISLIKE = 8;

    /**
     * 线上接口中：
     * <p>
     * 1、隐身设置
     * 2、不让他看我动态
     * 3、不看他动态
     * 4、黑名单
     */
    public static final int SECRET_SET_INVISIBLE = 1;//隐身设置
    public static final int SECRET_SET_INVISIABLE_MYSELF_ACTION = 2;//不让ta看我的动态
    public static final int SECRET_SET_INVISIABLE_OTHER_ACTION = 3;//不看ta动态
    public static final int SECRET_SET_BLACK_LIST = 4;//黑名单

    /**
     * 线上接口中：
     * <p>
     * 1.好友列表
     * 2.关注列表
     * 3.粉丝列表
     */
    public static final int SECRET_SET_FRIENDS_LIST = 1;//好友列表
    public static final int SECRET_SET_FOCUS_LIST = 2;//关注列表
    public static final int SECRET_SET_FANS_LIST = 3;//粉丝列表

    public static final int MATCHES_LIKE = 7;
    public static final int MATCHES_DISLIKE = 8;
    public static final int SECRET_SET_HADLE_TYPE_ADD = 1;
    public static final int SECRET_SET_HADLE_TYPE_DELETE = 2;

    public static final String IS_REFRESH_DATA = "is_refresh_data";

    public static final String UNIVERSITY_FILE = "university.json";
    public static final String PROVINCE_FILE = "province.json";

    /**
     * 地图定位
     */
    public static final String AMAP_APIKEY = "8469e9cf9e7d9592b8351ef390c86759";
    public static double LATITUDE;
    public static final String LATITUDE_KEY = "latitude_key";
    public static double LONGITUDE;
    public static final String LONGITUDE_KEY = "longitude_key";
    public static String ADDRESS;
    public static final String ADDRESS_KEY = "address_key";
    public static final String STATIC_MAP_URL = "static_map_url";
    public static final String ACTION_LOG_IP = "http://actionlog.iaround.com";

    /**
     * 计算两次定位相差距离超过这个值之后，上传定位
     */
    public static final int MAX_DISTANCE = 500;

    /**
     * 编辑头像的时候用来记录将上传成功的图片插入的位置，默认-1即作为新头像
     */
    public static int EDIT_HEAD_INDEX = -1;

    /**
     * 匹配界面
     */
    public static final String OTHER_UID = "other_uid";
    public static final String OTHER_HEAD_PIC = "other_head_pic";
    public static final String OTHER_NICKNAME = "other_nickname";
    /**
     * 聊吧面板提示更新版本地址
     */
    public static final String CHAT_BAR_UPDATE_DIALOB_URL = "http://dl.iaround.com/iaround.apk";


    public static final String KEY_RTMP = "rtmp";

    public static final String KEY_HLS = "Hls";

    public static final String FIRST_ANCHOR = "first";

    public static final String KEY_MIX_STREAM_ID = "mixStreamID";



    public static final String charmWeek = "charmWeek";
    public static final String charmMonth = "charmMonth";
    public static final String regalWeek = "regalWeek";
    public static final String regalMonth = "regalMonth";
    public static final String activeWeekSkill = "activeWeekSkill";
    public static final String activeMonthSkill = "activeMonthSkill";
    public static final String updateWeekSkill = "updateWeekSkill";
    public static final String updateMonthSkill = "updateMonthSkill";

    public static final int AUDIO_CHAT_GAME_ID = 13;//新版语音聊天 id

}
