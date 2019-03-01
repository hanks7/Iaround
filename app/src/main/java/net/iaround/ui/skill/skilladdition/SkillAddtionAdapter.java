package net.iaround.ui.skill.skilladdition;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.skill.SkillAdditionBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：zx on 2017/8/26 14:36
 */
public class SkillAddtionAdapter extends RecyclerView.Adapter<SkillAddtionAdapter.SkillAddtionViewHolder> {


    private List<SkillAdditionBean.AddtionRankBean> dataList;

    private int type = 0;//默认排行榜,充值成功率1

    public SkillAddtionAdapter() {
        this.dataList = new ArrayList<>();
    }

    @Override
    public SkillAddtionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.item_skill_addtion, parent, false);
        return new SkillAddtionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SkillAddtionViewHolder holder, int position) {
        if (null != dataList && dataList.size() > 0) {
            SkillAdditionBean.AddtionRankBean additionBean = dataList.get(position);
            if (position % 2 == 0){
                holder.rootLl.setBackgroundResource(R.color.white);
            }else {
                holder.rootLl.setBackgroundResource(R.color.light_gray);
            }

            if (type == 0){
                holder.tvRank.setText(additionBean.rankingNum);
                holder.tvGold.setText(additionBean.goldRate + "%");
                holder.tvCharm.setText(additionBean.charRate + "%");
            }else{
                holder.tvRank.setText(additionBean.amountNum + "RMB");
                holder.tvGold.setText(additionBean.updateRank + "%");
                holder.tvCharm.setText(additionBean.time + BaseApplication.appContext.getString(R.string.hour));
            }

        }
    }

    @Override
    public int getItemCount() {
        if (null != dataList && dataList.size() > 0) {
            return dataList.size();
        }
        return 0;
    }

    public void updateList(List<SkillAdditionBean.AddtionRankBean> datas){
        if (null != datas  && datas.size() > 0){
            dataList.clear();
            dataList.addAll(datas);
            notifyDataSetChanged();
        }
    }
    public void updateList(int type,List<SkillAdditionBean.AddtionRankBean> datas){
        this.type = type;
        if (null != datas  && datas.size() > 0){
            dataList.clear();
            dataList.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public class SkillAddtionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_rank) TextView tvRank;
        @BindView(R.id.tv_gold) TextView tvGold;
        @BindView(R.id.tv_charm) TextView tvCharm;
        @BindView(R.id.root_ll) LinearLayout rootLl;

        public SkillAddtionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
