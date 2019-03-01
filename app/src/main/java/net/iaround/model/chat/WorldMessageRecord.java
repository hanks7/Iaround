package net.iaround.model.chat;


import net.iaround.model.entity.Item;
import net.iaround.model.skill.SkillAttackResult;

/**
 * 作者：zx on 2017/8/24 17:05
 */
public class WorldMessageRecord{

//    public String content;
    public BaseWorldMessageBean.UserBean user;
    public int messageType;//30世界消息 31招募（需要group数组信息） 32礼物消息(客户端上传到接口) 33技能消息
    public Item rankItem;//称号属性

    public WorldMessageTextContent textContent;
    public SkillAttackResult skillContent;
    public WorldMessageRecruitContent recruitContent;
    public WorldMessageGiftContent giftContent;
    public Item item;
    public int recruit;//是否可招募




}
