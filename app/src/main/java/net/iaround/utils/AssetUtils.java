package net.iaround.utils;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;

import net.iaround.model.skill.SkillAttackResult;
import java.math.BigDecimal;

/**
 * 作者：zx on 2017/8/27 14:45
 */
public class AssetUtils {

    private static final int TYPE_COLOR = 0;
    private static final int IS_HIT = 1;

    private static final String[] successColor = BaseApplication.appContext.getResources().getStringArray(R.array.skill_desc_success);
    private static final String[] failureColor = BaseApplication.appContext.getResources().getStringArray(R.array.skill_desc_failure);
    private static final String[] successNoColor = BaseApplication.appContext.getResources().getStringArray(R.array.skill_desc_success_no_color);
    private static final String[] failureNoColor = BaseApplication.appContext.getResources().getStringArray(R.array.skill_desc_failure_no_color);

    private static final String[] successReceiveColor = BaseApplication.appContext.getResources().getStringArray(R.array.skill_desc_success_receive);
    private static final String[] failureReceiveColor = BaseApplication.appContext.getResources().getStringArray(R.array.skill_desc_failure_receive);
    
    /**
     *  获取技能内容模板，使用string数组，换掉读取本地文件方式
     * @param colorType 是否为变色文本，0：有色，1：无色
     * @return
     */
    public static String getDesc(int colorType, SkillAttackResult skillContent){
        if (null == skillContent){
            return "";
        }
        int skillId = skillContent.skillId;//技能id，同时为array的索引
        int isHit = skillContent.isHit;//是否命中
        String desc = "";//首先获取本地模板文本
        if (TYPE_COLOR == colorType){
            if (IS_HIT == isHit) {
                desc = successColor[skillId - 1];
            } else {
                desc = failureColor[skillId - 1];
            }

        }else {
            if (IS_HIT == isHit) {
                desc = successNoColor[skillId - 1];
            } else {
                desc = failureNoColor[skillId - 1];
            }

        }

        String format = "";//根据不同的技能id替换模板字符
        if (IS_HIT == isHit){
            if (1 == skillId || 2 == skillId){
                format = String.format(desc, skillContent.targetUser.NickName, skillContent.charm);
            }else if (3 == skillId || 4 == skillId){
                int time = 0;
                if (skillContent.affectTime > 60){
                    time = new BigDecimal(skillContent.affectTime / 60).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                }else {
                    time = 1;
                }
                format = String.format(desc, skillContent.targetUser.NickName, time);
            }else if (5 == skillId) {
                format = String.format(desc, skillContent.targetUser.NickName, skillContent.affectGoldNum);
            }
        }else {
            if (1 == skillId || 2 == skillId){
                format = String.format(desc, skillContent.targetUser.NickName, skillContent.charm);
            }else {
                format = String.format(desc, skillContent.targetUser.NickName);
            }
        }

        return format;
    }

    /**
     *  获取技能内容模板，使用string数组，换掉读取本地文件方式
     * @param colorType 是否为变色文本，0：有色，1：无色
     * @return
     */
    public static String getChatDesc(int colorType, SkillAttackResult skillContent){
        if (null == skillContent){
            return "";
        }
        int skillId = skillContent.skillId;//技能id，同时为array的索引
        int isHit = skillContent.isHit;//是否命中
        String desc = "";//首先获取本地模板文本
        if (TYPE_COLOR == colorType){
            if (skillContent.user.UserID == Common.getInstance().loginUser.getUid()){
                if (IS_HIT == isHit){
                    desc = successColor[skillId - 1];
                }else {
                    desc = failureColor[skillId - 1];
                }
            }else{
                if (IS_HIT == isHit){
                    desc = successReceiveColor[skillId - 1];
                }else {
                    desc = failureReceiveColor[skillId - 1];
                }
            }

        }else {

            if (IS_HIT == isHit) {
                desc = successNoColor[skillId - 1];
            } else {
                desc = failureNoColor[skillId - 1];
            }

        }

        String format = "";//根据不同的技能id替换模板字符
        if (skillContent.user.UserID == Common.getInstance().loginUser.getUid()){
            if (IS_HIT == isHit){
                if (1 == skillId || 2 == skillId){
                    format = String.format(desc, skillContent.targetUser.NickName, skillContent.charm);
                }else if (3 == skillId || 4 == skillId){
                    int time = 0;
                    if (skillContent.affectTime > 60){
                        time = new BigDecimal(skillContent.affectTime / 60).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                    }else {
                        time = 1;
                    }
                    format = String.format(desc, skillContent.targetUser.NickName, time);
                }else if (5 == skillId) {
                    format = String.format(desc, skillContent.targetUser.NickName, skillContent.affectGoldNum);
                }
            }else {
                if (1 == skillId || 2 == skillId){
                    format = String.format(desc, skillContent.targetUser.NickName, skillContent.charm);
                }else {
                    format = String.format(desc, skillContent.targetUser.NickName);
                }
            }
        }else{
            if (IS_HIT == isHit){
                if (1 == skillId || 2 == skillId){
                    format = String.format(desc, skillContent.user.NickName, skillContent.charm);
                }else if (3 == skillId || 4 == skillId){
                    int time = 0;
                    if (skillContent.affectTime > 60){
                        time = new BigDecimal(skillContent.affectTime / 60).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                    }else {
                        time = 1;
                    }
                    format = String.format(desc, skillContent.user.NickName, time);
                }else if (5 == skillId) {
                    format = String.format(desc, skillContent.user.NickName, skillContent.affectGoldNum);
                }
            }else {
                if (1 == skillId || 2 == skillId){
                    format = String.format(desc, skillContent.user.NickName, skillContent.charm);
                }else {
                    format = String.format(desc, skillContent.user.NickName);
                }
            }
        }


        return format;
    }

}
