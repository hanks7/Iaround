package net.iaround.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.SendBackManage;
import net.iaround.connector.protocol.PhotoHttpProtocol;
import net.iaround.contract.EditUserInfoContract;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.entity.EditUserInfoEntity;
import net.iaround.model.entity.HobbyType;
import net.iaround.model.type.FileUploadType;
import net.iaround.presenter.EditUserInfoPresenter;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.editpage.EditCompanyActivity;
import net.iaround.ui.activity.editpage.EditJobActivity;
import net.iaround.ui.activity.editpage.EditNicknameActivity;
import net.iaround.ui.activity.editpage.EditProvinceActivity;
import net.iaround.ui.activity.editpage.EditSignatureActivity;
import net.iaround.ui.activity.editpage.EditUniversityActivity;
import net.iaround.ui.activity.editpage.SelectLableActivity;
import net.iaround.ui.adapter.EditUserInfoHobbysAdapter;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.Photos;
import net.iaround.ui.group.activity.SpaceMe;
import net.iaround.ui.view.dialog.EditUserSexDialog;
import net.iaround.ui.view.dialog.PictureFrameDialog;
import net.iaround.utils.eventbus.HeadImageNotifyEvent;
import net.iaround.utils.eventbus.NickNameNotifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import me.huyunfeng.libs.android.upload.HttpRequest;


public class EditUserInfoActivity extends TitleActivity implements EditUserInfoContract.View, View.OnClickListener, HttpCallBack, PictureFrameDialog.ItemOnclick {

    private static final int EDIT_NICKNAME = 11;
    private static final int EDIT_BIRTHDAY = 12;
    private static final int EDIT_SIGNATURE = 13;
    private static final int EDIT_JOB = 14;
    private static final int EDIT_HOMETOWN = 15;
    private static final int EDIT_UNIVERSITY = 16;
    private static final int EDIT_COMPANY = 17;
    public static final int UPLOAD_HEAD = 18;
    private static final int EDIT_SPACE_UPLOAD_ICON = 1002;
    public static final int MSG_UPLOAD_ICON_STATE = 0xffff6;
    private static final int EDIT_SPACE_MODIFY_PHOTO = 1013; //
    private EditUserInfoPresenter presenter;
    /**
     * 基本资料
     */
    private TextView tvNickname;
    private TextView tvSex;
    private TextView tvBirthday;
    private TextView tvSignature;
    private TextView tvLoveStatus;
    private TextView tvBodyHeight;
    private TextView tvBodyWeight;

    /**
     * 私密资料
     *
     * @param savedInstanceState
     */
    private TextView tvIncome;
    private TextView tvJob;
    private TextView tvHometown;
    private TextView tvOwnHouse;
    private TextView tvOwnCar;
    private TextView tvUniversity;
    private TextView tvCompany;
    private TextView tvLable;
    private ListView lvHobbys;

    /**
     * 兴趣爱好部分所需要的数据
     *
     * @param savedInstanceState
     */

    private int[] hobbyTypeIcons = {R.drawable.edit_sport, R.drawable.edit_travel, R.drawable.edit_arts, R.drawable.edit_food, R.drawable.edit_fun, R.drawable.edit_rest};
    private String[] hobbyTypeDesc;
    private int[] hobbyTypeColor = {0xFF41CBD8, 0xFF7491FF, 0xFFA18EFF, 0xFFFFCB37, 0xFFFF66A3, 0xFF81CB51};
    private List<HobbyType> hobbyTypes = new ArrayList<HobbyType>();
//    private List<List<Lable>> lablesList = new ArrayList<List<Lable>>();
//    private List<Lable> myLables = new ArrayList<>();

    private TextView tvTitleLeft;
    private TextView tvTitleRight;
    private ImageView ivRight;
    private FrameLayout flRight;
    private LinearLayout llEditNickname;
    private LinearLayout llEditSex;
    private LinearLayout llEditbirthday;
    private LinearLayout llEditSignature;
    private LinearLayout llEditLoveStatus;
    private LinearLayout llEditJob;
    private LinearLayout llEditHometown;
    private LinearLayout llEditBodyHeight;
    private LinearLayout llEditBodyWeight;
    private LinearLayout llEditIncome;
    private LinearLayout llEditOwnHouse;
    private LinearLayout llEditOwnCar;
    private LinearLayout llEditUniversity;
    private LinearLayout llEditCompany;
    private LinearLayout llEditLookMore;
    private LinearLayout llEditSkillTitle;//技能称号
    private TextView tvSkillTitle;//技能称号
    private ImageView ivPhoneFlag;
    private ImageView ivLocalFlag;
    private ImageView ivAdd;
    private ImageView ivAddOne;
    private ImageView ivAddTwo;

//    private ViewPager vpHeadPic;
//    private EditUserInfoPicVPAdapter editUserInfoPicVPAdapter;

    private static final int REQ_CODE_SPORT = 99;
    private static final int REQ_CODE_TRAVLE = 100;
    private static final int REQ_CODE_ART = 101;
    private static final int REQ_CODE_FOOD = 102;
    private static final int REQ_CODE_FUN = 103;
    private static final int REQ_CODE_REST = 104;
    private static final int REQ_CODE_MYLABLE = 105;
    private EditUserInfoHobbysAdapter editUserInfoHobbysAdapter;
    private LinearLayout llEditMylable;
    private List<String> restIds = new ArrayList<>();
    private List<String> funIds = new ArrayList<>();
    private List<String> foodIds = new ArrayList<>();
    private List<String> artIds = new ArrayList<>();
    private List<String> travelIds = new ArrayList<>();
    private List<String> sportIds = new ArrayList<>();
    private List<String> mylableIds = new ArrayList<>();

    private ImageView ivPersonHeadview;  // 不需要加会员标识
    //    private HeadPhotoView ivPersonHeadview;
    private Dialog mProgressDialog;
    private String mNewIconUrl;
    private Bitmap mNewIconBitmap;
    private ArrayList<String> pics;
    private ArrayList<String> photoThum;
    private PictureFrameDialog pictureFrameDialog;
    private PictureFrameDialog pictureFrameDialogHead;
    private long delblogflag;
    private EditUserInfoEntity editUserInfoEntity;
    private boolean isEditChanged;
    private int loveStatusIndex;

    private String lurData;
    public String[] loveStatusArr;
    private int isPhoneFlagChanged;
    private int isLocalFlagChanged;

    private final int REQUEST_CODE_GALLERY_ICON = 1003;//选择上传头像的请求码
    private final int REQUEST_CODE_GALLERYS = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new EditUserInfoPresenter(EditUserInfoActivity.this, this);

        initViews();
        initDatas();
        initListeners();
        //YC 创建了pics实例，判空
        pics = new ArrayList<>();
        if (presenter != null && presenter.getEntity() != null && presenter.getEntity().getHeadPics() != null) {
            for (int i = 0; i < presenter.getEntity().getHeadPics().size(); i++) {
                pics.add(presenter.getEntity().getHeadPics().get(i));
            }
        }
//        pics = presenter.getEntity().getHeadPics();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }



    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                },
                getResString(R.string.edit_title), false, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentData = presenter.getEntity().toString();
                isEditChanged = !lurData.equals(currentData);


                //TODO 取消编辑个人信息返回上一个界面   // 有修改，提示是否保存
                if (isEditChanged) {
                    DialogUtil.showTwoButtonDialog(EditUserInfoActivity.this,
                            mContext.getString(R.string.dialog_title),
                            mContext.getString(R.string.edit_cancel_tip_title),
                            mContext.getString(R.string.give_up),
                            mContext.getString(R.string.continue_editing),
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // save( );
                                    finish();

                                }
                            }, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                            /*
                             * finish( ); mActivity.overridePendingTransition( 0
							 * , R.anim.modify_profile_bottom_out );
							 */
                                }
                            });
                } else {
                    Toast.makeText(EditUserInfoActivity.this, getResources().getString(R.string.no_changed), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
//        tvTitleLeft = findView(R.id.tv_left);
//        tvTitleLeft.setTextColor(0xFFFF4064);
//        tvTitleRight = findView(R.id.tv_right);
//        tvTitleRight.setTextColor(0xFFFF4064);
        ivRight = findView(R.id.iv_right);
        ivRight.setImageResource(R.drawable.icon_publish);
        setContent(R.layout.activity_edit_userinfo);
//        vpHeadPic = findView(R.id.vp_picture);
        llEditNickname = findView(R.id.ll_edit_nickname);
        llEditSex = findView(R.id.ll_edit_sex);
        tvNickname = findView(R.id.tv_nickname);
        tvSex = findView(R.id.tv_sex);
        llEditbirthday = findView(R.id.ll_edit_birthday);
        tvBirthday = findView(R.id.tv_birthday);
        llEditSignature = findView(R.id.ll_edit_signature);
        tvSignature = findView(R.id.tv_signature);
        llEditLoveStatus = findView(R.id.ll_edit_love_status);
        tvLoveStatus = findView(R.id.tv_love_status);
        llEditJob = findView(R.id.ll_edit_job);
        tvJob = findView(R.id.tv_job);
        llEditHometown = findView(R.id.ll_edit_hometown);
        tvHometown = findView(R.id.tv_hometown);
        llEditBodyHeight = findView(R.id.ll_edit_body_height);
        tvBodyHeight = findView(R.id.tv_body_height);
        llEditBodyWeight = findView(R.id.ll_edit_body_weight);
        tvBodyWeight = findView(R.id.tv_body_weight);

        llEditIncome = findView(R.id.ll_edit_income);
        tvIncome = findView(R.id.tv_income);
        llEditOwnHouse = findView(R.id.ll_edit_own_house);
        tvOwnHouse = findView(R.id.tv_own_house);
        llEditOwnCar = findView(R.id.ll_edit_own_car);
        tvOwnCar = findView(R.id.tv_own_car);
        llEditUniversity = findView(R.id.ll_edit_university);
        tvUniversity = findView(R.id.tv_university);
        llEditCompany = findView(R.id.ll_edit_company);
        tvCompany = findView(R.id.tv_company);

        llEditMylable = findView(R.id.ll_edit_mylable);
        tvLable = findView(R.id.tv_lable);
        lvHobbys = findView(R.id.lv_hobbys);

        ivPhoneFlag = findView(R.id.iv_show_phone_flag);
        ivLocalFlag = findView(R.id.iv_show_loacal_flag);
//        ivPersonHeadview = (HeadPhotoView) findViewById(R.id.iv_person_headview);
        ivPersonHeadview = (ImageView) findViewById(R.id.iv_person_headview);
        llEditLookMore = (LinearLayout) findViewById(R.id.ll_whatchMore);

        ivAdd = findView(R.id.iv_user_info_add_pic);
        ivAddOne = findView(R.id.iv_user_info_pic_one);
        ivAddTwo = findView(R.id.iv_user_info_pic_two);

        llEditSkillTitle = (LinearLayout) findViewById(R.id.ll_edit_skill_title);
        tvSkillTitle = (TextView) findViewById(R.id.tv_skill_title);
        //编辑称号功能没有处理，暂时隐掉入口
        llEditSkillTitle.setVisibility(View.GONE);

        pictureFrameDialog = new PictureFrameDialog(EditUserInfoActivity.this, 2);
        pictureFrameDialog.setItemOnclick(this);
        pictureFrameDialogHead = new PictureFrameDialog(EditUserInfoActivity.this, 1);
        pictureFrameDialogHead.setItemOnclick(this);

    }

    private void initDatas() {
        presenter.init();
        loveStatusArr = getResources().getStringArray(R.array.love_status_data);
        isPhoneFlagChanged = presenter.getEntity().getPhoneFlag();
        isLocalFlagChanged = presenter.getEntity().getLastLocalFlag();

    }

    private void initListeners() {
        llEditNickname.setOnClickListener(this);
        llEditbirthday.setOnClickListener(this);
        llEditSignature.setOnClickListener(this);
        llEditLoveStatus.setOnClickListener(this);
        llEditJob.setOnClickListener(this);
        llEditHometown.setOnClickListener(this);
        llEditBodyHeight.setOnClickListener(this);
        llEditBodyWeight.setOnClickListener(this);
        llEditIncome.setOnClickListener(this);
        llEditOwnHouse.setOnClickListener(this);
        llEditOwnCar.setOnClickListener(this);
        llEditUniversity.setOnClickListener(this);
        llEditCompany.setOnClickListener(this);
        llEditMylable.setOnClickListener(this);
        ivPhoneFlag.setOnClickListener(this);
        ivLocalFlag.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);
        ivPersonHeadview.setOnClickListener(this);
        llEditLookMore.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
    }

    @Override
    public void setVPHeadPics(ArrayList<String> picsThum, ArrayList<String> picList, final List<Photos.PhotosBean> photos) {
        presenter.setPhotoSize(photos.size());
        if (photos != null && photos.size() > 0) {
            if (photos.size() == 1) {
                llEditLookMore.setVisibility(View.GONE);
                GlideUtil.loadImage(BaseApplication.appContext, photos.get(photos.size() - 1).getImage(), ivAddOne);
                GlideUtil.loadImage(BaseApplication.appContext, "", ivAddTwo);
                ivAddOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pictureFrameDialog.show(photos.size() - 1, photos);
                    }
                });
                ivAddTwo.setOnClickListener(null);
            } else if (photos.size() >= 2) {
                llEditLookMore.setVisibility(View.VISIBLE);
                GlideUtil.loadImage(BaseApplication.appContext, photos.get(photos.size() - 1).getImage(), ivAddOne);
                GlideUtil.loadImage(BaseApplication.appContext, photos.get(photos.size() - 2).getImage(), ivAddTwo);
                ivAddOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pictureFrameDialog.show(photos.size() - 1, photos);
                    }
                });
                ivAddTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pictureFrameDialog.show(photos.size() - 2, photos);
                    }
                });
            } else {
                GlideUtil.loadImage(BaseApplication.appContext, "", ivAddOne);
                GlideUtil.loadImage(BaseApplication.appContext, "", ivAddTwo);
                ivAddOne.setOnClickListener(null);
                ivAddTwo.setOnClickListener(null);
            }
        }
    }

    @Override
    public void refreshHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, final List<Photos.PhotosBean> photos) {
        presenter.setPhotoSize(photos.size());
        photoThum = picsThum;
        if (photos.size() == 1) {
            llEditLookMore.setVisibility(View.GONE);
            GlideUtil.loadImage(BaseApplication.appContext, photos.get(0).getImage(), ivAddOne);
            GlideUtil.loadImage(BaseApplication.appContext, "", ivAddTwo);
            ivAddOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pictureFrameDialog.show(0, photos);
                }
            });
            ivAddTwo.setOnClickListener(null);
        } else if (photos.size() >= 2) {
            llEditLookMore.setVisibility(View.VISIBLE);
            GlideUtil.loadImage(BaseApplication.appContext, photos.get(0).getImage(), ivAddOne);
            GlideUtil.loadImage(BaseApplication.appContext, photos.get(1).getImage(), ivAddTwo);
            ivAddOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pictureFrameDialog.show(0, photos);
                }
            });
            ivAddTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pictureFrameDialog.show(1, photos);
                }
            });
        } else {
            GlideUtil.loadImage(BaseApplication.appContext, "", ivAddOne);
            GlideUtil.loadImage(BaseApplication.appContext, "", ivAddTwo);
            ivAddOne.setOnClickListener(null);
            ivAddTwo.setOnClickListener(null);
        }
    }

    @Override
    public void setUIInfo(EditUserInfoEntity data) {
        editUserInfoEntity = data;
        lurData = editUserInfoEntity.toString();
        setNickname(data.getNickname());
        setSex(data.getSex());
        setBirthday(data.getBirthday());
        setSignature(data.getSignature());
        setLoveStatus(data.getLoveStatus());
        setJob(data.getOccupation());
        presenter.getEntity().setOccupation(data.getOccupation());
        setHometown(data.getHometown());
        setBodyHeight(data.getHeight());
        setBodyWeight(data.getWeight());
        setIncome(data.getIncome());
        setOwnHouse(data.getOwnHouse());
        setOwnCar(data.getOwnCar());
        setUniversity(data.getUniversity());
        setCompany(data.getCompany());
        setHobbys(data.getHobbys());
        setLocalFlag(data.getLastLocalFlagB());
        setPhoneFlag(data.getPhoneFlagB());
    }

    @Override
    public void setNickname(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            tvNickname.setText(getString(R.string.edit_nickname_tips));
            tvNickname.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            SpannableString spNickname = FaceManager.getInstance(this)
                    .parseIconForString(this, nickname, 0, null);
            tvNickname.setText(spNickname);
            tvNickname.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public String getNickname() {
        return tvNickname.getText().toString();
    }

    @Override
    public void setSex(String sex) {
        tvSex.setText(sex);
    }

    @Override
    public void setBirthday(long birthday) {
        if (birthday == 0) {
            tvBirthday.setText(getString(R.string.edit_birthday_tips));
            tvBirthday.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            tvBirthday.setText(TimeFormat.convertTimeLong2String(birthday, Calendar.DATE));
            tvBirthday.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public void setBirthday(String birthday) {
        tvBirthday.setText(birthday);
    }

    @Override
    public long getBirthday() {
        String birthday = tvBirthday.getText().toString();
        return TimeFormat.convertTimeString2Long(birthday, Calendar.DATE);
    }

    @Override
    public void setSignature(String signature) {
        if (TextUtils.isEmpty(signature) || TextUtils.equals("null", signature)) {
            tvSignature.setText(getString(R.string.edit_signature_tips));
            tvSignature.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            SpannableString spSignature = FaceManager.getInstance(this).parseIconForString(this, signature, 0, null);
            tvSignature.setText(spSignature);
            tvSignature.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public String getSignature() {
        return tvSignature.getText().toString();
    }

    @Override
    public void setLoveStatus(String status) {
        if (presenter.getEntity().getLoveStatusIndex() == 0) {
            tvLoveStatus.setText(getString(R.string.edit_love_status_empty_tips));
            tvLoveStatus.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            tvLoveStatus.setTextColor(getResColor(R.color.edit_user_value));

            tvLoveStatus.setText(status);
        }
    }

    @Override
    public void setJob(String job) {
        int index = Integer.parseInt(job);
        if (index < 0) {
            tvJob.setText(getString(R.string.edit_job_tips));
            tvJob.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            //YC 添加防止数组越界
            if (index > 32)
            {
                tvJob.setText(getResStringArr(R.array.job)[0]);
                tvJob.setTextColor(getResColor(R.color.edit_user_value));
            }else {
                tvJob.setText(getResStringArr(R.array.job)[index]);//index - 1
                tvJob.setTextColor(getResColor(R.color.edit_user_value));
            }
        }
    }

    @Override
    public String getJob() {
        return tvJob.getText().toString();
    }

    @Override
    public void setHometown(String hometown) {
        if (TextUtils.isEmpty(hometown) || TextUtils.equals("null", hometown)) {
            tvHometown.setText(getString(R.string.edit_hometown_tips));
            tvHometown.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
//            HometownEntity hometownEntity = GsonUtil.getInstance().getServerBean(hometown, HometownEntity.class);
            String home = hometown.replace(":", ",");

            String[] hometownEntity = home.split(",");
            if (hometownEntity.length > 5) {
                tvHometown.setText(hometownEntity[5] + " " + hometownEntity[3]);
                tvHometown.setTextColor(getResColor(R.color.edit_user_value));
            } else if (hometownEntity.length >= 2) {
                tvHometown.setText(hometownEntity[1]);
                tvHometown.setTextColor(getResColor(R.color.edit_user_value));
            } else {

            }

        }
    }

    @Override
    public String getHometown() {
        return tvHometown.getText().toString();
    }

    @Override
    public void setBodyHeight(int height) {
        if (0 == height) {//height == 0
            tvBodyHeight.setText(getString(R.string.edit_body_height_empty_tips));
            tvBodyHeight.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
//            final String[] heightArr = getResources().getStringArray(R.array.height_data);
//           tvBodyHeight.setText(heightArr[Integer.parseInt(height)] + "");
            tvBodyHeight.setText(height + "cm");
            tvBodyHeight.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public int getBodyHeight() {
        return 0;
    }

    @Override
    public void setBodyWeight(int weight) {
        if (0 == weight) {//weight == 0
            tvBodyWeight.setText(getString(R.string.edit_body_weight_empty_tips));
            tvBodyWeight.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
//            final String[] weightArr = getResources().getStringArray(R.array.weight_data);
//
            tvBodyWeight.setText(weight + "kg");
            tvBodyWeight.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public int getBodyWeight() {
        return 0;
    }

    @Override
    public void setIncome(String income) {
        if (presenter.getEntity().getIncomeIndex() == -1 || presenter.getEntity().getIncomeIndex() <= 0) {
            tvIncome.setText(getString(R.string.edit_income_empty_tips));
            tvIncome.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            tvIncome.setText(income);
            tvIncome.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public String getIncome() {
        return tvIncome.getText().toString();
    }

    @Override
    public void setOwnHouse(String status) {
        if (presenter.getEntity().getOwnHouseIndex() <= 0) {
            tvOwnHouse.setText(getString(R.string.edit_own_house_empty_tips));
            tvOwnHouse.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            tvOwnHouse.setText(status);
            tvOwnHouse.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public void setOwnCar(String status) {
        if (presenter.getEntity().getOwnCarIndex() <= 0) {//<= 0
            tvOwnCar.setText(getString(R.string.edit_own_car_empty_tips));
            tvOwnCar.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            tvOwnCar.setText(status);
            tvOwnCar.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public void setUniversity(String university) {

        if (TextUtils.isEmpty(university) || university.contains("null")) {
            tvUniversity.setText(getString(R.string.edit_university_empty_tips));
            tvUniversity.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            String[] school = university.split(":");//,
            if (school.length > 0) {
                tvUniversity.setText(school[0]);
                tvUniversity.setTextColor(getResColor(R.color.edit_user_value));
            } else {
                tvUniversity.setText(getString(R.string.edit_university_empty_tips));
                tvUniversity.setTextColor(getResColor(R.color.edit_user_tips));
            }


        }
    }

    @Override
    public String getUniversity() {
        return tvUniversity.getText().toString();
    }

    @Override
    public void setCompany(String company) {
        if (TextUtils.isEmpty(company) || company.contains("null")) {
            tvCompany.setText(getString(R.string.edit_company_empty_tips));
            tvCompany.setTextColor(getResColor(R.color.edit_user_tips));
        } else {
            tvCompany.setText(company);
            tvCompany.setTextColor(getResColor(R.color.edit_user_value));
        }
    }

    @Override
    public String getCompany() {
        return tvCompany.getText().toString();
    }

    @Override
    public void setHobbys(List<String> hobbyIds) {
        /*List<String> myLables = new ArrayList<String>();{
            myLables.add("文艺");
            myLables.add("随性");
            myLables.add("选择恐惧症");
            myLables.add("选择恐惧症");
            myLables.add("生活不知眼前的苟且，还有诗和远方的田野");
        }*/
//        setMylable(hobbyIds);
//        lablesList.add(getClassLable(hobbyIds, Constants.LABLE_SPORT_START_INDEX, Constants.LABLE_SPORT_END_INDEX));
//        lablesList.add(getClassLable(hobbyIds, Constants.LABLE_TRAVEL_START_INDEX, Constants.LABLE_TRAVEL_END_INDEX));
//        lablesList.add(getClassLable(hobbyIds, Constants.LABLE_ART_START_INDEX, Constants.LABLE_ART_END_INDEX));
//        lablesList.add(getClassLable(hobbyIds, Constants.LABLE_FOOD_START_INDEX, Constants.LABLE_FOOD_END_INDEX));
//        lablesList.add(getClassLable(hobbyIds, Constants.LABLE_FUN_START_INDEX, Constants.LABLE_FUN_END_INDEX));
//        lablesList.add(getClassLable(hobbyIds, Constants.LABLE_REST_START_INDEX, Constants.LABLE_REST_END_INDEX));//yuchao

//        sortHobbyids(lablesList);

        hobbyTypeDesc = getResStringArr(R.array.edit_interest);
        for (int i = 0; i < hobbyTypeIcons.length; i++) {
            HobbyType hobbyType = new HobbyType();
            hobbyType.setIconId(hobbyTypeIcons[i]);
            hobbyType.setType(hobbyTypeDesc[i]);
            hobbyType.setTypeColor(hobbyTypeColor[i]);
//            hobbyType.setDetails(lablesList.get(i));
            hobbyTypes.add(hobbyType);
        }

        editUserInfoHobbysAdapter = new EditUserInfoHobbysAdapter(hobbyTypes);
        lvHobbys.setAdapter(editUserInfoHobbysAdapter);
        CommonFunction.setListViewHeightBasedOnChildren(lvHobbys);
        lvHobbys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case Constants.LABLE_SPORT_POSITION:
                        starLablePage(Constants.LABLE_SPORT_POSITION, REQ_CODE_SPORT);
                        break;
                    case Constants.LABLE_TRAVEL_POSITION:
                        starLablePage(Constants.LABLE_TRAVEL_POSITION, REQ_CODE_TRAVLE);
                        break;
                    case Constants.LABLE_ART_POSITION:
                        starLablePage(Constants.LABLE_ART_POSITION, REQ_CODE_ART);
                        break;
                    case Constants.LABLE_FOOD_POSITION:
                        starLablePage(Constants.LABLE_FOOD_POSITION, REQ_CODE_FOOD);
                        break;
                    case Constants.LABLE_FUN_POSITION:
                        starLablePage(Constants.LABLE_FUN_POSITION, REQ_CODE_FUN);
                        break;
                    case Constants.LABLE_REST_POSITION:
                        starLablePage(Constants.LABLE_REST_POSITION, REQ_CODE_REST);
                        break;
                }
            }
        });
    }

//    private void sortHobbyids(List<List<Lable>> lablesList) {
//        for (int i = 0; i < lablesList.get(0).size(); i++) {
//            sportIds.add(lablesList.get(0).get(i).getLableId());
//        }
//        for (int i = 0; i < lablesList.get(1).size(); i++) {
//            travelIds.add(lablesList.get(1).get(i).getLableId());
//        }
//        for (int i = 0; i < lablesList.get(2).size(); i++) {
//            artIds.add(lablesList.get(2).get(i).getLableId());
//        }
//        for (int i = 0; i < lablesList.get(3).size(); i++) {
//            foodIds.add(lablesList.get(3).get(i).getLableId());
//        }
//        for (int i = 0; i < lablesList.get(4).size(); i++) {
//            funIds.add(lablesList.get(4).get(i).getLableId());
//        }
//        for (int i = 0; i < lablesList.get(5).size(); i++) {
//            restIds.add(lablesList.get(5).get(i).getLableId());
//        }
//    }//yuchao

//    private void setMylable(List<String> hobbyIds) {
//        myLables = getClassLable(hobbyIds, Constants.LABLE_LABLE_START_INDEX, Constants.LABLE_LABLE_END_INDEX);
//        StringBuilder lables = new StringBuilder();
//        if (myLables.size() > 0) {
//            mylableIds.clear();
//            lables.append(myLables.get(0).getLableValue());
//            mylableIds.add(myLables.get(0).getLableId());
//            for (int i = 1; i < myLables.size(); i++) {
//                lables.append("，" + myLables.get(i).getLableValue());
//                mylableIds.add(myLables.get(i).getLableId());
//            }
//        }
//        tvLable.setText(lables.toString());
//    }//设置我的标签

    private void starLablePage(int position, int requestCode) {
        Intent intent = new Intent(this, SelectLableActivity.class);
        ArrayList<String> lableIds = new ArrayList<>();
//        List<Lable> lables = lablesList.get(position);
//        for (int i = 0; i < lables.size(); i++) {
//            lableIds.add(lables.get(i).getLableId());
//        }//yuchao
        /*for(Lable lable :lables){
            lableIds.add(lable.getLableId());
        }*/
        intent.putStringArrayListExtra(Constants.LABLE_SELECTED_IDS, lableIds);
        intent.putExtra(Constants.LABLE_SELECTED_POSITION, position);
        startActivityForResult(intent, requestCode);
    }

//    public List<Lable> getClassLable(List<String> ids, int stardIndex, int endIndex) {
//        List<Lable> lableList = DataSupport.where(CommonFunction.getInAndRoundSelectCode(Constants.LABLE_IDS, ids, stardIndex, endIndex)).find(Lable.class);
//        if (lableList == null) {
//            return new ArrayList<Lable>();
//        }
//        return lableList;
//    }

    @Override
    public void setPhoneFlag(boolean status) {
        if (status) {
            ivPhoneFlag.setImageResource(R.drawable.open);
        } else {
            ivPhoneFlag.setImageResource(R.drawable.close);
        }
    }

    @Override
    public void setLocalFlag(boolean status) {
        if (status) {
            ivLocalFlag.setImageResource(R.drawable.open);
        } else {
            ivLocalFlag.setImageResource(R.drawable.close);
        }
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void userinfoUpdateSuc() {
        showToast(getString(R.string.edit_user_success_tips));
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void setHeadviewPhoto(ArrayList<String> picList) {
        if (picList != null) {
            pics = picList;
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                if (pics != null && pics.size() > 0){
                    pics.remove(0);
                    pics.add(0,verifyicon);
                }else{
                    pics.add(verifyicon);
                }
            }
            GlideUtil.loadCircleImage(EditUserInfoActivity.this, pics.get(0), ivPersonHeadview, NetImageView.DEFAULT_FACE, NetImageView.DEFAULT_FACE);
//            ivPersonHeadview.execute(ChatFromType.UNKONW,Common.getInstance().loginUser,null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit_nickname://昵称
                EditNicknameActivity.actionStartForResult(this, getNickname(), EDIT_NICKNAME);
                break;
            case R.id.ll_edit_birthday://生日
                BirthdayPickerActivity.launchForResult(this, TimeFormat.convertTimeLong2String(getBirthday(), Calendar.SECOND), EDIT_BIRTHDAY);
                break;
            case R.id.ll_edit_signature://个性签名
                EditSignatureActivity.actionStartForResult(this, getSignature(), EDIT_SIGNATURE);
                break;
            case R.id.ll_edit_love_status://恋爱状态
                loveStatusIndex = presenter.getEntity().getLoveStatusIndex();
                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_love_status_title), getResStringArr(R.array.love_status_data), loveStatusIndex > 0 ? loveStatusIndex - 1 : -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which <= 2) {
                            presenter.getEntity().setLoveStatus(which + 1);
                            setLoveStatus(presenter.getEntity().getLoveStatus());

                        } else if (which == 3) {
                            presenter.getEntity().setLoveStatus(which + 7);
                            setLoveStatus(loveStatusArr[3]);
                        } else if (which == 4) {
                            presenter.getEntity().setLoveStatus(which + 7);
                            setLoveStatus(loveStatusArr[4]);
                        }

//                        setLoveStatus(presenter.getEntity().getLoveStatus());
                    }
                });
                break;
            case R.id.ll_edit_job://职业
                EditJobActivity.actionStartForResult(this, getJob(), EDIT_JOB);
                break;
            case R.id.ll_edit_hometown://家乡
//                EditActivity.actionStartForResult(this, getString(R.string.edit_nickname_title), getHometown(), EDIT_HOMETOWN);
                EditProvinceActivity.actionStartForResult(this, EDIT_HOMETOWN);
                break;

            case R.id.ll_edit_body_height:
                int heightIndex = presenter.getEntity().getHeight() - 100;
                String[] heightArr = new String[131];//120
                for (int i = 0; i < heightArr.length; i++) {
                    heightArr[i] = i + 100 + "cm";
                }

                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_heigt_title), heightArr, heightIndex >= 0 ? heightIndex : -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setBodyHeight(which + 100);
                        presenter.getEntity().setHeight(which + 100);
                    }
                });

//                final String[] heightArr = getResources().getStringArray(R.array.height_data);
//                int heightIndex = presenter.getEntity().getHeight();
//                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_heigt_title), heightArr, heightIndex > 0 ? heightIndex - 1 : -1, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        presenter.getEntity().setHeight(which + 1);
//                        setBodyHeight(heightArr[presenter.getEntity().getHeight() - 1]);
//                    }
//                });
                break;

            case R.id.ll_edit_body_weight:
                int weightIndex = presenter.getEntity().getWeight() - 35;
                String[] weightArr = new String[166];
                for (int i = 0; i < weightArr.length; i++) {
                    weightArr[i] = i + 35 + "kg";//
                }
                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_weight_title), weightArr, weightIndex >= 0 ? weightIndex : -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {//0
                        setBodyWeight(which + 35);//+ 35
                        presenter.getEntity().setWeight(which + 35);//+ 35
                    }
                });

//                final String[] weightArr = getResources().getStringArray(R.array.weight_data);
//                int weightIndex = presenter.getEntity().getHeight();
//                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_weight_title), weightArr, weightIndex > 0 ? weightIndex - 1 : -1, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        presenter.getEntity().setWeight(which + 1);
//                        setBodyWeight(weightArr[presenter.getEntity().getWeight() - 1]);
//                    }
//                });
                break;
            case R.id.ll_edit_income://我的收入
                final String[] incomeArr = getResources().getStringArray(R.array.income_data);
                int incomeIndex = presenter.getEntity().getIncomeIndex();
                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_income_title), incomeArr, incomeIndex > 0 ? incomeIndex - 1 : -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.getEntity().setIncome(which + 1);
                        setIncome(presenter.getEntity().getIncome());
                    }
                });
                break;
            case R.id.ll_edit_own_house://住房情况
                int ownHouseIndex = presenter.getEntity().getOwnHouseIndex();
                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_own_house_title), getResStringArr(R.array.own_house_data), ownHouseIndex > 0 ? ownHouseIndex - 1 : -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.getEntity().setOwnHouse(which + 1);
                        setOwnHouse(presenter.getEntity().getOwnHouse());
                    }
                });
                break;
            case R.id.ll_edit_own_car://购车情况
                int ownCarIndex = presenter.getEntity().getOwnCarIndex();
                DialogUtil.showSingleChoiceDialog(this, getString(R.string.edit_own_car_title), getResStringArr(R.array.own_car_data), ownCarIndex > 0 ? ownCarIndex - 1 : -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.getEntity().setOwnCar(which + 1);
                        setOwnCar(presenter.getEntity().getOwnCar());
                    }
                });
                break;
            case R.id.ll_edit_university://毕业院校
                EditUniversityActivity.actionStartForResult(this, getUniversity(), EDIT_UNIVERSITY);
                break;
            case R.id.ll_edit_company://就业公司
                EditCompanyActivity.actionStartForResult(this, getCompany(), EDIT_COMPANY);
                break;
            case R.id.ll_edit_mylable:
                Intent intent = new Intent(this, SelectLableActivity.class);
                ArrayList<String> lableIds = new ArrayList<>();
//                for (Lable lable : myLables) {
//                    lableIds.add(lable.getLableId());
//                }//yuchao
                intent.putStringArrayListExtra(Constants.LABLE_SELECTED_IDS, lableIds);
                intent.putExtra(Constants.LABLE_SELECTED_POSITION, Constants.LABLE_MYLABLE_POSITION);
                startActivityForResult(intent, REQ_CODE_MYLABLE);
                break;
            case R.id.fl_right:
            case R.id.iv_right:
                ArrayList<String> lablesListIds = new ArrayList<>();
                lablesListIds.addAll(mylableIds);
                lablesListIds.addAll(sportIds);
                lablesListIds.addAll(travelIds);
                lablesListIds.addAll(artIds);
                lablesListIds.addAll(foodIds);
                lablesListIds.addAll(funIds);
                lablesListIds.addAll(restIds);
                if (presenter.getEntity().getPhoneFlag() == isPhoneFlagChanged) {
                    if (isPhoneFlagChanged == 1) {
                        presenter.getEntity().setShowDevice(true);
                    } else {
                        presenter.getEntity().setShowDevice(false);
                    }
                }
                if (presenter.getEntity().getLastLocalFlag() == isLocalFlagChanged) {
                    if (isLocalFlagChanged == 1) {
                        presenter.getEntity().setShowLocation(true);
                    } else {
                        presenter.getEntity().setShowLocation(false);
                    }
                }
                presenter.uploadInfo(this, lablesListIds);
                break;
            case R.id.iv_show_phone_flag:
                presenter.changePhoneFlag();
//                if (后台有数据)
//                {
//                    presenter.changePhoneFlag();
//                }else
//                {
//
//                }
                break;
            case R.id.iv_show_loacal_flag:
                presenter.changeLocalFlag();
                break;
            case R.id.iv_person_headview:
                // 进入修改用户头像的流程
//                uploadAvatar();
                pictureFrameDialogHead.show(0, pics);
                break;
            case R.id.ll_whatchMore:
                //查看更过相册图片
                Intent i = new Intent(EditUserInfoActivity.this, SpaceTopic.class);
                i.putExtra("uid", Common.getInstance().loginUser.getUid());
                i.putExtra("nickName", Common.getInstance().loginUser.getNickname());
                i.putExtra("notename", Common.getInstance().loginUser.getNoteName(false));
                i.putExtra("type", 1);
                // startActivity( i );
                startActivityForResult(i, EDIT_SPACE_MODIFY_PHOTO);
                break;
            case R.id.iv_user_info_add_pic:
                if (presenter.getPhotoSize() >= 11) {
                    CommonFunction.toastMsg(EditUserInfoActivity.this,
                            getString(R.string.total_upload_photo_full));
                    return;
                }
                int moreSize;
                if (11 - presenter.getPhotoSize() <= 8) {
                    moreSize = 11 - presenter.getPhotoSize();
                } else {
                    moreSize = 8;
                }
//                PictureMultiSelectActivity.skipToPictureMultiSelectAlbum(this, EditUserInfoActivity.UPLOAD_HEAD, moreSize);

                GalleryUtils.getInstance().openGalleryMuti(this,REQUEST_CODE_GALLERYS,moreSize,mOnHanlderResultCallback);
                break;
            case R.id.ll_edit_sex:
                DialogUtil.showUserSexDialog(EditUserInfoActivity.this, new EditUserSexDialog.SureClickListener() {
                    @Override
                    public void onSure(String gender) {
                        //gh 确认修改性别
                        presenter.setGender(gender);
                        tvSex.setText(presenter.getEntity().getSex());

                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case EDIT_NICKNAME:
                    String nickname = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (!TextUtils.isEmpty(nickname)) {
                        setNickname(nickname);
                        presenter.getEntity().setNickname(nickname);
                    }

                    break;
                case EDIT_BIRTHDAY:
                    String birthday = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    int userAge = data.getIntExtra(Constants.EDIT_BIRTHDAT_AGE,0);
//                    if (age == null)
//                        age ="0";
//                    int userAge = Integer.parseInt(age);
                    if (!TextUtils.isEmpty(birthday)) {
                        setBirthday(birthday);
//                        presenter.getEntity().setBirthday(TimeFormat.convertTimeString2Long(birthday, Calendar.DATE));
                        presenter.getEntity().setBirthday(birthday + "");
                    }
                    if (userAge != 0) {
                        presenter.getEntity().setUserAge(userAge);
                    }

                    int horoscope = data.getIntExtra("horoscope", 0);
                    presenter.getEntity().setHoroscope(horoscope);
                    break;
                case EDIT_SIGNATURE:
                    String signature = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (!TextUtils.isEmpty(signature)) {
                        setSignature(signature);
                        presenter.getEntity().setSignature(signature);
                    }

                    break;
                case EDIT_JOB:
                    String job = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (!TextUtils.isEmpty(job)) {
                        setJob(job);
                        presenter.getEntity().setJob(job);

                    }

                    break;
                case EDIT_HOMETOWN:
                    String hometown = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (!TextUtils.isEmpty(hometown)) {
                        setHometown(hometown);
                        presenter.getEntity().setHometown(hometown);
                    }

                    break;
                case EDIT_UNIVERSITY:
                    String university = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (!TextUtils.isEmpty(university)) {
                        setUniversity(university);

                        String[] school = university.split(",");
                        presenter.getEntity().setUniversity(school[0]);
                        presenter.getEntity().setSchoolid(Integer.parseInt(school[1]));
                    }

                    break;
                case EDIT_COMPANY:
                    String company = data.getStringExtra(Constants.EDIT_RETURN_INFO);
                    if (!TextUtils.isEmpty(company)) {
                        setCompany(company);
                        presenter.getEntity().setCompany(company);
                    }

                    break;
//                case UPLOAD_HEAD:
//                    ArrayList<String> picPathList = data.getStringArrayListExtra(PictureMultiSelectActivity.PATH_LIST);
//                    presenter.updatePic(this, picPathList);
//                    break;
//                case EDIT_SPACE_UPLOAD_ICON:
//                    hideProgressDialog();
//                    mProgressDialog = DialogUtil.showProgressDialog(EditUserInfoActivity.this,
//                            R.string.edit_face, R.string.please_wait, null);
//                    ArrayList<String> list = data
//                            .getStringArrayListExtra(PictureMultiSelectActivity.PATH_LIST);
//                    try {
//                        uploadPic(list.get(0));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    break;
                case EDIT_SPACE_MODIFY_PHOTO:
                    ArrayList<Photos.PhotosBean> pics = Common.getInstance().getAlbumPic();
                    Collections.reverse(pics);
                    refreshHeadPics(presenter.getEntity().getHeadPicsthum(), presenter.getEntity().getHeadPics(), pics);
                    break;
            }
        }
    }

    /**
     * 隐藏进度条
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 上传图片
     *
     * @param url
     * @throws FileNotFoundException
     */
    private void uploadPic(final String url) throws FileNotFoundException {
        final Bitmap bitmap = CommonFunction.createBitmap(url);
        if (bitmap == null || bitmap.isRecycled()) {
            hideProgressDialog();
            return;
        }

        Toast.makeText(mContext, R.string.icon_uploading, Toast.LENGTH_SHORT).show();
        new Thread() {
            public void run() {
                try {
                    File file = new File(url);
                    HttpRequest httpRequest = HttpRequest.post(Config.sPictureHost)
                            .part("file", "file.jpg", file)
                            .part("key", ConnectorManage.getInstance(mContext).getKey())
                            .part("type", String.valueOf(FileUploadType.PIC_FACE));
                    int status = httpRequest.code();
                    if (status == 200) {
                        // 上传成功
                        String result = httpRequest.body();
                        JSONObject json = new JSONObject(result);
                        mNewIconUrl = CommonFunction.jsonOptString(json, "url");
                        Common.getInstance().loginUser.setIcon(mNewIconUrl);
                        mNewIconBitmap = bitmap;
//                        mMyselfInfoBack.setIconBitmap(bitmap);
                        SpaceModel.getInstance(mContext).uploadHeadIconReq(EditUserInfoActivity.this,
                                mNewIconUrl, EditUserInfoActivity.this);
                    } else {
                        // 上传失败
//                        handler.sendMessage(handler.obtainMessage(MSG_UPLOAD_ICON_STATE, 0,
//                                0));
                    }
                } catch (Throwable t) {
                    CommonFunction.log(t);
                    // 上传失败
//                    handler.sendMessage(handler.obtainMessage(MSG_UPLOAD_ICON_STATE, 0, 0));
                }
            }
        }.start();
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        HashMap<String, Object> res = null;
        try {
            res = SpaceModel.getInstance(EditUserInfoActivity.this).getRes(result, flag);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (res != null) {
            SpaceModelReqTypes reqType = (SpaceModelReqTypes) res.get("reqType");
            if (reqType != null) {
                switch (reqType) {
                    case UPLOAD_ICON: {
                        try {
                            int status = (Integer) res.get("status");
                            if (status == 200) {
                                handler.sendMessage(handler.obtainMessage(
                                        MSG_UPLOAD_ICON_STATE, 1, 0, null));
                                SendBackManage
                                        .saveMaxNum(EditUserInfoActivity.this, new JSONObject(result));
                            } else {
                                handler.sendMessage(handler.obtainMessage(
                                        MSG_UPLOAD_ICON_STATE, 0, 0));
                            }
                        } catch (JSONException e) {
                            handler.sendMessage(handler.obtainMessage(MSG_UPLOAD_ICON_STATE,
                                    0, 0));
                        }
                    }
                    break;
                }
            }
        }
        if (delblogflag == flag) {
            CommonFunction.log("shifengxiong", "result=" + result);
            try {
                JSONObject json = new JSONObject(result);
                int status = json.optInt("status");
                if (status == 200) {
                    pictureFrameDialog.getPhotoBean().remove(pictureFrameDialog.getPostion());
                    List<Photos.PhotosBean> photosBeanList = pictureFrameDialog.getPhotoBean();
                    presenter.refreshHeadPics(null, null, photosBeanList);
                } else {
                    CommonFunction.showToast(EditUserInfoActivity.this, getResString(R.string.operate_fail), Toast.LENGTH_SHORT);
//                    ErrorCode.showError(EditUserInfoActivity.this,result);
                }
            } catch (Exception e) {

            }


//            try {
//                JSONObject json = new JSONObject(result);
//                int status = json.optInt("status");
//                int lastnum = json.optInt("lastnum");
//                if (status == 200) {
//                    amount--;
//                    mUser.setPhotouploadleft(lastnum);
//                    Common.getInstance().loginUser.setPhotouploadleft(lastnum);
//                }
//                ((TextView) mActionBar.findViewById(R.id.tv_title)).setText(getContext()
//                        .getString(mType == 0 ? R.string.like : R.string.all_photo)
//                        + "("
//                        + amount
//                        + ")");
//            } catch (Exception e) {
//                // TODO: handle exception
//            }

            return;
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            switch (msg.what) {

                case MSG_UPLOAD_ICON_STATE: {
                    if (msg.arg1 == 0) {
                        // 失败
                        mNewIconUrl = "";
                        mNewIconBitmap = null;
                        Toast.makeText(mContext, R.string.upload_icon_failded,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // 成功，刷新ICON
                        Toast.makeText(mContext, R.string.upload_icon_complete,
                                Toast.LENGTH_SHORT).show();
                        Common.getInstance().loginUser.setIcon(mNewIconUrl);
                        Common.getInstance().loginUser.setIconBitmap(mNewIconBitmap);
                        if (pics.size() > 0) {
                            pics.remove(0);
                            pics.add(0, mNewIconUrl);
                        }
                        // 刷新个人资料页的icon
                        if (SpaceMe.sSpaceMe != null) {
//                            SpaceMe.sSpaceMe.refreshBasicInfo( mMyselfInfoBack );
                        }
                        // 刷新设置里的ICON
//                        if ( ApplySettingActivity.sInstance != null )
//                        {
//                            ApplySettingActivity.sInstance.refreshUserIcon( );
//                        }
//                         刷新主菜单的icon
//                        if ( MainActivity.sInstance != null )
//                        {
//                            MainActivity.sInstance.freshLoginUserIcon( );
//                        }
                        // 刷新修改资料页的icon
//                        mMyselfInfoBack.setIcon(mNewIconUrl);
//                        mMyselfInfoBack.setIconBitmap(mNewIconBitmap);
                        if(false==CommonFunction.activityIsDestroyed(EditUserInfoActivity.this)) {
                            updateIconUI();
                        }
                        Common.getInstance().loginUser.setVerifyicon(mNewIconUrl);//修改本地审核头像地址
                        EventBus.getDefault().post(new HeadImageNotifyEvent());
                        if (pics != null && pics.size() > 0){
                            pics.remove(0);
                            pics.add(0,mNewIconUrl);
                        }else{
                            pics.add(mNewIconUrl);
                        }
                    }
                }
                break;

            }
        }
    };

    public void updateIconUI() {
        // 获取uri
        String iconUri = !CommonFunction.isEmptyOrNullStr(mNewIconUrl) ? CommonFunction
                .thumPicture(mNewIconUrl) : "drawable://"
                + NetImageView.DEFAULT_FACE;
        // 加载头像
        GlideUtil.loadCircleImage(BaseApplication.appContext, iconUri, ivPersonHeadview,
                NetImageView.DEFAULT_FACE, NetImageView.DEFAULT_FACE);
    }

    private void delPhoto(String photoId) {
        delblogflag = PhotoHttpProtocol.photoDel(EditUserInfoActivity.this, photoId, this);
    }

    @Override
    public void itemOnclick(View view) {
        if (view.getTag().equals("delete")) {

            delPhoto((String) pictureFrameDialog.getPhotoBean().get(pictureFrameDialog.getPostion()).getPhotoid());
        } else if (view.getTag().equals("reviewBig")) {
            ArrayList<String> photos = new ArrayList<>();
            for (Photos.PhotosBean url : pictureFrameDialog.getPhotoBean()) {
                photos.add(url.getImage());
            }
            PictureDetailsActivity.launch(EditUserInfoActivity.this, photos, pictureFrameDialog.getPostion());
        } else if (view.getTag().equals("modifyHead")) {
            requestCamera();
        } else if (view.getTag().equals("headReviewBig")) {
            PictureDetailsActivity.launch(EditUserInfoActivity.this, pics, pictureFrameDialogHead.getPostion());
        }
    }

    @Override
    public void doCamera() {
        super.doCamera();
//        PictureMultiSelectActivity.skipToPictureMultiSelectAlbumCrop(
//                mContext, EDIT_SPACE_UPLOAD_ICON);//EDIT_SPACE_UPLOAD_ICON

        GalleryUtils.getInstance().openGallerySingleCrop(this, REQUEST_CODE_GALLERY_ICON,mOnHanlderResultCallback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String currentData = presenter.getEntity().toString();
            //YC 添加lurData空判断
            if (lurData != null || !"".equals(lurData))
            {
                isEditChanged = !lurData.equals(currentData == null ? "" : currentData);
            }


            //TODO 取消编辑个人信息返回上一个界面   // 有修改，提示是否保存
            if (isEditChanged) {
                DialogUtil.showTwoButtonDialog(EditUserInfoActivity.this,
                        mContext.getString(R.string.dialog_title),
                        mContext.getString(R.string.edit_cancel_tip_title),
                        mContext.getString(R.string.give_up),
                        mContext.getString(R.string.continue_editing),
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // save( );
                                finish();

                            }
                        }, new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                            /*
                             * finish( ); mActivity.overridePendingTransition( 0
							 * , R.anim.modify_profile_bottom_out );
							 */
                            }
                        });
            } else {
                Toast.makeText(EditUserInfoActivity.this, getResources().getString(R.string.no_changed), Toast.LENGTH_LONG).show();
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {

            if (resultList != null) {
                if (reqeustCode == REQUEST_CODE_GALLERY_ICON){
                    final ArrayList<String> list = new ArrayList<>();

                    for (PhotoInfo photoInfo : resultList) {
                        list.add(photoInfo.getPhotoPath());
                    }

                    hideProgressDialog();
                    mProgressDialog = DialogUtil.showProgressDialog(EditUserInfoActivity.this,
                            R.string.edit_face, R.string.please_wait, null);
                    try {
                        uploadPic(list.get(0));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else if (reqeustCode == REQUEST_CODE_GALLERYS){
                    final ArrayList<String> list = new ArrayList<>();

                    for (PhotoInfo photoInfo : resultList) {
                        list.add(photoInfo.getPhotoPath());
                    }
                    presenter.updatePic(EditUserInfoActivity.this, list);
                }


            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(EditUserInfoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
   public void  onEventMainThread(NickNameNotifyEvent event){
        if(!TextUtils.isEmpty(event.getSkillTitle())){
            tvSkillTitle.setText(event.getSkillTitle() + "");
        }

    }

    @Override
    public void refreshSex(boolean isHadsetname) {
        if (isHadsetname){
            llEditSex.setOnClickListener(null);
            findViewById(R.id.iv_edit_userinfo_base_sex_detail).setVisibility(View.INVISIBLE);
        }else{
            llEditSex.setOnClickListener(this);
            findViewById(R.id.iv_edit_userinfo_base_sex_detail).setVisibility(View.VISIBLE);
        }
    }

}
