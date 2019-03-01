package net.iaround.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.DailyDetailEntity.DailyDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 主播每日明细界面Adapter
 * Created by Administrator on 2018/4/11.
 */

public class DailyDetailAdapter extends BaseExpandableListAdapter {

    private Context mContext;

    private LinkedHashMap<String,ArrayList<DailyDetail>> mStringDailyDetailHashMap;

    private Object[] objects;

    public DailyDetailAdapter(Context context) {
        mContext = context;
    }

    public void updateList(LinkedHashMap<String,ArrayList<DailyDetail>> stringDailyDetailHashMap) {
        if(stringDailyDetailHashMap == null){
            return;
        }
        mStringDailyDetailHashMap = stringDailyDetailHashMap;
        objects = mStringDailyDetailHashMap.keySet().toArray();
        notifyDataSetChanged();
    }

    //获取分组的个数
    @Override
    public int getGroupCount() {
        return mStringDailyDetailHashMap == null ? 0:mStringDailyDetailHashMap.size();
    }

    //获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        if(mStringDailyDetailHashMap != null){
            ArrayList<DailyDetail> dailyDetails = mStringDailyDetailHashMap.get(objects[groupPosition]);
            return dailyDetails == null ? 0:dailyDetails.size();
        }
        return 0;
    }

    //获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return objects[groupPosition];
    }

    //获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mStringDailyDetailHashMap.get(objects[groupPosition]).get(childPosition);
    }

    //获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
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
            viewHolderTitle.llMonthCash = (LinearLayout) convertView.findViewById(R.id.ll_month_cash);
            convertView.setTag(viewHolderTitle);
        }else {
            viewHolderTitle = (ViewHolderTitle) convertView.getTag();
        }
        viewHolderTitle.llMonthCash.setVisibility(View.GONE);

        viewHolderTitle.tvTitle.setText(objects[groupPosition].toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderContent viewHolderContent = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_daily_detail, parent, false);
            viewHolderContent = new ViewHolderContent();
            viewHolderContent.tvDatetime = (TextView) convertView.findViewById(R.id.tv_datetime);
            viewHolderContent.tvTotal = (TextView) convertView.findViewById(R.id.tv_total);
            viewHolderContent.tvTip = (TextView) convertView.findViewById(R.id.tv_tip);
            convertView.setTag(viewHolderContent);
        }else {
            viewHolderContent = (ViewHolderContent) convertView.getTag();
        }
        String key = objects[groupPosition].toString();
        ArrayList<DailyDetail> dailyDetails = mStringDailyDetailHashMap.get(key);
        DailyDetail dailyDetail = dailyDetails.get(childPosition);
        if(dailyDetail.status == 0){
            viewHolderContent.tvTip.setVisibility(View.VISIBLE);
            viewHolderContent.tvTip.setText(R.string.no_withdrawal);
            }else {
            viewHolderContent.tvTip.setVisibility(View.GONE);
            }
            viewHolderContent.tvDatetime.setText(dailyDetail.datetime);
        viewHolderContent.tvTotal.setText("¥ " + dailyDetail.total);

        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolderTitle {
        TextView tvTitle;
        TextView tvMonthTotal;
        TextView tvMonthCash;
        LinearLayout llMonthCash;
    }

    class ViewHolderContent {
        TextView tvDatetime;
        TextView tvTotal;
        TextView tvTip;
    }

}
