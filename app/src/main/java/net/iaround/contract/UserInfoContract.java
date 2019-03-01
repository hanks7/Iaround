package net.iaround.contract;

import android.app.Activity;
import android.content.Context;

import net.iaround.model.entity.UserInfoEntity;
import net.iaround.ui.datamodel.Photos;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liush on 2016/12/5 14:32
 */
public interface UserInfoContract {

    interface View {
        void setTitle(String title);

        void showHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, ArrayList<Photos.PhotosBean> photos);

        void setAgeAndSex(String sex, String age);

        void setHoroscope(int horoscope);

        void setSignature(String signature);

        void setAction(int count, List<String> actionPics);

        void setAccount(int vip, int level, long vipexpire, int charmnum, int userlevel);

        void showGift(ArrayList<String> gifts);

        //设置标签，将标签的代码转换为对应的文本
        void setHobby(ArrayList<String> hobbys);

        void setAboutMe(ArrayList<UserInfoEntity.Data.AboutMe> aboutMe);

        void showFriendsAction(List<String> actions);

        void setAuthenInfo(int phoneStatus, int picStatus);

        void showLastLocal(Boolean isShow);

        void setLastLocal(UserInfoEntity.Data.Location location);

        void showUserPhone(Boolean isShow);

        void setUserPhone(String phone);

        void hideAllViews();

        void setAboutMeComplete(String complete);
    }

    interface Persenter {
        /* Activity创建
        * */
        public void onActivityCreate();

        /* Activity销毁
       * */
        public void onActivityDestroy();

        /**
         * 初始化个人信息
         */
        public void init(Context context, long uid, int cachetype);

        /**
         * 初始化他人信息
         *
         * @param uid
         */
        public void initOtherInfo(Context context, final long uid, int cachetype);

        /*获得用户信息
        * */
        public UserInfoEntity.Data getData();


        /**
         * 更新与他人的关系(不看他动态、不让他看动态、拉入黑名单)
         *
         * @param type 1、黑名单
         *             2、不让ta看我的动态
         *             3、我不看ta的动态
         *             4、关注
         * @param opt  1、添加  2、删除
         *             5.type  1。代表取消关注2。添加关注
         */
        public void updateRelation(final Context context, int type, final User me, final int opt);


        /**
         * @param uid 被举报对象
         */
        public void report(long uid, int type);


        public void startSecretActivity(Context context);

        public void startEditUserInfoActivity(Activity activity, int requestCode);
    }

}
