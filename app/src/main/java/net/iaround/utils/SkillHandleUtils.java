package net.iaround.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;

import net.iaround.model.chat.BaseWorldMessageBean;
import net.iaround.model.chat.WorldMessageBean;
import net.iaround.model.chat.WorldMessageGiftContent;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.model.chat.WorldMessageRecruitContent;
import net.iaround.model.chat.WorldMessageTextContent;
import net.iaround.model.entity.Item;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.RankingTitleUtil;

/**
 * 作者：zx on 2017/8/29 21:17
 */
public class SkillHandleUtils {

    public static WorldMessageRecord parseMsg(String message){

        WorldMessageBean worldMessageBean = GsonUtil.getInstance().getServerBean(message, WorldMessageBean.class);
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(worldMessageBean.message);
        String content = JSON.toJSONString(jsonObject);
        return pareseToRecord(content);
    }

    public static WorldMessageRecord pareseToRecord(String content){
        WorldMessageRecord record = new WorldMessageRecord();
        Item item = RankingTitleUtil.getInstance().getTitleItemFromChatBarWorkd(content);
        record.rankItem = item;
        BaseWorldMessageBean bean = GsonUtil.getInstance().getServerBean(content, BaseWorldMessageBean.class);
        if (null == bean){
            return null;
        }

        record.user = bean.user;
        record.messageType = bean.type;
        record.recruit = bean.recruit;

        if (30 == record.messageType){//文本
            WorldMessageTextContent textContent = new WorldMessageTextContent();
            textContent.content = bean.content;
            record.textContent = textContent;
        }
        if (31 == record.messageType){//招募
            WorldMessageRecruitContent recruitContent = GsonUtil.getInstance().getServerBean(bean.content, WorldMessageRecruitContent.class);
            record.recruitContent = recruitContent;
        }
        if (32 == record.messageType){//礼物
            WorldMessageGiftContent giftContent = GsonUtil.getInstance().getServerBean(bean.content, WorldMessageGiftContent.class);
            record.giftContent = giftContent;
        }
        if (33 == record.messageType){//技能
            SkillAttackResult skillContent = GsonUtil.getInstance().getServerBean(bean.content, SkillAttackResult.class);
            if (null == skillContent){
                return null;
            }
            if (null != skillContent.user){//针对技能消息列表用户，增加称号
                skillContent.user.rankItem = item;
            }
            record.skillContent = skillContent;
        }
        return record;
    }

    /**
     * 停止播放动画
     * @param iv_frame
     */
    public static void stopFrameAnim(ImageView iv_frame){
        iv_frame.setBackgroundDrawable(null);
    }

    /**
     * 播放帧动画
     * @param context
     * @param skillName
     * @param iv_frame
     */
    public static void playFrame(Context context, String skillName, final ImageView iv_frame){
        if (TextUtils.isEmpty(skillName)){
            return;
        }
        String[] split = skillName.split("_");
        skillName = "frame_loop_" + split[0];
        int drawableId = context.getResources().getIdentifier(skillName, "drawable", context.getPackageName());
        AnimationDrawable frameAnim = (AnimationDrawable) context.getResources().getDrawable(drawableId);
        iv_frame.setBackgroundDrawable(frameAnim);
        frameAnim.start();
    }

    /**
     * 设置首帧图
     * @param context
     * @param skillName
     * @param imageView
     */
    public static void setFirstFrameImage(Context context, String skillName, ImageView imageView){
        if (TextUtils.isEmpty(skillName)){
            return;
        }
        String[] split = skillName.split("_");
        skillName = split[0];
        if ("burst".equals(skillName)){
            skillName = "burst_2700_47_first";
        }else if ("kick".equals(skillName)){
            skillName = "kick_1000_15_first";
        }else if ("kiss".equals(skillName)){
            skillName = "kiss_1600_19_first";
        }else if ("protect".equals(skillName)){
            skillName = "protect_2000_29_first";
        }else if ("speak".equals(skillName)){
            skillName = "speak_1900_25_first";
        }
        int drawableId = context.getResources().getIdentifier(skillName, "drawable", context.getPackageName());
        imageView.setBackgroundResource(drawableId);
    }

}
