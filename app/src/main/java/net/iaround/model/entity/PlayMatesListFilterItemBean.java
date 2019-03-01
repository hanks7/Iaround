package net.iaround.model.entity;

import java.io.Serializable;

/**
 * Created by yz on 2018/8/8.
 */

public class PlayMatesListFilterItemBean extends BaseEntity implements Serializable{
    public String itemId;// all-不限
    public String itemName;//id对应的字符串

    public boolean isSelected = false;//是否被选中
}
