
package net.iaround.ui.store;


import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.model.database.FriendModel;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.Me;
import net.iaround.model.im.SocketSuccessResponse;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.model.type.ChatMessageType;
import net.iaround.pay.FragmentPayBuyGlod;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.UserVIPActivity;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.friend.FriendsAttentionActivity;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * 私藏礼物界面
 *7.13版本之后，私藏礼物改为背包礼物
 * @author zhengst
 */
public class StoreMineGiftActivity extends SuperActivity implements OnClickListener {
    private int goldnum = 0;
    private int diamondnum = 0;
    private int mCurPage = 1;
    private int mTotalPage = 1;
    private User mUser;// 用户实体
    private int currentAmount;
    private Gift mCurrentGift;
    private long lastClickTime;
    private ChatRecord mChatRecord;
    private ArrayList<Gift> mGifts = new ArrayList<Gift>();
    private static final int PAGE_SIZE = 24;
    private static String _goldNum, _diamondNum;
    private static String _nameTitle, _priceTitle;
    private static String _charmTitle, _buyTimeTitle;
    // View
//	private View dividerView;
    private TextView empty_view;

    private Dialog mProgressDialog;
    //	private LinearLayout llcontent;
    private MyGridView giftGridView;
    //	private TextView tvDiamond , tvGold;
    private HPopupWindow mMinePopupWindow;
    private GiftGridViewAdapter giftGridAdapter;
    private PullToRefreshScrollView mPullScrollView;
//	private TextView mTitleName , mTitleRight , tvGiftCount;

    //标题栏
    private ImageView mTitleBack;
    private TextView mTitleName;
    private TextView mTitleRight;
    private FrameLayout flLeft;
    private TextView tvPrivageCount;

    public static Dialog haveSentDialog = null;// 礼物已送到提示框
    public static Dialog diamondCanTalkDialog = null;// 送了钻石礼物可聊天提示框
    // Flag
    private long mReqSendMsgFlag;// 发送私聊的请求
    private static int from = 0;// 从哪里进入聊天
    private static int mtype = 0;// 聊天类型（0-正常私聊，1-搭讪聊天）
    private int mLauchType = LAUCH_LOOK;// 跳转到本页面的意图
    public final static int LAUCH_LOOK = 0;// 从个人资料跳转来查看礼物
    public final static int LAUCH_SENT = 1;// 私聊、个人资料跳转来送礼物
    public final static int LAUCH_FOLLOW = 2;// 商店跳转来查看礼物跳转到已关注列表
    private final static int MSG_WHAT_REFRESH_MINE_SOCKET_SEND = 995;// 赠送礼物后发送私聊
    private final static int MSG_WHAT_REFRESH_MINE_EMPTY = 996;// 刷新私藏礼物为空
    private final static int MSG_WHAT_REFRESH_MINE_SEND = 997;// 发送礼物成功
    private final static int MSG_WHAT_REFRESH_MINE = 998;// 刷新私藏礼物


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mine_gift_view);
        mContext = this;
        _goldNum = getResString(R.string.gold_balance);
        _diamondNum = getResString(R.string.diamond_balance);
        _nameTitle = getString(R.string.name_title_new);
        _priceTitle = getString(R.string.price_title_new);
        _buyTimeTitle = getString(R.string.btime_title_new);
        _charmTitle = getString(R.string.charisma_title_new);

        mLauchType = this.getIntent().getIntExtra("category", LAUCH_LOOK);
        mUser = (User) this.getIntent().getSerializableExtra("user");
        from = this.getIntent().getIntExtra("from", 0);
        mtype = this.getIntent().getIntExtra("mtype", 0);

        initView();
        setListener();
        initData();
    }


    private void initView() {
        mTitleName = (TextView) findViewById(R.id.tv_title);
        mTitleBack = (ImageView) findViewById(R.id.iv_left);
        mTitleRight = (TextView) findViewById(R.id.tv_right);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        tvPrivageCount = (TextView) findViewById(R.id.minegift_textview);

        mTitleBack.setVisibility(View.VISIBLE);
        mTitleRight.setVisibility(View.VISIBLE);

        mTitleName.setText(R.string.private_gift);
        mTitleBack.setImageResource(R.drawable.title_back);
        mTitleRight.setText(getResources().getString(R.string.vip_recharge));
//		llcontent = (LinearLayout) findViewById( R.id.llcontent );
//		tvGiftCount = (TextView) findViewById( R.id.tvGiftCount );
//		tvGold = (TextView) findViewById( R.id.tvGold );
//		tvDiamond = (TextView) findViewById( R.id.tvDiamond );
        empty_view = (TextView) findViewById(R.id.empty_view);
        if (mUser.getUid() == Common.getInstance().loginUser.getUid()) {
            empty_view.setText(R.string.store_minegift_no_data);
            String str = String.format(getResources().getString(R.string.iaround_mine_or_other_private_gift_toatal), 0);
            tvPrivageCount.setText(str);
        } else {
//            empty_view.setText(R.string.space_mine_no_gift_msg);
            empty_view.setText(R.string.store_minegift_no_data);
            String str = String.format(getResources().getString(R.string.iaround_mine_or_other_private_gift_toatal), 0);
            tvPrivageCount.setText(str);
        }
//		dividerView = findViewById( R.id.view1 );

        mPullScrollView = (PullToRefreshScrollView) findViewById(R.id.giftContent);
        giftGridView = (MyGridView) findViewById(R.id.store_mine_gift_gridview);
//		giftGridAdapter = new GiftGridViewAdapter( );
//		giftGridView.setAdapter( giftGridAdapter );
        giftGridView.setOnItemClickListener(onItemClickListener);
        mPullScrollView.setMode(Mode.DISABLED);

        if (mUser.getUid() != Common.getInstance().loginUser.getUid()) {
//			dividerView.setVisibility( View.GONE );
//			llcontent.setVisibility( View.GONE );
        }
    }


    private void setListener() {
        mTitleRight.setOnClickListener(this);
        mTitleBack.setOnClickListener(this);
        flLeft.setOnClickListener(this);
//		findViewById( R.id.gold_ly ).setOnClickListener( this );
//		findViewById( R.id.diamond_ly ).setOnClickListener( this );
        giftGridView.setOnTouchListener(onTouchListener);
        mPullScrollView.setCustomOnTouchListener(onTouchListener);

        mPullScrollView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {// 下拉刷新
//						reqMineGiftList( mCurPage = 1 );
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

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            dissmissAllPopupWindows();
            return false;
        }
    };

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		int times = 2000;
//		int firstPressTime = 0;
//		if (keyCode == KeyEvent.ACTION_DOWN)
//		{
//			if (System.currentTimeMillis() - firstPressTime > times)
//			{
//				reqMineGiftList( mCurPage = 1 );
//			}
//		}
//
//		return super.onKeyDown(keyCode, event);
//	}

    /**
     * 初始化数据
     */
    private void initData() {
        showProgressDialog(false);
        reqMineGiftList(mCurPage = 1);
    }

    /**
     * 请求我的私藏礼物列表
     */
    private void reqMineGiftList(int nextPage) {
        try {
            User user = null;
            Me me = Common.getInstance().loginUser;
            if (mLauchType == LAUCH_SENT || mLauchType == LAUCH_FOLLOW) {
                user = me;
            } else {
                user = mUser;
            }
            SpaceModel.getInstance(getApplicationContext()).giftMineReq(mActivity,
                    user.getUid(), nextPage, PAGE_SIZE, this);
        } catch (Throwable e) {
            if (mGifts != null && mGifts.size() > 0) {
                handleMineGiftNoData(false);
            } else {
                handleMineGiftNoData(true);
            }
        }
    }


    @Override
    protected void onStop() {
        dissmissAllPopupWindows();
        super.onStop();
    }

    /**
     * 礼物点击监听
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Gift gift = (Gift) parent.getAdapter().getItem(position);
            mCurrentGift = gift;

            if (mLauchType == LAUCH_LOOK) {// 查看他人的私藏礼物信息
                dissmissAllPopupWindows();
                int _dp5 = (int) (5 * getResources().getDisplayMetrics().density);
                mMinePopupWindow = new HPopupWindow(StoreMineGiftActivity.this, makePopupView(
                        gift, 1), _dp5 * 45, _dp5 * 24, R.id.up, R.id.down);
                mMinePopupWindow.setAnimationStyle(R.style.AnimationFade);
                mMinePopupWindow.showAsDropDown(view);
            } else if (mLauchType == LAUCH_SENT) {// 进行送礼
                sendGift(gift);
            } else if (mLauchType == LAUCH_FOLLOW) {// 跳转到已关注列表
                lauchFriendsAttention();
            }

        }
    };

    /**
     * 跳转到已关注列表
     */
    private void lauchFriendsAttention() {
        FriendModel model = FriendModel.getInstance(mActivity);
        int count = model.getMyFollowsCount();

        Intent intent = new Intent( mContext , FriendsAttentionActivity.class );
        Bundle bundle = new Bundle( );
        bundle.putInt( "mode" , 1 );
        bundle.putInt( "count" , count );
        bundle.putInt( "from" , from );
        bundle.putBoolean( "isFromStore" , true );
        bundle.putSerializable( "gift" , mCurrentGift );
        intent.putExtras( bundle );
        startActivityForResult( intent , 1000 );
        //gh
    }

    /**
     * 使用Gift制作PopupView
     *
     * @param gift 礼物实例
     * @param type 类型 TYPE_MINE或TYPE_RECEVIS
     * @return
     */
    private View makePopupView(final Gift gift, int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.space_giftlist_popup, null);
        NetImageView icon = (NetImageView) view.findViewById(R.id.icon);
        TextView person = (TextView) view.findViewById(R.id.source_person);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView price = (TextView) view.findViewById(R.id.price);
        TextView charm = (TextView) view.findViewById(R.id.charm);
        TextView time = (TextView) view.findViewById(R.id.time);
        Button handsel = (Button) view.findViewById(R.id.handsel_btn);

        icon.executeRound(PicIndex.DEFAULT_GIFT, gift.getIconUrl());
        name.setText(_nameTitle + gift.getName(mContext));
        price.setText(_priceTitle + gift.getPrice());
        charm.setText(_charmTitle + "+" + gift.getCharisma());
        time.setText(_buyTimeTitle + TimeFormat.timeFormat4(mContext, gift.getTime()));
        name.setVisibility(View.VISIBLE);
        price.setVisibility(View.VISIBLE);
        charm.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
        person.setVisibility(View.GONE);
        handsel.setVisibility(View.GONE);

        return view;
    }


    /**
     * 赠送礼物
     *
     * @param gift
     */
    private void sendGift(final Gift gift) {
        if (!mUser.isSVip() && !mUser.isVip() && gift.getVipLevel() == 1) {// 该礼物只有VIP才能发送
            DialogUtil.showTobeVipDialog(StoreMineGiftActivity.this, R.string.vip_gift,
                    R.string.tost_gift_vip_privilege);
            return;
        }

        DialogUtil.showOKCancelDialog(this, R.string.chat_personal_send_gift,
                R.string.send_gift_conf, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog(true);
                        try {
                            // 赠送礼物
                            SpaceModel.getInstance(StoreMineGiftActivity.this).sendMineGiftReq(mActivity,
                                    mUser.getUid(), gift.getMid(), StoreMineGiftActivity.this);
                        } catch (Throwable e) {
                            hideProgressDialog();
                            Toast.makeText(mContext, R.string.network_req_failed,
                                    Toast.LENGTH_SHORT).show();
                            CommonFunction.log(e);
                        }
                    }
                });
    }

    class GiftGridViewAdapter extends BaseAdapter {

        public void setData(ArrayList<Gift> gifts) {
            mGifts = gifts;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mGifts != null && mGifts.size() > 0) {
                return mGifts.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return mGifts.get(position);
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
                convertView = LayoutInflater.from(StoreMineGiftActivity.this).inflate(
                        R.layout.store_gift_classify_item, null);
                viewHolder.icon = (NetImageView) convertView.findViewById(R.id.gift_icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.gift_name);
                viewHolder.price = (TextView) convertView.findViewById(R.id.gift_price);
                viewHolder.charm = (TextView) convertView.findViewById(R.id.gift_charm);
                viewHolder.experience = (TextView) convertView.findViewById(R.id.gift_experience);
                viewHolder.giftFlagLy = (RelativeLayout) convertView
                        .findViewById(R.id.gift_flag);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Gift gift = mGifts.get(position);

            if (gift != null) {
                viewHolder.icon.executeFadeInRound(PicIndex.DEFAULT_GIFT, gift.getIconUrl());
                viewHolder.name.setText(gift.getName(mContext));

                String price = "";
                String priceTypeStr = "";
                if (gift.getCurrencytype() == 1) {// 1金币2钻石
                    priceTypeStr = mContext.getString(R.string.gold_balance);
                    viewHolder.price.setTextColor(Color.parseColor("#999999"));
                } else {
                    priceTypeStr = mContext.getString(R.string.diamond_balance);
                    viewHolder.price.setTextColor(Color.parseColor("#FF9900"));
                }

                if (gift.discountgoldnum != null && !gift.discountgoldnum.equals("null")) {// 优惠价格
                    price = gift.getDiscountgoldnum();
                } else {// 非优惠
                    price = String.valueOf(gift.getPrice());
                }

                viewHolder.price.setText(priceTypeStr + price);
                viewHolder.charm.setText(mContext.getString(R.string.charisma_title_new) + gift.getCharisma());
                viewHolder.experience.setText(String.format(mContext.getString(R.string.chat_gift_exp), gift.expvalue));
                viewHolder.giftFlagLy.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    private Handler mTheMainHandler = new Handler(Looper.getMainLooper()) {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            hideProgressDialog();
            HashMap<String, Object> res = (HashMap<String, Object>) msg.obj;

            if (msg.what == MSG_WHAT_REFRESH_MINE) {// 刷新私藏礼物
                refreshMineGiftsData(res);
            } else if (msg.what == MSG_WHAT_REFRESH_MINE_SEND) {// 赠送礼物结果
                handleSendGiftResult(res);
            } else if (msg.what == MSG_WHAT_REFRESH_MINE_SOCKET_SEND) {// 赠送礼物私聊socket结果
                handleSendSessionSuccess();
            } else if (msg.what == MSG_WHAT_REFRESH_MINE_EMPTY) {// 私藏礼物为空
                handleMineGiftNoData(true);
            }
            mPullScrollView.onRefreshComplete();
        }
    };

    /**
     * 礼物列表为空
     */
    protected void handleMineGiftNoData(Boolean isShowEmptyView) {
        hideProgressDialog();
        if (isShowEmptyView) {
            showEmptyView();
        } else {
            Toast.makeText(this, R.string.network_req_failed, Toast.LENGTH_SHORT).show();
        }
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
                if (isFastDoubleClick()) {
                    initData();
                }
            }
        });
        giftGridView.setVisibility(View.GONE);
    }


    /**
     * 关闭空数据的提示语
     */
    private void hideEmptyView() {
        empty_view.setVisibility(View.GONE);
        giftGridView.setVisibility(View.VISIBLE);
    }

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime > 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 赠送礼物结果
     */
    private void handleSendGiftResult(HashMap<String, Object> res) {
        int status = ((Integer) res.get("status")).intValue();
        if (res != null && status == 200) {
            sendSuccess();
        } else {
            int error = ((Integer) res.get("error")).intValue();
            if (status == -400) {
                error = 4000;
            }

            if (error == 4000 || error == 5930) { // 充值
                balance();
            } else {
                sendFail(res);
            }
        }
    }

    /**
     * 送礼成功
     */
    private void sendSuccess() {
        sendGiftMessage();// 发送一个送礼成功的私聊消息
    }

    /**
     * 送礼失败
     *
     * @param res
     */
    private void sendFail(HashMap<String, Object> res) {
        try {
            ErrorCode.showErrorByMap(mContext, res);
            ErrorCode.toastError(mContext, ErrorCode.E_5804);
            finish();
        } catch (Exception e) {
            CommonFunction.log(e);
        }
    }

    /**
     * 送礼成功Dialog提示
     */
    private void handleSendSessionSuccess() {
//		currentAmount = currentAmount - 1 ;
//		String str = String.format( getResources( ).getString( R.string.private_gift_number ) ,
//				currentAmount);
//		tvGiftCount.setText( str );

        haveSentDialog = DialogUtil.showOKDialog(StoreMineGiftActivity.this, R.string.chat_personal_send_gift,
                R.string.gift_sent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        haveSentDialog = null;
                        if (diamondCanTalkDialog == null) {
                            Intent data = new Intent();
                            data.putExtra("type", mCurrentGift.getCurrencytype());
                            setResult(Activity.RESULT_OK, data);
                            finish();
                        }
                    }
                });
    }

    /**
     * 充值
     */
    private void balance() {
        DialogUtil.showTwoButtonDialog(StoreMineGiftActivity.this, mContext.getString(R.string.send_gift_failed),
                mContext.getString(R.string.insufficient_balance),
                mContext.getString(R.string.ok), mContext.getString(R.string.get_gold_coins),
                null, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentPayBuyGlod.jumpPayBuyGlodActivity(StoreMineGiftActivity.this);
                    }
                });
    }

    /**
     * 当发送礼物消息成功之后，发送一条私聊消息
     */
    private void sendGiftMessage() {
        if (mCurrentGift != null) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("giftname", mCurrentGift.getName(this));
            map.put("charmnum", String.valueOf(mCurrentGift.getCharisma()));
            String price = "";
            if (mCurrentGift.getDiscountgoldnum() != null
                    && !mCurrentGift.getDiscountgoldnum().equals("null")) {
                price = mCurrentGift.getDiscountgoldnum();
            } else {
                price = String.valueOf(mCurrentGift.getPrice());
            }
            map.put("price", price);
            map.put("currencytype", String.valueOf(mCurrentGift.getCurrencytype()));
            map.put("giftnum", 1);
            map.put("exp", mCurrentGift.expvalue);
            String content = JsonUtil.mapToJsonString(map);
            mReqSendMsgFlag = System.currentTimeMillis();

            long flag = SocketSessionProtocol.sessionPrivateMsg(this, mReqSendMsgFlag,
                    mUser.getUid(), mtype, String.valueOf(ChatMessageType.GIFE_REMIND),
                    mCurrentGift.getIconUrl(), from, content);
            if (flag == -1) {
                mTheMainHandler.sendEmptyMessage(MSG_WHAT_REFRESH_MINE_SOCKET_SEND);
                return;
            }

            Me me = Common.getInstance().loginUser;
            mChatRecord = new ChatRecord();
            mChatRecord.setId(-1); // 消息id
            mChatRecord.setUid(me.getUid());
            mChatRecord.setNickname(me.getNickname());
            mChatRecord.setIcon(me.getIcon());
            mChatRecord.setVip(me.getViplevel());
            mChatRecord.setDatetime(mReqSendMsgFlag);
            mChatRecord.setType(Integer.toString(6));
            mChatRecord.setStatus(ChatRecordStatus.SENDING); // 发送中
            mChatRecord.setAttachment(mCurrentGift.getIconUrl());
            mChatRecord.setContent(content);
            mChatRecord.setUpload(false);
            mChatRecord.setfLat(mUser.getLat());
            mChatRecord.setfLng(mUser.getLng());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getConnectorManage().setCallBackAction(this);
    }


    /**
     * 刷新私藏的礼物UI
     *
     * @param res
     */
    @SuppressWarnings("unchecked")
    private void refreshMineGiftsData(HashMap<String, Object> res) {
        if (res == null) {
            return;
        }

        Object result = res.get("gifts");
        if (result == null) {
            return;
        }

        ArrayList<Gift> array = (ArrayList<Gift>) result;
        mCurPage = (Integer) res.get("pageno");
        int amount = (Integer) res.get("amount");
        Integer goldnum = (Integer) res.get("goldnum");
        Integer diamondnum = (Integer) res.get("diamondnum");
        this.goldnum = goldnum == null ? 0 : goldnum.intValue();
        this.diamondnum = diamondnum == null ? 0 : diamondnum.intValue();
        String str = String.format(getResources().getString(R.string.iaround_mine_or_other_private_gift_toatal), amount);
        tvPrivageCount.setText(str);
        if (amount > 0) {
            mTotalPage = amount / PAGE_SIZE;
            if (amount % PAGE_SIZE > 0) {
                mTotalPage++;
            }

            if (mCurPage <= 1) {
                if (mGifts == null) {
                    mGifts = new ArrayList<Gift>();
                } else {
                    mGifts.clear();
                }
            }
            if (array != null && array.size() > 0) {
                mGifts.addAll(array);
            }

            if (mTotalPage == mCurPage) {
                mPullScrollView.setMode(Mode.DISABLED);
            } else {
                mPullScrollView.setMode(Mode.PULL_FROM_END);
            }

            if (mUser.getUid() == Common.getInstance().loginUser.getUid()) {
                setGoldNum();
            }

//			String str = String.format( getResources( ).getString( R.string.private_gift_number ) ,
//					amount );
//			tvGiftCount.setText( str );
            currentAmount = amount;

            if (empty_view != null && empty_view.isShown()) {
                hideEmptyView();
            }
//			giftGridAdapter.notifyDataSetChanged( );
            giftGridAdapter = new GiftGridViewAdapter();
            giftGridAdapter.setData(mGifts);
            giftGridAdapter.notifyDataSetChanged();
            giftGridView.setAdapter(giftGridAdapter);
            mPullScrollView.setScrollY(0);
        } else {
            if (mGifts != null && mGifts.size() > 0) {
                handleMineGiftNoData(false);
            } else {
                handleMineGiftNoData(true);
            }
//			if ( mUser.getUid( ) == Common.getInstance( ).loginUser.getUid( ) ){
//				tvGold.setText( _goldNum + PayModel.getInstance( ).getGoldNum() );
//				tvDiamond.setText( _diamondNum + PayModel.getInstance( ).getDiamondNum() );
//			}
        }
    }

    /**
     * 设置金币 、钻石数量
     */
    private void setGoldNum() {
//		tvGold.setText( _goldNum + goldnum );
//		tvDiamond.setText( _diamondNum + diamondnum );
        PayModel.getInstance().setGoldNum(goldnum);
        PayModel.getInstance().setDiamondNum(diamondnum);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);

        SpaceModelReqTypes reqType = SpaceModel.getInstance(StoreMineGiftActivity.this).getReqType(flag);
        if (reqType == SpaceModelReqTypes.GIFT_DETAIL_MINE) {
            if (mGifts != null && mGifts.size() > 0) {
                handleMineGiftNoData(false);
            } else {
                handleMineGiftNoData(true);
            }
        } else {
            hideProgressDialog();
            ErrorCode.toastError(mContext, e);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        SpaceModel netwrok = SpaceModel.getInstance(this);
        HashMap<String, Object> res = null;
        try {
            res = netwrok.getRes(result, flag);
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }

        int status = 0;
        SpaceModelReqTypes reqType = (SpaceModelReqTypes) res.get("reqType");
        if (res != null && (status = (Integer) res.get("status")) == 200) {
            if (SpaceModelReqTypes.GIFT_DETAIL_MINE == reqType) {// 私藏礼物
                mTheMainHandler.sendMessage(mTheMainHandler.obtainMessage(MSG_WHAT_REFRESH_MINE,
                        res));
            } else if (SpaceModelReqTypes.SEND_MINE_GIFT == reqType) {// 赠送礼物
                mTheMainHandler.sendMessage(mTheMainHandler.obtainMessage(
                        MSG_WHAT_REFRESH_MINE_SEND, res));
            }
        } else if (res != null && status == 5806) {// 礼物为空
            if (SpaceModelReqTypes.GIFT_DETAIL_MINE == reqType
                    || SpaceModelReqTypes.SEND_MINE_GIFT == reqType) {
                mTheMainHandler.sendMessage(mTheMainHandler
                        .obtainMessage(MSG_WHAT_REFRESH_MINE_EMPTY));
            }
        } else {
            if (SpaceModelReqTypes.GIFT_DETAIL_MINE == reqType) {
                mTheMainHandler.sendMessage(mTheMainHandler
                        .obtainMessage(MSG_WHAT_REFRESH_MINE_EMPTY));
            }
        }
    }

    @Override
    public void onReceiveMessage(TransportMessage message) {
        if (message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC
                || message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT_FAIL) {

            String json = message.getContentBody();
            SocketSuccessResponse response = GsonUtil.getInstance().getServerBean(json,
                    SocketSuccessResponse.class);

            if (response.flag == mReqSendMsgFlag) {
                if (message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC) { // 发送成功
                    mChatRecord.setId(response.msgid);
                    mChatRecord.setStatus(ChatRecordStatus.ARRIVED); // 已送达

                    int subGroup;
                    if (mtype == 0) {
                        subGroup = SubGroupType.NormalChat;
                    } else {
                        subGroup = SubGroupType.SendAccost;
                    }
                    ChatPersonalModel.getInstance().insertOneRecord(mActivity, mUser,
                            mChatRecord, subGroup, from);
                }
            }
            mTheMainHandler.sendEmptyMessage(MSG_WHAT_REFRESH_MINE_SOCKET_SEND);

        }
        super.onReceiveMessage(message);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//			case R.id.gold_ly :
//				FragmentPayBuyGlod.jumpPayBuyGlodActivity(mContext);
//				break;
//			case R.id.diamond_ly :
//				FragmentPayDiamond.jumpPayDiamondActivity(mContext);
//				break;
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                Intent vipIntent = new Intent(StoreMineGiftActivity.this, UserVIPActivity.class);
                startActivity(vipIntent);
                break;
        }
    }

    /**
     * 送礼成功就关闭这个界面
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            sendGiftAfterRefreshView(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 送礼成功后返回到该页面刷新
     *
     * @param data
     */
    private void sendGiftAfterRefreshView(Intent data) {
        if (data != null) {
            int giftId = data.getIntExtra("giftId", 0);
            if (mCurrentGift.getMid() == giftId) {
                if (mGifts.size() > 1) {
                    for (int i = 0; i < mGifts.size(); i++) {
                        if (mGifts.get(i).getMid() == giftId) {
                            mGifts.remove(i);
//							currentAmount = currentAmount - 1 ;
//							String str = String.format( getResources( ).getString( R.string.private_gift_number ) ,
//									currentAmount  );
//							tvGiftCount.setText( str );
                            giftGridAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    mGifts.clear();
                    giftGridAdapter.notifyDataSetChanged();
                    showEmptyView();
                }
            }
        }
    }


    /**
     * 从他人个人资料入口进入，点击礼物查看礼物信息
     *
     * @param context
     * @param user
     */
    public static void launchMineGiftToLook(Context context, User user) {
        Intent intent = new Intent(context, StoreMineGiftActivity.class);
        intent.setClass(context, StoreMineGiftActivity.class);
        intent.putExtra("category", LAUCH_LOOK);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }

    /**
     * 从商店入口或登录用户的个人资料跳转到私藏礼物页面， 该跳转意图下，点击礼物跳转至已关注列表，可进行送礼
     */
    public static void launchMineGiftToFollow(Context context, User user) {
        Intent intent = new Intent(context, StoreMineGiftActivity.class);
        intent.setClass(context, StoreMineGiftActivity.class);
        intent.putExtra("category", LAUCH_FOLLOW);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }

    /**
     * 个人资料下底栏或私聊入口进入，该跳转意图下，点击礼物进行送礼
     *
     * @param context
     * @param user
     * @param requestCode
     * @param from        从哪里来
     * @param mType       聊天的类型 (0为正常私聊，1为搭讪 )
     */
    public static void launchMineGiftToSent(Context context, User user, int requestCode,
                                            int from, int mType) {
        Intent intent = new Intent(context, StoreMineGiftActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("uid", user.getUid());
        intent.putExtra("gender", user.getSexIndex());
        intent.putExtra("viplevel", user.getViplevel());
        intent.putExtra("nickname", user.getNickname());
        intent.putExtra("icon", user.getIcon());
        intent.putExtra("category", LAUCH_SENT);
        intent.putExtra("from", from);
        intent.putExtra("mtype", mType);
        intent.putExtra("lat", user.getLat());
        intent.putExtra("lng", user.getLng());
        intent.setClass(context, StoreMineGiftActivity.class);
        if (requestCode > 0) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 显示加载框
     *
     * @param isSending
     */
    private void showProgressDialog(Boolean isSending) {
        if (mProgressDialog == null) {
            if (isSending == true) {// 送礼
                mProgressDialog = DialogUtil.showProgressDialog(mContext,
                        R.string.chat_personal_send_gift,
                        R.string.pull_to_refresh_refreshing_label, null);
            } else {
                // 加载
                mProgressDialog = DialogUtil.showProgressDialog(mContext, R.string.dialog_title,
                        R.string.content_is_loading, null);
            }
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


    static class ViewHolder {
        NetImageView icon;
        TextView name;
        TextView price;
        TextView experience;
        TextView charm;
        RelativeLayout giftFlagLy;
    }

}
