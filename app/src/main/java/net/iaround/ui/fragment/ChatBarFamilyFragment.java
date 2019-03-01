package net.iaround.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.ChatBarHttpProtocol;
import net.iaround.model.chatbar.ChatBarFamily;
import net.iaround.model.chatbar.ChatBarFamily.FamilyNew;
import net.iaround.model.chatbar.ChatBarFamily.JoinGroupBean;
import net.iaround.model.chatbar.ChatBarFamily.PushGroupBean;
import net.iaround.model.chatbar.ChatBarFamilyType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.chat.ChatBarCommonMyFamilyAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 聊吧 我的 界面
 */
public class ChatBarFamilyFragment extends LazyLoadBaseFragment implements HttpCallBack {


    private PullToRefreshListView mLvContent;
    private ChatBarCommonMyFamilyAdapter chatBarCommonAdapter;
    private List<ChatBarFamilyType> mAttentionList;
    private List<FamilyNew> mFamilyBeanList;
    private List<JoinGroupBean> mJoinGroupList;
    private List<PushGroupBean> mPushGroupList;
    private List<PushGroupBean> mPushGroupListTotal;
    private List<ChatBarFamilyType> mChatBarFamilyTypeList;
    private List<JoinGroupBean> mJoinGroupListConvert;
    private int joinCount;

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

    private static final String DYNAMICACTION = "com.byread.dynamic";

    public static final String FOLLOW_ACTION = "follow_action";

    private boolean mFirstLoadData = false;

    public ChatBarFamilyFragment() {

    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_chat_bar_family;
    }

    @Override
    protected boolean lazyLoad() {
        initData();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView(getContentView());
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_chat_bar_family, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initView(view);
//        initData();
//
//    }

    private void initView(View view) {

        mLvContent = (PullToRefreshListView) view.findViewById(R.id.lv_attention);
        mLvContent.getRefreshableView().setSelection(1);//mLvContent.getRefreshableView().getHeaderViewsCount()

        mLvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mLvContent.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mLvContent.getRefreshableView().setFastScrollEnabled(false);
        mLvContent.getRefreshableView().setFadingEdgeLength(-1);
        mLvContent.getRefreshableView().setSelector(R.drawable.transparent);
        mLvContent.setOnRefreshListener(mOnRefreshListener);


        chatBarCommonAdapter = new ChatBarCommonMyFamilyAdapter(getActivity(), mAttentionList);
        mLvContent.setAdapter(chatBarCommonAdapter);
        mLvContent.setVerticalScrollBarEnabled(false);

        // 注册自定义动态广播消息
        IntentFilter filter_dynamic = new IntentFilter();
        filter_dynamic.addAction(DYNAMICACTION);
        filter_dynamic.addAction(FOLLOW_ACTION);
        getActivity().registerReceiver(dynamicReceiver, filter_dynamic);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(dynamicReceiver);
    }

    private void initData() {
        mAttentionList = new ArrayList<>();
        mChatBarFamilyTypeList = new ArrayList<>();
        mJoinGroupList = new ArrayList<>();
        mPushGroupList = new ArrayList<>();
        mJoinGroupListConvert = new ArrayList<>();
        mFamilyBeanList = new ArrayList<>();
        mPushGroupListTotal = new ArrayList<>();
        pageNum = 1;
        GET_ATTENTION_DATA_FLAG = ChatBarHttpProtocol.getInstance().getChatBarAttentionData(getContext(), pageNum, pageSize, this);
        chatBarCommonAdapter.updateData(mAttentionList);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (Constant.isSuccess(result)) {
            if (flag == GET_ATTENTION_DATA_FLAG) {
                mLvContent.onRefreshComplete();
                ChatBarFamily bean = GsonUtil.getInstance().getServerBean(result, ChatBarFamily.class);
                if (null != bean && bean.isSuccess()) {
                    mCurrentPage = bean.getPage_no();
                    mTotalPage = bean.getPush_total_page();

                    if (mCurrentPage == 1) {
                        if (mChatBarFamilyTypeList != null) {
                            mChatBarFamilyTypeList.clear();
                        }
                        if (mJoinGroupList != null) {
                            mJoinGroupList.clear();
                        }
                        if (mPushGroupList != null) {
                            mPushGroupList.clear();
                        }
                        if (mJoinGroupListConvert != null) {
                            mJoinGroupListConvert.clear();
                        }
                        mFamilyBeanList = bean.getFamily();//我创建的家族
                        mJoinGroupList = bean.getJoin_group();//我加入的家族
                        mPushGroupList = bean.getPush_group();//推荐的家族
                        if (mFamilyBeanList != null && mJoinGroupList != null && mFamilyBeanList.isEmpty() && mJoinGroupList.isEmpty()) {
                            ChatBarFamilyType chatBarEmptyType = new ChatBarFamilyType();
                            chatBarEmptyType.setType(7);
                            if(mChatBarFamilyTypeList != null){
                                mChatBarFamilyTypeList.add(chatBarEmptyType);
                            }
                        }
                    } else {
                        mFamilyBeanList = bean.getFamily();//我创建的家族
                        mJoinGroupList = bean.getJoin_group();//我加入的家族
                        mPushGroupList = bean.getPush_group();//推荐的家族
                        if (bean.getPush_group() != null && !bean.getPush_group().isEmpty()) {
                            mPushGroupListTotal.addAll(mPushGroupList);
                        }

                    }

                    if (mPushGroupListTotal != null) {
                        mPushGroupListTotal.clear();
                    }

                    if (mPushGroupList != null && mPushGroupList.size() > 0 && mPushGroupListTotal != null) {
                        mPushGroupListTotal.addAll(mPushGroupList);
                    }

                    if (mJoinGroupList != null && !mJoinGroupList.isEmpty()) {
                        if (mFamilyBeanList != null && !mFamilyBeanList.isEmpty()) {
                            joinCount = mJoinGroupList.size() + mFamilyBeanList.size();
                            for (int i = 0; i <= mFamilyBeanList.size() - 1; i++) {
                                FamilyNew familyNew = mFamilyBeanList.get(i);
                                JoinGroupBean joinGroupBean = new JoinGroupBean();
                                joinGroupBean.setFamilurl(familyNew.getFamilurl());
                                joinGroupBean.setFamily(familyNew.getFamily());
                                joinGroupBean.setGroupid(familyNew.getGroupid());
                                joinGroupBean.setHot(familyNew.getHot());
                                joinGroupBean.setName(familyNew.getName());
                                joinGroupBean.setVerify(familyNew.getVerify());
                                mJoinGroupListConvert.add(joinGroupBean);
                            }
                            mJoinGroupList.addAll(0, mJoinGroupListConvert);
                        } else {
                            joinCount = mJoinGroupList.size();
                        }
                        if (joinCount % 2 == 0) {
                            int join = joinCount / 2;
                            for (int i = 1; i <= join; i++) {
                                ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                chatBarJoinGroupType1.setType(4);
                                chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(2 * i - 2));
                                chatBarJoinGroupType1.setObjectRight(mJoinGroupList.get(2 * i - 1));
                                mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                            }
                        } else {
                            if (joinCount == 1) {
                                ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                chatBarJoinGroupType1.setType(4);
                                chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(0));
                                chatBarJoinGroupType1.setObjectRight(null);
                                mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                            } else {
                                int joinSingle = joinCount / 2 + 1;
                                for (int i = 1; i <= joinSingle - 1; i++) {
                                    ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                    chatBarJoinGroupType1.setType(4);
                                    chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(2 * i - 2));
                                    chatBarJoinGroupType1.setObjectRight(mJoinGroupList.get(2 * i - 1));
                                    mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                                }
                                ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                chatBarJoinGroupType1.setType(4);
                                chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(2 * joinSingle - 2));
                                chatBarJoinGroupType1.setObjectRight(null);
                                mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                            }

                        }

                    } else {
                        if (mFamilyBeanList != null && !mFamilyBeanList.isEmpty()) {
                            joinCount = mJoinGroupList.size() + mFamilyBeanList.size();
                            for (int i = 0; i <= mFamilyBeanList.size() - 1; i++) {
                                FamilyNew familyNew = mFamilyBeanList.get(i);
                                JoinGroupBean joinGroupBean = new JoinGroupBean();
                                joinGroupBean.setFamilurl(familyNew.getFamilurl());
                                joinGroupBean.setFamily(familyNew.getFamily());
                                joinGroupBean.setGroupid(familyNew.getGroupid());
                                joinGroupBean.setHot(familyNew.getHot());
                                joinGroupBean.setName(familyNew.getName());
                                joinGroupBean.setVerify(familyNew.getVerify());
                                mJoinGroupListConvert.add(joinGroupBean);
                            }
                            mJoinGroupList.addAll(0, mJoinGroupListConvert);

                            if (joinCount % 2 == 0) {
                                int join = joinCount / 2;
                                for (int i = 1; i <= join; i++) {
                                    ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                    chatBarJoinGroupType1.setType(4);
                                    chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(2 * i - 2));
                                    chatBarJoinGroupType1.setObjectRight(mJoinGroupList.get(2 * i - 1));
                                    mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                                }
                            } else {
                                if (joinCount == 1) {
                                    ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                    chatBarJoinGroupType1.setType(4);
                                    chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(0));
                                    chatBarJoinGroupType1.setObjectRight(null);
                                    mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                                } else {
                                    int joinSingle = joinCount / 2 + 1;
                                    for (int i = 1; i <= joinSingle - 1; i++) {
                                        ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                        chatBarJoinGroupType1.setType(4);
                                        chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(2 * i - 2));
                                        chatBarJoinGroupType1.setObjectRight(mJoinGroupList.get(2 * i - 1));
                                        mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                                    }
                                    ChatBarFamilyType chatBarJoinGroupType1 = new ChatBarFamilyType();
                                    chatBarJoinGroupType1.setType(4);
                                    chatBarJoinGroupType1.setObjectLeft(mJoinGroupList.get(2 * joinSingle - 2));
                                    chatBarJoinGroupType1.setObjectRight(null);
                                    mChatBarFamilyTypeList.add(chatBarJoinGroupType1);
                                }

                            }
                        }
                    }

                    if(mPushGroupListTotal != null && !mPushGroupListTotal.isEmpty()){
                        joinCount = mFamilyBeanList.size();
                        if (mCurrentPage == 1) {
                            ChatBarFamilyType chatBarPushGroupType = new ChatBarFamilyType();
                            chatBarPushGroupType.setType(5);
                            chatBarPushGroupType.setObjectLeft(BaseApplication.appContext.getResources().getString(R.string.chat_bar_push));
                            mChatBarFamilyTypeList.add(chatBarPushGroupType);
                        }

                        int pushCount = mPushGroupListTotal.size();
                        if (pushCount % 2 == 0) {
                            int push = pushCount / 2;
                            for (int i = 1; i <= push; i++) {
                                ChatBarFamilyType chatBarPushGroupType1 = new ChatBarFamilyType();
                                chatBarPushGroupType1.setType(6);
                                chatBarPushGroupType1.setObjectLeft(mPushGroupListTotal.get(2 * i - 2));
                                chatBarPushGroupType1.setObjectRight(mPushGroupListTotal.get(2 * i - 1));
                                mChatBarFamilyTypeList.add(chatBarPushGroupType1);
                            }
                        } else {
                            if (pushCount == 1) {
                                ChatBarFamilyType chatBarPushGroupType1 = new ChatBarFamilyType();
                                chatBarPushGroupType1.setType(6);
                                chatBarPushGroupType1.setObjectLeft(mPushGroupListTotal.get(0));
                                chatBarPushGroupType1.setObjectRight(null);
                                mChatBarFamilyTypeList.add(chatBarPushGroupType1);
                            } else {
                                int push = pushCount / 2 + 1;
                                for (int i = 1; i <= push - 1; i++) {
                                    ChatBarFamilyType chatBarPushGroupType1 = new ChatBarFamilyType();
                                    chatBarPushGroupType1.setType(6);
                                    chatBarPushGroupType1.setObjectLeft(mPushGroupListTotal.get(2 * i - 2));
                                    chatBarPushGroupType1.setObjectRight(mPushGroupListTotal.get(2 * i - 1));
                                    mChatBarFamilyTypeList.add(chatBarPushGroupType1);

                                }
                                ChatBarFamilyType chatBarPushGroupType1 = new ChatBarFamilyType();
                                chatBarPushGroupType1.setType(6);
                                chatBarPushGroupType1.setObjectLeft(mPushGroupListTotal.get(2 * push - 2));
                                chatBarPushGroupType1.setObjectRight(null);
                                mChatBarFamilyTypeList.add(chatBarPushGroupType1);
                            }

                        }

                    }
                    chatBarCommonAdapter.updateData(mChatBarFamilyTypeList);

                } else {
                    mLvContent.onRefreshComplete();
                    showFail();
                    ErrorCode.showError(BaseApplication.appContext, result);
                }

            }
        } else {
            mLvContent.onRefreshComplete();
            ErrorCode.showError(BaseApplication.appContext, result);
            showFail();
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (flag == GET_ATTENTION_DATA_FLAG) {
            mLvContent.onRefreshComplete();
            showFail();
            ErrorCode.toastError(BaseApplication.appContext, e);
        }
    }

    private void showFail() {
        showPullToReListViewLoadFailedView(mLvContent, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPageData(mCurrentPage + 1, pageSize);
            }
        });
    }

    public void requestPageData(int pageNum, int pageSize) {
        GET_ATTENTION_DATA_FLAG = ChatBarHttpProtocol.getInstance().getChatBarAttentionData(getContext(), pageNum, pageSize, this);
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            mCurrentPage = 0;
            requestPageData(mCurrentPage + 1, pageSize);
        }

        @Override
        public void onPullUpToRefresh(
                final PullToRefreshBase<ListView> refreshView) {

            if (mCurrentPage < mTotalPage) {
                requestPageData(mCurrentPage + 1, pageSize);
                hideWaitDialog();
                mLvContent.onRefreshComplete();
            } else {
                mLvContent.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        hideWaitDialog();
                        CommonFunction.toastMsg(getContext(), R.string.no_more_data);
                        mLvContent.onRefreshComplete();
                    }
                }, 200);
            }
        }
    };

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DYNAMICACTION) || FOLLOW_ACTION.equals(intent.getAction())) {
                requestPageData(1, 10);
            }
        }
    };
}
