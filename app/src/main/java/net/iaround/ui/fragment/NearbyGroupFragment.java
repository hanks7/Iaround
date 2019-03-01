//package net.iaround.ui.fragment;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.text.SpannableString;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RatingBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshScrollBarListView;
//
//import net.iaround.BaseApplication;
//import net.iaround.R;
//import net.iaround.conf.ErrorCode;
//import net.iaround.connector.HttpCallBack;
//import net.iaround.connector.protocol.GroupHttpProtocol;
//import net.iaround.contract.fragment.PersonalContract;
//import net.iaround.entity.type.ChatFromType;
//import net.iaround.model.entity.GeoData;
//import net.iaround.tools.CommonFunction;
//import net.iaround.tools.DialogUtil;
//import net.iaround.tools.FaceManager;
//import net.iaround.tools.GsonUtil;
//import net.iaround.tools.LocationUtil;
//import net.iaround.tools.PicIndex;
//import net.iaround.tools.glide.GlideUtil;
//import net.iaround.ui.comon.EmptyLayout;
//import net.iaround.ui.datamodel.GroupModel;
//import net.iaround.ui.group.activity.CreateGroupPreviewActivity;
//import net.iaround.ui.group.activity.GroupInfoActivity;
//import net.iaround.ui.group.activity.GroupListActivity;
//import net.iaround.ui.group.activity.GroupRecommendActivity;
//import net.iaround.ui.group.activity.RecentNewGroupsActivity;
//import net.iaround.ui.group.bean.Group;
//import net.iaround.ui.group.bean.GroupNearBean;
//import net.iaround.ui.group.bean.Landmark;
//import net.iaround.ui.seach.NearbySearchActivity;
//import net.iaround.ui.view.HeadPhotoView;
//import net.iaround.ui.view.IndicatorViewPager;
//
//import java.util.ArrayList;
//
///**
// * Class: 附近
// * Author：gh
// * Date: 2016/11/30 10:46
// * Emial：jt_gaohang@163.com
// */
//public class NearbyGroupFragment extends Fragment implements PersonalContract.View, View.OnClickListener {
//
////    private PullToRefreshListView ptrlvContent;
////    private TextView emptyTipsText;
////    private View footerView;
////    private PersonalAdapter mAdapter;
////
////    private PersonalPrester prester;
////
////    private List<PersonalItem> personalItemList;
////    private List<PersonalByExtendUser> personalByExtendUserList;
////
////    private HashSet<Long> userSet = new HashSet<Long>();
////
////    private long currentTime;
////    private boolean isFirstRequestBack = false;
////    private boolean isFilter;       // 筛选请求
//
//
//    /**
//     * 加载数据Loading
//     */
//    private Dialog mProgressDialog;
//
//
//    /**
//     * 列表控件
//     */
//    private PullToRefreshScrollBarListView mListView;
//    private Button createGroupBtn;
//
//    /**
//     * 当前页
//     */
//    private int mCurrentPage;
//    /**
//     * 总页数
//     */
//    private int mTotalPage;
//    /**
//     * 每页数
//     */
//    private int PAGE_SIZE = 20;
//    /**
//     * 位置信息
//     */
//    private GeoData mCurrentGeo;
//
//    /**
//     * 数据适配器
//     */
//    private NearbyGroupFragment.DataAdapter mAdapter;
//
//    /**
//     * 获取附近圈子的flag
//     */
//    private long GET_NEARBY_GROUP_FLAG;
//
//    /**
//     * 圈子数据列表
//     */
//    private ArrayList<Group> mDataList = new ArrayList<Group>();
//
//    /**
//     * 附近圈子的实体bean
//     */
//    private GroupNearBean mGroupBean;
//
//    /**
//     * 空布局
//     */
//    private EmptyLayout mEmptyLayout;
//
//    /**
//     * 是否第一次加载数据
//     */
//    private boolean isFirstInit = true;
//
//    /**
//     * 分页标识地标id
//     */
//    private String mLandmarkid;
//
//    /**
//     * 是否加入了新建圈子
//     */
//    public boolean isAddRecentGroups;
//
//    public static boolean isNeedRefresh = false;
//    public HttpCallBack httpCallBack;
//
//    /**
//     * 标题栏
//     */
//    private FrameLayout flLeft;
//    private ImageView titleBack;
//    private TextView titleText;
//    private ImageView searchButton;
//    private TextView tvNum_of_myGroup;
//
//    /**
//     * 跳转到我的圈子请求码
//     */
//    public static int REQUESET_CODE_TO_MY_GROUPLIST = 100;
//
//    /**
//     * 创建圈子请求码
//     */
//    public static int REQUEST_CODE_CREATE_GROUP = 101;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.activity_group_nearby, null);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initView();
//        setListeners();
//        initData(true);
//
//        mListView.setRefreshing(true);
//        requestData(1);
//    }
//
//    /**
//     * 初始化标题栏
//     * yuchao
//     * 暂时未使用，当把圈子迁出去后添加标题栏就好
//     */
//    private void initActionBar() {
////        titleBack = (ImageView) getView().findViewById(R.id.iv_left);
////        searchButton = (ImageView) getView().findViewById(R.id.iv_right);
////        titleText = (TextView) getView().findViewById(R.id.tv_title);
////
////        titleBack.setVisibility(View.VISIBLE);
////        searchButton.setVisibility(View.VISIBLE);
////
////        titleText.setText(getString(R.string.iaround_group_title));
////        titleBack.setImageResource(R.drawable.title_back);
////        searchButton.setImageResource(R.drawable.group_contacts_search);
////
////        searchButton.setOnClickListener(this);
////        titleBack.setOnClickListener(this);
//    }
//
//    @Override
//    public void initView() {
//        if (getView() == null)
//            return;
//        //标题栏
//        flLeft = (FrameLayout) getView().findViewById(R.id.fl_left);
//        titleBack = (ImageView) getView().findViewById(R.id.iv_left);
//        titleText = (TextView) getView().findViewById(R.id.tv_title);
//        titleText.setText(getString(R.string.iaround_group_title));
//        searchButton = (ImageView) getView().findViewById(R.id.iv_right);
//        titleBack.setVisibility(View.VISIBLE);
//        searchButton.setVisibility(View.VISIBLE);
//        titleBack.setImageResource(R.drawable.title_back);
//        searchButton.setImageResource(R.drawable.group_contacts_search);
//
//
//        mListView = (PullToRefreshScrollBarListView) getView().findViewById(R.id.group_listview);
//        mListView.setMode(PullToRefreshBase.Mode.BOTH);
//        mEmptyLayout = new EmptyLayout(getContext(), mListView.getRefreshableView());
//        createGroupBtn = (Button) getView().findViewById(R.id.create_group_btn);
//
//        //暂时的头布局，用来加载我的圈子
//        View headView = LayoutInflater.from(getContext()).inflate(R.layout.group_list_head_view,null);
//        tvNum_of_myGroup = (TextView) headView.findViewById(R.id.tvNum_of_myGroup);
//
//        mListView.getRefreshableView().addHeaderView(headView);
//        headView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(getContext(),GroupListActivity.class);
////                startActivity(intent);
//                startActivityForResult(intent,REQUESET_CODE_TO_MY_GROUPLIST);
//            }
//        });
//    }
//
//
////    @Override
////    public void onPersonalSelected() {
////        CommonFunction.scrollToListviewTop(mListView.getRefreshableView());
////    }
//
//
//    private void setListeners() {
//        flLeft.setOnClickListener(this);
//        titleBack.setOnClickListener(this);
//        searchButton.setOnClickListener(this);
//        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                mLandmarkid = "";
//                mCurrentPage = 0;
//                initData(true);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                if (mCurrentPage < mTotalPage) {
//                    requestData(mCurrentPage + 1);
//                } else {
//                    mListView.postDelayed(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            Toast.makeText(getContext(), R.string.no_more,
//                                    Toast.LENGTH_SHORT).show();
//                            mListView.onRefreshComplete();
//                        }
//                    }, 200);
//                }
//            }
//        });
//
//        /**
//         * 圈子列表点击事件处理
//         */
//        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                Group group = null;
//                try {
//                    group = mDataList.get(position - 2);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (group != null && group.contentType == 2) {
//                    launchGroupInfoActivity(group);
//                }
//            }
//        };
//
//        mListView.setOnItemClickListener(onItemClickListener);
////		mListView.setOnScrollListener( new PauseOnScrollListener( ImageViewUtil.getDefault( )
////				.getImageLoader( ) , true , true ) );//jiqiang
//
////        for (int i = 0; i < mDataList.size(); i++) {
////            if (mDataList.get(i).isShowDivider == -1 || mDataList.get(i).isShowDivider == -2 )
////            {
////                mDataList.remove(i);
////            }
////        }
//        mAdapter = new DataAdapter(mDataList);
//
//        mListView.setAdapter(mAdapter);
//        createGroupBtn.setOnClickListener(this);
//        httpCallBack = new HttpCallBack() {
//            @Override
//            public void onGeneralSuccess(String result, long flag) {
//                hideProgressDialogue();
//                CommonFunction.log("group", "result***" + result);
//                if (flag == GET_NEARBY_GROUP_FLAG) {
//                    mListView.onRefreshComplete();
//
//                    mGroupBean = (GroupNearBean) GsonUtil.getInstance().getServerBean(result,
//                            GroupNearBean.class);
//                    if (mGroupBean != null) {
//                        if (mGroupBean.isSuccess()) {
//                            /**
//                             * 我的圈子数量
//                             */
//                            tvNum_of_myGroup.setText(mGroupBean.groupnum+"");
//
//                            if (mCurrentPage == 0) {
//                                mDataList.clear();
//                            }
//                            mLandmarkid = mGroupBean.landmarkid;
//                            mCurrentPage++;
//                            mTotalPage = mGroupBean.amount / PAGE_SIZE;
//                            if (mGroupBean.amount % PAGE_SIZE > 0) {
//                                mTotalPage++;
//                            }
//                            handleGroupData();
//                        } else {
////                            ErrorCode.showError(getActivity(),result);
//                            handleDataFail(mGroupBean.error, flag);
//                        }
//                    } else {
//                        handleDataFail(107, flag);
//                    }
//                }
//            }
//
//            @Override
//            public void onGeneralError(int e, long flag) {
//                hideProgressDialogue();
//                if (flag == GET_NEARBY_GROUP_FLAG) {
//                    mListView.onRefreshComplete();
////					ErrorCode.toastError( mContext , e );
//                }
//            }
//
////            @Override
////            public void onGeneralSuccess(String result, int flag) {
////                hideProgressDialogue();
////                CommonFunction.log("group", "result***" + result);
////                if (flag == GET_NEARBY_GROUP_FLAG) {
////                    mListView.onRefreshComplete();
////                    mGroupBean = (GroupNearBean) GsonUtil.getInstance().getServerBean(result,
////                            GroupNearBean.class);
////                    if (mGroupBean != null) {
////                        if (mGroupBean.isSuccess()) {
////
////                            if (mCurrentPage == 0) {
////                                mDataList.clear();
////                            }
////                            mLandmarkid = mGroupBean.landmarkid;
////                            mCurrentPage++;
////                            mTotalPage = mGroupBean.amount / PAGE_SIZE;
////                            if (mGroupBean.amount % PAGE_SIZE > 0) {
////                                mTotalPage++;
////                            }
////                            handleGroupData();
////                        } else {
////                            handleDataFail(mGroupBean.error, flag);
////                        }
////                    } else {
////                        handleDataFail(107, flag);
////                    }
////                }
////            }
////
////            @Override
////            public void onGeneralError(String error, Exception e, int flag) {
////                hideProgressDialogue();
////                if (flag == GET_NEARBY_GROUP_FLAG) {
////                    mListView.onRefreshComplete();
//////					ErrorCode.toastError( mContext , e );
////                }
////            }
//        };
//    }
//
//    private void initData(final boolean isRefresh) {
//        if (mCurrentGeo == null) {
//            mCurrentGeo = LocationUtil.getCurrentGeo(getContext());
//            if (mCurrentGeo == null) {
//                new LocationUtil(getContext()).startListener(new LocationUtil.MLocationListener() {
//
//                    @Override
//                    public void updateLocation(int type, int lat, int lng, String address,
//                                               String simpleAddress) {
//                        mCurrentGeo = LocationUtil.getCurrentGeo(getContext());
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                initData(isRefresh);
//                            }
//                        });
//                    }
//                }, 1);
//            } else {
//                initData(isRefresh);
//            }
//        } else {
//            // 取到经纬度
//            if (GroupModel.getInstance().getCacheGroupNearList().isEmpty()
//                    || isRefresh) {
//                String cacheResult = "";
//                if (CommonFunction.isEmptyOrNullStr(cacheResult)) {
//                    requestData(1);
//                } else {
//                    httpCallBack.onGeneralSuccess(cacheResult, GET_NEARBY_GROUP_FLAG);
//                }
//            } else {
//                mListView.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        mListView.onRefreshComplete();
//                    }
//                }, 200);
//                mDataList.addAll(GroupModel.getInstance().getCacheGroupNearList());
//                mCurrentPage = GroupModel.getInstance().getCacheGroupNearCurPage();
//                mTotalPage = GroupModel.getInstance().getCacheGroupNearTotalPage();
//                CommonFunction.log("group", "initData notifyDataSetChanged");
//                mAdapter.notifyDataSetChanged();
//            }
//        }
//    }
//
//    private void requestData(final int pageIndex) {
//        showProgressDialogue();
//        int lat = mCurrentGeo.getLat();
//        int lng = mCurrentGeo.getLng();
//        GET_NEARBY_GROUP_FLAG = GroupHttpProtocol.groupNear(getActivity(), lat, lng,
//                mLandmarkid, pageIndex, PAGE_SIZE, httpCallBack);//jiqiang
//        if (GET_NEARBY_GROUP_FLAG < 0) {
//            handleDataFail(104, GET_NEARBY_GROUP_FLAG);
//            isFirstInit = true;
//        }
//    }
//
//    private void showProgressDialogue() {
//        if (mProgressDialog == null) {
//            mProgressDialog = DialogUtil.showProgressDialog(getContext(), R.string.prompt,
//                    R.string.please_wait, null);
//        }
//        mProgressDialog.show();
//    }
//
//    /**
//     * @param e
//     * @param flag
//     * @Title: handleDataFail
//     * @Description: 处理失败的数据返回
//     */
//    private void handleDataFail(int e, long flag) {
//        if (flag == GET_NEARBY_GROUP_FLAG) {
//            isFirstInit = true;
//            mListView.onRefreshComplete();
////            ErrorCode.toastError(getContext(), e);
//        }
//    }
//
//    public void launchGroupInfoActivity(Group group) {
//        Intent intent = new Intent(getContext(), GroupInfoActivity.class);
//        intent.putExtra(GroupInfoActivity.GROUPID, group.id + "");
//        intent.putExtra(GroupInfoActivity.CREATEID, group.user.userid);
//        intent.putExtra(GroupInfoActivity.CREATEORICONURL, group.user.icon);
//        intent.putExtra(GroupInfoActivity.FROMSEARCH, true);
//        intent.putExtra(GroupInfoActivity.ISJOIN, false);
//        startActivity(intent);
//    }
//
//    private void hideProgressDialogue() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.hide();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUESET_CODE_TO_MY_GROUPLIST && resultCode == Activity.RESULT_OK) {
//            mListView.setRefreshing(true);
//            requestData(1);
//        } else if (requestCode == REQUEST_CODE_CREATE_GROUP && resultCode == Activity.RESULT_OK)
//        {
//            mListView.setRefreshing(true);
//            requestData(1);
//        }
//    }
//
//    class DataAdapter extends BaseAdapter {
//        private int NORMAL_GROUP_TYPE = 0;// 普通展示类型
//        private int RECENT_GROUP_TYPE = 1;// 七日新建展示类型
//        private int RECOMMEND_GROUP_TYPE = 2;// 推荐的圈子
//
//        private ArrayList<Group> mItemList;
//
//        public DataAdapter(ArrayList<Group> groupList) {
//            this.mItemList = groupList;
//        }
//
//        @Override
//        public int getCount() {
//            return mItemList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mItemList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
////            if (mItemList.get(position).isShowDivider == -1) {
////
////                return RECENT_GROUP_TYPE;
////            } else if (mItemList.get(position).isShowDivider == -2) {
////
////                return RECOMMEND_GROUP_TYPE;
////            } else {
////                return NORMAL_GROUP_TYPE;
////            }
//            return NORMAL_GROUP_TYPE;
//        }
//
//        @Override
//        public int getViewTypeCount() {
//
//            return 1;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (getItemViewType(position) == RECENT_GROUP_TYPE)//七日内新圈
//            {
////                SpecialViewHolder specialViewHolder = null;
//                ViewHolder viewHolder = null;
//                if (convertView == null) {
////                    specialViewHolder = new SpecialViewHolder();
//                    viewHolder = new ViewHolder();
//                    convertView = LayoutInflater.from(getContext()).inflate(
//                            R.layout.nearby_recent_group_item, null);
//                    convertView.setTag(viewHolder);
//                } else {
//                    viewHolder = (ViewHolder) convertView.getTag();
//                }
//                setMoreBtnClick(convertView);
//
//                LinearLayout recentGroups = (LinearLayout) convertView
//                        .findViewById(R.id.recent_groups_view);
//                for (int i = 0; i < recentGroups.getChildCount(); i++) {
//                    View childView = recentGroups.getChildAt(i);
//                    if (mGroupBean.activegroups != null
//                            && i >= mGroupBean.activegroups.size()) {
//                        childView.setVisibility(View.GONE);
//                        break;
//                    }
//                    viewHolder.groupImage = (HeadPhotoView) childView
//                            .findViewById(R.id.group_icon);
//                    viewHolder.groupName = (TextView) childView
//                            .findViewById(R.id.group_name);
//                    viewHolder.groupMembersIcon = (ImageView) childView
//                            .findViewById(R.id.group_members_icon);
//                    viewHolder.groupMemberNum = (TextView) childView
//                            .findViewById(R.id.group_members);
//                    viewHolder.groupCategory = (TextView) childView
//                            .findViewById(R.id.group_category);
//                    viewHolder.groupLocation = (TextView) childView
//                            .findViewById(R.id.group_location);
//
//                    if (mGroupBean.activegroups != null
//                            && mGroupBean.activegroups.size() > 0) {
//                        final Group activeGroup = mGroupBean.activegroups.get(i);
//                        if (activeGroup != null) {
//                            childView.setOnClickListener(new View.OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//
//                                    launchGroupInfoActivity(activeGroup);
//                                }
//                            });
//
////							ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
////									CommonFunction.thumPicture( activeGroup.icon ) ,
////									specialViewHolder.groupImage ,
////									PicIndex.DEFAULT_GROUP_SMALL ,
////									PicIndex.DEFAULT_GROUP_SMALL , null , 0 , "#00000000" );
////                            GlideUtil.loadCircleImage(getContext(), CommonFunction.thumPicture(activeGroup.icon), viewHolder.groupImage, PicIndex.DEFAULT_GROUP_SMALL,
////                                    PicIndex.DEFAULT_GROUP_SMALL);//取消使用
//
//                            SpannableString groupName = FaceManager.getInstance(
//                                    getContext()).parseIconForString(
//                                    viewHolder.groupName, getContext(),
//                                    activeGroup.name + "   ", 16);
//                            viewHolder.groupName.setText(groupName);
//
//                            // 小圈显示当前人数和总人数，大圈和十万圈只显示当前人数
//                            if (activeGroup.classify == 1) {
////                                specialViewHolder.groupMembersIcon
////                                        .setImageResource( R.drawable.group_members_gray_icon );
//                                viewHolder.groupMemberNum.setText(String
//                                        .valueOf(activeGroup.usercount) + "/"
//                                        + String.valueOf(activeGroup.maxcount));
//                            } else if (activeGroup.classify == 2) {
//                                viewHolder.groupMembersIcon
//                                        .setImageResource(R.drawable.group_num_big_round);
//                                viewHolder.groupMemberNum.setText(String
//                                        .valueOf(activeGroup.usercount));
//                            } else if (activeGroup.classify == 3) {
////                                specialViewHolder.groupMembersIcon
////                                        .setImageResource( R.drawable.group_members_red_icon );
//                                viewHolder.groupMembersIcon.setImageResource(R.drawable.group_num_big_round);
//                                viewHolder.groupMemberNum.setText(String
//                                        .valueOf(activeGroup.usercount));
//                            }
//
//                            if (activeGroup.category != null) {
//                                String[] typeArray = activeGroup.category.split("\\|");
//                                int langIndex = CommonFunction.getLanguageIndex(getContext());
//                                viewHolder.groupCategory
//                                        .setText(typeArray[langIndex]);
//                            }
//
//                            viewHolder.groupLocation.setText(activeGroup.landmarkname);
//                        }
//                    }
//                }
//                convertView.setVisibility(View.GONE);//隐藏七日内新圈
//            } else if (getItemViewType(position) == RECOMMEND_GROUP_TYPE) {//推荐圈子
////                SpecialViewHolder specialViewHolder = null;
//                ViewHolder viewHolder = null;
//                if (convertView == null) {
//                    viewHolder = new ViewHolder();
//                    convertView = LayoutInflater.from(getContext()).inflate(
//                            R.layout.nearby_recent_group_item, null);
//                    convertView.setTag(viewHolder);
//                } else {
//                    viewHolder = (ViewHolder) convertView.getTag();
//                }
//
//                setRecommendMoreBtnClick(convertView); // 查看更多推荐的圈子列表
//
//                LinearLayout recentGroups = (LinearLayout) convertView
//                        .findViewById(R.id.recent_groups_view);
//                for (int i = 0; i < recentGroups.getChildCount(); i++) {
//
//                    View childView = recentGroups.getChildAt(i);
//                    if (mGroupBean.recommendgroups != null
//                            && i >= mGroupBean.recommendgroups.size()) {
//                        childView.setVisibility(View.GONE);
//                        break;
//                    }
//                    viewHolder.groupImage = (HeadPhotoView) childView
//                            .findViewById(R.id.group_icon);
//                    viewHolder.groupName = (TextView) childView
//                            .findViewById(R.id.group_name);
//                    viewHolder.groupMembersIcon = (ImageView) childView
//                            .findViewById(R.id.group_members_icon);
//                    viewHolder.groupMemberNum = (TextView) childView
//                            .findViewById(R.id.group_members);
//                    viewHolder.groupCategory = (TextView) childView
//                            .findViewById(R.id.group_category);
//                    viewHolder.groupLocation = (TextView) childView
//                            .findViewById(R.id.group_location);
//
//                    if (mGroupBean.recommendgroups != null
//                            && mGroupBean.recommendgroups.size() > 0) {
//                        final Group activeGroup = mGroupBean.recommendgroups.get(i);
//                        if (activeGroup != null) {
//                            childView.setOnClickListener(new View.OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//
//                                    launchGroupInfoActivity(activeGroup);
//                                }
//                            });
//
////							ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
////									CommonFunction.thumPicture( activeGroup.icon ) ,
////									specialViewHolder.groupImage ,
////									PicIndex.DEFAULT_GROUP_SMALL ,
////									PicIndex.DEFAULT_GROUP_SMALL , null , 0 , "#00000000" );
////									GlideUtil  jiqiang
////                            GlideUtil.loadCircleImage(getContext(), CommonFunction.thumPicture(activeGroup.icon), viewHolder.groupImage, PicIndex.DEFAULT_GROUP_SMALL,
////                                    PicIndex.DEFAULT_GROUP_SMALL);//取消使用
//                            SpannableString groupName = FaceManager.getInstance(
//                                    parent.getContext()).parseIconForString(
//                                    viewHolder.groupName, parent.getContext(),
//                                    activeGroup.name + "   ", 16);
//                            viewHolder.groupName.setText(groupName);
//
//                            // 小圈显示当前人数和总人数，大圈和十万圈只显示当前人数
//                            if (activeGroup.classify == 1) {
////                                specialViewHolder.groupMembersIcon
////                                        .setImageResource( R.drawable.group_members_gray_icon );
//                                viewHolder.groupMembersIcon.setImageResource(R.drawable.group_num_small_round);
//                                viewHolder.groupMemberNum.setText(String
//                                        .valueOf(activeGroup.usercount) + "/"
//                                        + String.valueOf(activeGroup.maxcount));
//                            } else if (activeGroup.classify == 2) {
////                                specialViewHolder.groupMembersIcon
////                                        .setImageResource( R.drawable.group_members_red_icon );
//                                viewHolder.groupMembersIcon.
//                                        setImageResource(R.drawable.group_num_big_round);
//                                viewHolder.groupMemberNum.setText(String
//                                        .valueOf(activeGroup.usercount));
//                            } else if (activeGroup.classify == 3) {
////                                specialViewHolder.groupMembersIcon
////                                        .setImageResource( R.drawable.group_members_red_icon );
//                                viewHolder.groupMembersIcon.
//                                        setImageResource(R.drawable.group_num_big_round);
//                                viewHolder.groupMemberNum.setText(String
//                                        .valueOf(activeGroup.usercount));
//                            }
//
//                            if (activeGroup.category != null) {
//                                String[] typeArray = activeGroup.category.split("\\|");
//                                int langIndex = CommonFunction.getLanguageIndex(getContext());
//                                viewHolder.groupCategory
//                                        .setText(typeArray[langIndex]);
//                            }
//
//                            viewHolder.groupLocation.setText(activeGroup.landmarkname);
//                        }
//
//                    }
//                }
//                convertView.setVisibility(View.GONE);
//            } else if (getItemViewType(position) == NORMAL_GROUP_TYPE) {//普通展示
//                ViewHolder holder = null;
//                if (convertView == null) {
//                    holder = new ViewHolder();
//                    convertView = LayoutInflater.from(getContext()).inflate(
//                            R.layout.group_near_item, null);
//                    holder.headerPart = convertView.findViewById(R.id.header_part);
//                    holder.groupHeader = (TextView) convertView
//                            .findViewById(R.id.header_text);
//                    holder.groupDistance = (TextView) convertView
//                            .findViewById(R.id.distance_text);
//
//                    holder.groupContent = (RelativeLayout) convertView
//                            .findViewById(R.id.content_layout);
//                    holder.groupImage = (HeadPhotoView) convertView
//                            .findViewById(R.id.group_img);
//                    holder.groupTypeImg = (ImageView) convertView
//                            .findViewById(R.id.group_type);
//
//                    holder.groupName = (TextView) convertView.findViewById(R.id.group_name);
//
//                    holder.groupMembersIcon = (ImageView) convertView.findViewById(R.id.group_members_icon);
//                    holder.groupMemberNum = (TextView) convertView.findViewById(R.id.group_members);
//                    holder.groupCategory = (TextView) convertView
//                            .findViewById(R.id.group_category);
//
////                    holder.groupOwnerBackground = convertView
////                            .findViewById( R.id.group_owner_gender_view );
//                    holder.groupOwnerBackground = convertView.findViewById(R.id.ll_group_owner);
//                    holder.groupOwnerImg = (ImageView) convertView
//                            .findViewById(R.id.group_owner_gender_icon);
//                    holder.groupFlag = (ImageView) convertView.findViewById(R.id.group_flag);
//                    holder.groupDesc = (TextView) convertView.findViewById(R.id.group_desc);
//                    holder.bottomDivider = convertView.findViewById(R.id.bottom_divider);
//                    convertView.setTag(holder);
//                } else {
//                    holder = (ViewHolder) convertView.getTag();
//                }
//
//                Group group = mItemList.get(position);
//                if (group != null) {
//                    //gh 分割线没有显示
////                    holder.bottomDivider.setVisibility(View.VISIBLE);
//
//					if ( group.isShowDivider == 1 )
//					{
//						holder.bottomDivider.setVisibility( View.VISIBLE );
//					}
//					else
//					{
//						holder.bottomDivider.setVisibility( View.GONE );
//					}
//
//                    if (group.contentType == 1) {
//                        if (group.isShowDivider == -1 || group.isShowDivider == -2)
//                        {//推荐圈子和七日内新圈不显示圈子标题
//                            holder.headerPart.setVisibility(View.GONE);
//                        }else {
//                            holder.headerPart.setVisibility(View.VISIBLE);
//                        }
//                        holder.groupHeader.setVisibility(View.VISIBLE);
//                        holder.groupContent.setVisibility(View.GONE);
//                        holder.groupHeader.setText(group.landmarkname + "");
//                        holder.groupDistance.setText( CommonFunction
//                                .covertSelfDistance( group.rang ) );
//                        if ("热门圈子".equals(group.landmarkname))
//                        {//热门圈子不显示右侧距离
//                            holder.groupDistance.setVisibility(View.GONE);
//                        }
//                    } else {
//                        holder.headerPart.setVisibility(View.GONE);
//                        holder.groupHeader.setVisibility(View.GONE);
//                        holder.groupContent.setVisibility(View.VISIBLE);
//
////						ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
////								CommonFunction.thumPicture( group.icon ) , holder.groupImage ,
////								PicIndex.DEFAULT_GROUP_SMALL ,
////								PicIndex.DEFAULT_GROUP_SMALL , null , 0 , "#00000000" );
//                        GlideUtil.loadCircleImage(BaseApplication.appContext, CommonFunction.thumPicture(group.icon), holder.groupImage.getImageView(), PicIndex.DEFAULT_GROUP_SMALL,
//                                PicIndex.DEFAULT_GROUP_SMALL);
////                        holder.groupImage.execute(ChatFromType.UNKONW,group.user.convertBaseToUser(),null);//加载带边框的圈头像
////						ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView(
////								group.categoryicon , holder.groupTypeImg ,
////								R.drawable.default_pitcure_small ,
////								R.drawable.default_pitcure_small , null , 30 );
//                        GlideUtil.loadCircleImage(BaseApplication.appContext, CommonFunction.thumPicture(group.categoryicon), holder.groupTypeImg, R.drawable.default_pitcure_small,
//                                R.drawable.default_pitcure_small);
//
//                        SpannableString groupName = FaceManager.getInstance(
//                                getContext()).parseIconForString(holder.groupName,
//                                getContext(), group.name + "   ", 16);
//                        holder.groupName.setText(groupName);
//
//                        SpannableString groupDesc = FaceManager.getInstance(getContext())
//                                .parseIconForString(holder.groupDesc, getContext(),
//                                        group.getGroupDesc(getContext()), 13);
//                        holder.groupDesc.setText(groupDesc);
//
////                        if ( group.flag == 1 ) yuchao
////                        { // new
////                            holder.groupFlag.setVisibility( View.VISIBLE );
////                            holder.groupFlag.setImageResource( R.drawable.z_common_new_flag_img );
////                        }
////                        else if ( group.flag == 2 )
////                        { // hot
////                            holder.groupFlag.setVisibility( View.VISIBLE );
////                            holder.groupFlag.setImageResource( R.drawable.z_common_hot_flag_img );
////                        }
////                        else
////                        {
////                            holder.groupFlag.setVisibility( View.GONE );
////                        }
//
//                        // 小圈显示当前人数和总人数，大圈和十万圈只显示当前人数
//                        if (group.classify == 1) {
//                            holder.groupMembersIcon.setImageResource( R.drawable.group_num_small_round );
//                            holder.groupMemberNum.setText(String.valueOf(group.usercount) + "/" + String.valueOf(group.maxcount));
//                        } else if (group.classify == 2) {
//                            holder.groupMembersIcon.setImageResource(R.drawable.group_num_big_round);
//                            holder.groupMemberNum.setText(String.valueOf(group.usercount));
//                        } else if (group.classify == 3) {
//                            holder.groupMembersIcon.setImageResource(R.drawable.group_num_big_round);
//                            holder.groupMemberNum.setText(String.valueOf(group.usercount));
//                        }
//
//                        if (group.category != null) {
//                            String[] typeArray = group.category.split("\\|");
//                            int langIndex = CommonFunction.getLanguageIndex(getContext());
//                            holder.groupCategory.setText(typeArray[langIndex]);
//                        }
//
//                        if (group.user != null) {
//                            if (group.user.viplevel > 0) {
//                                holder.groupOwnerBackground
//                                        .setBackgroundResource(R.drawable.group_recommend_owner_bg);
//                                holder.groupOwnerImg.setImageResource(R.drawable.group_owner_vip);
//                            } else {
//                                if ("f".equals(group.user.gender)) {
//                                    holder.groupOwnerBackground
//                                            .setBackgroundResource(R.drawable.encounter_woman_circel_bg);
//                                    holder.groupOwnerImg.setImageResource(R.drawable.thread_register_woman_select);
//                                } else {
//                                    holder.groupOwnerBackground
//                                            .setBackgroundResource(R.drawable.encounter_man_circel_bg);
//                                    holder.groupOwnerImg.setImageResource(R.drawable.thread_register_man_select);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            return convertView;
//        }
//
//        private void setMoreBtnClick(View newgroupsView) {
//            newgroupsView.findViewById(R.id.header_part).setOnClickListener(
//                    new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//							RecentNewGroupsActivity.launch(getContext());//jiqiang
//                        }
//                    });
//        }
//
//        private void setRecommendMoreBtnClick(View newgroupsView) {
//            ((TextView) (newgroupsView.findViewById(R.id.header_text)))
//                    .setText(R.string.group_inf_group_recommend_title);
//            newgroupsView.findViewById(R.id.header_part).setOnClickListener(
//                    new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
//						RecentNewGroupsActivity.launch( getContext() , true );//jiqiang
//                        }
//                    });
//        }
//    }
//
//    static class ViewHolder {
//        public TextView groupHeader;
//        public RelativeLayout groupContent;
////        public ImageView groupImage;
//        public HeadPhotoView groupImage;
//        public ImageView groupTypeImg;
//        public TextView groupName;
//        public TextView groupDesc;
//        public RatingBar groupLevel;
//        public TextView groupMemberNum;
//
//        public View headerPart;
//        public TextView groupDistance;
//        public ImageView groupMembersIcon;
//        public ImageView groupFlag;
//        public TextView groupCategory;
//        public View bottomDivider;
//
//        public View groupOwnerBackground;
//        public ImageView groupOwnerImg;
//
//        public TextView titleText;
//        public View moreView;
//        public TextView groupLocation;
//    }
//
////    static class SpecialViewHolder {
////        public TextView titleText;
////        public View moreView;
////        public ImageView groupImage;
////        public TextView groupName;
////        public ImageView groupMembersIcon;
////        public TextView groupMemberNum;
////        public TextView groupCategory;
////        public TextView groupLocation;
////
////    }
//
//    private void handleGroupData() {
//        String landmarkName = "";
//        for (Landmark landmark : mGroupBean.landmarks) {
//            int distance = LocationUtil.calculateDistance(mCurrentGeo.getLng(),
//                    mCurrentGeo.getLat(), landmark.lng, landmark.lat);
//            if (!landmark.name.equals(landmarkName)) {
//                Group header = new Group();
//                header.contentType = 1;
//                header.landmarkname = landmark.name;
//                header.rang = landmark.distance;
//                header.isShowDivider = 0;
//                mDataList.add(header);
//            }
//
//            int length = landmark.groups.size();
//
//            for (int i = 0; i < length; i++) {
//                Group group = landmark.groups.get(i);
//                group.contentType = 2;
//                group.rang = distance;
//                if (i < (length - 1)) {
//                    group.isShowDivider = 1;
//                } else {
//                    group.isShowDivider = 0;
//                }
//                mDataList.add(group);
//            }
//
//            landmarkName = landmark.name;
//        }
//
//        if (mGroupBean.activegroups != null && mGroupBean.activegroups.size() > 1) {
//            int size = mDataList.size();
//            int seq = 0;
//            boolean isHaverecommend = (mGroupBean.recommendgroups != null && mGroupBean.recommendgroups.size() > 0);
//
//            int poi = isHaverecommend ? 3 : 2;
//            for (int i = 0; i < size; i++) {
//                if (mDataList.get(i).contentType == 1) {
//                    seq++;
//                    if (seq == poi) {
//                        Group newGroup = new Group();
//                        newGroup.isShowDivider = -1;
//                        newGroup.contentType = 1;
//                        mDataList.add(i, newGroup);
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (mGroupBean.recommendgroups != null && mGroupBean.recommendgroups.size() > 0) {
//            int size = mDataList.size();
//            int seq = 0;
//            for (int i = 0; i < size; i++) {
//                if (mDataList.get(i).contentType == 1) {
//                    seq++;
//                    if (seq == 2) {
//                        Group newGroup = new Group();
//                        newGroup.isShowDivider = -2;
//                        newGroup.contentType = 1;
//                        mDataList.add(i, newGroup);
//                        break;
//                    }
//                }
//            }
//        }
//
//        // 缓存列表数据
//        GroupModel.getInstance().cacheGroupNearList(mDataList, mCurrentPage,
//                mTotalPage);
//        CommonFunction.log("group", "handleGroupData notifyDataSetChanged");
//        // 刷新列表数据
//        mAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isNeedRefresh) {
//            if (mAdapter != null) {
//                /*mDataList.clear( );
//                mDataList.addAll( GroupModel.getInstance( ).getCacheGroupNearList( ) );*/
//                mAdapter.notifyDataSetChanged();
//            }
//            isNeedRefresh = false;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mProgressDialog != null) {
//            mProgressDialog.dismiss();
//            mProgressDialog = null;
//        }
//    }
//
//    public void setFilterRefershing() {
//        mListView.setRefreshing(true);
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (searchButton.equals(v)) {
////			NearbySearchActivity.launch( mActivity , NearbySearchActivity.TAB_INDEX_GROUPS ,
////					REQUEST_CODE_GET_LATLNG );//jiqiang  sousuo
//
//            Intent intent = new Intent();
//            intent.setClass(this.getContext(), NearbySearchActivity.class);
//            intent.putExtra("isFrom",1);
//            startActivity(intent);
//        } else if (titleBack.equals(v) || flLeft.equals(v)) {
//            getActivity().finish();
//        } else if (createGroupBtn.equals(v)) {
//            Intent intent = new Intent(getContext(), CreateGroupPreviewActivity.class);
////            startActivity(intent);//jiqiang
//            startActivityForResult(intent,REQUEST_CODE_CREATE_GROUP);
//        }
//    }
//}
