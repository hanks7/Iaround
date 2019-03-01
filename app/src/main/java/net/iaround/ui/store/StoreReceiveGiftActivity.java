
package net.iaround.ui.store;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.model.database.SpaceModel;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 收到礼物界面
 *
 * @author zhengst
 */
public class StoreReceiveGiftActivity extends SuperActivity implements OnClickListener {
    private TextView empty_view;
    private FrameLayout mTitleBack;
    private Dialog mProgressDialog;
    private MyGridView giftGridView;
    private HPopupWindow mMinePopupWindow;
    private GiftGridViewAdapter giftGridAdapter;
    private PullToRefreshScrollView mPullScrollView;
    private TextView tvGiftCount, tvMinegift;
    private TextView mTitleName;
    private FrameLayout mTitleRight;
    private static String _nameTitle, _priceTitle, _charmTitle, _buyTimeTitle;
    private TextView mTitlePrivate;
//    private ImageView mTitleBack;

    private int mCurPage = 1;//当前页码
    private int mTotalPage = 1;//总页数
    private User mUser;// 用户实体
    private static final int PAGE_SIZE = 24;
    public final static String CATEGORY = "category";
    public final static int LAUCH_LOOK = 0;// 从个人资料跳转来查看礼物
    public final static int LAUCH_SENT = 1;// 私聊、个人资料跳转来送礼物
    public final static int LAUCH_FOLLOW = 2;// 商店跳转来查看礼物跳转到已关注列表
    public static Dialog haveSentDialog = null;// 礼物已送到提示框
    public static Dialog diamondCanTalkDialog = null;// 送了钻石礼物可聊天提示框
    private final static int MSG_WHAT_REFRESH_RECEIVE_EMPTY = 996;// 刷新收到礼物为空
    private final static int MSG_WHAT_REFRESH_RECEIVE = 998;// 刷新收到礼物

    /**
     * 所有礼物的集合
     */
    private List<ReceiveGiftBean.ListBean> listBeans;
    /**
     * 礼物总数
     */
    private long giftNum = 0;


    private Handler mTheMainHandler = new Handler(Looper.getMainLooper()) {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            hideProgressDialog();

            HashMap<String, Object> res = (HashMap<String, Object>) msg.obj;

            if (msg.what == MSG_WHAT_REFRESH_RECEIVE) {// 刷新收到的礼物
//                refreshReceiveGiftsData(res);
            } else if (msg.what == MSG_WHAT_REFRESH_RECEIVE_EMPTY) {//收到礼物为空.
                if (listBeans != null && listBeans.size() > 0) {
                    handleMineGiftNoData(false);
                } else {
                    handleMineGiftNoData(true);
                }
            }
            mPullScrollView.onRefreshComplete();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.receive_gift_view);

        _nameTitle = getString(R.string.ps_title_new);
        _priceTitle = getString(R.string.price_title_new);
        _charmTitle = getString(R.string.charisma_title_new);
        _buyTimeTitle = getString(R.string.stime_title_new);

        mUser = (User) this.getIntent().getSerializableExtra("user");

        listBeans = new ArrayList<>();

        initView();
        setListener();
        initData();
    }

    private void initView() {

        mTitleName = (TextView) findViewById(R.id.tv_title);
        mTitleBack = (FrameLayout) findViewById(R.id.fl_left);
        tvGiftCount = (TextView) findViewById(R.id.tvGiftCount);
        mTitleName.setText(R.string.iaround_received_gift_box_title);
        tvMinegift = (TextView) findViewById(R.id.minegift_textview);
        mTitlePrivate = (TextView) findViewById(R.id.tv_right);
        mTitlePrivate.setText(getResources().getString(R.string.iaround_mine_gift_box_title));
        mTitlePrivate.setVisibility(View.VISIBLE);

        mPullScrollView = (PullToRefreshScrollView) findViewById(R.id.giftContent);
        giftGridView = (MyGridView) findViewById(R.id.store_gift_gridview);
        giftGridAdapter = new GiftGridViewAdapter(listBeans);
        giftGridView.setAdapter(giftGridAdapter);
        /***取消礼物点击展示送礼人事件*/
        giftGridView.setOnItemClickListener(giftClicklistener);

        mPullScrollView.setMode(Mode.DISABLED);
        if (mUser.getUid() == Common.getInstance().loginUser.getUid()) {
            mTitlePrivate.setVisibility(View.VISIBLE);
        } else {
            mTitlePrivate.setVisibility(View.GONE);
        }

        empty_view = (TextView) findViewById(R.id.empty_view);
        if (mUser.getUid() == Common.getInstance().loginUser.getUid()) {
            empty_view.setText(R.string.no_content_gift_get);
            String str = String.format(getResources().getString(R.string.iaround_mine_or_other_gift_toatal), "0");
            tvMinegift.setText(str);
        } else {
            empty_view.setText(R.string.space_sent_his_first_gift);
            String str = String.format(getResources().getString(R.string.iaround_mine_or_other_gift_toatal), "0");
            tvMinegift.setText(str);
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        showProgressDialog();
        reqMineGiftList(mCurPage = 1);
    }

    private void setListener() {
        mTitleBack.setOnClickListener(this);
        mTitlePrivate.setOnClickListener(this);
        findViewById(R.id.mine_gift_layout).setOnClickListener(this);
        giftGridView.setOnTouchListener(onTouchListener);
        mPullScrollView.getRefreshableView().setOnTouchListener(onTouchListener);

        mPullScrollView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {// 下拉刷新
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {// 上拉刷新
                        if (mCurPage < mTotalPage) {
                            reqMineGiftList(mCurPage + 1);
                        } else {
                            refreshView.postDelayed(new Runnable() {
                                public void run() {
                                    Toast.makeText(getBaseContext(), R.string.no_more,
                                            Toast.LENGTH_SHORT).show();
                                    mPullScrollView.onRefreshComplete();
                                }
                            }, 200);
                        }
                    }
                });

        // List滚动监听，list滚动时隐藏所有的popupWindow
        giftGridView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                dissmissAllPopupWindows();
            }
        });
    }

    /**
     * 取消展示PopupWindow
     */
    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            dissmissAllPopupWindows();
            return false;
        }
    };

    /**
     * 请求我收到的礼物列表
     */
    private void reqMineGiftList(int nextPage) {
        try {
            SpaceModel.getInstance(getApplicationContext()).giftReceiveReq(mActivity,
                    mUser.getUid(), nextPage, PAGE_SIZE, this);//gh
        } catch (Throwable e) {
            mTheMainHandler.sendMessage(mTheMainHandler
                    .obtainMessage(MSG_WHAT_REFRESH_RECEIVE_EMPTY));
        }
    }


    @Override
    protected void onStop() {
        dissmissAllPopupWindows();
        super.onStop();
    }

    /**
     * 点击礼物，查看当前的送礼人和礼物信息
     * 呵呵哒，估计这个效果得砍掉
     */
    private OnItemClickListener giftClicklistener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {// 查看礼物
            // TODO:7.1.5
//            ReceiveGiftBean.ListBean gift = (ReceiveGiftBean.ListBean) parent.getAdapter().getItem(position);
//            dissmissAllPopupWindows();
//            int _dp5 = (int) (5 * getResources().getDisplayMetrics().density);
//            mMinePopupWindow = new HPopupWindow(StoreReceiveGiftActivity.this,
//                    makePopupView(gift, 1), _dp5 * 45, _dp5 * 22, R.id.up, R.id.down);
//            mMinePopupWindow.setAnimationStyle(R.style.AnimationFade);
//            mMinePopupWindow.showAsDropDown(view);
        }
    };

    /**
     * 使用Gift制作PopupView
     *
     * @param gift 礼物实例
     * @param type 类型 TYPE_MINE或TYPE_RECEVIS
     * @return
     */
    private View makePopupView(final ReceiveGiftBean.ListBean gift, int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.space_giftlist_popup, null);
        NetImageView icon = (NetImageView) view.findViewById(R.id.icon);
        TextView person = (TextView) view.findViewById(R.id.source_person);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView charm = (TextView) view.findViewById(R.id.charm);
        TextView time = (TextView) view.findViewById(R.id.time);
        Button handsel = (Button) view.findViewById(R.id.handsel_btn);

        icon.executeFadeIn(PicIndex.DEFAULT_GIFT, gift.getUser().getIcon());
        name.setText(_nameTitle + gift.getUser().getNickname());
        charm.setText(_charmTitle + "+" + gift.getCharmnum());
        time.setText(_buyTimeTitle + TimeFormat.timeFormat4(mContext, gift.getDatetime()));
        name.setVisibility(View.VISIBLE);
        price.setVisibility(View.GONE);
        charm.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
        person.setVisibility(View.GONE);
        handsel.setVisibility(View.GONE);

        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                User user = new User();
                user.setUid(Long.valueOf(gift.getUser().getUserid()));
                user.setIcon(gift.getUser().getIcon());
                user.setNickname(gift.getUser().getNickname());
                user.setAge(gift.getUser().getAge());
                user.setSex("m".equals(gift.getUser().getGender()) ? 1 : 2);

                Intent intent = new Intent(StoreReceiveGiftActivity.this, OtherInfoActivity.class);
                intent.putExtra(Constants.UID, Long.parseLong(gift.getUser().getUserid()));
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);

        ErrorCode.toastError(this, e);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        if (result != null) {
            ReceiveGiftBean bean = GsonUtil.getInstance().getServerBean(result, ReceiveGiftBean.class);
            handleGiftData(bean, result);
        }

    }

    /**
     * 解析处理请求数据
     */
    private void handleGiftData(ReceiveGiftBean bean, String result) {
        if (bean != null) {
            if (bean.isSuccess()) {
                mCurPage = bean.getPageno();
                int amount = bean.getAmount();
                if (amount > 0) {
                    mTotalPage = amount / PAGE_SIZE;
                    if (amount % PAGE_SIZE > 0) {
                        mTotalPage++;
                    }
                    /**
                     * 第一页
                     */
                    if (mCurPage <= 1) {
                        if (listBeans == null) {
                            listBeans = new ArrayList<>();
                        } else {
                            listBeans.clear();
                        }
                    }

                    if (mTotalPage == mCurPage) {
                        mPullScrollView.setMode(Mode.DISABLED);
                    } else {
                        mPullScrollView.setMode(Mode.PULL_FROM_END);
                    }
                    listBeans.addAll(bean.getList());
//                    if (listBeans != null)
//                    {
//                        for (int i = 0; i < listBeans.size(); i++)
//                        {
//                            giftNum += listBeans.get(i).getGiftnum();
//                        }
//                    }
                    hideEmptyView();// 隐藏空数据的提示语
                    giftGridAdapter.refreshData(listBeans);
                    mPullScrollView.setScrollY(0);
                } else {
                    if (listBeans != null && listBeans.size() > 0) {
                        handleMineGiftNoData(false);
                    } else {
                        handleMineGiftNoData(true);
                    }
                }
                String str = String.format(getResources().getString(R.string.iaround_mine_or_other_gift_toatal), "" + bean.getAmount());
                tvMinegift.setText(str);
            }
        }
        //处理错误结果
        if (result != null && bean != null) {
            if (bean.status == 5806) {
                mTheMainHandler.sendMessage(mTheMainHandler
                        .obtainMessage(MSG_WHAT_REFRESH_RECEIVE_EMPTY));
            } else {
                mTheMainHandler.sendMessage(mTheMainHandler
                        .obtainMessage(MSG_WHAT_REFRESH_RECEIVE_EMPTY));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
                finish();
                break;
            case R.id.tv_right:// 跳转到私藏禮物
                Intent intent = new Intent();
                intent.setClass(this, StoreMineGiftActivityNew.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 礼物列表为空
     */
    protected void handleMineGiftNoData(Boolean isShowEmptyView) {
        hideProgressDialog();
        if (isShowEmptyView) {
            showEmptyView();
        }
//        else {
//            Toast.makeText(this, R.string.network_req_failed, Toast.LENGTH_SHORT).show();
//        }
        mPullScrollView.onRefreshComplete();
    }

    /**
     * 展示空数据的提示语
     */
    private void showEmptyView() {
        empty_view.setVisibility(View.VISIBLE);
        empty_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reqMineGiftList(1);
            }
        });
    }

    /**
     * 关闭空数据的提示语
     */
    private void hideEmptyView() {
        empty_view.setVisibility(View.GONE);
    }

    /**
     * 从个人资料跳转到收藏礼物页面，点击礼物查看礼物信息
     *
     * @param context
     */
    public static void launchMineGiftToLook(Context context, User user) {
        Intent intent = new Intent(context, StoreReceiveGiftActivity.class);
        intent.setClass(context, StoreReceiveGiftActivity.class);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }

    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(mContext, R.string.dialog_title,
                    R.string.content_is_loading, null);
        }

        mProgressDialog.show();
    }

    /**
     * 隐藏加载框
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 隐藏PopupWindows
     */
    private void dissmissAllPopupWindows() {
        if (mMinePopupWindow != null && mMinePopupWindow.getPopupWindow().isShowing()) {
            mMinePopupWindow.dismiss();
        }
    }


    class GiftGridViewAdapter extends BaseAdapter {

        private List<ReceiveGiftBean.ListBean> giftBeans;

        public GiftGridViewAdapter(List<ReceiveGiftBean.ListBean> giftBeans) {
            this.giftBeans = giftBeans;
        }

        public void refreshData(List<ReceiveGiftBean.ListBean> listBeens) {
            this.giftBeans = listBeens;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
//            return mGifts != null && mGifts.size() > 0 ? mGifts.size() : 0;
            return giftBeans != null && giftBeans.size() > 0 ? giftBeans.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return giftBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(StoreReceiveGiftActivity.this).inflate(
                        R.layout.store_gift_classify_item, null);
                viewHolder.icon = (NetImageView) convertView.findViewById(R.id.gift_icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.gift_name);
                viewHolder.price = (TextView) convertView.findViewById(R.id.gift_price);
                viewHolder.experience = (TextView) convertView.findViewById(R.id.gift_experience);
                viewHolder.charm = (TextView) convertView.findViewById(R.id.gift_charm);
                viewHolder.giftFlagLy = (RelativeLayout) convertView
                        .findViewById(R.id.gift_flag);
                viewHolder.viewGift = convertView.findViewById(R.id.view_gift);
                viewHolder.tvGiftNum = (TextView) convertView.findViewById(R.id.tv_have_num);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position > 0) {
                if (position % 2 == 1) {
                    viewHolder.viewGift.setVisibility(View.GONE);
                } else {
                    viewHolder.viewGift.setVisibility(View.VISIBLE);
                }
            } else if (position == 0) {
                viewHolder.viewGift.setVisibility(View.GONE);
            }

            ReceiveGiftBean.ListBean listBean = giftBeans.get(position);
            ReceiveGiftBean.ListBean.GiftBean gift = listBean.getGift();
            if (gift != null) {
                viewHolder.icon.executeFadeInRound(PicIndex.DEFAULT_GIFT, gift.getIcon());
                gift.setNameArray(gift.getName());
                String giftName = gift.getNameArray();
                viewHolder.name.setText(giftName);
                String priceTypeStr = "";
                if (gift.getCurrencytype() == 1) {// 1金币2钻石3爱心6星星
                    priceTypeStr = mContext.getString(R.string.gold_balance);
                    viewHolder.price.setText(priceTypeStr + gift.getGoldnum());
                    viewHolder.price.setTextColor(Color.parseColor("#999999"));
                } else if (gift.getCurrencytype() == 2) {
                    priceTypeStr = mContext.getString(R.string.diamond_balance);
                    viewHolder.price.setTextColor(Color.parseColor("#FF9900"));
                    viewHolder.price.setText(priceTypeStr + gift.getGoldnum());
                } else if(gift.getCurrencytype() == 6){
                    priceTypeStr = mContext.getString(R.string.stars_m);
                    viewHolder.price.setTextColor(Color.parseColor("#46BAFE"));
                    viewHolder.price.setText(priceTypeStr + gift.getGoldnum());
                } else {
                    priceTypeStr = mContext.getString(R.string.item_pay_love_order) + ":";
                    viewHolder.price.setTextColor(Color.parseColor("#FF4064"));
                    viewHolder.price.setText(priceTypeStr + gift.getGoldnum());
                }
                if (listBean.getGiftnum() > 0) {
                    if (listBean.getGiftnum() < 10) {
                        viewHolder.tvGiftNum.setBackgroundResource(R.drawable.store_gift_num1);
                    } else {
                        viewHolder.tvGiftNum.setBackgroundResource(R.drawable.store_gift_num);
                    }
                    viewHolder.tvGiftNum.setText("" + listBean.getGiftnum());
                } else {
                    viewHolder.tvGiftNum.setVisibility(View.GONE);
                }

                viewHolder.charm.setText(mContext.getString(R.string.charisma_title_new) + gift.getCharmnum());
                viewHolder.experience.setText(String.format(mContext.getString(R.string.chat_gift_exp), gift.getExpvalue() + ""));
                viewHolder.giftFlagLy.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

    static class ViewHolder {
        NetImageView icon;
        TextView name;
        TextView price;
        TextView experience;
        TextView charm;
        RelativeLayout giftFlagLy;
        View viewGift;
        TextView tvGiftNum;
    }

}
