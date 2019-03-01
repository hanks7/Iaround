package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.contract.UserVipOpenContract;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.entity.GoogleNotifyRec;
import net.iaround.model.entity.GooglePayDBBean;
import net.iaround.model.entity.GooglePayNotifyBean;
import net.iaround.model.entity.GooglePayNotifyBean.GoogleOrder;
import net.iaround.model.im.BaseServerBean;
import net.iaround.pay.ChannelType;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.pay.bean.VipBuyMemberListBean;
import net.iaround.pay.bean.VipBuyMemberListBean.Goods;
import net.iaround.pay.google.IabHelper;
import net.iaround.pay.google.IabResult;
import net.iaround.pay.google.Inventory;
import net.iaround.pay.google.Purchase;
import net.iaround.presenter.UserVipOpenPresenter;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PermissionUtils;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.PayModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author：liush 通过点击会员中心的开通会员跳转过来
 */
public class UserVipOpenActivity extends TitleActivity implements View.OnClickListener, UserVipOpenContract.View, HttpCallBack {

    private UserVipOpenPresenter presenter;
    private List<LinearLayout> packageList = new ArrayList<>();//套餐布局的集合
    private List<Boolean> packageListStatus = new ArrayList<>();//套餐选中状态记录

    private int lastSelected = 0;
    private LinearLayout llOpenVipSixMonth;
    private LinearLayout llOpenVipTwelveMonth;
    private LinearLayout llOpenVipThreeMonth;
    private LinearLayout llOpenVipOneMonth;
    private TextView tvPackageSixMonth;
    private TextView tvPackageSixDollar;
    private TextView tvPackageSixSave;
    private TextView tvPackageTwelveMonth;
    private TextView tvPackageTwelveDollar;
    private TextView tvPackageTwelveSave;
    private TextView tvPackageThreeMonth;
    private TextView tvPackageThreeDollar;
    private TextView tvPackageThreeSave;
    private TextView tvPackageOneMonth;
    private TextView tvPackageOneDollar;
    private TextView tvPackageOneSave;
    private LinearLayout llOpenPayHelp;
    private TextView tvIaroundText;
    private TextView orderNew;
    private String packagePrefix;
    private String packageSuffix;
    private NetImageView ivVipBanner;
    /**
     * 新需求，跳转到新的页面只有三个按钮，点击每一个按钮都会跳转到支付界面
     */
    private TextView tvNewPay;
    private LinearLayout llNewPay;
    /**
     * 咪咕动漫联合会员支付
     */
    private LinearLayout llMiguAnimationPay;

    private int DefaulHeadPic = R.drawable.vp_demo;
    private String Uid = "";
    private long orderDBID;
    private int currentMonth = 0;

    public ArrayList<Goods> goods;// 购买会员列表
    private VipBuyMemberListBean bean;
    public ArrayList<Goods> goodsTemp;
    private String payChannel = "";
    private static final int SHOW_DATA = 1000;
    private static final int GET_DATA_FAIL = 1001;
    private static final int PAY_REQUESTCODE = 1002;

    private static final int INSERT_ORDER_TO_DATABASE = 1003;
    private static final int DELETE_ORDER_FROME_DATABASE = 1004;
    /**
     * 是否为大陆地区，1是，2否
     */
    private int isChina = 1;
    private long mGetMemberList;
    private long notify_flag;
    private long mGetMiguStatus;

    /**
     * google pay start
     */
    private IabHelper mHelper;
    private ArrayList<String> skuArray;
    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkDjXyvs5ql3E/CWemJDXdhw6HM5LAXtjIs7+jfsESol032Qn+nJUwSvdeNOLOl+Owe3p2sQ/M5v2z5YvuI6IviWcqRAH2gMwpJ1FSbMQMCHq4FWyWpeJCuAumNrBUMARdr+VHy+eYlCgXtwOJNMEi/9Qs1g2P/qQN098KxRJTgxm1nTauFn5Nem0jPSAf2BGw8Th9kAUUGxiw0SiWCqIwwsA9P8OUFJGotFLmijyoozzDT2AVd/cPqQukHA2fxnPtCIncyWv41t9hCyZPL6PN/gJRWBRi5uxQRizd0tjy0Dw9D9nu879AkmPxQAqbsj7iWGxg1gNCx1vGshKSKJl1QIDAQAB";


    private Goods data;

    /**
     * google pay end
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new UserVipOpenPresenter(this);
        Uid = String.valueOf(Common.getInstance().loginUser.getUid());
        initViews();
        initDatas();
        initListeners();

        Statistics.onPageClick(Statistics.PAGE_MEMBER_PAY);
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.user_vip_open_title), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_user_vip_open);
        llOpenVipSixMonth = findView(R.id.ll_open_vip_six_month);
        tvPackageSixMonth = findView(R.id.tv_package_six_month);
        tvPackageSixDollar = findView(R.id.tv_package_six_dollar);
        tvPackageSixSave = findView(R.id.tv_package_six_save);
        packageList.add(llOpenVipSixMonth);
        packageListStatus.add(false);
        setSelectedPackage(0);
        llOpenVipTwelveMonth = findView(R.id.ll_open_vip_twelve_month);
        tvPackageTwelveMonth = findView(R.id.tv_package_twelve_month);
        tvPackageTwelveDollar = findView(R.id.tv_package_twelve_dollar);
        tvPackageTwelveSave = findView(R.id.tv_package_twelve_save);
        packageList.add(llOpenVipTwelveMonth);
        packageListStatus.add(false);
        llOpenVipThreeMonth = findView(R.id.ll_open_vip_three_month);
        tvPackageThreeMonth = findView(R.id.tv_package_three_month);
        tvPackageThreeDollar = findView(R.id.tv_package_three_dollar);
        tvPackageThreeSave = findView(R.id.tv_package_three_save);
        packageList.add(llOpenVipThreeMonth);
        packageListStatus.add(false);
        llOpenVipOneMonth = findView(R.id.ll_open_vip_one_month);
        tvPackageOneMonth = findView(R.id.tv_package_one_month);
        tvPackageOneDollar = findView(R.id.tv_package_one_dollar);
        tvPackageOneSave = findView(R.id.tv_package_one_save);
        packageList.add(llOpenVipOneMonth);
        packageListStatus.add(false);
        llOpenPayHelp = (LinearLayout) findViewById(R.id.ll_open_pay_help);
        tvIaroundText = (TextView) findViewById(R.id.tv_iaround_text);
        ivVipBanner = (NetImageView) findViewById(R.id.iv_vip_banner);
        tvNewPay = findView(R.id.tv_new_pay);
        llNewPay = (LinearLayout) findViewById(R.id.ll_open_new_pay);
        llMiguAnimationPay = (LinearLayout) findViewById(R.id.ll_open_migu_animation_pay);

        orderNew = (TextView) findViewById(R.id.tv_pay_vip_order_new);
        orderNew.setOnClickListener(this);
        tvNewPay.setOnClickListener(this);
        llNewPay.setOnClickListener(this);
        llMiguAnimationPay.setOnClickListener(this);


    }

    private void initDatas() {
        int showMigu = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.SHOWMIGU);
        int showManlian = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.SHOW_MANLIAN);

        if (showManlian == 0) {
            llMiguAnimationPay.setVisibility(View.GONE);
        } else {
            llMiguAnimationPay.setVisibility(View.VISIBLE);
        }

        if (showMigu == 1) {
            int languageIndex = CommonFunction.getLanguageIndex(this);
            if (languageIndex == 1) {
                llNewPay.setVisibility(View.VISIBLE);
                tvNewPay.setVisibility(View.VISIBLE);

            } else {
                llNewPay.setVisibility(View.GONE);
                tvNewPay.setVisibility(View.GONE);
            }
//            orderNew.setVisibility(View.VISIBLE);
//            llNewPay.setVisibility(View.VISIBLE);
        } else {
            orderNew.setVisibility(View.GONE);
//            llNewPay.setVisibility(View.GONE);
        }


        packagePrefix = getResString(R.string.user_vip_open_month_prefix);
        packageSuffix = getResString(R.string.user_vip_open_price_suffix);
        goodsTemp = new ArrayList<>();
//        presenter.init(CommonFunction.getLanguageIndex(mContext), CommonFunction.getPackageMetaData(this));
        requestData();
    }


    /**
     * 请求购买会员列表列表数据
     */
    private void requestData() {
        boolean area = PhoneInfoUtil.getInstance(mContext).isChinaCarrier();
        isChina = area ? 1 : 2;
        CommonFunction.log("", "--->isChina = " + isChina);
        mGetMemberList = GoldHttpProtocol.getBuyMemberList(mContext,
                String.valueOf(isChina), this);
    }

    private void initListeners() {
        llOpenVipSixMonth.setOnClickListener(this);
        llOpenVipTwelveMonth.setOnClickListener(this);
        llOpenVipThreeMonth.setOnClickListener(this);
        llOpenVipOneMonth.setOnClickListener(this);
        llOpenPayHelp.setOnClickListener(this);
        tvIaroundText.setOnClickListener(this);
        ivVipBanner.setOnClickListener(this);
    }

    private void setSelectedPackage(int index) {
        packageList.get(lastSelected).setSelected(false);
        packageListStatus.set(lastSelected, false);
        lastSelected = index;
        packageList.get(index).setSelected(true);
        packageListStatus.set(index, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_open_vip_six_month:
//                if (packageListStatus.get(0)) {
//
////                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
//                } else {
//                    setSelectedPackage(0);
//                }
                setSelectedPackage(0);
                if (goodsTemp.size() <= 0) return;
                data = goodsTemp.get(0);
                if (data != null) {
                    currentMonth = data.month;
                    lanchPayChannelActivity(data);
                }
                break;
            case R.id.ll_open_vip_twelve_month:
                setSelectedPackage(1);
                if (goodsTemp.size() <= 1) return;
                data = goodsTemp.get(1);
                if (data != null) {
                    currentMonth = data.month;
                    lanchPayChannelActivity(data);
                }
//                if (packageListStatus.get(1)) {

//                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
//                } else {
//                    setSelectedPackage(1);
//                }
                break;
            case R.id.ll_open_vip_three_month:
                setSelectedPackage(2);
                if (goodsTemp.size() <= 2) return;
                data = goodsTemp.get(2);
                if (data != null) {
                    currentMonth = data.month;
                    lanchPayChannelActivity(data);
                }
//                if (packageListStatus.get(2)) {
//                    Goods data = (Goods) goodsTemp.get(2);
//                    if (data != null) {
//                        currentMonth = data.month;
//                        lanchPayChannelActivity(data);
//                    }
////                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
//                } else {
//                    setSelectedPackage(2);
//                }
                break;
            case R.id.ll_open_vip_one_month:
                setSelectedPackage(3);
                if (goodsTemp.size() <= 3) return;
                data = goodsTemp.get(3);
                if (data != null) {
                    currentMonth = data.month;
                    lanchPayChannelActivity(data);
                }
//                if (packageListStatus.get(3)) {
//                    Goods data = (Goods) goodsTemp.get(3);
//                    if (data != null) {
//                        currentMonth = data.month;
//                        lanchPayChannelActivity(data);
//                    }
////                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
//                } else {
//                    setSelectedPackage(3);
//                }
                break;
            case R.id.ll_open_pay_help:
                jumpWebViewActivity(2);
                break;
            case R.id.tv_iaround_text:
                jumpWebViewActivity(1);
                break;
            case R.id.tv_pay_vip_order_new:
                requestPayMiGu();
                break;
            case R.id.ll_open_new_pay:
            case R.id.tv_new_pay:
                Intent intent = new Intent();
                intent.setClass(this, UserVipNewPayActivity.class);
                intent.putExtra(UserVipNewPayActivity.KEY_HEADER_ICON, bean.icon);
                startActivity(intent);
                break;
            case R.id.ll_open_migu_animation_pay:
                requestPermission(new String[]{PermissionUtils.PERMISSION_SEND_SMS});
                break;
        }
    }

    @Override
    public void doPermission() {
        Intent intent1 = new Intent();
        intent1.setClass(this, UserMiGuAnimationVipPayActivity.class);
        intent1.putExtra(UserVipNewPayActivity.KEY_HEADER_ICON, bean.icon);
        startActivity(intent1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent1 = new Intent();
            intent1.setClass(this, UserMiGuAnimationVipPayActivity.class);
            intent1.putExtra(UserVipNewPayActivity.KEY_HEADER_ICON, bean.icon);
            startActivity(intent1);
        }
    }

    /**
     * 咪咕订购状态
     */
    private void requestPayMiGu() {
        mGetMiguStatus = GoldHttpProtocol.getMiguStatus(this, this);
    }

    @Override
    public void setPackageData(ArrayList<Goods> goods) {
        /**
         * 由于用的老接口，新的项目数据顺序有改动 goods:   index 0:十二个月    1：一个月   2：三个月   3：六个月（不同语言顺序不一样）
         * 这个新版本不同语言顺序一样，后台直接改顺序即可
         */
        this.goods = goods;
        if (goods != null && goods.size() > 0) {
            tvPackageSixMonth.setText(goods.get(1).month + packageSuffix);
            tvPackageSixDollar.setText(packagePrefix + goods.get(1).price);
            if (!TextUtils.isEmpty(goods.get(1).discount)) {
                tvPackageSixSave.setVisibility(View.VISIBLE);
                tvPackageSixSave.setText(CommonFunction.getLangText(UserVipOpenActivity.this, goods.get(1).discount));
            } else {
                tvPackageSixSave.setVisibility(View.GONE);
            }

            tvPackageTwelveMonth.setText(goods.get(0).month + packageSuffix);
            tvPackageTwelveDollar.setText(packagePrefix + goods.get(0).price);
            if (!TextUtils.isEmpty(goods.get(0).discount)) {
                tvPackageTwelveSave.setVisibility(View.VISIBLE);
                tvPackageTwelveSave.setText(CommonFunction.getLangText(this, goods.get(0).discount));
            } else {
                tvPackageTwelveSave.setVisibility(View.GONE);
            }

            tvPackageThreeMonth.setText(goods.get(2).month + packageSuffix);
            tvPackageThreeDollar.setText(packagePrefix + goods.get(2).price);
            if (!TextUtils.isEmpty(goods.get(2).discount)) {
                tvPackageThreeSave.setVisibility(View.VISIBLE);
                tvPackageThreeSave.setText(CommonFunction.getLangText(this, goods.get(2).discount));
            } else {
                tvPackageThreeSave.setVisibility(View.GONE);
            }

            tvPackageOneMonth.setText(goods.get(3).month + packageSuffix);
            tvPackageOneDollar.setText(packagePrefix + goods.get(3).price);
            if (!TextUtils.isEmpty(goods.get(3).discount)) {
                tvPackageOneSave.setVisibility(View.VISIBLE);
                tvPackageOneSave.setText(CommonFunction.getLangText(this, goods.get(3).discount));
            } else {
                tvPackageOneSave.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void showDialog() {
        showWaitDialog();
    }

    @Override
    public void hideDialog() {
        hideWaitDialog();
    }

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
            url = CommonFunction.getLangText(mContext, Config.VIP_AGREEMENT_URL);
        } else {
            url = CommonFunction.getLangText(mContext, Config.iAroundPayFAQUrl);
            str = getResString(R.string.common_questions);
        }
        Intent intent = new Intent(mContext, WebViewAvtivity.class);
        intent.putExtra(WebViewAvtivity.WEBVIEW_TITLE, str);
        intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        startActivity(intent);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == mGetMemberList) {
            BaseServerBean Basebean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (Basebean != null && Basebean.isSuccess()) {
                VipBuyMemberListBean data = GsonUtil.getInstance().getServerBean(result,
                        VipBuyMemberListBean.class);
                if (data != null && data.goods.size() > 0) {
                    payChannel = result;
                    Message msg = Message.obtain();
                    msg.what = SHOW_DATA;
                    msg.obj = data;
                    mHandler.sendMessage(msg);

                } else {
                    Message msg = Message.obtain();
                    msg.what = GET_DATA_FAIL;
                    mHandler.sendMessage(msg);
                }
            } else {
                Message msg = Message.obtain();
                msg.what = GET_DATA_FAIL;
                mHandler.sendMessage(msg);
            }
        } else if (notify_flag == flag) {

        } else if (flag == mGetMiguStatus) {
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.has("status") && obj.optLong("status") == 200) {
                    int payStatus = CommonFunction.jsonOptInt(obj, "pay");
                    if (payStatus == 0) {
                        requestMonthlyPay();
                    } else {
                        CommonFunction.toastMsg(this, "您已是咪咕遇见会员，无法重复订购。");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (flag == mGetMemberList) {
            ErrorCode.toastError(mContext, e);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATA:
                    bean = (VipBuyMemberListBean) msg.obj;
                    refreshView();
                    break;

                case GET_DATA_FAIL:
//                    hideProgressDialog();
                    break;

                case INSERT_ORDER_TO_DATABASE: {
                    String content = (String) msg.obj;
                    orderDBID = PayModel.getInstance().insertOrder(
                            UserVipOpenActivity.this, ChannelType.GOOGLEPAY, content);
                }
                break;
                case DELETE_ORDER_FROME_DATABASE: {
                    String result = (String) msg.obj;
                    GoogleNotifyRec bean = GsonUtil.getInstance().getServerBean(result,
                            GoogleNotifyRec.class);
                    if (bean.isSuccess()) {
                        PayModel.getInstance().deleteOrder(UserVipOpenActivity.this, orderDBID);
                    }
                }
                break;
            }
        }
    };

    /**
     * 更新页面
     */
    protected void refreshView() {
        // banner栏是否显示
        if (CommonFunction.isEmptyOrNullStr(bean.icon)) {
            if (ivVipBanner.isShown()) {
                ivVipBanner.setVisibility(View.GONE);
            }
        } else {
            ivVipBanner.setVisibility(View.VISIBLE);
            ivVipBanner.execute(DefaulHeadPic, bean.icon);
        }

        // 大陆与非大陆的数据解析 area=1=大陆 area=2=海外
        if (isChina == 1) {
            handleChinaData();
        } else {
            handleOverseasData();
        }
    }

    /**
     * 大陆地区，处理数据
     */
    private void handleChinaData() {
//        hideProgressDialog( );

        for (Goods i : bean.goods) {
            i.price = "¥" + i.price;
        }
//        mList = bean.goods;
//        GridAdapter.notifyDataSetChanged();
        updataeDataChange();


        String cacheStr = GsonUtil.getInstance().getStringFromJsonObject(bean);
        SharedPreferenceCache.getInstance(mContext).putString("vip_pay_member" + Uid,
                cacheStr);
    }

    /**
     * 非大陆地区，处理数据，需要连接google，查询该商品转换成对应汇率的价格
     */
    private void handleOverseasData() {
        try {
            initGoogleConnect();
        } catch (Exception e) {
            handleQueryFail();
            e.printStackTrace();
        }
    }

    /**
     * 连接google查询商品价格失败
     */
    private void handleQueryFail() {
//        hideProgressDialog( );

        for (Goods i : bean.goods) {
            i.price = "NT$" + i.price;
        }
//        mList = bean.goods;
//        GridAdapter.notifyDataSetChanged( );
        updataeDataChange();

        String cacheStr = GsonUtil.getInstance().getStringFromJsonObject(bean);
        SharedPreferenceCache.getInstance(mContext).putString("vip_pay_member" + Uid,
                cacheStr);
    }

    /**
     * 连接谷歌，查询商品列表
     */
    private void initGoogleConnect() {
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    handleQueryFail();
                    // Toast.makeText( VipBuyMemberActivity.this , "安装IAB失败" , 1
                    // ).show( );
                    return;
                }

                // 查询商品列表(异步)
                skuArray = new ArrayList<String>();
                for (Goods goods : bean.goods) {
                    skuArray.add(goods.goodsid);
                }
                mHelper.queryInventoryAsync(true, skuArray, mQueryFinishedListener);
            }
        });
    }

    // 查询商品列表完成的侦听器
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                handleQueryFail();
                // Toast.makeText( VipBuyMemberActivity.this , "查询商品失败" , 1
                // ).show( );
                return;
            }

            for (int i = 0; i < skuArray.size(); i++) {
                String SKU = skuArray.get(i);
                try {
                    if (inventory.getSkuDetails(SKU) != null) {
                        updateProductData(inventory.getSkuDetails(SKU).getPrice(), SKU,
                                i);
                    } else {
                        CommonFunction.log("", "--->SKU RETURNED NULL" + SKU);
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
                Message msg = new Message();
                msg.what = INSERT_ORDER_TO_DATABASE;
                msg.obj = content;
                mHandler.sendMessage(msg);

                // 上报服务端查询用户消费情况
                notify_flag = PayHttpProtocol.googleNotify(UserVipOpenActivity.this,
                        signeddata, purchase.getSignature(), UserVipOpenActivity.this);

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
        for (Goods goods : bean.goods) {
            if (goods.goodsid == SKU) {
                goods.price = price;
            }
        }

        if (i == skuArray.size() - 1) {
//            hideProgressDialog( );
//            mList = bean.goods;
//            GridAdapter.notifyDataSetChanged( );
            updataeDataChange();

            // 将获取到的数据缓存
            String result = GsonUtil.getInstance().getStringFromJsonObject(bean);
            SharedPreferenceCache.getInstance(mContext).putString("vip_pay_member" + Uid,
                    result);
        }
    }


    public void updataeDataChange() {
        goodsTemp = bean.goods;

//        Collections.sort(bean.goods, new Comparator<Goods>() {
//
//            /*
//             * int compare(Student o1, Student o2) 返回一个基本类型的整型，
//             * 返回负数表示：o1 小于o2，
//             * 返回0 表示：o1和o2相等，
//             * 返回正数表示：o1大于o2。
//             */
//            public int compare(Goods o1, Goods o2) {
//
//                //按照学生的年龄进行升序排列
//                if (o1.month < o2.month) {
//                    return 1;
//                }
//                if (o1.month == o2.month) {
//                    return 0;
//                }
//                return -1;
//            }
//        });
        tvPackageSixMonth.setText(goodsTemp.get(0).month + packageSuffix);
        tvPackageSixDollar.setText(goodsTemp.get(0).price + "");
        tvPackageSixSave.setText(CommonFunction.getLangText(UserVipOpenActivity.this, goodsTemp.get(0).discount));
        tvPackageTwelveMonth.setText(goodsTemp.get(1).month + packageSuffix);
        tvPackageTwelveDollar.setText(goodsTemp.get(1).price + "");
        tvPackageTwelveSave.setText(CommonFunction.getLangText(UserVipOpenActivity.this, goodsTemp.get(1).discount));
        tvPackageThreeMonth.setText(goodsTemp.get(2).month + packageSuffix);
        tvPackageThreeDollar.setText(goodsTemp.get(2).price + "");
        tvPackageThreeSave.setText(CommonFunction.getLangText(UserVipOpenActivity.this, goodsTemp.get(2).discount));
        tvPackageOneMonth.setText(goodsTemp.get(3).month + packageSuffix);
        tvPackageOneDollar.setText(goodsTemp.get(3).price + "");
        tvPackageOneSave.setText(CommonFunction.getLangText(UserVipOpenActivity.this, goodsTemp.get(3).discount));


    }

    private GooglePayNotifyBean composeSignedData(Purchase purchase) {
        if (purchase == null) {
            return null;
        }

        GooglePayNotifyBean bean = new GooglePayNotifyBean();
        bean.nonce = "0";// 服务端没用字段

        GoogleOrder order = new GoogleOrder();
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

    /**
     * 跳转到支付渠道页面
     *
     * @param data
     */
    private void lanchPayChannelActivity(Goods data) {
        Intent intent = new Intent(mContext, PayChannelListActivity.class);
        PayGoodsBean payBean = new PayGoodsBean();
        payBean.goodsid = data.goodsid;
        String name = "";
        name = mContext.getString((R.string.pay_result_vip_goods_name), data.month + "");
        payBean.name = name;
        intent.putExtra("goods", payBean);
        intent.putExtra("channel", payChannel);
//        intent.putExtra( EventBuffer.TAG_NAME , DataTag.VIEW_me_vip_pay );
        startActivityForResult(intent, PAY_REQUESTCODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAY_REQUESTCODE) {
            if (resultCode == Activity.RESULT_OK) {// 支付成功
                String Str = String.format(getResString(R.string.vip_buy_success),
                        currentMonth);
                CommonFunction.toastMsg(mContext, Str);
                finish();
            }
        }
    }

    /**
     * 跳转到vip购买页面
     *
     * @param context
     */
    public static void lanchVipBuyMemberActivity(Context context, String enter) {
        Intent intent = new Intent(context, UserVipOpenActivity.class);
        context.startActivity(intent);
    }

    private void requestMonthlyPay() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("spCode", "783287");
        map.put("channelCode", "43016000");
        map.put("serviceID", "705663246765");
        map.put("webId", "yj10" + getRadom() + Common.getInstance().loginUser.getUid());
        map.put("productDescribe", "咪咕遇见白银会员");
        map.put("monthStatus", 1);
        map.put("redirectURL", "http://www.baidu.com");
        map.put("failRedirectURL", "http://www.google.cn");
        String url = Config.sMonthlyPay + getUrlParamsByMap(map, false);
        Intent i = new Intent(mContext, WebViewAvtivity.class);
        i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        startActivityForResult(i, 201);

    }

    /**
     * 生成随机数7位数的
     *
     * @return
     */
    private String getRadom() {
        String s = "";
        int intCount = 0;
        intCount = (new Random()).nextInt(9999999);
        if (intCount < 1000000)
            intCount += 1000;
        s = intCount + "";
        return s;
    }

    /**
     * 将map 转为 string
     *
     * @param map
     * @return
     */
    public String getUrlParamsByMap(Map<String, Object> map,
                                    boolean isSort) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        List<String> keys = new ArrayList<String>(map.keySet());
        if (isSort) {
            Collections.sort(keys);
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key).toString();
            sb.append(key + "=" + value);
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.lastIndexOf("&"));
        }
        return s;
    }
}
