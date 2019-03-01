
package net.iaround.ui.group;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.Text;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.FileUploadManager;
import net.iaround.connector.FileUploadManager.FileProfix;
import net.iaround.connector.UploadFileCallback;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.comon.SuperView;
import net.iaround.ui.group.activity.CreateGroupActivity;
import net.iaround.ui.group.bean.CreateGroupInfo;
import net.iaround.ui.group.bean.GroupNextStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: CreateGroupEditInfo
 * @Description: 创建圈子——编辑圈信息
 * @date 2013-12-9 下午2:13:36
 */
public class CreateGroupEditInfo extends SuperView implements INextCheck, UploadFileCallback {

    private RelativeLayout mSelectImgLayout;
    private ImageView mGroupImg;
    private TextView mGroupName;
    private EditText mGroupDesc;
    private SuperActivity mActivity;

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

    /**
     * 选择聊天室图标
     */
    private final int REQUEST_CODE_SEL_ROOM_ICON = 1001;

    /**
     * 是否已上传了圈图
     */
    private boolean isUploadImg = false;
    /**
     * 已上传成功的圈图地址
     */
    private String roomIconUrl = "";
    /**
     * 未上传的圈图地址
     */
    private String roomIconPath = "";

    private ICreateGroupParentCallback mParentCallback;

    /**
     * 是否已经加载数据
     */
    private boolean isInitData = false;

    /**
     * 是否以类别图作为圈图
     */
    private boolean isUseCaregoryIcon = true;

    public CreateGroupEditInfo(SuperActivity activity,
                               ICreateGroupParentCallback createGroupActivity) {
        super(activity, R.layout.view_create_group_editinfo);
        this.mActivity = activity;
        this.mParentCallback = createGroupActivity;
        initViews();
        setListeners();
        CommonFunction.log("create_group", "CreateGroupEditInfo initView");
    }

    private void initViews() {
        mSelectImgLayout = (RelativeLayout) findViewById(R.id.select_image);
        mGroupImg = (ImageView) findViewById(R.id.group_img);
        mGroupName = (TextView) findViewById(R.id.edit_group_name);
        mGroupDesc = (EditText) findViewById(R.id.edit_group_desc);
    }

    private void setListeners() {
        mSelectImgLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				Intent i = PictureCaptureHandle.getPickPhotoFromCameraIntent(
//						getAttachActivity( ) , new int[ ]
//							{ 640 , 640 } , 100 , false , true );
//				getAttachActivity( ).startActivityForResult( i , REQUEST_CODE_SEL_ROOM_ICON );
                requestCamera(mActivity);
//				DialogUtil.showTowButtonDialog( getAttachActivity( ) , getAttachActivity( )
//						.getString( R.string.dialog_title ) ,
//						getAttachActivity( ).getString( R.string.please_sel_group_photo ) ,
//						getAttachActivity( ).getString( R.string.take_image_from_albume ) ,
//						getAttachActivity( ).getString( R.string.take_image_from_camera ) ,
//						new View.OnClickListener( )
//						{
//							
//							@Override
//							public void onClick( View v )
//							{
//								selGroupIcon( 1 );
//							}
//						} , new View.OnClickListener( )
//						{
//							
//							@Override
//							public void onClick( View v )
//							{
//								selGroupIcon( 2 );
//							}
//						} );

            }
        });
    }

    private final int REQUEST_CODE_GALLERY = 1001;

    @Override
    public void doCamera() {
        super.doCamera();
//        PictureMultiSelectActivity.skipToPictureMultiSelectAlbumCrop(getAttachActivity(),
//                REQUEST_CODE_SEL_ROOM_ICON);

        GalleryUtils.getInstance().openGallerySingleCrop(BaseApplication.appContext,REQUEST_CODE_GALLERY,mOnHanlderResultCallback);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                final ArrayList<String> list = new ArrayList<>();

                for (PhotoInfo photoInfo : resultList) {
                    list.add(photoInfo.getPhotoPath());
                }
                mParentCallback.showWaitDialog(true);

                final String bmPath = list.get(0);
                GlideUtil.loadCircleImage(getContext(), "file://" + bmPath, mGroupImg, PicIndex.DEFAULT_GROUP_SMALL,
                        PicIndex.DEFAULT_GROUP_SMALL);

                // 获取需要发送的图片
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("key", ConnectorManage.getInstance(getAttachActivity())
                            .getKey());
                    map.put("type", String.valueOf(FileUploadType.PIC_GROUP_FACE));

                    CreateGroupActivity.UPLOAD_GROUPIMG_FLAG = System.currentTimeMillis();

                    FileUploadManager
                            .createUploadTask(getContext(), bmPath, FileProfix.JPG, Config.sPictureHost, map, CreateGroupEditInfo.this, CreateGroupActivity.UPLOAD_GROUPIMG_FLAG)
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
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CODE_SEL_ROOM_ICON:
//                // 选择圈图
//                if (resultCode == SuperActivity.RESULT_OK) {
//                    mParentCallback.showWaitDialog(true);
//
//                    final ArrayList<String> list = data
//                            .getStringArrayListExtra(PictureMultiSelectActivity.PATH_LIST);
//                    final String bmPath = list.get(0);
//                    GlideUtil.loadCircleImage(getContext(), "file://" + bmPath, mGroupImg, PicIndex.DEFAULT_GROUP_SMALL,
//                            PicIndex.DEFAULT_GROUP_SMALL);
//
//                    // 获取需要发送的图片
//                    try {
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("key", ConnectorManage.getInstance(getAttachActivity())
//                                .getKey());
//                        map.put("type", String.valueOf(FileUploadType.PIC_GROUP_FACE));
//
//                        CreateGroupActivity.UPLOAD_GROUPIMG_FLAG = System.currentTimeMillis();
//
////						getAttachActivity( ).getConnectorManage( ).upLoadFile(
////								Config.sPictureHost , fis , "picture.jpg" , map ,
////								CreateGroupActivity.UPLOAD_GROUPIMG_FLAG );
//
//                        FileUploadManager
//                                .createUploadTask(getContext(), bmPath, FileProfix.JPG, Config.sPictureHost, map, this, CreateGroupActivity.UPLOAD_GROUPIMG_FLAG)
//                                .start();
//
//                        uploadRoomIconStatus = UPLOAD_STATUS_ING;//上传聊天室图标
//
//
//                    } catch (Throwable t) {
//                        t.printStackTrace();
//                        handleUploadGroupIconFail();
//                    }
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    /**
     * @Title: handleUploadGroupIconFail
     * @Description: 处理圈图上传失败
     */
    private void handleUploadGroupIconFail() {

        uploadRoomIconStatus = UPLOAD_STATUS_FAIL;
        CommonFunction.toastMsg(getAttachActivity(), R.string.upload_fail);
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

        isUseCaregoryIcon = false;
        uploadRoomIconStatus = UPLOAD_STATUS_FINISHED;
        CommonFunction.toastMsg(getAttachActivity(), R.string.upload_complete);
    }

    /**
     * 释放聊天室图标的bitmap
     */
    private void releaseRoomIcon() {
        /*
         * if (roomIconBitmap != null && !roomIconBitmap.isRecycled()) {
		 * roomIconBitmap.recycle(); roomIconBitmap = null; }
		 */
    }

    /*****************************************
     * INextCheck接口实现
     *****************************************/

    @Override
    public GroupNextStep getGroupNextStep() {
        GroupNextStep step = new GroupNextStep();
        String groupName = mGroupName.getText().toString().replace("\\n", "").trim();
        String groupDesc = mGroupDesc.getText().toString().replace("\\n", "").trim();
        step.nextMsg = "";
        // 允许圈图为空，默认采用类别对应的图片
		/*
		 * if(uploadRoomIconStatus != UPLOAD_STATUS_FINISHED){ step.nextMsg =
		 * getContext().getString(R.string.please_reupload_group_icon); }else
		 */
        String groupSensitiveName = CommonFunction.getSensitiveKeyword(getContext(),
                groupName);
        String groupSensitiveDesc = CommonFunction.getSensitiveKeyword(getContext(),
                groupDesc);
        if (groupName.trim().equals("")) {
            step.nextMsg = getContext().getString(R.string.group_name_length_not_correct);
        } else if (groupName.length() < 2 || groupName.length() > 15) {
            step.nextMsg = getContext().getString(R.string.group_name_length_not_correct);
        }/*
		 * else if(groupDesc.equals("")){ step.nextMsg =
		 * getContext().getString(R.string.enter_group_desc);; }else
		 * if(groupDesc.length() < 15 || groupDesc.length() > 140){ step.nextMsg
		 * = getContext().getString(R.string.group_desc_length_not_correct);; }
		 */ else if (!groupSensitiveName.equals("")) {
            step.nextMsg = getContext().getString(R.string.have_sensitive_keyword) + ":"
                    + groupSensitiveName;
        } else if (!groupSensitiveDesc.equals("")) {
            step.nextMsg = getContext().getString(R.string.have_sensitive_keyword) + ":"
                    + groupSensitiveDesc;
        } else {
            step.nextParams = new String[3];
            if (isUseCaregoryIcon) {
                step.nextParams[0] = "";
            } else {
                step.nextParams[0] = roomIconUrl;
            }
            step.nextParams[1] = CommonFunction.filterKeyWordAndReplaceEmoji(getContext(),
                    groupName);
            String uploadDesc = CommonFunction.filterKeyWordAndReplaceEmoji(getContext(),
                    groupDesc);
            if (uploadDesc == null) {
                uploadDesc = "";
            }
            step.nextParams[2] = uploadDesc;
        }

        return step;
    }

    @Override
    public void initData(BaseServerBean bean, boolean isBack) {
        CommonFunction.log("create_group", "initData***" + isUseCaregoryIcon);
        if (isUseCaregoryIcon) {
            CreateGroupInfo info = mParentCallback.getGroupInfo();
//			ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView( info.groupTypeIcon ,
//					mGroupImg , PicIndex.DEFAULT_GROUP_SMALL ,
//				PicIndex.DEFAULT_GROUP_SMALL , null );
            GlideUtil.loadCircleImage(BaseApplication.appContext, info.groupTypeIcon, mGroupImg, PicIndex.DEFAULT_GROUP_SMALL,
                    PicIndex.DEFAULT_GROUP_SMALL);
        } else {
//			ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView(
//					roomIconUrl , mGroupImg , PicIndex.DEFAULT_GROUP_SMALL ,
//				PicIndex.DEFAULT_GROUP_SMALL , null , 100 );
            GlideUtil.loadCircleImage(BaseApplication.appContext, roomIconUrl, mGroupImg, PicIndex.DEFAULT_GROUP_SMALL,
                    PicIndex.DEFAULT_GROUP_SMALL);
        }
        if (!isInitData) {
            // TODO 加载数据
            isInitData = true;
        }

    }

    @Override
    public void onUploadFileProgress(int lengthOfUploaded, long flag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUploadFileFinish(long flag, String result) {
        // TODO Auto-generated method stub
        CommonFunction.log("group", "result***" + result);
        Message msg = new Message();
        msg.what = REQUEST_CODE_SEL_ROOM_ICON;
        msg.arg1 = 1;
        msg.obj = new Object[]{flag, result};
        mHandler.sendMessage(msg);
		/*mParentCallback.showWaitDialog( false );
		if ( CreateGroupActivity.UPLOAD_GROUPIMG_FLAG == flag )
		{
			CommonFunction.log("group", "handleUploadGroupIconSuccess");
			handleUploadGroupIconSuccess( result );
		}*/
    }

    @Override
    public void onUploadFileError(String e, long flag) {
        // TODO Auto-generated method stub
        CommonFunction.log("group", "error***" + e);
		/*mParentCallback.showWaitDialog( false );
		if ( CreateGroupActivity.UPLOAD_GROUPIMG_FLAG == flag )
		{
			CommonFunction.log("group", "handleUploadGroupIconFail");
			handleUploadGroupIconFail( );
		}*/
        Message msg = new Message();
        msg.what = REQUEST_CODE_SEL_ROOM_ICON;
        msg.arg1 = 0;
        msg.obj = new Object[]{flag, e};
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            mParentCallback.showWaitDialog(false);
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

}
