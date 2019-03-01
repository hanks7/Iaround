
package net.iaround.ui.datamodel;


import android.content.Context;
import android.database.Cursor;

import net.iaround.conf.Common;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.PayOrderWorker;
import net.iaround.tools.JsonUtil;

import java.util.Map;


public class PayModel extends Model
{
	
	private static PayModel payModel;
	public boolean alipaystatus = false;
	public boolean gfanstatus = false;
	public boolean paypalstatus = false;
	public boolean googlepaystatus = false;
	public boolean szfstatus = false;
	public boolean unionpaystatus = false;
	public boolean tenpaystatus = false;
	
	public String resultBuyGold = "";
	private long goldNum = 0; // 当前用户的金币数，最好每次获取到的最新金币都设置一下
	private long diamondNum = 0;// 当前用户钻石数
	private double credits = 0;// 当前用户爱心积分
	private double loveNum = 0;// 当前用户星星数
	private double star = 0;

	public double getCredits() {
		return credits;
	}

	public void setCredits(double credits) {
		this.credits = credits;
	}

	public long getDiamondNum( )
	{
		return diamondNum;
	}
	
	public void setDiamondNum( long diamondNum )
	{
		this.diamondNum = diamondNum;
	}
	
	private PayModel( )
	{
	}
	
	public static PayModel getInstance( )
	{
		if ( payModel == null )
		{
			payModel = new PayModel( );
		}
		return payModel;
	}
	
	/**
	 * 设置当前用户的金币数
	 * 
	 * @return
	 */
	public long getGoldNum( )
	{
		return goldNum;
	}
	
	/**
	 * 设置当前用户的金币数
	 * 
	 * @param goldNum
	 */
	public void setGoldNum( long goldNum )
	{
		this.goldNum = goldNum;
	}

	public double getLoveNum() {
		return loveNum;
	}

	public void setLoveNum(double loveNum) {
		this.loveNum = loveNum;
	}

	public double getStar() {
		return star;
	}

	public void setStar(double star) {
		this.star = star;
	}

	/**
	 * 解析数据
	 * 
	 * @return Map<String,Object>
	 */
	public Map< String , Object > paramPay(long code , String result )
	{
		return JsonUtil.jsonToMap( result );
	}
	
	public Cursor onSelectPage(Context context, long uid)
	{
		PayOrderWorker worker = DatabaseFactory.getPayOrderWorker( context );
		return worker.onSelectPage( uid );
	}
	
	/**
	 * 往数据库插入一条订单记录
	 * @param context
	 * @param payType
	 * @param content
	 * @return 数据库本地id
	 */
	public long insertOrder(Context context, int payType, String content)
	{
		PayOrderWorker worker = DatabaseFactory.getPayOrderWorker( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		return worker.insetOrder( uid , payType , content );
	}
	
	
	/**
	 * 删除数据库的一条记录
	 * @param context
	 * @param localId
	 */
	public void deleteOrder(Context context, long localId)
	{
		PayOrderWorker worker = DatabaseFactory.getPayOrderWorker( context );
		worker.delete( localId );
	}
	
	public void reset( )
	{
		payModel = null;
	}
}
