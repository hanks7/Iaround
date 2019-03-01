
package net.iaround.pay.activity;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.tenpay.android.service.TenpayServiceHelper;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.pay.PayGoodsList;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.PayChannelListActivity;
import net.iaround.ui.comon.SuperActivity;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * 财付通支付，通常情况下采用“共享支付”，获得shareUid保存本地； 若共享登录出现问题时，采用“普通支付”的模式
 *
 * @author Administrator
 */
public class TenpayActivity extends SuperActivity {
    private final String TAG = "TenpayActivity";
    public static String TENPAY_GOODSID = "goodsid";
    private String token_id = "";
    private String bargainor_id = "";
    private final static int MSG_PAY_RESULT = 1001;
    private final static int MSG_LOGIN_RESULT = 1002;
    /**
     * 订单号成功生成
     **/
    private final int MSG_ORDER_SUCCESS = 2001;
    /**
     * 订单号生成失败
     **/
    private final int MSG_ORDER_FAIL = 2002;
    private final int MSG_WAP_ORDER_SUCCESS = 2003;
    private final int TENPAY_BACK_SUCCESS = 2004;

    private long plugin_flag = 0;
    private long wap_flag = 0;

    private Dialog dialog;
    private TenpayServiceHelper tenpayHelper;

    // webview
    private WebView webview;

    private String payurl = "";
    private String backurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String goodsid = getIntent().getStringExtra(TENPAY_GOODSID);

        tenpayHelper = new TenpayServiceHelper(mActivity);
        tenpayHelper.setLogEnabled(Config.DEBUG);

        // 判断并安装财付通安全支付服务应用,当没有安装该插件时，直接调用webview方式
        if (!tenpayHelper.isTenpayServiceInstalled()) {
            webview = new WebView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            addContentView(webview, params);
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
                    CommonFunction.log("TenpayActivity1", "url:" + url);
                    CommonFunction.log("TenpayActivity2", "backurl:" + backurl);
                    if (!CommonFunction.isEmptyOrNullStr(backurl) && url.contains(backurl)) {
                        Message msg = new Message();
                        msg.what = TENPAY_BACK_SUCCESS;
                        msg.obj = url;
                        mHandler.sendMessage(msg);
                        return;
                    }

                    try {
                        if (dialog != null)
                            dialog.show();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    try {
                        if (dialog != null)
                            dialog.dismiss();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    super.onPageFinished(view, url);
                }
            });
            getOrderID(2, goodsid);
        } else {
            getOrderID(1, goodsid);
        }
    }

    // 发起支付
    private void sendpay(String shareUid) {
        if (token_id == null || token_id.length() < 32) {
            return;
        }
        // 构造支付参数
        HashMap<String, String> payInfo = new HashMap<String, String>();
        payInfo.put("token_id", token_id);
        payInfo.put("bargainor_id", bargainor_id);
        tenpayHelper.pay(payInfo, mHandler, MSG_PAY_RESULT);
    }

    /**
     * 获取订单号
     *
     * @param type 1插件模式，2webview模式
     */
    private void getOrderID(int type, String goodsid) {
//		if ( type == 1 )
//		{
//			dialog = DialogUtil.showProgressDialog( mActivity , "" ,
//					mActivity.getResString( R.string.please_wait ) , null );
//			plugin_flag = PayHttpProtocol.tenpayOrder( mActivity , goodsid , this );
//		}
//		else
//		{
//		}
        dialog = DialogUtil.showProgressDialog(mActivity, "",
                mActivity.getResString(R.string.please_wait), null);
        wap_flag = PayHttpProtocol.tenpayWapOrder(mActivity, goodsid, this);
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (plugin_flag == flag || wap_flag == flag) {
            mHandler.sendEmptyMessage(MSG_ORDER_FAIL);
        }
        super.onGeneralError(e, flag);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
//		if ( plugin_flag == flag )
//		{
//			Message msg = new Message( );
//			msg.what = MSG_ORDER_SUCCESS;
//			msg.obj = result;
//			mHandler.sendMessage( msg );
//		}
//		else
        if (wap_flag == flag) {
            Message msg = new Message();
            msg.what = MSG_WAP_ORDER_SUCCESS;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
        super.onGeneralSuccess(result, flag);
    }

    // 接收支付返回值的Handler
    protected Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (dialog != null)
                dialog.dismiss();
            switch (msg.what) {
                case MSG_PAY_RESULT:
                    String strRet = (String) msg.obj; // 支付返回值
                    CommonFunction.log(TAG, "pay_result:" + strRet);
                    String statusCode = null;
                    String info = null;
                    String result = null;
                    JSONObject jo;
                    try {
                        jo = new JSONObject(strRet);
                        if (jo != null) {
                            statusCode = jo.getString("statusCode");
                            info = jo.getString("info");
                            result = java.net.URLDecoder.decode(jo.getString("result"));
                            CommonFunction.log(TAG, "pay_result:" + result);
                            if ("0".equals(statusCode)
                                    && result.contains("&pay_result=0&")) {
                                finish(PayGoodsList.RESULT_OK);
                            } else {
                                DialogUtil.showOKDialog(mActivity,
                                        mActivity.getString(R.string.prompt), info,
                                        new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                finish(PayGoodsList.RESULT_FAILURE);
                                            }

                                        });
                            }
                            return;
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    finish(PayGoodsList.RESULT_FAILURE);
                    break;
                case MSG_ORDER_SUCCESS: // 订单成功
                    if (dialog != null)
                        dialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(String.valueOf(msg.obj));
                        if (json.optInt("status") == 200) {
                            bargainor_id = CommonFunction.jsonOptString(json, "bargainorid");
                            token_id = CommonFunction.jsonOptString(json, "tokenid");
                            sendpay("");
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish(PayGoodsList.RESULT_FAILURE);
                    break;
                case MSG_ORDER_FAIL: // 订单失败
                    if (dialog != null)
                        dialog.dismiss();
                    finish(PayGoodsList.RESULT_FAILURE);
                    break;
                case MSG_WAP_ORDER_SUCCESS: // Wap支付回调
                    if (dialog != null)
                        dialog.dismiss();
                    try {


                        JSONObject wapJson = new JSONObject(String.valueOf(msg.obj));
                        if (wapJson != null && wapJson.optInt("status") == 200) {
                            PayChannelListActivity.mOrdernumber = CommonFunction
                                    .jsonOptString(wapJson, "orderid");
                            payurl = CommonFunction.jsonOptString(wapJson, "payurl");
                            backurl = CommonFunction.jsonOptString(wapJson, "backurl");
                            if (webview != null && !CommonFunction.isEmptyOrNullStr(payurl)) {
                                webview.loadUrl(payurl);
                            }
                        } else {
                            ErrorCode.showError(mActivity, String.valueOf(msg.obj));
                            finish(PayGoodsList.RESULT_NO_DES);
                        }
                    } catch (Exception e) {
                        finish(PayGoodsList.RESULT_FAILURE);
                        e.printStackTrace();
                    }
                    break;
                case TENPAY_BACK_SUCCESS: // 财付通回调
                    finish(PayGoodsList.RESULT_OK);
                    break;
            }
        }
    };

    public void finish(int resutcode) {
        setResult(resutcode);
        super.finish();
    }

    @Override
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
        return super.onKeyDown(keyCode, event);
    }
}
