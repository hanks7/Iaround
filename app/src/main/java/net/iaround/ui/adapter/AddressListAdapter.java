package net.iaround.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;

import java.util.ArrayList;
import java.util.Locale;

import net.iaround.ui.activity.MapSearchIaroundActivity.PoiInfo;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月13日 下午2:40:10
 * @Description: 选择地址的列表适配器
 */
public class AddressListAdapter extends BaseAdapter implements OnClickListener, Filterable
{
	private final int HOLDER_TAG = R.layout.map_iaround_item;
	
	private AddressFilter mFilter;//地址过滤器
	private final Object mLock = new Object();
	private ArrayList<PoiInfo> mOriginalValues = new ArrayList<PoiInfo>(); //完整的数据请求数据
	
	private ArrayList< PoiInfo > addresses = new ArrayList<PoiInfo>();
	private Activity mContext;
	private String strCity = ""; 
	private String selectedName = "";
	
	public AddressListAdapter(Activity mActivity, ArrayList< PoiInfo > addresses, String selectedName)
	{
		super( );
		this.mContext = mActivity;
		this.addresses = addresses;
		mOriginalValues.addAll(addresses);
		this.selectedName = selectedName;
	}
	
	public int getCount( )
	{
		if ( addresses != null )
		{
			return addresses.size( );
		}
		return 0;
	}
	
	public long getItemId( int position )
	{
		return position;
	}

	class AddressItemHolder{
		 
		
		RelativeLayout rlNoShow;
		TextView tvNoShow;
		
		RelativeLayout rlContent;
		ImageView ivLocationIcon;
		TextView name;
		TextView address;
		
		ImageView ivSelected;
	}
	
	
	public View getView( int position , View convertView , ViewGroup parent )
	{
		PoiInfo addr = addresses.get( position );
		AddressItemHolder holder;
		if ( convertView == null )
		{
			convertView = View.inflate( parent.getContext( ) , R.layout.map_iaround_item ,null );
			holder = new AddressItemHolder();
			holder.rlNoShow = (RelativeLayout) convertView.findViewById( R.id.rlNoshow );
			holder.tvNoShow = (TextView) convertView.findViewById( R.id.no_show );

			holder.rlContent = (RelativeLayout) convertView.findViewById( R.id.rlContent );
			holder.ivLocationIcon = (ImageView) convertView.findViewById(R.id.ivLocationIcon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			
			holder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
			
			convertView.setOnClickListener( this );
			convertView.setTag(HOLDER_TAG, holder);
		}else{
			holder = (AddressItemHolder) convertView.getTag(HOLDER_TAG);
		}
		
		int type = addr.type;
		
		if ( type == 0 )
		{
			holder.tvNoShow.setText(addr.name);
			
			holder.rlNoShow.setVisibility( View.VISIBLE );
			holder.rlContent.setVisibility( View.GONE );
			
		}else
		{
			holder.name.setText( addr.name );
			holder.address.setText( addr.address );
			
			holder.rlNoShow.setVisibility( View.GONE );
			holder.rlContent.setVisibility( View.VISIBLE );
		}
		
		String nameStr = addr.name;
		if(selectedName.contains(",")){
			selectedName = selectedName.split(",")[1];
		}
		if(!TextUtils.isEmpty(selectedName) && selectedName.contains(nameStr)){
			holder.ivSelected.setVisibility(View.VISIBLE);
			holder.name.setTextColor(mContext.getResources().getColor(R.color.c_333333));
			holder.ivLocationIcon.setImageResource(R.drawable.z_dynamic_addresslist_loctionicon_selected);
		}else{
			holder.ivSelected.setVisibility(View.INVISIBLE);
			holder.name.setTextColor(mContext.getResources().getColor(R.color.c_999999));
			holder.ivLocationIcon.setImageResource(R.drawable.z_dynamic_addresslist_loctionicon_normal);
		}
		convertView.setTag( addr );
		return convertView;
	}
	
	
	@Override
	public void onClick( View v )
	{
		PoiInfo addr = ( PoiInfo ) v.getTag( );
		int lat = ( int ) ( addr.lat * 1E6 );
		int lng = ( int ) ( addr.lng * 1E6 );
		
		if ( lat == 0 && lng == 0 )
		{
			Intent data = new Intent( );
			
			data.putExtra( "Lat" , lat );
			data.putExtra( "Lng" , lng );
			data.putExtra( "Name" , "" );
			data.putExtra( "Address" , "" );
			mContext.setResult( Activity.RESULT_OK , data );
			mContext.finish( );
		}
		else
		{
			Intent data = new Intent( );
			
			String tempName = addr.name;
			if ( !getStrCity().equals( tempName ) )
			{
				tempName = getStrCity() + "," + addr.name;
			}
			data.putExtra( "Lat" , lat );
			data.putExtra( "Lng" , lng );
			data.putExtra( "Name" , tempName );
			data.putExtra( "Address" , addr.address );
			mContext.setResult( Activity.RESULT_OK , data );
			mContext.finish( );
		}
	}
	
	@Override
	public Object getItem( int position )
	{
		return addresses.get( position );
	}

	@Override
	public Filter getFilter() {
		if(mFilter == null)
		{
			mFilter = new AddressFilter();
		}
		return mFilter;
	}
	
	
	public String getStrCity() {
		return strCity;
	}

	public void setStrCity(String strCity) {
		this.strCity = strCity;
	}

	public ArrayList<PoiInfo> getmOriginalValues() {
		return mOriginalValues;
	}

	public void setmOriginalValues(ArrayList<PoiInfo> mOriginalValues) {
		addresses = mOriginalValues;
		this.mOriginalValues.clear();
		this.mOriginalValues.addAll(mOriginalValues);
		notifyDataSetChanged();
	}

	class AddressFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			
			FilterResults results = new FilterResults();
			
			if(TextUtils.isEmpty(constraint))
			{
				synchronized (mLock) {
					ArrayList<PoiInfo> list = new ArrayList<PoiInfo>(getmOriginalValues());
					results.values = list;
					results.count = list.size();
				}
			}else{
				Locale defloc = Locale.getDefault();
				String prefixString = constraint.toString().toLowerCase(defloc);
				
				final ArrayList<PoiInfo> values = getmOriginalValues();
				
				final int count = values.size();
				
				final ArrayList<PoiInfo> newValue = new ArrayList<PoiInfo>(count);
				
				for(PoiInfo value : values){
					String name = value.name;
					if(TextUtils.isEmpty(name))
					{
						continue;
					}
					
					if(name.indexOf(prefixString) != -1)
					{
						newValue.add(value);
					}
				}
				
				results.values = newValue;
				results.count = newValue.size();
			}
			
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			
			addresses = (ArrayList<PoiInfo>) results.values;
			if(results.count > 0){
				notifyDataSetChanged();
			}else{
				notifyDataSetInvalidated();
			}
		}
	}
}
