package net.iaround.manager;

import android.app.Activity;
import android.content.Intent;

import net.iaround.conf.Config;
import net.iaround.pay.ChannelType;
import net.iaround.pay.activity.AlipayActivity;
import net.iaround.pay.google.GooglePayActivity;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.wxapi.WXPayEntryActivity;

/**
 * 支付管理类
 */
public class IaroundPayManager {

    private final String TAG = IaroundPayManager.class.getSimpleName();

    private Activity mContext;//上下文对象

    private int mChannelID;

    private String mGoodsid;

    public static final int RESQUESTCODE = 101;

    public static final int RESULT_FAILURE = 3;

    public static final int RESULT_NO_DES = 4; // 无需描述

    private static IaroundPayManager mIaroundPayManager;

    private IaroundPayManager() {
    }

    public static IaroundPayManager getIaroundPayManager() {
        if (null == mIaroundPayManager) {
            synchronized (IaroundPayManager.class) {
                if (null == mIaroundPayManager) {
                    mIaroundPayManager = new IaroundPayManager();
                }
            }
        }
        return mIaroundPayManager;
    }

    /**
     * 设置上下文
     *
     * @param activity
     * @return
     */
    public IaroundPayManager setContext(Activity activity) {
        this.mContext = activity;
        return this;
    }

    /**
     * 设置支付渠道
     *
     * @param channelID
     * @return
     */
    public IaroundPayManager setChannelID(int channelID) {
        this.mChannelID = channelID;
        return this;
    }

    /**
     * 设置商品ID
     *
     * @param goodsid
     * @return
     */
    public IaroundPayManager setGoodsid(String goodsid) {
        this.mGoodsid = goodsid;
        return this;
    }

    /**
     * 开始支付
     * 跳转到各个渠道支付
     */
    public void startPay() {
        Intent intent = null;
        switch (mChannelID) {
            case ChannelType.WECHATPAY:
                intent = new Intent(mContext, WXPayEntryActivity.class);
                intent.putExtra(WXPayEntryActivity.WECHAT_GOODSID, mGoodsid);
                break;
            case ChannelType.ALIPAY:// 支付宝
                intent = new Intent(mContext, AlipayActivity.class);
                intent.putExtra(AlipayActivity.ALIPAY_GOODSID, mGoodsid);
                intent.putExtra(AlipayActivity.ALIPAY_CODE, "");
                break;
            case ChannelType.GOOGLEPAY:
                intent = new Intent(mContext, GooglePayActivity.class);
                intent.putExtra(GooglePayActivity.GOOGLEPAY_GOODSID, mGoodsid);
                break;
        }
        if (intent != null) {
            mContext.startActivityForResult(intent, RESQUESTCODE);
        }
    }
}
