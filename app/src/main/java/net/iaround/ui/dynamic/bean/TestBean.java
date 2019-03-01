package net.iaround.ui.dynamic.bean;

import java.util.List;

/**
 * Created by admin on 2017/4/21.
 */

public class TestBean {

    /**
     * dynamic : {"user":{"icon":"http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg","distance":0,"userid":61355920,"age":20,"gender":"m","svip":0,"relation":1,"nickname":"Yestin Zhao","contact":0,"viplevel":0,"charmnum":2151},"dynamic":{"address":"北京市 京仪科大厦B座","type":1,"content":"红红火火恍恍惚惚","url":"","distance":9,"userid":61355920,"datetime":1492651020000,"parentid":0,"dynamicid":117206049,"dynamiccategory":0,"dynamicsource":0},"reviewinfo":{"total":1,"reviews":[{"content":"哈哈哈","user":{"icon":"http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg","distance":0,"userid":61355920,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"m","relation":0,"nickname":"Yestin Zhao","contact":0,"charmnum":2151},"datetime":1492748377592,"reviewid":105408888}]},"loveinfo":{"total":7,"curruserlove":1,"loveusers":[{"icon":"http://p7.iaround.com/201703/25/FACE/03095b55be161205949c7ff1a4c98ab5_s.jpg","userid":61673255,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"m","nickname":"刀鱼Marshal","moodtext":"","lastonlinetime":1492748484000},{"icon":"http://p4.iaround.com/201704/20/FACE/087fa1cdfdbc997dfcf358741a6826b0_s.jpg","userid":61900120,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"大眼睛","moodtext":"","lastonlinetime":1492748426000},{"icon":"http://p4.iaround.com/201704/20/FACE/f3843fc3633a4fb0ec46bee23223be0b_s.jpg","userid":61899681,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"总想闹","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p7.iaround.com/201704/18/FACE/2ca5a3171a6ab642b1c6b921da37dfab_s.jpg","userid":61899827,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"小菜鸟","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p1.iaround.com/201704/18/FACE/92eb7865b1d82b3e147c07f06c626795_s.jpg","userid":61899282,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"东北扛把子","moodtext":"","lastonlinetime":1492748453000},{"icon":"http://p1.iaround.com/201704/20/FACE/03e59bb863fb588a5249c91804f7c6a9_s.jpg","userid":61899686,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"千山暮雪","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p7.iaround.com/201704/21/FACE/5bf7fc001bf413f6fccf855540bbb473_s.jpg","userid":61899519,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"许谁一世真情~~","moodtext":"","lastonlinetime":1492748426000}]}}
     */

    private DynamicBeanX dynamic;

    public DynamicBeanX getDynamic() {
        return dynamic;
    }

    public void setDynamic(DynamicBeanX dynamic) {
        this.dynamic = dynamic;
    }

    public static class DynamicBeanX {
        /**
         * user : {"icon":"http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg","distance":0,"userid":61355920,"age":20,"gender":"m","svip":0,"relation":1,"nickname":"Yestin Zhao","contact":0,"viplevel":0,"charmnum":2151}
         * dynamic : {"address":"北京市 京仪科大厦B座","type":1,"content":"红红火火恍恍惚惚","url":"","distance":9,"userid":61355920,"datetime":1492651020000,"parentid":0,"dynamicid":117206049,"dynamiccategory":0,"dynamicsource":0}
         * reviewinfo : {"total":1,"reviews":[{"content":"哈哈哈","user":{"icon":"http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg","distance":0,"userid":61355920,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"m","relation":0,"nickname":"Yestin Zhao","contact":0,"charmnum":2151},"datetime":1492748377592,"reviewid":105408888}]}
         * loveinfo : {"total":7,"curruserlove":1,"loveusers":[{"icon":"http://p7.iaround.com/201703/25/FACE/03095b55be161205949c7ff1a4c98ab5_s.jpg","userid":61673255,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"m","nickname":"刀鱼Marshal","moodtext":"","lastonlinetime":1492748484000},{"icon":"http://p4.iaround.com/201704/20/FACE/087fa1cdfdbc997dfcf358741a6826b0_s.jpg","userid":61900120,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"大眼睛","moodtext":"","lastonlinetime":1492748426000},{"icon":"http://p4.iaround.com/201704/20/FACE/f3843fc3633a4fb0ec46bee23223be0b_s.jpg","userid":61899681,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"总想闹","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p7.iaround.com/201704/18/FACE/2ca5a3171a6ab642b1c6b921da37dfab_s.jpg","userid":61899827,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"小菜鸟","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p1.iaround.com/201704/18/FACE/92eb7865b1d82b3e147c07f06c626795_s.jpg","userid":61899282,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"东北扛把子","moodtext":"","lastonlinetime":1492748453000},{"icon":"http://p1.iaround.com/201704/20/FACE/03e59bb863fb588a5249c91804f7c6a9_s.jpg","userid":61899686,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"千山暮雪","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p7.iaround.com/201704/21/FACE/5bf7fc001bf413f6fccf855540bbb473_s.jpg","userid":61899519,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"许谁一世真情~~","moodtext":"","lastonlinetime":1492748426000}]}
         */

        private UserBean user;
        private DynamicBean dynamic;
        private ReviewinfoBean reviewinfo;
        private LoveinfoBean loveinfo;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public DynamicBean getDynamic() {
            return dynamic;
        }

        public void setDynamic(DynamicBean dynamic) {
            this.dynamic = dynamic;
        }

        public ReviewinfoBean getReviewinfo() {
            return reviewinfo;
        }

        public void setReviewinfo(ReviewinfoBean reviewinfo) {
            this.reviewinfo = reviewinfo;
        }

        public LoveinfoBean getLoveinfo() {
            return loveinfo;
        }

        public void setLoveinfo(LoveinfoBean loveinfo) {
            this.loveinfo = loveinfo;
        }

        public static class UserBean {
            /**
             * icon : http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg
             * distance : 0
             * userid : 61355920
             * age : 20
             * gender : m
             * svip : 0
             * relation : 1
             * nickname : Yestin Zhao
             * contact : 0
             * viplevel : 0
             * charmnum : 2151
             */

            private String icon;
            private int distance;
            private int userid;
            private int age;
            private String gender;
            private int svip;
            private int relation;
            private String nickname;
            private int contact;
            private int viplevel;
            private int charmnum;

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getDistance() {
                return distance;
            }

            public void setDistance(int distance) {
                this.distance = distance;
            }

            public int getUserid() {
                return userid;
            }

            public void setUserid(int userid) {
                this.userid = userid;
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

            public int getRelation() {
                return relation;
            }

            public void setRelation(int relation) {
                this.relation = relation;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getContact() {
                return contact;
            }

            public void setContact(int contact) {
                this.contact = contact;
            }

            public int getViplevel() {
                return viplevel;
            }

            public void setViplevel(int viplevel) {
                this.viplevel = viplevel;
            }

            public int getCharmnum() {
                return charmnum;
            }

            public void setCharmnum(int charmnum) {
                this.charmnum = charmnum;
            }
        }

        public static class DynamicBean {
            /**
             * address : 北京市 京仪科大厦B座
             * type : 1
             * content : 红红火火恍恍惚惚
             * url :
             * distance : 9
             * userid : 61355920
             * datetime : 1492651020000
             * parentid : 0
             * dynamicid : 117206049
             * dynamiccategory : 0
             * dynamicsource : 0
             */

            private String address;
            private int type;
            private String content;
            private String url;
            private int distance;
            private int userid;
            private long datetime;
            private int parentid;
            private int dynamicid;
            private int dynamiccategory;
            private int dynamicsource;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getDistance() {
                return distance;
            }

            public void setDistance(int distance) {
                this.distance = distance;
            }

            public int getUserid() {
                return userid;
            }

            public void setUserid(int userid) {
                this.userid = userid;
            }

            public long getDatetime() {
                return datetime;
            }

            public void setDatetime(long datetime) {
                this.datetime = datetime;
            }

            public int getParentid() {
                return parentid;
            }

            public void setParentid(int parentid) {
                this.parentid = parentid;
            }

            public int getDynamicid() {
                return dynamicid;
            }

            public void setDynamicid(int dynamicid) {
                this.dynamicid = dynamicid;
            }

            public int getDynamiccategory() {
                return dynamiccategory;
            }

            public void setDynamiccategory(int dynamiccategory) {
                this.dynamiccategory = dynamiccategory;
            }

            public int getDynamicsource() {
                return dynamicsource;
            }

            public void setDynamicsource(int dynamicsource) {
                this.dynamicsource = dynamicsource;
            }
        }

        public static class ReviewinfoBean {
            /**
             * total : 1
             * reviews : [{"content":"哈哈哈","user":{"icon":"http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg","distance":0,"userid":61355920,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"m","relation":0,"nickname":"Yestin Zhao","contact":0,"charmnum":2151},"datetime":1492748377592,"reviewid":105408888}]
             */

            private int total;
            private List<ReviewsBean> reviews;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public List<ReviewsBean> getReviews() {
                return reviews;
            }

            public void setReviews(List<ReviewsBean> reviews) {
                this.reviews = reviews;
            }

            public static class ReviewsBean {
                /**
                 * content : 哈哈哈
                 * user : {"icon":"http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg","distance":0,"userid":61355920,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"m","relation":0,"nickname":"Yestin Zhao","contact":0,"charmnum":2151}
                 * datetime : 1492748377592
                 * reviewid : 105408888
                 */

                private String content;
                private UserBeanX user;
                private long datetime;
                private int reviewid;

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public UserBeanX getUser() {
                    return user;
                }

                public void setUser(UserBeanX user) {
                    this.user = user;
                }

                public long getDatetime() {
                    return datetime;
                }

                public void setDatetime(long datetime) {
                    this.datetime = datetime;
                }

                public int getReviewid() {
                    return reviewid;
                }

                public void setReviewid(int reviewid) {
                    this.reviewid = reviewid;
                }

                public static class UserBeanX {
                    /**
                     * icon : http://p4.iaround.com/201702/14/FACE/9da43384499280656f62b91717f8eec7_s.jpg
                     * distance : 0
                     * userid : 61355920
                     * vip : 0
                     * horoscope : 10
                     * birthday : 852048000000
                     * age : 20
                     * gender : m
                     * relation : 0
                     * nickname : Yestin Zhao
                     * contact : 0
                     * charmnum : 2151
                     */

                    private String icon;
                    private int distance;
                    private int userid;
                    private int vip;
                    private int horoscope;
                    private long birthday;
                    private int age;
                    private String gender;
                    private int relation;
                    private String nickname;
                    private int contact;
                    private int charmnum;

                    public String getIcon() {
                        return icon;
                    }

                    public void setIcon(String icon) {
                        this.icon = icon;
                    }

                    public int getDistance() {
                        return distance;
                    }

                    public void setDistance(int distance) {
                        this.distance = distance;
                    }

                    public int getUserid() {
                        return userid;
                    }

                    public void setUserid(int userid) {
                        this.userid = userid;
                    }

                    public int getVip() {
                        return vip;
                    }

                    public void setVip(int vip) {
                        this.vip = vip;
                    }

                    public int getHoroscope() {
                        return horoscope;
                    }

                    public void setHoroscope(int horoscope) {
                        this.horoscope = horoscope;
                    }

                    public long getBirthday() {
                        return birthday;
                    }

                    public void setBirthday(long birthday) {
                        this.birthday = birthday;
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

                    public int getRelation() {
                        return relation;
                    }

                    public void setRelation(int relation) {
                        this.relation = relation;
                    }

                    public String getNickname() {
                        return nickname;
                    }

                    public void setNickname(String nickname) {
                        this.nickname = nickname;
                    }

                    public int getContact() {
                        return contact;
                    }

                    public void setContact(int contact) {
                        this.contact = contact;
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

        public static class LoveinfoBean {
            /**
             * total : 7
             * curruserlove : 1
             * loveusers : [{"icon":"http://p7.iaround.com/201703/25/FACE/03095b55be161205949c7ff1a4c98ab5_s.jpg","userid":61673255,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"m","nickname":"刀鱼Marshal","moodtext":"","lastonlinetime":1492748484000},{"icon":"http://p4.iaround.com/201704/20/FACE/087fa1cdfdbc997dfcf358741a6826b0_s.jpg","userid":61900120,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"大眼睛","moodtext":"","lastonlinetime":1492748426000},{"icon":"http://p4.iaround.com/201704/20/FACE/f3843fc3633a4fb0ec46bee23223be0b_s.jpg","userid":61899681,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"总想闹","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p7.iaround.com/201704/18/FACE/2ca5a3171a6ab642b1c6b921da37dfab_s.jpg","userid":61899827,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"小菜鸟","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p1.iaround.com/201704/18/FACE/92eb7865b1d82b3e147c07f06c626795_s.jpg","userid":61899282,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"东北扛把子","moodtext":"","lastonlinetime":1492748453000},{"icon":"http://p1.iaround.com/201704/20/FACE/03e59bb863fb588a5249c91804f7c6a9_s.jpg","userid":61899686,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"千山暮雪","moodtext":"","lastonlinetime":1492748501000},{"icon":"http://p7.iaround.com/201704/21/FACE/5bf7fc001bf413f6fccf855540bbb473_s.jpg","userid":61899519,"vip":0,"horoscope":10,"birthday":852048000000,"age":20,"gender":"f","nickname":"许谁一世真情~~","moodtext":"","lastonlinetime":1492748426000}]
             */

            private int total;
            private int curruserlove;
            private List<LoveusersBean> loveusers;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getCurruserlove() {
                return curruserlove;
            }

            public void setCurruserlove(int curruserlove) {
                this.curruserlove = curruserlove;
            }

            public List<LoveusersBean> getLoveusers() {
                return loveusers;
            }

            public void setLoveusers(List<LoveusersBean> loveusers) {
                this.loveusers = loveusers;
            }

            public static class LoveusersBean {
                /**
                 * icon : http://p7.iaround.com/201703/25/FACE/03095b55be161205949c7ff1a4c98ab5_s.jpg
                 * userid : 61673255
                 * vip : 0
                 * horoscope : 10
                 * birthday : 852048000000
                 * age : 20
                 * gender : m
                 * nickname : 刀鱼Marshal
                 * moodtext :
                 * lastonlinetime : 1492748484000
                 */

                private String icon;
                private int userid;
                private int vip;
                private int horoscope;
                private long birthday;
                private int age;
                private String gender;
                private String nickname;
                private String moodtext;
                private long lastonlinetime;

                public String getIcon() {
                    return icon;
                }

                public void setIcon(String icon) {
                    this.icon = icon;
                }

                public int getUserid() {
                    return userid;
                }

                public void setUserid(int userid) {
                    this.userid = userid;
                }

                public int getVip() {
                    return vip;
                }

                public void setVip(int vip) {
                    this.vip = vip;
                }

                public int getHoroscope() {
                    return horoscope;
                }

                public void setHoroscope(int horoscope) {
                    this.horoscope = horoscope;
                }

                public long getBirthday() {
                    return birthday;
                }

                public void setBirthday(long birthday) {
                    this.birthday = birthday;
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

                public String getNickname() {
                    return nickname;
                }

                public void setNickname(String nickname) {
                    this.nickname = nickname;
                }

                public String getMoodtext() {
                    return moodtext;
                }

                public void setMoodtext(String moodtext) {
                    this.moodtext = moodtext;
                }

                public long getLastonlinetime() {
                    return lastonlinetime;
                }

                public void setLastonlinetime(long lastonlinetime) {
                    this.lastonlinetime = lastonlinetime;
                }
            }
        }
    }
}
