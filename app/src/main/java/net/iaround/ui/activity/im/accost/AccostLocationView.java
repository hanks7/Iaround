package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.fragment.FragmentAMap;
import net.iaround.ui.map.MapUtils;


/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-地图
 */

public class AccostLocationView extends AccostRecordView implements OnClickListener
{

	private TextView tvTime;
	private TextView tvAddress;
	private ImageView ivLoaction;
	private FrameLayout flContent;
	private String[] latLng;

	public AccostLocationView(Context context )
	{
		super( context );
		createView( R.layout.accost_record_loaction );
	}

	@Override
	public void build( ChatRecord record )
	{
		tvTime = (TextView) findViewById( R.id.tvTime );
		String timeStr = TimeFormat.timeFormat4( mContext , record.getDatetime( ) );
		tvTime.setText( timeStr );

		String locationUrl = record.getAttachment( );
		double lat = 0;
		double lng = 0;
		if ( !TextUtils.isEmpty( locationUrl ) )
		{
			latLng = locationUrl.split( "," );
			try
			{
				lat =  (Double.parseDouble( latLng[ 0 ] )/ 1000000d);
				lng =  (Double.parseDouble( latLng[ 1 ] )/ 1000000d);
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}

			if (MapUtils.isLoadNativeMap( mContext )|| GooglePlayServicesUtil.isGooglePlayServicesAvailable( mContext ) != ConnectionResult.SUCCESS )
			{
				locationUrl = getMapUrl(lat, lng);
			}
			else {
				locationUrl = getLocationPreview(lat, lng);
			}
		}
		ivLoaction = (ImageView)findViewById( R.id.ivLocation );
		int defResId = PicIndex.DEFAULT_LOCATION_SMALL;
//		ImageViewUtil.getDefault( ).loadImage( locationUrl, ivLoaction, defResId, defResId );//jiqiang
		GlideUtil.loadImage(BaseApplication.appContext,locationUrl,ivLoaction, defResId, defResId);
		
		tvAddress = (TextView) findViewById( R.id.tvAddress );
		tvAddress.setText( record.getContent( ) );
		
		LoactionHolder holder = new LoactionHolder( );
		holder.lat = Integer.parseInt(latLng[0]);
		holder.lng = Integer.parseInt(latLng[1]);
		holder.address = record.getContent( );
		
		flContent = (FrameLayout) findViewById( R.id.content );
		flContent.setOnClickListener(  this);
		flContent.setTag( holder );
	}

	@Override
	public void onClick( View v )
	{
		LoactionHolder holder = ( LoactionHolder ) v.getTag( );
		Toast.makeText(getContext(),"click map",Toast.LENGTH_LONG).show();
		MapUtils.showOnePositionMap( mContext , MapUtils.LOAD_TYPE_POS_MAP , holder.lat ,
				holder.lng , holder.address , "" );
		
		
		//限制连续点击事件
		flContent.setOnClickListener( null );
		new Handler( ).postDelayed(new Runnable( )
		{		
			@Override
			public void run( )
			{
				flContent.setOnClickListener( AccostLocationView.this );
			}
		} , 2000 );
	}
	
	class LoactionHolder
	{
		int lat;
		int lng;
		String address;
	}
}
