package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.model.entity.Item;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.tools.FaceManager;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月29日 下午5:12:17
 * @Description: 我的的消息的基础类
 */
public abstract class MySelfBaseRecordView extends ChatRecordView {

	// sendStatus res
	private static String mStrRead;
	private static int mColRead;
	private static String mStrArrival;
	private static int mColArrival;
	private static String mStrFail;
	private static int mColFail;

	protected HeadPhotoView mIconView;// 用户头像
	protected TextView mStatusView;// 状态
	protected ProgressBar mProgress;// LoadingBar
	protected CheckBox checkbox;// 勾选的View

	/***
	 * 发出信息用户的身份标识
	 */
	protected LinearLayout llUserInfoMe;
	protected LinearLayout llUserIdentity;
	protected TextView tvIdentityManagerMe;//管理员
	protected TextView tvIdentityOwnerMe;//吧主
	protected LinearLayout llCharmRankMe; //魅力排行榜布局
	protected TextView tvTimeMe;//本周，上周，本月，上月
	protected TextView tvRankMe;//排名
	protected LinearLayout llWealthRankMe;//富豪排行榜布局
	protected TextView tvWealthTimeMe;
	protected TextView tvWealthRankMe;
	protected TextView tvNameMe;
	private UserTitleView userTitleView;

	public MySelfBaseRecordView(Context context) {
		super(context);
		initDefaultRes(context);
		inflatView(context);

		mIconView = (HeadPhotoView) findViewById(R.id.icon);
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mStatusView = (TextView) findViewById(R.id.status);//jiqiang
		checkbox = (CheckBox) findViewById(R.id.checkbox);
		/**
		 * 个人信息
		 */
		llUserInfoMe = (LinearLayout) findViewById(R.id.myslef_ly);
		tvNameMe = (TextView) findViewById(R.id.tvName_me);
		llUserIdentity = (LinearLayout) findViewById(R.id.ll_user_identity);
		tvIdentityManagerMe = (TextView) findViewById(R.id.tvIdentity_manager_me);
		tvIdentityOwnerMe = (TextView) findViewById(R.id.tvIdentity_owner_me);
		llCharmRankMe = (LinearLayout) findViewById(R.id.ll_charm_rank_me);
		llWealthRankMe = (LinearLayout) findViewById(R.id.ll_wealth_rank_me);
		tvTimeMe = (TextView) findViewById(R.id.tv_time_me);
		tvWealthTimeMe = (TextView) findViewById(R.id.tv_wealth_time_me);
		tvRankMe = (TextView) findViewById(R.id.tv_top_me);
		tvWealthRankMe = (TextView) findViewById(R.id.tv_wealth_top_me);
		userTitleView = (UserTitleView) findViewById(R.id.friend_ustitle_me);
	}

	/** 实例化View,必须实现 */
	protected abstract void inflatView(Context context);

	@Override
	public void initRecord(Context context, ChatRecord record) {
		setUserIconClickListener(context, mIconView);
		mStatusView.setOnClickListener(mResendClickListener);
		checkbox.setOnClickListener(mCheckBoxClickListener);
		setOnClickListener(mCheckBoxClickListener);
	}

	protected void updateStatus(Context context, ChatRecord record) {

		resetStatus();
		// 状态
		int status = record.getStatus();

		// 发送中的状态私聊和圈聊都有,其它状态都只有私聊显示
		if (ChatRecordStatus.isSending(status)) {
			showSendProgress(true);
			mStatusView.setEnabled(false);
		} else if (ChatRecordStatus.isFail(status)) {
			mStatusView.setText(mStrFail);
			mStatusView.setTextColor(mColFail);
			mStatusView.setTag(record);
			mStatusView.setEnabled(true);
		} else {

			if (context instanceof GroupChatTopicActivity)
				return;// 圈聊不显示除发送中以外状态

			// mType 定义的是（0-正常私聊消息，1-搭讪）
			int mType = ((ChatPersonal) context).getMType();
			if (mType == 1)
				return;// 搭讪不显示除发送中以外状态

			if (ChatRecordStatus.isArrived(status)) {
				mStatusView.setText(mStrArrival);
				mStatusView.setTextColor(mColArrival);
				mStatusView.setEnabled(false);
			} else if (ChatRecordStatus.isRead(status)) {
				mStatusView.setText(mStrRead);
				mStatusView.setTextColor(mColRead);
				mStatusView.setEnabled(false);
			}
		}

		String userNameMe = record.getNoteName(true);
		if (context instanceof GroupChatTopicActivity)
		{
			if (context instanceof GroupChatTopicActivity)
			{
				/***
				 * 判断是否是系统用户
				 */
				if (!bIsSystemUser(record.getFuid())) {
					String strMe = userNameMe;
					SpannableString spContentMe = FaceManager.getInstance(getContext()).parseIconForString(tvNameMe, getContext(), strMe, 12);
					tvNameMe.setText(spContentMe);
				}
			}
		}
	}

	/**
	 * 初始化发送状态的Str和Col
	 * 
	 * @param context
	 */
	private void initDefaultRes(Context context) {
		if (!TextUtils.isEmpty(mStrRead))
			return;

		mStrArrival = context
				.getString(R.string.chat_personal_record_msg_tag_send);
		mColArrival = context.getResources().getColor(
				R.color.chat_personal_msg_tag_text_send);

		mStrRead = context
				.getString(R.string.chat_personal_record_msg_tag_read);
		mColRead = context.getResources().getColor(
				R.color.chat_personal_msg_tag_text_read);

		mStrFail = context
				.getString(R.string.chat_personal_record_msg_tag_fail);
		mColFail = context.getResources().getColor(
				R.color.chat_personal_msg_tag_text_fail);
	}

	@Override
	public void showCheckBox(boolean isShow) {
		checkbox.setVisibility(isShow ? View.VISIBLE : View.GONE);
		setEnabled(isShow);
		setContentClickEnabled(!isShow);
	}

	public abstract void setContentClickEnabled(boolean isEnable);
	
	/** 重置消息状态 */
	private void resetStatus() {
		showSendProgress(false);
		mStatusView.setText("");
	}

	/** 显示发送loading标志 */
	private void showSendProgress(boolean showProgress) {
		mStatusView.setVisibility(showProgress ? GONE : VISIBLE);
		mProgress.setVisibility(showProgress ? VISIBLE : GONE);
	}
	/** 判断是否系统用户 */
	protected boolean bIsSystemUser( long uid )
	{
		return uid <= Config.SYSTEM_UID;
	}
	/**
	 *  设置用户的名字和距离 只有圈聊显示名字和距离
	 */
	protected void setUserNameDis(Context context, ChatRecord record,int grouprole )
	{
		if ( context instanceof GroupChatTopicActivity)
		{
			llUserInfoMe.setVisibility( View.VISIBLE );
			userTitleView.setVisibility(GONE);

			int svip = record.getfSVip( );
			if ( svip == 0 )
			{
//				ivSVIP.setVisibility( GONE );
				tvNameMe.setTextColor( getResources( ).getColor( R.color.c_999999 ) );
			}
			else
			{
//				ivSVIP.setVisibility( GONE );
				tvNameMe.setTextColor( getResources( ).getColor( R.color.c_ee4552 ) );
			}


			String userNameMe = record.getNoteName(true);
			if (context instanceof GroupChatTopicActivity)
			{

				/***
				 * 判断是否是系统用户
				 */
				if (!bIsSystemUser(record.getUid()))
				{
					String str = userNameMe;
					SpannableString spContent = FaceManager.getInstance(getContext())
							.parseIconForString(tvNameMe, getContext(), str, 12);
					tvNameMe.setText(spContent);

					/**
					 * 判断收到信息的用户的身份标识
					 */
					if (grouprole == 0)
					{
						llUserIdentity.setVisibility(VISIBLE);
						tvIdentityOwnerMe.setVisibility(VISIBLE);
						tvIdentityManagerMe.setVisibility(GONE);
						tvIdentityOwnerMe.setBackgroundResource(R.drawable.chatbar_groupowner_bg);
						tvIdentityOwnerMe.setText(R.string.chatbar_owner);
					}else if (grouprole == 1)
					{
						llUserIdentity.setVisibility(VISIBLE);
						tvIdentityManagerMe.setVisibility(VISIBLE);
						tvIdentityOwnerMe.setVisibility(GONE);
						tvIdentityManagerMe.setBackgroundResource(R.drawable.chatbar_groupmanager_bg);
						tvIdentityManagerMe.setText(R.string.chatbar_manager);

					}else if (grouprole == 2)
					{
						llUserIdentity.setVisibility(GONE);
						tvIdentityManagerMe.setVisibility(GONE);
						tvIdentityOwnerMe.setVisibility(GONE);
					}

					Item item = record.getItem();
					if(item != null){
//						llCharmRankMe.setVisibility(VISIBLE);
						userTitleView.setVisibility(VISIBLE);
						userTitleView.setTitleText(item);
						///这个没用吧？
						///RankingTitleUtil.getInstance().handleReallyRank(item.key,item.value,tvTimeMe);
					}else {
						userTitleView.setVisibility(GONE);
//						llCharmRankMe.setVisibility(GONE);
					}

				} else
				{// 5.7圈聊中系统用户不显示距离 tanzy
					tvNameMe.setText(userNameMe);
				}
			}
		}
		else
		{
			llUserInfoMe.setVisibility( View.GONE );
		}
	}
}
