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

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.conf.DataTag;
import net.iaround.model.obj.Dynamic;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.PreviewActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

import java.util.Locale;

/**
 * Class: 动态文本图片
 * Author：gh
 * Date: 2016/12/10 19:21
 * Email：jt_gaohang@163.com
 */
public class DynamicItemView extends RelativeLayout {
    public static final int DYNAMIC_CENTER = 1;// 展示在动态中心
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

    private ItemDynamicClick itemDynamicClick;

    public DynamicItemView(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater.from(getContext()).inflate(
                R.layout.item_dynamic, this);
        initView();
    }

    /**
     * @param context
     * @return
     */
    public static DynamicItemView initDynamicView(Context context) {
        return new DynamicItemView(context);
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
    }

    /**
     * 是否是详情的动态View
     * @param isDetails
     */
    public void setDetails(boolean isDetails){
        if (isDetails){
            if (ivComment != null){
                ivComment.setImageResource(R.drawable.dynamic_details_comment);
            }
        }
    }


    public void Build(final Dynamic dynamic) {

        initFunctionView(dynamic);
        if (dynamic.vip != Constants.USER_SVIP && dynamic.vip != Constants.USER_VIP) {
            tvName.setTextColor(mContext.getResources().getColor(R.color.common_black));
        }

        User user = new User();
        user.setUid(dynamic.uid);
        user.setSVip(dynamic.vip);
        user.setIcon(dynamic.headPic);
        ivVater.execute(user);

        String name = dynamic.nickname;
        if (name == null || name.length() <= 0 || name.equals("null")) {
            name = dynamic.nickname;
        }
        SpannableString spName = FaceManager.getInstance(mContext)
                .parseIconForString(mContext, name, 0, null);
        tvName.setText(spName);

        if(dynamic.gender == 1){
            ivSex.setImageResource(R.drawable.thread_register_man_select);
            lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);
        } else {
            ivSex.setImageResource(R.drawable.thread_register_woman_select);
            lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
        }
        try {
            tvAge.setText(""+""+TimeFormat.getCurrentAgeByBirthdate(dynamic.brithday));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] items = mContext.getResources().getStringArray(R.array.horoscope_date);
        //星座
        tvConstellation.setText(items[dynamic.horoscope]);

        DynamicImageLayout layout = DynamicImageLayout.initDynamicImage(mContext);

        if (dynamic.dynamicPic.size() > 0){
            rlPic.setVisibility(VISIBLE);
            layout.buildImage(lyPic,dynamic.dynamicPic);
        }else{
            rlPic.setVisibility(GONE);
        }

//        layout.setImageListenter(new DynamicImageLayout.ImageListenter() {
//            @Override
//            public void imageList(int position) {
//                PreviewActivity.launch(mContext,dynamic.dynamicPic,position,DataTag.DYNAMIC_LIST_IMAGE);
//            }
//        });

        String timeStr = TimeFormat.timeFormat1( getContext( ) , dynamic.time );
        String distanceStr = CommonFunction.covertSelfDistance(dynamic.distance);
        tvRight.setText(timeStr+" · "+distanceStr);

        String content = dynamic.content;
        if (content == null || content.length() <= 0 || content.equals("null")) {
            tvContent.setVisibility(GONE);
        }
        SpannableString spContente = FaceManager.getInstance(mContext)
                .parseIconForString(mContext, content, 0, null);
        tvContent.setText(spContente);

        tvComment.setText(String.valueOf(dynamic.commentCount));

        // 过滤地址为空,null,"null"的情况
        String addressStr = dynamic.address;
        Locale locale = Locale.getDefault();
        if (TextUtils.isEmpty(addressStr)
                || "null".equals(addressStr.toLowerCase(locale))) {
            lyAddress.setVisibility(GONE);
        }else {
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
    private void initFunctionView(final Dynamic bean) {
        // 需要知道是否点赞过，设置点赞效果
        if (bean.loved == 1)// 没有点过赞
        {
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
        tvThumbs.setText(""+bean.loveCount);
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

    /** 播放点赞的动画 */
    public void playLikeAnimation() {
        ivLoveAnimation.setVisibility(VISIBLE);
        Animation mAnimation = AnimationUtils.loadAnimation(getContext(),R.anim.dynamic_love);
        ivLoveAnimation.setAnimation(mAnimation );
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


    public interface ItemDynamicClick{
        void ItemGreetDynamic(Dynamic dynamic);
        void ItemCommentDynamic(Dynamic dynamic);

    }

}
