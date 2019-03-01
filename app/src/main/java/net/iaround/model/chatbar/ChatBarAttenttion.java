package net.iaround.model.chatbar;

import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.ResourceBanner;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ray on 2017/6/12.
 */

public class ChatBarAttenttion extends BaseServerBean implements Serializable {
    private static final long serialVersionUID = -3440061414071692254L;


    /**
     * page_no : 1
     * page_size : 10
     * list : [{"name":"刚好遇见你","type":1,"hot":6959,"family":0,"url":"http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0"},{"name":"刚好遇见你","type":2,"hot":4555,"family":0,"url":"http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0"},{"name":"遇见好声音","type":3,"hot":35533,"family":0,"url":"http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0"},{"name":"刚好遇见你","type":1,"hot":6959,"family":0,"url":"http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0"},{"name":"刚好遇见你","type":2,"hot":4555,"family":0,"url":"http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0"},{"name":"遇见好声音","type":3,"hot":35533,"family":0,"url":"http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0"}]
     * banner : []
     */

    private int page_no;
    private int page_size;
    private int total_page;//总页数
    private int amount;//总数量
    private int response_type;//0我加入的圈子1推荐的圈子
    private List<AttentionBean> list;
    private List<AttentionBean> remaining;

    public List<AttentionBean> getRemaining() {
        return remaining;
    }

    public void setRemaining(List<AttentionBean> remaining) {
        this.remaining = remaining;
    }

    private List<ResourceBanner> banner;

    public int getResponse_type() {
        return response_type;
    }

    public void setResponse_type(int response_type) {
        this.response_type = response_type;
    }

    public int getTotal_page() {

        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<FamilyBean> getFamily() {
        return family;
    }

    public void setFamily(List<FamilyBean> family) {
        this.family = family;
    }

    private List<FamilyBean> family;

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

    public List<AttentionBean> getList() {
        return list;
    }

    public void setList(List<AttentionBean> list) {
        this.list = list;
    }

    public List<ResourceBanner> getBanner() {
        return banner;
    }

    public void setBanner(List<ResourceBanner> banner) {
        this.banner = banner;
    }

    public static class AttentionBean {
        /**
         * name : 刚好遇见你
         * type : 1
         * hot : 6959
         * family : 0
         * url : http://inews.gtimg.com/newsapp_ls/0/1643811484_640330/0
         */
        private int groupid;//聊吧id

        public int getGroupid() {
            return groupid;
        }

        public void setGroupid(int groupid) {
            this.groupid = groupid;
        }

        private String name;// 聊吧名称
        private int type;//1小时热榜 2官方推荐 3广告位
        private int hot;//热度
        private int family;// 是否家族 0否 1是
        private String url;//家族图片url

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class FamilyBean {
        /**
         * name : 我的家族
         * radio : 家族招收给力打手
         * online : 343
         * hot : 65535
         */
        private int groupid;//家族的id
        private int family_url;//家族url

        public int getFamily_url() {
            return family_url;
        }

        public void setFamily_url(int family_url) {
            this.family_url = family_url;
        }

        public int getGroupid() {
            return groupid;
        }

        public void setGroupid(int groupid) {
            this.groupid = groupid;
        }

        private String name;//家族名称
        private String radio;//家族公告
        private int online;//家族在线
        private int hot;//家族热度

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
    }

}
