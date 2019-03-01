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

package cn.finalteam.galleryfinal.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PictureMimeType;
import cn.finalteam.galleryfinal.R;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.utils.DateUtils;
import cn.finalteam.galleryfinal.utils.StringUtils;
import cn.finalteam.galleryfinal.utils.Utils;
import cn.finalteam.galleryfinal.widget.GFImageView;
import cn.finalteam.toolsfinal.adapter.ViewHolderAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/10 下午4:59
 */
public class PhotoListAdapter extends ViewHolderAdapter<PhotoListAdapter.PhotoViewHolder, PhotoInfo> {

    private List<PhotoInfo> mSelectList;
    private int mScreenWidth;
    private int mRowWidth;

    private Activity mActivity;

    private OnCheckListener onCheckListener;

    private Map<String,ImageView> map = new HashMap<>();

    public PhotoListAdapter(Activity activity, List<PhotoInfo> list, List<PhotoInfo> selectList, int screenWidth) {
        super(activity, list);
        this.mSelectList = selectList;
        this.mScreenWidth = screenWidth;
        this.mRowWidth = mScreenWidth/3;
        this.mActivity = activity;
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    /**
     * 更新适配器
     * @param mPhoto
     */
    public void updateData(PhotoInfo mPhoto){
        for (PhotoInfo photoInfo : mSelectList){
            if (photoInfo.getPhotoId() != mPhoto.getPhotoId()){
                this.mSelectList.clear();
            }
        }
    }

    public List<PhotoInfo> getSelectedImages() {
        if (mSelectList == null) {
            mSelectList = new ArrayList<>();
        }
        return mSelectList;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflate(R.layout.gf_adapter_photo_list_item, parent);
        setHeight(view);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        PhotoInfo photoInfo = getDatas().get(position);
        holder.mIvThumb.setTag(getDatas().get(position).getPhotoPath());
        if (photoInfo.getPhotoId() == -1){
            holder.mIvCheck.setVisibility(View.GONE);
            holder.mIvThumb.setVisibility(View.GONE);
            if(false == Utils.disableTakePhoto()) {
                holder.tackPhoto.setVisibility(View.VISIBLE);
                holder.mView.setAnimation(null);
            }else{
                holder.tackPhoto.setVisibility(View.GONE);
                holder.mView.setAnimation(null);
                holder.mView.setVisibility(View.GONE);
            }
        }else{
            holder.tackPhoto.setVisibility(View.GONE);
            holder.mIvCheck.setVisibility(View.VISIBLE);
            holder.mIvThumb.setVisibility(View.VISIBLE);
            String path = "";
            if (photoInfo != null) {
                path = photoInfo.getPhotoPath();
            }

            Drawable defaultDrawable = mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo);
            GalleryFinal.getCoreConfig().getImageLoader().displayImage(mActivity, path, holder.mIvThumb, defaultDrawable, mRowWidth, mRowWidth);
            holder.mView.setAnimation(null);
            if (GalleryFinal.getCoreConfig().getAnimation() > 0) {
                holder.mView.setAnimation(AnimationUtils.loadAnimation(mActivity, GalleryFinal.getCoreConfig().getAnimation()));
            }
            holder.mIvCheck.setImageResource(GalleryFinal.getGalleryTheme().getIconCheck());
            if ( GalleryFinal.getFunctionConfig().isMutiSelect() ) {
                holder.mIvCheck.setVisibility(View.VISIBLE);
                if (mSelectList.contains(photoInfo) || photoInfo.isSelect) {
                    holder.mIvCheck.setBackgroundColor(mActivity.getResources().getColor(R.color.ok_pressed));
                } else {
                    holder.mIvCheck.setBackgroundColor(mActivity.getResources().getColor(R.color.select_normal));
                }
            } else {
                holder.mIvCheck.setVisibility(View.GONE);
            }
            final String pictureType = photoInfo.getPictureType();
            final int picture = PictureMimeType.isPictureType(pictureType);
            if (GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofAudio()) {
                holder.tv_duration.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.picture_audio);
                StringUtils.modifyTextViewDrawable(holder.tv_duration, drawable, 0);
            } else {
                Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(holder.tv_duration, drawable, 0);
                holder.tv_duration.setVisibility(picture == FunctionConfig.TYPE_VIDEO
                        ? View.VISIBLE : View.GONE);
            }
            long duration = photoInfo.getDuration();
            holder.tv_duration.setText(DateUtils.timeParse(duration));

            if (!GalleryFinal.getFunctionConfig().isMutiSelect()){
                holder.mIvCheck.setVisibility(View.GONE);
            }

            holder.mIvCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCheckListener){
                        onCheckListener.onCheck(holder.mView,position);
                    }
                }
            });
        }


    }

    private void setHeight(final View convertView) {
        int height = mScreenWidth / 3 - 8;
        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }

    public static class PhotoViewHolder extends ViewHolderAdapter.ViewHolder {

        public GFImageView mIvThumb;
        public RelativeLayout tackPhoto;
        public ImageView mIvCheck;
        private TextView tv_duration;
        View mView;

        public PhotoViewHolder(View view) {
            super(view);
            mView = view;
            mIvThumb = (GFImageView) view.findViewById(R.id.iv_thumb);
            tackPhoto = (RelativeLayout) view.findViewById(R.id.rl_tack_photo);
            mIvCheck = (ImageView) view.findViewById(R.id.iv_check);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
        }
    }

    public interface OnCheckListener{
        void onCheck(View view, int position);
    }
}
