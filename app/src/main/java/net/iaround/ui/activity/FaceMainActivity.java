
package net.iaround.ui.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.model.entity.Face;
import net.iaround.model.entity.FaceAd;
import net.iaround.model.entity.FaceCenterListBean;
import net.iaround.model.entity.FaceCenterModel;
import net.iaround.model.entity.FaceCenterModel.FaceCenterReqTypes;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.FaceManager.FaceLogoIcon;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.ZipUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.tools.store.LightTimer;
import net.iaround.ui.view.face.FaceAdViewPager;
import net.iaround.ui.view.face.TextProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class FaceMainActivity extends BaseFragmentActivity implements OnTouchListener , OnClickListener
{
	private Dialog pd;
	private String Uid;
	private Toast toast = null;
	private long FACE_DESCRIBE;// 请求表情描述的flag

	private LightTimer mAdBannerTimer;
	private FaceAdViewPager mViewFlow;
	private RelativeLayout mTitleLayout;
	private static FaceAdapter mFaceAdapter;
	//标题栏
	private TextView mTitleName , mTitleRight;
	private ImageView mTitleBack;
	private FrameLayout flLeft;

	private PullToRefreshListView mPullListView;
	public static boolean isDownloadFace = false;

	private final int UPDATE_PRECENT = 100;// 更新进度条
	private final int GET_FACES = 200;// 获取表情数据
	private final int GET_FACES_FAIL = 300;// 获取数据失败
	private final int UNZIPFOLDER_FAIL = 400;// 表情解压失败
	private final int BUY_FACE_FAIL = 500;// 购买表情失败
	private final int HANDLE_DOWLOAD_FAIL = 600;// 表情下载失败
	private final int BUY_FACE_SUCCESS = 700;// 购买表情成功
	private final int HANDLE_DOWLOAD_SUCCESS = 800;// 表情安装完成
	private final int HANDLE_DOWLOAD_PROGRESS = 900;// 更新表情下载进度条

	public static ArrayList<Face> buyFace = new ArrayList< Face >( );
	public static ArrayList< Face > mFaces = new ArrayList< Face >( );
	public static ArrayList< Integer > clickFace = new ArrayList< Integer >( );
	private HashMap< Long , Face > faceMap = new HashMap< Long , Face >( );// 保存购买的表情的
	private HashMap< String , ViewHolder > viewHolderMap = new HashMap< String , ViewHolder >( );
	private HashMap< Long , Integer > flagMap = new HashMap< Long , Integer >( );// 请求描述的表情对应着flag
	private HashMap< Integer , String > resultMap = new HashMap< Integer , String >( );// 表情的ID对应着描述
	private HashMap< Integer , Long > faceLenghtMap = new HashMap< Integer , Long >( );// 表情的压缩包大小

	public static void launch( Context context )
	{
		Intent i = new Intent( context , FaceMainActivity.class );
		context.startActivity( i );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_face_main);

		initView( );
		initAdapter( );
		setListener( );
		displayMyfaceBtn( false );
		performPulling( );
		run( );
	}

	public void initView( )
	{
		mTitleLayout = ( RelativeLayout ) findViewById( R.id.title_layout );
		mTitleName = ( TextView ) findViewById( R.id.tv_title );
		mTitleRight = ( TextView ) findViewById( R.id.tv_right );
		mTitleBack = ( ImageView ) findViewById( R.id.iv_left );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		mTitleRight.setText( R.string.my_face );
		mTitleName.setText( R.string.face_center );
		mTitleBack.setVisibility(View.VISIBLE);
		mTitleBack.setImageResource(R.drawable.title_back);

		mPullListView = (PullToRefreshListView) findViewById( R.id.pullView );
		mPullListView.setMode( Mode.PULL_FROM_START );
		mPullListView.getRefreshableView( ).setDividerHeight( 0 );
		mPullListView.getRefreshableView( ).setCacheColorHint( Color.TRANSPARENT );
		// 广告
		mViewFlow = new FaceAdViewPager( this );
		int layoutWidth = getResources( ).getDisplayMetrics( ).widthPixels;
		mViewFlow.setLayoutParams( new ListView.LayoutParams( layoutWidth , layoutWidth / 2 ) );
		mPullListView.getRefreshableView( ).addHeaderView( mViewFlow );
		// 最底下 “更多表情”的TextView
		TextView footView = new TextView( this );
		footView.setLayoutParams( new ListView.LayoutParams( layoutWidth , 100 ) );
		footView.setGravity( Gravity.CENTER );
		footView.setTextColor( getResources( ).getColor( R.color.c_b28850 ) );
		footView.setText( R.string.face_more_title );
		mPullListView.getRefreshableView( ).addFooterView( footView );
	}

	private void setListener( )
	{
		flLeft.setOnClickListener(this);
		mTitleBack.setOnClickListener(this);
		mTitleRight.setOnClickListener(this);
		findViewById(R.id.fl_back).setOnClickListener(this);

		mPullListView.setOnItemClickListener( new OnItemClickListener( )
		{
			@Override
			public void onItemClick( AdapterView< ? > parent , View view , int position , long id )
			{
				position -= 2;
				Face clickFace = mFaceAdapter.getItem( position );
				if ( clickFace != null )
				{
					if ( clickFace.getNewflag( ) == 1 )
					{
						// 点击之后，该表情状态为查看过
						clickFace.setNewflag( 0 );
						mFaceAdapter.notifyDataSetChanged( );
					}
					FaceDetailActivity.launchForResult( FaceMainActivity.this ,
							clickFace.getFaceid( ) );
				}
			}
		} );
	}

	/**
	 * 初始化数据
	 *
	 * @param ads
	 * @param faces
	 */
	private void initNetData(ArrayList<FaceAd> ads , ArrayList< Face > faces )
	{
		initAdData( ads );
		initFaceData( faces );
	}

	/**
	 * @Title: initFaceData
	 * @Description: 初始化表情数据并显示
	 * @param faces
	 *            表情列表
	 */
	private void initFaceData( ArrayList< Face > faces )
	{
		FaceManager.getInstance( mContext ).setAllFace( faces );
		if ( mFaces.size( ) <= 0 ){
			mFaces.addAll( faces );
		}else{
			if ( mFaces.size( ) > faces.size( ) ){
				delOverFace( faces , mFaces );// 移除已下架表情
			}else if ( mFaces.size( ) < faces.size( ) ){
				addNewFace( faces , mFaces );// 添加新上架表情
			}

			updateFreeType( faces , mFaces );
			displayNewFlag( );
		}
		displayMyfaceBtn( true );

	}


	private void initAdapter( )
	{
		mFaceAdapter = new FaceAdapter( );
		mPullListView.getRefreshableView( ).setAdapter( mFaceAdapter );
		mPullListView.setOnRefreshListener( new OnRefreshListener< ListView >( )
		{
			@Override
			public void onRefresh( PullToRefreshBase< ListView > refreshView )
			{
				requestData( );
			}
		} );
	}

	protected void requestData( )
	{
		Uid = Common.getInstance().getUid( ) ;
		String data = SharedPreferenceCache.getInstance( mContext ).getString(
				"faceCenterList" + String.valueOf(Uid) );

		if ( data != null && !data.isEmpty( ) )
		{
			// 获取缓存数据展示
			FaceCenterListBean bean = GsonUtil.getInstance( ).getServerBean( data ,
					FaceCenterListBean.class );
			initNetData( bean.ads , bean.faces );
		}

		long flag = FaceCenterModel.getInstance( FaceMainActivity.this ).getFaceCenterData(
				FaceMainActivity.this , 1 , 20 , callBack );
		if ( flag == -1 )
		{
			stopPulling( );
			CommonFunction.toastMsg( mContext , R.string.group_init_fail );
		}
	}



	/**
	 * 初始化广告数据
	 */
	private void initAdData( List< FaceAd > ads )
	{
		if ( ads == null || ads.size( ) == 0 )
		{
			return;
		}

		mViewFlow.setData( ads );
		mViewFlow.setOnItemClickListener( avBannerOnClickListener );

		if ( mAdBannerTimer != null )
		{
			mAdBannerTimer.stop( );
			mAdBannerTimer = null;
		}

		mAdBannerTimer = new LightTimer( )
		{
			@Override
			public void run( LightTimer timer )
			{
				mViewFlow.showNext( );
			}
		};
		mAdBannerTimer.startTimerDelay( 5000 , 5000 );
	}

    private HttpCallBack callBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            HashMap< String , Object > response = FaceCenterModel.getInstance( FaceMainActivity.this ).getRes( result ,
                    flag );
            if ( response.isEmpty( ) )
            {
                return;
            }
            FaceCenterReqTypes reqType = ( FaceCenterReqTypes ) ( response.get( "reqType" ) );
            if ( ( Integer ) ( response.get( "status" ) ) != 200 )
            {
                // 网络访问出错了
                if ( reqType == FaceCenterReqTypes.FaceCenterMainData )
                {
                    mMainHandler.sendMessage( mMainHandler.obtainMessage( GET_FACES_FAIL , result ) );
                }
                else if ( reqType == FaceCenterReqTypes.BuyFaceData )
                {
                    Message msg = new Message( );
                    msg.what = BUY_FACE_FAIL;
                    Bundle bundle = new Bundle( );
                    bundle.putLong( "flag" , flag );
                    bundle.putString( "result" , result );
                    msg.setData( bundle );
                    mMainHandler.sendMessage( msg );
                }
            }
            else
            {
                if ( reqType == FaceCenterReqTypes.FaceCenterMainData )
                {
                    mMainHandler.sendMessage( mMainHandler.obtainMessage( GET_FACES , response ) );
                }
                else if ( reqType == FaceCenterReqTypes.BuyFaceData )
                {
                    sendMessage( BUY_FACE_SUCCESS , 0 , 0 , flag );
                }
                else if ( flag == FACE_DESCRIBE )
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
        }

        @Override
        public void onGeneralError(int e, long flag) {
            FaceCenterModel.FaceCenterReqTypes reqType = FaceCenterModel.getInstance( FaceMainActivity.this ).getReqType( flag );
            if ( reqType == FaceCenterReqTypes.FaceCenterMainData )
            {
                mMainHandler.sendEmptyMessage( GET_FACES_FAIL );
            }
        }
    };

	private Handler mMainHandler = new Handler( Looper.getMainLooper( ) )
	{
		@SuppressWarnings ( "unchecked" )
		@Override
		public void handleMessage( Message msg )
		{
			HashMap< String , Object > response = null;

			if ( pd != null && pd.isShowing( ) )
			{
				pd.dismiss( );
			}
			switch ( msg.what )
			{
				case GET_FACES :// 获取服务端数据
					response = ( HashMap< String , Object > ) msg.obj;
					getNetData( response );
					break;

				case GET_FACES_FAIL :// 获取服务端数据失败
					initNetDataFail( ( String ) msg.obj );
					break;

				case BUY_FACE_SUCCESS :// 成功购买表情
					long buySuccessFlag = ( Long ) msg.obj;
					buySuccess( buySuccessFlag );
					break;

				case BUY_FACE_FAIL :// 购买表情失败
					Bundle bundle = msg.getData( );
					long buyFailflag = bundle.getLong( "flag" );
					String result = bundle.getString( "result" );
					buyFail( result , buyFailflag );
					break;

				case HANDLE_DOWLOAD_FAIL :// 下載失敗
					unzipfolderFail( msg.arg1 , ( String ) msg.obj , 2 );
					break;

				case UNZIPFOLDER_FAIL :// 解壓失敗
					unzipfolderFail( msg.arg1 , ( String ) msg.obj , 1 );
					break;

				case HANDLE_DOWLOAD_SUCCESS :// 下载并解压成功
					mFaceAdapter.notifyDataSetChanged( );
					saveDescribeToFile( ( String ) msg.obj , msg.arg1 , resultMap.get( msg.arg1 ) );
					delFailFile( ( String ) msg.obj );// 删除缓存文件
					resetFace( msg.arg1 );// 更改表情后缀名，初始化动态表情
					break;

				case UPDATE_PRECENT : // 在详情页或我的表情页下载表情，刷新本页面
					handleUpdateProgress1( msg.arg1 , ( Face ) msg.obj );
					break;

				case HANDLE_DOWLOAD_PROGRESS :// 更新下载进度条
					handleUpdateProgress( msg.arg1 , ( Double ) msg.obj );
					break;

			}
		}
	};

	/**
	 * 获取服务端数据并缓存
	 *
	 * @param response
	 */
	@SuppressWarnings ( "unchecked" )
	private void getNetData( HashMap< String , Object > response )
	{
		if ( response != null )
		{
			initNetData( ( ArrayList< FaceAd > ) response.get( "ads" ) ,
					( ArrayList< Face > ) response.get( "faces" ) );

			cacheData( response );// 将表情与广告数据缓存
		}
		stopPulling( );
		mFaceAdapter.notifyDataSetChanged( );
	}

	/**
	 * 将表情描述的内容写成文件到对应的表情文件夹下边
	 *
	 * @param filePath
	 * @param faceId
	 */
	public static void saveDescribeToFile( String filePath , int faceId , String descriseResult )
	{
		String path = "";
		int index = filePath.lastIndexOf( "." ) - String.valueOf( faceId ).length( );
		path = filePath.substring( 0 , index );
		String saveFilePath = path + "/" + faceId + ".txt";

		if ( descriseResult != null && !descriseResult.equals( "" ) && !descriseResult.contains( "null" )  )
		{
			writeFileToSD( descriseResult , saveFilePath );
		}
	}


	// 写数据到SD中的文件
	public static void writeFileToSD( String str , String fileName )
	{
		String sdStatus = Environment.getExternalStorageState( );
		if ( !sdStatus.equals( Environment.MEDIA_MOUNTED ) )
		{
			return;
		}
		try
		{
			File file = new File( fileName );
			if ( !file.exists( ) )
			{
				file.createNewFile( );
			}
			FileOutputStream stream = new FileOutputStream( file );
			byte[ ] buf = str.getBytes( );
			stream.write( buf );
			stream.close( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * 在我的表情页或表情详情下载表情，刷新本页面
	 *
	 * @param position
	 * @param face
	 */
	private void handleUpdateProgress1( int position , Face face )
	{
		mFaceAdapter.updateView( position , face );
		if ( face.getPercent( ) == 100 )
		{
			mFaceAdapter.notifyDataSetChanged( );
			face.setPercent( 0 );
			resetMap( FaceCenterModel.upFacemainViewPrecentMap , face );
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
		int precent = ( int ) ( dPrecent * 100 );
		int position = 0;
		Face dface = null;
		for ( int i = 0 ; i < mFaces.size( ) ; i++ )
		{
			if ( faceId == mFaces.get( i ).getFaceid( ) )
			{
				position = i;
				mFaces.get( i ).setPercent( precent );
				dface = mFaces.get( i );
				break;
			}
		}
		mFaceAdapter.updateView( position , dface );
		FaceCenterModel.upMyfaceViewPrecentMap.put( dface , precent );
		FaceCenterModel.upFaceDetailPrecentMap.put( dface , precent );
	}


	/**
	 * 表情安装失败 what 1代表解压失败 2 代表下载失败
	 *
	 * @param fileName
	 */
	private void unzipfolderFail( int faceId , String fileName , int what )
	{
		for ( int i = 0 ; i < mFaces.size( ) ; i++ )
		{
			if ( faceId == mFaces.get( i ).getFaceid( ) )
			{
				mFaces.get( i ).setPercent( 0 );
				FaceCenterModel.upMyfaceViewFailList.add( mFaces.get( i ) );
				FaceCenterModel.upFaceDetailFailList.add( mFaces.get( i ) );
				resetMap( FaceCenterModel.upMyfaceViewPrecentMap , mFaces.get( i ) );
                resetMap( FaceCenterModel.upFaceDetailPrecentMap , mFaces.get( i ) );
				if ( what == 1 )
				{
					showTextToast( "(" + mFaces.get( i ).getTitle( ) + ")"
							+ getString( R.string.unZipfolder_fail ) );
				}
				else
				{
					showTextToast( "(" + mFaces.get( i ).getTitle( ) + ")"
							+ getString( R.string.download_fail ) );
				}
				break;
			}
		}

		mFaceAdapter.notifyDataSetChanged( );
		delFailFile( fileName );// 删除不完整压缩包
	}

	/**
	 * 从Map里移除某项
	 *
	 * @param map
	 * @param face
	 */
	public static void resetMap(Map< Face , Integer > map , Face face )
	{
		if ( map != null && map.size( ) > 0 )
		{
			Iterator< Entry< Face , Integer >> it = map.entrySet( ).iterator( );
			while ( it.hasNext( ) )
			{
				Entry< Face , Integer > entry = it.next( );
				Face key = entry.getKey( );
				if ( key != null && key.getFaceid( ) == face.getFaceid( ) )
				{
					it.remove( );
				}
			}
		}
	}

	/**
	 * 更改表情后缀名，初始化动态表情
	 *
	 * @param faceid
	 */
	private void resetFace( int faceid )
	{
		for ( int i = 0 ; i < mFaces.size( ) ; i++ )
		{
			if ( faceid == mFaces.get( i ).getFaceid( ) )
			{
				mFaces.get( i ).setPercent( 0 );
				break;
			}
		}

		String id = String.valueOf( faceid );
		String path = PathUtil.getFaceDir( ) + Uid + "//" + id;
		File file = new File( path );
		if ( file.exists( ) )
		{
			CommonFunction.reFaceFileName( path );// 更改文件后缀名
		}

		FaceManager.resetOtherFace( );// 初始化动态表情
		sendBroadcast( new Intent( ).setAction( FaceManager.FACE_INIT_ACTION ) );
	}

	/**
	 * 缓存数据
	 *
	 * @param response
	 */
	@SuppressWarnings ( "unchecked" )
	private void cacheData( HashMap< String , Object > response )
	{
		ArrayList< Face > faces = ( ArrayList< Face > ) response.get( "faces" );
		ArrayList< FaceAd > ads = ( ArrayList< FaceAd > ) response.get( "ads" );
		FaceCenterListBean bean = new FaceCenterListBean( );
		bean.faces = faces;
		bean.ads = ads;
		String faceCenterList = GsonUtil.getInstance( ).getStringFromJsonObject( bean );
		SharedPreferenceCache.getInstance( mContext ).putString( "faceCenterList" + String.valueOf(Uid ) ,
				faceCenterList );

	}

	/**
	 * 移除已下架表情
	 *
	 * @param faces
	 * @param mFaces
	 */
	private void delOverFace( ArrayList< Face > faces , ArrayList< Face > mFaces )
	{
		ArrayList< Face > temp = new ArrayList< Face >( );
		ArrayList< Face > temp1 = new ArrayList< Face >( );

		for ( Face mface : mFaces ){
		  for ( Face face : faces ){
			if ( face.getFaceid( ) == mface.getFaceid( ) ){
				temp.add( mface );
				break;
			}
		  }
		}
		temp1.addAll( mFaces );
		temp1.removeAll( temp );
		mFaces.removeAll( temp1 );
	}

	/**
	 * 添加新上架表情
	 *
	 * @param faces
	 * @param mFaces
	 */
	private void addNewFace( ArrayList< Face > faces , ArrayList< Face > mFaces )
	{
		ArrayList< Face > temp = new ArrayList< Face >( );
		for ( Face face : faces ){
		  for ( Face mface : mFaces ){
			 if ( face.getFaceid( ) == mface.getFaceid( ) ){
				temp.add( face );
				break;
			}
		}}

		ArrayList< Face > temp1 = new ArrayList< Face >( );
		temp1.addAll( faces );
		temp1.removeAll( temp );
		mFaces.addAll( 0 , temp1 );
	}

	//更新表情信息状态
	private void updateFreeType(ArrayList<Face> faces, ArrayList<Face> mFaces) {
		for (Face face : faces) {
			for (Face mface : mFaces) {
				if (face.getFaceid() == mface.getFaceid()) {
					mface.setFeetype(face.getFeetype());
					mface.setDynamic(face.getDynamic());
					mface.setIcon(face.getIcon());
					mface.setTitle(face.getTitle());
					mface.setNewflag(face.getNewflag());
					mface.setOwn(face.getOwn());
					mface.setDownUrl(face.getDownUrl());
					mface.setEndTime(face.getEndTime());
					mface.setGoldNum(face.getGoldNum());
					mface.setName(face.getName());
					mface.setVipgoldnum(face.getVipgoldnum());
				}
			}
		}
	}

	private OnClickListener avBannerOnClickListener = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			FaceAd ad = ( FaceAd ) v.getTag( );
			if ( ad.getType( ) == FaceAd.TYPE_FACE )
			{
				int faceId = 0;
				try
				{
					faceId = Integer.valueOf( ad.getFaceId( ) );
				}
				catch ( NumberFormatException e )
				{
					faceId = 0;
				}

				if ( faceId > 0 )
				{
					FaceDetailActivity.launch( FaceMainActivity.this , faceId );
				}
			}
			else if ( ad.getType( ) == FaceAd.TYPE_WEBVIEW_URL )
			{
				try
				{
					Uri uri = Uri.parse( ad.getJumpUrl( ) );
					Intent i = new Intent( mContext , WebViewAvtivity.class );
					i.putExtra( WebViewAvtivity.WEBVIEW_TITLE , ad.getName( ) );
					i.putExtra( WebViewAvtivity.WEBVIEW_URL , uri.toString( ) );
					startActivity( i );
				}
				catch ( Exception e )
				{
					Toast.makeText( FaceMainActivity.this ,
							R.string.game_center_ad_game_link_error , Toast.LENGTH_SHORT ).show( );
				}
			}
			else if ( ad.getType( ) == FaceAd.TYPE_URL )
			{
				try
				{
					Uri uri = Uri.parse( ad.getJumpUrl( ) );
					Intent intent = new Intent( Intent.ACTION_VIEW );
					intent.setData( uri );
					startActivity( intent );
				}
				catch ( Exception e )
				{
					Toast.makeText( FaceMainActivity.this ,
							R.string.game_center_ad_game_link_error , Toast.LENGTH_SHORT ).show( );
				}
			}
			else if ( ad.getType( ) == FaceAd.TYPE_FACE_LIST )
			{
			}
		}
	};

	@Override
	protected void onResume( )
	{
		super.onResume( );

		if ( isDownloadFace == true )
		{
			performPulling( );
			isDownloadFace = false;
		}

		if ( buyFace.size( ) > 0 )
		{
			for ( Face buyface : buyFace )
				for ( Face mface : mFaces )
					if ( buyface.getFaceid( ) == mface.getFaceid( ) )
					{
						mface.setOwn( 1 );
					}
			buyFace.clear( );
		}

		run( );
		displayNewFlag( );
		resetFailList( );
		mFaceAdapter.notifyDataSetChanged( );
	}


    @Override
	protected void onDestroy( )
	{
		super.onDestroy( );

		stopPulling( );
		if ( pd != null )
		{
			pd.dismiss( );
			pd = null;
		}
	}

	/**
	 * 启动执行runnable
	 */
	private void run( )
	{
		if ( !FaceCenterModel.upFacemainViewPrecentMap.isEmpty( ) )
		{
			mMainHandler.postDelayed( runnable , 0 );
		}
	}

	/**
	 * 购买失败
	 *
	 * @param result
	 */
	public void buyFail( String result , Long flag )
	{
		final Face face = faceMap.get( flag );
		if ( !TextUtils.isEmpty( result ) )
		{
			JSONObject obj = null;
			try
			{
				obj = new JSONObject( result );
				int status = obj.optInt( "status" );
				int error = obj.optInt( "error" );
				if ( status == -400 )
				{
					error = 4000;
				}
				if ( error == 4000 )
				{
					if ( face.getCurrencytype( ) == 1 )
					{
						buyFail_goldNotEnougn( );// 金币不足
					}
					else if ( face.getCurrencytype( ) == 2 )
					{
						DialogUtil
								.showDiamondDialog( FaceMainActivity.this , "" );// 获取钻石
					}
					return;
				}
				else if ( error == 5954 )
				{
					BuyFail_notJoinActivities( face );// 未参加活动
				}
				else if ( error == 5930 )
				{
					DialogUtil.showDiamondDialog( FaceMainActivity.this , "" );// 获取钻石
					return;
				}
				else if ( error == 5953 )
				{
					CommonFunction.log( "" , "--->5953" + getString( R.string.face_vip_can_used ) );
					return;
				}
				else if ( error == 5952 )
				{
					ViewHolder viewHolder = viewHolderMap.get( "viewholder" );
					Download( face , viewHolder );
					CommonFunction.log( "" , "--->5952" );
					return;
				}
				else
				{
					ErrorCode.showError( FaceMainActivity.this , result );
				}
			}
			catch ( JSONException e )
			{
			}
		}
		else
		{
			Toast.makeText( FaceMainActivity.this , R.string.start_reconnect , Toast.LENGTH_SHORT )
					.show( );
		}
	}

	/**
	 * 金币不足
	 */
	private void buyFail_goldNotEnougn( )
	{
//		DialogUtil.showTwoButtonDialog( FaceMainActivity.this ,
//				ErrorCode.getErrorMessageId( ErrorCode.E_4000 ) ,
//				getString( R.string.face_gold_not_enough ) , getString( R.string.cancel ) ,
//				getString( R.string.diamond_for_gold_ok ) , null , new OnClickListener( )
//				{
//					@Override
//					public void onClick( View v )
//					{
////						FragmentPayBuyGlod.jumpPayBuyGlodActivity(mContext);
//					}
//
//				} );
		Toast.makeText(FaceMainActivity.this, "金币不足", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 未参加活动
	 *
	 * @param face
	 */
	private void BuyFail_notJoinActivities( final Face face )
	{
		DialogUtil.showTwoButtonDialog( FaceMainActivity.this , getString( R.string.dialog_title ) ,
				getString( R.string.face_not_participate_active ) , getString( R.string.ok ) ,
				getString( R.string.face_participate_active ) , null , new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						try
						{
							if ( face != null )
							{
								skipWebView( face );
							}
						}
						catch ( Exception e )
						{
							Toast.makeText( FaceMainActivity.this ,
									R.string.game_center_ad_game_link_error , Toast.LENGTH_SHORT )
									.show( );
						}
					}
				} );
		return;
	}

	/**
	 * 跳转到webview
	 *
	 * @param face
	 */
	private void skipWebView( Face face )
	{
		switch ( face.getOpenType( ) )
		{
			case FaceAd.TYPE_WEBVIEW_URL :
				Uri uri = Uri.parse( face.getActiveurl( ) );
				Intent i = new Intent( mContext , WebViewAvtivity.class );
				i.putExtra( WebViewAvtivity.WEBVIEW_TITLE , face.getHeadcontent( ) );
				i.putExtra( WebViewAvtivity.WEBVIEW_URL , uri.toString( ) );
				startActivity( i );
				break;
			case FaceAd.TYPE_URL :
				Uri uri2 = Uri.parse( face.getActiveurl( ) );
				Intent intent = new Intent( Intent.ACTION_VIEW );
				intent.setData( uri2 );
				startActivity( intent );
				break;
			case FaceAd.TYPE_FACE :
				Uri uri3 = Uri.parse( face.getActiveurl( ) );
				Intent i1 = new Intent( mContext , WebViewAvtivity.class );
				i1.putExtra( WebViewAvtivity.WEBVIEW_TITLE , face.getHeadcontent( ) );
				i1.putExtra( WebViewAvtivity.WEBVIEW_URL , uri3.toString( ) );
				startActivity( i1 );
				break;
		}
	}

	/** 购买表情并下载 */
	protected void buySuccess( long flag )
	{
		Face downloadFace = faceMap.get( flag );
		downloadFace.setOwn( 1 );
		ViewHolder viewHolder = viewHolderMap.get( "viewholder" );
		// 移除掉原来过期的表情
		FaceLogoIcon faceLogoIcon = null;
		for ( FaceLogoIcon logoIcon : FaceManager.mOwnGifFaces )
		{
			if ( logoIcon.pkgId == downloadFace.getFaceid( ) )
			{
				faceLogoIcon = logoIcon;
				break;
			}
		}
		if ( faceLogoIcon != null )
		{
			FaceManager.mOwnGifFaces.remove( faceLogoIcon );
		}

		FaceManager.mOwnGifFaces.add( new FaceLogoIcon( downloadFace.getFaceid( ) ) );
		Download( downloadFace , viewHolder );
	}

	@Override
	protected void onStart( )
	{
		if ( mAdBannerTimer != null )
		{
			mAdBannerTimer.startTimerDelay( 5000 , 5000 );
		}
		super.onStart( );

	}

	@Override
	protected void onStop( )
	{
		if ( mAdBannerTimer != null )
		{
			mAdBannerTimer.stop( );
		}
		mMainHandler.removeCallbacks( runnable ); // 停止
		super.onStop( );

	}

	private class FaceAdapter extends BaseAdapter
	{
		@Override
		public int getCount( )
		{
			if ( mFaces != null )
			{
				return mFaces.size( );
			}
			return 0;
		}

		@Override
		public Face getItem( int position )
		{
			if ( mFaces != null && position < mFaces.size( ) )
			{
				return mFaces.get( position );
			}
			return null;
		}

		public void updateView( int position , Face face )
		{
			int visiblePos = mPullListView.getRefreshableView( ).getFirstVisiblePosition( );
			int offset = position - visiblePos + 2;

			// 只有在可见区域才更新
			if ( offset < 0 )
				return;

			View view = mPullListView.getRefreshableView( ).getChildAt( offset );
			if ( view == null )
			{
				return;
			}
			ViewHolder viewHolder = ( ViewHolder ) view.getTag( );
			displayProgress( viewHolder , face );
		}

		@Override
		public View getView( int position , View convertView , ViewGroup parent )
		{
			ViewHolder viewHolder = null;
			if ( convertView == null )
			{
				viewHolder = new ViewHolder( );
				convertView = LayoutInflater.from( FaceMainActivity.this ).inflate(
						R.layout.face_list_item , null );
				viewHolder.faceIcon = ( ImageView ) convertView.findViewById( R.id.icon_img );
				viewHolder.faceName = ( TextView ) convertView.findViewById( R.id.name );
				viewHolder.faceTag = ( TextView ) convertView.findViewById( R.id.face_tag );
				viewHolder.faceInfo = ( TextView ) convertView.findViewById( R.id.info );
				viewHolder.animationFace = ( TextView ) convertView
						.findViewById( R.id.animation_flag );
				viewHolder.newFace = ( ImageView ) convertView.findViewById( R.id.new_flag );
				viewHolder.vipFlag = ( TextView ) convertView.findViewById( R.id.vip_flag );

				viewHolder.DownloadLayout = ( RelativeLayout ) convertView
						.findViewById( R.id.progress_ly );
				viewHolder.progressTextView = ( TextView ) convertView
						.findViewById( R.id.progress_text );
				viewHolder.mTextProgressBar = ( TextProgressBar ) convertView
						.findViewById( R.id.progressBar );
				viewHolder.hasFace = ( ImageView ) convertView.findViewById( R.id.face_has_btn );

				viewHolder.DownloadLayout.setTag( viewHolder );
				convertView.setTag( viewHolder );
			}
			else
			{
				viewHolder = ( ViewHolder ) convertView.getTag( );
			}

			final Face face = getItem( position );
			if ( face != null )
			{
//				ImageViewUtil.getDefault( ).fadeInLoadImageInConvertView( face.getIcon( ) ,
//						viewHolder.faceIcon , R.drawable.default_pitcure_small ,
//						R.drawable.default_pitcure_small );
				GlideUtil.loadImage(BaseApplication.appContext,face.getIcon( ),viewHolder.faceIcon, R.drawable.default_pitcure_small, R.drawable.default_pitcure_small);
				viewHolder.faceName.getPaint( ).setFakeBoldText( false );
				viewHolder.faceName.setText( face.getTitle( ) );
				if ( !CommonFunction.isEmptyOrNullStr( face.getTagname( ) ) )
				{
					viewHolder.faceTag.setText( "(" + face.getTagname( ) + ")" );
					viewHolder.faceTag.setVisibility( View.VISIBLE );
				}
				else
				{
					viewHolder.faceTag.setVisibility( View.GONE );
				}
				viewHolder.faceInfo.setText( "" );
				viewHolder.faceInfo.setTextColor( Color.BLACK );
				viewHolder.faceInfo.setCompoundDrawables( null , null , null , null );
				viewHolder.faceInfo.setPadding( 0 , 0 , 0 , 0 );
				viewHolder.animationFace.setText( R.string.dynamic );
				viewHolder.mTextProgressBar
						.setTextSize( 13 * getResources( ).getDisplayMetrics( ).density );
				viewHolder.mTextProgressBar.setTextColor( getResources( ).getColor(
						R.color.c_cccccc ) );

				if ( face.getNewflag( ) == 1 )
				{
					viewHolder.newFace.setVisibility( View.VISIBLE );
				}
				else
				{
					viewHolder.newFace.setVisibility( View.GONE );
				}
				if ( face.getDynamic( ) == 1 )
				{// 动态图
					viewHolder.animationFace.setVisibility( View.VISIBLE );
				}
				else
				{
					viewHolder.animationFace.setVisibility( View.GONE );
				}

				// 是否拥有该表情
				int appStatus = checkFaceInstalled( face.getFaceid( ) );
				if ( appStatus == -1 )
				{
					if ( face.getPercent( ) > 0 )
					{
						displayProgress( viewHolder , face );
					}
					else
					{
						viewHolder.progressTextView.setVisibility( View.VISIBLE );
						viewHolder.progressTextView
								.setText( getString( R.string.game_center_task_download ) );
						viewHolder.mTextProgressBar.setVisibility( View.GONE );
						viewHolder.DownloadLayout.setClickable( true );
						viewHolder.hasFace.setVisibility( View.GONE );
						viewHolder.DownloadLayout
								.setBackgroundResource( R.drawable.face_download_btn );
					}
				}
				else if ( appStatus == 1 )
				{// 已安装
					viewHolder.DownloadLayout.setBackgroundResource( 0 );
					viewHolder.progressTextView.setText( "" );
					viewHolder.progressTextView.setBackgroundResource( 0 );
					viewHolder.hasFace.setVisibility( View.VISIBLE );
					viewHolder.mTextProgressBar.setVisibility( View.GONE );
					viewHolder.DownloadLayout.setClickable( false );
				}

				if ( face.getFeetype( ) == 2 )
				{// VIP特享
					viewHolder.vipFlag.setVisibility( View.VISIBLE );
					viewHolder.faceInfo.setVisibility( View.GONE );
				}
				else if ( face.getFeetype( ) == 1 )
				{// 免费
					viewHolder.faceInfo.setVisibility( View.VISIBLE );
					viewHolder.vipFlag.setVisibility( View.GONE );
					String freeStr = getString( R.string.face_price_neednt_gold_tip );
					String oldPriceStr = String.format(
							getDisplayUnitByType( face.getCurrencytype( ) ) , face.getGoldNum( ) );
					String finalText = freeStr + " " + oldPriceStr;
					SpannableString spanText = new SpannableString( finalText );
					spanText.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF9900" ) ) ,
							finalText.indexOf( freeStr ) , freeStr.length( ) ,
							SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );
					spanText.setSpan( new StrikethroughSpan( ) , finalText.indexOf( oldPriceStr ) ,
							spanText.length( ) , SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );

					viewHolder.faceInfo.setTextColor( Color.GRAY );
					viewHolder.faceInfo.setTextSize( 13 );
					viewHolder.faceInfo.setText( spanText );
				}
				else if ( face.getFeetype( ) == 3 )
				{
					// 收费
					viewHolder.faceInfo.setVisibility( View.VISIBLE );
					viewHolder.vipFlag.setVisibility( View.GONE );
					viewHolder.faceInfo.setTextSize( 13 );
					viewHolder.faceInfo.setTextColor( Color.parseColor( "#FF9900" ) );
					viewHolder.faceInfo.setText( String.format(
							getDisplayUnitByType( face.getCurrencytype( ) ) , face.getGoldNum( ) ) );
				}
				else if ( face.getFeetype( ) == 4 )
				{
					// 参加活动获得
					viewHolder.faceInfo.setVisibility( View.VISIBLE );
					viewHolder.vipFlag.setVisibility( View.GONE );
					viewHolder.faceInfo.setTextColor( Color.parseColor( "#FF9900" ) );
					viewHolder.faceInfo.setTextSize( 13 );
					viewHolder.faceInfo.setText( R.string.get_face_by_activite );
				}
				else if ( face.getFeetype( ) == 5 )
				{
					// 限时免费但过期
					viewHolder.faceInfo.setVisibility( View.VISIBLE );
					viewHolder.vipFlag.setVisibility( View.GONE );
					long curTime = System.currentTimeMillis( );
					if ( face.getEndTime( ) - curTime < 0 )
					{
						viewHolder.faceInfo.setTextSize( 13 );
						viewHolder.faceInfo.setTextColor( Color.parseColor( "#FF9900" ) );
						viewHolder.faceInfo
								.setText( String.format(
										getDisplayUnitByType( face.getCurrencytype( ) ) ,
										face.getGoldNum( ) ) );
					}
					else
					{
						// 限时免费
						viewHolder.faceInfo.setTextSize( 13 );
						viewHolder.faceInfo.setTextColor( Color.parseColor( "#FF9900" ) );
						viewHolder.faceInfo.setText( R.string.face_limit_free );
					}

				}
				else if ( face.getFeetype( ) == 6 )
				{
					// 打折
					viewHolder.faceInfo.setVisibility( View.VISIBLE );
					viewHolder.vipFlag.setVisibility( View.GONE );
					String priceText = String.format(
							getDisplayUnitByType( face.getCurrencytype( ) ) , face.getGoldNum( ) );
					String discountText = String
							.format( getDisplayUnitByType2( face.getCurrencytype( ) ) ,
									face.getOldgoldnum( ) );
					String finalText = priceText + " " + discountText;
					SpannableString spanText = new SpannableString( finalText );
					spanText.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF9900" ) ) , 0 ,
							finalText.indexOf( discountText ) ,
							SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );
					spanText.setSpan( new StrikethroughSpan( ) , priceText.length( ) + 1 ,
							spanText.length( ) , SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );

					viewHolder.faceInfo.setTextColor( Color.GRAY );
					viewHolder.faceInfo.setTextSize( 13 );
					viewHolder.faceInfo.setText( spanText );
				}

				viewHolder.DownloadLayout.setTag( viewHolder );
				// 如果未安装 可点击下载表情
				viewHolder.DownloadLayout.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						final ViewHolder viewHolder = ( ViewHolder ) v.getTag( );
						int appStatus = checkFaceInstalled( face.getFaceid( ) );

						if ( face.getPercent( ) > 0 || appStatus == 1 )
						{
							viewHolder.DownloadLayout.setClickable( false );
						}
						else
						{
							viewHolder.DownloadLayout.setClickable( true );
							DownLoadFace( viewHolder , face );
							viewHolderMap.put( "viewholder" , viewHolder );
						}
					}
				} );
			}

			return convertView;
		}

		@Override
		public long getItemId( int position )
		{
			return position;
		}
	}


	// 下载表情的回调函数
	private DownloadFileCallback callback = new DownloadFileCallback( )
	{
		// 下载进度条
		@Override
		public void onDownloadFileProgress( long lenghtOfFile , long LengthOfDownloaded , int flag )
		{
			faceLenghtMap.put( flag , lenghtOfFile );
			double dPrecent = ( double ) LengthOfDownloaded / lenghtOfFile;
			sendMessage( HANDLE_DOWLOAD_PROGRESS , flag , 0 , dPrecent );
		}

		// 下载完成
		@Override
		public void onDownloadFileFinish( int flag , String fileName , final String savePath )
		{
			// 下载完成后解压该表情包
			File dir = new File( savePath + fileName );
			if ( dir.exists( ) )
			{
				UnZipFolder( flag , dir , fileName , savePath );
			}
		}

		// 下载失败
		@Override
		public void onDownloadFileError( int flag , String fileName , String savePath )
		{
			sendMessage( HANDLE_DOWLOAD_FAIL , flag , 0 , savePath + fileName );
		}
	};


	private void sendMessage( int what , int arg1 , int arg2 , Object obj )
	{
		Message msg = mMainHandler.obtainMessage( );
		msg.what = what;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		msg.obj = obj;
		mMainHandler.sendMessage( msg );
	}

	/**
	 * 删除文件
	 *
	 * @param fileName
	 */
	public static void delFailFile( String fileName )
	{
		File file = new File( fileName );
		if ( file.exists( ) )
		{
			file.delete( );
		}
	}

	/**
	 * 解压表情文件包
	 *
	 * @param faceid
	 * @param file
	 * @param savePath
	 */
	private void UnZipFolder( int faceid , File file , String fileName , String savePath )
	{
		long FileLength = faceLenghtMap.get( faceid );
		if ( file.length( ) == FileLength )
		{
			try
			{
				ZipUtil.UnZipFolder( file.getAbsolutePath( ) , savePath );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}

			sendMessage( HANDLE_DOWLOAD_SUCCESS , faceid , 0 , savePath + fileName );
		}
		else
		{
			sendMessage( UNZIPFOLDER_FAIL , faceid , 0 , savePath + fileName );
		}
	}

	Runnable runnable = new Runnable( )
	{
		@Override
		public void run( )
		{
			if ( !FaceCenterModel.upFacemainViewPrecentMap.isEmpty( ) )
			{
				updatePrecent( FaceCenterModel.upFacemainViewPrecentMap );
			}
			else
			{
				resetFailList( );
				mMainHandler.removeCallbacks( this );
			}
		}
	};

	/**
	 * 处理刷新进度条
	 * @param map
	 */
	private void updatePrecent( Map< Face , Integer > map )
	{
		int position = 0;
		Face face = null;
		int value = 0;
		Iterator< Face > keys = map.keySet( ).iterator( );
		while ( keys.hasNext( ) )
		{
			Face key = keys.next( );
			if ( key != null && mFaces != null )
			{
				for ( int i = 0 ; i < mFaces.size( ) ; i++ )
				{
					if ( mFaces.get( i ).getFaceid( ) == key.getFaceid( ) )
					{
						position = i;
						face = mFaces.get( i );
						value = map.get( key );
						mFaces.get( i ).setPercent( value );

						sendMessage( UPDATE_PRECENT , position , 0 , face );
						break;
					}
				}}
		}
		resetFailList( );
		mMainHandler.postDelayed( runnable , 1000 ); // 在这里实现每秒执行一次
	}

	/**
	 * 如果表情下载失败，从List里移除该表情 并将进度置为零
	 */
	private void resetFailList( )
	{
		if ( !FaceCenterModel.upFacemainViewFailList.isEmpty( ) )
		{
			removeFailElement( FaceCenterModel.upFacemainViewFailList );
		}
	}

	public void removeFailElement( ArrayList< Face > list )
	{
		Iterator< Face > it = list.iterator( );
		while ( it.hasNext( ) )
		{
			Face key = it.next( );
			for ( Face face : mFaces )
			{
				if ( key.getFaceid( ) == face.getFaceid( ) )
				{
					face.setPercent( 0 );
					mFaceAdapter.notifyDataSetChanged( );
					it.remove( );
				}
			}}
	}

	/** 下载表情规则判断 */
	public void DownLoadFace( final ViewHolder viewHolder , final Face face )
	{
		if ( face.getOwn( ) == 0 )
		{
			long curTime = System.currentTimeMillis( );
			// 未拥有表情
			if ( face.getFeetype( ) == 2 )
			{
				// VIP免费
				if ( !Common.getInstance().isVip( )
					&& !Common.getInstance().isSVip())
				{
					DialogUtil.showTobeVipDialog( mContext , R.string.vip_face ,
							R.string.tost_face_vip_privilege, "");
					return;
				}
			}
			else if ( face.getFeetype( ) == 3 || face.getFeetype( ) == 6
					|| ( face.getFeetype( ) == 5 && face.getEndTime( ) < curTime ) )
			{
				// ** 5.6 NEW **购买类型为打折或者限时免费，价格没有是否VIP之分
				int money = 0;
				if ( face.getFeetype( ) == 6
						|| ( face.getFeetype( ) == 5 && face.getEndTime( ) < curTime ) )
				{
					money = face.getGoldNum( );
				}
				else
				{
					if ( !Common.getInstance().isVip( )
						&& !Common.getInstance().isSVip())
					{
						money = face.getGoldNum( );
					}
					else
					{
						money = face.getVipgoldnum( );
					}
				}
				String buyInfo = "";
				if ( face.getCurrencytype( ) == 2 )
				{
					buyInfo = String.format( getString( R.string.face_pay_price_diamond_msg ) ,
							money );
					DialogUtil.showTwoButtonDialog( mContext ,
							getString( R.string.store_get_gift_tip ) , buyInfo ,
							getString( R.string.cancel ) , getString( R.string.ok ) , null ,
							new OnClickListener( )
							{
								@Override
								public void onClick( View v )
								{
									buyFace( face );
								}
							} );
				}
				else
				{
					buyInfo = String.format( getString( R.string.face_pay_price_msg ) , money );
					Toast.makeText(FaceMainActivity.this, "金币不足", Toast.LENGTH_SHORT).show();
				}
				
				return;
			}

			buyFace( face );
		}
		else
		{
			Download( face , viewHolder );
		}
	}

	/**
	 * 购买表情
	 */
	private void buyFace( Face face )
	{

		if ( pd != null && pd.isShowing( ) )
		{
			pd.dismiss( );
		}
		pd = DialogUtil.showProgressDialog( mContext , getString( R.string.dialog_title ) ,
				getString( R.string.please_wait ) , null );
		long flag = FaceCenterModel.getInstance( this ).buyFace( this , face.getFaceid( ) , callBack );
		faceMap.put( flag , face );
		if ( flag < 0 )
		{
			pd.dismiss( );
		}
	}

	/**
	 * 开启线程下载表情
	 *
	 * @param viewHolder
	 * @param face
	 */
	private void Download( final Face face , ViewHolder viewHolder )
	{
		face.setPercent( 1 );
		displayProgress( viewHolder , face );
		FaceCenterModel.upMyfaceViewPrecentMap.put( face , 1 );
		FaceCenterModel.upFaceDetailPrecentMap.put( face , 1 );
		new Thread( new Runnable( )
		{
			@Override
			public void run( )
			{
				FACE_DESCRIBE = BusinessHttpProtocol.getFaceDescribe( mContext ,
						face.getFaceid( ) , callBack );
				flagMap.put( FACE_DESCRIBE , face.getFaceid( ) );
				/** 表情保存路径 */
				String path = PathUtil.getFaceDir( ) + Uid ;
				FileDownloadManager manager;
				try
				{
					manager = new FileDownloadManager( mContext , callback ,
							face.getDownUrl( ) , String.valueOf( face.getFaceid( ) ) + ".face" ,
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



	/**
	 * 获取不到数据时我的表情按钮为灰色不可点击
	 */
	private void displayMyfaceBtn( final boolean isRefreshComplete )
	{
		if ( isRefreshComplete == true && mFaces.size( ) > 0 )
		{
			mTitleRight.setClickable( true );
			mTitleRight.setTextColor( getResources( ).getColor( R.color.common_white ) );
		}
		else
		{
			mTitleRight.setClickable( false );
			mTitleRight.setTextColor( getResources( ).getColor( R.color.c_ccffffff ) );
		}
	}

	private void performPulling( )
	{
		mPullListView.setRefreshing( );
	}

	private void stopPulling( )
	{
		mPullListView.onRefreshComplete( );
	}

	private void initNetDataFail( String result )
	{
		// 网络获取数据失败
		if ( !TextUtils.isEmpty( result ) )
		{
			ErrorCode.showError( FaceMainActivity.this , result );
		}
		else
		{
			CommonFunction.toastMsg( mContext , getString( R.string.network_req_failed ) );
		}
		stopPulling( );
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

	private void displayProgress( ViewHolder viewHolder , Face face )
	{
		viewHolder.progressTextView.setVisibility( View.GONE );
		viewHolder.mTextProgressBar.setVisibility( View.VISIBLE );
		viewHolder.mTextProgressBar.setMax( 100 );
		viewHolder.mTextProgressBar.setProgress( face.getPercent( ) );
		viewHolder.DownloadLayout.setClickable( false );
		viewHolder.hasFace.setVisibility( View.GONE );
		viewHolder.DownloadLayout.setBackgroundResource( 0 );
	}

	/*************************************************************************************
	 *
	 * 以下代码为防止广告Banner与、PullRefreshView、ListView产生滑动的冲突
	 *
	 *************************************************************************************/
	@Override
	public boolean dispatchTouchEvent( MotionEvent ev )
	{
		if ( mViewFlow != null && mTitleLayout != null && mPullListView != null )
		{
			int action = ev.getAction( );
			int w = View.MeasureSpec.makeMeasureSpec( 0 , View.MeasureSpec.UNSPECIFIED );
			int h = View.MeasureSpec.makeMeasureSpec( 0 , View.MeasureSpec.UNSPECIFIED );
			mTitleLayout.measure( 2 , h );
			if ( action == MotionEvent.ACTION_MOVE
					&& ev.getY( ) < mViewFlow.getHeight( ) + mTitleLayout.getMeasuredHeight( )
					&& mPullListView.isPullToRefreshEnabled( ) )
			{
				mPullListView.setMode( Mode.DISABLED );
			}
			else if ( action == MotionEvent.ACTION_UP )
			{
				mPullListView.setMode( Mode.PULL_FROM_START );
			}
		}
		return super.dispatchTouchEvent( ev );
	}

	@Override
	public boolean onTouch( View v , MotionEvent event )
	{
		int action = event.getAction( );
		if ( action == MotionEvent.ACTION_DOWN )
		{
			mAdBannerTimer.stop( );
		}
		else if ( action == MotionEvent.ACTION_UP )
		{
			mAdBannerTimer.startTimerDelay( 5000 , 5000 );
		}

		return false;
	}

	static class ViewHolder
	{
		ImageView faceIcon;
		TextView faceName;
		TextView faceTag;
		TextView faceInfo;
		ImageView newFace;
		ImageView hasFace;
		TextView animationFace;
		TextView vipFlag;
		TextView progressTextView;
		TextProgressBar mTextProgressBar;
		RelativeLayout DownloadLayout;

	}

	/**
	 * @Title: getDisplayUnitByType
	 * @Description: 获取金币/钻石显示内容1
	 * @param currencyType
	 * @return
	 */
	private String getDisplayUnitByType( int currencyType )
	{
		if ( currencyType == 1 )
		{
			// 金币商品
			return getString( R.string.face_price_neednt_gold_2 );
		}
		else if ( currencyType == 2 )
		{
			// 钻石商品
			return getString( R.string.face_price_neednt_diamond );
		}
		return "";
	}

	/**
	 * @Title: getDisplayUnitByType2
	 * @Description: 获取金币/钻石显示内容2
	 * @param currencyType
	 * @return
	 */
	private String getDisplayUnitByType2( int currencyType )
	{
		if ( currencyType == 1 )
		{
			// 金币商品
			return getString( R.string.face_price_discounts );
		}
		else if ( currencyType == 2 )
		{
			// 钻石商品
			return getString( R.string.face_price_diamond );
		}
		return "";
	}

	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		// 从表情详情页返回，如果购买过表情，则把这个界面刷新一下
		if ( requestCode == 1 && resultCode == RESULT_OK )
		{
			boolean isBuy = data.getBooleanExtra( "isBuy" , false );
			if ( isBuy )
			{
				performPulling( );
			}
		}
		// 从我的表情页返回，如果添加或删除过表情，则把这个界面刷新一下
		if ( requestCode == 2 && resultCode == RESULT_OK )
		{
			boolean isAddordel = data.getBooleanExtra( "isAddordelFace" , false );
			if ( isAddordel )
			{
				performPulling( );
			}
		}

		super.onActivityResult( requestCode , resultCode , data );
	}

	private void displayNewFlag( )
	{
		if ( clickFace != null && clickFace.size( ) > 0 )
		{
			for ( int id : clickFace )
				for ( Face face : mFaces )
					if ( id == face.getFaceid( ) )
					{
						face.setNewflag( 0 );
					}
			clickFace.clear( );
		}
	}

	/**
	 * 判断表情有没有安装
	 *
	 * @param faceId
	 * @return -1是未安装，1是已安装
	 */
	public static int checkFaceInstalled( int faceId )
	{
		return CommonFunction.isFaceInstalled( String.valueOf( faceId ) );
	}

	@SuppressWarnings (
		{ "rawtypes" , "unchecked" } )
	public static List removeDuplicate( List list )
	{
		Set set = new HashSet( );
		List newList = new ArrayList( );
		for ( Iterator iter = list.iterator( ) ; iter.hasNext( ) ; )
		{
			Object element = iter.next( );
			if ( set.add( element ) )
				newList.add( element );
		}
		return newList;
	}

	@Override
	public void onClick( View v )
	{
		if (  v.equals( mTitleBack ) || v.equals(flLeft) || v.getId() == R.id.fl_back)
		{
			finish();

		}else if( v.equals( mTitleRight ) ){

			Intent i = new Intent( mContext , OwnFaceActivity.class );
			startActivityForResult( i , 2 );
		}
	}

}
