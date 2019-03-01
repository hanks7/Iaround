package net.iaround.model.obj;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class: 动态对象
 * Author：gh
 * Date: 2016/12/10 17:44
 * Email：jt_gaohang@163.com
 */
public class Dynamic implements Serializable{

    public long dynid;
    public String nickname;
    public String headPic;
    public int gender;
    public double distance;
    public long time;
    public int vip;
    public long brithday;
    public String content;
    public String address;
    public long uid;
    public int horoscope;
    public int loveCount;
    public int commentCount;
    public int loved;//当前用户是否点赞（0-否，1-是）
    public ArrayList<String> dynamicPic;

    public boolean isCurrentHanleView = false;//是否当前点赞或取消点赞的View


}
