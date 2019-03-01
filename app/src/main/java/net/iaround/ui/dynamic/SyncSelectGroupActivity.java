
package net.iaround.ui.dynamic;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollBarListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.share.interior.CustomShareDialog;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.seach.GroupListBean;

import java.util.ArrayList;


/**
 * 同步到圈子时选择圈子的页面
 */
public class SyncSelectGroupActivity extends SuperActivity {
    Context mContext;
    Activity mActivity;

    /**
     * 加载数据Loading
     */
    private Dialog mProgressDialog;

    private ImageView mTitleBack;
    private TextView mTitleName;
    private PullToRefreshScrollBarListView mListView;
    private EmptyLayout mEmptyLayout;

    private long GET_USER_GROUPS_FLAG;
    private GroupListBean mJointGroups;

    private DataAdapter mAdapter;
    private ArrayList<Group> mDataList = new ArrayList<Group>();

    CustomShareDialog shareDialog;
    private String excludedGroupId = "";// 需要排除的圈子
    private String GROUP_ID = "";
    private String GROUP_NAME = "";

    public static void launch(Context context, long groupid, int requestCode) {
        Intent intent = new Intent(context, SyncSelectGroupActivity.class);
        intent.putExtra("exclude_groupid", groupid);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iaround_share_select_group_layout);

        CommonFunction.log("sync",
                "exclude_groupid***" + getIntent().getLongExtra("exclude_groupid", 0));
        excludedGroupId = String.valueOf(getIntent().getLongExtra("exclude_groupid", 0));
        CommonFunction.log("sync", "excludedGroupId***" + excludedGroupId);

        mContext = this;
        mActivity = this;

        initViews();
        initData();

        requestData();

        setListeners();

    }

    private void initViews() {
        mTitleBack = (ImageView) findViewById(R.id.title_back);
        mTitleName = (TextView) findViewById(R.id.title_name);
        mTitleName.setText(getString(R.string.sync_select_group_title));

        mListView = (PullToRefreshScrollBarListView) findViewById(R.id.group_listview);
        mListView.setMode(Mode.DISABLED);

        mEmptyLayout = new EmptyLayout(mContext, mListView.getRefreshableView());
        mEmptyLayout.setErrorMessage(getString(R.string.network_req_failed));
        mEmptyLayout.setEmptyMessage(getString(R.string.no_data));
    }

    private void setListeners() {
        mTitleBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Group group = null;
                try {
                    group = mDataList.get(position - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (group != null && group.flag >= 0) {
                    for (int i = 0; i < mDataList.size(); i++) {
                        if (mDataList.get(i).isselect == 1) {
                            mDataList.get(i).isselect = 0;
                            break;
                        }
                    }

                    group.isselect = 1;
                    GROUP_ID = group.id;
                    GROUP_NAME = group.name;

                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void initData() {
        mAdapter = new DataAdapter();
        mListView.setAdapter(mAdapter);
    }

    private void requestData() {
        showProgressDialogue();
        GET_USER_GROUPS_FLAG = GroupHttpProtocol.groupMylist(this, this);
    }

    private void showProgressDialogue() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(this, R.string.prompt,
                    R.string.please_wait, null);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialogue() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public void handleGroupData(ArrayList<Group> groups) {
        if (groups != null) {
            int length = groups.size();

            int index = -1;
            for (int i = 0; i < length; i++) {
                Group group = groups.get(i);
                /*
				 * if ( i < ( length - 1 ) ) { group.isShowDivider = 1; } else {
				 * group.isShowDivider = 0; }
				 */
                if (group.id.equals(excludedGroupId)) {
                    index = i;
                }
                group.isselect = 0;
            }
            if (index >= 0) {
                groups.remove(index);
            }

            // 先进行分组
            ArrayList<Group> myGroupList = new ArrayList<Group>();
            ArrayList<Group> myJoinList = new ArrayList<Group>();
            for (Group group : groups) {
                if (group.user.userid == Common.getInstance().loginUser.getUid()) {
                    // 我创建的圈子
                    // groupItem.setTypeNumber( 0 );
                    myGroupList.add(group);
                } else {
                    // 我加入的圈子
                    // groupItem.setTypeNumber( 1 );
                    myJoinList.add(group);
                }
                group.isShowDivider = 1;
            }

            mDataList.clear();

            // 我创建的圈子
            if (!myGroupList.isEmpty()) {
                Group myListItem = new Group();
                myListItem.id = "my_group";
                myListItem.name = getString(R.string.my_create_group);
                myListItem.flag = -1;
                mDataList.add(myListItem);
                myGroupList.get(myGroupList.size() - 1).isShowDivider = 0;
                mDataList.addAll(myGroupList);

            }

            // 我加入的圈子
            if (!myJoinList.isEmpty()) {
                Group myJoinItem = new Group();
                myJoinItem.id = "joint_group";
                myJoinItem.name = getString(R.string.my_join_group);
                myJoinItem.flag = -1;
                mDataList.add(myJoinItem);
                myJoinList.get(myJoinList.size() - 1).isShowDivider = 0;
                mDataList.addAll(myJoinList);
            }
			
			/*
			 * mDataList.clear( ); mDataList.addAll( groups );
			 */
        }
    }

    public void onGeneralSuccess(String result, long flag) {
        CommonFunction.log("sync", "result---" + result);
        if (flag == GET_USER_GROUPS_FLAG) {
            hideProgressDialogue();
            mListView.onRefreshComplete();
            mJointGroups = GsonUtil.getInstance()
                    .getServerBean(result, GroupListBean.class);
            if (mJointGroups != null) {
                if (mJointGroups.isSuccess()) {
                    handleGroupData(mJointGroups.groups);

                    mAdapter.notifyDataSetChanged();

                    if (mDataList.isEmpty()) {
                        mEmptyLayout.showEmpty();
                    }

                    if (mJointGroups.groups == null || mJointGroups.amount == 0) {
                        // 没有圈子，直接退出
                        DialogUtil.showOKDialog(mContext, "",
                                getString(R.string.share_no_groups_tips),
                                R.layout.iaround_share_tips_dialog, new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                    }
                } else {
                    if (mJointGroups.groups == null || mJointGroups.amount == 0) {
                        // 没有圈子，直接退出
                        DialogUtil.showOKDialog(mContext, "",
                                getString(R.string.network_req_failed),
                                R.layout.iaround_share_tips_dialog, new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                    } else {
                        onGeneralError(mJointGroups.error, flag);
                    }
                }
            } else {
                onGeneralError(107, flag);
            }
        }
    }

    public void onGeneralError(int e, long flag) {
        if (flag == GET_USER_GROUPS_FLAG) {
            hideProgressDialogue();
            mListView.onRefreshComplete();
            ErrorCode.toastError(this, e);
            mEmptyLayout.showError();
        }
    }

    private void saveData() {
        Intent intent = new Intent();
        intent.putExtra("groupid", GROUP_ID);
        intent.putExtra("groupname", GROUP_NAME);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void finish() {
        if (shareDialog != null) {
            shareDialog.hide();
        }
        super.finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveData();
        }
        return super.onKeyDown(keyCode, event);
    }

    class DataAdapter extends BaseAdapter {

        Drawable drawable = null;

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.sync_select_group_item, null);

                holder.title = (TextView) convertView.findViewById(R.id.sync_group_title);
                holder.itemView = convertView.findViewById(R.id.sync_group_item_view);
                holder.groupImage = (ImageView) convertView.findViewById(R.id.group_icon);
                holder.groupName = (TextView) convertView.findViewById(R.id.group_name);

                holder.groupFlag = (TextView) convertView.findViewById(R.id.group_flag);
                holder.groupCurrentMembers = (TextView) convertView
                        .findViewById(R.id.group_current_members);
                holder.splitLine = (TextView) convertView.findViewById(R.id.split_line);
                holder.groupMaxMembers = (TextView) convertView
                        .findViewById(R.id.group_max_members);
                holder.groupCategory = (TextView) convertView
                        .findViewById(R.id.group_category);

                holder.selectMark = (ImageView) convertView
                        .findViewById(R.id.group_select_mark);
                holder.bottomDivider = convertView.findViewById(R.id.item_wrap_divider);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Group group = mDataList.get(position);
            if (group != null && holder != null) {
                if (group.flag < 0) {
                    holder.itemView.setVisibility(View.GONE);
                    holder.title.setVisibility(View.VISIBLE);
                    holder.title.setText(group.name);
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.title.setVisibility(View.GONE);

                    if (group.isShowDivider == 1) {
                        holder.bottomDivider.setVisibility(View.VISIBLE);
                    } else {
                        holder.bottomDivider.setVisibility(View.INVISIBLE);
                    }

                    if (group.icon == null || holder.groupImage == null) {
                        CommonFunction.log("groupshare", "group.icon null********");
                    } else {
//						ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
//								CommonFunction.thumPicture( group.icon ) , holder.groupImage ,
//								PicIndex.DEFAULT_GROUP_SMALL , PicIndex.DEFAULT_GROUP_SMALL ,
//								null , 0 , "#00000000" );//jiqiang
                        GlideUtil.loadCircleImage(BaseApplication.appContext, CommonFunction.thumPicture(group.icon), holder.groupImage, PicIndex.DEFAULT_GROUP_SMALL, PicIndex.DEFAULT_GROUP_SMALL);
                    }

                    if (group.name == null || holder.groupName == null) {
                        CommonFunction.log("groupshare", "group.name null+++++++++++");
                    } else {
                        SpannableString groupName = FaceManager.getInstance(
                                parent.getContext()).parseIconForString(holder.groupName,
                                parent.getContext(), group.name, 16);
                        holder.groupName.setText(groupName);
                    }

                    if (group.flag == 1) {
                        holder.groupFlag.setVisibility(View.GONE);
                        holder.groupName.setCompoundDrawables(null, null, null, null);
                    } else if (group.flag == 2) {
                        holder.groupFlag.setVisibility(View.GONE);

                        holder.groupFlag.setText("hot");
                        int flagIcon = R.drawable.group_flag_hot;
                        holder.groupFlag.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                flagIcon, 0);
                        holder.groupFlag.setBackgroundResource(R.drawable.group_flag_hot_bg);
                    } else {
                        holder.groupFlag.setVisibility(View.GONE);
                        holder.groupName.setCompoundDrawables(null, null, null, null);
                    }

                    // 小圈显示当前人数和总人数，大圈和十万圈只显示当前人数
                    holder.groupCurrentMembers.setText(String.valueOf(group.usercount));
                    if (group.classify == 1) {
                        drawable = getResources().getDrawable(
                                R.drawable.group_members_gray_icon);
                        holder.groupCurrentMembers.setTextColor(getResources()
                                .getColorStateList(R.color.c_999999));

                        holder.splitLine.setVisibility(View.VISIBLE);
                        holder.groupMaxMembers.setText(String.valueOf(group.maxcount));
                    } else if (group.classify == 2) {
                        drawable = getResources().getDrawable(
                                R.drawable.group_members_red_icon);
                        holder.groupCurrentMembers.setTextColor(getResources()
                                .getColorStateList(R.color.c_f28381));

                        holder.splitLine.setVisibility(View.GONE);
                        holder.groupMaxMembers.setText("");
                    } else if (group.classify == 3) {
                        drawable = getResources().getDrawable(
                                R.drawable.group_members_red_icon);
                        holder.groupCurrentMembers.setTextColor(getResources()
                                .getColorStateList(R.color.c_f28381));

                        holder.splitLine.setVisibility(View.GONE);
                        holder.groupMaxMembers.setText("");
                    }
                    if (drawable != null) {
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                drawable.getMinimumHeight());
                        holder.groupCurrentMembers.setCompoundDrawables(drawable, null,
                                null, null);
                    }

                    if (group.category != null) {
                        String[] typeArray = group.category.split("\\|");
                        int langIndex = CommonFunction.getLanguageIndex(mContext);
                        holder.groupCategory.setText(typeArray[langIndex]);
                    }

                    if (group.isselect == 1) {
                        holder.selectMark.setImageResource(R.drawable.round_check_true);
                    } else {
                        holder.selectMark.setImageResource(R.drawable.round_check_false);
                    }
                }

            }

            return convertView;
        }
    }

    class ViewHolder {
        public TextView title;

        public View itemView;
        public ImageView groupImage;
        public TextView groupName;

        public TextView groupCurrentMembers;
        public TextView splitLine;
        public TextView groupMaxMembers;
        public TextView groupCategory;

        public TextView groupFlag;
        public ImageView selectMark;
        public View bottomDivider;
    }
}
