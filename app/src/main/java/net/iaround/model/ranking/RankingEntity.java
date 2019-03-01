package net.iaround.model.ranking;

import net.iaround.model.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ray on 2017/6/13.
 */

public class RankingEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7635127122654526974L;

    /**
     * cat : 1
     * type : 1
     * page_no : 1
     * page_size : 10
     * total_page : 100
     * time : 1497324247000
     * amount : 1000
     * list : [{"userid":2826407,"nickname":"时间冲淡一切","icon":"http://p3.iaround.com/201705/16/FACE/5fbe852e5b85f385e5ed79016f84a7fc_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":1235566,"gender":"m"},{"userid":9994347,"nickname":"风流小生vb","icon":"http://p7.iaround.com/201705/07/FACE/1685f43b941f142002837dac997e50fd_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":1235564,"gender":"m"},{"userid":39944897,"nickname":"看着你吃","icon":"http://p3.iaround.com/201705/16/FACE/5fbe852e5b85f385e5ed79016f84a7fc_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":1235562,"gender":"m"},{"userid":54971357,"nickname":"看着你睡","icon":"http://p7.iaround.com/201705/07/FACE/1685f43b941f142002837dac997e50fd_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":1235560,"gender":"m"},{"userid":2826406,"nickname":"尴尬生动形象","icon":"http://p3.iaround.com/201705/16/FACE/5fbe852e5b85f385e5ed79016f84a7fc_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":1235559,"gender":"m"},{"userid":9994345,"nickname":"感谢信潇洒","icon":"http://p7.iaround.com/201705/07/FACE/1685f43b941f142002837dac997e50fd_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":1235558,"gender":"m"},{"userid":39944893,"nickname":"","icon":"http://p3.iaround.com/201705/16/FACE/5fbe852e5b85f385e5ed79016f84a7fc_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":1235552,"gender":"m"},{"userid":549713511,"nickname":"fg对对对","icon":"http://p7.iaround.com/201705/07/FACE/1685f43b941f142002837dac997e50fd_s.jpg","vip":1,"notes":"","viplevel":1,"age":22,"love":123554,"gender":"m"}]
     */

    private int cat;//类型1魅力 2富豪
    private int type;//1本周 2上周 3本月 4上月
    private int page_no;//页码
    private int page_size;//页码数量
    private int total_page;//总页数
    private long time;//
    private int amount;//总数量
    private List<RankingBean> list;//

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<RankingBean> getList() {
        return list;
    }

    public void setList(List<RankingBean> list) {
        this.list = list;
    }

    public static class RankingBean {
        /**
         * userid : 2826407
         * nickname : 时间冲淡一切
         * icon : http://p3.iaround.com/201705/16/FACE/5fbe852e5b85f385e5ed79016f84a7fc_s.jpg
         * vip : 1
         * notes :
         * viplevel : 1
         * age : 22
         * love : 1235566
         * gender : m
         */

        private int userid;
        private String nickname;
        private String icon;
        private int vip;
        private String notes;
        private int viplevel;
        private int age;
        private int love;
        private String gender;
        private int index;//排名
        private int svip;//是否是会员

        public int getSvip() {
            return svip;
        }

        public void setSvip(int svip) {
            this.svip = svip;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getNickname() {
            if (nickname == null || "null".equals(nickname)) {
                return "";
            } else {
                return nickname;
            }
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getIcon() {
            if (icon == null || "null".equals(icon)) {
                return "";
            } else {
                return icon;
            }
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getVip() {
            return vip;
        }

        public void setVip(int vip) {
            this.vip = vip;
        }

        public String getNotes() {
            if (notes == null || "null".equals(notes)) {
                return "";
            } else {
                return notes;
            }
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public int getViplevel() {
            return viplevel;
        }

        public void setViplevel(int viplevel) {
            this.viplevel = viplevel;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getLove() {
            return love;
        }

        public void setLove(int love) {
            this.love = love;
        }

        public String getGender() {
            if (gender == null || "null".equals(gender)) {
                return "";
            } else {
                return gender;
            }
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }
}
