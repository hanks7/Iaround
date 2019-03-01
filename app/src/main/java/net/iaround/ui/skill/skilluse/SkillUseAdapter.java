package net.iaround.ui.skill.skilluse;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.skill.SkillUsedItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.skill.RecycleItemClickListener;
import net.iaround.ui.view.RatingBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：zx on 2017/8/15 17:44
 */
public class SkillUseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private int mSelectedItem = -1;
    private RecycleItemClickListener<SkillUsedItemBean> mListener;
    private List<SkillUsedItemBean> dataList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEAT = 1;

    public SkillUseAdapter(RecycleItemClickListener<SkillUsedItemBean> mListener) {
        this.dataList = new ArrayList<>();
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_SEAT == viewType){
            View seatView = LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.item_use_skill_seat_view, parent, false);
            return new SeatViewHolder(seatView);
        }else if (TYPE_ITEM == viewType){
            View view = LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.item_use_skill, parent, false);
            return new SkillUseViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SkillUseViewHolder){
            SkillUseViewHolder holder = (SkillUseViewHolder) viewHolder;
            if (null != dataList && dataList.size() > 0){
                SkillUsedItemBean itemBean = dataList.get(position);
                holder.ratingBarView.setStarView(itemBean.mySkillLevel);
                if (null != itemBean.props){
                    holder.skill_name.setText(CommonFunction.getLangText(itemBean.props.get(0).name));
                }
                GlideUtil.loadImage(BaseApplication.appContext, itemBean.skillBackIcon, holder.skillIcon);

                if (position == mSelectedItem){
                    holder.item_ll.setBackgroundResource(R.drawable.selected_frame_shadow);
                    holder.iv_skill_first.setBackgroundDrawable(null);
                    playFrame(BaseApplication.appContext, itemBean.skillGiftIcon, holder.iv_skill_ani);
                }else {
                    holder.item_ll.setBackgroundResource(R.color.white);
                    stopFrameAnim(holder.iv_skill_ani);
                    setFirstFrameImage(BaseApplication.appContext, itemBean.skillGiftIcon, holder.iv_skill_first);
                }
                if (0 == itemBean.status){//置灰背景
                    iconToGray(0.3f, holder.skillIcon, holder.iv_skill_first, holder.iv_skill_ani);
                }else {
                    iconToGray(1, holder.skillIcon, holder.iv_skill_first, holder.iv_skill_ani);
                }
                holder.item_ll.setOnClickListener(this);
                holder.item_ll.setTag(holder);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()){
            return TYPE_SEAT;
        }else{
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (null != dataList && dataList.size() > 0){
            return dataList.size() + 1;
        }
        return 6;
    }

    public void updateList(List<SkillUsedItemBean> datas){
        if (null != datas && datas.size() > 0){
            dataList.clear();
            dataList.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public SkillUsedItemBean updatePositionView(int position, SkillAttackResult attackResult) {
        if (null != dataList && dataList.size() > 0){
            SkillUsedItemBean itemBean = dataList.get(position);
            itemBean.mySkillLevel = attackResult.userSkillLevel;
            itemBean.otherSkillLevel = attackResult.targetSkillLevel;
            if (null != attackResult.propsNum){
                itemBean.props.get(0).num = attackResult.propsNum.num;
                itemBean.props.get(0).needNum = attackResult.propsNum.needNum;
            }
            dataList.remove(position);
            dataList.add(position, itemBean);
            notifyDataSetChanged();
            return itemBean;
        }

        return null;
    }

    public void setmSelectedItem(int mSelectedItem) {
        this.mSelectedItem = mSelectedItem;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_ll:
                SkillUseViewHolder holder = (SkillUseViewHolder) v.getTag();
                if (mListener != null) {
                    mListener.onItemClick(holder.getPosition(), dataList.get(holder.getPosition()));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置首帧图
     * @param context
     * @param skillName
     * @param imageView
     */
    private void setFirstFrameImage(Context context, String skillName, ImageView imageView){
        if (TextUtils.isEmpty(skillName)){
            stopFrameAnim(imageView);
            return;
        }
        String[] split = skillName.split("_");
        skillName = split[0];
        if ("burst".equals(skillName)){
            skillName = "burst_2700_47_first";
        }else if ("kick".equals(skillName)){
            skillName = "kick_1000_15_first";
        }else if ("kiss".equals(skillName)){
            skillName = "kiss_1600_19_first";
        }else if ("protect".equals(skillName)){
            skillName = "protect_2000_29_first";
        }else if ("speak".equals(skillName)){
            skillName = "speak_1900_25_first";
        }
        int drawableId = context.getResources().getIdentifier(skillName, "drawable", context.getPackageName());
        imageView.setBackgroundResource(drawableId);
    }

    /**
     * 停止播放动画
     * @param iv_frame
     */
    private void stopFrameAnim(ImageView iv_frame){
        iv_frame.setBackgroundDrawable(null);
    }

    /**
     * 播放帧动画
     * @param context
     * @param skillName
     * @param iv_frame
     */
    private void playFrame(Context context, String skillName, final ImageView iv_frame){
        String[] split = skillName.split("_");
        skillName = "frame_" + split[0];
        int drawableId = context.getResources().getIdentifier(skillName, "drawable", context.getPackageName());
        AnimationDrawable frameAnim = (AnimationDrawable) context.getResources().getDrawable(drawableId);
        iv_frame.setBackgroundDrawable(frameAnim);
        frameAnim.start();
    }

    private void iconToGray(float saturation, ImageView...imageViews){
        for (ImageView iv : imageViews){
            iv.setAlpha(saturation);
        }
    }

    public class SkillUseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.skill_icon) ImageView skillIcon;
        @BindView(R.id.iv_skill_ani) ImageView iv_skill_ani;
        @BindView(R.id.iv_skill_first) ImageView iv_skill_first;
        @BindView(R.id.ratingBarView) RatingBarView ratingBarView;
        @BindView(R.id.item_ll) LinearLayout item_ll;
        @BindView(R.id.skill_name) TextView skill_name;

        public SkillUseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public class SeatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.skill_icon) ImageView skillIcon;
        public SeatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
