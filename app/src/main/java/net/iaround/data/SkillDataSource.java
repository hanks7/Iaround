package net.iaround.data;

import net.iaround.connector.HttpCallBack;

import java.util.Map;

/**
 * 作者：zx on 2017/8/9 16:37
 */
public interface SkillDataSource {
    long getSkillList(HttpCallBack callBack);
    long getSkillDetail(String skillId, HttpCallBack callBack);
    long updateSkill(String skillId, String propId, String currencyType, HttpCallBack callBack);
    long getSkillUsedInfo(String targetUserId, HttpCallBack callBack);
    long skillAttack(String targetUserId, String skillId, String groupId, String currencyType, String propsId, HttpCallBack callBack);
    long getPropsShopData(HttpCallBack callBack);
    long propsShopBuy(String propsShopId, HttpCallBack callBack);
    long openSkill(String skillId, HttpCallBack callBack);
    long getSkillAddition(HttpCallBack callBack);
}
