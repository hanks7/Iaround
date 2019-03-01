package net.iaround.ui.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.entity.VideoAnchorBean.VideoAnchorItem;
import net.iaround.model.entity.VideoAnchorsItem;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;

import java.util.List;

/**
 * 主播视频列表适配
 * Created by gh on 2017/11/13.
 */

public class VideoAnchorAdapter extends BaseAdapter {

    private Context mContext;
    private List<VideoAnchorsItem> mDatas;

    private OnItemVideoAnchorListener onItemVideoAnchorListener;

    public VideoAnchorAdapter(Context mContext, List<VideoAnchorsItem> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    /**
     * 更细适配器
     *
     * @param mDatas
     */
    public void updateData(List<VideoAnchorsItem> mDatas) {
        this.mDatas = mDatas;
        this.notifyDataSetChanged();
    }

    /**
     * 设置点击监听
     * @param onItemVideoAnchorListener
     */
    public void setOnItemVideoAnchorListener(OnItemVideoAnchorListener onItemVideoAnchorListener) {
        this.onItemVideoAnchorListener = onItemVideoAnchorListener;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public VideoAnchorsItem getItem(int position) {
        return mDatas != null ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VideoAnchorsItem item = getItem(position);

        VideoAnchorHolder videoAnchorHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_video_anchor, null);
            videoAnchorHolder = new VideoAnchorHolder(convertView);
            convertView.setTag(videoAnchorHolder);
        } else {
            videoAnchorHolder = (VideoAnchorHolder) convertView.getTag();
        }

        if (videoAnchorHolder instanceof VideoAnchorHolder) {
            if (item.leftUser != null) {
                videoAnchorHolder.layoutLeft.setVisibility(View.VISIBLE);
                referView(videoAnchorHolder.bgLeft, item.leftUser.pic, videoAnchorHolder.stateLeft, videoAnchorHolder.stateLeftIv, item.leftUser.status, item.leftUser.nickName, item.leftUser.moodText, videoAnchorHolder.nameLeft, videoAnchorHolder.moodTextLeft, item.leftUser.age, videoAnchorHolder.ageLeft, item.leftUser.gender, videoAnchorHolder.sexLeft, videoAnchorHolder.sexLeftLy,item.leftUser.notes,item.leftUser.uid);
                videoAnchorHolder.layoutLeft.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (onItemVideoAnchorListener != null && item.leftUser != null) {
                            onItemVideoAnchorListener.onItemVideo(item.leftUser);
                        }
                    }
                });
            } else {
                videoAnchorHolder.layoutLeft.setVisibility(View.INVISIBLE);
            }

            if (item.rightUser != null) {
                videoAnchorHolder.layoutRight.setVisibility(View.VISIBLE);
                referView(videoAnchorHolder.bgRight, item.rightUser.pic, videoAnchorHolder.stateRight, videoAnchorHolder.stateRightIv, item.rightUser.status, item.rightUser.nickName, item.rightUser.moodText, videoAnchorHolder.nameRight, videoAnchorHolder.moodTextRight, item.rightUser.age, videoAnchorHolder.ageRight, item.rightUser.gender, videoAnchorHolder.sexRight, videoAnchorHolder.sexRightLy,item.rightUser.notes,item.rightUser.uid);
                videoAnchorHolder.layoutRight.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (onItemVideoAnchorListener != null && item.rightUser != null) {
                            onItemVideoAnchorListener.onItemVideo(item.rightUser);
                        }
                    }
                });
            } else {
                videoAnchorHolder.layoutRight.setVisibility(View.INVISIBLE);
            }
        }


        return convertView;
    }

    /**
     * 更新View
     *
     * @param picIv
     * @param pic
     * @param stateTv
     * @param stateIv
     * @param state
     * @param nichName
     * @param moodText
     * @param nameTv
     * @param moodTextTv
     * @param age
     * @param ageTv
     * @param gender
     * @param sexIv
     * @return
     */
    private boolean referView(ImageView picIv, String pic, TextView stateTv, ImageView stateIv, int state, String nichName, String moodText, TextView nameTv, TextView moodTextTv, int age, TextView ageTv, String gender, ImageView sexIv, LinearLayout sexLy,String noteStr,long uid) {

        GlideUtil.loadImage(mContext, pic, picIv);

        switch (state) {
            case 3:
                stateIv.setBackgroundResource(R.drawable.video_anchor_disturb);
                stateTv.setText(mContext.getString(R.string.video_anchor_disturb_state));
                break;

            case 1:
                stateIv.setBackgroundResource(R.drawable.video_anchor_free);
                stateTv.setText(mContext.getString(R.string.video_anchor_free_state));
                break;

            case 2:
                stateIv.setBackgroundResource(R.drawable.video_anchor_hot_chat);
                stateTv.setText(mContext.getString(R.string.video_anchor_hot_chat_state));
                break;
        }

        if (moodText == null){
            moodTextTv.setText(mContext.getString(R.string.signature_empty_tips));
        }else{
            if (TextUtils.isEmpty(moodText)){
                moodTextTv.setText(mContext.getString(R.string.signature_empty_tips));
            }else{
                moodTextTv.setText(FaceManager.getInstance(mContext)
                        .parseIconForString(mContext, moodText, 0, null));
            }
        }

        if (gender.equals("m")) {
            sexIv.setImageResource(R.drawable.thread_register_man_select);
            sexLy.setBackgroundResource(R.drawable.encounter_man_circel_bg);

        } else {
            sexIv.setImageResource(R.drawable.thread_register_woman_select);
            sexLy.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
        }
        try {
            ageTv.setText("" + age);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name = nichName;
        SpannableString spName;
        if (TextUtils.isEmpty(name) || name == null) {
            if (!"".equals(noteStr) && noteStr != null) {
                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, noteStr,
                        0, null);
            } else {
                if (!TextUtils.isEmpty(nichName)) {
                    spName = FaceManager.getInstance(mContext).parseIconForString(mContext, nichName,
                            0, null);
                } else {
                    spName = FaceManager.getInstance(mContext).parseIconForString(mContext
                            , String.valueOf(uid), 0, null);
                }
            }

            nameTv.setText(spName);
        } else if (!TextUtils.isEmpty(name) || name != null) {
            spName = FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, name, 0, null);
            nameTv.setText(spName);
        }

        return false;
    }

    public interface OnItemVideoAnchorListener{
        void onItemVideo(VideoAnchorItem videoAnchorItem);
    }

    class VideoAnchorHolder {

        private LinearLayout layoutLeft;
        private LinearLayout layoutRight;

        private LinearLayout sexLeftLy;
        private LinearLayout sexRightLy;

        private ImageView bgLeft;
        private ImageView bgRight;

        private TextView stateLeft;
        private TextView stateRight;

        private ImageView stateLeftIv;
        private ImageView stateRightIv;

        private ImageView sexLeft;
        private ImageView sexRight;

        private TextView nameLeft;
        private TextView nameRight;

        private TextView ageLeft;
        private TextView ageRight;

        private TextView moodTextLeft;
        private TextView moodTextRight;

        public VideoAnchorHolder(View view) {
            layoutLeft = (LinearLayout) view.findViewById(R.id.ly_video_anchor_left);
            layoutRight = (LinearLayout) view.findViewById(R.id.ly_video_anchor_right);

            sexLeftLy = (LinearLayout) view.findViewById(R.id.ly_user_age_left);
            sexRightLy = (LinearLayout) view.findViewById(R.id.ly_user_age_right);

            bgLeft = (ImageView) view.findViewById(R.id.iv_video_anchor_left_bg);
            bgRight = (ImageView) view.findViewById(R.id.iv_video_anchor_right_bg);

            stateLeft = (TextView) view.findViewById(R.id.tv_video_anchor_state_left);
            stateRight = (TextView) view.findViewById(R.id.tv_video_anchor_state_right);

            stateLeftIv = (ImageView) view.findViewById(R.id.iv_video_anchor_state_left);
            stateRightIv = (ImageView) view.findViewById(R.id.iv_video_anchor_state_right);

            nameLeft = (TextView) view.findViewById(R.id.tv_video_anchor_name_left);
            nameRight = (TextView) view.findViewById(R.id.tv_video_anchor_name_right);

            sexLeft = (ImageView) view.findViewById(R.id.iv_user_sex_left);
            sexRight = (ImageView) view.findViewById(R.id.iv_user_sex_right);

            ageLeft = (TextView) view.findViewById(R.id.tv_user_age_left);
            ageRight = (TextView) view.findViewById(R.id.tv_user_age_right);

            moodTextLeft = (TextView) view.findViewById(R.id.tv_video_anchor_moodtext_left);
            moodTextRight = (TextView) view.findViewById(R.id.tv_video_anchor_moodtext_right);

        }
    }



}
