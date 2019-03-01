
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



/**
 * 支付回调服务端情况
 * 
 * @author linyg
 * 
 */
public class PayOrderWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_payorder"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String UID = "uid"; // 用户uid
	
	/** 支付类型（支付通道）{@link ChannelType} */
	public static final String P_TYPE = "ptype";
	public static final String P_CONTENT = "pcontent"; // 支付提交的内容
	public static final String[ ] selectors =
		{ ID , UID , P_TYPE , P_CONTENT };
	
	/** 表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + UID + " VERCHAR(15), " + P_TYPE
			+ " VERCHAR(10)," + P_CONTENT + " VERCHAR(255));";
	
	protected PayOrderWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	
	
	/**------------ 增 ------------**/
	
	/**
	 * 插入回调订单号
	 * 
	 * @param uid
	 *            用户id
	 * @param type
	 *            支付通道 PayChannelType
	 * @param content
	 */
	public long insetOrder( long uid , int type , String content )
	{
		ContentValues values = new ContentValues( );
		values.put( UID , uid );
		values.put( P_TYPE , type );
		values.put( P_CONTENT , content );
		return onInsert( values );
	}
	
	
	/**------------ 删 ------------**/
	
	/**
	 * 删除某条订单信息，只有订单成功才能删除
	 * 
	 * @param id
	 */
	public void delete( long id )
	{
		SQLiteDatabase db = getDb( );
		db.execSQL( "DELETE FROM " + TB_NAME + " WHERE " + ID + " = " + id );
	}
	
	
	/**------------ 改 ------------**/
	
	
	
	
	/**------------ 查 ------------**/
	
	/**
	 * 查询当前未处理的订单信息
	 * 
	 * @param uid
	 * @return
	 */
	public Cursor onSelectPage(long uid )
	{
		String where = UID + " = " + uid;
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	
	/**
	 * 根据支付渠道查询订单
	 * @param uid
	 * @param type 支付渠道
	 * @return
	 */
	public Cursor onSelectePageByType(long uid, int type)
	{
		String where = UID + " = " + uid + " AND " + P_TYPE + " = " + type;
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	
	/**
	 * 根据支付渠道查询订单数目
	 * @param uid
	 * @param type 支付渠道
	 * @return
	 */
	public int getOrderCount(long uid, int type)
	{
		String where = UID + " = " + uid + " AND " + P_TYPE + " = " + type;
		Cursor cursor = onSelect( selectors , where );
		return cursor.getCount( );
	}
}
