package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 *
 * 视频列表bean
 * Created by Administrator on 2017/12/11.
 */

public class VideoAnchorBean extends BaseServerBean {

    public int pageno;

    public int pagesize;

    public int amount;

    public ArrayList< VideoAnchorItem > list;


    public static class VideoAnchorItem {
        public long uid;
        public String nickName;
        public String notes;
        public String gender;
        public String moodText;
        public int status;
        public String pic;
        public String video;
        public String firstvideopic;
        public int age;

    }



}
