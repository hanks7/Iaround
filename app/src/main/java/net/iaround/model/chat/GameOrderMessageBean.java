package net.iaround.model.chat;

import net.iaround.model.im.BaseServerBean;

/**
 * Created by yz on 2018/8/24.
 * 游戏订单消息实体类
 */

public class GameOrderMessageBean extends BaseServerBean {

    public int step;//订单状态步骤
    public long senderId;//发送者id
    public long anchor_id;//主播id
    public long orderInfoId;//订单id

    public int orderStatus; //0-订单未操作  1-已同意 2-已拒绝

}
