package net.iaround.ui.view.dynamic;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.DynamicDetailsBean.DynamicLove;
import net.iaround.model.entity.DynamicLoveBean;
import net.iaround.model.im.DynamicLoveInfo;
import net.iaround.model.im.DynamicLoveInfo.LoverUser;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.PictureDetailsActivity;
import net.iaround.ui.activity.UserLikeListActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.dynamic.bean.DynamicInfo;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.view.HeadPhotoView;

import java.util.List;
import java.util.Locale;

/**
 * Class: 动态详情头部
 * Author：gh
 * Date: 2016/12/10 19:21
 * Email：jt_gaohang@163.com
 */
public class DynamicDetailsHeadView extends RelativeLayout {

    private Context mContext;

    private HeadPhotoView ivVater;
    private TextView tvName;
    private LinearLayout lyAge;
    private ImageView ivSex;
    private TextView tvAge;
    private TextView tvRight;
    private TextView tvConstellation;
    private TextView tvContent;
    private LinearLayout lyPic;
    private RelativeLayout rlPic;
    private ImageView ivLoveAnimation;
    private LinearLayout lyAddress;
    private TextView tvAddress;
    private TextView tvComment;
    private TextView tvThumbs;
    private ImageView ivThumbs;
    private LinearLayout lyComment;
    private LinearLayout lyThumbs;
    private ImageView ivComment;
    private LinearLayout lyLovePhoto;
    private LinearLayout lyHeadPhoto;
    private TextView commentLoveCount;
    private LinearLayout lyLoveComment;
    private TextView tvCommentCount;
    private View viewLine;

    private ItemDynamicClick itemDynamicClick;

    public DynamicDetailsHeadView(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater.from(getContext()).inflate(
                R.layout.item_dynamic_head, this);
        initView();
    }

    /**
     * @param context
     * @return
     */
    public static DynamicDetailsHeadView initDynamicView(Context context) {
        return new DynamicDetailsHeadView(context);
    }

    /**
     * 根据显示动态的位置,初始化View
     */
    public void initView() {
        ivVater = (HeadPhotoView) findViewById(R.id.iv_dynamic_avatar);
        tvName = (TextView) findViewById(R.id.tv_dynamic_name);
        lyAge = (LinearLayout) findViewById(R.id.ly_dynamic_age);
        ivSex = (ImageView) findViewById(R.id.iv_dynamic_sex);
        tvAge = (TextView) findViewById(R.id.tv_dynamic_age);
        tvRight = (TextView) findViewById(R.id.tv_dynamic_right);
        tvConstellation = (TextView) findViewById(R.id.tv_dynamic_constellation);
        tvContent = (TextView) findViewById(R.id.tv_dynamic_content);
        rlPic = (RelativeLayout) findViewById(R.id.rl_dynamic_pic);
        lyPic = (LinearLayout) findViewById(R.id.ly_dynamic_pic);
        ivLoveAnimation = (ImageView) findViewById(R.id.iv_dynamic_anmation);
        lyAddress = (LinearLayout) findViewById(R.id.ly_dynamic_address);
        tvAddress = (TextView) findViewById(R.id.tv_dynamic_address);
        tvComment = (TextView) findViewById(R.id.tv_dynamic_comment);
        tvThumbs = (TextView) findViewById(R.id.tv_dynamic_thumbs);
        ivThumbs = (ImageView) findViewById(R.id.iv_dynamic_thumbs);
        ivComment = (ImageView) findViewById(R.id.iv_dynamic_comment);
        lyComment = (LinearLayout) findViewById(R.id.ly_dynamic_comment);
        lyThumbs = (LinearLayout) findViewById(R.id.ly_dynamic_thumbs);

        viewLine = findViewById(R.id.line4);
        lyLovePhoto = (LinearLayout) findViewById(R.id.ly_dynamic_details_love_photo);
        lyHeadPhoto = (LinearLayout) findViewById(R.id.ly_head_photo);
        commentLoveCount = (TextView) findViewById(R.id.tv_details_love_number);
        lyLoveComment = (LinearLayout) findViewById(R.id.ly_dynamic_details_comment);
        tvCommentCount = (TextView) findViewById(R.id.tv_dynamic_details_comment);
    }

    /**
     * 是否是详情的动态View
     *
     * @param isDetails
     */
    public void setDetails(boolean isDetails) {
        if (isDetails) {
            if (ivComment != null) {
                ivComment.setImageResource(R.drawable.dynamic_new_comment);
            }
        }
    }

    public void Build(final DynamicItemBean dynamicHeader) {
        final DynamicInfo dynamic = dynamicHeader.getDynamicInfo();
        final DynamicLoveInfo dynamicLove = dynamicHeader.getDynamicLoveInfo();

//        if (dynamicHeader.getDynamicReviewInfo().total <= 0) {
        lyLoveComment.setVisibility(GONE);
//        } else {
//            lyLoveComment.setVisibility(VISIBLE);
//        }

        if (dynamicHeader.getDynamicLoveInfo() == null) {
            lyLovePhoto.setVisibility(GONE);
            viewLine.setVisibility(GONE);
        } else {
            if (dynamicHeader.getDynamicLoveInfo().loveusers == null){
                lyLovePhoto.setVisibility(GONE);
                viewLine.setVisibility(GONE);
            }else{
                if (dynamicHeader.getDynamicLoveInfo().loveusers.size() <= 0) {
                    lyLovePhoto.setVisibility(GONE);
                    viewLine.setVisibility(GONE);
                } else {
                    lyLovePhoto.setVisibility(VISIBLE);
                    viewLine.setVisibility(VISIBLE);
                }
            }

        }
        initLove(dynamicLove, dynamic.dynamicid);
        initFunctionView(dynamicHeader);
        tvComment.setText(String.valueOf(dynamicHeader.reviewcount));

        if (dynamicHeader.getDynamicUser().vip != Constants.USER_SVIP && dynamicHeader.getDynamicUser().vip != Constants.USER_VIP) {//dynamic.vip
            tvName.setTextColor(mContext.getResources().getColor(R.color.common_black));
        }

        User user = new User();
        user.setUid(dynamicHeader.getDynamicUser().userid);
        user.setSVip(dynamicHeader.getDynamicUser().vip);
        user.setIcon(dynamicHeader.getDynamicUser().icon);
        ivVater.execute(user, BaseApplication.appContext);

        String name = dynamicHeader.getDynamicUser().nickname;
        if (name == null || name.length() <= 0 || name.equals("null")) {
            name = dynamicHeader.getDynamicUser().nickname;
        }
        SpannableString spName = FaceManager.getInstance(mContext)
                .parseIconForString(mContext, name, 0, null);
        tvName.setText(spName);

        // 年龄、性别
        tvAge.setText(String.valueOf(dynamicHeader.getDynamicUser().age));
        if (tvAge.getTag() == null
                || (dynamicHeader.getDynamicUser().isMale() ^ (Boolean) tvAge.getTag())) {
            if (dynamicHeader.getDynamicUser().isMale()) {
                ivSex.setImageResource(R.drawable.thread_register_man_select);
                lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);
            } else {
                ivSex.setImageResource(R.drawable.thread_register_woman_select);
                lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
            }
        }
        tvAge.setTag(dynamicHeader.getDynamicUser().isMale());
//        if ("y".equals(dynamicHeader.getDynamicUser().gender)) {
//            ivSex.setImageResource(R.drawable.thread_register_man_select);
//            lyAge.setBackgroundResource(R.drawable.encounter_dynamic_man_circle_bg);
//        } else {
////            ivSex.setImageResource(R.drawable.thread_register_woman_new_select);
//            ivSex.setImageResource(R.drawable.thread_register_woman_select);
//            lyAge.setBackgroundResource(R.drawable.encounter_dynamic_woman_circle_bg);
//        }
//        try {
//            tvAge.setText("" + "" + TimeFormat.getCurrentAgeByBirthdate(dynamicHeader.getDynamicUser().age));//brithday
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //星座
        String[] items = mContext.getResources().getStringArray(R.array.horoscope_date);
//        tvConstellation.setText(items[dynamicHeader.getDynamicUser().getHoroscope()]);//jiqiang星座需要加
        tvConstellation.setVisibility(GONE);
        DynamicImageLayout layout = DynamicImageLayout.initDynamicImage(mContext);

        if (dynamic.getPhotoList().size() > 0) {//dynamicPic
            rlPic.setVisibility(VISIBLE);
            layout.buildImage(lyPic, dynamic.getPhotoList());
        } else {
            rlPic.setVisibility(GONE);
        }

        layout.setImageListenter(new DynamicImageLayout.ImageListenter() {
            @Override
            public void imageList(int position) {
//                PreviewActivity.launch(mContext, dynamic.getPhotoList(), position, DataTag.DYNAMIC_LIST_IMAGE);
                PictureDetailsActivity.launch(mContext, dynamic.getPhotoList(), position);
            }
        });

        String timeStr = TimeFormat.timeFormat1(getContext(), dynamic.datetime);//dynamic.time
        String distanceStr = CommonFunction.covertSelfDistance(dynamic.distance);
        tvRight.setText(timeStr + " · " + distanceStr);

        String content = dynamic.getContent();
        if (content == null || content.length() <= 0 || content.equals("null")) {
            tvContent.setVisibility(GONE);
        }
        SpannableString spContente = FaceManager.getInstance(mContext)
                .parseIconForString(mContext, content, 0, null);
        tvContent.setText(spContente);

        // 过滤地址为空,null,"null"的情况
        String addressStr = dynamic.getAddress();
        Locale locale = Locale.getDefault();
        if (TextUtils.isEmpty(addressStr)
                || "null".equals(addressStr.toLowerCase(locale))) {
            lyAddress.setVisibility(GONE);
        } else {
            lyAddress.setVisibility(VISIBLE);
        }

        tvAddress.setText(addressStr);

    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ivLoveAnimation.setVisibility(View.GONE);
        }
    };

    // 设置按钮和地理位置距离
//    private void initFunctionView(final Dynamic bean) {
//        // 需要知道是否点赞过，设置点赞效果
//        if (bean.loved == 1)// 没有点过赞
//        {
//            if (bean.isCurrentHanleView) {
//                playLikeAnimation();
//                bean.isCurrentHanleView = false;
//            }
//            ivThumbs.setImageResource(R.drawable.dynamic_like_normal);
//        } else {
//            if (bean.isCurrentHanleView) {
//                bean.isCurrentHanleView = false;
//            }
//            ivThumbs.setImageResource(R.drawable.dynamic_like_pres);
//        }
//        tvThumbs.setText("" + bean.loveCount);
//        lyThumbs.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                itemDynamicClick.ItemGreetDynamic(bean);
//            }
//        });
//        lyComment.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                itemDynamicClick.ItemCommentDynamic(bean);
//            }
//        });
//    }

    // 设置按钮和地理位置距离
    private void initFunctionView(final DynamicItemBean bean) {
        // 需要知道是否点赞过，设置点赞效果
        if (bean.curruserlove == 1)// 点过赞
        {
//            playLikeAnimation();
            if (bean.isCurrentHanleView) {
                playLikeAnimation();
                bean.isCurrentHanleView = false;
            }
            ivThumbs.setImageResource(R.drawable.dynamic_like_normal);
        } else {
            if (bean.isCurrentHanleView) {
                bean.isCurrentHanleView = false;
            }
            ivThumbs.setImageResource(R.drawable.dynamic_like_pres);
        }
        tvThumbs.setText(String.valueOf(bean.likecount));
        lyThumbs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                itemDynamicClick.ItemGreetDynamic(bean);
            }
        });
        lyComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                itemDynamicClick.ItemCommentDynamic(bean);
            }
        });
    }

    /**
     * 播放点赞的动画
     */
    public void playLikeAnimation() {
        ivLoveAnimation.setVisibility(VISIBLE);
        Animation mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.dynamic_love);
        ivLoveAnimation.setAnimation(mAnimation);
        mAnimation.setAnimationListener(animationListener);
        mAnimation.start();

    }

    /**
     * 设置赞按钮的点击监听器
     *
     * @param listener
     */
    public void setBtnClickListener(ItemDynamicClick listener) {
        this.itemDynamicClick = listener;
    }


    //    public interface ItemDynamicClick {
//        public void ItemGreetDynamic(Dynamic dynamic);
//
//        public void ItemCommentDynamic(Dynamic dynamic);
//
//    }
    public interface ItemDynamicClick {
        void ItemGreetDynamic(DynamicItemBean dynamic);

        void ItemCommentDynamic(DynamicItemBean dynamic);

    }

    private void initLove(final DynamicLove dynamicLove, final long dynid) {
        refreshPhotoView(dynamicLove.loveusers, lyHeadPhoto);
//        commentLoveCount.setText(dynamicLove.total + mContext.getResources().getString(R.string.dynamic_details_love_number));

        lyLovePhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserLikeListActivity.skipToUserLike(mContext, dynid, dynamicLove.total, dynamicLove.loveusers);
            }
        });
    }

    private void initLove(final DynamicLoveInfo dynamicLove, final long dynid) {
        if (dynamicLove != null)
            refreshPhotoViewNew(dynamicLove.loveusers, lyHeadPhoto);
//        commentLoveCount.setText(dynamicLove.total + mContext.getResources().getString(R.string.dynamic_details_love_number));

        lyLovePhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UserLikeListActivity.skipToUserLikeNew(mContext, dynid, dynamicLove.total, dynamicLove.loveusers);
            }
        });
    }

    private void refreshPhotoView(List<DynamicLoveBean> list, LinearLayout lyHead) {
        if (list != null && list.size() > 0) {
            lyHead.removeAllViews();
            for (int index = 0; index < 6; index++) {
                DynamicLoveBean bean = list.get(index);
                if (bean != null) {
                    View jobView = createPhotoView(bean);
                    lyHead.addView(jobView);
                }
            }
        }
    }

    private void refreshPhotoViewNew(List<LoverUser> list, LinearLayout lyHead) {
        if (list != null && list.size() > 0) {
            lyHead.removeAllViews();
            for (int index = 0; index < list.size(); index++) {
                if (index < 6) {
                    LoverUser bean = list.get(index);
                    if (bean != null) {
                        View jobView = createPhotoView(bean);
                        lyHead.addView(jobView);
                    }
                }

            }
        }
    }

    private View createPhotoView(DynamicLoveBean bean) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_head_photo_view, null);
        HeadPhotoView photoView = (HeadPhotoView) view.findViewById(R.id.iv_details_avatar);
        User user = new User();
        user.setIcon(bean.icon);
        user.setUid(bean.uid);
        user.setSVip(bean.vip);
        photoView.execute(user);
        return view;
    }

    private View createPhotoView(LoverUser bean) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_head_photo_view, null);
        HeadPhotoView photoView = (HeadPhotoView) view.findViewById(R.id.iv_details_avatar);
        User user = new User();
        user.setIcon(bean.icon);//bean.headPic
        user.setUid(bean.userid);//bean.uid
        user.setSVip(bean.svip);
        user.setViplevel(bean.vip);
        photoView.execute(user);
        return view;
    }

}
