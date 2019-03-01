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
import net.iaround.conf.Config;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.ShareHttpProtocol;
import net.iaround.model.im.LuckPanBean;
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
 * 聊吧分享
 * Created by liangyuanhuan on 19/10/2017.
 */

public class ShareChatbarDialog extends Dialog implements ShareActionListener {
    private static final String TAG = "ShareChatbarDialog";

    //request code
    public static final int REQUEST_CODE_WEIBO_AUTH = 32973;
    public static final int REQUEST_CODE_QQ_AUTH = 11101;
    public static final int REQUEST_CODE_QQ_SHARE = 10103;
    public static final int REQUEST_CODE_QZONE_SHARE = 10104;

    private int SHARE_GET_LOTTERY_TIMES = 1;
    private static boolean sShowingShareChatbar = false;
    private static String sSharePicUrl = null;
    private static int sWeChatAppState = -1; // -1 安装包未检测 1 已安装 0 未安装
    private static int sWeiboAppState = -1; // -1 安装包未检测 1 已安装 0 未安装
    private static int sQQAppState = -1; // -1 安装包未检测 1 已安装 0 未安装
    private static ShareChatbarDialog sCustomChatBarDialog = null;
    private Context mContext;
    private TextView mShareGetLotteryView;
    private ImageView mWechat;
    private ImageView mWZone;
    private ImageView mWeibo;
    private ImageView mQQ;
    private ImageView mQZone;
    private Button mCancel;
    private AbstractShareUtils mAbstractShareUtils = null;
    private boolean mSharing = false;
    private int mShareType = 0; // 1 "Wechat" ; 2 "WZone" ; 3 "Weibo"; 4 "QQ" ; 5 "QZone"
    private ShareSuccessListener mListener = null;

    //抽奖回调接口
    public interface ShareSuccessListener{
        //free 分享成功后获得的免费抽奖次数
        void onShareSuccess(int free);
    }

    /* sharePicUrl 分享出去的图片地址
    * */
    public static void showDialog(Context mContext, String sharePicUrl, int freeLottery, ShareSuccessListener listener){
        if(sShowingShareChatbar == true){
            return;
        }
        sShowingShareChatbar = true;
        sSharePicUrl = sharePicUrl;
        sCustomChatBarDialog = new ShareChatbarDialog(mContext);
        sCustomChatBarDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(android.content.DialogInterface dialog) {
                CommonFunction.log(TAG, "dialog dismiss...");
                sShowingShareChatbar = false;
                if(null!=sCustomChatBarDialog){
                    if(sCustomChatBarDialog.mAbstractShareUtils!=null){
                        sCustomChatBarDialog.mAbstractShareUtils.setShareActionListener(null);
                        sCustomChatBarDialog.mAbstractShareUtils = null;
                    }
                    sCustomChatBarDialog = null;
                }
            }
        });
        sCustomChatBarDialog.setFreeLottery(freeLottery);
        sCustomChatBarDialog.setShareSuccessListener(listener);
        CommonFunction.log(TAG, "dialog show...");
        sCustomChatBarDialog.show();
    }

    protected ShareChatbarDialog(Context context) {
        super(context, R.style.chat_bar_transparent_dialog);
        this.mContext = context;

    }

    protected ShareChatbarDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbar_share);

        initView();

    }

    private void initView() {

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);

        //底部弹出样式
        //window.setWindowAnimations(R.style.popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setCancelable(true); //BACK 按钮
        setCanceledOnTouchOutside(false);

        mShareGetLotteryView = (TextView) findViewById(R.id.share_get_lottery);
        mShareGetLotteryView.setText(String.format(mContext.getString(R.string.share_get_lottery),SHARE_GET_LOTTERY_TIMES));

        if(-1 == sWeChatAppState){
            IARWeixin wechat = IARWeixin.getInstance();
            wechat.initApi(this.mContext, "wx0e4a408c5839e9c4");//wx3a3e1883deda3c59
            if(wechat.isAvailable()){
                sWeChatAppState = 1;
            }else{
                sWeChatAppState = 0;
            }
        }
        if(1 == sWeChatAppState){
            mWechat = (ImageView) findViewById(R.id.share_wechat);
            mWechat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(true == mSharing){
                        return;
                    }
                    WechatSessionUtil share = (WechatSessionUtil)AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(),26);
                    if(null==share) {
                        return;
                    }
                    mAbstractShareUtils = share;
                    mSharing = true;
                    CommonFunction.log(TAG, "dialog share to wechat chat...");
                    mShareType = 1;
                    share.setShareActionListener(ShareChatbarDialog.this);
                    share.share2Weibo((Activity) mContext,null,null,null,null,sSharePicUrl);
                }
            });
            mWZone = (ImageView) findViewById(R.id.share_wzone);
            mWZone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(true == mSharing){
                        return;
                    }
                    WechatGroupUtil share = (WechatGroupUtil)AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(),27);
                    if(null==share) {
                        return;
                    }
                    mAbstractShareUtils = share;
                    mSharing = true;
                    CommonFunction.log(TAG, "dialog share to wechat group...");
                    mShareType = 2;
                    share.setShareActionListener(ShareChatbarDialog.this);
                    share.share2Weibo((Activity) mContext,null,null,null,null,sSharePicUrl);
                }
            });
        }else{
            LinearLayout wechatContainer = (LinearLayout) findViewById(R.id.share_wechat_container);
            LinearLayout wzoneContainer = (LinearLayout) findViewById(R.id.share_wzone_container);
            wechatContainer.setVisibility(View.GONE);
            wzoneContainer.setVisibility(View.GONE);
        }

        if(-1 == sWeiboAppState){
            WeiboAppManager.WeiboInfo weibo = WeiboAppManager.getInstance(mContext).getWeiboInfo();
            if(weibo!=null && weibo.isLegal()){
                sWeiboAppState = 1;
            }else{
                sWeiboAppState = 0;
            }
        }
        if( 1 == sWeiboAppState ){
            mWeibo = (ImageView) findViewById(R.id.share_weibo);
            mWeibo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(true == mSharing){
                        return;
                    }
                    SinaWeiboUtil share = (SinaWeiboUtil)AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(),12);
                    if(null==share){
                        return;
                    }
                    mAbstractShareUtils = share;
                    mSharing = true;
                    CommonFunction.log(TAG, "dialog share to weibo...");
                    mShareType = 3;
                    share.setShareActionListener(ShareChatbarDialog.this);
                    share.share2Weibo((Activity) mContext,mContext.getString(R.string.share_title),mContext.getString(R.string.share_content),null,null,sSharePicUrl);
                }
            });
        }else{
            LinearLayout weiboContainer = (LinearLayout) findViewById(R.id.share_weibo_container);
            weiboContainer.setVisibility(View.GONE);
        }

        if(-1 == sQQAppState){
            Tencent tencent  = Tencent.createInstance("100297231",mContext);
            if(null!=tencent && tencent.isSupportSSOLogin((Activity)mContext)){
                sQQAppState = 1;
            }else{
                sQQAppState = 0;
            }
        }
        if( 1 == sQQAppState ){
            mQQ = (ImageView) findViewById(R.id.share_qq);
            mQQ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(true == mSharing){
                        return;
                    }
                    QQUtil share = (QQUtil)AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(),28);
                    if(null==share) {
                        return;
                    }
                    mAbstractShareUtils = share;
                    mSharing = true;
                    CommonFunction.log(TAG, "dialog share to qq chat...");
                    mShareType = 4;
                    share.setShareActionListener(ShareChatbarDialog.this);
                    share.share2Weibo((Activity) mContext,null,null,null,null,sSharePicUrl);
                }
            });
            mQZone = (ImageView) findViewById(R.id.share_qzone);
            mQZone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(true == mSharing){
                        return;

                    }
                    QQZoneUtil share = (QQZoneUtil)AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(),25);
                    if(null==share) {
                        return;
                    }
                    mAbstractShareUtils = share;
                    mSharing = true;
                    CommonFunction.log(TAG, "dialog share to qq zone...");
                    mShareType = 5;
                    share.setShareActionListener(ShareChatbarDialog.this);
                    share.share2Weibo((Activity) mContext,mContext.getString(R.string.share_title),mContext.getString(R.string.share_content),"http://notice.iaround.com/share/index.html",null,sSharePicUrl);
                }
            });
        }else{
            LinearLayout qqContainer = (LinearLayout) findViewById(R.id.share_qq_container);
            LinearLayout qzoneContainer = (LinearLayout) findViewById(R.id.share_qzone_container);
            qqContainer.setVisibility(View.GONE);
            qzoneContainer.setVisibility(View.GONE);
        }

        mCancel = (Button) findViewById(R.id.share_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareChatbarDialog.this.dismiss();
                mSharing = false;
            }
        });
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data ){
        CommonFunction.log(TAG, "onActivityResult() into, requesetCode=" + requestCode + ", resultCode=" + requestCode );
        if(sShowingShareChatbar == true && sCustomChatBarDialog!=null){
            if(sCustomChatBarDialog.mAbstractShareUtils != null){
                CommonFunction.log(TAG, "call AbstractShareUtils onActivityResult()");
                sCustomChatBarDialog.mAbstractShareUtils.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    public static void onNewIntent(Intent intent ) {
        CommonFunction.log(TAG, "onNewIntent() into" );
        if(sShowingShareChatbar == true && sCustomChatBarDialog!=null){
            sCustomChatBarDialog.mAbstractShareUtils.onNewIntent(intent);
        }
    }

    //分享回调接口
    @Override
    public void onComplete(AbstractShareUtils abstractShareUtils, int action, Map<String, Object> map) {
        CommonFunction.log(TAG, "onComplete() into, action=" + action);
        if(action == AbstractShareUtils.ACTION_UPLOADING) {
            mSharing = false;
            ShareHttpProtocol.shareSuccessPost(BaseApplication.appContext, mShareType, new HttpCallBack() {
                @Override
                public void onGeneralSuccess(String result, long flag) {
                    if (result != null) {
                        ShareChatbarBean bean = GsonUtil.getInstance().getServerBean(result,ShareChatbarBean.class);
                        if (null!=bean && bean.isSuccess()) {
                            if(ShareChatbarDialog.this.mListener!=null){
                                ShareChatbarDialog.this.mListener.onShareSuccess(bean.getShareFlag());
                            }
                        }
                    }
                }

                @Override
                public void onGeneralError(int e, long flag) {

                }
            });
            this.dismiss();
            CommonFunction.showToast(mContext, mContext.getString(R.string.share_success), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onError(AbstractShareUtils abstractShareUtils, int action, Throwable throwable) {
        CommonFunction.log(TAG, "onError() into, action=" + action);
        this.dismiss();
        mSharing = false;
        CommonFunction.showToast(mContext,mContext.getString(R.string.share_fail), Toast.LENGTH_SHORT);
    }

    @Override
    public void onCancel(AbstractShareUtils abstractShareUtils, int action) {
        CommonFunction.log(TAG, "onCancel() into, action=" + action);
        mSharing = false;
        this.dismiss();
    }

    public void setShareSuccessListener(ShareSuccessListener listener){
        this.mListener = listener;
    }

    public void setFreeLottery(int free){
        if(free>=1) {
            this.SHARE_GET_LOTTERY_TIMES = free;
        }
    }
}