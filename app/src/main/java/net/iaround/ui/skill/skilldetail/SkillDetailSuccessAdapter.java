package net.iaround.ui.skill.skilldetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.skill.SkillDetailBean;

import java.util.List;

/**
 * 技能详情成功率适配器
 * Created by gh on 2017/11/9.
 */

public class SkillDetailSuccessAdapter extends BaseAdapter {

    private Context mContext;
    private List<SkillDetailSuccessBean> mDataList;

    public SkillDetailSuccessAdapter(Context mContext, List<SkillDetailSuccessBean> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    /**
     * 更新技能成功率选项
     * @param mDataList
     */
    public void updateData(List<SkillDetailSuccessBean> mDataList){
        this.mDataList = mDataList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_skill_detail_success, null);
            holder.tv_base_success = (TextView) convertView.findViewById(R.id.tv_base_success);
            holder.tv_acer_success = (TextView) convertView.findViewById(R.id.tv_acer_success);
            holder.tv_ranking = (TextView) convertView.findViewById(R.id.tv_ranking);
            holder.tv_diamond_success_rate = (TextView) convertView.findViewById(R.id.tv_diamond_success_rate);
            holder.tv_recharge_upgrade = (TextView) convertView.findViewById(R.id.tv_recharge_upgrade);
            holder.tv_recharge_upgrade_star = (TextView) convertView.findViewById(R.id.tv_recharge_upgrade_star);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_base_success.setVisibility(View.GONE);
        holder.tv_acer_success.setVisibility(View.GONE);
        holder.tv_ranking.setVisibility(View.GONE);
        holder.tv_diamond_success_rate.setVisibility(View.GONE);
        holder.tv_recharge_upgrade.setVisibility(View.GONE);
        holder.tv_recharge_upgrade_star.setVisibility(View.GONE);
        SkillDetailSuccessBean bean = mDataList.get(position);

        switch (bean.type){
            case 1:
                holder.tv_base_success.setVisibility(View.VISIBLE);
                holder.tv_base_success.setText(BaseApplication.appContext.getString(R.string.base_success_rate) + bean.success + "%");
                break;
            case 2:
                holder.tv_acer_success.setVisibility(View.VISIBLE);
                holder.tv_acer_success.setText(BaseApplication.appContext.getString(R.string.props_addition) + bean.success + "%");
                break;
            case 3:
                holder.tv_diamond_success_rate.setVisibility(View.VISIBLE);
                holder.tv_diamond_success_rate.setText(BaseApplication.appContext.getString(R.string.diamond_update_addition) + bean.success + "%");
                break;
            case 4:
                holder.tv_ranking.setVisibility(View.VISIBLE);
                holder.tv_ranking.setText(BaseApplication.appContext.getString(R.string.rank_addition) + bean.success + "%");
                break;
            case 5:
                holder.tv_recharge_upgrade.setVisibility(View.VISIBLE);
                holder.tv_recharge_upgrade.setText(BaseApplication.appContext.getString(R.string.recharge_upgrade) + bean.success + "%");
                break;
            case 6:
                holder.tv_recharge_upgrade_star.setVisibility(View.VISIBLE);
                holder.tv_recharge_upgrade_star.setText(BaseApplication.appContext.getString(R.string.star_upgrade) + bean.success + "%");
                break;

        }

        return convertView;
    }

    class ViewHolder {
        public TextView tv_base_success;
        public TextView tv_acer_success;
        public TextView tv_ranking;
        public TextView tv_diamond_success_rate;
        public TextView tv_recharge_upgrade;
        public TextView tv_recharge_upgrade_star;
    }
}
