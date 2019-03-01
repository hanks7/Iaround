package net.iaround.model.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: 我的动态消息
 * Author：gh
 * Date: 2017/2/18 14:37
 * Email：jt_gaohang@163.com
 */
public class DynamicMessageBean {

    public ArrayList<DynamicLoveModel> love;
    public List<DynamicComment> comment;

    public class DynamicComment{
        public long uid;
        public long dynid;
        public String headPic;
        public String nickname;
        public int gender;
        public int vip;
        public long time;
        public long birthday;
        public String pic;
        public String content;
        public String comment;

    }

    public int existData;
}
