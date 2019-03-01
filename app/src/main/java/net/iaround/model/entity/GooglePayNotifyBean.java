package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * @ClassName GooglePayNotifyBean.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-21 上午10:34:36
 * @Description: 
 */

public class GooglePayNotifyBean
{
	public String nonce;
	public ArrayList< GoogleOrder > orders = new ArrayList< GoogleOrder >();

	public static class GoogleOrder
	{
		public String token;
		public String notificationId;
		public String orderId;
		public String packageName;
		public String productId;
		public long purchaseTime;
		public int purchaseState;//支付状态，
		public String developerPayload;//"uid,order"
	}
}


