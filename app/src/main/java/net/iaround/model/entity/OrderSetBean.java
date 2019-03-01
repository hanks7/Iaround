package net.iaround.model.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yz on 2018/8/4.
 */

public class OrderSetBean extends BaseEntity implements Serializable {

    public ArrayList<OrderGameItem> games;

    public class OrderGameItem implements Serializable{
        public long gameId;
        public String gameName;
        public double price;
        public int discount;//折扣
        public String unit;//单位
        public int orderCount;//接单此数 0-不能开启折扣 1-可以开启折扣
        public ArrayList<Integer> discountList;//折扣列表
        public ArrayList<Double> priceList;//价格列表

    }

    public String beginTime;
    public String endTime;
    public ArrayList<Integer> repeat;//重复日期标识 0-星期日，1-星期一
}
