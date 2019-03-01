
package net.iaround.ui.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView.PinnedSectionListAdapter;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.Visitor;
import net.iaround.model.entity.VisitorUser;
import net.iaround.model.entity.VisitorsBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.Me;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.BasePinnedItem;
import net.iaround.ui.view.HeadPhotoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 最近来访
 */
public class RecentVisitorActivity extends BaseFragmentActivity implements HttpCallBack, OnClickListener {
    private String Uid = "";
    private View mActionBar;
    private int distanceInt = 0;
    private Me user;
    private VisitorAdapter mVisitorAdapter;
    private Dialog mProgressDialog;// 进度条
    private User mFriend;// 当前查看的用户资料实体
    private static final int MSG_SHOW_ERROR = -1; // 提示错误
    private static final int MSG_ADD_FOLLOW = 2; // 关注
    private VisitorUser currentVisitorUser = new VisitorUser();
    private PullToRefreshPinnedSectionListView mPullToRefreshPinnedListView;
    private long mGetVisitorListHttpFlag, mClearVisitorHttpFlag, mAddFollowHttpFlag;
    private ArrayList<BasePinnedItem<Visitor>> mVisitors = new ArrayList<BasePinnedItem<Visitor>>();

    //标题栏
    private TextView tvTitle, tvRight;
    private ImageView ivLeft;
    private FrameLayout flLeft;

    //空布局
    private RelativeLayout mEmptyLayout1;
    private EmptyLayout mEmptyLayout;
    private ImageView ivEmpty;
    private TextView tvEmpty;

    public static void launch(Context context, long uid) {
        context.startActivity(new Intent(context, RecentVisitorActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iaround_activity_recent_visitor);
        Uid = String.valueOf(Common.getInstance().loginUser.getUid());
        user = Common.getInstance().loginUser;

        initView();
        setListener();

        // 最近来访有新数据时从服务端获取，否则获取缓存展示
        if (Common.getInstance().loginUser.isHasNewVisitors()) {
            mPullToRefreshPinnedListView.setRefreshing();
            requestData();
        } else {
            String data = SharedPreferenceCache.getInstance(mContext).getString("Recent_Visitor" + Uid);
            if (!CommonFunction.isEmptyOrNullStr(data)) {
                VisitorsBean bean = new VisitorsBean();
                bean = GsonUtil.getInstance().getServerBean(data, VisitorsBean.class);
                refreshListView(bean.visits);
            } else {
                mPullToRefreshPinnedListView.setRefreshing();
                requestData();
            }
        }
        Common.getInstance().loginUser.setHasNewVisitors(false);

    }

    private void initView() {
        mPullToRefreshPinnedListView = (PullToRefreshPinnedSectionListView) findViewById(R.id.recent_visitor);
        mPullToRefreshPinnedListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头

        mActionBar = findViewById(R.id.abTitle);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);

        tvRight.setVisibility(View.VISIBLE);
        ivLeft.setVisibility(View.VISIBLE);

        tvTitle.setText(getResources().getString(R.string.user_fragment_visitor));
        tvRight.setText(getResources().getString(R.string.empty));
//        tvRight.setTextColor(getResources().getColor(R.color.chat_update_message_count));
//        tvRight.setTextColor(getResources().getColor(R.color.chat_update_chatInput_hintTextColor));
        ivLeft.setImageResource(R.drawable.title_back);

        mEmptyLayout = new EmptyLayout(RecentVisitorActivity.this,
                mPullToRefreshPinnedListView.getRefreshableView());
        mEmptyLayout1 = (RelativeLayout) findViewById(R.id.empty);
        ivEmpty = (ImageView) findViewById(R.id.iv_no_friends);
        tvEmpty = (TextView) findViewById(R.id.nullStr);

        mEmptyLayout.setEmptyMessage("");
        ivEmpty.setImageResource(R.drawable.contacts_no_fans);
        tvEmpty.setText(getString(R.string.recent_constacts));
        mEmptyLayout.showEmpty();
    }


    /**
     * 设置监听
     */
    private void setListener() {
        mPullToRefreshPinnedListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData();
            }
        });
        ivLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        mEmptyLayout1.setOnClickListener(this);
    }

    /**
     * 清空来访记录
     */
    private void clearData() {
        if (mVisitors == null || mVisitors.isEmpty()) {
            return;
        }
        // 弹出dialog，真的要清空么？
        DialogUtil.showOKCancelDialog(this, R.string.dialog_title,
                R.string.clear_recent_visitor, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearDataForServer();
                    }
                });
    }

    /**
     * 清空最近来访数据
     */
    private void clearDataForServer() {
        try {
            mClearVisitorHttpFlag = UserHttpProtocol.removeVisits(mActivity, this);
        } catch (Throwable e) {// 清空失败
            clearVisitorFail();
        }
    }


    /**
     * 请求来访记录数据
     */
    private void requestData() {
        try {
            mGetVisitorListHttpFlag = UserHttpProtocol.userVisits(mActivity, this);
        } catch (Throwable e) {
            mPullToRefreshPinnedListView.onRefreshComplete();
        }
    }


    /**
     * 处理清空来访记录
     *
     * @param result
     */
    private void parseClearVisitorResult(String result) {
        BaseServerBean serverResult = GsonUtil.getInstance().getServerBean(result,
                BaseServerBean.class);
        if (serverResult.isSuccess()) {// 清除成功
            clearVisitorSuccess();
        } else {// 清除失败
            clearVisitorFail();
        }
    }

    /**
     * 清空来访记录成功
     */
    private void clearVisitorSuccess() {
        Toast.makeText(mActivity, R.string.clear_recent_visitor_success, Toast.LENGTH_SHORT)
                .show();
        mVisitors.clear();
        refreshListView(null);
        SharedPreferenceCache.getInstance(mContext).putString("Recent_Visitor" + Uid, "");
    }

    /**
     * 清空来访记录失败
     */
    private void clearVisitorFail() {
        Toast.makeText(mActivity, R.string.clear_recent_visitor_fail, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 数据请求失败
     *
     * @param result
     */
    private void refreshListViewFail(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ErrorCode.showError(RecentVisitorActivity.this, result);
                mPullToRefreshPinnedListView.onRefreshComplete();
            }
        });
    }


    /**
     * 请求数据成功，刷新页面
     *
     * @param visitors
     */
    private void refreshListView(List<Visitor> visitors) {
        if (visitors == null || visitors.isEmpty()) {
//            mEmptyLayout.showEmpty();
            mEmptyLayout1.setVisibility(View.VISIBLE);
            mPullToRefreshPinnedListView.setAdapter(null);
            mVisitorAdapter = null;
            return;
        }

        Map<String, ArrayList<Visitor>> visitorMap = new LinkedHashMap<String, ArrayList<Visitor>>();
        for (Visitor visitor : visitors) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
            String todayTime = sdf.format(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String yesterdayTime = sdf.format(cal.getTime());
            String startTime = sdf.format(visitor.datetime);
            if (todayTime.equals(startTime)) {
                startTime = getString(R.string.today);
            } else if (yesterdayTime.equals(startTime)) {
                startTime = getString(R.string.yesterday);
            }

            if (!visitorMap.containsKey(startTime)) {
                visitorMap.put(startTime, new ArrayList<Visitor>());
            }

            visitorMap.get(startTime).add(visitor);
        }

        mVisitors.clear();
        Iterator<Entry<String, ArrayList<Visitor>>> visitorIterator = visitorMap.entrySet()
                .iterator();
        while (visitorIterator.hasNext()) {
            Entry<String, ArrayList<Visitor>> visitorEntry = visitorIterator.next();
            mVisitors.add(new BasePinnedItem<Visitor>(BasePinnedItem.GROUP, visitorEntry
                    .getKey(), null));
            ArrayList<Visitor> value = visitorEntry.getValue();
            for (Visitor visitor : value) {
                mVisitors.add(new BasePinnedItem<Visitor>(BasePinnedItem.CONTENT, null,
                        visitor));
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshPinnedListView.onRefreshComplete();
                if (mVisitorAdapter == null) {
                    mVisitorAdapter = new VisitorAdapter();
                    mPullToRefreshPinnedListView.setAdapter(mVisitorAdapter);
                    mContext=RecentVisitorActivity.this;
                } else {
                    mVisitorAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            dissmissDialog();

            if (msg.obj == null)
                return;

            switch (msg.what) {
                case MSG_SHOW_ERROR:
                    if (msg.obj != null) {
                        if (!TextUtils.isEmpty(msg.obj.toString())) {
                            ErrorCode.showError(mContext, msg.obj.toString());
                        }
                    }
                    break;

                // case MSG_ADD_FOLLOW :
                // addFollow( );
                // break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                clearData();
                break;
            case R.id.empty:
                requestData();
                break;
        }
    }


    // /**
    // * 添加好友关注
    // */
    // private void addFollow( )
    // {// relation 0:自己 ，1：好友 ，2：陌生人 3、关注 4、粉丝 5推荐
    // mFriend.setRelationship( mFriend.isMyFans( ) ? User.RELATION_FRIEND
    // : User.RELATION_FOLLOWING );
    // mFriend.setFansNum( mFriend.getFansNum( ) + 1 );
    // mFriend.getRelationLink( ).middle.contact = ChatFromType.RecentVisit;
    // refreshFollowStateUI( mFriend.getRelationship( ) );
    // if ( mFriend.isMyFriend( ) )
    // {
    // FriendModel.getInstance( mActivity ).updateFriendList( mFriend );
    // }
    // else
    // {
    // FriendModel.getInstance( mActivity ).addFollow( mFriend );
    // }
    // }
    //
    //
    // /**
    // * 更改关注按钮状态
    // *
    // * @param relation
    // */
    // private void refreshFollowStateUI( int relation )
    // {
    // for ( BasePinnedItem< Visitor > i : mVisitors )
    // {
    // if ( i.data != null )
    // {
    // if ( i.data.user.userid == currentVisitorUser.userid )
    // {
    // i.data.relation = relation;
    // }
    // }
    // }
    //
    // CommonFunction.toastMsg( mContext , R.string.follow_suc );
    // mVisitorAdapter.notifyDataSetChanged( );
    // }

    private class VisitorAdapter extends BaseAdapter implements PinnedSectionListAdapter,
            OnClickListener {

        @Override
        public int getCount() {
            return mVisitors.size();
        }

        @Override
        public BasePinnedItem<Visitor> getItem(int position) {
            return mVisitors.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (getItem(position).data != null) {
                return getItem(position).data.user.userid;
            } else {
                return 0;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);

            if (type == BasePinnedItem.GROUP) {
                convertView = View.inflate(RecentVisitorActivity.this, R.layout.x_common_pinneditem_head, null);
                convertView.setClickable(false);
            } else {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.visitor_list_item,
                            null);
                }
            }

            BasePinnedItem<Visitor> item = getItem(position);
            if (type == BasePinnedItem.GROUP) {
                TextView headerText = (TextView) convertView.findViewById(R.id.header_text);
                headerText.setText(item.title);
            } else {
                View divider = convertView.findViewById(R.id.divider);
                if (position == 0) {
                    divider.setVisibility(View.GONE);
                } else {
                    int lastType = getItemViewType(position - 1);
                    if (lastType == BasePinnedItem.GROUP) {
                        divider.setVisibility(View.GONE);
                    } else {
                        divider.setVisibility(View.VISIBLE);
                    }
                }

                final VisitorUser visitorUser = item.data.user;
                convertView.setTag(visitorUser);
                convertView.setOnClickListener(this);

                // 头像
                final HeadPhotoView icon = (HeadPhotoView) convertView.findViewById(R.id.friend_icon);


                icon.setTag(visitorUser);
                icon.execute(ChatFromType.UNKONW, visitorUser.convertBaseToUser(), null, true);
                icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.getInstance().loginUser.isVip()) {
                            Intent intent = new Intent(getApplicationContext(), OtherInfoActivity.class);
                            intent.putExtra("uid", visitorUser.userid);
                            intent.putExtra("user", visitorUser.convertBaseToUser());
                            intent.putExtra("from", 0);
                            startActivity(intent);

                        } else {
                            DialogUtil
                                    .showTobeVipDialog(mContext, R.string.menu_title_visitor, R.string.svip_can_see_visitor
                                    );
                        }
                    }
                });


                // 昵称
                TextView tvNickName = (TextView) convertView.findViewById(R.id.tvNickName);
                String name = visitorUser.notes;

                if (Common.getInstance().loginUser.isVip()) {
                    if (CommonFunction.isEmptyOrNullStr(name.trim())) {
                        name = String.valueOf(visitorUser.nickname);
                    }
                    SpannableString spName = FaceManager.getInstance(parent.getContext())
                            .parseIconForString(tvNickName, parent.getContext(), name, 13);
                    tvNickName.setText(spName);
                } else {
                    tvNickName.setText(mContext.getString(R.string.recent_visitor));
                }

                //vip
                if (visitorUser.svip > 0) {//仅为svip
                    tvNickName.setTextColor(Color.parseColor("#ee4552"));
                } else {//非vip
                    tvNickName.setTextColor(Color.parseColor("#333333"));
                }

                // 年龄
                RelativeLayout rlSexAge = (RelativeLayout) convertView.findViewById(R.id.rlSexAge);
                ImageView ivSex = (ImageView) convertView.findViewById(R.id.ivSex);
                TextView tvAge = (TextView) convertView.findViewById(R.id.tvAge);

                if (visitorUser.age <= 0) {
                    if ("m".equals(visitorUser.gender)) {
                        rlSexAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
//                        ivSex.setImageResource(R.drawable.z_common_female_icon);
                        ivSex.setImageResource(R.drawable.thread_register_man_select);
                    } else {
                        rlSexAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                        ivSex.setImageResource(R.drawable.thread_register_woman_select);
                    }
                    tvAge.setText(R.string.unknown);
                } else {
                    if ("m".equals(visitorUser.gender)) {
                        rlSexAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
//                        ivSex.setImageResource(R.drawable.z_common_female_icon);
                        ivSex.setImageResource(R.drawable.thread_register_man_select);
                    } else {
                        rlSexAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                        ivSex.setImageResource(R.drawable.thread_register_woman_select);
                    }
                    tvAge.setText(String.valueOf(visitorUser.age));
                }

                // 性别
//				int sex = "m".equals( visitorUser.gender ) ? 1 : 2;
//				if ( sex <= 0 )
//				{
//					tvAge.setBackgroundResource( R.drawable.z_common_female_bg );
//					tvAge.setCompoundDrawablesWithIntrinsicBounds( R.drawable.z_common_female_icon ,
//							0 , 0 , 0 );
//				}
//				else
//				{
//					int sexIcon = sex == 1 ? R.drawable.z_common_male_icon
//							: R.drawable.z_common_female_icon;
//					tvAge.setCompoundDrawablesWithIntrinsicBounds( sexIcon , 0 , 0 , 0 );
//					tvAge.setBackgroundResource( sex == 1 ? R.drawable.z_common_male_bg
//							: R.drawable.z_common_female_bg );
//				}

                // 距离
                TextView distance = (TextView) convertView.findViewById(R.id.tvDistance);
                int showDistance = visitorUser.distance;
                if (showDistance < 0) { // 不可知
                    distance.setText(R.string.unable_to_get_distance);
                } else {
                    distance.setText(" · " + CommonFunction.covertSelfDistance(showDistance));
                }
                // if ( mGeoData == null || visitorUser.lat <= 0 ||
                // visitorUser.lng <= 0 )
                // { // 不可知
                // distance.setText( R.string.unable_to_get_distance );
                // }
                // else
                // {
                // distanceInt = LocationUtil.calculateDistance(
                // mGeoData.getLng( ) ,
                // mGeoData.getLat( ) , visitorUser.lng , visitorUser.lat );
                // distance.setText( CommonFunction.covertSelfDistance(
                // distanceInt ) );
                // }

                // 来访时间
                TextView tvState = (TextView) convertView.findViewById(R.id.tvState);
                String time = TimeFormat.timeFormat2(tvState.getContext(), item.data.datetime);
                if (time != null && time.length() > 0) {
                    tvState.setText(time);
                } else {
                    tvState.setText(R.string.unable_to_get_time);
                }

                // 签名
                String infor = visitorUser.selftext;
                if (CommonFunction.isEmptyOrNullStr(infor)) {
                    infor = getString(R.string.make_great_think);
                }
                TextView tvSign = (TextView) convertView.findViewById(R.id.tvSign);
                tvSign.setText("");
                if (!"".equals(infor)) {

                    tvSign.setVisibility(View.VISIBLE);
                    SpannableString spSign = FaceManager.getInstance(parent.getContext())
                            .parseIconForString(tvSign, parent.getContext(), infor, 13);
                    tvSign.setText(spSign);
                }
                // 微博
//				LinearLayout weiboIcon = ( LinearLayout ) convertView
//						.findViewById( R.id.llWeiboIcon );
//				CommonFunction.showRightIcon( weiboIcon , User.parseWeiboStr( visitorUser.weibo ) ,
//						parent.getContext( ) );

                ImageView[] weibos = new ImageView[6];
                weibos[0] = (ImageView) convertView.findViewById(R.id.weibos_icon_1);
                weibos[1] = (ImageView) convertView.findViewById(R.id.weibos_icon_2);
                weibos[2] = (ImageView) convertView.findViewById(R.id.weibos_icon_3);
                weibos[3] = (ImageView) convertView.findViewById(R.id.weibos_icon_4);
                weibos[4] = (ImageView) convertView.findViewById(R.id.weibos_icon_5);
                weibos[5] = (ImageView) convertView.findViewById(R.id.weibos_icon_6);
                CommonFunction.showWeibosIcon(weibos, User.parseWeiboStr(visitorUser.weibo),
                        visitorUser.occupation, parent.getContext());


            }
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return mVisitors.get(position).groupType;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == BasePinnedItem.GROUP;
        }

        @Override
        public void onClick(View v) {
            if (Common.getInstance().loginUser.isVip()) {
                Object obj = v.getTag();
                if (obj != null && obj instanceof VisitorUser) {
                    VisitorUser visitorUser = (VisitorUser) obj;
                    //YC
//                    Intent intent = new Intent(mContext, OtherInfoActivity.class);
                    Intent intent = new Intent(BaseApplication.appContext,OtherInfoActivity.class);
                    intent.putExtra("uid", visitorUser.userid);
                    intent.putExtra("user", visitorUser.convertBaseToUser());
                    intent.putExtra("from", 0);
                    startActivity(intent);
//				SpaceOther.launchUser( mActivity, visitorUser.userid, visitorUser.convertBaseToUser( ), ChatFromType.RecentVisit );//jiqiang查看用户资料没做
                }
            } else {
                DialogUtil
                        .showTobeVipDialog(mContext, R.string.menu_title_visitor, R.string.svip_can_see_visitor
                        );
            }
        }
    }


    /**
     * 隐藏Dialog
     */
    private void dissmissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
//		super.onGeneralError( e , flag );
        dissmissDialog();
        if (flag == mGetVisitorListHttpFlag) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshPinnedListView.onRefreshComplete();
                }
            });
        } else if (flag == mClearVisitorHttpFlag) {
            clearVisitorFail();
        }
        // else if ( flag == mAddFollowHttpFlag )
        // {
        // Toast.makeText( mActivity , R.string.network_req_failed ,
        // Toast.LENGTH_SHORT ).show( );
        // }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
//		super.onGeneralSuccess( result , flag );
        if (flag == mGetVisitorListHttpFlag) {
            VisitorsBean bean = GsonUtil.getInstance().getServerBean(result,
                    VisitorsBean.class);
            if (bean.isSuccess()) {
                refreshListView(bean.visits);
                // 缓存最近来访数据
                SharedPreferenceCache.getInstance(mContext).putString("Recent_Visitor" + Uid,
                        result);
            } else {
                refreshListViewFail(result);
            }
        } else if (flag == mClearVisitorHttpFlag) {
            parseClearVisitorResult(result);
        }
        // else if ( flag == mAddFollowHttpFlag )
        // {
        // BaseServerBean serverResult = GsonUtil.getInstance( ).getServerBean(
        // result ,
        // BaseServerBean.class );
        // if ( serverResult.status != 200 && serverResult.status != 5701 )
        // {
        // mMainHandler.sendMessage( mMainHandler.obtainMessage( MSG_SHOW_ERROR
        // , result ) );
        // }
        // else
        // {
        // mMainHandler.sendMessage( mMainHandler.obtainMessage( MSG_ADD_FOLLOW
        // , result ) );
        // }
        // }
    }

    protected String getResString(int id) {
        return getResources().getString(id);
    }

}
