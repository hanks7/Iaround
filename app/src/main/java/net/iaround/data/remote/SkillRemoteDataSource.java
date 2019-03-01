package net.iaround.data.remote;

import android.text.TextUtils;

import net.iaround.BaseApplication;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.data.SkillDataSource;

import java.util.LinkedHashMap;

/**
 * 作者：zx on 2017/8/9 16:39
 */
public class SkillRemoteDataSource implements SkillDataSource{

    private static final String url_skill_list = "/v1/skill/user/list";
    private static final String url_skill_detail = "/v1/skill/user/detail";
    private static final String url_skill_update = "/v1/skill/user/update";
    private static final String url_props_shop = "/v1/skill/props/shop";
    private static final String url_props_shop_buy = "/v1/skill/props/buy";
    private static final String url_skill_use_pk = "/v1/skill/user/pk";
    private static final String url_skill_use_attack = "/v1/skill/user/attack";
    private static final String url_skill_open = "/v1/skill/user/open";
    private static final String url_skill_addtion = "/v1/skill/tip/rate";
    private static final String url_skill_update1 = "/v1/chatbar/noticeinfo";

    @Override
    public long getSkillList(HttpCallBack callBack) {
        return skillPost(url_skill_list, null, callBack);
    }

    @Override
    public long getSkillDetail(String skillId, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("skillId", skillId);
        return skillPost(url_skill_detail, params, callBack);
    }

    @Override
    public long updateSkill(String skillId, String propId, String currencyType, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("skillId", skillId);
        params.put("propId", propId);
        params.put("currencyType", currencyType);
        return skillPost(url_skill_update, params, callBack);
    }

    @Override
    public long getSkillUsedInfo(String targetUserId, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("targetUserId", targetUserId);
        return skillPost(url_skill_use_pk, params, callBack);
    }

    @Override
    public long skillAttack(String targetUserId, String skillId, String groupId, String currencyType, String propsId, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("targetUserId", targetUserId);
        params.put("skillId", skillId);
        params.put("currencyType", currencyType);
        if (!TextUtils.isEmpty(groupId)){
            params.put("groupId", groupId);
        }
        params.put("propsId", propsId);
        return skillPost(url_skill_use_attack, params, callBack);
    }

    @Override
    public long getPropsShopData(HttpCallBack callBack) {
        return skillPost(url_props_shop, null, callBack);
    }

    @Override
    public long propsShopBuy(String propsShopId, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("propsShopId", propsShopId);
        return skillPost(url_props_shop_buy, params, callBack);
    }

    @Override
    public long openSkill(String skillId, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("skillId", skillId);
        return skillPost(url_skill_open, params, callBack);
    }

    @Override
    public long getSkillAddition(HttpCallBack callBack) {
        return skillPost(url_skill_addtion, null, callBack);
    }


    private long skillPost(String url, LinkedHashMap<String, Object> params, HttpCallBack callback) {
        return ConnectorManage.getInstance(BaseApplication.appContext).asynPost(url, params, callback);
    }

    /**
     * 获取推荐升级技能
     * @param callback
     * @return
     */
    public static long getSkillUpdate(HttpCallBack callback ){
        return ConnectorManage.getInstance(BaseApplication.appContext).asynPost(url_skill_update1, null, callback);
    }
}
