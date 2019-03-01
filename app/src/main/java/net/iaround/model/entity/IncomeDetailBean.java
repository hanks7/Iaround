package net.iaround.model.entity;

public class IncomeDetailBean {

    public String earntime;//日期
    public String monthtotal;//收益金额
    public String monthcash;//已提现金额

    public IncomeDetailBean(String earntime, String monthtotal, String monthcash) {
        this.earntime = earntime;
        this.monthtotal = monthtotal;
        this.monthcash = monthcash;
    }
}
