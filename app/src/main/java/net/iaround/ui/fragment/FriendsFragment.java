package net.iaround.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.interfaces.ContactChangeListener;
import net.iaround.model.database.FriendModel;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.adapter.ContactsAdapterFriend;
import net.iaround.ui.contacts.bean.FriendListBean;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: 好友
 * Author：gh
 * Date: 2016/12/1 16:00
 * Emial：jt_gaohang@163.com
 */
public class FriendsFragment extends BaseFragment implements ContactChangeListener, HttpCallBack {
    private PullToRefreshListView mListView;
    private TextView tvCount;
    private TextView tvNullStr;
    private ImageView ivNoFriends;
    private RelativeLayout rlEmpty;

    private ContactsAdapterFriend adapter;
    private List<FriendListBean.FriendsBean> friends;
    private FriendListBean friendListBean;
    private Context context;
    private ArrayList<User> userList;


    private int mCurPage = 1;
    private int mTotalPage = 1;
    private final int mPageSize = 15;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        destroyWaitDialog();
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
                requestPageData();
            }
        });
    }

    private void initData() {
        if (adapter == null) {
            adapter = new ContactsAdapterFriend(getActivity(), friends);
        }
        mListView.setAdapter(adapter);
        requestPageData();
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            requestPageData();
        }

        @Override
        public void onPullUpToRefresh(
                final PullToRefreshBase<ListView> refreshView) {

            if (mCurPage >= mTotalPage) {
                CommonFunction.toastMsg(getContext(), R.string.no_more_data);
                mListView.onRefreshComplete();
                hideWaitDialog();
            } else {
                mCurPage++;
                requestPageData();
//                mListView.onRefreshComplete();
            }
        }
    };

    public void requestPageData() {
        showWaitDialog();
        FriendHttpProtocol.friendsGet(BaseApplication.appContext, this);
    }

    public void refershData(List<FriendListBean.FriendsBean> been) {

        if (been != null && been.size() > 0) {
            this.friends = been;
            adapter.updataList(friends);
        }else
        {
            tvCount.setVisibility(View.GONE);
            tvNullStr.setVisibility(View.VISIBLE);
            ivNoFriends.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void changeContact(FriendListBean friendListBean) {
        friends = friendListBean.getFriends();
        adapter.updataList(friends);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
        mListView.onRefreshComplete();
        tvNullStr.setVisibility(View.GONE);
        ivNoFriends.setVisibility(View.GONE);
        tvCount.setVisibility(View.VISIBLE);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("users");
        if (jsonArray != null && jsonObject != null )
        {
            if (jsonArray.size() == 0) {
                mListView.setVisibility(View.GONE);
                rlEmpty.setVisibility(View.VISIBLE);
                ivNoFriends.setVisibility(View.VISIBLE);
                tvNullStr.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.GONE);
            }else if (jsonArray.size() > 0)
            {
                mListView.setVisibility(View.VISIBLE);
                rlEmpty.setVisibility(View.GONE);
                ivNoFriends.setVisibility(View.GONE);
                tvCount.setVisibility(View.VISIBLE);
                tvNullStr.setVisibility(View.GONE);
            }
        }
        friends = JSON.parseArray(String.valueOf(jsonArray), FriendListBean.FriendsBean.class);
        if (friends != null) {
            if (getActivity() != null){
                tvCount.setText(friends.size() + getActivity().getResources().getString(R.string.fragment_friend_text));
            }else{
                tvCount.setText(friends.size() + BaseApplication.appContext.getResources().getString(R.string.fragment_friend_text));
            }

        }

        refershData(friends);
        saveFriendDataToUser();
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

    public void saveFriendDataToUser() {
        FriendModel friendModel = FriendModel.getInstance(getActivity());
        // 缓存我的好友
        friendModel.cacheMyFriends(convertFriendToUserList(
                friends));
    }

    /**
     * ArrayList< FriendListBean.FriendsBean > 转成 ArrayList< User >
     */
    public static ArrayList<User> convertFriendToUserList(List<FriendListBean.FriendsBean> contactUsers) {
        ArrayList<User> users = new ArrayList<User>();

        if (contactUsers != null && contactUsers.size() > 0) {
            for (FriendListBean.FriendsBean contact : contactUsers) {
                users.add(convertToUserFriend(contact));
            }
        }
        return users;
    }

    private static User convertToUserFriend(FriendListBean.FriendsBean contactUser) {
        if (contactUser != null) {
            User user = new User();

//            user.setDistance(contactUser.distance);
            user.setDistance(contactUser.getDistance());
//            user.setIcon(contactUser.user.icon);
            user.setIcon(contactUser.getUser().getIcon());
//            user.setUid(contactUser.user.userid);
            user.setUid(contactUser.getUser().getUserid());
//            user.setNoteName(contactUser.user.notes);
            user.setNoteName(contactUser.getUser().getNickname());
//            user.setViplevel(contactUser.user.viplevel);
            user.setViplevel(contactUser.getUser().getViplevel());
//            user.setSVip(contactUser.user.svip);
            user.setSVip(contactUser.getUser().getSvip());
//            user.setNickname(contactUser.user.nickname);
            user.setNickname(contactUser.getUser().getNickname());
//            user.setPhotoNum(contactUser.user.photonum);
            user.setPhotoNum(contactUser.getUser().getPhotonum());

            int sex = 2;//contactUser.user.gender
            if (!CommonFunction.isEmptyOrNullStr(contactUser.getUser().getGender())) {
                if (contactUser.getUser().getGender().equals("m")) {
                    sex = 1;
                } else if (contactUser.getUser().getGender().equals("f")) {
                    sex = 2;
                }
            }
            user.setSex(sex);
//            user.setAge(contactUser.user.age);
            user.setAge(contactUser.getUser().getAge());
//            user.setLat(contactUser.user.lat);
            user.setLat(contactUser.getUser().getLat());
//            user.setLng(contactUser.user.lng);
            user.setLng(contactUser.getUser().getLng());
//            user.setPersonalInfor(contactUser.user.selftext)
            user.setPersonalInfor(contactUser.getUser().getSelftext());
//            user.setSign(contactUser.user.selftext);
            user.setSign(contactUser.getUser().getSelftext());
//            user.setOnline(contactUser.user.isOnline());
            user.setOnline(contactUser.getUser().online());
//            user.setForbid(contactUser.user.isForbidUser());
            user.setForbid(contactUser.getUser().forbidUser());
//            user.setLastLoginTime(contactUser.user.lastonlinetime);
            user.setLastLoginTime(contactUser.getUser().getLastonlinetime());
//            user.setWeibo(contactUser.user.weibo);
            user.setWeibo(contactUser.getUser().getWeibo());
//            UserRelationLink link = new UserRelationLink();
//            link.middle = link.new MiddleNode();
//            link.middle.contact = contactUser.contact;
//            link.middle.contact = contactUser.contact;
//            user.setRelationLink(link);//jiqiang 朋友列表没有关系这个字段

            return user;
        }
        return null;
    }
}
