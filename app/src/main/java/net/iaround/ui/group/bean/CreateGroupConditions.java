package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

public class CreateGroupConditions extends BaseServerBean {

    /**
     * 剩余可以创建圈子的数量
     */
    public int groupnum;

    /**
     * 是否绑定手机（1-绑定、0-未绑定）
     */
    public int bindphone;

    /**
     * 用户剩余钻石数
     */
    public int diamondnum;

    /***用户剩余金币数*/
    public int gold;
    /**
     * 圈子类型信息
     */
    public ArrayList<GroupTypeItem> items;
    /**
     * 当前用户等级
     */
    public int level;
    /**
     * 当前用户地址
     */
    public int address;
    /**
     * 当前用户lat,lng
     */
    public long lat;
    public long lng;
    public String sddress;

    public String getSddress() {
        return sddress;
    }

    public void setSddress(String sddress) {
        this.sddress = sddress;
    }
}

