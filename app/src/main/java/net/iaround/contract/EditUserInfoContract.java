package net.iaround.contract;

import net.iaround.model.entity.EditUserInfoEntity;
import net.iaround.ui.datamodel.Photos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liush on 2016/12/12 21:27
 */
public interface EditUserInfoContract {

    interface View {
        /**
         * @param picsThum 相册缩略图
         * @param pics     头像
         * @param photos   相册原图
         */
        void setVPHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, List<Photos.PhotosBean> photos);

        void refreshHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, List<Photos.PhotosBean> photos);

        void setUIInfo(EditUserInfoEntity data);

        void setNickname(String nickname);

        String getNickname();

        void setSex(String sex);

        void setBirthday(long birthday);

        void setBirthday(String birthday);

        long getBirthday();

        void setSignature(String signature);

        String getSignature();

        void setLoveStatus(String status);

        void setJob(String job);

        String getJob();

        void setHometown(String hometown);

        String getHometown();

        void setBodyHeight(int height);

        int getBodyHeight();

        void setBodyWeight(int weight);

        int getBodyWeight();

        void setIncome(String income);

        String getIncome();

        void setOwnHouse(String status);

        void setOwnCar(String status);

        void setUniversity(String university);

        String getUniversity();

        void setCompany(String company);

        String getCompany();

        void setHobbys(List<String> hobbyIds);

        void setPhoneFlag(boolean status);

        void setLocalFlag(boolean status);

        void showToast(String msg);

        void userinfoUpdateSuc();

        void setHeadviewPhoto(ArrayList<String> pics);

        void refreshSex(boolean isHadsetname);


    }

    interface Persenter {
    }
}
