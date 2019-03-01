package net.iaround.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.contract.fragment.DynamicContract;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.presenter.DynamicPrester;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.DynamicAdapterNew;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.dynamic.PublishDynamicActivity;
import net.iaround.ui.dynamic.bean.DynamicCenterListBean;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.view.DynamicItemViewNew;
import net.iaround.ui.view.dialog.NotLoginDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Class: 个人动态
 * Author：gh
 * Date: 2017/1/5 12:08
 * Email：jt_gaohang@163.com
 */
public class PersonalDynamicActivity extends ActionBarActivity implements DynamicContract.View {

    public static final String DYNAMCI_UID = "dynamic_uid";

    private final static int DYNAMIC_DETAIL_CODE = 1000;// 动态详情
    private final static int PUBLISH_REQUEST_CODE = 1002;// 发布动态requestCode

    private PullToRefreshListView ptrlvContent;
    //    private PullToRefreshScrollView mEmptyView;
    private LinearLayout mEmptyView;
    private DynamicAdapterNew mAdapter;

    private DynamicPrester prester;

    private long currentTime;
    private long userId;//用户id
    private long dynamicId;//点赞时动态Id
    private User user;

    //标题
    private ImageView mIvLeft, mIvRight;
    private TextView mTvTitle;
    private FrameLayout flLeft;

//    private TextView mTvEmptyText;

    private ArrayList<DynamicCenterListItemBean> dynamicCenterList;
    private ArrayList<DynamicItemBean> dynamicList;
    private ArrayList<DynamicItemBean> unSendList;
    private long getUserDynamicFlag;
    private long GREET_DYNAMIC_FLAG;// 点赞动态Flag
    private long UNGREET_DYNAMIC_FLAG;// 取消点赞动态Flag

    private int amount;//动态列表返回的总条数
    private int pageSize;//动态列表返回的总页数
    private int SIZE_PER_PAGE = 10;// 动态列表每次请求10条
    private int MaxPage;
    private int currentPage = 1;
    private ImageView ivPublish;// 发布按钮

    private Dialog mWaitDialog;

    /**
     * 跳转到动态界面
     *
     * @param mContext
     * @dynamicCategory 2-代表个人动态
     */

    public static void skipToPersonalDynamicActivity(Context mContext, Fragment fragment, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, PersonalDynamicActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到动态界面
     *
     * @param mContext
     * @dynamicCategory 2-代表个人动态
     */
    public static void skipToPersonalDynamicActivity(Context mContext, Activity activity, long userId,
                                                     int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, PersonalDynamicActivity.class);
        intent.putExtra(DYNAMCI_UID, userId);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void skipToPersonalDynamicActivity(Context context, Activity activity, User user, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, PersonalDynamicActivity.class);
        intent.putExtra("user", user);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_dynamic);


        Intent intent = getIntent();
        userId = intent.getLongExtra(DYNAMCI_UID, 0);
        user = (User) intent.getExtras().getSerializable("user");
        if (prester == null)
            prester = new DynamicPrester();
        initView();
        initActionBar();
        initListener();
        initData();

        dynamicList = new ArrayList<>();
        mAdapter = new DynamicAdapterNew(getActivity(), dynamicCenterList, DynamicItemViewNew.DYNAMIC_CENTER, onClickListener);
        ptrlvContent.setAdapter(mAdapter);
        mAdapter.setFuctionBarClickListener(btnClickListener);

    }

    @Override
    public void initView() {
        ptrlvContent = (PullToRefreshListView) findViewById(R.id.pull_near_dynamic);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_friend);
//        mEmptyView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ptrlvContent.setMode(PullToRefreshBase.Mode.BOTH);
        ptrlvContent.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        ptrlvContent.getRefreshableView().setFastScrollEnabled(false);
        ptrlvContent.setOnRefreshListener(mDoubleRefreshListener);//mOnRefreshListener
        ivPublish = (ImageView) findViewById(R.id.ivPublish);
        if (user != null) {
            if (user.getUid() != Common.getInstance().loginUser.getUid()) {
                ivPublish.setVisibility(View.GONE);
            } else {
                ivPublish.setVisibility(View.VISIBLE);
            }
        }

//        mTvEmptyText = (TextView) findViewById(R.id.tv_empty);
    }

    private void initActionBar() {
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mIvRight.setVisibility(View.VISIBLE);
        mIvLeft.setVisibility(View.VISIBLE);
        mIvLeft.setImageResource(R.drawable.title_back);
        mIvRight.setImageResource(R.drawable.near_dynamic_publish);
        mIvRight.setVisibility(View.GONE);
        mTvTitle.setText(getResources().getString(R.string.personal_dynamics_title));
//        if (user != null) {
//            if (user.getUid() == Common.getInstance().loginUser.getUid()) {
//                mTvTitle.setText(getResources().getString(R.string.personal_dynamics_title));
//            } else
//                mTvTitle.setText(getResources().getString(R.string.friend_dynamic_title));
//        }//YC  只显示个人动态

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
        mIvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NotLoginDialog.getInstance(getActivity()).isLogin()) {
                    Intent intent = new Intent(getActivity(), PublishDynamicActivity.class);
                    startActivityForResult(intent, PUBLISH_REQUEST_CODE);
                }

            }
        });
        ivPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DynamicModel.getInstent().getUnSendSuccessList().size() > 0) {
                    Toast.makeText(mContext, R.string.dynamic_unsend_notice,
                            Toast.LENGTH_SHORT).show();
                } else {
//				DataStatistics.get(mContext).addButtonEvent(
//						DataTag.BTN_dynamic_publish);//jiqiang
                    PublishDynamicActivity.skipToPublishDynamicActivity(mContext,
                            PersonalDynamicActivity.this, PUBLISH_REQUEST_CODE);
                }
            }
        });
        //请求网络数据
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqListData();
                refreshData();
            }
        });
    }

    private void initData() {
//        reqListData(1);
        ptrlvContent.setRefreshing(true);
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            if (System.currentTimeMillis() - currentTime < 10 * 1000) {
                ptrlvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrlvContent.onRefreshComplete();
                    }
                }, 10 * 1000);
            } else {
                currentTime = System.currentTimeMillis();
                currentPage = 1;
                reqListData();
            }
        }

        @Override
        public void onPullUpToRefresh(
                final PullToRefreshBase<ListView> refreshView) {
            if (dynamicList.size() <= pageSize) {
                Toast.makeText(mContext, R.string.no_more_data_text,
                        Toast.LENGTH_SHORT).show();
                refreshView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refreshView.onRefreshComplete();
                    }
                }, 200);
            } else if (currentPage < pageSize && currentPage < MaxPage) {
                currentPage++;
                reqListData();
            } else {


            }
        }
    };

    /**
     * listView刷新监听器
     */
    private PullToRefreshBase.OnRefreshListener2<ListView> mDoubleRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//            reqDynamicDetailData();
//            pageNo = 1;
            currentPage = 1;
            reqListData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            getUserDynamicCommentList();
        }
    };

    // 获取用户动态列表
    private void getUserDynamicCommentList() {
        currentPage++;
        MaxPage = amount / SIZE_PER_PAGE
                + (amount % SIZE_PER_PAGE > 0 ? 1 : 0);
        if (MaxPage > 0 && currentPage > MaxPage) {
            // 已无更多地数据
            CommonFunction.toastMsg(mContext, R.string.no_more_data);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    ptrlvContent.onRefreshComplete();
                    ptrlvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }, 500);


        } else {
            reqListData();
//            GET_COMMENT_LIST_FLAG = DynamicHttpProtocol
//                    .getUserDynamicCommentList(mContext,
//                            dynamicBean.getDynamicInfo().dynamicid, pageno,
//                            SIZE_PER_PAGE, this);
        }
    }


    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            hideWaitDialog();
            if (flag == getUserDynamicFlag) {
                ptrlvContent.onRefreshComplete();
//                mEmptyView.onRefreshComplete();
                if (dynamicCenterList == null) {
                    dynamicCenterList = new ArrayList<>();
                }
                if (currentPage == 1) {
                    dynamicCenterList.clear();
                }
                handleGetDynamicListBack(result, flag);
            } else if (GREET_DYNAMIC_FLAG == flag) {
                BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                        BaseServerBean.class);
                if (bean != null && bean.isSuccess()) {
                    dynamicGreet();
                } else {
                    ErrorCode.showError(PersonalDynamicActivity.this, result);
                }
            } else if (UNGREET_DYNAMIC_FLAG == flag) {
                BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                        BaseServerBean.class);
                if (bean != null && bean.isSuccess()) {
                    dynamicCancelGreet();
                }
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
//            mEmptyView.onRefreshComplete();
            hideWaitDialog();
        }
    };

    //点赞成功
    private void dynamicGreet() {
        for (int i = 0; i < dynamicList.size(); i++) {
            DynamicItemBean info = dynamicList.get(i);
            if (info.getDynamicInfo().dynamicid == dynamicId) {
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
    private void dynamicCancelGreet() {
        for (int i = 0; i < dynamicList.size(); i++) {
            DynamicItemBean info = dynamicList.get(i);
            if (info.getDynamicInfo().dynamicid == dynamicId) {
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

    // 处理获取我的动态列表返回
    private void handleGetDynamicListBack(String result, long flag) {
        dynamicList.clear();
        Gson gsonParser = new Gson();
        Type collectionType = new TypeToken<DynamicCenterListBean>() {
        }.getType();
        if (result == null) {
            return;
        }
        hideWaitDialog();
        DynamicCenterListBean bean = gsonParser.fromJson(result, collectionType);
        if (bean != null && bean.isSuccess()) {

            ArrayList<DynamicItemBean> newDynamicList = null;
            if (bean.dynamics == null) {
                newDynamicList = new ArrayList<>();
            } else {

                newDynamicList = bean.dynamics;
                amount = bean.amount;
                pageSize = bean.pagesize;

                MaxPage = amount / SIZE_PER_PAGE
                        + (amount % SIZE_PER_PAGE > 0 ? 1 : 0);
            }

//            if (pageNo == 1)
//                dynamicList.clear();

            if (bean.dynamics == null)
                dynamicCenterList.clear();

            dynamicList.addAll(newDynamicList);
            refreshData();
        } else {
//            mTvEmptyText.setText(R.string.content_is_error);
            ErrorCode.showError(PersonalDynamicActivity.this, result);
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
                dynamicItem.reviewcount = dynamicItem.getDynamicReviewInfo().total;
                dynamicItem.likecount = dynamicItem.getDynamicLoveInfo().total;
                dynamicItem.curruserlove = dynamicItem.getDynamicLoveInfo().curruserlove;
                dynamicItem.setDynamicUser(user);
                itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;
                itemBean.itemBean = dynamicItem;
                dynamicCenterList.add(itemBean);
            }
        }
        if (dynamicCenterList.isEmpty()) {
//                mTvEmptyText.setText(R.string.content_is_empty);
            showEmptyView();
        } else {
//                mTvEmptyText.setVisibility(View.GONE);
            hideEmptyView();
        }
        mAdapter.setFreshData(dynamicCenterList);
    }

    private void insertData() {
        if (dynamicCenterList == null) {
            dynamicCenterList = new ArrayList<>();
        } else {
            dynamicCenterList.clear();
        }
        if (unSendList != null) {
            unSendList.addAll(DynamicModel.getInstent()
                    .getUnSendSuccessList());
            if (!unSendList.isEmpty()) {
                for (int i = 0; i < unSendList.size(); i++) {
                    DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
                    DynamicItemBean dynamicItem = unSendList.get(i);
                    itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;//dynamicItem.getDynamicInfo().type
                    itemBean.itemBean = dynamicItem;
                    dynamicCenterList.add(itemBean);
                }
            }

        }


        if (dynamicList != null) {
            for (int i = 0; i < dynamicList.size(); i++) {
                DynamicCenterListItemBean itemBean = new DynamicCenterListItemBean();
                DynamicItemBean dynamicItem = dynamicList.get(i);
                if (dynamicItem.getDynamicInfo() != null) {
                    if (dynamicItem.getDynamicReviewInfo() != null) {
                        dynamicItem.reviewcount = dynamicItem.getDynamicReviewInfo().total;
                    }
                    if (dynamicItem.getDynamicLoveInfo() != null) {
                    }
                    
                }
                dynamicItem.setDynamicUser(user);
                itemBean.itemType = DynamicCenterListItemBean.IMAGE_TEXT;
                itemBean.itemBean = dynamicItem;
                dynamicCenterList.add(itemBean);
            }
        }
        if (dynamicCenterList.isEmpty()) {
//                mTvEmptyText.setText(R.string.content_is_empty);
            showEmptyView();
        } else {
//                mTvEmptyText.setVisibility(View.GONE);
            hideEmptyView();
        }
        mAdapter.setFreshData(dynamicCenterList);
    }

    /**
     * 动态条目点击事件
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DynamicItemBean bean = (DynamicItemBean) view.getTag();
            long dynamicId = bean.getDynamicInfo().dynamicid;
            long userId = bean.getDynamicUser().userid;
            if (bean.getDynamicInfo().getPhotoList().size() > 0) {
                if (bean.getDynamicInfo().getPhotoList().get(0).contains("storage/")) {
                    return;
                }
            }
//            DynamicDetailActivity.skipToDynamicDetail(getActivity(),
//                    PersonalDynamicActivity.this, "" + dynamicId, "" + userId,
//                    DYNAMIC_DETAIL_CODE, false);
            DynamicDetailActivity.skipToDynamicDetail(getActivity(), PersonalDynamicActivity.this, bean, DYNAMIC_DETAIL_CODE, false);
        }
    };


    /**
     * 点赞和取消点赞的点击事件
     */
    private DynamicItemViewNew.ItemDynamicClick btnClickListener = new DynamicItemViewNew.ItemDynamicClick() {

        @Override
        public void ItemGreetDynamic(DynamicItemBean bean) {
            if (CommonFunction.forbidSay(mContext)) {
                return;
            }
            if (bean != null) {
//                mCurrentItemBean = bean;
                dynamicId = bean.getDynamicInfo().dynamicid;
                byte loveType = bean.curruserlove;
                if (loveType == 0) // 如果当前没有点赞，则点赞
                {// 网络未连接时不发送请求，直接操作，将该条put到GreetUnSendSuccessMap

                    GREET_DYNAMIC_FLAG = DynamicHttpProtocol
                            .greetUserDynamic(mContext,
                                    dynamicId,
                                    httpCallBack);
                } else {
                    UNGREET_DYNAMIC_FLAG = DynamicHttpProtocol
                            .greetCancelUserDynamic(mContext,
                                    dynamicId,
                                    httpCallBack);

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
//                    PersonalDynamicActivity.this, "" + dynamic.getDynamicInfo().dynamicid, "" + dynamic.getDynamicUser().userid,
//                    DYNAMIC_DETAIL_CODE, true);
            DynamicDetailActivity.skipToDynamicDetail(getActivity(), PersonalDynamicActivity.this, dynamic, DYNAMIC_DETAIL_CODE, true);
        }


    };

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

    /**
     * 请求动态列表数据
     */
    private void reqListData() {
//        getUserDynamicFlag = prester.doDynamic(this, userId, pageNum, pageSize, httpCallBack);
        if (user != null) {
            getUserDynamicFlag = prester.doDynamic(this, user.getUid(), currentPage, SIZE_PER_PAGE, httpCallBack);
        }

    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        ptrlvContent.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
        ptrlvContent.setVisibility(View.VISIBLE);
    }
}
