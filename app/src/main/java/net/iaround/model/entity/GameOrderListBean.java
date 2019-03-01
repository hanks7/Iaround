package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/9.
 */

public class GameOrderListBean extends BaseEntity {

    public int pagenum;//当前页数
    public int pagecount;//页面记录条数
    public int amount;//记录总条数
    public ArrayList<OrderListItem> orderlist;

    public class OrderListItem {

        public int ordertype;//0-主播 1-用户
        public long orderid;//订单类型id
        public long anchorid;//此订单主播id
        public long userid;//用户id

        public String nickname;//主播昵称
        public String icon;//主播头像
        public String comment;//描述

        public int paybut;//去支付按钮 0：不显示，1：显示
        public int commentbut;//评论按钮 0：不显示，1：显示

        public int onemorebut;//再来一单按钮 0：不显示，1：显示

        public int status;//订单状态 0：待支付 1：已取消 2：已完成 3：已退款
    }
}
