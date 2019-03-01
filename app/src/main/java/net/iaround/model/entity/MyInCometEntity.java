package net.iaround.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: 我的收益
 * Author：gh
 * Date: 2017/3/6 21:31
 * Email：jt_gaohang@163.com
 */
public class MyInCometEntity {

    public String total;
    public String alreadyPresent;
    public String tax;
    public List<InComeDetails> incomeDetails;
    public List<InComeDetails> incomeAmount;

    public class InComeDetails{
        public long time;
        public String paymentType;
        public double money;
    }

    public MyInCometEntity() {
        total = "1000";
        alreadyPresent = "12100";
        tax = "100";
        incomeDetails = new ArrayList<InComeDetails>();
        incomeAmount = new ArrayList<InComeDetails>();

        for (int i = 0; i < 20; i++){
            InComeDetails inComeDetails = new InComeDetails();
            inComeDetails.money = 125;
            inComeDetails.paymentType = "送礼";
            inComeDetails.time = 1488808269;
            incomeDetails.add(inComeDetails);
        }
    }
}
