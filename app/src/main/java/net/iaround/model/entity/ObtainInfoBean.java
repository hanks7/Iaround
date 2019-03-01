package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

/**
 * 获取当前用户所在哪个聊吧
 * Created by gh on 2017/11/14.
 */

public class ObtainInfoBean extends BaseServerBean {

    public String gname;//聊吧名字
    public String gicon;//聊吧图标
    public long gid;//聊吧id
    public int setBlockStaus;//阻断状态
    /**
     * 0:自己 ，1：好友 ，2：陌生人 3、关注 4、粉丝 5推荐
     */
    public int relation;

    public int userType;//用户角色 0，普通用户 1，主播

    public GameInfo gameInfo;
    public OrderInfo orderInfo;

    public String getGname() {
        return gname;
    }

    public String getGicon() {
        return gicon;
    }

    public long getGid() {
        return gid;
    }

    public class GameInfo {
        public long anchorID;//主播ID
        public long gameId;
        public String gameName;
        public double price;
        public String unit;
        public String gameRank;
        public int gameLevel;
    }

    public class OrderInfo {
        public long orderId;
        public int step;//订单流程步骤
        public String gameName;//游戏名称
        public long anchorId;//主播ID
    }
//    "gameInfo": {
//        "anchorID": 62985588,
//                "gameId": 1,
//                "gameName": "王者荣耀",
//                "price": 0.01,
//                "unit": "元/小时",
//                "gameRank": "倔强青铜"
//    },
//            "orderInfo": {
//        "gameName":"第五人格",
//             "orderId":300,
//                     "step":4,
//                     "anchorId":61835476
//    },

}
