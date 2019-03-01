package net.iaround.onepush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.serializer.FilterUtils;
import com.peng.one.push.OnePush;
import com.peng.one.push.entity.OnePushCommand;
import com.peng.one.push.entity.OnePushMsg;
import com.peng.one.push.receiver.BaseOnePushReceiver;
import com.sina.weibo.sdk.api.share.Base;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.connector.FilterUtil;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.RomUtils;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.SplashActivity;

/**
 * Created by pyt on 2017/5/16.
 */
public class TestPushReceiver extends BaseOnePushReceiver {

    private static final String TAG = "OneLog";
    private Context mContext;

    @Override
    public void onReceiveNotification(Context context, OnePushMsg msg) {
        super.onReceiveNotification(context, msg);
        Log.i(TAG, "onReceiveNotification: " + msg.toString());
    }

    @Override
    public void onReceiveNotificationClick(Context context, OnePushMsg msg) {
        Log.i(TAG, "onReceiveNotificationClick: " + msg.toString());

        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.NOTIFICATION_MESSAGE_EXTAR,msg.getExtraMsg());
//        // 魅族手机通知点击已经启动SplashActivity
        if (RomUtils.isFlymeRom()){
            return;
        }
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onReceiveMessage(Context context, OnePushMsg msg) {
        Log.i(TAG, "onReceiveMessage: " + msg.toString());
    }

    @Override
    public void onCommandResult(final Context context, OnePushCommand command) {
        this.mContext = context;
        if(command == null)return;

        //注册消息推送失败，再次注册
        if (command.getType() == OnePush.TYPE_REGISTER && command.getResultCode() == OnePush.RESULT_ERROR) {
            OnePush.register();
        }

        Common.getInstance().setDeviceToken(command.getToken());

        Log.i(TAG, "onCommandResult: " + command.toString());
    }


}
