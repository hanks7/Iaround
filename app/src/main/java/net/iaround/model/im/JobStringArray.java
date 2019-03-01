
package net.iaround.model.im;


import android.content.Context;

import net.iaround.R;


/**
 * 根据需要获取完整或者新版本的职业字符串数组 显示使用完整版，选择使用最新版
 * */
public class JobStringArray
{
	private static JobStringArray instant;
	
	private Context context;
	
	private JobStringArray(Context context )
	{
		this.context = context;
	}
	
	public static JobStringArray getInstant( Context context )
	{
		if ( instant == null )
		{
			instant = new JobStringArray( context );
		}
		return instant;
	}
	
	/**
	 * 获取全部职业
	 * */
	public String[ ] getAllJobs( )
	{
		return context.getResources( ).getStringArray( R.array.space_occupation );
	}
	
	/**
	 * 获取5.4版本职业 5.4版本职业，删除8，11，13，14，16，17，18，29，添加30，31，32
	 * */
	public String[ ] getJob54( )
	{
		String[ ] allJobs = context.getResources( ).getStringArray( R.array.space_occupation );
		String[ ] Jobs_54 = new String[ allJobs.length - 8 ];
		for ( int i = 0 , j = 0 ; i < allJobs.length ; i++ )
		{
			switch ( i )
			{
				case 8 :
				case 11 :
				case 13 :
				case 14 :
				case 16 :
				case 17 :
				case 18 :
				case 29 :
					break;
				default :
				{
					Jobs_54[ j ] = allJobs[ i ];
					j++;
				}
					break;
			}
		}
		return Jobs_54;
	}
	
	/**
	 * 根据位置获取职业id，5.4版本
	 * */
	public int getIDByPosition( int position )
	{
		String[ ] Jobs_54 = getJob54( );
		if ( position > Jobs_54.length )
		{
			return 0;
		}
		String job = Jobs_54[ position ];
		String[ ] id_job = job.split( "," );
		return Integer.parseInt( id_job[ 0 ] );
	}
}
