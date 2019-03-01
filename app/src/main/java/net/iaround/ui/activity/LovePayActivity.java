package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.model.entity.GoogleNotifyRec;
import net.iaround.model.entity.GooglePayDBBean;
import net.iaround.model.entity.GooglePayNotifyBean;
import net.iaround.pay.ChannelType;
import net.iaround.pay.PayGoodsList;
import net.iaround.pay.activity.AlipayActivity;
import net.iaround.pay.bean.PayChannelBean;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.pay.google.GooglePayActivity;
import net.iaround.pay.google.IabHelper;
import net.iaround.pay.google.IabResult;
import net.iaround.pay.google.Inventory;
import net.iaround.pay.google.Purchase;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.ui.adapter.LovePayAdapter;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.wxapi.WXPayEntryActivity;

import java.util.ArrayList;

/**
 * 爱心支付页面
 * Created by Administrator on 2017/12/13.
 */

public class LovePayActivity extends BaseActivity implements View.OnClickListener, HttpCallBack {

    private final static int REQ_CODE_PAY = 200;
    private long mPayChannelFlag = 0;
    private long notify_flag = 0;

    /**
     * google pay start
     */
    private IabHelper mHelper;
    private ArrayList<String> skuArray;
    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkDjXyvs5ql3E/CWemJDXdhw6HM5LAXtjIs7+jfsESol032Qn+nJUwSvdeNOLOl+Owe3p2sQ/M5v2z5YvuI6IviWcqRAH2gMwpJ1FSbMQMCHq4FWyWpeJCuAumNrBUMARdr+VHy+eYlCgXtwOJNMEi/9Qs1g2P/qQN098KxRJTgxm1nTauFn5Nem0jPSAf2BGw8Th9kAUUGxiw0SiWCqIwwsA9P8OUFJGotFLmijyoozzDT2AVd/cPqQukHA2fxnPtCIncyWv41t9hCyZPL6PN/gJRWBRi5uxQRizd0tjy0Dw9D9nu879AkmPxQAqbsj7iWGxg1gNCx1vGshKSKJl1QIDAQAB";
    private long orderDBID;

    private boolean isChina;
    private String payChannel;
    private ArrayList<PayGoodsBean> goods = new ArrayList<PayGoodsBean>();
    private PayGoodsBean goodsItem;

    private int payModel = 2; //支付方式0-支付宝，1-微信，2-Google

    private LovePayAdapter adapter;

    private TextView totalTv;
    private ListView orderLv;
    private TextView moneyTv;

    private RelativeLayout googleRl;
    private ImageView alipeyIv;
    private ImageView wechatpayIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_pay);
        mContext = this;

        isChina = PhoneInfoUtil.getInstance(mContext).isChinaCarrier();

        initView();
        initData();

    }

    private void initView(){
        TextView titleTv = (TextView)findViewById(R.id.tv_title);
        totalTv = (TextView)findViewById(R.id.tv_love_pay_total);
        orderLv = (ListView)findViewById(R.id.lv_love_pay_order);
        moneyTv = (TextView)findViewById(R.id.tv_love_pay_total_money);

        googleRl = (RelativeLayout)findViewById(R.id.rl_love_pay_select_google);

        alipeyIv = (ImageView)findViewById(R.id.iv_love_pay_select_alipay_check);
        wechatpayIv = (ImageView)findViewById(R.id.iv_love_pay_select_wechatpay_check);

        titleTv.setText(getString(R.string.love_pay_title));

        findViewById(R.id.fl_back).setOnClickListener(this);
        findViewById(R.id.tv_love_pay_recharge).setOnClickListener(this);
        findViewById(R.id.ly_pay_help).setOnClickListener(this);

        findViewById(R.id.rl_love_pay_select_alipay).setOnClickListener(this);
        findViewById(R.id.rl_love_pay_select_wechatpay).setOnClickListener(this);
        googleRl.setOnClickListener(this);
    }

    private void initData() {
        if (adapter == null){
            adapter = new LovePayAdapter(this);
        }

        orderLv.setAdapter(adapter);

        adapter.setListener(new LovePayAdapter.OnItemLovePayListener() {
            @Override
            public void onItemClick(PayGoodsBean bean) {
                goodsItem = bean;
                moneyTv.setText(bean.strPrice);
            }
        });

        // Google支付是否显示
        if (Config.isShowGoogleApp) {
            googleRl.setVisibility(View.VISIBLE);
            findViewById(R.id.ly_love_pay_pay).setVisibility(View.GONE);

            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
                payModel = 3;
            }
        }

        reqListData(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PAY && resultCode == PayGoodsList.RESULT_OK){
            reqListData(false);

            //通知启动购买爱心的activity支付成功
            setResult(Activity.RESULT_OK);
        }
    }

    // 请求爱心订单
    public void reqListData(boolean isShow) {
        if (isShow) showWaitDialog();
        mPayChannelFlag = GoldHttpProtocol.getMywallet(LovePayActivity.this, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_back:
                finish();
                break;
            case R.id.tv_love_pay_recharge:
                if (goodsItem == null)return;
                Intent intent = null;
                if (payModel == 1){
                    intent = new Intent(LovePayActivity.this, AlipayActivity.class);
                    String goodsid = goodsItem.getGoodsid();
                    double price = goodsItem.getPrice();
                    intent.putExtra(AlipayActivity.ALIPAY_GOODSID, goodsid);
                    intent.putExtra(AlipayActivity.ALIPAY_MONEY,price );
                    intent.putExtra(AlipayActivity.ALIPAY_CODE, "");
                }else if (payModel == 2){
                    intent = new Intent(LovePayActivity.this, WXPayEntryActivity.class);
                    intent.putExtra(WXPayEntryActivity.WECHAT_GOODSID, goodsItem.getGoodsid());
                }else{
                    intent = new Intent( LovePayActivity.this , GooglePayActivity.class );
                    intent.putExtra( GooglePayActivity.GOOGLEPAY_GOODSID , goodsItem.getGoodsid( ) );
                }

                if (intent != null){
                    startActivityForResult(intent,REQ_CODE_PAY);
                }

                break;
            case R.id.ly_pay_help:
                jumpWebViewActivity();
                break;
            case R.id.rl_love_pay_select_alipay:
                payModel = 1;
                alipeyIv.setBackgroundResource(R.drawable.love_pay_select);
                wechatpayIv.setBackgroundResource(R.drawable.love_pay_nomal);
                break;
            case R.id.rl_love_pay_select_wechatpay:
                payModel = 2;
                alipeyIv.setBackgroundResource(R.drawable.love_pay_nomal);
                wechatpayIv.setBackgroundResource(R.drawable.love_pay_select);
                break;
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (mPayChannelFlag == flag){
            hideWaitDialog();
            payChannel = result;
            PayChannelBean bean = GsonUtil.getInstance().getServerBean(
                    result, PayChannelBean.class);

            setLoveTotal(bean.love);
            if (bean != null && bean.isSuccess()) {

                if(bean.loves!=null) {
                    goods = bean.loves;
                    if (goods.size() > 0) {
                        goods.get(0).isCheck = true;
                        goodsItem = goods.get(0);
                    }
                    if (!isChina && Config.isShowGoogleApp) {
                        try {
                            initGoogleConnect();
                            showWaitDialog();
                        } catch (Exception e) {
                            for (PayGoodsBean good : goods) {
                                good.strPrice = "NT$" + good.price;
                            }
                            updateDiamond();
                        }
                    } else {
                        for (PayGoodsBean good : goods) {
                            good.strPrice = "¥" + good.price;
                        }
                        updateDiamond();
                    }
                }

            } else {
                // 失败处理
                ErrorCode.toastError(mContext, bean.error);
            }
        }else if (notify_flag == flag){
            GoogleNotifyRec bean = GsonUtil.getInstance().getServerBean(
                    result, GoogleNotifyRec.class);
            if (bean.isSuccess()) {
                deleteOrder();
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        CommonFunction.toastMsg(mContext, R.string.e_104);
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
                   hideWaitDialog();
                    for (PayGoodsBean good : goods) {
                        good.strPrice = "NT$" + good.price;
                    }
                    updateDiamond();
                    return;
                }
                // 查询商品列表(异步)
                skuArray = new ArrayList<String>();
                for (PayGoodsBean good : goods) {
                    skuArray.add(good.goodsid);
                }
                mHelper.queryInventoryAsync(true, skuArray,
                        mQueryFinishedListener);
            }
        });

    }

    // 查询商品列表完成的侦听器
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            hideWaitDialog();
            if (result.isFailure()) {
                for (PayGoodsBean good : goods) {
                    good.strPrice = "NT$" + good.price;
                }
                updateDiamond();
                return;
            }

            for (int i = 0; i < skuArray.size(); i++) {
                String SKU = skuArray.get(i);
                try {
                    if (inventory.getSkuDetails(SKU) != null) {
                        updateProductData(inventory.getSkuDetails(SKU)
                                .getPrice(), SKU, i);
                    }
                    boolean bPurchased = inventory.hasPurchase(SKU);
                    if (bPurchased) {
                        mHelper.consumeAsync(inventory.getPurchase(SKU),
                                mConsumeFinishedListener);
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
    protected void updateProductData(String price, String SKU, int i) {
        for (PayGoodsBean good : goods) {
            if (good.goodsid == SKU) {
                good.strPrice = price;
            }
        }
        updateDiamond();
    }

    // 消费商品完成的侦听器
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                // 获知响应数据
                GooglePayNotifyBean bean = composeSignedData(purchase);
                String signeddata = GsonUtil.getInstance()
                        .getStringFromJsonObject(bean);

                // 插入数据库
                GooglePayDBBean dbBean = new GooglePayDBBean();
                dbBean.signature = purchase.getSignature();
                dbBean.signeddata = signeddata;
                String content = GsonUtil.getInstance()
                        .getStringFromJsonObject(dbBean);

                insertOrder(content);

                // 上报服务端查询用户消费情况
                notify_flag = PayHttpProtocol.googleNotify(mContext,
                        signeddata, purchase.getSignature(),
                        LovePayActivity.this);
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

    private void insertOrder(String content) {
        orderDBID = PayModel.getInstance().insertOrder(getActivity(),
                ChannelType.GOOGLEPAY, content);
    }

    private void deleteOrder() {
        PayModel.getInstance().deleteOrder(getActivity(), orderDBID);
    }

    /**
     * 设置爱心总数
     * @param loveTotal
     */
    private void setLoveTotal(int loveTotal){
        PayModel.getInstance().setDiamondNum(loveTotal);
        totalTv.setText(getString(R.string.love_pay_total)+":  "+loveTotal);
    }

    /**
     * 更新适配器
     */
    private void updateDiamond(){
        adapter.UpdateData(goods);
        CommonFunction.setListViewHeightBasedOnChildren(orderLv);
    }
}
