package net.iaround.model.entity;

/**
 * Created by yz on 2018/8/16.
 */

public class GetEarnDetailBean extends BaseEntity {
    public DetailBean detail;
    public class DetailBean{
        public double returncash;
        public double todaycash;
        public double freezecash;
    }
}
