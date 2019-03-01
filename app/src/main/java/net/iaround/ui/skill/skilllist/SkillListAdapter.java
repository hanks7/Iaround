package net.iaround.ui.skill.skilllist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.android.percent.support.PercentRelativeLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.skill.SkillBean;
import net.iaround.model.skill.SkillOpenBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.skill.RecycleItemClickListener;
import net.iaround.ui.skill.skilluse.SkillUseAdapter;
import net.iaround.ui.view.RatingBarView;
import net.iaround.utils.SkillHandleUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：zx on 2017/8/9 18:04
 */
public class SkillListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private List<SkillBean> dataList;
    private RecycleItemClickListener mItemClickListener;

    public SkillListAdapter(RecycleItemClickListener itemClickListener) {
        this.dataList = new ArrayList<>();
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_HEADER == viewType){
            View headerView = LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.item_use_skill_header_view, parent, false);
            return new HeaderViewHolder(headerView);
        }else if (TYPE_ITEM == viewType){
            View view = LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.item_skill_list, parent, false);
            return new SkillListViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder){
            return;
        }
        SkillListViewHolder holder = (SkillListViewHolder) viewHolder;
        if (null != dataList && dataList.size() > 0){
            SkillBean skillBean = dataList.get(position - 1);
            GlideUtil.loadImage(BaseApplication.appContext, skillBean.Icon, holder.skill_icon);
            SkillHandleUtils.setFirstFrameImage(BaseApplication.appContext, skillBean.Gif, holder.iv_skill_first);
            holder.skill_name.setText(CommonFunction.getLangText(skillBean.Name));
            holder.skill_name_icon_below.setText(CommonFunction.getLangText(skillBean.Name));
            holder.tv_open_desc.setText(BaseApplication.appContext.getString(R.string.come_as)+ skillBean.NeedUserLevel +BaseApplication.appContext.getString(R.string.open_skill_level));
            if (0 == skillBean.Status){//未开启显示开启条件文案
                holder.tv_open_desc.setVisibility(View.VISIBLE);
                holder.ratingBarView.setVisibility(View.GONE);

            }else {
                holder.tv_open_desc.setVisibility(View.GONE);
                holder.ratingBarView.setVisibility(View.VISIBLE);
            }

            holder.skill_desc.setText(CommonFunction.getLangText(skillBean.Desc));
            holder.ratingBarView.setStarView(skillBean.Level);
            holder.btn_update.setText(skillBean.Status == 1 ?  BaseApplication.appContext.getString(R.string.update) : BaseApplication.appContext.getString(R.string.setting_notice_receive_new_msg_open));
            holder.btn_update.setBackgroundResource(skillBean.Status > 0 ? R.drawable.touch_bg_red : R.drawable.touch_bg_gray);
            holder.btn_update.setOnClickListener(this);
            holder.btn_update.setTag(holder);
            holder.ctl.setOnClickListener(this);
            holder.ctl.setTag(holder);
        }
    }

    @Override
    public int getItemCount() {
        if (null != dataList && dataList.size() > 0){
            return dataList.size() + 1;
        }
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position){
            return TYPE_HEADER;
        }else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:
            case R.id.ctl:
                SkillListViewHolder holder1 = (SkillListViewHolder) v.getTag();
                int position = holder1.getPosition();
                if (position > 0){
                    if (null != mItemClickListener){
                        mItemClickListener.onItemClick(position - 1, dataList.get(position - 1));
                    }
                }
                break;
        }
    }

    public void updateList(List<SkillBean> datas){
        if (null != datas  && datas.size() > 0){
            dataList.clear();
            dataList.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void updatePositionView(int position, SkillOpenBean.SkillOpenItem skill) {
        if (null != dataList && dataList.size() > 0){
            SkillBean skillBean = dataList.get(position);
            skillBean.Status = skill.Status;
            skillBean.Level = skill.Level;
            dataList.remove(position);
            dataList.add(position, skillBean);
            notifyDataSetChanged();
        }
    }

    public void updatePositionLevel(int position, int skillLevel) {
        if (null != dataList && dataList.size() > 0){
            SkillBean skillBean = dataList.get(position);
            skillBean.Level = skillLevel;
            dataList.remove(position);
            dataList.add(position, skillBean);
            notifyDataSetChanged();
        }
    }

    public class SkillListViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.skill_icon) ImageView skill_icon;
        @BindView(R.id.iv_skill_first) ImageView iv_skill_first;
        @BindView(R.id.skill_name) TextView skill_name;
        @BindView(R.id.skill_name_icon_below) TextView skill_name_icon_below;
        @BindView(R.id.ratingBarView) RatingBarView ratingBarView;
        @BindView(R.id.skill_desc) TextView skill_desc;
        @BindView(R.id.tv_open_desc) TextView tv_open_desc;
        @BindView(R.id.btn_update) Button btn_update;
        @BindView(R.id.ctl) PercentRelativeLayout ctl;

        public SkillListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
