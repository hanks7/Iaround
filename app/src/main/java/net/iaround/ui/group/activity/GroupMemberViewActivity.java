
package net.iaround.ui.group.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView.PinnedSectionListAdapter;
import com.nostra13.universalimageloader.utils.L;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.enums.ProfileEntrance;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.GroupUser;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.dialog.CustomManagerMemberDia;
import net.iaround.utils.eventbus.NickNameNotifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/***
 * 圈成员页面
 */
public class GroupMemberViewActivity extends GroupHandleActivity implements OnClickListener {

    /**
     * 是否圈子转让
     */
    private boolean mIsGroupTransfer = false;
    /**
     * 返回按钮
     */
    private ImageView mTitleBack;
    private FrameLayout flLeft;
    private TextView mTitleRight;
    /**
     * 标题
     */
    private TextView mTitleName;
    /**
     * 更多
     */
    private TextView mTitleManage;

    /**
     * 成员列表
     */
    private PullToRefreshPinnedSectionListView mMemberListView;

    /**
     * 加载框
     */
    private Dialog mWaitDialog;

    /**
     * 用户列表的Bean
     */
    private GroupMemberSearchBean mGroupUserBean;

    /**
     * 数据列表
     */
    private ArrayList<GroupSearchUser> mMemberList = new ArrayList<GroupSearchUser>();

    /**
     * 数据适配器
     */
    private DataAdapter mAdapter;

    /**
     * 圈子id
     */
    private String mGroupId;

    /**
     * 用户角色
     */
    private int mGroupRole;

    /**
     * 圈子创建者
     */
    private GroupUser user;

    /**
     * 圈子名称
     */
    private String mGroupName;

    /**
     * 创建者Id
     */
    private long createId;

    /**
     * 每页数
     */
    private int PAGE_SIZE = 20;
    /**
     * 当前页数
     */
    private int mCurrentPage = 0;
    /**
     * 总页数
     */
    private int mTotalPage = 0;

    private long FLAG_GET_MEMBER_LIST;

    private final int MENU_MEMBER_MANAGER = 1;
    private final int MENU_MEMBER_SEARCH = 2;
    private final int MENU_FORBIDDEN_USER = 3;
    private final int MENU_CANCEL = 4;

    private final int REQUEST_CODE_GROUP_MEMBER = 10;
    private final int REQUEST_CODE_GROUP_SEARCH = 11;
    private boolean isChangeUser = false;

    /**
     * 是否显示管理按钮
     */
    private boolean isShowManageBtn;

    /**
     * 点击管理按钮弹窗
     */
//	private CustomContextDialog mCustomContextDialog;

    private long transferGroupFlag;
    /**
     * 判断是否展示操作按钮的标识
     **/
    private boolean isShowOpreatBtn = false;
    /**
     * 判断是来自聊吧转让
     */
    private int isFromChatbarTransfer = 0;// 1 不是 2 是

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_memeber_view);

        EventBus.getDefault().register(this);

        createId = getIntent().getLongExtra(GroupInfoActivity.CREATEID, 0);
        mGroupId = getIntent().getStringExtra("groupId");
        mGroupRole = getIntent().getIntExtra("groupRole", 2);
        mGroupName = getIntent().getStringExtra("groupName");
        isShowManageBtn = getIntent().getBooleanExtra("isShowManage", true);
        user = (GroupUser) getIntent().getSerializableExtra("groupUser");
        isFromChatbarTransfer = getIntent().getIntExtra("isFrom", 0);

        mIsGroupTransfer = getIntent().getBooleanExtra("groupTransfer", false);

        initViews();
        setListeners();
        initData();
    }

    @Subscribe
    public void receiveUpdateUserData(NickNameNotifyEvent event) {
        if (mMemberList == null || mMemberList.size() == 0)
            return;
        GroupSearchUser groupSearchUser = event.getGroupSearchUser();
        if (TextUtils.isEmpty(event.getUpdateUser())) {
            for (int i = 0; i < mMemberList.size(); i++) {
                if (groupSearchUser.user.userid != mMemberList.get(i).user.userid)
                    continue;
                mMemberList.get(i).grouprole = groupSearchUser.grouprole;
            }
        } else {
            mMemberList.remove(groupSearchUser);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * @Title: initViews
     * @Description: 初始化控件
     */
    private void initViews() {
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        mTitleBack = (ImageView) findViewById(R.id.iv_left);
        mTitleName = (TextView) findViewById(R.id.tv_title);
        mTitleRight = (TextView) findViewById(R.id.tv_right);

        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setImageResource(R.drawable.title_back);
        mTitleRight.setText(getResources().getString(R.string.group_manager));

        if ((mGroupRole == 0 || mGroupRole == 1) && isShowManageBtn) {
            mTitleRight.setVisibility(View.VISIBLE);
            mTitleRight.setText(R.string.group_manager);
            mTitleRight.setTextColor(getResColor(R.color.login_btn));
        } else {
            mTitleRight.setVisibility(View.GONE);
        }

        mMemberListView = (PullToRefreshPinnedSectionListView) findViewById(R.id.member_listview);
        mMemberListView.setMode(Mode.BOTH);
        mMemberListView.getRefreshableView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mMemberListView.getRefreshableView().setDescendantFocusability(
                ListView.FOCUS_BLOCK_DESCENDANTS);
        mWaitDialog = DialogUtil.getProgressDialog(GroupMemberViewActivity.this, "",
                getString(R.string.please_wait), null);

        mTitleName.setText(R.string.group_info_item_member);// group_info_item_member
        if (mIsGroupTransfer) {
            mTitleRight.setVisibility(View.INVISIBLE);
            mTitleName.setText(R.string.group_inf_group_transfer_title);
        }
    }

    /**
     * @Title: setListeners
     * @Description: 设置监听器
     */
    private void setListeners() {
        flLeft.setOnClickListener(this);
        mTitleBack.setOnClickListener(this);
        mTitleRight.setOnClickListener(this);

        mMemberListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                        mCurrentPage = 0;
                        initData();
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                        if (mCurrentPage < mTotalPage) {
                            loadPageData(mCurrentPage + 1);
                        } else {
                            mMemberListView.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    mMemberListView.onRefreshComplete();
                                }
                            }, 200);
                        }
                    }
                });
    }

    /**
     * @Title: initData
     * @Description: 初始化数据
     */
    private void initData() {
        mAdapter = new DataAdapter();
        mMemberListView.setAdapter(mAdapter);

        showWaitDialog(true);
        // 获取成员类表数据
        mCurrentPage = 0;
        loadPageData(1);
    }

    /**
     * @param pageIndex
     * @Title: loadPageData
     * @Description: 加载分页数据
     */
    private void loadPageData(int pageIndex) {
        FLAG_GET_MEMBER_LIST = GroupHttpProtocol.groupMember(mContext, mGroupId, pageIndex,
                PAGE_SIZE, this);
        if (FLAG_GET_MEMBER_LIST < 0) {
            showWaitDialog(false);
            handleDataFail(104, FLAG_GET_MEMBER_LIST);
        }
    }

    @Override
    protected void onDestroy() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    public void onGeneralSuccess(final String result, final long flag) {
        super.onGeneralSuccess(result, flag);
        if (transferGroupFlag == flag) {
            try {
                JSONObject json = new JSONObject(result);

                if (json.optInt("status") == 200) {
                    DialogUtil.showOKDialog(mContext, getResString(R.string.prompt),
                            getResString(R.string.group_inf_group_transfer_server_back),
                            null);
                } else if (result.contains("error")) {
                    ErrorCode.showError(mContext, result);
                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        } else {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    handleDataSuccess(result, flag);
                }
            });
        }
    }

    /**
     * @param result
     * @param flag
     * @Title: handleDataSuccess
     * @Description: 处理数据成功
     */

    private void handleDataSuccess(String result, long flag) {
        if (flag == FLAG_GET_MEMBER_LIST) {
            showWaitDialog(false);
            mMemberListView.onRefreshComplete();
            mGroupUserBean = GsonUtil.getInstance().getServerBean(
                    result, GroupMemberSearchBean.class);
            if (mGroupUserBean != null) {
                if (mGroupUserBean.isSuccess()) {
                    if (mCurrentPage == 0) {
                        mMemberList.clear();
                    }
                    mCurrentPage = mGroupUserBean.pageno;
                    mTotalPage = mGroupUserBean.amount / PAGE_SIZE;
                    if (mGroupUserBean.amount % PAGE_SIZE > 0) {
                        mTotalPage++;
                    }
                    handleGroupUserData(mGroupUserBean.users);
                } else {
                    // handleDataFail( 104 , flag );
                    ErrorCode.showError(mContext, result);
                    mMemberListView.onRefreshComplete();
                    showWaitDialog(false);
                }
            } else {
                handleDataFail(104, flag);
            }
        }
    }

    /**
     * @param users
     * @Title: handleGroupUserData
     * @Description: 处理数据，进行分组及添加标题
     */
    private void handleGroupUserData(ArrayList<GroupSearchUser> users) {
        int tmpRole = -1;
        if (mMemberList.isEmpty()) {
            // 第一次加载数据或刷新列表数据
        } else {
            // 否则取上一页最后一条数据的角色
            tmpRole = mMemberList.get(mMemberList.size() - 1).grouprole;
        }
        if (users == null)
            return;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).grouprole != tmpRole) {
                GroupSearchUser user = new GroupSearchUser();
                user.headerType = GroupSearchUser.HEADER;
                user.grouprole = users.get(i).grouprole;
                mMemberList.add(user);
                tmpRole = user.grouprole;
            }
            //gh 获取当前用户在吧内的角色
            if (users.get(i).user.userid == Common.getInstance().loginUser.getUid()) {
                mGroupRole = users.get(i).grouprole;
                if (mGroupRole == 0 || mGroupRole == 1) {
                    mTitleRight.setVisibility(View.VISIBLE);
                    mTitleRight.setText(R.string.group_manager);
                    mTitleRight.setTextColor(getResColor(R.color.login_btn));
                } else {
                    mTitleRight.setVisibility(View.GONE);
                }
            }
            mMemberList.add(users.get(i));
        }
        // 刷新数据显示
        // mMemberListView.setAdapter(mAdapter);
        if (users.size() == 1) {
            // 无任何圈管理员/圈成员时，添加无任何圈成员的显示。
            GroupSearchUser user = new GroupSearchUser();
            user.headerType = GroupSearchUser.HEADER;
            user.grouprole = 2;
            mMemberList.add(user);
            GroupSearchUser userEmpty = new GroupSearchUser();
            userEmpty.headerType = GroupSearchUser.CONTENT;
            userEmpty.contentType = 1;
            mMemberList.add(userEmpty);
        }
        isShowOpreatBtn = false;
        mAdapter.notifyDataSetChanged();
    }

    /**
     * @param e
     * @param flag
     * @Title: handleDataFail
     * @Description: 处理数据失败
     */
    private void handleDataFail(int e, long flag) {
        if (flag == FLAG_GET_MEMBER_LIST) {
            mMemberListView.onRefreshComplete();
            ErrorCode.toastError(mContext, e);
            showWaitDialog(false);
        }
    }

    /**
     * @param type 操作类型(1设置管理员；2取消管理员；3踢出圈子)
     * @Title: handleSetManagerDataSuccess
     * @Description: 对列表用户操作成功之后的数据处理
     */
    private void handleMemberRoleChangeSuccess(int type, GroupSearchUser groupSearchUser) {
        ArrayList<GroupSearchUser> userList = new ArrayList<GroupSearchUser>();
        for (GroupSearchUser user : mMemberList) {
            if (user.headerType == GroupSearchUser.CONTENT) {
                // 用户数据
                if (type == 1) {
                    // 设为管理员
                    if (user.user != null && user.user.userid == groupSearchUser.user.userid) {
                        user.grouprole = 1;
                        // 添加到管理员列表的缓存
                        GroupModel.getInstance().addToManagerIdList(
                                String.valueOf(user.user.userid));
                    }
                } else if (type == 2) {
                    // 取消管理员
                    if (user.user != null && user.user.userid == groupSearchUser.user.userid) {
                        user.grouprole = 2;
                        // 从管理员列表的缓存中移除
                        GroupModel.getInstance().delFromManagerIdList(
                                String.valueOf(user.user.userid));
                    }
                } else if (type == 3) {
                    // 踢出圈子
                    if (user.user != null && user.user.userid == groupSearchUser.user.userid) {
                        user.grouprole = -1;
                        // 从管理员列表的缓存中移除
                        GroupModel.getInstance().delFromManagerIdList(
                                String.valueOf(user.user.userid));
                    }
                }
                if (user.grouprole != -1) {
                    userList.add(user);
                }
            }
        }
        Collections.sort(userList);
        mMemberList.clear();
        handleGroupUserData(userList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                exitActivity();
                break;
            case R.id.tv_right:
                mTitleRight.setTextColor(getResources().getColor(R.color.login_btn));
                if (!isShowOpreatBtn) {
                    mTitleRight.setText(R.string.complete);
                    isShowOpreatBtn = true;
                    mAdapter.notifyDataSetChanged();
                } else {
                    mTitleRight.setText(R.string.group_manager);
                    isShowOpreatBtn = false;
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * @param isShow
     * @Title: showWaitDialog
     * @Description: 显示加载框
     */
    private void showWaitDialog(boolean isShow) {
        if (mWaitDialog != null) {
            if (isShow) {
                mWaitDialog.show();
            } else {
                mWaitDialog.hide();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_CODE_GROUP_MEMBER && resultCode == RESULT_OK)
                || (requestCode == REQUEST_CODE_GROUP_SEARCH && resultCode == RESULT_OK)) {
            isChangeUser = true;

            showWaitDialog(true);
            long id = Thread.currentThread().getId();
            CommonFunction.log("kevin", "onActivityResult==" + id);
            mCurrentPage = 0;
            loadPageData(1);
        }
    }

    /**
     * @Title: exitActivity
     * @Description: 退出activity
     */
    private void exitActivity() {
        if (isChangeUser) {
            setResult(RESULT_OK);
        }
        finish();
    }


    /**
     * @author zhonglong kylin17@foxmail.com
     * @ClassName: DataAdapter
     * @Description: 数据适配器
     * @date 2013-12-11 下午2:24:13
     */
    class DataAdapter extends BaseAdapter implements PinnedSectionListAdapter, OnClickListener {

        private GeoData userGeoData;

        public DataAdapter() {
            this.userGeoData = LocationUtil.getCurrentGeo(mContext);
        }

        public void updateData(List<GroupSearchUser> groupSearchUsers) {
            mMemberList.clear();
            mMemberList.addAll(groupSearchUsers);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mMemberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMemberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(),
                        R.layout.user_nearby_list_item2, null);
                viewHolder.groupTitle = (TextView) convertView
                        .findViewById(R.id.list_group_title);
                viewHolder.groupContent = (RelativeLayout) convertView
                        .findViewById(R.id.list_group_content);
                viewHolder.groupContentEmpty = (TextView) convertView
                        .findViewById(R.id.content_empty);
                viewHolder.checkUser = (CheckBox) convertView
                        .findViewById(R.id.user_checkbox);
                viewHolder.userIcon = (HeadPhotoView) convertView
                        .findViewById(R.id.friend_icon);
                viewHolder.tvNickName = (TextView) convertView
                        .findViewById(R.id.tvNickName);
                viewHolder.charmnum = (TextView) convertView.findViewById(R.id.user_charmnum);
                viewHolder.tvAge = (TextView) convertView.findViewById(R.id.tvAge);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.tvDistance);
                viewHolder.tvDot = (TextView) convertView.findViewById(R.id.tvDot);
                viewHolder.tvState = (TextView) convertView.findViewById(R.id.tvState);
                viewHolder.tvSign = (TextView) convertView.findViewById(R.id.tvSign);
                viewHolder.weiboIcon = (LinearLayout) convertView
                        .findViewById(R.id.llWeiboIcon);
                viewHolder.divider = convertView.findViewById(R.id.divider);
                //添加性别
                viewHolder.ivSex = (ImageView) convertView.findViewById(R.id.iv_sex);
                viewHolder.llSexAndAge = (LinearLayout) convertView.findViewById(R.id.info_center);

                //操作按钮
                viewHolder.btnOpreate = (Button) convertView.findViewById(R.id.btn_operating);
                viewHolder.flOperating = (FrameLayout) convertView.findViewById(R.id.fl_operating);
                viewHolder.tvState.setVisibility(View.VISIBLE);
                viewHolder.distance.setVisibility(View.VISIBLE);
                viewHolder.tvDot.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //聊吧转让显示选择框
            if (isFromChatbarTransfer == 2) {
                viewHolder.checkUser.setVisibility(View.VISIBLE);
                viewHolder.btnOpreate.setVisibility(View.GONE);
            } else {
                viewHolder.checkUser.setVisibility(View.GONE);
                viewHolder.btnOpreate.setVisibility(View.VISIBLE);
            }

            final GroupSearchUser groupUser = mMemberList.get(position);

            if (groupUser.headerType == GroupSearchUser.HEADER) {
                viewHolder.groupTitle.setVisibility(View.VISIBLE);
                viewHolder.groupContent.setVisibility(View.GONE);
                viewHolder.groupContentEmpty.setVisibility(View.GONE);
                switch (groupUser.grouprole) {
                    case 0:
                        viewHolder.groupTitle.setText(R.string.group_owner);
                        break;
                    case 1:
                        viewHolder.groupTitle.setText(R.string.group_managers);
                        break;
                    case 2:
                        viewHolder.groupTitle.setText(R.string.group_member);
                        break;
                    default:
                        break;
                }
                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                if (groupUser.contentType == 1) {
                    viewHolder.groupTitle.setVisibility(View.GONE);
                    viewHolder.groupContent.setVisibility(View.GONE);
                    viewHolder.groupContentEmpty.setVisibility(View.VISIBLE);
                    convertView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else {
                    viewHolder.groupTitle.setVisibility(View.GONE);
                    viewHolder.groupContent.setVisibility(View.VISIBLE);
                    viewHolder.groupContentEmpty.setVisibility(View.GONE);

                    // 头像

                    viewHolder.userIcon.setOnHeadPhotoViewClick(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            int entrance = ProfileEntrance.GROUP_MEMBER;
                            if (groupUser != null &&
                                    groupUser.headerType == GroupSearchUser.CONTENT) {
                                //查看他人资料
                                if (groupUser.grouprole == 0) {
                                    if (groupUser.user.userid == Common.getInstance().loginUser.getUid()) {
                                        Intent intent = new Intent(GroupMemberViewActivity.this, UserInfoActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(GroupMemberViewActivity.this, OtherInfoActivity.class);
                                        intent.putExtra("uid", groupUser.user.userid);
                                        intent.putExtra("nickname", groupUser.user.nickname);
                                        intent.putExtra("notename", groupUser.user.notename);
                                        intent.putExtra(Constants.UID, groupUser.user.userid);
                                        intent.putExtra("user", groupUser.user.convertBaseToUser());
                                        startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent(GroupMemberViewActivity.this, OtherInfoActivity.class);
                                    intent.putExtra("uid", groupUser.user.userid);
                                    intent.putExtra("nickname", groupUser.user.nickname);
                                    intent.putExtra("notename", groupUser.user.notename);
                                    intent.putExtra(Constants.UID, groupUser.user.userid);
                                    intent.putExtra("user", groupUser.user.convertBaseToUser());
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                    //加载图片
                    viewHolder.userIcon.execute(ChatFromType.Group,
                            groupUser.user.convertBaseToUser(), null);


                    /*
                     * viewHolder.checkUser.setOnCheckedChangeListener(new
                     * OnCheckedChangeListener() {
                     *
                     * @Override public void onCheckedChanged(CompoundButton
                     * buttonView, boolean isChecked) { if(groupUser.user.userid
                     * == Common.getInstance().loginUser.getUid()){
                     * CommonFunction.toastMsg(mContext,
                     * R.string.cannot_operate_self);
                     * buttonView.setChecked(false); return; } if(isChecked){
                     * groupUser.isCheck = true; mCurrentSelCount++; }else{
                     * groupUser.isCheck = false; mCurrentSelCount--; } } });
                     */


                    if (groupUser.isCheck) {
                        viewHolder.checkUser.setChecked(true);
                    } else {
                        viewHolder.checkUser.setChecked(false);
                    }
                    // 昵称
                    String name = groupUser.user.nickname;
                    if (name == null || name.length() <= 0) {
                        name = String.valueOf(groupUser.user.userid);
                    }
                    SpannableString spName = FaceManager.getInstance(parent.getContext())
                            .parseIconForString(viewHolder.tvNickName, parent.getContext(),
                                    name, 20);
                    viewHolder.tvNickName.setText(spName);

                    // vip
                    if (groupUser.user.svip > 0) {// 仅为svip
                        viewHolder.tvNickName.setTextColor(Color.parseColor("#ee4552"));
                    } else {// 非vip
                        viewHolder.tvNickName.setTextColor(Color.parseColor("#000000"));
                    }

                    // 年龄
                    if (groupUser.user.age <= 0) {
                        viewHolder.tvAge.setText(R.string.unknown);
                    } else {
                        viewHolder.tvAge.setText(String.valueOf(groupUser.user.age));
                    }

                    // 性别
                    int sex = groupUser.user.getSex();
                    if (sex == 2) {
                        viewHolder.ivSex.setImageResource(R.drawable.thread_register_woman_select);
                        viewHolder.llSexAndAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                    } else if (sex == 1) {
                        viewHolder.ivSex.setImageResource(R.drawable.thread_register_man_select);
                        viewHolder.llSexAndAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
                    }

                    //魅力值
                    int[] charismaSymbole = CommonFunction.getCharismaSymbol(groupUser.user.charmnum);
                    viewHolder.charmnum.setVisibility(View.GONE);
                    if (charismaSymbole.length >= 6) {
                        int rank = charismaSymbole[5];
                        if (rank >= 20) {
                            viewHolder.charmnum.setVisibility(View.GONE);
                            int index = (rank - 1) / 20;
                            index = index > 4 ? 4 : index;
                            viewHolder.charmnum.setBackgroundResource(PicIndex.drawCharm[index]);
                            viewHolder.charmnum.setText(rank + mContext.getString(R.string.charm_lv));
                        } else {
                            viewHolder.charmnum.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolder.charmnum.setVisibility(View.GONE);
                    }

                    // 距离
                    int distance = -1;
                    try {
                        distance = LocationUtil.calculateDistance(userGeoData.getLng(),
                                userGeoData.getLat(), groupUser.user.lng,
                                groupUser.user.lat);
                    } catch (Exception e) {

                    }
                    if (distance < 0) { // 不可知
                        viewHolder.distance.setText(R.string.unable_to_get_distance);
                    } else {
                        viewHolder.distance.setText(CommonFunction
                                .covertSelfDistance(distance));
                    }

                    // 在线状态

                    String time = TimeFormat.timeFormat1(viewHolder.tvState.getContext(),
                            groupUser.user.lastonlinetime);
                    if (time != null && time.length() > 0) {
                        viewHolder.tvState.setText(time);
                    } else {
                        viewHolder.tvState.setText(R.string.unable_to_get_time);
                    }

                    // 签名
                    String infor = groupUser.user.getPersonalInfor(viewHolder.tvSign
                            .getContext());
                    if (infor != null && !"".equals(infor)) {//如果签名不为空，签名可见

                        SpannableString spSign = FaceManager.getInstance(parent.getContext()).parseIconForString(parent.getContext(), infor, 0, null);
                        viewHolder.tvSign.setVisibility(View.VISIBLE);
                        viewHolder.tvSign.setText(spSign);
                    } else {
                        viewHolder.tvSign.setText("");
                    }

                    // TODO 微博

                    // CommonFunction.showRightIcon( viewHolder.weiboIcon ,
                    // groupUser ,
                    // parent.getContext( ) );

                    ImageView[] weibos = new ImageView[6];
                    weibos[0] = (ImageView) convertView.findViewById(R.id.weibos_icon_1);
                    weibos[1] = (ImageView) convertView.findViewById(R.id.weibos_icon_2);
                    weibos[2] = (ImageView) convertView.findViewById(R.id.weibos_icon_3);
                    weibos[3] = (ImageView) convertView.findViewById(R.id.weibos_icon_4);
                    weibos[4] = (ImageView) convertView.findViewById(R.id.weibos_icon_5);
                    weibos[5] = (ImageView) convertView.findViewById(R.id.weibos_icon_6);
//					CommonFunction.showWeibosIcon( weibos ,
//							User.parseWeiboStr( groupUser.user.weibo ) ,
//							groupUser.user.occupation , parent.getContext( ) );


                    if (position == 1 || position == mMemberList.size() - 1) {
                        viewHolder.divider.setVisibility(View.GONE);
                    } else {
                        viewHolder.divider.setVisibility(View.VISIBLE);
                    }

                    //操作按钮的展示
                    if (isShowOpreatBtn) {
                        /**
                         * 如果是自己，不显示操作按钮
                         * 如果我是管理员，吧主不显示操作按钮
                         */
                        if (Common.getInstance().loginUser.getUid() == mMemberList.get(position).user.userid
                                || mMemberList.get(position).grouprole == 0) {
                            viewHolder.btnOpreate.setVisibility(View.GONE);
                        } else {
                            viewHolder.btnOpreate.setVisibility(View.VISIBLE);
                        }
                    } else {
                        viewHolder.btnOpreate.setVisibility(View.GONE);
                    }

                    viewHolder.btnOpreate.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final GroupSearchUser currentUser = mMemberList.get(position);
                            CustomManagerMemberDia.launchCustomManagerMemberDia(GroupMemberViewActivity.this, GroupMemberViewActivity.this, currentUser
                                    , mGroupId, new CustomManagerMemberDia.UdapterMemberDataListener() {
                                        @Override
                                        public void becameManagerListener(final GroupSearchUser groupSearchUser) {
                                            if (groupSearchUser == null || mMemberList.size() == 0)
                                                return;
                                            handleMemberRoleChangeSuccess(1, groupSearchUser);
                                        }

                                        @Override
                                        public void cancelManagerListener(GroupSearchUser groupSearchUser) {
                                            if (groupSearchUser == null || mMemberList.size() == 0)
                                                return;
                                            handleMemberRoleChangeSuccess(2, groupSearchUser);
                                        }

                                        @Override
                                        public void tickChatbarListener(final GroupSearchUser groupSearchUser) {
                                            if (groupSearchUser == null || mMemberList.size() == 0)
                                                return;
                                            handleMemberRoleChangeSuccess(3, groupSearchUser);
                                        }
                                    });
                        }
                    });
                    viewHolder.flOperating.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final GroupSearchUser currentUser = mMemberList.get(position);
                            CustomManagerMemberDia.launchCustomManagerMemberDia(GroupMemberViewActivity.this, GroupMemberViewActivity.this, currentUser
                                    , mGroupId, new CustomManagerMemberDia.UdapterMemberDataListener() {
                                        @Override
                                        public void becameManagerListener(final GroupSearchUser groupSearchUser) {
                                            if (groupSearchUser == null || mMemberList.size() == 0)
                                                return;
                                            handleMemberRoleChangeSuccess(1, groupSearchUser);
                                        }

                                        @Override
                                        public void cancelManagerListener(GroupSearchUser groupSearchUser) {
                                            if (groupSearchUser == null || mMemberList.size() == 0)
                                                return;
                                            handleMemberRoleChangeSuccess(2, groupSearchUser);
                                        }

                                        @Override
                                        public void tickChatbarListener(final GroupSearchUser groupSearchUser) {
                                            if (groupSearchUser == null || mMemberList.size() == 0)
                                                return;
                                            handleMemberRoleChangeSuccess(3, groupSearchUser);
                                        }
                                    });
                        }
                    });

                    final CheckBox checkBox = viewHolder.checkUser;
                    convertView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (!mIsGroupTransfer) {
                                int entrance = ProfileEntrance.GROUP_MEMBER;
                                if (groupUser != null
                                        && groupUser.headerType == GroupSearchUser.CONTENT) {
                                    if (groupUser.grouprole == 0 && groupUser.user.userid == Common.getInstance().loginUser.getUid()) {//查看个人资料
                                        Intent intent = new Intent(GroupMemberViewActivity.this, UserInfoActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Intent intent = new Intent(GroupMemberViewActivity.this, OtherInfoActivity.class);
                                        intent.putExtra("uid", groupUser.user.userid);
                                        intent.putExtra("nickname", groupUser.user.nickname);
                                        intent.putExtra("notename", groupUser.user.notename);
                                        intent.putExtra(Constants.UID, groupUser.user.userid);
                                        intent.putExtra("user", groupUser.user.convertBaseToUser());
                                        startActivity(intent);
                                    }
                                }
                            } else {
                                if (groupUser.grouprole == 0) {
                                    if (groupUser.user.userid == Common.getInstance().loginUser.getUid()) {
                                        Intent intent = new Intent(GroupMemberViewActivity.this, UserInfoActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(GroupMemberViewActivity.this, OtherInfoActivity.class);
                                        intent.putExtra("uid", groupUser.user.userid);
                                        intent.putExtra("nickname", groupUser.user.nickname);
                                        intent.putExtra("notename", groupUser.user.notename);
                                        intent.putExtra(Constants.UID, groupUser.user.userid);
                                        intent.putExtra("user", groupUser.user.convertBaseToUser());
                                        startActivity(intent);
                                    }
                                } else {
                                    String fomat = getResString(R.string.group_inf_group_menu_transfer);
                                    String userName = String.format(fomat,
                                            groupUser.user.nickname);
                                    DialogUtil
                                            .showOKCancelDialog(
                                                    mContext,
                                                    getResString(R.string.group_inf_group_transfer_title),
                                                    userName, new OnClickListener() {

                                                        @Override
                                                        public void onClick(View v) {
                                                            // TODO Auto-generated
                                                            // method stub
                                                            String uid = String
                                                                    .valueOf(groupUser.user.userid);
                                                            transferGroupFlag = GroupHttpProtocol
                                                                    .transferGroup(
                                                                            GroupMemberViewActivity.this,
                                                                            mGroupId,
                                                                            uid,
                                                                            GroupMemberViewActivity.this);
                                                        }
                                                    });
                                }
                            }
                        }
                    });
                }
            }

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return mMemberList.get(position).headerType;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == GroupSearchUser.HEADER;
        }

        @Override
        public void onClick(View v) {

        }
    }

    static class ViewHolder {
        TextView groupTitle;
        TextView groupContentEmpty;
        RelativeLayout groupContent;
        CheckBox checkUser;
        HeadPhotoView userIcon;//用户头像
        TextView tvNickName;
        TextView tvAge;
        TextView distance;
        TextView tvState;
        TextView tvSign;
        LinearLayout weiboIcon;
        TextView charmnum;
        View divider;
        ImageView ivSex;
        LinearLayout llSexAndAge;
        Button btnOpreate;//操作按钮
        TextView tvDot;
        FrameLayout flOperating;
    }

    @Override
    protected String getGroupId() {
        return mGroupId;
    }

    private View.OnClickListener groupTransferListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            arg0.getTag();
            String uid = String.valueOf(Common.getInstance().loginUser.getUid());
            transferGroupFlag = GroupHttpProtocol.transferGroup(GroupMemberViewActivity.this,
                    mGroupId, uid, GroupMemberViewActivity.this);
        }
    };


}
