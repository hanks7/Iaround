package net.iaround.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.IncomeDetailBean;
import net.iaround.model.entity.IncomeDetailEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 主播每日明细界面Adapter
 * Created by Administrator on 2018/4/11.
 */

public class IncomeDetailAdapter extends BaseExpandableListAdapter {

    private LinkedHashMap<String,ArrayList<IncomeDetailEntity.DailyDetail>> mStringDailyDetailHashMap;

    private ArrayList<IncomeDetailBean> mIncomeDetailBeanArrayList;

    private Object[] objects;

        private Context mContext;

        public IncomeDetailAdapter(Context context) {
        mContext = context;
    }

    public void updateList(LinkedHashMap<String,ArrayList<IncomeDetailEntity.DailyDetail>> stringDailyDetailHashMap,ArrayList<IncomeDetailBean> incomeDetailBeanArrayList) {
        if(stringDailyDetailHashMap == null){
            return;
        }
        mIncomeDetailBeanArrayList = incomeDetailBeanArrayList;
        mStringDailyDetailHashMap = stringDailyDetailHashMap;
        objects = mStringDailyDetailHashMap.keySet().toArray();
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return mStringDailyDetailHashMap == null ? 0:mStringDailyDetailHashMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(mStringDailyDetailHashMap != null){
            ArrayList<IncomeDetailEntity.DailyDetail> dailyDetails = mStringDailyDetailHashMap.get(objects[groupPosition]);
            return dailyDetails == null ? 0:dailyDetails.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mIncomeDetailBeanArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mStringDailyDetailHashMap.get(objects[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderTitle viewHolderTitle = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_daily_detail_title, parent, false);
            viewHolderTitle = new ViewHolderTitle();
            viewHolderTitle.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolderTitle.tvMonthTotal = (TextView) convertView.findViewById(R.id.tv_month_total);
            viewHolderTitle.tvMonthCash = (TextView) convertView.findViewById(R.id.tv_month_cash);
            viewHolderTitle.ll_month_total = (LinearLayout) convertView.findViewById(R.id.ll_month_total);
            convertView.setTag(viewHolderTitle);
        }else {
            viewHolderTitle = (ViewHolderTitle) convertView.getTag();
        }

        IncomeDetailBean incomeDetailBean = mIncomeDetailBeanArrayList.get(groupPosition);

        viewHolderTitle.tvMonthTotal.setText(incomeDetailBean.monthtotal);
        viewHolderTitle.tvMonthCash.setText(incomeDetailBean.monthcash);
        viewHolderTitle.tvTitle.setText(incomeDetailBean.earntime);
        viewHolderTitle.ll_month_total.setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderContent viewHolderContent = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_daily_detail, null);
            viewHolderContent = new ViewHolderContent();
            viewHolderContent.tvDatetime = (TextView) convertView.findViewById(R.id.tv_datetime);
            viewHolderContent.tvTotal = (TextView) convertView.findViewById(R.id.tv_total);
            viewHolderContent.tvTip = (TextView) convertView.findViewById(R.id.tv_tip);
            convertView.setTag(viewHolderContent);
        } else {
            viewHolderContent = (ViewHolderContent) convertView.getTag();
        }
        String key = objects[groupPosition].toString();
        ArrayList<IncomeDetailEntity.DailyDetail> dailyDetails = mStringDailyDetailHashMap.get(key);
        IncomeDetailEntity.DailyDetail dailyDetail = dailyDetails.get(childPosition);
        viewHolderContent.tvTip.setText(R.string.get_money_tip);
        viewHolderContent.tvDatetime.setText(dailyDetail.datetime);
        viewHolderContent.tvTotal.setText("¥ " + dailyDetail.total);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolderTitle {
        TextView tvTitle;
        TextView tvMonthTotal;
        TextView tvMonthCash;
        LinearLayout ll_month_total;
    }

    class ViewHolderContent {
        TextView tvDatetime;
        TextView tvTotal;
        TextView tvTip;
    }
}
