
package net.iaround.ui.store;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.store.StoreDataListBean.GiftsBags;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.iaround.R;
import net.iaround.analytics.ums.DataTag;

import java.util.ArrayList;


/**
 * 商店首页礼包栏目适配器
 * @author Administrator
 *
 */
public class StoreGiftBagAdapter extends BaseAdapter
{
	private Context mContext;
	private ArrayList<GiftsBags> mList;
	
	
	public StoreGiftBagAdapter(Context context , ArrayList< GiftsBags > giftbags )
	{
		mContext = context;
		mList = giftbags;
	}
	
	@Override
	public int getCount( )
	{
		if ( mList.size( ) > 0 )
		{
			return mList.size( );
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public Object getItem(int position )
	{
		return mList.get( position );
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
	
	class ViewHolder
	{
		NetImageView image;
	}
	
	@Override
	public View getView(int position , View convertView , ViewGroup parent )
	{
		ViewHolder viewHolder = null;
		if ( convertView == null )
		{
			viewHolder = new ViewHolder( );
			convertView = View.inflate( mContext , R.layout.x_store_giftbags_gridview_item , null );
			viewHolder.image = ( NetImageView ) convertView.findViewById( R.id.ItemImage );
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = ( ViewHolder ) convertView.getTag( );
		}
		
		GiftsBags giftsBags = mList.get( position );
		if ( giftsBags != null )
		{
//			viewHolder.image.execute( R.drawable.z_find_giftbag_default , giftsBags.getImageUrl( ) );
			viewHolder.image.setTag( giftsBags );
			viewHolder.image.setOnClickListener( new OnClickListener( )
			{
				@Override
				public void onClick( View v )
				{
					GiftsBags giftsBags = ( GiftsBags ) v.getTag( );
					// 跳转到大礼包页面进行购买
//					DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_find_store_giftBag);
//					StoreGiftPacksActivity.launcherGiftPacksToBuy( mContext , giftsBags.giftbagid );//gh 不要了
				}
			} );
		}
		
		return convertView;
	}
}
