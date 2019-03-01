package net.iaround.model.chatbar;

import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ray on 2017/6/12.
 */

public class ChatBarFamily extends BaseServerBean implements Serializable {
    private static final long serialVersionUID = -3440061414071692254L;


    /**
     * page_size : 10
     * page_no : 1
     * family : []
     * join_group : [{"groupid":103,"name":"酒色财气","hot":300,"family":0,"familurl":"http://p1.dev.iaround.com/201706/15/FACE_GROUP/e0698423291afb3a7160a05903514ddd.jpg"},{"groupid":106,"name":"小清新","hot":0,"family":0,"familurl":"http://p1.dev.iaround.com/201706/19/FACE_GROUP/c08fdfe20fff104cadc7f6a110c29530.jpg"},{"groupid":107,"name":"zoo","hot":0,"family":0,"familurl":"http://p1.dev.iaround.com/201706/19/FACE_GROUP/85994273ba5f15ee4378968327090ea1.jpg"}]
     * push_group : [{"name":"酒色财气","groupid":103,"url":"http://p1.dev.iaround.com/201706/15/FACE_GROUP/e0698423291afb3a7160a05903514ddd.jpg","type":3,"hot":300,"family":0}]
     * push_amount : 1
     * push_total_page : 1
     * status : 200
     */

    private int page_size;
    private int page_no;
    private int push_amount;
    private int push_total_page;
    private int status;
    private List<FamilyNew> family;
    private List<JoinGroupBean> join_group;
    private List<PushGroupBean> push_group;

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public int getPush_amount() {
        return push_amount;
    }

    public void setPush_amount(int push_amount) {
        this.push_amount = push_amount;
    }

    public int getPush_total_page() {
        return push_total_page;
    }

    public void setPush_total_page(int push_total_page) {
        this.push_total_page = push_total_page;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<FamilyNew> getFamily() {
        return family;
    }

    public void setFamily(List<FamilyNew> family) {
        this.family = family;
    }

    public List<JoinGroupBean> getJoin_group() {
        return join_group;
    }

    public void setJoin_group(List<JoinGroupBean> join_group) {
        this.join_group = join_group;
    }

    public List<PushGroupBean> getPush_group() {
        return push_group;
    }

    public void setPush_group(List<PushGroupBean> push_group) {
        this.push_group = push_group;
    }

    public static class JoinGroupBean {
        /**
         * groupid : 103
         * name : 酒色财气
         * hot : 300
         * family : 0
         * familurl : http://p1.dev.iaround.com/201706/15/FACE_GROUP/e0698423291afb3a7160a05903514ddd.jpg
         */

        private int groupid;
        private String name;
        private int hot;
        private int family;
        private String familurl;
        private int verify;

        public int getGroupid() {
            return groupid;
        }

        public void setGroupid(int groupid) {
            this.groupid = groupid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getHot() {
            return hot;
        }

        public void setHot(int hot) {
            this.hot = hot;
        }

        public int getFamily() {
            return family;
        }

        public void setFamily(int family) {
            this.family = family;
        }

        public String getFamilurl() {
            return familurl;
        }

        public void setFamilurl(String familurl) {
            this.familurl = familurl;
        }

        public int getVerify() {
            return verify;
        }

        public void setVerify(int verify) {
            this.verify = verify;
        }
    }

    public static class PushGroupBean {
        /**
         * name : 酒色财气
         * groupid : 103
         * url : http://p1.dev.iaround.com/201706/15/FACE_GROUP/e0698423291afb3a7160a05903514ddd.jpg
         * type : 3
         * hot : 300
         * family : 0
         */

        private String name;
        private int groupid;
        private String url;
        private int type;
        private int hot;
        private int family;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getGroupid() {
            return groupid;
        }

        public void setGroupid(int groupid) {
            this.groupid = groupid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getHot() {
            return hot;
        }

        public void setHot(int hot) {
            this.hot = hot;
        }

        public int getFamily() {
            return family;
        }

        public void setFamily(int family) {
            this.family = family;
        }
    }

    public static class FamilyNew {
        /**
         * name : 我的家族
         * radio : 家族招收给力打手
         * online : 343
         * hot : 65535
         */
        private int groupid;//家族的id
        private String name;//家族名称
        private String radio;//家族公告
        private int online;//家族在线
        private int hot;//家族热度
        private int family;//是不是家族
        private String familurl;
        private int members_count;//家族在线人数
        private int verify;//0 通过 ，2 审核中

        public int getMembers_count() {
            return members_count;
        }

        public void setMembers_count(int members_count) {
            this.members_count = members_count;
        }

        public int getFamily() {
            return family;
        }

        public void setFamily(int family) {
            this.family = family;
        }

        public String getFamilurl() {
            return familurl;
        }

        public void setFamilurl(String familurl) {
            this.familurl = familurl;
        }

        public int getGroupid() {
            return groupid;
        }

        public void setGroupid(int groupid) {
            this.groupid = groupid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRadio() {
            return radio;
        }

        public void setRadio(String radio) {
            this.radio = radio;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }

        public int getHot() {
            return hot;
        }

        public void setHot(int hot) {
            this.hot = hot;
        }

        public int getVerify() {
            return verify;
        }

        public void setVerify(int verify) {
            this.verify = verify;
        }
    }

}
