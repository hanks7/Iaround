
package net.iaround.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sina.weibo.sdk.api.share.Base;

import net.iaround.BaseApplication;


/**
 * 以shareprefence方式存储数据
 *
 * @author linyg
 */
public class SharedPreferenceUtil {
    private static SharedPreferenceUtil sharedPreferenceUtil;
    private static SharedPreferences sharedPreferences;
    private String SAVE_PUBLIC_KEY = "snPE6LHB8S8GLN96";
    private final static String KEY = "meyou_sharepreferences";
    //Im变量
    public final static String KEYWORD_VERSION = "keyword_version"; // 关键字版本
    // 用于保存与其他用户之间的关系，需要加上用户的uid作为key使用(ACCOST_RELATION + uid)
    public final static String ACCOST_RELATION = "accost_relation";
    public final static String IS_ENTRY_MAIN_ACTIVITY = "isEntryMain"; //是否已经进入了MainActivity

    /**
     * 验证
     */
    public final static String IAROUND_SESSIONKEY = "sessionkey";
    public final static String LOGIN_SUCCESS_DATA = "success_login_data";
    public final static String GEO_LAT = "geo_lat"; // 经纬度
    public final static String GEO_LNG = "geo_lng";
    public final static String GEO_ADDRESS = "geo_address";
    public final static String GEO_SIMPLE_ADDRESS = "geo_simple_address";
    public final static String DEVICEID = "device_id"; // 设备唯一码
    public final static String ACCESS_TOKEN = "access_token";
    public final static String USER_ID = "USER_ID";
    public final static String USER_AVATAR = "USER_AVATAR";
    public final static String USER_PROFILE = "USER_PROFILE";
    public final static String CHAT_SIGNTURE = "CHAT_SIGNTURE";
    public final static String CHAT_BAR_WELCOME_GROUP_ID = "CHAT_BAR_WELCOME_GROUP_ID";//一分钟进入聊吧不进行提示的key
    public final static String CHAT_BAR_WELCOME_BY_OWNER = "CHAT_BAR_WELCOME_BY_OWNER";//1小时进入聊吧多次不进行以吧主身份提示的key
    public final static String GIFT_DIAMOND_MIN_NUM = "GIFT_DIAMOND_MIN_NUM";//发送礼物超过多少钻石发送世界消息
    public final static String CHAT_BAR_SAVE_WORLD_MESSAGE = "CHAT_BAR_SAVE_WORLD_MESSAGE";//发送礼物超过多少钻石发送世界消息
    /***** 使用第三方接口开关 ---大众点评---- **************/
    public final static String SWITCH = "switchs";
    /**
     * 最近使用的表情
     **/
    public final static String LAST_USED_FACE = "last_used_face";

    /**
     * 登录方式: 1遇见账号（邮箱或id），2 新浪微博，3 QQ，4 Facebook，5 Twitter，6 其他
     */
    public static final String LOGIN_TYPE = "login_type";
    public static final String LOGIN_TOKEN_SINA = "login_token_sina"; // 微博登陆token
    public static final String LOGIN_ID_SINA = "login_id_sina"; // 微博登陆id
    public static final String LOGIN_EXPIRES_SINA = "login_expires_sina"; // 微博登陆expires
    public static final String LOGIN_TOKEN_QQ = "login_token_qq"; // qq登陆token
    public static final String LOGIN_ID_QQ = "login_id_qq"; // qq登陆id
    public static final String LOGIN_EXPIRES_QQ = "login_expires_qq"; // qq登陆expires
    public static final String LOGIN_TOKEN_FACEBOOK = "login_token_facebook"; // facebook登陆token
    public static final String LOGIN_ID_FACEBOOK = "login_id_facebook"; // facebook登陆id
    public static final String LOGIN_EXPIRES_FACEBOOK = "login_expires_facebook"; // facebook登陆expires
    public static final String LOGIN_TOKEN_TWITTER = "login_token_twitter"; // twitter登陆token
    public static final String LOGIN_ID_TWITTER = "login_id_twitter"; // twitter登陆id
    public static final String LOGIN_EXPIRES_TWITTER = "login_expires_twitter"; // twitter登陆expires

    public static final String LOGIN_TOKEN_WECHAT = "login_token_wechat"; // wechat登陆token
    public static final String LOGIN_ID_WECHAT = "login_id_wechat"; // wechat登陆openid
    public static final String LOGIN_UNIONID_WECHAT = "login_union_wechat"; // wechat登陆unionid
    public static final String LOGIN_EXPIRES_WECHAT = "login_expires_wechat"; // wechat登陆expires

    // 匹配筛选
    public final static String ENCOUNTER_FILTER_MAX_AGE = "encounter_filter_max_age";
    public final static String ENCOUNTER_FILTER_MIN_AGE = "encounter_filter_min_age";
    public final static String ENCOUNTER_FILTER_GENDER = "encounter_filter_gender";

    // 动态筛选
    public final static String DYNAMIC_FILTER_MAX_AGE = "dynamic_filter_max_age";
    public final static String DYNAMIC_FILTER_MIN_AGE = "dynamic_filter_min_age";
    public final static String DYNAMIC_FILTER_GENDER = "dynamic_filter_gender";

    // 附近人筛选
    public final static String NEAR_FILTER_MAX_AGE = "near_filter_max_age";
    public final static String NEAR_FILTER_MIN_AGE = "near_filter_min_age";
    public final static String NEAR_FILTER_GENDER = "near_filter_gender";
    public final static String NEAR_FILTER_CONSTELLATION = "near_filter_constellation";
    public final static String NEAR_FILTER_TIME = "near_filter_time";

    //启动页
    public final static String FIRST_OPEN = "first_open";

    /**
     * 推送警告次数
     */
    public final static String PUSH_WARN_NUMBER = "push_warn_number";
    /**
     * 推送警告时间
     */
    public final static String PUSH_WARN_TIME = "push_warn_time";
    /**
     * 是否展示兑换积分项  0不展示，1展示
     */
    public final static String SHOWCREDITS_AVAILABLE = "showcredits_available";
    /**
     * 最近玩过的游戏
     **/
    public final static String CHAT_RECENT_GAME_TIPS = "chat_recent_game_tips";
    /**
     * 保存用户邂逅数据
     */
    public final static String USER_MEET_DATA = "user_meet_data";

    /**
     * 聊吧技能升级弹窗状态（存贮上一次弹窗的时间）
     */
    public final static String GROUO_CHAT_BAR_SKILL_UPDATE = "grouo_chat_bar_skill_update";

    /**
     * 聊吧底部任务状态（存贮上一次弹窗的时间）
     */
    public final static String GROUO_CHAT_BAR_TASK_READ_STATE = "grouo_chat_bar_task_read_state";

    //IM
    /**
     * 更新国家地区和语言的最近更新时间
     */
    public final static String UPDATE_COUNTRY_AND_LANGUAGE_TIME = "update_country_and_language_time";
    public final static String VOICE_SETTINGS = "voice_settings"; // 新安装
    /**
     * 振动
     **/
    public final static String VIBRATE_ENABLE = "vibrate_enable";
    /**
     * 切换到主菜单震动状态
     **/
    public final static String VIBRATE_ENABLE_BACK = "vibrate_enable_back";
    /**
     * 提示音私聊
     **/
    public final static String VOICE_CHAT = "voice_chat";
    /**
     * 提示音最新动态
     **/
    public final static String VOICE_DYNAMIC = "voice_dynamic";
    /**
     * 是否有新的积分产生
     */
    public final static String NEW_CREDITS_PRODUCE = "new_credits_produce";
    // 加密协议切换
    public final static String FLAG_NET = "flag_net";
    public static final String LOGIN_ACCOUNT = "login_accout"; // 自动登录用户名
    public static final String LOGIN_PASSWORD = "login_password";// 自动登录密码
    /**
     * 当前用户是否在麦上
     */
    public static final String SYSTEM_MIC = "system_mic";

    /**
     * 登录后的操作
     */
    public final static String TOKEN = "token";
    public final static String USERID = "uid";
    /**
     * 第1位 短信通道开关 　　第2位 连续登陆天数页面开关 　　第3位 每天登陆时长页面开关 　　第4位 绑定微博领取金币开关
     */
    public final static String NEWONOFFS = "newonoffs";

    // [消息提示音]类型：int
    // ( 完全静音：0，仅声音：1，仅振动：2，声音+振动：3，仅发送消息有提示音：4 )
    public final static String SOUND_VIBRATION_PROMPT = "group_message_prompt";

    /**
     * 记录展示大图的时间
     */
    public final static String SHOW_BIG_PHOTO_TIME = "show_big_photo_time";

    //记录手机是否播放过动态中的置顶动画,跟手机关联
    public final static String DYNAMIC_TOP_GUIDE_KEY = "dynamic_top_guide";
    //记录动态过滤选择需,要加上用户的uid作为key使用
    public final static String DYNAMIC_FILTER_SELECTION_KEY = "dynamic_filter_selection";

    public final static String START_PAGE_AD_TYPE_CONTROL = "start_page_ad_type_control";

    // 隐藏私聊正文：boolean
    public final static String HIDE_CHAT_DETAIL = "hide_chat_detail";
    // 消息声音：boolean
    public final static String VOICE = "voice";
    // 圈消息声音：boolean
    public final static String GROUP_VOICE = "group_voice";
    // 消息振动：boolean
    public final static String SHAKE = "shake";
    // 圈消息振动：boolean
    public final static String GROUP_SHAKE = "group_shake";

    // 世界消息弹幕开关：boolean
    public final static String BARRAGE_FLAG = "barrage_flag";

    /**
     * 启动页的广告图
     */
    public final static String START_PAGE_AD = "start_page_ad";
    public final static String START_PAGE_AD_URL = "start_page_ad_url";
    public final static String START_PAGE_AD_LINK = "start_page_ad_link";
    public final static String START_PAGE_AD_OPENURL = "start_page_web_url";

    public static final String REG_URL = "reg_url";// 记录无法 登录url过期时间，格式 ：

    /**
     * 事件统计，首次启动时间，-1为非首次启动
     */
    public final static String IAROUND_FIRST_START = "iaround_first_start";

    /**
     * 部分页面第一页缓存
     **/
    public static final String CACHE_NEAR_FRIEND = "cache_near_friend"; // 附近用户

    /**
     * 接收评论
     **/
    public final static String REC_COMMENTS = "rec_comments";

    // 侃啦被评论提醒：boolean，默认为true
    public final static String KANLA_REPLY_NOTIC = "kanla_reply_notic";

    /**
     * 提示音
     **/
    public final static String VOICE_ENABLE = "voice_enable";
    // [振动]类型：boolean
    public final static String VIBRATE = "vibrate";

    /**
     * 用户状态
     */
    public final static String USER_STATE = "user_state";
    /**
     * 自动登录
     **/
    public final static String AUTO_LOGIN = "auto_login";
    /**
     * 消息设置
     */
    public final static String NOTFIC_SETTING = "notfic_setting";
    /**
     * 接受离线消息
     **/
    public final static String REC_OFFLINE_MSG = "rec_offline_msg";
    /**
     * 接受私信
     **/
    public final static String REC_PRIVATE_MSG = "rec_private_msg";
    /**
     * 接收通知
     **/
    public final static String REC_NOTICE = "rec_notice";
    /**
     * 默认的用户数据
     */
    public final static String USER_SETTING = "usersetting";
    /**
     * 新消息通知 --动态提醒
     */
    public final static String DYNAMIC_NOTICE = "dynamic_notice";

    public final static String VERSION_CODE = "version_code"; // 新安装

    /**
     * 设备是否激活过
     */
    public static final String DEVICE_ACTIVITY = "device_activity";
    //是否是Google安装包
    public final static String GOOGLE_APP = "google_app";

    /**
     * 是否显示咪咕乐游会员入口
     */
    public final static String SHOWMIGU = "showMigu";
    /**
     * 是否显示咪咕动漫联合会员入口
     */
    public final static String SHOW_MANLIAN = "showManlian";
    /**
     * 首页 显示附近的人还是显示陪玩
     */
    public final static String ACCOMPANY_IS_SHOW = "accompanyIsShow";
    /**
     * 邂逅通知数据状态 1显示别人对我有意思 2显示别人和我互有意思
     */
    public final static String MEET_GAME_FLAG = "meet_game_flag";

    /**
     * 动态消息Remove Status
     */
    public final static String GAMECENTER_AVAILABLE = "gamecenter_available";
    /**
     * 礼物更新时间
     */
    public static final String GIFT_LAST_UPDATE = "gift_last_update";
    /**
     * 下载失败礼物
     */
    public static final String DOWNLOAD_FAILURE_GIFT = "download_failure_gift";

    /**
     * 统计
     */
    public static final String STATISTICS_SERVICE_ENABLE = "statistics_service_enable"; //统计是否开启
    public static final String APPLICATION_CREATED_TIME = "application_created_time"; //Application 创建时间
    public static final String DEVICE_ACTIVATE_SEND = "device_activate_send"; //是否已经发送设备激活

    /*调试开关设置*/
    public static final String APP_DEBUG_SWITCH = "APP_DEBUG_SWITCH";

    /**
     * 通知消息缓存
     */
    public static final String NOTIFICATION_MESSAGE_EXTAR = "notification_message_extar";

    //最后一次上传日志的时间
    public final static String UPLOAD_LOG_LAST_TIME = "upload_log_last_time_";

    private SharedPreferenceUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceUtil getInstance(Context context) {
        if (sharedPreferenceUtil == null) {
            //crash 处理
            if (null != BaseApplication.appContext) {
                sharedPreferenceUtil = new SharedPreferenceUtil(BaseApplication.appContext);
            } else {
                sharedPreferenceUtil = new SharedPreferenceUtil(context.getApplicationContext());
            }
        }
        return sharedPreferenceUtil;
    }

    /**
     * 设置String类型值
     *
     * @param key
     * @param value
     * @time 2011-5-30 上午09:26:38
     * @author:linyg
     */
    public void putString(String key, String value) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 设置long类型值
     *
     * @param key
     * @param value
     * @time 2011-5-30 上午09:28:25
     * @author:linyg
     */
    public void putLong(String key, long value) {
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 设置int类型值
     *
     * @param key
     * @param value
     * @time 2011-5-30 上午09:30:06
     * @author:linyg
     */
    public void putInt(String key, int value) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 设置Boolean类型值
     *
     * @param key
     * @param value
     * @time 2011-5-30 上午09:30:56
     * @author:linyg
     */
    public void putBoolean(String key, boolean value) {
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 设置Float类型值
     *
     * @param key
     * @param value
     * @time 2011-5-30 上午09:31:42
     * @author:linyg
     */
    public void putFloat(String key, float value) {
        Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 设置序列化的Object类型
     *
     * @param key
     * @param strObject
     */
    public void putObject(String key, String strObject) {
        Editor editor = sharedPreferences.edit();
        editor.putString(key, strObject);
        editor.commit();
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为""
     *
     * @param key
     * @return
     */
    public String getObject(String key) {
        return getString(key, "");
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为""
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getObject(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为""
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为""
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为false
     *
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为false
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为0
     *
     * @param key
     * @return
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为0
     *
     * @param key
     * @return
     */
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为0
     *
     * @param key
     * @return
     */
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为0
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(String key, Long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为0
     *
     * @param key
     * @return
     */
    public float getFloat(String key) {
        return getFloat(key, 0f);
    }

    /**
     * 获取key相对应的value，如果不设默认参数，默认值为0
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloat(String key, Float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * 判断是否存在此字段
     */
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 判断是否存在此字段
     */
    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 删除sharedPreferences文件中对应的Key和value
     */
    public boolean remove(String key) {
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 通过加密保存
     *
     * @param key
     * @param value
     */
    public void setEncodeByAES(String key, String value) {
        try {
            putString(key, AES.encode(value, SAVE_PUBLIC_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过加密的方式保存
     *
     * @param key
     * @return
     */
    public String getDecodeByAES(String key) {
        if (has(key)) {
            try {
                return AES.decode(getString(key), SAVE_PUBLIC_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /** 6.0 */

    /**
     * 圈助手是否顶置与消息列表中，int型，0：不顶置，1：顶置，使用时在前面加uid
     */
    public final static String GROUP_HEPLER_TOP_AT_MSG = "group_hepler_top_at_msg";

    /** v5.4 */

    /** 接收搭讪 */
//	public final static String REC_CHAT_GREETING = "push_chat";

    /**
     * 私聊消息通知
     */
    public final static String PRIVATE_NEWS_NOTIFY = "private_news_notify";
    /**
     * 动态消息通知
     */
    public final static String DYNAMIC_NEWS_NOTIFY = "dynamic_news_notify";
    /**
     * 搭讪消息通知
     */
    public final static String ACCOST_NEWS_NOTIFY = "accost_news_notify";

    /**
     * 关于通知设置-在后面添加用户id
     */

    // 免打扰开关：boolean
    public final static String DND_SETTING = "dnd_setting";
    // 免打扰开始时间，在登录时已将推送结束时间存进此字段：int
    public final static String REC_START_TIME = "rec_start_time";
    // 免打扰结束时间，在登录时已将推送开始时间存进此字段：int
    public final static String REC_END_TIME = "rec_end_time";
    /**
     * 上次用户点击进入圈助手的是将，long型，默认值为0，使用时在后面添加用户id
     */
    public final static String GROUP_HELPER_ENTER_TIME = "group_helper_enter_time";


    /**
     * 视屏主播免打扰设置
     */
    public final static String NOT_DISTURB = "not_disturb";

    /**
     * 语音主播免打扰设置
     */
    public final static String VOICENOT_DISTURB = "voice_not_disturb";
}
