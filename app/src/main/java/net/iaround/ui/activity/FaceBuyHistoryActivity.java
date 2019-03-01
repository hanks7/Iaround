
package net.iaround.ui.activity;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.model.entity.FaceBuyHistoryBean;
import net.iaround.model.entity.FaceBuyHistoryBean.FaceBuy;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.face.FaceDetailActivityNew;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FaceBuyHistoryActivity extends ActionBarActivity
{
	private ListView mListView;
	private ArrayList< FaceBuy > ownFaceList;
	private long FLAG;
	private Dialog mProgressDialog;
	private int RESPONE_SUCCESS = 100;
	private int HIDE_DIALOG = 200;
	private int DATA_NULL =300;
	private FaceBuyHistoryBean buyHistoryBean;
	private FaceRecordAdapter myAdapter;
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_face_buy_history);
		initView( );
		getFaceBuyHistory( );
	}
	
	private void initView( )
	{
		mListView = ( ListView ) findViewById( R.id.ListView );
		mListView.setDivider( null );
		mListView.setDividerHeight( 0 );
		TextView title_name = ( TextView ) findViewById( R.id.tv_title );
		title_name.setText( R.string.face_buy_history );
		ImageView title_back = ( ImageView ) findViewById( R.id.iv_left );
		title_back.setVisibility(View.VISIBLE);
		title_back.setImageResource(R.drawable.title_back);
		findViewById(R.id.fl_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title_back.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				finish( );
			}
		} );
		findViewById(R.id.fl_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		showProgressDialog( );
		
	}
	
	
	/**
	 * 从服务端获取表情购买记录
	 */
	private void getFaceBuyHistory( )
	{
		new Thread( )
		{
			public void run( )
			{
				FLAG = BusinessHttpProtocol.FaceBuyHistory( mContext , callBack );
			}
			
		}.start( );
	}

	private HttpCallBack callBack = new HttpCallBack() {
		@Override
		public void onGeneralSuccess(String result, long flag) {
			if ( flag == FLAG )
			{
				BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
						BaseServerBean.class );
				if ( bean != null && bean.isSuccess( ) )
				{
					buyHistoryBean = GsonUtil.getInstance( ).getServerBean( result ,
							FaceBuyHistoryBean.class );
					if ( buyHistoryBean != null && buyHistoryBean.history.size( )>0 )
					{

						SortTime( buyHistoryBean.history );

						Message msg = mMainHandler.obtainMessage( );
						msg.what = RESPONE_SUCCESS;
						mMainHandler.sendMessage( msg );
					}
					else
					{
						Message msg = mMainHandler.obtainMessage( );
						msg.what = DATA_NULL;
						mMainHandler.sendMessage( msg );
					}
				}
				else
				{
					Message msg = mMainHandler.obtainMessage( );
					msg.what = HIDE_DIALOG;
					mMainHandler.sendMessage( msg );

				}
			}
		}

		@Override
		public void onGeneralError(int e, long flag) {
			if ( flag == FLAG )
			{
				Message msg = mMainHandler.obtainMessage( );
				msg.what = HIDE_DIALOG;
				mMainHandler.sendMessage( msg );

			}
		}
	};
	
	
	class FaceRecordAdapter extends BaseAdapter
	{
		@Override
		public int getCount( )
		{
			return ownFaceList == null ? 0 : ownFaceList.size( );
		}
		
		@Override
		public Object getItem( int position )
		{
			return ownFaceList == null ? null : ownFaceList.get( position );
		}
		
		@Override
		public long getItemId( int position )
		{
			return 0;
		}
		
		@Override
		public View getView( int position , View convertView , ViewGroup parent )
		{
			if ( convertView == null )
			{
				convertView = View.inflate( mContext , R.layout.face_record_item , null );
			}
			
			ImageView icon = ( ImageView ) convertView.findViewById( R.id.icon_img );
			TextView name = ( TextView ) convertView.findViewById( R.id.name );
			TextView price = ( TextView ) convertView.findViewById( R.id.price );
			TextView time = ( TextView ) convertView.findViewById( R.id.buyTime );
			TextView expires = ( TextView ) convertView.findViewById( R.id.expires );
			
			// 初始化
			icon.setBackgroundResource( R.color.transparent );
			name.setText( "" );
			price.setText( "" );
			time.setText( "" );
			expires.setText( "" );
			
			FaceBuy face = ( FaceBuy ) getItem( position );
			if ( face != null )
			{
				// 表情logo
				GlideUtil.loadImage(BaseApplication.appContext, face.icon , icon ,
						R.drawable.default_pitcure_small , R.drawable.default_pitcure_small);
				// 表情名称
				name.setText( face.name );
				
				// 表情使用期限
				if ( face.expires != -1 )
				{
					expires.setText( face.expires );
				}
				else
				{
					expires.setText( getString( R.string.use_unlimited ) );
				}
				
				// 表情购买时间
				String format_time = null;
				SimpleDateFormat sdf = new SimpleDateFormat( );
				sdf.applyPattern( "yyyy-MM-dd" );
				format_time = sdf.format( face.buytime );
				if ( format_time != null )
				{
					time.setText( format_time );
				}
				else
				{
					time.setText( getString( R.string.unable_to_get_time ) );
				}
				
				// 表情价格
				if ( face.feetype == 1 )
				{
					// 免费
					String freeStr = getString( R.string.face_price_neednt_gold_tip );
					String oldPriceStr = String.format( getDisplayUnitByType( face.currencytype ) ,
							face.goldnum );
					String finalText = freeStr + " " + oldPriceStr;
					SpannableString spanText = new SpannableString( finalText );
					spanText.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF9900" ) ) ,
							finalText.indexOf( freeStr ) , freeStr.length( ) ,
							SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );
					spanText.setSpan( new StrikethroughSpan( ) , finalText.indexOf( oldPriceStr ) ,
							spanText.length( ) , SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );
					
					price.setTextColor( Color.GRAY );
					price.setTextSize( 13 );
					price.setText( spanText );
				}
				else if ( face.feetype == 2 )
				{
					// VIP免费特供
					price.setTextColor( Color.parseColor( "#FF9900" ) );
					price.setTextSize( 13 );
					price.setText( getString( R.string.face_vip_neednt_gold ) );
					
				}
				else if ( face.feetype == 3 )
				{
					// 收费
					price.setTextSize( 13 );
					price.setTextColor( Color.parseColor( "#FF9900" ) );
					price.setText( String.format( getDisplayUnitByType( face.currencytype ) ,
							face.goldnum ) );
				}
				else if ( face.feetype == 4 )
				{
					// 参加活动获得
					price.setTextColor( Color.parseColor( "#FF9900" ) );
					price.setTextSize( 13 );
					price.setText( getString( R.string.get_face_by_activite ) );
				}
				else if ( face.feetype == 5 )
				{
					// 限时免费
					price.setTextSize( 13 );
					price.setTextColor( Color.parseColor( "#FF9900" ) );
					price.setText( getString( R.string.face_limit_free ) );
				}
				else if ( face.feetype == 6 )
				{
					// 打折
					String priceText = String.format( getDisplayUnitByType( face.currencytype ) ,
							face.goldnum );
					String discountText = String.format(
							getDisplayUnitByType2( face.currencytype ) , face.oldgoldnum );
					String finalText = priceText + " " + discountText;
					SpannableString spanText = new SpannableString( finalText );
					spanText.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF9900" ) ) , 0 ,
							finalText.indexOf( discountText ) ,
							SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );
					spanText.setSpan( new StrikethroughSpan( ) , priceText.length( ) + 1 ,
							spanText.length( ) , SpannableString.SPAN_INCLUSIVE_EXCLUSIVE );
					price.setTextColor( Color.GRAY );
					price.setTextSize( 13 );
					price.setText( spanText );
					
				}
			}
			mListView.setOnItemClickListener( new OnItemClickListener( )
			{
				@Override
				public void onItemClick( AdapterView< ? > parent , View view , int position ,
						long id )
				{
					FaceBuy clickFace = ( FaceBuy ) getItem( position );
					FaceDetailActivityNew.launchForResult( FaceBuyHistoryActivity.this , clickFace.pkgid );
				}
			} );
			
			return convertView;
		}
		
		
		
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
	
	
	// 显示加载框
	private void showProgressDialog( )
	{
		if ( mProgressDialog == null )
		{
			mProgressDialog = DialogUtil.showProgressDialog( mContext , "" ,
					getString( R.string.please_wait ) , null );
			mProgressDialog.setCanceledOnTouchOutside( false );
		}
		
		mProgressDialog.show( );
	}
	
	// 隐藏加载框
	private void hideProgressDialog( )
	{
		if ( mProgressDialog != null )
		{
			mProgressDialog.hide( );
		}
	}
	
	private Handler mMainHandler = new Handler( Looper.getMainLooper( ) )
	{
		@Override
		public void handleMessage( Message msg )
		{
			if ( msg.what == RESPONE_SUCCESS )
			{
				if ( myAdapter == null )
				{
					hideProgressDialog( );
					myAdapter = new FaceRecordAdapter( );
					myAdapter.notifyDataSetChanged( );
					mListView.setAdapter( myAdapter );
				}
				else
				{
					hideProgressDialog( );
					myAdapter.notifyDataSetChanged( );
				}
			}
			
			else if ( msg.what == HIDE_DIALOG )
			{
				hideProgressDialog( );
				finish( );
				ErrorCode.toastError( mContext , ErrorCode.E_107 );
			}
			else if ( msg.what == DATA_NULL )
			{
				hideProgressDialog( );
				CommonFunction.toastMsg( mContext , getString( R.string.goto_buy_face ) );
			}
		}
	};
	
	@Override
	public void onDestroy( )
	{
		super.onDestroy( );
		
		if ( mProgressDialog != null )
		{
			mProgressDialog.dismiss( );
			mProgressDialog = null;
		}
	}
	
	public class comparator implements Comparator< Object >
	{
		public int compare( Object o1 , Object o2 )
		{
			FaceBuy face1 = ( FaceBuy ) o1;
			FaceBuy face2 = ( FaceBuy ) o2;
			int flag = face2.getFormatTime( ).compareTo( face1.getFormatTime( ) );
			return flag;
		}
	}
	
	private void SortTime( ArrayList<FaceBuy> list )
	{
		for ( FaceBuy record : list )
		{
			record.setFormatTime( String.valueOf( record.buytime ) );
		}
		
		comparator cm = new comparator( );
		Collections.sort( list , cm );
		ownFaceList = list;
	}
}
