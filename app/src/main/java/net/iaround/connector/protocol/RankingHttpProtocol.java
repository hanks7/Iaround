package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


public class RankingHttpProtocol {

    /**
     * 魅力榜接口
     */
    private static String RANKING_CHARM = "/v1/top/index";
    /**
     * 富豪榜接口
     */
    private static String RANKING_REGAL = "/v1/top/index";
    /**
     * 技能排行榜榜接口
     */
    private static String RANKING_SKILL = "/v1/skill/top/active";
    /**
     * 技能排行榜榜接口详情
     */
    private static String RANKING_SKILL_INFO = "/v1/skill/top/active/detail";



    public static RankingHttpProtocol instance;

    public static RankingHttpProtocol getInstance() {
        if (instance == null) {
            instance = new RankingHttpProtocol();
        }
        return instance;
    }

    public static long getData(Context context, String url,
                               LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_CHATBAR, callback);
    }

    public static long getDataSkill(Context context, String url,
                               LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return  ConnectorManage.getInstance(context).asynPost(url,map,callback);
    }

    /**
     * 获取排行榜列表数据
     *
     * @param context
     * @param pageNum  页码
     * @param pageSize 页面尺寸
     * @param cat      类型1魅力 2富豪
     * @param type     1本周 2上周 3本月 4上月
     * @param callBack
     * @return
     */
    public long getChatBarAttentionData(Context context, int pageNum, int pageSize, int cat, int type, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("page_no", pageNum);
        params.put("page_size", pageSize);
        params.put("cat", cat);
        params.put("type", type);
        return getData(context, RANKING_CHARM, params, callBack);
    }

    /**
     * 获取技能排行榜列表数据
     *
     * @param context
     * @param type     1本周 2上周 3本月 4上月
     * @param callBack
     * @return
     */
    public long getChatBarSkillRankingData(Context context, int type, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("type", type);
        return getDataSkill(context, RANKING_SKILL, params, callBack);
    }

    /**
     * 获取技能详情排行榜列表数据
     *
     * @param context
     * @param pageNo  页码
     * @param pageSize 页面尺寸
     * @param index      类型1魅力 2富豪
     * @param type     1本周 2上周 3本月 4上月
     * @param callBack
     * @return
     */
    public long getChatBarSkillRankingDataInfo(Context context,int type, int skillId, String index, int pageNo, int pageSize, HttpCallBack callBack) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("type", type);// 1本周 2上周 3本月 4上月
        params.put("skillId", skillId);//skillId	技能ID
        params.put("index", index);//索引类型 取值范围 active update 。 active代表互动  update升级
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);

        return getDataSkill(context, RANKING_SKILL_INFO, params, callBack);
    }
}
