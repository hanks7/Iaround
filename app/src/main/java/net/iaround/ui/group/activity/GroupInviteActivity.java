
package net.iaround.ui.group.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.database.FriendModel;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.friend.bean.FriendUsersBean;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author shifengxiong
 * @ClassName: GroupInviteActivity
 * @Description: 邀请好友
 * @date
 */
public class GroupInviteActivity extends BaseFragmentActivity implements OnClickListener, HttpCallBack {

    /**
     * 邀请好友加入圈子
     */
    private long FLAG_GET_USER_LIST;

    private long FLAG_INVITE_FRIENDS;

    private PullToRefreshListView mListView;
    private DataAdapter adapter;
    private static final int STOP_PULLING = 100;
    private static final int INVIT_PRIENDS = 101;
    private static final int INVIT_ERROR = 102;
    private static final int ERROR = 103;
    private static final int SHOW_DATA_FROM_MODLE = 104;
    private boolean isFirst = true;
    //标题栏
    private TextView tvTitle;
    private ImageView tvRightText, mIvLeft;
    private FrameLayout flLeft;

    //加载好友进度框
    private ProgressDialog progressDialog;

    private String mGroupId = "";

    private ArrayList<FriendUsersBean.UserAndDistance> showList = new ArrayList<>();

    private ArrayList<Boolean> checkboxUsers = new ArrayList<>();

    private int pageno = 1;
    private final int PAGE_SIZE = 20;
    private int amount = 0;
    private int mTotalPage = 1;


    public static void launch(Context context, String groupId) {
        Intent intent = new Intent();
        intent.setClass(context, GroupInviteActivity.class);
        intent.putExtra("group_id", groupId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);

        initViews();
        setListeners();
        init();
    }

    /**
     * @Title: initViews
     * @Description: 初始化所有控件
     */
    private void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.group_chat_invite_title);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        tvRightText = (ImageView) findViewById(R.id.iv_right);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);

        mIvLeft.setVisibility(View.VISIBLE);
        tvRightText.setVisibility(View.VISIBLE);

        mIvLeft.setImageResource(R.drawable.title_back);
        tvRightText.setImageResource(R.drawable.icon_publish);

        progressDialog = new ProgressDialog(this);

        mListView = ((PullToRefreshListView) findViewById(R.id.set_invisible_list));
        mListView.setMode(Mode.PULL_FROM_START);
        mListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mListView.getRefreshableView().setDividerHeight(0);
        mListView.getRefreshableView().setSelector(R.drawable.info_bg_center_selector);
        mListView.getRefreshableView().setFastScrollEnabled(false);

        if (adapter == null) {
            adapter = new DataAdapter();
            mListView.setAdapter(adapter);
        }

    }

    /**
     * @Title: setListeners
     * @Description: 初始化监听器
     */
    private void setListeners() {
        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.iv_right).setOnClickListener(this);

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageno = 1;
                request(pageno);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (pageno < mTotalPage)
//                {
//                    ++pageno;
//                    request(pageno);
//                }else
//                {
//                    mListView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            CommonFunction.toastMsg(GroupInviteActivity.this, R.string.no_more_data);
//                            mListView.onRefreshComplete();
//                        }
//                    },200);
//                }
            }
        });
    }

    private void init() {
        mGroupId = getIntent().getStringExtra("group_id");
        request(pageno);
    }

    /**
     * 邀请好友
     */
    private void inviteFriends() {
        String userIds = "";
        for (int i = 0; i < checkboxUsers.size(); i++) {
            if (checkboxUsers.get(i)) {
                if (!CommonFunction.isEmptyOrNullStr(userIds)) {
                    userIds += ",";
                }
                userIds += showList.get(i).user.userid;
            }
        }
        if (TextUtils.isEmpty(userIds)) {
            finish();
        } else {
//            FLAG_INVITE_FRIENDS = GroupHttpProtocol.getGroupInviteFriends(mContext, mGroupId, userIds, this);
            FLAG_INVITE_FRIENDS = GroupHttpProtocol.chatbarInviteUser(mContext, mGroupId, userIds, this);
        }
    }

    private void request(int pageno) {
        try {
//            progressDialog.show();
//            FLAG_GET_USER_LIST = FriendHttpProtocol.userAttentions(BaseApplication.appContext, Common.getInstance().loginUser.getUid()
//                    ,pageno,PAGE_SIZE, this);
            FLAG_GET_USER_LIST = FriendHttpProtocol.friendsGet(BaseApplication.appContext,this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 主线程做UI处理
     */
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case STOP_PULLING://停止刷新
                    if (mListView != null)
                        mListView.onRefreshComplete();
                    break;
                case SHOW_DATA_FROM_MODLE://展示数据
                break;
                case INVIT_PRIENDS:
                    DialogUtil.showOKDialog(mContext,
                            getResources().getString(R.string.prompt), getResources()
                                    .getString(R.string.group_chat_invite_dialog),
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    finish();
                                }
                            });
                    break;
                case INVIT_ERROR:
                    ErrorCode.showError(mContext, (String) msg.obj);
                    break;
                case ERROR:
                    ErrorCode.toastError(mContext, 101);
                    break;
            }
            super.handleMessage(msg);
        }

    };
    @Override
    public void onGeneralError(int e, long flag) {
        if (mListView != null)
            mListView.onRefreshComplete();

        handler.sendEmptyMessage(ERROR);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        handler.sendEmptyMessage(STOP_PULLING);
        if (result == null)
            return;
        if (flag == FLAG_GET_USER_LIST) {//获取好友列表
//            progressDialog.dismiss();
            FriendUsersBean bean = GsonUtil.getInstance().getServerBean(result,
                    FriendUsersBean.class);
            if (bean == null)
                return;
            if (bean.isSuccess()) {
                showList.clear();
//                pageno = bean.pageno;
//                amount = bean.amount;
//                //上拉加载下拉刷新
//                if (pageno == 1)
//                {//下拉刷新
//
//                }else
//                {
//                    if (amount % PAGE_SIZE == 0)
//                    {
//                        mTotalPage = amount / PAGE_SIZE;
//                    }else
//                    {
//                        mTotalPage = amount / PAGE_SIZE + 1;
//                    }
//                }
                for (int i = 0; i < bean.users.size(); i++) {
                    // 因为下发下来的user中的distance都为0，所以要赋值进去
                    bean.users.get(i).user.distance = bean.users.get(i).distance;
                    showList.add(bean.users.get(i));
                }
                for (int i = 0; i < bean.users.size(); i++) {
                    checkboxUsers.add(false);
                }
                adapter.updateData(showList);

                if (showList == null || showList.size() <= 0) {
                    DialogUtil.showOKDialog(mContext, R.string.prompt,
                            R.string.group_chat_invite_no_friend, null);
                }
            }
        } else if (flag == FLAG_INVITE_FRIENDS) {//邀请好友加入
            if (Constant.isSuccess(result)) {
                handler.sendEmptyMessage(INVIT_PRIENDS);
            } else {
                Message msg = new Message();
                msg.what = INVIT_ERROR;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.iv_right:
                inviteFriends();
                break;

        }
    }

    public class DataAdapter extends BaseAdapter {

        public void updateData(ArrayList<FriendUsersBean.UserAndDistance> userList)
        {
            showList = userList;
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return showList.size();
        }

        @Override
        public User getItem(int position) {
            return showList.get(position).user.convertBaseToUser();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(GroupInviteActivity.this).inflate(R.layout.invisible_setting_user_item,parent,false);
            }
            final User groupUser = getItem(position);
            // 头像
            HeadPhotoView icon = (HeadPhotoView) convertView.findViewById(R.id.icon);


            icon.setOnHeadPhotoViewClick(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction.log("group", "mGroupId***" + mGroupId);
                }
            });

            icon.execute(ChatFromType.UNKONW, groupUser, null);


            // 名字
            ((TextView) convertView.findViewById(R.id.name)).setText(FaceManager
                    .getInstance(mContext).parseIconForString(
                            (TextView) convertView.findViewById(R.id.name), mContext,
                            getItem(position).getNoteName(true), 18));

            // 选中
            CheckBox check = (CheckBox) convertView.findViewById(R.id.check_box);


            check.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkboxUsers.get(position))
                    {
                        checkboxUsers.set(position,false);
                    }else
                    {
                        checkboxUsers.set(position,true);
                    }
                    notifyDataSetChanged();
                }
            });

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkboxUsers.get(position))
                    {
                        checkboxUsers.set(position,false);
                    }else
                    {
                        checkboxUsers.set(position,true);
                    }
                    notifyDataSetChanged();
                }
            });
            check.setChecked(checkboxUsers.get(position));
            return convertView;
        }
    }

}
