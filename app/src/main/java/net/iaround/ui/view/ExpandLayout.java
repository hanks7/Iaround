package net.iaround.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iaround.R;
import net.iaround.utils.ScreenUtils;

import java.util.List;

import static net.iaround.ui.fragment.SkillRankingFragment.icons;
import static net.iaround.ui.fragment.SkillRankingFragment.iconsbg;

/**
 * 带过渡动画的折叠收缩布局
 */
public class ExpandLayout extends LinearLayout {
    private View layoutView;
    //    private View layoutView;
    private int viewHeight = ScreenUtils.dp2px(80);
    private boolean isExpand;
    private long animationDuration;
    private ImageView ivFirstLeftMain;
    private ImageView ivFirstRightMain;
    private ImageView ivFirstDownLeftMain;

    private RelativeLayout rlFirstLeftMain;
    private RelativeLayout rlFirstRightMain;
    private RelativeLayout rlFirstDownLeftMain;

    private RatingBarView rbExpandLeftFirst;
    private RatingBarView rbExpandRightFirst;
    private RatingBarView rbExpandDownLeftFirst;

    private int ranking;//技能的等级
    private int segment;//技能的段位（共12段位）

    public ExpandLayout(Context context) {
        this(context, null);

    }

    public ExpandLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }



    private void initView(Context context) {
        setOrientation(VERTICAL);
        layoutView = LayoutInflater.from(context).inflate(R.layout.expand_layout_skill, this, false);
//        layoutView = inflate(context, R.layout.actvity_other_info_skill, null);
        ivFirstLeftMain = (ImageView) layoutView.findViewById(R.id.iv_skill_left_first_main_ex);
        rbExpandLeftFirst = (RatingBarView) layoutView.findViewById(R.id.rbv_skill_expand_left_first);
        rlFirstLeftMain = (RelativeLayout) layoutView.findViewById(R.id.rl_skill_left_first_main_ex);
        rlFirstRightMain = (RelativeLayout) layoutView.findViewById(R.id.rl_skill_right_main_ex);
        rlFirstDownLeftMain = (RelativeLayout) layoutView.findViewById(R.id.rl_skill_left_expand_down_main);

        ivFirstRightMain = (ImageView) layoutView.findViewById(R.id.iv_skill_right_main_ex);
        rbExpandRightFirst = (RatingBarView) layoutView.findViewById(R.id.rbv_skill_expand_right_first);

        ivFirstDownLeftMain = (ImageView) layoutView.findViewById(R.id.iv_skill_left_expand_down_main);
        rbExpandDownLeftFirst = (RatingBarView) layoutView.findViewById(R.id.rbv_skill_expand_down_left_first);
        rbExpandLeftFirst.setStarView(1);
        rbExpandRightFirst.setStarView(2);
        rbExpandDownLeftFirst.setStarView(3);

        addView(layoutView);

        isExpand = true;
        animationDuration = 300;

        setViewDimensions();
    }

    /**
     * 初始化数据
     */
    public void initShowData(int oneLeft,int skillIdOneLeft,int oneRight,int skillIdOneRight,int twoLeft,int skillIdtwoLeft) {
        if(skillIdOneLeft >=0 ) {
            rbExpandLeftFirst.setStarView(oneLeft);
            ivFirstLeftMain.setImageResource(icons[skillIdOneLeft]);
            rlFirstLeftMain.setBackgroundResource(iconsbg[skillIdOneLeft]);
        }
        if(0 <= skillIdOneRight) {
            rbExpandRightFirst.setStarView(oneRight);
            rlFirstRightMain.setBackgroundResource(iconsbg[skillIdOneRight]);
            ivFirstRightMain.setImageResource(icons[skillIdOneRight]);
        }
        if(0 <= skillIdtwoLeft) {
            rbExpandDownLeftFirst.setStarView(twoLeft);
            rlFirstDownLeftMain.setBackgroundResource(iconsbg[skillIdtwoLeft]);
            ivFirstDownLeftMain.setImageResource(icons[skillIdtwoLeft]);
        }

    }
    /**
     * 根据段位跟等级进行展示
     */
    public  void showSegmentAndStarts(List<String> list){

    }

    /**
     * @param isExpand 初始状态是否折叠
     */
    public void initExpand(boolean isExpand) {
        this.isExpand = isExpand;
        if (!isExpand) {
            animateToggle(10);
        }
    }

    /**
     * 设置动画时间
     *
     * @param animationDuration 动画时间
     */
    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    /**
     * 获取subView的总高度
     * View.post()的runnable对象中的方法会在View的measure、layout等事件后触发
     */
    private void setViewDimensions() {
        layoutView.post(new Runnable() {
            @Override
            public void run() {
                if (viewHeight <= 0) {
                    viewHeight = layoutView.getMeasuredHeight();
                }
            }
        });
    }


    public static void setViewHeight(View view, int height) {
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.requestLayout();
    }

    /**
     * 切换动画实现
     */
    private void animateToggle(long animationDuration) {
        ValueAnimator heightAnimation = isExpand ?
                ValueAnimator.ofFloat(0f, viewHeight) : ValueAnimator.ofFloat(viewHeight, 0f);
        heightAnimation.setDuration(animationDuration / 2);
        heightAnimation.setStartDelay(animationDuration / 2);

        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                setViewHeight(layoutView, (int) val);
            }
        });

        heightAnimation.start();
    }

    public boolean isExpand() {
        return isExpand;
    }

    /**
     * 折叠view
     */
    public void collapse() {
        isExpand = false;
        animateToggle(animationDuration);
    }

    /**
     * 展开view
     */
    public void expand() {
        isExpand = true;
        animateToggle(animationDuration);
    }

    public void toggleExpand(final ImageView expandView) {
        if (isExpand) {
            collapse();
            expandView.post(new Runnable() {
                @Override
                public void run() {
                    expandView.setImageResource(R.drawable.other_info_skill_expand);
                }
            });

        } else {
            expand();
            expandView.post(new Runnable() {
                @Override
                public void run() {
                    expandView.setImageResource(R.drawable.other_info_skill_receive);
                }
            });

        }
    }
}