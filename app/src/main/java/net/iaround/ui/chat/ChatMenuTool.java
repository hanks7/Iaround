
package net.iaround.ui.chat;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.view.dynamic.MeBaseAdapter;

import java.util.ArrayList;


/**
 * 聊天菜单
 * 
 * @author linyg
 * 
 */
public class ChatMenuTool extends LinearLayout
{
	public final static int CHAT_TOOL_CAMARA = 1;
	public final static int CHAT_TOOL_PHOTO = 2;
	public final static int CHAT_TOOL_VIDEO = 3;
	public final static int CHAT_TOOL_SENDGIFT = 4;
	public final static int CHAT_TOOL_POSITION = 5;
	public final static int CHAT_TOOL_APP_SCENE = 6;

	private Context mContext;
	private LayoutInflater vInflater;
	private GridView gridView;
	private MenuToolListener onListener;

	private ArrayList< ChatToolMenuItem > chatToolMenus;

	/**
	 * 初始化菜单控件
	 *
	 * @param context
	 * @param viewGroup
	 */
	public ChatMenuTool( Context context , ViewGroup viewGroup )
	{
		super( context );
		mContext = context;
		initComponent( viewGroup );
	}

	// 初始化控件
	private void initComponent( ViewGroup viewGroup )
	{
		vInflater = LayoutInflater.from( mContext );
		View mainView = vInflater.inflate( R.layout.appointprop_grid , viewGroup , false );
		addView( mainView , new LayoutParams( LayoutParams.MATCH_PARENT ,
				LayoutParams.MATCH_PARENT ) );
		gridView = ( GridView ) mainView.findViewById( R.id.gift_grid );
		gridView.setOnItemClickListener( new OnItemClickListener( )
		{
			@Override
			public void onItemClick( AdapterView< ? > arg0 , View arg1 , int arg2 , long arg3 )
			{

			}
		} );
	}

	/**
	 * 初始化数据
	 *
	 * @param groupType
	 *            1是私聊菜单列表，2圈子菜单列表
	 */
	public void initTool( MenuToolListener onListener , int groupType )
	{
		this.onListener = onListener;
		initToolMenuData( groupType );
		gridView.setAdapter( new DataAdapter( chatToolMenus ) );
	}

	/**
	 * 初始化菜单按钮数据
	 *
	 * @param type
	 *            1为私聊菜单，2为圈子菜单
	 */
	private void initToolMenuData( int type )
	{

		chatToolMenus = new ArrayList<>( );
		//照片
		ChatToolMenuItem pictureItem = new ChatToolMenuItem();
		pictureItem.id = CHAT_TOOL_PHOTO;
		pictureItem.icon = mContext.getResources().getDrawable(R.drawable.iaround_new_chat_add_picture);
		pictureItem.title = mContext.getResources().getString(R.string.chat_update_text_toolMenu_of_picture);

		// 拍照
		ChatToolMenuItem photoItem = new ChatToolMenuItem( );
		photoItem.id = CHAT_TOOL_CAMARA;
		photoItem.icon = mContext.getResources( ).getDrawable(R.drawable.iaround_new_chat_add_photo );
		photoItem.title = mContext.getString( R.string.chat_update_text_toolMenu_of_photo );

		// 视频
		ChatToolMenuItem videoItem = new ChatToolMenuItem( );
		videoItem.id = CHAT_TOOL_VIDEO;
		videoItem.icon = mContext.getResources( ).getDrawable(R.drawable.iaround_new_chat_add_video );
		videoItem.title = mContext.getString( R.string.video );

		// 送礼
		ChatToolMenuItem sendgiftItem = new ChatToolMenuItem( );
		sendgiftItem.id = CHAT_TOOL_SENDGIFT;
		sendgiftItem.icon = mContext.getResources( ).getDrawable(R.drawable.iaround_new_chat_add_gift );
		sendgiftItem.title = mContext.getString( R.string.sendgift );

		// 位置
		ChatToolMenuItem positionItem = new ChatToolMenuItem( );
		positionItem.id = CHAT_TOOL_POSITION;
		positionItem.icon = mContext.getResources( ).getDrawable(R.drawable.iaround_new_chat_add_location );
		positionItem.title = mContext.getString( R.string.position );


		/**
		 * 根据type类型显示不同的工具按钮
		 * type 1 私聊
		 *      2 圈聊
		 */
		if ( type == 1 )
		{
			chatToolMenus.clear();
			chatToolMenus.add(pictureItem);
//			chatToolMenus.add( photoItem );//取消打开相机
			chatToolMenus.add( videoItem );
			chatToolMenus.add( sendgiftItem );
			chatToolMenus.add( positionItem );
			gridView.setNumColumns( 3 );
		}
		if (type == 2)
		{
			chatToolMenus.clear();
			chatToolMenus.add(pictureItem);
//			chatToolMenus.add(photoItem);//取消打开相机
			chatToolMenus.add(positionItem);
			chatToolMenus.add(videoItem);
			//群聊时只显示一行
			gridView.setHorizontalSpacing(getResources().getDimensionPixelOffset(R.dimen.chat_update_space_of_horizontal));
			gridView.setVerticalSpacing(0);
			gridView.setNumColumns( 3 );
		}

	}

	private class DataAdapter extends MeBaseAdapter
	{
		protected DataAdapter( ArrayList< ? > arrayList )
		{
			super( arrayList );
			notifyDataSetChanged();
		}

		@Override
		public View getView( int position , View convertView , ViewGroup parent )
		{
			if ( convertView == null )
			{
				convertView = vInflater.inflate( R.layout.chat_menu_tool_item , null );
			}
			ImageView toolIcon = ( ImageView ) convertView.findViewById( R.id.tool_icon );
			TextView toolTitle = ( TextView ) convertView.findViewById( R.id.tool_title );

			ChatToolMenuItem menuItem = ( ChatToolMenuItem ) getItem( position );
			toolIcon.setImageDrawable( menuItem.icon );
			toolTitle.setText( menuItem.title );
			toolIcon.setTag( menuItem.id );
			toolIcon.setOnClickListener( new MenuOnClickOnlistener( ) );
			return convertView;
		}

		@Override
		public View createView( int position )
		{
			return null;
		}
	}

	class MenuOnClickOnlistener implements OnClickListener
	{
		@Override
		public void onClick( View v )
		{
			int id = ( Integer ) v.getTag( );
			onListener.onToolListener( id );
		}
	}

	// 聊天菜单
	class ChatToolMenuItem
	{
		public int id;
		public String title;
		public Drawable icon;
	}

	interface MenuToolListener
	{
		/**
		 * 菜单事件监听器
		 *
		 * @param type
		 *            1 拍照，2相片，3视频，4送礼，5位置
		 */
		void onToolListener( int type );
	}
}
