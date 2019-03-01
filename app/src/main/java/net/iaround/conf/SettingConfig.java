package net.iaround.conf;

import android.content.Context;

import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;

/**
 * @author：liush on 2017/1/6 20:30
 */
public class SettingConfig {

    private Context context;
    private static SettingConfig instance = null;
    private SettingConfig(Context context){
        this.context = context;
    }

    public static SettingConfig getInstance(Context context){
        if(instance == null){
            instance = new SettingConfig(context);
        }
        return instance;
    }

//    /**
//     * 获取私聊通知内容显示的配置
//     * @return true:隐藏私聊正文
//     *          false:显示私聊正文
//     */
//    public Boolean isHideMsgText(){
//        return SharedPreferenceUtil.getInstance(context).getBoolean(Constants.HIDE_TALK_MSG_TEXT + CommonFunction.getUSERID(context));
//    }
//
//    /**
//     * 获取消息通知声音设置
//     * @return true:开启消息声音提示
//     *          false:关闭消息声音提示
//     */
//    public Boolean isOpenVoice(){
//        return SharedPreferenceUtil.getInstance(context).getBoolean(Constants.MESSAGE_VOICE + CommonFunction.getUSERID(context));
//    }
//
//    /**
//     * 获取消息通知震动设置
//     * @return true:开启消息振动提示
//     *          false:关闭消息振动提示
//     */
//    public Boolean isOpenVibration(){
//        return SharedPreferenceUtil.getInstance(context).getBoolean(Constants.MESSAGE_VIBRATION + CommonFunction.getUSERID(context));
//    }
//
//    /**
//     * 获取动态评论提醒设置
//     * @return true:开启动态评论提醒
//     *          false:关闭动态评论提醒
//     */
//    public Boolean isActionCommonRemind(){
//        return SharedPreferenceUtil.getInstance(context).getBoolean(Constants.ACTION_COMMENT + CommonFunction.getUSERID(context));
//    }
//
//    /**
//     * 获取搭讪提醒设置
//     * @return true:开启搭讪提醒
//     *          false:关闭搭讪提醒
//     */
//    public Boolean isAccostRemind(){
//        return SharedPreferenceUtil.getInstance(context).getBoolean(Constants.ACTION_COMMENT + CommonFunction.getUSERID(context));
//    }
}
