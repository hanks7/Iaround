package net.iaround.entity;

import net.iaround.model.im.BaseServerBean;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/7/17 11:26
 * Email：15369302822@163.com
 */
public class ChatbarSendGiftBean extends BaseServerBean {

    //{"gift_id":45,"gift_num":6482,"status":200}
    public int gift_id;//礼物ID
    public int gift_num;//剩余礼物数量
    public long user_gift_id;//礼物连击ID
    public long userid;//
    public long user_diamond;//
    public long user_star;
    public long user_gold;//
    public long user_love;//
    public long charm_num;//增长魅力
    public int gift_currencytype;//礼物货币类型
    public int price;//价格
    public String gift_name;//礼物名称
    public int exp;//经验值
    public int send_gift_num;//赠送礼物数量
    public String gift_desc;//礼物单位
    public String gift_icon;//礼物图标
    public long user_had_time;//剩余聊天时长

}
