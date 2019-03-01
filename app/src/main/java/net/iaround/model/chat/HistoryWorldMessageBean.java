package net.iaround.model.chat;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * 作者：zx on 2017/8/29 15:20
 */
public class HistoryWorldMessageBean extends BaseServerBean{

    public List<String> message;
    public int type;//1:世界历史消息，2：技能历史消息
    public long ts;

}
