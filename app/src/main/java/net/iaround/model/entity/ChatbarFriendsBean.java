package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/16 10:59
 * Email：15369302822@163.com
 */
public class ChatbarFriendsBean extends BaseServerBean {


    private List<UsersBean> fans;
    private List<UsersBean> freshman;
    private List<UsersBean> users;

    private int amount;//总数
    private int pageno;
    private int pagesize;


    public List<UsersBean> getUsers() {
        return users;
    }

    public void setUsers(List<UsersBean> users) {
        this.users = users;
    }

    public List<UsersBean> getFreshman() {
        return freshman;
    }

    public void setFreshman(List<UsersBean> freshman) {
        this.freshman = freshman;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public List<UsersBean> getFans() {
        return fans;
    }

    public void setFans(List<UsersBean> fans) {
        this.fans = fans;
    }

    public static class UsersBean {
        /**
         * user : {"icon":"http://p7.iaround.com/201705/18/FACE/86f0af9031ea71ff617dfdaea8d5c523_s.jpg","lat":40007700,"lng":116349657,"userid":62212431,"weibo":"260,000,","notes":"","vip":0,"occupation":0,"age":20,"gender":"m","svip":0,"lastonlinetime":1502177456000,"selftext":"","isonline":"n","distance":0,"nickname":"2017Andoni","todayphotos":0,"photonum":0,"forbid":0,"viplevel":0,"photouploadleft":0,"todayphotostotal":0,"photouploadtotal":0,"expire":0,"charmnum":0}
         * distance : 4167
         */

        private UserBean user;
        private int distance;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public static class UserBean {
            /**
             * icon : http://p7.iaround.com/201705/18/FACE/86f0af9031ea71ff617dfdaea8d5c523_s.jpg
             * lat : 40007700
             * lng : 116349657
             * userid : 62212431
             * weibo : 260,000,
             * notes :
             * vip : 0
             * occupation : 0
             * age : 20
             * gender : m
             * svip : 0
             * lastonlinetime : 1502177456000
             * selftext :
             * isonline : n
             * distance : 0
             * nickname : 2017Andoni
             * todayphotos : 0
             * photonum : 0
             * forbid : 0
             * viplevel : 0
             * photouploadleft : 0
             * todayphotostotal : 0
             * photouploadtotal : 0
             * expire : 0
             * charmnum : 0
             */

            private String icon;
            private int lat;
            private int lng;
            private int userid;
            private String weibo;
            private String notes;
            private int vip;
            private int occupation;
            private int age;
            private String gender;
            private int svip;
            private long lastonlinetime;
            private String selftext;
            private String isonline;
            private int distance;
            private String nickname;
            private int todayphotos;
            private int photonum;
            private int forbid;
            private int viplevel;
            private int photouploadleft;
            private int todayphotostotal;
            private int photouploadtotal;
            private int expire;
            private int charmnum;

            private boolean isSelected;//表示当前用户是否被选中

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getLat() {
                return lat;
            }

            public void setLat(int lat) {
                this.lat = lat;
            }

            public int getLng() {
                return lng;
            }

            public void setLng(int lng) {
                this.lng = lng;
            }

            public int getUserid() {
                return userid;
            }

            public void setUserid(int userid) {
                this.userid = userid;
            }

            public String getWeibo() {
                return weibo;
            }

            public void setWeibo(String weibo) {
                this.weibo = weibo;
            }

            public String getNotes() {
                return notes;
            }

            public void setNotes(String notes) {
                this.notes = notes;
            }

            public int getVip() {
                return vip;
            }

            public void setVip(int vip) {
                this.vip = vip;
            }

            public int getOccupation() {
                return occupation;
            }

            public void setOccupation(int occupation) {
                this.occupation = occupation;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public int getSvip() {
                return svip;
            }

            public void setSvip(int svip) {
                this.svip = svip;
            }

            public long getLastonlinetime() {
                return lastonlinetime;
            }

            public void setLastonlinetime(long lastonlinetime) {
                this.lastonlinetime = lastonlinetime;
            }

            public String getSelftext() {
                return selftext;
            }

            public void setSelftext(String selftext) {
                this.selftext = selftext;
            }

            public String getIsonline() {
                return isonline;
            }

            public void setIsonline(String isonline) {
                this.isonline = isonline;
            }

            public int getDistance() {
                return distance;
            }

            public void setDistance(int distance) {
                this.distance = distance;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getTodayphotos() {
                return todayphotos;
            }

            public void setTodayphotos(int todayphotos) {
                this.todayphotos = todayphotos;
            }

            public int getPhotonum() {
                return photonum;
            }

            public void setPhotonum(int photonum) {
                this.photonum = photonum;
            }

            public int getForbid() {
                return forbid;
            }

            public void setForbid(int forbid) {
                this.forbid = forbid;
            }

            public int getViplevel() {
                return viplevel;
            }

            public void setViplevel(int viplevel) {
                this.viplevel = viplevel;
            }

            public int getPhotouploadleft() {
                return photouploadleft;
            }

            public void setPhotouploadleft(int photouploadleft) {
                this.photouploadleft = photouploadleft;
            }

            public int getTodayphotostotal() {
                return todayphotostotal;
            }

            public void setTodayphotostotal(int todayphotostotal) {
                this.todayphotostotal = todayphotostotal;
            }

            public int getPhotouploadtotal() {
                return photouploadtotal;
            }

            public void setPhotouploadtotal(int photouploadtotal) {
                this.photouploadtotal = photouploadtotal;
            }

            public int getExpire() {
                return expire;
            }

            public void setExpire(int expire) {
                this.expire = expire;
            }

            public int getCharmnum() {
                return charmnum;
            }

            public void setCharmnum(int charmnum) {
                this.charmnum = charmnum;
            }
        }
    }
}
