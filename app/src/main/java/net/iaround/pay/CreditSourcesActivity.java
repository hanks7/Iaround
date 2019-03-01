package net.iaround.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.entity.CreditsSourcesBackBean;
import net.iaround.entity.CreditsSourcesBackBean.creditData;
import net.iaround.model.im.Me;
import net.iaround.model.im.enums.ProfileEntrance;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.utils.ImageViewUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 积分来源
 * @author zhengst
 *
 */
public class CreditSourcesActivity extends SuperActivity implements
        OnClickListener,HttpCallBack {

	private int mCurPage = 1;
	private int mTotalPage = 1;
	private final int PAGE_SIZE = 15;
	private TextView loveCount;
	private MyAdapter myAdapter;
	private Dialog mProgressDialog;
	private EmptyLayout emptyLayout;
	private PullToRefreshListView mPullListView;
	private ArrayList<creditData> creditslist = new ArrayList<creditData>();
	private long HTTP_GET_DATA;
	private CreditsSourcesBackBean bean;
	private final static int HANDLE_GET_DATA_SUCCESS = 1000;
	private final static int HANDLE_GET_DATA_FAIL = 1001;

	public static void jumpCreditSourcesActivity(Context context) {
		Intent i = new Intent(context, CreditSourcesActivity.class);
		context.startActivity(i);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.x_credit_sources_view);

		initView();
		setListener();
		initData();
	}

	private void initView() {
		TextView mTitleName = (TextView) findViewById(R.id.tv_title);
		TextView mTitleRight = (TextView) findViewById(R.id.tv_go);
		mTitleName.setText(R.string.points_sources);
		mTitleRight.setText(R.string.points_credits_explain);

		//gh 去掉积分说明
		mTitleRight.setVisibility(View.GONE);

		NetImageView userIcon = (NetImageView) findViewById(R.id.user_icon);
		TextView userName = (TextView) findViewById(R.id.user_name);
		loveCount = (TextView) findViewById(R.id.balance_count);
		Me user = Common.getInstance().loginUser;
		userIcon.executeRoundFrame(NetImageView.DEFAULT_AVATAR_ROUND_LIGHT,user.getIcon());
		userIcon.setVipLevel(user.getViplevel());//vip
		userName.setText(Common.getInstance().loginUser.getNickname());
		loveCount.setText(PayModel.getInstance().getCredits()+"");

		mPullListView = (PullToRefreshListView) findViewById(R.id.list_view);
		myAdapter = new MyAdapter();
		mPullListView.getRefreshableView().setAdapter(myAdapter);
		mPullListView.setMode(Mode.PULL_FROM_END);
		emptyLayout = new EmptyLayout(mContext,mPullListView.getRefreshableView());
		emptyLayout.setEmptyMessage(getString(R.string.credit_no_data_hint));
		emptyLayout.setErrorMessage(getString(R.string.credit_no_data_hint));
	}

	private void setListener() {
		findViewById(R.id.fl_back).setOnClickListener(this);
		findViewById(R.id.fl_go).setOnClickListener(this);

		mPullListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新
						// requsetData(1);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 上拉刷新
						if (mCurPage < mTotalPage) {
							requsetData(mCurPage + 1);
						} else {
							refreshView.postDelayed(new Runnable() {
								public void run() {
									Toast.makeText(getBaseContext(),
											R.string.no_more,
											Toast.LENGTH_SHORT).show();
									mPullListView.onRefreshComplete();
								}
							}, 200);
						}
					}
				});
	}

	protected void requsetData(int nextPage) {
		HTTP_GET_DATA = GoldHttpProtocol.getCreditsHistory(mContext, nextPage,
				PAGE_SIZE, this);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// showProgressDialog();
		emptyLayout.showLoading();
		requsetData(mCurPage = 1);
	}

	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (creditslist != null) {
				return creditslist.size();
			}
			return 0;
		}

		@Override
		public creditData getItem(int position) {
			if (creditslist != null && position < creditslist.size()) {
				return creditslist.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(CreditSourcesActivity.this)
						.inflate(R.layout.x_credits_source_item_view, null);
				viewHolder.userIcon = (ImageView) convertView
						.findViewById(R.id.gifts_icon);
				viewHolder.giftName = (TextView) convertView
						.findViewById(R.id.gifts_name);
				viewHolder.sendTime = (TextView) convertView
						.findViewById(R.id.send_time);
				viewHolder.sendUserName = (TextView) convertView
						.findViewById(R.id.send_name);
				viewHolder.loveNumber = (TextView) convertView
						.findViewById(R.id.love_count);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			creditData bean = getItem(position);
			ImageViewUtil.getDefault().loadRoundFrameImageInConvertView(
			CommonFunction.thumPicture(bean.gifticon),viewHolder.userIcon,
			NetImageView.DEFAULT_AVATAR_ROUND_LIGHT,
			NetImageView.DEFAULT_AVATAR_ROUND_LIGHT, null, 0,"#00000000");
			viewHolder.giftName.setText(CommonFunction.getLangText(mContext, bean.giftname));
			//积分
			viewHolder.loveNumber.setText("+"+bean.credits);
			//赠送时间
			viewHolder.sendTime.setText(getString(R.string.stime_title_new)
			+ TimeFormat.convertTimeLong2String(bean.sendtime, Calendar.DATE));
			//赠送者
			String finalText = getString(R.string.ps_title_new) + bean.sendername;
			SpannableString spanText = new SpannableString(finalText);
			spanText.setSpan(new ForegroundColorSpan(
			      Color.parseColor("#FFBD54")),finalText.indexOf(bean.sendername) ,
			       finalText.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
			spanText.setSpan( new UnderlineSpan( ) , finalText.indexOf( bean.sendername ) ,
					finalText.length( ) , SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );
			viewHolder.sendUserName.setTextColor(Color.parseColor("#999999"));
			viewHolder.sendUserName.setText(spanText);

			viewHolder.sendUserName.setTag(bean);
			viewHolder.sendUserName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					creditData bean = (creditData) arg0.getTag();
//					SpaceOther.launch(mContext, bean.senderid, ProfileEntrance.UNKNOWN);//gh
				}
			});
			return convertView;
		}
	}

	@Override
	public void onGeneralError(int e, long flag) {
		super.onGeneralError(e, flag);
	}

	@Override
	public void onGeneralSuccess(String result, long flag) {
		super.onGeneralSuccess(result, flag);
		if (flag == HTTP_GET_DATA) {
			bean = GsonUtil.getInstance().getServerBean(result,
					CreditsSourcesBackBean.class);
			if (bean != null && bean.isSuccess()) {
				Message msg = new Message();
				msg.what = HANDLE_GET_DATA_SUCCESS;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = HANDLE_GET_DATA_FAIL;
				mHandler.sendMessage(msg);
			}
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressDialog();
			switch (msg.what) {
			case HANDLE_GET_DATA_SUCCESS:
				handleGetDataBackSuccess();
				break;
			case HANDLE_GET_DATA_FAIL:
				handleGetDataBackFail();
				break;
			}
		}
    };

	protected void handleGetDataBackSuccess() {
		mCurPage = bean.pageno;
		int total = bean.amount;
		mTotalPage = total / PAGE_SIZE;
		if (total % PAGE_SIZE > 0) {
			mTotalPage++;
		}
		if (mCurPage <= 1) {
			if (creditslist == null) {
				creditslist = new ArrayList<creditData>();
			} else {
				creditslist.clear();
			}
		} 
        if(bean.creditslist != null ){
			creditslist.addAll(bean.creditslist);
		}
		if (mTotalPage == mCurPage) {
			mPullListView.setMode(Mode.DISABLED);
		} else {
			mPullListView.setMode(Mode.PULL_FROM_END);
		}
		if (creditslist.isEmpty()) {
			emptyLayout.showEmpty();
		}
		stopPulling();
		PayModel.getInstance().setCredits(bean.credits);
		loveCount.setText( bean.credits + "" );
		myAdapter.notifyDataSetChanged();
	}

	protected void handleGetDataBackFail() {
		stopPulling();
		if (creditslist.isEmpty()) {
			emptyLayout.showError();
		} else {
			CommonFunction.toastMsg(mContext, R.string.e_104);
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

	/**
	 * 关闭dialog
	 */
	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.cancel();
		}
	}

	private void stopPulling() {
		mPullListView.onRefreshComplete();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.fl_back:
			finish();
			break;
		case R.id.fl_go:// 积分规则
			jumpWeb();
			break;
		}
	}

	private void jumpWeb(){
		Uri uri = Uri.parse( "http://www.iaround.com/m/newshelp/integration.html" );
		Intent i = new Intent( mContext , WebViewAvtivity.class );
		i.putExtra( WebViewAvtivity.WEBVIEW_TITLE , getString(R.string.points_credits_explain) );
		i.putExtra( WebViewAvtivity.WEBVIEW_URL , uri.toString( ) );
		startActivity( i );
	}
	
	static class ViewHolder {
		ImageView userIcon;
		TextView giftName;
		TextView sendUserName;
		TextView sendTime;
		TextView loveText;
		TextView loveNumber;
		RelativeLayout DownloadLayout;
	}
}
