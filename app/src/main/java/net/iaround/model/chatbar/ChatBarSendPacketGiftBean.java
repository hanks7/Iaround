package net.iaround.model.chatbar;

import net.iaround.model.entity.BaseEntity;

import java.io.Serializable;

/**
 * Created by Ray on 2017/7/17.
 */

public class ChatBarSendPacketGiftBean extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7635127122654526974L;


    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    /**
     * send_user : {"userid":61001275,"nickname":"布达拉宫","icon":"http://p1.dev.iaround.com/201707/07/FACE/f97e6a97fc56cafac62de2b3ad3f266a_s.jpg","vip":0,"viplevel":0,"age":22,"gender":"m"}
     * receive_user : {"userid":"61001273","nickname":"时间从来不","icon":"http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg","vip":0,"viplevel":0,"age":33,"gender":"m"}
     * gift : {"gift_id":45,"gift_icon":"http://p1.iaround.net/201201/06/c6baccd0f52cfc79137d44da15b3df9e.jpg","gift_name":"Yacht|私家游艇|私家遊艇","gift_num":0,"user_gift_id":602367,"combo_type":0,"combo_name":"香料|香料|香料","combo_num":1,"combo_value":1,"combo_animation":""}
     */
    private long group_id;

    private SendUserBean send_user;
    private ReceiveUserBean receive_user;
    private GiftBean gift;

    public SendUserBean getSend_user() {
        return send_user;
    }

    public void setSend_user(SendUserBean send_user) {
        this.send_user = send_user;
    }

    public ReceiveUserBean getReceive_user() {
        return receive_user;
    }

    public void setReceive_user(ReceiveUserBean receive_user) {
        this.receive_user = receive_user;
    }

    public GiftBean getGift() {
        return gift;
    }

    public void setGift(GiftBean gift) {
        this.gift = gift;
    }

    public static class SendUserBean {
        /**
         * userid : 61001275
         * nickname : 布达拉宫
         * icon : http://p1.dev.iaround.com/201707/07/FACE/f97e6a97fc56cafac62de2b3ad3f266a_s.jpg
         * vip : 0
         * viplevel : 0
         * age : 22
         * gender : m
         */

        public int userid;
        public String nickname;
        public String icon;
        public int vip;
        public int viplevel;
        public int age;
        public String gender;

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getNickname() {
            return nickname;
        }

        public String getNoteName(){
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getIcon() {
            return icon;
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

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        @Override
        public String toString() {
            return "SendUserBean{" +
                    "userid=" + userid +
                    ", nickname='" + nickname + '\'' +
                    ", icon='" + icon + '\'' +
                    ", vip=" + vip +
                    ", viplevel=" + viplevel +
                    ", age=" + age +
                    ", gender='" + gender + '\'' +
                    '}';
        }
    }

    public static class ReceiveUserBean {
        /**
         * userid : 61001273
         * nickname : 时间从来不
         * icon : http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg
         * vip : 0
         * viplevel : 0
         * age : 33
         * gender : m
         */

        private int userid;
        private String nickname;
        private String icon;
        private int vip;
        private int viplevel;
        private int age;
        private String gender;

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }

        public String getNickname() {
            return nickname;
        }

        /**
         * 优先返回备注
         * @return
         */
        public String getNoteName() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getIcon() {
            return icon;
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

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        @Override
        public String toString() {
            return "ReceiveUserBean{" +
                    "userid='" + userid + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", icon='" + icon + '\'' +
                    ", vip=" + vip +
                    ", viplevel=" + viplevel +
                    ", age=" + age +
                    ", gender='" + gender + '\'' +
                    '}';
        }
    }

    public static class GiftBean {
        /**
         * gift_id : 45
         * gift_icon : http://p1.iaround.net/201201/06/c6baccd0f52cfc79137d44da15b3df9e.jpg
         * gift_name : Yacht|私家游艇|私家遊艇
         * gift_num : 0
         * user_gift_id : 602367
         * combo_type : 0
         * combo_name : 香料|香料|香料
         * combo_num : 1
         * combo_value : 1
         * combo_animation :
         */

        private int gift_id; // 礼物id
        private String gift_icon;// 礼物图标
        private String gift_name;// 礼物名称
        private int gift_num; // 剩余礼物数量
        private long user_gift_id;// 赠送礼物成功后，返回的标识ID,下次赠送同一个礼物时带上改参数，视为是否连击标识
        private int combo_type;// 连击类型 0无连送;1累计;2数组
        private String combo_name; //连击名称
        private int combo_num;//连击次数 1 2 3
        private int combo_value; //连击值 1 88 666 1314
        private String combo_animation; //动画标识
        private String gift_desc;//单位
        private int gift_charm_num;//魅力值
        private int gift_exp;//经验值
        private int gift_currencytype;//货币类型
        private int gift_price;//礼物的价格
        private String combo_resource_url;//礼物的下载资源地址

        public int getGift_price() {
            return gift_price;
        }

        public void setGift_price(int gift_price) {
            this.gift_price = gift_price;
        }

        public String getGift_desc() {
            return gift_desc;
        }

        public void setGift_desc(String gift_desc) {
            this.gift_desc = gift_desc;
        }

        public int getGift_charm_num() {
            return gift_charm_num;
        }

        public void setGift_charm_num(int gift_charm_num) {
            this.gift_charm_num = gift_charm_num;
        }

        public int getGift_exp() {
            return gift_exp;
        }

        public void setGift_exp(int gift_exp) {
            this.gift_exp = gift_exp;
        }

        public int getGift_currencytype() {
            return gift_currencytype;
        }

        public void setGift_currencytype(int gift_currencytype) {
            this.gift_currencytype = gift_currencytype;
        }

        public int getGift_id() {
            return gift_id;
        }

        public void setGift_id(int gift_id) {
            this.gift_id = gift_id;
        }

        public String getGift_icon() {
            return gift_icon;
        }

        public void setGift_icon(String gift_icon) {
            this.gift_icon = gift_icon;
        }

        public String getGift_name() {
            return gift_name;
        }

        public void setGift_name(String gift_name) {
            this.gift_name = gift_name;
        }

        public int getGift_num() {
            return gift_num;
        }

        public void setGift_num(int gift_num) {
            this.gift_num = gift_num;
        }

        public long getUser_gift_id() {
            return user_gift_id;
        }

        public void setUser_gift_id(long user_gift_id) {
            this.user_gift_id = user_gift_id;
        }

        public int getCombo_type() {
            return combo_type;
        }

        public void setCombo_type(int combo_type) {
            this.combo_type = combo_type;
        }

        public String getCombo_name() {
            return combo_name;
        }

        public void setCombo_name(String combo_name) {
            this.combo_name = combo_name;
        }

        public int getCombo_num() {
            return combo_num;
        }

        public void setCombo_num(int combo_num) {
            this.combo_num = combo_num;
        }

        public int getCombo_value() {
            return combo_value;
        }

        public void setCombo_value(int combo_value) {
            this.combo_value = combo_value;
        }

        public String getCombo_animation() {
            return combo_animation == null ? "" : combo_animation;
        }

        public void setCombo_animation(String combo_animation) {
            this.combo_animation = combo_animation;
        }

        public String getCombo_resource_url() {
            return combo_resource_url;
        }

        public void setCombo_resource_url(String combo_resource_url) {
            this.combo_resource_url = combo_resource_url;
        }

        @Override
        public String toString() {
            return "GiftBean{" +
                    "gift_id=" + gift_id +
                    ", gift_icon='" + gift_icon + '\'' +
                    ", gift_name='" + gift_name + '\'' +
                    ", gift_num=" + gift_num +
                    ", user_gift_id=" + user_gift_id +
                    ", combo_type=" + combo_type +
                    ", combo_name='" + combo_name + '\'' +
                    ", combo_num=" + combo_num +
                    ", combo_value=" + combo_value +
                    ", combo_animation='" + combo_animation + '\'' +
                    ", gift_desc='" + gift_desc + '\'' +
                    ", gift_charm_num=" + gift_charm_num +
                    ", gift_exp=" + gift_exp +
                    ", gift_currencytype=" + gift_currencytype +
                    ", gift_price=" + gift_price +
                    ", combo_resource_url='" + combo_resource_url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ChatBarSendPacketGiftBean{" +
//                "send_user=" + send_user +
//                ", receive_user=" + receive_user +
                ", gift=" + gift +
                '}';
    }
}
