package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/16.
 */

public class LoveDetailsEntity extends BaseEntity {

    public int pageno;
    public int pagesize;
    public int amount;
    public ArrayList<LoveDetails> list;// 银行详情列表

    public class LoveDetails {
        public int consumetype; // 类型
        public int lovenum; // 钻石数
        public long datetime; // 时间
    }
}
