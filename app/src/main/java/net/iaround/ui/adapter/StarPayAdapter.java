package net.iaround.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.StarPayItemBean;

import java.util.List;

public class StarPayAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater inflater;

    private List<StarPayItemBean> dataList;

    public StarPayAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public List<StarPayItemBean> getDataList() {
        return dataList;
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.star_pay_adapter_content, parent, false);
            holder.mLlStar = (LinearLayout) convertView.findViewById(R.id.ll_star);
            holder.mTvFinalPrice = (TextView) convertView.findViewById(R.id.tv_final_price);
            holder.mTvOriginalPrice = (TextView) convertView.findViewById(R.id.tv_original_price);
            holder.mIvSelected = (ImageView) convertView.findViewById(R.id.iv_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final StarPayItemBean starPayItemBean = dataList.get(position);
        holder.mTvFinalPrice.setText(starPayItemBean.starnum + mContext.getResources().getString(R.string.stars));
        holder.mTvOriginalPrice.setText(starPayItemBean.strPrice);
        if (starPayItemBean.isCheck) {
            holder.mIvSelected.setBackgroundResource(R.drawable.love_pay_select);
        } else {
            holder.mIvSelected.setBackgroundResource(R.drawable.love_pay_nomal);
        }
        holder.mLlStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < dataList.size(); i++) {
                    StarPayItemBean payGoodsBean = dataList.get(i);
                    if (starPayItemBean.goodsid == payGoodsBean.goodsid) {
                        payGoodsBean.isCheck = true;
                    } else {
                        payGoodsBean.isCheck = false;
                        if (listener != null) {
                            listener.onItemClick(starPayItemBean, position);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public void updateList(List<StarPayItemBean> list) {
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        private LinearLayout mLlStar;
        public TextView mTvFinalPrice;
        public TextView mTvOriginalPrice;
        public ImageView mIvSelected;
    }

    public OnItemStarPayListener listener;

    public interface OnItemStarPayListener {
        void onItemClick(StarPayItemBean bean, int position);
    }

    public void setOnItemStarPayListener(OnItemStarPayListener onItemStarPayListener) {
        this.listener = onItemStarPayListener;
    }
}
