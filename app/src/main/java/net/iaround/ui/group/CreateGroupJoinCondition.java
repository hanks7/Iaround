
package net.iaround.ui.group;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.comon.SuperView;
import net.iaround.ui.group.activity.CreateGroupActivity;
import net.iaround.ui.group.bean.GoldDistanceBean;
import net.iaround.ui.group.bean.GroupNextStep;
import net.iaround.ui.group.bean.UserGoldBean;


/**
 * @ClassName: CreateGroupJoinCondition
 * @Description: 创建圈子——设置加入条件
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-9 下午6:32:44
 * 
 */
public class CreateGroupJoinCondition extends SuperView implements INextCheck
{
	
	private RelativeLayout mDistanceLayout;
	private TextView mDistance;
	private TextView mNeedCoins;
	private Button mBtnComplete;
	
	private ICreateGroupParentCallback mParentCallback;
	
	private SuperActivity mSuperActivity;
	
	/** 是否已加载数据 */
	private boolean isInitData = false;
	
	/** 地理范围、金币消耗对照 */
	private GoldDistanceBean goldDistanceBean;
	/** 用户金币 */
	private UserGoldBean mUserGoldBean;
	/** 地理范围表 */
	private String[ ] rangArray;
	/** 历史最大范围 */
	private double oldMaxRang = 0;
	/** 当前选择的范围的索引 */
	private int rangeItem;
	
	public CreateGroupJoinCondition(SuperActivity activity ,
									ICreateGroupParentCallback createGroupActivity )
	{
		super( activity , R.layout.view_create_group_joincondition );
		mSuperActivity = activity;
		this.mParentCallback = createGroupActivity;
		initViews( );
		setListeners( );
		CommonFunction.log( "create_group" , "CreateGroupJoinCondition initView" );
	}
	
	private void initViews( )
	{
		mDistanceLayout = (RelativeLayout) findViewById( R.id.set_distance_layout );
		mDistance = (TextView) findViewById( R.id.group_distance );
		mNeedCoins = (TextView) findViewById( R.id.create_group_coins );
		mBtnComplete = (Button) findViewById( R.id.btn_complete );
	}
	
	private void setListeners( )
	{
		mDistanceLayout.setOnClickListener( new View.OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( goldDistanceBean != null && rangArray != null )
				{
					
					ArrayAdapter< String > adapter = new ArrayAdapter< String >(
							getAttachActivity( ) , R.layout.chat_normal_phrase_list_item ,
							rangArray );
					AlertDialog.Builder builder = new AlertDialog.Builder( getAttachActivity( ) );
					builder.setSingleChoiceItems( adapter , rangeItem ,
							new DialogInterface.OnClickListener( )
							{
								public void onClick(DialogInterface dialog , int item )
								{
									rangeItem = item;
									initGoldUsedList( item );
									dialog.dismiss( );
								}
							} );
					AlertDialog alert = builder.create( );
					alert.show( );
				}
				else if(!isInitData)
				{
					initData( goldDistanceBean , false );
				}
				
				
			}
		} );
		
		mBtnComplete.setOnClickListener( new View.OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				if ( rangArray != null && goldDistanceBean != null )
				{
					String range = rangArray[ rangeItem ];
					int needGold = goldDistanceBean.goldused.get( rangeItem ).gold;
					String note = String.format(
							getContext( ).getString( R.string.create_group_note ) , range ,
							needGold + "" );
					
					DialogUtil.showOKCancelDialog( getContext( ) ,
							getContext( ).getString( R.string.dialog_title ) , note ,
							new View.OnClickListener( )
							{
								
								@Override
								public void onClick( View v )
								{
									mParentCallback.goNext( getGroupNextStep( ) );
								}
							} );
				}
				else if(!isInitData)
				{
					initData( goldDistanceBean , false );
				}
			}
		} );
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		if ( flag == CreateGroupActivity.GET_GOLD_DISTANCE_FLAG
				|| flag == CreateGroupActivity.GET_USER_GOLD_FLAG )
		{
			handleGeneralError( e , flag );
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		try
		{
			handleGeneralSuccess( result , flag );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			handleGeneralError( 107 , flag );
		}
	}
	
	/**
	 * @Title: handleGeneralError
	 * @Description: 处理失败
	 * @param e
	 */
	private void handleGeneralError( int e , long flag )
	{
		ErrorCode.toastError( getAttachActivity( ) , e );
		isInitData = false;
		mParentCallback.showWaitDialog( false );
	}
	
	/**
	 * @Title: handleGeneralSuccess
	 * @Description: 处理返回数据
	 * @param result
	 */
	private void handleGeneralSuccess(String result , long flag )
	{
		if ( CreateGroupActivity.GET_GOLD_DISTANCE_FLAG == flag )
		{
			goldDistanceBean = GsonUtil.getInstance( ).getServerBean(
					result , GoldDistanceBean.class );
			if ( goldDistanceBean != null )
			{
				if ( goldDistanceBean.isSuccess( ) )
				{
					rangArray = new String[ goldDistanceBean.goldused.size( ) ];
					for ( int i = 0 ; i < goldDistanceBean.goldused.size( ) ; i++ )
					{
						rangArray[ i ] = goldDistanceBean.goldused.get( i ).rang + "";
					}
					// rangArray = goldDistanceBean.goldused.toArray(new
					// String[0]);
					if ( CreateGroupActivity.isCreateGroup )
					{
						rangeItem = 0;
					}
					else
					{
						// TODO 处理编辑
					}
					initGoldUsedList( rangeItem );
					getUserGold( );
				}
				else
				{
					handleGeneralError( goldDistanceBean.error , flag );
				}
			}
			else
			{
				handleGeneralError( 107 , flag );
			}
			
		}
		else if ( flag == CreateGroupActivity.GET_USER_GOLD_FLAG )
		{
			mParentCallback.showWaitDialog( false );
			mUserGoldBean = GsonUtil.getInstance( )
					.getServerBean( result , UserGoldBean.class );
			if ( mUserGoldBean != null )
			{
				if ( mUserGoldBean.isSuccess( ) )
				{
					isInitData = true;
				}
				else
				{
					handleGeneralError( mUserGoldBean.error , flag );
				}
			}
			else
			{
				handleGeneralError( 107 , flag );
			}
			
		}
		
	}
	
	/**
	 * 初始地理范围、金币消耗对照表
	 */
	private void initGoldUsedList( int rangItem )
	{
		if ( rangArray != null && rangArray.length > 0 )
		{
			String key = rangArray[ rangeItem ];
			mDistance.setText( key + "Km" );
			String gold = goldDistanceBean.goldused.get( rangItem ).gold + "";
			if ( Double.valueOf( key ) <= oldMaxRang )
			{
				gold = "0";
			}
			mNeedCoins.setText( " " + gold +" ");
		}
	}
	
	/**
	 * @Title: getUserGold
	 * @Description: 获取用户的金币数
	 */
	public void getUserGold()
	{
//		CreateGroupActivity.GET_USER_GOLD_FLAG = GoldHttpProtocol.userGoldGet(
//				getAttachActivity( ) , this );//jiqiang
		if ( CreateGroupActivity.GET_USER_GOLD_FLAG < 0 )
		{
			mParentCallback.showWaitDialog( false );
			handleGeneralError( 107 , CreateGroupActivity.GET_USER_GOLD_FLAG );
		}
	}
	
	/*****************************************
	 * 
	 * INextCheck接口实现
	 * 
	 *****************************************/
	
	@Override
	public void initData(BaseServerBean mServerBean , boolean isBack )
	{
		CommonFunction.log( "create_group" , "CreateGroupJoinCondition initData" );
		if ( !isInitData )
		{
			// TODO 加载数据
//			CreateGroupActivity.GET_GOLD_DISTANCE_FLAG = GroupHttpProtocol.groupGoldUsedList(
//					getAttachActivity( ) , this );//jiqiang
			mParentCallback.showWaitDialog( true );
			if ( CreateGroupActivity.GET_GOLD_DISTANCE_FLAG < 0 )
			{
				mParentCallback.showWaitDialog( false );
				handleGeneralError( 107 , CreateGroupActivity.GET_GOLD_DISTANCE_FLAG );
			}
		}
	}
	
	@Override
	public GroupNextStep getGroupNextStep( )
	{
		GroupNextStep step = new GroupNextStep( );
		if ( goldDistanceBean == null || mUserGoldBean == null )
		{
			step.nextMsg =  ErrorCode.getErrorMessageId(ErrorCode.E_107 );
			return step;
		}
		String range = rangArray[ rangeItem ];
		long needGold = goldDistanceBean.goldused.get( rangeItem ).gold;
		// long curGold = PayModel.getInstance().getGoldNum();
		long curGold = mUserGoldBean.goldnum;
		if ( curGold < needGold )
		{
			step.nextMsg = String.format(
					mSuperActivity.getResString( R.string.chat_create_room_no_gold_msg ) ,
					range , needGold + "" , curGold + "" );
		}
		else
		{
			step.nextMsg = "";
			step.nextParams = new String[ 1 ];
			step.nextParams[ 0 ] = range;
		}
		return step;
	}
	
}
