package net.iaround.presenter;

import android.content.Context;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BatchUploadManager;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.PhotoHttpProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.contract.EditUserInfoContract;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.entity.BaseEntity;
import net.iaround.model.entity.EditUserInfoEntity;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.UserInfoEntity;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.EditUserInfoActivity;
import net.iaround.ui.datamodel.AlbumUploadBackBean;
import net.iaround.ui.datamodel.AlbumUploadBaseBean;
import net.iaround.ui.datamodel.Photo;
import net.iaround.ui.datamodel.Photos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author：liush on 2016/12/12 21:46
 */
public class EditUserInfoPresenter implements BatchUploadManager.BatchUploadCallBack {

    private EditUserInfoContract.View editUserInfoView;
    private EditUserInfoEntity editUserInfoEntity;
    private Context mContext;
    private long SPACE_PHOTOLIST_FLAG;

    private int photoSize;

    public EditUserInfoPresenter(Context context, EditUserInfoContract.View editUserInfoView) {
        this.mContext = context;
        this.editUserInfoView = editUserInfoView;
    }

    public void init() {
        UserInfoEntity.Data userinfo = (UserInfoEntity.Data) ((EditUserInfoActivity) editUserInfoView).getIntent().getSerializableExtra(Constants.USERINFO);
        initEntity(userinfo);
        if (editUserInfoEntity != null) {
            editUserInfoView.setVPHeadPics(editUserInfoEntity.getHeadPicsthum(), editUserInfoEntity.getHeadPics(), editUserInfoEntity.getPhotos());
            editUserInfoView.setUIInfo(editUserInfoEntity);
            editUserInfoView.setHeadviewPhoto(editUserInfoEntity.getHeadPics());
            editUserInfoView.refreshSex(userinfo.isHadsetname());
            editUserInfoEntity.setGenderCache(userinfo.getGender());
        }

    }

    private void initEntity(UserInfoEntity.Data userinfo) {
        if (userinfo != null) {
            editUserInfoEntity = new EditUserInfoEntity(BaseApplication.appContext);
            ArrayList<String> headThum = new ArrayList<String>();
//            if (userinfo.getHeadPic() != null && userinfo.getHeadPic().size() > 0) {
//                for (String headUrl : userinfo.getHeadPic()) {
//                    String thumUrl = CommonFunction.getThumPicUrl(headUrl);
//                    headThum.add(thumUrl);
//                }
//            }
            if (userinfo.getHeadPhonts() != null && userinfo.getHeadPhonts().size() > 0) {
                for (Photos.PhotosBean photosBean : userinfo.getHeadPhonts()) {
                    String thumUrl = CommonFunction.getThumPicUrl(photosBean.getImage());
                    headThum.add(thumUrl);
                }
            }
            editUserInfoEntity.setHeadPicsthum(headThum);
            editUserInfoEntity.setHeadPics(userinfo.getHeadPic());
            Collections.reverse(userinfo.getHeadPhonts());
            editUserInfoEntity.setPhotos(userinfo.getHeadPhonts());
            editUserInfoEntity.setNickname(userinfo.getNickname());
            editUserInfoEntity.setSex(userinfo.getGender());
            editUserInfoEntity.setBirthday(userinfo.getBirthday());
            editUserInfoEntity.setSignature(userinfo.getMoodtext());
            editUserInfoEntity.setLoveStatus(userinfo.getAboutMe2().getLove());
            editUserInfoEntity.setJob(userinfo.getSecret().getOccupation() + "");
            editUserInfoEntity.setHometown(userinfo.getAboutMe2().getHometown());
            editUserInfoEntity.setHeight(userinfo.getAboutMe2().getHeight());
            for (int i = 0; i < userinfo.getAboutme().size(); i++) {
                int uname = Integer.parseInt(userinfo.getAboutme().get(i).getUname());
                if (uname == 3) {
                    editUserInfoEntity.setHeight(Integer.parseInt(userinfo.getAboutme().get(i).getUvalue()));
                }
                if (uname == 4) {
                    editUserInfoEntity.setWeight(Integer.parseInt(userinfo.getAboutme().get(i).getUvalue()));
                }
            }
//            editUserInfoEntity.setHeight(userinfo.getAboutMe2().getHeight());
//            editUserInfoEntity.setWeight(userinfo.getAboutMe2().getWeight());
            editUserInfoEntity.setIncome(userinfo.getSecret().getSalary());
            editUserInfoEntity.setOwnHouse(userinfo.getSecret().getHouse());
            editUserInfoEntity.setOwnCar(userinfo.getSecret().getCar());
            editUserInfoEntity.setUniversity(userinfo.getSecret().getSchool());
            editUserInfoEntity.setCompany(userinfo.getSecret().getCompany());
//            editUserInfoEntity.setHobbys(userinfo.getHobby().getSpecial());
            editUserInfoEntity.setPhoneFlag(userinfo.getPhoneFlag());
            editUserInfoEntity.setLastLocalFlag(userinfo.getLocationFlag());
            editUserInfoEntity.setHoroscope(userinfo.getHoroscope());
            editUserInfoEntity.setOccupation(userinfo.getSecret().getOccupation() + "");
            Common.getInstance().setAlbumPic(userinfo.getHeadPhonts());
        }

    }

    public EditUserInfoEntity getEntity() {
        if (editUserInfoEntity == null) {
            editUserInfoEntity = new EditUserInfoEntity(BaseApplication.appContext);
        }
        return editUserInfoEntity;
    }

    public void refreshHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, List<Photos.PhotosBean> photos) {
        editUserInfoView.refreshHeadPics(picsThum, pics, photos);
    }

    /**
     * 发协议的地方
     */
    private void sendProtocal(final AlbumUploadBaseBean bean) {
        String photoStr = "";// 组装
        int count = bean.getPhotoList() != null ? bean.getPhotoList().size()
                : 0;
        for (int i = 0; i < count; i++) {

            photoStr += bean.getPhotoList().get(i)
                    + (count - i != 1 ? "," : "");
        }
        if (mContext == null)
            mContext = BaseApplication.appContext;
        GeoData geoData = LocationUtil.getCurrentGeo(mContext);
        int plat = Config.PLAT;
        String content = bean.getContent();

        PhotoHttpProtocol.asynPublishPhoto(mContext,
                plat, photoStr, content, geoData.getLat(),
                geoData.getLng(), bean.getAddress(), bean.getSync(), new HttpCallBack() {
                    @Override
                    public void onGeneralSuccess(String result, long flag) {
                        AlbumUploadBackBean backBean = GsonUtil.getInstance().getServerBean(result, AlbumUploadBackBean.class);
                        if (backBean != null && backBean.isSuccess()) {

                            SpaceModel.getInstance(mContext).photoListReq(Common.getInstance().loginUser.getUid(), 1, 200, new HttpCallBack() {
                                @Override
                                public void onGeneralSuccess(String result, long flag) {
                                    HashMap<String, Object> res = null;
                                    try {
                                        res = SpaceModel.getInstance(mContext).getRes(result, flag);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                    if (res != null) {
                                        if ((Integer) res.get("status") == 200) {
                                            ArrayList<Photo> array = (ArrayList<Photo>) res.get("photos");
                                            ArrayList<Photos.PhotosBean> photosBeanArrayList = new ArrayList<Photos.PhotosBean>();
                                            for (Photo photo : array) {
                                                Photos.PhotosBean bean = new Photos.PhotosBean();
                                                bean.setImage(photo.getUri());
                                                bean.setPhotoid(photo.getId());
                                                photosBeanArrayList.add(bean);
                                            }
                                            editUserInfoEntity.setPhotos(photosBeanArrayList);
                                            refreshHeadPics(editUserInfoEntity.getHeadPicsthum(), editUserInfoEntity.getHeadPics(), editUserInfoEntity.getPhotos());

                                        }
                                    }
                                }

                                @Override
                                public void onGeneralError(int e, long flag) {

                                }
                            });
                        } else {
                            ErrorCode.showError(mContext, result);
                        }

                    }

                    @Override
                    public void onGeneralError(int e, long flag) {
                        ErrorCode.toastError(mContext, e);
                    }
                });
    }

    public void upLoadPic(ArrayList<String> picPathList) {
        AlbumUploadBaseBean bean = new AlbumUploadBaseBean();
        bean.setPhotoList(picPathList);
        bean.setSync("");
        sendProtocal(bean);

    }

    public void uploadInfo(final Context context, ArrayList<String> lableList) {
        this.mContext = context;
        UserHttpProtocol.userInfoUpdate(context, editUserInfoEntity, new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
//                CommonFunction.log("xiaohua", "result = " + result);
                BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
                if (baseEntity != null) {
                    if (baseEntity.isSuccess()) {
                        editUserInfoView.userinfoUpdateSuc();
                        Common.getInstance().loginUser.setNickname(editUserInfoEntity.getNickname());
                        Common.getInstance().loginUser.setAge(editUserInfoEntity.getUserAge());
                    } else {
                        ErrorCode.showError(context, result);
                    }
                }
            }

            @Override
            public void onGeneralError(int e, long flag) {

            }
        });
    }

    public void changePhoneFlag() {
        if (editUserInfoEntity != null) {
            if (editUserInfoEntity.getPhoneFlagB()) {
                editUserInfoEntity.setShowDevice(false);
            } else {
                editUserInfoEntity.setShowDevice(true);
            }
            editUserInfoEntity.setPhoneFlagB(!editUserInfoEntity.getPhoneFlagB());
            editUserInfoView.setPhoneFlag(editUserInfoEntity.getPhoneFlagB());
        }

    }

    public void changeLocalFlag() {
        if (editUserInfoEntity != null) {
            if (editUserInfoEntity.getLastLocalFlagB()) {
                editUserInfoEntity.setShowLocation(false);
            } else {
                editUserInfoEntity.setShowLocation(true);
            }
            editUserInfoEntity.setLastLocalFlagB(!editUserInfoEntity.getLastLocalFlagB());
            editUserInfoView.setLocalFlag(editUserInfoEntity.getLastLocalFlagB());
        }

    }

    public void updatePic(Context mContext, ArrayList<String> uploadTaskArray) {
        this.mContext = mContext;
        final long taskFlag = TimeFormat.getCurrentTimeMillis()
                & BatchUploadManager.IMAGE_TASK_MASK;
        try {
            new BatchUploadManager(mContext).uploadImage(taskFlag, uploadTaskArray,
                    FileUploadType.PIC_ALBUM, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void batchUploadSuccess(long taskFlag, ArrayList<String> serverUrlList) {
        upLoadPic(serverUrlList);
    }

    @Override
    public void batchUploadFail(long taskFlag) {
        CommonFunction.showToast(mContext, mContext.getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT);
    }


    public int getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(int photoSize) {
        this.photoSize = photoSize;
    }

    public void setHeadPic(ArrayList<String> headPic) {
        editUserInfoEntity.setHeadPics(headPic);
    }

    public void setHeadPicsthum(ArrayList<String> headPic) {
        editUserInfoEntity.setHeadPicsthum(headPic);
    }


    public void setGender(String gender){
        editUserInfoEntity.setSex(gender);
    }

}
