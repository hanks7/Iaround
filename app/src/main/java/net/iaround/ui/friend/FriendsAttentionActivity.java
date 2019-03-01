
package net.iaround.ui.friend;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.database.FriendModel;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.Me;
import net.iaround.model.im.SocketSuccessResponse;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.pay.FragmentPayBuyGlod;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.GroupUser;
import net.iaround.ui.datamodel.NewFansModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.ui.view.HeadPhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: FriendsAttentionActivity
 * @Description: 所有好友/所有关注的界面
 * @date 2013-12-18 下午3:42:18
 */
public class FriendsAttentionActivity extends SuperActivity implements OnClickListener {

    /**
     * 好友为0，关注为1，粉丝为2，2应该不会出现
     */
    private int mViewMode = 0;

    private TextView mTitleName;
//    private ImageView mTitleBack;
    private EditText mSearchEditText;
    private ImageView mBtnSearch;
    private PullToRefreshListView mListView;
    private Dialog mProgressDialog;
    private EmptyLayout mEmptyLayout;
    private FrameLayout flLleft;

    private int mCurrentPage = 0;
    private int mTotalPage = 0;
    private int PAGE_SIZE = 20;
    private int mTotalNum;

    private long FLAG_GET_USER_LIST;
    private ArrayList<GroupSearchUser> mUserList = new ArrayList<GroupSearchUser>();
    private DataAdapter mAdapter;

    private GroupUser mLastClickUser;

    /**
     * 取消关注flag
     */
    private long FLAG_UNFOLLOWING;
    private long friendId;

    private net.iaround.model.im.Me me;
    private User fUser;// 目标用户实体
    private Gift gift;// 要赠送的礼物
    private long mReqSendMsgFlag;// 发送私聊的请求
    /**
     * 是否从商店跳转来
     */
    private Boolean isFromStore = false;
    private ChatRecord mChatRecord;// 聊天记录
    private static int from = 0;// 从哪里进入聊天
    private static int ischat = 0;// 类型（int） 是否交流（0-否，1-有）
    public static Dialog haveSentDialog = null;// 礼物已送到提示框
    public static Dialog diamondCanTalkDialog = null;// 送了钻石礼物可聊天提示框
    private final static int MSG_WHAT_REFRESH_MINE_SOCKET_SEND = 1001;// 赠送礼物后发送私聊
    private final static int MSG_WHAT_REFRESH_TITLE = 1002;// 赠送礼物后发送私聊

    public static final int MSG_MODIFY_NOTE = 5; // 修改备注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_attentions);
        Intent intent = getIntent();
        mViewMode = intent.getIntExtra("mode", 0);
        mTotalNum = intent.getIntExtra("count", 0);
        me = Common.getInstance().loginUser;

        // 如果从商店-私藏礼物 跳转至此，则需要这几个参数
        isFromStore = intent.getBooleanExtra("isFromStore", false);
        if (isFromStore == true) {
            gift = (Gift) intent.getSerializableExtra("gift");
            from = this.getIntent().getIntExtra("from", 0);
        }

        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        mTitleName = (TextView) findViewById(R.id.tv_title);
        flLleft = (FrameLayout) findViewById(R.id.fl_left);
        showTitle();

//        mTitleBack = (ImageView) findViewById(R.id.title_back);
        mProgressDialog = DialogUtil.getProgressDialog(mContext, "",
                getString(R.string.please_wait), new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mCurrentPage == 0) {
                            finish();
                        }
                    }
                });
        mSearchEditText = (EditText) findViewById(R.id.etSearch);
//		mBtnSearch = ( ImageView ) findViewById( R.id.btn_search );
//		mBtnSearch.setVisibility( View.GONE );

        mListView = (PullToRefreshListView) findViewById(R.id.user_listview);
        if (mViewMode != 2) {
            if (isFromStore && mTotalNum == 0) {
                mListView.setMode(Mode.BOTH);
            } else {
                mListView.setMode(Mode.PULL_FROM_START);
            }
        } else {
            mListView.setMode(Mode.BOTH);
        }
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 0;
                requestData(mCurrentPage + 1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mCurrentPage < mTotalPage) {
                    requestData(mCurrentPage + 1);
                } else {
                    mListView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mListView.onRefreshComplete();
                        }
                    }, 200);
                }
            }
        });
        mEmptyLayout = new EmptyLayout(mContext, mListView.getRefreshableView());
        mEmptyLayout.setEmptyMessage(getResString(R.string.empty_search_user));
    }

    private void setListeners() {
//        mTitleBack.setOnClickListener(this);
        flLleft.setOnClickListener(this);
        mSearchEditText.addTextChangedListener(filterTextWatch);
    }

    private void initData() {
        mAdapter = new DataAdapter(mUserList);
        mListView.setAdapter(mAdapter);
        mEmptyLayout.showLoading();
        requestData(mCurrentPage + 1);
    }

    private void requestData(int pageIndex) {
        showWaitDialog(true);
        try {
            if (mViewMode == 0) {
                FLAG_GET_USER_LIST = FriendHttpProtocol.friendsGet(mContext, this);
            } else if (mViewMode == 1) {
                if (mTotalNum == 0) {
                    FLAG_GET_USER_LIST = FriendHttpProtocol.userAttentions(mContext,
                            Common.getInstance().loginUser.getUid(), pageIndex, PAGE_SIZE,
                            this);
                } else {
                    FLAG_GET_USER_LIST = FriendHttpProtocol.userAttentions(mContext,
                            Common.getInstance().loginUser.getUid(), pageIndex, mTotalNum,
                            this);
                }
            } else if (mViewMode == 2) {
                FLAG_GET_USER_LIST = FriendHttpProtocol.userFans(mActivity,
                        Common.getInstance().loginUser.getUid(), pageIndex, PAGE_SIZE,
                        this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (FLAG_GET_USER_LIST <= 0) {
            showWaitDialog(false);
            handleDataFail(107, FLAG_GET_USER_LIST);
            if (pageIndex == 1) {
                mEmptyLayout.showError();
            }
        }
    }

    private void showTitle() {
        if (mViewMode == 0) {
            // 我的好友
            mTitleName.setText(getString(R.string.all_friends) + "(" + mTotalNum + ")");
        } else if (mViewMode == 1) {
            // 我的关注
            mTitleName.setText(getString(R.string.all_attentions) + "(" + mTotalNum + ")");
        } else if (mViewMode == 2) {
            // 我的粉丝
            mTitleName.setText(getString(R.string.my_friend_fans_title));
        }
    }

    @Override
    public void onGeneralSuccess(final String result, final long flag) {
        super.onGeneralSuccess(result, flag);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    handleDataSuccess(result, flag);
                } catch (Exception e) {
                    e.printStackTrace();
                    handleDataFail(107, flag);
                }
            }
        });
    }

    @Override
    public void onGeneralError(final int e, final long flag) {
        super.onGeneralError(e, flag);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                handleDataFail(e, flag);
            }
        });
    }

    private void handleDataSuccess(String result, long flag) {
        if (flag == FLAG_GET_USER_LIST) {
            mListView.onRefreshComplete();
            showWaitDialog(false);
            GroupMemberSearchBean userBean = GsonUtil.getInstance()
                    .getServerBean(result, GroupMemberSearchBean.class);
            if (userBean != null) {
                if (userBean.isSuccess()) {
                    if (mCurrentPage == 0) {
                        mUserList.clear();
                    }
                    mCurrentPage = userBean.pageno;
                    mTotalPage = userBean.amount / PAGE_SIZE;
                    if (userBean.amount % PAGE_SIZE > 0) {
                        mTotalPage++;
                    }
                    if (mViewMode == 0) {
                        // 我的好友
                        if (userBean.users != null) {
                            mUserList.addAll(userBean.users);

                            // 缓存完整的联系人列表
                            int listCount = mUserList.size();
                            ArrayList<User> friendList = new ArrayList<User>(listCount);
                            for (int i = 0; i < listCount; i++) {
                                GroupSearchUser item = mUserList.get(i);
                                friendList.add(GroupSearchUser.convertToUser(item));
                            }
                            FriendModel.getInstance(mContext).setAllFriendList(friendList);
                        }
                    } else if (mViewMode == 1 || mViewMode == 2) {
                        // 我的关注//我的粉丝
                        if (userBean.fans != null) {
                            if (isFromStore) {
                                mUserList.clear();
                                mUserList.addAll(userBean.fans);
                            } else {
                                mUserList.addAll(userBean.fans);
                            }
                        }
                        mTotalNum = userBean.amount;
                        mTheMainHandler.sendEmptyMessage(MSG_WHAT_REFRESH_TITLE);

                        // if ( mViewMode == 1 && mTotalNum == 0 )
                        // {
                        // // 我的关注
                        // mTitleName.setText( getString(
                        // R.string.all_attentions ) + "("
                        // + userBean.amount + ")" );
                        // }
                    }

                    if (mListView != null) {
                        if (mUserList.isEmpty() && mAdapter != null) {
                            mEmptyLayout.showEmpty();
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    handleDataFail(userBean.error, flag);
                }
            } else {
                handleDataFail(107, flag);
            }
        } else if (flag == FLAG_UNFOLLOWING) {
            hideDialog();
            try {
                JSONObject json = new JSONObject(result);
                int status = json.optInt("status");
                if (200 == status) {
                    deleteUser(friendId);
                    NewFansModel.getInstance().updateRelation(mContext, friendId, 4, Common.getInstance().loginUser.getUid());
                    Toast.makeText(mContext, R.string.disfollow_suc, Toast.LENGTH_SHORT)
                            .show();
                } else if (5702 == status)// ErrorCode.E_5702 该用户未被关注
                {
                    // Toast.makeText( mContext , R.string.e_5702 ,
                    // Toast.LENGTH_SHORT ).show( );
                    ErrorCode.toastError(mContext, ErrorCode.E_5702);
                } else {
                    CommonFunction.log("kevin", "ChatContactView-status==" + status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SpaceModelReqTypes reqType = SpaceModel.getInstance(mContext).getReqType(flag);
            if (SpaceModelReqTypes.SEND_MINE_GIFT.equals(reqType)) {
                JSONObject json;
                try {
                    json = new JSONObject(result);
                    int status = json.optInt("status");
                    if (200 == status) {
                        sendGiftMessage();// 发送一个送礼成功的私聊消息

                    } else {
                        int error = json.optInt("error");
                        if (status == -400) {
                            error = 4000;
                        }

                        if (error == 4000 || error == 5930) { // 充值
                            balance();
                        } else {
                            ErrorCode.toastError(mContext, ErrorCode.E_5804);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleDataFail(int e, long flag) {
        if (flag == FLAG_GET_USER_LIST) {
            mListView.onRefreshComplete();
            showWaitDialog(false);
            ErrorCode.toastError(mContext, e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommonFunction.hideInputMethod(mContext, mSearchEditText);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(flLleft)) {
            CommonFunction.hideInputMethod(mContext, mSearchEditText);
            setResult(Activity.RESULT_OK);
            finish();
        }

    }

    /**
     * @param isShow
     * @Title: showWaitDialog
     * @Description: 显示加载框
     */
    private void showWaitDialog(boolean isShow) {
        if (mProgressDialog != null) {
            if (isShow) {
                mProgressDialog.show();
            } else {
                mProgressDialog.hide();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

    }

    private TextWatcher filterTextWatch = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            new DataAdapter(mUserList).getFilter().filter(s.toString());
        }
    };

    /**
     * @author zhonglong kylin17@foxmail.com
     * @ClassName: DataAdapter
     * @Description: 数据适配器
     * @date 2013-12-11 下午2:24:13
     */
    class DataAdapter extends BaseAdapter implements Filterable {

        private final ArrayList<GroupSearchUser> userList;
        private GeoData userGeoData;

        public DataAdapter(ArrayList<GroupSearchUser> userList) {
            this.userList = userList;
            this.userGeoData = LocationUtil.getCurrentGeo(mContext);
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
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
                convertView = View.inflate(parent.getContext(),
                        R.layout.user_nearby_list_item2, null);//chat_contact_list_item2

//				viewHolder.userIcon = (HeadPhotoView) convertView
//						.findViewById( R.id.friend_icon );
//				viewHolder.tvNickName = (TextView) convertView
//						.findViewById( R.id.tvNickName );
//				viewHolder.tvAge = (TextView) convertView.findViewById( R.id.tvAge );
//				viewHolder.distance = (TextView) convertView.findViewById( R.id.tvDistance );
//				viewHolder.tvState = (TextView) convertView.findViewById( R.id.tvState );
//				viewHolder.tvSign = (TextView) convertView.findViewById( R.id.tvSign );
//				viewHolder.forbidImage = (ImageView) convertView
//						.findViewById( R.id.bannedIcon );
//				viewHolder.weiboIcon = (LinearLayout) convertView
//						.findViewById( R.id.llWeiboIcon );
//				convertView.setTag( viewHolder );

                viewHolder.userIcon = (HeadPhotoView) convertView
                        .findViewById(R.id.friend_icon);
                viewHolder.tvNickName = (TextView) convertView
                        .findViewById(R.id.tvNickName);
                viewHolder.tvAge = (TextView) convertView.findViewById(R.id.tvAge);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.tvDistance);
                viewHolder.tvState = (TextView) convertView.findViewById(R.id.tvState);
                viewHolder.tvSign = (TextView) convertView.findViewById(R.id.tvSign);
                viewHolder.forbidImage = (ImageView) convertView
                        .findViewById(R.id.bannedIcon);
                viewHolder.weiboIcon = (LinearLayout) convertView
                        .findViewById(R.id.llWeiboIcon);
                viewHolder.llSexAndAge = (LinearLayout) convertView.findViewById(R.id.info_center);
                viewHolder.ivSex = (ImageView) convertView.findViewById(R.id.iv_sex);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final GroupUser groupUser = userList.get(position).user;

            // 头像


//			viewHolder.userIcon.setOnHeadPhotoViewClick( new OnClickListener( )
//			{
//				@Override
//				public void onClick( View v )
//				{
//
//					if ( groupUser.isForbidUser( ) )
//					{
//						DialogUtil.showOneButtonDialog( mContext,
//							mContext.getString( R.string.dialog_title ),
//							mContext.getString( R.string.this_user_is_forbiden ),
//							mContext.getString( R.string.ok ), null );
//						return;
//					}
//
//					// 点击头像，跳转到用户资料
//					mLastClickUser = groupUser;
//					ProfileEntrance entrace =
//						mViewMode == 0 ? ProfileEntrance.MY_FRIEND : ProfileEntrance.MY_FOLLOW;
//
//					SpaceOther.launchUser( mContext, groupUser.userid,
//						groupUser.convertBaseToUser( ), ChatFromType.UNKONW );
//				}
//			} );

            viewHolder.userIcon.execute(ChatFromType.ContactsList, groupUser.convertBaseToUser(),
                    null);

//
//			// 昵称
//			String name = groupUser.getDisplayName( true );
//			if ( name == null || name.length( ) <= 0 )
//			{
//				name = String.valueOf( groupUser.userid );
//			}
//			SpannableString spName = FaceManager.getInstance( parent.getContext( ) )
//					.parseIconForString( viewHolder.tvNickName , parent.getContext( ) , name ,
//							20 );
//			viewHolder.tvNickName.setText( spName );
//
//			// SVIP
//			if ( groupUser.svip > 0 )
//			{
//				viewHolder.tvNickName.setTextColor( mContext.getResources( ).getColor(
//						R.color.c_ee4552 ) );
//			}
//			else
//			{
//				viewHolder.tvNickName.setTextColor( mContext.getResources( ).getColor(
//						R.color.c_333333 ) );
//			}
//
//			// 年龄
//			if ( groupUser.age <= 0 )
//			{
//				viewHolder.tvAge.setText( R.string.unknown );
//			}
//			else
//			{
//				viewHolder.tvAge.setText( String.valueOf( groupUser.age ) );
//			}
//
//			// 性别
//			int sexIcon = "m".equals( groupUser.gender ) ? R.drawable.z_common_male_icon
//					: R.drawable.z_common_female_icon;
//			viewHolder.tvAge.setCompoundDrawablesWithIntrinsicBounds( sexIcon , 0 , 0 , 0 );
//			viewHolder.tvAge
//					.setBackgroundResource( "m".equals( groupUser.gender ) ? R.drawable.z_common_male_bg
//							: R.drawable.z_common_female_bg );
//
//			String sign = groupUser.selftext;
//			SpannableString spSign = FaceManager.getInstance( parent.getContext( ) )
//					.parseIconForString( viewHolder.distance , parent.getContext( ) , sign ,
//							13 );
//			viewHolder.distance.setText( spSign );
//
//			// 距离和在线状态
//			int distance = -1;
//			String disText = "";
//			try
//			{
//				distance = LocationUtil.calculateDistance( userGeoData.getLng( ) ,
//						userGeoData.getLat( ) , groupUser.lng , groupUser.lat );
//			}
//			catch ( Exception e )
//			{
//
//			}
//			if ( distance < 0 )
//			{ // 不可知
//				// viewHolder.distance.setText(
//				// R.string.unable_to_get_distance
//				// );
//				disText = mContext.getString( R.string.unable_to_get_distance );
//			}
//			else
//			{
//				// viewHolder.distance.setText(
//				// CommonFunction.covertSelfDistance( distance ) );
//				disText = CommonFunction.covertSelfDistance( distance );
//			}
//
//			String time = TimeFormat.timeFormat1( viewHolder.tvState.getContext( ) ,
//					groupUser.lastonlinetime );
//			if ( time != null && time.length( ) > 0 )
//			{
//				// viewHolder.tvState.setText( time );
//			}
//			else
//			{
//				time = mContext.getString( R.string.unable_to_get_time );
//				// viewHolder.tvState.setText( R.string.unable_to_get_time );
//			}
//			viewHolder.tvState.setText( disText + " | " + time );
//			// viewHolder.forbidImage.setVisibility(
//			// groupUser.user.isForbidUser( ) ? View.VISIBLE
//			// : View.GONE );
//			viewHolder.forbidImage.setVisibility( View.GONE );

            // 签名
            // String infor = groupUser.user.getPersonalInfor(
            // viewHolder.tvSign.getContext( ) );
            // if ( infor != null )
            // {
            //
            // SpannableString spSign = FaceManager.getInstance(
            // parent.getContext( ) )
            // .parseIconForString( viewHolder.tvSign , parent.getContext( ) ,
            // infor , 13 );
            // viewHolder.tvSign.setText( spSign );
            // viewHolder.tvSign.setVisibility( View.GONE );
            // }

//			CommonFunction.showRightIcon( viewHolder.weiboIcon , groupUser.convertBaseToUser() ,
//					parent.getContext( ) );//jiqiang

//////////////////////////////////////
            // 头像
            viewHolder.userIcon.execute(groupUser.convertBaseToUser(), true);


            // 昵称
            String name = groupUser.getDisplayName(true);
            if (name == null || name.length() <= 0) {
                name = String.valueOf(groupUser.userid);
            }
            SpannableString spName = FaceManager.getInstance(parent.getContext())
                    .parseIconForString(viewHolder.tvNickName, parent.getContext(), name,
                            20);
            viewHolder.tvNickName.setText(spName);

            // 年龄
            if (groupUser.age <= 0) {
                viewHolder.tvAge.setText(R.string.unknown);
            } else {
                viewHolder.tvAge.setText(String.valueOf(groupUser.age));
            }
            // 性别
            int sex = groupUser.getSex();
            if (sex == 2) {
                viewHolder.ivSex.setImageResource(R.drawable.z_common_female_icon);
                viewHolder.llSexAndAge.setBackground(getResources().getDrawable(R.drawable.group_member_age_girl_bg));
            } else if (sex == 1) {
                viewHolder.ivSex.setImageResource(R.drawable.z_common_male_icon);
                viewHolder.llSexAndAge.setBackground(getResources().getDrawable(R.drawable.group_member_age_man_bg));
            }
            // 距离
            int distance = -1;
            try {
                distance = LocationUtil.calculateDistance(userGeoData.getLng(),
                        userGeoData.getLat(), groupUser.lng, groupUser.lat);
            } catch (Exception e) {

            }
            if (distance < 0) { // 不可知
                viewHolder.distance.setText(R.string.unable_to_get_distance);
            } else {
                viewHolder.distance.setText(CommonFunction.covertSelfDistance(distance));
            }

            // 在线状态
            String time = TimeFormat.timeFormat1(viewHolder.tvState.getContext(),
                    groupUser.lastonlinetime);
            if (time != null && time.length() > 0) {
                viewHolder.tvState.setText(time);
            } else {
                viewHolder.tvState.setText(R.string.unable_to_get_time);
            }

            viewHolder.forbidImage
                    .setVisibility(groupUser.isForbidUser() ? View.VISIBLE : View.GONE);

            viewHolder.tvSign.setText("");
            // 签名
            final String infor = groupUser.getPersonalInfor(FriendsAttentionActivity.this);
            if (infor != null && !"".equals(infor)) {
                SpannableString spSign = FaceManager.getInstance(parent.getContext())
                        .parseIconForString(viewHolder.tvSign, parent.getContext(), infor,
                                10);
                viewHolder.tvSign.setText(spSign);
                viewHolder.tvSign.setVisibility(View.VISIBLE);
//				if ( mViewType == 3 || mViewType == 4)
//				{
//					viewHolder.tvSign.setVisibility( View.VISIBLE );
//				}
//				else
//				{
//					viewHolder.tvSign.setVisibility( View.GONE );
//				}
            }
            /////////////////////////////////

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFromStore) {

                        if (groupUser.isForbidUser()) {
                            DialogUtil.showOneButtonDialog(mContext,
                                    mContext.getString(R.string.dialog_title),
                                    mContext.getString(R.string.this_user_is_forbiden),
                                    mContext.getString(R.string.ok), null);
                            return;
                        }

                        int relation = mViewMode == 0 ? 1 : 3;
                        User user = groupUser.convertBaseToUser();
                        user.setRelationship(relation);
                        ChatPersonal.skipToChatPersonal(mContext, user);

                    } else {
                        fUser = groupUser.convertBaseToUser();
                        sendGift(groupUser.userid, gift);
                    }
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    friendId = groupUser.userid;
                    onLongClickEvent(friendId);
                    return false;
                }
            });

            return convertView;
        }

        @Override
        public Filter getFilter() {
            FriendsFilter filter = new FriendsFilter();
            return filter;
        }
    }

    static class ViewHolder {
        public HeadPhotoView userIcon;
        public TextView tvNickName;
        public TextView tvAge;
        public TextView distance;
        public TextView tvState;
        public TextView tvSign;
        public ImageView forbidImage;
        public LinearLayout weiboIcon;
        public LinearLayout llSexAndAge;
        public ImageView ivSex;
    }

    class FriendsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = mSearchEditText.getText().toString();
            FilterResults result = new FilterResults();
            if (constraint != null && mSearchEditText.toString().length() > 0) {
                ArrayList<GroupSearchUser> copyList = new ArrayList<GroupSearchUser>();
                ArrayList<GroupSearchUser> filterList = new ArrayList<GroupSearchUser>();
                synchronized (this) {
                    copyList = mUserList;
                }

                for (int i = 0; i < copyList.size(); i++) {
                    GroupSearchUser user = copyList.get(i);
                    if (user.user.getDisplayName(true).toLowerCase()
                            .contains(constraint.toString().toLowerCase())
                            || (user.user.userid + "").contains(constraint.toString()
                            .toLowerCase())) {
                        filterList.add(user);
                    }
                }

                result.count = filterList.size();
                result.values = filterList;

            } else {
                synchronized (this) {
                    result.count = mUserList.size();
                    result.values = mUserList;
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<GroupSearchUser> friends = (ArrayList<GroupSearchUser>) results.values;
            mAdapter = new DataAdapter(friends);
            mListView.setAdapter(mAdapter);
            if (mAdapter.isEmpty()) {
                mEmptyLayout.showEmpty();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MSG_MODIFY_NOTE) {
            String noteName = data.getStringExtra("noteName");
            if (mLastClickUser != null) {
                mLastClickUser.notes = noteName;
            }
        }
    }

    private void onLongClickEvent(final long fUID) {
        // 系统用户不可以长按
        if (fUID <= 1000) {
            return;
        }
        DialogUtil.showTowButtonDialog(mContext, getResources().getText(R.string.prompt),
                getResources().getText(R.string.unfollowing_yes_or_not),
                getResources().getText(R.string.give_up),
                getResources().getText(R.string.space_other_cancel_att), null,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            // FLAG_UNFOLLOWING = FriendModel.getInstance(
                            // mContext )
                            // .cancalFollowReq( FriendsAttentionActivity.this ,
                            // fUID ,
                            // FriendsAttentionActivity.this );

                            FLAG_UNFOLLOWING = FriendHttpProtocol.userFanDislike(mContext,
                                    fUID, FriendsAttentionActivity.this);

                            showDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }

    private void showDialog() {
        hideDialog();
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(mContext, "",
                    mContext.getResources().getString(R.string.please_wait), null);
            return;
        }
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void deleteUser(long uid) {
        for (GroupSearchUser item : mUserList) {
            if (item.user.userid == uid) {
                mUserList.remove(item);
                break;
            }
        }

        FriendModel.getInstance(mContext).deleteFollow(uid);
        FriendModel.getInstance(mContext).deleteFriend(uid);

        // 如果要缓存，需要把缓存中的关系修改
        Me friend = UserBufferHelper.getInstance().read(uid);
        if (null != friend) {
            friend.setNoteName(null);
            friend.setRelationship(friend.isMyFriend() ? User.RELATION_FANS
                    : User.RELATION_STRANGER);
            friend.setFansNum(friend.getFansNum() - 1);
            UserBufferHelper.getInstance().save(friend);
        }

        mTotalNum = mTotalNum - 1;
        mTheMainHandler.sendEmptyMessage(MSG_WHAT_REFRESH_TITLE);
        // if ( mViewMode == 0 )
        // {
        // // 我的好友
        // mTitleName.setText( getString( R.string.all_friends ) + "(" +
        // mTotalNum + ")" );
        // }
        // else if ( mViewMode == 1 )
        // {
        // // 我的关注
        // mTitleName.setText( getString( R.string.all_attentions ) + "(" +
        // mTotalNum + ")" );
        // }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 根据来源编号获取来源字符串
     */
//	private String getContactString(int contact , int group )
//	{
//		String data = "";
//		switch ( contact )
//		{
//			case ContactType.DEFAULT_TYPE :
//				data = mContext.getString( R.string.by_iaround );
//				break;
//			case ContactType.SEARCH_BY_ID :
//				data = mContext.getString( R.string.by_id_search );
//				break;
//			case ContactType.SEARCH_BY_NICKNAME :
//				data = mContext.getString( R.string.by_nickname_search );
//				break;
//			case ContactType.NEARBY_PEOPLE :
//				data = mContext.getString( R.string.by_nearby );
//				break;
//			case ContactType.MAP_ROAM :
//				data = mContext.getString( R.string.by_map_roaming );
//				break;
//			case ContactType.GLOBAL_FOCUS :
//				data = mContext.getString( R.string.by_global_focus );
//				break;
//			case ContactType.NEARBY_FOCUS :
//				data = mContext.getString( R.string.by_near_focus );
//				break;
//			case ContactType.GROUP :
//				data = mContext.getString( R.string.by_group );
//				break;
//			case ContactType.FEATURED_PHOTOS :
//				data = mContext.getString( R.string.by_selected_photo );
//				break;
//			case ContactType.RANK :
//				data = mContext.getString( R.string.by_list );
//				break;
//			case ContactType.GAME_PLAYERS :
//				data = mContext.getString( R.string.by_game_player );
//				break;
//			case ContactType.ENCOUNTER :
//				data = mContext.getString( R.string.by_meet_game );
//				break;
//			case ContactType.RECENT_VISITORS :
//				data = mContext.getString( R.string.by_near_visit );
//				break;
//			case ContactType.CONTACTS_FRIEND :
//				data = mContext.getString( R.string.by_contact_friend );
//				break;
//			case ContactType.DEEPER_CONTACTS_FRIEND :
//				data = mContext.getString( R.string.by_contact_friend_contact_friend );
//				break;
//			case ContactType.CONTACTS_FRIEND_FRIEND :
//				data = mContext.getString( R.string.by_contact_friend_friend );
//				break;
//			case ContactType.FRIEND_OF_FRIEND :
//				data = mContext.getString( R.string.by_friend_friend );
//				break;
//			case ContactType.ANY_GAME_PLAYER :
//				data = mContext.getString( R.string.by_playing_same_game );
//				break;
//			default :
//				data = mContext.getString( R.string.by_iaround );
//				break;
//		}
//
//		if ( group == 0 )
//		{
//			data += mContext.getString( R.string.know_me );
//		}
//		else if ( group == 1 )
//		{
//			data += mContext.getString( R.string.focus_him );
//		}
//
//		return data;
//	}
    @Override
    protected void onResume() {
        super.onResume();
        getConnectorManage().setCallBackAction(this);
    }

    /**
     * 赠送礼物
     *
     * @param gift
     */
    private void sendGift(final long userid, final Gift gift) {

        if (!me.isVip() && gift.getVipLevel() == 1) {// 该礼物只有VIP才能发送
            DialogUtil.showTobeVipDialog(this, R.string.gift,
                    R.string.only_vip_can_send_this_gift);
            return;
        }

        DialogUtil.showOKCancelDialog(this, R.string.sendgift,
                R.string.sure_sendgift_to_he, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
                        try {// 赠送礼物
                            mProgressDialog = DialogUtil
                                    .showProgressDialog(
                                            mActivity,
                                            "",
                                            mContext.getResources().getString(
                                                    R.string.please_wait), null);
                            SpaceModel.getInstance(mContext).sendMineGiftReq(mActivity,
                                    userid, gift.getMid(), FriendsAttentionActivity.this);
                        } catch (Throwable e) {
                            hideDialog();
                            Toast.makeText(mContext, R.string.network_req_failed, Toast.LENGTH_LONG)
                                    .show();
                            CommonFunction.log(e);
                        }
                    }
                });
    }

    /**
     * 充值弹窗
     */
    private void balance() {
        DialogUtil.showTwoButtonDialog(mContext,
                mContext.getString(R.string.send_gift_failed),
                mContext.getString(R.string.insufficient_balance),
                mContext.getString(R.string.ok),
                mContext.getString(R.string.get_gold_coins), null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentPayBuyGlod.jumpPayBuyGlodActivity(mContext);
                    }
                });
    }

    /**
     * 当发送礼物消息成功之后，发送一条私聊消息
     */
    private void sendGiftMessage() {
        if (gift != null) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("giftname", gift.getName(this));
            map.put("charmnum", String.valueOf(gift.getCharisma()));
            map.put("price", String.valueOf(gift.getPrice()));
            map.put("giftnum", 1);
            map.put("exp", gift.expvalue);
            map.put("currencytype", String.valueOf(gift.getCurrencytype()));
            String content = JsonUtil.mapToJsonString(map);
            mReqSendMsgFlag = System.currentTimeMillis();

            ischat = ChatPersonalModel.getInstance().getAccostRelation(mContext,
                    me.getUid(), fUser.getUid());
            CommonFunction.log("", "--->ischat == " + ischat);
            String ChatRecordType = String.valueOf(ChatRecordViewFactory.GIFE_REMIND);
            long flag = SocketSessionProtocol.sessionPrivateMsg(this, mReqSendMsgFlag,
                    fUser.getUid(), ischat == 1 ? 0 : 1, ChatRecordType,
                    gift.getIconUrl(), from, content);

            if (flag == -1) {
                mTheMainHandler.sendEmptyMessage(MSG_WHAT_REFRESH_MINE_SOCKET_SEND);
                return;
            }

            Me me = Common.getInstance().loginUser;
            mChatRecord = new ChatRecord();
            mChatRecord.setId(-1); // 消息id
            mChatRecord.setUid(me.getUid());
            mChatRecord.setNickname(me.getNickname());
            mChatRecord.setIcon(me.getIcon());
            mChatRecord.setVip(me.getViplevel());
            mChatRecord.setSVip(me.getSVip());
            mChatRecord.setDatetime(mReqSendMsgFlag);
            mChatRecord.setType(Integer.toString(6));
            mChatRecord.setStatus(ChatRecordStatus.SENDING); // 发送中
            mChatRecord.setAttachment(gift.getIconUrl());
            mChatRecord.setContent(content);
            mChatRecord.setUpload(false);
        }
    }


    @Override
    public void onReceiveMessage(TransportMessage message) {
        if (message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC
                || message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT_FAIL) {
            String json = message.getContentBody();
            SocketSuccessResponse response = GsonUtil.getInstance().getServerBean(json,
                    SocketSuccessResponse.class);

            if (response.flag == mReqSendMsgFlag) {
                if (message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC) { // 发送成功
                    mChatRecord.setId(response.msgid);
                    mChatRecord.setStatus(ChatRecordStatus.ARRIVED); // 已送达

                    int subGroup;
                    if (ischat == 1) {
                        subGroup = SubGroupType.NormalChat;
                    } else {
                        subGroup = SubGroupType.SendAccost;
                    }
                    ChatPersonalModel.getInstance().insertOneRecord(mActivity, fUser,
                            mChatRecord, subGroup, from);
                }
            }
            mTheMainHandler.sendEmptyMessage(MSG_WHAT_REFRESH_MINE_SOCKET_SEND);

        }
        super.onReceiveMessage(message);
    }

    private Handler mTheMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT_REFRESH_MINE_SOCKET_SEND) {// 赠送礼物私聊socket结果
                hideDialog();
                sendSuccessTip();
            } else if (msg.what == MSG_WHAT_REFRESH_TITLE) {
                showTitle();
            }
        }
    };

    /**
     * 赠送礼物私聊socket结果
     */
    private void sendSuccessTip() {

        haveSentDialog = DialogUtil.showOKDialog(mContext, R.string.chat_personal_send_gift,
                R.string.gift_sent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        haveSentDialog = null;
                        if (diamondCanTalkDialog == null) {
                            Intent data = new Intent();
                            data.putExtra("giftId", gift.getMid());
                            setResult(Activity.RESULT_OK, data);
                            finish();
                        }
                    }
                });
    }
}
