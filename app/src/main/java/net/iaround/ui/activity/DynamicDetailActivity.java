package net.iaround.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollBarListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.model.entity.DynamicCommentBack;
import net.iaround.model.entity.DynamicReviewItem;
import net.iaround.model.entity.DynamicReviewItem.ReplyUser;
import net.iaround.model.entity.DynamicReviewItem.ReviewUser;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.DynamicLoveInfo.LoverUser;
import net.iaround.model.im.Me;
import net.iaround.model.obj.DynamicDetails;
import net.iaround.model.type.ReportType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.EditTextUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.FaceManager.FaceIcon;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.StringUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.adapter.DynamicDetailsAdapter;
import net.iaround.ui.chat.ChatFace;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.bean.DynamicItemBeanParent;
import net.iaround.ui.dynamic.bean.DynamicReviewInfo;
import net.iaround.ui.space.bean.InvisibilitySettingOption;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.dialog.DynamicDeatailsSettingDialog;
import net.iaround.ui.view.dialog.DynamicDeatailsSettingDialog.ItemOnclick;
import net.iaround.ui.view.dialog.NotLoginDialog;
import net.iaround.ui.view.dynamic.CommentItemView;
import net.iaround.ui.view.dynamic.DynamicDetailsHeadView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Class: 动态详情页面
 * Author：gh
 * Date: 2017/1/3 22:14
 * Email：jt_gaohang@163.com
 * Note:关于这个列表为什么不把内容做成评论的HeadView的解释:<br>
 * 因为当只有内容没有评论的时候,且内容的View超过一屏,这个时候会导致分屏显示,滑动时候出现异常.<br>
 * 也就是ListView为空,而HeadView超过一屏.
 */
public class DynamicDetailActivity extends TitleActivity implements
        OnClickListener, HttpCallBack {

    private PullToRefreshScrollBarListView svDynamicContent;// 动态ScrollView
    private DynamicDetailsAdapter mAdapter;

    // 输入框View 1.输入框EditText 2.发送button 3.face button
    private RelativeLayout llInputBar;// chat_input_layout
    private ImageView face_menu;// 表情按钮
    private EditText editContent;// 输入框
    private TextView mBtnSend;
    private FrameLayout rlFaceLayout;// 表情布局
    protected ChatFace chatFace = null;// 表情按钮对象
    private int inputState = 0;// 初始为0，显示表情为1，显示键盘为2
    private int mLength = 140; // 限制输入长度
    private static int FACE_TAG_NUM = 4;// 单个表情占几个长度

//    private DynamicItemBean dynamic;

    private List<DynamicDetails> dynamicDetailses = new ArrayList<DynamicDetails>();
    private static final String mIsShowKeyboradKey = "isShowKeyboard";
    public static final String mDynamicIdKey = "dynamicId";
    public static final String mDynamicKey = "dynamic";
    public static final String mUserIdKey = "userid";
    private static final String mUserNameKey = "userName";
    private long dynamicId;// 动态id
    private long userId;// 用户id 当前动态的userId


    private String replyID;// 回复用户的id
    private long replyuid;// 回复用户的id
    private String replyName;// 回复用户的名字
    private String replyContent;// 回复内容复制

    private Dialog progressDialog;// 加载栏

    private DynamicDeatailsSettingDialog settingDialog;// 设置的Dialog
    private CustomContextDialog reportDialog;// 举报的Dialog

    private boolean isMyDynamic = false;

    //设置标题
    private ImageView mIvLeft, mIvRight;
    private TextView mTvTitle;
    private FrameLayout flLeft;

    public static boolean isRequestDetailSuccess = false;
    public final static int RESULT_DELETE = 0X100;// 刷新请求

    private int pageSize = 20;
    private int page = 1;

    private long GET_DYNAMIC_DETAIL_FLAG;// 获取整条动态的详情
    private long mGetCommentHistoryFlag;// 获取历史评论flag
    private long GREET_DYNAMIC_FLAG;// 点赞动态Flag
    private long UNGREET_DYNAMIC_FLAG;// 取消点赞动态Flag
    private long COMMENT_FLAG;// 评论的Flag
    private long REPORT_DYNAMIC_FALG;// 举报动态Flag
    private long RESTRICTION_DYNAMIC_FALG;// 动态权限限制Flag
    private long DELETE_DYNAMIC_FLAG;// 删除动态Flag
    private long DELETE_COMMENT_FALG;// 删除评论
    private DynamicItemBean back;

    private DynamicDetailsHeadView header;

    public int DYNAMIC_REJECT_HIM = 0;//不让他看我的动态
    public int DYNAMIC_NO_SEE_HIM = 0;//不看TA的动态
    private long UPDATE_INVISIBILITY_SETTING_FLAG;
    private long UPDATE_INVISIBILITY_SETTING_REJECT_FLAG;
    private long GET_INVISIBILITY_SETTING_FLAG;//请求关于隐私设置的标识

    private InvisibilitySettingOption option;

    public static boolean isDynamicData = false;

    /**
     * listView刷新监听器
     */
    private OnRefreshListener2<ListView> mDoubleRefreshListener = new OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            if (back != null) {
                page = 1;
                getUserDynamicCommentList();
            } else {
                svDynamicContent.onRefreshComplete();
            }

        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            if (back != null) {
                page++;
                getUserDynamicCommentList();
            } else {
                svDynamicContent.onRefreshComplete();
            }

        }
    };

    /**
     * 跳转到动态详情
     *
     * @param mContext
     * @param dynamicId
     * @param userId
     * @param requestCode
     * @param isShowKeyboard
     */
    public static void skipToDynamicDetail(Context mContext, Fragment fragment,
                                           String dynamicId, String userId, int requestCode, boolean isShowKeyboard) {
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(mDynamicIdKey, dynamicId);
        intent.putExtra(mUserIdKey, userId);
        intent.putExtra(mIsShowKeyboradKey, isShowKeyboard);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到动态详情
     *
     * @param mContext
     * @param dynamicId
     * @param userId
     * @param isShowKeyboard
     */
    public static void skipToDynamicDetail(Context mContext,
                                           String dynamicId, String userId, boolean isShowKeyboard) {
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(mDynamicIdKey, dynamicId);
        intent.putExtra(mUserIdKey, userId);
        intent.putExtra(mIsShowKeyboradKey, isShowKeyboard);
        mContext.startActivity(intent);
    }

    /**
     * 跳转到动态详情
     *
     * @param mContext
     * @param activity
     * @param requestCode
     * @param isShowKeyboard
     */
    public static void skipToDynamicDetail(Context mContext, Activity activity,
                                           String dynamicId, String userId, int requestCode, boolean isShowKeyboard) {
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(mDynamicIdKey, dynamicId);
        intent.putExtra(mUserIdKey, userId);
        intent.putExtra(mIsShowKeyboradKey, isShowKeyboard);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到动态详情-带动态数据
     *
     * @param mContext
     * @param activity
     * @param requestCode
     * @param isShowKeyboard
     */
    public static void skipToDynamicDetail(Context mContext, Activity activity,
                                           DynamicItemBean dynamicItemBean, int requestCode, boolean isShowKeyboard) {
        isDynamicData = true;
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(mDynamicKey, dynamicItemBean);
        intent.putExtra(mIsShowKeyboradKey, isShowKeyboard);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到动态详情-带动态数据
     *
     * @param mContext
     * @param fragment
     * @param requestCode
     * @param isShowKeyboard
     */
    public static void skipToDynamicDetail(Context mContext, Fragment fragment,
                                           DynamicItemBean dynamicItemBean, int requestCode, boolean isShowKeyboard) {
        isDynamicData = true;
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(mDynamicKey, dynamicItemBean);
        intent.putExtra(mIsShowKeyboradKey, isShowKeyboard);
        fragment.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daynamic_detail);

        initView();

        settingDialog = new DynamicDeatailsSettingDialog(this, 0, 0, 0, 0, 0);
        settingDialog.setItemOnclick(itemOnclick);
        reportDialog = new CustomContextDialog(this, 1);
        reportDialog.setListenner(reportClick);

        if (isDynamicData) {
            back = (DynamicItemBean) getIntent().getSerializableExtra(mDynamicKey);
            if (back == null) {
                dynamicId = getIntent().getLongExtra(mDynamicIdKey, 0);
                userId = getIntent().getLongExtra(mUserIdKey, 0);

            } else {
                dynamicId = back.getDynamicInfo().dynamicid;
                userId = back.getDynamicUser().userid;
                handleGetDynamicBackNew();
            }

        } else {
            if (getIntent().getStringExtra(mDynamicIdKey) != null && "null" != getIntent().getStringExtra(mDynamicIdKey)) {
                dynamicId = Long.valueOf(getIntent().getStringExtra(mDynamicIdKey));
            }
            if (getIntent().getStringExtra(mUserIdKey) != null && "null" != getIntent().getStringExtra(mUserIdKey)) {
                userId = Long.valueOf(getIntent().getStringExtra(mUserIdKey));
            }

        }

        if (getIntent().getBooleanExtra(mIsShowKeyboradKey, false)) {
            hideFaceShowKeyboard();
        }

        reqDynamicDetailData();
        reqMoreData();
    }

    private void initView() {

        svDynamicContent = (PullToRefreshScrollBarListView) findViewById(R.id.ptrslvDynamicContent);
        svDynamicContent.getRefreshableView().setSelector(R.color.transparent);
        svDynamicContent.setOnRefreshListener(mDoubleRefreshListener);
        svDynamicContent.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        svDynamicContent.getRefreshableView().setFastScrollEnabled(false);
        svDynamicContent.setMode(Mode.BOTH);

        mAdapter = new DynamicDetailsAdapter(this, dynamicDetailses);
        mAdapter.setCommentLongClickListener(commentLongClickListener);
        mAdapter.setCommentContentClickListener(commentContentClickListener);

        svDynamicContent.setAdapter(mAdapter);
        if (header == null) {
            header = DynamicDetailsHeadView.initDynamicView(mContext);
        }

        svDynamicContent.getRefreshableView().addHeaderView(header);

        initActionBar();
        initInputBar();
    }

    private void initActionBar() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);

        mTvTitle.setVisibility(View.VISIBLE);
        mIvRight.setVisibility(View.VISIBLE);
        mIvLeft.setVisibility(View.VISIBLE);

        mTvTitle.setText(getResources().getString(R.string.dynamic_detail_title));
        mIvLeft.setImageResource(R.drawable.title_back);
        mIvRight.setImageResource(R.drawable.title_more);

        mIvLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);
    }

    // 初始化输入框
    private void initInputBar() {
        hiddenKeyBoard(this);
        llInputBar = (RelativeLayout) findViewById(R.id.chat_input_layout);
        llInputBar.setVisibility(View.INVISIBLE);

        face_menu = (ImageView) findViewById(R.id.face_menu);
        face_menu.setImageResource(R.drawable.iaround_chat_face);
        face_menu.setOnClickListener(this);

        editContent = (EditText) findViewById(R.id.editContent);
        editContent.requestFocus();
        EditTextUtil.autoLimitLength(editContent, mLength);
        editContent.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!NotLoginDialog.getInstance(getActivity()).isLogin())
                    return;
                hideFaceShowKeyboard();
            }
        });
        rlFaceLayout = (FrameLayout) findViewById(R.id.faceMenu);

//		mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend = (TextView) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(this);
    }

    /**
     * 初始化举报的Menu
     */
    private void initPopMenuData() {
        if (isMyDynamic) {
            settingDialog.init(DynamicDeatailsSettingDialog.DELETE);
        } else {
            settingDialog.init(DynamicDeatailsSettingDialog.REPORT);
            settingDialog.setSeeHimDynamic(DYNAMIC_NO_SEE_HIM);
            settingDialog.setMyDynamic(DYNAMIC_REJECT_HIM);
        }
        settingDialog.setItemOnclick(itemOnclick);
    }

    private ItemOnclick itemOnclick = new ItemOnclick() {
        @Override
        public void itemOnclick(View view) {
            if (view.getTag().equals("comment_delete")) {
                showProgressDialog();
                DELETE_COMMENT_FALG = DynamicHttpProtocol.deleteDynamicComment(mContext, dynamicId, Long.valueOf(replyID), new HttpCallBackImpl(DynamicDetailActivity.this) );
            } else if (view.getTag().equals("copy")) {
                CommonFunction.setClipboard(mContext, replyContent);
            } else if (view.getTag().equals("report_user")) {
                settingDialog.dismiss();
                reportDialog.show();
            } else if (view.getTag().equals("delete")) {
                DialogUtil.showOKCancelDialog(mContext, R.string.prompt,
                        R.string.dynamic_delete_prompt_info,
                        new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                showProgressDialog();
                                DELETE_DYNAMIC_FLAG = DynamicHttpProtocol.deleteDynamic(mContext, dynamicId, new HttpCallBackImpl(DynamicDetailActivity.this));
                            }
                        });
            } else if (view.getTag().equals("other_notice")) {
//                setDynamicOpen(3);
                if (DYNAMIC_NO_SEE_HIM == 0) {
                    //不看他的动态
                    UPDATE_INVISIBILITY_SETTING_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, userId, 3, "n", new HttpCallBackImpl(DynamicDetailActivity.this) );
                } else {
                    //可以看他动态
                    UPDATE_INVISIBILITY_SETTING_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, userId, 3, "y", new HttpCallBackImpl(DynamicDetailActivity.this) );
                }
            } else if (view.getTag().equals("mine_notice")) {
//                setDynamicOpen(2);
                if (DYNAMIC_REJECT_HIM == 0) {
                    //不让他我我动态
                    UPDATE_INVISIBILITY_SETTING_REJECT_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, userId, 2, "n", new HttpCallBackImpl(DynamicDetailActivity.this) );
                } else {
                    //让他看我的动态
                    UPDATE_INVISIBILITY_SETTING_REJECT_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, userId, 2, "y", new HttpCallBackImpl(DynamicDetailActivity.this) );
                }
            }
        }
    };

    @Override
    public void onClick(View arg0) {

        if (arg0.equals(face_menu)) {
            faceMenuBtnClick();
        } else if (arg0.equals(mBtnSend)) {
            if (!NotLoginDialog.getInstance(getActivity()).isLogin()) return;

            if (!TextUtils.isEmpty(editContent.getEditableText().toString())) {
                sendBtnClick();
            }
        }
        if (arg0.equals(flLeft) || arg0.equals(mIvLeft)) {
            if (settingDialog.isShowing()) {
                settingDialog.hide();
            } else {
                if (reportDialog.isShowing()) {
                    reportDialog.hide();
                } else {
                    String dynamicInfo = GsonUtil.getInstance().getStringFromJsonObject(
                            getDynamicInfo());
                    if (dynamicInfo == null)
                        return;
                    Intent data = new Intent();
                    data.putExtra("dynamicInfo", dynamicInfo);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }

            }
        } else if (arg0.equals(mIvRight)) {
            if (!NotLoginDialog.getInstance(getActivity()).isLogin())  //登录注册对话框
                return;
            reportDialog.hide();
            settingDialog.show();


            initPopMenuData();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String dynamicInfo = GsonUtil.getInstance().getStringFromJsonObject(
                    getDynamicInfo());
            Intent data = new Intent();
            data.putExtra("dynamicInfo", dynamicInfo);
            setResult(Activity.RESULT_OK, data);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @return
     */
    private DynamicItemBean getDynamicInfo() {
        DynamicItemBean dynamicItemBean = new DynamicItemBean();
        int loveNumber = 0;
        if (back == null)
            return dynamicItemBean;
        loveNumber = back.likecount;
        int commentNumber = 0;
        commentNumber = back.reviewcount;
        dynamicItemBean.likecount = loveNumber;
        dynamicItemBean.reviewcount = commentNumber;
        dynamicItemBean.curruserlove = back.curruserlove;
        dynamicItemBean.setDynamicLoveInfo(back.getDynamicLoveInfo());
        dynamicItemBean.setDynamicInfo(back.getDynamicInfo());
        dynamicItemBean.setDynamicReviewInfo(back.getDynamicReviewInfo());
        dynamicItemBean.setDynamicUser(back.getDynamicUser());
        dynamicItemBean.setPublishInfo(back.getPublishInfo());
        dynamicItemBean.setSendStatus(back.getSendStatus());
        return dynamicItemBean;
    }

    /**
     * 表情按钮
     */
    private void faceMenuBtnClick() {
        if (!NotLoginDialog.getInstance(getActivity()).isLogin()) return;
        if (!isFaceMenuShow()) {
            hiddenKeyBoard(this);
            if (inputState == 0 || inputState == 2) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        if (inputState == 0 || inputState == 2) {
                            showFaceMenu();
                        }
                    }
                }, 200);
            }
        } else {
            hideFaceShowKeyboard();
        }
    }

    // 发送按钮
    private void sendBtnClick() {
        // 发送 1.清空数据 2.发送协议
        replyContent = editContent.getText().toString();
        if (TextUtils.isEmpty(replyContent)) {
            CommonFunction.toastMsg(mContext, R.string.dynamic_comment_tips);
        } else {
            showProgressDialog();

            COMMENT_FLAG = DynamicHttpProtocol.commentUserDynamic(mContext, back.getDynamicInfo().dynamicid, replyContent, replyuid, new HttpCallBackImpl(DynamicDetailActivity.this));
            hideFaceMenuAndKeyboard();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        isDynamicData = false;
    }

    // 请求详情的数据
    private void reqDynamicDetailData() {
        showProgressDialog();
        GET_DYNAMIC_DETAIL_FLAG = DynamicHttpProtocol.getDynamicDetail(mContext, dynamicId, new HttpCallBackImpl(DynamicDetailActivity.this));
    }

    // 获取用户评论请求
    private void getUserDynamicCommentList() {
        mGetCommentHistoryFlag = DynamicHttpProtocol.doCommentList(this, dynamicId, page, pageSize, new HttpCallBackImpl(DynamicDetailActivity.this));
    }

    // 处理获取动态详情的返回
    private void handleGetDynamicDetailBackNew() {
        if (dynamicDetailses != null)
            dynamicDetailses.clear();

        isMyDynamic = (back.getDynamicUser().userid == Common
                .getInstance().loginUser.getUid());


        if (back.getDynamicInfo() != null)
            back.likecount = back.getDynamicLoveInfo().total;
        back.curruserlove = back.getDynamicLoveInfo().curruserlove;
        if (back.getDynamicReviewInfo() != null)
            back.reviewcount = back.getDynamicReviewInfo().total;

        //YC 添加header空判断
        if (header == null) {
            header = DynamicDetailsHeadView.initDynamicView(mContext);
        }
        header.setDetails(true);
        header.Build(back);
        header.setBtnClickListener(btnClickListener);
        llInputBar.setVisibility(View.VISIBLE);

        if (back.getDynamicReviewInfo() != null && back.getDynamicReviewInfo().reviews != null) {
            for (DynamicReviewItem review : back.getDynamicReviewInfo().reviews) {
                DynamicDetails replyDynamic = new DynamicDetails();
                replyDynamic.itemType = DynamicDetails.TEXT_COMMENT;
                replyDynamic.itemBean = review;
                dynamicDetailses.add(replyDynamic);
            }
        }else{
            // 避免没有数据，头部视图过高展示不全
            DynamicDetails replyDynamic = new DynamicDetails();
            replyDynamic.itemType = DynamicDetails.TEXT_COMMENT;
            replyDynamic.itemBean = null;
            dynamicDetailses.add(replyDynamic);
        }
//        svDynamicContent.getRefreshableView().addHeaderView(header);

        mAdapter.updateData(dynamicDetailses);

    }

    // 处理列表传进来动态数据
    private void handleGetDynamicBackNew() {
        if (dynamicDetailses != null)
            dynamicDetailses.clear();

        isMyDynamic = (back.getDynamicUser().userid == Common
                .getInstance().loginUser.getUid());

        header.setDetails(true);
        header.Build(back);
        header.setBtnClickListener(btnClickListener);

        llInputBar.setVisibility(View.VISIBLE);

        if (back.getDynamicReviewInfo() != null && back.getDynamicReviewInfo().reviews != null) {
            for (DynamicReviewItem review : back.getDynamicReviewInfo().reviews) {
                DynamicDetails replyDynamic = new DynamicDetails();
                replyDynamic.itemType = DynamicDetails.TEXT_COMMENT;
                replyDynamic.itemBean = review;
                dynamicDetailses.add(replyDynamic);
            }
        } else {
            // 避免没有数据，头部视图过高展示不全
            DynamicDetails replyDynamic = new DynamicDetails();
            replyDynamic.itemType = DynamicDetails.TEXT_COMMENT;
            replyDynamic.itemBean = null;
            dynamicDetailses.add(replyDynamic);
        }

        mAdapter.updateData(dynamicDetailses);
    }


    /**
     * 评论
     *
     * @param reviewid
     */
    private void handleCommentBack(long reviewid) {
        DynamicReviewItem dynamicReviewItem = new DynamicReviewItem();
        dynamicReviewItem.reviewid = reviewid;
        dynamicReviewItem.datetime = TimeFormat.getCurrentTimeMillis();
        dynamicReviewItem.content = editContent.getText().toString();
        dynamicReviewItem.user = getReviewUser();
        if (!TextUtils.isEmpty(replyName)) {
            ReplyUser replyUser = new ReplyUser();
            replyUser.nickname = replyName;
            dynamicReviewItem.reply = replyUser;
        }

        DynamicDetails dynamicDetails = new DynamicDetails();
        dynamicDetails.itemType = DynamicDetails.TEXT_COMMENT;
        dynamicDetails.itemBean = dynamicReviewItem;
        dynamicDetailses.add(0, dynamicDetails);
        //YC
        mAdapter.updateData(dynamicDetailses);
        svDynamicContent.getRefreshableView().setSelection(0);
        back.reviewcount++;
        header.Build(back);
        editContent.setText("");//清除评论框内容
        resetReplyUser();
    }

    private ReviewUser getReviewUser() {
        ReviewUser reviewUser = new ReviewUser();
        Me me = Common.getInstance().loginUser;
        reviewUser.userid = me.getUid();
        reviewUser.icon = me.getIcon();
        reviewUser.vip = me.getSVip();
        reviewUser.distance = me.getDistance();
        reviewUser.horoscope = me.getHoroscopeIndex();
        reviewUser.age = me.getAge();
        reviewUser.gender = me.getGender();
        reviewUser.nickname = me.getNickname();
        return reviewUser;
    }

    // 处理删除评论的操作
    private void handleDeleteCommentBack(long reviewid) {
        for (int i = 0; i < dynamicDetailses.size(); i++) {
            DynamicDetails dynamicDetails = dynamicDetailses.get(i);
            if (dynamicDetails.itemType == DynamicDetails.TEXT_COMMENT) {
                DynamicReviewItem dynamicReviewItem = (DynamicReviewItem) dynamicDetails.itemBean;
                if (dynamicReviewItem.reviewid == reviewid) {
                    dynamicDetailses.remove(i);
                    back.reviewcount--;
                    header.Build(back);

                    mAdapter.updateData(dynamicDetailses);
                }
            }
        }

    }

    // 处理动态删除的返回
    private void handleDeleteDynamicBack() {


        Intent data = new Intent();
        data.putExtra(mDynamicIdKey, dynamicId);
        setResult(RESULT_DELETE, data);
        finish();
    }

    // 点赞按钮
    private DynamicDetailsHeadView.ItemDynamicClick btnClickListener = new DynamicDetailsHeadView.ItemDynamicClick() {

        @Override
        public void ItemGreetDynamic(DynamicItemBean dynamic) {
            if (!NotLoginDialog.getInstance(getActivity()).isLogin()) return;
            if (dynamic != null) {
                int loveType = dynamic.curruserlove;
                if (loveType == 0) // 如果当前没有点赞，则点赞
                {
                    GREET_DYNAMIC_FLAG = DynamicHttpProtocol.greetUserDynamic(mContext, dynamicId, new HttpCallBackImpl(DynamicDetailActivity.this));
                } else {
                    UNGREET_DYNAMIC_FLAG = DynamicHttpProtocol.greetCancelUserDynamic(mContext, dynamicId, new HttpCallBackImpl(DynamicDetailActivity.this));
                }
            }
        }

        @Override
        public void ItemCommentDynamic(DynamicItemBean dynamic) {
            if (!NotLoginDialog.getInstance(getActivity()).isLogin()) return;
            resetReplyUser();
            resetEditText();
            hideFaceShowKeyboard();
            showScrollViewBottom(0);
        }

    };

    //点赞成功
    private void dynamicGreet(LoverUser bean) {
        back.curruserlove = 1;
        back.isCurrentHanleView = true;

        if (back.getDynamicLoveInfo() != null) {
            if (back.getDynamicLoveInfo().loveusers == null) {
                back.getDynamicLoveInfo().loveusers = new ArrayList<LoverUser>();
            }
            back.getDynamicLoveInfo().loveusers.add(0, bean);
        }
        back.likecount++;
        header.Build(back);
    }

    //取消点赞成功
    private void dynamicCancelGreet() {
        back.curruserlove = 0;
        back.isCurrentHanleView = true;

        if (back.getDynamicLoveInfo() != null) {
            if (back.getDynamicLoveInfo().loveusers != null)
                if (back.getDynamicLoveInfo().loveusers.size() >= 1)
                    back.getDynamicLoveInfo().loveusers.remove(0);
        }
        if (back.likecount >= 0)
            back.likecount--;
        header.Build(back);
    }

    // 评论内容的点击事件
    private OnClickListener commentContentClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            DynamicReviewItem itemBean = (DynamicReviewItem) arg0.getTag(CommentItemView.COMMENT_KEY);

            if (itemBean != null) {
                long userid = itemBean.user.userid;
                long loginUserid = Long.valueOf(Common.getInstance().loginUser.getUid());//Common.getInstance().getUid()

                if (userid == loginUserid)// 该评论是自己的,只能删除
                {
                    resetEditText();
                    resetReplyUser();
                    replyuid = itemBean.user.userid;

                } else {
                    String hintText = mContext.getResources().getString(R.string.dynamic_answer_text) + "  " + itemBean.user.nickname;
                    SpannableString spContent = FaceManager.getInstance(
                            mContext).parseIconForString(editContent, mContext,
                            hintText, 12);
                    editContent.setHint(spContent);
                    editContent.setText("");
                    replyName = itemBean.user.getNickName();
                    replyuid = itemBean.user.userid;
                }

                hideFaceShowKeyboard();
            }
        }
    };

    private View.OnLongClickListener commentLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {

            DynamicReviewItem itemBean = (DynamicReviewItem) v
                    .getTag(CommentItemView.COMMENT_KEY);
            if (itemBean != null) {
                long userid = itemBean.user.userid;
                long loginUserid = Long.valueOf(Common.getInstance().loginUser.getUid());

                handleCommentClick(itemBean, userid == loginUserid);
            }

            return true;
        }
    };

    // 处理点击评论
    private void handleCommentClick(DynamicReviewItem bean, boolean isMyComment) {
        replyID = "" + bean.reviewid;
        replyContent = bean.content;

        settingDialog.show();
        settingDialog.setCopyComment(isMyComment, userId);

    }

    // 显示ScrollView的底部
    private void showScrollViewBottom(int delayMilli) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                scrollToPosition(mAdapter.getCount());
            }
        }, delayMilli);

    }

    private void scrollToPosition(int index) {
        // 1.先计算currentView的高度,要在键盘弹出之前计算,[键盘弹出后,原本显示在键盘位置的View会获取不到]
        // 2.再计算listView的高度,要在键盘弹出后计算listView的剩余高度
        if (svDynamicContent != null) {

            final int position = index
                    + svDynamicContent.getRefreshableView()
                    .getHeaderViewsCount();
            int firstIndex = svDynamicContent.getRefreshableView()
                    .getFirstVisiblePosition();
            View currentView = svDynamicContent.getRefreshableView()
                    .getChildAt(position - firstIndex);
            final int currentViewHeight = currentView == null ? 0 : currentView
                    .getHeight();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    int listHeight = svDynamicContent.getRefreshableView()
                            .getHeight();
                    int offset = listHeight - currentViewHeight;

                    svDynamicContent.getRefreshableView().setSelectionFromTop(
                            position, offset);
                }
            }, 1000);
        }
    }

    // 显示加载框
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DialogUtil.showProgressDialog(this,
                    R.string.dialog_title,
                    R.string.common_sending_progress_notice, null);
        }
        progressDialog.show();
    }

    // 隐藏加载框
    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }

    }

    private OnClickListener reportClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int type = ReportType.OTHER;
            switch ((int) view.getTag()) {
                case 0:// 色情
                    type = ReportType.SEX;
                    break;
                case 1:// 广告
                    type = ReportType.ADVERTISEMENT;
                    break;
                case 2:// 骚扰
                    type = ReportType.DISTURB;
                    break;
                case 3:// 欺诈
                    type = ReportType.CHEAT;
                    break;
            }
            reportDialog.dismiss();
            showProgressDialog();
            REPORT_DYNAMIC_FALG = DynamicHttpProtocol.doReport(mContext, String.valueOf(type), String.valueOf(dynamicId), new HttpCallBackImpl(DynamicDetailActivity.this));


        }
    };

    // 重置回复人的信息
    private void resetReplyUser() {
        replyID = "";
        replyuid = 0l;
        replyName = "";
    }

    // *******************输入框&表情栏的操作

    private void resetEditText() {
        editContent.setHint("");
    }

    private boolean isFaceMenuShow() {
        return rlFaceLayout.getHeight() > 0;
    }

    private void showFaceMenu() {

        if (chatFace == null || rlFaceLayout.getChildAt(0) == null) {
            chatFace = createChatFace();
            rlFaceLayout.addView(chatFace, 0);
        }
        chatFace.setVisibility(View.VISIBLE);
        rlFaceLayout.invalidate();
        showScrollViewBottom(0);

        inputState = 1;
    }

    /**
     * 构造和初始化表情视图
     */
    private ChatFace createChatFace() {
        ChatFace cf = new ChatFace(this, ChatFace.TYPE_NOMAL);
        cf.setIconClickListener(new IconClickListener());
        cf.initFace();

        return cf;
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideProgressDialog();
        BaseServerBean basebean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (basebean != null) {
            if (basebean.error == 9300) {
                if (userId == Common.getInstance().loginUser.getUid()) {
                    ErrorCode.showError(DynamicDetailActivity.this, result);
                    return;
                }
            }
        }
        if (GET_DYNAMIC_DETAIL_FLAG == flag) {
            svDynamicContent.onRefreshComplete();
            //获取整条动态的详情
            DynamicItemBeanParent dynamicItemBeanParent = GsonUtil.getInstance().json2Bean(result, DynamicItemBeanParent.class);
            if (dynamicItemBeanParent != null & dynamicItemBeanParent.isSuccess()) {
                back = dynamicItemBeanParent.getDynamic();
                if (back != null) {

                    handleGetDynamicDetailBackNew();
                }
            } else {
                ErrorCode.showError(DynamicDetailActivity.this, result);
            }

        } else if (GREET_DYNAMIC_FLAG == flag) {
            // 点赞动态Flag
//            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
//                    BaseServerBean.class);
//            if (bean != null && bean.isSuccess()) {
//            } else {
//                ErrorCode.showError(DynamicDetailActivity.this, result);
//            }
            LoverUser loverUser = new LoverUser();
            loverUser.icon = Common.getInstance().getIcon();
            loverUser.userid = Common.getInstance().loginUser.getUid();
            loverUser.vip = Common.getInstance().loginUser.getViplevel();
            dynamicGreet(loverUser);

        } else if (UNGREET_DYNAMIC_FLAG == flag) {
            // 取消点赞动态Flag
//            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
//                    BaseServerBean.class);
//            if (bean != null && bean.isSuccess()) {
//            } else {
//                ErrorCode.showError(DynamicDetailActivity.this, result);
//            }
            dynamicCancelGreet();
        } else if (COMMENT_FLAG == flag) {//评论
            DynamicCommentBack bean = GsonUtil.getInstance().getServerBean(result,
                    DynamicCommentBack.class);
            if (bean != null && bean.isSuccess()) {
                handleCommentBack(bean.reviewid);
            } else {
                ErrorCode.showError(DynamicDetailActivity.this, result);
            }
        } else if (RESTRICTION_DYNAMIC_FALG == flag) {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (bean == null) {
                return;
            }
            if (bean.isSuccess()) {
                CommonFunction.toastMsg(mContext, R.string.operate_success);
            }
        } else if (REPORT_DYNAMIC_FALG == flag) {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (bean == null) {
                return;
            }
            if (bean.isSuccess()) {
                CommonFunction.toastMsg(mContext,
                        R.string.report_return_content_photo);
            } else {

                REPORT_DYNAMIC_FALG = 0;
                ErrorCode.showError(mContext, result);
            }
        } else if (DELETE_DYNAMIC_FLAG == flag) {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (bean != null && bean.isSuccess()) {
                handleDeleteDynamicBack();
            } else {
                ErrorCode.showError(mContext, result);
            }
        } else if (DELETE_COMMENT_FALG == flag) {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (bean != null && bean.isSuccess()) {
                handleDeleteCommentBack(Long.valueOf(replyID == null ? "-1" : replyID));
            } else {
                ErrorCode.showError(mContext, result);
            }
        } else if (flag == mGetCommentHistoryFlag) {
            svDynamicContent.onRefreshComplete();
            DynamicReviewInfo bean = GsonUtil.getInstance().getServerBean(result, DynamicReviewInfo.class);
            if (bean != null && bean.isSuccess()) {
                if (bean != null) {
                    if (page != 1) {
                        if (bean.reviews != null) {
                            for (DynamicReviewItem dynamicReviewItem : bean.reviews) {
                                DynamicDetails dynamicDetails = new DynamicDetails();
                                dynamicDetails.itemType = DynamicDetails.TEXT_COMMENT;
                                dynamicDetails.itemBean = dynamicReviewItem;
                                dynamicDetailses.add(dynamicDetails);
                            }
                        }

                    }
                    if (bean.reviews != null) {
                        if (bean.reviews.size() >= pageSize) {
                            if (page != 1)
                                Toast.makeText(mContext, R.string.no_more_data_text,
                                        Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                mAdapter.notifyDataSetChanged();
            } else {
                ErrorCode.showError(DynamicDetailActivity.this, result);
            }
        } else if (GET_INVISIBILITY_SETTING_FLAG == flag) {
            //隐私设置
            option = GsonUtil.getInstance()
                    .getServerBean(result, InvisibilitySettingOption.class);
            if (option != null) {
                refreshMoreView();
            } else {
                ErrorCode.showError(DynamicDetailActivity.this, result);
            }
        } else if (UPDATE_INVISIBILITY_SETTING_FLAG == flag) {
            hideWaitDialog();
            //看他动态
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
            if (bean.isSuccess()) {
                if (DYNAMIC_NO_SEE_HIM == 0) {
                    DYNAMIC_NO_SEE_HIM = 1;
                } else {
                    DYNAMIC_NO_SEE_HIM = 0;
                }
                settingDialog.setSeeHimDynamic(DYNAMIC_NO_SEE_HIM);
            } else {
                ErrorCode.showError(DynamicDetailActivity.this, result);
            }
        } else if (UPDATE_INVISIBILITY_SETTING_REJECT_FLAG == flag) {
            //不让他看我动态
            hideWaitDialog();
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
            if (bean.isSuccess()) {
                if (DYNAMIC_REJECT_HIM == 0) {
                    DYNAMIC_REJECT_HIM = 1;
                } else {
                    DYNAMIC_REJECT_HIM = 0;
                }
                settingDialog.setMyDynamic(DYNAMIC_REJECT_HIM);
            } else {
                ErrorCode.showError(DynamicDetailActivity.this, result);
            }
        } else {
            ErrorCode.showError(DynamicDetailActivity.this, result);
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        hideProgressDialog();
        if (svDynamicContent != null)
            svDynamicContent.onRefreshComplete();
        ErrorCode.toastError(this, e);
    }

    static class HttpCallBackImpl implements HttpCallBack{
        private WeakReference<DynamicDetailActivity> mActivity;
        public HttpCallBackImpl(DynamicDetailActivity activity){
            mActivity = new WeakReference<DynamicDetailActivity>(activity);
        }
        @Override
        public void onGeneralSuccess(String result, long flag) {
            DynamicDetailActivity activity = mActivity.get();
            if(activity!=null){
                if(Build.VERSION.SDK_INT>=17){
                    if(activity.isDestroyed()){
                        return;
                    }
                }
                activity.onGeneralSuccess(result, flag);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            DynamicDetailActivity activity = mActivity.get();
            if(activity!=null){
                if(Build.VERSION.SDK_INT>=17){
                    if(activity.isDestroyed()){
                        return;
                    }
                }
                activity.onGeneralError(e, flag);
            }
        }
    }

    /**
     * 更新点击更多对话框的显示参数
     */
    public void refreshMoreView() {
        if (option.showmydynamic != null && option.showmydynamic != "") {
            //y看到我的动态n看不到
            if ("y".equals(option.showmydynamic)) {
                DYNAMIC_REJECT_HIM = 0;
            } else {
                DYNAMIC_REJECT_HIM = 1;
            }

        }
        if (option.showdynamic != null && option.showdynamic != "") {
            //y看他（她）的动态，n不看他（她）的动态
            if ("y".equals(option.showdynamic)) {
                DYNAMIC_NO_SEE_HIM = 0;
            } else {
                DYNAMIC_NO_SEE_HIM = 1;
            }
        }

    }

    /**
     * 表情视图：点击表情图片的事件响应
     *
     * @author chenlb
     */
    class IconClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            FaceIcon icon = (FaceIcon) view.getTag();
            if ("back".equals(icon.key)) {
                KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL);
                editContent.onKeyDown(KeyEvent.KEYCODE_DEL, keyEventDown);
            } else if (StringUtil.getLengthCN1(editContent.getText().toString()
                    .trim()) <= (mLength - FACE_TAG_NUM)) {
                // 设置表情
                CommonFunction.setFace(DynamicDetailActivity.this, editContent, icon.key,
                        icon.iconId, Integer.MAX_VALUE);
            }
        }
    }

    /**
     * 表情界面隐藏，显示键盘
     */
    @Override
    public void hideFaceShowKeyboard() {
        // 菜单处于显示状态，则隐藏菜单
        hideFaceMenu();
        if (inputState == 0 || inputState == 1) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (inputState == 0 || inputState == 1) {
                        CommonFunction.showInputMethodForQuery(DynamicDetailActivity.this,
                                editContent);
                        inputState = 2;
                    }
                }
            }, 600);
        }
    }

    private void hideFaceMenu() {
        if (chatFace != null) {
            chatFace.setVisibility(View.GONE);
        }
    }

    private void hideFaceMenuAndKeyboard() {
        inputState = 0;
        resetEditText();
        hideFaceMenu();
        hiddenKeyBoard(this);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        // 当输入Layout显示时，点击输入Layout以外的任意地方隐藏输入
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, event)) {
                hideFaceMenuAndKeyboard();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null
                && ((v instanceof EditText) || (v instanceof LinearLayout))) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                if (event.getY() < top) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 跳转到动态详情
     *
     * @param mContext
     * @param dynamicId
     * @param userId
     * @param requestCode
     * @param isShowKeyboard
     */
    public static void skipToDynamicDetail(Context mContext, android.app.Fragment fragment,
                                           String dynamicId, String userId, int requestCode, boolean isShowKeyboard) {
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(mDynamicIdKey, dynamicId);
        intent.putExtra(mUserIdKey, userId);
        intent.putExtra(mIsShowKeyboradKey, isShowKeyboard);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到动态详情
     *
     * @param mContext
     * @param dynamicId 动态的id
     */
    public static void skipToDynamicDetailAndReply(Context mContext,
                                                   long dynamicId, boolean isShowKeyboard, long userId, String userName) {
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(mDynamicIdKey, dynamicId);
        intent.putExtra(mUserIdKey, userId);
        intent.putExtra(mUserNameKey, userName);
        intent.putExtra(mIsShowKeyboradKey, isShowKeyboard);
        mContext.startActivity(intent);
    }

    public void reqMoreData() {
        GET_INVISIBILITY_SETTING_FLAG = BusinessHttpProtocol.getInvisibilitySettingInfo(mContext, userId, new HttpCallBackImpl(DynamicDetailActivity.this));
    }



}
