package net.iaround.ui.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BatchUploadManager;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.AuthenticationBean;
import net.iaround.model.entity.BaseEntity;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by yz on 2018/8/14.
 * 实名认证界面
 */

public class AuthenticationActivity extends TitleActivity implements HttpCallBack, View.OnClickListener, BatchUploadManager.BatchUploadCallBack {

    private LinearLayout mLlStatus;
    private ImageView mIvStatus;
    private TextView mTvStatus;
    private TextView mTvFailTip;
    private TextView mTvAuthenticationTip;
    private EditText mEtRealName;
    private EditText mEtRealPhone;
    private ImageView mIvFront;
    private ImageView mIvBack;
    private LinearLayout mLlFront;
    private LinearLayout mLlBack;
    private TextView mTvSubmit;

    private BatchUploadManager mBatchUploadManager;

    private AuthenticationBean.AuthInfo mAuthInfo;

    private long mGetAuthenticationFlag;
    private long mPutAuthenticationFlag;
    private String mFront;//身份证正面url
    private String mBack;//身份证反面url
    private String mName;
    private String mPhone;
    private boolean isSubmit = false;
    private ArrayList<String> mTempList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_C(R.string.authentication);
        setContent(R.layout.activity_authentication);
        initView();
        mBatchUploadManager = new BatchUploadManager(this);
        showWaitDialog();
        mGetAuthenticationFlag = GameChatHttpProtocol.getAuthentication(this, this);
    }

    private void initView() {
        mLlStatus = (LinearLayout) findViewById(R.id.ll_status);
        mIvStatus = (ImageView) findViewById(R.id.iv_status);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvFailTip = (TextView) findViewById(R.id.tv_fail_tip);
        mTvAuthenticationTip = (TextView) findViewById(R.id.tv_authentication_tip);
        mEtRealName = (EditText) findViewById(R.id.et_real_name);
        mEtRealPhone = (EditText) findViewById(R.id.et_real_phone);
        mEtRealPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        mIvFront = (ImageView) findViewById(R.id.iv_front);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mLlFront = (LinearLayout) findViewById(R.id.ll_front);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mTvSubmit = (TextView) findViewById(R.id.tv_submit);
        mIvFront.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mLlFront.setOnClickListener(this);
        mLlBack.setOnClickListener(this);
        mTvSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_front:
                showBigPic(0);
                break;
            case R.id.iv_back:
                showBigPic(1);
                break;
            case R.id.ll_front:
                GalleryUtils.getInstance().openGallerySingle(this, 201, mOnHanlderResultCallback);
                break;
            case R.id.ll_back:
                GalleryUtils.getInstance().openGallerySingle(this, 202, mOnHanlderResultCallback);

                break;
            case R.id.tv_submit:

                if (checkInfo() && !isSubmit) {
                    isSubmit = true;
                    showWaitDialog();
                    uploadPic();
                }
                break;
        }
    }

    /**
     * 查看大图
     *
     * @param position 0-查看身份证正面 1-查看身份证反面
     */
    private void showBigPic(int position) {
        mTempList.clear();
        if (position == 0) {
            if (!TextUtils.isEmpty(mFront)) {
                mTempList.add(mFront);
                PictureDetailsActivity.launch(mContext, mTempList, 0);
            }else {
                GalleryUtils.getInstance().openGallerySingle(this, 201, mOnHanlderResultCallback);
            }
        }

        if (position == 1) {
            if (!TextUtils.isEmpty(mBack)) {
                mTempList.add(mBack);
                PictureDetailsActivity.launch(mContext, mTempList, 0);
            }else {
                GalleryUtils.getInstance().openGallerySingle(this, 202, mOnHanlderResultCallback);
            }
        }
    }

    private boolean checkInfo() {
        mName = mEtRealName.getText().toString().trim();
        if (TextUtils.isEmpty(mName)) {
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.edit_real_name);
            return false;
        }
        mPhone = mEtRealPhone.getText().toString().trim();

        if (TextUtils.isEmpty(mPhone) || !CommonFunction.isPhoneNumberValid(mPhone)) {
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.authen_phone_check_just);
            return false;
        }
        if (TextUtils.isEmpty(mFront) || TextUtils.isEmpty(mPhone)) {
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_identity_card_photo_tip);
            return false;
        }

        return true;
    }

    private void submitInfo() {
        mPutAuthenticationFlag = GameChatHttpProtocol.putAuthInfo(this, mName, mPhone, mFront, mBack, this);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int requestCode, List<PhotoInfo> resultList) {
            if (resultList != null && resultList.size() > 0) {
                String photo = resultList.get(0).getPhotoPath();
                if (requestCode == 201) {
                    mFront = photo;
                    GlideUtil.loadRoundImageNew(BaseApplication.appContext, photo, 10, mIvFront, R.drawable.pic_card_front, R.drawable.pic_card_front);

                } else if (requestCode == 202) {
                    mBack = photo;
                    GlideUtil.loadRoundImageNew(BaseApplication.appContext, photo, 10, mIvBack, R.drawable.pic_card_back, R.drawable.pic_card_back);

                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        if (mGetAuthenticationFlag == flag) {
            AuthenticationBean bean = GsonUtil.getInstance().getServerBean(result, AuthenticationBean.class);
            if (bean != null && bean.isSuccess()) {
                if (bean.authInfo != null) {
                    mAuthInfo = bean.authInfo;
                    mEtRealName.setText(mAuthInfo.realName);
                    mEtRealPhone.setText(mAuthInfo.phone);
                    if (!TextUtils.isEmpty(mAuthInfo.frontPhoto) && !TextUtils.isEmpty(mAuthInfo.backPhoto)) {
                        mFront = mAuthInfo.frontPhoto;
                        mBack = mAuthInfo.backPhoto;
                        GlideUtil.loadRoundImageNew(BaseApplication.appContext, mAuthInfo.frontPhoto, 10, mIvFront, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
                        GlideUtil.loadRoundImageNew(BaseApplication.appContext, mAuthInfo.backPhoto, 10, mIvBack, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
                    }
                    setStatus(mAuthInfo.status);
                }
            }
        } else if (mPutAuthenticationFlag == flag) {
            BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
            if (baseEntity.isSuccess()) {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.apply_join_group_success);
                finish();
            } else {
                isSubmit = false;
            }
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        isSubmit = false;
        ErrorCode.toastError(mContext, e);
    }

    //设置审核状态
    private void setStatus(int status) {
        switch (status) {
            case 0:
                mLlStatus.setVisibility(View.GONE);
                mTvAuthenticationTip.setVisibility(View.VISIBLE);
                break;
            case 1:
                mTvAuthenticationTip.setVisibility(View.GONE);
                mLlStatus.setVisibility(View.VISIBLE);
                mIvStatus.setImageResource(R.drawable.pic_success);
                mTvStatus.setText(R.string.authen_success);
                mTvSubmit.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                mTvSubmit.setTextColor(getResources().getColor(R.color.common_black));
                mTvSubmit.setEnabled(false);
                break;
            case 2:
                mTvAuthenticationTip.setVisibility(View.GONE);
                mLlStatus.setVisibility(View.VISIBLE);
                mIvStatus.setImageResource(R.drawable.pic_failure);
                mTvStatus.setText(R.string.authen_status_fail);
                if (!TextUtils.isEmpty(mAuthInfo.reason)) {
                    mTvFailTip.setText(mAuthInfo.reason);
                }
                mTvFailTip.setVisibility(View.VISIBLE);
                break;
            case 3:
                mTvAuthenticationTip.setVisibility(View.GONE);
                mLlStatus.setVisibility(View.VISIBLE);
                mIvStatus.setImageResource(R.drawable.pic_under_review);
                mTvStatus.setText(R.string.group_status_examining);
                mTvSubmit.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                mTvSubmit.setTextColor(getResources().getColor(R.color.common_black));
                mTvSubmit.setEnabled(false);
                break;
        }
    }

    //上传图片
    private void uploadPic() {
        final ArrayList<String> updateImgs = new ArrayList<>();
        String urlFront = mFront.contains(PathUtil.getFILEPrefix()) ? mFront.replace(PathUtil.getFILEPrefix(), "") : mFront;
        String urlBack = mBack.contains(PathUtil.getFILEPrefix()) ? mBack.replace(PathUtil.getFILEPrefix(), "") : mBack;

        updateImgs.add(urlFront);
        updateImgs.add(urlBack);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBatchUploadManager.uploadImage(TimeFormat.getCurrentTimeMillis(), updateImgs, FileUploadType.PIC_ALBUM, AuthenticationActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    isSubmit = false;
                    CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_pic_fail);
                    destroyWaitDialog();
                }
            }
        }).start();
    }

    @Override
    public void batchUploadSuccess(long taskFlag, ArrayList<String> serverUrlList) {
        if (serverUrlList != null && serverUrlList.size() > 1) {
            mFront = serverUrlList.get(0);
            mBack = serverUrlList.get(1);
            submitInfo();
        }
    }

    @Override
    public void batchUploadFail(long taskFlag) {
        isSubmit = false;
        CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_pic_fail);

    }
}
