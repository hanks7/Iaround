
package net.iaround.ui.group.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.GatherDetailBean.DetailUserBean;
import net.iaround.ui.group.bean.GatherDetailBean.DetailBean;
import net.iaround.ui.group.bean.GatherListBean;
import net.iaround.ui.group.bean.GatherListBean.GatherUserBean;
import net.iaround.ui.group.bean.GatherListBean.joininfoBeen;
import net.iaround.ui.group.bean.GatherListBean.GatherItemBean;


/**
 * @author zhengshaota
 * @version 创建时间：2014-11-25
 * @Description: 圈聚会列表已参加成员的头像列表
 */
public class GatherIconView extends LinearLayout
{
	/** 长度 Pixel为单位 **/
	private final int pxPerPhoto = CommonFunction.dipToPx( getContext( ) , 34 );// 已参加成员头像的尺寸
	private final int pxPhotoPadding = CommonFunction.dipToPx( getContext( ) , 6 );// 已参加成员头像间隔
	private final int pxLayoutMargin = CommonFunction.dipToPx( getContext( ) , 20 );// 已参加成员头像间隔
	// 已参加成员头像，每行最大个数
	private final int MAX_COUNT_PER_ROW = 8;
	private final int MAX_COUNT_SHOW = 18;// 已参加成员头像最大数量
	private LayoutParams onePicPara;
	private LinearLayout.LayoutParams rowPara;
	private int desResID = NetImageView.DEFAULT_FACE;
	private LinearLayout llGreetPhoto;// 已参加成员头像的Layout
	private TextView tvJoin_count;
	private View split;

	private View.OnClickListener userPicOnClickListener = new OnClickListener( )
	{
		@Override
		public void onClick( View arg0 )
		{
			
			User user = (User)arg0.getTag( );
			//gh
//			SpaceOther.launchUser( getContext( ) , user.getUid( ) , user , ChatFromType.UNKONW );
		}
	};
	
	public GatherIconView(Context context )
	{
		super( context );
		LayoutInflater.from( getContext( ) ).inflate( R.layout.gather_join_icon_view , this );
	}
	
	public GatherIconView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		LayoutInflater.from( getContext( ) ).inflate( R.layout.gather_join_icon_view , this );
	}
	
	public void initList( GatherItemBean bean )
	{
		initVariable( );
		
		llGreetPhoto = (LinearLayout) findViewById( R.id.llJoinPhoto );
		tvJoin_count = (TextView) findViewById( R.id.join_count );
		split = findViewById( R.id.split );
		
		if ( bean.joininfo != null && bean.joininfo.joinusers != null
				&& bean.joininfo.total > 0 )
		{
			tvJoin_count.setVisibility( View.VISIBLE );
			llGreetPhoto.setVisibility( View.VISIBLE );
			split.setVisibility( View.VISIBLE );
			setGreetLayout( bean.joininfo , bean.user );
		}
		else
		{
			llGreetPhoto.setVisibility( View.GONE );
			tvJoin_count.setVisibility( View.GONE );
			split.setVisibility( View.GONE );
		}
	}
	
	public void initDetail( DetailBean bean )
	{
		initVariable( );
		
		llGreetPhoto = (LinearLayout) findViewById( R.id.llJoinPhoto );
		tvJoin_count = (TextView) findViewById( R.id.join_count );
		split = findViewById( R.id.split );
		
		if ( bean.joininfo != null && bean.joininfo.joinusers != null
				&& bean.joininfo.total > 0 )
		{
			tvJoin_count.setVisibility( View.VISIBLE );
			llGreetPhoto.setVisibility( View.VISIBLE );
			split.setVisibility( View.VISIBLE );
			setDetailGreetLayout( bean.joininfo , bean.user );
		}
		else
		{
			llGreetPhoto.setVisibility( View.GONE );
			tvJoin_count.setVisibility( View.GONE );
			split.setVisibility( View.GONE );
		}
	}
	
	
	private void initVariable( )
	{
		onePicPara = new LayoutParams( pxPerPhoto , pxPerPhoto );
		onePicPara.setMargins( 0 , 0 , pxPhotoPadding , 0 );
		
		int wrap = LayoutParams.WRAP_CONTENT;
		rowPara = new LayoutParams( wrap , wrap );
		rowPara.setMargins( pxLayoutMargin , 0 , pxLayoutMargin , pxPhotoPadding );
	}
	
	/**
	 * 设置布局
	 */
	public void setDetailGreetLayout(GatherListBean.joininfoBeen joininfo , DetailUserBean user )
	{
		llGreetPhoto.removeAllViews( );
		if ( joininfo == null || joininfo.joinusers == null )
		{
			return;
		}
		
		int PicCount = joininfo.joinusers.size( );
		if ( PicCount <= 0 )
		{
			return;
		}
		int total = joininfo.total;
		String str = String.format( getResources( ).getString( R.string.gather_join_count ) ,
				total );
		tvJoin_count.setText( str );
		
		// 计算总共要显示几行
		int rowCount = PicCount / MAX_COUNT_PER_ROW
				+ ( PicCount % MAX_COUNT_PER_ROW > 0 ? 1 : 0 );
		// 计算最多显示多少行
		int max_column = MAX_COUNT_SHOW / MAX_COUNT_PER_ROW
				+ ( MAX_COUNT_SHOW % MAX_COUNT_PER_ROW == 0 ? 0 : 1 );
		
		for ( int row = 0 ; row < rowCount ; row++ )
		{
			LinearLayout llRow = new LinearLayout( getContext( ) );
			llRow.setLayoutParams( rowPara );
			llGreetPhoto.addView( llRow );
			
			// 计算这一行要显示的数量
			int columnCount = PicCount % MAX_COUNT_PER_ROW == 0 ? MAX_COUNT_PER_ROW : PicCount
					% MAX_COUNT_PER_ROW;
			if ( row != rowCount - 1 )
			{
				columnCount = MAX_COUNT_PER_ROW;
			}
			int rowOffset = row * MAX_COUNT_PER_ROW;// 行偏移
			for ( int column = 0 ; column < columnCount ; column++ )
			{
				
				int picIndex = rowOffset + column;
				String url = joininfo.joinusers.get( picIndex ).icon;
				String userid = joininfo.joinusers.get( picIndex ).userid;
				int vipLevel = joininfo.joinusers.get( picIndex ).viplevel;
				
				if ( String.valueOf( user.userid ) != userid )
				{
					
					NetImageView ivPic = new NetImageView( getContext( ) );
					ivPic.setScaleType( ScaleType.CENTER_CROP );
					ivPic.setLayoutParams( onePicPara );
					ivPic.setVipLevel( vipLevel );
					int vipIconSize = CommonFunction.dipToPx( getContext( ) , 10 );
//					ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView( url ,
//							ivPic.getImageView( ) , desResID , desResID , null , vipIconSize );
					
					ivPic.executeRoundFrame( desResID , url );
					
					User spaceUser = new User();
					spaceUser.setUid( Long.parseLong( userid ) );
					spaceUser.setIcon( url );
					spaceUser.setLevel( vipLevel );
					ivPic.setTag( spaceUser );
					ivPic.setOnClickListener( userPicOnClickListener );
					
					llRow.addView( ivPic );
				}
			}
			
			
		}
	}
	
	/**
	 * 设置布局
	 */
	public void setGreetLayout(joininfoBeen joininfo , GatherUserBean user )
	{
		
		llGreetPhoto.removeAllViews( );
		if ( joininfo == null || joininfo.joinusers == null )
		{
			return;
		}
		
		int PicCount = joininfo.joinusers.size( );
		if ( PicCount <= 0 )
		{
			return;
		}
		int total = joininfo.total;
		String str = String.format( getResources( ).getString( R.string.gather_join_count ) ,
				total );
		tvJoin_count.setText( str );
		
		// 计算总共要显示几行
		int rowCount = PicCount / MAX_COUNT_PER_ROW
				+ ( PicCount % MAX_COUNT_PER_ROW > 0 ? 1 : 0 );
		// 计算最多显示多少行
		int max_column = MAX_COUNT_SHOW / MAX_COUNT_PER_ROW
				+ ( MAX_COUNT_SHOW % MAX_COUNT_PER_ROW == 0 ? 0 : 1 );
		
		for ( int row = 0 ; row < rowCount ; row++ )
		{
			LinearLayout llRow = new LinearLayout( getContext( ) );
			llRow.setLayoutParams( rowPara );
			llGreetPhoto.addView( llRow );
			
			// 计算这一行要显示的数量
			int columnCount = PicCount % MAX_COUNT_PER_ROW == 0 ? MAX_COUNT_PER_ROW : PicCount
					% MAX_COUNT_PER_ROW;
			if ( row != rowCount - 1 )
			{
				columnCount = MAX_COUNT_PER_ROW;
			}
			int rowOffset = row * MAX_COUNT_PER_ROW;// 行偏移
			for ( int column = 0 ; column < columnCount ; column++ )
			{
				
				int picIndex = rowOffset + column;
				String url = joininfo.joinusers.get( picIndex ).icon;
				String userid = joininfo.joinusers.get( picIndex ).userid;
				int vipLevel = joininfo.joinusers.get( picIndex ).viplevel;
				
				NetImageView ivPic = new NetImageView( getContext( ) );
				ivPic.setScaleType( ScaleType.CENTER_CROP );
				ivPic.setLayoutParams( onePicPara );
				ivPic.setVipLevel( vipLevel );
				// int vipIconSize = CommonFunction.dipToPx( getContext( ) , 10
				// );
				
				// ImageViewUtil.getDefault(
				// ).fadeInRoundLoadImageInConvertView( url ,
				// ivPic.getImageView( ) ,
				// desResID , desResID , null , vipIconSize );
				
				ivPic.executeRoundFrame( NetImageView.DEFAULT_AVATAR_ROUND_LIGHT , url );
				
				User spaceUser = new User();
				spaceUser.setIcon( url ) ;
				spaceUser.setUid( Long.parseLong( userid ) );
				
				ivPic.setTag( spaceUser );
				ivPic.setOnClickListener( userPicOnClickListener );
				
				llRow.addView( ivPic );
				
			}
			
			
		}
	}
	
}
