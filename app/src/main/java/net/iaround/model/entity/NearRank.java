package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * Created by 施丰雄 on 2016/4/11.
 * email 119535453@qq.com
 */
public class NearRank extends BaseServerBean
{

	public Arith arith;
	public ArrayList< RankData> rankList;




	public class Arith
	{
		public  long interval; //两次出价间隔时间，毫秒
		public int period;  //出价可以呆多久，小时
		public  int limit; //只显示这么多个，多的不显示
		public int min; //出价起步价，单位：钻石
		public float rate;  //每秒消耗率
		public float addupr;  //比别人出价递增的基数百分比（就是那个0.1即10%）
	}



}
