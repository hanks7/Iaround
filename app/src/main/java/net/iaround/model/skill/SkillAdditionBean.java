package net.iaround.model.skill;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * 作者：zx on 2017/8/26 14:30
 */
public class SkillAdditionBean extends BaseServerBean {
    /**
         "currencyRate": 10,   //货币加成
         "expendRate": 10,		//消耗加成
         "ranking": [
             {
             "rankingNum": "1”,  //名词
             "goldRate": 10,	//富豪榜
             "charRate": 10	//魅力榜
             },
             {
             "rankingNum": "2",
             "goldRate": 8,
             "charRate": 8
             },
             {
             "rankingNum": "4-10",
             "goldRate": 4,
             "charRate": 4
             }
         ]
     */

    public String currencyRate;
    public String expendRate;
    public List<AddtionRankBean> ranking;
    public List<AddtionRankBean> successRanking;

    public static class AddtionRankBean{
        public String rankingNum;
        public String goldRate;
        public String charRate;

        public String amountNum;
        public String updateRank;
        public String time;
    }
}
