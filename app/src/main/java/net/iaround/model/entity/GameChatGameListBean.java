package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/5.
 */

public class GameChatGameListBean extends BaseEntity {
    public ArrayList<GameChatGameListItem> gamelists;

    public class GameChatGameListItem {
        public String catename;
        public ArrayList<GameListItem> gamelist;

    }

    public class GameListItem {
        public long GameID;
        public String GameName;
        public String GameIcon;
        public int IsHot;

    }
}
