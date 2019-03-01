
package net.iaround.ui.store;


import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.tools.CommonFunction;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.PicIndex;
import net.iaround.ui.store.StoreDataListBean.Sections;

import net.iaround.R;


/**
 * 商店首页礼物适配器
 * @author Administrator
 *
 */
public class StoreMainGridAdapter extends BaseAdapter
{
	private Context mContext;
	public StoreDataListBean.Sections data;
	
	public StoreMainGridAdapter(Sections data , Context context)
	{
		this.mContext = context;
		this.data = data;
	}
	
	@Override
	public int getCount( )
	{
		if ( data != null )
		{
			return data.gifts.size( );
		}
		return 0 ;
	}
	
	@Override
	public Object getItem(int position )
	{
		if ( data != null ){
			return data.gifts.get( position );
		}
		return null;
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
	
	@Override
	public View getView(int position , View convertView , ViewGroup parent )
	{
		ViewHolder ViewHolder = null;
		if ( convertView == null )
		{
			ViewHolder = new ViewHolder( );
			convertView = View.inflate( mContext , R.layout.store_gift_classify_item , null );
			ViewHolder.icon = (NetImageView) convertView.findViewById( R.id.gift_icon );
			ViewHolder.name = (TextView) convertView.findViewById( R.id.gift_name );
			ViewHolder.price = (TextView) convertView.findViewById( R.id.gift_price );
			ViewHolder.charm = (TextView) convertView.findViewById( R.id.gift_charm );
			ViewHolder.experience = (TextView) convertView.findViewById( R.id.gift_experience );
			ViewHolder.Flag1 = (TextView) convertView.findViewById( R.id.flag1 );
			ViewHolder.Flag2 = (TextView) convertView.findViewById( R.id.flag2 );
			ViewHolder.giftFlagly = (RelativeLayout) convertView
					.findViewById( R.id.gift_flag );
			convertView.setTag( ViewHolder );
		}
		else
		{
			ViewHolder = ( ViewHolder ) convertView.getTag( );
		}
		
		Gifts gift = data.gifts.get( position );
		ViewHolder.name.setText( CommonFunction.getLangText(mContext, gift.name ) );
		ViewHolder.icon.executeFadeInRound( PicIndex.DEFAULT_GIFT , gift.icon );
		
		String price = "";
		String priceTypeStr = "";
		if ( gift.discountgoldnum != null && !gift.discountgoldnum.equals( "null" ) )
		{// 优惠价格
			price = gift.discountgoldnum;
		}else
		{// 非优惠
			price = String.valueOf( gift.goldnum );
		}
		
		if ( gift.currencytype == 1 )
		{// 1金币2钻石
			priceTypeStr = mContext.getString( R.string.gold_balance );
			ViewHolder.price.setText( priceTypeStr + price );
			ViewHolder.price.setTextColor( Color.parseColor( "#999999" ) );
		}
		else
		{
			priceTypeStr = mContext.getString( R.string.diamond_balance ) ;
			ViewHolder.price.setTextColor( Color.parseColor( "#FF9900" ) );
			ViewHolder.price.setText( priceTypeStr + price );
		}
		
		ViewHolder.charm.setText( mContext.getString( R.string.charisma_title_new ) + gift.charmnum );
		ViewHolder.experience.setText( String.format(mContext.getString( R.string.chat_gift_exp ) , ""+gift.expvalue ));

		//gh 礼物标签 显示顺序：专属--优惠--新--热
		if ( gift.viptype == 1 ){
			ViewHolder.Flag1.setVisibility(View.VISIBLE);

		}else{
			ViewHolder.Flag1.setVisibility(View.GONE);

		}

		if (gift.ishot == 0 && gift.isnew == 0 && gift.discountgoldnum == null){
			ViewHolder.Flag2.setVisibility(View.GONE);
		}

		if ( gift.ishot == 1 ){
			ViewHolder.Flag2.setBackgroundResource(R.drawable.z_store_gift_hotflag);
			ViewHolder.Flag2.setVisibility(View.VISIBLE);
		}

		if ( gift.isnew == 1 ){
			ViewHolder.Flag2.setBackgroundResource(R.drawable.z_store_gift_newflag);
			ViewHolder.Flag2.setVisibility(View.VISIBLE);
		}

		if ( gift.discountgoldnum != null && !TextUtils.isEmpty( gift.discountgoldnum )) {
			if (gift.goldnum > Integer.valueOf( gift.discountgoldnum )) {
				ViewHolder.Flag2.setBackgroundResource(R.drawable.z_store_gift_saleflag);
				ViewHolder.Flag2.setVisibility(View.VISIBLE);
			}
		}
		
		// 礼物标签 显示顺序：专属--优惠--新--热
//		 if (  gift.flag1 == -1 && gift.flag2 == -1  )
//		 {
//			ViewHolder.giftFlagly.setVisibility( View.GONE );
//		 }
//		 else
//		 {
//			ViewHolder.giftFlagly.setVisibility( View.VISIBLE );
//
//			if ( gift.flag1 != -1 ){
//				ViewHolder.Flag1.setVisibility( View.VISIBLE );
//				ViewHolder.Flag1.setBackgroundResource( gift.flag1 );
//			}else{
//				ViewHolder.Flag1.setVisibility( View.GONE );
//			}
//
//			if ( gift.flag2 != -1 ){
//				ViewHolder.Flag2.setVisibility( View.VISIBLE );
//				ViewHolder.Flag2.setBackgroundResource( gift.flag2 );
//			}else{
//				ViewHolder.Flag2.setVisibility( View.GONE );
//			}
//		}
		 
		return convertView;
	}
	
	class ViewHolder
	{
		NetImageView icon;
		TextView name;
		TextView price;
		TextView charm;
		TextView experience;
		TextView Flag1;
		TextView Flag2;
		RelativeLayout giftFlagly;
	}
	
}
