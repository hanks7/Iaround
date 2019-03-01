package net.iaround.ui.friend.bean;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * Class: 拉黑名单用户
 * Author：gh
 * Date: 2017/7/20 17:14
 * Email：jt_gaohang@163.com
 */
public class BlacklistFilteringBean extends BaseServerBean {

    public List<UserBlack> userid;

    public class UserBlack{
        public long userid;
        public int status;
    }
}
