package net.iaround.model.entity;

/**
 * Created by yz on 2018/8/5.
 */

public class OrderSetOptionItemBean extends BaseEntity {
    public int repeatDate;//重复日期标识 0-星期日，1-星期一
    public String  repeatDateStr;//重复日期文字 周日 周一
    public double price;//价格
    public int discount;//折扣
    public String unit;//单位
    public boolean isSelected;//是否被选中
}