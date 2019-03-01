package net.iaround.model.entity;

import android.content.Context;

import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/16.
 */

public class IncomeDetailsEntity extends BaseEntity {

    public int pageno;
    public int pagesize;
    public int amount;
    public ArrayList<IncomeDetails> list;// 详情列表

    public class IncomeDetails {

        public IncomeUser user;
        public int giftnum;//礼物数量
        public long videotime;//通话时长
        public String giftname;//送礼名称
        public int consumetype; // 类型 1-充值，2-购买礼物，3-视频聊天
        public int lovenum; // 钻石数
        public long datetime; // 时间

        public User getUser(){
            User users = new User();

            if (user == null)return users;
            users.setUid(user.uid);
            users.setSVip(user.svip);
            users.setViplevel(user.vip);
            users.setIcon(user.icon);

            return users;
        }

        public String getGiftname(Context context) {
            int index = CommonFunction.getLanguageIndex(context);
            String[] name = giftname.split("\\|");
            return (giftname == null || name.length <= 0) ? "" : (name.length <= index ? name[0]
                    : name[index]);
        }
    }

    public class IncomeUser{
        public String nickname;
        public int age;
        public String gender;
        public String icon;
        public int vip;
        public int svip;
        public long uid;
    }
}
