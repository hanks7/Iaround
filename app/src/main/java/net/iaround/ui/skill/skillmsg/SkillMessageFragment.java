package net.iaround.ui.skill.skillmsg;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseFragment;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.ui.chat.view.SkillMsgRecordView;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * 作者：zx on 2017/8/22 21:07
 */
public class SkillMessageFragment extends MvpBaseFragment<SkillMsgLlistContract.Presenter> implements SkillMsgLlistContract.View {


    @BindView(R.id.skill_list) PullToRefreshListView skillList;
    private SkillMsgAdapter adapter;
    private SkillMsgLlistContract.Presenter mPresenter;
    private String groupId;
    private volatile long timeStamp = 0;
    private ArrayList<SkillAttackResult> mDataList;
    private ArrayList<SkillAttackResult> recordHistory = new ArrayList<>();
    private boolean isLast = true;

    public static SkillMessageFragment getInstance(Bundle bundle){
        SkillMessageFragment fragment = new SkillMessageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void getBundles() {
        Bundle bundle = getArguments();
        if (null != bundle){
            groupId = bundle.getString("groupId");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_skill_message;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getBundles();
        init();
        mPresenter = new SKillMsgListPresenter(this);
        mPresenter.setSocketCallback();
        skillList.post(new Runnable() {//进入自动刷新
            @Override
            public void run() {
                skillList.setRefreshing(true);
            }
        });
    }


    private void init() {
        mDataList = new ArrayList<>();
        adapter = new SkillMsgAdapter();
        View headView = View.inflate(BaseApplication.appContext, R.layout.chat_personal_record_head, null);
        View footerView = View.inflate(BaseApplication.appContext, R.layout.record_footer, null);
        skillList.getRefreshableView().addHeaderView(headView);
        skillList.getRefreshableView().addFooterView(footerView);
        skillList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//PULL_FROM_START
        skillList.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        skillList.getRefreshableView().setFastScrollEnabled(false);
        skillList.setOnRefreshListener(mOnRefreshListener);
        skillList.setAdapter(adapter);
        skillList.setVerticalScrollBarEnabled(false);
        skillList.getRefreshableView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int count = view.getChildCount();
                switch (scrollState){
                    case SCROLL_STATE_IDLE:
                            for (int i = 0; i < count; i++) {
                                if (view.getChildAt(i) instanceof SkillMsgRecordView){
                                    SkillMsgRecordView skillRecordView = (SkillMsgRecordView) view.getChildAt(i);
                                    skillRecordView.setScrolled(false);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        break;
                    case SCROLL_STATE_FLING:
                    case SCROLL_STATE_TOUCH_SCROLL:
                        for (int i = 0; i < count; i++) {
                            if (view.getChildAt(i) instanceof SkillMsgRecordView) {
                                SkillMsgRecordView skillRecordView = (SkillMsgRecordView) view.getChildAt(i);
                                skillRecordView.setScrolled(true);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isLast = skillList.getRefreshableView().getLastVisiblePosition() == totalItemCount - 1;
            }
        });
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            mPresenter.getSkillMessageHistory(groupId, timeStamp);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

        }
    };

    @Override
    public void updateLastSkillMessage(SkillAttackResult result) {
        if (null != result){
            mDataList.add(result);
            skillList.post(new Runnable() {
                @Override
                public void run() {
                    adapter.updateRecordList(mDataList);
                    if (isLast){
                        skillList.getRefreshableView().setSelection(mDataList.size());
                        skillList.getRefreshableView().smoothScrollToPosition(mDataList.size());
                    }
                }
            });
        }
    }

    @Override
    public void updateHistoryMessage(final ArrayList<SkillAttackResult> recordList, long timeStamp) {
        if(timeStamp>0){
            this.timeStamp = timeStamp;
        }
        if (null != recordList && recordList.size() > 0){
            final int size = recordList.size();
            recordHistory.clear();
            recordHistory.addAll(recordList);
            recordHistory.addAll(mDataList);
            mDataList.clear();
            mDataList.addAll(recordHistory);
            skillList.postDelayed(new Runnable() {
                @Override
                public void run() {
                    skillList.onRefreshComplete();
                    skillList.setAdapter(adapter);
                    adapter.updateRecordList(mDataList);
                    skillList.getRefreshableView().setSelected(true);
                    skillList.getRefreshableView().setSelection(size);
                }
            }, 200);
        }else {
            refreshCompleted();
        }
    }

    @Override
    public void refreshCompleted() {
        skillList.post(new Runnable() {
            @Override
            public void run() {
                skillList.onRefreshComplete();
            }
        });
    }


    @Override
    public void setPresenter(SkillMsgLlistContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public boolean isActive() {
        return isLive();
    }

    @Override
    public void onDestroy() {
        if (null != mPresenter){
            mPresenter.clearSocketCallback();
        }
        super.onDestroy();
    }
}
