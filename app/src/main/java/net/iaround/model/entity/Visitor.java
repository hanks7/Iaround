
package net.iaround.model.entity;


import android.os.Parcel;
import android.os.Parcelable;


public class Visitor implements Parcelable
{
	public VisitorUser user;
	public long datetime;
	public int relation;//0:自己 ，1：好友 ，2：陌生人 3、关注 4、粉丝 5推荐
	
	@Override
	public int describeContents( )
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest , int flags )
	{
		dest.writeParcelable( user , flags );
		dest.writeLong( datetime );
		dest.writeInt( relation );
	}
	
	public static final Parcelable.Creator< Visitor > CREATOR = new Creator< Visitor >( )
	{
		
		@Override
		public Visitor[ ] newArray( int size )
		{
			return new Visitor[ size ];
		}
		
		@Override
		public Visitor createFromParcel( Parcel source )
		{
			Visitor visitor = new Visitor( );
			visitor.user = source.readParcelable( ClassLoader.getSystemClassLoader( ) );
			visitor.datetime = source.readLong( );
			visitor.relation = source.readInt( );
			return visitor;
		}
	};
	
}
