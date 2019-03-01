
package net.iaround.ui.game;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.ui.activity.im.ChatGameActivity;
import net.iaround.ui.activity.im.ChatGameActivity.ChatGameSelectedPage;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.game.HorizontalListView.ScrollStateWatcher;
import net.iaround.ui.game.HorizontalListView.ScrollFinish;
import net.iaround.ui.view.HeadPhotoView;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * 搭讪游戏分组横幅头像
 * 
 * @author
 */
public class ChatGameUserList extends FrameLayout implements ScrollStateWatcher
{
	
	private final static int STAND_EMPTY = 2;//用于占位显示，使图标能够居中
	private final static int ANIMATION_TIME = 150;
	private static final int MIN_CLICK_INTER = 3000;
	private static final int INIT_DATA = 1;
	private static final int MSG_HIDE_BID = 2;
	private final int DEFAULT_DATA_SIZE = 50;
	
	private ChatGameClickIconListener mClickIconListener;
	
	private int[ ] mDefaultData;// 默认数据
	private int mCurPrice;// 当前出价价格
	private int mTime;// 显示时间
	private View mFocusBid;
	private HorizontalListView mChatGameUserListView;
	private HorListViewAdapter mChatGameUserAdapter;
	private long mLastBidTime;
	private Animation mShowAnim;
	private Animation mHideAnim;
	private ArrayList<User> mChatGameUsers;
	
	private int mRealUserDataLen;
	
	private ChatGameActivity parentActicity;
	
	public ChatGameUserList(Context context )
	{
		super( context );
		init( context );
	}
	
	public ChatGameUserList(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		init( context );
	}
	
	public ChatGameUserList(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		init( context );
	}
	
	private void init( Context context )
	{
		LayoutInflater.from( context ).inflate( R.layout.chatgame_horizontal_list , this );
		
		mChatGameUserListView = ( HorizontalListView ) findViewById( R.id.horListView );

		parentActicity = ( ChatGameActivity ) context;
		
		mChatGameUsers = new ArrayList< User >( );
		
		
	
	}
	
	
	
	public void setData( Context context )
	{
		
		mChatGameUserAdapter = new HorListViewAdapter( mChatGameUsers , context );
		mChatGameUserListView.setAdapter( mChatGameUserAdapter );
		mChatGameUserListView.setScrollStateWatcher( this );
		
		mChatGameUserListView.setOnItemClickListener( new FocusOnItemClick( ) );
		
		parentActicity.setChatGamePageChangeListener( new ChatGameSelectedPage( )
		{
			
			@Override
			public void onChatGamePageSelected( int selectIndex )
			{
				// TODO Auto-generated method stub
				//
				mChatGameUserAdapter.setItemHolder( selectIndex + STAND_EMPTY , null );
				
				mChatGameUserListView.setSelection( selectIndex );				
								
			}

			@Override
			public void onChatGamePageDeleted( int deletedIndex )
			{

				mChatGameUserAdapter.delUser(deletedIndex+ STAND_EMPTY);
			}
		} );
		mChatGameUserListView.setScrollFinish( new ScrollFinish( )
		{
			
			@Override
			public void onScrollFinish( int position )
			{
				// TODO Auto-generated method stub
				mChatGameUserAdapter.setItemHolder( position + STAND_EMPTY , null );
				mChatGameUserListView.setSelection( position );				
				
				if ( mClickIconListener != null )
				{
					// 在position 中多了一个占位的空值此处要减1
					User user = mChatGameUsers.get( position + STAND_EMPTY );
					mClickIconListener.onIconSelect( user , position  );
					
				}
			}
		} );
		
//		mChatGameUserListView.setSelection( 0 );
	}
	
	/** 覆盖旧数据 */
	public void setChatGameUserList( ArrayList< User > newUsers )
	{
		this.mChatGameUsers.clear( );
		for(int i=0;i<STAND_EMPTY;i++)
		{
			this.mChatGameUsers.add( new User( ) );// 添加占位项
		}

		addUser( newUsers );
		mRealUserDataLen = mChatGameUsers.size( );
		for(int i=0;i<STAND_EMPTY;i++)
		{
			this.mChatGameUsers.add( new User( ) );// 添加占位项
		}

	}
	
	private void addUser( ArrayList< User > users )
	{
		this.mChatGameUsers.addAll( users );
	}
	
	
	public void setShowTime( int time )
	{
		this.mTime = time <= 0 ? this.mTime : time;
	}
	
	@Override
	public void onScroll( HorizontalListView listView )
	{
		
	}
	
	/**
	 * 这是一个淡入淡出的动画集合
	 * 
	 * @param fromX位移动画相对于父容器的起始位置
	 * @param toX位移动画相对于父容器的结束位置
	 * @param fromAlp渐变动画的起始透明度
	 * @param toAlp渐变动画的结束透明度
	 * @param listener动画集合监听器
	 * @param isAfter动画是否停留在结束位置
	 * @return
	 */
	public Animation getAnimationSet(float fromX , float toX , float fromAlp , float toAlp ,
									 AnimationListener listener , boolean isAfter )
	{
		AnimationSet animationSet = new AnimationSet( true );
		TranslateAnimation translateAnimation = new TranslateAnimation( fromX , toX , 0 , 0 );
		translateAnimation.setAnimationListener( listener );
		AlphaAnimation alpAnimation = new AlphaAnimation( fromAlp , toAlp );
		alpAnimation.setDuration( ANIMATION_TIME );
		translateAnimation.setDuration( ANIMATION_TIME );
		animationSet.addAnimation( translateAnimation );
		animationSet.addAnimation( alpAnimation );
		if ( isAfter )
		{
			animationSet.setFillEnabled( true );
			animationSet.setFillAfter( true );
		}
		return animationSet;
	}
	
	private Handler mTheMainHandler = new Handler( )
	{
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case INIT_DATA :
				{
					
				}
					break;
				case MSG_HIDE_BID :
				{
					if ( mFocusBid.getVisibility( ) == View.VISIBLE )
					{
						mFocusBid.startAnimation( mHideAnim );
					}
				}
					break;
			}
		}
	};
	
	private class HorListViewAdapter extends BaseAdapter
	{
		private ArrayList< ? > users;
		private boolean[ ] changed;
		private Context context;
		private ItemHolder[] mholder;
		
		
		private int mSelPosition = STAND_EMPTY;
		
		private DecimalFormat format;
		
		public HorListViewAdapter(ArrayList< ? > users , Context context )
		{
			this.users = users;
			this.context = context;
			format = new DecimalFormat( "00" );
			
			initChanged( );// 查看现有数量
			
			mholder = new ItemHolder[getCount( )];
		}
		
		public Boolean delUser(int index)
		{
			if(index <=users.size( ) )
			{
				users.remove( index );
				notifyDataSetChanged( );
				mRealUserDataLen --;
				return true;
			}
			
			return false;
		}
		
		@Override
		public int getCount( )
		{
			int count = users == null ? 0 : users.size( );
//			return count < mDefaultData.length ? mDefaultData.length : count;
			return count;
		}
		
		// 此方法不使用
		public User getItem( int position )
		{
			User user = ( User ) users.get( position );
			if ( user != null )
			{
				return user;
			}
			return null;
		}
		
		@Override
		public long getItemId( int position )
		{
			return position;
		}
		
		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			initChanged( );// 刷新现有数量
			ItemHolder holder = null;
			if ( convertView == null )
			{
				convertView = LayoutInflater.from( context ).inflate(
						R.layout.chatgame_list_item , null );
				holder = new ItemHolder( );
				holder.iconBg = (ImageView) convertView.findViewById( R.id.focus_user_bg );
				holder.icon = (HeadPhotoView) convertView.findViewById( R.id.friend_icon );
				holder.iconSel = (ImageView) convertView
						.findViewById( R.id.chatgame_user_sel );
				holder.iconBg.setVisibility( View.GONE );
				holder.iconSel.setVisibility( View.INVISIBLE );
				holder.positionSel = position;
				convertView.setTag( holder );
			}
			else
			{
				
				holder = ( ItemHolder ) convertView.getTag( );
				holder.positionSel = position;
				
			}
			
		
			if ( mSelPosition == holder.positionSel )
			{
				holder.iconSel.setVisibility( View.VISIBLE );
			}
			else
			{
				holder.iconSel.setVisibility( View.INVISIBLE );
			}
			
			if ( position < STAND_EMPTY ||position>=mRealUserDataLen)
			{
				convertView.setVisibility( View.INVISIBLE );
			}
			else
			{
				convertView.setVisibility( View.VISIBLE );
			}
			
			mholder[position] = holder;
			
			if ( changed[ position ] )
			{
//				if ( users == null || position >= users.size( ) )
//				{ // 显示默认项
//					int index = position - users.size( );
//					holder.iconBg.setImageResource( R.drawable.default_face_big );
//					holder.icon.getImageView( ).setAdjustViewBounds( true );
//					ImageViewUtil.getDefault( ).loadRoundedImageInConvertView(
//							"drawable://" + mDefaultData[ index ] ,
//							holder.icon.getImageView( ) , NetImageView.DEFAULT_FACE ,
//							NetImageView.DEFAULT_FACE , null );
//					/*
//					 * holder.icon.getImageView().setImageResource(
//					 * mDefaultData[index]);
//					 */
//					holder.icon.setVipLevel( 0 );// 隐藏VIP标示
//					holder.iconSel.setVisibility( View.INVISIBLE );
//					
//				}
//				else
				{ // 显示实际数据
					final User user = ( User ) users.get( position );
//					holder.icon.getImageView( ).setImageDrawable( null );
//					holder.icon.getImageView( ).setTag( "" );
//					holder.icon.getImageView( ).setAdjustViewBounds( true );
//
//					ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView( user.getIcon( ),
//						holder.icon.getImageView( ), R.drawable.default_avatar_round_light,
//						R.drawable.default_avatar_round_light, null, 0, "" );

					holder.icon.execute( ChatFromType.UNKONW,user,null );
																		
				}
				
				
				
				holder.icon.setOnClickListener( new OnClickListener( )
				{
					public void onClick( View v )
					{
						
						
					}
				} );
				
				changed[ position ] = false;
			}
			return convertView;
		}
		
		private void initChanged( )
		{
			changed = new boolean[ getCount( ) ];
			
			for ( int i = 0 ; i < changed.length ; i++ )
			{
				changed[ i ] = true;
			}
		}
		
		public void setItemHolder( int selItem , ItemHolder selHolder )
		{
			
			// notifyDataSetChanged( ) ;
			// notifyDataSetInvalidated( );
			if(mholder ==null)
			{
				return;
			}
			
			if ( selHolder == null )
			{
				
				if(mholder[mSelPosition]!=null)
				{
					mholder[mSelPosition].iconSel.setVisibility( View.INVISIBLE );
				}
				if(mholder[selItem]!=null)
				{
					if(mholder[selItem].positionSel==selItem)
					{
						mholder[selItem].iconSel.setVisibility( View.VISIBLE );
					}
					
				}
				
			}
			else
			{				
				if ( mholder != null )
				{
					mholder[mSelPosition].iconSel.setVisibility( View.INVISIBLE );
				}
				
				mholder[selItem].iconSel.setVisibility( View.VISIBLE );
			}
			mSelPosition = selItem;
		}
		
		
		
	}
	
	private class FocusOnItemClick implements OnItemClickListener
	{
		
		@Override
		public void onItemClick(AdapterView< ? > parent , View view , int position , long id )
		{
			if ( position <STAND_EMPTY )
			{
				//前STAND_EMPTY项是占位项
				return;
			}
			if ( mChatGameUsers != null && position < mChatGameUsers.size( ) )
			{
				// 处理实际数据
				
				if ( view != null )
				{
//					mChatGameUserAdapter.setItemHolder( position ,
//							( ItemHolder ) view.getTag( ) );
				}
				User user = mChatGameUsers.get( position );
				
				if ( mClickIconListener != null )
				{
					// 在position 中多了STAND_EMPTY个占位的空值此处要减STAND_EMPTY
					mClickIconListener.onIconSelect( user , position - STAND_EMPTY );
					
				}
				// if ( user != null )
				// {
				// SpaceOther.appendUserData( user) ;
				// SpaceOther.launch( view.getContext( ) , user.getUid( ) ,
				// ProfileEntrance.USER_NEARBY );
				//
				// StatisticsApi.putStatisticsEvent(
				// StatisticsApi.EVENT_ID_USER_FOCUS_20001 );
				// }
			}
		}
	}
	
	private static class ItemHolder
	{
		ImageView iconBg;
		HeadPhotoView icon;
		ImageView iconSel;
		int positionSel;
	}
	
	
	public void setOnClickIconListener( ChatGameClickIconListener onClickIconListener )
	{
		mClickIconListener = onClickIconListener;
	}
	
	
	public interface ChatGameClickIconListener
	{
		void onIconSelect(User user, int position);
		
	}
	
}
