package net.iaround.ui.skill.skillpropshop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.skill.PropItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.skill.RecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：zx on 2017/8/18 11:56
 */
public class SkillPropsShopAdapter extends RecyclerView.Adapter<SkillPropsShopAdapter.SkillPropsShopViewHolder> implements View.OnClickListener {

    private int mSelectedItem = 0;//当前选中的条目，默认选中第一个元素
    private List<PropItemBean> mDataList;
    private Context mContext;
    private RecycleItemClickListener<PropItemBean> mItemClickListener;

    public SkillPropsShopAdapter(RecycleItemClickListener<PropItemBean> itemClickListener) {
        this.mItemClickListener = itemClickListener;
        mDataList = new ArrayList<>();
        mContext = BaseApplication.appContext;
    }

    @Override
    public SkillPropsShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_moon_charge, parent, false);
        return new SkillPropsShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SkillPropsShopViewHolder holder, int position) {
        if (null != mDataList && mDataList.size() > 0) {
            PropItemBean itemBean = mDataList.get(position);
            holder.tvPropsName.setText(CommonFunction.getLangText(itemBean.Name));
            holder.tvPropsNum.setText("X" + itemBean.Num);
            holder.tvExpend.setText(itemBean.CurrencyNum + (itemBean.CurrencyType == 1 ? mContext.getString(R.string.pay_gold) : itemBean.CurrencyType ==2 ? mContext.getString(R.string.diamond) : itemBean.CurrencyType ==6 ? mContext.getString(R.string.stars) : ""));
            String successRate = null;
            if (itemBean.PopsID != null && itemBean.PopsID.equals("10002")) { //金月亮
                successRate = mContext.getString(R.string.skill_update_success_rate) + "<font color='#ff4064'>" + "+" + itemBean.SkillUpdateRate + "%" + "</font>";
                holder.tvUpdateSuccessful.setText(Html.fromHtml(successRate));
            } else if (itemBean.PopsID != null && itemBean.PopsID.equals("10001")) { //银月亮
                successRate = mContext.getString(R.string.skill_update_tool);
                holder.tvUpdateSuccessful.setText(successRate);
            }

            GlideUtil.loadImage(BaseApplication.appContext, itemBean.ICON, holder.ivProps);

            if (position == mSelectedItem) {
                holder.item_ll.setBackgroundResource(R.drawable.user_vip_open_frame_blue);
            } else {
                holder.item_ll.setBackgroundResource(R.color.white);
            }

            holder.item_ll.setOnClickListener(this);
            holder.item_ll.setTag(holder);
        }

    }

    @Override
    public int getItemCount() {
        if (null != mDataList && mDataList.size() > 0) {
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_ll:
                SkillPropsShopViewHolder holder = (SkillPropsShopViewHolder) v.getTag();
                mSelectedItem = holder.getPosition();
                notifyDataSetChanged();
                if (null != mItemClickListener) {
                    mItemClickListener.onItemClick(holder.getPosition(), mDataList.get(mSelectedItem));
                }
                break;
            default:
                break;
        }
    }

    public void updateDatas(List<PropItemBean> list) {
        if (null != list && list.size() > 0) {
            mDataList.clear();
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public int getSelectItem() {
        return mSelectedItem;
    }

    public class SkillPropsShopViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_props_name)
        TextView tvPropsName;
        @BindView(R.id.iv_props)
        ImageView ivProps;
        @BindView(R.id.tv_props_num)
        TextView tvPropsNum;
        @BindView(R.id.tv_update_successful)
        TextView tvUpdateSuccessful;
        @BindView(R.id.tv_expend)
        TextView tvExpend;
        @BindView(R.id.item_ll)
        LinearLayout item_ll;

        public SkillPropsShopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
