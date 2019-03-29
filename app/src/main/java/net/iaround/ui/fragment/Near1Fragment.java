
package net.iaround.ui.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BuildConfig;
import net.iaround.R;
import net.iaround.annotation.IAroundAD;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.NearHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.AdLink;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.NearByExtendUser;
import net.iaround.model.entity.NearByExtendUser.NearByUser;
import net.iaround.model.entity.NearRank;
import net.iaround.model.entity.NearbyFilterConditionsInfo;
import net.iaround.model.entity.NearbyUserListBean;
import net.iaround.model.entity.RefreshRecord;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.LocationUtil.MLocationListener;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.FilterActivity;
import net.iaround.ui.activity.MainFragmentActivity.PagerSelectNear;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.map.ActivityLocationMap;
import net.iaround.ui.view.FlyBanner;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.near.NearbyActivityBannerView;
import net.iaround.utils.eventbus.HeadImageNotifyEvent;
import net.iaround.utils.eventbus.NoteNameNotifyEvent;
import net.iaround.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;


public class Near1Fragment extends LazyLoadBaseFragment implements OnClickListener, MLocationListener, HttpCallBack, PagerSelectNear {
    private final String TAG = "Near1Fragment";

    private final String SP_CATCH = "near_focus_data";
    private static final int PAGE_SIZE = 24;
    private final String PULL_DOWN_TO_REFRESH_LAST_TIME = "pull_down_to_refresh_last_time";

    Activity mActivity;
    Context mContext;

//    /**
//     * 标题
//     */
//    private TextView mTitleText;
//    //新标题
//    private ImageView mIvLeft;
//    private ImageView mIvRight, mIvRight_1;

    private PullToRefreshListView mNearUserListView;
    private View emptyView;
    private NearbyListAdapter mNearbyListAdapter;
    private View footerView;
    private TextView emptyTipsText;

    private Button mSwitchBtn;
    private View headerView;
    private FlyBanner topbannersView;

    private LocationUtil mLocationUtil;
    private ArrayList<NearByExtendUser> mNearbyUsers;// 当前附近用户
    private HashSet<Long> userIdList = new HashSet<Long>();
    private int mCurPage = 1;// 当前页
    private int mTotalPage;// 总页数
    private String mCacheKeyNearby;
    private String mRefreshLastTimeKey;

    private boolean isChangeConditions = false;
    private NearbyFilterConditionsInfo filterData;

    private int mVipquery;
    private GeoData userGeoData;

    private long GET_NEARBY_USER_LIST_FLAG;

    private Handler mHandler;

    /**
     * 是否成功加载第一页数据，true为成功，false为失败
     */
    private Boolean isFirstPageLoaded = false;

    private Receiver receiver;

    private ArrayList<AdLink> bannerData = new ArrayList<AdLink>();
    private NearRank nearRank = new NearRank();

    private int mFirstVisibleItem, mVisibleItemCount;
    private int headItemCount = 2; //用于广告展示统计

    private Object nativeADDataRef;  //com.qq.e.ads.nativ.NativeADDataRef

    public static int REQ_CODE_NEAR_FILTER = 208;

    private int inmobiLoad;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mActivity = activity;
        mContext = activity;

    }

    @Override
    protected int setContentView() {
        return R.layout.main_nearby_view;
    }

    @Override
    protected boolean lazyLoad() {
        initData();
        setListeners();
        initAnimation();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//        View view = LayoutInflater.from(mActivity).inflate(R.layout.main_nearby_view, null, false);
        emptyView = inflater.inflate(R.layout.no_data_view, null);
        initViews(getContentView());
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        CommonFunction.log(TAG,"onCreateView() into");
//        return inflateAndSetupView(inflater, container, savedInstanceState);
//    }
//
//    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
//                                     Bundle savedInstanceState) {
//        View view = LayoutInflater.from(mActivity).inflate(R.layout.main_nearby_view, null, false);
//        emptyView = inflater.inflate(R.layout.no_data_view, null);
//        initViews(view);
//        initData();
//        setListeners();
//        initAnimation();
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//    }

    private void initViews(View view) {
//        mIvLeft = (ImageView) view.findViewById(R.id.iv_left);
//        mTitleText = (TextView) view.findViewById(R.id.tv_title);
//        mIvRight = (ImageView) view.findViewById(R.id.iv_right);
//        mIvRight_1 = (ImageView) view.findViewById(R.id.iv_right);
//        mIvRight_1.setVisibility(View.VISIBLE);
//        mIvLeft.setVisibility(View.GONE);
//        mIvRight_1.setImageResource(R.drawable.group_contacts_search);
//        mIvRight.setImageResource(R.drawable.near_filter);



        headerView = LayoutInflater.from(mContext).inflate(R.layout.nearby_entrance_bar, null);

        topbannersView = (FlyBanner) headerView.findViewById(R.id.vp_charbarpopular);

        initNearUserListView(view);
        mNearUserListView.getRefreshableView().addHeaderView(headerView);

        mSwitchBtn = (Button) view.findViewById(R.id.switch_mode_btn);

        footerView = LayoutInflater.from(mContext).inflate(
                R.layout.x_nearby_fragment_empty_view, null);
        emptyTipsText = (TextView) footerView.findViewById(R.id.empty_text);

    }

    /**
     * @Title: setListeners
     * @Description: 设置监听器
     */
    private void setListeners() {
        mSwitchBtn.setOnClickListener(this);
        footerView.setOnClickListener(this);
//        mIvRight.setOnClickListener(this);
//        mIvRight_1.setOnClickListener(this);

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
            mNearbyListAdapter = new NearbyListAdapter();
            mNearUserListView.setAdapter(mNearbyListAdapter);
        }
        mNearUserListView.setEmptyView(emptyView);
        mVipquery = Common.getInstance().loginUser.isVip() ? 1 : 0;
        initFilterData();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                pullDownToRequest();
            }
        }, 2000);

        mCurPage = 1;
        mTotalPage = 1;
        if (mNearbyUsers == null) {
            mNearbyUsers = new ArrayList<>();
        }

        long uid = Common.getInstance().loginUser.getUid();
        mCacheKeyNearby = SharedPreferenceUtil.CACHE_NEAR_FRIEND + "_" + uid;
        mRefreshLastTimeKey = PULL_DOWN_TO_REFRESH_LAST_TIME + uid;

        loadBufferedData();

        IntentFilter filter = new IntentFilter();
        filter.addAction("net.iaround.fragment");
        filter.setPriority(1000);
        receiver = new Receiver();

        if (mActivity != null)
            mActivity.registerReceiver(receiver, filter);

//        mTitleText.setText(getResources().getString(R.string.near_fragment));

        String nearFocus = SharedPreferenceUtil.getInstance(mContext).getString(SP_CATCH);
        if (!TextUtils.isEmpty(nearFocus)) {
            nearRank = GsonUtil.getInstance().getServerBean(nearFocus, NearRank.class);
        }

        initAdData();

    }

    @IAroundAD
    private void initAdData(){
        if(BuildConfig.showAdvert==true && CommonFunction.updateAdCount(TAG)) {
            net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().loadAdDatas();
        }
    }

    /**
     * 点击两次按钮刷新数据
     *
     * @param isTrue
     */
    public void isFastPressTwice(boolean isTrue) {
        if (isTrue) {
            mNearUserListView.setRefreshing(true);
        }
    }

    /**
     * 初始化过滤用户条件
     */
    private void initFilterData() {
        if (filterData == null) {
            filterData = new NearbyFilterConditionsInfo();
        }
        filterData.constellation = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_CONSTELLATION + Common.getInstance().loginUser.getUid(), 0);
        filterData.dialects = 0;
        filterData.distance = 0;
        filterData.gender = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_GENDER + Common.getInstance().loginUser.getUid(), 0);
        filterData.logintime = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_TIME + Common.getInstance().loginUser.getUid(), 3);
        filterData.love = 0;
        filterData.maxage = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(), -1);
        filterData.minage = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(), -1);
        filterData.profession = 0;
        filterData.hometown = "";
    }

    /**
     * 成功加载缓存数据，会返回true
     */
    public boolean loadBufferedData() {
        String currentResultNearby = SharedPreferenceUtil.getInstance(mContext).getString(mCacheKeyNearby);
        if (!CommonFunction.isEmptyOrNullStr(currentResultNearby)) {
            try {
                handleNearbyData(currentResultNearby,1);
                return true;
            } catch (Throwable t) {
                CommonFunction.log(t);
            }
        }

        return false;
    }

    /**
     * 初始化附近用户列表View
     */
    private void initNearUserListView(View view) {
        if (mNearUserListView == null) {
            mNearUserListView = (PullToRefreshListView) view.findViewById(R.id.nearby_user_listview);
            mNearUserListView.setMode(Mode.BOTH);

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
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            CommonFunction.log(TAG, "mCurPage***" + mCurPage);
                            CommonFunction.log(TAG, "mTotalPage***" + mTotalPage);
                            if (mCurPage < mTotalPage) {
                                reqData(mCurPage + 1);
                            } else {
                                mHandler.postDelayed(new Runnable() {
                                    public void run() {
                                        Toast.makeText(mContext, R.string.no_more, Toast.LENGTH_SHORT).show();
                                        mNearUserListView.onRefreshComplete();
                                    }
                                }, 200);
                            }
                        }
                    });
        } else {
            mTotalPage = mCurPage + 1;
        }

        mNearUserListView.setVisibility(View.VISIBLE);
    }

    private void handlePullDown() {
        if (isChangeConditions) {
            isChangeConditions = false;
            pullDownToRequest();
        } else {
            if (!ConnectorManage.getInstance(mContext).CheckNetwork(mContext))// 网络是连接
            {
                mHandler.postDelayed(
                        new Runnable() {
                            public void run() {
                                ErrorCode.toastError(mContext, ErrorCode.E_101);
                                CommonFunction.log(TAG,"网络没有链接");
                                mNearUserListView.onRefreshComplete();
                            }
                        }, 200);
                return;
            }
            String recordStr = SharedPreferenceUtil.getInstance(mContext).getString(mRefreshLastTimeKey);
            CommonFunction.log(TAG, "recordStr***" + recordStr);
            if (!TextUtils.isEmpty(recordStr)) {
                RefreshRecord record = GsonUtil.getInstance().getServerBean(recordStr, RefreshRecord.class);
                if (System.currentTimeMillis() - record.lastTime < 5 * 1000 && record.isSuccess) {
                    //去掉请求提示
                    mHandler.postDelayed(
                            new Runnable() {
                                public void run() {
                                    mNearUserListView.onRefreshComplete();
                                }
                            }, 500);
                } else {
                    pullDownToRequest();
                }
            } else {
                pullDownToRequest();
            }
        }
    }

    private void pullDownToRequest() {
        mNearUserListView.getRefreshableView().removeFooterView(footerView);
        mCurPage = 1;

        reqData(mCurPage);
        isFirstPageLoaded = false;
        mHandler.removeCallbacks(mStopPullRunnable);
        mHandler.postDelayed(mStopPullRunnable, 30 * 1000);
    }

    public void reqData(int page) {

        int type = StartModel.getInstance().getLoginConnnetStatus();
        // 定时检测网络连接情况
        if (type == StartModel.TYPE_NET_WORK_DISABLED) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, R.string.network_req_failed, Toast.LENGTH_SHORT).show();
                    mNearUserListView.onRefreshComplete();
                }
            }, 200);

            return;
        }
        mCurPage = page;
        // 当为第1页时，使用缓存，不再获取经纬度
        if (page == 1) {
            if (mLocationUtil == null) {
                mLocationUtil = new LocationUtil(mContext);
            }
            mLocationUtil.startListener(this, 0);
            return;
        }

        GeoData geo = LocationUtil.getCurrentGeo(mContext);
        int lat = 0;
        int lng = 0;
        if (LocationUtil.getCurrentGeo(mContext) != null && geo.getLat() != 0 && geo.getLng() != 0) {
            lat = geo.getLat();
            lng = geo.getLng();
        }
        updateLocation(1, lat, lng, "", "");
    }

    public void toFilter() {
        Intent intent = new Intent(mContext, FilterActivity.class);
        intent.putExtra(FilterActivity.KEY_FILTER, 2);
        startActivityForResult(intent, REQ_CODE_NEAR_FILTER);
    }

    @Override
    public void onClick(View v) {
//        if (v.equals(mIvRight)) {
//            Intent intent = new Intent(mContext, FilterActivity.class);
//            intent.putExtra(FilterActivity.KEY_FILTER, 2);
//            startActivityForResult(intent, REQ_CODE_NEAR_FILTER);
//        } else
        if (v.equals(mSwitchBtn)) {
            Intent intent = new Intent(mContext, ActivityLocationMap.class);
            startActivity(intent);
        } else if (v.equals(footerView)) {
            footerView.setVisibility(View.GONE);
            performPulling();
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        CommonFunction.log(TAG, "result---" + result);
        if (Constant.isSuccess(result)) {
            if (flag == GET_NEARBY_USER_LIST_FLAG) {
                handleNearbyData(result,2);
            }

        } else {
            BaseServerBean errorBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
            if (errorBean != null) {
                onGeneralError(errorBean.error, flag);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        // TODO Auto-generated method stub
        if (flag == GET_NEARBY_USER_LIST_FLAG) {
            stopPulling();
            ErrorCode.toastError(mContext, e);
            if (mCurPage <= 1) {
                saveRefreshRecord(false);
            }
        }
    }

    private void saveRefreshRecord(boolean isSuccess) {
        RefreshRecord record = new RefreshRecord();
        record.isSuccess = isSuccess;
        record.lastTime = System.currentTimeMillis();
        String recordStr = GsonUtil.getInstance().getStringFromJsonObject(record);
        SharedPreferenceUtil.getInstance(mContext).putString(
                mRefreshLastTimeKey, recordStr);
    }

    private void handleNearbyData(String result,int isloadBuff) {
        if (mNearUserListView != null) {
            mNearUserListView.onRefreshComplete();
        }
        CommonFunction.log(TAG, "result***" + result);

        NearbyUserListBean bean = GsonUtil.getInstance().getServerBean(result, NearbyUserListBean.class);
        if (bean != null && bean.isSuccess()) {
            if(bean.users == null){
                return;
            }
            for (int i = 0; i < bean.users.size(); i++) {
                if (bean.users.get(i).type == 2) {
                    bean.users.remove(i);
                }
            }
            if (bean.topbanners != null) {
                if (bean.topbanners.size() > 0) {
                    topbannersView.setImagesUrl(bean.topbanners,false);
                } else {
                    topbannersView.setVisibility(View.GONE);
                    mNearUserListView.getRefreshableView().removeHeaderView(headerView);
                    headItemCount = 1;
                }
            }

            if (bean.users == null) {
                if (mCurPage == 1) {
                    mNearbyUsers.clear();
                    if (mNearbyListAdapter != null) {
                        mNearbyListAdapter.notifyDataSetChanged();
                    }

                    CommonFunction.log(TAG, "show empty");

                    footerView.setVisibility(View.VISIBLE);
                    mNearUserListView.getRefreshableView().addFooterView(footerView);
                    emptyTipsText.setText(getString(R.string.nearby_user_list_empty_tips));

                    SharedPreferenceUtil.getInstance(mContext).putString(mCacheKeyNearby, "");
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
                SharedPreferenceUtil.getInstance(mContext).putString(mCacheKeyNearby, result);
                mNearbyUsers.clear();
                userIdList.clear();
                mNearUserListView.getRefreshableView().removeFooterView(footerView);

                saveRefreshRecord(true);
                isFirstPageLoaded = true;
            }

            if (mNearbyUsers == null) {
                mNearbyUsers = new ArrayList<>();
            }

            if (bean.users != null) {
                boolean adCenterHaveDatas = false;
                if(BuildConfig.showAdvert==true) {
                    isHaveADData();
                    adCenterHaveDatas = mIsHaveADData;
                }
                for (int i = 0; i < bean.users.size(); i++) {
                    NearByExtendUser item = bean.users.get(i);
                    // 如果是广告类型，则判断第三方广告中心是否有广告
                    if (!adCenterHaveDatas && item.type == ACTIVITY_THIRD_AD) {
                        continue;
                    }
                    if (item.user != null) {
                        if (!userIdList.contains(item.user.userid)) {
                            mNearbyUsers.add(item);
                        } else {
                            userIdList.add(item.user.userid);
                        }
                    } else {
                        mNearbyUsers.add(item);
                    }
                }

                if (isloadBuff == 2 & mCurPage < 2){
                    String advert = SharedPreferenceUtil.getInstance(getActivity()).getString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL);
                    if (!TextUtils.isEmpty(advert) && CommonFunction.updateAdCount(TAG)) {
                        String[] spilt = advert.split(":");
                        if (spilt.length >= 2 && !TextUtils.isEmpty(spilt[1])) {
                            String[] nearByAd = spilt[1].split(",");
                            if ("1".equals(nearByAd[0])) {
                                addGdtAdPosition(Integer.parseInt(nearByAd[1]));
                            } else {
                                inmobiLoad = 1;
                                addInmobiAdPosition(Integer.parseInt(nearByAd[1]));
                            }

                            if ("1".equals(nearByAd[2])) {
                                addGdtAdPosition(Integer.parseInt(nearByAd[3]));
                            } else {
                                inmobiLoad = 2;
                                addInmobiAdPosition(Integer.parseInt(nearByAd[3]));
                            }
                        }
                    }
                }

            }

            if (mNearbyListAdapter != null) {
                mNearbyListAdapter.notifyDataSetChanged();
            }

            if (mNearUserListView != null) {
                if (mNearbyUsers.isEmpty()) {
                    CommonFunction.toastMsg(mContext, R.string.no_data);
                } else {
                    mNearUserListView.getRefreshableView().removeFooterView(footerView);
                }
            }
        }
    }

    private boolean mIsHaveADData = false;
    @IAroundAD
    private void isHaveADData(){
        mIsHaveADData = net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().isHaveAdDatas();
    }

    @IAroundAD
    private void addGdtAdPosition(int position) {
        if(BuildConfig.showAdvert==true) {
            if (!net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().isLoadClose && position < mNearbyUsers.size()) {
                nativeADDataRef = net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().getNextAdData();
                NearByExtendUser user = new NearByExtendUser();
                user.type = 5;
                mNearbyUsers.add(position, user);
            }
        }
    }

    @IAroundAD
    private void addInmobiAdPosition(final int position) {
        if(BuildConfig.showAdvert==true) {
            if (position < mNearbyUsers.size()) {
                NearByExtendUser user = new NearByExtendUser();
                user.type = 6;
                mNearbyUsers.add(position, user);
            }
        }
    }

    @Override
    public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {
        CommonFunction.log(TAG, "updateLocation() into, type="+type + ", lat=" + lat + ", lng=" + lng+ ", address="+address);
        if (type == 0) { // 失败
            stopPulling();
            Toast.makeText(mContext, R.string.location_service_unavalible, Toast.LENGTH_SHORT).show();
            return;
        }

        if (this.userGeoData == null) {
            this.userGeoData = LocationUtil.getCurrentGeo(mContext);
        }
        mVipquery = Common.getInstance().loginUser.isVip() ? 1 : 0;

        try {
            GET_NEARBY_USER_LIST_FLAG = NearHttpProtocol.usersNearlist(mContext, lat, lng,
                    filterData.gender, filterData.logintime < 0 ? 0 : filterData.logintime,
                    filterData.minage, filterData.maxage, filterData.constellation, mCurPage, PAGE_SIZE,
                    mVipquery, filterData.profession, filterData.love, filterData.dialects,
                    filterData.hometown, filterData.distance, this);
        } catch (Throwable t) {
            stopPulling();
            CommonFunction.log(TAG,"获取附近聊表失败:" + t);
            Toast.makeText(mContext, R.string.network_req_failed, Toast.LENGTH_SHORT).show();
        }

        loadAdDatas();
    }

    @IAroundAD
    private void loadAdDatas(){
        if(BuildConfig.showAdvert==true && CommonFunction.updateAdCount(TAG)) {
            try {
                net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().loadAdDatas();
            } catch (Throwable t) {
                stopPulling();
                CommonFunction.log(TAG, "获取广告失败:" + t);
            }
        }
    }

    public void stopPulling() {
        if (mNearUserListView != null) {
            mNearUserListView.onRefreshComplete();
        }
    }

    public void performPulling() {
        if (mNearUserListView != null) {
            mNearUserListView.setRefreshing(true);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonFunction.log(TAG, "onActivityResult() requestCode="+requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQ_CODE_NEAR_FILTER && resultCode == Activity.RESULT_OK) {
            mNearUserListView.setRefreshing(true);
            initFilterData();
            pullDownToRequest();
        }
    }

    public void onResume() {
        super.onResume();

        CommonFunction.log("DataStatistics", "附近 onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            mActivity.unregisterReceiver(receiver);
        }
        EventBus.getDefault().unregister(this);

    }

    @Subscribe
    public void onEventMainThread(NoteNameNotifyEvent event) {
        // 更新他人昵称
        referNote(event.getMsg(), event.getUserId());
    }

    private void referNote(String name, long userId) {
        synchronized (mNearbyUsers) {
            for (int i = 0; i < mNearbyUsers.size(); i++) {
                NearByExtendUser nearByExtendUser = mNearbyUsers.get(i);
                if (nearByExtendUser.user != null) {
                    if (nearByExtendUser.user.userid == userId) {
                        nearByExtendUser.user.notename = name;
                        mNearbyListAdapter.notifyDataSetChanged(mNearUserListView.getRefreshableView(), i);
                    }
                }

            }
        }
    }

    @Subscribe
    public void onEventMainThread(HeadImageNotifyEvent event) {
        referAvatar();
    }

    /**
     * 更新头像
     */
    private void referAvatar() {
        synchronized (mNearbyUsers) {
            for (int i = 0; i < mNearbyUsers.size(); i++) {
                NearByExtendUser nearByExtendUser = mNearbyUsers.get(i);
                if (nearByExtendUser.user != null) {
                    if (nearByExtendUser.user.userid == Common.getInstance().loginUser.getUid()) {
                        nearByExtendUser.user.icon = Common.getInstance().loginUser.getVerifyicon();
                        mNearbyListAdapter.notifyDataSetChanged(mNearUserListView.getRefreshableView(), i);
                    }
                }

            }
        }
    }

    private class NearbyListAdapter extends BaseAdapter {


        public NearbyListAdapter() {
            super();
        }

        public int getCount() {
            if (mNearbyUsers != null) {
                return mNearbyUsers.size();
            }
            return 0;
        }

        public NearByExtendUser getItem(int position) {
            if (mNearbyUsers != null) {
                return mNearbyUsers.get(position);
            }
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            int type = mNearbyUsers.get(position).type;
            if (type == ACTIVITY_BANNER_TYPE) {
                return ACTIVITY_BANNER_TYPE;
            } else if (type == ACTIVITY_THIRD_AD) {
                return ACTIVITY_THIRD_AD;
            } else if (type == ACTIVITY_INMOBI_AD) {
                return ACTIVITY_INMOBI_AD;
            } else {
                return NORMAL_ITEM_TYPE;
            }
        }

        /**
         * 局部更新数据，调用一次getView()方法
         *
         * @param listView 要更新的listview
         * @param position 要更新的位置
         */
        public void notifyDataSetChanged(ListView listView, int position) {
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            int lastVisiblePosition = listView.getLastVisiblePosition();

            if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
                View view = listView.getChildAt(position - firstVisiblePosition);
                getView(position, view, listView);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getViewTypeCount() {
            return 7;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            for (int i = 0; i < mNearbyUsers.size(); i++) {
                if (mNearbyUsers.get(i).type == ACTIVITY_BANNER_TYPE) {
                    AdLink ad = new AdLink();
                    ad.position = i + headItemCount;
                    ad.isShowing = false;
                    ad.banner = mNearbyUsers.get(i).banner;
                    if (ad.banner != null) {
                        ad.ADid = mNearbyUsers.get(i).banner.id;
                        ad.banner.setThirtReport(mContext);
                    }
                    bannerData.add(ad);
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final NearByExtendUser item = mNearbyUsers.get(position);
            int itemViewType = getItemViewType(position);
            switch (itemViewType) {
                case ACTIVITY_BANNER_TYPE:// banner postbars
                    if (convertView == null) {
                        convertView = new NearbyActivityBannerView(mContext, item.banner);
                    } else {
                        ((NearbyActivityBannerView) convertView).refreshView(item.banner);
                    }
                    break;
                case ACTIVITY_THIRD_AD:// 第三方广告
                    if(BuildConfig.showAdvert==true) {
                        convertView = handleGDTItemView(convertView);
                    }
                    break;
                case ACTIVITY_INMOBI_AD://inmobi 广告
                    if(BuildConfig.showAdvert==true) {
                        convertView = handleInmobiItemView(convertView, parent);
                    }
                    break;
                case ACTIVITY_RECOMMENDS:
                    break;
                default:
                    NearHolder holder = null;
                    if (convertView == null || holder == null) {
                        holder = new NearHolder();
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_personal_view, null);
                        holder.headIcon = (HeadPhotoView) convertView.findViewById(R.id.user_icon);
                        holder.tvNickName = (TextView) convertView.findViewById(R.id.user_name);
                        holder.lyAge = (LinearLayout) convertView.findViewById(R.id.ly_user_age_left);
                        holder.ivAge = (ImageView) convertView.findViewById(R.id.iv_user_sex_left);
                        holder.tvAge = (TextView) convertView.findViewById(R.id.tv_user_age_left);
                        holder.tvConstellation = (TextView) convertView.findViewById(R.id.tv_user_constellation);
                        holder.tvNotes = (RichTextView) convertView.findViewById(R.id.user_notes);
                        holder.distance = (TextView) convertView.findViewById(R.id.user_location);
                        convertView.setTag(holder);
                    } else {
                        holder = (NearHolder) convertView.getTag();
                    }

                    User user = new User();
                    if (item.user == null) return null;
                    user.setIcon(item.user.icon);
                    if(item.user.userid == Common.getInstance().loginUser.getUid()){
                        String verifyicon = Common.getInstance().loginUser.getVerifyicon();
                        if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                            user.setIcon(verifyicon);
                        }
                    }

                    user.setSVip(item.user.svip);//YC   将vip改为svip
                    user.setViplevel(item.user.viplevel);//YC 添加viplevel字段
                    user.setUid(item.user.userid);

                    holder.headIcon.execute(ChatFromType.UNKONW, user, null);

                    String name = item.user.notename;


                    SpannableString spName;
                    if (TextUtils.isEmpty(name) || name == null) {
                        if (!"".equals(item.user.notes) && item.user.notes != null) {
                            spName = FaceManager.getInstance(getContext()).parseIconForString(getContext(), item.user.notes, 0, null);
                        } else {
                            if (!TextUtils.isEmpty(item.user.nickname)) {
                                spName = FaceManager.getInstance(getContext()).parseIconForString(getContext(), item.user.nickname, 0, null);
                            } else {
                                spName = FaceManager.getInstance(getContext()).parseIconForString(getContext(), String.valueOf(item.user.userid), 0, null);
                            }
                        }

                        holder.tvNickName.setText(spName);
                    } else if (!TextUtils.isEmpty(name) || name != null) {
                        spName = FaceManager.getInstance(mContext).parseIconForString(mContext, name, 0, null);
                        holder.tvNickName.setText(spName);
                    }


                    if (item.user.svip > 0) {//yuchao  将vip改为svip
                        holder.tvNickName.setTextColor(Color.parseColor("#FF4064"));
                    } else {
                        holder.tvNickName.setTextColor(Color.parseColor("#000000"));
                    }

                    if (item.user.getSex() == 1) {
                        holder.ivAge.setImageResource(R.drawable.thread_register_man_select);
                        holder.lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                    } else {
                        holder.ivAge.setImageResource(R.drawable.thread_register_woman_select);
                        holder.lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                    }
                    try {
                        holder.tvAge.setText("" + item.user.age);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //设置星座
                    String[] items = mContext.getResources().getStringArray(R.array.horoscope_date);
                    holder.tvConstellation.setText(items[item.user.horoscope]);

                    String content = null;
                    if(item.getGname() == null) {
                        //聊吧为空则显示签名
                        content = item.user.selftext;
                        if (content == null || content.length() <= 0 || content.equals("null")) {
                            holder.tvNotes.setVisibility(View.GONE);
                            content = item.user.selftext;
                        }
                        SpannableString spContent = FaceManager.getInstance(mContext).parseIconForString(mContext, content, 0, null);
                        //判断签名是否为空
                        if (spContent != null && content != null && content.length() > 0) {
                            holder.tvNotes.setVisibility(View.VISIBLE);
                            holder.tvNotes.setText(spContent);
                        }
                    }else{
                        content = mContext.getResources( ).getString(R.string.show_chat_bar_one) + " " +
                                "<font color=\"#fc2452\">" + item.getGname() + "</font>" + " " +
                                mContext.getResources( ).getString(R.string.show_chat_bar_two);
                        holder.tvNotes.setText(content);
                        holder.tvNotes.parseIconWithColor(ScreenUtils.dp2px(14));
                        holder.tvNotes.setVisibility(View.VISIBLE);
                    }

                    if (item.distance < 0) { // 不可知
                        holder.distance.setText(TimeFormat.timeFormat1(mContext, item.user.lastonlinetime) + " · ");
                    } else {
                        holder.distance.setText(TimeFormat.timeFormat1(mContext, item.user.lastonlinetime) + " · " + CommonFunction.covertSelfDistance(item.distance));
                    }
                    convertView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickUser(item);
                        }
                    });
                    break;
            }

            return convertView;
        }

        private class NearHolder {
            HeadPhotoView headIcon;
            TextView tvNickName;
            LinearLayout lyAge;
            ImageView ivAge;
            TextView tvAge;
            TextView tvConstellation;
            RichTextView tvNotes;
            TextView distance;
        }

        public void onClickUser(NearByExtendUser neu) {
            NearByUser u = neu.user;
            User user = u.convertBaseToUser();

            user.setUid(u.userid);
            user.setNickname(u.nickname);
            user.setNoteName(u.notes);
            user.setIcon(u.icon);
            user.setViplevel(u.viplevel);
            user.setPhotoNum(u.photonum);
            user.setJob(u.occupation >= 0 ? u.occupation : -1);
            user.setSign(u.selftext);
            int sex = 0;
            if ("f".equals(u.gender)) {
                sex = 2;
            } else if ("m".equals(u.gender)) {
                sex = 1;
            }
            user.setSex(sex);
            user.setLat(u.lat);
            user.setLng(u.lng);
            user.setAge(u.age);
            user.setPersonalInfor(u.selftext);
            user.setLastLoginTime(u.lastonlinetime);
            user.setWeibo(u.weibo);
            user.setDistance(u.distance);
            user.setRelationship(u.relation);
            if (user.getUid() == Common.getInstance().loginUser.getUid()) {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                intent.putExtra(Constants.UID, user.getUid());
                intent.putExtra("user", Common.getInstance().loginUser);
                startActivityForResult(intent, 201);
            }else {
                if (neu.getGname() == null) {
                    Intent intent = new Intent(getContext(), OtherInfoActivity.class);
                    intent.putExtra(Constants.UID, user.getUid());
                    intent.putExtra("user", user);
                    startActivity(intent);
                } else if (neu.getGid() != 0) {
                    Intent intent = new Intent(getContext(), OtherInfoActivity.class);
                    intent.putExtra(Constants.UID, user.getUid());
                    intent.putExtra("user", user);
                    intent.putExtra("chatbarid", neu.getGid() + "");  //需要显示聊吧
                    startActivity(intent);
                }
            }

        }
    }

    @IAroundAD
    private View handleGDTItemView(View convertView){
        if(BuildConfig.showAdvert==true) {
            if (convertView == null) {
                convertView = new net.iaround.ui.view.near.NearThridGdtAdView(mContext, (com.qq.e.ads.nativ.NativeADDataRef)nativeADDataRef);
            } else {
                ((net.iaround.ui.view.near.NearThridGdtAdView) convertView).refreshView((com.qq.e.ads.nativ.NativeADDataRef)nativeADDataRef);
            }
        }
        return convertView;
    }

    @IAroundAD
    private View handleInmobiItemView(View convertView, ViewGroup parent){
        if(BuildConfig.showAdvert==true) {
            if (convertView == null) {
                convertView = new net.iaround.ui.view.near.NearThridGdtAdView(mContext, convertView, parent, inmobiLoad);
            } else {
                ((net.iaround.ui.view.near.NearThridGdtAdView) convertView).notifyRefershView(parent,convertView, inmobiLoad);
            }
            if (inmobiLoad == 2) {
                inmobiLoad = 1;
            } else if (inmobiLoad == 1) {
                inmobiLoad = 0;
            }
        }
        return convertView;
    }


    /**
     * 初始悬浮按钮的动画效果
     */
    private void initAnimation() {
        final AnimationSet mAnimationShow = new AnimationSet(true);

        TranslateAnimation mTransAniShow = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
        mTransAniShow.setAnimationListener(
                new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }
                });
        mTransAniShow.setDuration(600);
        mAnimationShow.addAnimation(mTransAniShow);

        final AnimationSet mAnimationHide = new AnimationSet(true);
        TranslateAnimation mTransAniHide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
        mTransAniHide.setAnimationListener(
                new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
        mTransAniHide.setDuration(600);
        mAnimationHide.addAnimation(mTransAniHide);

        mNearUserListView.setOnScrollListener(
                new AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        mFirstVisibleItem = firstVisibleItem;
                        mVisibleItemCount = visibleItemCount;
                    }
                });
    }


    @Override
    public void onNearSelected() {
        for (int i = 0; i < bannerData.size(); i++) {
            bannerData.get(i).isShowing = false;
        }
        CommonFunction.log("DataStatistics", "附近点击触发的广告");

    }

    public interface SwitchTabFragmentCallback {
        void switchToChatBarFragment();
    }

    public class Receiver extends BroadcastReceiver {
        //收到 登录成功的广播
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getExtras() != null) {
                String name = intent.getExtras().getString("login");
                if ("LONGIN_SUCCESS".equals(name)) {
                    pullDownToRequest();
                }
            }
        }

    }

    private final int NORMAL_ITEM_TYPE = 0;
    private final int ACTIVITY_RECOMMENDS = 2;
    private final int ACTIVITY_BANNER_TYPE = 3;
    private final int ACTIVITY_THIRD_AD = 5;
    private final int ACTIVITY_INMOBI_AD = 6;

    private int indexRemove = 0;

    public void skipToFilter(){
        if (isAdded()){
            Intent intent = new Intent(getActivity(), FilterActivity.class);
            intent.putExtra(FilterActivity.KEY_FILTER, 2);
            startActivityForResult(intent, REQ_CODE_NEAR_FILTER);
        }

    }

    //超时停止下拉刷新
    private Runnable mStopPullRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isFirstPageLoaded) {
                ErrorCode.toastError(mContext, ErrorCode.E_101);
                CommonFunction.log(TAG,"第一页加载超时(10秒)");
                mNearUserListView.onRefreshComplete();
            }
        }
    } ;
}
