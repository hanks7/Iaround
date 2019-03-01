package net.iaround.pay.bean;

import java.io.Serializable;


public class PayGoodsBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 9055742595163122000L;
    public String goodsid;  //商品编号
    public String icon;    //图标
    public float price; //价格
    public int senddiamond; //送钻石数量
    public String name;
    public int goldnum;
    public int diamondnum;
    public String strPrice;
    public String acon;        // 赠送
    public int fpay;        // 是否首充标示 1 是不显示 0 显示首充翻倍
    public boolean isCheck;//是否选中
    public String getGoodsid() {
        return goodsid;
    }

    public double getPrice() {
        return price;
    }

    public int getDiamondnum() {
        return diamondnum;
    }
}
