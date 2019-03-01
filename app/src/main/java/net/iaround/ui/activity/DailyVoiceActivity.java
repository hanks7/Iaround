package net.iaround.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.internal.RecycleViewLoadingLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.DailyVoiceListData;
import net.iaround.model.entity.DailyVoiceListItemData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DensityUtils;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.DailyVoiceActivityAdapter;
import net.iaround.ui.adapter.poweradapter.OnLoadMoreListener;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;

import java.util.List;

/**
 * 语音明细
 */
public class DailyVoiceActivity extends TitleActivity implements HttpCallBack, PowerAdapter.OnEmptyClickListener, PowerAdapter.OnErrorClickListener, OnLoadMoreListener {

    private static final String TAG = DailyVoiceActivity.class.getSimpleName();
    private PullToRefreshRecyclerView mPtrlvVoicePage;

    private RecyclerView mRecyclerView;

    private DailyVoiceActivityAdapter voiceFragmentAdapter;

    private int mCurrentPageno = 1;

    private int pagesize = 24;

    private Runnable loadMoreAction;

    private boolean isRun;

    private static final int DEFAULT_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.daily_detail_voice), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_daily_voice);
        mPtrlvVoicePage = (PullToRefreshRecyclerView) findViewById(R.id.pull_refresh_recycler_daily_voice);
        mPtrlvVoicePage.setHeaderLayout(new RecycleViewLoadingLayout(BaseApplication.appContext));
        mRecyclerView = mPtrlvVoicePage.getRefreshableView();
        LinearLayoutManager manager = new LinearLayoutManager(BaseApplication.appContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        voiceFragmentAdapter = new DailyVoiceActivityAdapter(DailyVoiceActivity.this);
        voiceFragmentAdapter.attachRecyclerView(mRecyclerView);
        voiceFragmentAdapter.setLoadMoreListener(this);
        voiceFragmentAdapter.setEmptyClickListener(this);
        voiceFragmentAdapter.setErrorClickListener(this);
        loadMoreAction = new Runnable() {
            @Override
            public void run() {
                initdata(2);
            }
        };
        mPtrlvVoicePage.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                initdata(1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showWaitDialog();
        initdata(1);
    }


    private void initdata(int type) {
        if (type == 1) {
            GameChatHttpProtocol.getVoiceDetailListInfo(BaseApplication.appContext, 1, pagesize, this);
        } else if (type == 2) {
            GameChatHttpProtocol.getVoiceDetailListInfo(BaseApplication.appContext, mCurrentPageno + 1, pagesize, this);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        stopPulling();
        destroyWaitDialog();
        DailyVoiceListData mVoiceListData = GsonUtil.getInstance().getServerBean(result, DailyVoiceListData.class);
        if (mVoiceListData != null && mVoiceListData.isSuccess()) {
            mCurrentPageno = mVoiceListData.pageno;
            List<DailyVoiceListItemData> list = mVoiceListData.chatlist;
            if (list != null && list.size() > 0) {
                voiceFragmentAdapter.setTotalCount(mVoiceListData.count);
                if (mVoiceListData.pageno <= 1) {
                    voiceFragmentAdapter.setList(list);
                } else {
                    voiceFragmentAdapter.appendList(list);
                }
            } else {
                emptyAnderrorView();
            }
        } else {
            emptyAnderrorView();
        }
        isRun = false;
    }

    @Override
    public void onGeneralError(int e, long flag) {
        stopPulling();
        destroyWaitDialog();
        ErrorCode.toastError(BaseApplication.appContext, e);
        emptyAnderrorView();
        isRun = false;
    }

    public void stopPulling() {
        if (mPtrlvVoicePage != null) {
            mPtrlvVoicePage.onRefreshComplete();
        }
    }

    @Override
    public void onEmptyClick(View view) {
        showWaitDialog();
        initdata(1);
    }

    @Override
    public void onErrorClick(View view) {
        showWaitDialog();
        initdata(1);
    }

    public void emptyAnderrorView() {
        if (mCurrentPageno <= 1 && voiceFragmentAdapter.getItemRealCount() <= 0) {
            voiceFragmentAdapter.setErrorView(View.inflate(BaseApplication.appContext, R.layout.no_data_view, null));
            voiceFragmentAdapter.showError(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (isRun) {
            CommonFunction.log(TAG, "onLoadMore:正在执行，直接返回。。。 ");
            return;
        }
        CommonFunction.log(TAG, "onLoadMore: ");
        isRun = true;
        mRecyclerView.postDelayed(loadMoreAction, DEFAULT_TIME);
    }
}
