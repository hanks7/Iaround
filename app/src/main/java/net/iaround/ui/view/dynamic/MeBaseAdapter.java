
package net.iaround.ui.view.dynamic;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;


/**
 * 适配器，具备着保存列表数据的功能 by linyg
 */
public abstract class MeBaseAdapter extends BaseAdapter
{
	private List< ? > arrayList; // 列表数据
	private String[ ] strings; // 数组数据
	private HashMap< Integer , View > hashMap; // 用于保存数据的HashMap
	private boolean iscache = true; // 是否保存
	
	protected MeBaseAdapter(List< ? > arrayList )
	{
		this.arrayList = arrayList;
		hashMap = new HashMap< Integer , View >( );
	}
	
	protected MeBaseAdapter(String[ ] strings )
	{
		this.strings = strings;
		hashMap = new HashMap< Integer , View >( );
	}
	
	/**
	 * 获取列表或者数组的大小
	 */
	
	public int getCount( )
	{
		if ( arrayList != null )
		{
			return arrayList.size( );
		}
		else
		{
			return strings.length;
		}
	}
	
	
	/**
	 * 根据指定的列表或者数组位置获取列表或者数组元素
	 * 
	 * @param position
	 *            指定的列表或者数组位置
	 */
	
	public Object getItem( int position )
	{
		try
		{
			if ( arrayList != null )
			{
				return arrayList.get( position );
			}
			else
			{
				return strings[ position ];
			}
		}
		catch ( Exception e )
		{
			return null;
		}
	}
	
	
	/**
	 * 获取指定的列表或者数组位置
	 * 
	 * @param position
	 */
	
	public final long getItemId( int position )
	{
		return position;
	}
	
	
	/**
	 * 判断HashMap中是否保存着指定位置的视图，如果有，则从HashMap中获取，否则，将调用 createView(int position)
	 * 方法创建一个
	 */
	
	public View getView( int position , View convertView , ViewGroup parent )
	{
		View view = null;
		iscache = true;
		if ( !hashMap.containsKey( position ) )
		{
			view = createView( position );
			if ( iscache )
			{
				hashMap.put( position , view );
			}
		}
		else
		{
			view = hashMap.get( position );
		}
		return view;
	}
	
	/**
	 * 是否保存每一项
	 * 
	 * @param iscache
	 *            默认是true保存每一项子项，为false则每滚动一次都将加载视图item
	 * @time 2011-9-26 上午11:02:18
	 * @author:linyg
	 */
	public void setIsCache( boolean iscache )
	{
		this.iscache = iscache;
	}
	
	/**
	 * 清除每项
	 * 
	 * @time 2011-10-26 下午04:53:54
	 * @author:linyg
	 */
	public void clearItems( )
	{
		if ( hashMap != null )
		{
			hashMap.clear( );
		}
	}
	
	/**
	 * 根据指定的列表或者数组位置创建列表中的一项视图
	 * 
	 * @param position
	 *            指定的列表或者数组位置
	 * @return 列表中的一项视图
	 */
	public abstract View createView( int position );
}
