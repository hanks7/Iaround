
package net.iaround.tools;


import android.content.Context;

import net.iaround.R;
import net.iaround.conf.Common;


public class SettingUtil {
    private static SettingUtil settingUtil;

    private SettingUtil() {
    }

    public static SettingUtil getInstance() {
        if (settingUtil == null) {
            settingUtil = new SettingUtil();
        }
        return settingUtil;
    }

    /**
     * 获取设置声音状态
     *
     * @param context
     * @param flag
     * @return
     */
    public static boolean getSoundState(Context context, int flag) {
        final String[] titles = new String[]
                {context.getString(R.string.iAround_circle),
                        context.getString(R.string.private_chat),
                        context.getString(R.string.information)};
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        final String spKey = SharedPreferenceUtil.VOICE_SETTINGS
                + Common.getInstance().loginUser.getUid();
        if (sp.has(spKey)) {
            String settingData = sp.getString(spKey);
            String[] pSettings = settingData.split("%");
            int size = titles.length < pSettings.length ? titles.length : pSettings.length;
            for (int i = 0; i < size; i++) {
                if (flag <= i) {
                    return Boolean.parseBoolean(pSettings[flag + 1]);
                }
            }
        } else {
            return false;
        }

        return false;

    }

    /**
     * 读取String类型值，默认为""
     *
     * @param key
     * @time 2013-11-11 上午09:33:01
     * @author:ZengJ
     * @deprecated {@linkplain SharedPreferenceUtil}中的getString方法
     */
    public static String getString(Context context, String key) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        return sp.getString(key);
    }

    /**
     * 读取boolean类型值，默认为false;
     *
     * @param key
     * @return
     * @time 2013-11-11 上午09:33:54
     * @author:ZengJ
     */
    public boolean getBoolean(Context context, String key) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        if (sp.has(key)) {
            return sp.getBoolean(key);
        } else {
            sp.putBoolean(key, false);
        }
        return sp.getBoolean(key);
    }

    /**
     * 读取int类型值，默认为0
     *
     * @param key
     * @return
     * @time 2013-11-11 上午09:35:57
     * @author:ZengJ
     * @deprecated {@linkplain SharedPreferenceUtil}中的getInt方法
     */
    public int getInt(Context context, String key) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        return sp.getInt(key);
    }

    /**
     * 读取long类型值，默认为0
     *
     * @param key
     * @return
     * @time 2013-11-11 上午09:36:43
     * @author:ZengJ
     * @deprecated {@linkplain SharedPreferenceUtil}中的getLong方法
     */
    public long getLong(Context context, String key) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        return sp.getLong(key);
    }

    /**
     * 读取float类型值，默认为0
     *
     * @param key
     * @return
     * @time 2013-11-11 上午09:37:42
     * @author:ZengJ
     * @deprecated {@linkplain SharedPreferenceUtil}中的getFloat方法
     */
    public float getFloat(Context context, String key) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        return sp.getFloat(key);
    }

    /**
     * 判断是否存在此字段
     */
    public boolean has(Context context, String key) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        return sp.has(key);
    }

    /**
     * 没登陆情况设置默认值
     */
    public void saveDefaultUserSetting(Context context) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
//		String themeKey = SharedPreferenceUtil.THEME_INDEX;
//		String bgTypeKey = SharedPreferenceUtil.BACKGROUND_IMAGE_TYPE;
//		String bgNameKey = SharedPreferenceUtil.BACKGROUND_IMAGE_NAME;
//		String identityVerificationKey = SharedPreferenceUtil.AUTHENTICATION_CHECK;
//		String publicLocationKey = SharedPreferenceUtil.PUBLIC_LOCATION;
//		String publicTrackKey = SharedPreferenceUtil.PUBLIC_TRACK;
        String voiceEnableKey = SharedPreferenceUtil.VOICE_ENABLE;
        String vibrateEnableKey = SharedPreferenceUtil.VIBRATE_ENABLE;
//		String pushContentKey = SharedPreferenceUtil.PUSH_CONTENT;
//		String recOfflineMsgKey = SharedPreferenceUtil.REC_OFFLINE_MSG;
//		String recPrivateMsgKey = SharedPreferenceUtil.REC_PRIVATE_MSG;
        String recCommentsKey = SharedPreferenceUtil.REC_COMMENTS;
//		String recNoticeKey = SharedPreferenceUtil.REC_NOTICE;
        String recStartTimeKey = SharedPreferenceUtil.REC_START_TIME;
        String recEndTimeKey = SharedPreferenceUtil.REC_END_TIME;
//		String publicDeviceSourceKey = SharedPreferenceUtil.PUBLIC_DEVICE_SOURCE;
        String autoLoginKey = SharedPreferenceUtil.AUTO_LOGIN;
//		sp.putInt( themeKey , 6 );// 主题下标
//		sp.putInt( bgTypeKey , 0 );// 背景类型
//		sp.putString( bgNameKey , String.valueOf( 0 ) );// 背景名称
//		sp.putBoolean( identityVerificationKey , false );// 身份验证
//		sp.putInt( publicLocationKey , 1 );// 公开位置
//		sp.putBoolean( publicTrackKey , true );// 轨迹公开
        sp.putBoolean(voiceEnableKey, true);// 提示音
        sp.putBoolean(vibrateEnableKey, false); // 振动
//		sp.putBoolean( pushContentKey , false );// 推送电话簿
//		sp.putBoolean( recOfflineMsgKey , true );// 接收离线消息
//		sp.putBoolean( recPrivateMsgKey , true );// 接收私信
        sp.putBoolean(recCommentsKey, true);// 接收评论
//		sp.putBoolean( recNoticeKey , true );// 接收通知
        sp.putInt(recStartTimeKey, 9);// 接收时间段--开始
        sp.putInt(recEndTimeKey, 21);// 接收时间段--结束
//		sp.putBoolean( publicDeviceSourceKey , true );// 公开设备和软件来源
        sp.putBoolean(autoLoginKey, true);// 自动登录
        // 5.0版本不针对用户只针对手机保存的数据状态
//		String dynamicNoticeKey = SharedPreferenceUtil.DYNAMIC_NOTICE;
//		String circleMessageNotificationKey = SharedPreferenceUtil.CIRCLE_MESSAGE_NOTIFICATION;
//		String hiddenPrivateKey = SharedPreferenceUtil.HIDDEN_PRIVATE_CHAT_TEXT;
        String vibrateKey = SharedPreferenceUtil.VIBRATE;
        String soundVibrationKey = SharedPreferenceUtil.SOUND_VIBRATION_PROMPT;
//		if ( !sp.has( dynamicNoticeKey ) )
//		{// 动态通知栏是否显示
//			sp.putBoolean( dynamicNoticeKey , true );
//		}
//		if ( !sp.has( circleMessageNotificationKey ) )
//		{// 圈子消息通知默认设置flase
//			sp.putBoolean( circleMessageNotificationKey , false );
//		}
//		if ( !sp.has( hiddenPrivateKey ) )
//		{// 隐藏私聊正文
//			sp.putBoolean( hiddenPrivateKey , true );
//		}

        if (!sp.has(vibrateKey)) {
            sp.putBoolean(vibrateKey, false);// 振动
        }
        if (!sp.has(soundVibrationKey)) {
            sp.putInt(soundVibrationKey, 1);//遇见内消息提示
        }
    }

    /**
     * 登陆后恢复默认设置
     */
    public void restoreDefaultUserSetting(Context context) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        long uid = Common.getInstance().loginUser.getUid();
//		String themeKey = SharedPreferenceUtil.THEME_INDEX + uid;
//		String bgTypeKey = SharedPreferenceUtil.BACKGROUND_IMAGE_TYPE + uid;
//		String bgNameKey = SharedPreferenceUtil.BACKGROUND_IMAGE_NAME + uid;
//		String identityVerificationKey = SharedPreferenceUtil.AUTHENTICATION_CHECK + uid;
//		String publicLocationKey = SharedPreferenceUtil.PUBLIC_LOCATION + uid;
//		String publicTrackKey = SharedPreferenceUtil.PUBLIC_TRACK + uid;
        String voiceEnableKey = SharedPreferenceUtil.VOICE_ENABLE + uid;
        String vibrateEnableKey = SharedPreferenceUtil.VIBRATE_ENABLE + uid;
//		String pushContentKey = SharedPreferenceUtil.PUSH_CONTENT + uid;
//		String recOfflineMsgKey = SharedPreferenceUtil.REC_OFFLINE_MSG + uid;
//		String recPrivateMsgKey = SharedPreferenceUtil.REC_PRIVATE_MSG + uid;
        String recCommentsKey = SharedPreferenceUtil.REC_COMMENTS + uid;
//		String recNoticeKey = SharedPreferenceUtil.REC_NOTICE + uid;
        String recStartTimeKey = SharedPreferenceUtil.REC_START_TIME + uid;
        String recEndTimeKey = SharedPreferenceUtil.REC_END_TIME + uid;
//		String publicDeviceSourceKey = SharedPreferenceUtil.PUBLIC_DEVICE_SOURCE + uid;
        String autoLoginKey = SharedPreferenceUtil.AUTO_LOGIN + uid;
//		sp.putInt( themeKey , 6 );// 主题下标
//		sp.putInt( bgTypeKey , 0 );// 背景类型
//		sp.putString( bgNameKey , String.valueOf( 0 ) );// 背景名称
//		sp.putBoolean( identityVerificationKey , false );// 身份验证
//		sp.putInt( publicLocationKey , 1 );// 公开位置
//		sp.putBoolean( publicTrackKey , true );// 轨迹公开
        sp.putBoolean(voiceEnableKey, true);// 提示音
        sp.putBoolean(vibrateEnableKey, false); // 振动
//		sp.putBoolean( pushContentKey , false );// 推送电话簿
//		sp.putBoolean( recOfflineMsgKey , true );// 接收离线消息
//		sp.putBoolean( recPrivateMsgKey , true );// 接收私信
        sp.putBoolean(recCommentsKey, true);// 接收评论
//		sp.putBoolean( recNoticeKey , true );// 接收通知
        sp.putInt(recStartTimeKey, 9);// 接收时间段--开始
        sp.putInt(recEndTimeKey, 21);// 接收时间段--结束
//		sp.putBoolean( publicDeviceSourceKey , true );// 公开设备和软件来源
        sp.putBoolean(autoLoginKey, true);// 自动登录
    }

}
