package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.entity.ChatbarSendGiftBean;
import net.iaround.model.chatbar.ChatBarBackpackBean;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.ObtainInfoBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.Me;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.LovePayActivity;
import net.iaround.ui.activity.VideoChatActivity;
import net.iaround.ui.adapter.chat.GiftLayoutAdapter;
import net.iaround.ui.chat.view.ChatFacePointView;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.chatbar.adapter.ChatBarGiftPopWindowAdapter;
import net.iaround.ui.chatbar.view.GroupChatBarGiftPopWindow;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.interfaces.ChatbarSendPersonalSocketListener;
import net.iaround.ui.interfaces.PersonalSendGiftPopListenerDismiss;
import net.iaround.ui.interfaces.PersonalSendGiftPopListenerShow;
import net.iaround.ui.interfaces.SendPersonalSocketListener;
import net.iaround.ui.interfaces.VideoChatPersonalSocketListenter;
import net.iaround.ui.view.LuxuryGiftView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class: 爱心礼物模块
 * Author：yuchao
 * Date: 2017/8/8 18:29
 * Email：15369302822@163.com
 */
public class CustomGiftDialog extends Dialog implements View.OnClickListener, DialogInterface.OnKeyListener {
    private static final String TAG = "CustomGiftDialog";
    /**记录关闭当前礼物面板时记录商店最后礼物位置的key*/
    public static String LAST_LOVE_GIFT_POSTION = "lastLoveGiftPosition";
    /**记录关闭当前礼物面板时记录商店最后礼物数组位置的key*/
    public static String LAST_LOVE_GIFT_ARRAY_POSTION = "lastLoveGGiftArrayPosition";
    /**记录商店最后点击的礼物数组的数量**/
    public static String LAST_LOVE_GIFT_ARRAY_VALUE = "lastLoveGGiftArrayValue";
    /**记录商店最后点击的礼物数组的数量**/
    public static String LIST_STORE_CACHE = "ListStoreCache";
    /**最后选中商店的礼物的位置*/
    private int lastStoreGiftPosition;
    /**最后选中商店的礼物的数组的位置*/
    private int lastStoreGiftArrayPosition;
    /**最后选中商店的礼物数组的Value*/
    private long lastStoreGiftArrayValue = 1;
    /**记录当前选中的礼物的key*/
    public final int CURRENT_GIFT = 10001;
    /**记录当前定时器时间的key*/
    public final int CURRENT_TIME = 10002;

    public final int CURRENT_UPDATE_TIME = 10005;
    /**判断当前剩余礼物数量是否充足*/
    public final int GIFT_IS_ENOUGH = 10003;
    /**初始化选中礼物的数组的默认值*/
    public final int GIFT_ARRAY_DATA = 10004;
    /**当前选中礼物*/
    private ChatBarBackpackBean.ListBean mSelectGiftBean;
    /**上次选中的商店礼物礼物*/
    private ChatBarBackpackBean.ListBean lastSelectStoreGiftBean = new ChatBarBackpackBean.ListBean();
    /**判断上一个礼物跟下一礼物数组之间的中间数组数量*/
    private long lastArrayNum = 0;
    /**上次礼物选中的数组*/
    private long lastArrayValue = 1;
    /**发送商店礼物Flag*/
    private long sendStoreGiftFlag;
    /**礼物赠送数量可点击*/
    private LinearLayout llSendgiftAmount;
    /**礼物赠送数量不可点击*/
    private LinearLayout llSendgiftAmoundNo;
    /**礼物布局*/
    private ViewPager vpGifyLayout;
    /**圆点指示器布局*/
    private ChatFacePointView circlePageIndicator;
    /**vp中view适配器*/
    private GiftLayoutAdapter giftLayoutAdapter;
    /**金币钻石充值区域*/
    private LinearLayout llDiamondLayout;
    private TextView tvLoveToatal;
    /**发送按钮*/
    private LinearLayout llSendLayout;
    /**数组和发送区域*/
    private LinearLayout llArrayAndSendLayout;
    /**倒计时区域*/
    private RelativeLayout rlTimeTask;
    private RelativeLayout rlTime;
    private TextView tvTime;
    /**礼物适配器*/
    private GiftAdapter giftAdapter;
    /**礼物数组选定的数字*/
    private TextView tvChatBarGifgNum;
    /**礼物布局RecycleView*/
    private RecyclerView rvGift;
    /**加载等待提示*/
    private Dialog mWaitDialog;
    private GroupChatBarGiftPopWindow popWindow;
    /**发送礼物按钮*/
    private Button btSend;
    /**
     * View结束
     */
    private Timer timer;
    /**判断是否是私聊*/
    int isFromChatPersonal = 2;// 1 私聊 2 聊吧 3 爱心
    private Typeface typeface;
    /**上下文*/
    private Context mContext;
    /**当前聊吧id*/
    private long groupId;
    /**当前被点击的User对象*/
    private User curendUser;
    /**商店礼物详情*/
    private List<ChatBarBackpackBean.ListBean> giftStoreBeens;
    /**GridView数量的集合*/
    private List<View> mViewPagerGridList;
    /**网络获取商店，背包礼物flag*/
    private long storeGiftDataFlag;
    /**礼物数组bean类的集合*/
    private List<ChatBarBackpackBean.ListBean.GiftComboBean> mGifgComboBeanList;
    /**当前选中礼物的数组连送的集合*/
    private List<ChatBarBackpackBean.ListBean.GiftComboBean> listGiftComboBeans;
    /**当前选中礼物数组value值的集合*/
    private List<Integer> listGiftComboBeanValues;
    /**礼物总页数*/
    private int totalPage;
    /**每页展示8条目数*/
    private int mPagesize = 8;
    /**更新金币，钻石是否刷新布局*/
    private boolean  isRefreshGiftLayout= true;
    /**展示动画的view*/
    private LuxuryGiftView luxuryGiftView;
    /**当前用户的钻石数和金币数*/
    private long userLove;
    /**判断popwindow当前点击的位置*/
    private int currentPostion = 0;
    /**判断是否是背包礼物*/
    private boolean isBagsGift = false;
    /**背包某个礼物剩余总量*/
    private long haveGiftTotalNum;
    /**是否连击的标识*/
    private long userGiftId = 0;
    /**倒计时时间3秒*/
    private int time = 0;
    /**倒计时时间3秒常量*/
    private int timeStatic = 0;
    //礼物的类
    private Gift mCurrentGift;
    private String giftUrl;//礼物的图标
    // Flag
    private long mReqSendMsgFlag;// 发送私聊的请求
    private static int from = 0;// 从哪里进入聊天
    private int mtype = 1;// 聊天类型（0-正常私聊，1-搭讪聊天）
    /*** 发送私聊socket消息时候本地插入一条消息*/
    private ChatRecord mChatRecord;
    private ImageView ivSendGiftUp;
    /**发送完socket后回传数据给私聊页面的回调*/
    private SendPersonalSocketListener mPersonalListenr;
    private PersonalSendGiftPopListenerDismiss mPsrsonalSendGiftDismiss;
    private PersonalSendGiftPopListenerShow mPersonalSendGiftshow;
    /**倒计时结束回传数据给activity的回调*/
    private ChatbarSendPersonalSocketListener mListener;
    /***
     * 剩余时长监听
     */
    private VideoChatPersonalSocketListenter mHadTimeListener;

    private long mRequestRelationFlag = 0;

    public CustomGiftDialog(Context context) {
        super(context, R.style.chat_bar_sendgift_transparent_dialog);
        this.mContext = context;
    }

    public CustomGiftDialog(Context context, int themeResId) {
        super(context, R.style.chat_bar_sendgift_transparent_dialog);
        this.mContext = context;
    }

    protected CustomGiftDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context,R.style.chat_bar_sendgift_transparent_dialog);
        this.mContext = context;
    }

    /**
     * 聊吧打开礼物面板
     * @param context
     * @param user
     *              送礼对象
     * @param groupId
     *              当前所在聊吧id
     * @param luxuryGiftView
     *              礼物动画
     */
    public static CustomGiftDialog jumpIntoCustomGiftDia(Context context, User user, long groupId, LuxuryGiftView luxuryGiftView,ChatbarSendPersonalSocketListener listener,VideoChatPersonalSocketListenter mHadTimeListener)
    {
        CustomGiftDialog customGiftDia = new CustomGiftDialog(context);
        customGiftDia.setGroupId(groupId);
        customGiftDia.setUser(user);
        customGiftDia.setLuxuryGiftView(luxuryGiftView);
        customGiftDia.setListener(listener);
        customGiftDia.setVideoChatHadTimeListenter(mHadTimeListener);
        if (!customGiftDia.isShowing())
        {
            customGiftDia.show();
        }

        return customGiftDia;
    }
    public static void jumpIntoCustomGiftDia(Context context,User user,SendPersonalSocketListener listener,PersonalSendGiftPopListenerDismiss mPsrsonalSendGiftDismiss,PersonalSendGiftPopListenerShow mPersonalSendGiftshow)
    {
        CustomGiftDialog customGiftDia = new CustomGiftDialog(context);
        customGiftDia.setUser(user);
        customGiftDia.setIsFromChatPersonal(3);
        customGiftDia.setSendPersonalSocketListener(listener);
        customGiftDia.setPersonalSendGiftPopListenerDismiss(mPsrsonalSendGiftDismiss);
        customGiftDia.setPersonalSendGiftPopListenerShow(mPersonalSendGiftshow);
        if (!customGiftDia.isShowing())
        {
            customGiftDia.show();
        }
    }
    /**
     * 获取当前送礼的user对象
     * @param user
     */
    private void setUser(User user)
    {
        this.curendUser = user;
    }

    /**
     * 获取当前所在的聊吧id
     */
    private void setGroupId(long groupId)
    {
        this.groupId = groupId;
    }
    /**
     * 获取展示动画的view
     */
    private void setLuxuryGiftView(LuxuryGiftView luxuryGiftView)
    {
        this.luxuryGiftView = luxuryGiftView;
        if(luxuryGiftView!=null){
            luxuryGiftView.upDateDefaultMargin(5);
        }
    }
    private void setListener(ChatbarSendPersonalSocketListener listener)
    {
        this.mListener = listener;
    }

    /**
     * 剩余时长监听方法
     * @param listener
     */
    private void setVideoChatHadTimeListenter(VideoChatPersonalSocketListenter listener)
    {
        this.mHadTimeListener = listener;
    }
    private void setIsFromChatPersonal(int isFromChatPersonal)
    {
        this.isFromChatPersonal = isFromChatPersonal;
    }
    private void setSendPersonalSocketListener(SendPersonalSocketListener listener)
    {
        this.mPersonalListenr = listener;
    }
    private void setPersonalSendGiftPopListenerDismiss(PersonalSendGiftPopListenerDismiss listener)
    {
        this.mPsrsonalSendGiftDismiss = listener;
    }
    private void setPersonalSendGiftPopListenerShow(PersonalSendGiftPopListenerShow listener)
    {
        this.mPersonalSendGiftshow = listener;
    }
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isBagGift;
            switch (msg.what)
            {
                case CURRENT_TIME://定时器
                    ChatBarBackpackBean.ListBean listBean = (ChatBarBackpackBean.ListBean) msg.obj;
                    if (listBean != null)
                    {
                        if (timer != null)
                            timer.cancel();
                        timer = null;
                        rlTimeTask.setVisibility(View.GONE);
                        llArrayAndSendLayout.setVisibility(View.VISIBLE);
                        if(isFromChatPersonal==2){
                            mListener.update(true,listBean);
                        }
                    }
                    break;
                case GIFT_ARRAY_DATA://初始化礼物数组选择
                    isBagGift = (boolean) msg.obj;
                    rememberUserArraySelect(isBagGift);
                    initGiftArrayAndSendLayout(isBagGift);
                    break;
                case CURRENT_GIFT:// 切换礼物时，取消连送效果
                    isBagGift = (boolean) msg.obj;
                    cancleSendAnimation(isBagGift);
                    break;
                case CURRENT_UPDATE_TIME:
                    int reclen = (int) msg.obj;
                    tvTime.setText(reclen + "");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbar_bottom_love_gift_layout);

        EventBus.getDefault().register(CustomGiftDialog.this);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //底部弹出样式
        window.setWindowAnimations(R.style.popwin_anim_style);//动画
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//范围
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setOnKeyListener(this);

        giftStoreBeens = new ArrayList<>();
        mViewPagerGridList = new ArrayList<>();
        mGifgComboBeanList=new ArrayList<>();
        listGiftComboBeans = new ArrayList<>();
        listGiftComboBeanValues = new ArrayList<>();

        requestBlock();

        initView();
        initData();
        initListener();
        if(mPersonalSendGiftshow!=null){
            mPersonalSendGiftshow.updateHeightShow();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(CustomGiftDialog.this);
        SharedPreferenceUtil.getInstance(mContext).putInt(LAST_LOVE_GIFT_POSTION,lastStoreGiftPosition);
        SharedPreferenceUtil.getInstance(mContext).putInt(LAST_LOVE_GIFT_ARRAY_POSTION,lastStoreGiftArrayPosition);
        SharedPreferenceUtil.getInstance(mContext).putLong(LAST_LOVE_GIFT_ARRAY_VALUE,lastStoreGiftArrayValue);
    }
    /***
     * EventBus接收更新金币，钻石通知
     */
    @Subscribe
    public void receiveUpdateMsg(String msg)
    {
        isRefreshGiftLayout = false;
        storeGiftDataFlag = GroupHttpProtocol.getLoveGiftData(mContext,new HttpCallBackImpl(this));
    }
    private void initView()
    {
        btSend = (Button) findViewById(R.id.bt_send);
        llSendgiftAmount = (LinearLayout) findViewById(R.id.ll_select_amound);
        llSendgiftAmoundNo = (LinearLayout) findViewById(R.id.ll_select_amound_no);
        vpGifyLayout = (ViewPager) findViewById(R.id.vp_gift);
        circlePageIndicator = (ChatFacePointView) findViewById(R.id.chatface_point);

        llDiamondLayout = (LinearLayout) findViewById(R.id.ll_diamond_layout);
        tvLoveToatal = (TextView) findViewById(R.id.tv_diamond_amount);

        llSendLayout = (LinearLayout) findViewById(R.id.ll_send_layout);

        tvChatBarGifgNum= (TextView) findViewById(R.id.tv_chat_bar_gift_num);

        rlTimeTask = (RelativeLayout) findViewById(R.id.rl_time_task);
        rlTime = (RelativeLayout) findViewById(R.id.rl_time);
        tvTime = (TextView) findViewById(R.id.tv_time);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null == typeface)
                    typeface = Typeface.createFromAsset(mContext.getAssets(), "DIN-BlackItalic.otf");
                tvTime.setTypeface(typeface);
            }
        });

        llArrayAndSendLayout = (LinearLayout) findViewById(R.id.ll_sendgift_amout);

        ivSendGiftUp= (ImageView) findViewById(R.id.iv_group_send_gift_up);
        //初始化vp
        vpGifyLayout.setCurrentItem(0);
    }
    private void initData()
    {

        String storeListCache = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(LIST_STORE_CACHE);

        //加载上一次礼物数据
        if (storeListCache != null && !TextUtils.isEmpty(storeListCache)){
            try {
                ChatBarBackpackBean bean = GsonUtil.getInstance().getServerBean(storeListCache,ChatBarBackpackBean.class);
                handleStoreGiftData(bean,storeListCache);
            }catch (Exception e){
                e.printStackTrace();
                //请求商店礼物的数据
                storeGiftDataFlag = GroupHttpProtocol.getLoveGiftData(mContext, new HttpCallBackImpl(this));
            }

        }
        //请求商店礼物的数据
        storeGiftDataFlag = GroupHttpProtocol.getLoveGiftData(mContext, new HttpCallBackImpl(this));

    }
    private void initListener() {
        vpGifyLayout.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当前索引是页码，滚动的时候只跟新绘制当前页面
                if (mViewPagerGridList.get(position) instanceof RecyclerView){
                    RecyclerView giftRe = (RecyclerView) mViewPagerGridList.get(position);
                    giftRe.getAdapter().notifyDataSetChanged();
                }

                circlePageIndicator.setCurrentItem(position);
                circlePageIndicator.setPageOffset(positionOffset);
                circlePageIndicator.setCount(totalPage);
                circlePageIndicator.invalidate();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        llDiamondLayout.setOnClickListener(this);
        btSend.setOnClickListener(this);
        llSendLayout.setOnClickListener(this);
        rlTimeTask.setOnClickListener(this);
    }

    /**
     * 初始化礼物面板UI和数据
     * @param listbeans
     * @param isBagGift
     *                  true 是背包礼物
     *                  false 不是背包礼物
     */
    private void initGiftData(List<ChatBarBackpackBean.ListBean> listbeans,boolean isBagGift)
    {
        //初始化RecyclerView数量
        mViewPagerGridList.clear();
        mSelectGiftBean = null;
        //获取礼物数量的总页数
        if (listbeans.size() % 8 == 0)
        {
            totalPage = listbeans.size() / 8;
        }else
        {
            totalPage = listbeans.size() / 8 + 1;
        }

        /***
         * 判断当前拉取礼物列表是否为空
         * 如果是背包礼物，展示空布局
         */
        if (listbeans == null || listbeans.size() == 0)
        {
            circlePageIndicator.setVisibility(View.GONE);
            //设置数组不可点击，发送按钮不可点击状态
            llSendgiftAmoundNo.setVisibility(View.VISIBLE);
            llSendgiftAmount.setVisibility(View.GONE);
            llSendLayout.setBackgroundResource(R.drawable.chatbar_gift_send_gift_no);
            btSend.setEnabled(false);
        }else
        {
            circlePageIndicator.setVisibility(View.VISIBLE);

            for (int i = 0; i < totalPage; i++)
            {
                rvGift = (RecyclerView) LayoutInflater.from(mContext).inflate(R.layout.chatbar_gift_gridview_layout,vpGifyLayout,false);
                giftAdapter = new GiftAdapter(mContext,i,isBagGift,listbeans);
                rvGift.setAdapter(giftAdapter);
                initUserGiftSelect(isBagGift);
                RecyclerView.LayoutManager manager = new GridLayoutManager(mContext,4);
                rvGift.setLayoutManager(manager);
                mViewPagerGridList.add(rvGift);
            }
            giftLayoutAdapter = new GiftLayoutAdapter(mViewPagerGridList);
            vpGifyLayout.setAdapter(giftLayoutAdapter);
        }

        /**
         * 记录用户选择
         */
        initUserGiftSelectPosition(listbeans,isBagGift);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_select_amound://弹出礼物数组
                initGiftArrayPopWindow(isBagsGift);
                Animation operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.group_chat_bar_gift_array);
                operatingAnim.setFillAfter(!operatingAnim.getFillAfter());//每次都取相反值，使得可以不恢复原位的旋转
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                ivSendGiftUp.startAnimation(operatingAnim);
                break;
            case R.id.ll_diamond_layout://钻石
                Intent intent = new Intent(mContext, LovePayActivity.class);
                if(mContext instanceof VideoChatActivity){
                    VideoChatActivity activity = (VideoChatActivity)mContext;
                    activity.startActivityForResult(intent,VideoChatActivity.LOVE_PAY_REQUEST_CODE);
                }else{
                    mContext.startActivity(intent);
                }
                break;
            case R.id.ll_send_layout:
            case R.id.bt_send://发送礼物
                handleSendGift(isBagsGift);
                break;
            case R.id.rl_time_task://连送礼物
                handleBatterSendGift();
                break;
        }
    }
    /**
     * 记录用户离开礼物面板时的礼物选择
     * 1.选中的商店礼物，背包礼物   initUserGiftSelect
     * 2.商店礼物选中数组，背包礼物选中数组  rememberUserArraySelect
     * 3.选中商店礼物所在页，选中背包礼物所在页  initUserGiftSelectPosition
     * 4.如果是背包礼物还需要选择礼物数组小于库存 rememberUserArraySelect
     *
     * @param isBagGift
     *              是否是背包礼物
     */
    /**
     * 记录用户礼物选择
     * @param isBagGift
     */
    private void initUserGiftSelect(boolean isBagGift)
    {
        lastStoreGiftPosition = SharedPreferenceUtil.getInstance(mContext).getInt(LAST_LOVE_GIFT_POSTION,0);

        if (giftStoreBeens.size() <= lastStoreGiftPosition)return;
        if (giftStoreBeens.size() > 0)
            giftStoreBeens.get(lastStoreGiftPosition).isSelected = true;
        mSelectGiftBean = giftStoreBeens.get(lastStoreGiftPosition);
        giftAdapter.setSelectPos(lastStoreGiftPosition);
    }
    private void initUserGiftSelectPosition(List<ChatBarBackpackBean.ListBean> giftBagBeans,boolean isBagGift)
    {
        if (giftBagBeans == null || giftBagBeans.size() <= 0)
            return;
        int giftPosition;
        {
            lastStoreGiftPosition = SharedPreferenceUtil.getInstance(mContext).getInt(LAST_LOVE_GIFT_POSTION,0);
            //记录用户选择礼物所在页
            if (lastStoreGiftPosition < 8)
            {
                giftPosition = 0;
            }else
            {
                giftPosition = lastStoreGiftPosition / 8 ;
            }
            vpGifyLayout.setCurrentItem(giftPosition);
        }
    }
    /**
     * 商店礼物选中数组，背包礼物选中数组
     * 1.商店切换礼物时，首先选择等于上一个礼物的数组，如果没有则取小于上一个礼物数组的数组值
     * 2.背包礼物，首先去上一个礼物数组值并做库存判断，如果上一个礼物数组值大于库存，则取小于库存且小于上一个礼物的数组值
     * @param isBagGift
     */
    private void rememberUserArraySelect(boolean isBagGift) {
        listGiftComboBeans = handleGiftArrayData(isBagGift);
        if (listGiftComboBeans == null )
            return;
        {
            lastStoreGiftArrayPosition = SharedPreferenceUtil.getInstance(mContext).getInt(LAST_LOVE_GIFT_ARRAY_POSTION, mSelectGiftBean.getGift_combo().size() - 1);
            lastArrayNum = SharedPreferenceUtil.getInstance(mContext).getLong(LAST_LOVE_GIFT_ARRAY_VALUE, 1L);

            for (int i = 0; i < listGiftComboBeans.size(); i++) {
                if (lastArrayNum > listGiftComboBeans.get(0).getCombo_value())
                {
                    tvChatBarGifgNum.setText("" + listGiftComboBeans.get(0).getCombo_value());
                    lastStoreGiftArrayPosition = 0;
                    lastStoreGiftArrayValue = mSelectGiftBean.getGift_combo().get(0).getCombo_value();
                    SharedPreferenceUtil.getInstance(mContext).putInt(LAST_LOVE_GIFT_ARRAY_POSTION, lastStoreGiftArrayPosition);
                    SharedPreferenceUtil.getInstance(mContext).putLong(LAST_LOVE_GIFT_ARRAY_VALUE, lastStoreGiftArrayValue);
                    break;
                }
                if (lastArrayNum < listGiftComboBeans.get(i).getCombo_value())
                    continue;
                tvChatBarGifgNum.setText("" + listGiftComboBeans.get(i).getCombo_value());
                lastStoreGiftArrayPosition = i;
                lastStoreGiftArrayValue = mSelectGiftBean.getGift_combo().get(i).getCombo_value();
                SharedPreferenceUtil.getInstance(mContext).putInt(LAST_LOVE_GIFT_ARRAY_POSTION, lastStoreGiftArrayPosition);
                SharedPreferenceUtil.getInstance(mContext).putLong(LAST_LOVE_GIFT_ARRAY_VALUE, lastStoreGiftArrayValue);
                break;
            }
        }
    }
    /**
     * 发送礼物
     */
    private void handleSendGift(boolean isBagGift)
    {
        CommonFunction.log(TAG, "handleSendGift() into");
        if (mSelectGiftBean == null)
            return;
        //自己给你自己送礼
        if (curendUser.getUid() == Common.getInstance().loginUser.getUid())
        {
            Toast.makeText(mContext,mContext.getResources().getString(R.string.chatbar_gift_send_gift_to_self_error), Toast.LENGTH_SHORT).show();
            return;
        }
        lastArrayValue = lastArrayNum;
        haveGiftTotalNum = mSelectGiftBean.getGift_num();
        {//商店礼物
            if (judgeUserDiamonAndGold())
                return;
            sendStoreGift(isBagGift);
            lastStoreGiftArrayValue = SharedPreferenceUtil.getInstance(mContext).getLong(LAST_LOVE_GIFT_ARRAY_VALUE,1L);
            if (userLove >= (mSelectGiftBean.getGift_love() * lastStoreGiftArrayValue) )
            {
                userLove = userLove - (mSelectGiftBean.getGift_love() * lastStoreGiftArrayValue);
                tvLoveToatal.setText(""+userLove);
            }
        }
        for (int i = 0; i < mSelectGiftBean.getGift_combo().size(); i++) {
            if (mSelectGiftBean.getGift_combo().get(i).getCombo_value() != lastArrayNum)
                continue;
            if (mSelectGiftBean.getGift_combo().get(i).getCombo_type() == 0 || isFromChatPersonal == 1)
            {
                llArrayAndSendLayout.setVisibility(View.VISIBLE);
                rlTimeTask.setVisibility(View.GONE);
            }else
            {
                llArrayAndSendLayout.setVisibility(View.GONE);
                rlTimeTask.setVisibility(View.VISIBLE);
                timerTask();
            }
        }
    }
    /**
     * 连击发送礼物
     */
    private void handleBatterSendGift()
    {
        CommonFunction.log(TAG, "handleBatterSendGift() into");
        //动画开始之前可见
        rlTime.setVisibility(View.VISIBLE);
        //开启动画
        initRlTimeTaskAnimation();

        if (curendUser == null || mSelectGiftBean == null)
            return;

        if (userGiftId > 0)
        {//判断连击
            {
            //商店礼物
            if (judgeUserDiamonAndGold())
            {
                return;
            }
            lastStoreGiftArrayValue = SharedPreferenceUtil.getInstance(mContext).getLong(LAST_LOVE_GIFT_ARRAY_VALUE,1L);
            sendStoreGift(false);
            if (userLove >= (mSelectGiftBean.getGift_love() * lastStoreGiftArrayValue) )
            {
                userLove = userLove - (mSelectGiftBean.getGift_love() * lastStoreGiftArrayValue);
                tvLoveToatal.setText(""+userLove);
            }
        }
        }
        //取消之前定时器
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
        //创建新的定时器
        timer = new Timer();
        time = timeStatic;
        if (timer == null)
            timer = new Timer();
        synchronized (timer){
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if(time>0){
                        time--;
                    }

                    Message updateTime = Message.obtain();
                    updateTime.what = CURRENT_UPDATE_TIME;
                    updateTime.obj = time;
                    mHandler.sendMessage(updateTime);

                    if (time == 0){

                        Message message = Message.obtain();
                        message.what = CURRENT_TIME;
                        message.obj = mSelectGiftBean;
                        mHandler.sendMessage(message);
                    }
                }
            };
            timer.schedule(timerTask, 0, 100);
        }
    }

    /**
     * 发送商店礼物
     */
    private void sendStoreGift(boolean isBagGift)
    {
        CommonFunction.log(TAG, "sendStoreGift() into");
        int gift_comboId;
        listGiftComboBeans = handleGiftArrayData(isBagGift);

        if (listGiftComboBeans == null || listGiftComboBeans.size() <= 0)
            return;
        lastArrayNum = SharedPreferenceUtil.getInstance(mContext).getLong(LAST_LOVE_GIFT_ARRAY_VALUE,1L);

        for (int i = 0; i < listGiftComboBeans.size(); i++) {
            if (lastArrayNum != listGiftComboBeans.get(i).getCombo_value())
                continue;
            gift_comboId = listGiftComboBeans.get(i).getCombo_id();
            if(isFromChatPersonal == 1){
                CommonFunction.log(TAG, "sendStoreGift() call sendStoreGift() from ChatPersonal");
                sendStoreGiftFlag = GroupHttpProtocol.sendStoreGift(mContext, curendUser.getUid(), mSelectGiftBean.getGift_id(), userGiftId, 1, gift_comboId, groupId, new HttpCallBackImpl(this));
            }else {
                CommonFunction.log(TAG, "sendStoreGift() call sendStoreGift()");
                sendStoreGiftFlag = GroupHttpProtocol.sendStoreGift(mContext, curendUser.getUid(), mSelectGiftBean.getGift_id(), userGiftId, 1, gift_comboId, groupId, new HttpCallBackImpl(this) );
            }
        }
    }
    /**
     * 处理发送商店礼物成功的回到
     */
    private void handleSendStoreGiftSuc(String result)
    {

        CommonFunction.log("VideoChat", "result" + result);
        CommonFunction.log(TAG, "handleSendStoreGiftSuc() into");
        if (result != null)
        {
            ChatbarSendGiftBean bean = GsonUtil.getInstance().getServerBean(result,ChatbarSendGiftBean.class);
            if (bean != null)
            {
                if (bean.isSuccess())
                {
                    if (bean.user_love > 0)
                    {
                        tvLoveToatal.setText(""+bean.user_love);
                        userLove = bean.user_love;
                    }else
                    {
                        if (bean.user_love == 0)
                        {
                            tvLoveToatal.setText(mContext.getString(R.string.user_wallet_recharge_title));
                        }
                    }
                    userGiftId = bean.user_gift_id;

                    //TODO:判断是私聊页面，发送socket消息
                    if(isFromChatPersonal==1){
                        mCurrentGift = new Gift();
                        int sendNum = bean.send_gift_num;
                        mCurrentGift.setName(bean.gift_name);//礼物名称
                        mCurrentGift.setGiftdesc(bean.gift_desc);//礼物单位
                        mCurrentGift.setCharisma((int) (bean.charm_num));//// 所代表的魅力值
                        mCurrentGift.setCurrencytype(bean.gift_currencytype);
                        mCurrentGift.setPrice(bean.price);
                        mCurrentGift.setExperience(bean.exp);//经验值
                        mCurrentGift.setIconUrl(mSelectGiftBean.getGift_icon());
                        CommonFunction.log(TAG, "handleSendStoreGiftSuc() call sendGiftMessage()");
                        sendGiftMessage(curendUser.getUid(),sendNum);
                        if (bean.gift_currencytype == 2){//钻石礼物，阻断释放
                            EventBus.getDefault().post("refer_chat_block_gifts");
                        }
                    }
                }

                if (mHadTimeListener != null){
                    mHadTimeListener.updateHadTime(bean.user_had_time);
                }

                if (bean.status == -100)
                {
                    if (bean.error == 4000)
                        return;
                    ErrorCode.showError(mContext,result);
                }
            }
        }
    }

    /**
     * 当发送礼物消息成功之后，发送一条私聊消息
     */
    private void sendGiftMessage(long fUid,int giftNum) {
        CommonFunction.log(TAG, "sendGiftMessage() into");
        if (mCurrentGift != null) {
            giftUrl = mCurrentGift.getIconUrl();
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("giftname", mCurrentGift.getName(mContext));
            map.put("charmnum", String.valueOf(mCurrentGift.getCharisma()*giftNum));
            String price = "";
            if (mCurrentGift.getDiscountgoldnum() != null
                    && !mCurrentGift.getDiscountgoldnum().equals("null")) {
                price = mCurrentGift.getDiscountgoldnum();
            } else {
                price = String.valueOf(mCurrentGift.getPrice()*giftNum);
            }
            map.put("price", price);
            map.put("currencytype", String.valueOf(mCurrentGift.getCurrencytype()));
            map.put("giftnum", giftNum);
            map.put("exp", mCurrentGift.expvalue*giftNum);
            map.put("gift_desc",mCurrentGift.getGiftdesc(mContext)+"");
            map.put("isFromChatRoom",2+"");
            String content = JsonUtil.mapToJsonString(map);
            mReqSendMsgFlag = System.currentTimeMillis();
            //发私聊消息
            CommonFunction.log(TAG, "sendGiftMessage call SocketSessionProtocol.sessionPrivateMsg() start");
            long flag = SocketSessionProtocol.sessionPrivateMsg(mContext, mReqSendMsgFlag,
                    fUid, mtype, String.valueOf(ChatRecordViewFactory.GIFE_REMIND),
                    giftUrl, from, content);//  mCurrentGift.getIconUrl()
            CommonFunction.log(TAG, "sendGiftMessage call SocketSessionProtocol.sessionPrivateMsg() end");

            //发世界消息
//            if(!TextUtils.isEmpty(price)){
//                int priceNum = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.GIFT_DIAMOND_MIN_NUM);
//                if(mCurrentGift.getCurrencytype() != 1 &Integer.parseInt(price) > priceNum){
//                    String str = mContext.getResources().getString(R.string.chat_bar_send_gift_world_message)+"@"+giftNum+mCurrentGift.getGiftdesc(mContext)+mCurrentGift.getName(mContext)+mContext.getResources().getString(R.string.chat_bar_send_gift_consume_much);
//                    WorldMessageGiftContent worldMessageGiftContent = new WorldMessageGiftContent();
//                    worldMessageGiftContent.message = str;
//                    if(user1111111111111111111 != null){
//                        worldMessageGiftContent.targetUserName = user1111111111111111111.getNickname();
//                    }
//                    String message = JSON.toJSONString(worldMessageGiftContent);
//                    CommonFunction.log(TAG, "sendGiftMessage call SendWorldMessageProtocol.getSendWorldMessageData() start");
//                    SendWorldMessageProtocol.getInstance().getSendWorldMessageData(mContext,Integer.parseInt(String.valueOf(groupId)),message,32,null);
//                    CommonFunction.log(TAG, "sendGiftMessage call SendWorldMessageProtocol.getSendWorldMessageData() end");
//                }
//            }

            Me me = Common.getInstance().loginUser;
            mChatRecord = new ChatRecord();
            mChatRecord.setId(-1); // 消息id
            mChatRecord.setUid(me.getUid());
            mChatRecord.setNickname(me.getNickname());
            mChatRecord.setIcon(me.getIcon());
            mChatRecord.setVip(me.getViplevel());
            mChatRecord.setLevel(me.getLevel());
            mChatRecord.setDatetime(mReqSendMsgFlag);
            mChatRecord.setType(Integer.toString(ChatRecordViewFactory.GIFE_REMIND));
            mChatRecord.setStatus(ChatRecordStatus.ARRIVED); // TODO：发送中改为送达
            mChatRecord.setAttachment(mCurrentGift.getIconUrl());
            mChatRecord.setContent(content);
            mChatRecord.setUpload(false);
            if(curendUser!=null) {
                mChatRecord.setfLat(curendUser.getLat());
                mChatRecord.setfLng(curendUser.getLng());
                GeoData geo = LocationUtil.getCurrentGeo(mContext);
                mChatRecord.setDistance(LocationUtil.calculateDistance(curendUser.getLng(), curendUser.getLat(), geo.getLng(), geo.getLat()));
            }
            CommonFunction.log(TAG, "sendGiftMessage call ChatPersonalModel.insertOneRecord() start");
            ChatPersonalModel.getInstance().insertOneRecord(mContext, curendUser, mChatRecord, getMtype(), from);//subGroup
            if (mChatRecord != null) {
                mPersonalListenr.update(flag, mChatRecord);
            }
            CommonFunction.log(TAG, "sendGiftMessage call ChatPersonalModel.insertOneRecord() end");
        }
    }
    /**
     * 判断当前用户的钻石和金币是否充足
     */
    private boolean judgeUserDiamonAndGold()
    {
        if (userLove >= 0 && userLove < mSelectGiftBean.getGift_love() * lastStoreGiftArrayValue)
        {
            DialogUtil.showTowButtonDialog(mContext, mContext.getResources().getString(R.string.prompt),
                    mContext.getResources().getString(R.string.video_chat_gift_love_not_enough),
                    mContext.getResources().getString(R.string.edit_cancle),
                    mContext.getResources().getString(R.string.chatbar_gift_get_diamond),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, LovePayActivity.class);
                            if(mContext instanceof VideoChatActivity){
                                VideoChatActivity activity = (VideoChatActivity)mContext;
                                activity.startActivityForResult(intent,VideoChatActivity.LOVE_PAY_REQUEST_CODE);
                            }else{
                                mContext.startActivity(intent);
                            }
                            dismiss();
                        }
                    });
            return true;
        }
        return false;
    }

    /**
     * 计时器
     */
    private void timerTask()
    {
        if (timer == null)
            timer = new Timer();
        synchronized (timer)
        {
            time = timeStatic;
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if(time>0){
                        time--;
                    }

                    Message updateTime = Message.obtain();
                    updateTime.what = CURRENT_UPDATE_TIME;
                    updateTime.obj = time;
                    mHandler.sendMessage(updateTime);

                    if (time == 0){

                        Message message = Message.obtain();
                        message.what = CURRENT_TIME;
                        message.obj = mSelectGiftBean;
                        mHandler.sendMessage(message);
                    }
                }
            };
            timer.schedule(timerTask, 0, 100);
        }
    }
    /**
     * 设置点击连送礼物动画
     */
    private void initRlTimeTaskAnimation()
    {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rlTimeTask, "scaleX", 1f, 0.7f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rlTimeTask, "scaleY", 1f, 0.7f, 1.2f, 1f);
        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(rlTime, "scaleX", 1f, 0.7f,1.2f,1.3f);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(rlTime, "scaleY", 1f, 0.7f,1.2f,1.3f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(rlTime, "alpha", 1f,1f,0.7f,0f);
        animatorSet.setDuration(400);
        animatorSet.play(scaleX).with(scaleY).with(scaleX1).with(scaleY1).with(alpha);
        animatorSet.start();
    }
    /***
     * 初始化礼物数组
     */
    private void initGiftArrayPopWindow(final boolean isBagGift)
    {

        handleGiftArrayData(isBagGift);

        popWindow = new GroupChatBarGiftPopWindow(mContext,listGiftComboBeans.size());
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                ivSendGiftUp.clearAnimation();
                ivSendGiftUp.setImageResource(R.drawable.chatbar_gift_array_icon);
            }
        });
        if (mSelectGiftBean == null || listGiftComboBeans.size() == 0 )
            return;
        ChatBarGiftPopWindowAdapter adapter = new ChatBarGiftPopWindowAdapter(mContext, listGiftComboBeans);
        popWindow.setAdapter(adapter);
        popWindow.showUp(tvChatBarGifgNum,listGiftComboBeans.size(),llSendgiftAmount);
        popWindow.setItemSelectListener(new GroupChatBarGiftPopWindow.MItemSelectListener() {
            @Override
            public void onItemClick(int position) {
                currentPostion=position;
                tvChatBarGifgNum.setText(""+listGiftComboBeans.get(position).getCombo_value());
                lastArrayNum=listGiftComboBeans.get(position).getCombo_value();
                {
                    lastStoreGiftArrayPosition = currentPostion;
                    lastStoreGiftArrayValue = lastArrayNum;
                    SharedPreferenceUtil.getInstance(mContext).putInt(LAST_LOVE_GIFT_ARRAY_POSTION,lastStoreGiftArrayPosition);
                    SharedPreferenceUtil.getInstance(mContext).putLong(LAST_LOVE_GIFT_ARRAY_VALUE,lastStoreGiftArrayValue);
                }
            }
        });
    }
    /***
     * 初始化礼物数组布局和发送按钮布局及其点击事件
     * 如果礼物数组只有1 则不可点击
     * 如果礼物数组很多，可点击
     */
    private void  initGiftArrayAndSendLayout(boolean isBagGift)
    {
        handleGiftArrayData(isBagGift);
        if (listGiftComboBeans == null || listGiftComboBeans.size() == 0 || mSelectGiftBean == null )
            return;
        if (listGiftComboBeans.size() == 1)
        {
            btSend.setEnabled(true);
            llSendgiftAmoundNo.setVisibility(View.VISIBLE);
            llSendgiftAmount.setVisibility(View.GONE);
            llSendLayout.setOnClickListener(CustomGiftDialog.this);
            llSendLayout.setBackgroundResource(R.drawable.chatbar_gift_send_bg);
        }else if (listGiftComboBeans.size() > 1)
        {
            btSend.setEnabled(true);
            llSendgiftAmount.setVisibility(View.VISIBLE);
            llSendgiftAmoundNo.setVisibility(View.GONE);
            llSendgiftAmount.setOnClickListener(CustomGiftDialog.this);
            llSendLayout.setOnClickListener(CustomGiftDialog.this);
            llSendLayout.setBackgroundResource(R.drawable.chatbar_gift_send_bg);
        }else
        {
            btSend.setEnabled(false);
            llSendgiftAmount.setVisibility(View.GONE);
            llSendgiftAmoundNo.setVisibility(View.VISIBLE);
            llSendLayout.setBackgroundResource(R.drawable.chatbar_gift_send_gift_no);
        }
    }
    /**
     * 点击其他礼物的时候取消连击效果
     */
    private void cancleSendAnimation(boolean isBagGift) {
        {

            CommonFunction.log("handleSendGiftData","切换了");
            if (lastSelectStoreGiftBean == null || timer == null || mSelectGiftBean == null
                    || lastSelectStoreGiftBean.getGift_id() == mSelectGiftBean.getGift_id())
                return;

            Message updateTime = Message.obtain();
            updateTime.what = CURRENT_UPDATE_TIME;
            updateTime.obj = 0;
            mHandler.sendMessage(updateTime);

            if (time != 0){

                Message message = Message.obtain();
                message.what = CURRENT_TIME;
                message.obj = mSelectGiftBean;
                mHandler.sendMessage(message);
            }
            rlTimeTask.setVisibility(View.GONE);
            llArrayAndSendLayout.setVisibility(View.VISIBLE);
            //记录上一次点击的礼物
            lastSelectStoreGiftBean = mSelectGiftBean;
        }
    }
    /**
     * 展示背包，商店礼物可选数组初始化
     * 商店礼物：没有礼物数量限制，只要有的数组都可以展示
     * 背包礼物：展示小于库存的数组，如果只有1个，只展示1，且不可点击
     */
    private List<ChatBarBackpackBean.ListBean.GiftComboBean> handleGiftArrayData(boolean isBagGift)
    {
        if (listGiftComboBeans == null)
            listGiftComboBeans = new ArrayList<>();
        /***
         * 添加礼物数组
         */
        listGiftComboBeanValues.clear();
        listGiftComboBeans.clear();
        if (mSelectGiftBean == null)
            return null;
        for (int i = 0; i < mSelectGiftBean.getGift_combo().size(); i++)
        {
            if (isBagGift)
            {
                if (mSelectGiftBean.getGift_num() < mSelectGiftBean.getGift_combo().get(i).getCombo_value())
                    continue;
                listGiftComboBeans.add(mSelectGiftBean.getGift_combo().get(i));
                listGiftComboBeanValues.add(mSelectGiftBean.getGift_combo().get(i).getCombo_value());
            }else
            {
                listGiftComboBeans.add(mSelectGiftBean.getGift_combo().get(i));
                listGiftComboBeanValues.add(mSelectGiftBean.getGift_combo().get(i).getCombo_value());
            }
        }
        return listGiftComboBeans;
    }

    static class HttpCallBackImpl implements HttpCallBack{
        private WeakReference<CustomGiftDialog> mDialog;
        public HttpCallBackImpl(CustomGiftDialog dialog){
            mDialog = new WeakReference<CustomGiftDialog>(dialog);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            CustomGiftDialog dialog = mDialog.get();
            if(dialog == null){
                return;
            }
            if (result == null) {
                return;
            }
            if (flag == dialog.storeGiftDataFlag) {
                ChatBarBackpackBean bean = GsonUtil.getInstance().getServerBean(result,ChatBarBackpackBean.class);
                if (bean != null && bean.isSuccess())
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(LIST_STORE_CACHE,result);
                dialog.handleStoreGiftData(bean,result);
            }else if (flag == dialog.sendStoreGiftFlag) {
                dialog.handleSendStoreGiftSuc(result);
            }else if(flag == dialog.mRequestRelationFlag){
                dialog.handleRequestRelationResult(result);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            CustomGiftDialog dialog = mDialog.get();
            if(dialog == null){
                return;
            }
            if(flag == dialog.mRequestRelationFlag){

            }else {
                ErrorCode.toastError(BaseApplication.appContext, e);
            }
        }
    }

    /**
     * 获取商店礼物列表
     * @param bean
     * @param result
     */
    private void handleStoreGiftData(ChatBarBackpackBean bean,String result)
    {
        if (bean == null)
            return;
        if (bean.isSuccess())
        {
            isBagsGift = false;

            if (bean.getUser_loves() <= 0)
            {
                tvLoveToatal.setText(mContext.getString(R.string.user_wallet_recharge_title));
            }else
            {
                userLove = bean.getUser_loves();
                tvLoveToatal.setText(bean.getUser_loves()+"");
            }

            if (isRefreshGiftLayout)
            {
                mGifgComboBeanList.clear();
                giftStoreBeens.clear();
                userLove = bean.getUser_loves();
                timeStatic = bean.getComboTime()/100;
                if (bean.getList() == null || bean.getList().size() == 0)
                    return;
                giftStoreBeens.addAll(bean.getList());
                initGiftData(giftStoreBeens,false);
            }
            isRefreshGiftLayout = true;
        }
        if (bean.status == -100)
        {
            ErrorCode.showError(mContext,result);
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if(isFromChatPersonal==1){
            mPsrsonalSendGiftDismiss.updateHeightDismiss();
        }else {
            if(luxuryGiftView!=null){
                luxuryGiftView.setHeightPosition(33);
                luxuryGiftView.upDateDefaultMargin(33);
            }
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mPersonalListenr != null){
                mPersonalListenr.showStatus();
            }
            return false;
        } else {
            return false;
        }
    }

    /***
     * RecyclerView的适配器
     */
    public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.MyViewHolder>
    {

        private Context mContext;
        private int index;
        private int selectedPos = 0;
        private int selectdGiftId = 0;
        private List<ChatBarBackpackBean.ListBean> giftBeans = new ArrayList<>();
        private boolean isBagGift;

        public void updateData(List<ChatBarBackpackBean.ListBean> giftBeans)
        {
            this.giftBeans = giftBeans;
            notifyDataSetChanged();
        }
        public GiftAdapter(Context mContext,int index,boolean isBagGift, List<ChatBarBackpackBean.ListBean> giftBeans)
        {
            this.giftBeans = giftBeans;
            this.index = index;
            this.mContext = mContext;
            this.isBagGift = isBagGift;
        }
        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            public ImageView ivGiftIcon;
            public TextView tvGiftName,tvGiftPrice;
            public RelativeLayout rlGiftLayout;
            public View selectGiftBg,selectGiftColor;
            public TextView tvHaveNum;//礼物剩余数量

            public MyViewHolder(View convertView)
            {
                super(convertView);
                ivGiftIcon = (ImageView) convertView.findViewById(R.id.iv_gift_icon);
                tvGiftName = (TextView) convertView.findViewById(R.id.tv_gift_name);
                tvGiftPrice = (TextView) convertView.findViewById(R.id.tv_gift_price);
                rlGiftLayout = (RelativeLayout) convertView.findViewById(R.id.rl_gift_layout);
                selectGiftBg = convertView.findViewById(R.id.chatbar_select_gift_bg);
                selectGiftColor = convertView.findViewById(R.id.chatbar_select_gift_color);

                tvHaveNum = (TextView) convertView.findViewById(R.id.tv_have_num);
            }
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(mContext).inflate(R.layout.gift_detail_layout,null);
            final MyViewHolder viewHolder = new MyViewHolder(itemView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final int pos = position + index * mPagesize;
            if (giftBeans == null || giftBeans.size() == 0)
                return;
            //加载礼物图片
            GlideUtil.loadImageCache(BaseApplication.appContext,giftBeans.get(pos).getGift_icon(),holder.ivGiftIcon);
            //加载礼物名称，并做国际化
            giftBeans.get(pos).setGiftNameArray(giftBeans.get(pos).getGift_name());
            String giftName = giftBeans.get(pos).getGiftNameArray(mContext);
            holder.tvGiftName.setText(giftName);
            //加载礼物单位
            holder.tvGiftPrice.setText(giftBeans.get(pos).getGift_love() +" "+ mContext.getResources().getString(R.string.item_pay_love_order));
            //设置背包礼物库存
            if (isBagGift)
            {
                holder.tvHaveNum.setVisibility(View.VISIBLE);
                if (giftBeans.get(pos).getGift_num() < 10)
                {
                    holder.tvHaveNum.setBackgroundResource(R.drawable.chatbar_gift_have_num_bg);
                } else
                {
                    holder.tvHaveNum.setBackgroundResource(R.drawable.chatbar_gift_hava_num_bg1);
                }
                holder.tvHaveNum.setText(giftBeans.get(pos).getGift_num() + "");
            }
            //初始化礼物数组位置
            Message msg = Message.obtain();
            msg.obj = isBagGift;
            msg.what = GIFT_ARRAY_DATA;
            mHandler.sendMessage(msg);
            //选中当前点击的礼物
            if (giftBeans.get(pos).isSelected)
            {
                holder.rlGiftLayout.setBackgroundResource(R.drawable.chatbar_gift_layout_selelct_bg);
            }else
            {
                holder.rlGiftLayout.setBackgroundResource(R.drawable.chatbar_gift_layout_no_select_bg);
            }
            //通过设置tag设置当前点击位置
            holder.rlGiftLayout.setTag(R.id.chatbar_gift_selelct,pos);

            holder.rlGiftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取当前点击的位置
                    mGifgComboBeanList.clear();
                    int position = (int) v.getTag(R.id.chatbar_gift_selelct);
                    mSelectGiftBean  = giftBeans.get(position);
                    /**
                     * 记录最后点击的礼物的位置
                     */
                    {
                        lastStoreGiftPosition = position;
                        SharedPreferenceUtil.getInstance(mContext).putInt(LAST_LOVE_GIFT_POSTION,lastStoreGiftPosition);
                    }

                    for (ChatBarBackpackBean.ListBean listBean : giftBeans){
                        listBean.isSelected = mSelectGiftBean.getGift_id() == listBean.getGift_id();
                    }
                    //重新绑定数据
                    notifyDataSetChanged();
                    //切换礼物取消动画
                    Message msg = Message.obtain();
                    msg.what = CURRENT_GIFT;
                    msg.obj = isBagGift;
                    mHandler.sendMessage(msg);
                }
            });
        }
        public void setSelectPos(int pos){
            this.selectedPos = pos;
            notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            return  giftBeans.size() > 0 && (giftBeans.size() > mPagesize * (index + 1)) ? mPagesize : giftBeans.size() - mPagesize * index ;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /* 触摸外部弹窗 */
        if (isOutOfBounds(getContext(), event)) {
            if (mPersonalListenr != null){
                mPersonalListenr.showStatus();
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

    public int getMtype() {
        return mtype;
    }

    public void setMtype(int mtype) {
        this.mtype = mtype;
    }

    /**
     * 获取两人的关系
     */
    private void requestBlock(){
        mRequestRelationFlag = UserHttpProtocol.obtainInfo(getContext(), curendUser.getUid(), new HttpCallBackImpl(this));
    }

    private void handleRequestRelationResult(String result){
        ObtainInfoBean bean = GsonUtil.getInstance().getServerBean(result, ObtainInfoBean.class);

        if (null!=bean && bean.isSuccess()) {
            setMtype(bean.setBlockStaus == 1 ? 0 : 1);
        }
    }
}