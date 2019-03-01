package net.iaround.ui.dynamic.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.InnerJump;
import net.iaround.tools.StringUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.PictureDetailsActivity;
import net.iaround.ui.dynamic.bean.DynamicInfo;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dynamic.DynamicImageLayout;

import java.util.ArrayList;
import java.util.Locale;

import static android.R.attr.type;


/**
 * Class: 动态文本图片
 * Author：gh
 * Date: 2016/12/10 19:21
 * Email：jt_gaohang@163.com
 */
public class DynamicItemViewNew extends RelativeLayout {

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
    private ImageView ivPosition;
    private LinearLayout lyAddress;
    private TextView tvAddress;
    private TextView tvComment;
    private TextView tvThumbs;
    private ImageView ivThumbs;
    private LinearLayout lyComment;
    private LinearLayout lyThumbs;
    private ImageView ivComment;

    private ItemDynamicClick itemDynamicClick;

    protected int showType;// 显示的位置
    protected final int MAX_LENGTH = 140;// 列表时话题内容显示的最大字数
    private final int SHOW_MAX_LINES = 7;//动态详情以外,最多显示的行数
    protected int CONTENT_TEXT_SIZE_DP = 16;
    public static final int DYNAMIC_CENTER = 1;// 展示在动态中心
    public static final int DYNAMIC_PERSONAL = 2;// 展示在个人动态
    public static final int DYNAMIC_DETAIL = 3;// 展示在动态详情

    public DynamicItemViewNew(Context context) {
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
    public static DynamicItemViewNew initDynamicView(Context context) {
        return new DynamicItemViewNew(context);
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
        ivPosition = (ImageView) findViewById(R.id.iv_position);
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
     *
     * @param isDetails
     */
    public void setDetails(boolean isDetails) {
        if (isDetails) {
            if (ivComment != null) {
                ivComment.setImageResource(R.drawable.dynamic_details_comment);
            }
        }
    }

    //线上的展示方法
    public void Build(DynamicItemBean bean, int showType) {
        this.showType = showType;
        if (bean == null) {
            return;
        }
        if (bean.getDynamicUser() != null) {
            ivVater.execute(bean.getDynamicUser().contact,
                    bean.getDynamicUser().convertBaseToUser(), null);
        }
        initFunctionView(bean);
        DynamicItemBean.DynamicUserBean user = bean.getDynamicUser();
        DynamicInfo info = bean.getDynamicInfo();

        tvComment.setText(String.valueOf(bean.reviewcount));

        SpannableString spName = null;
        if(!TextUtils.isEmpty(user.notes)){
            spName = FaceManager.getInstance(getContext())
                    .parseIconForString(tvName, getContext(),
                            user.notes, CONTENT_TEXT_SIZE_DP);
        }else if (!TextUtils.isEmpty(user.nickname)) {
            spName = FaceManager.getInstance(getContext())
                    .parseIconForString(tvName, getContext(),
                            user.nickname, CONTENT_TEXT_SIZE_DP);

        }else {
            spName = FaceManager.getInstance(getContext())
                    .parseIconForString(tvName, getContext(),
                            user.userid + "", CONTENT_TEXT_SIZE_DP);
        }
        if(spName != null) {
            // 名字
            tvName.setText(spName);
        }

        if (user.svip == 0 || type == DYNAMIC_DETAIL) {
            tvName.setTextColor(getResources().getColor(R.color.c_323232));
        } else if (type == DYNAMIC_PERSONAL) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setTextColor(getResources().getColor(R.color.c_ee4552));
        }

        // 年龄、性别
        tvAge.setText(String.valueOf(user.age));
        if (tvAge.getTag() == null
                || (user.isMale() ^ (Boolean) tvAge.getTag())) {
            if (user.isMale()) {
                ivSex.setImageResource(R.drawable.thread_register_man_select);
                lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);
            } else {
                ivSex.setImageResource(R.drawable.thread_register_woman_select);
                lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
            }
        }
        tvAge.setTag(user.isMale());


        if (info.dynamiccategory == 1) {
//            tvTime.setVisibility(View.GONE);
//            tvPersonalTime.setVisibility(View.GONE);
        } else if (showType == DYNAMIC_PERSONAL) {
//            tvRight.setVisibility(View.GONE);
//            String timeStr = TimeFormat.timeFormat4(getContext(), info.datetime);
//            tvPersonalTime.setText(timeStr);
//            tvPersonalTime.setVisibility(View.VISIBLE);

        } else if (showType != DYNAMIC_PERSONAL) {
            String timeStr = TimeFormat.timeFormat4(getContext(), info.datetime);
            int distanceStr = info.distance;
            // 距离需要换算单位
            String disStr = "";
            if (bean.getDynamicInfo().distance > 0) {
                disStr = CommonFunction
                        .covertSelfDistance(distanceStr);
            }
            tvRight.setText(timeStr + " · " + disStr);
        }


        // 过滤地址为空,null,"null"的情况
        String addressStr = bean.getDynamicInfo().getAddress();
        Locale locale = Locale.getDefault();
        String address;
        if (TextUtils.isEmpty(addressStr)
                || "null".equals(addressStr.toLowerCase(locale))) {
            addressStr = "";
        }
//        String disAdressStr = TextUtils.isEmpty(disStr) ? addressStr : disStr
//                + " " + addressStr;

        if (TextUtils.isEmpty(addressStr)) {
            ivPosition.setVisibility(GONE);
            tvAddress.setVisibility(View.GONE);
        } else {
            ivPosition.setVisibility(VISIBLE);
            tvAddress.setVisibility(View.VISIBLE);
            if (addressStr.contains(",")) {
                address = addressStr.replace(",", " ");
                tvAddress.setText(address);
            } else {
                tvAddress.setText(addressStr);
            }

        }
        //星座
        String[] items = mContext.getResources().getStringArray(R.array.horoscope_date);
        int horosopeNum = bean.getDynamicUser().getHoroscope();
        if (horosopeNum == -1) {
            tvConstellation.setVisibility(GONE);//判断horosopeNum = -1 的情况 yuchao
        } else {
            tvConstellation.setText(items[horosopeNum]);
        }
        //展示图片
        final ArrayList<String> photoList = bean.getDynamicInfo().getPhotoList();
        DynamicImageLayout layout = DynamicImageLayout.initDynamicImage(mContext);

        if (photoList.size() > 0) {
            rlPic.setVisibility(VISIBLE);
            if (photoList.get(0).contains("storage/")){
                layout.buildSDImage(lyPic, photoList);
            }else{
                layout.buildImage(lyPic, photoList);
            }

        } else {
            rlPic.setVisibility(GONE);
        }

        layout.setImageListenter(new DynamicImageLayout.ImageListenter() {
            @Override
            public void imageList(int position) {
//                PreviewActivity.launch(mContext, photoList, position, DataTag.DYNAMIC_LIST_IMAGE);//取消点击动态图片浏览界面
                PictureDetailsActivity.launch(mContext, photoList, position);
            }
        });

        //展示文字内容
        String content = bean.getDynamicInfo().getContent();

        if (TextUtils.isEmpty(content)) {
            String Str = bean.getDynamicInfo().getLabel();
            if (!CommonFunction.isEmptyOrNullStr(Str)) {
                tvContent.setVisibility(VISIBLE);
                setContent(content, bean);
            } else {
                tvContent.setVisibility(GONE);
            }
        } else {

            if (type != DYNAMIC_DETAIL) {
                content = StringUtil.getEntireFaceString(
                        bean.getDynamicInfo().getContent(), MAX_LENGTH);
                if (bean.getDynamicInfo().getContent().length() > MAX_LENGTH) {
                    content += "...";
                }
                tvContent.setEllipsize(TextUtils.TruncateAt.END);
                tvContent.setMaxLines(SHOW_MAX_LINES);
            }

            tvContent.setVisibility(VISIBLE);
            setContent(content, bean);
        }


        if (bean.getSendStatus() == DynamicItemBean.SUCCESS) {
            this.setClickable(true);
        } else {
            this.setClickable(false);
        }
    }

    //改版的展示方法
    public void Build(final DynamicItemBean dynamic) {
//        DynamicItemBean.DynamicUserBean userBean = dynamic.getDynamicUser();
//
//        ///////
//        initFunctionView(dynamic);
//        if (dynamic.vip != Constants.USER_SVIP && dynamic.vip != Constants.USER_VIP) {
//            tvName.setTextColor(mContext.getResources().getColor(R.color.common_black));
//        }
//
//        User user = new User();
//        user.setUid(dynamic.uid);
//        user.setSVip(dynamic.vip);
//        user.setIcon(dynamic.headPic);
//        ivVater.execute(user);
//
//        String name = dynamic.nickname;
//        if (name == null || name.length() <= 0 || name.equals("null")) {
//            name = dynamic.nickname;
//        }
//        SpannableString spName = FaceManager.getInstance(mContext)
//                .parseIconForString(mContext, name, 0, null);
//        tvName.setText(spName);
//
//        if (dynamic.gender == 1) {
//            ivSex.setImageResource(R.drawable.thread_register_man_select);
//            lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);
//        } else {
//            ivSex.setImageResource(R.drawable.thread_register_woman_select);
//            lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
//        }
//        try {
//            tvAge.setText("" + "" + TimeFormat.getCurrentAgeByBirthdate(dynamic.brithday));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String[] items = mContext.getResources().getStringArray(R.array.horoscope_date);
//        //星座
//        tvConstellation.setText(items[dynamic.horoscope]);
//
//        DynamicImageLayout layout = DynamicImageLayout.initDynamicImage(mContext);
//
//        if (dynamic.dynamicPic.size() > 0) {
//            rlPic.setVisibility(VISIBLE);
//            layout.buildImage(lyPic, dynamic.dynamicPic);
//        } else {
//            rlPic.setVisibility(GONE);
//        }
//
//        layout.setImageListenter(new DynamicImageLayout.ImageListenter() {
//            @Override
//            public void imageList(int position) {
//                PreviewActivity.launch(mContext, dynamic.dynamicPic, position, DataTag.DYNAMIC_LIST_IMAGE);
//            }
//        });
//
//        String timeStr = TimeFormat.timeFormat1(getContext(), dynamic.time);
//        String distanceStr = CommonFunction.covertSelfDistance(dynamic.distance);
//        tvRight.setText(timeStr + " · " + distanceStr);
//
//        String content = dynamic.content;
//        if (content == null || content.length() <= 0 || content.equals("null")) {
//            tvContent.setVisibility(GONE);
//        }
//        SpannableString spContente = FaceManager.getInstance(mContext)
//                .parseIconForString(mContext, content, 0, null);
//        tvContent.setText(spContente);
//
//        tvComment.setText(String.valueOf(dynamic.commentCount));
//
//        // 过滤地址为空,null,"null"的情况
//        String addressStr = dynamic.address;
//        Locale locale = Locale.getDefault();
//        if (TextUtils.isEmpty(addressStr)
//                || "null".equals(addressStr.toLowerCase(locale))) {
//            lyAddress.setVisibility(GONE);
//        } else {
//            lyAddress.setVisibility(VISIBLE);
//        }
//
//        tvAddress.setText(addressStr);

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
    private void initFunctionView(final DynamicItemBean bean) {
        // 需要知道是否点赞过，设置点赞效果
        if (bean.curruserlove == 1)// 没有点过赞
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
        tvThumbs.setText("" + bean.likecount);
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


    public interface ItemDynamicClick {
        void ItemGreetDynamic(DynamicItemBean dynamic);

        void ItemCommentDynamic(DynamicItemBean dynamic);

    }

    private void setContent(String content, final DynamicItemBean bean) {
        final Context context = getContext();
        String Str = bean.getDynamicInfo().getLabel();
        if (!CommonFunction.isEmptyOrNullStr(Str)) {
            String labelStr = "#" + CommonFunction.getLangText(context, Str) + "#";

            String finalStr = labelStr;

            SpannableString contentStr = FaceManager.getInstance(context)
                    .parseIconForStringBaseline(tvContent, context,
                            finalStr + content, 0);

            contentStr.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#22a4ff"));
                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(View widget) {
                    InnerJump.Jump(context, bean.getDynamicInfo().getLabelurl());
                }
            }, 0, labelStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            tvContent.setText(contentStr);
            //解决ListView里TextView设置LinkMovementMethod后导致其ItemClick失效的问题
            /*
            不用LinkMovementMethod方法，直接用OnTouchListener对ClickableSpan事件进行处理
			 */
            tvContent.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean ret = false;
                    CharSequence text = ((TextView) v).getText();
                    Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
                    TextView widget = (TextView) v;
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        x -= widget.getTotalPaddingLeft();
                        y -= widget.getTotalPaddingTop();
                        x += widget.getScrollX();
                        y += widget.getScrollY();
                        Layout layout = widget.getLayout();
                        int line = layout.getLineForVertical(y);
                        int off = layout.getOffsetForHorizontal(line, x);
                        ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);
                        if (link.length != 0) {
                            if (action == MotionEvent.ACTION_UP) {
                                link[0].onClick(widget);
                            }
                            ret = true;
                        }
                    }
                    return ret;
                }
            });


        } else {
            SpannableString contentStr = FaceManager.getInstance(context)
                    .parseIconForStringBaseline(
                            tvContent, context, content, 0);
            tvContent.setText(contentStr);
        }
    }

}
