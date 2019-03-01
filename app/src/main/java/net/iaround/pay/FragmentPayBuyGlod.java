package net.iaround.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.pay.bean.PayChannelBean;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.PayModel;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 购买金币
 *
 * @author huyunfeng my519820363@gmail.com
 */
public class FragmentPayBuyGlod extends SuperActivity implements PayViewUpdate,
        HttpCallBack, OnClickListener {

    private long mGoldGetFlag = 0;
    private long getExchangeListFlag = 0;
    private long diamondForGoldFlag = 0;
    private TextView balance_count;
    private PayChannelAdapter mlistAdapter;
    private PullToRefreshListView listView;
    /**
     * 是否从商店钻石直购路径跳转至此
     */
    public Boolean isExchangeGoldPrice = false;
    private final static String DIAMOND_FOR_GOLD = "diamond_for_gold";
    private ArrayList<PayGoodsBean> goods = new ArrayList<PayGoodsBean>();

    private final static int HANDLE_GET_GOLD = 1000;//获取金币数量
    private final static int HANDLE_EXCHANGE_LIST_FLAG = 1001;//获取钻石金币兑换列表
    private final static int HANDLE_DIAMOND_FOR_GOLD = 1002;//兑换金币


    public static void jumpPayBuyGlodActivity(Context context) {
        Intent i = new Intent(context, FragmentPayBuyGlod.class);
        context.startActivity(i);
    }

    public static void jumpPayBuyGlodActivity(Context context, String fromTag) {
        Intent intent = new Intent(context, FragmentPayBuyGlod.class);
//		intent.putExtra( EventBuffer.TAG_NAME , fromTag );//jiqiang
        context.startActivity(intent);
    }

    public static void jumpPayBuyGlodActivityForResult(Context context, int requestCode) {
        Intent intent = new Intent(context, FragmentPayBuyGlod.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }


    public static void jumpPayBuyGlodActivityFromStore(Context context, int requestCode) {
        Intent intent = new Intent(context, FragmentPayBuyGlod.class);
        intent.putExtra("isExchangeGoldPrice", true);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.x_pay_buy_diamond_gold_view);

        String data = SharedPreferenceCache.getInstance(mContext).getString(DIAMOND_FOR_GOLD);
        if (!TextUtils.isEmpty(data)) {
            PayChannelBean bean = GsonUtil.getInstance().getServerBean(data,
                    PayChannelBean.class);
            initData(bean);
            findViewById(R.id.llEmpty).setVisibility(View.GONE);
        }

        isExchangeGoldPrice = getIntent().getBooleanExtra("isExchangeGoldPrice", false);

        initView();
        initListener();
        requestData();
    }

    private void initView() {
        listView = (PullToRefreshListView) findViewById(R.id.message_list);
        mlistAdapter = new PayChannelAdapter();
        listView.setAdapter(mlistAdapter);

        TextView title = (TextView) findViewById(R.id.title_name);
        title.setText(R.string.exchange_gold);
        TextView title_right = (TextView) findViewById(R.id.title_right_text);
        title_right.setText(R.string.pay_main_right_title);
        ImageView balance_icon = (ImageView) findViewById(R.id.balance_icon);
        balance_icon.setImageResource(R.drawable.z_pay_buy_dgold_icon);
        TextView balance_tv = (TextView) findViewById(R.id.balance_tv);
        balance_tv.setText(R.string.gold_count);
        balance_count = (TextView) findViewById(R.id.balance_count);
        balance_count.setText(PayModel.getInstance().getGoldNum() + "");
        TextView foot_text = (TextView) findViewById(R.id.foot_text);
        foot_text.setText(R.string.exchange_gold);
    }

    private void initListener() {
        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.title_right_text).setOnClickListener(this);
    }

    /**
     * 获取钻石金币兑换列表
     */
    private void requestData() {
        getExchangeListFlag = GoldHttpProtocol.diamondForGold(mContext, this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.title_back:
                finishActivity();
                break;
            case R.id.title_right_text:// 免费金币
//			DataStatistics.get(mContext).addButtonEvent(DataTag.BTN_me_wallet_freeGold);
//			Intent intent = new Intent(this,RewardTaskMainFragmentActivity.class);
//			startActivity(intent);//jiqiang
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 如果是返回商店后需要进行的操作
     */
    public void finishActivity() {
        if (isExchangeGoldPrice == true) {
            setResult(Activity.RESULT_OK);
        }
        isExchangeGoldPrice = false;
        finish();
    }

    // 初始化支付渠道
    private void initData(PayChannelBean bean) {
        goods = bean.goods;
    }

    // 数据列表组合
    private class PayChannelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return goods.size();
        }

        @Override
        public PayGoodsBean getItem(int position) {
            return goods.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.x_paymian_goods_item,
                        null);
            }

            final PayGoodsBean goodgBean = getItem(position);
            if (goodgBean != null) {
                ImageView imag = (ImageView) convertView
                        .findViewById(R.id.diamond_icon);
                TextView text = (TextView) convertView
                        .findViewById(R.id.diamond_num);
                TextView tvPrice = (TextView) convertView
                        .findViewById(R.id.price);
                View view = convertView.findViewById(R.id.btn_buy);
                String iconStr = goodgBean.icon;

//				ImageViewUtil.getDefault().fadeInLoadImageInConvertView(
//						iconStr, imag, R.drawable.z_pay_gold_default,
//						R.drawable.z_pay_gold_default);
                GlideUtil.loadCircleImage(BaseApplication.appContext, iconStr, imag, R.drawable.z_pay_gold_default,
                        R.drawable.z_pay_gold_default);
                text.setText(goodgBean.goldnum
                        + getResources().getString(R.string.pay_gold));
                tvPrice.setText(goodgBean.diamondnum + "");
                view.setTag(goodgBean);
                view.setOnClickListener(onChangeIconListener);
            }

            return convertView;
        }
    }

    // 获取当前用户的金币数
    private void getUserGoldForServer() {
        mGoldGetFlag = GoldHttpProtocol.userGoldGet(mContext, this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (mGoldGetFlag == flag) {
            Message msg = new Message();
            msg.what = HANDLE_GET_GOLD;
            msg.obj = result;
            mHandler.sendMessage(msg);
        } else if (getExchangeListFlag == flag) {
            Message msg = new Message();
            msg.what = HANDLE_EXCHANGE_LIST_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        } else if (diamondForGoldFlag == flag) {
            Message msg = new Message();
            msg.what = HANDLE_DIAMOND_FOR_GOLD;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        ErrorCode.toastError(mContext, e);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_EXCHANGE_LIST_FLAG:
                    parseExchangeList(msg.obj);
                    break;

                case HANDLE_GET_GOLD:
                    parseGoldNumber(msg.obj, true);
                    break;

                case HANDLE_DIAMOND_FOR_GOLD:
                    parseGoldNumber(msg.obj, false);
                    break;
            }
        }

    };


    /**
     * 解析获取金币或兑换金币的返回
     *
     * @param msg
     * @param isGetGold 是否是获取金币接口返回
     */
    private void parseGoldNumber(Object msg, Boolean isGetGold) {
        try {
            String result = String.valueOf(msg);
            JSONObject json = new JSONObject(result);
            if (json.optInt("status") == 200) {
                long goldnum = json.optLong("goldnum");
                long diamondnum = json.optLong("diamondnum");
                setGold(goldnum);
                setDiamond(diamondnum);
                if (!isGetGold) {
                    if (isExchangeGoldPrice == true) {
                        finishActivity();
                    }
                }
            } else {
                ErrorCode.showError(mContext, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析获取钻石金币兑换列表的返回
     *
     * @param data
     */
    private void parseExchangeList(Object data) {
        String json = (String) data;
        PayChannelBean bean = GsonUtil.getInstance().getServerBean(json, PayChannelBean.class);
        findViewById(R.id.llEmpty).setVisibility(View.GONE);
        if (bean != null) {
            if (bean.isSuccess()) {
                SharedPreferenceCache.getInstance(mContext).putString(
                        DIAMOND_FOR_GOLD, json);
                initData(bean);
            } else {
                ErrorCode.toastError(mContext, bean.error);
            }
        }
        mlistAdapter.notifyDataSetChanged();
    }

    /**
     * 更新当前用户的金币数
     */
    public void setGold(long gold) {
        PayModel.getInstance().setGoldNum(gold);
        balance_count.setText(gold + "");
    }

    /**
     * 更新当前用户的钻石数
     */
    public void setDiamond(long diamond) {
        PayModel.getInstance().setDiamondNum(diamond);
    }


    @Override
    public void onUpdate() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserGoldForServer();
    }

    // 用钻石兑换金币
    View.OnClickListener onChangeIconListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            final PayGoodsBean goodgBean = (PayGoodsBean) v.getTag();
            if (PayModel.getInstance().getDiamondNum() < goodgBean.diamondnum) {
                DialogUtil.showDiamondDialog(mActivity);
                return;
            }

            DialogUtil.showTwoButtonDialog(mContext, mContext.getResources()
                            .getString(R.string.diamond_for_gold_tip), String.format(
                    getResources().getString(
                            R.string.you_want_to_use_gold_diamond_exchange),
                    goodgBean.diamondnum, goodgBean.goldnum), mContext
                            .getResources().getString(R.string.cancel), mContext
                            .getResources().getString(R.string.diamond_for_gold_ok),
                    null, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            diamondForGoldFlag = GoldHttpProtocol
                                    .onclickDiamondForGold(mContext,
                                            goodgBean.goodsid,
                                            FragmentPayBuyGlod.this);
                        }
                    });
        }
    };
}
