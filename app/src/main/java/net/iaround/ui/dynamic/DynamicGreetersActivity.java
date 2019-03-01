
package net.iaround.ui.dynamic;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.model.entity.ReviewsListServerBean;
import net.iaround.model.entity.ReviewsListServerBean.ReviewsItem;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.dynamic.adapter.DynamicMessagesAdapter;
import net.iaround.ui.dynamic.bean.DynamicMessagesItemBean;

import java.util.ArrayList;


/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月16日 上午11:56:35
 * @Description: 查看对我的评论点赞的列表
 */
public class DynamicGreetersActivity extends SuperActivity implements OnClickListener
{
	
	/** TitleLayout */
	private TextView titleName;
	private TextView titleRight;
	private ImageView titleLeft;

	private long mGetOldListFlag;
	private long mGetNewListFlag;
	private boolean isFirstGetNew = false;
	
	private Dialog mProgressDialog;
	private PullToRefreshListView listView;
	private EmptyLayout empty;
	
	private DynamicMessagesAdapter adapter;
	private ArrayList<ReviewsItem> dataList;
	private ArrayList< DynamicMessagesItemBean > reviews = new ArrayList<DynamicMessagesItemBean>( );
	private DynamicMessagesItemBean moreItemBean;
	private ArrayList< DynamicMessagesItemBean > mDataList = new ArrayList< DynamicMessagesItemBean >( );
	
	private int mPageNo = 1;// 历史评论的请求第几页
	private int mPageSize = 20;// 每页的请求数量
	private int mTotalPage = 0;// 总共的页数
	private long mLastTime = 0;// 最后一条的时间
	
	public static final int STOP_PULLING = 2;
	public static final int SHOW_DATA = 3;
	public static final int SHOW_EMPTY = 5;
	
	public static String UNREAD_DATA_KEY = "unread_data";// 未读点赞数据Key
	public static String REQUEST_NEW = "request_new";// 请求未读点赞列表key
	private ReviewsListServerBean mUnreadData;// 未读点赞数据Value
	
	private boolean isShowNewGreeter = false;// 是否展示最新的点赞内容

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_dynamic_messages );
		
		String data = "";
		Intent intent = getIntent( );
		if ( intent != null )
			data = intent.getStringExtra( UNREAD_DATA_KEY );
		boolean reqNew = intent.getBooleanExtra( REQUEST_NEW , false );

		if ( !TextUtils.isEmpty( data ) )
		{// 从动态消息列表点击进入
			mUnreadData = GsonUtil.getInstance( ).getServerBean( data ,
					ReviewsListServerBean.class );
			dataList = mUnreadData.msgs;
			
			for ( int i = 0 ; i < dataList.size( ) ; i++ )
			{
				DynamicMessagesItemBean itemBean = new DynamicMessagesItemBean( );
				itemBean.setItemType( DynamicMessagesItemBean.CONTENT_TYPE );
				itemBean.setReviewItem( dataList.get( i ) );
				reviews.add( itemBean );
			}
			mLastTime = dataList.get( dataList.size( ) - 1 ).msg.datetime;
			isShowNewGreeter = true;
			initView( );
			mMainHandler.sendEmptyMessage( SHOW_DATA );
		}
		else if ( reqNew )
		{
			isShowNewGreeter = true;
			initView( );
			mGetNewListFlag = DynamicHttpProtocol.getUnreadGreeterList( DynamicGreetersActivity.this , this );//mContext
			empty.showLoading( );
		}
		else
		{
			isShowNewGreeter = false;
			initView( );
			initData( );
		}
	}
	
	// 初始化布局
	private void initView( )
	{
		titleName = (TextView) findViewById( R.id.tv_title );
		titleName.setText( R.string.greet_list_title );
		
//		titleRight = (TextView) findViewById( R.id.title_right_text );
//		titleRight.setVisibility( View.GONE );
		
		titleLeft = (ImageView) findViewById( R.id.iv_left );
		titleLeft.setBackgroundResource( R.drawable.title_back );
		titleLeft.setOnClickListener( this );
		findViewById(R.id.fl_left).setOnClickListener(this);

		mProgressDialog = DialogUtil.showProgressDialog( mActivity , "" , mContext
				.getResources( ).getString( R.string.please_wait ) , null );
		
		listView = (PullToRefreshListView) findViewById( R.id.ptrlvMessagesList );
		listView.setPullToRefreshOverScrollEnabled( false );// 禁止滚动过头
		listView.getRefreshableView( ).setFastScrollEnabled( false );
		if ( isShowNewGreeter )
		{
			listView.setMode( Mode.DISABLED );
		}
		else
		{
			listView.setMode( Mode.PULL_FROM_END );
		}
		
		listView.setOnRefreshListener( new OnRefreshListener< ListView >( )
		{
			
			@Override
			public void onRefresh( PullToRefreshBase< ListView > refreshView )
			{
				if ( mPageNo > mTotalPage && !isFirstGetNew )
				{
					CommonFunction.toastMsg( mContext , R.string.no_more_data );
					mMainHandler.sendEmptyMessage( STOP_PULLING );
					moreItemBean.setHasMore( false );
					moreItemBean.setItemCount( reviews.size( ) );
					listView.setMode( Mode.DISABLED );
					adapter.notifyDataSetChanged( );
				}
				else
					mGetOldListFlag = DynamicHttpProtocol.getGreeterHistoryList( mContext ,
							mPageNo , mPageSize , mLastTime , DynamicGreetersActivity.this );
			}
		} );
		
		moreItemBean = new DynamicMessagesItemBean( );
		moreItemBean.setItemType( DynamicMessagesItemBean.FOOTER_TYPE );
		moreItemBean.setHasMore( true );
		
		empty = new EmptyLayout( mContext , listView.getRefreshableView( ) );
	}
	
	private void initData( )
	{
		mGetOldListFlag = DynamicHttpProtocol.getGreeterHistoryList( mContext , mPageNo ,
				mPageSize , mLastTime , DynamicGreetersActivity.this );
		
		empty.showLoading( );
	}
	
	private Handler mMainHandler = new Handler( Looper.getMainLooper( ) )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case STOP_PULLING :
					listView.onRefreshComplete( );
					break;
				case SHOW_EMPTY :
					empty.showEmpty( );
					break;
				case SHOW_DATA :
				{
					refreshData( );
					if ( mProgressDialog != null && mProgressDialog.isShowing( ) )
					{
						mProgressDialog.hide( );
					}
					if ( adapter == null )
					{
						adapter = new DynamicMessagesAdapter( mContext , mDataList , null ,
								moreOnClickListener );
						listView.setAdapter( adapter );
					}
					else
					{
						adapter.dataList = mDataList;
						adapter.notifyDataSetChanged( );
					}
				}
					break;
				default :
					break;
			}
		}
    };
	
	public void onGeneralSuccess(String result , long flag )
	{
		mProgressDialog.hide( );
		if ( flag == mGetOldListFlag )
		{// 历史点赞消息
			mMainHandler.sendEmptyMessage( STOP_PULLING );
			ReviewsListServerBean data = GsonUtil.getInstance( ).getServerBean( result ,
					ReviewsListServerBean.class );
			if ( data.isSuccess( ) )
			{
				mPageNo++;
				if ( data.msgs != null )
				{
					int all = data.amount;
					mTotalPage = all % mPageSize == 0 ? all / mPageSize
							: ( all / mPageSize ) + 1;
					isFirstGetNew = false;
					if ( dataList == null )
					{
						dataList = data.msgs;
					}
					else
					{
						dataList.clear( );
						dataList.addAll( data.msgs );
					}
					
					for ( int i = 0 ; i < dataList.size( ) ; i++ )
					{
						DynamicMessagesItemBean itemBean = new DynamicMessagesItemBean( );
						itemBean.setItemType( DynamicMessagesItemBean.CONTENT_TYPE );
						itemBean.setReviewItem( dataList.get( i ) );
						reviews.add( itemBean );
					}
					
					mLastTime = dataList.get( dataList.size( ) - 1 ).msg.datetime;
				}
				mMainHandler.sendEmptyMessage( SHOW_DATA );
				
			}
			else
			{
				empty.showEmpty( );
			}
		}
		else if ( flag == mGetNewListFlag )
		{// 未读点赞消息
			mMainHandler.sendEmptyMessage( STOP_PULLING );
			mUnreadData = GsonUtil.getInstance( ).getServerBean( result ,
					ReviewsListServerBean.class );
			if ( mUnreadData.isSuccess( ) )
			{
				if ( DynamicModel.getInstent( ).getNewNumBean( ) != null )
					DynamicModel.getInstent( ).getNewNumBean( ).setLikenum( 0 );
				dataList = mUnreadData.msgs;
				if ( mUnreadData.msgs != null )
				{
					for ( int i = 0 ; i < dataList.size( ) ; i++ )
					{
						DynamicMessagesItemBean itemBean = new DynamicMessagesItemBean( );
						itemBean.setItemType( DynamicMessagesItemBean.CONTENT_TYPE );
						itemBean.setReviewItem( dataList.get( i ) );
						reviews.add( itemBean );
					}
					mLastTime = dataList.get( dataList.size( ) - 1 ).msg.datetime;
					mMainHandler.sendEmptyMessage( SHOW_DATA );
				}
				else
				{
					empty.showEmpty( );
				}
			}
			else
			{
				empty.showError( );
			}
		}
	}

    @Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		mProgressDialog.hide( );
		ErrorCode.toastError( mContext , e );
		if ( flag == mGetOldListFlag || flag == mGetNewListFlag )
		{
			empty.showError( );
		}
	}
	
	private View.OnClickListener moreOnClickListener = new OnClickListener( )
	{
		
		@Override
		public void onClick( View v )
		{
			
			listView.setMode( Mode.PULL_FROM_END );
			isShowNewGreeter = false;
			mGetOldListFlag = DynamicHttpProtocol.getGreeterHistoryList( mContext , mPageNo ,
					mPageSize , mLastTime , DynamicGreetersActivity.this );
		}
	};
	
	private void refreshData( )
	{
		
		mDataList.clear( );
		
		if ( reviews != null && reviews.size( ) > 0 )
		{
			mDataList.addAll( reviews );
		}
		
		if ( moreItemBean != null )
		{
			if ( isShowNewGreeter )
			{
				moreItemBean.setHasMore( true );
				mDataList.add( moreItemBean );
			}
			else if ( mPageNo > mTotalPage )
			{
				moreItemBean.setHasMore( false );
				moreItemBean.setItemCount( reviews.size( ) );
				mDataList.add( moreItemBean );
			}
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
		}
	}
	
	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
		mProgressDialog.dismiss( );
	}
	
}
