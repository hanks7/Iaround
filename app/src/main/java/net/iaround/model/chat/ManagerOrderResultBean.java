package net.iaround.model.chat;

import net.iaround.model.im.BaseServerBean;

/**
 * Created by yz on 2018/8/24.
 * 同意或者拒绝订单实体
 */

public class ManagerOrderResultBean extends BaseServerBean {
    public int orderStatus;//步骤
    public long msgid;//消息
    public int agree;//是否同意 1-同意 2-拒绝
}
