/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.galleryfinal;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.finalteam.galleryfinal.adapter.FolderListAdapter;
import cn.finalteam.galleryfinal.adapter.PhotoListAdapter;
import cn.finalteam.galleryfinal.model.PhotoFolderInfo;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.permission.AfterPermissionGranted;
import cn.finalteam.galleryfinal.permission.EasyPermissions;

import cn.finalteam.galleryfinal.play.PictureVideoPlayActivity;
import cn.finalteam.galleryfinal.utils.DoubleUtils;
import cn.finalteam.galleryfinal.utils.ILogger;
import cn.finalteam.galleryfinal.utils.LocalMediaLoader;
import cn.finalteam.galleryfinal.utils.PictureFileUtils;
import cn.finalteam.galleryfinal.utils.Utils;
import cn.finalteam.galleryfinal.widget.FloatingActionButton;
import cn.finalteam.galleryfinal.widget.PhotoPopupWindow;
import cn.finalteam.toolsfinal.DeviceUtils;
import cn.finalteam.toolsfinal.StringUtils;
import cn.finalteam.toolsfinal.io.FilenameUtils;

/**
 * Desction:图片选择器
 * Author:pengjianbo
 * Date:15/10/10 下午3:54
 */
public class PhotoSelectActivity extends PhotoBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, PhotoPopupWindow.OnItemClickListener {

    private final int HANLDER_TAKE_PHOTO_EVENT = 1000;
    private final int HANDLER_REFRESH_LIST_EVENT = 1002;
    private final int HANDLER_REFRESH_VIDEO_LIST_EVENT = 10031003;

    private GridView mGvPhotoList;
    private ListView mLvFolderList;
    private LinearLayout mLlFolderPanel;
    private ImageView mIvBack;
    private TextView mTvChooseCount;
    private TextView mTvPreviewCount;
    private TextView mTvSubTitle;
    private FloatingActionButton mFabOk;
    private TextView mTvEmptyView;
    private RelativeLayout mTitlebar;
    private TextView mTvTitle;
    private ImageView mIvFolderArrow;

    private List<PhotoFolderInfo> mAllPhotoFolderList;
    private FolderListAdapter mFolderListAdapter;

    private ArrayList<PhotoInfo> mCurPhotoList;
    private PhotoListAdapter mPhotoListAdapter;

    //是否需要刷新相册
    private boolean mHasRefreshGallery = false;
    private ArrayList<PhotoInfo> mSelectPhotoList = new ArrayList<>();

    private LocalMediaLoader mediaLoader;
    private PhotoPopupWindow popupWindow;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectPhotoMap", mSelectPhotoList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectPhotoList = (ArrayList<PhotoInfo>) getIntent().getSerializableExtra("selectPhotoMap");
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( msg.what == HANLDER_TAKE_PHOTO_EVENT ) {
                PhotoInfo photoInfo = (PhotoInfo) msg.obj;
                takeRefreshGallery(photoInfo);
                refreshSelectCount();
            } else if ( msg.what == HANDLER_REFRESH_LIST_EVENT ){
                refreshSelectCount();
                mPhotoListAdapter.notifyDataSetChanged();
                mFolderListAdapter.notifyDataSetChanged();

                if(mAllPhotoFolderList != null && mAllPhotoFolderList.size() > 0){
                    if (mAllPhotoFolderList.get(0).getPhotoList() == null ||
                            mAllPhotoFolderList.get(0).getPhotoList().size() == 0) {
                        mTvEmptyView.setText(R.string.no_photo);
                    }
                }

                mGvPhotoList.setEnabled(true);
                mTvSubTitle.setEnabled(true);
            }else if (msg.what == HANDLER_REFRESH_VIDEO_LIST_EVENT){
                mPhotoListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( GalleryFinal.getFunctionConfig() == null || GalleryFinal.getGalleryTheme() == null) {
            resultFailureDelayed(getString(R.string.please_reopen_gf), true);
        } else {
            setContentView(R.layout.gf_activity_photo_select);
            mPhotoTargetFolder = null;

            mediaLoader = new LocalMediaLoader(this, GalleryFinal.getFunctionConfig().getMimeType());

            findViews();
            setListener();

            mAllPhotoFolderList = new ArrayList<>();
            mFolderListAdapter = new FolderListAdapter(this, mAllPhotoFolderList, GalleryFinal.getFunctionConfig());
            mLvFolderList.setAdapter(mFolderListAdapter);

            mCurPhotoList = new ArrayList<>();
            mPhotoListAdapter = new PhotoListAdapter(this, mCurPhotoList, mSelectPhotoList, mScreenWidth);
            mGvPhotoList.setAdapter(mPhotoListAdapter);

            // 复选框监听
            mPhotoListAdapter.setOnCheckListener(new PhotoListAdapter.OnCheckListener() {
                @Override
                public void onCheck(View view, int position) {
                    checkItemClick(view,position);
                }
            });
            if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
                mTvChooseCount.setVisibility(View.VISIBLE);
                mFabOk.setVisibility(View.VISIBLE);
            }else{
                mTvPreviewCount.setVisibility(View.GONE);
            }

            setTheme();
            mGvPhotoList.setEmptyView(mTvEmptyView);

            refreshSelectCount();
            requestGalleryPermission();

            mGvPhotoList.setOnScrollListener(GalleryFinal.getCoreConfig().getPauseOnScrollListener());
        }

        Global.mPhotoSelectActivity = this;

        if (GalleryFinal.getFunctionConfig()!=null && GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofAll()) {
            popupWindow = new PhotoPopupWindow(this);
            popupWindow.setOnItemClickListener(this);
        }
    }

    private void setTheme() {
        mIvFolderArrow.setImageResource(GalleryFinal.getGalleryTheme().getIconFolderArrow());
        if (GalleryFinal.getGalleryTheme().getIconFolderArrow() == R.drawable.ic_gf_triangle_arrow) {
            mIvFolderArrow.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }

        mFabOk.setIcon(GalleryFinal.getGalleryTheme().getIconFab());
        mFabOk.setColorPressed(getResources().getColor(R.color.ok_pressed));
        mFabOk.setColorNormal(getResources().getColor(R.color.ok_normal));
    }

    private void findViews() {
        mGvPhotoList = (GridView) findViewById(R.id.gv_photo_list);
        mLvFolderList = (ListView) findViewById(R.id.lv_folder_list);
        mTvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        mLlFolderPanel = (LinearLayout) findViewById(R.id.ll_folder_panel);
        mTvChooseCount = (TextView) findViewById(R.id.tv_choose_count);
        mTvPreviewCount = (TextView) findViewById(R.id.tv_preview);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mFabOk = (FloatingActionButton) findViewById(R.id.fab_ok);
        mTvEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mTitlebar = (RelativeLayout) findViewById(R.id.titlebar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvFolderArrow = (ImageView) findViewById(R.id.iv_folder_arrow);

    }

    private void setListener() {
        mTvSubTitle.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mIvFolderArrow.setOnClickListener(this);

        mLvFolderList.setOnItemClickListener(this);
        mGvPhotoList.setOnItemClickListener(this);
        mFabOk.setOnClickListener(this);
        mTvPreviewCount.setOnClickListener(this);
    }

    protected void deleteSelect(int photoId) {
        try {
            for(Iterator<PhotoInfo> iterator = mSelectPhotoList.iterator();iterator.hasNext();){
                PhotoInfo info = iterator.next();
                if (info != null && info.getPhotoId() == photoId) {
                    iterator.remove();
                    break;
                }
            }
        } catch (Exception e){}

        refreshAdapter();
    }

    private void refreshAdapter() {
        mHanlder.sendEmptyMessageDelayed(HANDLER_REFRESH_LIST_EVENT, 100);
    }

    protected void takeRefreshGallery(PhotoInfo photoInfo, boolean selected) {
        if (isFinishing() || photoInfo == null) {
            return;
        }
        int width = getPicSize(photoInfo.getPhotoPath())[0];
        int height = getPicSize(photoInfo.getPhotoPath())[1];
        if (width < 350 | height < 350){
            toast(getString(R.string.photo_size_error1));
            return;
        }

        Message message = mHanlder.obtainMessage();
        message.obj = photoInfo;
        message.what = HANLDER_TAKE_PHOTO_EVENT;
        mSelectPhotoList.add(photoInfo);
        mHanlder.sendMessageDelayed(message, 100);
    }

    /**
     * 解决在5.0手机上刷新Gallery问题，从startActivityForResult回到Activity把数据添加到集合中然后理解跳转到下一个页面，
     * adapter的getCount与list.size不一致，所以我这里用了延迟刷新数据
     * @param photoInfo
     */
    private void takeRefreshGallery(PhotoInfo photoInfo) {
        mCurPhotoList.add(1, photoInfo);
        mPhotoListAdapter.notifyDataSetChanged();

        //添加到集合中
        if (mAllPhotoFolderList.size() > 0) {
            List<PhotoInfo> photoInfoList = mAllPhotoFolderList.get(0).getPhotoList();
            if (photoInfoList == null) {
                photoInfoList = new ArrayList<>();
            }
            photoInfoList.add(0, photoInfo);
            mAllPhotoFolderList.get(0).setPhotoList(photoInfoList);
        }

        if ( mFolderListAdapter.getSelectFolder() != null ) {
            PhotoFolderInfo photoFolderInfo = mFolderListAdapter.getSelectFolder();
            List<PhotoInfo> list = photoFolderInfo.getPhotoList();
            if ( list == null ) {
                list = new ArrayList<>();
            }
            list.add(0, photoInfo);
            if ( list.size() == 1 ) {
                photoFolderInfo.setCoverPhoto(photoInfo);
            }
            mFolderListAdapter.getSelectFolder().setPhotoList(list);
        } else {
            String folderA = new File(photoInfo.getPhotoPath()).getParent();
            for (int i = 1; i < mAllPhotoFolderList.size(); i++) {
                PhotoFolderInfo folderInfo = mAllPhotoFolderList.get(i);
                String folderB = null;
                if (!StringUtils.isEmpty(photoInfo.getPhotoPath())) {
                    folderB = new File(photoInfo.getPhotoPath()).getParent();
                }
                if (TextUtils.equals(folderA, folderB)) {
                    List<PhotoInfo> list = folderInfo.getPhotoList();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(0, photoInfo);
                    folderInfo.setPhotoList(list);
                    if ( list.size() == 1 ) {
                        folderInfo.setCoverPhoto(photoInfo);
                    }
                }
            }
        }

        mFolderListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void takeResult(PhotoInfo photoInfo) {

        int width = getPicSize(photoInfo.getPhotoPath())[0];
        int height = getPicSize(photoInfo.getPhotoPath())[1];
        if (width < 350 | height < 350){
            toast(getString(R.string.photo_size_error1));
            return;
        }
        Message message = mHanlder.obtainMessage();
        message.obj = photoInfo;
        message.what = HANLDER_TAKE_PHOTO_EVENT;

        if ( !GalleryFinal.getFunctionConfig().isMutiSelect() ) { //单选
            mSelectPhotoList.clear();
            mSelectPhotoList.add(photoInfo);

            if ( GalleryFinal.getFunctionConfig().isEditPhoto() ) {//裁剪
                mHasRefreshGallery = true;
                toPhotoEdit();
            } else {
                ArrayList<PhotoInfo> list = new ArrayList<>();
                list.add(photoInfo);
                resultData(list);
            }

            mHanlder.sendMessageDelayed(message, 100);
        } else {//多选
            mSelectPhotoList.add(photoInfo);
            mHanlder.sendMessageDelayed(message, 100);
        }
    }

    /**
     * 执行裁剪
     */
    protected void toPhotoEdit() {
        Intent intent = new Intent(this, PhotoEditActivity.class);
        intent.putExtra(PhotoEditActivity.SELECT_MAP, mSelectPhotoList);
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if ( id == R.id.tv_sub_title || id == R.id.iv_folder_arrow) {
            if ( mLlFolderPanel.getVisibility() == View.VISIBLE ) {
                mLlFolderPanel.setVisibility(View.GONE);
                mLlFolderPanel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.gf_flip_horizontal_out));
            } else {
                mLlFolderPanel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.gf_flip_horizontal_in));
                mLlFolderPanel.setVisibility(View.VISIBLE);
            }
        } else if ( id == R.id.iv_back ) {
            if ( mLlFolderPanel.getVisibility() == View.VISIBLE ) {
                mTvSubTitle.performClick();
            } else {
                finish();
            }
        } else if ( id == R.id.fab_ok ) {
            if(mSelectPhotoList.size() > 0) {
                resultData(mSelectPhotoList);
            }
        }else if ( id == R.id.tv_preview ) {
            Intent intent = new Intent(this, PhotoPreviewActivity.class);
            intent.putExtra(PhotoPreviewActivity.PHOTO_LIST, mSelectPhotoList);
            intent.putExtra(PhotoPreviewActivity.IS_PREVIEW, true);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int parentId = parent.getId();
        if ( parentId == R.id.lv_folder_list ) {
            folderItemClick(position);
        } else {
            photoItemClick(view, position);
        }
    }
    private void folderItemClick(int position) {
        mLlFolderPanel.setVisibility(View.GONE);
        mCurPhotoList.clear();
        PhotoFolderInfo photoFolderInfo = mAllPhotoFolderList.get(position);
        if ( photoFolderInfo.getPhotoList() != null ) {
            mCurPhotoList.addAll(photoFolderInfo.getPhotoList());
        }

        //gh 加入默认相机
        if(false == Utils.disableTakePhoto() && GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofImage()) {
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setPhotoId(-1);
            photoInfo.setPhotoPath("");
            mCurPhotoList.add(0, photoInfo);
        }

        mPhotoListAdapter.notifyDataSetChanged();

        if (position == 0) {
            mPhotoTargetFolder = null;
        } else {
            PhotoInfo photoInfo = photoFolderInfo.getCoverPhoto();
            if (photoInfo != null && !StringUtils.isEmpty(photoInfo.getPhotoPath())) {
                mPhotoTargetFolder = new File(photoInfo.getPhotoPath()).getParent();
            } else {
                mPhotoTargetFolder = null;
            }
        }
        mTvSubTitle.setText(photoFolderInfo.getFolderName());
        mFolderListAdapter.setSelectFolder(photoFolderInfo);
        mFolderListAdapter.notifyDataSetChanged();

        if (mCurPhotoList.size() == 0) {
            mTvEmptyView.setText(R.string.no_photo);
        }
    }

    private void photoItemClick(View view, int position) {
        if(mCurPhotoList==null){
            return;
        }
        if((position+1)>mCurPhotoList.size()){
            return;
        }
        PhotoInfo info = mCurPhotoList.get(position);
        if (info.getPhotoId() == -1){
//            //判断是否达到多选最大数量
            if (GalleryFinal.getFunctionConfig().isMutiSelect() && mSelectPhotoList.size() == GalleryFinal.getFunctionConfig().getMaxSize()) {
                toast(getString(R.string.select_max_tips));
                return;
            }

            if (!DeviceUtils.existSDCard()) {
                toast(getString(R.string.empty_sdcard));
                return;
            }
            if(false == Utils.disableTakePhoto()) {
                // 防止快速点击，但是单独拍照不管
                if (!DoubleUtils.isFastDoubleClick()) {
                    switch (GalleryFinal.getFunctionConfig().getMimeType()) {
                        case FunctionConfig.TYPE_ALL:
                            // 如果是全部类型下，单独拍照就默认图片 (因为单独拍照不会new此PopupWindow对象)
                            if (popupWindow != null) {
                                if (popupWindow.isShowing()) {
                                    popupWindow.dismiss();
                                }
                                popupWindow.showAsDropDown(mTvSubTitle);
                            } else {
                                requestCamerPermission();
                            }
                            break;
                        case FunctionConfig.TYPE_IMAGE:
                            // 拍照
                            requestCamerPermission();
                            break;
                        case FunctionConfig.TYPE_VIDEO:
                            // 录视频
                            requestVideoPermission();
                            break;
                    }
                }
            }
        } else {
            int width = getPicSize(info.getPhotoPath())[0];
            int height = getPicSize(info.getPhotoPath())[1];
            if (info.getMimeType() != PictureMimeType.ofVideo()) {
                if (width < 350 | height < 350) {

                    toast(getString(R.string.photo_size_error1));
                    return;
                }
            }else{
                int second = (int)info.getDuration() / 1000;
                if (second < 5){
                    toast(getString(R.string.photo_video_long_min));
                    return;
                }
                if (second > 30){
                    toast(getString(R.string.photo_video_long_max));
                    return;
                }
            }

            if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
                for (PhotoInfo photoInfo : mCurPhotoList) {
                    photoInfo.isSelect = false;
                }
                for (PhotoInfo photoInfo1 : mCurPhotoList) {
                    for (PhotoInfo photoInfo : mSelectPhotoList) {
                        if (photoInfo.getPhotoId() == photoInfo1.getPhotoId()) {
                            photoInfo1.isSelect = true;
                        }
                    }
                }

                if (info.getMimeType() != PictureMimeType.ofVideo()){
                    GalleryUtils.getInstance().setmCurrent(mCurPhotoList);
                    Intent intent = new Intent(this, PhotoPreviewActivity.class);
                    intent.putExtra(PhotoPreviewActivity.PHOTO_INDEX, position);
                    startActivityForResult(intent,201);
                }else{
                    Intent intent = new Intent(this, PictureVideoPlayActivity.class);
                    intent.putExtra("video_path", info.getPhotoPath());
                    startActivity(intent);
                }

            } else {
                mSelectPhotoList.clear();
                mSelectPhotoList.add(info);
                if (GalleryFinal.REQUEST_CODE_GALLERY_ICON == GalleryFinal.getRequestCode()) {
                    String ext = FilenameUtils.getExtension(info.getPhotoPath());
                    if (GalleryFinal.getFunctionConfig().isEditPhoto() && (ext.equalsIgnoreCase("png")
                            || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
                        toPhotoEdit();
                    }
                } else {
                    resultData(mSelectPhotoList);
                }
            }
        }

    }

    /**
     * 复选框监听
     * @param view
     * @param position
     */
    private void checkItemClick(View view, int position){
        if(mCurPhotoList == null){
            return;
        }
        if((position+1)>mCurPhotoList.size()){
            return;
        }
        PhotoInfo info = mCurPhotoList.get(position);
        int width = getPicSize(info.getPhotoPath())[0];
        int height = getPicSize(info.getPhotoPath())[1];
        if (info.getMimeType() == PictureMimeType.ofImage() && width < 350 | height < 350){
            toast(getString(R.string.photo_size_error1));
            return;
        }
        if (!GalleryFinal.getFunctionConfig().isMutiSelect()) {
            mSelectPhotoList.clear();
            mSelectPhotoList.add(info);
            String ext = FilenameUtils.getExtension(info.getPhotoPath());
            if (GalleryFinal.getFunctionConfig().isEditPhoto() && (ext.equalsIgnoreCase("png")
                    || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
                toPhotoEdit();
            } else {
                ArrayList<PhotoInfo> list = new ArrayList<>();
                list.add(info);
                resultData(list);
            }
            return;
        }

        boolean checked = false;
        if (!mSelectPhotoList.contains(info)) {
            if (GalleryFinal.getFunctionConfig().isMutiSelect() && mSelectPhotoList.size() == GalleryFinal.getFunctionConfig().getMaxSize()) {
                toast(getString(R.string.select_max_tips));
                return;
            } else {
                mSelectPhotoList.add(info);
                checked = true;
            }
        } else {
            try {
                for(Iterator<PhotoInfo> iterator = mSelectPhotoList.iterator();iterator.hasNext();){
                    PhotoInfo pi = iterator.next();
                    if (pi != null && TextUtils.equals(pi.getPhotoPath(), info.getPhotoPath())) {
                        iterator.remove();
                        break;
                    }
                }
            } catch (Exception e){}
            checked = false;
        }
        refreshSelectCount();

        PhotoListAdapter.PhotoViewHolder holder = (PhotoListAdapter.PhotoViewHolder) view.getTag();
        if (holder != null) {
            if (checked) {
                holder.mIvCheck.setBackgroundColor(getResources().getColor(R.color.ok_pressed));
            } else {
                holder.mIvCheck.setBackgroundColor(getResources().getColor(R.color.select_normal));
            }
        } else {
            mPhotoListAdapter.notifyDataSetChanged();
        }
    }

    public void refreshSelectCount() {
        mTvChooseCount.setText(getString(R.string.selected, mSelectPhotoList.size(), GalleryFinal.getFunctionConfig().getMaxSize()));
        if ( mSelectPhotoList.size() > 0 && GalleryFinal.getFunctionConfig().isMutiSelect() ) {
            mTvPreviewCount.setText(getString(R.string.preview)+"("+mSelectPhotoList.size()+")");
            mTvPreviewCount.setTextColor(getResources().getColor(R.color.preview_pressed));
        } else {
            mTvPreviewCount.setTextColor(getResources().getColor(R.color.preview_normal));
            mTvPreviewCount.setText(getString(R.string.preview));
        }

    }

    @Override
    public void onPermissionsGranted(List<String> list) {
        getPhotos();
    }

    @Override
    public void onPermissionsDenied(List<String> list) {
        mTvEmptyView.setText(R.string.permissions_denied_tips);
    }

    /**
     * 获取所有图片
     */
    @AfterPermissionGranted(GalleryFinal.PERMISSIONS_CODE_GALLERY)
    private void requestGalleryPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            getPhotos();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.permissions_tips_gallery),
                    GalleryFinal.PERMISSIONS_CODE_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void getPhotos() {
        mTvEmptyView.setText(R.string.waiting);
        mGvPhotoList.setEnabled(false);
        mTvSubTitle.setEnabled(false);

        mAllPhotoFolderList.clear();

        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<PhotoFolderInfo> folders) {
                ILogger.i("loadComplete:" + folders.size());
                if (folders.size() > 0) {
                    mAllPhotoFolderList.addAll(folders);
                    mCurPhotoList.clear();
                    if (folders.get(0).getPhotoList() != null) {
                        mCurPhotoList.addAll(folders.get(0).getPhotoList());
                        mTvSubTitle.setText(mAllPhotoFolderList.get(0).getFolderName());
                    }

                    for (PhotoInfo info : folders.get(0).getPhotoList()){
                        Log.d("hanggao1111",info.getPhotoPath());
                    }

                }else{
                    mGvPhotoList.setEmptyView(mTvEmptyView);
                }

                //gh 加入默认相机
                if (false == Utils.disableTakePhoto() && GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofImage()) {
                    PhotoInfo photoInfo = new PhotoInfo();
                    photoInfo.setPhotoId(-1);
                    photoInfo.setPhotoPath("");
                    mCurPhotoList.add(0, photoInfo);
                }

                refreshAdapter();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) {
            if ( null != mLlFolderPanel && mLlFolderPanel.getVisibility() == View.VISIBLE ) {
                if (mTvSubTitle != null)
                    mTvSubTitle.performClick();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( mHasRefreshGallery) {
            mHasRefreshGallery = false;
            requestGalleryPermission();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if ( GalleryFinal.getCoreConfig() != null &&
                GalleryFinal.getCoreConfig().getImageLoader() != null ) {
            GalleryFinal.getCoreConfig().getImageLoader().clearMemoryCache();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoTargetFolder = null;
        if (mSelectPhotoList != null)
            mSelectPhotoList.clear();
        System.gc();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201 & resultCode == RESULT_OK) {
            mCurPhotoList = GalleryUtils.getInstance().getmCurrent();
            mSelectPhotoList.clear();
            for (PhotoInfo photoInfo : mCurPhotoList) {
                if (photoInfo.isSelect) {
                    mSelectPhotoList.add(photoInfo);
                }
            }
            refreshSelectCount();
            mPhotoListAdapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_OK && requestCode == 909) {
            mCurPhotoList = GalleryUtils.getInstance().getmCurrent();
            isAudio(data);
            // on take photo success
            final File file = new File(cameraPath);
            String toType = PictureMimeType.fileToType(file);
            ILogger.i(this.getClass().getName(), "camera result:" + toType);

            // 生成新拍照片或视频对象
            PhotoInfo media = new PhotoInfo();
            media.setPhotoPath(cameraPath);

            boolean eqVideo = toType.startsWith("video");
            int duration = eqVideo ? PictureMimeType.getLocalVideoDuration(cameraPath) : 0;
            String pictureType = "";
            if (GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofAudio()) {
                pictureType = "audio/mpeg";
                duration = PictureMimeType.getLocalVideoDuration(cameraPath);
            } else {
                pictureType = eqVideo ? PictureMimeType.createVideoType(cameraPath)
                        : PictureMimeType.createImageType(cameraPath);
            }
            media.setPictureType(pictureType);
            media.setDuration(duration);
            media.setMimeType(GalleryFinal.getFunctionConfig().getMimeType());
            media.setSelect(true);

            // 因为加入了单独拍照功能，所有如果是单独拍照的话也默认为单选状态
            if (!GalleryFinal.getFunctionConfig().isMutiSelect()) {
                // 不裁剪 不压缩 直接返回结果
                mCurPhotoList.add(media);
                resultData(mSelectPhotoList);
            } else {
                // 多选 返回列表并选中当前拍照的
                if (false == Utils.disableTakePhoto() && GalleryFinal.getFunctionConfig().getMimeType() != PictureMimeType.ofAll()) {
                    mCurPhotoList.add(1, media);
                } else {
                    mCurPhotoList.add(0, media);
                }

                mSelectPhotoList.clear();
                for (PhotoInfo photoInfo : mCurPhotoList) {
                    if (photoInfo.isSelect) {
                        mSelectPhotoList.add(photoInfo);
                    }
                }
                refreshSelectCount();
            }

            if (mPhotoListAdapter != null) {
                // 解决部分手机拍照完Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                // 不及时刷新问题手动添加
                manualSaveFolder(media);
                mTvEmptyView.setVisibility(mCurPhotoList.size() > 0
                        ? View.INVISIBLE : View.VISIBLE);

                mPhotoListAdapter.notifyDataSetChanged();
            }

            if (GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofImage()) {
                int lastImageId = getLastImageId(eqVideo);
                if (lastImageId != -1) {
                    removeImage(lastImageId, eqVideo);
                }
            }
        }
    }

    private int[] getPicSize(String path){
        int[] picSize = new int[2];
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( path, opts);
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(path, opts);
        picSize[0]=opts.outWidth;
        picSize[1]=opts.outHeight;
        return picSize;
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // 拍照
                requestCamerPermission();

                break;
            case 1:
                // 录视频
                requestVideoPermission();

                break;
        }
    }

    /**
     * 打开相机
     */
    @AfterPermissionGranted(GalleryFinal.PERMISSIONS_CODE_OPEN_CAMER)
    private void requestCamerPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            takePhotoAction();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.permissions_tips_gallery),
                    GalleryFinal.PERMISSIONS_CODE_OPEN_CAMER, Manifest.permission.CAMERA);
        }
    }

    /**
     * 获取所有图片
     */
    @AfterPermissionGranted(GalleryFinal.PERMISSIONS_CODE_OPEN_VIDEO)
    private void requestVideoPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO)) {
            startOpenCameraVideo();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.permissions_tips_video),
                    GalleryFinal.PERMISSIONS_CODE_OPEN_VIDEO, Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO);
        }
    }

    /**
     * 录视频
     */
    public void startOpenCameraVideo() {
        if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
            GalleryUtils.getInstance().setmCurrent(mCurPhotoList);
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = PictureFileUtils.createCameraFile(this, GalleryFinal.getFunctionConfig().getMimeType() ==
                            FunctionConfig.TYPE_ALL ? FunctionConfig.TYPE_VIDEO : GalleryFinal.getFunctionConfig().getMimeType(),
                    "", ".JPEG");
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(cameraIntent, 909);
        }
    }
    /**
     * 手动添加拍照后的相片到图片列表，并设为选中
     *
     * @param media
     */
    private void manualSaveFolder(PhotoInfo media) {
        try {
            createNewFolder(mAllPhotoFolderList);
            PhotoFolderInfo folder = getImageFolder(media.getPhotoPath(), mAllPhotoFolderList);
            PhotoFolderInfo cameraFolder = mAllPhotoFolderList.size() > 0 ? mAllPhotoFolderList.get(0) : null;
            if (cameraFolder != null && folder != null) {
                // 相机胶卷
                cameraFolder.setFirstImagePath(media.getPhotoPath());
                cameraFolder.setPhotoList(mCurPhotoList);
                cameraFolder.setImageNum(cameraFolder.getImageNum() + 1);
                // 拍照相册
                int num = folder.getImageNum() + 1;
                folder.setImageNum(num);
                folder.getPhotoList().add(0, media);
                folder.setFirstImagePath(cameraPath);
                mFolderListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
