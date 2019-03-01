
package net.iaround.utils;


import net.iaround.conf.Config;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


/**
 * 本地库接口
 *
 * @author linyg
 *
 */
public class NativeLibUtil
{
	static
	{

		System.loadLibrary( "native-utils" );
		switch ( Config.TEST_SERVER )
		{
			case 1 :
				System.loadLibrary( "testdebugiaroundnet" );
				break;
			case 2 :
				System.loadLibrary( "testiaroundnet" );
				break;
			case 3 :
				System.loadLibrary( "debugiaroundnet" );
				break;
			case 4 :
				System.loadLibrary( "iaroundnet" );
				break;
		}

	}

	public native String bbb( Context a , PackageInfo b , String c , String d );

	public native String aaa( Context a , PackageInfo b , String c , String d );

	public static native String hi( );

	/**
	 * 获取包签名值
	 *
	 * @param context
	 * @return
	 */
	public PackageInfo getPackageInfo( Context context )
	{
		try
		{
			return context.getPackageManager( ).getPackageInfo( "net.iaround" ,
					PackageManager.GET_SIGNATURES );
		}
		catch ( Exception e )
		{
		}

		return null;
		//  30820281308201eaa00302010202044e
		// "30820281308201eaa00302010202044e549d3a300d06092a864886f70d0101050500308183310b300906035504061302434e31123010060355040813094775616e67446f6e6731123010060355040713094775616e675a686f7531183016060355040a130f7777772e6961726f756e642e6e657431183016060355040b130f7777772e6961726f756e642e6e6574311830160603550403130f7777772e6961726f756e642e6e65743020170d3131303832343036343230325a180f32303636303532373036343230325a308183310b300906035504061302434e31123010060355040813094775616e67446f6e6731123010060355040713094775616e675a686f7531183016060355040a130f7777772e6961726f756e642e6e657431183016060355040b130f7777772e6961726f756e642e6e6574311830160603550403130f7777772e6961726f756e642e6e657430819f300d06092a864886f70d010101050003818d0030818902818100a9e4d0d806b8788bf5b0a6b175ca06476aaacbff17aca32fd3b6a7d94e4d3e5dcc80871b1614f11ad157dcef240ea56e5eb7be219ec57911ad117c0e8f4f655fa4daff023e21c239fd907f622b1940dd808407eee7922dbc67c6a5ee0c42e77a21a92c3cbeaecb7d8f5e1ceeb1dbbca337b79859c66df3ad9fe9f03d452d40fd0203010001300d06092a864886f70d0101050500038181009be8d8e0a4ca86b2dd1b3acee832ec14a5be13186da583e9c83030138115bfc84e8ccbc67ce9f90a65bb05e4670cf6777f3a9710b1e255dbacddfeb1a34f9f989b8fa6a3b228c66a082250edad8aae341be3a4c75cf2e31b67e0a6e2831146836e30692f38c2c8bdff0687b03a2a98b741742eb83e7f4c0ae1eb90406e694197";
	}

	public String cbb( Context context , String a , String b )
	{
		return aaa( context , getPackageInfo( context ) , a , b );
	}
}
