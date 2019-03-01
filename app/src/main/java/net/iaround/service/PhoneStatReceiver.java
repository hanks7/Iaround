package net.iaround.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import net.iaround.floatwindow.AudioChatFloatWindowHelper;
import net.iaround.mic.AudioChatManager;
import net.iaround.conf.Common;
import net.iaround.im.WebSocketManager;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.AudioChatActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.VideoChatActivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;

/**
 * Class: 监测手机来电、去电
 * Author：gh
 * Date: 2017/6/28 12:22
 * Email：jt_gaohang@163.com
 */
public class PhoneStatReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStatReceiver";

//        private static MyPhoneStateListener phoneListener = new MyPhoneStateListener();

    private static boolean incomingFlag = false;

    private static String incoming_number = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        //如果是拨打电话
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            incomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            CommonFunction.log( "call OUT:"+phoneNumber);
        }else{
            //如果是来电
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            Activity activity = CloseAllActivity.getInstance().getTopActivity();
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;//标识当前是来电
                    incoming_number = intent.getStringExtra("incoming_number");
                    CommonFunction.log("RINGING :"+ incoming_number);

                    if ( activity instanceof GroupChatTopicActivity) {
                        if ( ( (GroupChatTopicActivity) activity ).instant != null ) {
                            ((GroupChatTopicActivity) activity).instant.callStateRinging();
                        }
                    }else if (activity instanceof VideoChatActivity) {
                        VideoChatActivity vc = (VideoChatActivity)activity;
                        vc.onCallStateRinging();
                    }else{
                        ChatBarZoomWindow.getInstance().onCallStateRinging();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if(incomingFlag){
                        CommonFunction.log(TAG, "incoming ACCEPT :"+ incoming_number);

                        if (activity instanceof VideoChatActivity) {
                            VideoChatActivity vc = (VideoChatActivity)activity;
                            vc.onCallStateOffHook();
                        }

                        //退出语音通话
                        if(AudioChatManager.getsInstance().isHasLogin()){
//                            AudioChatFloatWindowHelper.getInstance().destroy();
                            WebSocketManager.getsInstance().logoutAudioRoom(true);
                            if(activity instanceof AudioChatActivity){
                                activity.finish();
                            }
                        }
                    }

                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    if(incomingFlag){
                        CommonFunction.log( "incoming IDLE");
                        if ( activity instanceof GroupChatTopicActivity) {
                            if ( ( (GroupChatTopicActivity) activity ).instant != null ) {
                                ((GroupChatTopicActivity) activity).instant.callStateIdle();
                            }
                        }else if (activity instanceof VideoChatActivity) {
                            VideoChatActivity vc = (VideoChatActivity)activity;
                            vc.onCallStateIdle();
                        }else{
                            ChatBarZoomWindow.getInstance().onCallStateIdle();
                        }
                    }

                    break;
            }
        }
    }
}