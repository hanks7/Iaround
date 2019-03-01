package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/13.
 */

public class QualificationBean extends BaseEntity {
    public AuthInfo authInfo;
    public class AuthInfo {
        public long gameId;
        public ArrayList<String> photos;
        public String voice;//音频url
        public int gameLevel;//游戏等级
        public String description;//描述
        public int status;//审核状态 0-认证中 1-认证成功 2-认证失败
        public ArrayList<LevelInfo> levelList;
        public String picCaption;//上传资质图提示
        public String voiceCaption;//上传音频提示
        public String sample;//示例图地址
    }

    public class LevelInfo {
        public int type;//等级
        public String name;//等级对应的称号
    }
}
