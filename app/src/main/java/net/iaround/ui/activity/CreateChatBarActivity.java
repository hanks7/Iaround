package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.FileUploadManager;
import net.iaround.connector.UploadFileCallback;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.editpage.EditNicknameActivity;
import net.iaround.ui.activity.editpage.EditSignatureActivity;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.dynamic.PublishDynamicActivity;
import net.iaround.ui.group.activity.CreateGroupActivity;
import net.iaround.ui.group.bean.CreateGroupInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * @ClassName: CreateChatbarActivity
 * @Description: 创建聊吧
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-11 下午7:41:34
 *
 */

public class CreateChatBarActivity extends SuperActivity implements View.OnClickListener,UploadFileCallback, LocationUtil.MLocationListener{

    /**选择聊吧头像*/
    private ImageView mIvSelectIcon;
    /**聊吧名称*/
    private TextView mTvChatbarName;
    /**聊吧中心*/
    private TextView mTvChatbarPosition;
    /**聊吧介绍*/
    private TextView mTvChatbarContent;
    /**创建聊吧*/
    private Button mBtnCreateChatBar;
    /**选择聊吧图片*/
    private final int REQUEST_CODE_SEL_ROOM_ICON = 1001;
    /**修改吧名*/
    private final int REQUEST_CHATBAR_NAME = 1002;
    /** 选择圈中心 */
    private final int REQUEST_CODE_SEL_ROOM_CENTER = 1003;
    /**修改吧介绍*/
    private final int REQUEST_CHATBAR_CONTENT = 1004;
    /**
     * 上传聊天室图标状态：未上传
     */
    private final int UPLOAD_STATUS_NO = 1;
    /**
     * 上传聊天室图标状态：上传中
     */
    private final int UPLOAD_STATUS_ING = 2;
    /**
     * 上传聊天室图标状态：上传完毕
     */
    private final int UPLOAD_STATUS_FINISHED = 3;
    /**
     * 上传聊天室图标状态：上传失败
     */
    private final int UPLOAD_STATUS_FAIL = 4;
    /**
     * 上传聊天室图标的状态
     */
    private int uploadRoomIconStatus = UPLOAD_STATUS_NO;
    /** 上传圈图的flag */
    public static long UPLOAD_GROUPIMG_FLAG;
    /**
     * 已上传成功的圈图地址
     */
    private String roomIconUrl = "";
    /**标题栏*/
    private TextView mTvTitle;
    /**创建聊吧flag*/
    private static long CREATE_GROUP_FLAG;
    /**聊吧信息*/
    CreateGroupInfo mGroupInfo;

    private GeoData mGeoData;

    private int lat;
    private int lng;
    private String address;
    private String city;



    private String mCenterId = "";
    private String mCenterName = "";
    private int mCenterLat = 0;
    private int mCenterLng = 0;
    private String chatbarName = "";
    private String content = "";
    private int diamondnum = 0;
    private int gold = 0;
    private String newAddress;
    private String bmPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_bar);
        mContext = CreateChatBarActivity.this;
        initView();
        initData();
        initListener();
    }

    private void initView()
    {
        mBtnCreateChatBar = (Button) findViewById(R.id.btn_create_chatbar);
        mIvSelectIcon = (ImageView) findViewById(R.id.iv_selected_picture);
        mTvChatbarName = (TextView) findViewById(R.id.tv_charbar_name);
        mTvChatbarPosition = (TextView) findViewById(R.id.tv_charbar_position);
        mTvChatbarContent = (TextView) findViewById(R.id.tv_chatbar_content);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
    }
    private void initListener()
    {
        mBtnCreateChatBar.setOnClickListener(this);
        mIvSelectIcon.setOnClickListener(this);
        mTvChatbarName.setOnClickListener(this);
        mTvChatbarPosition.setOnClickListener(this);
        mTvChatbarContent.setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.fl_back).setOnClickListener(this);
        findViewById(R.id.ll_charbar_name).setOnClickListener(this);
        findViewById(R.id.ll_charbar_position).setOnClickListener(this);
        findViewById(R.id.ll_chatbar_content).setOnClickListener(this);
    }
    private void initData()
    {
        mTvTitle.setText(getResString(R.string.chat_bar_create));

        GeoData geo = LocationUtil.getCurrentGeo(mContext);
        if (LocationUtil.getCurrentGeo(mContext) != null && geo.getLat() != 0
                && geo.getLng() != 0) {
            lat = geo.getLat();
            lng = geo.getLng();
            address = geo.getAddress();
            city = geo.getCity();
        }

        updateLocation(1,lat,lng,"","");

        mGroupInfo = new CreateGroupInfo( );
        mGroupInfo.groupType = getIntent().getIntExtra("group_type", 0);
        mGroupInfo.diamondCost = getIntent().getIntExtra("diamond_cost", 0);
        diamondnum = getIntent().getIntExtra("diamondnum",0);
        gold = getIntent().getIntExtra("gold",0);

        mTvChatbarPosition.setText(city + getResources().getString(R.string.city));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_selected_picture:
                //选择头像
                requestCamera();
                break;
            case R.id.ll_charbar_name:
            case R.id.tv_charbar_name:
                intent.setClass(this, EditNicknameActivity.class);
                intent.putExtra(Constants.INFO_CONTENT, mTvChatbarName.getText().toString());
                intent.putExtra("isFrom", 0);
                startActivityForResult(intent, REQUEST_CHATBAR_NAME);
                //设置吧名
                break;
            case R.id.ll_charbar_position:
            case R.id.tv_charbar_position:
                break;
            case R.id.ll_chatbar_content:
            case R.id.tv_chatbar_content://设置吧介绍
                intent.setClass(this, EditSignatureActivity.class);
                intent.putExtra(Constants.INFO_CONTENT, mTvChatbarContent.getText().toString());//EDIT_RETURN_INFO
                intent.putExtra("isFrom", 1);
                startActivityForResult(intent, REQUEST_CHATBAR_CONTENT);
                break;
            case R.id.fl_left:
            case R.id.iv_left:
            case R.id.fl_back:
                finish();
                break;
            case R.id.btn_create_chatbar://创建聊吧
                // TODO: 2017/6/15 创建聊吧  判断icon是否为空，昵称，介绍是否为空，这些都是必填项
                //聊吧昵称是否为空
                if ("".equals(chatbarName)|| chatbarName == null || "null".equals(chatbarName)) {
                    Toast.makeText(CreateChatBarActivity.this, R.string.createa_chatbar_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }else if (chatbarName.length() < 2)
                {
                    Toast.makeText(CreateChatBarActivity.this,R.string.createa_chatbar_name_length, Toast.LENGTH_SHORT).show();
                    return;
                }
                //聊吧介绍是否为空
                if ("".equals(content) || content == null || "null".equals(content)) {
                    Toast.makeText(CreateChatBarActivity.this,R.string.createa_chatbar_content_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断金币是否充足
                if ( gold < 5000 && gold >= 0)
                {
                    String note = getResString(R.string.create_chat_bar_have_no_gold);
                    DialogUtil.showOKCancelDialog(this ,
                            "" , note ,
                            new View.OnClickListener( )
                            {
                                @Override
                                public void onClick( View v )
                                {
                                    // TODO: 2017/6/15 金币不足，跳转到h5兑换金币
                                    String url = Config.getGoldDescUrlNew(CommonFunction.getLang(mContext));
                                    Intent i = new Intent(mContext, WebViewAvtivity.class);
                                    i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
                                    startActivity(i);
                                }
                            } );
                    return;
                }
                CREATE_GROUP_FLAG = GroupHttpProtocol.createGroup_5_3( mContext ,
                        1, chatbarName ,roomIconUrl, "20" ,String.valueOf(lat),
                        String.valueOf(lng) ,address , mGroupInfo.groupRang ,content , mCenterId ,
                        city,String.valueOf(lat) , String.valueOf(lng), this );
                if ( CREATE_GROUP_FLAG < 0 )
                {
                    onGeneralError( 107 , CREATE_GROUP_FLAG );
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHATBAR_NAME://获取圈名字
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    chatbarName = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (chatbarName != null || !"".equals(chatbarName) || "null".equals(chatbarName))
                    {
                        mTvChatbarName.setText(chatbarName);
                    }
                }
                break;
            case REQUEST_CHATBAR_CONTENT://圈介绍
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    content = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (content != null || !"".equals(content) || "null".equals(content))
                    {
                        mTvChatbarContent.setText(content);
                    }
                }
                break;
            case REQUEST_CODE_SEL_ROOM_CENTER://圈中心
            default:
                break;
        }

    }
    /**
     * @Title: handleUploadGroupIconFail
     * @Description: 处理圈图上传失败
     */
    private void handleUploadGroupIconFail() {

        uploadRoomIconStatus = UPLOAD_STATUS_FAIL;
        CommonFunction.toastMsg(this, R.string.upload_fail);
    }
    /**
     * @Title: handleUploadGroupIconSuccess
     * @Description: 圈图上传成功
     */
    private void handleUploadGroupIconSuccess(String result) {
        Map<String, Object> map = null;
        map = JsonUtil.jsonToMap(result);
        roomIconUrl = String.valueOf(map.get("url"));
        if (CommonFunction.isEmptyOrNullStr(roomIconUrl)) {
            handleUploadGroupIconFail();
            return;
        }
        uploadRoomIconStatus = UPLOAD_STATUS_FINISHED;
        CommonFunction.toastMsg(this, R.string.upload_complete);
    }

    private final int REQUEST_CODE_GALLERY = 1001;
    @Override
    public void doCamera() {
        super.doCamera();

        GalleryUtils.getInstance().openGallerySingleCrop(this,REQUEST_CODE_GALLERY,mOnHanlderResultCallback);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                final ArrayList<String> list = new ArrayList<>();

                for (PhotoInfo photoInfo : resultList) {
                    list.add(photoInfo.getPhotoPath());
                }
                bmPath = list.get(0);
                GlideUtil.loadCircleImage(BaseApplication.appContext, "file://" + bmPath, mIvSelectIcon, PicIndex.DEFAULT_GROUP_SMALL,
                        PicIndex.DEFAULT_GROUP_SMALL);

                // 获取需要发送的图片
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("key", ConnectorManage.getInstance(CreateChatBarActivity.this)
                            .getKey());
                    map.put("type", String.valueOf(FileUploadType.PIC_GROUP_FACE));

                    CreateGroupActivity.UPLOAD_GROUPIMG_FLAG = System.currentTimeMillis();
                    CreateGroupActivity.UPLOAD_GROUPIMG_FLAG = System.currentTimeMillis();

                    FileUploadManager
                            .createUploadTask(CreateChatBarActivity.this, bmPath, FileUploadManager.FileProfix.JPG,
                                    Config.sPictureHost, map,CreateChatBarActivity.this, CreateGroupActivity.UPLOAD_GROUPIMG_FLAG)
                            .start();
                    uploadRoomIconStatus = UPLOAD_STATUS_ING;//上传聊天室图标
                } catch (Throwable t) {
                    t.printStackTrace();
                    handleUploadGroupIconFail();
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(CreateChatBarActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onUploadFileProgress(int lengthOfUploaded, long flag) {

    }

    @Override
    public void onUploadFileFinish(long flag, String result) {
        CommonFunction.log("group", "result***" + result);
        Message msg = new Message();
        msg.what = REQUEST_CODE_SEL_ROOM_ICON;
        msg.arg1 = 1;
        msg.obj = new Object[]{flag, result};
        mHandler.sendMessage(msg);
    }

    @Override
    public void onUploadFileError(String e, long flag) {
        CommonFunction.log("group", "error***" + e);
        Message msg = new Message();
        msg.what = REQUEST_CODE_SEL_ROOM_ICON;
        msg.arg1 = 0;
        msg.obj = new Object[]{flag, e};
        mHandler.sendMessage(msg);
    }
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_CODE_SEL_ROOM_ICON:
                    Object[] extras = null;
                    long flag = 0;
                    if (msg.obj != null) {
                        extras = (Object[]) msg.obj;
                        flag = (Long) extras[0];
                    }
                    if (msg.arg1 == 1) {

                        if (CreateGroupActivity.UPLOAD_GROUPIMG_FLAG == flag) {
                            CommonFunction.log("group", "handleUploadGroupIconSuccess");
                            handleUploadGroupIconSuccess(String.valueOf(extras[1]));
                        }
                    } else if (msg.arg1 == 0) {

                        if (CreateGroupActivity.UPLOAD_GROUPIMG_FLAG == flag) {
                            CommonFunction.log("group", "handleUploadGroupIconFail");
                            handleUploadGroupIconFail();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        if (flag == CREATE_GROUP_FLAG)
        {
            BaseServerBean bean = GsonUtil.getInstance( )
                    .getServerBean( result , BaseServerBean.class );
            if ( bean != null )
            {
                if ( bean.isSuccess( ) )
                {
                    // 圈子创建完成，提交成功
                    CommonFunction.toastMsg( mContext ,
                            R.string.new_group_has_submit );
                    setResult( RESULT_OK );
                    finish( );
                }
                else
                {
                    onGeneralError( bean.error , flag );
                }
            }
            else
            {
                onGeneralError( 107 , flag );
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);
        //错误
        ErrorCode.toastError( mContext , e );
    }

    @Override
    public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {

    }
}
