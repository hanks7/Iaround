package net.iaround.ui.view.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.PlayMatesListFilterBean;
import net.iaround.model.entity.PlayMatesListFilterItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/7.
 * 陪玩分类详情列表筛选项弹窗
 */

public class FilterGameListPopupWindow {

    protected Context context;
    protected View contentView;
    protected PopupWindow mInstance;
    private ListView mLv;
    private TextView mTvReset;
    private TextView mTvSaveFilter;
    private PlayMatesListFilterBean mListBean;
    private ListAdapter mAdapter;
    private AdapterView.OnItemClickListener mItemClickGender;
    private AdapterView.OnItemClickListener mItemClickPrice;
    private AdapterView.OnItemClickListener mItemClickLevel;
    private OnSaveClickListener mOnSaveClickListener;

    public FilterGameListPopupWindow(Context c) {
        context = c;
        contentView = LayoutInflater.from(c).inflate(R.layout.filter_game_list, null, false);
        mLv = (ListView) contentView.findViewById(R.id.lv_filter);
        mTvReset = (TextView) contentView.findViewById(R.id.tv_reset);
        mTvSaveFilter = (TextView) contentView.findViewById(R.id.tv_save_filter);
        mInstance = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initWindow();

        initListener();
        mAdapter = new ListAdapter();
        mLv.setAdapter(mAdapter);


        mInstance.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    private void initListener() {

        mInstance.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mInstance.dismiss();
                    return true;
                }
                return false;
            }
        });


        mItemClickGender = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayMatesListFilterItemBean itemBean = (PlayMatesListFilterItemBean) parent.getAdapter().getItem(position);
                if (itemBean != null) {

                    for (int i = 0; i < mListBean.genders.size(); i++) {

                        if (mListBean.genders.get(i).itemId.equals(itemBean.itemId)) {
                            mListBean.genders.get(i).isSelected = true;
                        } else {
                            mListBean.genders.get(i).isSelected = false;

                        }
                    }
                    ((ItemFilterAdapter) parent.getAdapter()).notifyDataSetChanged();

                }


            }
        };
        mItemClickPrice = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayMatesListFilterItemBean itemBean = (PlayMatesListFilterItemBean) parent.getAdapter().getItem(position);
                if (itemBean != null) {

                    for (int i = 0; i < mListBean.prices.size(); i++) {

                        if (mListBean.prices.get(i).itemId.equals(itemBean.itemId)) {
                            mListBean.prices.get(i).isSelected = true;
                        } else {
                            mListBean.prices.get(i).isSelected = false;

                        }
                    }
                    ((ItemFilterAdapter) parent.getAdapter()).notifyDataSetChanged();

                }

            }
        };
        mItemClickLevel = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayMatesListFilterItemBean itemBean = (PlayMatesListFilterItemBean) parent.getAdapter().getItem(position);
                if (itemBean != null) {

                    for (int i = 0; i < mListBean.levels.size(); i++) {

                        if (mListBean.levels.get(i).itemId.equals(itemBean.itemId)) {
                            mListBean.levels.get(i).isSelected = true;
                        } else {
                            mListBean.levels.get(i).isSelected = false;

                        }
                    }
                    ((ItemFilterAdapter) parent.getAdapter()).notifyDataSetChanged();

                }

            }
        };

        mTvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mListBean.genders.size(); i++) {
                    if ("all".equals(mListBean.genders.get(i).itemId)) {

                        mListBean.genders.get(i).isSelected = true;
                    } else {

                        mListBean.genders.get(i).isSelected = false;
                    }
                }
                for (int i = 0; i < mListBean.prices.size(); i++) {
                    if ("all".equals(mListBean.prices.get(i).itemId)) {

                        mListBean.prices.get(i).isSelected = true;
                    } else {

                        mListBean.prices.get(i).isSelected = false;
                    }
                }
                for (int i = 0; i < mListBean.levels.size(); i++) {
                    if ("all".equals(mListBean.levels.get(i).itemId)) {

                        mListBean.levels.get(i).isSelected = true;
                    } else {

                        mListBean.levels.get(i).isSelected = false;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        mTvSaveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSaveClickListener != null && mListBean != null) {
                    mOnSaveClickListener.onSaveClick(mListBean);
                    mInstance.dismiss();
                }

            }
        });

    }

    public interface OnSaveClickListener {
        void onSaveClick(PlayMatesListFilterBean listBean);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        mOnSaveClickListener = listener;
    }

    public View getContentView() {
        return contentView;
    }

    public PopupWindow getPopupWindow() {
        return mInstance;
    }


    public void show(View view, PlayMatesListFilterBean listBean) {
        if (!mInstance.isShowing()) {

            mListBean = (PlayMatesListFilterBean) CommonFunction.deepClone(listBean);

            if (mListBean != null) {

                mAdapter.updateList(mListBean);
                if (Build.VERSION.SDK_INT == 24) {
                    // 获取控件的位置，安卓系统=7.0
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    Log.d("FilterGameList: ", "x: " + x + "  y: " + y);
                    mInstance.showAtLocation(view, Gravity.NO_GRAVITY, 0, location[1] + view.getHeight());
                } else {
                    mInstance.showAsDropDown(view);
                }
            }
        }
    }

    protected void initWindow() {
        mInstance.setAnimationStyle(R.style.popwin_anim_from_top_style);
        mInstance.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mInstance.setOutsideTouchable(true);
        mInstance.setTouchable(true);
        mInstance.setFocusable(true);
    }

    class ListAdapter extends BaseAdapter {
        PlayMatesListFilterBean listBean;

        public void updateList(PlayMatesListFilterBean listBean) {
            this.listBean = listBean;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (listBean != null && listBean.levels != null && listBean.levels.size() > 0) {
                return 3;
            }
            return 2;
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
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                holder.adapter = new ItemFilterAdapter();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_game_list_filter, null);
                holder.tv_filter_title = (TextView) convertView.findViewById(R.id.tv_filter_title);
                holder.myGridView = (MyGridView) convertView.findViewById(R.id.mgv_filter);
                holder.myGridView.setAdapter(holder.adapter);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                holder.myGridView.setOnItemClickListener(mItemClickGender);
                holder.tv_filter_title.setText(R.string.encounter_filter_sex);
                holder.myGridView.setPadding(62, 0, 62, 0);
                holder.myGridView.setNumColumns(3);
                holder.adapter.update(listBean.genders);
            } else if (position == 1) {
                holder.myGridView.setOnItemClickListener(mItemClickPrice);
                holder.tv_filter_title.setText(R.string.order_to_set_price);
                holder.myGridView.setNumColumns(4);
                holder.adapter.update(listBean.prices);
            } else if (position == 2) {
                holder.myGridView.setOnItemClickListener(mItemClickLevel);
                holder.tv_filter_title.setText(R.string.level);
                holder.myGridView.setNumColumns(3);
                holder.adapter.update(listBean.levels);

            }
            return convertView;
        }

        class ViewHolder {
            public TextView tv_filter_title;
            public MyGridView myGridView;
            public ItemFilterAdapter adapter;
        }
    }


    class ItemFilterAdapter extends BaseAdapter {
        ArrayList<PlayMatesListFilterItemBean> itemBeans;

        public void update(ArrayList<PlayMatesListFilterItemBean> itemBeans) {
            this.itemBeans = itemBeans;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (itemBeans == null) {
                return 0;
            }
            return itemBeans.size();
        }

        @Override
        public Object getItem(int position) {
            if (itemBeans != null) {
                return itemBeans.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_filter_item, null);
            final TextView tv = (TextView) convertView.findViewById(R.id.tv_filter_name);
            if (itemBeans.get(position).isSelected) {
                tv.setSelected(true);
            } else {
                tv.setSelected(false);
            }
            tv.setText(itemBeans.get(position).itemName);

            return convertView;
        }
    }
}
