
package net.iaround.model.entity;


import android.os.Parcel;
import android.os.Parcelable;

import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.User;


public class VisitorUser extends BaseUserInfo implements Parcelable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5105708777685788479L;
//	public String icon;
//	public long userid;
//	public String weibo;
//	public String notes;
//	public int vip;
//	public int age;
//	public String gender;
//	public int lat;
//	public int lng;
//	public String isonline;
//	public String nickname;
//	public long lastonlinetime;
//	public String selftext;
//	public int viplevel;
//	public int photonum;
//	public int forbid;
//	public int occupation;
//	public int distance;
//	public int svip;
	
	@Override
	public int describeContents( )
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest , int flags )
	{
		dest.writeString( icon );
		dest.writeLong( userid );
		dest.writeString( weibo );
		dest.writeString( notes );
		dest.writeInt( vip );
		
		dest.writeInt( age );
		dest.writeString( gender );
		dest.writeInt( lat );
		dest.writeInt( lng );
		
		dest.writeString( isonline );
		dest.writeString( nickname );
		dest.writeLong( lastonlinetime );
		dest.writeString( selftext );
		dest.writeInt( viplevel );
	
		dest.writeInt( forbid );
		dest.writeInt( photonum );
		dest.writeInt( occupation );
	}
	
	public static final Parcelable.Creator< VisitorUser > CREATOR = new Creator< VisitorUser >( )
	{
		
		@Override
		public VisitorUser[ ] newArray( int size )
		{
			return new VisitorUser[ size ];
		}
		
		@Override
		public VisitorUser createFromParcel( Parcel source )
		{
			VisitorUser visitorUser = new VisitorUser( );
			visitorUser.icon = source.readString( );
			visitorUser.userid = source.readLong( );
			visitorUser.weibo = source.readString( );
			visitorUser.notes = source.readString( );
			visitorUser.vip = source.readInt( );
			
			visitorUser.age = source.readInt( );
			visitorUser.gender = source.readString( );
			visitorUser.lat = source.readInt( );
			visitorUser.lng = source.readInt( );
			
			visitorUser.isonline = source.readString( );
			visitorUser.nickname = source.readString( );
			visitorUser.lastonlinetime = source.readLong( );
			visitorUser.selftext = source.readString( );
			visitorUser.viplevel = source.readInt( );
			
			visitorUser.forbid = source.readInt( );
			visitorUser.photonum = source.readInt( );
			visitorUser.occupation = source.readInt( );
			return visitorUser;
		}
	};
	
	 public static User convertToUser(VisitorUser visitor ,int distance)
	 {
		 if ( visitor != null )
			{
				User user = new User( );
				
				user.setDistance( distance );
				user.setIcon( visitor.icon );
				user.setUid( visitor.userid );
				user.setNoteName( visitor.notes );
				user.setViplevel( visitor.viplevel );
				user.setNickname( visitor.nickname );
				user.setPhotoNum( visitor.photonum );
				user.setJob( visitor.occupation );
				
				int sex = 2;
				if ( !CommonFunction.isEmptyOrNullStr( visitor.gender ) )
				{
					if ( visitor.gender.equals( "m" ) )
					{
						sex = 1;
					}
					else if ( visitor.gender.equals( "f" ) )
					{
						sex = 2;
					}
				}
				user.setSex( sex );
				user.setAge( visitor.age );
				user.setLat( visitor.lat );
				user.setLng( visitor.lng );
				user.setPersonalInfor( visitor.selftext );
				user.setSign( visitor.selftext );
//				user.setOnline( visitor.isOnline( ) );
//				user.setForbid( visitor.isForbidUser( ) );
				user.setLastLoginTime( visitor.lastonlinetime );
				user.setWeibo( visitor.weibo );
				
				return user;
			}
			return null;
	 }
	
}
