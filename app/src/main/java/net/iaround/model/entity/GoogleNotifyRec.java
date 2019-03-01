package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * @ClassName GoogleNotifyRec.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-21 上午11:56:31
 * @Description: 
 */

public class GoogleNotifyRec extends BaseServerBean
{
	public String nonce;//订单码
	public ArrayList< OrderStatus > orders;
	
	
	public class OrderStatus
	{
		public String googleorderid;//google订单ID
		public int status;//加金币处理状(0:成功，1：订单为取消状态，2：订单过期，3:账单已被处理，4：订单不存在，5.订单暂时不存在(在服务端可以找到订单，但是在在google服务器取不到数据)，-1：未知错误)
		
		public boolean bIsAddMoneySuccess()
		{
			return status == 0;
		}
		
		public boolean bIsUnknowFail()
		{
			return status == -1;
		}
	}
	
}
