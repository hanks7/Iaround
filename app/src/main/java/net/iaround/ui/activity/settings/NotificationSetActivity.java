package net.iaround.ui.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.conf.SettingConfig;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.ui.space.more.DndSetting;
import net.iaround.ui.space.more.GroupsMsgSettingActivity;
import net.iaround.ui.space.more.PrivateSettingType;
import net.iaround.ui.view.FlagImageView;

public class NotificationSetActivity extends TitleActivity implements View.OnClickListener{

    /**
     * 接收新消息推送
     * */
    private LinearLayout llReceive;
    /**
     * 免打扰时段
     */
    private LinearLayout llAvoidTroubleTilme;
    /**
     * 免打扰时间
     */
    private TextView tvTrouleTime;
    /**
     * 隐藏私聊正文
     */
    private FlagImageView fivHideChatText;
    /**
     * 声音
     */
    private FlagImageView fivSetAudio;
    /**
     * 圈声音
     */
    private FlagImageView fivSetCircleAudio;
    /**
     * 震动
     */
    private FlagImageView fivSetVabration;
    /**
     * 圈震动
     */
    private FlagImageView fivSetCircleVibration;
    /**
     * 圈消息设置
     */
    private LinearLayout llCircleMsgSetting;
//    /**
//     * 动态被评论提醒
//     */
//    private FlagImageView fivAcionMsgReminder;
//    /**
//     * 收到搭讪提醒
//     */
//    private FlagImageView fivAccostReminder;
    /**
     * 私聊消息通知
     */
    private FlagImageView fivPrivateNewsNotify;
    /**
     * 动态消息通知
     */
    private FlagImageView fivDynamicNewsNotify;
    /**
     * 搭讪消息通知
     */
    private FlagImageView fivAccostNewsNotify;


    private SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( this );

    private String dndKey;// 免打扰开关
    private String recStartKey;//
    private String recEndKey;
    private String chathidedetailKey;// 隐藏私聊正文开关
    private String voiceKey;// 声音
    private String groupVoiceKey;// 圈声音
    private String shakeKey;// 振动
    private String groupShakeKey;// 圈振动
//    private String dynamicReplyNoticeKey;// 动态被评论提醒
//    private String recAccostKey;// 收到搭讪提醒
    private String privateNewsKey;// 私聊消息通知
    private String dynamicNewsKey;// 动态消息通知
    private String accostNewsKey;// 搭讪消息通知

    private boolean hideChatDetail;// 隐藏私聊正文
    private boolean voice;// 声音
    private boolean groupVoice;// 圈声音
    private boolean shake;// 振动
    private boolean groupShake;// 圈振动
//    private boolean dynamicReplyNotice;// 动态被评论提醒
//    private boolean accostNotice;// 收到搭讪提醒
    private boolean privateNews;// 私聊消息通知
    private boolean dynamicNews;// 动态消息通知
    private boolean accostNews;// 搭讪消息通知

    private final static int MSG_REFRESH_DND_DATA = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndUpload( );
                finish( );
            }
        }, getString(R.string.setting_notification_setting), true, 0, null, null);
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndUpload();
                finish();
            }
        });
        setContent(R.layout.activity_notification_set);
        llReceive = findView(R.id.ll_receive_new_msg);
        llAvoidTroubleTilme = findView(R.id.ll_avoid_trouble_time);
        tvTrouleTime = (TextView) findViewById(R.id.tv_undistrupt_time);
        fivHideChatText = findView(R.id.fiv_hide_chat_text);
        fivSetAudio = findView(R.id.fiv_set_audio);
        fivSetCircleAudio = findView(R.id.fiv_set_circle_audio);
        fivSetVabration = findView(R.id.fiv_set_vibration);
        fivSetCircleVibration = findView(R.id.fiv_set_circle_vibration);
        llCircleMsgSetting = findView(R.id.ll_circle_msg_setting);
//        fivAcionMsgReminder = findView(R.id.fiv_action_msg_reminder);
//        fivAccostReminder = findView(R.id.fiv_accost_reminder);
        fivPrivateNewsNotify = findView(R.id.fiv_private_news_notify);
        fivDynamicNewsNotify = findView(R.id.fiv_dynamic_news_notify);
        fivAccostNewsNotify = findView(R.id.fiv_accost_news_notify);
    }

    private void initDatas() {

        chathidedetailKey = SharedPreferenceUtil.HIDE_CHAT_DETAIL + CommonFunction.getUSERID(this);
        hideChatDetail = sp.getBoolean( chathidedetailKey , false );
        fivHideChatText.setState(hideChatDetail);

        //声音
        voiceKey = SharedPreferenceUtil.VOICE + CommonFunction.getUSERID(this);
        voice = sp.getBoolean( voiceKey , true );
        fivSetAudio.setState(voice);
        //圈声音
        groupVoiceKey = SharedPreferenceUtil.GROUP_VOICE + CommonFunction.getUSERID(this);
        groupVoice = sp.getBoolean(groupVoiceKey,false);
        fivSetCircleAudio.setState(groupVoice);
        //震动
        shakeKey = SharedPreferenceUtil.SHAKE + CommonFunction.getUSERID(this);
        shake = sp.getBoolean(shakeKey , false);
        fivSetVabration.setState(shake);
        //圈震动
        groupShakeKey = SharedPreferenceUtil.GROUP_SHAKE + CommonFunction.getUSERID(this);
        groupShake = sp.getBoolean(groupShakeKey,false);
        fivSetCircleVibration.setState(groupShake);

        dndKey = SharedPreferenceUtil.DND_SETTING + CommonFunction.getUSERID(this);
        recStartKey = SharedPreferenceUtil.REC_START_TIME + CommonFunction.getUSERID(this);
        recEndKey = SharedPreferenceUtil.REC_END_TIME + CommonFunction.getUSERID(this);

//        dynamicReplyNoticeKey = SharedPreferenceUtil.DYNAMIC_REPLY_NOTICE + CommonFunction.getUSERID(this);
//        if ( sp.contains( dynamicReplyNoticeKey ) )
//            dynamicReplyNotice = sp.getBoolean( dynamicReplyNoticeKey , true );
//        else
//            dynamicReplyNotice = sp.getBoolean( oldDynamicReplyNoticeKey , true );
//        fivAcionMsgReminder.setState(dynamicReplyNotice);

//        recAccostKey = SharedPreferenceUtil.REC_CHAT_GREETING + CommonFunction.getUSERID(this);
//        accostNotice = sp.getBoolean( recAccostKey , false );
//        fivAccostReminder.setState(accostNotice);

        privateNewsKey = SharedPreferenceUtil.PRIVATE_NEWS_NOTIFY + CommonFunction.getUSERID(this);
        privateNews = sp.getBoolean( privateNewsKey , false );
        fivPrivateNewsNotify.setState(privateNews);

        dynamicNewsKey = SharedPreferenceUtil.DYNAMIC_NEWS_NOTIFY + CommonFunction.getUSERID(this);
        dynamicNews = sp.getBoolean( dynamicNewsKey , false );
        fivDynamicNewsNotify.setState(dynamicNews);

        accostNewsKey = SharedPreferenceUtil.ACCOST_NEWS_NOTIFY + CommonFunction.getUSERID(this);
        accostNews = sp.getBoolean( accostNewsKey , true );
        fivAccostNewsNotify.setState(accostNews);

        parseTimeData();
    }

    private void initListeners() {
        llReceive.setOnClickListener(this);
        llAvoidTroubleTilme.setOnClickListener(this);
        fivHideChatText.setOnClickListener(this);
        fivSetAudio.setOnClickListener(this);
        fivSetCircleAudio.setOnClickListener(this);
        fivSetVabration.setOnClickListener(this);
        fivSetCircleVibration.setOnClickListener(this);
        llCircleMsgSetting.setOnClickListener(this);
//        fivAcionMsgReminder.setOnClickListener(this);
//        fivAccostReminder.setOnClickListener(this);
        fivPrivateNewsNotify.setOnClickListener(this);
        fivDynamicNewsNotify.setOnClickListener(this);
        fivAccostNewsNotify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Boolean state;
        switch (v.getId()){
            case R.id.ll_receive_new_msg://接收新推送消息
                // TODO: 2017/5/4 后期会添加新消息推送
                break;
            case R.id.ll_avoid_trouble_time://免打扰时段
                Intent intentDnd = new Intent( mContext , DndSetting.class );
                startActivityForResult( intentDnd , DndSetting.REQ_FROM_PUSH_NOTIFICATION );
                break;
            case R.id.fiv_hide_chat_text://隐藏私聊正文
                state = !fivHideChatText.isSelected();
                fivHideChatText.setState(state);
                break;
            case R.id.fiv_set_audio://声音
                state = !fivSetAudio.isSelected();
                fivSetAudio.setState(state);
                break;
            case R.id.fiv_set_circle_audio://圈声音
                state = !fivSetCircleAudio.isSelected();
                fivSetCircleAudio.setState(state);
                break;
            case R.id.fiv_set_vibration://震动
                state = !fivSetVabration.isSelected();
                fivSetVabration.setState(state);
                break;
            case R.id.fiv_set_circle_vibration://圈震动
                state = !fivSetCircleVibration.isSelected();
                fivSetCircleVibration.setState(state);
                break;
            case R.id.ll_circle_msg_setting://圈消息设置
                Intent intent = new Intent( );
                intent.setClass( mContext , GroupsMsgSettingActivity.class );
                mContext.startActivity( intent );
                break;
//            case R.id.fiv_action_msg_reminder://动态被评论提醒
//                state = !fivAcionMsgReminder.isSelected();
//                fivAcionMsgReminder.setState(state);
//                break;
//            case R.id.fiv_accost_reminder://收到搭讪提醒
//                state = !fivAccostReminder.isSelected();
//                fivAccostReminder.setState(state);
//                break;
            case R.id.fiv_private_news_notify://私聊消息通知
                state = !fivPrivateNewsNotify.isSelected();
                fivPrivateNewsNotify.setState(state);
                break;
            case R.id.fiv_dynamic_news_notify://动态消息通知
                state = !fivDynamicNewsNotify.isSelected();
                fivDynamicNewsNotify.setState(state);
                break;
            case R.id.fiv_accost_news_notify://搭讪消息通知
                state = !fivAccostNewsNotify.isSelected();
                fivAccostNewsNotify.setState(state);
                break;

        }
    }

    private void saveAndUpload( )
    {
        if ( hideChatDetail != fivHideChatText.isSelected() )
        {// 隐藏私聊正文
            sp.putBoolean( chathidedetailKey , fivHideChatText.isSelected( ) );
            // old：要上传的是“是否显示正文”，所以要将“是否隐藏私聊正文”的开关取反上传
            //6.2版本由于服务端和iOS的错误，导致"/user/privacy/get_5_4"下发的"showcontent"为"y"时"隐藏私聊正文"开关为开，android按这样子改
            UserHttpProtocol.userPrivacyUpdate( mContext ,
                    PrivateSettingType.SHOW_MESSAGE_CONTENT_SWITCH ,
                    fivHideChatText.isSelected( ) ? "y" : "n" , null );
            CommonFunction.log( "sherlock" ,
                    "hidedetail change to " + fivHideChatText.isSelected( ) + " , saved" );
        }
//        if ( accostNotice != fivAccostReminder.isSelected( ) )
//        {// 收到搭讪提醒我
//            sp.putBoolean( recAccostKey , fivAccostReminder.isSelected( ) );
//            UserHttpProtocol.userPrivacyUpdate( mContext ,
//                    PrivateSettingType.GREETED_NOTIFICATION ,
//                    fivAccostReminder.isSelected( ) ? "y" : "n" , null );
//            CommonFunction.log( "sherlock" ,
//                    "receive accost change to " + fivAccostReminder.isSelected( ) + " , saved" );
//        }
        if ( voice != fivSetAudio.isSelected( ) )
        {// 声音
            sp.putBoolean( voiceKey , fivSetAudio.isSelected( ) );
            CommonFunction.log( "sherlock" , "voice change to " + fivSetAudio.isSelected( )
                    + " , saved" );
        }
        if ( groupVoice != fivSetCircleAudio.isSelected() )
        {// 圈声音
            sp.putBoolean( groupVoiceKey , fivSetCircleAudio.isSelected() );
            CommonFunction.log( "sherlock" ,
                    "groupvoice change to " + fivSetCircleAudio.isSelected() + " , saved" );
        }//gh
        if ( shake != fivSetVabration.isSelected( ) )
        {// 震动
            sp.putBoolean( shakeKey , fivSetVabration.isSelected( ) );
            CommonFunction.log( "sherlock" , "shake change to " + fivSetVabration.isSelected( )
                    + " , saved" );
        }
        if ( groupShake != fivSetCircleVibration.isSelected() )
        {// 圈振动
            sp.putBoolean( groupShakeKey , fivSetCircleVibration.isSelected() );
            CommonFunction.log( "sherlock" ,
                    "groupshake change to " + fivSetCircleVibration.isSelected() + " , saved" );
        }
//        if ( dynamicReplyNotice != fivAcionMsgReminder.isSelected( ) )
//        {// 动态被评论通知我
//            sp.putBoolean( dynamicReplyNoticeKey , fivAcionMsgReminder.isSelected( ) );
//            UserHttpProtocol.userPrivacyUpdate( mContext ,
//                    PrivateSettingType.DYNAMIC_NOTIFICATION ,
//                    fivAcionMsgReminder.isSelected( ) ? "y" : "n" , null );
//            CommonFunction.log( "sherlock" ,
//                    "dynamic change to " + fivAcionMsgReminder.isSelected( ) + " , saved" );
//        }

        if ( privateNews != fivPrivateNewsNotify.isSelected( ) )
        {// 私聊消息通知
            sp.putBoolean( privateNewsKey , fivPrivateNewsNotify.isSelected( ) );
            UserHttpProtocol.userPrivacyUpdate( mContext ,
                    PrivateSettingType.PRIVATE_NEWS_NOTIFY ,
                    fivPrivateNewsNotify.isSelected( ) ? "1" : "0" , null );
            CommonFunction.log( "sherlock" ,
                    "receive accost change to " + fivPrivateNewsNotify.isSelected( ) + " , saved" );
        }
        if ( dynamicNews != fivDynamicNewsNotify.isSelected( ) )
        {// 动态消息通知
            sp.putBoolean( dynamicNewsKey , fivDynamicNewsNotify.isSelected( ) );
            UserHttpProtocol.userPrivacyUpdate( mContext ,
                    PrivateSettingType.DYNAMIC_NEWS_NOTIFY ,
                    fivDynamicNewsNotify.isSelected( ) ? "1" : "0" , null );
            CommonFunction.log( "sherlock" ,
                    "receive accost change to " + fivDynamicNewsNotify.isSelected( ) + " , saved" );
        }

        if ( accostNews != fivAccostNewsNotify.isSelected( ) )
        {// 搭讪消息通知
            sp.putBoolean( accostNewsKey , fivAccostNewsNotify.isSelected( ) );
            UserHttpProtocol.userPrivacyUpdate( mContext ,
                    PrivateSettingType.ACCOST_NEWS_NOTIFY ,
                    fivAccostNewsNotify.isSelected( ) ? "1" : "0" , null );
            CommonFunction.log( "sherlock" ,
                    "receive accost change to " + fivAccostNewsNotify.isSelected( ) + " , saved" );
        }
    }

    @Override
    public boolean onKeyDown( int keyCode , KeyEvent event )
    {
        if ( keyCode == KeyEvent.KEYCODE_BACK )
        {
            saveAndUpload( );
            finish( );
        }
        return false;
    }

    public void parseTimeData( )
    {
        if ( sp.getBoolean( dndKey ) )
        {
            int startTime = sp.getInt( recStartKey );
            int endTime = sp.getInt( recEndKey );
            String timeRange = TimeFormat.getTimeShort( startTime ) + "-"
                    + TimeFormat.getTimeShort( endTime );

            tvTrouleTime.setText( timeRange );
        }
        else
        {
            tvTrouleTime.setText( R.string.dismiss );
        }
    }
    private Handler handler = new Handler( Looper.getMainLooper( ) )
    {
        public void handleMessage( android.os.Message msg )
        {
            switch ( msg.what )
            {
                case MSG_REFRESH_DND_DATA :
                {
                    parseTimeData( );
                }
                break;

            }
        }
    };
    @Override
    protected void onActivityResult( int requestCode , int resultCode , Intent data )
    {
        if ( requestCode == DndSetting.REQ_FROM_PUSH_NOTIFICATION )
        {
            if ( resultCode == Activity.RESULT_OK )
            {
                handler.sendEmptyMessage( MSG_REFRESH_DND_DATA );
            }
        }
    }
}
