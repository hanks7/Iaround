package net.iaround.model.entity;

import net.iaround.ui.datamodel.BaseUserInfo;

import java.io.Serializable;

/**
 * Created by 施丰雄 on 2016/4/12.
 * email 119535453@qq.com
 */
public class RankData extends BaseUserInfo implements Serializable
{

	public long offertime;  //出价时间(Long)
	public String remain; //当前剩余金额
	public int offerprice; //出价金额
	public int remainPrice; //剩余价格
}
