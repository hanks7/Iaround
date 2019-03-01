package net.iaround.pay.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.analytics.ums.DataTag;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.entity.GoogleNotifyRec;
import net.iaround.model.entity.GooglePayDBBean;
import net.iaround.model.entity.GooglePayNotifyBean;
import net.iaround.model.entity.GooglePayNotifyBean.GoogleOrder;
import net.iaround.pay.ChannelType;
import net.iaround.pay.PayViewUpdate;
import net.iaround.pay.bean.PayChannelBean;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.pay.google.IabHelper;
import net.iaround.pay.google.IabResult;
import net.iaround.pay.google.Inventory;
import net.iaround.pay.google.Purchase;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.ui.activity.DetailedActivity;
import net.iaround.ui.activity.PayChannelListActivity;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.utils.ImageViewUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 购买钻石
 *
 * @author
 */
public class FragmentPayDiamond extends SuperActivity implements PayViewUpdate,
		HttpCallBack, OnClickListener {


	private final static int REQ_CODE_PAY = 200;
	private final static int HANDLE_ID_GET_GOLE = 1;
	private final static int HANDLE_ID_GET_CHANNEL = 2;
	private final static int INSERT_ORDER_TO_DATABASE = 3;
	private final static int DELETE_ORDER_FROM_DATABASE = 4;
	private final static String CACHE_MY_WALLET = "get_my_wallet";

	private TextView balance_count;
	private Dialog mProgressDialog;
	private PayChannelAdapter mlistAdapter;
	private PullToRefreshListView listView;

	private long mGoldGetFlag = 0;
	private long mPayChannelFlag = 0;
	private long notify_flag = 0;
	private boolean isChina;
	private String payChannel;
	private ArrayList<PayGoodsBean> goods = new ArrayList<PayGoodsBean>();

	/** google pay start */
	private IabHelper mHelper;
	private ArrayList<String> skuArray;
	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkDjXyvs5ql3E/CWemJDXdhw6HM5LAXtjIs7+jfsESol032Qn+nJUwSvdeNOLOl+Owe3p2sQ/M5v2z5YvuI6IviWcqRAH2gMwpJ1FSbMQMCHq4FWyWpeJCuAumNrBUMARdr+VHy+eYlCgXtwOJNMEi/9Qs1g2P/qQN098KxRJTgxm1nTauFn5Nem0jPSAf2BGw8Th9kAUUGxiw0SiWCqIwwsA9P8OUFJGotFLmijyoozzDT2AVd/cPqQukHA2fxnPtCIncyWv41t9hCyZPL6PN/gJRWBRi5uxQRizd0tjy0Dw9D9nu879AkmPxQAqbsj7iWGxg1gNCx1vGshKSKJl1QIDAQAB";
	private long orderDBID;
	/** google pay end */

	public static void jumpPayDiamondActivity( Context context )
	{
		jumpPayDiamondActivity(context, DataTag.UNKONW);
	}

	public static void jumpPayDiamondActivity(Context context , String fromTag )
	{
		Intent intent = new Intent( context , FragmentPayDiamond.class );
//		intent.putExtra( EventBuffer.TAG_NAME , fromTag );
		context.startActivity( intent );
	}

	public static void jumpPayDiamondActivityForResult(Context context , int requestCode )
	{
		jumpPayDiamondActivityForResult( context , requestCode , DataTag.UNKONW );
	}

	public static void jumpPayDiamondActivityForResult(Context context , int requestCode ,
													   String fromTag )
	{
		Intent intent = new Intent( context , FragmentPayDiamond.class );
//		intent.putExtra( EventBuffer.TAG_NAME , fromTag );
		( (Activity) context ).startActivityForResult( intent , requestCode );
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.x_pay_buy_diamond_gold_view);

		isChina = PhoneInfoUtil.getInstance(mContext).isChinaCarrier();
		String data = SharedPreferenceCache.getInstance(mContext).getString(
				CACHE_MY_WALLET);
		if (!TextUtils.isEmpty(data)) {
			PayChannelBean bean = GsonUtil.getInstance().getServerBean(data,
					PayChannelBean.class);
			initData(bean);
			findViewById(R.id.llEmpty).setVisibility(View.GONE);
		}

		// 启动事件统计，添加入口界面的tag
//		String enterTag = getIntent( ).getStringExtra( EventBuffer.TAG_NAME );
//		if ( !CommonFunction.isEmptyOrNullStr( enterTag ) && !DataTag.UNKONW.equals( enterTag ) )
//		{
//			EventBuffer.getInstant( ).startEvent( DataTag.EVENT_buy_diamond);
//			EventBuffer.getInstant( ).appen( enterTag );
//			EventBuffer.getInstant( ).appen( DataTag.VIEW_me_wallet_buyDiamon );
//		}

		initView();
		initListener();
		requestData();
	}

	private void initView() {
		listView = (PullToRefreshListView) findViewById(R.id.message_list);
		mlistAdapter = new PayChannelAdapter();
		listView.setAdapter(mlistAdapter);

		TextView title = (TextView) findViewById(R.id.title_name);
		title.setText(R.string.to_get_diamond);
		TextView title_right = (TextView) findViewById(R.id.title_right_text);
		title_right.setText(R.string.diamond_detailed);
		ImageView balance_icon = (ImageView) findViewById(R.id.balance_icon);
		balance_icon.setImageResource(R.drawable.z_pay_buy_diamond_icon);
		TextView balance_tv = (TextView) findViewById(R.id.balance_tv);
		balance_tv.setText(R.string.diamond_count);
		balance_count = (TextView) findViewById(R.id.balance_count);
		balance_count.setText(PayModel.getInstance().getDiamondNum() + "");
		TextView foot_text = (TextView) findViewById(R.id.foot_text);
		foot_text.setText(R.string.pay_title_diamond_change);
	}

	private void initListener() {
		findViewById(R.id.title_back).setOnClickListener(FragmentPayDiamond.this);
		findViewById(R.id.title_right_text).setOnClickListener(FragmentPayDiamond.this);
	}

	private void requestData() {
		mPayChannelFlag = GoldHttpProtocol.getMywallet(mContext, FragmentPayDiamond.this);
	}

	// 初始化支付渠道
	private void initData(PayChannelBean bean) {
		goods = bean.goods;
	}

	// 数据列表组合
	private class PayChannelAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (goods == null)
				return 0;
			else if (goods.size() % 2 == 0) {
				return goods.size() / 2;
			}
			return goods.size() / 2 + 1;
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

			final PayGoodsBean goodsItem = getItem(position);
			if (goodsItem != null) {
				ImageView imag = (ImageView) convertView
						.findViewById(R.id.diamond_icon);
				TextView text = (TextView) convertView
						.findViewById(R.id.diamond_num);
				TextView tvPrice = (TextView) convertView
						.findViewById(R.id.price);
				View view = convertView.findViewById(R.id.btn_buy);
				ImageViewUtil.getDefault().fadeInLoadImageInConvertView(
						goodsItem.icon, imag, R.drawable.z_pay_diamond_default,
						R.drawable.z_pay_diamond_default);

				String strDiamond = mContext.getResources().getString(
						R.string.diamond);
				tvPrice.setCompoundDrawables(null, null, null, null);
				if (goodsItem.senddiamond > 0) {
					text.setText(goodsItem.diamondnum + "+"
							+ goodsItem.senddiamond + strDiamond);
				} else {
					text.setText(goodsItem.diamondnum + strDiamond);
				}
				if (isChina) {
					tvPrice.setText("¥" + goodsItem.price + "");
				} else {
					tvPrice.setText(goodsItem.strPrice);
				}

				view.setTag(goodsItem);
				view.setOnClickListener(onBuyDiamondClick);
			}
			return convertView;
		}
	}

	// 获取当前用户的金币数
	public void getUserGoldForServer() {
		mGoldGetFlag = GoldHttpProtocol.userGoldGet(mContext, FragmentPayDiamond.this);
	}

	@Override
	public void onGeneralSuccess(String result, long flag) {
		if (mGoldGetFlag == flag) {
			Message msg = new Message();
			msg.what = HANDLE_ID_GET_GOLE;
			msg.obj = result;
			mHandler.sendMessage(msg);
		} else if (mPayChannelFlag == flag) {
			Message msg = new Message();
			msg.what = HANDLE_ID_GET_CHANNEL;
			msg.obj = result;
			mHandler.sendMessage(msg);
		} else if (notify_flag == flag) {
			Message msg = new Message();
			msg.what = DELETE_ORDER_FROM_DATABASE;
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
			if (msg.what == HANDLE_ID_GET_GOLE) {
				try {
					String result = String.valueOf(msg.obj);
					JSONObject json = new JSONObject(result);
					if (json.optInt("status") == 200) {
						long goldnum = json.optLong("goldnum");
						long diamondnum = json.optLong("diamondnum");
						setGold(goldnum);
						setDiamond(diamondnum);
					} else {
						ErrorCode.showError(mContext, result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == HANDLE_ID_GET_CHANNEL) {
				payChannel = (String) msg.obj;
				PayChannelBean bean = GsonUtil.getInstance().getServerBean(
						payChannel, PayChannelBean.class);
				setGold(bean.gold);
				setDiamond(bean.diamond);
				findViewById(R.id.llEmpty).setVisibility(View.GONE);
				if (bean != null) {
					if (bean.isSuccess()) {
						SharedPreferenceCache.getInstance(mContext).putString(
								CACHE_MY_WALLET, payChannel);
						initData(bean);
						if (!isChina) {
							try {
								initGoogleConnect();
								showProgressDialog();
							} catch (Exception e) {
								for (PayGoodsBean good : goods) {
									good.strPrice = "NT$" + good.price;
								}
								mlistAdapter.notifyDataSetChanged();
							}
						} else {
							mlistAdapter.notifyDataSetChanged();
						}
					} else {
						// 失败处理
						ErrorCode.toastError(mContext, bean.error);
					}
				}
			} else if (msg.what == DELETE_ORDER_FROM_DATABASE) {
				String result = (String) msg.obj;
				GoogleNotifyRec bean = GsonUtil.getInstance().getServerBean(
						result, GoogleNotifyRec.class);
				if (bean.isSuccess()) {
					deleteOrder();
					getUserGoldForServer();
				}
			} else if (msg.what == INSERT_ORDER_TO_DATABASE) {
				String content = (String) msg.obj;
				insertOrder(content);
			}
		}
    };

	@Override
	public void onUpdate() {
	}

	/**
	 * 更新当前用户的金币数
	 */
	public void setGold(long gold) {
		PayModel.getInstance().setGoldNum(gold);
	}

	/**
	 * 更新当前用户的钻石数
	 */
	public void setDiamond(long diamond) {
		PayModel.getInstance().setDiamondNum(diamond);
		balance_count.setText(diamond + "");
	}

	View.OnClickListener onBuyDiamondClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final PayGoodsBean goodsItem = (PayGoodsBean) v.getTag();
//			DataStatistics.get(mContext).addButtonEvent(
//					DataTag.BTN_me_wallet_buy);
			PayChannelListActivity.launchForResult(FragmentPayDiamond.this,
					goodsItem, REQ_CODE_PAY);
		}
	};

	/**
	 * 连接谷歌，查询商品列表
	 */
	private void initGoogleConnect() {

		mHelper = new IabHelper(mContext, base64EncodedPublicKey);
		mHelper.enableDebugLogging(true);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					mProgressDialog.dismiss();
					for (PayGoodsBean good : goods) {
						good.strPrice = "NT$" + good.price;
					}
					mlistAdapter.notifyDataSetChanged();
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
			mProgressDialog.dismiss();
			if (result.isFailure()) {
				for (PayGoodsBean good : goods) {
					good.strPrice = "NT$" + good.price;
				}
				mlistAdapter.notifyDataSetChanged();
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
				Message msg = new Message();
				msg.what = INSERT_ORDER_TO_DATABASE;
				msg.obj = content;
				mHandler.sendMessage(msg);

				// 上报服务端查询用户消费情况
				notify_flag = PayHttpProtocol.googleNotify(mContext,
						signeddata, purchase.getSignature(),
						FragmentPayDiamond.this);
			}
		}
	};

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
		mlistAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.title_back:
				finish();
				break;
			case R.id.title_right_text:// 钻石明细
				DetailedActivity.jump(mContext);
				break;
		}
	}

	// 显示加载框
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = DialogUtil.showProgressDialog(mContext, "",
					getString(R.string.please_wait), null);
		}
		mProgressDialog.show();
	}

	// 隐藏加载框
	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Activity.RESULT_OK == resultCode) {
			getUserGoldForServer();
			finish();
		}
	}

	private void insertOrder(String content) {
		orderDBID = PayModel.getInstance().insertOrder(getActivity(),
				ChannelType.GOOGLEPAY, content);
	}

	private void deleteOrder() {
		PayModel.getInstance().deleteOrder(getActivity(), orderDBID);
	}

	@Override
	protected void onDestroy( )
	{
//		EventBuffer.getInstant( ).uploadEvent( mContext , 0 );
		super.onDestroy( );
	}

}
