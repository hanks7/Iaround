package net.iaround.model.chatbar;

import java.io.Serializable;

/**
 * Created by Ray on 2017/6/21.
 *热门聊吧封装bean类，可以添加类型
 */

public class ChatBarPopularType implements Serializable {
    private static final long serialVersionUID = 7635127122654526974L;
    private int type;
    private Object objectLeft;
    private Object objectRight;
    private Object objectBanner;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObjectLeft() {
        return objectLeft;
    }

    public void setObjectLeft(Object objectLeft) {
        this.objectLeft = objectLeft;
    }

    public Object getObjectRight() {
        return objectRight;
    }

    public void setObjectRight(Object objectRight) {
        this.objectRight = objectRight;
    }

    public Object getObjectBanner() {
        return objectBanner;
    }

    public void setObjectBanner(Object objectBanner) {
        this.objectBanner = objectBanner;
    }
}
