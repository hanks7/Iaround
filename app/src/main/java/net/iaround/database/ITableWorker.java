
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import net.iaround.tools.CommonFunction;

import java.util.ArrayList;


/**
 * 数据操作,基本的增、删、改、查都基本，需要特别的操作，则在子类实现
 * 
 * @author linyg
 * 
 */
public class ITableWorker
{
	private SQLiteHelper sqlhelp;
	private SQLiteDatabase db;
	private String KEY_ID;
	private String TB_NAME;
	
	protected ITableWorker(Context context )
	{
		sqlhelp = SQLiteHelper.getInstance( context );
		db = sqlhelp.getWritableDatabase( );
	}
	
	protected ITableWorker(Context context , String keyId , String tb_name )
	{
		this.KEY_ID = keyId;
		this.TB_NAME = tb_name;
		sqlhelp = SQLiteHelper.getInstance( context );
		db = sqlhelp.getWritableDatabase( );
	}
	
	/**
	 * 查找数据
	 * 
	 * @param columns
	 * @param where
	 * @return
	 * @time 2011-6-24 下午12:02:26
	 * @author:linyg
	 */
	public Cursor onSelect(String[ ] columns , String where )
	{
		Cursor cursor = db.query( TB_NAME , columns , where , null , null , null , null );
		return cursor;
	}
	
	/**
	 * 通过完整的sql语句查询
	 * 
	 * @param selectionArgs 选择的参数
	 * @param sql sql语句
	 * @return Cursor
	 */
	public Cursor onSelectBySql(String[ ] selectionArgs , String sql)
	{
		Cursor cursor = db.rawQuery( sql , selectionArgs );
		return cursor;
	}
	
	/**
	 * 插入数据，以ContentValues的格式
	 * 
	 * @param values
	 * @return
	 * @time 2011-6-24 上午11:58:49
	 * @author:linyg
	 */
	public synchronized long onInsert( ContentValues values )
	{
		long id = db.insert( TB_NAME , "" , values );
		return id;
	}
	
	public synchronized void onBatchInsert(ArrayList<ContentValues> values)
	{
		db.beginTransaction();
		for(int i= 0; i < values.size(); i++)
		{
			db.insert( TB_NAME , "" , values.get(i) );
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	/**
	 * 更新数据
	 * 
	 * @param values
	 * @param where
	 * @return
	 * @time 2011-6-24 下午12:06:06
	 * @author:linyg
	 */
	public synchronized int onUpdate(ContentValues values , String where )
	{
		int id = db.update( TB_NAME , values , where , null );
		CommonFunction.log( "TB_NAME" , "tb_name:" +TB_NAME +", values=" + values+ ", where="+ where +", update id=" +id );
		return id;
	}
	
	/**
	 * 删除某条信息
	 * 
	 * @param _id
	 * @return
	 * @time 2011-6-24 下午01:25:17
	 * @author:linyg
	 */
	public synchronized int onDelete( String _id )
	{
		int id = 0;
		String where = KEY_ID + " = " + _id;
		id = db.delete( TB_NAME , where , null );
		return id;
	}
	
	public synchronized int delete( String where )
	{
		int id = db.delete( TB_NAME , where , null );
		return id;
	}
	
	/**
	 * 删除表里的所有数据
	 * 
	 * @return
	 * @time 2011-6-24 下午01:25:11
	 * @author:linyg
	 */
	public boolean onDeleteAll( )
	{
		try
		{
			db.execSQL( "DELETE FROM " + TB_NAME );
		}
		catch ( SQLException e )
		{
			return false;
		}
		return true;
	}
	
	/**
	 * 删除该表
	 * 
	 * @time 2011-6-24 下午01:25:02
	 * @author:linyg
	 */
	public synchronized void onDelTable( )
	{
		try
		{
			db.execSQL( "DROP TABLE " + TB_NAME );
		}
		catch ( SQLException e )
		{
		}
	}
	
	/**
	 * 执行普通sql语句
	 * 
	 * @param sql
	 * @time 2011-6-24 下午01:24:47
	 * @author:linyg
	 */
	public synchronized void onExcute( String sql )
	{
		try
		{
			db.execSQL( sql );
		}
		catch ( SQLException e )
		{
		}
	}
	
	/**
	 * 关闭
	 */
	public void onClose( )
	{
		// try{
		// db.close();
		// }catch(Exception e){
		// e.printStackTrace();
		// }
	}
	
	public SQLiteDatabase getDb( )
	{
		return this.db;
	}
}
