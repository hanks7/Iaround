package net.iaround.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.entity.type.ChatFromType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.datamodel.User;
import net.nostra13.universalimageloader.core.assist.ImageLoadingListener;


/**
 * modify by liush on 2016/12/22.
 */

public class HeadPhotoView extends RelativeLayout {

    private ImageView mImageView;
    private ImageView mBackground;
    private ImageView mUserLevelImageView; //会员等级头像背景，分普通用户等级和会员用户等级
    private TextView mUserLevelTextView; //会员等级具体数值
    private User photoUser;
    private int defaultImage = R.drawable.default_avatar_round;
    private int chatFromtype = ChatFromType.UNKONW;

    public HeadPhotoView(Context context) {
        super(context);
        init(context);
    }

    public HeadPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeadPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    private void init(Context context) {
        //头像
        mImageView = new ImageView(context);
        mImageView.setId(R.id.head_photo);

        //用户等级
        mUserLevelImageView = new ImageView(context);
        mUserLevelTextView = new TextView(context);
        mUserLevelTextView.setTextColor(getResources().getColor(R.color.white));
        mUserLevelTextView.setTextSize(8);

        // 实际图片会员
        mBackground = new ImageView(context);
        mBackground.setScaleType(ScaleType.FIT_XY);

        addView(mImageView);
        addView(mUserLevelImageView);
        addView(mUserLevelTextView);
        addView(mBackground);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mUserLevelImageView.setLayoutParams(params);

        LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mUserLevelTextView.setLayoutParams(params2);
    }

    public void setScaleType(ScaleType type) {
        if (mImageView != null) {
            mImageView.setScaleType(type);
        }
    }

    public void execute(User user) {
        photoUser = user;
        String url = user.getIcon();
        if (user.getSVip() > 0) {
            setVipLevel(true, false, false, null);
        } else if (user.getViplevel() > 0) {
            setVipLevel(false, true, false, null);
        } else {
            setVipLevel(false, false, false, null);
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage,defaultImage);
        mImageView.setOnClickListener(headPhotoClick);
    }

    public void execute(User user,Context mContext) {
        photoUser = user;
        String url = user.getIcon();
        if (user.getSVip() > 0) {
            setVipLevel(true, false, false, null);
        } else if (user.getViplevel() > 0) {
            setVipLevel(false, true, false, null);
        } else {
            setVipLevel(false, false, false, null);
        }
        if(mContext == null){
            return;
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage,defaultImage);
        mImageView.setOnClickListener(headPhotoClick);
    }

    /**
     * 聊吧
     * @param user
     * @param isFrom 1不支持缓存 0 缓存
     */
    public void execute(User user,int isFrom) {
        photoUser = user;
        String url = user.getIcon();
        if (user.getSVip() > 0) {
            setVipLevel(true, false, false, null);
        } else if (user.getViplevel() > 0) {
            setVipLevel(false, true, false, null);
        } else {
            setVipLevel(false, false, false, null);
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage, defaultImage);
    }

    public void execute(int chatFromType, User user, ImageLoadingListener listener) {
        if (user == null) return;
        this.chatFromtype = chatFromType;
        photoUser = user;
        String url = user.getIcon();
        if (user.getSVip() > 0) {
            setVipLevel(true, false, false, null);
        } else if (user.getViplevel() > 0) {
            setVipLevel(false, true ,false, null);
        } else {
            setVipLevel(false, false ,false, null);
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage,defaultImage);
        mImageView.setOnClickListener(headPhotoClick);

    }

    /**
     * 绘制会员边框
     *
     * @param chatFromType
     * @param user
     * @param listener
     * @param isVip
     */
    public void execute(int chatFromType, User user, ImageLoadingListener listener, boolean isVip) {
        if (user == null) return;
        this.chatFromtype = chatFromType;
        photoUser = user;
        String url = user.getIcon();
        if (user.getSVip() > 0) {
            setVipLevel(true, false,false, null);
        } else if (user.getViplevel() > 0) {
            setVipLevel(false, true,false, null);
        } else {
            setVipLevel(false, false,false, null);
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage,defaultImage);

    }


    /**
     * @param user        图片对应的用户信息
     * @param isClickable 是否可点击 true:可以点击
     */
    public void execute(User user, boolean isClickable) {
        if (user == null) return;
        photoUser = user;
        String url;
        if (!TextUtils.isEmpty(user.getIcon()) && !user.getIcon().contains("_s.")) {
            url = CommonFunction.getThumPicUrl(user.getIcon());
        } else {
            url = user.getIcon();
        }
        if (user.getSVip() > 0) {
            setVipLevel(true, false,false, null);
        } else if (user.getViplevel() > 0) {
            setVipLevel(false, true,false, null);
        } else {
            setVipLevel(false, false,false, null);
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage,defaultImage);
        mImageView.setOnClickListener(headPhotoClick);
        this.setClickable(isClickable);
    }

    /* isVip 是否会员 isSvip 是否永久会员
    * */
    public void setVipLevel(boolean isSvip, boolean isVip, boolean showLevel, String level) {

        LayoutParams bgIv = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mBackground.setLayoutParams(bgIv);

        LayoutParams lpIv = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        if (isSvip || isVip){
//            int margin = CommonFunction.dipToPx(getContext(), (int) ((int)this.getLayoutParams().width * 0.025));
//            lpIv.setMargins(margin, margin, margin, margin);
//        }

        mImageView.setScaleType(ScaleType.FIT_START);
        mBackground.setScaleType(ScaleType.FIT_XY);
        mImageView.setLayoutParams(lpIv);

        if (isSvip) {
            mBackground.setImageResource(R.drawable.head_photo_vip);
            mBackground.setVisibility(VISIBLE);
        } else if (isVip) {
            mBackground.setImageResource(R.drawable.head_photo_svip);
            mBackground.setVisibility(VISIBLE);
        } else {
            mBackground.setVisibility(GONE);
        }
        if(showLevel){
            if(isSvip || isVip){
                mUserLevelImageView.setImageResource(R.drawable.user_level_vip);
            }else{
                mUserLevelImageView.setImageResource(R.drawable.user_level_normal);
            }
            mUserLevelImageView.setVisibility(VISIBLE);
            mUserLevelTextView.setVisibility(VISIBLE);
            mUserLevelTextView.setText(level);
        }else{
            mUserLevelImageView.setVisibility(GONE);
            mUserLevelTextView.setVisibility(GONE);
        }
    }

    public void clickable(Boolean isClickable) {
        mImageView.setClickable(isClickable);
    }

    public void setOnHeadPhotoViewClick(OnClickListener listener) {
        mImageView.setOnClickListener(listener);
    }

    private OnClickListener headPhotoClick = new OnClickListener() {
        @Override
        public void onClick(View v) {//取消头像点击事件
            if (photoUser.getUid() == Common.getInstance().loginUser.getUid()) {
                Intent intent = new Intent(getContext(), UserInfoActivity.class);
                intent.putExtra(Constants.UID, photoUser.getUid());
                Activity activity = CloseAllActivity.getInstance().getTopActivity();
                if(activity!=null){
                    activity.startActivity(intent);
                }
            } else {
                Intent intent = new Intent(getContext(), OtherInfoActivity.class);
                intent.putExtra(Constants.UID, photoUser.getUid());
                intent.putExtra("user", photoUser);
                Activity activity = CloseAllActivity.getInstance().getTopActivity();
                if(activity!=null){
                    activity.startActivity(intent);
                }
            }

        }
    };

    public void executeChat(int defaultImage, String url, int svip, int vip,int level) {
        boolean isSvip = svip > 0;
        boolean isvip = vip > 0;
        if(level>=0){
            setVipLevel(isSvip, isvip, true, String.valueOf(level));
        }else{
            setVipLevel(isSvip, isvip, false, null);
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage,defaultImage);
    }

    public void executeRoundFrame(int defaultImage, String url) {
        setVipLevel(false, false,false, null);
        if (CommonFunction.isEmptyOrNullStr(url)) {
            // 当图片链接为空，设置默认图片并且隐藏进度条
            mImageView.setImageResource(defaultImage);
        } else {
            int borderSize = CommonFunction.dipToPx(getContext(), 0.1f);
            GlideUtil.loadCircleImageFrame(BaseApplication.appContext, url, mImageView, defaultImage, borderSize, R.color.splash_login);
        }
    }

    public void executeGreet(int FromType, User user,
                             ImageLoadingListener listener) {
        chatFromtype = FromType;
        photoUser = user;
        String url = user.getIcon();
        mImageView.setScaleType(ScaleType.CENTER_INSIDE);
        if (user.getSVip() > 0) {
            setVipLevel(true, false,false, null);
        } else if (user.getViplevel() > 0) {
            setVipLevel(false, true,false, null);
        } else {
            setVipLevel(false, false,false, null);
        }
        GlideUtil.loadCircleChaceImage(BaseApplication.appContext, url, mImageView, defaultImage,defaultImage);
        mImageView.setOnClickListener(headPhotoClick);
    }


    public void executeWithCallback(String url, int svip, int vip, int level, GlideUtil.IOnLoadBitmap iOnLoadBitmap) {
        boolean isSvip = svip > 0;
        boolean isvip = vip > 0;
        if(level>=0){
            setVipLevel(isSvip, isvip, true, String.valueOf(level));
        }else{
            setVipLevel(isSvip, isvip, false, null);
        }
        GlideUtil.loadCircleImage(BaseApplication.appContext, url, mImageView, defaultImage, iOnLoadBitmap);
    }

}
