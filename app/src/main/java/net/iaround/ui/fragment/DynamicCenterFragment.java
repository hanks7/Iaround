package net.iaround.ui.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import net.iaround.BuildConfig;
import net.iaround.R;
import net.iaround.annotation.IAroundAD;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.PublishManager;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.DynamicDetailActivity;
import net.iaround.ui.activity.FilterActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.adapter.DynamicAdapterNew;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.ResourceListBean;
import net.iaround.ui.datamodel.ResourceListBean.ResourceItemBean;
import net.iaround.ui.dynamic.DynamicPublishManager;
import net.iaround.ui.dynamic.PublishDynamicActivity;
import net.iaround.ui.dynamic.bean.DynamicCenterListBean;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.bean.DynamicPublishBean;
import net.iaround.ui.dynamic.bean.DynamicUnLikeOrReviewBean;
import net.iaround.ui.dynamic.view.DynamicItemViewNew;
import net.iaround.ui.view.dynamic.DynamicItemView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2015-3-28 下午8:01:34
 * @Description: 动态中心页面
 * @注意 在动态中心, DynamicItemBean实体中, 不使用ShareInfo和SyncInfo的数据[点赞数量, 是否点赞和评论数量],
 * 所以要在动态详情的操作的数据要同步出来
 */
public class DynamicCenterFragment extends LazyLoadBaseFragment implements
        OnClickListener, HttpCallBack, MainFragmentActivity.PagerSelectDynamic {

    public static DynamicCenterFragment instant;
    private final String TAG = this.getClass().getName();

    public static boolean sPublishFrequent = false;// 用于判断是否发送过于频繁
    public static boolean sBannedPublish = false;// 用于判断是否被系统禁止发布动态

    private View mBaseView;
    private Context mContext;

    private ImageView ivPublish;// 发布按钮
    private RelativeLayout rlFailTipsBar;// 发送失败的提示框
    private PullToRefreshListView ptrlvContent;
    private PullToRefreshScrollView mEmptyView;
    private TextView mTvEmptyText;

    private DynamicCenterHandler mHandler;
    private DynamicAdapterNew mAdapter;

    private ArrayList<DynamicCenterListItemBean> dynamicCenterList;
    private ArrayList<DynamicItemBean> dynamicList;// 列表数据
    private ArrayList<ResourceItemBean> resourcesList;// 资源列表

    private long GREET_DYNAMIC_FLAG;// 点赞动态Flag
    private long UNGREET_DYNAMIC_FLAG;// 取消点赞动态Flag
    private long DYNAMIC_LIST_GET;// 获取动态list
    private long GET_RESOUCE_FLAG;

    // startActivityForResult请求码
    private final static int DYNAMIC_DETAIL_CODE = 1000;// 动态详情
    private final static int PUBLISH_REQUEST_CODE = 1002;// 发布动态requestCode
    public static int REQ_CODE_NEAR_DYNAMIC_FILTER = 208;

    private GeoData geoData;

    // Key为flag， Value为dynamicID
    private HashMap<Long, Long> requestMap = new HashMap<Long, Long>();

    private int SIZE_PER_PAGE = 25;// 下拉刷新请求的数量
    private int SIZE_PER_PAGE_FIRST = 6;// 首次刷新请求的数量
    private boolean isHasNext = true;// 请否有下一页的数据
    private boolean isFirstRequestBack = true;// 是否第一页的请求
    private long mLastDynamicId = 0;// 最后一条动态的id
    private int mResourceTime = 0;// 资源协议请求的次数,因为可能还没登陆成功,导致资源请求失败.请求3次不成功就不请求资源

    // 记录经纬度是为了在下次进来的时候判断是否大于限定的距离,决定是否刷新列表
    private int DISTANCE_OFFSET = 500;

    private int lng = 0;// 记录经度
    private int lat = 0;// 记录纬度


    /**
     * 滑动提示相关
     */
    private boolean mIsNoticeShowed = false;// 是否已经显示过提示, 安装后只提示一次

    private DynamicItemBean mCurrentItemBean;
    private int mFirstVisibleItem, mVisibleItemCount;

    private int page = 1;

    private final int REQUEST_DATA = 1;// 刷新列表数据
    public class DynamicCenterHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_DATA:
                    reqListData(mLastDynamicId, SIZE_PER_PAGE_FIRST);
                    break;
            }

            super.handleMessage(msg);
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_dynamic_center;
    }

    @Override
    protected boolean lazyLoad() {
        initData();
        initAnimation();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        instant = this;
        mIsNoticeShowed = SharedPreferenceUtil.getInstance(mContext).getBoolean(SharedPreferenceUtil.DYNAMIC_TOP_GUIDE_KEY, false);

        initView(getContentView());


        CommonFunction.log(TAG, "onCreateView DynamicCenterFragment");
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        instant = this;
//
//        mBaseView = View.inflate(mContext, R.layout.activity_dynamic_center, null);
//
//        mIsNoticeShowed = SharedPreferenceUtil.getInstance(mContext).getBoolean(SharedPreferenceUtil.DYNAMIC_TOP_GUIDE_KEY, false);
//
//        initView();
//        initData();
//        initAnimation();
//
//        CommonFunction.log(TAG, "onCreateView DynamicCenterFragment");
//
//        return mBaseView;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getActivity();
        geoData = LocationUtil.getCurrentGeo(mContext);
        lng = geoData.getLng();
        lat = geoData.getLat();
        mHandler = new DynamicCenterHandler();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    /**
     * 初始发布按钮的动画效果
     */
    private void initAnimation() {
        final AnimationSet mAnimationShow = new AnimationSet(true);

        TranslateAnimation mTransAniShow = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 3f, Animation.RELATIVE_TO_SELF, 0f);
        mTransAniShow.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                ivPublish.setVisibility(View.VISIBLE);
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
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 3f);
        mTransAniHide.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivPublish.setVisibility(View.GONE);
            }
        });
        mTransAniHide.setDuration(600);
        mAnimationHide.addAnimation(mTransAniHide);

        //ScrollListener的调用顺序onScrollStateChanged -> n* onScroll -> onScrollStateChanged
        //需要实现的是向上滑(查看上面的数据)的时候,不要让发布按钮消失,向下滑(查看下面的数据)的时候,要让发布按钮消失
        //因为要通过onScroll中来判断是向上滑or向下滑,所以就不能在onScrollStateChanged中去执行发布按钮消失的动画
        ptrlvContent.setOnScrollListener(new AbsListView.OnScrollListener() {

            private boolean scrollFlag = false;// 标记是否滑动
            private int lastVisibleItemPosition;// 标记上次滑动位置
            private boolean isSrollOverHalfPage = false;// 是否超过半页
            private boolean isStateChanged = false;//listview的状态是否发生改变,只判断idle和其他状态之间的切换
            private boolean isIdle = true;//是否处于idle状态

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (SCROLL_STATE_IDLE == scrollState) {
                    if (!isIdle && !isStateChanged) {
                        ivPublish.startAnimation(mAnimationShow);
                    }
                    scrollFlag = false;
                    isIdle = true;
                    isStateChanged = true;

                } else {
                    //isStateChanged包括SCROLL_STATE_TOUCH_SCROLL和SCROLL_STATE_FLING两种状态
                    if (isIdle) {
                        isStateChanged = true;
                    }
                    isIdle = false;
                    scrollFlag = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                mFirstVisibleItem = firstVisibleItem;
                mVisibleItemCount = visibleItemCount;
                mAdapter.uploadAaData(mContext, firstVisibleItem, visibleItemCount);

                //执行让发布按钮消失的动画,在向下滑的动作中
                if (isStateChanged && firstVisibleItem > lastVisibleItemPosition) {
                    if (ivPublish.getVisibility() == View.VISIBLE) {
                        ivPublish.startAnimation(mAnimationHide);
                    }
                    isStateChanged = false;
                }
                //处理第一次显示那个引导
                if (scrollFlag) {
                    if (!mIsNoticeShowed) {
                        if (firstVisibleItem > lastVisibleItemPosition) {
                            if (firstVisibleItem >= SIZE_PER_PAGE / 2) {
                                isSrollOverHalfPage = true;
                            }
                        } else if (firstVisibleItem < lastVisibleItemPosition) {

                            if (isSrollOverHalfPage) {
                                //showNoticeView();
                                mIsNoticeShowed = true;
                                SharedPreferenceUtil.getInstance(mContext)
                                        .putBoolean(SharedPreferenceUtil.DYNAMIC_TOP_GUIDE_KEY,
                                                mIsNoticeShowed);
                            }
                        }
                    }
                    lastVisibleItemPosition = firstVisibleItem;
                }
            }
        });
    }

    private void initResource() {
        page = 1;
        GET_RESOUCE_FLAG = BusinessHttpProtocol.getResourceList(mContext, Config.PLAT, 2, this);
    }

    private void initData() {

        refreshData();
        refreshLikeStatus();
        mAdapter = new DynamicAdapterNew(mContext, dynamicCenterList, DynamicItemView.DYNAMIC_CENTER, DynamicItemClickListener);
        mAdapter.setFuctionBarClickListener(btnClickListener);
        ptrlvContent.setAdapter(mAdapter);
        mHandler.sendEmptyMessageDelayed(REQUEST_DATA, 2000);

    }

    private void refreshData() {
        if (dynamicCenterList == null) {
            dynamicCenterList = new ArrayList<>();
        } else {
            dynamicCenterList.clear();
        }

        ArrayList<DynamicItemBean> unSendList = DynamicModel.getInstent().getUnSendSuccessList();
        for (int i = 0; i < unSendList.size(); i++) {
            DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
            DynamicItemBean dynamicItem = unSendList.get(i);
            itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;
            itemBean.itemBean = dynamicItem;
            dynamicCenterList.add(itemBean);
        }


        dynamicList = DynamicModel.getInstent().getDynamicCenterList();
        for (int i = 0; i < dynamicList.size(); i++) {
            DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
            DynamicItemBean dynamicItem = dynamicList.get(i);
            itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;//dynamicItem.getDynamicInfo().type
            itemBean.itemBean = dynamicItem;
            dynamicCenterList.add(itemBean);
        }
        refreshResourceData();
    }

    private void refreshResourceData() {
        resourcesList = DynamicModel.getInstent().getResourcesList();
        for (int i = 0; i < resourcesList.size(); i++) {
            ResourceItemBean resourceItem = resourcesList.get(i);

            DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
            itemBean.itemType = DynamicCenterListItemBean.RESOURCE_BANNER;
            itemBean.itemBean = resourceItem;

            int position = resourceItem.pageposition - 1;
            if (position >= 0) {
                if (position >= dynamicCenterList.size()) {
                    dynamicCenterList.add(itemBean);
                } else {
                    dynamicCenterList.add(position, itemBean);
                }
            }
        }

        if (page < 2) {
            String advert = SharedPreferenceUtil.getInstance(getActivity()).getString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL);
            if (!TextUtils.isEmpty(advert) && CommonFunction.updateAdCount(TAG)) {
                String[] spilt = advert.split(":");
                if (spilt.length >= 3 && !TextUtils.isEmpty(spilt[2])) {
                    String[] nearByAd = spilt[2].split(",");
                    if ("1".equals(nearByAd[0])) {
                        addGdtAdPosition(Integer.parseInt(nearByAd[1]));
                    } else {
                        Common.inmobiLoad = 1;
                        addInmobiAdPosition(Integer.parseInt(nearByAd[1]));
                    }

                    if ("1".equals(nearByAd[2])) {
                        addGdtAdPosition(Integer.parseInt(nearByAd[3]));
                    } else {
                        Common.inmobiLoad = 2;
                        addInmobiAdPosition(Integer.parseInt(nearByAd[3]));
                    }
                }
            }
        }
    }

    @IAroundAD
    private void addGdtAdPosition(int position) {
        if(BuildConfig.showAdvert==true) {
            boolean adCenterHaveDatas = net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().isLoadClose;
            if ((!adCenterHaveDatas) && position < dynamicCenterList.size()) {
                DynamicCenterListItemBean bean = new DynamicCenterListItemBean();
                bean.itemType = 4;
                dynamicCenterList.add(position, bean);
            }
        }
    }

    @IAroundAD
    private void addInmobiAdPosition(final int position) {
        if(BuildConfig.showAdvert==true) {
            if (position < dynamicCenterList.size()) {
                DynamicCenterListItemBean bean = new DynamicCenterListItemBean();
                bean.itemType = 5;
                dynamicCenterList.add(position, bean);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initView(getContentView());
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (geoData == null)return;
            // 相当于Fragment的onResume
            int currentLng = geoData.getLng();
            int currentLat = geoData.getLat();

            int distance = LocationUtil.calculateDistance(lng, lat, currentLng,
                    currentLat);
            if (distance > DISTANCE_OFFSET) {
                pullToRefresh();
            }
            lng = currentLng;
            lat = currentLat;

            mContext = getActivity();

        } else {
            // 相当于Fragment的onPause
        }
    }

    private void reqListData(long lastContentTime, int pageSize) {
        DYNAMIC_LIST_GET = DynamicHttpProtocol.getRecommendDynamicList(mContext, lat, lng, pageSize, lastContentTime, "0", 1, getGenderStr(), this);
    }

    private String getGenderStr() {
        int sex = SharedPreferenceUtil.getInstance(getActivity()).getInt(SharedPreferenceUtil.DYNAMIC_FILTER_GENDER + Common.getInstance().getUid());
        // 性别
        String mGenderType = "all";
        if (sex == 1) {
            mGenderType = "m";
        } else if (sex == 2) {
            mGenderType = "f";
        }
        return mGenderType;
    }

    // 初始化布局
    private void initView(View view) {
        ivPublish = (ImageView) view.findViewById(R.id.ivPublish);
        ivPublish.setOnClickListener(this);

        rlFailTipsBar = (RelativeLayout) view.findViewById(R.id.rlFailTipsBar);
        rlFailTipsBar.setOnClickListener(sendFailLayoutClickListener);
        rlFailTipsBar.bringToFront();

        ptrlvContent = (PullToRefreshListView) view.findViewById(R.id.ptrlvContent);

        ptrlvContent.setMode(Mode.BOTH);
        ptrlvContent.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        ptrlvContent.getRefreshableView().setFastScrollEnabled(false);
        ptrlvContent.setOnRefreshListener(mOnRefreshListener);

        mEmptyView = (PullToRefreshScrollView) view.findViewById(R.id.empty);

        mEmptyView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mTvEmptyText = (TextView) view.findViewById(R.id.tv_empty_text);
        mEmptyView.setMode(Mode.PULL_FROM_START);

        setListener();
    }

    // 刷新的监听器
    private OnRefreshListener2<ListView> mOnRefreshListener = new OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            mLastDynamicId = 0;
            reqListData(mLastDynamicId, SIZE_PER_PAGE);
            isFirstRequestBack = true;

            initResource();

            ArrayList<DynamicItemBean> unsendList = DynamicModel.getInstent().getUnSendSuccessList();
            if (unsendList.size() <= 0){
                rlFailTipsBar.setVisibility(View.GONE);
            }else{
                for (DynamicItemBean item : unsendList) {
                    if (item.isSendFail()) {
                        rlFailTipsBar.setVisibility(View.VISIBLE);
                        DynamicCenterFragment.sPublishFrequent = false;
                        break;
                    }
                }
            }

        }

        @Override
        public void onPullUpToRefresh(
                final PullToRefreshBase<ListView> refreshView) {
            if (isHasNext) {
                reqListData(mLastDynamicId, SIZE_PER_PAGE);
            } else {

                Toast.makeText(mContext, R.string.no_more_data_text, Toast.LENGTH_SHORT).show();
                refreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshView.onRefreshComplete();
                        }
                }, 200);
            }
        }
    };

    /**
     * 空布局下拉刷新数据
     */
    private void setListener() {
        mEmptyView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mLastDynamicId = 0;
                reqListData(mLastDynamicId, SIZE_PER_PAGE);
                isFirstRequestBack = true;

                initResource();

                ArrayList<DynamicItemBean> unsendList = DynamicModel.getInstent().getUnSendSuccessList();
                if (unsendList.size() <= 0){
                    rlFailTipsBar.setVisibility(View.GONE);
                }else{
                    for (DynamicItemBean item : unsendList) {
                        if (item.isSendFail()) {
                            rlFailTipsBar.setVisibility(View.VISIBLE);
                            DynamicCenterFragment.sPublishFrequent = false;
                            break;
                        }
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(ivPublish)) {
            if (DynamicModel.getInstent().getUnSendSuccessList().size() > 0) {
                Toast.makeText(mContext, R.string.dynamic_unsend_notice, Toast.LENGTH_SHORT).show();
            } else {
                PublishDynamicActivity.skipToPublishDynamicActivity(mContext, this, PUBLISH_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == DYNAMIC_LIST_GET) {
            ptrlvContent.onRefreshComplete();
            mEmptyView.onRefreshComplete();
            if (result == null) {
                return;
            }
            handleGetDynamicListBack(result, flag);
            hideEmptyView();
            refreshData();
            refreshLikeStatus();

            if (!isHasNext) {
                DynamicCenterListItemBean totalItem = new DynamicCenterListItemBean();
                totalItem.itemType = DynamicCenterListItemBean.TOTOAL_NOTICE;
                totalItem.itemBean = dynamicList.size();
                dynamicCenterList.add(totalItem);
                ptrlvContent.setMode(Mode.PULL_FROM_START);
            } else {
                ptrlvContent.setMode(Mode.BOTH);
            }
            mAdapter.setChange(true);
            mAdapter.initAdBannerData();
            mAdapter.notifyDataSetChanged();
        } else if (GREET_DYNAMIC_FLAG == flag || UNGREET_DYNAMIC_FLAG == flag) {
            handleDynamicGreetBack(flag, result);
        } else if (GET_RESOUCE_FLAG == flag) {
            ResourceListBean bean = GsonUtil.getInstance().getServerBean(result, ResourceListBean.class);
            if (bean != null) {
                if (bean.status == 200 && bean.resources != null) {
                    if (resourcesList == null) {
                        resourcesList = DynamicModel.getInstent().getResourcesList();
                    }
                    resourcesList.clear();
                    int count = bean.resources.size();
                    for (int i = 0; i < count; i++) {
                        ResourceItemBean itemBean = bean.resources.get(i);
                        // 如果是第三方广告，则不需要判断banner属性是否为空，只需要判断广告中心是否有广告
                        if (itemBean.type == ResourceListBean.RESOURCE_TYPE_THRID_AD) {
                           addADData(bean,i);
                        } else if (itemBean.banner != null) {
                            resourcesList.add(bean.resources.get(i));
                        }
                    }
                    if (resourcesList.size() > 0) {
                        refreshData();
                        mAdapter.setChange(true);
                        mAdapter.initAdBannerData();
                        mAdapter.notifyDataSetChanged();
                    }
                } else if (bean.error == ErrorCode.E_4100 && mResourceTime < 3) {
                    initResource();
                    mResourceTime++;
                    page = 0;
                }
            }
        }
    }

    @IAroundAD
    private void addADData(ResourceListBean bean, int i){
        if(BuildConfig.showAdvert==true) {
            if (net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().isHaveAdDatas()) {
                resourcesList.add(bean.resources.get(i));
            }
        }
    }

    // 处理获取我的动态列表返回
    private void handleGetDynamicListBack(String result, long flag) {
        DynamicCenterListBean bean = GsonUtil.getInstance().getServerBean(
                result, DynamicCenterListBean.class);
        if (bean != null && bean.isSuccess()) {
            isHasNext = bean.isHasNext();
            mLastDynamicId = bean.last;

            ArrayList<DynamicItemBean> newDynamicList = new ArrayList<>();
            ArrayList<DynamicItemBean> newDynamicListFileter = new ArrayList<>();
            if (bean.dynamics == null) {
                newDynamicList = new ArrayList<>();
            } else {
                newDynamicList = bean.dynamics;
                for (int i = 0; i < newDynamicList.size(); i++) {
                    DynamicItemBean dynamicItemBean = newDynamicList.get(i);
                    if (dynamicItemBean.getDynamicInfo().getSync() <= 0 || dynamicItemBean.getDynamicInfo().synctype <= 0) {
                        newDynamicListFileter.add(dynamicItemBean);
                    }

                }
            }

            if (isFirstRequestBack) {
                isFirstRequestBack = false;

                dynamicList.clear();
                if (newDynamicListFileter != null) {
                    dynamicList.addAll(newDynamicListFileter);
                }

                DynamicModel.getInstent().saveDynamicCenterListToFile();
                ArrayList<DynamicItemBean> tempList = DynamicModel.getInstent().getUnreviewedItem(dynamicList);
                if (tempList.size() > 0) {
                    //插入刚发送的动态
                    dynamicList.addAll(0, tempList);
                }

            } else {
                dynamicList.addAll(newDynamicList);
            }

            if (dynamicList.isEmpty()) {
                mTvEmptyText.setText(R.string.content_is_empty);
            }
        } else {
            ErrorCode.showError(getActivity(), result);
            mTvEmptyText.setText(R.string.content_is_error);
        }
    }

    // 处理赞或者取消赞操作返回
    private void handleDynamicGreetBack(long flag, String result) {
        BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                BaseServerBean.class);
        if (bean != null && bean.isSuccess()) {

            requestMap.remove(flag);
        } else {
            if (bean != null) {
                if (bean.error == 9301 || bean.error == 9414) {
                    requestMap.remove(flag);
                }
            } else {
                addGreetUnSendSuccessMap(flag);
            }
        }
    }

    //点赞成功
    private void DynamicGreet(long dynamicID) {
        for (int i = 0; i < dynamicList.size(); i++) {
            DynamicItemBean info = dynamicList.get(i);
            if (info.getDynamicInfo().dynamicid == dynamicID) {
                info.curruserlove = 1;
                info.likecount++;
                info.isCurrentHanleView = true;
                info.setDynamicLoveInfo(null);
                break;
            }
        }
        mAdapter.setChange(false);
        mAdapter.initAdBannerData();
        mAdapter.notifyDataSetChanged();
    }

    //取消点赞成功
    private void DynamicCancelGreet(long dynamicID) {
        for (int i = 0; i < dynamicList.size(); i++) {
            DynamicItemBean info = dynamicList.get(i);
            if (info.getDynamicInfo().dynamicid == dynamicID) {
                info.curruserlove = 0;
                info.likecount--;
                info.isCurrentHanleView = true;
                info.setDynamicLoveInfo(null);
                break;
            }
        }
        mAdapter.setChange(false);
        mAdapter.initAdBannerData();
        mAdapter.notifyDataSetChanged();
    }

    //刷新保存的未发送成功的赞评对应的动态
    private void refreshLikeStatus() {
        Map<String, Object> map = DynamicModel.getInstent().getLikeUnSendSuccessMap();
        if (map.size() > 0) {
            Iterator<String> keys = map.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                DynamicUnLikeOrReviewBean value = (DynamicUnLikeOrReviewBean) map.get(key);

                for (DynamicItemBean list : dynamicList) {
                    String dynamicId = String.valueOf(list.getDynamicInfo().dynamicid);
                    if (key.equals(dynamicId)) {
                        if (list.curruserlove != value.type) {
                            if (value.type == 1) {
                                list.curruserlove = 1;
                                list.likecount++;
                            } else if (value.type == 2) {
                                list.curruserlove = 0;
                                list.likecount--;
                                if (list.likecount < 0) {
                                    list.likecount = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {

        ErrorCode.toastError(mContext, e);

        ptrlvContent.onRefreshComplete();
        mEmptyView.onRefreshComplete();
        if (flag == DYNAMIC_LIST_GET) {

            mTvEmptyText.setText(R.string.content_is_error);
        } else if (GREET_DYNAMIC_FLAG == flag || UNGREET_DYNAMIC_FLAG == flag) {
            addGreetUnSendSuccessMap(flag);
        }
    }

    // 点赞或取消点赞操作如果失败，将该条动态的信息添加到待重新发送的map
    private void addGreetUnSendSuccessMap(long flag) {
        if (mCurrentItemBean != null) {
            DynamicUnLikeOrReviewBean value = new DynamicUnLikeOrReviewBean();
            if (flag != GREET_DYNAMIC_FLAG && flag != UNGREET_DYNAMIC_FLAG) {
                value.type = flag == 0 ? 1 : 2;
            } else {
                value.type = flag == GREET_DYNAMIC_FLAG ? 1 : 2;
            }
            value.dynamicid = mCurrentItemBean.getDynamicInfo().dynamicid;
            value.postbarid = mCurrentItemBean.getDynamicInfo().dynamiccategory
                    == PublishDynamicActivity.DYNAMIC ? -1
                    : mCurrentItemBean.getDynamicInfo().parentid;
            DynamicModel.getInstent().addLikeUnSendSuccessMap(
                    String.valueOf(mCurrentItemBean.getDynamicInfo().dynamicid), value);
        }
    }

    // 发送失败的Bar是否显示
    private void handleSendFailBar() {
        boolean isShowFailBar = false;

        ArrayList<DynamicItemBean> unsendSuccessList = DynamicModel.getInstent().getUnSendSuccessList();

        for (int i = 0; i < unsendSuccessList.size(); i++) {
            if (unsendSuccessList.get(i).isSendFail()) {
                isShowFailBar = true;
                break;
            }
        }

        if (isShowFailBar && !DynamicCenterFragment.sPublishFrequent) {
            rlFailTipsBar.setVisibility(View.VISIBLE);
        } else {
            rlFailTipsBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void pullToRefresh() {
        if (ptrlvContent != null) {
            ptrlvContent.setRefreshing(true, Mode.PULL_FROM_START);
        }
    }

    /****************
     * 点击事件的监听器
     *****************/

    private OnClickListener DynamicItemClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            DynamicItemBean bean = (DynamicItemBean) arg0.getTag();

            if (bean.getDynamicInfo().dynamiccategory == PublishDynamicActivity.DYNAMIC) {

                if (bean.getDynamicInfo().getPhotoList().size() > 0){
                    if (bean.getDynamicInfo().getPhotoList().get(0).contains("storage/")){
                        return;
                    }
                }

                DynamicDetailActivity.skipToDynamicDetail(mContext,DynamicCenterFragment.this,bean,DYNAMIC_DETAIL_CODE,false);
            } else {

            }
        }
    };

    private OnClickListener sendFailLayoutClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            DynamicPublishManager manager = (DynamicPublishManager) DynamicPublishManager.create(mContext, PublishDynamicActivity.DYNAMIC);

            ArrayList<DynamicItemBean> unsendSuccessList = DynamicModel.getInstent().getUnSendSuccessList();
            for (int i = 0; i < unsendSuccessList.size(); i++) {
                DynamicItemBean bean = unsendSuccessList.get(i);
                if (!bean.isSendFail()) {
                    continue;
                }
                DynamicPublishBean publishBean = bean.getPublishInfo();

                // 未发送成功前,dateTime == dynamicId
                long sendFailItemDynamicId = bean.getDynamicInfo().dynamicid;

                int taskID = (int) (sendFailItemDynamicId & PublishManager.NOTIFICATION_TASK_ID_MASK);

                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(taskID);

                // 取消
                if (publishBean != null) {

                    long currentTime = TimeFormat.getCurrentTimeMillis();

                    if (bean.getDynamicInfo().dynamicid == sendFailItemDynamicId) {
                        bean.getDynamicInfo().dynamicid = currentTime;
                        bean.getDynamicInfo().datetime = currentTime;
                        bean.getPublishInfo().dynamicid = currentTime;
                        bean.getPublishInfo().datetime = currentTime;
                        bean.setSendStatus(DynamicItemBean.SENDDING);
                    }

                    manager.addTask(publishBean);
                }
            }
            handleSendFailBar();

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUBLISH_REQUEST_CODE) {
            refreshData();
            mAdapter.setChange(false);
            mAdapter.initAdBannerData();
            mAdapter.notifyDataSetChanged();
        } else if (requestCode == DYNAMIC_DETAIL_CODE) {

            if (resultCode == Activity.RESULT_OK) {//更新动态
                String dynamicinfo = data.getStringExtra("dynamicInfo");
                DynamicItemBean bean = GsonUtil.getInstance().getServerBean(
                        dynamicinfo, DynamicItemBean.class);
                if (bean != null) {
                    int count = dynamicList.size();
                    for (int i = 0; i < count; i++) {
                        DynamicItemBean itemBean = dynamicList.get(i);
                        if (bean.getDynamicInfo().dynamicid == itemBean
                                .getDynamicInfo().dynamicid) {
                            itemBean.curruserlove = bean.curruserlove;
                            itemBean.likecount = bean.likecount;
                            itemBean.reviewcount = bean.reviewcount;

                            itemBean.setDynamicLoveInfo(null);
                            itemBean.setDynamicReviewInfo(null);
                            break;
                        }
                    }
                }
                mAdapter.setChange(false);
                mAdapter.initAdBannerData();
                mAdapter.notifyDataSetChanged();
            } else if (resultCode == DynamicDetailActivity.RESULT_DELETE) {//删除动态
                long dynamicId = data.getLongExtra(
                        DynamicDetailActivity.mDynamicIdKey, 0l);
                if (dynamicId > 0) {
                    int count = dynamicList.size();
                    for (int i = 0; i < count; i++) {
                        DynamicItemBean itemBean = dynamicList.get(i);
                        if (dynamicId == itemBean.getDynamicInfo().dynamicid) {
                            dynamicList.remove(i);
                            refreshData();
                            mAdapter.setChange(false);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        } else if (requestCode == REQ_CODE_NEAR_DYNAMIC_FILTER & resultCode == Activity.RESULT_OK) {
            ptrlvContent.setRefreshing(true);
            mLastDynamicId = 0;
            reqListData(mLastDynamicId, SIZE_PER_PAGE);
        }
    }

    /**
     * 条目事件
     */
    private DynamicItemViewNew.ItemDynamicClick btnClickListener = new DynamicItemViewNew.ItemDynamicClick() {

        @Override
        public void ItemGreetDynamic(DynamicItemBean bean) {
            if (CommonFunction.forbidSay(mContext)) {
                return;
            }
            if (bean != null) {
                mCurrentItemBean = bean;
                byte loveType = bean.curruserlove;
                if (loveType == 0) // 如果当前没有点赞，则点赞
                {// 网络未连接时不发送请求，直接操作，将该条put到GreetUnSendSuccessMap
                    if (!ConnectorManage.getInstance(mContext).CheckNetwork(mContext)) {
                        addGreetUnSendSuccessMap(0);
                    } else {
                        if (bean.getDynamicInfo().dynamiccategory == PublishDynamicActivity.DYNAMIC) {
                            GREET_DYNAMIC_FLAG = DynamicHttpProtocol
                                    .greetUserDynamic(mContext,
                                            bean.getDynamicInfo().dynamicid,
                                            DynamicCenterFragment.this);
                        } else {
                        }
                        requestMap.put(GREET_DYNAMIC_FLAG,
                                bean.getDynamicInfo().dynamicid);
                    }

                    DynamicGreet(bean.getDynamicInfo().dynamicid);

                } else {

                    if (!ConnectorManage.getInstance(mContext).CheckNetwork(mContext)) {
                        addGreetUnSendSuccessMap(1);
                    } else {
                        if (bean.getDynamicInfo().dynamiccategory == PublishDynamicActivity.DYNAMIC) {
                            UNGREET_DYNAMIC_FLAG = DynamicHttpProtocol
                                    .greetCancelUserDynamic(mContext,
                                            bean.getDynamicInfo().dynamicid,
                                            DynamicCenterFragment.this);
                        } else {
                        }
                        requestMap.put(UNGREET_DYNAMIC_FLAG, bean.getDynamicInfo().dynamicid);
                    }

                    DynamicCancelGreet(bean.getDynamicInfo().dynamicid);
                }
            }
        }

        @Override
        public void ItemCommentDynamic(DynamicItemBean dynamic) {
            if (dynamic.getDynamicInfo().getPhotoList().size() > 0){
                if (dynamic.getDynamicInfo().getPhotoList().get(0).contains("/storage/")){
                    return;
                }
            }

            DynamicDetailActivity.skipToDynamicDetail(mContext,DynamicCenterFragment.this,dynamic,DYNAMIC_DETAIL_CODE,true);
        }

    };


    @Override
    public void onDynamicSelected(int mode) {
        if (MainFragmentActivity.PAGE_CLICK == mode) {
            if (ptrlvContent != null) {
                ptrlvContent.getRefreshableView().smoothScrollToPosition(0);
            }
        }  else if (MainFragmentActivity.PAGE_SELECT == mode) {
            if (mAdapter != null) {
                mAdapter.setBannerAdData();
                CommonFunction.log("DataStatistics", "动态 切换");
                mAdapter.uploadAaData(mContext, mFirstVisibleItem, mVisibleItemCount);
            }
        }

    }

    private void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
        ptrlvContent.setVisibility(View.VISIBLE);
    }

    public void skipToFilter(){
        if (isAdded()){
            Intent intent = new Intent(getActivity(), FilterActivity.class);
            intent.putExtra(FilterActivity.KEY_FILTER, 1);
            startActivityForResult(intent, REQ_CODE_NEAR_DYNAMIC_FILTER);
        }

    }


    @Subscribe
    public void onEvent(String event){
        if (event.equals("upload_error")){
            handleSendFailBar( );
        }
    }
}
