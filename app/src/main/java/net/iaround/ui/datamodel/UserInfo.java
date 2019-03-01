
package net.iaround.ui.datamodel;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * @ClassName: UserInfo
 * @Description: 地图功能中用到的用户实体类
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-10-8 下午4:00:33
 * 
 */
public class UserInfo extends BaseUserInfo implements Parcelable, Comparable< UserInfo >
{
//	// 头像地址
//	public String icon;
//	// vip等级
//	public int viplevel;
//	// 昵称
//	public String nickname;
//	// 用户id
//	public long userid;
//	// 经度
//	public int lng;
//	// 纬度
//	public int lat;
//	// 上一次在线时间
//	public long lastonlinetime;
//	// 是否vip
//	public int vip;
//	// 年龄
//	public int age;
//	// 备注
//	public String notename;
//	
//	public String gender;
//	public String selftext;
//	public String notes;
//	public String forbid;
//	public String weibo;
//	public int occupation;
//	
//	// 与我的距离
//	public int distance;
	
	public LatLng getLatLng( )
	{
		return new LatLng( lat * 1.0 / 1E6 , lng * 1.0 / 1E6 );
	}
	
	public com.amap.api.maps2d.model.LatLng getAMapLatLng( )
	{
		return new com.amap.api.maps2d.model.LatLng( lat * 1.0 / 1E6 , lng * 1.0 / 1E6 );
	}
	
	public boolean isMerage = false;
	public int merageGroup;
	public ArrayList< UserInfo > userList = new ArrayList< UserInfo >( );
	
	public static final Parcelable.Creator< UserInfo > CREATOR = new Creator< UserInfo >( )
	{
		
		@Override
		public UserInfo[ ] newArray( int size )
		{
			return new UserInfo[ size ];
		}
		
		@Override
		public UserInfo createFromParcel( Parcel source )
		{
			UserInfo info = new UserInfo( );
			info.userid = source.readLong( );
			info.gender = source.readString( );
			info.nickname = source.readString( );
			info.notename = source.readString( );
			info.viplevel = source.readInt( );
			info.icon = source.readString( );
			info.lng = source.readInt( );
			info.lat = source.readInt( );
			info.distance = source.readInt( );
			return info;
		}
	};
	
	@Override
	public int describeContents( )
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest , int flags )
	{
		dest.writeLong( userid );
		dest.writeString( gender );
		dest.writeString( nickname );
		dest.writeString( notename );
		dest.writeInt( viplevel );
		dest.writeString( icon );
		dest.writeInt( lng );
		dest.writeInt( lat );
		dest.writeInt( distance );
	}
	
	@Override
	public int compareTo( UserInfo another )
	{
		return another.icon.compareTo( icon );
	}
	
	
	
}
