package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/11.
 */

public class DailyDetailEntity{
    public int status;
    public int pageno;
    public int pagecount;
    public int amount;//总条数
    public ArrayList<DetailsList> Details;

    public class DetailsList {
        public String earntime;//日期
        public ArrayList<DailyDetail> detail;//每日收益列表
    }

    public class DailyDetail {
        //这部分是为了合并数据

        public String date;//当月已提现
        public String datetime;//时间
        public float total;//收益，保留两位小数
        public int status;
    }
}
