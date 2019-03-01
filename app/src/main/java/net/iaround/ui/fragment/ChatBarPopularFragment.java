package net.iaround.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.ChatBarHttpProtocol;
import net.iaround.model.chatbar.ChatBarAttenttion;
import net.iaround.model.chatbar.ChatBarAttenttion.AttentionBean;
import net.iaround.model.chatbar.ChatBarPopularType;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.ui.adapter.ChatBarCommonAdapter;
import net.iaround.ui.chatbar.SearchBar;
import net.iaround.ui.chatbar.SearchListView;
import net.iaround.ui.datamodel.ResourceBanner;
import net.iaround.ui.seach.news.SearchChatBarActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * 聊吧 热门 界面
 */
public class ChatBarPopularFragment extends LazyLoadBaseFragment implements View.OnClickListener, HttpCallBack, LocationUtil.MLocationListener {
    private SearchListView mLvContent;
    private SearchBar mSearchBar;

    private ChatBarCommonAdapter chatBarCommonAdapter;
    private List<AttentionBean> mAttentionList;//展示其他数据的集合
    private List<AttentionBean> mAttentionRecommendList;//展示推荐数据的集合


    private List<AttentionBean> mAttentionListTotal;
    private List<ResourceBanner> mCahtResourceBanners;

    private GeoData userGeoData;

    /**
     * 页码
     */
    private int pageNum = 1;
    /**
     * 页面展示数量
     */
    private int pageSize = 10;
    /**
     * 请求关注数据的flag
     */
    private long GET_ATTENTION_DATA_FLAG;

    private int mCurrentPage = 0;
    private int mTotalPage = 1;
    private static int CHAT_BAR_POPULAR_UPDATE_DATA = 1;


    private List<ChatBarPopularType> mAttentionPopularTypeList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHAT_BAR_POPULAR_UPDATE_DATA) {
                if (mCurrentPage == 1) {
                    mAttentionListTotal.clear();
                    mAttentionPopularTypeList.clear();
                }
                if (mAttentionList != null && !mAttentionList.isEmpty()) {
                    mAttentionListTotal.clear();
                    mAttentionListTotal.addAll(mAttentionList);
                }

                if (mCurrentPage == 1) {
                    converPopularToType(mAttentionListTotal, mAttentionRecommendList);
                } else {
                    addTypeData(mAttentionList);
                }
                if (!mAttentionPopularTypeList.isEmpty()) {
                    getLlLoadFailed().setVisibility(View.GONE);
                    chatBarCommonAdapter.updateData(mAttentionPopularTypeList);
                } else {
                    showEmptyView();
                }
            }
        }
    };

    private void addTypeData(List<AttentionBean> mAttentionListTotal) {
        if (mAttentionListTotal != null) {
            if (!mAttentionListTotal.isEmpty()) {

                int pushCount = mAttentionListTotal.size();
                if (pushCount % 2 == 0) {
                    int push = pushCount / 2;
                    for (int i = 1; i <= push; i++) {
                        ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                        chatBarPushGroupType1.setType(4);
                        chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(2 * i - 2));
                        chatBarPushGroupType1.setObjectRight(mAttentionListTotal.get(2 * i - 1));
                        mAttentionPopularTypeList.add(chatBarPushGroupType1);
                    }
                } else {
                    if (pushCount == 1) {
                        ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                        chatBarPushGroupType1.setType(4);
                        chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(0));
                        chatBarPushGroupType1.setObjectRight(null);
                        mAttentionPopularTypeList.add(chatBarPushGroupType1);
                    } else {
                        int push = pushCount / 2 + 1;
                        for (int i = 1; i <= push - 1; i++) {
                            ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                            chatBarPushGroupType1.setType(4);
                            chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(2 * i - 2));
                            chatBarPushGroupType1.setObjectRight(mAttentionListTotal.get(2 * i - 1));
                            mAttentionPopularTypeList.add(chatBarPushGroupType1);

                        }
                        ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                        chatBarPushGroupType1.setType(4);
                        chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(2 * push - 2));
                        chatBarPushGroupType1.setObjectRight(null);
                        mAttentionPopularTypeList.add(chatBarPushGroupType1);
                    }

                }

            }
        }

    }

    public void converPopularToType(List<AttentionBean> mAttentionListTotal, List<AttentionBean> mAttentionRecommendList) {
        if (mCurrentPage == 1) {
            if (mAttentionRecommendList != null) {
                if (!mAttentionRecommendList.isEmpty()) {
                    ChatBarPopularType chatBarRecommendTypeTitle = new ChatBarPopularType();
                    chatBarRecommendTypeTitle.setType(1);//代表推荐
                    mAttentionPopularTypeList.add(chatBarRecommendTypeTitle);
                    ChatBarPopularType chatBarRecommendTypeContent = new ChatBarPopularType();
                    chatBarRecommendTypeContent.setType(2);
                    chatBarRecommendTypeContent.setObjectLeft(mAttentionRecommendList);
                    mAttentionPopularTypeList.add(chatBarRecommendTypeContent);
                }
            }
            if (mAttentionListTotal != null) {
                if (!mAttentionListTotal.isEmpty()) {
                    ChatBarPopularType chatBarOtherTypeTitle = new ChatBarPopularType();
                    chatBarOtherTypeTitle.setType(3);
                    mAttentionPopularTypeList.add(chatBarOtherTypeTitle);
                }
            }


            if (!mAttentionListTotal.isEmpty()) {

                int pushCount = mAttentionListTotal.size();
                if (pushCount % 2 == 0) {
                    int push = pushCount / 2;
                    for (int i = 1; i <= push; i++) {
                        ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                        chatBarPushGroupType1.setType(4);
                        chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(2 * i - 2));
                        chatBarPushGroupType1.setObjectRight(mAttentionListTotal.get(2 * i - 1));
                        mAttentionPopularTypeList.add(chatBarPushGroupType1);
                    }
                } else {
                    if (pushCount == 1) {
                        ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                        chatBarPushGroupType1.setType(4);
                        chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(0));
                        chatBarPushGroupType1.setObjectRight(null);
                        mAttentionPopularTypeList.add(chatBarPushGroupType1);
                    } else {
                        int push = pushCount / 2 + 1;
                        for (int i = 1; i <= push - 1; i++) {
                            ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                            chatBarPushGroupType1.setType(4);
                            chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(2 * i - 2));
                            chatBarPushGroupType1.setObjectRight(mAttentionListTotal.get(2 * i - 1));
                            mAttentionPopularTypeList.add(chatBarPushGroupType1);

                        }
                        ChatBarPopularType chatBarPushGroupType1 = new ChatBarPopularType();
                        chatBarPushGroupType1.setType(4);
                        chatBarPushGroupType1.setObjectLeft(mAttentionListTotal.get(2 * push - 2));
                        chatBarPushGroupType1.setObjectRight(null);
                        mAttentionPopularTypeList.add(chatBarPushGroupType1);
                    }

                }

                if (mCahtResourceBanners != null) {
                    if (!mCahtResourceBanners.isEmpty()) {
                        ChatBarPopularType chatBarRecommendTypeTitle = new ChatBarPopularType();
                        chatBarRecommendTypeTitle.setType(5);//代表活动
                        chatBarRecommendTypeTitle.setObjectBanner(mCahtResourceBanners);
                        mAttentionPopularTypeList.add(0, chatBarRecommendTypeTitle);
                    }
                }

            }
        }

    }

    public ChatBarPopularFragment() {
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_chat_bar_popular;
    }

    @Override
    protected boolean lazyLoad() {
        //统计登录后在热门聊吧列表停留的用户人数
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uid", Common.getInstance().getUid());
        MobclickAgent.onEventValue(BaseApplication.appContext, "watch_chat_bar_popular", map, 21000);
        initData();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView(getContentView());
        initListener();
    }

    private void initView(View view) {
        mLvContent = (SearchListView) view.findViewById(R.id.lv_attention);

        mLvContent.setEnableRefresh(true);//设 false

        mSearchBar = new SearchBar(BaseApplication.appContext);
        mSearchBar.setTextEditable(false);

        mSearchBar.getSearchText().setText(getString(R.string.nearby_search_table_group));

        mLvContent.addHeaderView(mSearchBar);

        mLvContent.showHeader(true);
        mLvContent.pullRefreshEnable(true);//下拉刷新
        mLvContent.setAutoFetchMore(false);


        chatBarCommonAdapter = new ChatBarCommonAdapter(getActivity(), mAttentionPopularTypeList);
        mLvContent.setAdapter(chatBarCommonAdapter);

        mLvContent.setOnRefreshListener(new SearchListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 1;
                requestPageData(mCurrentPage, pageSize);
            }
        });

        mSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), SearchChatBarActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initData() {
        mAttentionPopularTypeList = new ArrayList<>();
        mAttentionList = new ArrayList<>();
        mAttentionListTotal = new ArrayList<>();
        mCahtResourceBanners = new ArrayList<>();
        pageNum = 1;
        requestPageData(pageNum, pageSize);
    }

    @Override
    public void onDestroyView() {
        CommonFunction.log("ChatBarPopularFragment", "onDestroyView() into");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        CommonFunction.log("ChatBarPopularFragment", "onDestroy() into");
        super.onDestroy();
    }

    private void initListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_empty_attention:
                //requestPageData(pageNum, pageSize);
                break;
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        CommonFunction.log("ChatBarPopularFragment", "onGeneralSuccess() into, result=" + result + ", flag=" + flag);
        if (Constant.isSuccess(result)) {
            if (flag == GET_ATTENTION_DATA_FLAG) {
                ChatBarAttenttion bean = GsonUtil.getInstance().getServerBean(result, ChatBarAttenttion.class);
                mLvContent.onRefreshComplete();

                if (bean.isSuccess()) {
                    if (bean != null) {
                        mAttentionList = bean.getRemaining();
                        mAttentionRecommendList = bean.getList();
                        mCurrentPage = bean.getPage_no();
                        mTotalPage = bean.getTotal_page();
                        mCahtResourceBanners = bean.getBanner();

                        mHandler.sendEmptyMessage(CHAT_BAR_POPULAR_UPDATE_DATA);
                    }
                } else {
                    mLvContent.onRefreshComplete();
                    showEmptyView();
                    ErrorCode.showError(BaseApplication.appContext, result);
                }
            }
        } else {
            mLvContent.onRefreshComplete();
            showEmptyView();
            ErrorCode.showError(BaseApplication.appContext, result);
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        CommonFunction.log("ChatBarPopularFragment", "onGeneralSuccess() into, flag=" + flag);
        if (GET_ATTENTION_DATA_FLAG == flag) {
            mLvContent.onRefreshComplete();
            showEmptyView();
            ErrorCode.toastError(BaseApplication.appContext, e);
        }
    }

    public void requestPageData(int pageNum, int pageSize) {
        GET_ATTENTION_DATA_FLAG = ChatBarHttpProtocol.getInstance().getChatBarPopularData(getContext(), pageNum, pageSize, this);
        CommonFunction.log("ChatBarPopularFragment", "requestPageData() into, pageNum=" + pageNum + ", pageSize=" + pageSize + ", flag=" + GET_ATTENTION_DATA_FLAG);
    }


    public void showEmptyView() {
        if (mAttentionPopularTypeList.isEmpty()) {
            showLoadFailedView(null);
        }
    }

    @Override
    public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {
        CommonFunction.log("ChatBarPopularFragment", "updateLocation() into, type=" + type + ", lat=" + lat + ", lng=" + lng + ", address=" + address);

        if (this.userGeoData == null) {
            this.userGeoData = LocationUtil.getCurrentGeo(getContext());
        }
    }
}
