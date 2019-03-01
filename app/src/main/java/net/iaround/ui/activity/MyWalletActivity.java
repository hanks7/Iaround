package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.model.entity.GoogleNotifyRec;
import net.iaround.model.entity.GooglePayDBBean;
import net.iaround.model.entity.GooglePayNotifyBean;
import net.iaround.pay.ChannelType;
import net.iaround.pay.bean.PayChannelBean;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.pay.google.IabHelper;
import net.iaround.pay.google.IabResult;
import net.iaround.pay.google.Inventory;
import net.iaround.pay.google.Purchase;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.ui.adapter.MyWalletDiamondAdapter;
import net.iaround.ui.adapter.MyWalletStarAdapter;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.ui.view.FitTextView;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.utils.OnClickUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyWalletActivity extends TitleActivity implements View.OnClickListener, HttpCallBack, AdapterView.OnItemClickListener {
    private TextView tvRight;
    private TextView tvTitle;
    private LinearLayout llAnchorIncome;
    private TextView tvIncome;
    private MyGridView mListViewGoodsStar;
    private MyGridView mListViewGoodsDiamond;
    private final static int REQ_CODE_PAY = 200;
    private boolean isChina;
    private FitTextView tvStarCount;
    private FitTextView tvDiamond;
    private FitTextView tvCoin;
    private MyWalletStarAdapter starAdapter;
    private MyWalletDiamondAdapter diamondAdapter;
    /**
     * google pay start
     */
    private IabHelper mHelper;
    private ArrayList<String> skuArray;
    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkDjXyvs5ql3E/CWemJDXdhw6HM5LAXtjIs7+jfsESol032Qn+nJUwSvdeNOLOl+Owe3p2sQ/M5v2z5YvuI6IviWcqRAH2gMwpJ1FSbMQMCHq4FWyWpeJCuAumNrBUMARdr+VHy+eYlCgXtwOJNMEi/9Qs1g2P/qQN098KxRJTgxm1nTauFn5Nem0jPSAf2BGw8Th9kAUUGxiw0SiWCqIwwsA9P8OUFJGotFLmijyoozzDT2AVd/cPqQukHA2fxnPtCIncyWv41t9hCyZPL6PN/gJRWBRi5uxQRizd0tjy0Dw9D9nu879AkmPxQAqbsj7iWGxg1gNCx1vGshKSKJl1QIDAQAB";
    private long orderDBID;
    private long notify_flag = 0;
    //包含钻石和星星商品
    private ArrayList<PayGoodsBean> mDiamonds = new ArrayList<>(); //商品信息
    private ArrayList<PayGoodsBean> mStarts = new ArrayList<>(); //商品信息

    /**
     * google pay end
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isChina = PhoneInfoUtil.getInstance(mContext).isChinaCarrier();
        initTitle();
        initViews();
        Statistics.onPageClick(Statistics.PAGE_DIAMOND_PAY);
    }

    private void initTitle() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.user_wallet_wallet_title), false, 0, getString(R.string.diamond_detailed), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OnClickUtil.isFastClick()) {
                    return;
                }
                DetailedActivity.jump(mContext);
            }
        });
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("updateDiamond");
                finish();
            }
        });
    }

    private void initViews() {
        tvTitle = findView(R.id.tv_title);
        tvRight = findView(R.id.tv_right);
        setContent(R.layout.activity_my_wallet);
        llAnchorIncome = findView(R.id.ll_anchor_income);
        tvIncome = findView(R.id.tv_anchor_income_count);
        mListViewGoodsStar = findView(R.id.list_view_goods_star);
        mListViewGoodsDiamond = findView(R.id.list_view_goods_diamond);
        tvDiamond = findView(R.id.tv_diamond);
        tvCoin = findView(R.id.tv_coin);
        tvStarCount = findView(R.id.tv_star_count);
        llAnchorIncome.setOnClickListener(this);
        starAdapter = new MyWalletStarAdapter(this);
        mListViewGoodsStar.setAdapter(starAdapter);
        diamondAdapter = new MyWalletDiamondAdapter(this);
        mListViewGoodsDiamond.setAdapter(diamondAdapter);
        findViewById(R.id.ly_pay_help).setOnClickListener(this);
        mListViewGoodsStar.setOnItemClickListener(this);
        mListViewGoodsDiamond.setOnItemClickListener(this);
        if (Common.getInstance().loginUser.getGameUserType() == 1) {
            llAnchorIncome.setVisibility(View.VISIBLE);
        } else {
            llAnchorIncome.setVisibility(View.GONE);
        }
        showWaitDialog();
        initDatas();

    }

    private void initDatas() {
        int pageType = getIntent().getIntExtra(Constants.USER_WALLET_PAGE_TYPE, Constants.USER_WALLET_PAGE_WALLET);
        if (pageType == Constants.USER_WALLET_PAGE_WALLET) {
            tvTitle.setText(getString(R.string.user_wallet_wallet_title));
            tvRight.setVisibility(View.VISIBLE);
        } else if (pageType == Constants.USER_WALLET_PAGE_RECHARGE) {
            tvTitle.setText(getString(R.string.user_wallet_recharge_title));
            tvRight.setVisibility(View.GONE);
        }
        GoldHttpProtocol.getMywallet(MyWalletActivity.this, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            EventBus.getDefault().post("updateDiamond");
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (OnClickUtil.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ly_pay_help:
                jumpWebViewActivity();
                break;
            case R.id.ll_anchor_income:
                Intent intent = new Intent(this, GetEarnDetailActivity.class);
                startActivity(intent);
                break;
        }
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
            PayChannelBean payChannelBean = GsonUtil.getInstance().getServerBean(result, PayChannelBean.class);
            if (payChannelBean != null && payChannelBean.isSuccess()) {
                mStarts.clear();
                mDiamonds.clear();
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String todayGameEarnings = decimalFormat.format(payChannelBean.todayGameEarnings);
                tvIncome.setText(todayGameEarnings);
                setGold(payChannelBean.gold);
                setDiamond(payChannelBean.diamond);
                setStar(payChannelBean.star);
                List<PayGoodsBean> listStars = payChannelBean.stars;
                if (listStars != null && listStars.size() > 0) {
                    listStars.get(1).isCheck = true;
                    mStarts.addAll(listStars);
                }
                List<PayGoodsBean> listDiamond = payChannelBean.goods;
                if (listDiamond != null && listDiamond.size() > 0) {
                    listDiamond.get(1).isCheck = true;
                    mDiamonds.addAll(listDiamond);
                }
                if (!isChina) {
                    try {
                        showWaitDialog();
                        initGoogleConnect();
                    } catch (Exception e) {
                        updateStarts();
                        updateDiamonds();
                    }
                } else {
                    updateStartsChina();
                    updateDiamondsChina();
                }
            }
        }
    }

    public void updateStarts() {
        if (mStarts != null && mStarts.size() > 0) {
            for (PayGoodsBean good : mStarts) {
                good.strPrice = "NT$" + good.price;
            }
            starAdapter.update(mStarts);
        }
    }

    public void updateDiamonds() {
        if (mDiamonds != null && mDiamonds.size() > 0) {
            for (PayGoodsBean good : mDiamonds) {
                good.strPrice = "NT$" + good.price;
            }
            diamondAdapter.update(mDiamonds);
        }
    }

    public void updateStartsChina() {
        if (mStarts != null && mStarts.size() > 0) {
            for (PayGoodsBean good : mStarts) {
                good.strPrice = good.price + this.getResources().getString(R.string.rmb);
            }
            starAdapter.update(mStarts);
        }
    }

    public void updateDiamondsChina() {
        if (mDiamonds != null && mDiamonds.size() > 0) {
            for (PayGoodsBean good : mDiamonds) {
                good.strPrice = good.price + this.getResources().getString(R.string.rmb);
            }
            diamondAdapter.update(mDiamonds);
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        ErrorCode.toastError(BaseApplication.appContext, e);
    }


    /**
     * 更新当前用户的金币数
     */
    public void setGold(long gold) {
        PayModel.getInstance().setGoldNum(gold);
        tvCoin.setText(gold + "");
    }

    /**
     * 更新当前用户的钻石数
     */
    public void setDiamond(long diamond) {
        PayModel.getInstance().setDiamondNum(diamond);
        tvDiamond.setText(diamond + "");
    }


    /**
     * 更新当前用户的星星数
     */
    public void setStar(long star) {
        PayModel.getInstance().setStar(star);
        tvStarCount.setText(star + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommonFunction.log("MyWalletActivity", "onActivityResult() into, requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQ_CODE_PAY) {
            if (resultCode == Activity.RESULT_OK) {
                GoldHttpProtocol.getMywallet(MyWalletActivity.this, this);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (OnClickUtil.isFastClick()) {
            return;
        }
        List<PayGoodsBean> dataList = null;
        if (parent.getId() == R.id.list_view_goods_star) {
            dataList = starAdapter.getDataList();
            refrushData(dataList, position, 1);
        } else if (parent.getId() == R.id.list_view_goods_diamond) {
            dataList = diamondAdapter.getDataList();
            refrushData(dataList, position, 2);
        }
    }

    private void refrushData(List<PayGoodsBean> dataList, int position, int type) {
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        PayGoodsBean payGoodsBean = dataList.get(position);
        if (payGoodsBean == null) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            PayGoodsBean payGoods = dataList.get(i);
            if (payGoodsBean.goodsid.equals(payGoods.goodsid)) {
                payGoods.isCheck = true;
            } else {
                payGoods.isCheck = false;
            }
        }
        if (type == 1) {
            starAdapter.notifyDataSetChanged();
        } else {
            diamondAdapter.notifyDataSetChanged();
        }
        PayChannelListActivity.launchForResult(MyWalletActivity.this, payGoodsBean, REQ_CODE_PAY);
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
                    updateStarts();
                    updateDiamonds();
                    return;
                }
                // 查询商品列表(异步)
                skuArray = new ArrayList<>();
                if (mStarts != null && mStarts.size() > 0) {
                    for (PayGoodsBean good : mStarts) {
                        skuArray.add(good.goodsid);
                    }
                }
                if (mDiamonds != null && mDiamonds.size() > 0) {
                    for (PayGoodsBean good : mDiamonds) {
                        skuArray.add(good.goodsid);
                    }
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
                updateStarts();
                updateDiamonds();
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
        if (mStarts != null && mStarts.size() > 0) {
            for (PayGoodsBean good : mStarts) {
                if (good.goodsid.equals(SKU)) {
                    good.strPrice = price;
                }
            }
            starAdapter.update(mStarts);
        }
        if (mDiamonds != null && mDiamonds.size() > 0) {
            for (PayGoodsBean good : mDiamonds) {
                if (good.goodsid.equals(SKU)) {
                    good.strPrice = price;
                }
            }
            diamondAdapter.update(mDiamonds);
        }
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
                notify_flag = PayHttpProtocol.googleNotify(mContext, signeddata, purchase.getSignature(), new HttpCallBackImpl(MyWalletActivity.this));
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
        private WeakReference<MyWalletActivity> mActivity;

        public HttpCallBackImpl(MyWalletActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            MyWalletActivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity) == false) {
                activity.onGeneralSuccess(result, flag);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            MyWalletActivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity) == false) {
                activity.onGeneralError(e, flag);
            }
        }
    }
}
