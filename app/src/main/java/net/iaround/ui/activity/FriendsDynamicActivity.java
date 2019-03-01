package net.iaround.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.contract.fragment.DynamicContract;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.ui.adapter.DynamicAdapterNew;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.ResourceListBean;
import net.iaround.ui.dynamic.PublishDynamicActivity;
import net.iaround.ui.dynamic.bean.DynamicCenterListBean;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.bean.DynamicUnLikeOrReviewBean;
import net.iaround.ui.dynamic.view.DynamicItemViewNew;
import net.iaround.ui.view.dialog.NotLoginDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class: 好友动态
 * Author：gh
 * Date: 2017/1/5 12:08
 * Email：jt_gaohang@163.com
 * //
 */
public class FriendsDynamicActivity extends ActionBarActivity implements DynamicContract.View, HttpCallBack {

    public static final String DYNAMIC_CATEGORY = "dynamic_category";

    private final static int DYNAMIC_DETAIL_CODE = 1000;// 动态详情
    private final static int PUBLISH_REQUEST_CODE = 1002;// 发布动态requestCode

    private PullToRefreshListView ptrlvContent;

    //    private DynamicAdapter mAdapter;
    private DynamicAdapterNew mAdapter;
//    private DynamicPrester prester;

    private long dynamicId;
    private int categoryDyn;
    private long currentTime;

    //标题
    private ImageView mIvLeft, mIvRight;
    private TextView mTvTitle;
    private FrameLayout flLeft;

    //发布动态按钮
    private ImageView ivPublish;

    private LinearLayout mEmptyView;
    //    private PullToRefreshScrollView mEmptyView;
//    private TextView mTvEmptyText;
    private EmptyLayout emptyLayout;
    private long GREET_DYNAMIC_FLAG;// 点赞动态Flag
    private long UNGREET_DYNAMIC_FLAG;// 取消点赞动态Flag
    private long DYNAMIC_LIST_GET;// 获取动态list
    private long DYNAMIC_LIST_GET1;// 重置获取动态list
    private int lng = 0;// 记录经度
    private int lat = 0;// 记录纬度
    private int SIZE_PER_PAGE = 25;// 下拉刷新请求的数量
    private int SIZE_PER_PAGE_FIRST = 6;// 首次刷新请求的数量
    private boolean isHasNext = true;// 请否有下一页的数据
    private boolean isFirstRequestBack = true;// 是否第一页的请求
    private long mLastDynamicId = 0;// 最后一条动态的id
    private GeoData geoData;
    private final int REQUEST_DATA = 1;// 刷新列表数据
    private final int REFRESH_BADAGE = 2;// 刷新Badage
    private DynamicCenterHandler mHandler;
    private ArrayList<DynamicItemBean> unSendList;
    private ArrayList<DynamicCenterListItemBean> dynamicCenterList;
    private ArrayList<DynamicItemBean> dynamicList;// 列表数据
    private ArrayList<ResourceListBean.ResourceItemBean> resourcesList;// 资源列表
    // Key为flag， Value为dynamicID
    private HashMap<Long, Long> requestMap = new HashMap<Long, Long>();
    private DynamicItemBean mCurrentItemBean;
    private static String DYNAMIC_FRIEND = "2";
    private static String DYNAMIC_GENDER = "all";
    private int currentPageNo = 1;

    private Dialog mWaitDialog;

    /**
     * 跳转到动态界面
     *
     * @param mContext
     * @dynamicCategory 2-代表个人动态；3-代表好友动态
     */

    public static void skipToOtherDynamicsActivity(Context mContext, Fragment fragment, int dynamicCategory,
                                                   int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, FriendsDynamicActivity.class);
        intent.putExtra(DYNAMIC_CATEGORY, dynamicCategory);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到动态界面
     *
     * @param mContext
     * @dynamicCategory 2-代表个人动态；3-代表好友动态
     */
    public static void skipToOtherDynamicsActivity(Context mContext, Activity activity, int dynamicCategory,
                                                   int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, FriendsDynamicActivity.class);
        intent.putExtra(DYNAMIC_CATEGORY, dynamicCategory);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_dynamic);

//        rightTitleActionBarEvent(R.drawable.near_dynamic_publish,null);
        initView();
        initActionBar();
        initListener();
        initData();
//        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                }, categoryDyn == 2 ? getResources().getString(R.string.personal_dynamics_title) : getResources().getString(R.string.friend_dynamic_title)
//                , false, R.drawable.near_dynamic_publish, null, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (NotLoginDialog.getInstance(getActivity()).isLogin()){
//                            Intent intent = new Intent(getActivity(), PublishDynamicActivity.class);
//                            startActivityForResult(intent,206);
//                        }
//                    }
//                });

//        if (prester == null)
//            prester = new DynamicPrester();
        //YC dynamicCenterList添加初始化
        dynamicCenterList = new ArrayList<>();
        mAdapter = new DynamicAdapterNew(mContext, dynamicCenterList,
                DynamicItemViewNew.DYNAMIC_CENTER, DynamicItemClickListener);
        ptrlvContent.setAdapter(mAdapter);
        mAdapter.setFuctionBarClickListener(btnClickListener);
    }

    @Override
    public void initView() {
        ptrlvContent = (PullToRefreshListView) findViewById(R.id.pull_near_dynamic);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_friend);
//        mEmptyView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        mTvEmptyText = (TextView) findViewById(R.id.tv_empty);
        ivPublish = (ImageView) findViewById(R.id.ivPublish);
        ptrlvContent.setMode(PullToRefreshBase.Mode.BOTH);
        ptrlvContent.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        ptrlvContent.getRefreshableView().setFastScrollEnabled(false);
        ptrlvContent.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                dynamicCenterList.clear();//yuchao  清空集合消除重复数据
                dynamicList.clear();
                if (System.currentTimeMillis() - currentTime < 10 * 1000) {
                    ptrlvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentPageNo = 1;
                            ptrlvContent.onRefreshComplete();
                        }
                    }, 10 * 1000);
                } else {
                    currentTime = System.currentTimeMillis();
                    isFirstRequestBack = true;
                    mLastDynamicId = 0;
                    currentPageNo = 1;
                    reqListData(mLastDynamicId, SIZE_PER_PAGE);
                }
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
                if (isHasNext) {
                    reqListData(mLastDynamicId, SIZE_PER_PAGE);

                } else {

                    Toast.makeText(mContext, R.string.no_more_data_text,
                            Toast.LENGTH_SHORT).show();
                    refreshView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            refreshView.onRefreshComplete();
                        }
                    }, 200);
                }
            }
        });
        emptyLayout = new EmptyLayout(FriendsDynamicActivity.this,
                ptrlvContent.getRefreshableView());
    }

    private void initData() {
        //获取当前经纬度
        geoData = LocationUtil.getCurrentGeo(mContext);
        lng = geoData.getLng();
        lat = geoData.getLat();

        mHandler = new DynamicCenterHandler();
        dynamicList = new ArrayList<>();
        reqListData(mLastDynamicId, SIZE_PER_PAGE);
    }

    /**
     * 初始化标题栏
     */
    private void initActionBar() {
        flLeft = (FrameLayout) findViewById(R.id.fl_left);

        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        categoryDyn = getIntent().getIntExtra(DYNAMIC_CATEGORY, 2);
        mIvRight.setVisibility(View.VISIBLE);
        mIvLeft.setVisibility(View.VISIBLE);
        mIvLeft.setImageResource(R.drawable.title_back);
        mIvRight.setImageResource(R.drawable.near_dynamic_publish);
        mIvRight.setVisibility(View.GONE);
//        if (categoryDyn == 2) {
////            setActionBarTitle(getResources().getString(R.string.personal_dynamics_title));
//            mTvTitle.setText(getResources().getString(R.string.personal_dynamics_title));
//        } else {
//            setActionBarTitle(getResources().getString(R.string.friend_dynamic_title));
        mTvTitle.setText(getResources().getString(R.string.friend_dynamic_title));
//        }

    }

    private void initListener() {
        flLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotLoginDialog.getInstance(getActivity()).isLogin()) {
                    Intent intent = new Intent(getActivity(), PublishDynamicActivity.class);
                    startActivityForResult(intent, PUBLISH_REQUEST_CODE);
                }
            }
        });
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setListener();
    }

    /**
     * 空布局下拉刷新数据
     */
    private void setListener() {
//        mEmptyView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
//            @Override
//            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                if (System.currentTimeMillis() - currentTime < 10 * 1000) {
//                    ptrlvContent.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ptrlvContent.onRefreshComplete();
//                        }
//                    }, 10 * 1000);
//                } else {
//                    currentTime = System.currentTimeMillis();
//                    isFirstRequestBack = true;
//                    mLastDynamicId = 0;
//                    reqListData(mLastDynamicId, SIZE_PER_PAGE);
//                }
//            }
//        });
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqListData(mLastDynamicId, SIZE_PER_PAGE);
            }
        });
    }

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
                        if (bean.getDynamicInfo().dynamiccategory == net.iaround.ui.dynamic.PublishDynamicActivity.DYNAMIC) {
                            GREET_DYNAMIC_FLAG = DynamicHttpProtocol
                                    .greetUserDynamic(mContext,
                                            bean.getDynamicInfo().dynamicid,
                                            FriendsDynamicActivity.this);
                        }
                        requestMap.put(GREET_DYNAMIC_FLAG,
                                bean.getDynamicInfo().dynamicid);
                    }

                    DynamicGreet(bean.getDynamicInfo().dynamicid);

                } else {

                    if (!ConnectorManage.getInstance(mContext).CheckNetwork(mContext)) {
                        addGreetUnSendSuccessMap(1);
                    } else {
                        if (bean.getDynamicInfo().dynamiccategory == net.iaround.ui.dynamic.PublishDynamicActivity.DYNAMIC) {
                            UNGREET_DYNAMIC_FLAG = DynamicHttpProtocol
                                    .greetCancelUserDynamic(mContext,
                                            bean.getDynamicInfo().dynamicid,
                                            FriendsDynamicActivity.this);
                        }
                        requestMap.put(UNGREET_DYNAMIC_FLAG, bean.getDynamicInfo().dynamicid);
                    }

                    DynamicCancelGreet(bean.getDynamicInfo().dynamicid);
                }
            }
        }

        @Override
        public void ItemCommentDynamic(DynamicItemBean dynamic) {
            if (dynamic.getDynamicInfo().getPhotoList().size() > 0) {
                if (dynamic.getDynamicInfo().getPhotoList().get(0).contains("storage/")) {
                    return;
                }
            }
//            DynamicDetailActivity.skipToDynamicDetail(getActivity(),
//                    FriendsDynamicActivity.this, "" + dynamic.getDynamicInfo().dynamicid, "" + dynamic.getDynamicInfo().userid,
//                    DYNAMIC_DETAIL_CODE, true);
            DynamicDetailActivity.skipToDynamicDetail(getActivity(), FriendsDynamicActivity.this, dynamic, DYNAMIC_DETAIL_CODE, true);
        }

//		@Override
//		public void ItemCommentDynamic(Dynamic dynamic) {
//			String dynamicItemStr = GsonUtil.getInstance()
//					.getStringFromJsonObject(dynamic);
//			DynamicDetailActivity.skipToDynamicDetail(getActivity(),
//					OtherDynamicsActivity.this, dynamicItemStr,
//					DYNAMIC_DETAIL_CODE, true);
//		}

    };


    private void reqListData(long lastContentTime, int pageSize) {
        showWaitDialog(FriendsDynamicActivity.this);
        DYNAMIC_LIST_GET = DynamicHttpProtocol.getRecommendDynamicList(
                mContext, lat, lng, pageSize, lastContentTime,
                DYNAMIC_FRIEND, 1, DYNAMIC_GENDER, this);
//		emptyLayout.showLoading();
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
        if (flag == DYNAMIC_LIST_GET) {
            ptrlvContent.onRefreshComplete();

            if (dynamicCenterList == null) {
                dynamicCenterList = new ArrayList<>();
            }
            if (mLastDynamicId == 0) {
                dynamicCenterList.clear();
            }
            handleGetDynamicListBack(result, flag);
        } else if (GREET_DYNAMIC_FLAG == flag ||
                UNGREET_DYNAMIC_FLAG == flag) {
            handleDynamicGreetBack(flag, result);
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
    }

    public class DynamicCenterHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_DATA:
                    reqListData(mLastDynamicId, SIZE_PER_PAGE_FIRST);
                    break;
                case REFRESH_BADAGE:
//                    refreshBadage();
                    break;
            }

            super.handleMessage(msg);
        }
    }

    // 处理获取我的动态列表返回
    private void handleGetDynamicListBack(String result, long flag) {
        DynamicCenterListBean bean = GsonUtil.getInstance().getServerBean(
                result, DynamicCenterListBean.class);
        if (bean != null && bean.isSuccess()) {
            mEmptyView.setVisibility(View.GONE);
            isHasNext = bean.isHasNext();
            mLastDynamicId = bean.last;

            ArrayList<DynamicItemBean> newDynamicList = null;
            if (bean.dynamics == null) {
                newDynamicList = new ArrayList<>();
            } else {
                if (currentPageNo == 1) {

                    dynamicList.clear();
                }
                newDynamicList = bean.dynamics;
            }
            dynamicCenterList.clear();//yuchao  清空集合消除重复数据
            dynamicList.addAll(newDynamicList);
            refreshData();
            currentPageNo++;
        } else {
//            emptyLayout.showError();
//            mTvEmptyText.setText(R.string.content_is_error);
            showEmptyView();
            ErrorCode.showError(FriendsDynamicActivity.this, result);
            showEmptyView();
        }


    }

    private void refreshData() {
        if (dynamicCenterList == null) {
            dynamicCenterList = new ArrayList<>();
        }

        if (unSendList == null) {
            unSendList = new ArrayList<>();
        }

        for (int i = 0; i < unSendList.size(); i++) {
            DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
            DynamicItemBean dynamicItem = unSendList.get(i);
            itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;//dynamicItem.getDynamicInfo().type
            itemBean.itemBean = dynamicItem;
            dynamicCenterList.add(itemBean);
        }

        if (dynamicList != null) {
            for (int i = 0; i < dynamicList.size(); i++) {
                DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
                DynamicItemBean dynamicItem = dynamicList.get(i);
                itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;
                itemBean.itemBean = dynamicItem;
                dynamicCenterList.add(itemBean);
            }
        }
        if (dynamicCenterList.isEmpty()) {
//                emptyLayout.showEmpty();
//                mTvEmptyText.setText(R.string.content_is_empty);
            showEmptyView();
        } else {
            hideEmptyView();
        }
        mAdapter.setFreshData(dynamicCenterList);
//        refreshResourceData();//jiqiang不包括资源广告文件
    }

    private void insertData() {
        if (dynamicCenterList == null) {
            dynamicCenterList = new ArrayList<>();
        } else {
            dynamicCenterList.clear();
        }
        if (DynamicModel.getInstent().getUnSendSuccessList() != null && DynamicModel.getInstent().getUnSendSuccessList().size() >= 0) {
            if (unSendList == null){
                unSendList = new ArrayList<>();
            }
            unSendList.addAll(DynamicModel.getInstent()
                    .getUnSendSuccessList());
        }
        for (int i = 0; i < unSendList.size(); i++) {
            DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
            DynamicItemBean dynamicItem = unSendList.get(i);
            itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;//dynamicItem.getDynamicInfo().type
            itemBean.itemBean = dynamicItem;
            dynamicCenterList.add(itemBean);
        }

        if (dynamicList != null) {
            for (int i = 0; i < dynamicList.size(); i++) {
                DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
                DynamicItemBean dynamicItem = dynamicList.get(i);
                itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;
                itemBean.itemBean = dynamicItem;
                dynamicCenterList.add(itemBean);
            }
        }
        if (dynamicCenterList.isEmpty()) {
//                emptyLayout.showEmpty();
//                mTvEmptyText.setText(R.string.content_is_empty);
            showEmptyView();
        } else {
            hideEmptyView();
        }
        mAdapter.setFreshData(dynamicCenterList);
//        refreshResourceData();//jiqiang不包括资源广告文件
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


    // 处理赞或者取消赞操作返回
    private void handleDynamicGreetBack(long flag, String result) {
        BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                BaseServerBean.class);
        if (bean != null && bean.isSuccess()) {
            long dynamicID = requestMap.get(flag);
            requestMap.remove(flag);
            // DynamicModel.getInstent().removeLikeOrReviewUnSendSuccessMap(dynamicID);
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
                    == net.iaround.ui.dynamic.PublishDynamicActivity.DYNAMIC ? -1
                    : mCurrentItemBean.getDynamicInfo().parentid;
            DynamicModel.getInstent().addLikeUnSendSuccessMap(
                    String.valueOf(mCurrentItemBean.getDynamicInfo().dynamicid), value);
        }
    }

    /****************
     * 点击事件的监听器
     *****************/

    private View.OnClickListener DynamicItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            DynamicItemBean bean = (DynamicItemBean) arg0.getTag();
            long dynamicId = bean.getDynamicInfo().dynamicid;
            if (bean.getDynamicInfo().dynamiccategory == net.iaround.ui.dynamic.PublishDynamicActivity.DYNAMIC) {

//                DataStatistics.get(mContext).addButtonEvent(
//                        DataTag.BTN_dynamic_item);
                if (bean.getDynamicInfo().getPhotoList().size() > 0) {
                    if (bean.getDynamicInfo().getPhotoList().get(0).contains("storage/")) {
                        return;
                    }
                }
//                DynamicDetailActivity.skipToDynamicDetail(mContext, FriendsDynamicActivity.this, "" + dynamicId, "" + bean.getDynamicInfo().userid,
//                        DYNAMIC_DETAIL_CODE, false);
                DynamicDetailActivity.skipToDynamicDetail(getActivity(), FriendsDynamicActivity.this, bean, DYNAMIC_DETAIL_CODE, false);
            } else {
//                DataStatistics.get(mContext).addButtonEvent(DataTag.BTN_dynamic_hotKanla);
//                PostbarTopicDetailActivity.launch(mContext,DynamicCenterFragment.this,
//                        bean.getDynamicInfo().parentid, "", dynamicId,bean.converTopicExtendInfo(bean) ,
//                        PostbarTopicDetailActivity.REQ_POSTBAR_THEME_SQUARE);//jiqiang 话题先注释掉
            }
        }
    };


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
        refreshData();
        mAdapter.setFreshData(dynamicCenterList);
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
        refreshData();
        mAdapter.setFreshData(dynamicCenterList);
        mAdapter.notifyDataSetChanged();
    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        ptrlvContent.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
        ptrlvContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUBLISH_REQUEST_CODE) {
            insertData();
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
                            insertData();
                            mAdapter.setChange(false);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean actionBarBackPressed() {
        // 重置好友动态请求
        DYNAMIC_LIST_GET1 = DynamicHttpProtocol.getRecommendDynamicList(
                mContext, lat, lng, 1, 0,
                "1", 1, DYNAMIC_GENDER, this);
        return true;
    }
}
