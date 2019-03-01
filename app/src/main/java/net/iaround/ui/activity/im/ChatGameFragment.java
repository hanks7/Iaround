
package net.iaround.ui.activity.im;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.connector.HttpCallBack;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.activity.im.accost.AccostRecordFactory;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.fragment.BaseFragment;
import net.iaround.ui.view.HeadPhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChatGameFragment extends BaseFragment implements HttpCallBack
{
	private List< ProgressBar > bars = new ArrayList< ProgressBar >( );
	
	private HeadPhotoView mFriendIcon;
	private TextView mGenderAge;
	private ImageView mVipFlag;
	private ImageView mWeiBoIcon;
	
	
	RelativeLayout mRlUserInf;
	
	private TextView mLevel; // 等级
	private TextView mNiname;// 昵称
	private TextView mSign; // 签名
	private ImageView mIvSex;//性别
	private TextView mTvAge;//性别
	private RelativeLayout rlAgeSex;//性别背景
	
	private ChatGameActivity parentActivity;
	
	
	protected Context mContext;
	private View view;
	
	private int mPosition;
	private String mUrl;
	private long mUid;
	
	// =======================搭讪消息的显示===========================
	protected ListView chatRecordListView;
	protected ArrayList<ChatRecord> mRecordList = new ArrayList< ChatRecord >( );
	protected BaseAdapter adapter;//
	private User fUser;
	
	
	// =====================================================================
	
	private Handler mHandler = new Handler( )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			switch ( msg.what )
			{
				case 0 :
					
					showPersonalInfor( );
					
					break;
			}
		}
		
	};
	
	@Override
	public void onAttach( Activity activity )
	{
		// TODO Auto-generated method stub
		mContext = activity;
		Bundle bundle = getArguments( );
		if ( bundle != null )
		{

			mUid = bundle.getLong( "UserId" );
			mPosition = bundle.getInt( "Position" );
			mUrl = bundle.getString( "photoId" );

		}

		parentActivity = ( ChatGameActivity ) activity;
		
		super.onAttach( activity );
//		SocialGameHttpProtocol.getGreetingUserInfo( mContext , mUid + "" , this );//jiqiang
		
	}
	
	public void refreshData(int point , String Url , long Uid )
	{
		mPosition = point;
		mUrl = Url;
		mUid = Uid;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container ,
							 Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		
		view = inflater.inflate( R.layout.fragment_accost_content , container , false );
		
		
		mRlUserInf = (RelativeLayout) view.findViewById( R.id.rlContent );
		
		mFriendIcon = ( HeadPhotoView ) view.findViewById( R.id.nivFriendIcon );
		mGenderAge = (TextView) view.findViewById( R.id.tvAge );
		// mVipFlag = ( ImageView ) view.findViewById( R.id.ivVipIcon );
		//
		// mWeiBoIcon = ( ImageView ) view.findViewById( R.id.ivWeiBoIcon );
		mNiname = (TextView) view.findViewById( R.id.tvNickName );
		mLevel = (TextView) view.findViewById( R.id.tvLevel );
		mSign = (TextView) view.findViewById( R.id.tvsign );
		mIvSex = (ImageView) view.findViewById(R.id.ivSex);
		mTvAge = (TextView) view.findViewById(R.id.tvAge);
		rlAgeSex = (RelativeLayout) view.findViewById(R.id.rl_age_sex);

		chatRecordListView = (ListView) view.findViewById( R.id.chatRecordList );
		
		
		fUser = parentActivity.getUserItem( mPosition );
		if ( fUser != null )
		{
			getHistoryRecord( );
			showPersonalInfor( );

			//头像
			mFriendIcon.execute( ChatFromType.UNKONW,fUser,null );

			adapter = new ChatGameRecordAdapter( mContext , fUser , mRecordList ,
					chatRecordListView , mHandler );
			chatRecordListView.setAdapter( adapter );
			// loadRoomRecords( );
			mRlUserInf.setOnClickListener( new OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					// TODO Auto-generated method stub


					if (Common.getInstance().loginUser.getUid() == fUser.getUid( ))
					{
						// TODO: 2017/5/18 跳转到个人页面
						Intent intent = new Intent(mContext, UserInfoActivity.class);
						mContext.startActivity(intent);
					}else
					{
						Intent intent = new Intent(mContext, OtherInfoActivity.class);
						intent.putExtra(Constants.UID,fUser.getUid( ));
						mContext.startActivity(intent);
					}
//					SpaceOther.launchUser( parentActivity , fUser.getUid( ) , fUser , ChatFromType.UNKONW );//jiqiang
				}
			} );
		}
		return view;
	}

	@Override
	public void onStop( )
	{
		// TODO Auto-generated method stub
		if(null!=adapter) {
			((ChatGameRecordAdapter) adapter).stopAllAudioPlay();
		}
		super.onStop( );
	}
	
	private void showPersonalInfor( )
	{
		
		if ( fUser != null )
		{
			// 签名
			SpannableString spSign;
			String infor = fUser.getPersonalInfor( parentActivity );
			if ( !TextUtils.isEmpty(infor) )
			{
				
				spSign = FaceManager.getInstance( parentActivity ).parseIconForString( mSign ,
						parentActivity , infor , 13 );
				if (!TextUtils.isEmpty(spSign))
				{
					mSign.setVisibility(View.VISIBLE);
					mSign.setText(spSign);
				}
			}

			//昵称
			SpannableString nickname = FaceManager.getInstance( parentActivity ).parseIconForString( mNiname ,
					parentActivity , fUser.getNoteName( true ) , 16 );
			if (nickname != null) {
				mNiname.setText(nickname);
			}
			
			// 年龄
			if ( fUser.getAge( ) <= 0 )
			{
				mTvAge.setText( R.string.unknown );
			}
			else
			{
				mTvAge.setText( String.valueOf( fUser.getAge( ) ) );
			}
			
			// 性别
			int sex = fUser.getSexIndex( );
			if ( sex ==2 )
			{
				rlAgeSex.setBackgroundResource( R.drawable.group_member_age_girl_bg );
				mIvSex.setImageResource(R.drawable.thread_register_woman_select);
			} else
			{
				rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
				mIvSex.setImageResource(R.drawable.thread_register_man_select);
			}
			
			// 距离
			TextView distance = (TextView) view.findViewById( R.id.tvDistance );
			int userDistance = 0;

			userDistance = fUser.getDistance( );

			if ( userDistance < 0 )
			{ // 不可知
				distance.setText( R.string.unable_to_get_distance );
			}
			else
			{
				distance.setText( CommonFunction.covertSelfDistance( userDistance ) );
			}
			
			// 在线状态
			TextView tvState = (TextView) view.findViewById( R.id.vtTime );
			String time = TimeFormat.timeFormat1( tvState.getContext( ) ,
					fUser.getOnlineTime( ) );
			
			if ( !TextUtils.isEmpty( time ) )
			{
				tvState.setText( time );
			}
			else
			{
				tvState.setText( R.string.unable_to_get_time );
			}

			// 微博
			// LinearLayout weiboIcon = ( LinearLayout )
			// convertView.findViewById( R.id.llWeiboIcon );
			// CommonFunction.showRightIcon( weiboIcon , user ,
			// parent.getContext( ) );
			
			ImageView[ ] weibos = new ImageView[ 6 ];
			weibos[ 0 ] = (ImageView) view.findViewById( R.id.weibos_icon_1 );
			weibos[ 1 ] = (ImageView) view.findViewById( R.id.weibos_icon_2 );
			weibos[ 2 ] = (ImageView) view.findViewById( R.id.weibos_icon_3 );
			weibos[ 3 ] = (ImageView) view.findViewById( R.id.weibos_icon_4 );
			weibos[ 4 ] = (ImageView) view.findViewById( R.id.weibos_icon_5 );
			weibos[ 5 ] = (ImageView) view.findViewById( R.id.weibos_icon_6 );
//			CommonFunction.showWeibosIcon( weibos , User.parseWeiboStr( fUser.getWeiboString( )) , fUser.getJob( ) ,
//					parentActivity );//jiqiang
			
			
			mLevel.setText( "LV." + fUser.getLevel( ) );
			
		}
		
		
	}



	public void removeImageCache( String imageUrl )
	{
//		MemoryCacheAware< String , Bitmap > memoryMap = ImageViewUtil.getDefault( )
//				.getImageLoader( ).getMemoryCache( );
//		MemoryCacheUtil.removeFromCache( imageUrl , memoryMap );
	}
	

	
	private void getHistoryRecord( )
	{
		
		mRecordList.clear( );
		ArrayList< ChatRecord > historyRecords = ChatPersonalModel.getInstance( )
				.getChatRecordDESC( mContext ,
						String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) ,
						String.valueOf( getTargetID( ) ) , 0 , 2);
		
		
		ChatRecord fromRecord = new ChatRecord( );
		
		fromRecord.setType( String.valueOf( AccostRecordFactory.ACCOST_NOTICE ) );
		
		
		mRecordList.add( fromRecord );
		if ( historyRecords != null && !historyRecords.isEmpty( ) )
		{
			for ( ChatRecord record : historyRecords )
			{
				// addRecord( record );
				if ( TextUtils.isEmpty( record.getType( ) )
						|| SuperChat.TIME_LINE_TYPE.equals( record.getType( ) ) )
				{
					continue;
				}
				mRecordList.add( record );
			}
		}
		
		int size = mRecordList.size( );
		if ( size > 1 )
		{
			ChatRecord record = mRecordList.get( size - 1 );
			if ( record != null )
			{
				fUser.setDistance( record.getDistance( ) );
				fUser.setOnlineTime( record.getDatetime( ) );
				
			}
			if ( mRecordList.get( 1 ).getFrom( ) == 0 )
			{
				mRecordList.remove( 0 );
			}
			else
			{
				fromRecord.setFrome( mRecordList.get( 1 ).getFrom( ) );
			}
			
		}
		
	}
	
	
	public long getTargetID( )
	{
		return fUser != null ? fUser.getUid( ) : 0;
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		// TODO Auto-generated method stub
		JSONObject json;
		try
		{
			json = new JSONObject( result );
			if ( json != null && json.optInt( "status" ) == 200 )
			{
				fUser.setPersonalInfor( CommonFunction.jsonOptString( json , "selftext" ) );
				fUser.setLastLoginTime( json.optLong( "lastonlinetime" ) );
				fUser.setWeibo( CommonFunction.jsonOptString( json , "weibo" ) );
				fUser.setLevel( json.optInt( "level" ) );
				fUser.setJob( json.optInt( "occupation" , -1 ) );
				mHandler.sendEmptyMessage( 0 );
				
			}
			else
			{
				
			}
		}
		catch ( JSONException e )
		{
			CommonFunction.log( "ChatGameActivity" , e.getMessage( ) );
		}
		
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		// TODO Auto-generated method stub
		
	}
	
}
