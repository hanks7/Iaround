package net.iaround.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.internal.RecycleViewLoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.HomepageHttpProtocol;
import net.iaround.model.entity.VoiceListData;
import net.iaround.model.entity.VoiceListItemData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DensityUtils;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.AudioDetailsActivity;
import net.iaround.ui.adapter.VoiceFragmentAdapter;
import net.iaround.ui.adapter.poweradapter.AdapterLoader;
import net.iaround.ui.adapter.poweradapter.OnLoadMoreListener;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;
import net.iaround.ui.adapter.poweradapter.SpacesItemDecoration;
import net.iaround.ui.view.dialog.ScreenSizeUtils;

import java.util.HashMap;
import java.util.List;

/**
 * 首页语音列表
 */

public class VoiceFragment extends LazyLoadBaseFragment implements HttpCallBack, PowerAdapter.OnEmptyClickListener, PowerAdapter.OnErrorClickListener, OnLoadMoreListener {

    private static final String TAG = VoiceFragment.class.getSimpleName();
    private PullToRefreshRecyclerView mPtrlvVoicePage;

    private RecyclerView mRecyclerView;

    private VoiceFragmentAdapter mVoiceFragmentAdapter;

    private int mCurrentPageno = 1;

    private int mPagesize = 24;

    private SpacesItemDecoration mDecor;

    private Runnable loadMoreAction;

    private boolean isRun;

    private static final int DEFAULT_TIME = 1000;

    @Override
    protected int setContentView() {
        return R.layout.fragment_voice;
    }

    @Override
    protected boolean lazyLoad() {
        //统计登录后在首页语音列表停留的用户人数
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uid", Common.getInstance().getUid());
        MobclickAgent.onEvent(BaseApplication.appContext, "watch_home_voice_num", map);

        showWaitDialog();
        initdata(1);
        return false;
    }

    private void initdata(int type) {
        if (type == 1) {
            HomepageHttpProtocol.getGameVoiceInfo(BaseApplication.appContext, 1, mPagesize, this);
        } else if (type == 2) {
            HomepageHttpProtocol.getGameVoiceInfo(BaseApplication.appContext, mCurrentPageno + 1, mPagesize, this);
        }
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = getContentView();
        mPtrlvVoicePage = (PullToRefreshRecyclerView) contentView.findViewById(R.id.pull_refresh_recycler);
        mPtrlvVoicePage.setHeaderLayout(new RecycleViewLoadingLayout(BaseApplication.appContext));
        mRecyclerView = mPtrlvVoicePage.getRefreshableView();
        GridLayoutManager manager = new GridLayoutManager(BaseApplication.appContext, 2);
        mRecyclerView.setLayoutManager(manager);
        mDecor = new SpacesItemDecoration.Builder(DensityUtils.dp2px(BaseApplication.appContext, 11), 2, true)
                .setBottomSpace(DensityUtils.dp2px(BaseApplication.appContext, 10))
                .setShowTopBottom(true)
                .create();
        mRecyclerView.addItemDecoration(mDecor);
        mVoiceFragmentAdapter = new VoiceFragmentAdapter(BaseApplication.appContext);
        mVoiceFragmentAdapter.attachRecyclerView(mRecyclerView);
        mVoiceFragmentAdapter.setLoadMoreListener(this);
        mVoiceFragmentAdapter.setEmptyClickListener(this);
        mVoiceFragmentAdapter.setErrorClickListener(this);
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

        mVoiceFragmentAdapter.setOnItemClickListener(new AdapterLoader.OnItemClickListener<VoiceListItemData>() {
            @Override
            public void onItemClick(@NonNull View itemView, int position, VoiceListItemData item) {
                if (item != null) {
                    Intent intent = new Intent(getContext(), AudioDetailsActivity.class);
                    intent.putExtra("UserId", item.userId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        stopPulling();
        destroyWaitDialog();
        VoiceListData mVoiceListData = GsonUtil.getInstance().getServerBean(result, VoiceListData.class);
        if (mVoiceListData != null && mVoiceListData.isSuccess()) {
            mCurrentPageno = mVoiceListData.pageno;
            List<VoiceListItemData> list = mVoiceListData.list;
            if (list != null && list.size() > 0) {
                mVoiceFragmentAdapter.setTotalCount(mVoiceListData.count);
                if (mVoiceListData.pageno <= 1) {
                    mVoiceFragmentAdapter.setList(list);
                } else {
                    mVoiceFragmentAdapter.appendList(list);
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
        if (mCurrentPageno <= 1 && mVoiceFragmentAdapter.getItemRealCount() <= 0) {
            mVoiceFragmentAdapter.setErrorView(View.inflate(BaseApplication.appContext, R.layout.no_data_view, null));
            mVoiceFragmentAdapter.showError(true);
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

    public void smoothScrollToStart() {
        if (mRecyclerView != null && mVoiceFragmentAdapter != null) {
            List<VoiceListItemData> list = mVoiceFragmentAdapter.getList();
            if (list != null && list.size() > 6) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        }
    }
}
