package net.iaround.ui.store;

import android.content.Context;

import net.iaround.BaseApplication;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;

import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/7/29 16:19
 * Email：15369302822@163.com
 */
public class ReceiveGiftBean extends BaseServerBean {

    /**
     * pageno : 1
     * pagesize : 24
     * amount : 22
     * list : [{"user":{"icon":"http://wx.qlogo.cn/mmopen/LQFYGQichZDibzTMicT1ib8Os0WXhEd2IH2EKWw2LhvHYOQQyIE6D90MyD3ByQ05R7rDicvJNPMeiaUbTmxzZxGDyp9FhceGr8P6kv/0","nickname":"史毅力👑","userid":"61001269","vip":"0","gender":"m","age":18,"lastonlinetime":1501248305000,"lat":"39971431","lng":"116337711","lelftext":"我个性 你签名","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1497875664000,"giftnum":2,"charmnum":1776,"gift":{"giftid":43,"name":"Ferrari|法拉利|法拉利","icon":"http://p1.iaround.net/201201/06/af35cc3fe1b553975dfc211c39599620.jpg","goldnum":10000,"color":"","charmnum":888,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Ferrari|法拉利|法拉利","ishot":0,"isnew":0,"discountgoldnum":10000,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p1.dev.iaround.com/201707/07/FACE/f97e6a97fc56cafac62de2b3ad3f266a_s.jpg","nickname":"布达拉宫","userid":"61001275","vip":"0","gender":"m","age":22,"lastonlinetime":1501314788000,"lat":"0","lng":"0","lelftext":"你好呀哈哈哈","weibo":"981","forbid":"0","photonum":1,"isonline":"y","notes":"","occupation":"3"},"datetime":1500004182000,"giftnum":38,"charmnum":33744,"gift":{"giftid":45,"name":"Yacht|私家游艇|私家遊艇","icon":"http://p1.iaround.net/201201/06/c6baccd0f52cfc79137d44da15b3df9e.jpg","goldnum":8880,"color":"","charmnum":888,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Yacht|私家游艇|私家遊艇","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p2.iaround.com/201706/13/FACE/23bac9895cb023b1a0abe5a057a4cee3_s.jpg","nickname":"佛祖","userid":"61001267","vip":"0","gender":"m","age":25,"lastonlinetime":1500519119000,"lat":"0","lng":"0","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":0},"datetime":1497875707000,"giftnum":1,"charmnum":500,"gift":{"giftid":325,"name":"Golden Horse|马上有钱|馬上有錢","icon":"http://g.test.iaround.com/manager_up/images/50236b159236bd766866b426a76f4447.png","goldnum":5000,"color":"","charmnum":500,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":1,"giftdesc":"Golden Horse|马上有钱|馬上有錢","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p1.test.iaround.com/114_s.png","nickname":"遇见客服","userid":"114","vip":"1","gender":"f","age":20,"lastonlinetime":1501143497000,"lat":"23122458","lng":"113370826","lelftext":"遇见你，认识我","weibo":"120001","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1497924001000,"giftnum":3,"charmnum":6000,"gift":{"giftid":338,"name":"Love pearl|真爱珍珠|真爱珍珠","icon":"http://g.test.iaround.com/manager_up/images/f50b861eeced43f7bec7f456fde13047.jpg","goldnum":1,"color":"","charmnum":2000,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Love pearl|真爱珍珠|真爱珍珠","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":500}},{"user":{"icon":"http://wx.qlogo.cn/mmopen/LQFYGQichZDibzTMicT1ib8Os0WXhEd2IH2EKWw2LhvHYOQQyIE6D90MyD3ByQ05R7rDicvJNPMeiaUbTmxzZxGDyp9FhceGr8P6kv/0","nickname":"史毅力👑","userid":"61001269","vip":"0","gender":"m","age":18,"lastonlinetime":1501248305000,"lat":"39971431","lng":"116337711","lelftext":"我个性 你签名","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1499670220000,"giftnum":1,"charmnum":600,"gift":{"giftid":350,"name":"grab|豪华挖掘机|豪華挖掘機","icon":"http://g.test.iaround.com/manager_up/images/16113ce7b97c29eff0f154176d5b70c0.png","goldnum":6000,"color":"","charmnum":600,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":1,"giftdesc":"grab|豪华挖掘机|豪華挖掘機","ishot":0,"isnew":0,"discountgoldnum":300,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p2.iaround.com/201706/13/FACE/23bac9895cb023b1a0abe5a057a4cee3_s.jpg","nickname":"佛祖","userid":"61001267","vip":"0","gender":"m","age":25,"lastonlinetime":1500519119000,"lat":"0","lng":"0","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":0},"datetime":1497875726000,"giftnum":1,"charmnum":125,"gift":{"giftid":384,"name":"Lucky dumplings|幸运饺子|幸運餃子","icon":"http://g.test.iaround.com/upload/images/8ed6033c977ee5d2ebbcf301cc3f0047.png","goldnum":10,"color":"","charmnum":125,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Lucky dumplings|幸运饺子|幸運餃子","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p2.iaround.com/201706/13/FACE/23bac9895cb023b1a0abe5a057a4cee3_s.jpg","nickname":"佛祖","userid":"61001267","vip":"0","gender":"m","age":25,"lastonlinetime":1500519119000,"lat":"0","lng":"0","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":0},"datetime":1497875674000,"giftnum":1,"charmnum":10,"gift":{"giftid":397,"name":"Thor|雷神玩偶|雷神玩偶","icon":"http://g.test.iaround.com/upload/images/cd9e7acaf39c99f99bec5bd0e732b286.png","goldnum":50,"color":"","charmnum":10,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Thor|雷神玩偶|雷神玩偶","ishot":0,"isnew":0,"discountgoldnum":50,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p2.iaround.com/201706/13/FACE/23bac9895cb023b1a0abe5a057a4cee3_s.jpg","nickname":"佛祖","userid":"61001267","vip":"0","gender":"m","age":25,"lastonlinetime":1500519119000,"lat":"0","lng":"0","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":0},"datetime":1497875684000,"giftnum":1,"charmnum":970000,"gift":{"giftid":399,"name":"Hulk|绿巨人玩偶|綠巨人玩偶","icon":"http://g.test.iaround.com/upload/images/323015cc8e2c42204396242f87cef1dd.png","goldnum":100,"color":"","charmnum":970000,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Hulk|绿巨人玩偶|綠巨人玩偶","ishot":0,"isnew":0,"discountgoldnum":100,"createTime":0,"expvalue":10}},{"user":{"icon":"http://p2.iaround.com/201706/13/FACE/23bac9895cb023b1a0abe5a057a4cee3_s.jpg","nickname":"佛祖","userid":"61001267","vip":"0","gender":"m","age":25,"lastonlinetime":1500519119000,"lat":"0","lng":"0","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":0},"datetime":1497875664000,"giftnum":1,"charmnum":2000000,"gift":{"giftid":400,"name":"Black Widow|黑寡妇玩偶|黑寡婦玩偶","icon":"http://g.test.iaround.com/upload/images/9a53d4117f8a160283cae4cf8e9010f7.png","goldnum":200,"color":"","charmnum":2000000,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Black Widow|黑寡妇玩偶|黑寡婦玩偶","ishot":0,"isnew":0,"discountgoldnum":200,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p1.dev.iaround.com/201706/24/FACE/a20768b8730c96254818cb80268042c6_s.jpg","nickname":"八两15475949","userid":"61001314","vip":"0","gender":"m","age":27,"lastonlinetime":1501301565000,"lat":"39969976","lng":"116331787","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1499755181000,"giftnum":1,"charmnum":20000,"gift":{"giftid":422,"name":"4 year cake|4周年蛋糕|4周年蛋糕","icon":null,"goldnum":400,"color":"","charmnum":20000,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"4 year cake|4周年蛋糕|4周年蛋糕","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":500}},{"user":{"icon":"http://p2.iaround.com/201706/13/FACE/23bac9895cb023b1a0abe5a057a4cee3_s.jpg","nickname":"佛祖","userid":"61001267","vip":"0","gender":"m","age":25,"lastonlinetime":1500519119000,"lat":"0","lng":"0","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":0},"datetime":1497875691000,"giftnum":2,"charmnum":180000000,"gift":{"giftid":439,"name":"Perfume|香水|香水","icon":"http://g.test.iaround.com/upload/images/bdd0900a240ba48d68f51f12139db334.png","goldnum":99,"color":"","charmnum":90000000,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"1|2|3","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":265}},{"user":{"icon":"http://p1.dev.iaround.com/201707/07/FACE/f97e6a97fc56cafac62de2b3ad3f266a_s.jpg","nickname":"布达拉宫","userid":"61001275","vip":"0","gender":"m","age":22,"lastonlinetime":1501314788000,"lat":"0","lng":"0","lelftext":"你好呀哈哈哈","weibo":"981","forbid":"0","photonum":1,"isonline":"y","notes":"","occupation":"3"},"datetime":1498447212000,"giftnum":17,"charmnum":86190,"gift":{"giftid":441,"name":"Aircraft|飞机|飞机","icon":"http://g.test.iaround.com/upload/images/0ad31c1d70ca6030866a73aeefe6f496.png","goldnum":169,"color":"","charmnum":5070,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"1|2|3","ishot":0,"isnew":0,"discountgoldnum":169,"createTime":0,"expvalue":760}},{"user":{"icon":"http://wx.qlogo.cn/mmopen/5AaLxibYcjmibpGVQEicggh9Qic2kjCGmibctg3tbtWchf3oJZvege5hJ8kmITjIv79ofC95BhuPuQZFXbNwGj9Qln7RBfscJLwnZ/0","nickname":"小玩家","userid":"61001550","vip":"0","gender":"m","age":16,"lastonlinetime":1501062879000,"lat":"39969925","lng":"116331799","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500435098000,"giftnum":5571,"charmnum":1504170,"gift":{"giftid":481,"name":"zhadan|炸弹|炸彈","icon":"http://f1.iaround.com/upload/images/dc1fdab8608924241415e11f59ec4677.png","goldnum":9,"color":"","charmnum":270,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"individual|个|個","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":900}},{"user":{"icon":"http://p1.dev.iaround.com/201707/07/FACE/f97e6a97fc56cafac62de2b3ad3f266a_s.jpg","nickname":"布达拉宫","userid":"61001275","vip":"0","gender":"m","age":22,"lastonlinetime":1501314788000,"lat":"0","lng":"0","lelftext":"你好呀哈哈哈","weibo":"981","forbid":"0","photonum":1,"isonline":"y","notes":"","occupation":"3"},"datetime":1500350191000,"giftnum":8028,"charmnum":722520,"gift":{"giftid":482,"name":"tuoxie|拖鞋|拖鞋","icon":"http://f1.iaround.com/upload/images/6b28d23a6b2326bd04eefef1896325fc.png","goldnum":3,"color":"","charmnum":90,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"pair|双|雙","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":30}},{"user":{"icon":"http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg","nickname":"时间从来不","userid":"61001273","vip":"0","gender":"m","age":33,"lastonlinetime":1501250720000,"lat":"39971230","lng":"116337794","lelftext":"","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500988758000,"giftnum":7347,"charmnum":220410,"gift":{"giftid":483,"name":"Rose|玫瑰|玫瑰","icon":"http://f1.iaround.com/upload/images/a04cf04197923b6548ee08a6feedfe03.png","goldnum":1,"color":"","charmnum":30,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Flower|朵|朵","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":1}},{"user":{"icon":"http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg","nickname":"时间从来不","userid":"61001273","vip":"0","gender":"m","age":33,"lastonlinetime":1501250720000,"lat":"39971230","lng":"116337794","lelftext":"","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500988756000,"giftnum":12014,"charmnum":360420,"gift":{"giftid":484,"name":"Soap|香皂|香皂","icon":"http://f1.iaround.com/upload/images/f46eccdc98f19411823a77ecc7c6a550.png","goldnum":1,"color":"","charmnum":30,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"block|块|塊","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":1}},{"user":{"icon":"http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg","nickname":"时间从来不","userid":"61001273","vip":"0","gender":"m","age":33,"lastonlinetime":1501250720000,"lat":"39971230","lng":"116337794","lelftext":"","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500988756000,"giftnum":15331,"charmnum":2299650,"gift":{"giftid":485,"name":"applause|鼓掌|鼓掌","icon":"http://f1.iaround.com/upload/images/f1291b8041cede6fb69c773ac7dde4c0.png","goldnum":5,"color":"","charmnum":150,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"second|次|次","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":5}},{"user":{"icon":"http://p7.iaround.com/201703/28/FACE/71947c50a7a7ac90efde4b87bed141be_s.jpg","nickname":"高行","userid":"61001063","vip":"0","gender":"m","age":20,"lastonlinetime":1501074553000,"lat":"39971425","lng":"116337710","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500990646000,"giftnum":378,"charmnum":79380,"gift":{"giftid":486,"name":"Candy|喜糖|喜糖","icon":"http://f1.iaround.com/upload/images/009dc411ea4c230c8bdbae38431b2821.png","goldnum":7,"color":"","charmnum":210,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Gold|枚|枚","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":7}},{"user":{"icon":"http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg","nickname":"时间从来不","userid":"61001273","vip":"0","gender":"m","age":33,"lastonlinetime":1501250720000,"lat":"39971230","lng":"116337794","lelftext":"","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500988757000,"giftnum":11705,"charmnum":6320700,"gift":{"giftid":487,"name":"V587|V587 |V587","icon":"http://f1.iaround.com/upload/images/7dc5c951affeacd0cd1b6fb71c4cd8dc.png","goldnum":18,"color":"","charmnum":540,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"second|次|次","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":18}},{"user":{"icon":"http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg","nickname":"时间从来不","userid":"61001273","vip":"0","gender":"m","age":33,"lastonlinetime":1501250720000,"lat":"39971230","lng":"116337794","lelftext":"","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500988755000,"giftnum":1868,"charmnum":2129520,"gift":{"giftid":488,"name":"Come on|加油|加油","icon":"http://f1.iaround.com/upload/images/688074c1bc5f8db481c41c878300cb70.png","goldnum":38,"color":"","charmnum":1140,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"second|次|次","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":38}},{"user":{"icon":"http://p7.iaround.com/201703/28/FACE/71947c50a7a7ac90efde4b87bed141be_s.jpg","nickname":"高行","userid":"61001063","vip":"0","gender":"m","age":20,"lastonlinetime":1501074553000,"lat":"39971425","lng":"116337710","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500990658000,"giftnum":2817,"charmnum":5746680,"gift":{"giftid":489,"name":"Lipstick|口红|口红","icon":"http://f1.iaround.com/upload/images/7092bb8a5f123b371bbd0bc5c3be4874.png","goldnum":68,"color":"","charmnum":2040,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"branch|支|支","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":68}},{"user":{"icon":"http://p7.iaround.com/201703/28/FACE/71947c50a7a7ac90efde4b87bed141be_s.jpg","nickname":"高行","userid":"61001063","vip":"0","gender":"m","age":20,"lastonlinetime":1501074553000,"lat":"39971425","lng":"116337710","lelftext":"","weibo":"","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"},"datetime":1500990657000,"giftnum":3757,"charmnum":17808180,"gift":{"giftid":490,"name":"Beer|啤酒|啤酒","icon":"http://f1.iaround.com/upload/images/f69baede2e2d18fe11aa6103a5e9752b.png","goldnum":158,"color":"","charmnum":4740,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Rolling|轧|軋","ishot":0,"isnew":0,"discountgoldnum":0,"createTime":0,"expvalue":158}}]
     * goldnum : 999999999
     * diamondnum : 99811085
     */

    private int pageno;//当前页码
    private int pagesize;//每页展示数量
    private int amount;//总页数
    private int goldnum;//金币数
    private int diamondnum;//钻石数
    private List<ListBean> list;//礼物集合

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getGoldnum() {
        return goldnum;
    }

    public void setGoldnum(int goldnum) {
        this.goldnum = goldnum;
    }

    public int getDiamondnum() {
        return diamondnum;
    }

    public void setDiamondnum(int diamondnum) {
        this.diamondnum = diamondnum;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * user : {"icon":"http://wx.qlogo.cn/mmopen/LQFYGQichZDibzTMicT1ib8Os0WXhEd2IH2EKWw2LhvHYOQQyIE6D90MyD3ByQ05R7rDicvJNPMeiaUbTmxzZxGDyp9FhceGr8P6kv/0","nickname":"史毅力👑","userid":"61001269","vip":"0","gender":"m","age":18,"lastonlinetime":1501248305000,"lat":"39971431","lng":"116337711","lelftext":"我个性 你签名","weibo":"981","forbid":"0","photonum":0,"isonline":"y","notes":"","occupation":"0"}
         * datetime : 1497875664000
         * giftnum : 2
         * charmnum : 1776
         * gift : {"giftid":43,"name":"Ferrari|法拉利|法拉利","icon":"http://p1.iaround.net/201201/06/af35cc3fe1b553975dfc211c39599620.jpg","goldnum":10000,"color":"","charmnum":888,"viptype":0,"startTime":0,"endTime":0,"categoryname":"","currencytype":2,"giftdesc":"Ferrari|法拉利|法拉利","ishot":0,"isnew":0,"discountgoldnum":10000,"createTime":0,"expvalue":500}
         */

        private UserBean user;//送礼人
        private long datetime;
        private int giftnum;//礼物数量
        private int charmnum;//获得的魅力值
        private GiftBean gift;//礼物实体类

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public long getDatetime() {
            return datetime;
        }

        public void setDatetime(long datetime) {
            this.datetime = datetime;
        }

        public int getGiftnum() {
            return giftnum;
        }

        public void setGiftnum(int giftnum) {
            this.giftnum = giftnum;
        }

        public int getCharmnum() {
            return charmnum;
        }

        public void setCharmnum(int charmnum) {
            this.charmnum = charmnum;
        }

        public GiftBean getGift() {
            return gift;
        }

        public void setGift(GiftBean gift) {
            this.gift = gift;
        }

        public static class UserBean {
            /**
             * icon : http://wx.qlogo.cn/mmopen/LQFYGQichZDibzTMicT1ib8Os0WXhEd2IH2EKWw2LhvHYOQQyIE6D90MyD3ByQ05R7rDicvJNPMeiaUbTmxzZxGDyp9FhceGr8P6kv/0
             * nickname : 史毅力👑
             * userid : 61001269
             * vip : 0
             * gender : m
             * age : 18
             * lastonlinetime : 1501248305000
             * lat : 39971431
             * lng : 116337711
             * lelftext : 我个性 你签名
             * weibo : 981
             * forbid : 0
             * photonum : 0
             * isonline : y
             * notes :
             * occupation : 0
             */

            private String icon;
            private String nickname;
            private String userid;
            private String vip;
            private String gender;
            private int age;
            private long lastonlinetime;
            private String lat;
            private String lng;
            private String lelftext;
            private String weibo;
            private String forbid;
            private int photonum;
            private String isonline;
            private String notes;
            private String occupation;

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getVip() {
                return vip;
            }

            public void setVip(String vip) {
                this.vip = vip;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }

            public long getLastonlinetime() {
                return lastonlinetime;
            }

            public void setLastonlinetime(long lastonlinetime) {
                this.lastonlinetime = lastonlinetime;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }

            public String getLelftext() {
                return lelftext;
            }

            public void setLelftext(String lelftext) {
                this.lelftext = lelftext;
            }

            public String getWeibo() {
                return weibo;
            }

            public void setWeibo(String weibo) {
                this.weibo = weibo;
            }

            public String getForbid() {
                return forbid;
            }

            public void setForbid(String forbid) {
                this.forbid = forbid;
            }

            public int getPhotonum() {
                return photonum;
            }

            public void setPhotonum(int photonum) {
                this.photonum = photonum;
            }

            public String getIsonline() {
                return isonline;
            }

            public void setIsonline(String isonline) {
                this.isonline = isonline;
            }

            public String getNotes() {
                return notes;
            }

            public void setNotes(String notes) {
                this.notes = notes;
            }

            public String getOccupation() {
                return occupation;
            }

            public void setOccupation(String occupation) {
                this.occupation = occupation;
            }
        }

        public static class GiftBean {
            /**
             * giftid : 43
             * name : Ferrari|法拉利|法拉利
             * icon : http://p1.iaround.net/201201/06/af35cc3fe1b553975dfc211c39599620.jpg
             * goldnum : 10000
             * color :
             * charmnum : 888
             * viptype : 0
             * startTime : 0
             * endTime : 0
             * categoryname :
             * currencytype : 2
             * giftdesc : Ferrari|法拉利|法拉利
             * ishot : 0
             * isnew : 0
             * discountgoldnum : 10000
             * createTime : 0
             * expvalue : 500
             */

            private int giftid;//礼物id
            private String name;//礼物名称
            private String icon;//礼物图标
            private int goldnum;//价值
            private String color;//
            private int charmnum;//
            private int viptype;//
            private int startTime;//
            private int endTime;//
            private String categoryname;//分类名称
            private int currencytype;//1为金币商品　2为钻石商品 3为爱心商品  6为星星商品
            private String giftdesc;//
            private int ishot;//
            private int isnew;//
            private int discountgoldnum;//优惠价格
            private int createTime;//
            private int expvalue;//经验值
            private String[] nameArray;

            public String getNameArray() {
                int index = CommonFunction.getLanguageIndex(BaseApplication.appContext);
                return ( nameArray == null || nameArray.length <= 0 ) ? "" : ( nameArray.length <= index ? nameArray[ 0 ]
                        : nameArray[ index ] );
            }

            public void setNameArray(String nameArray) {
                this.nameArray = nameArray.split( "\\|");
            }

            public int getGiftid() {
                return giftid;
            }

            public void setGiftid(int giftid) {
                this.giftid = giftid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public int getGoldnum() {
                return goldnum;
            }

            public void setGoldnum(int goldnum) {
                this.goldnum = goldnum;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public int getCharmnum() {
                return charmnum;
            }

            public void setCharmnum(int charmnum) {
                this.charmnum = charmnum;
            }

            public int getViptype() {
                return viptype;
            }

            public void setViptype(int viptype) {
                this.viptype = viptype;
            }

            public int getStartTime() {
                return startTime;
            }

            public void setStartTime(int startTime) {
                this.startTime = startTime;
            }

            public int getEndTime() {
                return endTime;
            }

            public void setEndTime(int endTime) {
                this.endTime = endTime;
            }

            public String getCategoryname() {
                return categoryname;
            }

            public void setCategoryname(String categoryname) {
                this.categoryname = categoryname;
            }

            public int getCurrencytype() {
                return currencytype;
            }

            public void setCurrencytype(int currencytype) {
                this.currencytype = currencytype;
            }

            public String getGiftdesc() {
                return giftdesc;
            }

            public void setGiftdesc(String giftdesc) {
                this.giftdesc = giftdesc;
            }

            public int getIshot() {
                return ishot;
            }

            public void setIshot(int ishot) {
                this.ishot = ishot;
            }

            public int getIsnew() {
                return isnew;
            }

            public void setIsnew(int isnew) {
                this.isnew = isnew;
            }

            public int getDiscountgoldnum() {
                return discountgoldnum;
            }

            public void setDiscountgoldnum(int discountgoldnum) {
                this.discountgoldnum = discountgoldnum;
            }

            public int getCreateTime() {
                return createTime;
            }

            public void setCreateTime(int createTime) {
                this.createTime = createTime;
            }

            public int getExpvalue() {
                return expvalue;
            }

            public void setExpvalue(int expvalue) {
                this.expvalue = expvalue;
            }
        }
    }
}
