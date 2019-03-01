
package net.iaround.ui.chat.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.MessagesPicsetBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.ui.comon.NetImageView;

import java.util.ArrayList;


/**
 * @ClassName PicsetPictextView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 侃啦,游戏中心推送图文消息
 */

public class PicsetPictextView extends ChatRecordView
{
	Context mContext;
	
	private LinearLayout llGameCenter;
	
	// 图组
	NetImageView img;
	FrameLayout img_layout;
	TextView lab; // 描述
	LinearLayout push_layout; // 整个图文布局
	RelativeLayout btn1;
	RelativeLayout btn2;
	RelativeLayout btn3;
	ArrayList< RelativeLayout > btns = new ArrayList< RelativeLayout >( );
	
	// 图文（只有大图的图组）
	NetImageView ptImg;
	FrameLayout ptImg_layout;
	TextView ptLab;
	TextView ptTitle;
	
	private ViewGroup.LayoutParams params;
	private int imgShowWidth;
	
	public PicsetPictextView( Context context )
	{
		super( context );
		mContext = context;
		LayoutInflater.from( context ).inflate( R.layout.chat_record_game_center_push , this );
		
		llGameCenter = (LinearLayout) findViewById( R.id.game_center_pictext );
		
		img = ( NetImageView ) findViewById( R.id.game_center_img );
		img_layout = (FrameLayout) findViewById( R.id.img_layout );
		lab = (TextView) findViewById( R.id.detail );
		
		push_layout = (LinearLayout) findViewById( R.id.game_center_buttons_layout );
		btn1 = (RelativeLayout) findViewById( R.id.button_1 );
		btn2 = (RelativeLayout) findViewById( R.id.button_2 );
		btn3 = (RelativeLayout) findViewById( R.id.button_3 );
		btns.add( btn1 );
		btns.add( btn2 );
		btns.add( btn3 );
		
		ptImg = ( NetImageView ) findViewById( R.id.pictext_img );
		ptImg_layout = (FrameLayout) findViewById( R.id.pictext_img_layout );
		ptLab = (TextView) findViewById( R.id.pictext_lab );
		ptTitle = (TextView) findViewById( R.id.pictext_title );
	}
	
	@Override
	public void initRecord(Context context , ChatRecord record )
	{
		img.setScaleType( ScaleType.FIT_XY );
		ptImg.setScaleType( ScaleType.FIT_XY );
		
		int sWidth = CommonFunction.getScreenPixWidth( context );
		imgShowWidth = sWidth - CommonFunction.dipToPx( mContext , 2 * 20 );
		
	}
	
	@Override
	public void showRecord(Context context , final ChatRecord record )
	{
		
		MessagesPicsetBean bean = GsonUtil.getInstance( ).getServerBean( record.getContent( ) ,
				MessagesPicsetBean.class );
		
		if ( bean == null )
		{
			CommonFunction.log( "sherlock" , "messages game center gatServerBean error == "
					+ record.getContent( ) );
			return;
		}
		
		int imgShowHegiht = imgShowWidth * bean.height / bean.width;
		params = new RelativeLayout.LayoutParams( imgShowWidth , imgShowHegiht );
		
		if ( bean.datas == null || bean.datas.size( ) <= 0 )
		{// 图文
			llGameCenter.setVisibility( View.VISIBLE );
			push_layout.setVisibility( View.GONE );
			
			ptLab.setText( bean.description );
			ptTitle.setText( bean.title );
			
			ptImg.getImageView( ).setLayoutParams( params );
			ptImg.executeRound( R.color.c_d1d1d1 , bean.picurl );
			
		}
		else
		{// 图组
			llGameCenter.setVisibility( View.GONE );
			push_layout.setVisibility( View.VISIBLE );
			
			lab.setText( bean.title );
			img.getImageView( ).setLayoutParams( params );
			img.executeRound( R.color.c_d1d1d1 , bean.picurl );
			
			if ( bean.datas != null )
			{
				for ( int i = 0 ; i < bean.datas.size( ) ; i++ )
				{
					RelativeLayout btn = btns.get( i );
					btn.setVisibility( View.VISIBLE );
					
					String picUrl = bean.datas.get( i ).picurl;
					String title = bean.datas.get( i ).title;
					final String url = bean.datas.get( i ).link;
					
					String iconTag = "btn_icon";
					String textTag = "btn_text";
					( ( NetImageView ) btn.findViewWithTag( iconTag ) ).execute( defSmall ,
							picUrl );
					
					( (TextView) btn.findViewWithTag( textTag ) ).setText( title );
					
					btn.setOnClickListener( new View.OnClickListener( )
					{
						@Override
						public void onClick( View v )
						{
							addDataBtn( record );
							InnerJump.Jump( mContext , url );
						}
					} );
//					btn.setTag( url );
				}
			}
			
		}
		
		final String url = bean.link;
		llGameCenter.setOnClickListener( new View.OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				addDataBtn( record );
				InnerJump.Jump( mContext , url );
			}
		} );
		
		push_layout.setOnClickListener( new View.OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				addDataBtn( record );
				InnerJump.Jump( mContext , url );
			}
		} );

		
	}

	private void addDataBtn(ChatRecord record)
	{
//		if ( record.getFuid( ) == Config.CUSTOM_SERVICE_UID )// 小秘书
//		{
//			DataStatistics.get( mContext )
//				.addButtonEvent( DataTag.BTN_message_chat_secretary_msg );
//		}
//		if ( record.getFuid( ) == 999 )// 游戏中心
//		{
//			DataStatistics.get( mContext )
//				.addButtonEvent( DataTag.BTN_message_chat_gamecenter_msg );
//		}
//		if ( record.getFuid( ) == 998 )// 圈管理
//		{
//			DataStatistics.get( mContext )
//				.addButtonEvent( DataTag.BTN_message_chat_group_admin_msg );
//		}
//		if ( record.getFuid( ) == 997 )// 贴吧
//		{
//			DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_kanla_msg );
//		}
//		if ( record.getFuid( ) == 996 )// 贴吧管理
//		{
//			DataStatistics.get( mContext )
//				.addButtonEvent( DataTag.BTN_message_chat_kanla_admin_msg );
//		}
//		if ( record.getFuid( ) == 995 )//会员中心
//		{
//			DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_vip_msg );
//		}
//		if ( record.getFuid( ) == 994 )//礼物商店
//		{
//			DataStatistics.get( mContext )
//				.addButtonEvent( DataTag.BTN_message_chat_giftshop_msg );
//		}
//		if ( record.getFuid( ) == 993 )//表情中心
//		{
//			DataStatistics.get( mContext )
//				.addButtonEvent( DataTag.BTN_message_chat_facecenter_msg );
//		}
//		if ( record.getFuid( ) == 992 )//积分商城
//		{
//			DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_shop_msg );
//		}//jiqiang
	}
	
	@Override
	public void reset( )
	{
		
		lab.setText( "" );
		img.execute( defShare , "" );
		for ( int i = 0 ; i < btns.size( ) ; i++ )
		{
			btns.get( i ).setVisibility( View.GONE );
		}
	}
}
