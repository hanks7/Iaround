package net.iaround.ui.fragment;

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
import net.iaround.ui.adapter.ContactsAdapterFans;
import net.iaround.ui.contacts.bean.FriendListBean;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.space.bean.UserRelationLink;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Class: 粉丝
 * Author：gh
 * Date: 2016/12/1 16:00
 * Emial：jt_gaohang@163.com
 */
public class FansFragment extends BaseFragment{

    private PullToRefreshListView mListView;
    private TextView tvCount;
    private TextView tvNullStr;
    private ImageView ivNoFans;
    private RelativeLayout rlEmpty;

    private ContactsAdapterFans adapter;
    private List<FriendListBean.AttentionsBean> fansList;
    private List<FriendListBean.AttentionsBean> totalFansList;

    private int mCurPage = 1;
    private int mTotalPage = 1;
    private final int mPageSize = 15;
    private int PAGE_SIZE = 20;
    private int totalPage = 0;

    private int mCurrentPage = 0;
    private int mTotalNum;

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
        ivNoFans = (ImageView) getView().findViewById(R.id.iv_no_friends);
        rlEmpty = (RelativeLayout) getView().findViewById(R.id.empty);

        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mListView.getRefreshableView().setFastScrollEnabled(false);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 0;
                requestPageData(mCurrentPage + 1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (mCurrentPage < mTotalPage) {
                    requestPageData(mCurrentPage + 1);
                    hideWaitDialog();
                    mListView.onRefreshComplete();
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
        });

        tvNullStr.setText(getResources().getString(R.string.contacts_no_fans));
        ivNoFans.setImageResource(R.drawable.contacts_no_attention);

        rlEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPageData(1);
            }
        });
    }

    private void initData() {
        totalFansList = new ArrayList<>();
        if (adapter == null)
            adapter = new ContactsAdapterFans(getActivity(), totalFansList);

        mListView.setAdapter(adapter);

        mCurPage = 1;
        requestPageData(mCurPage);
    }


    public void requestPageData(int pageNo) {
        showWaitDialog();
        FriendHttpProtocol.userFans(getContext(), Common.getInstance().loginUser.getUid(), pageNo, PAGE_SIZE, new FansHttpCallBack(this));
    }


    private void refershData(List<FriendListBean.AttentionsBean> fansList) {
        if (fansList != null && fansList.size() > 0) {
            adapter.updataList(fansList);
        } else {
            tvCount.setVisibility(View.GONE);
            tvNullStr.setVisibility(View.VISIBLE);
            ivNoFans.setVisibility(View.VISIBLE);
        }

    }

    static class FansHttpCallBack implements HttpCallBack{
        private WeakReference<FansFragment> mFansFragment;
        public FansHttpCallBack(FansFragment fansFragment){
            mFansFragment = new WeakReference<FansFragment>(fansFragment);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            FansFragment fansFragment = mFansFragment.get();
            if(fansFragment == null) return;

            fansFragment.hideWaitDialog();
            fansFragment.mListView.onRefreshComplete();

            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("fans");
            if (jsonObject != null && jsonArray != null) {
                if (jsonArray.size() == 0) {
                    fansFragment.mListView.setVisibility(View.GONE);
                    fansFragment.rlEmpty.setVisibility(View.VISIBLE);
                    fansFragment.ivNoFans.setVisibility(View.VISIBLE);
                    fansFragment.tvCount.setVisibility(View.GONE);
                    fansFragment.tvNullStr.setVisibility(View.VISIBLE);
                } else if (jsonArray.size() > 0) {
                    fansFragment.mListView.setVisibility(View.VISIBLE);
                    fansFragment.rlEmpty.setVisibility(View.GONE);
                    fansFragment.ivNoFans.setVisibility(View.GONE);
                    fansFragment.tvCount.setVisibility(View.VISIBLE);
                    fansFragment.tvNullStr.setVisibility(View.GONE);
                    fansFragment.fansList = JSON.parseArray(String.valueOf(jsonArray), FriendListBean.AttentionsBean.class);
                }

            }

            if (jsonObject != null )
            {
                if (jsonObject.containsKey("pageno")) {
                    fansFragment.mCurrentPage = jsonObject.getInteger("pageno");
                }
                if(fansFragment.mCurrentPage==1){
                    fansFragment.totalFansList.clear();
                }

                if (jsonObject.containsKey("amount")) {
                    fansFragment.mTotalNum = jsonObject.getInteger("amount");
                }else{
                    fansFragment.mTotalNum = 0;
                }
                fansFragment.mTotalPage = fansFragment.mTotalNum / fansFragment.PAGE_SIZE;
                if (fansFragment.mTotalPage % fansFragment.PAGE_SIZE > 0) {
                    fansFragment.mTotalPage++;
                }

                if (fansFragment.fansList != null) {
                    fansFragment.tvCount.setText(fansFragment.mTotalNum + BaseApplication.appContext.getResources().getString(R.string.fragment_fans_text));
                    fansFragment.totalFansList.addAll(fansFragment.fansList);
                }
            }

            fansFragment.refershData(fansFragment.totalFansList);
            fansFragment.saveFansDataToUser();
        }

        @Override
        public void onGeneralError(int e, long flag) {
            FansFragment fansFragment = mFansFragment.get();
            if(fansFragment == null) return;

            fansFragment.hideWaitDialog();
            fansFragment.mListView.onRefreshComplete();
            fansFragment.tvCount.setVisibility(View.GONE);
            fansFragment.tvNullStr.setVisibility(View.VISIBLE);
            fansFragment.ivNoFans.setVisibility(View.VISIBLE);
        }
    }


    public void saveFansDataToUser() {
        FriendModel friendModel = FriendModel.getInstance(getContext());
        // 缓存我的好友
        friendModel.cacheMyFans(convertFansToUserList(totalFansList));
    }

    /**
     * ArrayList< FriendListBean.AttentionsBean > 转成 ArrayList< User >
     */
    public static ArrayList<User> convertFansToUserList(List<FriendListBean.AttentionsBean> contactUsers) {
        ArrayList<User> users = new ArrayList<User>();

        if (contactUsers != null && contactUsers.size() > 0) {
            for (FriendListBean.AttentionsBean contact : contactUsers) {
                users.add(convertToUserFans(contact));
            }
        }
        return users;
    }

    private static User convertToUserFans(FriendListBean.AttentionsBean contactUser) {
        if (contactUser != null) {
            User user = new User();
            user.setDistance(contactUser.getDistance());
            user.setIcon(contactUser.getUser().getIcon());
            user.setUid(contactUser.getUser().getUserid());
            user.setNoteName(contactUser.getUser().getNickname());
            user.setViplevel(contactUser.getUser().getViplevel());
            user.setSVip(contactUser.getUser().getSvip());
            user.setNickname(contactUser.getUser().getNickname());
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
            user.setLastLoginTime(contactUser.getUser().getLastonlinetime());
            user.setWeibo(contactUser.getUser().getWeibo());
            UserRelationLink link = new UserRelationLink();
            link.middle = link.new MiddleNode();
            link.middle.contact = contactUser.getRelation();
            user.setRelationLink(link);

            return user;
        }
        return null;
    }
}
