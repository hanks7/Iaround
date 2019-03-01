package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * 语音主播资质详情实体类
 */
public class AudioDetailsBean extends BaseServerBean {
    public long targetUid; //主播id
    public String nickName;
    public String icon;
    public String addr;//主播所在地
    public ArrayList<String> photos;//主播资质图
    public String voice;//音频地址
    public String description;//描述
    public int price;//价格
    public String unit;//单位
    public String sex;//性别 f-女 m-男

}
