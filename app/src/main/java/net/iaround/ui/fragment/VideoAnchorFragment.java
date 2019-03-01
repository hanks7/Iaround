package net.iaround.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.AnchorsCertificationProtocol;
import net.iaround.model.entity.VideoAnchorsItem;
import net.iaround.model.entity.VideoAnchorBean;
import net.iaround.model.entity.VideoAnchorBean.VideoAnchorItem;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.AnchorsCertificationActivity;
import net.iaround.ui.activity.VideoDetailsActivity;
import net.iaround.ui.adapter.VideoAnchorAdapter;
import net.iaround.ui.datamodel.StartModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 视频主播列表
 * Created by Administrator on 2017/12/8.
 */

public class VideoAnchorFragment extends BaseFragment implements HttpCallBack {

    private final String TAG = "NearFragment";

    private PullToRefreshListView mNearUserListView;
    private View emptyView;
    private View footerView;
    private TextView emptyTipsText;

    private static final int PAGE_SIZE = 20;
    private int mCurPage = 1;// 当前页
    private int mTotalPage;// 总页数

    private List<VideoAnchorsItem> mNearbyUsers = new ArrayList<>();// 视频列表

    private VideoAnchorAdapter mNearbyListAdapter;

    /**
     * 是否成功加载第一页数据，true为成功，false为失败
     */
    private Boolean isFirstPageLoaded = false;

    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.main_nearby_view, null, false);
        emptyView = inflater.inflate(R.layout.no_data_view, null);
        footerView = inflater.inflate(R.layout.x_nearby_fragment_empty_view, null);
        emptyTipsText = (TextView) footerView.findViewById(R.id.empty_text);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initData();
    }

    /**
     * 初始化View
     */
    private void initView() {

        if (mNearUserListView == null) {
            mNearUserListView = (PullToRefreshListView) getView().findViewById(R.id.nearby_user_listview);
            mNearUserListView.setMode(PullToRefreshBase.Mode.BOTH);

            mNearUserListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
            mNearUserListView.getRefreshableView().setDividerHeight(0);
            mNearUserListView.getRefreshableView().setFadingEdgeLength(-1);
            mNearUserListView.getRefreshableView().setSelector(R.drawable.transparent);
            mNearUserListView.getRefreshableView().setFastScrollEnabled(false);
            mNearUserListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                        @Override
                        public void onPullDownToRefresh(
                                PullToRefreshBase<ListView> refreshView) {
                            handlePullDown();
                        }

                        @Override
                        public void onPullUpToRefresh(
                                PullToRefreshBase<ListView> refreshView) {
                            CommonFunction.log(TAG, "mCurPage***" + mCurPage);
                            CommonFunction.log(TAG, "mTotalPage***" + mTotalPage);
                            if (mCurPage < mTotalPage) {
                                reqData(mCurPage + 1);
                            } else {
                                mHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), R.string.no_more, Toast.LENGTH_SHORT).show();
                                        mNearUserListView.onRefreshComplete();
                                    }
                                }, 200);
                            }
                        }
                    });
        } else {
            mTotalPage = mCurPage + 1;
        }

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePullDown();
            }
        });

        mNearUserListView.setVisibility(View.VISIBLE);
    }

    /**
     * @Title: initData
     * @Description: 初始化数据
     */
    private void initData() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mNearbyListAdapter == null) {
            mNearbyListAdapter = new VideoAnchorAdapter(getActivity(),mNearbyUsers);
            mNearUserListView.setAdapter(mNearbyListAdapter);
        }
        mNearUserListView.setEmptyView(emptyView);

        mNearbyListAdapter.setOnItemVideoAnchorListener(new VideoAnchorAdapter.OnItemVideoAnchorListener() {
            @Override
            public void onItemVideo(VideoAnchorItem videoAnchorItem) {

                Intent intent = new Intent(getActivity(), VideoDetailsActivity.class);
                intent.putExtra(VideoDetailsActivity.KEY_VIDEO_UID,videoAnchorItem.uid);
                intent.putExtra(VideoDetailsActivity.KEY_VIDEO_PATH,videoAnchorItem.video);
                intent.putExtra(VideoDetailsActivity.KEY_VIDEO_IMG,videoAnchorItem.pic);
                startActivity(intent);
            }
        });

        pullDownToRequest();

    }

    // 下拉请求
    private void handlePullDown() {
        if (!ConnectorManage.getInstance(BaseApplication.appContext).CheckNetwork(BaseApplication.appContext))// 网络是连接
        {
            mHandler.postDelayed(
                    new Runnable() {
                        public void run() {
                            ErrorCode.toastError(BaseApplication.appContext, ErrorCode.E_101);
                            mNearUserListView.onRefreshComplete();
                        }
                    }, 200);
            return;
        }

        pullDownToRequest();
    }


    /**
     * 请求视频列表
     * @param page
     */
    public void reqData(int page) {

        int type = StartModel.getInstance().getLoginConnnetStatus();
        // 定时检测网络连接情况
        if (type == StartModel.TYPE_NET_WORK_DISABLED) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), R.string.network_req_failed, Toast.LENGTH_SHORT).show();
                    mNearUserListView.onRefreshComplete();
                }
            }, 200);

            return;
        }
        mCurPage = page;

        AnchorsCertificationProtocol.getAnchorList(getActivity(),mCurPage,PAGE_SIZE,this);

    }

    //下拉更新数据
    private void pullDownToRequest() {
        mNearUserListView.getRefreshableView().removeFooterView(footerView);
        mCurPage = 1;

        reqData(mCurPage);
        isFirstPageLoaded = false;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFirstPageLoaded) {
                    ErrorCode.toastError(BaseApplication.appContext, ErrorCode.E_101);
                    mNearUserListView.onRefreshComplete();
                }
            }
        }, 10 * 1000);
    }

    // 跳转主播认证
    public void skipToFilter(){
        if (isAdded()){
            Intent intent = new Intent(getActivity(), AnchorsCertificationActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (Constant.isSuccess(result)) {
            handleNearbyData(result);

        } else {
            BaseServerBean errorBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
            if (errorBean != null) {
                onGeneralError(errorBean.error, flag);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        stopPulling();
        ErrorCode.toastError(BaseApplication.appContext, e);
    }

    public void stopPulling() {
        if (mNearUserListView != null) {
            mNearUserListView.onRefreshComplete();
        }
    }

    private void handleNearbyData(String result) {
        if (mNearUserListView != null) {
            mNearUserListView.onRefreshComplete();
        }
        CommonFunction.log(TAG, "result***" + result);

        VideoAnchorBean bean = GsonUtil.getInstance().getServerBean(result,
                VideoAnchorBean.class);
        if (bean != null && bean.isSuccess()) {

            if (bean.list == null) {
                if (mCurPage == 1) {
                    mNearbyUsers.clear();
                    CommonFunction.log(TAG, "show empty");

                    footerView.setVisibility(View.VISIBLE);
                    mNearUserListView.getRefreshableView().addFooterView(footerView);
                    emptyTipsText.setText(getString(R.string.nearby_user_list_empty_tips));

                }
                return;
            }

            mCurPage = bean.pageno;
            int amount = bean.amount;
            mTotalPage = amount / PAGE_SIZE;
            if (amount % PAGE_SIZE > 0) {
                mTotalPage++;
            }
            CommonFunction.log(TAG, "success mTotalPage***" + mTotalPage);

            if (mCurPage <= 1) {
                mNearbyUsers.clear();
                mNearUserListView.getRefreshableView().removeFooterView(footerView);

                isFirstPageLoaded = true;
            }

            if (bean.list != null) {

                toUserBean(bean.list);
            }

            if (mNearbyListAdapter != null) {
                mNearbyListAdapter.updateData(mNearbyUsers);
            }

            if (mNearUserListView != null) {
                if (mNearbyUsers.isEmpty()) {
                    CommonFunction.toastMsg(getActivity(), R.string.no_data);
                } else {
                    mNearUserListView.getRefreshableView().removeFooterView(footerView);
                }
            }
        }

    }

    /**
     * 转成Bean
     * @param users
     */
    private void toUserBean(ArrayList<VideoAnchorItem> users){

        float size = (float) users.size() / (float) 2;

        for (int i = 1; i <= Math.round(size); i++) {
            VideoAnchorsItem nearUserItem = new VideoAnchorsItem();
            nearUserItem.leftUser = users.get(2 * i - 2);
            if (users.size() == 2 * i - 1){
                nearUserItem.rightUser = null;
            }else{
                nearUserItem.rightUser = users.get(2 * i - 1);
            }
            mNearbyUsers.add(nearUserItem);
        }

    }

}
