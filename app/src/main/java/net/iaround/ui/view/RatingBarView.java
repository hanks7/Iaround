package net.iaround.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.utils.ScreenUtils;

/**
 * 作者：zx on 2017/8/14 16:55
 */
public class RatingBarView extends LinearLayout{

    private float defaultStarViewSize;//默认星星的尺寸
    private float defaultPaddingSpace;//默认星与星之间的距离
    private int singelStarSize;//每一个星星的尺寸
    private int paddingSpace = 0;//星与星之间的距离
    private int segment = 1;//当前技能段位（需计算）
    private int selectedStarCount = 0;//当前段位选中的星星数（需计算）
    private int totalSkillLevel = 60;//总等级数
    private int everySegmentCount = 5;//每段的数量
    private int starSelectedDrawable = icons[9];
    private int starUnselectedDrawable = icons_unselected[9];

    public RatingBarView(Context context) {
        this(context, null);
    }

    public RatingBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        if (!isInEditMode()){
            defaultStarViewSize = ScreenUtils.dp2px(13);
            defaultPaddingSpace = ScreenUtils.dp2px(3);
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBarView);
        if (null != typedArray){
            singelStarSize = (int) typedArray.getDimension(R.styleable.RatingBarView_singelStarSize, defaultStarViewSize);
            paddingSpace = (int) typedArray.getDimension(R.styleable.RatingBarView_paddingSpace, defaultPaddingSpace);
        }
        typedArray.recycle();
        //创建星星
        for (int i = 0; i < everySegmentCount; ++i) {
            ImageView imageView = getStarImageView(context);
            addView(imageView);
        }
    }

    private ImageView getStarImageView(Context context) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(Math.round(singelStarSize + paddingSpace), Math.round(singelStarSize));
        imageView.setLayoutParams(param);
        //设置两颗星星的距离
        imageView.setPadding(0, 0, paddingSpace, 0);
        imageView.setImageResource(starUnselectedDrawable);
        imageView.setMaxWidth(singelStarSize);
        imageView.setMaxHeight(singelStarSize);
        return imageView;
    }

    /**
     * 设置段位，只需传入当前技能的等级即可
     * @param skillLevel 技能等级：1-60级
     */
    public void setStarView(int skillLevel) {
        calculateSegment(skillLevel);
        //处理选中的效果
        for (int i = 0; i < selectedStarCount; i++) {
            ImageView imageView = (ImageView) getChildAt(i);
//            Glide.with(getContext()).load(starSelectedDrawable).into(imageView);
//            Glide.with(BaseApplication.appContext).load(starSelectedDrawable).into(imageView);
            GlideUtil.loadImageDefault(BaseApplication.appContext,starSelectedDrawable,imageView);
        }
        //处理未被选中的效果
        for (int i = this.everySegmentCount - 1; i >= selectedStarCount; i--) {
            ImageView imageView = (ImageView) getChildAt(i);
//            Glide.with(getContext()).load(starUnselectedDrawable).into(imageView);
//            Glide.with(BaseApplication.appContext).load(starUnselectedDrawable).into(imageView);
            GlideUtil.loadImageDefault(BaseApplication.appContext,starUnselectedDrawable,imageView);
        }
    }

    /**
     * 获取当前选中的imageView
     * @return
     */
    public ImageView getSelectedView(){
        ImageView imageView = (ImageView) getChildAt(selectedStarCount - 1);
        return imageView;
    }

    /**
     * 获取当前选中imageView在屏幕中的位置
     * @return
     */
    public int[] getStarPosition(){
        int[] position = new int[2];
        getSelectedView().getLocationInWindow(position);
        return position;
    }

    /**
     * 获取选中imageView的drawable
     * @return
     */
    public int getDrawable(){
        return starSelectedDrawable;
    }

    /**
     * 获取当前选中view的尺寸
     * @return
     */
    public int[] getStarSize(){
        int[] size = new int[2];
        size[0] = singelStarSize;
        size[1] = singelStarSize;
        return size;
    }


    /**
     * 计算当前技能等级所在的段位与当前段位应选中的星星数
     * 技能等级一共有60级，分12个段位，每个段位5级
     * @param skillLevel
     */
    public void calculateSegment(int skillLevel){
        //对等级数值判定，小于0与大于总等级数（60）的值筛选
        skillLevel = skillLevel > this.totalSkillLevel ? this.totalSkillLevel : skillLevel;
        skillLevel = skillLevel < 0 ? 0 : skillLevel;
        segment = skillLevel / everySegmentCount;//计算当前段位
        selectedStarCount = skillLevel % everySegmentCount;//计算当前选中的星星数
        //特殊情况处理
        if (skillLevel <= 5){
            segment = 1;
            selectedStarCount = skillLevel;
        }
        if (skillLevel > 5 && selectedStarCount > 0){
            segment++;
        }
        if (skillLevel > 5 && selectedStarCount == 0){//此时为段位满星的情况，例如：10、15、20...即5的倍数时
            selectedStarCount = 5;
        }

        starSelectedDrawable = icons[segment - 1];
        starUnselectedDrawable = icons_unselected[segment -1];
    }

    /**
     * 选中的星星资源id
     */
    private static final int icons[] = {
            R.drawable.skill_segment_one_,
            R.drawable.skill_segment_two,
            R.drawable.skill_segment_three,
            R.drawable.skill_segment_four,
            R.drawable.skill_segment_five,
            R.drawable.skill_segment_six,
            R.drawable.skill_segment_seven,
            R.drawable.skill_segment_eight,
            R.drawable.skill_segment_nine,
            R.drawable.skill_segment_ten,
            R.drawable.skill_segment_eleven,
            R.drawable.skill_segment_twelve
    };

    /**
     * 未被选中的星星资源id
     */
    private static final int icons_unselected[] = {
            R.drawable.skill_rank_triangle_default,
            R.drawable.skill_rank_triangle_default,
            R.drawable.skill_rank_fourcorns_default,
            R.drawable.skill_rank_fourcorns_default,
            R.drawable.skill_rank_fourcorns_default,
            R.drawable.skill_rank_fourcorns_default,
            R.drawable.skill_rank_fivecorns_default,
            R.drawable.skill_rank_fivecorns_default,
            R.drawable.skill_rank_fivecorns_default,
            R.drawable.skill_rank_fivecorns_default,
            R.drawable.skill_rank_crown_default,
            R.drawable.skill_rank_crown_default
    };

}
