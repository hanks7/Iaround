package net.iaround.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.HomepageHttpProtocol;
import net.iaround.model.entity.GameChatInfo;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.ui.activity.GameChatGameListActivity;
import net.iaround.ui.activity.GameListActivity;
import net.iaround.ui.adapter.HomePageAdapter;
import net.iaround.ui.view.FlyBanner;
import net.iaround.utils.OnClickUtil;

import java.util.HashMap;


/**
 * Created by yz on 2018/7/30.
 */

public class HomePageFragment extends LazyLoadBaseFragment implements HttpCallBack, LocationUtil.MLocationListener {

    private static final String TAG = "HomePageFragment";
    private PullToRefreshListView mPtrlvHomePage;
    private View mHeaderView;
    private FlyBanner mTopbannersView;
    private HomePageAdapter mHomePageAdapter;

    private GameChatInfo mGameChatInfo;
    private LocationUtil mLocationUtil;

    private boolean isSuccess = false;//是否加载数据成功过

    public Handler mainHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected int setContentView() {
        return R.layout.home_page_fragment_layout;
    }

    @Override
    protected boolean lazyLoad() {
        showWaitDialog();
        if (mLocationUtil == null) {
            mLocationUtil = new LocationUtil(getContext());
        }
        mLocationUtil.startListener(this, 0);

        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //统计登录后在首页停留的用户人数
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uid", Common.getInstance().getUid());
        MobclickAgent.onEvent(BaseApplication.appContext, "Watch_home_person_num", map);
        initView(getContentView());
        mHomePageAdapter = new HomePageAdapter(getContext());
        mPtrlvHomePage.setAdapter(mHomePageAdapter);
        mHomePageAdapter.setGameTypeItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (OnClickUtil.isFastClick()) {
                    return;
                }
                GameChatInfo.GameType gameType = (GameChatInfo.GameType) parent.getAdapter().getItem(position);
                Intent intent;
                if (gameType.gid == mGameChatInfo.game_type.get(mGameChatInfo.game_type.size() - 1).gid) {
                    intent = new Intent(getContext(), GameChatGameListActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getContext(), GameListActivity.class);
                    intent.putExtra("GameId", gameType.gid);
                    startActivity(intent);
                }
            }
        });

    }

    private void initView(View v) {
        mPtrlvHomePage = (PullToRefreshListView) v.findViewById(R.id.ptrlv_home_page);
        mPtrlvHomePage.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPtrlvHomePage.setPullToRefreshOverScrollEnabled(false);
        mPtrlvHomePage.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (mLocationUtil == null) {
                    mLocationUtil = new LocationUtil(getContext());
                }
                mLocationUtil.startListener(HomePageFragment.this, 0);
            }
        });


        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.home_page_entrance_bar, null);
        mTopbannersView = (FlyBanner) mHeaderView.findViewById(R.id.vp_charbarpopular);

    }

    public void stopPulling() {
        if (mPtrlvHomePage != null) {
            mPtrlvHomePage.onRefreshComplete();
        }
    }

    private void showFailed() {
        if (!isSuccess) {
            showPullToReListViewLoadFailedView(mPtrlvHomePage, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLocationUtil == null) {
                        mLocationUtil = new LocationUtil(getContext());
                    }
                    mLocationUtil.startListener(HomePageFragment.this, 0);
                }
            });
        }
    }

    @Override
    public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {
        CommonFunction.log(TAG, "updateLocation() into, type=" + type + ", lat=" + lat + ", lng=" + lng + ", address=" + address);
        if (type == 0) { // 失败
            stopPulling();
            showFailed();
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.location_service_unavalible);
            return;
        }

        HomepageHttpProtocol.getGameChatInfo(getContext(), lat, lng, this);
    }


    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        stopPulling();
        CommonFunction.log("onGeneralSuccess ", " result: " + result);
        mGameChatInfo = GsonUtil.getInstance().getServerBean(result, GameChatInfo.class);
        if (mGameChatInfo != null && mGameChatInfo.isSuccess()) {
            isSuccess = true;
            int count = mPtrlvHomePage.getRefreshableView().getHeaderViewsCount();
            if (mGameChatInfo.topbanners != null && mGameChatInfo.topbanners.size() > 0) {
                if (count <= 1) {
                    mPtrlvHomePage.getRefreshableView().addHeaderView(mHeaderView);
                }

                mTopbannersView.setImagesUrl(mGameChatInfo.topbanners, true);

            } else {
                if (count > 1) {
                    mPtrlvHomePage.getRefreshableView().removeHeaderView(mHeaderView);
                }

            }
            mHomePageAdapter.updateData(mGameChatInfo);
        } else {
            showFailed();
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        stopPulling();
        destroyWaitDialog();
        showFailed();
        ErrorCode.toastError(BaseApplication.appContext, e);

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("HomePageFragment"); //统计页面("MainScreen"为页面名称，可自定义)
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("HomePageFragment");
//    }
}
