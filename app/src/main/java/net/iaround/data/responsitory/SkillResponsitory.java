package net.iaround.data.responsitory;

import net.iaround.connector.HttpCallBack;
import net.iaround.data.SkillDataSource;
import net.iaround.data.remote.SkillRemoteDataSource;

/**
 * 作者：zx on 2017/8/9 16:39
 */
public class SkillResponsitory implements SkillDataSource {

    private SkillDataSource dataSource;

    public SkillResponsitory() {
        this.dataSource = new SkillRemoteDataSource();
    }

    @Override
    public long getSkillList(HttpCallBack callBack) {
        return dataSource.getSkillList(callBack);
    }

    @Override
    public long getSkillDetail(String skillId, HttpCallBack callBack) {
        return dataSource.getSkillDetail(skillId, callBack);
    }

    @Override
    public long updateSkill(String skillId, String propId, String currencyType, HttpCallBack callBack) {
        return dataSource.updateSkill(skillId, propId, currencyType, callBack);
    }

    @Override
    public long getSkillUsedInfo(String targetUserId, HttpCallBack callBack) {
        return dataSource.getSkillUsedInfo(targetUserId, callBack);
    }

    @Override
    public long skillAttack(String targetUserId, String skillId, String groupId, String currencyType, String propsId, HttpCallBack callBack) {
        return dataSource.skillAttack(targetUserId, skillId, groupId, currencyType, propsId, callBack);
    }

    @Override
    public long getPropsShopData(HttpCallBack callBack) {
        return dataSource.getPropsShopData(callBack);
    }

    @Override
    public long propsShopBuy(String propsShopId, HttpCallBack callBack) {
        return dataSource.propsShopBuy(propsShopId, callBack);
    }

    @Override
    public long openSkill(String skillId, HttpCallBack callBack) {
        return dataSource.openSkill(skillId, callBack);
    }

    @Override
    public long getSkillAddition(HttpCallBack callBack) {
        return dataSource.getSkillAddition(callBack);
    }


}
