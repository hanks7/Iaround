
package net.iaround.ui.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.analytics.enums.ImageEntrance;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BatchUploadManager;
import net.iaround.connector.protocol.PhotoHttpProtocol;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.Me;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SquaredNetImageView;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.comon.SuperView;
import net.iaround.ui.datamodel.AlbumUploadBackBean;
import net.iaround.ui.datamodel.AlbumUploadBaseBean;
import net.iaround.ui.datamodel.Photo;
import net.iaround.ui.datamodel.Photos.PhotosBean;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.ui.space.SpacePictureActivity;
import net.iaround.ui.view.dialog.PictureFrameDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * 我的图片和我喜欢的图片的展示类
 */
public class SpaceTopicView extends SuperView implements PictureFrameDialog.ItemOnclick ,BatchUploadManager.BatchUploadCallBack{
    public static final String DEL_PIC_TIPS = "del_pic_tips"; // 删除图片提示开关
    private static final int PAGE_SIZE = 24;
    public static final int REQ_SPACE_TOPIC = 0x000f;
    public static final int REQ_SPACE_PHOTO = 0x00f0;
    public static final int REQ_PICK_PHOTO = 0x0f00;
    public static final int RES_SPACE_LIKE = 0x0f00;
    public static final int RES_SPACE_DELETE = 0xf000;
    public static final int RES_SPACE_UPLOAD = 0xf001;


    public static final int REQ_SPACE_CHILD_PAGE = 0x01;
    public static final int MSG_PROGRESS_UPDATE = 0x02;
    public static final int MSG_CLOSE_PD = 0x03;
    public static final int MSG_SHOW_ERROR = 0x04;
    public static final int MSG_REFRESH = 0x05;
    public static final int MSG_IMAGE_CHANGED = 0x06;

    private Context mContext;
    private long mUid;
    private int mType; // 0 喜欢，1 照片
    private PullToRefreshGridView mPullToRefreshGridView;
    private IARAdapter mAdapter;
    private int mCurPage;
    private int mTotalPage;
    private View mActionBar;
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mIds;
    private ArrayList<String> mSmalls;
    private boolean isShowDelect = false;
    private boolean isMyself = false;
    private boolean isVip = false;

    protected long REQUEST_FLAG;// 请求的Flag
    private long delblogflag;
    private ImageView ivModifyView;
    private boolean isTips = true;
    CheckBox checkBox;
    Me mUser;
    private int amount;

    private ImageEntrance entrance; // 跳转来路
    private boolean mClickAvatarIsFinish = false;

    private PictureFrameDialog pictureFrameDialog;
    private static int MAX = 11;
    private static int MAXPER = 8;

    /**
     * @param context
     * @param uid
     * @param type    0 喜欢，1 照片
     */
    public SpaceTopicView(SuperActivity context, long uid, int type) {
        super(context, R.layout.space_topic);
        this.mContext = context;
        mPhotos = new ArrayList<Photo>();
        mUid = uid;
        mType = type;
        mType = 1;
        mUser = UserBufferHelper.getInstance().read(mUid);


        isMyself = mUid == Common.getInstance().loginUser.getUid();

        isVip = Common.getInstance().loginUser.isVip();

        isTips = SharedPreferenceUtil.getInstance(getContext()).getBoolean(DEL_PIC_TIPS,
                true);

        initView();

        mMainHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                refrshPhotoData();
            }
        }, 300);

        pictureFrameDialog = new PictureFrameDialog(context, isMyself ? 2 : 3);
        pictureFrameDialog.setItemOnclick(this);
    }

    public void onResume() {
        boolean vip = isVip;
        isVip = Common.getInstance().loginUser.isVip();
        if (vip != isVip && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    public void setDeletSwitch(boolean onOff) {
        isShowDelect = onOff;
        mAdapter.notifyDataSetChanged();
    }

    public void refrshPhotoData() {
        mPullToRefreshGridView.setRefreshing();
    }

    public void initView() {
        mActionBar = findViewById(R.id.abTitle);
        ((TextView) mActionBar.findViewById(R.id.tv_title))
                .setText(mType == 0 ? R.string.like : R.string.all_photo);
        mActionBar.findViewById(R.id.iv_left).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                back();
            }
        });
        mActionBar.findViewById(R.id.fl_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.all_gridView);
//        int dp_5 = CommonFunction.dipToPx(getAttachActivity(), 5);
        mPullToRefreshGridView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
//        mPullToRefreshGridView.getRefreshableView().setVerticalSpacing(dp_5);
//        mPullToRefreshGridView.getRefreshableView().setHorizontalSpacing(dp_5);
        mPullToRefreshGridView.setMode(Mode.BOTH);
        mPullToRefreshGridView.getRefreshableView().setNumColumns(3);
        mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                mCurPage = 1;
                reqUserData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                if (mCurPage < mTotalPage) {
                    mCurPage = mCurPage + 1;
                    reqUserData();
                } else {
                    refreshView.onRefreshComplete();
                }
            }
        });

        ivModifyView = (ImageView) findViewById(R.id.modify_add);


        ivModifyView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isShowDelect) {
                    // 取消删除
                    isShowDelect = false;
                    mAdapter.notifyDataSetChanged();
                    ivModifyView.setImageResource(R.drawable.z_space_modify_add_photo);
                } else {
                    goToPickPhoto();
                }

            }
        });
    }

    private void reqUserData() {
        try {
            SpaceModel netwrokInterface = SpaceModel.getInstance(getAttachActivity());
            if (mType == 0) {
                netwrokInterface.favoritPhotosListReq(mUid, mCurPage, PAGE_SIZE, this);
            } else {
                netwrokInterface.photoListReq(mUid, mCurPage, PAGE_SIZE, this);
            }
        } catch (Exception e) {
            mPullToRefreshGridView.onRefreshComplete();
            Toast.makeText(getAttachActivity(), R.string.network_req_failed,
                    Toast.LENGTH_SHORT).show();
        }

    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLOSE_PD: {
                    mPullToRefreshGridView.onRefreshComplete();
                }
                break;
                case MSG_SHOW_ERROR: {
                    if (msg.obj != null
                            && !CommonFunction.isEmptyOrNullStr(msg.obj.toString())) {
                        ErrorCode.showError(getAttachActivity(), String.valueOf(msg.obj));
                    }
                }
                break;
                case MSG_REFRESH: {
                    refreshData(msg);
                }
                break;
            }
        }
    };

    private void refreshData(Message msg) {
        mCurPage = msg.arg1;
        amount = msg.arg2;
        mTotalPage = amount / PAGE_SIZE;
        if (amount % PAGE_SIZE > 0) {
            mTotalPage++;
        }

        ((TextView) mActionBar.findViewById(R.id.tv_title)).setText(getContext()
                .getString(mType == 0 ? R.string.like : R.string.all_photo)
                + "("
                + amount
                + ")");

        @SuppressWarnings("unchecked")
        ArrayList<Photo> array = (ArrayList<Photo>) msg.obj;
        if (array == null) {
            array = new ArrayList<Photo>();
        }
        if (mCurPage <= 1) {
            mPhotos.clear();
        }
        mPhotos.addAll(array);


        mIds = new ArrayList<String>();
        mSmalls = new ArrayList<String>();
        for (Photo ph : mPhotos) {
            if(null==ph){
                continue;
            }
            mIds.add(ph.getId() == null ? "" : ph.getId());

            mSmalls.add(CommonFunction.thumPicture(ph.getUri()));
        }

        if (isMyself) {
            mPhotos.add(0, null);
            Common.getInstance().loginUser.setPhotos(mPhotos);
        }

        if (mAdapter == null) {
            mAdapter = new IARAdapter();
            mPullToRefreshGridView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        Message msg = new Message();
        msg.what = MSG_CLOSE_PD;
        mMainHandler.sendMessage(msg);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (delblogflag == flag) {
            CommonFunction.log("shifengxiong", "result=" + result);
            try {
                JSONObject json = new JSONObject(result);
                int status = json.optInt("status");
                int lastnum = json.optInt("lastnum");
                if (status == 200) {
                    amount--;
                    mUser.setPhotouploadleft(lastnum);
                    Common.getInstance().loginUser.setPhotouploadleft(lastnum);
                }
                ((TextView) mActionBar.findViewById(R.id.tv_title)).setText(getContext()
                        .getString(mType == 0 ? R.string.like : R.string.all_photo)
                        + "("
                        + amount
                        + ")");
            } catch (Exception e) {
                // TODO: handle exception
            }

            return;
        }else if(flag == REQUEST_FLAG)
        {
            //发布成功1.刷新
            AlbumUploadBackBean bean = GsonUtil.getInstance().getServerBean(result, AlbumUploadBackBean.class);
            Common.getInstance().loginUser.setPhotouploadleft(bean.lastnum);

            mCurPage = 1;
            refrshPhotoData( );
            return;
        }

        Message msg = new Message();
        msg.what = MSG_CLOSE_PD;
        mMainHandler.sendMessage(msg);

        SpaceModel netwrokInterface = SpaceModel.getInstance(getAttachActivity());
        HashMap<String, Object> res = null;
        try {
            res = netwrokInterface.getRes(result, flag);
        } catch (Throwable e) {
            e.printStackTrace();
            Message msge = new Message();
            msge.what = MSG_SHOW_ERROR;
            mMainHandler.sendMessage(msge);
        }
        if (res != null) {
            if ((Integer) res.get("status") != 200) {
                Message msge = new Message();
                msge.what = MSG_SHOW_ERROR;
                msge.obj = result;
                mMainHandler.sendMessage(msge);
            } else {
                SpaceModelReqTypes type = (SpaceModelReqTypes) res.get("reqType");
                if ((type == SpaceModelReqTypes.FAVORIT_PHOTOS_LIST && mType == 0)
                        || (type == SpaceModelReqTypes.PHOTO_LIST && mType != 0)) {
                    Message msgref = new Message();
                    msgref.what = MSG_REFRESH;
                    Object pageno = res.get("pageno");
                    Object amount = res.get("amount");
                    msgref.arg1 = pageno == null ? 1 : (Integer) pageno;
                    msgref.arg2 = amount == null ? 1 : (Integer) amount;
                    msgref.obj = res.get("photos");
                    mMainHandler.sendMessage(msgref);
                    getAttachActivity().setResult(Activity.RESULT_OK);
                }
            }
        }
    }

    private void delPhoto(String photoId) {
        delblogflag = PhotoHttpProtocol.photoDel(getContext(), photoId, this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SPACE_TOPIC && resultCode == Activity.RESULT_OK) {
            mCurPage = 1;
            mPullToRefreshGridView.setRefreshing();
            getAttachActivity().setResult(Activity.RESULT_OK);
        } else if (requestCode == REQ_SPACE_PHOTO && resultCode == Activity.RESULT_OK) {
            mCurPage = 1;
            mPullToRefreshGridView.setRefreshing();
            getAttachActivity().setResult(Activity.RESULT_OK);
        } else if (requestCode == MSG_IMAGE_CHANGED && resultCode == Activity.RESULT_OK) {
            // 删除图片
            if (mUid == Common.getInstance().loginUser.getUid()) {
                mCurPage = 1;
                mPullToRefreshGridView.setRefreshing();
            }
        } else if (resultCode == RES_SPACE_LIKE) {
            // 切换喜欢
            if (mUid == Common.getInstance().loginUser.getUid()) {
                mCurPage = 1;
                mPullToRefreshGridView.setRefreshing();
            }
        } /*else if (requestCode == EditUserInfoActivity.UPLOAD_HEAD) {
            if (data != null) {
                *//** 这里改为可以拿多张图片 *//*

                final ArrayList<String> list = data
                        .getStringArrayListExtra(PictureMultiSelectActivity.PATH_LIST);

                updatePic(mContext, list);

            }
        }*/
    }


    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {

            if (resultList != null) {
                if (reqeustCode == REQUEST_CODE_GALLERYS) {
                    final ArrayList<String> list = new ArrayList<>();

                    for (PhotoInfo photoInfo : resultList) {
                        list.add(photoInfo.getPhotoPath());
                    }
                    updatePic(getContext(), list);
                }


            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private void warningDirty() {
        DialogUtil.showOKCancelDialog(getAttachActivity(), mType == 0 ? R.string.like
                : R.string.photo, R.string.list_data_dirty, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurPage = 1;
                reqUserData();
            }
        });
    }

    @Override
    public void itemOnclick(View view) {
        if (view.getTag().equals("delete")) {
            int mLength = pictureFrameDialog.getPostion();
            if (mPhotos.size() > mLength)
                mPhotos.remove(mLength);
            if (mIds.size() > mLength - 1)
                mIds.remove(mLength - 1);
            if (mSmalls.size() > mLength - 1)
                mSmalls.remove(mLength - 1);
            delPhoto(pictureFrameDialog.getPhoto().getId());
            mAdapter.notifyDataSetChanged();
        } else if (view.getTag().equals("reviewBig")) {
            gotoPhotoDetail(pictureFrameDialog.getPhoto());
        }
    }

    @Override
    public void batchUploadSuccess(long taskFlag, ArrayList<String> serverUrlList) {
        AlbumUploadBaseBean bean = new AlbumUploadBaseBean( );
        // 去除末尾的空格和所有的换行
        bean.datetime = TimeFormat.getCurrentTimeMillis( );
        bean.setContent( "" );
        bean.setAddress( "" );

        bean.setPhotoList(serverUrlList);
        ArrayList< Integer > shareList = new ArrayList< Integer >( );
        bean.setSync( "" );
        bean.setShareList( shareList );
        sendProtocal(bean);
    }

    @Override
    public void batchUploadFail(long taskFlag) {

    }

    /**
     * 发协议的地方
     */
    private void sendProtocal(AlbumUploadBaseBean bean) {
        String photoStr = "";// 组装
        int count = bean.getPhotoList() != null ? bean.getPhotoList().size()
                : 0;
        for (int i = 0; i < count; i++) {

            photoStr += bean.getPhotoList().get(i)
                    + (count - i != 1 ? "," : "");
        }

        GeoData geoData = LocationUtil.getCurrentGeo(mContext);
        int plat = Config.PLAT;
        String content = bean.getContent();

        REQUEST_FLAG = PhotoHttpProtocol.asynPublishPhoto(mContext,
                plat, photoStr, content, geoData.getLat(),
                geoData.getLng(), bean.getAddress(), bean.getSync(), this);
    }

    private class IARAdapter extends BaseAdapter implements View.OnClickListener {

        @Override
        public int getCount() {
            return mPhotos == null ? 0 : mPhotos.size();
        }

        @Override
        public Photo getItem(int position) {
            return mPhotos.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // convertView = new SquaredNetImageView( getAttachActivity( )
                // );
                LayoutInflater inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.z_space_photo_gridadapter, null);
            }
            final Photo photo = getItem(position);

            SquaredNetImageView ivLockView = (SquaredNetImageView) convertView
                    .findViewById(R.id.lock_icon);
            boolean isLock = false;
            if (!isVip & !isMyself) {
                if (position >= 8) {
                    ivLockView.setVisibility(VISIBLE);
                    // ivLockView.setScaleType( ScaleType.FIT_CENTER );

                    ivLockView.getImageView().setScaleType(ScaleType.CENTER_INSIDE);
                    ivLockView.getImageView().setImageResource(
                            R.drawable.z_space_photo_lock_src);


                    isLock = true;
                } else {
                    ivLockView.setVisibility(INVISIBLE);
                }

            } else {
                ivLockView.setVisibility(INVISIBLE);
            }

            SquaredNetImageView icon = (SquaredNetImageView) convertView
                    .findViewById(R.id.photo_view);


            if (isMyself & photo == null) {
                icon.setBackgroundResource(R.drawable.user_info_add_pic);
            } else {
                icon.setProgressBarVisible(false);
                icon.getImageView().setScaleType(ScaleType.FIT_XY);
                icon.getImageView().setAdjustViewBounds(true);
                icon.executeFadeIn(NetImageView.DEFAULT_SMALL,
                        CommonFunction.thumPicture(photo.getUri()));
            }


            int requestCode = REQ_SPACE_PHOTO;
            if (!isLock) {
                icon.setTag(photo);
                //改版的显示
                icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Photo photo = (Photo) v.getTag();
                        if (photo == null) {
                            goToPickPhoto();
                        } else {
                            pictureFrameDialog.show(position, photo);
//                            showAvatar(photo, position);
                        }

                    }
                });
            } else {
                icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        DialogUtil.showTobeVipDialog(getContext(), R.string.space_photo_dialog_title, R.string.only_vip_can_view_all_pic);

                    }
                });
            }
//            icon.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    // TODO Auto-generated method stub
//                    if (isMyself) {
//                        isShowDelect = true;
//                        ivModifyView.setImageResource(R.drawable.z_space_modify_cancel);
//                        notifyDataSetChanged();
//                        return true;
//                    }
//                    return false;
//                }
//            });


            return convertView;
        }

//        private void showAvatar(final Photo photo, final int position) {
//            final CustomContextMenu menu = new CustomContextMenu(BaseApplication.appContext);
//            menu.addMenuItemNew(0,
//                    mContext.getResources().getString(R.string.space_modify_review_big_phtoo),
//                    new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            //查看大图
//                            menu.dismiss();
//
//                        }
//                    }, false);
//
//            menu.addMenuItemNewRed(1,
//                    mContext.getResources().getString(R.string.del_photo),
//                    new View.OnClickListener() {
//                        //删除照片
//                        @Override
//                        public void onClick(View v) {
//                            menu.dismiss();
//                            mPhotos.remove(position);
//                            mIds.remove(position);
//                            mSmalls.remove(position);
//                            notifyDataSetChanged();
//                            delPhoto(photo.getId());
//                            ((Activity) getContext()).setResult(Activity.RESULT_OK);
//                        }
//                    }, true);
//            menu.addMenuItemNew(2, mContext.getResources().getString(R.string.dialog_cancel), new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    menu.dismiss();
//                }
//            }, true);
//
//            menu.showMenu(ivModifyView);
//        }

        public void onClick(View v) {
            Photo photo = (Photo) v.getTag();
            if (photo.isDirty()) {
                warningDirty();
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
    private final int REQUEST_CODE_GALLERYS = 1002;
    /*
     * 跳转到相册和拍照页
     */
    private void goToPickPhoto() {

//        int uploadLeftCount = mUser.getPhotouploadleft();
        int uploadLeftCount = MAX - mSmalls.size();

        if (uploadLeftCount > 0) {
//            int realCount = Math.min(uploadLeftCount,
//                    PictureMultiSelectActivity.MAX_PHOTOT_SIZE);
//            PictureMultiSelectActivity.skipToPictureMultiSelectAlbum(getContext(),
//                    EditUserInfoActivity.UPLOAD_HEAD, realCount);
            GalleryUtils.getInstance().openGalleryMuti(BaseApplication.appContext,REQUEST_CODE_GALLERYS,uploadLeftCount,mOnHanlderResultCallback);
        } else {
            // 超过图片上传总上限
            CommonFunction.toastMsg(getContext(),
                    getContext().getString(R.string.total_upload_photo_full));
        }
    }

    public void gotoPhotoDetail(Photo photo) {
        Intent i = new Intent(mContext, SpacePictureActivity.class);
        i.putExtra("photoid", photo.getId());
        i.putExtra("uid", Common.getInstance().loginUser.getUid());//mUid
        if (mIds != null && mIds.size() > 0) {
            i.putExtra("ids", mIds);
        }
        if (mSmalls != null && mSmalls.size() > 0) {
            i.putExtra("smallPhotos", mSmalls);
        }
        i.putExtra("entrance", ImageEntrance.ALBUM);
        i.putExtra("finish", mClickAvatarIsFinish);
        if (REQ_SPACE_PHOTO > 0) {
            ((Activity) mContext).startActivityForResult(i, REQ_SPACE_PHOTO);
        } else {
            mContext.startActivity(i);
        }
    }

    public void updatePic(Context mContext, ArrayList<String> imageUrlList) {

        try {
            final long taskFlag = TimeFormat.getCurrentTimeMillis()
                    & BatchUploadManager.IMAGE_TASK_MASK;
            new BatchUploadManager(getContext()).uploadImage(taskFlag, imageUrlList,
                    FileUploadType.PIC_ALBUM,
                    this);
        } catch (Exception e) {
            // 图片文件不存在
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void back(){
        if (mPhotos.size() > 0){
            ArrayList<PhotosBean> pics = new ArrayList<PhotosBean>();
            if (isMyself)
                mPhotos.remove(0);//剔除第一张占位图
            for (Photo photo : mPhotos){
                PhotosBean bean = new PhotosBean();
                // 避免url空对象
                String path = "";
                if (photo.getUri() != null)
                    path = photo.getUri();
                bean.setImage(path);
                bean.setPhotoid(String.valueOf(photo.getId()));
                pics.add(bean);
            }
            Collections.reverse(pics);
            Common.getInstance().setAlbumPic(pics);
        }

        getAttachActivity().setResult(Activity.RESULT_OK);
        getAttachActivity().finish();
    }


}
