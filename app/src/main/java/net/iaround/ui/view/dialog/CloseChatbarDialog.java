package net.iaround.ui.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboAppManager;
import com.tencent.tauth.Tencent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.ShareHttpProtocol;
import net.iaround.model.im.ShareChatbarBean;
import net.iaround.share.sina.weibo.SinaWeiboUtil;
import net.iaround.share.tencent.qq.QQUtil;
import net.iaround.share.tencent.qqzone.QQZoneUtil;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.share.utils.ShareActionListener;
import net.iaround.share.wechat.group.IARWeixin;
import net.iaround.share.wechat.group.WechatGroupUtil;
import net.iaround.share.wechat.session.WechatSessionUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;

import java.util.Map;

/**
 * 聊吧退出时弹出的对话框
 * Created by liangyuanhuan on 19/10/2017.
 */

public class CloseChatbarDialog extends Dialog {
    private static final String TAG = "CloseChatbarDialog";
    private static CloseChatbarDialog sCustomChatBarDialog = null;
    private static boolean sShowingCloseChatbarDialog = false;

    private Context mContext;
    private ImageView mZoomChatBar;
    private ImageView mCloseChatBar;

    private CloseChatbarListener mListener = null;

    public interface CloseChatbarListener{
        void onCloseChatBar();
        void onZoomChatBar();
    }

    /* listener 回调接口
    * */
    public static void showDialog(Context mContext, CloseChatbarListener listener){
        if(sShowingCloseChatbarDialog == true){
            return;
        }
        sShowingCloseChatbarDialog = true;

        sCustomChatBarDialog = new CloseChatbarDialog(mContext);
        sCustomChatBarDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(android.content.DialogInterface dialog) {
                CommonFunction.log(TAG, "dialog dismiss...");
                sShowingCloseChatbarDialog = false;
                if(null!=sCustomChatBarDialog){
                    sCustomChatBarDialog.setCloseChatbarListener(null);
                    sCustomChatBarDialog = null;
                }
            }
        });
        sCustomChatBarDialog.setCloseChatbarListener(listener);
        CommonFunction.log(TAG, "dialog show...");
        sCustomChatBarDialog.show();
    }

    protected CloseChatbarDialog(Context context) {
        super(context, R.style.chat_bar_transparent_dialog);
        this.mContext = context;

    }

    protected CloseChatbarDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbar_close);
        initView();
    }

    private void initView() {

        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);

        //底部弹出样式
        //window.setWindowAnimations(R.style.popwin_anim_style);
        //window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setCancelable(true); //BACK 物理按键是否生效
        setCanceledOnTouchOutside(false);

        mZoomChatBar = (ImageView) findViewById(R.id.zoom_chatbar);
        mZoomChatBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomChatBar();
            }
        });

        mCloseChatBar = (ImageView) findViewById(R.id.close_chatbar);
        mCloseChatBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChatBar();
            }
        });
    }

    public void setCloseChatbarListener(CloseChatbarListener listener){
        this.mListener = listener;
    }

    private void zoomChatBar(){
        if(mListener!=null){
            mListener.onZoomChatBar();
            dismiss();
        }
    }

    private void closeChatBar(){
        if(mListener!=null){
            mListener.onCloseChatBar();
            dismiss();
        }
    }

}