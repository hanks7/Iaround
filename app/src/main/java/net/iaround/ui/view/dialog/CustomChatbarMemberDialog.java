package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.im.GroupOnlineUser;
import net.iaround.model.im.GroupSimpleUser;
import net.iaround.model.im.GroupUser;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.interfaces.ChatbarSendPersonalSocketListener;
import net.iaround.ui.view.HeadPhotoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Class:
 * Author：yuchao
 * Date: 2017/6/15 21:45
 * Email：15369302822@163.com
 */
public class CustomChatbarMemberDialog extends Dialog implements HttpCallBack {

    /**
     * 关闭按钮
     */
    private ImageView ivClose;
    /**
     * 用户列表
     */
    private PullToRefreshListView lvChatbarMember;
    private Context mContext;
    /**
     * 获取在线用户的flag
     */
    private long getOnLineMemberFlag;
    /**
     * 聊吧id
     */
    private String groupid;
    /**
     * 用户集合
     */
    private List<GroupSimpleUser> online_list;
    /***
     * 当前页码
     */
    private int mCurrentPage;
    /***
     * 实际页码
     */
    private int mTotalPage;
    /**
     * 每页数量
     */
    private final int PAGE_SIZE = 20;
    /**
     * 用户集合
     */
    private ArrayList<GroupSimpleUser> totalOnline_list;
    private boolean isHasMore = false;
    private ChatBarMemberAdapter adapter;
    private User user;
    private Handler mHandler = new Handler();

    /***
     * 自定义个人资料弹框
     */
    private CustomChatBarDialog customChatBarDialog;
    private GroupOnlineUser onLineUser;
    public ChatbarSendPersonalSocketListener mSendListener;

    public CustomChatbarMemberDialog(Context context, ChatbarSendPersonalSocketListener listener) {
        super(context, R.style.chat_bar_transparent_dialog);
        this.mContext = context;
        this.mSendListener=listener;
    }

    public CustomChatbarMemberDialog(Context context, int themeResId) {
        super(context, R.style.chat_bar_transparent_dialog);
    }

    protected CustomChatbarMemberDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_chatbar_member);

        EventBus.getDefault().register(CustomChatbarMemberDialog.this);

        online_list = new ArrayList<>();
        totalOnline_list = new ArrayList<>();
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //底部弹出样式
        window.setWindowAnimations(R.style.chatbar_member_popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        initView();
        initData();
        initListener();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(CustomChatbarMemberDialog.this);
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    private void initView() {
        ivClose = (ImageView) findViewById(R.id.iv_close);
        lvChatbarMember = (PullToRefreshListView) findViewById(R.id.lv_chatbar_member);

        lvChatbarMember.setMode(PullToRefreshBase.Mode.BOTH);
        lvChatbarMember.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        lvChatbarMember.getRefreshableView().setFastScrollEnabled(false);
    }

    private void initData() {
        adapter = new ChatBarMemberAdapter();
        lvChatbarMember.setAdapter(adapter);
        loadPageNum(PAGE_SIZE, 1);
    }

    private void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        lvChatbarMember.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                loadPageNum(PAGE_SIZE, 1);
                mCurrentPage = 1;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (onLineUser!=null && (onLineUser.getAmount() - mCurrentPage * PAGE_SIZE > 0)) {
                    ++mCurrentPage;
                    loadPageNum(PAGE_SIZE, mCurrentPage);

                } else {
                    lvChatbarMember.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lvChatbarMember.onRefreshComplete();
                            CommonFunction.toastMsg(getContext(), R.string.no_more_data);
                        }
                    }, 200);
                }

            }
        });
        lvChatbarMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupUser groupUser = totalOnline_list.get(position - 1).user;
                user = new User();

                user.setUid(groupUser.getUserid());
                user.setIcon(groupUser.getIcon());
                user.setNickname(groupUser.getNickname());
                user.setNoteName(groupUser.getNotes());
                user.setSex(groupUser.getGender().equals("m") ? 1 : 0);
                user.setAge(groupUser.getAge());
                user.setDistance(groupUser.getDistance());
                user.setViplevel(groupUser.getViplevel());
                user.setSVip(groupUser.getSvip());
                user.setGroupRole(groupUser.getGroup_role());
                user.setAge(groupUser.getAudio());

                long creatorUid = Long.valueOf(GroupModel.getInstance().getGroupOwnerId());
                //如果点击的是自己的头像
                if (Common.getInstance().loginUser.getUid() == user.getUid())
                {
                    customChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                    customChatBarDialog.setUser(Common.getInstance().loginUser, Common.getInstance().loginUser.getGroupRole(), groupid);
                    customChatBarDialog.isClickMySelf(true);
                    customChatBarDialog.show();
                } else {
                    if (Common.getInstance().loginUser.getUid() == creatorUid) { //是群的创建者
                        customChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                        customChatBarDialog.setUser(user, 0, groupid);
                        customChatBarDialog.show();
                    } else if (Common.getInstance().loginUser.getGroupRole() == 1) { // 为管理员
                        customChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                        customChatBarDialog.setUser(user, 1, groupid);
                        customChatBarDialog.show();
                    } else {
                        /***
                         * 点击角色是圈成员，无论被点击的是圈主 圈管理员，圈成员都
                         * 打开他人资料
                         */
                        customChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                        customChatBarDialog.setUser(user, 3, groupid);
                        customChatBarDialog.show();
                    }
                }
            }
        });
    }

    static class GetMemberOnlineCallback implements HttpCallBack{
        private WeakReference<HttpCallBack> mHttpCallBack;
        public GetMemberOnlineCallback(HttpCallBack callback){
            mHttpCallBack = new WeakReference<HttpCallBack>(callback);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            HttpCallBack callback = mHttpCallBack.get();
            if(callback!=null){
                callback.onGeneralSuccess(result,flag);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            HttpCallBack callback = mHttpCallBack.get();
            if(callback!=null){
                callback.onGeneralError(e,flag);
            }
        }
    }

    private void loadPageNum(int pagesize, int pageno) {
        getOnLineMemberFlag = GroupHttpProtocol.getMemberOnLine(mContext, groupid, pagesize, pageno, new GetMemberOnlineCallback(this));
        if (getOnLineMemberFlag < 0) {
            handleDataFail(104, getOnLineMemberFlag);
        }

    }

    /**
     *点击送礼时，隐藏在线成员列表
     */
    @Subscribe
    public void dismissChatbarMemberDialog(Object obj)
    {
        dismiss();
    }
    /**
     * 处理错误数据
     *
     * @param e
     * @param flag
     */
    private void handleDataFail(int e, long flag) {
        if (flag == getOnLineMemberFlag) {
            lvChatbarMember.onRefreshComplete();
            ErrorCode.toastError(mContext, e);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == getOnLineMemberFlag) {
            lvChatbarMember.onRefreshComplete();
            onLineUser = GsonUtil.getInstance().getServerBean(result, GroupOnlineUser.class);
            if (onLineUser != null) {
                if (onLineUser.isSuccess()) {
                    mCurrentPage = onLineUser.getPage_no();
                    if (mCurrentPage == 1) {
                        totalOnline_list.clear();
                        if (onLineUser.getList() != null) {
                            totalOnline_list.addAll(onLineUser.getList());
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        if (onLineUser.getList() != null && onLineUser.getList().size() != 0) {
                            totalOnline_list.addAll(onLineUser.getList());
                        } else {
                            CommonFunction.toastMsg(getContext(), R.string.no_more_data);
                        }
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    handleDataFail(104, flag);
                    ErrorCode.showError(mContext, result);
                    lvChatbarMember.onRefreshComplete();
                }
            } else {
                handleDataFail(104, flag);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    private class ChatBarMemberAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (totalOnline_list != null && totalOnline_list.size() > 0) {
                return totalOnline_list.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(null==totalOnline_list){
                return  null;
            }
            return totalOnline_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chatbar_member, parent, false);
            }
            viewHolder = new ViewHolder(convertView);
            if (totalOnline_list != null) {

                GroupUser groupUser = totalOnline_list.get(position).user;
                user = new User();

                user.setUid(groupUser.getUserid());
                user.setIcon(groupUser.getIcon());
                if(groupUser.getUserid() == Common.getInstance().loginUser.getUid()){
                    String verifyicon = Common.getInstance().loginUser.getVerifyicon();
                    if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                        user.setIcon(verifyicon);
                    }
                }
                user.setNickname(groupUser.getNickname());
                user.setNoteName(groupUser.getNotes());
                user.setSex(groupUser.getGender().equals("m") ? 1 : 0);
                user.setAge(groupUser.getAge());
                user.setDistance(groupUser.getDistance());
                user.setViplevel(groupUser.getViplevel());
                user.setSVip(groupUser.getSvip());
                user.setGroupRole(groupUser.getGroup_role());
                user.setAudio(groupUser.getAudio());
                if (user != null) {
                    /***设置昵称*/
                    viewHolder.tvName.setText((FaceManager.getInstance(mContext).parseIconForString(mContext, user.getNickname(), 13, null)));
                    if (user.getSVip() > 0 || user.getViplevel() > 0) {//如果是会员的话颜色显示红色
                        viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.login_btn));
                    }else
                    {
                        viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.chat_update_edit_user_value));
                    }
                    /***设置年龄和性别*/
                    viewHolder.tvAge.setText(user.getAge() + "");
                    if (user.getGender().equals("m")) {
                        viewHolder.ivSex.setImageResource(R.drawable.thread_register_man_select);
                        viewHolder.rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
                    } else {
                        viewHolder.ivSex.setImageResource(R.drawable.thread_register_woman_select);
                        viewHolder.rlAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                    }
                    /***加载头像包含会员*/
                    viewHolder.userIcon.execute(user);

                    /**展示距离**/
                    int distance = user.getDistance();

                    if (distance > 1000) {
                        distance = distance / 1000;
                        viewHolder.tvDistance.setText(distance + "km");
                    } else {
                        viewHolder.tvDistance.setText(distance + "m");
                    }

                    if (user.getGroupRole() == 0 || user.getGroupRole() == 1) {
                        viewHolder.tvIdentity_manager.setVisibility(View.VISIBLE);
                        if (user.getGroupRole() == 0)
                        {
                            viewHolder.tvIdentity_manager.setBackgroundResource(R.drawable.chatbar_groupowner_bg);
                            viewHolder.tvIdentity_manager.setText(R.string.chatbar_owner);
                        }else if (user.getGroupRole() == 1)
                        {
                            viewHolder.tvIdentity_manager.setBackgroundResource(R.drawable.chatbar_groupmanager_bg);
                            viewHolder.tvIdentity_manager.setText(R.string.chatbar_manager);
                        }

                    } else {
                        viewHolder.tvIdentity_manager.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }

        private class ViewHolder {
            private HeadPhotoView userIcon;
            private TextView tvIdentity_manager;
            private TextView tvName;
            private ImageView ivSex;
            private TextView tvAge;
            private TextView tvDistance;
            private RelativeLayout rlAgeSex;

            public ViewHolder(View convertView) {
                rlAgeSex = (RelativeLayout) convertView.findViewById(R.id.rl_age_sex);
                tvName = (TextView) convertView.findViewById(R.id.tvName);
                userIcon = (HeadPhotoView) convertView.findViewById(R.id.uesrIcon);
                tvIdentity_manager = (TextView) convertView.findViewById(R.id.tvIdentity_manager);
                ivSex = (ImageView) convertView.findViewById(R.id.ivSex);
                tvAge = (TextView) convertView.findViewById(R.id.tvAge);
                tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            }
        }
    }
}
