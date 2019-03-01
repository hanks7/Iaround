package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/11.
 */

public class IncomeDetailEntity extends BaseEntity {

    public int pageno;
    public int pagesize;
    public int amount;//总条数
    public ArrayList<DetailsList> Details;

    public class DetailsList {
        public String earntime;//日期
        public String monthtotal;//收益金额
        public String monthcash;//已提现金额
        public ArrayList<DailyDetail> lists;//每日收益列表
    }

    public class DailyDetail {

        public String datetime;//时间
        public String total;//收益，保留两位小数
        public int status;
    }
}
