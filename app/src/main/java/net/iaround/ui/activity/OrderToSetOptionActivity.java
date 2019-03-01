package net.iaround.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.entity.OrderSetBean;
import net.iaround.model.entity.OrderSetOptionItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.view.FlagImageView;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/4.
 * 接单设置选项界面  价格-重复日期
 */

public class OrderToSetOptionActivity extends TitleActivity {
    private int mType; //0-游戏价格选择  1-日期选择
    private int mPosition;
    private OrderSetBean mOrderSetBean;
    private OrderSetBean.OrderGameItem mGameItem;
    private ArrayList<OrderSetOptionItemBean> mDiscountList;//折扣列表
    private ArrayList<OrderSetOptionItemBean> mPriceList;//价格列表
    private ArrayList<OrderSetOptionItemBean> mDateList;//日期列表
    private String mRepeat;//重复日期

    private LinearLayout mLlDiscount;
    private FlagImageView mFivBtnDiscount;
    private MyGridView mMgvDiscount;
    private MyGridView mMgvPriceOrDay;

    private OrderSetDiscountAdapter mDiscountAdapter;
    private PriceOrDayAdapter mPriceOrDayAdapter;

    private int mDiscount = 100;//选择哪个折扣
    private double mPrice = 0;//选择哪个价格

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getIntExtra("type", 1);
        mPosition = getIntent().getIntExtra("position", 0);
        mOrderSetBean = (OrderSetBean) getIntent().getSerializableExtra("OrderSetBean");
        mRepeat = getIntent().getStringExtra("repeat");
        setContent(R.layout.activity_order_set_option);

        mLlDiscount = (LinearLayout) findViewById(R.id.ll_discount);
        mFivBtnDiscount = (FlagImageView) findViewById(R.id.fiv_btn_discount);
        mFivBtnDiscount.setState(false);
        mMgvDiscount = (MyGridView) findViewById(R.id.mgv_discount);
        mMgvPriceOrDay = (MyGridView) findViewById(R.id.mgv_price);

        initData();

        if (mType == 1) {
            setTitle_C(R.string.order_to_set_repeat);
            mLlDiscount.setVisibility(View.GONE);
            if (mDateList != null) {
                mPriceOrDayAdapter = new PriceOrDayAdapter(mDateList);
                mMgvPriceOrDay.setAdapter(mPriceOrDayAdapter);
            }
        } else {
            setTitle_C(mGameItem.gameName);
        }
        getTvRight().setTextColor(getResColor(R.color.c_999999));
        setTitle_R(0, getResString(R.string.edit_save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backEvent();
            }
        });
        getFlRight().setEnabled(false);

        if (mDiscountList != null) {

            mDiscountAdapter = new OrderSetDiscountAdapter(mDiscountList);
            mMgvDiscount.setAdapter(mDiscountAdapter);
            if (mDiscount != 100) {
                mFivBtnDiscount.setState(true);
            } else {
                mMgvDiscount.setVisibility(View.GONE);
            }
        }
        if (mPriceList != null) {

            mPriceOrDayAdapter = new PriceOrDayAdapter(mPriceList);
            mMgvPriceOrDay.setAdapter(mPriceOrDayAdapter);
        }

        initClickListener();

    }

    private void initData() {
        if (mType == 0 && mOrderSetBean != null && mOrderSetBean.games != null) {
            mGameItem = mOrderSetBean.games.get(mPosition);
            //折扣列表
            if (mGameItem.discountList != null) {
                mDiscount = mGameItem.discount;
                if (mDiscount == 0) {
                    mDiscount = 100;
                }
                mDiscountList = new ArrayList<>();
                for (int i = 0; i < mGameItem.discountList.size(); i++) {
                    OrderSetOptionItemBean itemBean = new OrderSetOptionItemBean();
                    itemBean.discount = mGameItem.discountList.get(i);
                    if (mDiscount == 100 && i == 0) {
                        itemBean.isSelected = true;
                    } else {
                        if (mDiscount == itemBean.discount) {
                            itemBean.isSelected = true;
                        } else {
                            itemBean.isSelected = false;
                        }
                    }
                    mDiscountList.add(itemBean);
                }
            }
            //价格列表
            if (mGameItem.priceList != null) {
                mPriceList = new ArrayList<>();
                for (int i = 0; i < mGameItem.priceList.size(); i++) {
                    OrderSetOptionItemBean itemBean = new OrderSetOptionItemBean();
                    itemBean.price = mGameItem.priceList.get(i);
                    mGameItem.price = mGameItem.price / mDiscount * 100;
                    if (mGameItem.price == itemBean.price) {
                        itemBean.isSelected = true;
                    } else {
                        itemBean.isSelected = false;
                    }
                    mPriceList.add(itemBean);
                }
            }
        }
        if (mType == 1) {
            String[] repeat = null;
            //日期列表
            if (!TextUtils.isEmpty(mRepeat) && mRepeat.contains(",")) {
                repeat = mRepeat.split(",");
            }
            String[] dayList = getResStringArr(R.array.day_list);
            mDateList = new ArrayList<>();
            for (int i = 0; i < dayList.length; i++) {
                OrderSetOptionItemBean itemBean = new OrderSetOptionItemBean();
                itemBean.repeatDateStr = dayList[i];
                itemBean.repeatDate = i;
                if (repeat != null && repeat.length > 0) {
                    for (int j = 0; j < repeat.length; j++) {
                        if (repeat[j].equals(String.valueOf(i))) {
                            itemBean.isSelected = true;
                            break;
                        } else {
                            itemBean.isSelected = false;
                        }
                    }
                } else {

                    itemBean.isSelected = false;
                }
                mDateList.add(itemBean);
            }
        }
    }

    private void initClickListener() {
        mFivBtnDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFlRight().setEnabled(true);
                getTvRight().setTextColor(getResColor(R.color.common_iaround_red));
                if (mFivBtnDiscount.isSelected()) {
                    mFivBtnDiscount.setState(false);
                    if (mDiscountList != null) {

                        mDiscount = 100;//重置折扣
                    }
                    mPriceOrDayAdapter.notifyDataSetChanged();
                    mMgvDiscount.setVisibility(View.GONE);
                } else {

                    if (mGameItem != null && mGameItem.orderCount == 1) {
                        mFivBtnDiscount.setState(true);
                        mMgvDiscount.setVisibility(View.VISIBLE);
                        if (mDiscountAdapter != null && mPriceOrDayAdapter != null) {
                            mDiscountAdapter.notifyDataSetChanged();
                            mPriceOrDayAdapter.notifyDataSetChanged();
                        }
                    } else {
                        CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.opten_discount_tip));
                    }

                }
            }
        });

        mMgvDiscount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFlRight().setEnabled(true);
                getTvRight().setTextColor(getResColor(R.color.common_iaround_red));
                if (mDiscountList != null) {
                    for (int i = 0; i < mDiscountList.size(); i++) {
                        if (i == position) {
                            mDiscountList.get(i).isSelected = true;
                        } else {
                            mDiscountList.get(i).isSelected = false;
                        }
                    }
                }
                mDiscountAdapter.notifyDataSetChanged();
                mPriceOrDayAdapter.notifyDataSetChanged();
            }
        });

        mMgvPriceOrDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFlRight().setEnabled(true);
                getTvRight().setTextColor(getResColor(R.color.common_iaround_red));
                if (mPriceList != null) {
                    for (int i = 0; i < mPriceList.size(); i++) {
                        if (i == position) {
                            mPriceList.get(i).isSelected = true;
                        } else {
                            mPriceList.get(i).isSelected = false;
                        }
                    }
                    mPriceOrDayAdapter.notifyDataSetChanged();
                } else if (mDateList != null) {
                    if (mDateList.get(position).isSelected) {
                        mDateList.get(position).isSelected = false;
                    } else {
                        mDateList.get(position).isSelected = true;
                    }
                    mPriceOrDayAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void backEvent() {
        Intent intent = new Intent();
        if (mType == 0) {
            if (mPrice > 0) {
                intent.putExtra("price", mPrice);
                if (mDiscount == 100) {
                    intent.putExtra("discount", 0);
                } else {
                    intent.putExtra("discount", mDiscount);

                }
            } else {
                return;
            }
        } else {

            if (mDateList != null) {

                String repeatsStr = "";
                for (int i = 0; i < mDateList.size(); i++) {

                    if (mDateList.get(i).isSelected) {
                        repeatsStr = repeatsStr + mDateList.get(i).repeatDate + ",";
                    }
                }
                if (repeatsStr.length() <= 0) {
                    return;
                } else {
                    intent.putExtra("repeat", repeatsStr.substring(0, repeatsStr.length() - 1));
                }

            }
        }
        setResult(1002, intent);
        finish();
    }

    //选择折扣Adapter
    class OrderSetDiscountAdapter extends BaseAdapter {
        ArrayList<OrderSetOptionItemBean> discountList;

        public OrderSetDiscountAdapter(ArrayList<OrderSetOptionItemBean> discountList) {
            this.discountList = discountList;
        }

        @Override
        public int getCount() {
            if (discountList != null) {

                return discountList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (discountList != null) {

                return discountList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(OrderToSetOptionActivity.this).inflate(R.layout.item_order_set_discount, null);
            TextView tvDiscount = (TextView) convertView.findViewById(R.id.tv_discount);
            tvDiscount.setText(discountList.get(position).discount / 10 + getString(R.string.discount));
            if (discountList.get(position).isSelected) {
                mDiscount = mDiscountList.get(position).discount;

                tvDiscount.setSelected(true);
            } else {
                tvDiscount.setSelected(false);
            }
            return convertView;
        }
    }


    //显示价格或者日期的Adapter
    class PriceOrDayAdapter extends BaseAdapter {
        ArrayList<OrderSetOptionItemBean> list;

        public PriceOrDayAdapter(ArrayList<OrderSetOptionItemBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(OrderToSetOptionActivity.this).inflate(R.layout.item_order_set_option, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_selected = (ImageView) convertView.findViewById(R.id.iv_selected);
                viewHolder.ivDiscountIcon = (ImageView) convertView.findViewById(R.id.iv_discount_icon);
                viewHolder.tv_final_price = (TextView) convertView.findViewById(R.id.tv_final_price);
                viewHolder.tv_original_price = (TextView) convertView.findViewById(R.id.tv_original_price);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }

            if (list.get(position).isSelected) {
                viewHolder.iv_selected.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iv_selected.setVisibility(View.GONE);
            }

            if (mType == 0) {

                if (list.get(position).isSelected) {
                    mPrice = mPriceList.get(position).price;
                }

                viewHolder.tv_final_price.setText(list.get(position).price * mDiscount / 100 + mGameItem.unit);
                if (mFivBtnDiscount.isSelected()) {
                    viewHolder.ivDiscountIcon.setVisibility(View.VISIBLE);
                    viewHolder.tv_original_price.setVisibility(View.VISIBLE);
                    viewHolder.tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.tv_original_price.setText(list.get(position).price + mGameItem.unit);

                } else {
                    viewHolder.ivDiscountIcon.setVisibility(View.GONE);
                    viewHolder.tv_original_price.setVisibility(View.INVISIBLE);
                }
            } else {

                viewHolder.tv_final_price.setText(list.get(position).repeatDateStr);
            }

            return convertView;
        }

        class ViewHolder {
            public ImageView ivDiscountIcon;
            public ImageView iv_selected;
            public TextView tv_final_price;
            public TextView tv_original_price;

        }
    }


}
