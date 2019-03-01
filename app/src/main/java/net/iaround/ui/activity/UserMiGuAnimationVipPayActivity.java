package net.iaround.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.migu.sdk.api.MiguSdk;
import com.migu.sdk.api.OrderIdBean;
import com.migu.sdk.api.OrderIdCallBack;
import com.migu.sdk.api.PayBean;
import com.migu.sdk.api.PayCallBack;
import com.migu.sdk.api.PayResult;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.MiguAESUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.ui.comon.NetImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;


/**
 * 咪咕开通会员的类
 */
public class UserMiGuAnimationVipPayActivity extends TitleActivity implements View.OnClickListener, HttpCallBack {


    public static final String KEY_HEADER_ICON = "key_header_icon";
    public static final String CHANNEL_ID = "310000022";//需使用自有的CHANNELID
    public static final String CP_ID = "LP0023";//需使用申请包月对应的CPID
    public static final String APP_ID_10 = "152835139497717636861581100762";//10元包月对应的产品code
    public static final String APP_ID_15 = "152835139499417636861597872765";//15元包月对应的产品code
    public static final String APP_ID_20 = "152835139497818245064481710588";//20元包月对应的产品code


    private TextView tvTitle;
    private ImageView ivLeft;

    private NetImageView vipIcon;
    private TextView tvNewPay1;
    private TextView tvNewPay2;
    private TextView tvNewPay3;
    private TextView tvPay1Title;
    private TextView tvPay2Title;
    private TextView tvPay3Title;
    private TextView tvGetPackageGift;
    private TextView tvPay1Hint;
    private TextView tvPay2Hint;
    private TextView tvPay3Hint;
    private LinearLayout llMiguDownload;
    private LinearLayout llDownloadMiguVideoApk;
    private LinearLayout llDownloadMiguQuanQuanApk;


    private long mGetMiguStatusFlag;//得到咪咕动漫订购状态网络请求标识
    private long mSetMiguStatusFlag;//设置咪咕动漫订购状态网络请求标识
    // 咪咕当前订购状态
    private int mOrderStatus = 0;
    //咪咕动漫H5链接
    private String mH5Url;

    private String headerIcon;

//    private boolean isFirstGetOrderId = true;//是否成功得到orderId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // miguSDK初始化
        showWaitDialog();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //初始化比较耗时，又不能放到子线程执行
                MiguSdk.initializeApp(UserMiGuAnimationVipPayActivity.this, null);
                destroyWaitDialog();
            }
        }, 1000);

        setContentView(R.layout.activity_user_vip_new_pay);
        initData();
        initView();
        initActionBar();

    }

    /**
     * 初始化数据
     */
    private void initData() {
        headerIcon = getIntent().getStringExtra(KEY_HEADER_ICON);
    }

    private void initActionBar() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivLeft = (ImageView) findViewById(R.id.iv_left);

        tvTitle.setText(getResString(R.string.vip_recharge));

        findViewById(R.id.fl_left).setOnClickListener(this);
        ivLeft.setOnClickListener(this);
    }

    private void initView() {
        tvNewPay1 = (TextView) findViewById(R.id.tv_new_pay1);
        tvNewPay2 = (TextView) findViewById(R.id.tv_new_pay2);
        tvNewPay3 = (TextView) findViewById(R.id.tv_new_pay3);
        tvPay1Title = (TextView) findViewById(R.id.tv_pay1_title);
        tvPay2Title = (TextView) findViewById(R.id.tv_pay2_title);
        tvPay3Title = (TextView) findViewById(R.id.tv_pay3_title);
        tvPay1Hint = (TextView) findViewById(R.id.tv_pay1_hint);
        tvPay2Hint = (TextView) findViewById(R.id.tv_pay2_hint);
        tvPay3Hint = (TextView) findViewById(R.id.tv_pay3_hint);
        tvGetPackageGift = (TextView) findViewById(R.id.tv_get_packageGift);
        vipIcon = (NetImageView) findViewById(R.id.iv_vip_banner);
        llMiguDownload = (LinearLayout) findViewById(R.id.ll_migu_download);
        llDownloadMiguVideoApk = (LinearLayout) findViewById(R.id.ll_download_migu_video_apk);
        llDownloadMiguQuanQuanApk = (LinearLayout) findViewById(R.id.ll_download_migu_quanquan_apk);


        llMiguDownload.setVisibility(View.VISIBLE);
        tvPay1Title.setText(getString(R.string.user_vip_animation_pay1_title));
        tvPay2Title.setText(getString(R.string.user_vip_animation_pay2_title));
        tvPay3Title.setText(getString(R.string.user_vip_animation_pay3_title));
        tvPay1Hint.setText(getString(R.string.user_vip_animation_pay1_hint));
        tvPay2Hint.setText(getString(R.string.user_vip_animation_pay2_hint));
        tvPay3Hint.setText(getString(R.string.user_vip_animation_pay3_hint));
        tvGetPackageGift.setText(getString(R.string.user_vip_animation_activity));

        tvGetPackageGift.setOnClickListener(this);
        findViewById(R.id.rl_get_packageGift).setOnClickListener(this);
        tvNewPay1.setOnClickListener(this);
        tvNewPay2.setOnClickListener(this);
        tvNewPay3.setOnClickListener(this);
        llDownloadMiguVideoApk.setOnClickListener(this);
        llDownloadMiguQuanQuanApk.setOnClickListener(this);

        if (headerIcon != null) {
            if (!TextUtils.isEmpty(headerIcon)) {
                vipIcon.execute(R.drawable.group_info_bg, headerIcon);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_new_pay1:
                if (mOrderStatus > 0) {
                    if (mOrderStatus == 10) {//退订10元
                        payOrUnsubscribeVip("1300200910", "1000", APP_ID_10, "2", "1");
                    }
                    return;
                }
                //订阅10元
                payOrUnsubscribeVip("1300200910", "1000", APP_ID_10, "1", "0");

                break;
            case R.id.tv_new_pay2:
                if (mOrderStatus > 0) {
                    if (mOrderStatus == 15) {//退订15元
                        payOrUnsubscribeVip("1300200911", "1500", APP_ID_15, "2", "1");

                    }
                    return;
                }
                //订阅15元
                payOrUnsubscribeVip("1300200911", "1500", APP_ID_15, "1", "0");

                break;
            case R.id.tv_new_pay3:
                if (mOrderStatus > 0) {
                    if (mOrderStatus == 20) {//退订20元
                        payOrUnsubscribeVip("1300200912", "2000", APP_ID_20, "2", "1");

                    }
                    return;
                }
                //订阅20元
                payOrUnsubscribeVip("1300200912", "2000", APP_ID_20, "1", "0");

                break;
            case R.id.ll_download_migu_video_apk:
                WebViewAvtivity.launchVerifyCode(UserMiGuAnimationVipPayActivity.this, "http://dl.iaround.com/MiguVideo.apk");
                break;
            case R.id.ll_download_migu_quanquan_apk:
                WebViewAvtivity.launchVerifyCode(UserMiGuAnimationVipPayActivity.this, "http://dl.iaround.com/MiguQQ.apk");
                break;
            case R.id.tv_get_packageGift:
            case R.id.rl_get_packageGift:
                WebViewAvtivity.launchVerifyCode(UserMiGuAnimationVipPayActivity.this, getEncodeUrl());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPayMiGu();
    }

    @Override
    protected void onDestroy() {
        MiguSdk.exitApp(this);
        super.onDestroy();
    }

    /**
     * 得到咪咕订购状态
     */
    private void requestPayMiGu() {
        mGetMiguStatusFlag = GoldHttpProtocol.getMiguManLianStatus(this, this);
    }

    /**
     * 设置咪咕订购状态
     */
    private void setPayMiGuStatus(int orderStatus) {
        showWaitDialog();
        mSetMiguStatusFlag = GoldHttpProtocol.setMiguManLianStatus(this, orderStatus, this);
    }


    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == mGetMiguStatusFlag) {
            //会员状态是否开通（遇见服务器接口返回）
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.has("status") && obj.optLong("status") == 200) {
                    mOrderStatus = CommonFunction.jsonOptInt(obj, "manlianStatus");
                    mH5Url = CommonFunction.jsonOptString(obj, "url");


                    initViewByOrderStatus();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (flag == mSetMiguStatusFlag) {
            destroyWaitDialog();
            initViewByOrderStatus();
            CommonFunction.toastMsg(BaseApplication.getInstance(), "操作成功");
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (flag == mSetMiguStatusFlag) {
            destroyWaitDialog();
        }
        ErrorCode.toastError(this, e);
    }


    private void initViewByOrderStatus() {
        if (mOrderStatus == 10) {
            tvNewPay1.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
            tvNewPay1.setText("退订");
            tvNewPay2.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
            tvNewPay2.setText("开通会员");
            tvNewPay3.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
            tvNewPay3.setText("开通会员");
        } else if (mOrderStatus == 15) {
            tvNewPay1.setText("开通会员");
            tvNewPay1.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
            tvNewPay2.setText("退订");
            tvNewPay2.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
            tvNewPay3.setText("开通会员");
            tvNewPay3.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
        } else if (mOrderStatus == 20) {
            tvNewPay1.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
            tvNewPay1.setText("开通会员");
            tvNewPay2.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
            tvNewPay2.setText("开通会员");
            tvNewPay3.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
            tvNewPay3.setText("退订");
        } else {
            tvNewPay1.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
            tvNewPay1.setText("开通会员");
            tvNewPay2.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
            tvNewPay2.setText("开通会员");
            tvNewPay3.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
            tvNewPay3.setText("开通会员");
        }
    }

    /**
     * 得到加密过后的字符H5  url
     *
     * @return
     */
    private String getEncodeUrl() {
        String phoneNum = PhoneInfoUtil.getInstance(UserMiGuAnimationVipPayActivity.this).getPhoneNum();
        String imei = PhoneInfoUtil.getInstance(UserMiGuAnimationVipPayActivity.this).getIMEI();
        if (TextUtils.isEmpty(imei)) {
            imei = PhoneInfoUtil.getInstance(UserMiGuAnimationVipPayActivity.this).getDeviceId();
        }
        String urlEncodeString = "";
        if (!TextUtils.isEmpty(phoneNum)) {
            if (phoneNum.startsWith("+86")) {
                phoneNum = phoneNum.substring(3);
            }

        } else {
            //当手机号获取不到时，需要将phoneNum置为null，不然请求咪咕H5界面报参数错误
            phoneNum = null;
        }
        String productCode;
        switch (mOrderStatus) {
            case 10:
                productCode = APP_ID_10;
                break;
            case 15:
                productCode = APP_ID_15;
                break;
            case 20:
                productCode = APP_ID_20;
                break;
            default:
                productCode = APP_ID_20;

        }
        String source = "mobile=" + phoneNum + "|imei=" + imei + "|is_auth_controller=true|is_union=true|watch_num=20|cpId=" + CP_ID + "|productCode=" + productCode;
        String key = "migudm@XM.com";

        try {

            String encodedString = MiguAESUtil.aesEncrypt(source, key);
            urlEncodeString = URLEncoder.encode(encodedString, "UTF-8");
            urlEncodeString = URLEncoder.encode(urlEncodeString, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mH5Url + urlEncodeString;
    }

    /**
     * 订阅或退订
     *
     * @param paycode   计费点Id
     * @param fee       价格 ，单位为分
     * @param appId     产品id
     * @param orderType 1-订购，2-退订
     * @param operType  0-订购，1-退订
     */
    private void payOrUnsubscribeVip(final String paycode, final String fee, final String appId, final String orderType, final String operType) {
        showWaitDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                if (isFirstGetOrderId) {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                OrderIdBean bean = new OrderIdBean();
                bean.setOrderType(orderType);
                bean.setAppId(appId);  //需使用分配的APPID
                bean.setChannelId(CHANNEL_ID);                   //需使用自有的CHANNELID
                bean.setPaycode(paycode);
//                bean.setPaycode("");
                bean.setPayType("1002");
                bean.setOperType(operType);
                bean.setCpId(CP_ID);                           //需使用申请包月对应的CPID
                MiguSdk.getOrderId(UserMiGuAnimationVipPayActivity.this, bean, "", "",
                        new OrderIdCallBack.IOrderIdCallback() {

                            @Override
                            public void onResult(int resultCode, String statusCode, String message, String bossid) {
//                                String result = "";
                                switch (resultCode) {
                                    case PayResult.SUCCESS:
//                                        result = "获取orderid成功！resultCode："+resultCode+"statusCode："+statusCode+"message："+message;
                                        Message msg = new Message();
                                        Bundle data = new Bundle();
                                        data.putString("orderId", message);
                                        data.putString("bossId", bossid);
                                        data.putString("fee", fee);
                                        data.putString("operType", operType);

                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                        break;
                                    case PayResult.FAILED:
                                        CommonFunction.log("getOrderId", "获取orderid失败！resultCode：" + resultCode + " statusCode：" + statusCode + "  message：" + message);
                                        if (statusCode != null && statusCode.equals("D1")) {
                                            if (!TextUtils.isEmpty(PhoneInfoUtil.getInstance(UserMiGuAnimationVipPayActivity.this).getPhoneNum())) {
                                                CommonFunction.toastMsg(BaseApplication.getInstance(), "请重试");
                                            } else {
                                                CommonFunction.toastMsg(BaseApplication.getInstance(), message);
                                            }
                                        } else {
                                            CommonFunction.toastMsg(BaseApplication.getInstance(), " statusCode：" + statusCode + "   message：" + message);
                                        }
//                                        result = "获取orderid失败！resultCode："+resultCode+"statusCode："+statusCode+"message："+message;
                                        break;
                                }
                                destroyWaitDialog();
                            }
                        });
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("orderId");
            String bossid = data.getString("bossId");
            final String fee = data.getString("fee");
            final String operType = data.getString("operType");
            //Toast.makeText(UserMiGuAnimationVipPayActivity.this, val, Toast.LENGTH_LONG).show();
            PayBean paybean = new PayBean();
            paybean.setProductId(bossid);
            paybean.setCpId(CP_ID);                        //需使用申请包月对应的CPID
            paybean.setChannelId(CHANNEL_ID);                //需使用自有的CHANNELID
            paybean.setFee(fee);                             //需使用所申请包月对应的价格，单位为分
            paybean.setOrderId(val);
            paybean.setSpCode("698043");
            paybean.setOperType(operType);
            paybean.setSyn(false);
            paybean.setReservedParam2("");
            paybean.setReservedParam3("");
            paybean.setReservedParam4("");
            paybean.setReservedParam5("");
            paybean.setPlatfromCode("698043NLPT");
            MiguSdk.pay(UserMiGuAnimationVipPayActivity.this, paybean, "", "", new PayCallBack.IPayCallback() {
                @Override
                public void onResult(int resultCode, String statusCode, String message) {
                    String result = "";
                    switch (resultCode) {
                        case PayResult.SUCCESS:
                            //result = "购买道具成功！resultCode："+resultCode+"statusCode："+statusCode+"message："+message;
                            if ("0".equals(operType)) {
                                if ("1000".equals(fee)) {
                                    mOrderStatus = 10;
                                } else if ("1500".equals(fee)) {
                                    mOrderStatus = 15;

                                } else if ("2000".equals(fee)) {
                                    mOrderStatus = 20;
                                }
                            } else if ("1".equals(operType)) {
                                mOrderStatus = 0;
                            }
                            setPayMiGuStatus(mOrderStatus);
                            break;
                        case PayResult.FAILED:
                            //result = "购买道具失败！resultCode："+resultCode+"statusCode："+statusCode+"message："+message;
                            result = "操作失败  " + message;
                            break;
                        case PayResult.CANCELLED:
                            //result = "购买道具取消！resultCode："+resultCode+"statusCode："+statusCode+"message："+message;
                        default:
                            //result = "购买道具取消！resultCode："+resultCode+"statusCode："+statusCode+"message："+message;
                            result = "操作取消";
                            break;
                    }
//                    Toast.makeText(UserMiGuAnimationVipPayActivity.this, result, Toast.LENGTH_SHORT).show();
                    CommonFunction.toastMsg(BaseApplication.getInstance(), result);
                }
            });
        }
    };

}
