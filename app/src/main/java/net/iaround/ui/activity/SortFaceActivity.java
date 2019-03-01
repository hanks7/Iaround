
package net.iaround.ui.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.Face;
import net.iaround.model.entity.FaceCenterModel;
import net.iaround.tools.FaceManager;
import net.iaround.ui.fragment.SortFaceHandle;

import java.util.ArrayList;
import java.util.List;


public class SortFaceActivity extends BaseFragmentActivity
{
	private List<Face> allFace;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_sort_face);
		initView( );
	}
	
	private void initView( )
	{
		ImageView titleBack = ( ImageView ) findViewById( R.id.iv_left );
		ImageView titleRight = (ImageView) findViewById( R.id.iv_right );
		TextView titleName = ( TextView ) findViewById( R.id.tv_title );
		titleName.setText( R.string.my_face );
		titleBack.setImageResource(R.drawable.title_back);
		titleRight.setImageResource(R.drawable.icon_publish);

		titleBack.setVisibility(View.VISIBLE);
		titleRight.setVisibility(View.VISIBLE);
		//返回键
		titleBack.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				finish( );
			}
		} );
		findViewById(R.id.fl_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		titleRight.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				// 保存排序后的表情列表
				if ( SortFaceHandle.isSort == true )
				{
					getSortFace( );
					SortFaceHandle.isSort = false;
				}
				finish( );
			}
		} );
	}
	
	/**
	 * 保存排序后的表情列表
	 */
	@SuppressWarnings ( "static-access" )
	private void getSortFace( )
	{
		ArrayList< Face > tempList = new ArrayList< Face >( );
		allFace = FaceManager.getInstance( getApplicationContext( ) ).getAllFace( );
		if ( SortFaceHandle.tmpList != null && !SortFaceHandle.tmpList.isEmpty( ) )
		{
			for ( String face_name : SortFaceHandle.tmpList )
			{
				for ( Face face : allFace )
				{
					if ( face.getTitle( ).equals( face_name ) )
					{
						tempList.add( face );
					}
				}
			}
			
			if ( tempList != null && !tempList.isEmpty( ) )
			{
				FaceCenterModel.ownFace.clear( );
				FaceCenterModel.ownFace = tempList;
			}
		}
	}
}
