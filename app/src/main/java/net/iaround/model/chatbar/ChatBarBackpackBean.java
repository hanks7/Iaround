package net.iaround.model.chatbar;

import android.content.Context;

import net.iaround.model.entity.BaseEntity;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ray on 2017/7/11.
 */

public class ChatBarBackpackBean extends BaseServerBean implements Serializable {
    private static final long serialVersionUID = 7635127122654526974L;


    /**
     * uer_gold : 0
     * user_diamond : 0
     * list : [{"gift_id":479,"gift_name":"Diamond|愚人真心钻|愚人真心钻","gift_icon":"http://g.test.iaround.com/upload/images/03cdd880dc7eb699f5cdc10d2f16d410.jpg","gift_desc":"Diamond|钻石|钻石","gift_charm_num":1000,"gift_exp":100,"gift_gold":0,"gift_diamond":100,"giff_num":451,"gift_combo":[{"combo_id":1,"combo_name":"Diamond|愚人","combo_value":1,"combo_type":1,"combo_url":"dsdsdd"},{"combo_id":2,"combo_name":"Diamond|愚人","combo_value":66,"combo_type":2,"combo_url":"dsdsdd"},{"combo_id":3,"combo_name":"Diamond|愚人","combo_value":666,"combo_type":2,"combo_url":"dsdsdd"},{"combo_id":4,"combo_name":"Diamond|愚人","combo_value":1314,"combo_type":1,"combo_url":"dsdsdd"},{"combo_id":5,"combo_name":"Diamond|愚人","combo_value":888,"combo_type":0,"combo_url":"dsdsdd"}]}]
     */

    private int user_gold;
    private int user_loves;
    private int user_diamond;
    private int user_star;
    private List<ListBean> list;
    private int comboTime;//发送礼物倒计时的总时间3000ms

    public int getComboTime() {
        return comboTime;
    }

    public void setComboTime(int comboTime) {
        this.comboTime = comboTime;
    }

    public int getUer_gold() {
        return user_gold;
    }

    public int getUser_star() {
        return user_star;
    }

    public void setUer_gold(int uer_gold) {
        this.user_gold = uer_gold;
    }

    public int getUser_diamond() {
        return user_diamond;
    }

    public void setUser_diamond(int user_diamond) {
        this.user_diamond = user_diamond;
    }

    public int getUser_loves() {
        return user_loves;
    }

    public void setUser_loves(int user_loves) {
        this.user_loves = user_loves;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * gift_id : 479
         * gift_name : Diamond|愚人真心钻|愚人真心钻
         * gift_icon : http://g.test.iaround.com/upload/images/03cdd880dc7eb699f5cdc10d2f16d410.jpg
         * gift_desc : Diamond|钻石|钻石
         * gift_charm_num : 1000
         * gift_exp : 100
         * gift_gold : 0
         * gift_diamond : 100
         * giff_num : 451
         * gift_combo : [{"combo_id":1,"combo_name":"Diamond|愚人","combo_value":1,"combo_type":1,"combo_url":"dsdsdd"},{"combo_id":2,"combo_name":"Diamond|愚人","combo_value":66,"combo_type":2,"combo_url":"dsdsdd"},{"combo_id":3,"combo_name":"Diamond|愚人","combo_value":666,"combo_type":2,"combo_url":"dsdsdd"},{"combo_id":4,"combo_name":"Diamond|愚人","combo_value":1314,"combo_type":1,"combo_url":"dsdsdd"},{"combo_id":5,"combo_name":"Diamond|愚人","combo_value":888,"combo_type":0,"combo_url":"dsdsdd"}]
         */

        private int gift_id;
        private String[] gift_name_array;//礼物名字国家化
        private String gift_name;//礼物名字
        private String gift_icon;//礼物URL
        private String gift_desc;//
        private int gift_charm_num;//魅力值
        private int gift_exp;//经验值
        private int gift_gold;//金币数量
        private int gift_diamond;//钻石数量
        private int gift_love;//爱心数量
        private int gift_star;//星星数量
        private int giftArrayPosition;//礼物数组选择的位置
        private int giftPosition;//礼物所在位置

        public boolean isSelected = false;


        public int getGiftArrayPosition() {
            return giftArrayPosition;
        }

        public void setGiftArrayPosition(int giftArrayPosition) {
            this.giftArrayPosition = giftArrayPosition;
        }

        public int getGift_star() {
            return gift_star;
        }

        public int getGiftPosition() {
            return giftPosition;
        }

        public void setGiftPosition(int giftPosition) {
            this.giftPosition = giftPosition;
        }

        public int getGift_num() {
            return gift_num;
        }

        public void setGift_num(int gift_num) {
            this.gift_num = gift_num;
        }

        public int getGift_love() {
            return gift_love;
        }

        public void setGift_love(int gift_love) {
            this.gift_love = gift_love;
        }

        private int gift_num;//礼物数量
        private List<GiftComboBean> gift_combo;//礼物数组
        public boolean isClicked = false;//判断是否被点击

        public boolean isClicked() {
            return isClicked;
        }

        public void setClicked(boolean clicked) {
            isClicked = clicked;
        }


        public int getGift_id() {
            return gift_id;
        }

        public void setGift_id(int gift_id) {
            this.gift_id = gift_id;
        }

        public String getGift_name() {

            return gift_name;
        }

        public void setGiftNameArray(String giftNameArray) {
            this.gift_name_array = giftNameArray.split("\\|");
        }

        public String getGiftNameArray(Context context) {
            int index = CommonFunction.getLanguageIndex(context);
            return (gift_name_array == null || gift_name_array.length <= 0) ? "" : (gift_name_array.length <= index ? gift_name_array[0]
                    : gift_name_array[index]);
        }

        public void setGift_name(String gift_name) {
            this.gift_name = gift_name;
        }

        public String getGift_icon() {
            return gift_icon;
        }

        public void setGift_icon(String gift_icon) {
            this.gift_icon = gift_icon;
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

        public int getGift_gold() {
            return gift_gold;
        }

        public void setGift_gold(int gift_gold) {
            this.gift_gold = gift_gold;
        }

        public int getGift_diamond() {
            return gift_diamond;
        }

        public void setGift_diamond(int gift_diamond) {
            this.gift_diamond = gift_diamond;
        }


        public List<GiftComboBean> getGift_combo() {
            return gift_combo;
        }

        public void setGift_combo(List<GiftComboBean> gift_combo) {
            this.gift_combo = gift_combo;
        }

        public static class GiftComboBean {
            /**
             * combo_id : 1
             * combo_name : Diamond|愚人
             * combo_value : 1
             * combo_type : 1
             * combo_url : dsdsdd
             */

            private int combo_id;
            private String combo_name;
            private int combo_value;
            private int combo_type;//0无连送;1累计;2数组
            private String combo_url;

            public int getCombo_id() {
                return combo_id;
            }

            public void setCombo_id(int combo_id) {
                this.combo_id = combo_id;
            }

            public String getCombo_name() {
                return combo_name;
            }

            public void setCombo_name(String combo_name) {
                this.combo_name = combo_name;
            }

            public int getCombo_value() {
                return combo_value;
            }

            public void setCombo_value(int combo_value) {
                this.combo_value = combo_value;
            }

            public int getCombo_type() {
                return combo_type;
            }

            public void setCombo_type(int combo_type) {
                this.combo_type = combo_type;
            }

            public String getCombo_url() {
                return combo_url;
            }

            public void setCombo_url(String combo_url) {
                this.combo_url = combo_url;
            }
        }
    }
}
