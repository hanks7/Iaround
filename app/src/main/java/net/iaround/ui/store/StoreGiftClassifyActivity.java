
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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.Me;
import net.iaround.model.im.SocketSuccessResponse;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.model.type.ChatMessageType;
import net.iaround.pay.FragmentPayBuyGlod;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.store.RecommendGiftsBean.recommendGifts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;


/**
 * 商店礼物分类的页面
 *
 * @author zhengst
 *
 */
public class StoreGiftClassifyActivity extends SuperActivity implements
        OnClickListener, OnCheckedChangeListener
{
	// FLAG
	private long mBuyFlag;// 购买礼物的请求
	private long mSendGiftFlag;// 赠送礼物的请求
	private long mReqSendMsgFlag;// 发送私聊的请求
	private int mLauchType = LAUCH_PAY;// 跳转本页面的意图：购买或赠送
	public final static int LAUCH_PAY = 1001;// 跳转来购买礼物
	public final static int LAUCH_SENT = 1002;// 跳转来送礼物
	private static final int SEND_MSG_SUCCESS = 995;// 成功发送私聊
	private static final int SEND_GIFT_SUCCESS = 996;// 成功赠送礼物
	private final static int BUY_GIFT_SUCCESS = 997;// 成功购买礼物
	private static final int GET_GIFTLIST_FAIL = 998;// 获取礼物列表失败
	private static final int GET_GIFTLIST_SUCCESS = 999;// 成功获取礼物列表
	private static final int GET_GIFT_RECOMMEND_SUCCESS = 1000;// 成功获取推荐礼物数据
	public final static int PAYTYPE_GOLD = 1;// 金币支付方式
	public final static int PAYTYPE_DIAMOND = 2;// 钻石支付方式
	public final static int PAYTYPE_EXCHANGE = 3;// 金币礼物钻石直购支付方式
	public static final int REQUESTCODE_MINE = 1100;//跳转到私藏礼物的请求码
	public static final int REQUESTCODE_PAYMAIN = 1101;//跳转到兑换金币页的请求码

	// VIEW
	private Dialog mProgressDialog;
	private MyGridView giftGridView;
//	private TextView tvGold , tvDiamond;
	//标题栏
	private FrameLayout flLeft;
	private TextView mTitleName;
	private TextView mTitleRight;
	private ImageView mTitleBack;

	private GiftGridViewAdapter giftGridAdapter;
	private PullToRefreshScrollView mPullScrollView;
//	private TextView empty_view;//获取礼物列表失败的提示
	//推荐礼物的view
	private View recommendView;
	private CheckBox checkbox;
	private Button nextBtn;
	private Button sendBtn;
	private Button closeBtn;
	private TextView tvPrice;
	private TextView tvGiftPrice;
	private TextView tvGiftsname;
	private TextView tipsOffTitle;
	private NetImageView giftsIcon;
	private RelativeLayout tipsTitleLy;
	private TextView tips_emptyView;//获取推荐礼物失败的提示

	private String title = "";
	private Me me = new Me( );
	private String Uid = "";
	private Gift mCurrentGift = new Gift();//当前禮物
	private int mCurrentPrice = 0;//当前禮物價格
	private Integer ratio;// 钻石直购，兑换比例
	private Integer discount;// 钻石直购， 折扣，如 下发80 = 80%
	private int groupID = 0;//分类ID
	private int goldNum = 0;//金币数量
	private int diamonNum = 0;//钻石数量
	private int mCurPage = 1;
	private int mTotalPage = 1;
	private final int PAGE_SIZE = 24;
	private ChatRecord mChatRecord;
	private User mFriendUser;// 好友的资料
	private static int from = 0;// 从哪里进入聊天
	private static String _goldNum , _diamondNum;
	private ArrayList< Gift > mGifts = new ArrayList< Gift >( );
	private static int mtype = 0;// 聊天类型（0-正常私聊，1-搭讪聊天）
	public static Dialog haveSentDialog = null;// 礼物已送到提示框
	private int exchangeGoldPrice = 0 ;//前往钱包页面兑换的礼物的金币价格

	//推荐礼物
	/**兑换比例  钻石:人民币=1:0.1*/
	private double rmbrate = 0;
	/**兑换比例  钻石:新台币=1:0.1*/
	private double xtbrate = 0;
	private long HTTP_GET_RECOMMEND_DATA;//获取推荐礼物的flag
	private recommendGifts mCurrentRecommendGifts;//当前选择的推荐礼物
	private int currentIndex = 1 ;// 用于标记当前显示的是第几个礼物
	private Boolean ischecktipsoff = false ;//是否开启不再提示顶部黄色温馨提示
	private Boolean isClickCloseBtn = false;//是否点击关闭顶部黄色温馨提示栏的按钮
	private ArrayList<recommendGifts> recommendGiftsList = new ArrayList<recommendGifts>();

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.store_gift_classify_view );

		me = Common.getInstance( ).loginUser;
		Uid = String.valueOf(me.getUid());
		_goldNum = getResString( R.string.gold_balance ) ;
		_diamondNum = getResString( R.string.diamond_balance ) ;
		mLauchType = this.getIntent( ).getIntExtra( "category" , LAUCH_PAY );
		groupID = this.getIntent( ).getIntExtra( "groupId" , -1 );
		title = this.getIntent( ).getStringExtra( "title"  );

		initView( );
		setListener( );
		initData( );
		mPullScrollView.setRefreshing( );
	}

	/**
	 * 初始化布局
	 */
	private void initView( )
	{
		mTitleName = (TextView) findViewById( R.id.tv_title );
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		mTitleRight = (TextView) findViewById( R.id.tv_right );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);

		mTitleBack.setVisibility(View.VISIBLE);
		mTitleRight.setVisibility(View.VISIBLE);

		mTitleBack.setImageResource(R.drawable.title_back);
		mTitleRight.setText(getResources().getString(R.string.private_gift));

//		tvGold = (TextView) findViewById( R.id.tvGold );
//		tvDiamond = (TextView) findViewById( R.id.tvDiamond );
//		empty_view = (TextView) findViewById( R.id.empty_view );
		tips_emptyView = (TextView) findViewById(R.id.tips_empty_view);
//		empty_view.setVisibility( View.GONE );
//		mTitleRight.setText( R.string.private_gift );
//		ImageView ivRight = (ImageView) findViewById(R.id.iv_go);
//		ivRight.setImageResource(R.drawable.user_contact);
		mPullScrollView = (PullToRefreshScrollView) findViewById( R.id.giftContent );
		giftGridView = (MyGridView) findViewById( R.id.store_classify_gift_gridview );

		// 如果是跳转意图为发送添加推荐礼物的view
		if ( mLauchType == LAUCH_SENT ) {
			recommendView = findViewById(R.id.tips_view);
			nextBtn = (Button) recommendView.findViewById(R.id.next_btn);
			sendBtn = (Button) recommendView.findViewById(R.id.send_btn);
			tvPrice = (TextView) recommendView.findViewById(R.id.tvPrice);
			tvGiftPrice = (TextView) recommendView.findViewById(R.id.gifts_price);
			tvGiftsname = (TextView) recommendView.findViewById(R.id.gifts_name);
			giftsIcon = (NetImageView) recommendView.findViewById(R.id.gifts_icon);
			//提示栏
			tipsTitleLy = (RelativeLayout) findViewById(R.id.tips_ly);
			tipsOffTitle = (TextView) findViewById(R.id.tips);
			checkbox = (CheckBox) findViewById(R.id.checkbox);
			closeBtn = (Button) findViewById(R.id.close);
		}

		giftGridAdapter = new GiftGridViewAdapter( );
		giftGridView.setAdapter( giftGridAdapter );
		giftGridView.setOnItemClickListener( giftClicklistener );
		mPullScrollView.setMode( Mode.DISABLED );

		if ( mLauchType == LAUCH_SENT )
		{// 如果是跳转意图为发送获取参数
			Intent intent = getIntent( );
			long uid = intent.getLongExtra( "uid" , -1 );
			int age = intent.getIntExtra( "age" , 18 );
			int gender = intent.getIntExtra( "gender" , 0 );
			String icon = intent.getStringExtra( "icon" );
			String fNickName = intent.getStringExtra( "nickname" );
			int vip = intent.getIntExtra( "viplevel" , 0 );
			from = intent.getIntExtra( "from" , 0 );
			mtype = intent.getIntExtra( "mtype" , 0 );
			int  lat = intent.getIntExtra( "lat", 0 );
			int lng = intent.getIntExtra( "lng", 0 );
			mFriendUser = new User( );
			mFriendUser.setUid( uid );
			mFriendUser.setSex( gender );
			mFriendUser.setAge( age );
			mFriendUser.setIcon( icon );
			mFriendUser.setViplevel( vip );
			mFriendUser.setNickname( fNickName );
			mFriendUser.setLat( lat );
			mFriendUser.setLng( lng );
			mFriendUser.setNickname( fNickName );
			mTitleName.setText( R.string.buy_gift );
		} else {
			if (CommonFunction.isEmptyOrNullStr(title)) {
				title = getResString(R.string.category_all);
			}
			mTitleName.setText(title);
		}
	}

	/**
	 * 设置监听
	 */
	private void setListener( )
	{
		if ( mLauchType == LAUCH_SENT ) {
			checkbox.setOnCheckedChangeListener( this );
			nextBtn.setOnClickListener(this);
			sendBtn.setOnClickListener(this);
			closeBtn.setOnClickListener(this);
		}
		flLeft.setOnClickListener(this);
		mTitleRight.setOnClickListener( this );
		mTitleBack.setOnClickListener( this );
//		findViewById( R.id.diamond_ly ).setOnClickListener( this );
//		findViewById( R.id.gold_ly ).setOnClickListener( this );
		mPullScrollView.setOnRefreshListener(
				new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
					}

					@Override
					public void onPullUpToRefresh(// 上拉刷新
							PullToRefreshBase<ScrollView> refreshView) {
						if (mCurPage < mTotalPage) {
							mCurPage = mCurPage + 1;
							reqGiftList(mCurPage , groupID);
						} else {
							refreshView.postDelayed(new Runnable() {
								public void run() {
									Toast.makeText(getBaseContext(),
									R.string.no_more, Toast.LENGTH_SHORT).show();
									mPullScrollView.onRefreshComplete();
								}
							}, 200);
						}
					}
				});
	}


	/**
	 * 初始化数据
	 */
	private void initData( )
	{
		showProgressDialog(true );
		//gh 不要推荐礼物
//		if (!Common.getInstance().getIsGetRecommendGifts()
//				&&  mLauchType == LAUCH_SENT ) {
//			requestRecommendData();// 请求推荐礼物数据
//		}
		reqGiftList( mCurPage = 1 , groupID );// 请求礼物列表数据
	}

	/**
	 * 加载礼物列表数据
	 *
	 * @param nextPage
	 * @param groupId
	 */
	private void reqGiftList( int nextPage , int groupId  )
	{
		try {
			SpaceModel.getInstance(this).giftListReq(this, groupId, nextPage,PAGE_SIZE, this);
		} catch (Throwable e) {
			if (mGifts != null && mGifts.size() == 0) {
				handleMineGiftNoData(true);
			} else {
				handleMineGiftNoData(false);
			}
		}
	}
	
	//请求推荐礼物
	private void requestRecommendData(){
//		HTTP_GET_RECOMMEND_DATA = BusinessHttpProtocol.getGiftRecommend(mContext, this);
	}


	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );

		SpaceModelReqTypes reqType = SpaceModel.getInstance(mContext).getReqType(flag);
		if (SpaceModelReqTypes.GIFT_LIST.equals(reqType)) {
			if (mGifts != null && mGifts.size() == 0) {
				handleMineGiftNoData(true);
			} else {
				handleMineGiftNoData(false);
			}
		} else {
			hideProgressDialog();
			ErrorCode.toastError(mContext, e);
		}
	}

	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );

		HashMap< String , Object > res = null;
		try {
			res = SpaceModel.getInstance(this).getRes(result, flag);
		} catch (Throwable e) {
			CommonFunction.log(e);
		}

		if (res == null) {
			return;
		}
		SpaceModelReqTypes reqType = ( SpaceModelReqTypes ) res.get( "reqType" );
		if ( SpaceModelReqTypes.GIFT_LIST.equals( reqType ) )
		{// 获取列表数据
			parseGetGiftList( res );
		}
		if ( flag == mBuyFlag )
		{// 购买操作
			Message msg = Message.obtain( );
			msg.what = BUY_GIFT_SUCCESS;
			msg.obj = result;
			mTheMainHandler.sendMessage( msg );
		}
		else if ( flag == mSendGiftFlag )
		{// 送礼操作
			parseSendGift( result );
		}else if ( flag == HTTP_GET_RECOMMEND_DATA )
		{//推荐礼物
			BaseServerBean Basebean = GsonUtil.getInstance().getServerBean(
					result, BaseServerBean.class);
			if (Basebean != null && Basebean.isSuccess()) {
				RecommendGiftsBean data = GsonUtil.getInstance().getServerBean(
						result, RecommendGiftsBean.class);
				if (data != null && data.gifts.size() > 0) {
					rmbrate = data.rmbrate;
					xtbrate = data.xtbrate;
//					Common.getInstance().setIsGetRecommendGifts(true);//gh

					SharedPreferenceCache.getInstance(mContext).putString
					(SharedPreferenceCache.RECOMMEND_GIFTS_DATA + Uid, result);
				}
			}
		}
	}

	/**
	 * 解析礼物列表数据
	 *
	 * @param res
	 */
	private void parseGetGiftList( HashMap< String , Object > res )
	{
		Integer status = (Integer) res.get( "status" );
		if ( status != null )
		{
			Message msg = mTheMainHandler.obtainMessage( );
			if ( status == 200 )
			{
				msg.obj = res;
				ratio = (Integer) res.get( "ratio" );
				discount = (Integer) res.get( "discount" );
				Integer goldnum = (Integer) res.get( "goldnum" );
				Integer diamonnum = (Integer) res.get( "diamondnum" );
				goldNum = goldnum == null ? 0 : goldnum.intValue( );
				diamonNum = diamonnum == null ? 0 : diamonnum.intValue( );
				setGoldNum( );

				Integer pageno = (Integer) res.get( "pageno" );
				msg.arg1 = pageno == null ? 0 : pageno.intValue( );
				Integer amount = (Integer) res.get( "amount" );
				msg.arg2 = amount == null ? 0 : amount.intValue( );
				msg.obj = res.get( "gifts" );
				msg.what = GET_GIFTLIST_SUCCESS;
			}
            else {
				msg.what = GET_GIFTLIST_FAIL;
				msg.obj = res;
			}
			mTheMainHandler.sendMessage( msg );
		}
	}

	/**
	 * 解析送礼操作
	 */
	private void parseSendGift( Object obj )
	{
		try
		{
			Message msg = mTheMainHandler.obtainMessage( );
			JSONObject result = new JSONObject( String.valueOf( obj ) );
			int status = result.optInt( "status" );
			if ( status == 200 )
			{
				msg.what = SEND_GIFT_SUCCESS;
				// 更新金币钻石数量
				goldNum = result.optInt( "goldnum" );
				diamonNum = result.optInt( "diamondnum" );
				updateLoginUserMineGiftsBuf( mCurrentGift );
			}
			else
			{
				hideProgressDialog( );
				int error = result.optInt( "error" );
				if ( status == -400 )
				{
					error = 4000;
				}

				if ( error == 4000 || error == 5930 )
				{ // 充值
					if ( error == 4000 )
					{// 金币不足
						DialogUtil.showGoldDialog( mActivity );
					}
					else if ( error == 5930 )
					{// 钻石不足
						DialogUtil.showDiamondDialog( mActivity );
					}
				}
				else
				{
					try
					{
						ErrorCode.showErrorByMap( mContext, (HashMap< String, Object >) msg.obj );
					}
					catch ( Exception e )
					{
						CommonFunction.log( e );
					}
				}
			}
			mTheMainHandler.sendMessage( msg );
		}
		catch ( JSONException e1 )
		{
			e1.printStackTrace();
		}

	}

	class GiftGridViewAdapter extends BaseAdapter
	{
		@Override
		public int getCount() {
			if (mGifts != null) {
				return mGifts.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position )
		{
			return mGifts.get( position );
		}

		@Override
		public long getItemId( int position )
		{
			return position;
		}

		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			ViewHolder viewHolder = null;
			if ( convertView == null )
			{
				viewHolder = new ViewHolder( );
				convertView = LayoutInflater.from( StoreGiftClassifyActivity.this ).inflate(
						R.layout.store_gift_classify_item , null );
				viewHolder.tvNum = (TextView) convertView.findViewById(R.id.tv_have_num);
				viewHolder.icon = ( NetImageView ) convertView.findViewById( R.id.gift_icon );
				viewHolder.name = (TextView) convertView.findViewById( R.id.gift_name );
				viewHolder.price = (TextView) convertView.findViewById( R.id.gift_price );
				viewHolder.charm = (TextView) convertView.findViewById( R.id.gift_charm );
				viewHolder.experience = (TextView) convertView.findViewById( R.id.gift_experience );
				viewHolder.Flag1 = (TextView) convertView.findViewById( R.id.flag1 );
				viewHolder.Flag2 = (TextView) convertView.findViewById( R.id.flag2 );
				viewHolder.giftFlagly = (RelativeLayout) convertView
						.findViewById( R.id.gift_flag );
				convertView.setTag( viewHolder );
			}
			else
			{
				viewHolder = ( ViewHolder ) convertView.getTag( );
			}
			viewHolder.tvNum.setVisibility(View.GONE);
			
			Gift gift = mGifts.get( position );
			
			if ( gift != null )
			{
				viewHolder.icon.executeFadeInRound( PicIndex.DEFAULT_GIFT , gift.getIconUrl( ) );
				viewHolder.name.setText( gift.getName( mContext ) );
//				String price = "";
				String priceTypeStr = "";
//				if ( gift.goldnum != null && !gift.discountgoldnum.equals( "null" ) )
//				{// 优惠价格
//					price = gift.discountgoldnum;
//				}
//				else
//				{// 非优惠
//					price = String.valueOf( gift.getPrice( ) );
//				}
				int price = gift.getPrice();
				if ( gift.getCurrencytype( ) == 1 )
				{// 1金币2钻石
					priceTypeStr = mContext.getString( R.string.gold_balance );
					viewHolder.price.setTextColor( Color.parseColor( "#999999" ) );
					viewHolder.price.setText( priceTypeStr + price );
				}
				else
				{
					priceTypeStr = mContext.getString( R.string.diamond_balance );
					viewHolder.price.setTextColor( Color.parseColor( "#FF9900" ) );
					viewHolder.price.setText( priceTypeStr + price );
				}
				viewHolder.charm.setText( mContext.getString( R.string.charisma_title_new ) + gift.getCharisma( ) );
				viewHolder.experience.setText( String.format(mContext.getString( R.string.chat_gift_exp ) , gift.getExperience() ));

				// 礼物标签 显示顺序：专属--优惠--新--热
				if (  gift.flag1 == -1 && gift.flag2 == -1  )
				{
					viewHolder.giftFlagly.setVisibility( View.GONE );
				}else
				{
					viewHolder.giftFlagly.setVisibility( View.VISIBLE );
					if ( gift.flag1 != -1 ){
						viewHolder.Flag1.setVisibility( View.VISIBLE );
						viewHolder.Flag1.setBackgroundResource( gift.flag1 );
					}else{
						viewHolder.Flag1.setVisibility( View.GONE );
					}

					if ( gift.flag2 != -1 ){
						viewHolder.Flag2.setVisibility( View.VISIBLE );
						viewHolder.Flag2.setBackgroundResource( gift.flag2 );
					}else{
						viewHolder.Flag2.setVisibility( View.GONE );
					}
				}
			}
			return convertView;
		}
	}
	
	/**
	 * 礼物点击监听
	 */
	private OnItemClickListener giftClicklistener = new OnItemClickListener( )
	{
		@Override
		public void onItemClick(AdapterView< ? > parent , View view , int position , long id )
		{
			Gift gift = ( Gift ) parent.getAdapter( ).getItem( position );
			mCurrentGift = gift;
			if ( !Common.getInstance( ).loginUser.isSVip( ) &&
				!Common.getInstance( ).loginUser.isVip( ) && gift.getVipLevel( ) == 1 )
			{
				DialogUtil.showTobeVipDialog( mContext , R.string.vip_gift ,
						R.string.tost_gift_vip_privilege );
				return;
			}
			
			// 赠送礼物或购买礼物
			BuyOrSendGift( );
			
		}
	};
	
	/**
	 * 购买或赠送礼物
	 */
	private void BuyOrSendGift()
	{
		//先判断是否有折扣价，若有使用折扣价
		if ( mCurrentGift.discountgoldnum != null
				&& !mCurrentGift.discountgoldnum.equals( "null" ) ){
			mCurrentPrice =  Integer.parseInt( mCurrentGift.discountgoldnum ) ;
		}else{
			mCurrentPrice =  mCurrentGift.getPrice( ) ;
		}
		
		if ( mCurrentGift.getCurrencytype( ) == 1 ){// 金币礼物
			BuyOrSendGoldGiftMode(mCurrentPrice);
		}
		else{// 钻石礼物
		    BuyOrSendDiamonGiftMode( mCurrentPrice );
		}
	}
	
	/**
	 * 钻石礼物购买方式
	 * 
	 * @param price
	 */
	private void BuyOrSendDiamonGiftMode( int price )
	{
		if ( diamonNum >= price )
		{// 钻石充足，直接购买
			if ( mLauchType == LAUCH_PAY ){
				reqBuyGift( );
			}else if ( mLauchType == LAUCH_SENT ){
				reqSendGift(PAYTYPE_DIAMOND );
			}
		}else
		{// 充值
			DialogUtil.showDiamondDialog( mActivity );
		}
	}
	
	
	/**
	 * 金币礼物的购买或赠送
	 * 
	 * @param price
	 */
	private void BuyOrSendGoldGiftMode( int price )
	{
		if ( goldNum >= price )
		{// 金币充足，直接购买
			if ( mLauchType == LAUCH_PAY ){
				reqBuyGift( );
			}else if ( mLauchType == LAUCH_SENT ){
				reqSendGift(PAYTYPE_GOLD );
			}
		}
		else if ( discount != 0 && ratio != 0  )
		{// 金币不足，钻石直购
//			if ( diamonNum >= price * ((double)discount/100) / ratio )
//			{
//				showDiamonConvertDialog( price );
//			}else {// 充值提醒
//				DialogUtil.showDiamondDialog( mActivity );
//			}
			Toast.makeText(StoreGiftClassifyActivity.this, "金币不足", Toast.LENGTH_SHORT).show();
		}
//		else
//		{// 充值提醒
//			DialogUtil.showDiamondDialog( mActivity );
//		}//没有金币充值功能
	}
	
	/**
	 * 钻石直购对话框
	 */
	private void showDiamonConvertDialog( final int price )
	{
		DialogUtil.showDiamonConvertBuyDialog( mContext , mCurrentGift.getCurrencytype( ) ,
		price ,discount , ratio , new OnClickListener( )
			{
				@Override
				public void onClick( View v ){// 确定钻石直购
					showProgressDialog(false );
					if ( mLauchType == LAUCH_PAY ){//钻石直购方式  购买礼物的请求
						mBuyFlag = BusinessHttpProtocol.BuyGift( mContext ,
						mCurrentGift.getId( ),PAYTYPE_EXCHANGE , StoreGiftClassifyActivity.this );
					}else if ( mLauchType == LAUCH_SENT ){//钻石直购方式  赠送礼物的请求
						try{
							mSendGiftFlag = SpaceModel.getInstance( mContext ).sendGiftReq(
							mActivity , mFriendUser.getUid( ) , mCurrentGift.getId( ) ,
							PAYTYPE_EXCHANGE , StoreGiftClassifyActivity.this );
						}
						catch ( Throwable e ){
							e.printStackTrace();
						}
					}
				}
			}, new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{//跳转到兑换金币页面兑换金币后自动购买
						exchangeGoldPrice = price ;
						FragmentPayBuyGlod.jumpPayBuyGlodActivityFromStore(mContext, REQUESTCODE_PAYMAIN);
//						PayMainActivity.lancuPayMainActivity(mContext,REQUESTCODE_PAYMAIN);
					}
				});
	}
	

	/**
	 * 赠送礼物的请求
	 */
	private void reqSendGift(final int paytype)
	{
		DialogUtil.showOKCancelDialog( this , R.string.chat_personal_send_gift ,
		R.string.send_gift_conf , new View.OnClickListener( )
			{
				@Override
				public void onClick( View v )
				{
					hideProgressDialog( );
					mProgressDialog = DialogUtil.showProgressDialog( mContext ,
					R.string.chat_personal_send_gift , R.string.sending_gift , null );
					try
					{
						if ( paytype ==  PAYTYPE_GOLD){
							mSendGiftFlag = SpaceModel.getInstance( mContext ).sendGiftReq(
							mActivity , mFriendUser.getUid( ) , mCurrentGift.getId( ) ,
							PAYTYPE_GOLD , StoreGiftClassifyActivity.this );
						}else if (  paytype ==  PAYTYPE_DIAMOND ){
							mSendGiftFlag = SpaceModel.getInstance( mContext ).sendGiftReq(
							mActivity , mFriendUser.getUid( ) , mCurrentGift.getId( ) ,
							PAYTYPE_DIAMOND , StoreGiftClassifyActivity.this );
						}
					}
					catch ( Throwable e ){
						CommonFunction.log( e );
						hideProgressDialog( );
						Toast.makeText( mContext , R.string.network_req_failed ,
						Toast.LENGTH_SHORT ).show( );
					}
				}
			} );
	}

	/**
	 * 购买礼物的请求
	 */
	private void reqBuyGift( )
	{
		DialogUtil.showPayGiftDialog( mContext , new View.OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{ // 购买礼物
				int paytype = 0;
				if (mCurrentGift.getCurrencytype() == 1) {
					paytype = 1;
				} else if (mCurrentGift.getCurrencytype() == 2) {
					paytype = 2;
				}
				showProgressDialog( false );
				mBuyFlag = BusinessHttpProtocol.BuyGift( mContext , mCurrentGift.getId( ) ,
						paytype , StoreGiftClassifyActivity.this );

			}
		} );
	}

	private Handler mTheMainHandler = new Handler( Looper.getMainLooper( ) )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );

			switch ( msg.what )
			{
				case GET_GIFTLIST_SUCCESS :// 成功获取礼物列表
					handleGiftListSuccess( msg );
					break;

				case GET_GIFTLIST_FAIL :// 获取礼物列表失败
					if ( mGifts!=null && mGifts.size( ) == 0 ){
						handleMineGiftNoData(true);
					}else {
						handleMineGiftNoData(false);
					}
					break;

				case BUY_GIFT_SUCCESS :// 购买礼物成功
					parsePayGift( msg.obj );
					break;

				case SEND_GIFT_SUCCESS :// 成功发送礼物之后，将发送一条私聊消息
					sendGiftMessage( );
					break;

				case SEND_MSG_SUCCESS : // 发送私聊消息成功
					handleSendSessionSuccess( );
					break;

				case GET_GIFT_RECOMMEND_SUCCESS://获取推荐礼物
					handleParseRecommendGifts();
					break;
			}
		}
	};


	@Override
	protected void onResume( )
	{
		super.onResume( );
		getConnectorManage( ).setCallBackAction( this );

		if (PayModel.getInstance( ).getGoldNum( ) != 0) {
			goldNum = Integer.parseInt( String.valueOf( PayModel.getInstance( )
					.getGoldNum( ) ) );
		}if (PayModel.getInstance( ).getDiamondNum( ) != 0) {
			diamonNum = Integer.parseInt( String.valueOf( PayModel.getInstance( )
					.getDiamondNum( ) ) );
		}
		setGoldNum( );
	}

	//处理推荐礼物的返回
	protected void handleParseRecommendGifts() {
//		Common.getInstance().setIsGetRecommendGifts(true);//gh
	}


	/**
	 * 刷新推荐礼物的页面
	 */
	private void refreshRecommendView() {

//		recommendView.setVisibility(View.VISIBLE);

		ischecktipsoff = SharedPreferenceCache.getInstance(mContext).getBoolean
		(SharedPreferenceCache.RECOMMEND_GIFTS_ISCHECKTIPSOFF + Uid);
		if ( !ischecktipsoff && !isClickCloseBtn) {
			tipsTitleLy.setVisibility(View.VISIBLE);
			mTheMainHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					tipsTitleLy.setVisibility(View.GONE);
				}
			}, 1000*3);
		}

		int min = 0;
		int max = recommendGiftsList.size() - 1;
		currentIndex = new Random().nextInt(max - min + 1) + min;
		mCurrentRecommendGifts = recommendGiftsList.get(currentIndex);
		refreshRecommendGiftsData();
	}

	/**
	 * 显示下一个礼物
	 */
	private void showNextGift(){
		currentIndex++ ;
		if (currentIndex >= recommendGiftsList.size() ) {
			currentIndex = 0;
		}
		mCurrentRecommendGifts = recommendGiftsList.get(currentIndex);
		refreshRecommendGiftsData();
	}

	/**
	 * 刷新展示推荐礼物
	 */
	private void refreshRecommendGiftsData(){
		if (mCurrentRecommendGifts != null ) {

			//先判断是否有折扣价，若有使用折扣价
			int giftPrice = 0;
			if ( mCurrentRecommendGifts.discountgoldnum != null
					&& !mCurrentRecommendGifts.discountgoldnum.equals( "null" ) ){
				giftPrice =  Integer.parseInt( mCurrentRecommendGifts.discountgoldnum ) ;
			}else{
				giftPrice =  mCurrentRecommendGifts.goldnum;
			}
			//礼物价格
			float tempPrice = 0;
			String priceStr = "";
			String format = getString( R.string.recommend_gifts_title_price );
			int Language = CommonFunction.getLanguageIndex(mContext);
			//0:英文 ，1：中文简体，2：中文繁体（其他语言默认为0：英文）
			if ( Language == 1 ) {
				tempPrice = (float) (giftPrice * rmbrate);
				priceStr = String.format( format , tempPrice);
			}else if ( Language == 2 ) {
				tempPrice = (float) (giftPrice * xtbrate);
				priceStr = String.format( format , tempPrice);
			}else {
				priceStr = String.format( format , giftPrice);
			}
			tvPrice.setText(Html.fromHtml(priceStr));
			//礼物名称
			String nameStr = CommonFunction.getLangText(mContext,
					         mCurrentRecommendGifts.name);
			tvGiftsname.setText(nameStr);
			//礼物图标
			int defaultIcon = R.drawable.z_find_storemain_default_icon;
//			ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
//					mCurrentRecommendGifts.icon , giftsIcon.getImageView( ) ,
//					defaultIcon , defaultIcon ,null , 0 , "#00000000" );
			GlideUtil.loadRoundImage(BaseApplication.appContext,mCurrentRecommendGifts.icon,5,giftsIcon.getImageView( ));
			//礼物魅力值与钻石数
			String giftPriceStr = mContext.getString( (R.string.recommend_gifts_price ) ,
					giftPrice , mCurrentRecommendGifts.charmnum );
			tvGiftPrice.setText(giftPriceStr);
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isCheck) {
		if (isCheck) {
			hideTipsTitleLy(300);
			SharedPreferenceCache.getInstance(mContext).putBoolean
			(SharedPreferenceCache.RECOMMEND_GIFTS_ISCHECKTIPSOFF + Uid , true);
		}else {
			SharedPreferenceCache.getInstance(mContext).putBoolean
			(SharedPreferenceCache.RECOMMEND_GIFTS_ISCHECKTIPSOFF + Uid, false);
		}
	}


	/** 解析购买礼物 */
	private void parsePayGift( Object obj )
	{
		hideProgressDialog( );
		try
		{
			JSONObject result = new JSONObject( String.valueOf( obj ) );
			if ( result.optInt( "status" ) == 200 )
			{
				Toast.makeText( mContext , R.string.buy_gift_success , Toast.LENGTH_SHORT ).show( );
				// 更新金币钻石数量
				goldNum = Integer.parseInt( result.optString( "goldnum" ) );
				diamonNum = Integer.parseInt( result.optString( "diamondnum" ) );
				setGoldNum( );
				updateLoginUserMineGiftsBuf( mCurrentGift );
			}
			else
			{
				if (result.optInt("error") == 4000) {// 金币不足
					DialogUtil.showGoldDialog(mActivity);
				} else if (result.optInt("error") == 5930) {// 钻石不足
					DialogUtil.showDiamondDialog(mActivity);
				} else {
					ErrorCode.showError(mActivity, String.valueOf(obj));
				}
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * 解析礼物列表数据
	 *
	 * @param msg
	 */
	@SuppressWarnings( "unchecked" )
	private void handleGiftListSuccess( Message msg )
 {
		hideProgressDialog();
		mCurPage = msg.arg1;
		int total = msg.arg2;
		mTotalPage = total / PAGE_SIZE;
		if (total % PAGE_SIZE > 0) {
			mTotalPage++;
		}
		ArrayList<Gift> array = (ArrayList<Gift>) msg.obj;
		if (mCurPage <= 1) {
			if (mGifts == null) {
				mGifts = new ArrayList<Gift>();
			} else {
				mGifts.clear();
			}
		}
		if (array != null) {
			mGifts.addAll(setGiftFlagResourceId(array));
		}

		if (mTotalPage == mCurPage) {
			mPullScrollView.setMode(Mode.DISABLED);
		} else {
			mPullScrollView.setMode(Mode.PULL_FROM_END);
		}

		if (giftGridAdapter != null) {
			hideEmptyView();
			giftGridAdapter.notifyDataSetChanged();
			showRecommendView(false);
		}
		mPullScrollView.onRefreshComplete();
	}

	/**
	 * 展示推荐礼物的view
	 */
	private void showRecommendView(Boolean isShowEmptyView) {
		if (mLauchType == LAUCH_SENT) {
			showRecommendEmptyView(isShowEmptyView);

			if (recommendView != null &&
					recommendView.getVisibility() != View.VISIBLE) {
				String recommendCacheData = SharedPreferenceCache.getInstance(
						mContext).getString(SharedPreferenceCache.RECOMMEND_GIFTS_DATA + Uid);
				if (!CommonFunction.isEmptyOrNullStr(recommendCacheData)) {
					RecommendGiftsBean data = GsonUtil.getInstance().getServerBean(
							recommendCacheData, RecommendGiftsBean.class);
					recommendGiftsList = data.gifts;
					rmbrate = data.rmbrate;
					xtbrate = data.xtbrate;
					refreshRecommendView();
//					empty_view.setVisibility(View.GONE);
					scrollToTop();
				}
			}else {
				if (mCurPage <= 1 ) {
					scrollToTop();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_left:
			case R.id.fl_left:
				finish();
				break;
			case R.id.tv_right:// 右上角按钮，私藏礼物
				if (mLauchType == LAUCH_PAY) {
					Intent intent = new Intent(StoreGiftClassifyActivity.this,StoreMineGiftActivityNew.class);
					startActivity(intent);
//					StoreMineGiftActivity.launchMineGiftToFollow(mContext, me);
				} else {
					Intent intent = new Intent(StoreGiftClassifyActivity.this,StoreMineGiftActivityNew.class);
					startActivity(intent);
//					StoreMineGiftActivity.launchMineGiftToSent(mContext,
//							mFriendUser, REQUESTCODE_MINE, from, mtype);
				}
				break;

			case R.id.close:// 关闭提示的按钮
				hideTipsTitleLy(0);
				break;

			case R.id.next_btn:// 下一个礼物
				showNextGift();
				break;

			case R.id.send_btn:// 赠送礼物
				sendRecommendGifts();
				break;
//		case R.id.gold_ly:// 金币按钮
//			FragmentPayBuyGlod.jumpPayBuyGlodActivity(mContext);
//			break;
//
//		case R.id.diamond_ly:// 钻石按钮
//			FragmentPayDiamond.jumpPayDiamondActivity(mContext);
//			break;
		}
	}

	private void hideTipsTitleLy(int delayMillis){
		tipsOffTitle.setVisibility(View.GONE);
		mTheMainHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				tipsTitleLy.setVisibility(View.GONE);
			}
		}, delayMillis);
	}

	/**
	 * 赠送推荐礼物
	 */
	private void sendRecommendGifts(){
		if (!Common.getInstance().loginUser.isSVip()
		 && !Common.getInstance().loginUser.isVip()
		 && mCurrentRecommendGifts.viptype == 1) {
			DialogUtil.showTobeVipDialog(mContext,
			R.string.vip_gift, R.string.tost_gift_vip_privilege);
			return;
		}

		//判断是否有折扣价，若有使用折扣价
		if ( mCurrentRecommendGifts.discountgoldnum != null
				&& !mCurrentRecommendGifts.discountgoldnum.equals( "null" ) ){
			mCurrentPrice =  Integer.parseInt( mCurrentRecommendGifts.discountgoldnum ) ;
		}else{
			mCurrentPrice =  mCurrentRecommendGifts.goldnum;
		}

		mCurrentGift = new Gift();
		mCurrentGift.setId(mCurrentRecommendGifts.giftid);
		mCurrentGift.setName(mCurrentRecommendGifts.name);
		mCurrentGift.setIconUrl(mCurrentRecommendGifts.icon);
		mCurrentGift.setPrice(mCurrentPrice);
		mCurrentGift.setCurrencytype(mCurrentRecommendGifts.currencytype);
		mCurrentGift.setCharisma(mCurrentRecommendGifts.charmnum);
		mCurrentGift.setExperience(mCurrentRecommendGifts.expvalue);
		mCurrentGift.setVipLevel(mCurrentRecommendGifts.viptype);

		int paytype = 1;
		if (mCurrentRecommendGifts.currencytype == 1) {
			paytype = PAYTYPE_GOLD;
		}else {
			paytype = PAYTYPE_DIAMOND;
		}
		reqSendGift(paytype);
	}



	/**
	 * 当发送礼物消息成功之后，发送一条私聊消息
	 */
	private void sendGiftMessage( )
	{
		if ( mCurrentGift != null )
		{
			LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
			map.put( "giftname" , mCurrentGift.getName( this ) );
			map.put( "charmnum" , String.valueOf( mCurrentGift.getCharisma( ) ) );
			map.put( "price" , mCurrentPrice );
			map.put( "currencytype" , String.valueOf( mCurrentGift.getCurrencytype( ) ) );
			map.put( "giftnum" , 1 );
			map.put( "exp" , mCurrentGift.getExperience() );
			String content = JsonUtil.mapToJsonString( map );
			mReqSendMsgFlag = System.currentTimeMillis( );

			long flag = SocketSessionProtocol.sessionPrivateMsg( this , mReqSendMsgFlag ,
					mFriendUser.getUid( ) , mtype , String.valueOf( ChatMessageType.GIFE_REMIND ) ,
					mCurrentGift.getIconUrl( ) , from , content );

			if ( flag == -1 )
			{
				mTheMainHandler.sendEmptyMessage( SEND_MSG_SUCCESS );
				return;
			}

			mChatRecord = new ChatRecord( );
			mChatRecord.setId( -1 ); // 消息id
			mChatRecord.setUid( me.getUid( ) );
			mChatRecord.setNickname( me.getNickname( ) );
			mChatRecord.setNoteName( me.getNoteName( false ) );
			mChatRecord.setIcon( me.getIcon( ) );
			mChatRecord.setVip( me.getViplevel( ) );
			mChatRecord.setDatetime( mReqSendMsgFlag );
			mChatRecord.setType( Integer.toString( 6 ) );
			mChatRecord.setStatus( ChatRecordStatus.SENDING ); // 发送中
			mChatRecord.setAttachment( mCurrentGift.getIconUrl( ) );
			mChatRecord.setContent( content );
			mChatRecord.setUpload( false );
			mChatRecord.setfLat( mFriendUser.getLat( ) );
			mChatRecord.setfLng( mFriendUser.getLng() );
		}
	}


	@Override
	public void onReceiveMessage( TransportMessage message )
	{
		if ( message.getMethodId( ) == MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC
				|| message.getMethodId( ) == MessageID.SESSION_SEND_PRIVATE_CHAT_FAIL )
		{
			String json = message.getContentBody( );
			SocketSuccessResponse response = GsonUtil.getInstance( ).getServerBean( json ,
					SocketSuccessResponse.class );

			if ( response.flag == mReqSendMsgFlag )
			{
				if ( message.getMethodId( ) == MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC )
				{ // 发送成功
					mChatRecord.setId( response.msgid );
					mChatRecord.setStatus( ChatRecordStatus.ARRIVED ); // 已送达

					int subGroup;
					if ( mtype == 0 ){
						subGroup = SubGroupType.NormalChat;
					}else{
						subGroup = SubGroupType.SendAccost;
					}
					ChatPersonalModel.getInstance( ).insertOneRecord( mActivity ,
							mFriendUser ,mChatRecord , subGroup , from );
				}
			}
			mTheMainHandler.sendEmptyMessage( SEND_MSG_SUCCESS );

		}
		super.onReceiveMessage( message );
	}

	/**
	 * 发送私聊成功
	 */
	private void handleSendSessionSuccess( )
	{
		hideProgressDialog( );
		setGoldNum( );

		DialogUtil.showOKDialog(mContext, R.string.chat_personal_send_gift,
				R.string.gift_sent, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent data = new Intent();
						data.putExtra("type", mCurrentGift.getCurrencytype());
						setResult(Activity.RESULT_OK, data);
						finish();
					}
				});
	}
	
	/** 送礼成功就关闭这个界面 */
	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult( requestCode , resultCode , data );
		if ( resultCode == RESULT_OK )
		{
			switch ( requestCode )
			{
				case REQUESTCODE_MINE :
					setResult( resultCode , data );
					finish( );
					break;
				case REQUESTCODE_PAYMAIN:
					showProgressDialog( false );
					goldNum = Integer.parseInt( String.valueOf(
							  PayModel.getInstance( ).getGoldNum( ) ) );
					diamonNum = Integer.parseInt( String.valueOf(
							  PayModel.getInstance( ).getDiamondNum( ) ) );
//					tvGold.setText( _goldNum + goldNum );
//					tvDiamond.setText( _diamondNum + diamonNum );
					
					mTheMainHandler.postDelayed( new Runnable( )
					{
						@Override
						public void run( )
						{
							hideProgressDialog( );
							BuyOrSendGoldGiftMode( exchangeGoldPrice );
						}
					} , 300 );
					break;
			}
		}
	}
	
	
	/**
	 * 设置金币 、钻石数量
	 */
	private void setGoldNum( )
	{
//		tvGold.setText( _goldNum + goldNum );
//		tvDiamond.setText( _diamondNum + diamonNum );
		PayModel.getInstance( ).setGoldNum( goldNum );
		PayModel.getInstance( ).setDiamondNum( diamonNum );
	}
	
	/**
	 * 判断礼物标签属性，按顺序只显示两个图标
	 * 
	 * @return 显示顺序：专属--优惠--新--热
	 */
	protected ArrayList< Gift > setGiftFlagResourceId(ArrayList< Gift > mGifts )
	{
		for ( Gift gift : mGifts )
		{
			int firstFlagFromIndex = -1;
			firstFlagFromIndex = setFirstFlagIcon( gift , firstFlagFromIndex );
			setSecondFlagIcon( gift , firstFlagFromIndex );
		}
		return mGifts;
	}
	
	private int setFirstFlagIcon( Gift gift , int firstFlagFromIndex )
	{
		if ( gift.getVipLevel( ) == 1 )
		{// 是否VIP专属
			gift.flag1 = R.drawable.z_store_gift_vipflag;
			firstFlagFromIndex = 0;
		}
		else if(gift.discountgoldnum != null && !gift.discountgoldnum.equals( "null" )){
			gift.flag1 = R.drawable.z_store_gift_saleflag;
			firstFlagFromIndex = 1;
		}
		else if ( gift.getIsNew( ) == 1 )
		{// 是否最新 0：不是，1：是
			gift.flag1 = R.drawable.z_store_gift_newflag;
			firstFlagFromIndex = 2;
		}
		else if ( gift.getIsHot( ) == 1 )
		{// 是否热门 0：不是，1：是
			gift.flag1 = R.drawable.z_store_gift_hotflag;
			firstFlagFromIndex = 3;
		}
		else
		{
			gift.flag1 = -1;
		}
		return firstFlagFromIndex;
	}
	
	/**
	 * 显示礼物的第二个图标
	 * 
	 * @param gift
	 * @param firstFlagFromIndex
	 */
	private void setSecondFlagIcon( Gift gift , int firstFlagFromIndex )
	{
		gift.flag2 = -1 ; 
		switch ( firstFlagFromIndex )
		{
			case 0 :// 第一个为vip专属
				if ( gift.discountgoldnum != null && !gift.discountgoldnum.equals( "null" ) ){
					gift.flag2 = R.drawable.z_store_gift_saleflag;
		        }else if ( gift.getIsNew( ) == 1 ){
					gift.flag2 = R.drawable.z_store_gift_newflag;
				}else if ( gift.getIsHot( ) == 1 ){
					gift.flag2 = R.drawable.z_store_gift_hotflag;
		        }else{
					gift.flag2 = -1;
		        }
				break;
			
			case 1 :// 第一个为优惠
				if ( gift.getIsNew( ) == 1 ){
					gift.flag2 = R.drawable.z_store_gift_newflag;
		        }else if ( gift.getIsHot( ) == 1 ){
					gift.flag2 = R.drawable.z_store_gift_hotflag;
		        }else{
					gift.flag2 = -1;
		        }
				break;
			
			case 2 :// 第一个为最新
				if ( gift.getIsHot( ) == 1 ){
					gift.flag2 = R.drawable.z_store_gift_hotflag;
				}else{
					gift.flag2 = -1;
				}
				break;
			
			case 3 :
				gift.flag2 = -1;
				break;
				
			default : gift.flag2 = -1;
   			    break;
		}
	}
	
	/**
	 * 更新当前登陆用户的本地缓存的私藏礼物信息
	 */
	private void updateLoginUserMineGiftsBuf( Gift gift )
	{
		if ( me != null )
		{
			me.setMineGiftnum( me.getMineGiftnum( ) + 1 );
			me.getMineGifts( ).add( gift );
		}
		UserBufferHelper.getInstance( ).save( me );
	}
	
	/**
	 * 礼物列表为空
	 */
	protected void handleMineGiftNoData(Boolean isShowEmptyView )
	{
		hideProgressDialog( );
		if ( isShowEmptyView ){
			showEmptyView( );
		}else {
			Toast.makeText( this , R.string.network_req_failed , Toast.LENGTH_SHORT ).show( );
		}
		mPullScrollView.onRefreshComplete( );
	}
	
	private void scrollToTop(){
		mTheMainHandler.post(new Runnable() {
			@Override
			public void run() {
				mPullScrollView.getRefreshableView().smoothScrollTo(0, 0);
			}
		});
	}
	/**
	 * 展示空数据的提示语
	 */
	private void showEmptyView() {
		if (mLauchType == LAUCH_PAY) {
			showListEmptyView();
			tips_emptyView.setVisibility(View.GONE);
		}else {
			showRecommendView(true);
		}
	}
	
	//显示列表为空时的提示语
	private void showListEmptyView(){
//		empty_view.setVisibility(View.VISIBLE);
//		empty_view.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				initData();
//			}
//		});
	}
	
	//显示推荐礼物为空时的提示语
	private void showRecommendEmptyView(Boolean isShowEmptyView){
		if (mGifts.size() > 0) {
			tips_emptyView.setVisibility(View.GONE);
		}else {
			if (isShowEmptyView) {
				tips_emptyView.setVisibility(View.VISIBLE);
				tips_emptyView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						initData();
						isClickCloseBtn = true;
					}
				});
			}else {
				showListEmptyView();
				tips_emptyView.setVisibility(View.GONE);
			}
		}
	}
	
	/**
	 * 关闭空数据的提示语
	 */
	private void hideEmptyView( )
	{
//		empty_view.setVisibility( View.GONE );
	}
	
	// 显示加载框
	private void showProgressDialog(Boolean isCancelable)
	{
		if ( mProgressDialog == null ){
			mProgressDialog = DialogUtil.showProgressDialog( mContext , "" ,
			getString( R.string.please_wait ) , null );
			mProgressDialog.setCancelable( isCancelable );
		}
			
		 mProgressDialog.show( );
	}
	
	
	/**
	 * 关闭dialog
	 */
	private void hideProgressDialog( )
	{
		if ( mProgressDialog != null && mProgressDialog.isShowing( ) )
		{
			mProgressDialog.cancel( );
		}
	}
	
	
	/**
	 * 跳转到表情分类页面进行购买礼物
	 * 
	 * @param context
	 * @param groupId
	 */
	public static void launcherGiftClassify(Context context , int groupId )
	{
		Intent intent = new Intent( context , StoreGiftClassifyActivity.class );
		intent.setClass( context , StoreGiftClassifyActivity.class );
		intent.putExtra( "category" , LAUCH_PAY );
		intent.putExtra( "groupId" , groupId );
		context.startActivity( intent );
	}
	/**
	 * 从商店首页跳转到表情分类页面进行购买礼物
	 * 
	 * @param context
	 * @param groupId
	 */
	public static void launcherGiftClassifyFromStore(Context context , int groupId , String title )
	{
		Intent intent = new Intent( context , StoreGiftClassifyActivity.class );
		intent.setClass( context , StoreGiftClassifyActivity.class );
		intent.putExtra( "category" , LAUCH_PAY );
		intent.putExtra( "groupId" , groupId );
		intent.putExtra( "title" , title );
		context.startActivity( intent );
	}
	
	/**
	 * 跳转到本页面进行送礼(资料下底栏 + 聊天)
	 * 
	 * @param context
	 * @param user
	 * @param requestCode
	 * @param from
	 *            从哪里来
	 * @param mType
	 *            聊天的类型 (0为正常私聊，1为搭讪 )
	 */
	public static void launchGiftClassifyToSent(Context context , User user , int requestCode ,
												int from , int mType , int groupId )
	{
		Intent intent = new Intent( context , StoreGiftClassifyActivity.class );
		intent.putExtra( "groupId" , groupId );
		intent.putExtra( "uid" , user.getUid( ) );
		intent.putExtra( "gender" , user.getSexIndex( ) );
		intent.putExtra( "viplevel" , user.getViplevel( ) );
		intent.putExtra( "nickname" , user.getNickname( ) );
		intent.putExtra( "icon" , user.getIcon( ) );
		intent.putExtra( "category" , LAUCH_SENT );
		intent.putExtra( "from" , from );
		intent.putExtra( "mtype" , mType );
		intent.putExtra( "lat", user.getLat( ) );
		intent.putExtra( "lng", user.getLng( ) );
		if (requestCode > 0) {
			((Activity) context).startActivityForResult(intent, requestCode);
		} else {
			context.startActivity(intent);
		}
	}
	
	static class ViewHolder
	{
		NetImageView icon;
		TextView name;
		TextView price;
		TextView experience;
		TextView charm;
		TextView Flag1;
		TextView Flag2;
		MyGridView giftGridView;
		RelativeLayout giftFlagly;
		TextView tvNum;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideProgressDialog();
	}
	
}
