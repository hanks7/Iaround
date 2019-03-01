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
import net.iaround.conf.Common;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.model.database.FriendModel;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.adapter.ContactsAdapterAttention;
import net.iaround.ui.contacts.bean.FriendListBean;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.space.bean.UserRelationLink;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: 关注
 * Author：gh
 * Date: 2016/12/1 16:00
 * Emial：jt_gaohang@163.com
 */
public class AttentionFragment extends BaseFragment implements HttpCallBack {

    private PullToRefreshListView mListView;
    private TextView tvCount;
    private TextView tvNullStr;
    private ImageView ivNoAttention;
    private RelativeLayout rlEmpty;

    private ContactsAdapterAttention adapter;
    //    private ArrayList<Contacts> contactses;
    private List<FriendListBean.AttentionsBean> attentions;
    private List<FriendListBean.AttentionsBean> totalAttentions;
    private Context context;
    private FriendListBean friendListBean;
    private ArrayList<User> userList;

    private int mCurPage = 1;
    private int mTotalPage = 1;
    private final int mPageSize = 15;

    private int pageNo = 1;//加载页数
    private int pageSize = 20;//每页内容数


    private int mCurrentPage = 0;
    private int PAGE_SIZE = 20;
    private int mTotalNum = 0;

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
        mListView = (PullToRefreshListView) getView().findViewById(R.id.pull_contacts);
        tvCount = (TextView) getView().findViewById(R.id.tv_contacts_count);
        tvNullStr = (TextView) getView().findViewById(R.id.nullStr);
        ivNoAttention = (ImageView) getView().findViewById(R.id.iv_no_friends);
        rlEmpty = (RelativeLayout) getView().findViewById(R.id.empty);

        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mListView.getRefreshableView().setFastScrollEnabled(false);
        mListView.setOnRefreshListener(mOnRefreshListener);

        tvNullStr.setText(getResources().getString(R.string.contacts_no_attentin));
        ivNoAttention.setImageResource(R.drawable.contacts_no_attention);

        rlEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPageData(1);
            }
        });
    }

    private void initData() {
        totalAttentions = new ArrayList<>();
        if (adapter == null)
            adapter = new ContactsAdapterAttention(getActivity(), totalAttentions);

        mListView.setAdapter(adapter);
        requestPageData(pageNo);
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//            requestPageData(pageNo);


            mCurrentPage = 0;
            requestPageData(mCurrentPage + 1);
//            requestData(mCurrentPage + 1);
        }

        @Override
        public void onPullUpToRefresh(
                final PullToRefreshBase<ListView> refreshView) {

//            if (mCurPage >= mTotalPage) {
//                CommonFunction.toastMsg(getContext(), R.string.no_more_data);
//                mListView.onRefreshComplete();
//                hideWaitDialog();
//            } else {
//                mCurPage++;
//                requestPageData(mCurPage);
//                mListView.onRefreshComplete();
//            }


            if (mCurrentPage < mTotalPage) {
                requestPageData(mCurrentPage + 1);
                hideWaitDialog();
                mListView.onRefreshComplete();
//                requestData(mCurrentPage + 1);
            } else {
                mListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        hideWaitDialog();
                        mListView.onRefreshComplete();
                        CommonFunction.toastMsg(getContext(), R.string.no_more_data);

                    }
                }, 200);
            }
        }
    };

    public void requestPageData(int pageNo) {
        showWaitDialog();
        FriendHttpProtocol.userAttentions(BaseApplication.appContext, Common.getInstance().loginUser.getUid()
                , pageNo, pageSize, this);
    }


    public void refershData(List<FriendListBean.AttentionsBean> bean) {
        this.totalAttentions = bean;
        if (bean != null) {
            adapter.updataList(totalAttentions);
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
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("fans");

        if (jsonArray != null && jsonObject != null) {
            if (jsonArray.size() == 0) {
                mListView.setVisibility(View.GONE);
                rlEmpty.setVisibility(View.VISIBLE);
                ivNoAttention.setVisibility(View.VISIBLE);
                tvNullStr.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.GONE);
            } else if (jsonArray.size() > 0) {
                mListView.setVisibility(View.VISIBLE);
                rlEmpty.setVisibility(View.GONE);
                ivNoAttention.setVisibility(View.GONE);
                tvCount.setVisibility(View.VISIBLE);
                tvNullStr.setVisibility(View.GONE);
            }

        }

        attentions = JSON.parseArray(String.valueOf(jsonArray), FriendListBean.AttentionsBean.class);

        if (jsonObject != null) {
            //YC
            if (jsonObject.containsKey("pageno")) {
                mCurrentPage = jsonObject.getInteger("pageno");
            }
            if(mCurrentPage==1){
                totalAttentions.clear();
            }
            if (jsonObject.containsKey("amount")){
                mTotalNum = jsonObject.getInteger("amount");
                mTotalPage = mTotalNum / PAGE_SIZE;
            }

            if (mTotalPage % PAGE_SIZE > 0) {
                mTotalPage++;
            }

            if (attentions != null) {
                tvCount.setText(mTotalNum + BaseApplication.appContext.getResources().getString(R.string.fragment_attention_text));
                totalAttentions.addAll(attentions);
            }
        }
        refershData(totalAttentions);
        saveAttentionDataToUser();
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
                totalAttentions));
    }

    /**
     * ArrayList< FriendListBean.AttentionsBean > 转成 ArrayList< User >
     */
    public static ArrayList<User> convertAttentionToUserList(List<FriendListBean.AttentionsBean> contactUsers) {
        ArrayList<User> users = new ArrayList<User>();

        if (contactUsers != null && contactUsers.size() > 0) {
            for (FriendListBean.AttentionsBean contact : contactUsers) {
                users.add(convertToUserAttention(contact));
            }
        }
        return users;
    }

    private static User convertToUserAttention(FriendListBean.AttentionsBean contactUser) {
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
            user.setAge(contactUser.getUser().getAge());
            user.setLat(contactUser.getUser().getLat());
            user.setLng(contactUser.getUser().getLng());
            user.setPersonalInfor(contactUser.getUser().getSelftext());
            user.setSign(contactUser.getUser().getSelftext());
//            user.setOnline(contactUser.user.isOnline());//jiqiang 关系列表没有关系这个字段
//            user.setOnline(contactUser.getUser().online());
//            user.setForbid(contactUser.user.isForbidUser());
//            user.setForbid(contactUser.getUser().forbidUser());
//            user.setLastLoginTime(contactUser.user.lastonlinetime);
            user.setLastLoginTime(contactUser.getUser().getLastonlinetime());
//            user.setWeibo(contactUser.user.weibo);
            user.setWeibo(contactUser.getUser().getWeibo());
            UserRelationLink link = new UserRelationLink();
            link.middle = link.new MiddleNode();
//            link.middle.contact = contactUser.contact;
            link.middle.contact = contactUser.getRelation();
            user.setRelationLink(link);

            return user;
        }
        return null;
    }
}
