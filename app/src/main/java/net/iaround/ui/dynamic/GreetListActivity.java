package net.iaround.ui.dynamic;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GreetListBack;
import net.iaround.model.entity.GreetListItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.adapter.GreetListAdapter;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-26 上午11:09:47
 * @Description: 点赞列表的Activity
 */
public class GreetListActivity extends SuperActivity implements OnClickListener {

	private final static int DYNAMIC = 0;//请求的动态的点赞列表
	private final static int POST_BAR = 1;//请求的贴吧的点赞列表
	private final static int GROUP_TOPIC = 2;//请求的圈话题的点赞列表

	private ImageView tvTitleBack;// 返回按钮
	private TextView tvTitle;// 标题

	private ArrayList<GreetListItemBean> dataList;// 数据list
	private PullToRefreshListView listView;// 下拉刷新列表
	private GreetListAdapter mAdapter;// 点赞列表的适配器

	private int currentPage = 1;// 当前请求服务端的页数
	private final int PAGE_SIZE = 10;// 每一页请求的数量
	private int totalPage;// 总共页数

	private int greetType;// 点赞列表的类型
	private long targetId;// 动态id,贴吧id,圈子id
	private long topicId;// 话题id,只提供给贴吧和圈子使用

	/** 协议标识 */
	private static long GREET_MEMBER_FLAG;

	// Dialog
	private Dialog progressDialog;

	// msg.what
	private static final int MSG_UPDATE = 1;
	private static final int MSG_SHOW_TOAST = 2;
	private static final int MSG_COMPLETE = 0;
	
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_COMPLETE:
				listView.onRefreshComplete();
				break;
			case MSG_UPDATE:
				refreshUI();
				break;
			case MSG_SHOW_TOAST:
				showToast( (String) msg.obj );
				break;
			}
		}
    };

	public static void skipToPostBarGreetListActivity(Context context,
			long postbarid, long topicid) {
		Intent intent = new Intent();
		intent.setClass(context, GreetListActivity.class);
		intent.putExtra("GREET_TYPE", POST_BAR);
		intent.putExtra("TARGETID", postbarid);
		intent.putExtra("TOPICID", topicid);
		context.startActivity(intent);
	}

	public static void skipToDynamicGreetListActivity(Context context,
			long dynamicid) {
		Intent intent = new Intent();
		intent.setClass(context, GreetListActivity.class);
		intent.putExtra("GREET_TYPE", DYNAMIC);
		intent.putExtra("TARGETID", dynamicid);
		intent.putExtra("TOPICID", 0L);
		context.startActivity(intent);
	}

	public static void skipToGroupTopicGreetListActivity(Context context,
			long groupid, long topicid) {
		Intent intent = new Intent();
		intent.setClass(context, GreetListActivity.class);
		intent.putExtra("GREET_TYPE", GROUP_TOPIC);
		intent.putExtra("TARGETID", groupid);
		intent.putExtra("TOPICID", topicid);
		context.startActivity(intent);
	}

	private void showToast( String content )
	{
		CommonFunction.showToast( mContext , content , Toast.LENGTH_SHORT );
	}
	
	private void refreshUI() {
		mAdapter.notifyDataSetChanged();
		listView.onRefreshComplete();
	}

	private void requestData() {
		showDialog();

		GREET_MEMBER_FLAG = protocolReq();
		if (GREET_MEMBER_FLAG == -1) {
			// 失败
			listView.onRefreshComplete();

			Message msg = new Message();
			msg.what = MSG_SHOW_TOAST;
			msg.obj = ErrorCode.getErrorMessageId(ErrorCode.E_104);
			mHandler.sendMessage(msg);
		}
	}

	/** 请求协议 */
	private long protocolReq() {
		switch (greetType) {
		case POST_BAR:
//			return PostbarHttpProtocol.getPostbarTopicLikeList(mContext,
//					targetId, topicId, currentPage, PAGE_SIZE, this);//YC 贴吧
		case DYNAMIC:
			return DynamicHttpProtocol.getUserDynamicGreetList(mContext,
					targetId, currentPage, PAGE_SIZE, this);
		case GROUP_TOPIC:
			return GroupHttpProtocol.getUserTopicGreetList(mContext, targetId,
					topicId, currentPage, PAGE_SIZE, this);
		default:
			return -1;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_greet_list);

		greetType = getIntent().getIntExtra("GREET_TYPE", 0);
		targetId = getIntent().getLongExtra("TARGETID", 0);
		topicId = getIntent().getLongExtra("TOPICID", 0);

		initView();
		initVariable();
		setListener();
		
		showDialog();
		requestData();
	}

	private void initView() {
		tvTitleBack = (ImageView) findViewById(R.id.iv_left);
		findViewById(R.id.fl_left).setOnClickListener(this);
		tvTitleBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.greet_list_title);
		listView = (PullToRefreshListView) findViewById(R.id.ptrflvGreetList);
	}

	private void initVariable() {
		dataList = new ArrayList<GreetListItemBean>();
		mAdapter = new GreetListAdapter(mContext, dataList);
		listView.getRefreshableView().setAdapter(mAdapter);
		listView.setMode(Mode.PULL_FROM_END);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GreetListItemBean itemBean = dataList.get(position -1);
				if (itemBean.getUser().userid == Common.getInstance().loginUser.getUid())
				{
					Intent intent = new Intent(GreetListActivity.this, UserInfoActivity.class);
					startActivity(intent);
				}else
				{
					Intent intent = new Intent(GreetListActivity.this, OtherInfoActivity.class);
					intent.putExtra("user",itemBean.getUser().convertBaseToUser());
					intent.putExtra(Constants.UID,itemBean.getUser().userid);
					startActivity(intent);
				}
			}
		});
	}

	private void setListener() {
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (currentPage <= totalPage) {
					requestData();
				} else {
					CommonFunction.toastMsg(mContext, R.string.no_more);
					mHandler.sendEmptyMessage(MSG_COMPLETE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.equals(tvTitleBack) || v.getId() == R.id.fl_left) {
			finish();
		}
	}

	@Override
	public void onGeneralSuccess(String result, long flag) {
		hideDialog();
		super.onGeneralSuccess(result, flag);
		GreetListBack bean = GsonUtil.getInstance().getServerBean(result,
				GreetListBack.class);
		if (flag == GREET_MEMBER_FLAG) {
			if (bean.isSuccess()) {
				if(bean.getLikeuserList() != null && bean.getLikeuserList().size( ) >0)
				{
					dataList.addAll(bean.getLikeuserList());
				}
				currentPage = bean.pageno + 1;
				totalPage = (bean.amount / bean.pagesize)
						+ ((bean.amount % bean.pagesize) > 0 ? 1 : 0);
				mHandler.sendEmptyMessage(MSG_UPDATE);
			} else {
				ErrorCode.showError(mContext, result);
			}
		}
	}

	@Override
	public void onGeneralError(int e, long flag) {
		hideDialog();
		listView.onRefreshComplete();
		super.onGeneralError(e, flag);
		if (flag == GREET_MEMBER_FLAG) {
			// 失败
			ErrorCode.toastError(mContext, e);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private void showDialog() {
		hideDialog();

		if (progressDialog == null) {
			progressDialog = DialogUtil.showProgressDialog(GreetListActivity.this,
					R.string.share_select_style, R.string.please_wait, null);
			return;
		}

		progressDialog.show();
	}

	private void hideDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.hide();
		}
	}
}
