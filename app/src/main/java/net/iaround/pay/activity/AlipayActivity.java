
package net.iaround.pay.activity;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.pay.BaseHelper;
import net.iaround.pay.MobileSecurePayer;
import net.iaround.pay.PayGoodsList;
import net.iaround.pay.bean.AlixId;
import net.iaround.pay.bean.PayResult;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.PayChannelListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class AlipayActivity extends BaseFragmentActivity implements HttpCallBack {
    public static String ALIPAY_GOODSID = "alipay_goodsid";
    public static String ALIPAY_MONEY = "alipay_money";
    public static String ALIPAY_CODE = "alipay_code";

    private ProgressDialog mProgress = null;
    private Dialog dialog;
    private String goodsid = "";
    private String order = "";
    private String code;

    // 创建订单响应码
    private long PLUGIN_FLAG = 0;
    private long WEB_FLAG = 0;
    private long WALLET_FLAG = 0;


    private final int ORDER_RESULT_SUCCESS = 1005;
    private final int ORDER_RESULT_ERROR = 1006;
    private final int WAP_ORDER_RESULT_SUCCESS = 1007;

    private final int ALIPAY_BACK_SUCCESS = 1008;

    private final int SDK_CHECK_FLAG = 1009;
    private final int WALLET_ORDER_RESULT_SUCCESS = 1010;

    private final int WALLET_RESULT_CANCLE = 1012;

    private final int SDK_PAY_FLAG = 1011;

    public String alipay_public_key = "";

    // webview
    private WebView webview;
    private String backurl = "";
    private String payurl = "";
    private String payInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        goodsid = intent.getStringExtra(ALIPAY_GOODSID);
        code = intent.getStringExtra(ALIPAY_CODE);

        boolean isInstaledAliWallet = CommonFunction.isClientInstalled(this,
                "com.eg.android.AlipayGphone");
        if (isInstaledAliWallet && TextUtils.isEmpty(code)) {
            //客户端支付
            check();
        } else {
            //阿里支付Web
            payByPlugOrWeb();
        }

    }

    /*
     * 用支付宝钱包支付
     */



    /*
     * 用插件进行支付或用网页支付
     */

    private void payByPlugOrWeb() {
        // MobileSecurePayHelper spHelper = new MobileSecurePayHelper( mActivity
        // );
        // // 当前没有安装插件时，直接调用web方式请求支付
        // if ( !spHelper.isMobile_spExist( ) || code.equals( "1" ) ||
        // code.equals( "2" ) )
        {
//			setTheme(R.style.theme);//jiqiang 主题先注释掉
            webview = new WebView(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addContentView(webview, lp);
            webview.requestFocus();
            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setBuiltInZoomControls(true);
            // webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    CommonFunction.log("AlipayActivity2", url + "," + backurl);
                    if (!CommonFunction.isEmptyOrNullStr(backurl) && url.contains(backurl)) {
                        Message msg = new Message();
                        msg.what = ALIPAY_BACK_SUCCESS;
                        msg.obj = url;
                        mHandler.sendMessage(msg);
                        return;
                    } else if (url.contains("http://127.0.0.1/") || url.contains("http://192.168.100.81")) {

                        Message msg = new Message();
                        msg.what = WALLET_RESULT_CANCLE;
                        msg.obj = url;
                        mHandler.sendMessage(msg);
                        return;

                    } else if (url.contains("intent://platformapi/startapp?")) {
                        Message msg = new Message();
                        msg.what = ORDER_RESULT_ERROR;
                        msg.obj = url;
                        mHandler.sendMessage(msg);
                        return;
                    }
                    showDialog();
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (dialog != null)
                        dialog.dismiss();
                    super.onPageFinished(view, url);
                }
            });
            createAlipayOrder(2);
        }
        // else
        // {
        // createAlipayOrder( 1 );
        // }
    }


    /**
     * 支付宝订单生请求
     * <p>
     * 1请求插件方面的订单，2请求web的订单
     */

    private void creatAlipayWalletOrder() {
        dialog = DialogUtil.showProgressDialog(mActivity, "", mActivity.getResources()
                .getString(R.string.please_wait), null);
        WALLET_FLAG = PayHttpProtocol.alipaywalletTrade(mActivity, goodsid, this);
    }

    private void createAlipayOrder(int type) {
        if (type == 1) {
            dialog = DialogUtil.showProgressDialog(mActivity, "", mActivity.getResources()
                    .getString(R.string.please_wait), null);
            PLUGIN_FLAG = PayHttpProtocol.alipayTrade(mActivity, goodsid, this);
        } else {
            dialog = DialogUtil.showProgressDialog(mActivity, "", mActivity.getResources()
                    .getString(R.string.please_wait), null);
            WEB_FLAG = PayHttpProtocol.alipayWapOrder(mActivity, goodsid, code, this);
        }

    }

    // 支付调用
    private void pay(String order) {
        try {
            // 发送数据组装
            MobileSecurePayer msp = new MobileSecurePayer();
            boolean bRet = msp.pay(order, mHandler, AlixId.RQF_PAY, this);
            if (bRet) {
                closeProgress();
                mProgress = BaseHelper.showProgress(this, null,
                        this.getString(R.string.pay_now), false, true);
            }
        } catch (Exception ex) {
            Toast.makeText(AlipayActivity.this, R.string.pay_alipay_remote_call_failed,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            String strRet = String.valueOf(msg.obj);
            switch (msg.what) {
                case WALLET_RESULT_CANCLE: {
                    if (dialog != null)
                        dialog.dismiss();
                    finish(RESULT_CANCELED);
                }

                break;
                case AlixId.RQF_PAY: {
                    closeProgress();
                    try {
                        String resultStatus = "resultStatus={";
                        int statusStart = strRet.indexOf(resultStatus);
                        statusStart += resultStatus.length();
                        int statusEnd = strRet.indexOf("};memo=");
                        resultStatus = strRet.substring(statusStart, statusEnd);
                        if (resultStatus.equals("9000")) {
                            // 友盟统计
//							new UmengUtils( mActivity ).buyCoinsEvent( ( int ) money );//jiqiang
                            finish(PayGoodsList.RESULT_OK);
                        } else {
                            finish(PayGoodsList.RESULT_FAILURE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish(PayGoodsList.RESULT_FAILURE);
                    }
                }
                break;
                case ORDER_RESULT_SUCCESS: // 成功从服务端请求订单号
                    if (dialog != null)
                        dialog.dismiss();
                    JSONObject json;
                    try {
                        json = new JSONObject(String.valueOf(msg.obj));
                        if (json != null && json.optInt("status") == 200) {
                            alipay_public_key = CommonFunction.jsonOptString(json,
                                    "publickey");
                            order = CommonFunction.jsonOptString(json, "orderinfo");

                            PayChannelListActivity.mOrdernumber = order;
                            pay(order);
                        } else {
                            ErrorCode.showError(mActivity, String.valueOf(msg.obj));
                        }
                    } catch (JSONException e) {
                        CommonFunction.log(PayGoodsList.class.getName(), e.getMessage());
                    }
                    break;
                case ORDER_RESULT_ERROR: // 请求订单失败
                    if (dialog != null)
                        dialog.dismiss();
                    finish(PayGoodsList.RESULT_FAILURE);
                    break;
                case WALLET_ORDER_RESULT_SUCCESS: {
                    if (dialog != null)
                        dialog.dismiss();

                    JSONObject infoJson;

                    try {
                        infoJson = new JSONObject(String.valueOf(msg.obj));
                        if (infoJson != null && infoJson.optInt("status") == 200) {
                            payInfo = CommonFunction.jsonOptString(infoJson, "orderinfo");
                            PayChannelListActivity.mOrdernumber = CommonFunction
                                    .jsonOptString(infoJson, "orderid");
                            Runnable payRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    // 构造PayTask 对象
                                    PayTask alipay = new PayTask(AlipayActivity.this);
                                    // 调用支付接口，获取支付结果
                                    String result = alipay.pay(payInfo);

                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };

                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        } else {
                            Toast.makeText(AlipayActivity.this, "获取订单结果为：" + msg.obj,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
                break;


                case WAP_ORDER_RESULT_SUCCESS: // Wap订单产生结果
                    if (dialog != null)
                        dialog.dismiss();
                    JSONObject wapJson;
                    try {
                        wapJson = new JSONObject(String.valueOf(msg.obj));
                        if (wapJson != null && wapJson.optInt("status") == 200) {


                            PayChannelListActivity.mOrdernumber = CommonFunction
                                    .jsonOptString(wapJson, "orderid");
                            payurl = CommonFunction.jsonOptString(wapJson, "payurl");
                            backurl = CommonFunction.jsonOptString(wapJson, "backurl");

                            if (webview != null && !CommonFunction.isEmptyOrNullStr(payurl)) {
                                // GATEWAY_DEBIT_CARD储蓄卡，GATEWAY_CREDIT_CARD信用卡
                                webview.loadUrl(payurl);
                            }
                        } else {
                            ErrorCode.showError(mActivity, String.valueOf(msg.obj));
                            finish(PayGoodsList.RESULT_NO_DES);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case ALIPAY_BACK_SUCCESS: // alipay成功回调
                    if (dialog != null)
                        dialog.dismiss();
                    try {
                        String callback_result = String.valueOf(msg.obj);
                        Map<String, String> map = CommonFunction.paramURL(callback_result);
                        if (map.get("result").equals("success")) {
                            finish(PayGoodsList.RESULT_OK);
                        } else {
                            finish(PayGoodsList.RESULT_FAILURE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case SDK_CHECK_FLAG: {
                    boolean isExit = (Boolean) msg.obj;
                    if (isExit) {
//						 Toast.makeText( AlipayActivity.this , "检查结果为：" +
//						 msg.obj ,
//						 Toast.LENGTH_SHORT ).show( );
                        creatAlipayWalletOrder();
                    } else {

                        payByPlugOrWeb();
                    }
                    break;
                }

                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if ("9000".equals(resultStatus)) {

                        finish(PayGoodsList.RESULT_OK);

                        // Toast.makeText( AlipayActivity.this , "支付成功" ,
                        // Toast.LENGTH_SHORT )
                        // .show( );
                    } else if ("6001".equals(resultStatus)) {
                        //取消支付
                        finish(RESULT_CANCELED);
                    } else {

                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if ("8000".equals(resultStatus)) {
                            // Toast.makeText( AlipayActivity.this , "支付结果确认中" ,
                            // Toast.LENGTH_SHORT ).show( );

                        } else if ("4000".equals(resultStatus)) // 系统繁忙，请稍后再试
                        {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            // Toast.makeText( AlipayActivity.this , "支付失败" ,
                            // Toast.LENGTH_SHORT )
                            // .show( );
                            payByPlugOrWeb();

                        } else {
                            // resultStatus={4000};memo={系统繁忙，请稍后再试};result={}
                            finish(PayGoodsList.RESULT_NO_DES);
                        }
                    }
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    private void showDialog() {
        if (!isFinishing()) {// 不加判断会抛BadTokenException
            if (dialog != null)
                dialog.show();
        }
    }

    private void closeProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    // 按下返回
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview != null && webview.canGoBack()) {
                webview.goBack();
                return true;
            } else {
                this.finish(Activity.RESULT_CANCELED);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onGeneralError(int e, long flag) {
//		super.onGeneralError( e , flag );
        if (PLUGIN_FLAG == flag || WEB_FLAG == flag || WALLET_FLAG == flag) {
            Message msg = new Message();
            msg.what = ORDER_RESULT_ERROR;
            msg.obj = e;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
//		super.onGeneralSuccess( result , flag );
        if (PLUGIN_FLAG == flag) {
            Message msg = new Message();
            msg.what = ORDER_RESULT_SUCCESS;
            msg.obj = result;
            mHandler.sendMessage(msg);
        } else if (WEB_FLAG == flag) {
            Message msg = new Message();
            msg.what = WAP_ORDER_RESULT_SUCCESS;
            msg.obj = result;
            mHandler.sendMessage(msg);
        } else if (WALLET_FLAG == flag) {
            Message msg = new Message();
            msg.what = WALLET_ORDER_RESULT_SUCCESS;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeProgress();
    }

    public void finish(int resutcode) {
        closeProgress();
        Intent intent = new Intent();
        setResult(resutcode, intent);
        super.finish();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check() {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(AlipayActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

}
