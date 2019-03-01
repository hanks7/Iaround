package net.iaround.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.database.FriendModel;
import net.iaround.model.entity.ChatbarFriendsBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.ChatbarFriendsAdapter;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.seach.news.SearchChatBarActivity;

import java.util.ArrayList;
import java.util.List;

/**
 *聊吧邀请聊天，邀请好友腿甲界面 
 */
public class ChatbarRecommendFragment extends BaseFragment implements HttpCallBack{

    /**头布局搜索框*/
    private View headSearchView;
    private LinearLayout llSearch;
    private LinearLayout mLlEmptyView;
    private TextView searchText;// 输入搜索关键字

    private PullToRefreshListView mListView;
    private TextView tvCount;
    private TextView tvNullStr;
    private ImageView ivNoAttention;
    private RelativeLayout rlEmpty;

    private ChatbarFriendsAdapter adapter;
    private List<ChatbarFriendsBean.UsersBean> usersBeen;
    private ArrayList<Boolean> userSelected;
    
    private int mCurPage = 1;
    private int mTotalPage = 1;
    private final int mPageSize = 15;

    private int pageNo = 1;//加载页数
    private int pageSize = 20;//每页内容数


    private int mCurrentPage = 0;
    private int PAGE_SIZE = 20;
    private int mTotalNum;

    private String groupid;

    private List<Integer> selectUser;
    private InviteRecommendListCallback inviteRecommendListCallback;

    public ChatbarRecommendFragment(){

    }

    @SuppressLint("ValidFragment")
    public ChatbarRecommendFragment(String groupid, InviteRecommendListCallback inviteRecommendListCallback)
    {
        this.groupid = groupid;
        this.inviteRecommendListCallback = inviteRecommendListCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbar_recommend, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersBeen = new ArrayList<>();
        userSelected = new ArrayList<>();
        selectUser = new ArrayList<>();
        initView();
        initData();
    }


    private void initView() {
        if (getView() == null)
            return;
        headSearchView = LayoutInflater.from(getActivity()).inflate(R.layout.char_bar_fragment_popular_search_headview, null);//chat_bar_fragment_popular_search_headview
        llSearch = (LinearLayout) headSearchView.findViewById(R.id.edit_layout);
        mListView = (PullToRefreshListView) getView().findViewById(R.id.pull_contacts);
        tvCount = (TextView) getView().findViewById(R.id.tv_contacts_count);
        tvNullStr = (TextView) getView().findViewById(R.id.nullStr);
        ivNoAttention = (ImageView) getView().findViewById(R.id.iv_no_friends);
        rlEmpty = (RelativeLayout) getView().findViewById(R.id.empty);

        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mListView.getRefreshableView().setFastScrollEnabled(false);
        mListView.setOnRefreshListener(mOnRefreshListener);
        mListView.getRefreshableView().addHeaderView(headSearchView);

        tvNullStr.setText(getResources().getString(R.string.contacts_no_attentin));
        ivNoAttention.setImageResource(R.drawable.contacts_no_attention);

        searchText = (TextView) headSearchView.findViewById(R.id.edittext_keyword);

        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchChatBarActivity.class);
                startActivity(intent);
            }
        });
        rlEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPageData(1);
            }
        });
    }

    private void initData() {
        if (usersBeen == null)
            usersBeen = new ArrayList<>();
        if (userSelected == null)
            userSelected = new ArrayList<>();
        if (adapter == null)
            adapter = new ChatbarFriendsAdapter(getActivity(), usersBeen, userSelected, null, new ChatbarFriendsAdapter.InviteRecommendListCallback() {
                @Override
                public void getInviteRecommendList(List<Boolean> inviteUsers) {
                    selectUser.clear();
                    if (inviteUsers != null && inviteUsers.size() > 0)
                        for (int i = 0; i < inviteUsers.size(); i++) {
                            if (!inviteUsers.get(i))
                                continue;
                            selectUser.add(usersBeen.get(i).getUser().getUserid());
                        }
                    inviteRecommendListCallback.getInviteRecommendList(selectUser);
                }
            });
        mListView.setAdapter(adapter);
        requestPageData(pageNo);
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            mCurrentPage = 1;
            requestPageData(mCurrentPage);
        }

        @Override
        public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
            //没有上拉加载更多的功能
//            if (mCurrentPage < mTotalPage) {
//                ++mCurrentPage;
//                requestPageData(mCurrentPage);
//                hideWaitDialog();
//                mListView.onRefreshComplete();
//            } else {
//                mListView.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        hideWaitDialog();
//                        mListView.onRefreshComplete();
//                        CommonFunction.toastMsg(getContext(), R.string.no_more_data);
//
//                    }
//                }, 200);
//            }
        }
    };

    public void requestPageData(int pageNo) {
        showWaitDialog();
        GroupHttpProtocol.getRecommendFriendsList(BaseApplication.appContext,this);
    }


    public void refershData(List<ChatbarFriendsBean.UsersBean> bean) {
        this.usersBeen = bean;
        if (bean != null) {
            adapter.updataList(usersBeen,userSelected);
        } else {
            tvCount.setVisibility(View.GONE);
            tvNullStr.setVisibility(View.VISIBLE);
            ivNoAttention.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
        mListView.onRefreshComplete();
        tvNullStr.setVisibility(View.GONE);
        tvCount.setVisibility(View.GONE);
        if (result == null)
            return;
        ChatbarFriendsBean friendUsersBean = GsonUtil.getInstance().getServerBean(result,ChatbarFriendsBean.class);
        handleFriendsData(result,friendUsersBean);
        saveAttentionDataToUser();
    }

    /**
     * 处理成功数据
     * @param result
     * @param bean
     */
    private void handleFriendsData(String result, ChatbarFriendsBean bean)
    {
        if (bean == null)
            return;
        userSelected.clear();
        usersBeen.clear();
        if (bean.isSuccess())
        {
            if (bean.getFreshman() == null || bean.getFreshman().size() == 0) {
                rlEmpty.setVisibility(View.VISIBLE);
                return;
            }
            rlEmpty.setVisibility(View.GONE);
            usersBeen.addAll(bean.getFreshman());
            for (int i = 0; i < usersBeen.size(); i++) {
                userSelected.add(false);
            }
            refershData(usersBeen);
            saveAttentionDataToUser();
        }
        if (bean.status == -100)
        {
            ErrorCode.showError(BaseApplication.appContext,result);
        }
    }
    @Override
    public void onGeneralError(int e, long flag) {
        hideWaitDialog();
        mListView.onRefreshComplete();
        rlEmpty.setVisibility(View.VISIBLE);
        tvCount.setVisibility(View.GONE);
        tvNullStr.setVisibility(View.VISIBLE);
        ivNoAttention.setVisibility(View.VISIBLE);
    }

    public void saveAttentionDataToUser() {
        FriendModel friendModel = FriendModel.getInstance(getActivity());
        // 缓存我的好友
        friendModel.cacheMyFollows(convertAttentionToUserList(
                usersBeen));
    }

    /**
     * ArrayList< FriendListBean.AttentionsBean > 转成 ArrayList< User >
     */
    public static ArrayList<User> convertAttentionToUserList(List<ChatbarFriendsBean.UsersBean> contactUsers) {
        ArrayList<User> users = new ArrayList<User>();

        if (contactUsers != null && contactUsers.size() > 0) {
            for (ChatbarFriendsBean.UsersBean contact : contactUsers) {
                users.add(convertToUserAttention(contact));
            }
        }
        return users;
    }

    private static User convertToUserAttention(ChatbarFriendsBean.UsersBean contactUser) {
        if (contactUser != null) {
            User user = new User();

//            user.setDistance(contactUser.getDistance());
            user.setIcon(contactUser.getUser().getIcon());
            user.setUid(contactUser.getUser().getUserid());
            user.setNoteName(contactUser.getUser().getNickname());
            user.setViplevel(contactUser.getUser().getViplevel());
            user.setSVip(contactUser.getUser().getSvip());
            user.setNickname(contactUser.getUser().getNickname());
//            user.setPhotoNum(contactUser.getUser().getPhotonum());

            int sex = 2;//contactUser.user.gender
            if (!CommonFunction.isEmptyOrNullStr(contactUser.getUser().getGender())) {
                if (contactUser.getUser().getGender().equals("m")) {
                    sex = 1;
                } else if (contactUser.getUser().getGender().equals("f")) {
                    sex = 2;
                }
            }
            user.setSex(sex);
            user.setAge(contactUser.getUser().getAge());
//            user.setLat(contactUser.getUser().getLat());
//            user.setLng(contactUser.getUser().getLng());
//            user.setPersonalInfor(contactUser.getUser().getSelftext());
//            user.setSign(contactUser.getUser().getSelftext());
//            user.setLastLoginTime(contactUser.getUser().getLastonlinetime());
//            user.setWeibo(contactUser.getUser().getWeibo());
//            UserRelationLink link = new UserRelationLink();
//            link.middle = link.new MiddleNode();
//            link.middle.contact = contactUser.getRelation();
//            user.setRelationLink(link);

            return user;
        }
        return null;
    }
    /**
     * 邀请推荐用户的回调
     */
    public interface InviteRecommendListCallback
    {
        void getInviteRecommendList(List<Integer> inviteUsers);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyWaitDialog();
    }
}
