package net.iaround.model.entity;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-8 上午10:16:28
 * @ClassName RecordGiftBean.java
 * @Description: 聊天记录中礼物的实体
 */

public class RecordGiftBean {
    public String giftname;//礼物的名字
    public String charmnum;//魅力值
    public String price;//价格
    public int currencytype;//1为金币商品　2为钻石商品 3为爱心商品  6为星星商品
    public int giftnum;
    public int exp;//经验
    public long friend_id;
    public String gift_desc;//礼物的单位
    public String isFromChatRoom;//是否是聊吧送礼 0私聊 1聊吧

//    public int getGiftnum() {
//        if (giftnum != null && !"".equals(giftnum)) {
//            return (Integer) giftnum;
//        } else {
//            return 0;
//        }
//    }
//
//    public void setGiftnum(Object giftnum) {
//        this.giftnum = giftnum;
//    }
//
//    public String getGiftnumOne() {
//        if (giftnum != null && !"".equals(giftnum)) {
//            return (String) giftnum;
//        } else {
//            return "";
//        }
//    }

    /**
     * 判断该礼物是否钻石礼物
     *
     * @return true为钻石礼物、false为金币礼物
     */
    public boolean bIsDiamonGift() {
        return currencytype == 2;
    }
}
