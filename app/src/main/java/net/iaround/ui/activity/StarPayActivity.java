package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.manager.IaroundPayManager;
import net.iaround.model.entity.GoogleNotifyRec;
import net.iaround.model.entity.GooglePayDBBean;
import net.iaround.model.entity.GooglePayNotifyBean;
import net.iaround.model.entity.StarPayBean;
import net.iaround.model.entity.StarPayItemBean;
import net.iaround.pay.ChannelType;
import net.iaround.pay.google.IabHelper;
import net.iaround.pay.google.IabResult;
import net.iaround.pay.google.Inventory;
import net.iaround.pay.google.Purchase;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.adapter.StarPayAdapter;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.ui.view.face.MyGridView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 星星充值页面
 */
public class StarPayActivity extends TitleActivity implements HttpCallBack, StarPayAdapter.OnItemStarPayListener, View.OnClickListener {

    private TextView mStarNum;
    private MyGridView mGvStar;
    private ImageView mIvSelectedWechat;
    private ImageView mIvSelectedAlipay;
    private ImageView mIvSelectedGoogle;
    private TextView mTvPaySubmit;
    private LinearLayout mLlWebchatPay;
    private LinearLayout mLlAliPay;
    private LinearLayout mLlPayGoogle;
    private StarPayAdapter starPayAdapter;
    private int mPosition = 0;
    private int payCurrentType = ChannelType.WECHATPAY;
    private List<ImageView> feedbackTypes = new ArrayList<>();
    private int googleApp = 0;
    private ArrayList<StarPayItemBean> mAllStarts = new ArrayList<>(); //商品信息
    private boolean isChina;
    /**
     * google pay start
     */
    private IabHelper mHelper;
    private ArrayList<String> skuArray;
    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkDjXyvs5ql3E/CWemJDXdhw6HM5LAXtjIs7+jfsESol032Qn+nJUwSvdeNOLOl+Owe3p2sQ/M5v2z5YvuI6IviWcqRAH2gMwpJ1FSbMQMCHq4FWyWpeJCuAumNrBUMARdr+VHy+eYlCgXtwOJNMEi/9Qs1g2P/qQN098KxRJTgxm1nTauFn5Nem0jPSAf2BGw8Th9kAUUGxiw0SiWCqIwwsA9P8OUFJGotFLmijyoozzDT2AVd/cPqQukHA2fxnPtCIncyWv41t9hCyZPL6PN/gJRWBRi5uxQRizd0tjy0Dw9D9nu879AkmPxQAqbsj7iWGxg1gNCx1vGshKSKJl1QIDAQAB";
    private long orderDBID;
    private long notify_flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.user_wallet_diamond_star_pay), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isChina = PhoneInfoUtil.getInstance(mContext).isChinaCarrier();
        setContent(R.layout.star_pay_activity);
        initView();
    }

    private void initView() {
        mStarNum = (TextView) findView(R.id.star_num);
        mGvStar = (MyGridView) findView(R.id.gv_star);
        mIvSelectedWechat = (ImageView) findView(R.id.iv_selected_wechat);
        mIvSelectedAlipay = (ImageView) findView(R.id.iv_selected_alipay);
        mIvSelectedGoogle = (ImageView) findView(R.id.iv_selected_google);
        mLlWebchatPay = (LinearLayout) findView(R.id.ll_webchat_pay);
        mLlAliPay = (LinearLayout) findView(R.id.ll_ali_pay);
        mLlPayGoogle = (LinearLayout) findView(R.id.ll_pay_google);
        mTvPaySubmit = (TextView) findView(R.id.tv_pay_submit);
        feedbackTypes.add(mIvSelectedWechat);
        feedbackTypes.add(mIvSelectedAlipay);
        feedbackTypes.add(mIvSelectedGoogle);
        mLlWebchatPay.setOnClickListener(this);
        mLlAliPay.setOnClickListener(this);
        mLlPayGoogle.setOnClickListener(this);
        mTvPaySubmit.setOnClickListener(this);
        findViewById(R.id.ly_pay_help).setOnClickListener(this);
        starPayAdapter = new StarPayAdapter(this);
        mGvStar.setAdapter(starPayAdapter);
        starPayAdapter.setOnItemStarPayListener(this);
        SharedPreferenceUtil spUtil = SharedPreferenceUtil.getInstance(getApplicationContext());
        if (Config.isShowGoogleApp) {
            googleApp = spUtil.getInt(SharedPreferenceUtil.GOOGLE_APP);
        }
        if (googleApp == 1) {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
                mLlPayGoogle.setVisibility(View.VISIBLE);
                feedbackTypes.get(2).setSelected(true);
                payCurrentType = ChannelType.GOOGLEPAY;
            }
        } else {
            mLlAliPay.setVisibility(View.VISIBLE);
            boolean isInstaledAliWallet = CommonFunction.isClientInstalled(this, "com.tencent.mm");
            if (isInstaledAliWallet) {
                mLlWebchatPay.setVisibility(View.VISIBLE);
                feedbackTypes.get(0).setSelected(true);
                payCurrentType = ChannelType.WECHATPAY;
            }else {
                feedbackTypes.get(1).setSelected(true);
                payCurrentType = ChannelType.ALIPAY;
            }
        }
        showWaitDialog();
        initData();
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        if (flag == notify_flag) {
            GoogleNotifyRec bean = GsonUtil.getInstance().getServerBean(result, GoogleNotifyRec.class);
            if (bean.isSuccess()) {
                PayModel.getInstance().deleteOrder(getActivity(), orderDBID);
            }
        } else {
            StarPayBean starPayBean = GsonUtil.getInstance().getServerBean(result, StarPayBean.class);
            if (starPayBean != null && starPayBean.isSuccess()) {
                mAllStarts.clear();
                mStarNum.setText(starPayBean.stars + "");
                List<StarPayItemBean> list = starPayBean.starlist;
                if (list != null && list.size() > 0) {
                    list.get(0).isCheck = true;
                    mAllStarts.addAll(list);
                    if (!isChina) {
                        try {
                            showWaitDialog();
                            initGoogleConnect();
                        } catch (Exception e) {
                            for (StarPayItemBean good : mAllStarts) {
                                good.strPrice = "NT$" + good.price;
                            }
                            starPayAdapter.updateList(mAllStarts);
                        }
                    } else {
                        for (StarPayItemBean good : mAllStarts) {
                            good.strPrice = good.price + this.getResources().getString(R.string.rmb);
                        }
                        mTvPaySubmit.setText(this.getResources().getString(R.string.star_pay) + mAllStarts.get(0).strPrice);
                        starPayAdapter.updateList(mAllStarts);
                    }
                }
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        ErrorCode.toastError(BaseApplication.appContext, e);
    }

    private void initData() {
        GoldHttpProtocol.getStar(this, this);
    }

    @Override
    public void onItemClick(StarPayItemBean bean, int position) {
        this.mPosition = position;
        mTvPaySubmit.setText(this.getResources().getString(R.string.star_pay) + bean.strPrice);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_webchat_pay:
                changeType(0);
                payCurrentType = ChannelType.WECHATPAY;
                break;
            case R.id.ll_ali_pay:
                changeType(1);
                payCurrentType = ChannelType.ALIPAY;
                break;
            case R.id.ll_pay_google:
                changeType(2);
                payCurrentType = ChannelType.GOOGLEPAY;
                break;
            case R.id.tv_pay_submit:
                List<StarPayItemBean> dataList = starPayAdapter.getDataList();
                if (dataList == null || dataList.size() == 0) {
                    return;
                }
                IaroundPayManager.getIaroundPayManager()
                        .setContext(StarPayActivity.this)
                        .setChannelID(payCurrentType)
                        .setGoodsid(dataList.get(mPosition).goodsid)
                        .startPay();
                break;
            case R.id.ly_pay_help:
                jumpWebViewActivity();
                break;
        }
    }

    private void changeType(int type) {
        for (int i = 0; i < feedbackTypes.size(); i++) {
            if (i == type) {
                feedbackTypes.get(type).setSelected(true);
            } else {
                feedbackTypes.get(i).setSelected(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IaroundPayManager.RESQUESTCODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:// 支付成功
                    initData();
                    break;
                case Activity.RESULT_CANCELED: // 取消支付
                    Toast.makeText(this, this.getString(R.string.pay_paypal_cancel), Toast.LENGTH_SHORT).show();
                    break;
                case IaroundPayManager.RESULT_FAILURE: // 支付失败
                    break;
                case IaroundPayManager.RESULT_NO_DES: // 支付失败，无需提示
                    break;
            }
        }
    }

    /**
     * 连接谷歌，查询商品列表
     */
    private void initGoogleConnect() {
        mHelper = new IabHelper(mContext, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    destroyWaitDialog();
                    for (StarPayItemBean good : mAllStarts) {
                        good.strPrice = "NT$" + good.price;
                    }
                    starPayAdapter.updateList(mAllStarts);
                    return;
                }
                // 查询商品列表(异步)
                skuArray = new ArrayList<>();
                for (StarPayItemBean good : mAllStarts) {
                    skuArray.add(good.goodsid);
                }
                mHelper.queryInventoryAsync(true, skuArray, mQueryFinishedListener);
            }
        });
    }

    // 查询商品列表完成的侦听器
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            destroyWaitDialog();
            if (result.isFailure()) {
                for (StarPayItemBean good : mAllStarts) {
                    good.strPrice = "NT$" + good.price;
                }
                starPayAdapter.updateList(mAllStarts);
                return;
            }

            for (int i = 0; i < skuArray.size(); i++) {
                String SKU = skuArray.get(i);
                try {
                    if (inventory.getSkuDetails(SKU) != null) {
                        updateProductData(inventory.getSkuDetails(SKU).getPrice(), SKU, i);
                    }
                    boolean bPurchased = inventory.hasPurchase(SKU);
                    if (bPurchased) {
                        mHelper.consumeAsync(inventory.getPurchase(SKU), mConsumeFinishedListener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 从谷歌获取到转换后的价格 更新页面
     *
     * @param price
     * @param SKU
     */
    private void updateProductData(String price, String SKU, int i) {
        for (StarPayItemBean good : mAllStarts) {
            if (good.goodsid == SKU) {
                good.strPrice = price;
            }
        }
        mTvPaySubmit.setText(this.getResources().getString(R.string.star_pay) + mAllStarts.get(0).strPrice);
        starPayAdapter.updateList(mAllStarts);
    }

    // 消费商品完成的侦听器
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                // 获知响应数据
                GooglePayNotifyBean bean = composeSignedData(purchase);
                String signeddata = GsonUtil.getInstance().getStringFromJsonObject(bean);

                // 插入数据库
                GooglePayDBBean dbBean = new GooglePayDBBean();
                dbBean.signature = purchase.getSignature();
                dbBean.signeddata = signeddata;
                String content = GsonUtil.getInstance().getStringFromJsonObject(dbBean);
                orderDBID = PayModel.getInstance().insertOrder(getActivity(), ChannelType.GOOGLEPAY, content);
                // 上报服务端查询用户消费情况
                notify_flag = PayHttpProtocol.googleNotify(mContext, signeddata, purchase.getSignature(), new HttpCallBackImpl(StarPayActivity.this));
            }
        }
    };

    private GooglePayNotifyBean composeSignedData(Purchase purchase) {
        if (purchase == null) {
            return null;
        }
        GooglePayNotifyBean bean = new GooglePayNotifyBean();
        bean.nonce = "0";// 服务端没用字段
        GooglePayNotifyBean.GoogleOrder order = new GooglePayNotifyBean.GoogleOrder();
        order.token = purchase.getToken();
        order.notificationId = "";// 服务端没用字段
        order.orderId = purchase.getOrderId();
        order.packageName = purchase.getPackageName();
        order.productId = purchase.getSku();
        order.purchaseTime = purchase.getPurchaseTime();
        order.purchaseState = purchase.getPurchaseState();
        order.developerPayload = purchase.getDeveloperPayload();
        bean.orders.add(order);
        return bean;
    }

    static class HttpCallBackImpl implements HttpCallBack {
        private WeakReference<StarPayActivity> mActivity;

        public HttpCallBackImpl(StarPayActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            StarPayActivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity) == false) {
                activity.onGeneralSuccess(result, flag);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            StarPayActivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity) == false) {
                activity.onGeneralError(e, flag);
            }
        }
    }
}
