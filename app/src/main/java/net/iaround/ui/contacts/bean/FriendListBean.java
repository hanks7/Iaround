package net.iaround.ui.contacts.bean;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * Created by admin on 2017/4/15.
 */

public class FriendListBean extends BaseServerBean {
    /**
     * 联系人列表的bean类
     */

    private int groupnum;
    private int friendsnum;
    private int attentionsnum;
    private Object fan;
    private int fansnum;
    private List<FriendsBean> friends;
    private List<AttentionsBean> attentions;

    public int getGroupnum() {
        return groupnum;
    }

    public void setGroupnum(int groupnum) {
        this.groupnum = groupnum;
    }

    public int getFriendsnum() {
        return friendsnum;
    }

    public void setFriendsnum(int friendsnum) {
        this.friendsnum = friendsnum;
    }

    public int getAttentionsnum() {
        return attentionsnum;
    }

    public void setAttentionsnum(int attentionsnum) {
        this.attentionsnum = attentionsnum;
    }

    public Object getFan() {
        return fan;
    }

    public void setFan(Object fan) {
        this.fan = fan;
    }

    public int getFansnum() {
        return fansnum;
    }

    public void setFansnum(int fansnum) {
        this.fansnum = fansnum;
    }

    public List<FriendsBean> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendsBean> friends) {
        this.friends = friends;
    }

    public List<AttentionsBean> getAttentions() {
        return attentions;
    }

    public void setAttentions(List<AttentionsBean> attentions) {
        this.attentions = attentions;
    }

    public static class FriendsBean {
        /**
         * user : {"icon":"http://p2.iaround.com/201704/14/FACE/3387d1dc011b94b18a059f0d4ee76fd6_s.jpg","lat":35199354,"lng":113260045,"userid":61855267,"weibo":"","notes":"","vip":0,"occupation":0,"age":20,"gender":"f","svip":0,"lastonlinetime":1492237162000,"selftext":"","isonline":"n","distance":0,"nickname":"空城空心空等","todayphotos":0,"photonum":0,"forbid":0,"viplevel":0,"photouploadleft":0,"todayphotostotal":0,"photouploadtotal":0,"expire":0,"charmnum":0}
         * distance : 596483
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
             * icon : http://p2.iaround.com/201704/14/FACE/3387d1dc011b94b18a059f0d4ee76fd6_s.jpg
             * lat : 35199354
             * lng : 113260045
             * userid : 61855267
             * weibo :
             * notes :
             * vip : 0
             * occupation : 0
             * age : 20
             * gender : f
             * svip : 0
             * lastonlinetime : 1492237162000
             * selftext :
             * isonline : n
             * distance : 0
             * nickname : 空城空心空等
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

            /**
             * @return
             * @Title: isOnline
             * @Description: 当前用户是否在线
             */
            public boolean online() {
                return this.isonline.equals("y");
            }

            /**
             * @return
             * @Title: isForbidUser
             * @Description: 用户是否封停
             */
            public boolean forbidUser() {
                return this.forbid != 0;
            }
        }
    }

    public static class AttentionsBean {
        /**
         * user : {"icon":"","lat":45882972,"lng":126581988,"userid":61861389,"weibo":"260,000,","notes":"","vip":0,"occupation":0,"age":25,"gender":"f","svip":0,"lastonlinetime":1492183105000,"selftext":"","isonline":"n","distance":0,"nickname":"61861389","todayphotos":0,"photonum":0,"forbid":1,"viplevel":0,"photouploadleft":0,"todayphotostotal":0,"photouploadtotal":0,"expire":0,"charmnum":0}
         * distance : 1061695
         * relation : 3
         * newflag : 0
         */

        private UserBeanX user;
        private int distance;
        private int relation;
        private int newflag;

        public UserBeanX getUser() {
            return user;
        }

        public void setUser(UserBeanX user) {
            this.user = user;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getRelation() {
            return relation;
        }

        public void setRelation(int relation) {
            this.relation = relation;
        }

        public int getNewflag() {
            return newflag;
        }

        public void setNewflag(int newflag) {
            this.newflag = newflag;
        }

        public static class UserBeanX {
            /**
             * icon :
             * lat : 45882972
             * lng : 126581988
             * userid : 61861389
             * weibo : 260,000,
             * notes :
             * vip : 0
             * occupation : 0
             * age : 25
             * gender : f
             * svip : 0
             * lastonlinetime : 1492183105000
             * selftext :
             * isonline : n
             * distance : 0
             * nickname : 61861389
             * todayphotos : 0
             * photonum : 0
             * forbid : 1
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
