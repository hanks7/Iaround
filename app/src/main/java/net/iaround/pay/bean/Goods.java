
package net.iaround.pay.bean;

/**
 * 支付商品列表
 * 
 * @author linyg
 * 
 */
public class Goods
{
	private String goodsid; // 商品id
	private String icon; // 图标
	private int goldnum; // 原价金币数
	private int benegoldnum; // 赠送金币数
	private double price; // 价格
	private int unit; // 货币单位 1、人民币 2、台币 3、港币　4、美元
	private int type; // 哪种渠道
	private String sign; // 货币单位 字符串
	private int diamondnum; // 钻石数
	private String appendcontent;// 送VIP
	private String appendicon;// VIP图标
	
	public String getAppendcontent( )
	{
		return appendcontent;
	}
	
	public void setAppendcontent( String appendcontent )
	{
		this.appendcontent = appendcontent;
	}
	
	public String getAppendicon( )
	{
		return appendicon;
	}
	
	public void setAppendicon( String appendicon )
	{
		this.appendicon = appendicon;
	}
	
	public int getDiamondnum( )
	{
		return diamondnum;
	}
	
	public void setDiamondnum( int diamondnum )
	{
		this.diamondnum = diamondnum;
	}
	
	public String getGoodsid( )
	{
		return goodsid;
	}
	
	public void setGoodsid( String goodsid )
	{
		this.goodsid = goodsid;
	}
	
	public String getIcon( )
	{
		return icon;
	}
	
	public String getSign( )
	{
		return sign;
	}
	
	public void setSign( String sign )
	{
		this.sign = sign;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	public int getGoldnum( )
	{
		return goldnum;
	}
	
	public void setGoldnum( int goldnum )
	{
		this.goldnum = goldnum;
	}
	
	public int getBenegoldnum( )
	{
		return benegoldnum;
	}
	
	public void setBenegoldnum( int benegoldnum )
	{
		this.benegoldnum = benegoldnum;
	}
	
	public double getPrice( )
	{
		return price;
	}
	
	public void setPrice( double price )
	{
		this.price = price;
	}
	
	public int getUnit( )
	{
		return unit;
	}
	
	public void setUnit( int unit )
	{
		this.unit = unit;
	}
	
	public int getType( )
	{
		return type;
	}
	
	public void setType( int type )
	{
		this.type = type;
	}
}
