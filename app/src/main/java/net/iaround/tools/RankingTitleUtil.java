package net.iaround.tools;

import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.model.entity.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static net.iaround.conf.Constants.activeMonthSkill;
import static net.iaround.conf.Constants.activeWeekSkill;
import static net.iaround.conf.Constants.charmMonth;
import static net.iaround.conf.Constants.charmWeek;
import static net.iaround.conf.Constants.regalMonth;
import static net.iaround.conf.Constants.regalWeek;
import static net.iaround.conf.Constants.updateMonthSkill;
import static net.iaround.conf.Constants.updateWeekSkill;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsBaohufeiActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsBaohufeiGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsCharm;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsDamemeActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsDamemeGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJinguzhouActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJinguzhouGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJuhuancanActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJuhuancanGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsRegal;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsWuyingjiaoActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsWuyingjiaoGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveBaohufei;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveDameme;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveJinguzhou;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveJuhuacan;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveWuyingjiao;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesCharm;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowBaohufei;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowDameme;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowJinguzhou;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowJuhuacan;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowWuyingjiao;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesRegal;

/**
 * Created by Sjq on 2017/8/28.
 */

public class RankingTitleUtil {

    public static RankingTitleUtil instance;

    public static RankingTitleUtil getInstance() {
        if (instance == null) {
            instance = new RankingTitleUtil();
        }
        return instance;
    }

    public Item parseRank(String ranks) {
        if (ranks.length() < 3) {
            return null;
        }
        ranks = ranks.substring(2, ranks.length() - 2);
        Item item = new Item();
        List<Item> itemList = new ArrayList<>();
        String[] skills = ranks.split("\\},\\{");
        int min = 0;
        for (int i = 0; i < skills.length; i++) {
            String[] keyValues = skills[i].split(":");
            if (null != keyValues && keyValues.length > 1) {
                int num = Integer.parseInt(keyValues[1]);
                if (i == 0) {
                    min = num;
                }
                if (num < min) {
                    item.key = keyValues[0];
                    item.value = num;
                    min = num;
                    itemList.clear();
                }
                if (num == min) {
                    itemList.add(new Item(keyValues[0], num));
                }
            }
        }

        if (null != itemList && itemList.size() > 0) {
            Item rank = getRank(itemList);
            return rank;
        }
        return item;
    }

    public Item parseRankFromLogin(String ranks) {
        if (ranks.length() < 3) {
            return null;
        }
        ranks = ranks.substring(2, ranks.length() - 2);
        Item item = new Item();
        List<Item> itemList = new ArrayList<>();
        String[] skills = ranks.split("\\},\\{");
        int min = 0;
        for (int i = 0; i < skills.length; i++) {
            String[] keyValues = skills[i].split(":");
            if (null != keyValues && keyValues.length > 1) {
                int num = Integer.parseInt(keyValues[1]);
                if (i == 0) {
                    min = num;
                }
                if (num < min) {
                    item.key = keyValues[0];
                    item.value = num;
                    min = num;
                    itemList.clear();
                }
                if (num == min) {
                    itemList.add(new Item(keyValues[0], num));
                }
            }
        }

        if (null != itemList && itemList.size() > 0) {
            Item rank = getRank(itemList);
            return rank;
        }
        return item;
    }


    private Item getRank(List<Item> itemList) {

        Item item = new Item();
        for (Item target : itemList) {
            if (target.key.contains(regalMonth)) {
                item = target;
                break;
            } else if (target.key.contains(regalWeek)) {
                item = target;
                break;

            } else if (target.key.contains(charmMonth)) {
                item = target;
                break;

            } else if (target.key.contains(charmWeek)) {
                item = target;
                break;

            } else if (target.key.contains(activeMonthSkill)) {
                item = target;
                break;

            } else if (target.key.contains(activeWeekSkill)) {
                item = target;
                break;

            } else if (target.key.contains(updateMonthSkill)) {
                item = target;
                break;

            } else if (target.key.contains(updateWeekSkill)) {
                item = target;
                break;

            }

        }

        return item;
    }

    /**
     * 根据接口返回的rank字段筛选出排名最靠前的逻辑item    他人资料
     *
     * @param result
     * @return
     */
    public Item getTitleItem(String result) {
        JSONObject jsonObject = null;
        Item item = null;
        try {
            if (result.contains("data") && result.contains("ranking")) {
                jsonObject = new JSONObject(result);
                JSONObject datanew = jsonObject.getJSONObject("data");
                JSONArray rankingArray = datanew.getJSONArray("ranking");
                String rankStr = rankingArray.toString();
                item = RankingTitleUtil.getInstance().parseRank(rankStr);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;

    }

    /**
     * 根据接口返回的rank字段筛选出排名最靠前的逻辑item    他人资料
     *
     * @param result
     * @return
     */
    public Item getTitleItemFromLogin(String result) {
        JSONObject jsonObject = null;
        Item item = null;
        try {
            if (result.contains("rank")) {
                jsonObject = new JSONObject(result);
                JSONArray rankingArray = jsonObject.getJSONArray("rank");
                String rankStr = rankingArray.toString();
                item = RankingTitleUtil.getInstance().parseRank(rankStr);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;

    }

    /**
     * 根据接口返回的rank字段筛选出排名最靠前的逻辑item    聊吧
     *
     * @param result
     * @return
     */
    public Item getTitleItemFromChatBar(String result) {
        JSONObject jsonObject = null;
        Item item = null;
        try {
            if (result.contains("user") && result.contains("rank")) {
                jsonObject = new JSONObject(result);
                JSONObject datanew = jsonObject.getJSONObject("user");
                JSONArray rankingArray = datanew.getJSONArray("rank");
                String rankStr = "";
                if (null != rankingArray)
                    rankStr = rankingArray.toString();
                item = RankingTitleUtil.getInstance().parseRank(rankStr);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;

    }

    /**
     * 根据接口返回的rank字段筛选出排名最靠前的逻辑item    聊吧
     *
     * @param result
     * @return
     */
    public Item getTitleItemFromChatBarWorkd(String result) {
        JSONObject jsonObject = null;
        Item item = null;
        try {
            if (result.contains("rank")) {
                jsonObject = new JSONObject(result);
                JSONArray rankingArray = jsonObject.getJSONArray("rank");
                String rankStr = rankingArray.toString();
                item = RankingTitleUtil.getInstance().parseRank(rankStr);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;

    }


    /**
     * 将item的key跟value,跟需要展示的textview传入
     *
     * @param key
     * @param value
     * @param tvTitle
     */
    public void handleReallyRank(String key, int value, TextView tvTitle) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (key.contains(regalMonth)) {
            showRankingTitle(tvTitle, regalMonth, value, 0);

        } else if (key.contains(regalWeek)) {
            showRankingTitle(tvTitle, regalWeek, value, 0);

        } else if (key.contains(charmMonth)) {
            showRankingTitle(tvTitle, charmMonth, value, 0);

        } else if (key.contains(charmWeek)) {
            showRankingTitle(tvTitle, charmWeek, value, 0);

        } else if (key.contains(activeMonthSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitle(tvTitle, activeMonthSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitle(tvTitle, activeMonthSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitle(tvTitle, activeMonthSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitle(tvTitle, activeMonthSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitle(tvTitle, activeMonthSkill, value, 5);
            }

        } else if (key.contains(activeWeekSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitle(tvTitle, activeWeekSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitle(tvTitle, activeWeekSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitle(tvTitle, activeWeekSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitle(tvTitle, activeWeekSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitle(tvTitle, activeWeekSkill, value, 5);
            }
        } else if (key.contains(updateMonthSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitle(tvTitle, updateMonthSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitle(tvTitle, updateMonthSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitle(tvTitle, updateMonthSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitle(tvTitle, updateMonthSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitle(tvTitle, updateMonthSkill, value, 5);
            }
        } else if (key.contains(updateWeekSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitle(tvTitle, updateWeekSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitle(tvTitle, updateWeekSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitle(tvTitle, updateWeekSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitle(tvTitle, updateWeekSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitle(tvTitle, updateWeekSkill, value, 5);
            }
        }

    }

    public void handleReallyRankNew(String key, int value, TextView tvTitle, RelativeLayout ivTitle) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (key.contains(regalMonth)) {
            showRankingTitleNew(tvTitle, ivTitle, regalMonth, value, 0);

        } else if (key.contains(regalWeek)) {
            showRankingTitleNew(tvTitle, ivTitle, regalWeek, value, 0);

        } else if (key.contains(charmMonth)) {
            showRankingTitleNew(tvTitle, ivTitle, charmMonth, value, 0);

        } else if (key.contains(charmWeek)) {
            showRankingTitleNew(tvTitle, ivTitle, charmWeek, value, 0);

        } else if (key.contains(activeMonthSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitleNew(tvTitle, ivTitle, activeMonthSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitleNew(tvTitle, ivTitle, activeMonthSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitleNew(tvTitle, ivTitle, activeMonthSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitleNew(tvTitle, ivTitle, activeMonthSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitleNew(tvTitle, ivTitle, activeMonthSkill, value, 5);
            }

        } else if (key.contains(activeWeekSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitleNew(tvTitle, ivTitle, activeWeekSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitleNew(tvTitle, ivTitle, activeWeekSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitleNew(tvTitle, ivTitle, activeWeekSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitleNew(tvTitle, ivTitle, activeWeekSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitleNew(tvTitle, ivTitle, activeWeekSkill, value, 5);
            }
        } else if (key.contains(updateMonthSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitleNew(tvTitle, ivTitle, updateMonthSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitleNew(tvTitle, ivTitle, updateMonthSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitleNew(tvTitle, ivTitle, updateMonthSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitleNew(tvTitle, ivTitle, updateMonthSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitleNew(tvTitle, ivTitle, updateMonthSkill, value, 5);
            }
        } else if (key.contains(updateWeekSkill)) {
            String str = key.substring(key.length() - 2, key.length() - 1);
            if (str.equals("1")) {
                showRankingTitleNew(tvTitle, ivTitle, updateWeekSkill, value, 1);
            } else if (str.equals("2")) {
                showRankingTitleNew(tvTitle, ivTitle, updateWeekSkill, value, 2);
            } else if (str.equals("3")) {
                showRankingTitleNew(tvTitle, ivTitle, updateWeekSkill, value, 3);
            } else if (str.equals("4")) {
                showRankingTitleNew(tvTitle, ivTitle, updateWeekSkill, value, 4);
            } else if (str.equals("5")) {
                showRankingTitleNew(tvTitle, ivTitle, updateWeekSkill, value, 5);
            }
        }

    }


    public void showRankingTitle(TextView tvTitle, String key, int value, int skillId) {
        if (1 == value || 2 == value || 3 == value) {
            if (regalMonth.equals(key) || regalWeek.equals(key)) {//富豪月,周
                tvTitle.setText(titlesRegal[value - 1]);
                tvTitle.setBackgroundResource(iconsRegal[value - 1]);
            } else if (charmMonth.equals(key) || charmWeek.equals(key)) {//魅力月，周
                tvTitle.setText(titlesCharm[value - 1]);
                tvTitle.setBackgroundResource(iconsCharm[value - 1]);
            } else {//技能
                showRankingSkill(skillId, key, value, tvTitle);
            }

        }
//        else if(2 == value){
//            if(regalMonth.equals(key)||regalWeek.equals(key)){//富豪月,周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsRegal[value-1]);
//            }else if(charmMonth.equals(key)||charmWeek.equals(key)){//魅力月，周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsCharm[value-1]);
//            }else {//技能
//                showRankingSkill(skillId,key,value,tvTitle);
//            }
//
//        }else if(3 == value){
//            if(regalMonth.equals(key)||regalWeek.equals(key)){//富豪月,周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsRegal[value-1]);
//            }else if(charmMonth.equals(key)||charmWeek.equals(key)){//魅力月，周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsCharm[value-1]);
//            }else {//技能
//                showRankingSkill(skillId,key,value,tvTitle);
//            }
//        }
        else if (4 <= value) {
            if (regalMonth.equals(key) || regalWeek.equals(key)) {//富豪月,周
                tvTitle.setText(titlesRegal[3]);
                tvTitle.setBackgroundResource(iconsRegal[3]);
            } else if (charmMonth.equals(key) || charmWeek.equals(key)) {//魅力月，周
                tvTitle.setText(titlesCharm[3]);
                tvTitle.setBackgroundResource(iconsCharm[3]);
            } else {//技能
                showRankingSkill(skillId, key, value, tvTitle);
            }

        }
    }

    public void showRankingTitleNew(TextView tvTitle, RelativeLayout ivTitle, String key, int value, int skillId) {
        if (1 == value || 2 == value || 3 == value) {
            if (regalMonth.equals(key) || regalWeek.equals(key)) {//富豪月,周
                tvTitle.setText(titlesRegal[value - 1]);
                ivTitle.setBackgroundResource(iconsRegal[value - 1]);
            } else if (charmMonth.equals(key) || charmWeek.equals(key)) {//魅力月，周
                tvTitle.setText(titlesCharm[value - 1]);
                ivTitle.setBackgroundResource(iconsCharm[value - 1]);
            } else {//技能
                showRankingSkillNew(skillId, key, value, tvTitle, ivTitle);
            }

        }
//        else if(2 == value){
//            if(regalMonth.equals(key)||regalWeek.equals(key)){//富豪月,周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsRegal[value-1]);
//            }else if(charmMonth.equals(key)||charmWeek.equals(key)){//魅力月，周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsCharm[value-1]);
//            }else {//技能
//                showRankingSkill(skillId,key,value,tvTitle);
//            }
//
//        }else if(3 == value){
//            if(regalMonth.equals(key)||regalWeek.equals(key)){//富豪月,周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsRegal[value-1]);
//            }else if(charmMonth.equals(key)||charmWeek.equals(key)){//魅力月，周
//                tvTitle.setText("");
//                tvTitle.setBackgroundResource(iconsCharm[value-1]);
//            }else {//技能
//                showRankingSkill(skillId,key,value,tvTitle);
//            }
//        }
        else if (4 <= value) {
            if (regalMonth.equals(key) || regalWeek.equals(key)) {//富豪月,周
                tvTitle.setText(titlesRegal[3]);
                ivTitle.setBackgroundResource(iconsRegal[3]);
            } else if (charmMonth.equals(key) || charmWeek.equals(key)) {//魅力月，周
                tvTitle.setText(titlesCharm[3]);
                ivTitle.setBackgroundResource(iconsCharm[3]);
            } else {//技能
                showRankingSkillNew(skillId, key, value, tvTitle, ivTitle);
            }

        }
    }


    public void showRankingSkill(int skillId, String key, int value, TextView tvRankingTitle) {
        if (value >= 4) {
            value = 4;
        }
        if (key.equals(activeMonthSkill) || key.equals(activeWeekSkill)) {

            if (skillId == 1) {
                tvRankingTitle.setText(titlesActiveJuhuacan[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsJuhuancanActive[value - 1]);

            } else if (skillId == 2) {
                tvRankingTitle.setText(titlesActiveDameme[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsDamemeActive[value - 1]);

            } else if (skillId == 3) {
                tvRankingTitle.setText(titlesActiveBaohufei[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsBaohufeiActive[value - 1]);

            } else if (skillId == 4) {
                tvRankingTitle.setText(titlesActiveWuyingjiao[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsWuyingjiaoActive[value - 1]);

            } else if (skillId == 5) {
                tvRankingTitle.setText(titlesActiveJinguzhou[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsJinguzhouActive[value - 1]);

            }

        } else if (key.equals(updateMonthSkill) || key.equals(updateWeekSkill)) {
            if (skillId == 1) {
                tvRankingTitle.setText(titlesGrowJuhuacan[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsJuhuancanGrow[value - 1]);

            } else if (skillId == 2) {
                tvRankingTitle.setText(titlesGrowDameme[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsDamemeGrow[value - 1]);

            } else if (skillId == 3) {
                tvRankingTitle.setText(titlesGrowBaohufei[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsBaohufeiGrow[value - 1]);

            } else if (skillId == 4) {
                tvRankingTitle.setText(titlesGrowWuyingjiao[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsWuyingjiaoGrow[value - 1]);

            } else if (skillId == 5) {
                tvRankingTitle.setText(titlesGrowJinguzhou[value - 1]);
                tvRankingTitle.setBackgroundResource(iconsJinguzhouGrow[value - 1]);

            }

        }

    }

    public void showRankingSkillNew(int skillId, String key, int value, TextView tvRankingTitle, RelativeLayout ivRankingTitle) {
        if (value == 0) {
            return;
        }
        if (value >= 4) {
            value = 4;
        }
        if (key.equals(activeMonthSkill) || key.equals(activeWeekSkill)) {

            if (skillId == 1) {
                tvRankingTitle.setText(titlesActiveJuhuacan[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsJuhuancanActive[value - 1]);

            } else if (skillId == 2) {
                tvRankingTitle.setText(titlesActiveDameme[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsDamemeActive[value - 1]);

            } else if (skillId == 3) {
                tvRankingTitle.setText(titlesActiveJinguzhou[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsJinguzhouActive[value - 1]);

            } else if (skillId == 4) {
                tvRankingTitle.setText(titlesActiveWuyingjiao[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsWuyingjiaoActive[value - 1]);

            } else if (skillId == 5) {
                tvRankingTitle.setText(titlesActiveBaohufei[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsBaohufeiActive[value - 1]);

            }

        } else if (key.equals(updateMonthSkill) || key.equals(updateWeekSkill)) {
            if (skillId == 1) {
                tvRankingTitle.setText(titlesGrowJuhuacan[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsJuhuancanGrow[value - 1]);

            } else if (skillId == 2) {
                tvRankingTitle.setText(titlesGrowDameme[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsDamemeGrow[value - 1]);

            } else if (skillId == 3) {
                tvRankingTitle.setText(titlesGrowJinguzhou[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsJinguzhouGrow[value - 1]);

            } else if (skillId == 4) {
                tvRankingTitle.setText(titlesGrowWuyingjiao[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsWuyingjiaoGrow[value - 1]);

            } else if (skillId == 5) {
                tvRankingTitle.setText(titlesGrowBaohufei[value - 1]);
                ivRankingTitle.setBackgroundResource(iconsBaohufeiGrow[value - 1]);

            }

        }

    }


}
