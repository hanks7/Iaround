
package net.iaround.ui.fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.entity.Face;
import net.iaround.model.entity.FaceCenterModel;
import net.iaround.tools.glide.GlideUtil;

import java.util.ArrayList;
import java.util.List;


public class SortFaceHandle extends ListFragment
{
	private MyAdapter adapter;
	private DragSortListView mDslv;
	private DragSortController mController;
	public int dragStartMode = DragSortController.ON_DOWN;
	public boolean removeEnabled = false;
	public int removeMode = DragSortController.FLING_REMOVE;
	public boolean sortEnabled = true;
	public boolean dragEnabled = true;
	/** 表情图标 */
	private List< String > face_icon;
	/** 表情名称 */
	public static ArrayList< String > face_name;
	/** 排序后进行暂时存储的列表 */
	public static ArrayList< String > tmpList = new ArrayList< String >( );
	/** 获取从SharedPreference里获取到的已排序的列表 */
	public static List< String > getSaveList;
	public static boolean isSort = false ;
	
	@Override
	public View onCreateView( LayoutInflater inflater , ViewGroup container ,
			Bundle savedInstanceState )
	{
		mDslv = ( DragSortListView ) inflater.inflate( R.layout.face_fragment_main , container ,
				false );
		
		// 添加HeaderView
		View headView = View.inflate( getActivity( ) , R.layout.face_manage_head , null );
		mDslv.addHeaderView( headView , null , false );
		TextView headerText = ( TextView ) mDslv.findViewById( R.id.header_text );
		View view1 = mDslv.findViewById( R.id.view1 );
		headerText.setText( R.string.adjust_face_order );
		view1.setVisibility( View.GONE );
		
		mController = buildController( mDslv );
		mDslv.setFloatViewManager( mController );
		mDslv.setOnTouchListener( mController );
		mDslv.setDragEnabled( dragEnabled );
		mDslv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		return mDslv;
	}
	
	
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener( )
	{
		@Override
		public void drop( int from , int to )
		{
			if ( from != to )
			{
				String item = adapter.getItem( from );
				adapter.remove( item );
				adapter.insert( item , to );
				
				/** 数据随视图移动 **/
				String img = face_icon.get( from );
				face_icon.remove( from );
				face_icon.add( to , img );
			}
		}
	};
	
	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener( )
	{
		@Override
		public void remove( int which )
		{
			adapter.remove( adapter.getItem( which ) );
			/** 数据随视图移除 **/
			face_icon.remove( which );
		}
	};
	
	@Override
	public void onActivityCreated( Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );
		
		mDslv = ( DragSortListView ) getListView( );
		mDslv.setDropListener( onDrop );
		mDslv.setRemoveListener( onRemove );
		setListAdapter( );
	}
	
	public DragSortController buildController( DragSortListView dslv )
	{
		MyDSController c = new MyDSController( dslv );
		return c;
	}
	
	public void setListAdapter( )
	{
		// 获取进行展示的列表内容
		getSortFaceList( );
		adapter = new MyAdapter( face_name );
		setListAdapter( adapter );
	}
	
	/**
	 * 获取进行展示的列表内容
	 */
	private void getSortFaceList( )
	{
		face_icon = new ArrayList< String >( );
		face_name = new ArrayList< String >( );
		
		for ( Face face :  FaceCenterModel.ownFace  )
		{
			face_icon.add( face.getIcon( ) );
			face_name.add( face.getTitle( ) );
		}
	}

	
	private class MyAdapter extends ArrayAdapter< String >
	{
		
		public MyAdapter( List< String > artists )
		{
			super( getActivity( ) , R.layout.list_item_bg_handle , R.id.text , artists );
		}
		
		public View getView( int position , View convertView , ViewGroup parent )
		{
			View v = super.getView( position , convertView , parent );
			ImageView icon = ( ImageView ) v.findViewById( R.id.img );

//		    ImageViewUtil.getDefault( ).fadeInLoadImageInConvertView(
//						face_icon.get( position ) , icon , R.drawable.default_pitcure_small ,
//						R.drawable.default_pitcure_small );
			GlideUtil.loadImage(BaseApplication.appContext,face_icon.get( position ) , icon , R.drawable.default_pitcure_small ,
					R.drawable.default_pitcure_small);

			if ( position == 0 )
			{
				View view1 = v.findViewById( R.id.view1 );
				view1.setVisibility( View.GONE );
			}
			
			return v;
		}
	}
	
	private class MyDSController extends DragSortController
	{
		DragSortListView mDslv;
		
		public MyDSController( DragSortListView dslv )
		{
			super( dslv );
			/** 指定控件作为移动把手 **/
			setDragHandleId( R.id.move_handle );
			mDslv = dslv;
		}
		
		@Override
		public View onCreateFloatView( int position )
		{
			View v = adapter.getView( position , null , mDslv );
			return v;
		}
		
		@Override
		public void onDestroyFloatView( View floatView )
		{
			isSort = true ;
			tmpList = face_name;
		}
		
		@Override
		public int startDragPosition( MotionEvent ev )
		{
			int res = super.dragHandleHitPosition( ev );
			int width = mDslv.getWidth( );
			
			if ( ( int ) ev.getX( ) > width - width / 3 )
			{
				return res;
			}
			else
			{
				return DragSortController.MISS;
			}
		}
	}

}
