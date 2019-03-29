package net.iaround.ui.dynamic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.DataTag;
import net.iaround.connector.PublishManager;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.entity.type.SyncType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.DynamicLoveInfo;
import net.iaround.model.type.ChatMessageType;
import net.iaround.model.type.DynamicType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.EditTextUtil;
import net.iaround.tools.EditTextUtil.OnLimitLengthListener;
import net.iaround.tools.FaceManager.FaceIcon;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.LocationUtil.MLocationListener;
import net.iaround.tools.MapSearchIaround;
import net.iaround.tools.MapSearchIaround.SearchIaroundResultListener;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.StringUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.MapSearchIaroundActivity;
import net.iaround.ui.activity.PhotoCropActivity;
import net.iaround.ui.activity.PictureDetailsActivity;
import net.iaround.ui.activity.PreviewActivity;
import net.iaround.ui.chat.ChatFace;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.dynamic.bean.DynamicInfo;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.bean.DynamicPublishBean;
import net.iaround.ui.dynamic.bean.DynamicReviewInfo;
import net.iaround.ui.group.bean.GroupTopicPublishBean;
import net.iaround.ui.group.bean.PublishBaseBean;
import net.iaround.ui.group.bean.TopicListBean;
import net.iaround.ui.group.bean.TopicListContentBeen;
import net.iaround.ui.view.dynamic.DynamicImageLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-15 下午1:26:42
 * @Description: 发布页面(动态, 贴吧, 圈话题)
 */
public class PublishDynamicActivity extends BaseFragmentActivity implements
        OnClickListener {
    public final static int POST_BAR = 1;// 发布帖子
    public final static int GROUP_TOPIC = 2;// 发布圈话题
    public final static int DYNAMIC = 3;// 发布动态

    private final int ADDRESS_GET_PREVIEW_REQUEST = 2;// 获取地址的requestCode

    private static String PUBLISH_TYPE_KEY = "publish_type";
    private static String TARGET_ID_KEY = "target_id";
    private static String TARGET_NAME_KEY = "target_name";
    private static final String SAVE_DATA_KEY = "image_url_list";
    private static final String SAVE_EAUTIFYFLAG_KEY = "imageBeautifyFlagList";
    private int publishType;// 发布的类型
    private int titleResId;// 标题
    private PublishManager publishManager;
    private PublishBaseBean publishBean;

    private ImageView ivLeft;// 返回按钮
    private TextView tvTitle;// 标题
    private ImageView ivRight;// 发表按钮
    private FrameLayout flLeft;


    private ScrollView svContent;// 滑动背景
    private EditText etTextContent;// 文本编辑框
    private TextView tvAddress;// 地理位置
    private TextView tvCountLimit;// 字数限制的View
    private ImageView ivFaceBtn;// 表情显示的View

    private LinearLayout ivImage;// 图片列表为空显示

    private LinearLayout.LayoutParams params;//

    private RelativeLayout rlFaceLayout;// 表情显示的布局
    protected ChatFace chatFace = null;// 表情按钮对象

    private final int MAX_IMAGE_COUNT = 8;// 最多可以添加多少张图片

    private long targetId;// 发布圈话题的时候是圈id,发布贴吧动态的时候是贴吧的id
    private String targetName;//发布圈话题的时候是圈名称,发布贴吧动态的时候是贴吧的名称

    // 保存每一张照片的URL的List
    private ArrayList<String> imageUrlList = new ArrayList<String>(
            MAX_IMAGE_COUNT);

    private String addressStr;// 发布的地址

    private final int ADDRESS_GET_REQUEST = 1;// 获取地址的requestCode
    private static int FACE_TAG_NUM = 4;// 单个表情占几个长度
    private int mLength = 1000; // 限制长度
    private int inputState = 0;// 初始为0，显示表情为1，显示键盘为2

    private String strHotTopic = "";

    private Dialog loadDataProgressDialog;// 加载栏
    private Dialog publishProgressDialog;// 发送中等待进度
    private final int UPDATE_IMAGE_LAYOUT_FALG = 0XFF;
    private final int FINISH_ACTIVITY_FALG = 0XEF;
    private final int SAVE_IMAGE_ERROR_FALG = 0XDF;
    private PublishHandler mHandler = new PublishHandler();

    MapSearchIaround searchGetAddress;// 获取地址的类
    SearchIaroundResultListener listener;// 返回获取地址结果的监听器


    /**
     * 跳转到动态发布界面
     *
     * @param mContext
     * @PublishDynamicActivity DYNAMIC发布动态, POST_BAR发布帖子, GROUP_TOPIC发布圈话题
     */
    public static void skipToPublishDynamicActivity(Context mContext) {

        Intent intent = new Intent();
        intent.setClass(mContext, PublishDynamicActivity.class);
        intent.putExtra(PUBLISH_TYPE_KEY, DYNAMIC);
        intent.putExtra(TARGET_ID_KEY, 0l);
        mContext.startActivity(intent);
    }

    /**
     * 跳转到动态发布界面
     *
     * @param mContext
     * @PublishDynamicActivity DYNAMIC发布动态, POST_BAR发布帖子, GROUP_TOPIC发布圈话题
     */
    public static void skipToPublishDynamicActivity(Context mContext,
                                                    int requestCode) {

        Intent intent = new Intent();
        intent.setClass(mContext, PublishDynamicActivity.class);
        intent.putExtra(PUBLISH_TYPE_KEY, DYNAMIC);
        intent.putExtra(TARGET_ID_KEY, 0l);
        ((Activity) mContext).startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到动态发布界面
     *
     * @param mContext
     * @PublishDynamicActivity DYNAMIC发布动态, POST_BAR发布帖子, GROUP_TOPIC发布圈话题
     */
    public static void skipToPublishDynamicActivity(Context mContext, Fragment fragment,
                                                    int requestCode) {

        Intent intent = new Intent();
        intent.setClass(mContext, PublishDynamicActivity.class);
        intent.putExtra(PUBLISH_TYPE_KEY, DYNAMIC);
        intent.putExtra(TARGET_ID_KEY, 0l);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void skipToPublishDynamicActivity(Context mContext, Activity fragment,
                                                    int requestCode) {

        Intent intent = new Intent();
        intent.setClass(mContext, PublishDynamicActivity.class);
        intent.putExtra(PUBLISH_TYPE_KEY, DYNAMIC);
        intent.putExtra(TARGET_ID_KEY, 0l);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到圈话题发布界面
     *
     * @param mContext
     * @PublishDynamicActivity DYNAMIC发布动态, POST_BAR发布帖子, GROUP_TOPIC发布圈话题
     */
    public static void skipToPublishGroupTopicActivity(Context mContext,
                                                       long groupId, int requestCode) {

        Intent intent = new Intent();
        intent.setClass(mContext, PublishDynamicActivity.class);
        intent.putExtra(PUBLISH_TYPE_KEY, GROUP_TOPIC);
        intent.putExtra(TARGET_ID_KEY, groupId);
        ((Activity) mContext).startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到圈话题发布界面
     *
     * @param mContext
     * @PublishDynamicActivity DYNAMIC发布动态, POST_BAR发布帖子, GROUP_TOPIC发布圈话题
     */
    public static void skipToPublishGroupTopicActivity(Context mContext,
                                                       long groupId, String hotTopic, int requestCode) {

        Intent intent = new Intent();
        intent.setClass(mContext, PublishDynamicActivity.class);
        intent.putExtra(PUBLISH_TYPE_KEY, GROUP_TOPIC);
        intent.putExtra("HOT_TOPIC", hotTopic);
        intent.putExtra(TARGET_ID_KEY, groupId);
        ((Activity) mContext).startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到贴吧发布界面
     *
     * @param mContext
     * @PublishDynamicActivity DYNAMIC发布动态, POST_BAR发布帖子, GROUP_TOPIC发布圈话题
     */
    public static void skipToPublishPostBarActivity(Context mContext,
                                                    long postbarId, String postbarName) {

        Intent intent = new Intent();
        intent.setClass(mContext, PublishDynamicActivity.class);
        intent.putExtra(PUBLISH_TYPE_KEY, POST_BAR);
        intent.putExtra(TARGET_ID_KEY, postbarId);
        intent.putExtra(TARGET_NAME_KEY, postbarName);
        mContext.startActivity(intent);
    }

    private class PublishHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_IMAGE_LAYOUT_FALG:
                    hideProgressDialog();
                    initImageLayout();
                    displayPublishBtn();
                    break;
                case FINISH_ACTIVITY_FALG:
                    // hidePublishProgressDialog();
                    setResult(RESULT_OK);
                    finish();
                    break;
                case SAVE_IMAGE_ERROR_FALG:
                    CommonFunction.toastMsg(mContext, getString(R.string.save_error));
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_publish);
        mContext = this;
        Intent intent = getIntent();
        publishType = intent.getIntExtra(PUBLISH_TYPE_KEY, DYNAMIC);
        targetId = intent.getLongExtra(TARGET_ID_KEY, 0l);
        targetName = intent.getStringExtra(TARGET_NAME_KEY);
        strHotTopic = intent.getStringExtra("HOT_TOPIC");
        initPublishType();

        initView();
        // 即时更新位置
        new LocationUtil(this).startListener(
                new MLocationListener() {

                    public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {
                        LocationUtil.setGeo(mContext, lat, lng, address);
                        initGetAddressComponent();// 获取当前地址
                    }
                }, 1);


    }

    // 获取发布的类型
    private void initPublishType() {
        // 获取发布的类型.初始化title和publishManager
        publishManager = PublishManager.create(mContext, publishType);
        switch (publishType) {
            case DYNAMIC:
                titleResId = R.string.dynamic_publish_title;
                break;
            case POST_BAR:
                titleResId = R.string.post_bar_publish_title;
                break;
            case GROUP_TOPIC:
                titleResId = R.string.group_topic_publish_title;
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存图像的list
        outState.putStringArrayList(SAVE_DATA_KEY, imageUrlList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {

        super.onRestoreInstanceState(outState);
        imageUrlList.clear();
        imageUrlList.addAll(outState.getStringArrayList(SAVE_DATA_KEY));
    }

    // 初始化获取地址的组件
    private void initGetAddressComponent() {
        searchGetAddress = new MapSearchIaround();
        searchGetAddress.doSearchIaround(this, 1, "");

        listener = new SearchIaroundResultListener() {

            @Override
            public void onSearchResulted(List<GeoData> geoDatas) {

                if (geoDatas != null && geoDatas.size() > 0) {
                    addressStr = geoDatas.get(0).getSimpleAddress();
                    tvAddress.setText(addressStr);
                }

            }
        };
        searchGetAddress.setSearchIaroundResult(listener);
    }

    // 初始化布局变量
    private void initView() {
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivRight = (ImageView) findViewById(R.id.iv_right);

        svContent = (ScrollView) findViewById(R.id.svContent);
        etTextContent = (EditText) findViewById(R.id.etTextContent);
        etTextContent.setText(strHotTopic);

        etTextContent.setSelection(etTextContent.getText().length());
        etTextContent.requestFocus();
        // 限制字数输入
        EditTextUtil.autoLimitLength(etTextContent, mLength,
                new OnLimitLengthListener() {

                    @Override
                    public void limit(long limitCount, long overCount) {
//                        displayPublishBtn();

                        updateFontNum((int) overCount);

                    }
                });

        ScrollView llInputContent = (ScrollView) findViewById(R.id.llInputContent);
        llInputContent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                etTextContent.dispatchTouchEvent(arg1);
                return false;
            }
        });

        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvCountLimit = (TextView) findViewById(R.id.tvCountLimit);
        ivFaceBtn = (ImageView) findViewById(R.id.ivFace);
        ivImage = (LinearLayout) findViewById(R.id.ly_add);

        ivImage.setOnClickListener(new AddBtnClickListener());

        rlFaceLayout = (RelativeLayout) findViewById(R.id.rlFaceLayout);
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(mActivity);
        if (sp.has(SharedPreferenceUtil.SWITCH)) {
            String currentItemStatus = sp
                    .getString(SharedPreferenceUtil.SWITCH);
            try {
                if (currentItemStatus.length() >= 2) {
                    if (currentItemStatus.charAt(0) == '0'
                            && currentItemStatus.charAt(1) == '0') {
                        tvAddress.setVisibility(View.GONE);
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(22, 22, 22, 0);

        initActionBar();
        initImageLayout();
        initAddressLayout();
        initEditTextlayout();
//        initSyncBar();
    }

    // 初始化标题布局
    private void initActionBar() {
        flLeft.setOnClickListener(this);

        ivLeft.setImageResource(R.drawable.title_back);
        String title = getResString(titleResId);
        tvTitle.setText(title);
//        tvRight.setText(R.string.dynamic_publish_btn);
        ivRight.setEnabled(false);

//        tvRight.setTextColor(getResources().getColor(R.color.c_ccffffff));
//        tvRight.setPadding(3, 3, 3, 3);
        ivRight.setImageResource(R.drawable.icon_publish_normal);

        findViewById(R.id.fl_left).setOnClickListener(this);
        ivLeft.setOnClickListener(this);
    }

    // 初始化照片布局
    private void initImageLayout() {
        DynamicImageLayout layout = DynamicImageLayout.initDynamicImage(this);
        LinearLayout llFirstRow = (LinearLayout) findViewById(R.id.llFirstRow);
        if (imageUrlList.size() >= 8) {
            ivImage.setVisibility(View.INVISIBLE);
        } else {
            ivImage.setVisibility(View.VISIBLE);
        }
        /**
         * 展示发布按钮
         */
        displayPublishBtn();
        layout.buildSDImage(llFirstRow, imageUrlList);

//        svContent.postInvalidate();

        layout.setImageListenter(new DynamicImageLayout.ImageListenter() {
            @Override
            public void imageList(int position) {
                /**
                 * 跳转到图片浏览界面
                 */
//                Intent i = new Intent(PublishDynamicActivity.this, PreviewActivity.class);
//                //Intent i = new Intent(PublishDynamicActivity.this, PictureDetailsActivity.class);
//                i.putExtra("position", position);
//                i.putExtra("dataTag", DataTag.DYNAMIC_PUBLISH_IMAGE);
//                if (imageUrlList != null && imageUrlList.size() > 0) {
//                    i.putExtra("smallPhotos", imageUrlList);
//                }
//                startActivityForResult(i, ADDRESS_GET_PREVIEW_REQUEST);
                ArrayList<String> pictureDetails = new ArrayList<String>();
                for (String picture : imageUrlList){
                    pictureDetails.add(picture.replace(PathUtil.getFILEPrefix(),""));
                }
                PictureDetailsActivity.launch(PublishDynamicActivity.this,pictureDetails,position);
            }
        });

    }

    // 初始化地址
    private void initAddressLayout() {

        addressStr = getResString(R.string.dynamic_address_default);
        tvAddress.setText(addressStr);
        tvAddress.setOnClickListener(this);

        ivFaceBtn.setOnClickListener(this);

        int count = StringUtil.getLengthCN1(tvCountLimit.getText().toString());
        updateFontNum(count);
    }

    // 初始化文本输入框
    private void initEditTextlayout() {
        etTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

//                displayPublishBtn();

                updateFontNum(StringUtil.getLengthCN1(arg0.toString().trim()));
                CommonFunction.replaceBlank(arg0.toString());

            }
        });

        // 处于TextView的点击域时,直接消费touch事件,不传递给ScrollView
        etTextContent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                arg0.getParent().getParent()
                        .requestDisallowInterceptTouchEvent(true);
                switch (arg1.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        hideFaceShowKeyboard();
                        arg0.performClick();
                        arg0.getParent().getParent()
                                .requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        hideFaceShowKeyboard();
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.equals(ivLeft) || arg0.equals(flLeft)) {
            quitPublish();
        } else if (arg0.equals(ivRight)) {
            publish();

        } else if (arg0.equals(tvAddress)) {
            // 跳转到选择地址页面
            Intent intent = new Intent(this, MapSearchIaroundActivity.class);//yuchao 将mContext 修改为this
            intent.putExtra(MapSearchIaroundActivity.ADDRESS_NAME_KEY, addressStr);
            startActivityForResult(intent, ADDRESS_GET_REQUEST);

        } else if (arg0.equals(ivFaceBtn)) {
            if (!isFaceMenuShow()) {
                CommonFunction.hideInputMethod(mActivity, etTextContent);
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
    }


    // 发布
    private synchronized void publish() {

        if (CommonFunction.forbidSay(mActivity)) {
            return;
        }

        ivRight.setEnabled(false);
        PublishBaseBean bean = new PublishBaseBean();
        // 过滤多余的换行
        String feedLine = "\n";
        bean.datetime = TimeFormat.getCurrentTimeMillis();
        String editCon = etTextContent.getText().toString();
        editCon = CommonFunction.filterKeyWordAndReplaceEmoji(mContext, editCon);
        String contentStr = CommonFunction.replaceLineFeed(editCon);
        if (contentStr.startsWith(feedLine)) {
            contentStr = contentStr.substring(feedLine.length());
        }

        if (contentStr.endsWith(feedLine)) {
            contentStr = contentStr.substring(0, contentStr.length() - feedLine.length());
        }

        bean.setContent(contentStr);
        ArrayList<String> updateImgs = new ArrayList<>();
        for (String outputPath : imageUrlList) {
            String url = outputPath.contains(PathUtil.getFILEPrefix()) ? outputPath.replace(PathUtil.getFILEPrefix(), "") : outputPath;
            updateImgs.add(url);
        }
        bean.setPhotoList(updateImgs);
        String address = addressStr
                .equals(getResString(R.string.dynamic_address_default)) ? ""
                : addressStr;
        bean.setAddress(address);

//        showPublishProgressDialog();

        if (publishType == DYNAMIC) {
            publishBean = initDynamicBean(bean);
            insertBufferData();
        } else if (publishType == POST_BAR) {
//            publishBean = initPostBarBean(bean);
//            savePostbarTopicData();
//            ((PostBarPublishManager) publishManager).POSTBAR_ID = targetId;//jiqiang
        } else if (publishType == GROUP_TOPIC) {
            publishBean = initGroupTopicBean(bean);
        }

        publishManager.addTask(publishBean);

        Message msg = new Message();
        msg.what = FINISH_ACTIVITY_FALG;
        mHandler.sendMessageDelayed(msg, 500);
    }

    private void insertBufferData() {
        // DynamicItemBean组装
        DynamicItemBean itemBean = initDynamicItemBean(publishBean);
        DynamicModel.getInstent().addUnSendSuccessList(itemBean);
    }

    private DynamicItemBean initDynamicItemBean(PublishBaseBean publishInfo) {
        DynamicItemBean bean = new DynamicItemBean();
        bean.initDynamicUser(Common.getInstance().loginUser);
        DynamicInfo info = new DynamicInfo();
        info.datetime = publishInfo.datetime;
        info.dynamicid = publishInfo.datetime;
        info.userid = bean.getDynamicUser().userid;
        info.setContent(publishInfo.getContent());
        info.type = DynamicType.IMAGE_TEXT;
        info.setPhotoList(publishInfo.getPhotoList());
        info.distance = 0;
        info.dynamiccategory = 3;
        info.dynamicsource = 2;
        info.setAddress(publishInfo.getAddress());
        if (!TextUtils.isEmpty(publishInfo.getSyncvalue()))// 如果有同步出去的值,标记是同步出去的
        {
            info.synctype = SyncType.SYNC_OUT;
        }

        bean.setDynamicInfo(info);
        bean.setSendStatus(DynamicItemBean.SENDDING);
        bean.setPublishInfo((DynamicPublishBean) publishInfo);
        bean.setDynamicLoveInfo(new DynamicLoveInfo());
        bean.setDynamicReviewInfo(new DynamicReviewInfo());

        return bean;
    }

    class AddBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            requestCamera();
        }
    }

    @Override
    public void doCamera() {
        super.doCamera();
        // 跳转到选择图片的界面
        showPicPickMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            /*if (CommonFunction.TAKE_PHOTO_REQ == requestCode) {

                if (data != null) {
                    final String path = data
                            .getStringExtra(PictureMultiSelectActivity.FILE_PATH);

                    showProgressDialog();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            rotaingImage(path);
                            mHandler.sendEmptyMessage(UPDATE_IMAGE_LAYOUT_FALG);
                        }
                    }).start();
                }

            } else if (CommonFunction.PICK_PHOTO_REQ == requestCode) {
                //选择图片后的集合
                final ArrayList<String> list = data
                        .getStringArrayListExtra(PictureMultiSelectActivity.PATH_LIST);

                showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < list.size(); i++) {
                            rotaingImage(list.get(i));
                            imageBeautifyFlagList.add(0);
                        }
                        mHandler.sendEmptyMessage(UPDATE_IMAGE_LAYOUT_FALG);
                    }
                }).start();
            } else*/ if (ADDRESS_GET_REQUEST == requestCode) {
                // 选取地址成功
                if (data != null) {
                    addressStr = data.getStringExtra("Name");
                    if (CommonFunction.isEmptyOrNullStr(addressStr)) {
                        addressStr = getResString(R.string.dynamic_address_default);
                    }
                    tvAddress.setText(addressStr);
                }
            } else if (ADDRESS_GET_PREVIEW_REQUEST == requestCode) {
                imageUrlList = data.getStringArrayListExtra("dynamic_list");

                initImageLayout();

            }
        }
    }

    // 处理图片旋转的问题
    private void rotaingImage(String path) {
        String outputPath = PathUtil.getImageRotatePath(path);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            int degree = PhotoCropActivity.readPictureDegree(path);
            if (degree == 0) {
                outputPath = path;

            } else {
                File outputFile = new File(outputPath);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    bitmap = PhotoCropActivity.rotaingImageView(degree, bitmap);
                    FileOutputStream os = new FileOutputStream(outputFile);
                    bitmap.compress(CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                }
            }
            //create一个小图
            File thumbFile = new File(CommonFunction.thumPicture(outputPath));
            Bitmap thumbBitmap = CommonFunction.centerSquareScaleBitmap(bitmap, 136);
            FileOutputStream thumbOs = new FileOutputStream(thumbFile);
            thumbBitmap.compress(CompressFormat.JPEG, 100, thumbOs);
            thumbOs.flush();
            thumbOs.close();

        } catch (Exception e) {
            e.printStackTrace();
            outputPath = path;
        }
        String url = outputPath.contains(PathUtil.getFILEPrefix()) ? outputPath : PathUtil.getFILEPrefix() + outputPath;
        imageUrlList.add(url);
    }

    // 显示加载框
    private void showProgressDialog() {
        if (loadDataProgressDialog == null) {
            loadDataProgressDialog = DialogUtil.showProgressDialog(this,
                    R.string.dialog_title, R.string.content_is_loading, null);
            loadDataProgressDialog.setCancelable(false);
        }

        loadDataProgressDialog.show();
    }

    // 隐藏加载框
    private void hideProgressDialog() {
        if (loadDataProgressDialog != null) {
            loadDataProgressDialog.hide();
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (loadDataProgressDialog != null) {
            loadDataProgressDialog.dismiss();
            loadDataProgressDialog = null;
        }

        if (publishProgressDialog != null) {
            publishProgressDialog.dismiss();
            publishProgressDialog = null;
        }

    }

    // 根据需求展示发布按钮的形态
    private void displayPublishBtn() {
        final String str = etTextContent.getText().toString();
        runOnUiThread(new Runnable() {
            public void run() {
                if ((imageUrlList == null || imageUrlList.size() == 0)) {
                    ivRight.setEnabled(false);
                    ivRight.setOnClickListener(null);
                    ivRight.setImageResource(R.drawable.icon_publish_normal);
//                    tvRight.setTextColor(getResources().getColor(
//                            R.color.c_ccffffff));
                } else {
                    ivRight.setEnabled(true);
                    ivRight.setOnClickListener(PublishDynamicActivity.this);
//                    tvRight.setTextColor(getResources().getColor(R.color.white));
                    ivRight.setImageResource(R.drawable.icon_publish);
                }
                ivRight.invalidate();
            }
        });
    }

    private final int REQUEST_CODE_GALLERY = 1001;

    // 显示照片选取的菜单
    private void showPicPickMenu() {
        CommonFunction.hideInputMethod(this, etTextContent);
        int count = MAX_IMAGE_COUNT - imageUrlList.size();
//        PictureMultiSelectActivity.skipToPictureMultiSelectAlbum(this,
//                CommonFunction.PICK_PHOTO_REQ, count);//yuchao 将mContext修改为this

        ArrayList<String> mBeautifyPhotoIds = new ArrayList<>();
        for (String str : imageUrlList){
            mBeautifyPhotoIds.add(str.replace(PathUtil.getFILEPrefix(),""));
        }
        GalleryUtils.getInstance().openGalleryMuti(this,REQUEST_CODE_GALLERY,MAX_IMAGE_COUNT,mBeautifyPhotoIds,mOnHanlderResultCallback);

    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                showProgressDialog();
                final List<String> list = new ArrayList<>();
                for (PhotoInfo photoInfo : resultList){
                    list.add(photoInfo.getPhotoPath());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        imageUrlList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            rotaingImage(list.get(i));
                        }
                        mHandler.sendEmptyMessageDelayed(UPDATE_IMAGE_LAYOUT_FALG,100);
                    }
                }).start();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(PublishDynamicActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    // 如果用户输入了动态内容,就需要提示是否不保存
    private void quitPublish() {
        String content = etTextContent.getText().toString().trim();
        if (TextUtils.isEmpty(content) && imageUrlList.size() == 0) {
            finish();
        } else {
            DialogUtil.showOKCancelDialog(this, R.string.prompt,
                    R.string.dynamic_publish_quit_title,
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            finish();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        quitPublish();
    }

    private boolean isFaceMenuShow() {
        return rlFaceLayout.getHeight() > 0;
    }

    private void showFaceMenu() {

        if (chatFace == null || rlFaceLayout.getChildAt(0) == null) {
            chatFace = createChatFace();
            rlFaceLayout.addView(chatFace, 0);
        }
        ivFaceBtn.setImageResource(R.drawable.iaround_chat_keyborad);
        chatFace.setVisibility(View.VISIBLE);
        rlFaceLayout.invalidate();
        inputState = 1;
    }

    /**
     * 构造和初始化表情视图
     */
    private ChatFace createChatFace() {
        ChatFace cf = new ChatFace(this, ChatFace.TYPE_NOMAL);
        cf.setKeyboardClickListener(new KeyboardClickListener());
        cf.setIconClickListener(new IconClickListener());
        cf.initFace();

        return cf;
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
                etTextContent.onKeyDown(KeyEvent.KEYCODE_DEL, keyEventDown);
            } else if (StringUtil.getLengthCN1(etTextContent.getText()
                    .toString().trim()) <= (mLength - FACE_TAG_NUM)) {
                // 设置表情
                CommonFunction.setFace(mActivity, etTextContent, icon.key,
                        icon.iconId, Integer.MAX_VALUE);
            }
        }
    }

    /**
     * 表情视图：切换到键盘的事件响应
     *
     * @author chenlb
     */
    class KeyboardClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            hideFaceShowKeyboard();
        }
    }

    private void updateFontNum(int num) {
        tvCountLimit.setText(Html.fromHtml(num + "/" + mLength));
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
                        CommonFunction.showInputMethodForQuery(mActivity,
                                etTextContent);
                        inputState = 2;
                    }
                }
            }, 200);
        }
    }

    private void hideFaceMenu() {
        if (chatFace != null) {
            ivFaceBtn.setImageResource(R.drawable.iaround_chat_face);
            chatFace.setVisibility(View.GONE);
        }
    }

    /**
     * PublishBaseBean 转 DynamicPublishBean
     */
    private DynamicPublishBean initDynamicBean(PublishBaseBean info) {
        DynamicPublishBean dynamicPublishBean = new DynamicPublishBean();
        dynamicPublishBean.datetime = info.datetime;
        dynamicPublishBean.dynamicid = info.datetime;// 发布前,用时间来作为id
        dynamicPublishBean.type = DynamicType.IMAGE_TEXT;
        dynamicPublishBean.setTitle("");
        dynamicPublishBean.setUrl("");

        dynamicPublishBean.setContent(info.getContent());
        dynamicPublishBean.setPhotoList(info.getPhotoList());
        dynamicPublishBean.setAddress(info.getAddress());

        GeoData geoData = LocationUtil.getCurrentGeo(mContext);
        dynamicPublishBean.setShortaddress(geoData.getProvince()
                + geoData.getCity());

        ArrayList<Integer> shareList = new ArrayList<Integer>();
        dynamicPublishBean.setShareList(shareList);
        return dynamicPublishBean;
    }

    private GroupTopicPublishBean initGroupTopicBean(PublishBaseBean info) {
        GroupTopicPublishBean groupTopicBean = new GroupTopicPublishBean();
        groupTopicBean.groupid = targetId;
        if (info.getPhotoList().size() > 0)// 有图片类型就为图片,否则就为文字
        {
            groupTopicBean.type = ChatMessageType.IMAGE;
        } else {
            groupTopicBean.type = ChatMessageType.TEXT;
        }
        groupTopicBean.plat = Config.PLAT;
        groupTopicBean.topic_index = info.datetime;
        groupTopicBean.datetime = info.datetime;
        groupTopicBean.setContent(info.getContent());
        groupTopicBean.setPhotoList(info.getPhotoList());
        groupTopicBean.setAddress(info.getAddress());

        GeoData geoData = LocationUtil.getCurrentGeo(mContext);
        groupTopicBean.setShortaddress(geoData.getProvince()
                + geoData.getCity());

        GroupTopicSavePublishBeen(groupTopicBean);
        ArrayList<Integer> shareList = new ArrayList<Integer>();
        groupTopicBean.setShareList(shareList);

        return groupTopicBean;
    }

    private void GroupTopicSavePublishBeen(GroupTopicPublishBean been) {
        TopicListContentBeen topicContentBeen = new TopicListContentBeen();

        topicContentBeen.setSendTopicBeen(been);

        ArrayList<TopicListContentBeen> topicList = new ArrayList<TopicListContentBeen>();
        TopicListBean topic;
        String key = SharedPreferenceCache.TOPIC_SENDING_LIST
                + String.valueOf(been.groupid)
                + Common.getInstance().loginUser.getUid();

        String listStr = SharedPreferenceCache.getInstance(mContext).getString(
                key);
        if (!TextUtils.isEmpty(listStr)) {
            topic = GsonUtil.getInstance().getServerBean(
                    listStr, TopicListBean.class);
        } else {
            topic = new TopicListBean();
        }

        topicList.add(topicContentBeen);

        if (topic.topics != null) {
            topicList.addAll(topic.topics);
            topic.topics.clear();
        }

        topic.topics = topicList;

        String topicInfo = GsonUtil.getInstance()
                .getStringFromJsonObject(topic);

        SharedPreferenceCache.getInstance(mContext).putString(key, topicInfo);

    }

    public void finish() {

        super.finish();
        try {
            CommonFunction.hideInputMethod(mContext, etTextContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResString(int id) {
        return getResources().getString(id);
    }


}
