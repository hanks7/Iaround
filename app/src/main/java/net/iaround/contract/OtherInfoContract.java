package net.iaround.contract;

import android.content.Context;

import net.iaround.model.entity.Item;
import net.iaround.model.entity.UserInfoEntity;
import net.iaround.model.entity.UserInfoGameInfoBean;
import net.iaround.ui.datamodel.Photos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liush on 2017/1/19 19:47
 */
public interface OtherInfoContract {
    interface View {
        void setHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, ArrayList<Photos.PhotosBean> headPhonts);

        void setRelation(int relation);

        void setNickname(String nickname,String skillTitle);//技能称号

        void setTimeAndDistance(long time, double distance);

        void setAgeAndSex(String age, String sex);

        void setHoroscope(int horoscope);

        void setSignature(String signature);

        void setAction(int count, ArrayList<String> actionPics);

        void setAccount(int vip, int level, long vipexpire, int charmnum, int userlevel);

        void setGift(ArrayList<String> gifts);

        void setHobby(ArrayList<String> commonHobby, ArrayList<String> specialHobby);

        void setAboutMe(ArrayList<UserInfoEntity.Data.AboutMe> aboutMe);

        void setSecret(UserInfoEntity.Data.Secret secret);

        void setAuthenInfo(int phoneStatus, int picStatus);

        void setLocation(Boolean isShow, UserInfoEntity.Data.Location location);

        void setPhone(Boolean isShow, String phone);

        void hideAllViews();

        void showDialog(Context context);

        void hideDialog();

        void saveIsChat(int ischat);

        void setLastLocal(UserInfoEntity.Data.Location location);

        void setSkillStatus(UserInfoEntity.Data.AffectBean affect, UserInfoEntity.Data.PlayGroupBean playGroup);//AffectBean 技能状态，PlayGroupBean 当前聊吧

        void setSkillList (List<UserInfoEntity.Data.SkillBean> skill);

        void setRankingTitle(Item item);

        void setUserType(int userType,int love);

        //是否为语音主播
        void setVoiceUserType(int voiceUserType);

        void setGameList(ArrayList<UserInfoGameInfoBean> beans);
    }
}
