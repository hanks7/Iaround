package net.iaround.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.model.database.FriendModel;
import net.iaround.model.entity.ChatbarFriendsBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.ChatbarFriendsAdapter;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/15 17:26
 * Email：15369302822@163.com
 */

public class ChatbarFriendsFragment extends BaseFragment implements HttpCallBack{

    private PullToRefreshListView mListView;
    private TextView tvCount;
    private TextView tvNullStr;
    private ImageView ivNoFriends;
    private RelativeLayout rlEmpty;

    private ChatbarFriendsAdapter adapter;
    private List<ChatbarFriendsBean.UsersBean> friends;
    private ArrayList<Boolean> checkUsers;

    private int mCurPage = 1;//当前数据页码
    private int mTotalPage = 1;//总页数
    private int amount;//总数量
    private final int mPageSize = 15;//每页展示数量
    private int pageno = 1;

    private ArrayList<Integer> selectUsers;
    private InviteFriendsListCallback inviteFriendsListCallback;

    public ChatbarFriendsFragment(){

    }

    @SuppressLint("ValidFragment")
    public ChatbarFriendsFragment(InviteFriendsListCallback inviteFriendsListCallback)
    {
        this.inviteFriendsListCallback = inviteFriendsListCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbar_friends2, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friends = new ArrayList<>();
        checkUsers = new ArrayList<>();
        selectUsers = new ArrayList<>();
        initView();
        initData();
    }

    private void initView() {
        if (getView() == null)
            return;
        ivNoFriends = (ImageView) getView().findViewById(R.id.iv_no_friends);
        mListView = (PullToRefreshListView) getView().findViewById(R.id.pull_contacts);
        tvCount = (TextView) getView().findViewById(R.id.tv_contacts_count);
        tvNullStr = (TextView) getView().findViewById(R.id.nullStr);
        rlEmpty = (RelativeLayout) getView().findViewById(R.id.empty);

        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mListView.getRefreshableView().setFastScrollEnabled(false);
        mListView.setOnRefreshListener(mOnRefreshListener);
        tvNullStr.setText(getResources().getString(R.string.contacts_no_friends));

        rlEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPageData(pageno);
            }
        });

    }

    private void initData() {

        if (adapter == null) {
            adapter = new ChatbarFriendsAdapter(getActivity(), friends, checkUsers, new ChatbarFriendsAdapter.InviteFriendsListCallback() {
                @Override
                public void getInviteFriendList(List<Boolean> inviteUsers) {
                    selectUsers.clear();
                    if (inviteUsers != null && inviteUsers.size() > 0)
                        for (int i = 0; i < inviteUsers.size(); i++) {
                            if (!inviteUsers.get(i))
                                continue;
                            selectUsers.add(friends.get(i).getUser().getUserid());
                        }
                    inviteFriendsListCallback.getInviteFriendList(selectUsers);
                }
            }, null);
        }
        mListView.setAdapter(adapter);
        requestPageData(pageno);
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            mCurPage = 1;
            requestPageData(mCurPage);
        }

        @Override
        public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {

            if (mCurPage >= mTotalPage) {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonFunction.toastMsg(getContext(), R.string.no_more_data);
                        mListView.onRefreshComplete();
                        hideWaitDialog();
                    }
                },200);
            } else {
                mCurPage++;
                requestPageData(mCurPage);
            }
        }
    };

    public void requestPageData(int pageno) {
        showWaitDialog();
//        FriendHttpProtocol.userAttentions(BaseApplication.appContext, Common.getInstance().loginUser.getUid()
//                , pageno, mPageSize, this);
        FriendHttpProtocol.friendsGet(BaseApplication.appContext,this);
    }


    public void refershData(List<ChatbarFriendsBean.UsersBean> users) {
        if (users != null && users.size() > 0)
        {
            this.friends = users;
            adapter.updataList(friends,checkUsers);
        }else
        {
            tvCount.setVisibility(View.GONE);
            tvNullStr.setVisibility(View.VISIBLE);
            ivNoFriends.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
        mListView.onRefreshComplete();
        tvNullStr.setVisibility(View.GONE);
        ivNoFriends.setVisibility(View.GONE);
        tvCount.setVisibility(View.GONE);
        if (result == null)
            return;
        ChatbarFriendsBean friendUsersBean = GsonUtil.getInstance().getServerBean(result,ChatbarFriendsBean.class);
        handleFriendsData(result,friendUsersBean);
    }

    @Override
    public void onGeneralError(int e, long flag) {
        hideWaitDialog();
        mListView.onRefreshComplete();
        rlEmpty.setVisibility(View.VISIBLE);
        tvCount.setVisibility(View.GONE);
        tvNullStr.setVisibility(View.VISIBLE);
        ivNoFriends.setVisibility(View.VISIBLE);
    }
    private void handleFriendsData(String result, ChatbarFriendsBean bean)
    {
        if (bean == null)
            return;
        checkUsers.clear();
        friends.clear();
        if (bean.isSuccess())
        {
            if (bean.getUsers() == null) {
                rlEmpty.setVisibility(View.VISIBLE);
                return;
            }
            mCurPage = bean.getPageno();
            amount = bean.getAmount();

            if (mCurPage == 1)
            {
                friends.clear();
            }else
            {
                if (amount % mPageSize == 0)
                {
                    mTotalPage = amount / mPageSize;
                }else
                {
                    mTotalPage = amount / mPageSize + 1;
                }
            }
            rlEmpty.setVisibility(View.GONE);
            friends.addAll(bean.getUsers());
            for (int i = 0; i < friends.size(); i++) {
                checkUsers.add(false);
            }
            adapter.updataList(friends,checkUsers);
            refershData(friends);
            saveFriendDataToUser();
        }
        if (bean.status == -100)
        {
            ErrorCode.showError(BaseApplication.appContext,result);
        }
    }
    public void saveFriendDataToUser() {
        FriendModel friendModel = FriendModel.getInstance(getActivity());
        // 缓存我的好友
        friendModel.cacheMyFriends(convertFriendToUserList(
                friends));
    }

    /**
     * ArrayList< FriendListBean.FriendsBean > 转成 ArrayList< User >
     */
    public static ArrayList<User> convertFriendToUserList(List<ChatbarFriendsBean.UsersBean> contactUsers) {
        ArrayList<User> users = new ArrayList<User>();

        if (contactUsers != null && contactUsers.size() > 0) {
            for (ChatbarFriendsBean.UsersBean contact : contactUsers) {
                users.add(convertToUserFriend(contact));
            }
        }
        return users;
    }

    private static User convertToUserFriend(ChatbarFriendsBean.UsersBean contactUser) {
        if (contactUser != null) {
            User user = new User();
            ChatbarFriendsBean.UsersBean.UserBean baseUserInfo = contactUser.getUser();
            user.setDistance(contactUser.getDistance());
            user.setIcon(baseUserInfo.getIcon());
            user.setUid(baseUserInfo.getUserid());
            user.setNoteName(baseUserInfo.getNickname());
            user.setViplevel(baseUserInfo.getViplevel());
            user.setSVip(baseUserInfo.getSvip());
            user.setNickname(baseUserInfo.getNotes());
            user.setPhotoNum(baseUserInfo.getPhotonum());

            int sex = 2;
            if (!CommonFunction.isEmptyOrNullStr(baseUserInfo.getGender())) {
                if (baseUserInfo.getGender().equals("m")) {
                    sex = 1;
                } else if ("f".equals(baseUserInfo.getGender())) {
                    sex = 2;
                }
            }
            user.setSex(sex);
            user.setAge(baseUserInfo.getAge());
            user.setLat(baseUserInfo.getLat());
            user.setLng(baseUserInfo.getLng());
            user.setPersonalInfor(baseUserInfo.getSelftext());
            user.setOnline(!"n".endsWith(baseUserInfo.getIsonline()));
            user.setForbid(baseUserInfo.getForbid() == 0);
            user.setLastLoginTime(baseUserInfo.getLastonlinetime());
            user.setCharisma(baseUserInfo.getCharmnum());
            user.setWeibo(baseUserInfo.getWeibo());

            return user;
        }
        return null;
    }

    /**
     * 邀请好友的回调
     */
    public interface InviteFriendsListCallback
    {
        void getInviteFriendList(List<Integer> inviteUsers);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyWaitDialog();
    }
}
