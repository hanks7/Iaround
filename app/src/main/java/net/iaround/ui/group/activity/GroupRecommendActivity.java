
package net.iaround.ui.group.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.model.Text;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.pay.FragmentPayBuyGlod;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.group.bean.GroupInfoBean;
import net.iaround.tools.DialogUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: GroupMsgSetActivity
 * @Description: 5.6圈消息设置
 * @author shifengxiong
 * @date
 * 
 */
public class GroupRecommendActivity extends BaseFragmentActivity implements OnClickListener,HttpCallBack
{
	
	
	/**
	 * 请求我的圈子列表的Flag
	 */
	private long gooslistFlag = 0;
	/*
	 * 推荐圈子出价
	 */
	private long goosprice = 0;
	//标题
	private TextView tvTitle;
	private ImageView mIvLeft;
	private Dialog progressDialog;// 加载栏
	
	private String mGroupId = "";
	
	private int groupCurrentMemberNum;
	
	private ImageView groupImg;
	
	private TextView groupName;
	private TextView groupCurrentMembers;
	private TextView groupCategory;
	
	private TextView groupDec;
	
	private LinearLayout layout_Buttons;
	private Button[ ] goodsButtons;
	private GroupInfoBean mGroupInfoBean ;
	List< GoodsList > list = new ArrayList< GoodsList >( );
	
	private Handler handler = new Handler( )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case 0 : // 获取 推荐圈子商品列表
					layout_Buttons.setVisibility( View.VISIBLE );


					for ( int i = 0 ; i < list.size( ) ; i++ )
					{
						String formatStr = getResources( ).getString(
								R.string.group_inf_group_recommend_price );
						String text = String.format( formatStr ,
								String.valueOf( list.get( i ).number ) ,
								String.valueOf( list.get( i ).goldnum ) );
						goodsButtons[ i ].setText( text );

						goodsButtons[ i ].setTag( list.get( i ) );
					}

					break;
				case 1 :

//					CommonFunction.toastMsg( mContext , getResources( ).getString( R.string.focus_bid_succeeded ) ) ;

					DialogUtil.showOKDialog( mContext, getResources( ).getString( R.string.prompt ) , getResources( ).getString( R.string.focus_bid_succeeded ) , new OnClickListener( )
					{

						@Override
						public void onClick( View v )
						{
							// TODO Auto-generated method stub

							finish();

						}
					} ) ;
					break; // 推荐圈子出价
				case 2 :

					ErrorCode.toastError( mContext , 101 );

					break;
				case 3 : //出价失败
					// onGeneralError 失败
//					DialogUtil.showOKDialog( mContext, getResources( ).getString( R.string.prompt ) , "已经推荐过圈子" ,null);
					 if(((String)msg.obj).contains( "\"error\":6005" ))
					{
						// 金币不足
							DialogUtil.showTowButtonDialog( mContext ,
									getString( R.string.game_center_progress_title ) ,
									getString( R.string.group_inf_group_recommend_no_money ) ,
									getString( R.string.cancel ) , getString( R.string.get_gold_coins ) ,
									null , new View.OnClickListener( )
									{

										@Override
										public void onClick( View v )
										{
											FragmentPayBuyGlod.jumpPayBuyGlodActivity(mContext);//jiqiang
										}

									} );
					}
					 else
					 {
						 ErrorCode.showError( mContext,(String)msg.obj);
					 }
					break;
			}
		}
	};

	public static void launch(Context context , String groupid , int members)
	{
		Intent intent = new Intent( );
		intent.setClass( context , GroupRecommendActivity.class );
		intent.putExtra( "member_num" , members );
		intent.putExtra( "group_id" , groupid );
		context.startActivity( intent );
		// context.startActivity( new Intent( context ,
		// GroupRecommendActivity.class ) );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_recommend );

		initViews( );

		init( );

		setListeners( );

		initData( );
	}


	private void init( )
	{
		mGroupId = getIntent( ).getStringExtra( "group_id" );

		groupCurrentMemberNum= getIntent( ).getIntExtra( "member_num" , 0 ) ;

		String cacheData = SharedPreferenceCache.getInstance( mContext ).getString( mGroupId ,
				"" );
		if ( !CommonFunction.isEmptyOrNullStr( cacheData ) )
		{
			mGroupInfoBean = GsonUtil.getInstance( ).getServerBean( cacheData ,
					GroupInfoBean.class );


//		ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
//				CommonFunction.thumPicture( mGroupInfoBean.icon ) , groupImg ,
//				PicIndex.DEFAULT_GROUP_SMALL , PicIndex.DEFAULT_GROUP_SMALL ,
//				null , 0 , "#00000000" );//jiqiang
			GlideUtil.loadCircleImage(BaseApplication.appContext,CommonFunction.thumPicture( mGroupInfoBean.icon ),groupImg,PicIndex.DEFAULT_GROUP_SMALL , PicIndex.DEFAULT_GROUP_SMALL);
			SpannableString name = FaceManager.getInstance( mContext )
				.parseIconForStringBaseline( groupName , mContext , mGroupInfoBean.name , 14 );

			groupName.setText( name);

			//圈子分类

			int langIndex = CommonFunction.getLanguageIndex( mContext );

			String typeArray[] = mGroupInfoBean.category.split( "\\|" );
			groupCategory.setText( typeArray[ langIndex ] );

			//圈子人数
//			groupCurrentMembers.setText( String.valueOf( groupCurrentMemberNum ));
			String str = getString( R.string.group_info_member_count );
			String showString = String.format( str , groupCurrentMemberNum ,
					mGroupInfoBean.maxcount );
			groupCurrentMembers.setText( showString );


			SpannableString span = FaceManager.getInstance( mContext ).parseIconForString( groupDec , mContext , mGroupInfoBean.content  , 13 );

			groupDec.setText( span );

		}
	}

	/**
	 * @Title: initViews
	 * @Description: 初始化所有控件
	 */
	private void initViews( )
	{
		tvTitle = (TextView) findViewById( R.id.tv_title );
		mIvLeft = (ImageView) findViewById(R.id.iv_left);

		mIvLeft.setVisibility(View.VISIBLE);
		tvTitle.setText( R.string.group_inf_group_recommend_title );
		mIvLeft.setImageResource(R.drawable.title_back);
		mIvLeft.setOnClickListener(this);

		groupImg = (ImageView) findViewById( R.id.group_img );
		groupDec = (TextView) findViewById( R.id.group_desc );
		groupName = (TextView) findViewById( R.id.group_name );
		groupCurrentMembers = (TextView) findViewById( R.id.group_current_members );
		groupCategory = (TextView) findViewById( R.id.group_category );


		layout_Buttons = (LinearLayout) findViewById( R.id.button_list );
		goodsButtons = new Button[ 3 ];
		goodsButtons[ 0 ] = (Button) findViewById( R.id.btn_recommend_group1 );
		goodsButtons[ 1 ] = (Button) findViewById( R.id.btn_recommend_group2 );
		goodsButtons[ 2 ] = (Button) findViewById( R.id.btn_recommend_group3 );

		layout_Buttons.setVisibility( View.GONE );

	}

	/**
	 * 标题返回键
	 * @Title: setListeners
	 * @Description: 初始化监听器
	 */
	private void setListeners( )
	{
		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById( R.id.iv_left ).setOnClickListener( this );
		for ( int i = 0 ; i < goodsButtons.length ; i++ )
		{
			goodsButtons[ i ].setOnClickListener( buyRecommendClickListener );
		}


	}



	@Override
	protected void onResume( )
	{
		super.onResume( );

	}

	/**
	 * @Title: initData
	 * @Description: 加载数据
	 */
	private void initData( )
	{
		requestData( );
	}

	private void requestData( )
	{
		showProgressDialog( );
		gooslistFlag = GroupHttpProtocol.getRecommendGoodsList( mContext , this );
		if ( gooslistFlag == -1 )
		{
			hideProgressDialog( );
		}
	}



	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
	}



	@Override
	public void onGeneralError( int e , long flag )
	{
		hideProgressDialog( );

		handler.sendEmptyMessage( 2 );
//		super.onGeneralError( e , flag );//jiqiang
	}

	@Override
	public void onGeneralSuccess(final String result , long flag )
	{
		hideProgressDialog( );

		CommonFunction.log( "shifengxiong" , "result:" + result );
		if ( flag == gooslistFlag && !CommonFunction.isEmptyOrNullStr( result ) )
		{
			try
			{
				JSONObject json = new JSONObject( result );


				if ( json.optInt( "status" ) == 200 )
				{

					JSONArray goodslist = json.optJSONArray( "items" );

					for ( int i = 0 , iMax = goodslist.length( ) ; i < iMax ; i++ )
					{
						GoodsList good = new GoodsList( );

						good.id = goodslist.getJSONObject( i ).optInt( "id" );
						good.type = goodslist.getJSONObject( i ).optInt( "type" );
						good.number = goodslist.getJSONObject( i ).optInt( "number" );
						good.goldnum = goodslist.getJSONObject( i ).optInt( "goldnum" );
						list.add( good );
					}

					handler.sendEmptyMessage( 0 );
				}
				else
				{
					ErrorCode.showError( mContext , result ) ;
				}


			}
			catch ( Exception e )
			{
				// TODO: handle exception
			}


		}
		else if ( flag == goosprice )
		{

			try
			{
				JSONObject json = new JSONObject( result );

				if ( json.optInt( "status" ) == 200 )
				{
					handler.sendEmptyMessage( 1 );
				}

				else
				{
					Message msg = new Message( );

					msg.what = 3;
					msg.obj = result;
					handler.handleMessage( msg );
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}

		// formatStr = getResources( ).getString(
		// R.string.group_inf_group_recommend_price );
		// text = String.format( formatStr , bean.msg.user.nickname ,
		// bean.msg.content );
	}


	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left :
				finish( );
				break;

		}
	}


	// 显示加载框
	private void showProgressDialog( )
	{
		if ( progressDialog == null )
		{
			progressDialog = DialogUtil.showProgressDialog( mContext , R.string.dialog_title ,
					R.string.content_is_loading , null );
		}
		progressDialog.show( );
	}

	// 隐藏加载框
	private void hideProgressDialog( )
	{
		if ( progressDialog != null )
		{
			progressDialog.hide( );
		}

	}

	public class GoodsList
	{
		public int id;
		public int type;
		public int number;
		public int goldnum;
	}


	// 评论内容的点击事件
	private View.OnClickListener buyRecommendClickListener = new OnClickListener( )
	{

		@Override
		public void onClick( View arg0 )
		{

			final GoodsList goods = ( GoodsList ) arg0.getTag( );


			DialogUtil.showOKCancelDialog(
					GroupRecommendActivity.this ,
					getString( R.string.dialog_group_recommend_title ) ,
					String.format( getString( R.string.group_inf_group_recommend_tips ) ,
							String.valueOf( goods.goldnum ), String.valueOf( goods.number ) ) ,
					new View.OnClickListener( )
					{
						
						@Override
						public void onClick( View v )
						{
							showProgressDialog( );
							goosprice = GroupHttpProtocol.getRecommendGoodsList( mContext , goods.id ,
									mGroupId , GroupRecommendActivity.this );
						}
					} , null ) ;
			
			
		}
	};
	
}
