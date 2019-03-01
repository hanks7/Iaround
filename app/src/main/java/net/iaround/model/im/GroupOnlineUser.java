package net.iaround.model.im;

import java.util.List;

/**
 * Class:在线用户
 * Author：yuchao
 * Date: 2017/6/16 16:03
 * Email：15369302822@163.com
 */
public class GroupOnlineUser extends BaseServerBean {

    private long group_id;
    private int page_no;
    private int page_size;
    private int amount;
    private int total_page;
    private List<GroupSimpleUser> list;

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public List<GroupSimpleUser> getList() {
        return list;
    }

    public void setList(List<GroupSimpleUser> list) {
        this.list = list;
    }
}