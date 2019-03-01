package net.iaround.ui.chat.view;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.entity.type.ShareSubType;
import net.iaround.tools.InnerJump;
import net.iaround.tools.PathUtil;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.comon.NetImageView;
import net.iaround.model.entity.RecordSecretaryPushBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.game.GameWebViewActivity;


public class SecretaryPushRecordView extends ChatRecordView
{
	Context mContext;
	
	// 控件
	TextView tvTitle; // 标题
	NetImageView imgSmall; // 小图片
	NetImageView imgBig; // 大图片
	TextView lab; // 描述
	LinearLayout push_layout; // 整个图文布局
	FrameLayout img_layout;// 大图布局
	View line; // 底部按钮上面的横线
	RelativeLayout button_layout; // 底部按钮布局
	TextView tvButton; // 底部按钮文字
	
	RecordSecretaryPushBean bean;
	ChatRecord record;
	
	private int screenWidth;
	
	private int smallPicWidth;
	private int smallPicHeight;
	
	private int bigPicWidth;
	private int bigPicHeight;
	
	public SecretaryPushRecordView( Context context )
	{
		super( context );
		mContext = context;
		LayoutInflater.from( context ).inflate( R.layout.chat_record_secretary_push, this );
		
		tvTitle = (TextView) findViewById( R.id.secretary_push_title );
		imgSmall = ( NetImageView ) findViewById( R.id.secretary_push_img_small );
		imgSmall.setScaleType( ScaleType.FIT_CENTER );
		
		imgBig = ( NetImageView ) findViewById( R.id.secretary_push_img_big );
		imgBig.setScaleType( ScaleType.FIT_CENTER );
		
		lab = (TextView) findViewById( R.id.secretary_push_label );
		push_layout = (LinearLayout) findViewById( R.id.secretary_push_layout );
		img_layout = (FrameLayout) findViewById( R.id.secretary_push_img_layout );
		
		line = findViewById( R.id.secretary_push_line );
		button_layout = (RelativeLayout) findViewById( R.id.secretary_push_button_layout );
		tvButton = (TextView) findViewById( R.id.secretary_push_button_text );
		
	}
	
	@Override
	public void initRecord(Context context, ChatRecord record )
	{
		
		push_layout.setOnClickListener( clickListener );
		img_layout.setOnClickListener( clickListener );
		
		screenWidth = CommonFunction.getScreenPixWidth( context );
		
		bean = GsonUtil.getInstance( )
			.getServerBean( record.getContent( ), RecordSecretaryPushBean.class );
		if(bean ==null)return;
		smallPicWidth = screenWidth - CommonFunction.dipToPx( mContext, 2 * 20 );
		bigPicWidth = screenWidth - CommonFunction.dipToPx( mContext, 2 * 15 );
	}
	
	@Override
	public void showRecord(Context context, ChatRecord record )
	{
		bean = GsonUtil.getInstance( )
			.getServerBean( record.getContent( ), RecordSecretaryPushBean.class );
		this.record = record;
		
		// 判断标题和描述是否为空来决定显示的控件并设置图片大小
		if(bean ==null)return;
		
		boolean isHasTitle = !CommonFunction.isEmptyOrNullStr( bean.title );
		boolean isHasDesc = !CommonFunction.isEmptyOrNullStr( bean.description );

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}
		
		if ( isHasTitle && isHasDesc )
		{
			push_layout.setVisibility( VISIBLE );
			img_layout.setVisibility( GONE );
			
			tvTitle.setVisibility( VISIBLE );
			lab.setVisibility( VISIBLE );
			line.setVisibility( VISIBLE );
			button_layout.setVisibility( VISIBLE );
			
			if ( bean.height == 0 || bean.width == 0 )
				smallPicHeight = smallPicWidth;// 如果没有下发图片宽高，则默认显示正方形
			else
				smallPicHeight = smallPicWidth * bean.height / bean.width;
			ViewGroup.LayoutParams smallParams = new RelativeLayout.LayoutParams( smallPicWidth, smallPicHeight );
			imgSmall.getImageView( ).setLayoutParams( smallParams );
			imgSmall.execute( R.color.c_d1d1d1, bean.picurl );
		}
		else if ( isHasTitle )
		{
			push_layout.setVisibility( VISIBLE );
			img_layout.setVisibility( GONE );
			
			tvTitle.setVisibility( VISIBLE );
			lab.setVisibility( GONE );
			line.setVisibility( VISIBLE );
			button_layout.setVisibility( VISIBLE );
			
			if ( bean.height == 0 || bean.width == 0 )
				smallPicHeight = smallPicWidth;// 如果没有下发图片宽高，则默认显示正方形
			else
				smallPicHeight = smallPicWidth * bean.height / bean.width;
			ViewGroup.LayoutParams smallParams = new RelativeLayout.LayoutParams( smallPicWidth, smallPicHeight );
			imgSmall.getImageView( ).setLayoutParams( smallParams );
			imgSmall.execute( R.color.c_d1d1d1, bean.picurl );
			
		}
		else if ( isHasDesc )
		{
			
			push_layout.setVisibility( VISIBLE );
			img_layout.setVisibility( GONE );
			
			tvTitle.setVisibility( GONE );
			lab.setVisibility( VISIBLE );
			line.setVisibility( VISIBLE );
			button_layout.setVisibility( VISIBLE );
			
			if ( bean.height == 0 || bean.width == 0 )
				smallPicHeight = smallPicWidth;// 如果没有下发图片宽高，则默认显示正方形
			else
				smallPicHeight = smallPicWidth * bean.height / bean.width;
			ViewGroup.LayoutParams smallParams = new RelativeLayout.LayoutParams( smallPicWidth, smallPicHeight );
			imgSmall.getImageView( ).setLayoutParams( smallParams );

			imgSmall.execute( R.color.c_d1d1d1, bean.picurl );
		}
		else
		{
			push_layout.setVisibility( GONE );
			img_layout.setVisibility( VISIBLE );
			
			if ( bean.height == 0 || bean.width == 0 )
				bigPicHeight = bigPicWidth;// 如果没有下发图片宽高，则默认显示正方形
			else
				bigPicHeight = bigPicWidth * bean.height / bean.width;
			ViewGroup.LayoutParams bigParams = new RelativeLayout.LayoutParams( bigPicWidth, bigPicHeight );
			imgBig.getImageView( ).setLayoutParams( bigParams );
			imgBig.executeRound( R.color.c_d1d1d1, bean.picurl );
		}
		
		tvTitle.setText( bean.title );
		lab.setText( bean.description );
		
		if ( CommonFunction.isEmptyOrNullStr( bean.titlebutton ) )
			tvButton.setText( R.string.click_for_more );
		else
			tvButton.setText( bean.titlebutton );
		
		// push_layout.setTag( bean );
		// img_layout.setTag( bean );
	}
	
	@Override
	public void reset( )
	{
		tvTitle.setText( "" );
		lab.setText( "" );
//		imgSmall.execute( NetImageView.DEFAULT_SMALL, "" );
//		imgBig.execute( NetImageView.DEFAULT_SMALL, "" );//jiqiang
		tvButton.setText( "" );
	}
	
	/** 图文下发点击事件 */
	View.OnClickListener clickListener = new View.OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			// 点击跳转相应事件
			// RecordSecretaryPushBean pushBean = ( RecordSecretaryPushBean )
			// getTag( );

			String url = bean.url;
			String picname = bean.picname;
			CommonFunction.log( "sherlock", "SecretaryPushRecordView.onClick " + bean.url );
			if ( CommonFunction.isEmptyOrNullStr( url ) )
			{
				CommonFunction.log( "sherlock", "the jump url is null == " + url );
				return;
			}

//			if ( record.getFuid( ) == Config.CUSTOM_SERVICE_UID )// 小秘书
//			{
//				DataStatistics.get( mContext )
//					.addButtonEvent( DataTag.BTN_message_chat_secretary_msg );
//			}
//			if ( record.getFuid( ) == 999 )// 游戏中心
//			{
//				DataStatistics.get( mContext )
//					.addButtonEvent( DataTag.BTN_message_chat_gamecenter_msg );
//			}
//			if ( record.getFuid( ) == 998 )// 圈管理
//			{
//				DataStatistics.get( mContext )
//					.addButtonEvent( DataTag.BTN_message_chat_group_admin_msg );
//			}
//			if ( record.getFuid( ) == 997 )// 贴吧
//			{
//				DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_kanla_msg );
//			}
//			if ( record.getFuid( ) == 996 )// 贴吧管理
//			{
//				DataStatistics.get( mContext )
//					.addButtonEvent( DataTag.BTN_message_chat_kanla_admin_msg );
//			}
//			if ( record.getFuid( ) == 995 )//会员中心
//			{
//				DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_vip_msg );
//			}
//			if ( record.getFuid( ) == 994 )//礼物商店
//			{
//				DataStatistics.get( mContext )
//					.addButtonEvent( DataTag.BTN_message_chat_giftshop_msg );
//			}
//			if ( record.getFuid( ) == 993 )//表情中心
//			{
//				DataStatistics.get( mContext )
//					.addButtonEvent( DataTag.BTN_message_chat_facecenter_msg );
//			}
//			if ( record.getFuid( ) == 992 )//积分商城
//			{
//				DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_shop_msg );
//			}

			String httpPrefix = PathUtil.getHTTPPrefix( );
			boolean isRemoteUrl = url.contains( httpPrefix );

			if ( !CommonFunction.isEmptyOrNullStr( url ) && isRemoteUrl &&
				url.contains( "gamecenter" ) )
			{
				GameWebViewActivity.launchGameCenter( getContext( ), url );
			}
			else if ( isRemoteUrl )
			{// 网页跳转
				Intent webIntent = new Intent( );
				webIntent.setClass( mContext, WebViewAvtivity.class );
				webIntent.putExtra( WebViewAvtivity.WEBVIEW_URL, url );
				webIntent.putExtra( WebViewAvtivity.WEBVIEW_TITLE, picname );
				webIntent.putExtra( WebViewAvtivity.WEBVIEW_SHARE_SUBTYPE,
					ShareSubType.SHARE_SUBTYPE_SECRETARY );
				mContext.startActivity( webIntent );
			}
			else
			{// 仅内部跳转
//				if ( "iaround://credits".equals( url ) && record.getFuid( ) == 992 )
//					StatisticsApi.statisticEventCreditStoreEnter( mContext, 3 );
				InnerJump.Jump( getContext( ), url, false );
			}
		}
	};
	
}
