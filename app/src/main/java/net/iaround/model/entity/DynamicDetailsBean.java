package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * Class: 动态详情
 * Author：gh
 * Date: 2016/12/24 15:28
 * Email：jt_gaohang@163.com
 */
public class DynamicDetailsBean {

    public DynamicLove loveinfo;

    public ReviewInfo reviewinfo;

    public class DynamicLove{
        public int total;
        public ArrayList<DynamicLoveBean> loveusers;

    }

    public class ReviewInfo{
        public int total;
        public ArrayList<DynamicReplyBean> reviews;
    }

}
