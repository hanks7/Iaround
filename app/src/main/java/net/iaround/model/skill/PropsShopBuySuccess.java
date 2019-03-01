package net.iaround.model.skill;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * 作者：zx on 2017/8/21 10:16
 */
public class PropsShopBuySuccess extends BaseServerBean{

    /**
     {
     "list": [
     {
     "ID": 1,                     	   //我的道具ID
     "UserID": 61001617,
     "PropsID": 1,                 	  //道具ID
     "Num": 48,                      		//持有数量
     "CreateTime": 1503042519,
     "UpdateTIme": 1503042519,
     "Name": "银星星",                 //道具名称
     "ICON": ""                      //图标
     }
     ],
     "user": {
     "UserID": 61001617,
     "NickName": "八两",
     "ICON": "http://p1.dev.iaround.com/201707/26/FACE/401ea864b7e9d01875cd576f5c4a91f2_s.jpg",
     "VIP": 0,
     "Notes": "",
     "VipLevel": 0,
     "Age": 17,
     "Gender": "m",
     "DiamondNum": 0,
     "GoldNum": 0
     },
     "status": 200
     }
     */
    public List<PropsItemBuySuccess> list;
    public PropsShopUser user;

    public static class PropsItemBuySuccess{

        public String ID;
        public String UserID;
        public String PropsID;
        public int Num;
        public String Name;
        public String ICON;
    }

}
