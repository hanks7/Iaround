package net.iaround.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.internal.RecycleViewLoadingLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.model.entity.WalletDetailListData;
import net.iaround.model.entity.WalletDetailListItemData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.StarDetailFragmentAdapter;
import net.iaround.ui.adapter.poweradapter.OnLoadMoreListener;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;

import java.util.List;

public class StarDetailFragment extends LazyLoadBaseFragment implements HttpCallBack, PowerAdapter.OnEmptyClickListener, PowerAdapter.OnErrorClickListener, OnLoadMoreListener {

    private static final String TAG = StarDetailFragment.class.getSimpleName();
    private PullToRefreshRecyclerView mPtrlvVoicePage;

    private RecyclerView mRecyclerView;

    private int mCurrentPageno = 1;

    private int mPagesize = 24;

    private StarDetailFragmentAdapter mStarDetailFragmentAdapter;

    private Runnable loadMoreAction;

    private boolean isRun;

    private static final int DEFAULT_TIME = 1000;

    @Override
    protected int setContentView() {
        return R.layout.star_detail_fragment;
    }

    @Override
    protected boolean lazyLoad() {
        showWaitDialog();
        initdata(1);
        return false;
    }

    private void initdata(int type) {
        if(type == 1){
            GoldHttpProtocol.getWalletDetails(BaseApplication.appContext, 1, mPagesize, 6, this);
        }else if(type == 2){
            GoldHttpProtocol.getWalletDetails(BaseApplication.appContext, mCurrentPageno + 1, mPagesize, 6, this);
        }
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = getContentView();
        mPtrlvVoicePage = (PullToRefreshRecyclerView) contentView.findViewById(R.id.pull_refresh_recycler_star);
        mPtrlvVoicePage.setHeaderLayout(new RecycleViewLoadingLayout(BaseApplication.appContext));
        mRecyclerView = mPtrlvVoicePage.getRefreshableView();
        LinearLayoutManager manager = new LinearLayoutManager(BaseApplication.appContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mStarDetailFragmentAdapter = new StarDetailFragmentAdapter(BaseApplication.appContext);
        mStarDetailFragmentAdapter.attachRecyclerView(mRecyclerView);
        mStarDetailFragmentAdapter.setLoadMoreListener(this);
        mStarDetailFragmentAdapter.setEmptyClickListener(this);
        mStarDetailFragmentAdapter.setErrorClickListener(this);
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
    public void onGeneralSuccess(String result, long flag) {
        stopPulling();
        destroyWaitDialog();
        WalletDetailListData mVoiceListData = GsonUtil.getInstance().getServerBean(result, WalletDetailListData.class);
        if (mVoiceListData != null && mVoiceListData.isSuccess()) {
            mCurrentPageno = mVoiceListData.pageno;
            List<WalletDetailListItemData> list = mVoiceListData.list;
            if (list != null && list.size() > 0) {
                mStarDetailFragmentAdapter.setTotalCount(mVoiceListData.amount);
                if (mVoiceListData.pageno <= 1) {
                    mStarDetailFragmentAdapter.setList(list);
                } else {
                    mStarDetailFragmentAdapter.appendList(list);
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

    public void stopPulling() {
        if (mPtrlvVoicePage != null) {
            mPtrlvVoicePage.onRefreshComplete();
        }
    }

    public void emptyAnderrorView(){
        if (mCurrentPageno <= 1 && mStarDetailFragmentAdapter.getItemRealCount()<= 0) {
            mStarDetailFragmentAdapter.setErrorView(View.inflate(BaseApplication.appContext, R.layout.no_data_view, null));
            mStarDetailFragmentAdapter.showError(true);
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
