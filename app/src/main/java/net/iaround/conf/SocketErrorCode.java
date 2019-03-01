
package net.iaround.conf;


import android.content.Context;

import net.iaround.R;

import java.util.HashMap;
import java.util.Map;


/**
 * socket错误响应码
 * 
 * @author Administrator
 * 
 */
public class SocketErrorCode
{
	private static Map< Integer , Integer > map = new HashMap< Integer , Integer >( );
	
	/** 参数不正确 **/
	public final static int se_10053001 = -10053001;
	/** 服务端错误 **/
	public final static int se_20053001 = -20053001;
	
	/** 参数不正确 **/
	public final static int se_10053004 = -10053004;
	/** 服务端错误 **/
	public final static int se_20053004 = -20053004;
	
	/** 参数不正确 **/
	public final static int se_10053005 = -10053005;
	/** 服务端错误 **/
	public final static int se_20053005 = -20053005;
	/** 没有T人权限 **/
	public final static int se_30053005 = -30053005;
	/** T人太频繁 **/
	public final static int se_40053005 = -40053005;
	/** T人失败 **/
	public final static int se_50053005 = -50053005;
	
	/** 参数不正确 **/
	public final static int se_10053010 = -10053001;
	/** 服务端错误 **/
	public final static int se_20053010 = -20053010;
	
	/** 参数不正确 **/
	public final static int se_10053013 = -10053013;
	/** 服务端错误 **/
	public final static int se_20053013 = -20053013;
	/** 参数不正确 **/
	public final static int se_10083010 = -10083010;
	/** 服务端错误 **/
	public final static int se_20083010 = -20083010;
	/** 参数不正确 **/
	public final static int se_10083007 = -10083007;
	/** 服务端错误 **/
	public final static int se_20083007 = -20083007;
	/** 今天被T次数大于等于6 **/
	public final static int se_30083007 = -30083007;
	/** 今天被T次数大于等于6 **/
	public final static int se_30073009 = -30073009;
	/** 用户尚未退出聊天室 **/
	public final static int se_40083007 = -40083007;
	/** 分配房间失败 **/
	public final static int se_50083007 = -50083007;
	/** 参数不正确 **/
	public final static int se_10083006 = -10083006;
	/** 服务端错误 **/
	public final static int se_20083006 = -20083006;
	/** 参数不正确 **/
	public final static int se_10083004 = -10083004;
	/** 服务端错误 **/
	public final static int se_20083004 = -20083004;
	/** 今天被T次数大于等于6 **/
	public final static int se_30083004 = -30083004;
	/** 用户尚未退出聊天室 **/
	public final static int se_40083004 = -40083004;
	/** 分配房间失败 **/
	public final static int se_50083004 = -50083004;
	/** 每15钟才能更换一次房间 **/
	public final static int se_60083004 = -60083004;
	/** 对方已将你拉入黑名单 **/
	public final static int se_30083010 = -30083010;
	/** 进入群失败 */
	public final static int se_30073004 = -30073004;
	/** 群组人数达到上限 */
	public final static int se_40073004 = -40073004;
	
	public final static int se_50073009 = -50073009;
	/** 上麦失败 */
	public final static int se_10073030 = -10073030;
	public final static int se_20073030 = -20073030;
	public final static int se_30073030 = -30073030;
	/** 下麦失败 */
	public final static int se_10073031 = -10073031;
	/** 加入聊吧失败 */
	public final static int se_20073031 = -20073031;
	/** 退出聊吧失败 */
	public final static int se_50073004 = -50073004;
	public final static int se_40073030 = -40073030;
	public final static int se_10073035 = -10073035;
	public final static int se_10073037 = -10073037;
	public final static int se_30073031 = -30073031;
	public final static int se_10073044 = -10073044;
	public final static int se_20073044 = -20073044;
	public final static int se_60073030 = -60073030;

	static
	{
		map.put( se_10053001 , R.string.se_10053001 );
		map.put( se_10053004 , R.string.se_10053001 );
		map.put( se_10053013 , R.string.se_10053001 );
		map.put( se_10083010 , R.string.se_10053001 );
		map.put( se_10083007 , R.string.se_10053001 );
		map.put( se_10083006 , R.string.se_10053001 );
		map.put( se_10083004 , R.string.se_10053001 );
		
		map.put( se_20053001 , R.string.se_20053001 );
		map.put( se_20053004 , R.string.se_20053001 );
		map.put( se_20053010 , R.string.se_20053001 );
		map.put( se_20053013 , R.string.se_20053001 );
		map.put( se_20083010 , R.string.se_20053001 );
		map.put( se_20083006 , R.string.se_20053001 );
		map.put( se_20083004 , R.string.se_20053001 );
		
		map.put( se_30053005 , R.string.se_30053005 );
		map.put( se_40053005 , R.string.se_40053005 );
		map.put( se_50053005 , R.string.se_50053005 );
		map.put( se_30083007 , R.string.se_30083007 );
		map.put( se_30073009 , R.string.se_30073009 );
		map.put( se_40083007 , R.string.se_40083007 );
		map.put( se_50083007 , R.string.se_50083007 );
		map.put( se_30083004 , R.string.se_40083007 );
		map.put( se_50083004 , R.string.se_50083007 );
		map.put( se_60083004 , R.string.se_60083004 );
		map.put( se_30083010 , R.string.se_30083010 );
		map.put( se_30073004 , R.string.se_30073004 );
		map.put( se_40073004 , R.string.se_40073004 );
		map.put( se_50073009 , R.string.se_50073009 );
		map.put( se_10073030 , R.string.se_10073030 );
		map.put( se_20073030 , R.string.se_20073030 );
		map.put( se_30073030 , R.string.se_30073030 );
		//YC  和高行确认
//		map.put( se_10073031 , R.string.se_10073031 );
		map.put( se_20073031 , R.string.se_20073031 );
		map.put( se_50073004 , R.string.se_50073004 );
		map.put( se_40073030 , R.string.se_40073030 );
		map.put( se_10073035 , R.string.se_10073035 );
		map.put( se_10073037 , R.string.se_10073037 );
		map.put( se_30073031 , R.string.se_30073031 );
		map.put( se_10073044 , R.string.se_10073044 );
		map.put( se_20073044 , R.string.se_20073044 );
		map.put( se_60073030 , R.string.se_60073030 );
	}
	
	/**
	 * 语言描述
	 * 
	 * @param context
	 * @param id
	 * @return 返回对应描述的string，默认返回“出错啦”
	 * @time 2011-6-10 上午11:42:23
	 * @author:linyg
	 */
	public static String getCoreMessage(Context context , int id )
	{
		if ( map.containsKey( id ) )
		{
			return context.getResources( ).getString( map.get( id ) );
		}
		return context.getResources( ).getString( R.string.c_0 );
	}
}
