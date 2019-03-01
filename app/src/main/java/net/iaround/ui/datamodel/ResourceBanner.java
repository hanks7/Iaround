package net.iaround.ui.datamodel;

import android.content.Context;
import android.text.TextUtils;

import net.iaround.conf.Common;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PhoneInfoUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月2日 下午6:10:47
 * @Description: 广告资源的实体
 */
public class ResourceBanner {

	public int id;//广告的id
	public String imageurl;//广告图片url
	public int isshare;//0-可以分享, 1-不可以分享
	public String link;//广告跳转链接,包括H5,内部跳转链接
	public String title;
	public int jumptype;//跳转类型
	public String report; //第三方广告上报路径

	/** 获取广告图片的Url */
	public String getImageUrl()
	{
		if(TextUtils.isEmpty(imageurl))
		{
			return "";
		}
		return imageurl;
	}
	
	/** 获取广告跳转链接 */
	public String getLink()
	{
		if(TextUtils.isEmpty(link))
		{
			return "";
		}
		return link;
	}
	
	/** 获取广告跳转链接 */
	public String getTitle()
	{
		if(TextUtils.isEmpty(title))
		{
			return "";
		}
		return title;
	}
	/*
	{IDFA}
	{DEVICEID}
	{OPENUUID}
	{MAC}
	{MODEL}
	{CARRIER}
	{IP}
	{NET}
	{DPI}
	 */

	public void setThirtReport(Context context)
	{
		if(TextUtils.isEmpty( report ))
		{
			return;
		}
		String macaddress = PhoneInfoUtil.getInstance( context ).macAddress( );
		String strIMEI = PhoneInfoUtil.getInstance( context ).getIMEI( );
		String CARRIER = PhoneInfoUtil.getInstance( context ).getIMSICode( );
		String deviceid = strIMEI;
		String DPI = Common.windowWidth + "*" + Common.windowHeight;
		String NET = PhoneInfoUtil.getInstance( context ).getNetType( );
		String IP = PhoneInfoUtil.getInstance( context ).getWifiIpAddress( );
		IP = PhoneInfoUtil.getInstance( context ).getLocalIpAddress();
		report = report.replace("{IDFA}", "" );
		report = report.replace("{DEVICEID}", deviceid );
		report= report.replace("{NET}", NET );
		report= report.replace("{DPI}", DPI );
		report=report.replace( "{OPENUUID}",strIMEI + macaddress );
		report=report.replace( "{CARRIER}",CARRIER );
		report=report.replace( "{MAC}",macaddress );
//		report=report.replace( "{MODEL}",PhoneInfoUtil.getInstance( context ).getDeviceInfos( ) );
		report=report.replace( "{MODEL}",PhoneInfoUtil.getInstance( context ).getModel());
		report=report.replace( "{IP}",IP );
		report=report.replace( "{IMEI}",strIMEI );
	}
	/*
	点击还是展示
	 */
	public void getRequest(boolean isClick)
	{
		if ( TextUtils.isEmpty( report ) || !isClick)
		{
			return;
		}
		new Thread(new Runnable( )
		{
			@Override
			public void run( )
			{
				String[] httpUrl = report.split( "[|]" );
				for ( int i = 0; i < httpUrl.length; i++ )
				{
					try
					{
						CommonFunction.log( "DataStatistics", httpUrl[i] );
						HttpGet httpRequest = new HttpGet( httpUrl[i] );

						HttpClient httpclient = new DefaultHttpClient( );

						HttpResponse httpResponse = httpclient.execute( httpRequest );

				/*若状态码为200 ok*/
						if ( httpResponse.getStatusLine( ).getStatusCode( ) == 200 )
						{
			/*读*/
							String strResult = EntityUtils.toString( httpResponse.getEntity( ) );
            /*去没有用的字符*/
							//						strResult = eregi_replace("(\r\n|\r|\n|\n\r)","",strResult);
						}
						else
						{

						}
					}
					catch ( ClientProtocolException e )
					{
						e.printStackTrace( );
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
				}
			}
		} ).start();



	}
}
