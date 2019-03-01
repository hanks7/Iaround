//package net.iaround.ui.fragment;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshListView;
//import com.qq.e.ads.nativ.NativeADDataRef;
//
//import net.iaround.R;
//import net.iaround.conf.Common;
//import net.iaround.conf.Constant;
//import net.iaround.conf.ErrorCode;
//import net.iaround.connector.ConnectorManage;
//import net.iaround.connector.HttpCallBack;
//import net.iaround.connector.protocol.NearHttpProtocol;
//import net.iaround.model.entity.AdLink;
//import net.iaround.model.entity.GeoData;
//import net.iaround.model.entity.NearByExtendUser;
//import net.iaround.model.entity.NearUserBean;
//import net.iaround.model.entity.NearUserItem;
//import net.iaround.model.entity.NearbyFilterConditionsInfo;
//import net.iaround.model.entity.NearbyUserListBean;
//import net.iaround.model.entity.RefreshRecord;
//import net.iaround.model.im.BaseServerBean;
//import net.iaround.tools.CommonFunction;
//import net.iaround.tools.GsonUtil;
//import net.iaround.tools.LocationUtil;
//import net.iaround.tools.SharedPreferenceUtil;
//import net.iaround.ui.activity.FilterActivity;
//import net.iaround.ui.adapter.NearUserAdapter;
//import net.iaround.ui.datamodel.StartModel;
//import net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
///**
// *
// * 新附近列表
// * Created by gh on 2017/11/10.
// */
//
//public class NearFragment extends BaseFragment implements LocationUtil.MLocationListener, HttpCallBack {
//
//    private PullToRefreshListView mNearUserListView;
//
//    public static int REQ_CODE_NEAR_FILTER = 208;
//
//    private static final int PAGE_SIZE = 22;
//    private int mCurPage = 1;// 当前页++++++++++++++++++++
//    private int mTotalPage;// 总页数
//    private String mCacheKeyNearby;
//    private String mRefreshLastTimeKey;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        return LayoutInflater.from(getActivity()).inflate(R.layout.main_nearby_view,
//                null, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        initView();
//        initData();
//
//    }
//
//    /**
//     * 初始化View
//     */
//    private void initView() {
//        emptyView = LayoutInflater.from(mContext).inflate(R.layout.no_data_view, null);
//        footerView = LayoutInflater.from(mContext).inflate(
//                R.layout.x_nearby_fragment_empty_view, null);
//        emptyTipsText = (TextView) footerView.findViewById(R.id.empty_text);
//
//        if (mNearUserListView == null) {
//            mNearUserListView = (PullToRefreshListView) getView()
//                    .findViewById(R.id.nearby_user_listview);
//            mNearUserListView.setMode(PullToRefreshBase.Mode.BOTH);
//
//            mNearUserListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
//            mNearUserListView.getRefreshableView().setDividerHeight(0);
//            mNearUserListView.getRefreshableView().setFadingEdgeLength(-1);
//            mNearUserListView.getRefreshableView().setSelector(R.drawable.transparent);
//            mNearUserListView.getRefreshableView().setFastScrollEnabled(false);
//            mNearUserListView
//                    .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//
//                        @Override
//                        public void onPullDownToRefresh(
//                                PullToRefreshBase<ListView> refreshView) {
//                            handlePullDown();
//                        }
//
//                        @Override
//                        public void onPullUpToRefresh(
//                                PullToRefreshBase<ListView> refreshView) {
//                            CommonFunction.log(TAG, "mCurPage***" + mCurPage);
//                            CommonFunction.log(TAG, "mTotalPage***" + mTotalPage);
//                            if (mCurPage < mTotalPage) {
//                                reqData(mCurPage + 1);
//                            } else {
//                                mHandler.postDelayed(new Runnable() {
//                                    public void run() {
//                                        Toast.makeText(mContext, R.string.no_more,
//                                                Toast.LENGTH_SHORT).show();
//                                        mNearUserListView.onRefreshComplete();
//                                    }
//                                }, 200);
//                            }
//                        }
//                    });
//        } else {
//            mTotalPage = mCurPage + 1;
//        }
//
//            mNearUserListView.setVisibility(View.VISIBLE);
//    }
//
//    private final String TAG = "NearFragment";
//    private Context mContext;
//    private Handler mHandler;
//    private LocationUtil mLocationUtil;
//
//    private GeoData userGeoData;
//    private NearbyFilterConditionsInfo filterData;
//
//    private List<NearUserBean> mNearbyUsers = new ArrayList<>();// 当前附近用户
//    private HashSet<Long> userIdList = new HashSet<Long>();
//    private List<NearByExtendUser> cacheUser = new ArrayList<>();//缓存上一次加载余下的数据
//
//
//
//    private long GET_NEARBY_USER_LIST_FLAG;
//    /**
//     * 是否成功加载第一页数据，true为成功，false为失败
//     */
//    private Boolean isFirstPageLoaded = false;
//    private final String PULL_DOWN_TO_REFRESH_LAST_TIME = "pull_down_to_refresh_last_time";
//
//    private boolean isChangeConditions = false;
//
//    private int mVipquery;
//    private NearUserAdapter mNearbyListAdapter;
//    private View emptyView;
//    private View footerView;
//    private TextView emptyTipsText;
//
//    @Override
//    public void onAttach(Activity activity) {
//        // TODO Auto-generated method stub
//        super.onAttach(activity);
//        mContext = (Context) activity;
//
//    }
//
//
//    /**
//     * @Title: initData
//     * @Description: 初始化数据
//     */
//    private void initData() {
//        if (mHandler == null) {
//            mHandler = new Handler();
//        }
//        if (mNearbyListAdapter == null) {
//            mNearbyListAdapter = new NearUserAdapter(getActivity(),mNearbyUsers);
//            mNearUserListView.setAdapter(mNearbyListAdapter);
//        }
//        mNearUserListView.setEmptyView(emptyView);
//        mVipquery = Common.getInstance().loginUser.isVip() ? 1 : 0;
//        initFilterData();
//        mHandler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                pullDownToRequest();
//            }
//        }, 2000);
//
//        mCurPage = 1;
//        mTotalPage = 1;
//
//        long uid = Common.getInstance().loginUser.getUid();
//        mCacheKeyNearby = SharedPreferenceUtil.CACHE_NEAR_FRIEND + "_"
//                + uid;
//        mRefreshLastTimeKey = PULL_DOWN_TO_REFRESH_LAST_TIME + uid;
//
//        loadBufferedData();
//
//
//    }
//
//    /**
//     * 成功加载缓存数据，会返回true
//     */
//    public boolean loadBufferedData() {
//        String currentResultNearby = SharedPreferenceUtil.getInstance(mContext).getString(
//                mCacheKeyNearby);
//        if (!CommonFunction.isEmptyOrNullStr(currentResultNearby)) {
//            try {
//                handleNearbyData(currentResultNearby,1);
//
//                return true;
//            } catch (Throwable t) {
//                CommonFunction.log(t);
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * 初始化过滤用户条件
//     */
//    private void initFilterData() {
//        if (filterData == null) {
//            filterData = new NearbyFilterConditionsInfo();
//        }
//        filterData.constellation = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_CONSTELLATION + Common.getInstance().loginUser.getUid(), 0);
//        filterData.dialects = 0;
//        filterData.distance = 0;
//        filterData.gender = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_GENDER + Common.getInstance().loginUser.getUid(), 0);
//        filterData.logintime = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_TIME + Common.getInstance().loginUser.getUid(), 3);
//        filterData.love = 0;
//        filterData.maxage = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(), -1);
//        filterData.minage = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.NEAR_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(), -1);
//        filterData.profession = 0;
//        filterData.hometown = "";
//    }
//
//    private void pullDownToRequest() {
//        mNearUserListView.getRefreshableView().removeFooterView(footerView);
//        mCurPage = 1;
//
//        reqData(mCurPage);
//        isFirstPageLoaded = false;
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!isFirstPageLoaded) {
//                    ErrorCode.toastError(mContext, ErrorCode.E_101);
//                    mNearUserListView.onRefreshComplete();
//                }
//            }
//        }, 10 * 1000);
//    }
//
//    public void reqData(int page) {
//
//        int type = StartModel.getInstance().getLoginConnnetStatus();
//        // 定时检测网络连接情况
//        if (type == StartModel.TYPE_NET_WORK_DISABLED) {
//            mHandler.postDelayed(new Runnable() {
//                public void run() {
//                    Toast.makeText(mContext, R.string.network_req_failed,
//                            Toast.LENGTH_SHORT).show();
//                    mNearUserListView.onRefreshComplete();
//                }
//            }, 200);
//
//            return;
//        }
//        mCurPage = page;
//        // 当为第1页时，使用缓存，不再获取经纬度
//        if (page == 1) {
//            if (mLocationUtil == null) {
//                mLocationUtil = new LocationUtil(mContext);
//            }
//            mLocationUtil.startListener(this, 0);
//            // 接下来请跳到updateLocation(int, int, int,String)去看
//            return;
//        }
//
//        GeoData geo = LocationUtil.getCurrentGeo(mContext);
//        int lat = 0;
//        int lng = 0;
//        if (LocationUtil.getCurrentGeo(mContext) != null && geo.getLat() != 0
//                && geo.getLng() != 0) {
//            lat = geo.getLat();
//            lng = geo.getLng();
//        }
//        updateLocation(1, lat, lng, "", "");
//    }
//
//    private void handlePullDown() {
//        if (isChangeConditions) {
//            isChangeConditions = false;
//            pullDownToRequest();
//        } else {
//            if (!ConnectorManage.getInstance(mContext).CheckNetwork(mContext))// 网络是连接
//            {
//                mHandler.postDelayed(
//                        new Runnable() {
//                            public void run() {
//                                ErrorCode.toastError(mContext, ErrorCode.E_101);
//                                mNearUserListView.onRefreshComplete();
//                            }
//                        }, 200);
//                return;
//            }
//            String recordStr = SharedPreferenceUtil.getInstance(mContext).getString(
//                    mRefreshLastTimeKey);
//            CommonFunction.log(TAG, "recordStr***" + recordStr);
//            if (!TextUtils.isEmpty(recordStr)) {
//                RefreshRecord record = GsonUtil.getInstance().getServerBean(recordStr, RefreshRecord.class);
//                if (System.currentTimeMillis() - record.lastTime < 5 * 1000 && record.isSuccess) {
//                    //gh 去掉请求提示
//                    mHandler.postDelayed(
//                            new Runnable() {
//                                public void run() {
////                                    CommonFunction.toastMsg(
////                                            mContext, getString(R.string.new_data_loaded_text));
//                                    mNearUserListView.onRefreshComplete();
//                                }
//                            }, 500);
//                } else {
//                    pullDownToRequest();
//                }
//            } else {
//                pullDownToRequest();
//            }
//        }
//    }
//
//
//    @Override
//    public void updateLocation(int type, int lat, int lng, String address,
//                               String simpleAddress) {
//        CommonFunction.log(TAG, "updateLocation() into, type="+type + ", address="+address);
//        if (type == 0) { // 失败
//            stopPulling();
//            Toast.makeText(mContext, R.string.location_service_unavalible, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (this.userGeoData == null) {
//            this.userGeoData = LocationUtil.getCurrentGeo(mContext);
//        }
//        mVipquery = Common.getInstance().loginUser.isVip() ? 1 : 0;
//
//        try {
//            GET_NEARBY_USER_LIST_FLAG = NearHttpProtocol.usersNearlist(mContext, lat, lng,
//                    filterData.gender, filterData.logintime < 0 ? 0 : filterData.logintime,
//                    filterData.minage, filterData.maxage, filterData.constellation, mCurPage, PAGE_SIZE,
//                    mVipquery, filterData.profession, filterData.love, filterData.dialects,
//                    filterData.hometown, filterData.distance, this);
//
//            //ThridAdServiceCenter.getInstance().loadAdDatas();
//        } catch (Throwable t) {
//            stopPulling();
//            CommonFunction.log(TAG,"获取附近聊表失败:" + t);
//            Toast.makeText(mContext, R.string.network_req_failed, Toast.LENGTH_SHORT).show();
//        }
//
//
//    }
//
//    public void stopPulling() {
//        if (mNearUserListView != null) {
//            mNearUserListView.onRefreshComplete();
//        }
//    }
//
//    @Override
//    public void onGeneralSuccess(String result, long flag) {
//        CommonFunction.log(TAG, "result---" + result);
//        if (Constant.isSuccess(result)) {
//            if (flag == GET_NEARBY_USER_LIST_FLAG) {
//                handleNearbyData(result,2);
//            }
//
//        } else {
//            BaseServerBean errorBean = GsonUtil.getInstance().getServerBean(result,
//                    BaseServerBean.class);
//            if (errorBean != null) {
//                onGeneralError(errorBean.error, flag);
//            }
//        }
//    }
//
//    @Override
//    public void onGeneralError(int e, long flag) {
//        if (flag == GET_NEARBY_USER_LIST_FLAG) {
//            stopPulling();
//            ErrorCode.toastError(mContext, e);
//            if (mCurPage <= 1) {
//                saveRefreshRecord(false);
//            }
//        }
//    }
//
//    private void handleNearbyData(String result,int isloadBuff) {
//        if (mNearUserListView != null) {
//            mNearUserListView.onRefreshComplete();
//        }
//        CommonFunction.log(TAG, "result***" + result);
//
//        NearbyUserListBean bean = GsonUtil.getInstance().getServerBean(result,
//                NearbyUserListBean.class);
//        if (bean != null & bean.isSuccess()) {
//            if(bean.users == null){
//                return;
//            }
//            for (int i = 0; i < bean.users.size(); i++) {
//                if (bean.users.get(i).type == 2) {
//                    bean.users.remove(i);
//                }
//            }
//            if (bean.topbanners != null) if (bean.topbanners.size() > 0) {
//                List<AdLink> bannerData = new ArrayList<>();
//
//                bannerData.clear();
//
//                AdLink ad = new AdLink();
//                ad.position = 0;
//                ad.isShowing = false;
//                ad.banner = bean.topbanners.get(0);
//                if (ad.banner != null) {
//                    ad.ADid = bean.topbanners.get(0).id;
//                    ad.banner.setThirtReport(mContext);
//                }
//                bannerData.add(ad);
//
//                mNearbyUsers.add(new NearUserBean(0,bannerData));
//            }
//
//
//            if (bean.users == null) {
//                if (mCurPage == 1) {
//                    mNearbyUsers.clear();
//
//                    CommonFunction.log(TAG, "show empty");
//
//                    footerView.setVisibility(View.VISIBLE);
//                    mNearUserListView.getRefreshableView().addFooterView(footerView);
//                    emptyTipsText.setText(getString(R.string.nearby_user_list_empty_tips));
//
//                    SharedPreferenceUtil.getInstance(mContext).putString(mCacheKeyNearby, "");
//                }
//                return;
//            }
//
//            mCurPage = bean.pageno;
//            int amount = bean.amount;
//            mTotalPage = amount / PAGE_SIZE;
//            if (amount % PAGE_SIZE > 0) {
//                mTotalPage++;
//            }
//            CommonFunction.log(TAG, "success mTotalPage***" + mTotalPage);
//
//            if (mCurPage <= 1) {
//                SharedPreferenceUtil.getInstance(mContext).putString(mCacheKeyNearby, result);
//                mNearbyUsers.clear();
//                userIdList.clear();
//                mNearUserListView.getRefreshableView().removeFooterView(footerView);
//
//                saveRefreshRecord(true);
//                isFirstPageLoaded = true;
//            }
//
//            if (bean.users != null) {
//
//                toUserBean(bean.users);
//
//                if (isloadBuff == 2 & mCurPage < 2){
//                    String advert = SharedPreferenceUtil.getInstance(getActivity()).getString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL);
//                    if (!TextUtils.isEmpty(advert)) {
//                        String[] spilt = advert.split(":");
//                        if (spilt.length >= 2 && !TextUtils.isEmpty(spilt[1])) {
//                            String[] nearByAd = spilt[1].split(",");
//                            if ("1".equals(nearByAd[0])) {
//                                addGdtAdPosition(Integer.parseInt(nearByAd[1]));
//                            }
//                            if ("1".equals(nearByAd[2])) {
//                                addGdtAdPosition(Integer.parseInt(nearByAd[3]));
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            if (mNearbyListAdapter != null) {
//                mNearbyListAdapter.updateData(mNearbyUsers);
//            }
//
//            if (mNearUserListView != null) {
//                if (mNearbyUsers.isEmpty()) {
//                    CommonFunction.toastMsg(mContext, R.string.no_data);
//                } else {
//                    mNearUserListView.getRefreshableView().removeFooterView(footerView);
//                }
//            }
//        } /*else {
//            ErrorCode.showError(getActivity(), result);
//        }*/
//
//    }
//
//    /**
//     * 转成Bean
//     * @param users
//     */
//    private void toUserBean(ArrayList<NearByExtendUser> users){
//
//        boolean adCenterHaveDatas = ThridAdServiceCenter.getInstance().isHaveAdDatas();
//
//        // 上一次数据，保存
//        if (!cacheUser.isEmpty()){
//            users.addAll(0,cacheUser);
//            cacheUser.clear();
//        }
//
//        List<NearByExtendUser> nearByExtendUsers = new ArrayList<>();
//        for (int i = 0; i < users.size(); i++) {
//            NearByExtendUser item = users.get(i);
//            // 如果是广告类型，则判断第三方广告中心是否有广告
//            if (!adCenterHaveDatas && item.type == 5) {
//                continue;
//            }
//            if (item.user != null) {
//                if (!userIdList.contains(item.user.userid)) {
//                    nearByExtendUsers.add(item);
//                } else {
//                    userIdList.add(item.user.userid);
//                }
//            } else {
//                nearByExtendUsers.add(item);
//            }
//        }
//
//
//        for (int i = 1; i <= nearByExtendUsers.size() / 3; i++) {
//            NearUserItem nearUserItem = new NearUserItem();
//            nearUserItem.leftUser = nearByExtendUsers.get(3 * i - 3);
//            nearUserItem.centerUser = nearByExtendUsers.get(3 * i - 2);
//            nearUserItem.rightUser = nearByExtendUsers.get(3 * i - 1);
//            mNearbyUsers.add(new NearUserBean(2, nearUserItem));
//
//        }
//
//        if (nearByExtendUsers.size() % 3 == 1){
//            cacheUser.add(users.get(users.size() - 1));
//        }else if (nearByExtendUsers.size() % 3 == 2){
//            cacheUser.add(users.get(users.size() - 2));
//            cacheUser.add(users.get(users.size() - 1));
//        }
//
//    }
//
//    private void addGdtAdPosition(int position) {
//        if (!ThridAdServiceCenter.getInstance().isLoadClose && position < mNearbyUsers.size()) {
//            NativeADDataRef nativeADDataRef = ThridAdServiceCenter.getInstance().getNextAdData();
//            mNearbyUsers.add(position, new NearUserBean(1,nativeADDataRef));
//        }
//    }
//
//    private void saveRefreshRecord(boolean isSuccess) {
//        RefreshRecord record = new RefreshRecord();
//        record.isSuccess = isSuccess;
//        record.lastTime = System.currentTimeMillis();
//        String recordStr = GsonUtil.getInstance().getStringFromJsonObject(record);
//        SharedPreferenceUtil.getInstance(mContext).putString(
//                mRefreshLastTimeKey, recordStr);
//    }
//
//    /**
//     * 点击两次按钮刷新数据
//     *
//     * @param isTrue
//     */
//    public void isFastPressTwice(boolean isTrue) {
//        if (isTrue) {
//            mNearUserListView.setRefreshing(true);
//        }
//    }
//
//
//    public void skipToFilter(){
//        if (isAdded()){
//            //YC 解决上下文问题
//            Intent intent = new Intent(getActivity(), FilterActivity.class);
//            intent.putExtra(FilterActivity.KEY_FILTER, 2);
//            startActivityForResult(intent, REQ_CODE_NEAR_FILTER);
//        }
//
//    }
//
//}
