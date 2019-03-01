package net.iaround.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.GameOrderListBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DensityUtils;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.glide.GlideUtil;
import java.util.ArrayList;

/**
 * Created by yz on 2018/8/9.
 * 订单中心界面
 */

public class GameOrderCenterActivity extends TitleActivity implements HttpCallBack, View.OnClickListener {
    private PullToRefreshListView mPtrfOrderCenter;

    private View mEmptyView;

    private PopupWindow mWindow;
    private TextView mTvFilterUndone;//筛选项 未完成
    private TextView mTvFilterDone;//筛选项 已完成
    private TextView mTvFilterAll;//筛选项 全部

    private OrderCenterAdapter mAdapter;
    private ArrayList<GameOrderListBean.OrderListItem> mOrderList = new ArrayList<>();
    private GameOrderListBean mListBean;
    private int mPageNo = 1;
    private int mPageSize = 24;
    private int mStatus = 2;//0-未完成 1-已完成 2-全部

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_C(R.string.user_fragment_order_list);
        getIvRight().getLayoutParams().height = DensityUtils.dp2px(this, 17);
        getIvRight().getLayoutParams().width = DensityUtils.dp2px(this, 17);
        setTitle_R(R.drawable.icon_select, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(getIvRight());
            }
        });
        initPopUp();
        mEmptyView = LayoutInflater.from(this).inflate(R.layout.no_data_view, null);

        setContent(R.layout.activity_game_order_center);
        mPtrfOrderCenter = (PullToRefreshListView) findViewById(R.id.ptrl_order_center);
        mPtrfOrderCenter.setMode(PullToRefreshBase.Mode.BOTH);
        mPtrfOrderCenter.setPullToRefreshOverScrollEnabled(false);
        mHandler = new Handler();
        initListener();
        mAdapter = new OrderCenterAdapter();
        mPtrfOrderCenter.setAdapter(mAdapter);
        showWaitDialog();
    }

    private void initPopUp() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.filter_game_order_list, null, false);
        mWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.setAnimationStyle(R.style.popwin_anim_from_top_style);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setOutsideTouchable(true);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mTvFilterUndone = (TextView) popupView.findViewById(R.id.tv_filter_undone);
        mTvFilterDone = (TextView) popupView.findViewById(R.id.tv_filter_done);
        mTvFilterAll = (TextView) popupView.findViewById(R.id.tv_filter_all);
        mTvFilterUndone.setOnClickListener(this);
        mTvFilterDone.setOnClickListener(this);
        mTvFilterAll.setOnClickListener(this);
        mTvFilterAll.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_filter_undone:
                mTvFilterUndone.setSelected(true);
                mTvFilterDone.setSelected(false);
                mTvFilterAll.setSelected(false);
                mWindow.dismiss();
                mStatus = 0;
                break;
            case R.id.tv_filter_done:
                mTvFilterUndone.setSelected(false);
                mTvFilterDone.setSelected(true);
                mTvFilterAll.setSelected(false);
                mWindow.dismiss();
                mStatus = 1;
                break;
            case R.id.tv_filter_all:
                mTvFilterUndone.setSelected(false);
                mTvFilterDone.setSelected(false);
                mTvFilterAll.setSelected(true);
                mWindow.dismiss();
                mStatus = 2;
                break;
        }
        mPageNo = 1;
        requestData();
    }

    private void initListener() {

        mWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        mPtrfOrderCenter.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNo = 1;
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (mListBean != null) {

                    if (mOrderList.size() >= mListBean.amount) {

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CommonFunction.toastMsg(BaseApplication.appContext, R.string.no_more);
                                mPtrfOrderCenter.onRefreshComplete();
                            }
                        }, 200);
                    } else {
                        mPageNo = mListBean.pagenum + 1;
                        requestData();
                    }
                }

            }
        });

        mPtrfOrderCenter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameOrderListBean.OrderListItem item = (GameOrderListBean.OrderListItem) parent.getAdapter().getItem(position);
                if (item != null) {
                    InnerJump.JumpGameOrderDetail(mContext, item.orderid);
                }
            }
        });
    }

    private void showPopup(View view) {
        if (Build.VERSION.SDK_INT == 24) {
            // 获取控件的位置，安卓系统=7.0
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            mWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, location[1] + view.getHeight());
        } else {
            mWindow.showAsDropDown(view);
        }
    }

    private void requestData() {

        GameChatHttpProtocol.getOrderList(this, mPageNo, mPageSize, mStatus, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageNo = 1;
        requestData();
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        mPtrfOrderCenter.onRefreshComplete();
        destroyWaitDialog();
        mListBean = GsonUtil.getInstance().getServerBean(result, GameOrderListBean.class);

        if (mListBean != null && mListBean.orderlist != null) {
            if (mPageNo == 1) {
                mOrderList.clear();
            }
            mOrderList.addAll(mListBean.orderlist);
            mAdapter.updateData(mOrderList);
            if (mListBean.orderlist.size() <= 0) {
                mPtrfOrderCenter.setEmptyView(mEmptyView);
            }
        } else {
            mPtrfOrderCenter.setEmptyView(mEmptyView);
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        mPtrfOrderCenter.onRefreshComplete();
        destroyWaitDialog();
        mPtrfOrderCenter.setEmptyView(mEmptyView);
        ErrorCode.toastError(mContext, e);
    }

    class OrderCenterAdapter extends BaseAdapter {
        ArrayList<GameOrderListBean.OrderListItem> orderList;

        public ArrayList<GameOrderListBean.OrderListItem> getOrderList() {
            return orderList;
        }

        public void updateData(ArrayList<GameOrderListBean.OrderListItem> orderList) {
            this.orderList = orderList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (orderList != null) {
                return orderList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (orderList != null) {
                return orderList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(GameOrderCenterActivity.this).inflate(R.layout.item_game_order_center, null);
                holder = new ViewHolder();
                holder.iv_head_icon = (ImageView) convertView.findViewById(R.id.iv_head_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_time_price = (TextView) convertView.findViewById(R.id.tv_time_price);
                holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                holder.tv_judge = (TextView) convertView.findViewById(R.id.tv_judge);
                holder.tv_more_list = (TextView) convertView.findViewById(R.id.tv_more_list);
                holder.tv_to_pay = (TextView) convertView.findViewById(R.id.tv_to_pay);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final GameOrderListBean.OrderListItem item = orderList.get(position);
            GlideUtil.loadCircleImage(GameOrderCenterActivity.this, item.icon, holder.iv_head_icon);
            if (!TextUtils.isEmpty(item.nickname)) {
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, item.nickname, 0, null);
                holder.tv_name.setText(spName);
            }
            holder.tv_time_price.setText(item.comment);
            holder.tv_judge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InnerJump.JumpCommentGameOrder(mContext, item.orderid);
                }
            });
            holder.tv_more_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        InnerJump.JumpMakeGameOrder(mContext, item.anchorid);
                }
            });

            holder.tv_to_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InnerJump.JumpGameOrderDetail(mContext, item.orderid);
                }
            });

            holder.iv_head_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.getInstance().loginUser.getUid() == item.userid) {
                        // 跳转到个人页面
                        Intent intent = new Intent(mContext, UserInfoActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, OtherInfoActivity.class);
                        intent.putExtra(Constants.UID, item.userid);
                        startActivity(intent);
                    }
                }
            });

            if (item.status == 0) {
                holder.tv_status.setText(R.string.to_be_paid);
            } else if (item.status == 1) {
                holder.tv_status.setText(R.string.canceled);
            } else if (item.status == 2) {
                holder.tv_status.setText(R.string.completed);
            } else if (item.status == 3) {
                holder.tv_status.setText(R.string.refunded);
            }

            //去支付按钮
            if(item.paybut == 0){
                holder.tv_to_pay.setVisibility(View.GONE);
            }else if(item.paybut == 1){
                holder.tv_to_pay.setVisibility(View.VISIBLE);
                holder.tv_to_pay.setText(R.string.to_be_paid);
            }

            //评论按钮
            if(item.commentbut == 0){
                holder.tv_judge.setVisibility(View.GONE);
            }else if(item.commentbut == 1){
                holder.tv_judge.setVisibility(View.VISIBLE);
            }

            //再来一单按钮
            if(item.onemorebut == 0){
                holder.tv_more_list.setVisibility(View.GONE);
            }else if(item.onemorebut == 1){
                holder.tv_more_list.setVisibility(View.VISIBLE);
                holder.tv_more_list.setText(R.string.one_more_list);
            }

            return convertView;
        }

        class ViewHolder {
            public ImageView iv_head_icon;
            public TextView tv_name;
            public TextView tv_time_price;
            public TextView tv_status;
            public TextView tv_judge;
            public TextView tv_more_list;
            public TextView tv_to_pay;
        }
    }
}
