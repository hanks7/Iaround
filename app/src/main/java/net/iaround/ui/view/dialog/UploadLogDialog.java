package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LuckPanProtocol;
import net.iaround.model.im.LuckPanBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CustomDialog;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.view.luckpan.LuckPanDialog;
import net.iaround.ui.view.luckpan.LuckPanLayout2;
import net.iaround.utils.UploadZipFileUtils;

/* 抽奖规则
*
*
*
* */
public class UploadLogDialog extends Dialog implements UploadZipFileUtils.IUploadListener {
    private static final String TAG = "UploadLogDialog";
    private Context mContext;
    private ProgressBar mProgressBar;

    public UploadLogDialog(Context context) {
        super(context, R.style.chat_bar_transparent_dialog);
        this.mContext = context;

    }

    protected UploadLogDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_log);

        initView();

    }

    private void initView() {

        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        //底部弹出样式
        //window.setWindowAnimations(R.style.popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setCancelable(true); //BACK 按钮
        setCanceledOnTouchOutside(false);

        mProgressBar = (ProgressBar)findViewById(R.id.upload_progress);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.pull_ref_pb));

    }

    public void uploadLog(){
        String url = Config.loginUrl + "/rpc/upload";
        String token = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.IAROUND_SESSIONKEY);;
        String user = String.valueOf(Common.getInstance().loginUser.getUid());

        UploadZipFileUtils.upload(this, user, url, token);
    }

    @Override
    public void onUploadResult(int state) {
        if(0 == state){
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    CommonFunction.toastMsgLong(BaseApplication.appContext, "报告问题成功");
                    dismiss();
                }
            });
        }else{
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    CommonFunction.toastMsgLong(BaseApplication.appContext, "报告问题失败");
                    dismiss();
                }
            });
        }
    }
}
