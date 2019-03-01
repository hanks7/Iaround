
package net.iaround.ui.view.near;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.InnerJump;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.NearActivity;
import net.iaround.ui.datamodel.ResourceBanner;


/** 附近用户列表插入的活动栏 */
public class NearbyActivityBannerView extends RelativeLayout
{
	private Context mContext;
	
	private ImageView bannerImg;
	private Button closeBtn;

	private ResourceBanner data;
	private NearActivity.SwitchTabFragmentCallback mSwitchTabFragmentCallback;

	public NearbyActivityBannerView(Context context , ResourceBanner banner ) {
		super( context );
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.data = banner;
		initView( );
		refreshView( data );
	}
	
	private void initView( )
	{
		View itemView = LayoutInflater.from( mContext ).inflate(
				R.layout.x_nearby_activity_banner_view , this );
		bannerImg = (ImageView) itemView.findViewById( R.id.chat_ad_im );
		closeBtn = (Button) itemView.findViewById( R.id.chat_ad_btn );
	}
	
	public void refreshView( final ResourceBanner banner )
	{
		if ( banner != null )
		{
			setVisibility( View.VISIBLE );
//			ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView(
//					banner.imageurl, bannerImg, 0, 0, null, 10 );
			GlideUtil.loadRoundImage(BaseApplication.appContext,banner.imageurl,10, bannerImg);
			bannerImg.setOnClickListener(
					new OnClickListener( )
					{

						@Override
						public void onClick( View v )
						{
							// TODO Auto-generated method stub
//							DataStatistics.get( mContext ).addButtonEvent(
//									DataTag.BTN_near_banner );//gh
							String url = banner.link;
							if ( url != null )
							{
								if ( url.contains( "http://" ) | url.contains( "https://" ) )
								{
									if ( banner.jumptype == 1 )
									{
										InnerJump.Jump( mContext, url, true );
									}
									else if ( banner.jumptype == 3 )
									{
										Intent i = new Intent( Intent.ACTION_VIEW );
										i.setData( Uri.parse( url ) );
										mContext.startActivity( i );
									}
								}
								else if ( url.contains( "iaround://" ) )
								{
									if ( url.equals( "iaround://chatbarhall" ) )
									{
										if ( mSwitchTabFragmentCallback != null )
										{
											mSwitchTabFragmentCallback.switchToChatBarFragment( );
											return;
										}
									}
									InnerJump.Jump( mContext, url );
								}
								else
								{
									// 无法跳转
									CommonFunction.toastMsg( mContext, "无跳转" );
								}
//								StatisticsApi
//									.statisticADClick( mContext, banner.id, 2 );
//								DataStatistics.get( mContext ).addAdClickEvent(
//									banner.id, 2 );//gh
								banner.getRequest(true);
							}
							else
							{
								// 无法跳转
								CommonFunction.toastMsg( mContext, "无跳转" );
							}
						}
					} );

			closeBtn.setOnClickListener( new OnClickListener( )
			{

				@Override
				public void onClick( View v )
				{
					// TODO Auto-generated method stub
					setVisibility( View.GONE );
				}
			} );
		}
	}

	public void setSwitchTabFragmentCallback( NearActivity.SwitchTabFragmentCallback switchTabFragmentCallback )
	{
		mSwitchTabFragmentCallback = switchTabFragmentCallback;
	}
}
