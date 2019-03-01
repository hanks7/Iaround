package net.iaround.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.ChatBarHttpProtocol;
import net.iaround.model.chatbar.ChatBarAttenttion;
import net.iaround.model.chatbar.ChatBarAttenttion.AttentionBean;
import net.iaround.model.chatbar.ChatBarEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.ChatBarAdapter;
import net.iaround.ui.datamodel.ResourceBanner;
import net.iaround.ui.view.recycler.PullRecyclerView;
import net.iaround.ui.view.spacing.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatBarAttentionFragment extends BaseFragment implements View.OnClickListener, HttpCallBack {

    private SwipeRefreshLayout swipeRefreshLayout;
    private PullRecyclerView chatBarRecyleView;

    private LinearLayout mLlEmptyView;
    private ImageView mIvEmpty;
    private TextView mTvEmpty;
//    private PullToRefreshListView mLvContent;
//    private ChatBarCommonAdapter chatBarCommonAdapter;

    private ChatBarAdapter adapter;

    private List<ChatBarEntity> mDatas = new ArrayList<>();

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

    public ChatBarAttentionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_bar_attention, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initListener();
    }

    private void initView(View view) {
        chatBarRecyleView = (PullRecyclerView) view.findViewById(R.id.recycler_view);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //通过获取adapter来获取当前item的itemviewtype
                int type = chatBarRecyleView.getAdapter().getItemViewType(position);

                if (type == ChatBarEntity.RESOURCE_BANNER | type == ChatBarEntity.BANNER_NOTICE) {
                    //返回2，占一行
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,true);
        SpacesItemDecoration decoration = new SpacesItemDecoration(8);
        chatBarRecyleView.addItemDecoration(decoration);

        chatBarRecyleView.setLayoutManager(gridLayoutManager);
        chatBarRecyleView.setItemAnimator(new DefaultItemAnimator());
        chatBarRecyleView.setHasFixedSize(true);//每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 0;
                requestPageData(mCurrentPage + 1, pageSize);
            }
        });

        chatBarRecyleView.setOnLoadMoreListener(new PullRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mCurrentPage < mTotalPage) {
                    requestPageData(mCurrentPage + 1, pageSize);
                    hideWaitDialog();
                    chatBarRecyleView.serLoadMoreComplete();
                } else {
                    chatBarRecyleView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            hideWaitDialog();
                            CommonFunction.toastMsg(getContext(), R.string.no_more_data);
                            chatBarRecyleView.serLoadMoreComplete();
                        }
                    }, 200);
                }
            }
        });


        mLlEmptyView = (LinearLayout) view.findViewById(R.id.ll_empty_attention);
        mIvEmpty = (ImageView) view.findViewById(R.id.iv_empty);
        mTvEmpty = (TextView) view.findViewById(R.id.tv_empty);

//        chatBarCommonAdapter = new ChatBarCommonAdapter(getActivity(), attentionExtends);
//        mLvContent.setAdapter(chatBarCommonAdapter);

        adapter = new ChatBarAdapter(getActivity(), mDatas);
        chatBarRecyleView.setAdapter(adapter);
    }

    //"name": "刚好遇见你",
//        "type": 1,
//        "hot": 6959,
//        "family": 0,
//        "url": "http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0"
    private void initData() {
//        GET_ATTENTION_DATA_FLAG = ChatBarHttpProtocol.getInstance().getChatBarAttentionData(getContext(),pageNum,pageSize,this);
        List<AttentionBean> attentionBeanList = new ArrayList<>();
        List<ResourceBanner> cahtResourceBanners = new ArrayList<>();
        ChatBarAttenttion bean = new ChatBarAttenttion();

        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                AttentionBean attentionBean = new AttentionBean();
                attentionBean.setType(1);
                attentionBean.setHot(0);
                attentionBean.setHot(6959 + i);
                attentionBean.setFamily(0);
                attentionBean.setUrl("http://p1.iaround.com/201706/13/PIC_DYNAMIC/48262869f3dd2d54afedef31646700e9_375x466.jpg");
                attentionBean.setName("刚好遇见你");
                attentionBeanList.add(i, attentionBean);
            } else {
                AttentionBean attentionBean = new AttentionBean();
                attentionBean.setType(2);
                attentionBean.setHot(6959 + i);
                attentionBean.setFamily(0);
                attentionBean.setUrl("http://p4.iaround.com/201706/13/PIC_DYNAMIC/11651af957cc913e370ebb4850f98d59_375x417.jpg");
                attentionBean.setName("刚好遇见好声音");
                attentionBeanList.add(i, attentionBean);
            }
        }

        for (int k = 0; k < 2; k++) {
            ResourceBanner chatResouce = new ResourceBanner();
            chatResouce.title = "1313213";
            chatResouce.imageurl = "http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0";
            chatResouce.link = "www.baidu.com";
            cahtResourceBanners.add(chatResouce);
        }
        bean.setBanner(cahtResourceBanners);
        bean.setList(attentionBeanList);
        refershData(bean, false);
    }

    private void initListener() {
        mLlEmptyView.setOnClickListener(this);
//        mLvContent.setEmptyView(mLlEmptyView);
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        GET_ATTENTION_DATA_FLAG = ChatBarHttpProtocol.getInstance().getChatBarAttentionData(getContext(), pageNum, pageSize, this);
    }

    /**
     * 上拉加载数据
     */
    private void updateLoadData() {

    }

    /**
     * 显示空布局
     */
    private void showEmptyView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_empty_attention:
                break;
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (Constant.isSuccess(result)) {
            if (flag == GET_ATTENTION_DATA_FLAG) {
                ChatBarAttenttion bean = GsonUtil.getInstance().getServerBean(result, ChatBarAttenttion.class);
                if (bean.isSuccess()) {
                    swipeRefreshLayout.setRefreshing(false);
                    mCurrentPage = bean.getPage_no();
                    mTotalPage = bean.getTotal_page();
                    if (mCurrentPage <= mTotalPage) {
                        refershData(bean, true);
                    } else {
                        refershData(bean, false);
                    }

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    ErrorCode.showError(getActivity(), result);
                }

            }
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        mLlEmptyView.setVisibility(View.VISIBLE);
    }

    private void refershData(ChatBarAttenttion bean, boolean isLoadMore) {
        if (!isLoadMore) {
            if (mDatas.size() > 0) {
                mDatas.clear();
            }
        }

        for (AttentionBean attentionBean : bean.getList()) {
            ChatBarEntity entity = new ChatBarEntity();
            entity.itemType = ChatBarEntity.IMAGE_TEXT;
            entity.itemBean = attentionBean;
            mDatas.add(entity);
        }

        for (int i = 0; i < bean.getBanner().size(); i++) {
            ResourceBanner banner = bean.getBanner().get(i);
            ChatBarEntity entity = new ChatBarEntity();
            entity.itemType = ChatBarEntity.RESOURCE_BANNER;
            entity.itemBean = banner;
            if (i == 0) {
                mDatas.add(0, entity);
            } else {
                mDatas.add(11, entity);
            }

        }

        adapter.updateData(mDatas);

    }

    public void requestPageData(int pageNum, int pageSize) {
        GET_ATTENTION_DATA_FLAG = ChatBarHttpProtocol.getInstance().getChatBarAttentionData(getContext(), pageNum, pageSize, this);
    }
}
