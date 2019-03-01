package net.iaround.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import net.iaround.R;
import net.iaround.pay.bean.PayGoodsBean;

import java.util.List;

public class MyWalletStarAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater inflater;

    private List<PayGoodsBean> dataList;

    public MyWalletStarAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.my_wallet_star_item_adapter, parent, false);
            holder.mFlStarContent = (FrameLayout) convertView.findViewById(R.id.fl_star_content);
            holder.mStarHot = (ImageView) convertView.findViewById(R.id.star_hot);
            holder.mTvStarCount = (TextView) convertView.findViewById(R.id.tv_star_count);
            holder.mTvStarPrice = (TextView) convertView.findViewById(R.id.tv_star_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PayGoodsBean payGoodsBean = dataList.get(position);
        holder.mTvStarCount.setText(payGoodsBean.diamondnum + mContext.getResources().getString(R.string.stars));
        holder.mTvStarPrice.setText("/" + payGoodsBean.strPrice);
        if(payGoodsBean.isCheck){
            holder.mFlStarContent.setBackgroundResource(R.drawable.shape_rounded_edges_select_bg);
        } else {
            holder.mFlStarContent.setBackgroundResource(R.drawable.shape_rounded_edges_bg);
        }
        if(position == 1){
            holder.mStarHot.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public void update(List<PayGoodsBean> list) {
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    public List<PayGoodsBean> getDataList() {
        return dataList;
    }

    static class ViewHolder {
        public FrameLayout mFlStarContent;
        public ImageView mStarHot;
        public TextView mTvStarCount;
        public TextView mTvStarPrice;
    }
}
