
package net.iaround.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.model.entity.Face;
import net.iaround.model.entity.FaceCenterModel;
import net.iaround.model.entity.MyFaceListBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.ZipUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.face.FaceDetailActivityNew;
import net.iaround.ui.view.face.TextProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class OwnFaceActivity extends BaseActivity implements OnClickListener
{
	private Toast toast = null;
	private TextView title_right;
	private long Uid;
	private static MyFaceListAdapter mAdapter;

	/** 本次操作是否添加或删除了表情 */
	private boolean isAddordelFace = false;
	/** 我拥有的表情没有数据的标志 */
	public final int ownFace_NoData_Flag = -1000;
	/** 已删除的表情没有数据的标志 */
	public final int delFace_NoData_Flag = -2000;

	private long FACE_DESCRIBE;// 请求表情描述的flag
	private final int DELETE_FACE_REFRESH = 100;// 删除表情的标志
	private final int UPDATE_DOWLOAD_PROGRESS = 200;// 更新表情下载进度条
	private final int FACE_DOWLOAD_FAIL = 300;// 下载失败
	private final int FACE_DOWLOAD_SUCCESS = 400;// 下载完成
	private final int UNZIPFOLDER_FAIL = 500;// 解压压缩包失败
	private final int UPDATE_PRECENT = 600;// 非本页面下载，更新进度条
	private PullToRefreshExpandableListView mExpandableListView;
	private ArrayList<Face> allFace = new ArrayList< Face >( );
	private HashMap< Long , Integer > flagMap = new HashMap< Long , Integer >( );// 请求描述的表情对应着flag
	private HashMap< Integer , String > resultMap = new HashMap< Integer , String >( );// 表情的ID对应着描述
	private HashMap< Integer , Long > faceLenghtMap = new HashMap< Integer , Long >( );// 表情的压缩包大小

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_my_face);

		initView( );
		getOwnFaceList( );
		showData( );

	}


	private void initView( )
	{   //标题栏
		TextView title_name = ( TextView ) findViewById( R.id.tv_title );
		ImageView title_back = ( ImageView ) findViewById( R.id.iv_left );
		title_right = ( TextView ) findViewById( R.id.tv_right );

		title_right.setText( R.string.group_order );
		title_name.setText( R.string.my_face );
		title_back.setImageResource(R.drawable.title_back);

		title_back.setVisibility(View.VISIBLE);
		title_right.setVisibility(View.VISIBLE);

		findViewById(R.id.fl_left).setOnClickListener(this);
		title_back.setOnClickListener( this );

		mExpandableListView = (PullToRefreshExpandableListView) findViewById( R.id.myface_listview );
		View footView = View.inflate( mContext , R.layout.myface_footview , null );
		RelativeLayout buyRecord = ( RelativeLayout ) footView
				.findViewById( R.id.faceitem_footlayout );

		buyRecord.setOnClickListener( this );

		mExpandableListView.getRefreshableView( ).addFooterView( footView , null , false );
		mExpandableListView.getRefreshableView( ).setGroupIndicator( null );
		mExpandableListView.setMode( Mode.DISABLED );
		mExpandableListView.setOnScrollListener( mListOnScrollListener );
	}

	/**
	 * 获取表情列表
	 */
	private void getOwnFaceList( )
	{
		getData( );// 获取数据
		deleteUndercarriage( );// 移除下架的表情
		addEmptyFace( );// 添加假数据用来展示提示语
	}


	// 显示数据
	public void showData( )
	{
		mAdapter = new MyFaceListAdapter( );
		mExpandableListView.getRefreshableView( ).setAdapter( mAdapter );
		displaySortBtn( );
	}

	private Handler mMainHandler = new Handler( Looper.getMainLooper( ) )
	{
		@Override
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case DELETE_FACE_REFRESH :// 删除表情
					deleteFaceAfterUpdate( ( Face ) msg.obj );
					break;

				case FACE_DOWLOAD_FAIL :// 下载表情失败
					downLoadFail( ( String ) msg.obj , msg.arg1 , 2 );
					break;

				case UNZIPFOLDER_FAIL :// 解压安装包失败
					downLoadFail( ( String ) msg.obj , msg.arg1 , 1 );
					break;

				case FACE_DOWLOAD_SUCCESS :// 下载表情成功
					downloadSuccessAfterUpdate( msg.arg1 );// 列表更新
					downloadSuccessAfterReset( msg.arg1 , ( String ) msg.obj ,
							resultMap.get( msg.arg1 ) );// 更改后缀名
					break;

				case UPDATE_PRECENT :// 在表情中心下载表情，刷新本页
					handleUpdateProgress1( msg.arg1 , ( Face ) msg.obj );
					break;

				case UPDATE_DOWLOAD_PROGRESS :// 更新下载进度
					handleUpdateProgress( msg.arg1 , ( Double ) msg.obj );
					break;

			}
		}
	};

	/**
	 * 在表情中心或表情详情下载表情，刷新本页面
	 *
	 * @param position
	 * @param face
	 */
	private void handleUpdateProgress1( int position , Face face )
	{
		mAdapter.updateView( position , face );
		if ( face.getPercent( ) == 100 )
		{
			face.setPercent( 0 );

		    for ( Face face1 : FaceCenterModel.delFace )
			{
                  if ( face1.getFaceid( ) == face.getFaceid( ) )
				{
                	  face1.setPercent( 0 );
                	  FaceMainActivity.resetMap( FaceCenterModel.upMyfaceViewPrecentMap , face1 );
				}
			}

		    addNewInstalledFace( );
			delEmptyFace( );
			addEmptyFace( );
			mAdapter.notifyDataSetChanged( );
		}
	}

	/**
	 * 处理更新下载进度
	 *
	 * @param faceId
	 * @param dPrecent
	 */
	private void handleUpdateProgress( int faceId , double dPrecent )
	{
		Face face = null;
		int position = 0;
		int precent = ( int ) ( dPrecent * 100 );
		for ( int i = 0 ; i < FaceCenterModel.delFace.size( ) ; i++ )
		{
			if ( faceId == FaceCenterModel.delFace.get( i ).getFaceid( ) )
			{
				position = i;
				FaceCenterModel.delFace.get( i ).setPercent( precent );
				face = FaceCenterModel.delFace.get( i );
				break;
			}
		}

		mAdapter.updateView( position , face );
		FaceCenterModel.upFacemainViewPrecentMap.put( face , precent );
		FaceCenterModel.upFaceDetailPrecentMap.put( face , precent );
	}


	/**
	 *
	 * 为了让在非本页面下载的表情添加到拥有的列表里，并从已删除列表里移除
	 */
	private void addNewInstalledFace( )
	{
		ArrayList< Face > ownTemp = new ArrayList< Face >( );
		for ( Face face : allFace )
		{
			int appStatus = FaceMainActivity.checkFaceInstalled( face.getFaceid( ) );
			if ( appStatus == 1 )// 已安装
			{
				ownTemp.add( face );
			}
		}

		int ownSize = FaceCenterModel.ownFace.size( );
		for ( Face face : FaceCenterModel.ownFace )
		{
			if ( face.getFaceid( ) == ownFace_NoData_Flag )
			{
				ownSize = ownSize - 1;
				continue;
			}
		}

		if ( ownTemp.size( ) != ownSize )
		{// 移除已删掉列表里新安装的表情
			ArrayList< Face > temp = new ArrayList< Face >( );
			if ( FaceCenterModel.delFace.size( ) > 0 )
			{
				for ( Face delface : FaceCenterModel.delFace )
				{
					for ( Face face2 : ownTemp )
					{
						if ( delface.getFaceid( ) == face2.getFaceid( ) )
						{
							temp.add( delface );
						}
					}
				}
				FaceCenterModel.delFace.removeAll( temp );
			}

			// 添加新安装的表情到我拥有的表情列表
			ArrayList< Face > newInstalled = new ArrayList< Face >( );
			if ( FaceCenterModel.ownFace != null && FaceCenterModel.ownFace.size( ) > 0 )
			{
				for ( Face face : ownTemp )
				{
					for ( Face own : FaceCenterModel.ownFace )
					{
						if ( face.getFaceid( ) == own.getFaceid( ) )
						{
							newInstalled.add( face );
						}
					}
				}
				ownTemp.removeAll( newInstalled );
				for ( Face face : ownTemp )
				{
					FaceCenterModel.ownFace.add( 0 , face );
				}
			}
		}
	}

	/**
	 * 表情安装失败 what 1代表解压失败 2 代表下载失败
	 *
	 * @param fileName
	 */
	protected void downLoadFail( String fileName , int faceid , int what )
	{
		for ( int i = 0 ; i < FaceCenterModel.delFace.size( ) ; i++ )
		{
			if ( faceid == FaceCenterModel.delFace.get( i ).getFaceid( ) )
			{
				FaceCenterModel.delFace.get( i ).setPercent( 0 );
				FaceCenterModel.upFacemainViewFailList.add( FaceCenterModel.delFace.get( i ) );
				FaceCenterModel.upFaceDetailFailList.add( FaceCenterModel.delFace.get( i ) );
				FaceMainActivity.resetMap( FaceCenterModel.upFacemainViewPrecentMap ,
						FaceCenterModel.delFace.get( i ) );
				FaceMainActivity.resetMap( FaceCenterModel.upFaceDetailPrecentMap ,
						FaceCenterModel.delFace.get( i ) );
				if ( what == 1 )
				{
					showTextToast( "(" + FaceCenterModel.delFace.get( i ).getTitle( ) + ")"
							+ getString( R.string.unZipfolder_fail ) );
				}
				else
				{
					showTextToast( "(" + FaceCenterModel.delFace.get( i ).getTitle( ) + ")"
							+ getString( R.string.download_fail ) );
				}
				break;
			}
		}
		mAdapter.notifyDataSetChanged( );
		delFailFile( fileName );
	}

	/**
	 * 删除文件
	 *
	 * @param fileName
	 */
	private void delFailFile( String fileName )
	{
		File file = new File( fileName );
		if ( file.exists( ) )
		{
			file.delete( );
		}
	}

	/**
	 * 默认的，没有顺序的列表
	 */
	private void getDefaultList( )
	{
		for ( Face face : allFace )
		{
			int appStatus = FaceMainActivity.checkFaceInstalled( face.getFaceid( ) );
			if ( appStatus == 1 )// 已安装
			{
				FaceCenterModel.ownFace.add( face );
			}
			else if ( appStatus == -1 && face.getOwn( ) == 1 )// 拥有但未安装
			{
				FaceCenterModel.delFace.add( face );
			}
		}
	}


	/**
	 * 获取数据
	 */
	@SuppressWarnings ( "unchecked" )
	private void getData( )
	{
		allFace = FaceManager.getAllFace( );
		Uid = Common.getInstance().loginUser.getUid( );
		String data = SharedPreferenceCache.getInstance( mContext ).getString( "myFace" + String.valueOf( Uid ) );
		if ( !data.isEmpty( ) )// 获取缓存的表情列表
		{
			MyFaceListBean bean = GsonUtil.getInstance( ).getServerBean( data ,
					MyFaceListBean.class );
			if ( FaceCenterModel.ownFace.size( ) <= 0 && bean.ownList != null )
			{
				FaceCenterModel.ownFace.addAll( bean.ownList );
			}

			int delSize = getDelFaceSize( );
			if ( delSize <= 0 && bean.delList != null )
			{
				for ( Face i : bean.delList )
				{
					i.setPercent( 0 );
				}
				FaceCenterModel.delFace.addAll( bean.delList );
			}
			addNewInstalledFace( );// 添加在非本页新下载的表情以显示
			delEmptyFace( );// 移除提示项
		}
		else
		{
			getDefaultList( );// 获取默认的没有顺序的列表
		}
	}

	/**
	 * 移除已下架的表情
	 *
	 * @return
	 */
	private void deleteUndercarriage( )
	{
		ArrayList< Face > temp = new ArrayList< Face >( );
		ArrayList< Face > temp1 = new ArrayList< Face >( );
		temp.addAll( FaceCenterModel.delFace );
		temp.addAll( FaceCenterModel.ownFace );

		if ( temp.size( ) > 0 )
		{
			for ( Face face : temp ){
			  for ( Face allface : allFace ){
					if ( face.getFaceid( ) == allface.getFaceid( ) )
					{
						temp1.add( face );
					}
			}}
			if ( temp1.size( ) > 0 )
			{
				temp.removeAll( temp1 );
			}
			if ( temp.size( ) > 0 )
			{
				ArrayList< Face > ownTemp = new ArrayList< Face >( );
				ArrayList< Face > delTemp = new ArrayList< Face >( );
				for ( Face face : temp )
				{
					if ( FaceCenterModel.ownFace.contains( face )
							&& face.getFaceid( ) != ownFace_NoData_Flag )
					{
						ownTemp.add( face );
					}
					if ( FaceCenterModel.delFace.contains( face )
							&& face.getFaceid( ) != delFace_NoData_Flag )
					{
						delTemp.add( face );
					}
				}
				if ( ownTemp.size( ) != 0 )
				{
					FaceCenterModel.ownFace.removeAll( ownTemp );
				}
				if ( delTemp.size( ) != 0 )
				{
					FaceCenterModel.delFace.removeAll( delTemp );
				}
			}}
	}

	/**
	 * 如果表情列表为空，添加假数据用来展示提示
	 */
	private void addEmptyFace( )
	{
		if ( FaceCenterModel.ownFace.isEmpty( ) )
		{
			Face face = new Face( );
			face.setFaceid( ownFace_NoData_Flag );
			FaceCenterModel.ownFace.add( face );
		}

		if ( FaceCenterModel.delFace.isEmpty( ) && FaceCenterModel.ownFace.size( ) == 1 )
		{
			for ( Face myface : FaceCenterModel.ownFace )
			{
				if ( myface.getFaceid( ) == ownFace_NoData_Flag )
				{
					Face face = new Face( );
					face.setFaceid( delFace_NoData_Flag );
					FaceCenterModel.delFace.add( face );
				}
			}}
	}


	/**
	 * 删除“还没有购买或下载过表情”的提示项
	 */
	private void delEmptyFace( )
	{
		if ( FaceCenterModel.delFace.size( ) > 0 )
		{
			Iterator< Face > e = FaceCenterModel.delFace.iterator( );
			while ( e.hasNext( ) )
			{
				Face face = e.next( );
				if ( face.getFaceid( ) == delFace_NoData_Flag )
				{
					e.remove( );
				}
			}
		}
		if ( FaceCenterModel.ownFace.size( ) > 1 )
		{
			Iterator< Face > e1 = FaceCenterModel.ownFace.iterator( );
			while ( e1.hasNext( ) )
			{
				Face face = e1.next( );
				if ( face.getFaceid( ) == ownFace_NoData_Flag )
				{
					e1.remove( );
				}
			}
		}
	}


	/**
	 * 下载成功之后拥有与删除两个列表的操作
	 *
	 * @param faceId
	 */
	private void downloadSuccessAfterUpdate( int faceId )
	{
		if ( FaceCenterModel.delFace.size( ) > 0 )
		{
			Iterator< Face > e = FaceCenterModel.delFace.iterator( );
			while ( e.hasNext( ) )
			{
				Face face = e.next( );
				if ( faceId == face.getFaceid( ) )
				{
					e.remove( );
					face.setPercent( 0 );
					FaceCenterModel.ownFace.add( 0 , face );
				}
			}
		}
		if ( FaceCenterModel.ownFace.size( ) > 1 )
		{
			Iterator< Face > e1 = FaceCenterModel.ownFace.iterator( );
			while ( e1.hasNext( ) )
			{
				Face face = e1.next( );
				if ( face.getFaceid( ) == ownFace_NoData_Flag )
				{
					e1.remove( );
				}
			}
		}
		if ( FaceCenterModel.delFace.size( ) == 0
				&& FaceCenterModel.ownFace.contains( ownFace_NoData_Flag ) )
		{
			Face face = new Face( );
			face.setFaceid( delFace_NoData_Flag );
			FaceCenterModel.delFace.add( face );
		}
		isAddordelFace = true;
		mAdapter.notifyDataSetChanged( );
		FaceMainActivity.isDownloadFace = true;
	}

	/**
	 * 下载表情成功后修改后缀名，初始化动态表情
	 *
	 * @param id
	 */
	private void downloadSuccessAfterReset( int id , String fileName , String descriseResult )
	{
		FaceMainActivity.saveDescribeToFile( fileName , id , descriseResult );
		String uid = String.valueOf( Uid );
		String path = PathUtil.getFaceDir( ) + uid + "//" + String.valueOf( id );
		File file = new File( path );
		if ( file.exists( ) )
		{
			CommonFunction.reFaceFileName( path );// 更改文件后缀名
		}
		else
		{
			CommonFunction.log( "" , getString( R.string.download_fail ) );
		}
		delFailFile( fileName );
		FaceManager.resetOtherFace( );// 初始化动态表情
		sendBroadcast( new Intent( ).setAction( FaceManager.FACE_INIT_ACTION ) );
	}

	/**
	 * 删除表情之后拥有与删除两个列表的操作
	 *
	 * @param face1
	 */
	private void deleteFaceAfterUpdate( Face face1 )
	{
		showTextToast( getString( R.string.delete_succuss ) );
		Iterator< Face > own = FaceCenterModel.ownFace.iterator( );
		while ( own.hasNext( ) )
		{
			Face face = own.next( );
			if ( face == face1 )
			{
				own.remove( );
			}
		}
		FaceCenterModel.delFace.add( 0 , face1 );
		if ( FaceCenterModel.delFace.size( ) > 1 )
		{
			Iterator< Face > e = FaceCenterModel.delFace.iterator( );
			while ( e.hasNext( ) )
			{
				Face face = e.next( );
				if ( face.getFaceid( ) == delFace_NoData_Flag )
				{
					e.remove( );
				}
			}
		}
		if ( FaceCenterModel.ownFace.size( ) == 0 )
		{
			Face face = new Face( );
			face.setFaceid( ownFace_NoData_Flag );
			FaceCenterModel.ownFace.add( face );
		}
		isAddordelFace = true;
		mAdapter.notifyDataSetChanged( );
	}

	/**
	 * 缓存已有顺序的表情列表
	 */
	private void saveDataCache( )
	{
		MyFaceListBean bean = new MyFaceListBean( );
		if ( FaceCenterModel.ownFace != null && FaceCenterModel.ownFace.size( ) > 0 )
		{
			bean.ownList = FaceCenterModel.ownFace;
			for ( Face face : bean.ownList )
			{
				face.setPercent( 0 );
			}
		}
		if ( FaceCenterModel.delFace != null && FaceCenterModel.delFace.size( ) > 0 )
		{
			bean.delList = FaceCenterModel.delFace;
		}
		if ( bean.ownList != null && !bean.ownList.isEmpty( ) )
		{
			String data = GsonUtil.getInstance( ).getStringFromJsonObject( bean );
			SharedPreferenceCache.getInstance( mContext ).putString( "myFace" + String.valueOf( Uid ) , data );
		}
	}

	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left:
				finish( );
				break;

			case R.id.tv_right :
				Intent i = new Intent( mContext , SortFaceActivity.class );
				startActivity( i );
				break;

			case R.id.faceitem_footlayout :

				Intent intent = new Intent( mContext , FaceBuyHistoryActivity.class );
				startActivity( intent );
				break;
		}
	}

	/**
	 * 表情管理适配器
	 *
	 * @author Administrator
	 *
	 */
	class MyFaceListAdapter extends BaseExpandableListAdapter
	{
		@Override
		public int getGroupCount( )
		{
			return 2;
		}

		@Override
		public int getChildrenCount( int groupPosition )
		{
			if ( groupPosition == 0 )
			{
				if ( FaceCenterModel.ownFace == null )
					return 0;
				return FaceCenterModel.ownFace.size( );
			}
			else
			{
				if ( FaceCenterModel.delFace == null )
					return 0;
				return FaceCenterModel.delFace.size( );
			}
		}

		@Override
		public Object getGroup( int groupPosition )
		{
			if ( groupPosition == 0 )
				return FaceCenterModel.ownFace;
			else
				return FaceCenterModel.delFace;
		}

		@Override
		public Object getChild( int groupPosition , int childPosition )
		{
			if ( groupPosition == 0 )
				return FaceCenterModel.ownFace.get( childPosition );
			else
				return FaceCenterModel.delFace.get( childPosition );
		}

		@Override
		public long getGroupId( int groupPosition )
		{
			return 0;
		}

		@Override
		public long getChildId( int groupPosition , int childPosition )
		{
			return 0;
		}

		@Override
		public boolean hasStableIds( )
		{
			return false;
		}

		public void updateView( int position , Face face )
		{
			int VisiblePosition = mExpandableListView.getRefreshableView( )
					.getFirstVisiblePosition( );

			int offset = position - VisiblePosition + FaceCenterModel.ownFace.size( ) + 2;
			// 只有在可见区域才更新
			if ( offset < 0 )
				return;

			if ( offset >= 0 )
			{
				View view = mExpandableListView.getRefreshableView( ).getChildAt( offset );
				if ( view == null )
				{
					return;
				}
				ViewHolder vh = ( ViewHolder ) view.getTag( );

				if ( vh == null || face == null )
				{
					CommonFunction.log( "position" , "--->position ===" + position );
				}
				else
				{
					displayProgress( vh , face );
				}
			}
		}

		@Override
		public View getGroupView( int groupPosition , boolean isExpanded , View convertView ,
				ViewGroup parent )
		{
			if ( convertView == null )
			{
				convertView = View.inflate( mContext , R.layout.face_manage_head , null );
			}
			TextView headerText = ( TextView ) convertView.findViewById( R.id.header_text );
			String text = "";
			if ( groupPosition == 0 )
			{
				text = getString( R.string.ownFace_head_own );
			}
			else
			{
				text = getString( R.string.ownFace_head_del );
			}
			headerText.setText( text );
			convertView.setClickable( false );
			mExpandableListView.getRefreshableView( ).expandGroup( groupPosition );
			return convertView;
		}

		@Override
		public View getChildView( int groupPosition , final int childPosition ,
				boolean isLastChild , View convertView , ViewGroup parent )
		{
			ViewHolder viewHolder = null;
			if ( convertView == null )
			{
				viewHolder = new ViewHolder( );
				convertView = View.inflate( mContext , R.layout.myface_item , null );
				viewHolder.faceIcon = ( ImageView ) convertView.findViewById( R.id.face_img );
				viewHolder.faceName = ( TextView ) convertView.findViewById( R.id.face_name );
				viewHolder.buttonLayout = ( RelativeLayout ) convertView
						.findViewById( R.id.progress_ly );
				viewHolder.progress_text = ( TextView ) convertView
						.findViewById( R.id.progress_text );
				viewHolder.progressBar = (TextProgressBar) convertView
						.findViewById( R.id.progressBar );
				viewHolder.noFace = ( TextView ) convertView.findViewById( R.id.none_face );
				viewHolder.buttonLayout.setTag( viewHolder );
				convertView.setTag( viewHolder );
			}
			else
			{
				viewHolder = ( ViewHolder ) convertView.getTag( );
			}

			if ( groupPosition == 0 )
			{
				Face myface = ( Face ) getChild( 0 , childPosition );
				initOwnFaceView( viewHolder , convertView , myface );
			}
			if ( groupPosition == 1 )
			{
				Face myface = ( Face ) getChild( 1 , childPosition );
				initDelFaceView( viewHolder , convertView , myface );
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable( int groupPosition , int childPosition )
		{
			return false;
		}
	}

	/**
	 * 展示我拥有的表情列表
	 *
	 * @param viewHolder
	 * @param convertView
	 * @param myface
	 */
	private void initOwnFaceView( ViewHolder viewHolder , View convertView , final Face myface )
	{

		if ( myface != null )
		{
			if ( myface.getFaceid( ) == ownFace_NoData_Flag )
			{
				displayWidget( viewHolder , convertView );
				viewHolder.noFace.setText( R.string.goto_download_face );
			}
			else
			{
				viewHolder.noFace.setVisibility( View.GONE );
				viewHolder.progressBar.setVisibility( View.GONE );
				viewHolder.buttonLayout.setVisibility( View.VISIBLE );
				viewHolder.faceIcon.setVisibility( View.VISIBLE );
				viewHolder.faceName.setVisibility( View.VISIBLE );
				viewHolder.progress_text.setVisibility( View.VISIBLE );
				viewHolder.progress_text.setText( R.string.remove_face );
				viewHolder.buttonLayout.setBackgroundResource( R.drawable.face_download_btn );

				if ( myface.getTitle( ) != null )
				{
					viewHolder.faceName.setText( myface.getTitle( ) + "" );
				}
				if ( myface.getIcon( ) != null )
				{
//					ImageViewUtil.getDefault( ).fadeInLoadImageInConvertView( myface.getIcon( ) ,
//							viewHolder.faceIcon , R.drawable.default_pitcure_small ,
//							R.drawable.default_pitcure_small );
					GlideUtil.loadImage(BaseApplication.appContext,myface.getIcon( ),viewHolder.faceIcon, R.drawable.default_pitcure_small, R.drawable.default_pitcure_small);
				}
				viewHolder.buttonLayout.setTag( viewHolder );
				viewHolder.buttonLayout.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						final String faceId = String.valueOf( myface.getFaceid( ) );
						if ( !CommonFunction.isEmptyOrNullStr( faceId ) )
						{
							// 删除表情
							final ViewHolder viewHolder = ( ViewHolder ) v.getTag( );
							clickDelBtn( faceId , myface );
							viewHolder.buttonLayout.setClickable( false );
						}
					}
				} );

				convertView.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						FaceMainActivity.clickFace.add( myface.getFaceid( ) );
						FaceDetailActivityNew.launchForResult( OwnFaceActivity.this ,
								myface.getFaceid( ) );
					}
				} );
			}}
	}

	private void initDelFaceView( ViewHolder viewHolder , View convertView , final Face myface )
	{
		if ( myface != null )
		{
			if ( myface.getFaceid( ) == delFace_NoData_Flag )
			{
				displayWidget( viewHolder , convertView );
				viewHolder.noFace.setText( R.string.goto_buy_face );
			}
			else
			{
				viewHolder.noFace.setVisibility( View.GONE );
				viewHolder.faceIcon.setVisibility( View.VISIBLE );
				viewHolder.faceName.setVisibility( View.VISIBLE );
				viewHolder.progressBar
						.setTextSize( 13 * getResources( ).getDisplayMetrics( ).density );
				viewHolder.progressBar.setTextColor( getResources( ).getColor( R.color.c_cccccc ) );

				if ( myface.getPercent( ) > 0 )
				{
					displayProgress( viewHolder , myface );
				}
				else
				{
					viewHolder.buttonLayout.setVisibility( View.VISIBLE );
					viewHolder.progress_text.setVisibility( View.VISIBLE );
					viewHolder.progress_text.setText( R.string.title_add );
					viewHolder.progressBar.setVisibility( View.GONE );
					viewHolder.buttonLayout.setClickable( true );
					viewHolder.buttonLayout.setBackgroundResource( R.drawable.face_download_btn );
				}

				if ( myface.getTitle( ) != null )
				{
					viewHolder.faceName.setText( myface.getTitle( ) + "" );
				}
				if ( viewHolder.faceIcon != null )
				{
//					ImageViewUtil.getDefault( ).fadeInLoadImageInConvertView( myface.getIcon( ) ,
//							viewHolder.faceIcon , R.drawable.default_pitcure_small ,
//							R.drawable.default_pitcure_small );
					GlideUtil.loadImage(BaseApplication.appContext,myface.getIcon( ),viewHolder.faceIcon, R.drawable.default_pitcure_small, R.drawable.default_pitcure_small);

				}
				viewHolder.buttonLayout.setTag( viewHolder );
				viewHolder.buttonLayout.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						final ViewHolder viewHolder = ( ViewHolder ) v.getTag( );
						if ( myface.getPercent( ) > 0 )
						{
							viewHolder.buttonLayout.setClickable( false );
						}
						else
						{// 下载表情
							viewHolder.buttonLayout.setClickable( true );
							Download( viewHolder , myface );
						}

					}
				} );

				convertView.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						FaceMainActivity.clickFace.add( myface.getFaceid( ) );
						FaceDetailActivityNew.launchForResult( OwnFaceActivity.this ,
								myface.getFaceid( ) );
					}
				} );
			}}
	}

	/**
	 * 开启线程下载表情
	 *
	 * @param viewHolder
	 * @param face
	 */

	private void Download( final ViewHolder viewHolder , final Face face )
	{
		face.setPercent( 1 );
		displayProgress( viewHolder , face );
		FaceCenterModel.upFacemainViewPrecentMap.put( face , 1 );
		FaceCenterModel.upFaceDetailPrecentMap.put( face , 1 );
		new Thread( new Runnable( )
		{
			@Override
			public void run( )
			{
				FACE_DESCRIBE = BusinessHttpProtocol.getFaceDescribe( mContext ,
						face.getFaceid( ) , callBack);
				flagMap.put( FACE_DESCRIBE , face.getFaceid( ) );
				/** 表情保存路径 */
				String path = PathUtil.getFaceDir( ) + String.valueOf( Uid );
				FileDownloadManager manager;
				String fileName = String.valueOf( face.getFaceid( ) ) + ".face" ;
				try
				{
					manager = new FileDownloadManager( mContext , callback ,
							face.getDownUrl( ) , fileName ,
							path , face.getFaceid( ) );
					manager.run( );
				}
				catch ( ConnectionException e )
				{
					e.printStackTrace( );
				}
			}
		} ).start( );

	}

    private HttpCallBack callBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            if ( flag == FACE_DESCRIBE )
            {
                BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
                        BaseServerBean.class );
                if ( bean != null && bean.isSuccess( ) )
                {
                    int faceid = flagMap.get( FACE_DESCRIBE );
                    resultMap.put( faceid , result );
                }
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {

        }
    };


	/** 表情下载回调 */
	private DownloadFileCallback callback = new DownloadFileCallback( )
	{
		// 下载进度条
		@Override
		public void onDownloadFileProgress( long lenghtOfFile , long LengthOfDownloaded , int flag )
		{
			faceLenghtMap.put( flag , lenghtOfFile );
			double dPrecent = ( double ) LengthOfDownloaded / lenghtOfFile;
			sendMessage( UPDATE_DOWLOAD_PROGRESS , flag , 0 , dPrecent );
		}

		// 下载完成
		@Override
		public void onDownloadFileFinish( int flag , String fileName , final String savePath )
		{
			// 下载完成后解压该表情包
			File dir = new File( savePath + fileName );
			if ( dir.exists( ) )
			{
				UnZipFolder( flag , dir , savePath , fileName );
			}
		}

		// 下载失败
		@Override
		public void onDownloadFileError( int flag , String fileName , String savePath )
		{
			sendMessage( FACE_DOWLOAD_FAIL , flag , 0 , savePath + fileName );
		}
	};

	@Override
	protected void onResume( )
	{
		super.onResume( );

		if ( FaceDetailActivityNew.isBuyFace == true )
		{
			addNewInstalledFace( );
			addEmptyFace( );
			delEmptyFace( );
			mAdapter.notifyDataSetChanged( );
			FaceDetailActivityNew.isBuyFace = false;
		}
		else if ( mAdapter != null )
		{
			mAdapter.notifyDataSetChanged( );
		}

		FaceCenterModel.ownFace = removeDuplicateWithOrder( FaceCenterModel.ownFace );
		FaceCenterModel.delFace = removeDuplicateWithOrder( FaceCenterModel.delFace );
		mAdapter.notifyDataSetChanged( );

		if ( !FaceCenterModel.upMyfaceViewPrecentMap.isEmpty( ) )
		{
			mMainHandler.postDelayed( runnable , 0 );// 启动执行runnable.
		}
	}

	/**
	 * 为避免频繁弹出toast
	 *
	 * @param msg
	 */
	private void showTextToast( String msg )
	{
		if ( toast == null )
		{
			toast = Toast.makeText( mContext , msg , Toast.LENGTH_SHORT );
		}
		else
		{
			toast.setText( msg );
		}
		toast.show( );
	}

	/**
	 * 解压表情文件包
	 *
	 * @param faceid
	 * @param file
	 * @param savePath
	 */
	private void UnZipFolder( int faceid , File file , String savePath , String fileName )
	{
		long FileLenght = faceLenghtMap.get( faceid );
		if ( file.length( ) == FileLenght )
		{
			try
			{
				ZipUtil.UnZipFolder( file.getAbsolutePath( ) , savePath );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
			sendMessage( FACE_DOWLOAD_SUCCESS , faceid , 0 , savePath + fileName );
		}
		else
		{
			sendMessage( UNZIPFOLDER_FAIL , faceid , 0 , savePath + fileName );
		}
	}

	private void sendMessage( int what , int arg1 , int arg2 , Object obj )
	{
		Message msg = mMainHandler.obtainMessage( );
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		msg.what = what;
		msg.obj = obj;
		mMainHandler.sendMessage( msg );
	}

	Runnable runnable = new Runnable( )
	{
		@Override
		public void run( )
		{
			int delfaceSize = getDelFaceSize( );
			if ( !FaceCenterModel.upMyfaceViewPrecentMap.isEmpty( ) && delfaceSize > 0 )
			{
				updatePrecent( FaceCenterModel.upMyfaceViewPrecentMap );
			}
			else
			{
				resetFailList( );
				mMainHandler.removeCallbacks( this );
			}
		}
	};

	private void updatePrecent( Map< Face , Integer > map )
	{
		int position = 0;
		Face face = null;
		Iterator< Face > keys = map.keySet( ).iterator( );
		while ( keys.hasNext( ) )
		{
			Face key = keys.next( );
			if ( key != null )
			{
				for ( int i = 0 ; i < FaceCenterModel.delFace.size( ) ; i++ )
				{
					if ( FaceCenterModel.delFace.get( i ).getFaceid( ) == key.getFaceid( ) )
					{
						position = i;
						int value = map.get( key );
						face = FaceCenterModel.delFace.get( i );
						FaceCenterModel.delFace.get( i ).setPercent( value );
						sendMessage( UPDATE_PRECENT , position , 0 , face );
						break;
					}
				}
			}
		}
		resetFailList( );
		mMainHandler.postDelayed( runnable , 1000 ); // 在这里实现每秒执行一次
	}

	/**
	 * 如果表情下载失败，从List里移除该表情 并将进度置为零
	 */
	private void resetFailList( )
	{
		if ( !FaceCenterModel.upMyfaceViewFailList.isEmpty( ) )
		{
			removeFailElement( FaceCenterModel.upMyfaceViewFailList , false );
		}
	}

	public void removeFailElement( ArrayList< Face > list , Boolean isDetail )
	{
		Iterator< Face > it = list.iterator( );
		while ( it.hasNext( ) )
		{
			Face key = it.next( );
			for ( Face face : FaceCenterModel.delFace )
			{
				if ( key.getFaceid( ) == face.getFaceid( ) )
				{
					face.setPercent( 0 );
					mAdapter.notifyDataSetChanged( );
					it.remove( );
			}}}
	}

	/**
	 * 获取已删除列表的大小 ，不包含提示语
	 *
	 * @return
	 */
	private int getDelFaceSize( )
	{
		int delSize = FaceCenterModel.delFace.size( );
		for ( Face face : FaceCenterModel.delFace )
		{
			if ( face.getFaceid( ) == delFace_NoData_Flag )
			{
				delSize = delSize - 1;
				continue;
			}
		}
		return delSize;
	}

	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
		mMainHandler.removeCallbacks( runnable ); // 停止
	}

	private void displayProgress( ViewHolder viewHolder , Face face )
	{
		viewHolder.buttonLayout.setVisibility( View.VISIBLE );
		viewHolder.progress_text.setVisibility( View.GONE );
		viewHolder.progressBar.setVisibility( View.VISIBLE );
		viewHolder.progressBar.setMax( 100 );
		viewHolder.progressBar.setProgress( face.getPercent( ) );
		viewHolder.buttonLayout.setClickable( false );
		viewHolder.buttonLayout.setBackgroundResource( 0 );
	}

	private void displayWidget( ViewHolder viewHolder , View convertView )
	{
		viewHolder.noFace.setVisibility( View.VISIBLE );
		viewHolder.buttonLayout.setVisibility( View.GONE );
		viewHolder.faceIcon.setVisibility( View.GONE );
		viewHolder.faceName.setVisibility( View.GONE );
		viewHolder.progressBar.setVisibility( View.GONE );
		convertView.setClickable( false );
	}

	// 递归删除文件及文件夹
	public static void deleteSDCardFolder( File dir )
	{
		if ( dir.isFile( ) )
		{
			dir.delete( );
			return;
		}

		File to = new File( dir.getAbsolutePath( ) + System.currentTimeMillis( ) );
		dir.renameTo( to );

		if ( to.isDirectory( ) )
		{
			String[ ] children = to.list( );
			for ( int i = 0 ; i < children.length ; i++ )
			{
				File temp = new File( to , children[ i ] );
				if ( temp.isDirectory( ) )
				{
					deleteSDCardFolder( temp );
				}
				else
				{
					boolean b = temp.delete( );
					if ( b == false )
					{
						CommonFunction.log( "" , "--->DELETE FAIL" );
					}
				}
			}
			to.delete( );
		}
	}

	/**
	 * 点击删除按钮，删除表情
	 *
	 * @param faceId
	 * @param myface
	 */
	private void clickDelBtn( String faceId , Face myface )
	{
		// 删除表情文件夹
		String path = CommonFunction.getFacePath( faceId );
		File file = new File( path );
		deleteSDCardFolder( file );
		// 删除缓存
		String filename = faceId + ".face";
		String cacheDir = CommonFunction.getSDPath( ) + "/facecache/" + filename;
		File cacheFile = new File( cacheDir );
		deleteSDCardFolder( cacheFile );

		FaceMainActivity.isDownloadFace = true;


		int id = Integer.parseInt( faceId );
//		StatisticsApi.statisticEventRemoveFace( mContext , id );// 上报移除已下载表情事件

		sendMessage( DELETE_FACE_REFRESH , 0 , 0 , myface );
	}

	/**
	 * 去重
	 * @param list
	 * @return
	 */
	@SuppressWarnings ( { "rawtypes" , "unchecked" } )
	public ArrayList removeDuplicateWithOrder(List list) {
		Set set = new HashSet();
		ArrayList newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        return newList;
    }

	@Override
	protected void onStop( )
	{
		super.onStop( );

		saveDataCache( );
		FaceManager.resetOtherFace( );// 初始化动态表情
		sendBroadcast( new Intent( ).setAction( FaceManager.FACE_INIT_ACTION ) );
	}

	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			activityFinish( );
		}
		return super.onKeyDown( keyCode , event );
	}

	private void activityFinish( )
	{
		Intent intent = new Intent( );
		intent.putExtra( "isAddordelFace" , isAddordelFace );
		OwnFaceActivity.this.setResult( RESULT_OK , intent );
		OwnFaceActivity.this.finish( );
	}

	// 聊天面板的表情为空时，排序按钮为灰色不可点击
	private void displaySortBtn( )
	{
		runOnUiThread( new Runnable( )
		{
			public void run( )
			{
				if ( FaceCenterModel.ownFace != null && FaceCenterModel.ownFace.size( ) > 0 )
				{
					for ( Face face : FaceCenterModel.ownFace )
					{
						if ( FaceCenterModel.ownFace.size( ) == 1
								&& face.getFaceid( ) == ownFace_NoData_Flag )
						{
							title_right.setClickable( false );
							title_right.setOnClickListener( null );
							title_right
									.setTextColor( getResources( ).getColor( R.color.title_text ) );
						}
						else
						{
							title_right.setClickable( true );
							title_right.setOnClickListener( OwnFaceActivity.this );
							title_right.setTextColor( getResources( ).getColor( R.color.title_text ) );
						}

						title_right.invalidate( );
					}
				}
			}
		} );
	}

	private OnScrollListener mListOnScrollListener = new OnScrollListener( )
	{
		@Override
		public void onScrollStateChanged( AbsListView view , int scrollState )
		{
		}

		@Override
		public void onScroll( AbsListView view , int firstVisibleItem , int visibleItemCount ,
				int totalItemCount )
		{
			displaySortBtn( );
		}
	};

	static class ViewHolder
	{
		ImageView faceIcon;
		RelativeLayout buttonLayout;
		TextView faceName;
		TextView progress_text;
		TextView noFace;
		TextProgressBar progressBar;
	}
}
