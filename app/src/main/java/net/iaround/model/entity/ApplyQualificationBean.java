package net.iaround.model.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yz on 2018/8/3.
 */

public class ApplyQualificationBean extends BaseEntity {
    public ArrayList<GameItem> appliedList;
    public ArrayList<NotApplyList> notApplyList;

    public class NotApplyList {
        public String type;
        public ArrayList<GameItem> gameList;
    }

    public class GameItem implements Serializable{
        public long gameId;
        public String gameName;

    }
}
