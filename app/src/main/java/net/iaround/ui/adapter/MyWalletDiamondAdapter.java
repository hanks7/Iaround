package net.iaround.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import net.iaround.R;
import net.iaround.pay.bean.PayGoodsBean;
import java.util.List;

public class MyWalletDiamondAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater inflater;

    private List<PayGoodsBean> dataList;

    public MyWalletDiamondAdapter(Context context) {
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
            convertView = inflater.inflate(R.layout.my_wallet_diamond_item_adapter, parent, false);
            holder.mFlDiamondContent = (FrameLayout) convertView.findViewById(R.id.fl_diamond_content);
            holder.mDiamondHot = (ImageView) convertView.findViewById(R.id.diamond_hot);
            holder.mTvDiamondCount = (TextView) convertView.findViewById(R.id.tv_diamond_count);
            holder.mTvDiamondPrice = (TextView) convertView.findViewById(R.id.tv_diamond_price);
            holder.mIvFirstPic = (ImageView) convertView.findViewById(R.id.iv_first_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PayGoodsBean payGoodsBean = dataList.get(position);
        holder.mTvDiamondCount.setText(payGoodsBean.diamondnum + mContext.getResources().getString(R.string.user_wallet_diamond));
        holder.mTvDiamondPrice.setText("/" + payGoodsBean.strPrice);
        if(payGoodsBean.isCheck){
            holder.mFlDiamondContent.setBackgroundResource(R.drawable.shape_rounded_zs_select_bg);
        } else {
            holder.mFlDiamondContent.setBackgroundResource(R.drawable.shape_rounded_edges_bg);
        }
        if(position == 1){
            holder.mDiamondHot.setVisibility(View.VISIBLE);
        }
        if(payGoodsBean.fpay == 0){
            holder.mIvFirstPic.setVisibility(View.VISIBLE);
            if(payGoodsBean.isCheck){
                holder.mIvFirstPic.setBackgroundResource(R.drawable.pic_double_selected);
            } else {
                holder.mIvFirstPic.setBackgroundResource(R.drawable.pic_double);
            }
        }else if(payGoodsBean.fpay == 1){
            holder.mIvFirstPic.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void update(List<PayGoodsBean> listDiamond) {
        this.dataList = listDiamond;
        this.notifyDataSetChanged();
    }

    public List<PayGoodsBean> getDataList() {
        return dataList;
    }

    static class ViewHolder {
        public FrameLayout mFlDiamondContent;
        public ImageView mDiamondHot;
        public TextView mTvDiamondCount;
        public TextView mTvDiamondPrice;
        public ImageView mIvFirstPic;
    }
}
