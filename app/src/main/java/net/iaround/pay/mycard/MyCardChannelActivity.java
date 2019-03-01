
package net.iaround.pay.mycard;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.pay.ChannelType;
import net.iaround.pay.PayGoodsList;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.UserVipOpenActivity;
import net.iaround.ui.comon.SuperActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 
 *
 */
public class MyCardChannelActivity extends SuperActivity implements OnClickListener
{
	public static final String MYCARD_CHANNEL = "mycard_channel";
	public static final String MYCARD_VIP = "mycard_vip";
	
	private static final int REQ_PAY = 100;
	private PullToRefreshScrollView llEmpty;
	private PullToRefreshListView listview;
	private DataAdapter adapter;
	
	private ArrayList< MyCardChannelItem > channelLists = new ArrayList< MyCardChannelItem >( );
	private long channelFlag = 0;
	private int channelId = 0;
	private boolean isPayForVip = false;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.pay_mycard_channel );
		findViewById( R.id.fl_back ).setOnClickListener( this );
//		findViewById( R.id.title_right_img ).setVisibility( View.INVISIBLE );
		( (TextView) findViewById( R.id.tv_title ) ).setText( R.string.mycard_channel );
		
		channelId = getIntent( ).getIntExtra( MYCARD_CHANNEL , 0 );
		isPayForVip = getIntent( ).getBooleanExtra( MYCARD_VIP , false );
		
		llEmpty = (PullToRefreshScrollView) findViewById( R.id.llEmpty );
		llEmpty.getRefreshableView( ).setFillViewport( true );
		
		listview = (PullToRefreshListView) findViewById( R.id.channel_list );
		listview.getRefreshableView( ).setSelector( R.drawable.transparent );
		listview.getRefreshableView( ).setCacheColorHint( 0 );
		listview.setOnRefreshListener( new OnRefreshListener< ListView >( )
		{
			
			@Override
			public void onRefresh( PullToRefreshBase< ListView > refreshView )
			{
				channelFlag = PayHttpProtocol.paywaysChildlist( mActivity , Config.PLAT ,
						Config.APP_VERSION , channelId , MyCardChannelActivity.this );
				if ( channelFlag < 0 )
				{
					mHandler.sendEmptyMessage( -1 );
				}
			}
		} );
		
		llEmpty.setOnRefreshListener( new OnRefreshListener< ScrollView >( )
		{
			
			@Override
			public void onRefresh( PullToRefreshBase< ScrollView > refreshView )
			{
				channelFlag = PayHttpProtocol.paywaysChildlist( mActivity , Config.PLAT ,
						Config.APP_VERSION , channelId , MyCardChannelActivity.this );
				if ( channelFlag < 0 )
				{
					mHandler.sendEmptyMessage( -1 );
				}
			}
		} );
		
		adapter = new DataAdapter( );
		listview.setAdapter( adapter );
		
		listview.setRefreshing( );
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v.getId( ) == R.id.fl_back )
		{
			finish( );
		}
	}
	
	private class DataAdapter extends BaseAdapter
	{
		
		@Override
		public int getCount( )
		{
			return channelLists.size( );
		}
		
		@Override
		public MyCardChannelItem getItem( int position )
		{
			return channelLists.get( position );
		}
		
		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			ItemHolder holder = null;
			if ( convertView == null )
			{
				holder = new ItemHolder( );
				convertView = mInflater.inflate( R.layout.pay_mycard_channel_item , null );
				holder.btn_title = (Button) convertView.findViewById( R.id.btn_channel );
				convertView.setTag( holder );
			}
			else
			{
				holder = ( ItemHolder ) convertView.getTag( );
			}
			final MyCardChannelItem channel = getItem( position );
			if ( channel != null )
			{
				holder.btn_title.setText( channel.name );
			}
			holder.btn_title.setOnClickListener( new OnClickListener( )
			{
				@Override
				public void onClick( View arg0 )
				{
					if ( isPayForVip )
					{
						// 进入购买VIP
						Intent i = new Intent( mActivity , UserVipOpenActivity.class );
						i.putExtra( PayGoodsList.GOODSLIST_CHANNEL , channel.id );
						i.putExtra( PayGoodsList.GOODSLIST_PAYLOAD , "" );
						i.putExtra( PayGoodsList.GOODSLIST_TITLE , channel.name );
						i.putExtra( PayGoodsList.CHANNEL_TYPE , ChannelType.MYCARD );
						
						mActivity.startActivityForResult( i , REQ_PAY );
					}
					else
					{
						// 进入购买钻石
						Intent i = new Intent( mActivity , PayGoodsList.class );
						i.putExtra( PayGoodsList.GOODSLIST_CHANNEL , channel.id );
						i.putExtra( PayGoodsList.GOODSLIST_PAYLOAD , "" );
						i.putExtra( PayGoodsList.GOODSLIST_TITLE , channel.name );
						
						// mActivity.startActivity( i );
						mActivity.startActivityForResult( i , REQ_PAY );
					}
				}
			} );
			return convertView;
		}
		
		public void notifyDataSetChanged( )
		{
			// 是否为空
			if ( channelLists.size( ) < 1 )
			{
				showEmptyView( );
			}
			else
			{
				hideEmptyView( );
			}
			super.notifyDataSetChanged( );
		}
		
		@Override
		public long getItemId( int position )
		{
			return 0;
		}
	}
	
	private class ItemHolder
	{
		private Button btn_title;
	}
	
	private class MyCardChannelItem
	{
		public int id;
		public String name;
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( channelFlag == flag )
		{
			mHandler.sendEmptyMessage( -1 );
		}
		super.onGeneralError( e , flag );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( channelFlag == flag )
		{
			Message msg = new Message( );
			msg.what = 1;
			msg.obj = result;
			mHandler.sendMessage( msg );
		}
		super.onGeneralSuccess( result , flag );
	}
	
	private void showEmptyView( )
	{
		llEmpty.setVisibility( View.VISIBLE );
		listview.setVisibility( View.GONE );
	}
	
	private void hideEmptyView( )
	{
		llEmpty.setVisibility( View.GONE );
		listview.setVisibility( View.VISIBLE );
	}
	
	public void stopPulling( )
	{
		llEmpty.onRefreshComplete( );
		listview.onRefreshComplete( );
	}
	
	private Handler mHandler = new Handler( )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			if ( msg.what == 1 )
			{
				stopPulling( );
				try
				{
					String result = String.valueOf( msg.obj );
					JSONObject json = new JSONObject( result );
					if ( json.optInt( "status" ) == 200 )
					{
						JSONArray items = json.optJSONArray( "channels" );
						if ( items.length( ) > 0 )
						{
							channelLists.clear( );
						}
						for ( int i = 0 ; i < items.length( ) ; i++ )
						{
							JSONObject item = items.optJSONObject( i );
							if ( item != null )
							{
								MyCardChannelItem channel = new MyCardChannelItem( );
								channel.id = item.optInt( "paywayid" );
								channel.name = CommonFunction.jsonOptString( item ,
										"paywayname" );
								channelLists.add( channel );
							}
						}
					}
					else
					{
						ErrorCode.showError( mActivity , result );
					}
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
				adapter.notifyDataSetChanged( );
			}
			else if ( msg.what == -1 )
			{
				stopPulling( );
				adapter.notifyDataSetChanged( );
			}
		}
	};
	
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		if(REQ_PAY ==requestCode )
		{
			if(resultCode == RESULT_OK)
			{
				setResult( RESULT_OK );
				finish();
			}
		}
	}

}
