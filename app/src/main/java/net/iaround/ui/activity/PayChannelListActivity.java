
package net.iaround.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.manager.IaroundPayManager;
import net.iaround.model.im.TransportMessage;
import net.iaround.pay.ChannelType;
import net.iaround.pay.PayTipsDialog;
import net.iaround.statistics.Statistics;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.pay.bean.PayResultBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.utils.OnClickUtil;

import java.util.Calendar;


/**
 * 支付方式列表
 */
public class PayChannelListActivity extends TitleActivity implements OnClickListener, CallBackNetwork, HttpCallBack {

    public static String mOrdernumber = "";
    private long mPayResultQuery = 0;
    private final int PAY_MSG_SUCCES = 1003;
    private Handler mHandler = new MHandler();
    private PayTipsDialog loadingDialog;
    private PayTipsDialog successDialog;
    private PayTipsDialog errDialog;
    private PayGoodsBean goods;
    private boolean isRecievePayResult = false;
    private PayResultBean resultBean;
    //购买vip时候不用对 goods.price赋值，在支付渠道里面
    private RelativeLayout mRlPayAli;
    private RelativeLayout mRlPayWebchat;
    private RelativeLayout mRlPayGoogle;
    private TextView mHelp;
    private String title;
    private int googleApp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();
        setContent(R.layout.x_pay_channellist);
        goods = (PayGoodsBean) getIntent().getSerializableExtra("goods");
        initView();
    }

    private void initView() {
        mRlPayAli = (RelativeLayout) findView(R.id.rl_pay_ali);
        mRlPayWebchat = (RelativeLayout) findView(R.id.rl_pay_webchat);
        mRlPayGoogle = (RelativeLayout) findView(R.id.rl_pay_google);
        mHelp = (TextView) findView(R.id.help);
        mRlPayAli.setOnClickListener(this);
        mRlPayWebchat.setOnClickListener(this);
        mRlPayGoogle.setOnClickListener(this);
        mHelp.setOnClickListener(this);
        SharedPreferenceUtil spUtil = SharedPreferenceUtil.getInstance(getApplicationContext());
        if (Config.isShowGoogleApp) {
            googleApp = spUtil.getInt(SharedPreferenceUtil.GOOGLE_APP);
        }
        if (googleApp == 1) {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
                mRlPayGoogle.setVisibility(View.VISIBLE);
            }
        } else {
            mRlPayAli.setVisibility(View.VISIBLE);
            boolean isInstaledAliWallet = CommonFunction.isClientInstalled(this, "com.tencent.mm");
            if (isInstaledAliWallet) {
                mRlPayWebchat.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initTitle() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.pay_select_type), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(OnClickUtil.isFastClick()){
            return;
        }
        switch (v.getId()) {
            case R.id.rl_pay_ali:
                title = getResources().getString(R.string.alipay);
                start_Pay(ChannelType.ALIPAY);
                break;
            case R.id.rl_pay_webchat:
                title = getResources().getString(R.string.wechatpay);
                start_Pay(ChannelType.WECHATPAY);
                break;
            case R.id.rl_pay_google:
                title = getResources().getString(R.string.google_pay);
                start_Pay(ChannelType.GOOGLEPAY);
                break;
            case R.id.help:
                jumpWebViewActivity(2);
                break;
        }
    }

    public void start_Pay(int alipay) {
        if (goods == null) {
            return;
        }
        IaroundPayManager.getIaroundPayManager()
                .setContext(PayChannelListActivity.this)
                .setChannelID(alipay)
                .setGoodsid(goods.goodsid)
                .startPay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler = null;
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        if (errDialog != null) {
            errDialog.dismiss();
            errDialog = null;
        }
        if (successDialog != null) {
            successDialog.dismiss();
            successDialog = null;
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (mPayResultQuery == flag) {
            resultBean = GsonUtil.getInstance().getServerBean(result, PayResultBean.class);
            if (resultBean.isSuccess()) {
                if (!isRecievePayResult) {
                    isRecievePayResult = true;
                } else {
                    return;
                }
                if (errDialog != null) {
                    errDialog.dismiss();
                    errDialog = null;
                }
                mHandler.sendEmptyMessage(PAY_MSG_SUCCES);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IaroundPayManager.RESQUESTCODE) {
            switch (resultCode) {
                case Activity.RESULT_OK: {// 支付成功
                    if (isRecievePayResult && resultBean != null) {
                        showSuccessDialog();
                    } else {
                        loadingDialog = new PayTipsDialog(this);
                        loadingDialog.show();
                        mPayResultQuery = PayHttpProtocol.payResultQuery(mContext, mOrdernumber, PayChannelListActivity.this);
                        mHandler.postDelayed(runnable, 15000);
                    }
                }
                break;
                case Activity.RESULT_CANCELED: // 取消支付
                    Toast.makeText(this, this.getString(R.string.pay_paypal_cancel), Toast.LENGTH_SHORT).show();
                    break;
                case IaroundPayManager.RESULT_FAILURE: // 支付失败
                    DialogUtil.showOKDialog(this, R.string.pay_error, R.string.pay_error_des, payHelp);
                    break;
                case IaroundPayManager.RESULT_NO_DES: {
                } // 支付失败，无需提示
                break;
            }
        }
    }

    public void finish(int resutcode) {
        Intent intent = new Intent();
        setResult(resutcode, intent);
        super.finish();
    }

    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            switch (msg.what) {
                case PAY_MSG_SUCCES:
                    showSuccessDialog();
                    break;
            }
        }
    }

    protected void onResume() {
        super.onResume();
        CommonFunction.log("PayChannelListActivity", "onResume()");
        ConnectorManage.getInstance(this).setCallBackAction(this);
    }

    @Override
    public void onReceiveMessage(TransportMessage message) {
        CommonFunction.log("PayChannelListActivity", "TransportMessage ==" + message.getContentBody());
        if (message.getMethodId() == MessageID.SESSION_PUHS_PAY_RESULT) {
            if (!isRecievePayResult) {
                isRecievePayResult = true;
            } else {
                return;
            }
            resultBean = GsonUtil.getInstance().getServerBean(message.getContentBody(), PayResultBean.class);
            try {
                if (errDialog != null) {
                    errDialog.dismiss();
                    errDialog = null;
                } else {
                    mHandler.removeCallbacks(runnable);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            mHandler.sendEmptyMessage(PAY_MSG_SUCCES);
        }
    }


    @Override
    public void onSendCallBack(int e, long flag) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onConnected() {
        // TODO Auto-generated method stub
    }

    View.OnClickListener payResultQueryClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mPayResultQuery = PayHttpProtocol.payResultQuery(mContext, mOrdernumber, PayChannelListActivity.this);
        }
    };

    View.OnClickListener payHelp = new OnClickListener() {
        @Override
        public void onClick(View v) {
            jumpWebViewActivity(2);
        }
    };

    /**
     * 跳转到webview
     *
     * @param type （1-遇见会员协议 2-遇见充值帮助）
     */
    private void jumpWebViewActivity(int type) {
        String str = "";
        String url = "";
        if (type == 1) {
            str = getString(R.string.vip_protocol);
            url = CommonFunction.getLangText(PayChannelListActivity.this, Config.VIP_AGREEMENT_URL);
        } else {
            url = CommonFunction.getLangText(PayChannelListActivity.this, Config.iAroundPayFAQUrl);
            str = getResString(R.string.common_questions);
        }
        Intent intent = new Intent(PayChannelListActivity.this, WebViewAvtivity.class);
        intent.putExtra(WebViewAvtivity.WEBVIEW_TITLE, str);
        intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        startActivity(intent);
    }

    /**
     * 获取string
     *
     * @param id
     * @return
     */
    public String getResString(int id) {
        return getResources().getString(id);
    }


    public static void launchForResult(Activity activity, PayGoodsBean goods, int requestCode) {
        Intent intent = new Intent(activity, PayChannelListActivity.class);
        intent.putExtra("goods", goods);
        activity.startActivityForResult(intent, requestCode);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (loadingDialog != null) {
                try {
                    loadingDialog.dismiss();
                    loadingDialog = null;
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            if (isRecievePayResult && resultBean != null) {
                showSuccessDialog();
            } else {
                errDialog = new PayTipsDialog(PayChannelListActivity.this, title, payResultQueryClick, payHelp);
                errDialog.show();
            }
        }
    };

    DialogInterface.OnDismissListener dismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            // TODO Auto-generated method stub
            finish(Activity.RESULT_OK);
        }
    };

    private void showSuccessDialog() {
        String payResult = "";
        if (resultBean.goodstype == 1) { // 钻石
            if (resultBean.senddiamond > 0) {
                payResult = resultBean.diamondnum + "+" + resultBean.senddiamond + getResources().getString(R.string.diamond);
            } else {
                payResult = resultBean.diamondnum + getResources().getString(R.string.diamond);
            }
            //统计会员充值成功
            Statistics.onDiamondPay(goods.price);
        } else if (resultBean.goodstype == 2) {// 会员
            Common.getInstance().loginUser.setSVip(1);
            if (MainFragmentActivity.sInstance != null) {
                MainFragmentActivity.sInstance.freshSpaceVipFlag();
            }
            payResult = goods.name + " " + String.format(getResources().getString(R.string.pay_vip_endtime), TimeFormat.convertTimeLong2String(resultBean.expiredtime, Calendar.DATE));
            //统计会员充值成功
            Statistics.onMemberPay(goods.price);
        } else if (resultBean.goodstype == 3) { //爱心
            payResult = resultBean.diamondnum + getResources().getString(R.string.item_pay_love_order);
        } else if (resultBean.goodstype == 4) { //星星
            payResult = resultBean.diamondnum + getResources().getString(R.string.stars);
        }
        if (successDialog != null) return;
        successDialog = new PayTipsDialog(PayChannelListActivity.this, title, payResult);
        successDialog.show();
        successDialog.setOnDismissListener(dismissListener);
    }
}
