package net.iaround.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GreetListItemBean;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.datamodel.UserInfo;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-26 下午2:24:07
 * @Description: 
 */
public class GreetListAdapter extends BaseAdapter {

	private ArrayList<GreetListItemBean> dataList;
	private Context mContext;
	
	public GreetListAdapter(Context context, ArrayList<GreetListItemBean> list) {
		mContext = context;
		dataList = list;
	}
	
	@Override
	public int getCount( )
	{
		return dataList == null ? 0 : dataList.size( );
	}
	
	@Override
	public Object getItem(int position )
	{
		return  dataList == null ? null : dataList.get( position );
	}
	
	@Override
	public long getItemId( int position )
	{
		return position;
	}
	
	@Override
	public View getView(int position , View convertView , ViewGroup parent )
	{
		ViewHolder holder = null;
		if(convertView == null)
		{
			convertView = View.inflate( mContext , R.layout.greet_list_item , null );
			holder = initViewHolder( holder , convertView );
			convertView.setTag( holder );
		}else
		{
			holder = ( ViewHolder ) convertView.getTag( );
		}
		
		final GreetListItemBean item = dataList.get( position );
		final UserInfo user = item.getUser();
		
		
		//头像

		holder.ivImage.execute(ChatFromType.UNKONW, user.convertBaseToUser(),null );
		
		//名字
		String name = user.nickname;
		SpannableString spName = FaceManager.getInstance( parent.getContext( ) )
				.parseIconForString( holder.tvName , parent.getContext( ) , name , 20 );
		holder.tvName.setText( spName );
		
		//年龄性别
		holder.tvAge.setText( String.valueOf( user.age ) );
		if(user.gender != null && user.gender.contains( "f" ))
		{
			holder.rlAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
			holder.ivSex.setImageResource(R.drawable.thread_register_woman_select);
		}else
		{
			holder.rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
			holder.ivSex.setImageResource(R.drawable.thread_register_man_select);
		}
		
		//时间
		holder.tvTime.setText( TimeFormat.timeFormat4( mContext, item.datetime ) );
		
		String sign = item.getUser().selftext == null ? "" : item.getUser().selftext;
		SpannableString spSign = FaceManager.getInstance( parent.getContext( ) )
				.parseIconForString( holder.tvSign , parent.getContext( ) , sign , 13 );
		holder.tvSign.setText(spSign);

		return convertView;
	}
	
	private ViewHolder initViewHolder(ViewHolder holder, View view)
	{
		holder = new ViewHolder( );
		holder.ivImage = ( HeadPhotoView ) view.findViewById( R.id.ivImage );
		holder.tvName = (TextView) view.findViewById( R.id.tvName );
		holder.tvAge = (TextView) view.findViewById( R.id.tvAge );
		holder.tvTime = (TextView) view.findViewById( R.id.tvTime );
		holder.tvSign = (TextView) view.findViewById( R.id.tvSign );
		holder.ivSex = (ImageView) view.findViewById(R.id.ivSex);
		holder.rlAgeSex = (RelativeLayout) view.findViewById(R.id.rlAgeSex);
		return holder;
	}
	

	/** 适配器的控件缓存容器 */
	static class ViewHolder
	{
		public HeadPhotoView ivImage;
		public TextView tvName;
		public TextView tvAge;
		public TextView tvTime;
		public TextView tvSign;
		public RelativeLayout rlAgeSex;
		public ImageView ivSex;
	}
}
