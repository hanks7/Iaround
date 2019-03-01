package net.iaround.ui.group.bean;

import net.iaround.ui.datamodel.Gift;

import java.io.Serializable;

/**
 * Created by Ray on 2017/8/3.
 */

public class GiftComponentBean implements Serializable {
    private static final long serialVersionUID = 6559839982670551751L;
    private Gift gift;
    private int giftNum;
    private long usergiftId;

    public long getUsergiftId() {
        return usergiftId;
    }

    public void setUsergiftId(long usergiftId) {
        this.usergiftId = usergiftId;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }
}
