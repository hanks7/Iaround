package net.iaround.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.tools.glide.GlideUtil;

import java.util.List;

/**
 * 爱心订单适配器
 * Created by Administrator on 2017/12/14.
 */

public class LovePayAdapter extends BaseAdapter {

    private List<PayGoodsBean> mList;
    private LayoutInflater layoutInflater;
    private OnItemLovePayListener listener;


    public LovePayAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 更新适配器数据
     * @param lists
     */
    public void UpdateData(List<PayGoodsBean> lists) {
        this.mList = lists;
        this.notifyDataSetChanged();
    }

    public void setListener(OnItemLovePayListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater
                    .inflate(R.layout.item_love_pay, null);
            viewHolder.viewLy = (LinearLayout) convertView.findViewById(R.id.ly_item_love_pay);
            viewHolder.iconIv = (ImageView) convertView
                    .findViewById(R.id.iv_item_pay_love_icon);
            viewHolder.orederNumberTv = (TextView) convertView
                    .findViewById(R.id.tv_item_pay_love_order_number);
            viewHolder.orederPriceTv = (TextView) convertView
                    .findViewById(R.id.tv_item_pay_love_order_price);
            viewHolder.checkIv = (ImageView) convertView
                    .findViewById(R.id.iv_item_pay_love_check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PayGoodsBean bean = mList.get(position);

//        GlideUtil.loadImage(BaseApplication.appContext,bean.icon,viewHolder.iconIv);

        viewHolder.orederNumberTv.setText(bean.diamondnum+BaseApplication.appContext.getString(R.string.item_pay_love_order));
        viewHolder.orederPriceTv.setText(bean.strPrice);
        if (bean.isCheck){
            if (listener != null){
                listener.onItemClick(bean);
            }
            viewHolder.checkIv.setBackgroundResource(R.drawable.love_pay_select);
        }else {
            viewHolder.checkIv.setBackgroundResource(R.drawable.love_pay_nomal);
        }

        viewHolder.viewLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (PayGoodsBean payGoodsBean : mList){
                    if (bean.goodsid == payGoodsBean.goodsid){
                        payGoodsBean.isCheck = true;
                    }else{
                        payGoodsBean.isCheck = false;
                    }

                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    class ViewHolder{
        private LinearLayout viewLy;
        private ImageView iconIv;
        private TextView orederNumberTv;
        private TextView orederPriceTv;
        private ImageView checkIv;
    }

    public interface OnItemLovePayListener{
        void onItemClick(PayGoodsBean bean);
    }

}
