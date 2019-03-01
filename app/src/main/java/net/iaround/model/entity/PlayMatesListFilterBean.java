package net.iaround.model.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yz on 2018/8/8.
 */

public class PlayMatesListFilterBean extends BaseEntity implements Serializable{
    public ArrayList<PlayMatesListFilterItemBean> genders;
    public ArrayList<PlayMatesListFilterItemBean> prices;
    public ArrayList<PlayMatesListFilterItemBean> levels;
}
