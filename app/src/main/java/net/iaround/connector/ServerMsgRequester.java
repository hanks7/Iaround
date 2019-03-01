
package net.iaround.connector;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.Hashon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;


/**
 * 直接请求平台维护接口
 *
 * @author linyg@iaround.net
 * @ClassName: ServerMsgRequester
 * @Description: TODO
 * @date 2012-6-11 下午3:20:06
 */
public class ServerMsgRequester extends BaseHttp {
    private WeakReference<Handler> handler;
    private int messageId;

    public ServerMsgRequester(Context context) throws ConnectionException {
        super(context, Config.sServerMsgHost + "?" + System.currentTimeMillis());
    }

    public void setParams(Handler handler, Integer messageId) {
        this.handler = new WeakReference<Handler>(handler);
        this.messageId = messageId;
    }

//	public void request( )
//	{
//		if ( this.handler == null )
//		{
//			return;
//		}
//
//		new Thread( )
//		{
//			public void run( )
//			{
//				BufferedReader br = null;
//				String msg = null;
//				try
//				{
//					connection.connect( );
//					if ( connection.getResponseCode( ) == 200 )
//					{
//						InputStream is = connection.getInputStream( );
//						br = new BufferedReader( new InputStreamReader( is ) );
//						StringBuilder sb = new StringBuilder( );
//						String line = br.readLine( );
//						while ( line != null )
//						{
//							sb.append( line ).append( '\n' );
//							line = br.readLine( );
//						}
//						msg = sb.toString( ).trim( );
//					}
//				}
//				catch ( Throwable t )
//				{
//					CommonFunction.log( t );
//				}
//				finally
//				{
//					if ( br != null )
//					{
//						try
//						{
//							br.close( );
//						}
//						catch ( Throwable e )
//						{
//							CommonFunction.log( e );
//						}
//						br = null;
//					}
//				}
//
//				if ( msg != null && msg.length( ) > 0 && handler != null )
//				{
//					try
//					{
//						Hashon hashon = new Hashon( );
//						HashMap< String , Object > resMap = hashon.fromJson( msg );
//						Message resMsg = new Message( );
//						resMsg.what = messageId;
//						resMsg.obj = resMap;
//						handler.get( ).sendMessage( resMsg );
//					}
//					catch ( Throwable t )
//					{
//						CommonFunction.log( t );
//					}
//				}else{
//					Message resMsg = new Message( );
//					resMsg.what = messageId;
//					resMsg.obj = null;
//					handler.get( ).sendMessage( resMsg );
//				}
//			}
//		}.start( );
//	}

    public String request() {
        final String[] result = {""};
        new Thread() {
            public void run() {
                BufferedReader br = null;
                String msg = null;
                try {
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();
                        while (line != null) {
                            sb.append(line).append('\n');
                            line = br.readLine();
                        }
                        msg = sb.toString().trim();
                    }
                } catch (Throwable t) {
                    CommonFunction.log(t);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Throwable e) {
                            CommonFunction.log(e);
                        }
                        br = null;
                    }
                }

                if (msg != null && msg.length() > 0 && handler != null) {
                    result[0] = msg;
                    try {
                        Hashon hashon = new Hashon();
                        HashMap<String, Object> resMap = hashon.fromJson(msg);
                        Message resMsg = new Message();
                        resMsg.what = messageId;
                        resMsg.obj = resMap;
                        handler.get().sendMessage(resMsg);
                    } catch (Throwable t) {
                        CommonFunction.log(t);
                    }
                } else {
                    result[0] = "";
                }
            }
        }.start();

        return result[0];
    }

}
