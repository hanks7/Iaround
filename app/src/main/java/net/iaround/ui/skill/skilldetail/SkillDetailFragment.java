package net.iaround.ui.skill.skilldetail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.manager.mvpbase.MvpBaseFragment;
import net.iaround.model.im.Me;
import net.iaround.model.skill.EventSkillLevel;
import net.iaround.model.skill.PropsShopBuySuccess;
import net.iaround.model.skill.SkillDetailBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.skill.skilladdition.SkillAdditionActivity;
import net.iaround.ui.skill.skillpropshop.SkillPropsShopActivity;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.RatingBarView;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.utils.SkillHandleUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：zx on 2017/8/10 18:56
 */
public class SkillDetailFragment extends MvpBaseFragment<SkillDetailContract.Presenter> implements SkillDetailContract.View {

    private Drawable drawable;
    private String skillId;
    private String propId;
    private String goldPropId;
    private String silverpropId;
    private String currencyType;//代币类型 2钻石 1金币 6星星;

    private SkillDetailBean mSkillDetail;
    private SkillDetailBean.PropsListBean props1;
    private SkillDetailBean.PropsListBean props2;
    private int baseSuccessRate = 0;
    private int rankingSuccessRate = 0;
    private int rechargeUpgradeRate = 0;
    private int propsSuccessRate = 0;
    private int propsUpdateSuccessRate = 0;
    private int diamondSuccessRate = 0;
    private int starSuccessRate = 0;

    private int screenWith = 1080;
    private int currentRotate = 0;
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float alpha = 0.4f;
    private int currentScaleValue;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int controllX;
    private int controllY;
    private int ratingBarViewX;
    private int currentSkillLevel = 0;
    private Point currentPoint;
    private ValueAnimator anim;
    private ImageView imageView;
    private int lastSelectedProps;
    private int lastSelectedCharge;

    private List<SkillDetailSuccessBean> suceessList = new ArrayList<>();//成功率缓存
    private SkillDetailSuccessAdapter skillDetailSuccessAdapter;

    public static SkillDetailFragment getInstance(Bundle bundle) {
        SkillDetailFragment fragment = new SkillDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_skill_detail;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(SkillDetailFragment.this);
        getBundles();
        init();
        mPresenter.getSkillDetail(skillId);
    }

    private void init() {
        Typeface typeface = Typeface.createFromAsset(BaseApplication.appContext.getAssets(), "DINCond-Black.otf");
//        tv_hit_rate.setTypeface(typeface);

        screenWith = CommonFunction.getScreenPixWidth(BaseApplication.appContext);
        tv_title.setText(R.string.skill_detail);
        drawable = getResources().getDrawable(R.drawable.icon_skill_selected);
        Me user = Common.getInstance().loginUser;
        iv_user_head.execute(user, 1);
        SpannableString spNameSecond = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, user.getNickname(),
                0, null);
        tv_user_name.setText(spNameSecond);
        lastSelectedProps = R.id.moon_gold_rl;
        lastSelectedCharge = R.id.diamond_charge_rl;

        if (skillDetailSuccessAdapter == null) {
            skillDetailSuccessAdapter = new SkillDetailSuccessAdapter(mActivity, suceessList);
            gv_success_list.setAdapter(skillDetailSuccessAdapter);
            gv_success_list.setSelector(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    private void getBundles() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            skillId = bundle.getString("skillId");
        }
    }

    @Override
    public void setPresenter(SkillDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @OnClick({R.id.btn_charge, R.id.btn_increase, R.id.btn_update, R.id.btn_get_moon,
            R.id.moon_gold_rl, R.id.moon_silver_rl, R.id.diamond_charge_rl, R.id.gold_charge_rl,R.id.star_charge_rl,
            R.id.fl_back})
    public void onViewClicked(View view) {
        if (isActive()) {
            switch (view.getId()) {
                case R.id.fl_back://后退
                    mActivity.finish();
                    break;
                case R.id.btn_charge://充值
                    mActivity.startActivity(new Intent(mActivity, MyWalletActivity.class));
                    break;
                case R.id.btn_increase://提高成功率
                    mActivity.startActivity(new Intent(mActivity, SkillAdditionActivity.class));
                    break;
                case R.id.btn_update://立即升级
                    if (!TextUtils.isEmpty(skillId)) {
                        mPresenter.updateSkill(skillId, propId, currencyType);
                    }
                    break;
                case R.id.btn_get_moon://获取月亮
                    Intent intent = new Intent(getActivity(), SkillPropsShopActivity.class);
                    getActivity().startActivity(intent);
                    break;
                case R.id.moon_gold_rl:
                    moonExpendSelected(R.id.moon_gold_rl);
                    break;
                case R.id.moon_silver_rl:
                    moonExpendSelected(R.id.moon_silver_rl);
                    break;
                case R.id.diamond_charge_rl:
                    chargeSelected(R.id.diamond_charge_rl);
                    break;
                case R.id.gold_charge_rl:
                    chargeSelected(R.id.gold_charge_rl);
                    break;
                case R.id.star_charge_rl:
                    chargeSelected(R.id.star_charge_rl);
                    break;
            }
        }
    }


    /**
     * 更新技能详情页面
     *
     * @param skillDetail
     */
    @Override
    public void updateDetailView(SkillDetailBean skillDetail) {
        if (null != skillDetail) {
            mSkillDetail = skillDetail;
            tv_diamond_num.setText(getString(R.string.diamond_balance) + skillDetail.diamond);
            tv_gold_num.setText(getString(R.string.gold_balance) + skillDetail.gold);
            tv_star_num.setText(getString(R.string.stars_m) +skillDetail.star);
            currentSkillLevel = skillDetail.skill_level;
            GlideUtil.loadImage(BaseApplication.appContext, skillDetail.skill_icon, skill_icon);
            SkillHandleUtils.setFirstFrameImage(BaseApplication.appContext, skillDetail.skill_gif, skill_icon_first);
            skill_name.setText(CommonFunction.getLangText(skillDetail.skill_name));
            skill_name_icon_below.setText(CommonFunction.getLangText(skillDetail.skill_name));
            tv_skill_level.setText("LV." + skillDetail.skill_level);
            ratingBarView.setStarView(skillDetail.skill_level);
            tv_current_effect_desc.setText(CommonFunction.getLangText(skillDetail.currentEffect));
            tv_update_effect_desc.setText(CommonFunction.getLangText(skillDetail.updateEffect));
            pb_proficiency.setMax(skillDetail.MAXmastery);
            pb_proficiency.setProgress(skillDetail.currentMastery);

            List<SkillDetailBean.PropsListBean> propsList = skillDetail.propsList;
            if (null != propsList && propsList.size() > 1) {
                props1 = propsList.get(0);
                props2 = propsList.get(1);

                String color = getColor(propsList.get(0).propNum, propsList.get(0).propExpend);
                String propExpend = "<font color='" + color + "'>" + propsList.get(0).propNum + "/" + "</font>" + propsList.get(0).propExpend;
                tv_moon_gold_expend.setText(Html.fromHtml(propExpend));

                String updateSuccessRate = "<font color='#ff4064'>" + "+" + propsList.get(0).updateSuccessRate / 100 + "%" + "</font>";
                tv_moon_gold_successful.setText(Html.fromHtml(getString(R.string.success_rate) + updateSuccessRate));

                color = getColor(propsList.get(1).propNum, propsList.get(1).propExpend);
                String propExpend1 = "<font color='" + color + "'>" + propsList.get(1).propNum + "/" + "</font>" + propsList.get(1).propExpend;
                tv_moon_silver_expend.setText(Html.fromHtml(propExpend1));

                propsUpdateSuccessRate = propsList.get(0).updateSuccessRate / 100;
                goldPropId = propsList.get(0).propID;
                silverpropId = propsList.get(1).propID;
            }
            tv_diamond_charge.setText("X" + skillDetail.diamondExpend);
            tv_star_charge.setText("X" + skillDetail.starExpend);
            String diamondExpendRate = "<font color='#ff4064'>" + "+" + skillDetail.diamondSuccessRate / 100 + "%" + "</font>";
            tv_diamond_successful.setText(Html.fromHtml(getString(R.string.success_rate) + diamondExpendRate));
            String starExpendRate = "<font color='#ff4064'>" + "+" + skillDetail.starSuccessRate / 100 + "%" + "</font>";
            Log.e("tag","starExpendRate=="+Html.fromHtml(getString(R.string.success_rate) + starExpendRate));
            tv_star_successful.setText(Html.fromHtml(getString(R.string.success_rate) + starExpendRate));
            tv_gold_charge.setText("X" + skillDetail.goldExpend);
            tv_person_grade.setText(getString(R.string.level_come) + skillDetail.userLevel + getString(R.string.charm_lv));

            baseSuccessRate = skillDetail.baseSuccessRate / 100;
            //gh 技能排行加成返百分比
            rankingSuccessRate = skillDetail.RankingSuccessRate;
            rechargeUpgradeRate = skillDetail.skillSuccessRate;
            moonExpendSelected(lastSelectedProps);
            chargeSelected(lastSelectedCharge);
            calculateSuccessRate(-1);
        }
    }


    /**
     * 升级技能回调
     *
     * @param skillDetail
     */
    @Override
    public void updateSkillResult(SkillDetailBean skillDetail) {
        if (null != skillDetail) {
            EventBus.getDefault().post(new EventSkillLevel(skillDetail.skill_level));
            if (currentSkillLevel == skillDetail.skill_level) {
                updatedSkillDetail = skillDetail;
                CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.skill_update_failure));
                ratingBarView.calculateSegment(currentSkillLevel + 1);
                getAnimView();
                scaleStar(1);
                return;
            }

            if (currentSkillLevel > skillDetail.skill_level) {//升级失败
                CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.skill_update_failure));
                getAnimView();
                ratingBarViewX = ratingBarView.getStarPosition()[0] + ratingBarView.getStarSize()[0];
                endY = getSeatLinePosition()[1] - ratingBarView.getStarPosition()[1] - ratingBarView.getStarSize()[1];
                dropView(1, 1300);
            } else if (currentSkillLevel < skillDetail.skill_level) {
                CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.skill_update_success));
                ratingBarView.calculateSegment(skillDetail.skill_level);
                getAnimView();
                scaleStar(0);
            }
            currentSkillLevel = skillDetail.skill_level;
            updateDetailView(skillDetail);
        }

    }

    private SkillDetailBean updatedSkillDetail;


    /**
     * 获取扩大或者掉落的view
     *
     * @return
     */
    private void getAnimView() {
        int drawable = ratingBarView.getDrawable();

        int[] layoutPostion = getRootViewPostion();
        int[] position = ratingBarView.getStarPosition();
        int x = position[0];//星星在x轴的位置
        int y = position[1] - layoutPostion[1];//星星在y轴的位置

        int[] size = ratingBarView.getStarSize();
        int height = size[0];//星星的高度
        int width = size[1];//星星的宽度

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;

        if (null != anim && anim.isRunning()) {//处理快速点击时，有imageView存在，取消当前动画，状态初始化
            anim.cancel();
            resetAnimValue();
        }

        if (null != imageView) {
            imageView.clearAnimation();
            root_view_rl.removeView(imageView);
            imageView = null;
        }

        imageView = new ImageView(mActivity);
        imageView.setBackgroundResource(drawable);
        imageView.setLayoutParams(layoutParams);
        root_view_rl.addView(imageView);
    }

    /**
     * 初始化状态
     */
    private void resetAnimValue() {
        currentRotate = 0;
        scaleX = 1f;
        scaleY = 1f;
        alpha = 0.4f;
    }

    /**
     * view的扩大分两种情况，0：正常升级成功放大view，1：升级失败，但是等级不会改变时
     *
     * @param failureFlag
     */
    private void scaleStar(final int failureFlag) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 10);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentScaleValue = (int) animation.getAnimatedValue();
                scaleX = scaleX + 0.1f;
                scaleY = scaleY + 0.1f;
                if (currentScaleValue <= 4) {
                    alpha = alpha + 0.1f;
                }
                if (currentScaleValue > 4) {
                    alpha = alpha - 0.15f;
                }

                if (alpha > 1f) {
                    alpha = 1f;
                }

                if (alpha < 0f) {
                    alpha = 0f;
                }
                imageView.setScaleX(scaleX);
                imageView.setScaleY(scaleY);
                imageView.setAlpha(alpha);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scaleX = 1f;
                scaleY = 1f;
                if (null != imageView) {
                    root_view_rl.removeView(imageView);
                }
                if (1 == failureFlag) {
                    getAnimView();
                    ratingBarViewX = ratingBarView.getStarPosition()[0] + ratingBarView.getStarSize()[0];
                    endY = getSeatLinePosition()[1] - ratingBarView.getStarPosition()[1] - ratingBarView.getStarSize()[1];
                    dropView(1, 1300);
                    updateDetailView(updatedSkillDetail);
                }
                tv_skill_level.setText("LV." + currentSkillLevel);
                ratingBarView.setStarView(currentSkillLevel);
            }
        });
    }

    /**
     * 星星掉落动画，利用二阶贝塞尔曲线获取view运行的轨迹，获取轨迹上每个点的坐标，实时更新view的坐标位置
     * 计算规则：轨迹分四段，方法参数@param seat 代表当前的轨迹，startX、startY代表每一段轨迹的起始位置
     * endX、endY代表每一段轨迹的结束位置，controllX、controllY代表每一段轨迹的贝塞尔曲线控制点，控制点controllX
     * 为起始点startX与结束点endX的中间位置，动画的运行利用属性动画获取坐标值来动态更新
     *
     * @param seat     当前第几段轨迹
     * @param duration 每一段轨迹运行的时间
     */
    private void dropView(final int seat, int duration) {
        if (null != anim && 1 == seat) {
            anim.cancel();
        }
        if (1 == seat) {
            startX = 0;
            startY = 0;
            endX = (screenWith - ratingBarViewX) / 2;
            controllX = endX / 2;
            controllY = -600;
        } else {
            startX = endX;
            startY = endY;
            endX = startX + (screenWith - startX - ratingBarViewX) / 2;
            controllX = startX + (endX - startX) / 2;
            if (2 == seat) {
                controllY = -100;
            } else if (3 == seat) {
                endX = (int) (startX + (screenWith - startX - ratingBarViewX) * 0.65);
                controllY = 200;
            } else if (4 == seat) {
                endX = screenWith - ratingBarViewX + 100;
                controllY = endY - 80;
            }
        }

        Point startPoint = new Point(startX, startY);
        Point endPoint = new Point(endX, endY);
        Point controllPoint = new Point(controllX, controllY);
        anim = ValueAnimator.ofObject(new PointEvaluator(controllPoint), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                imageView.setTranslationX(currentPoint.getX());
                imageView.setTranslationY(currentPoint.getY());

                currentRotate = currentRotate + 7;
                if (currentRotate >= 360) {
                    currentRotate = 0;
                }
                imageView.setRotation(currentRotate);
                if (1 == seat) {
                    if (currentPoint.getX() <= controllX) {
                        scaleX = scaleX + 0.04f;
                        scaleY = scaleY + 0.04f;
                    } else {
                        scaleX = scaleX - 0.1f;
                        scaleY = scaleY - 0.1f;
                    }

                    if (scaleX < 1f) {
                        scaleX = 1f;
                    }
                    if (scaleY < 1f) {
                        scaleY = 1f;
                    }
                    imageView.setScaleX(scaleX);
                    imageView.setScaleY(scaleY);
                }
                if (1 == seat && (endX - currentPoint.getX()) <= 3) {
                    dropView(2, 800);
                }

                if (2 == seat && (endX - currentPoint.getX()) <= 3) {
                    dropView(3, 400);
                }

                if (3 == seat && (endX - currentPoint.getX()) <= 3) {
                    dropView(4, 100);
                }
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (1 == seat) {
                    tv_skill_level.setText("LV." + currentSkillLevel);
                    ratingBarView.setStarView(currentSkillLevel);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (null != imageView) {
                    root_view_rl.removeView(imageView);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (4 == seat && null != imageView) {
                    root_view_rl.removeView(imageView);
                }
            }
        });
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
    }


    private int[] getSeatLinePosition() {
        int[] position = new int[2];
        seat_line.getLocationInWindow(position);
        return position;
    }

    private int[] getRootViewPostion() {
        int[] position = new int[2];
        root_view_rl.getLocationInWindow(position);
        return position;
    }

    /**
     * 计算升级成功率，当选中不同的条目时，计算成功率，包含页面数据重载时的计算
     *
     * @param rlId 选中的条目id，传入-1代表默认初始化
     */
    private void calculateSuccessRate(int rlId) {
        if (null != mSkillDetail) {
            switch (rlId) {
                case R.id.moon_gold_rl:
                    propsSuccessRate = propsUpdateSuccessRate;
                    break;
                case R.id.moon_silver_rl:
                    propsSuccessRate = 0;
                    break;
                case R.id.diamond_charge_rl:
                    diamondSuccessRate = mSkillDetail.diamondSuccessRate / 100;
                    starSuccessRate = 0;
                    break;
                case R.id.gold_charge_rl:
                    diamondSuccessRate = 0;
                    starSuccessRate = 0;
                    break;
                case R.id.star_charge_rl:
                    starSuccessRate = mSkillDetail.starSuccessRate / 100;
                    diamondSuccessRate = 0;
                    break;
                default:
                    propsSuccessRate = propsUpdateSuccessRate;
                    diamondSuccessRate = mSkillDetail.diamondSuccessRate / 100;
                    break;
            }
        }
        int successRate = 0;
        if (-1 == rlId) {//当传入-1时，此时要确认上一次是否选中有加成功率的道具或者钻石
            moonExpendSelected(lastSelectedProps);
            chargeSelected(lastSelectedCharge);
            if (R.id.moon_gold_rl != lastSelectedProps) {//上一次被选中不是加成功率的道具
                propsSuccessRate = 0;
            }
            if (R.id.diamond_charge_rl != lastSelectedCharge) {//上一次被选中的不是钻石
                diamondSuccessRate = 0;
            }
            successRate = baseSuccessRate + propsSuccessRate + rankingSuccessRate + diamondSuccessRate + rechargeUpgradeRate + starSuccessRate;
        } else {
            successRate = baseSuccessRate + propsSuccessRate + rankingSuccessRate + diamondSuccessRate + rechargeUpgradeRate + starSuccessRate;
        }
        tv_success.setText(successRate + "%");

        if (suceessList.size() > 0)
            suceessList.clear();

        suceessList.add(new SkillDetailSuccessBean(1, baseSuccessRate));

        if (diamondSuccessRate != 0) {//钻石成功率
            suceessList.add(new SkillDetailSuccessBean(3, diamondSuccessRate));
        }
        if (propsSuccessRate != 0) {//道具成功率
            suceessList.add(new SkillDetailSuccessBean(2, propsSuccessRate));
        }
        if (rankingSuccessRate != 0) {//排行榜加成
            suceessList.add(new SkillDetailSuccessBean(4, rankingSuccessRate));
        }

        if (rechargeUpgradeRate != 0) {//充值升级加成
            suceessList.add(new SkillDetailSuccessBean(5, rechargeUpgradeRate));
        }

        if (starSuccessRate != 0) {//星星升级加成
            suceessList.add(new SkillDetailSuccessBean(6, starSuccessRate));
        }


        if (skillDetailSuccessAdapter != null) {
            skillDetailSuccessAdapter.updateData(suceessList);
        }

    }

    private void moonExpendSelected(int rlId) {
        lastSelectedProps = rlId;
        calculateSuccessRate(rlId);
        moon_gold_rl.setBackgroundDrawable(rlId == R.id.moon_gold_rl ? drawable : null);
        moon_silver_rl.setBackgroundDrawable(rlId == R.id.moon_silver_rl ? drawable : null);
        propId = rlId == R.id.moon_gold_rl ? goldPropId : silverpropId;
    }

    private void chargeSelected(int rlId) {
        lastSelectedCharge = rlId;
        calculateSuccessRate(rlId);
        diamond_charge_rl.setBackgroundDrawable(rlId == R.id.diamond_charge_rl ? drawable : null);
        gold_charge_rl.setBackgroundDrawable(rlId == R.id.gold_charge_rl ? drawable : null);
        star_charge_rl.setBackgroundDrawable(rlId == R.id.star_charge_rl ? drawable : null);
        switch (rlId){
            case R.id.diamond_charge_rl:
                currencyType = "2";
                break;
            case R.id.gold_charge_rl:
                currencyType = "1";
                break;
            case R.id.star_charge_rl:
                currencyType = "6";
                break;
        }
    }


    @Override
    public void showLoading() {
        showWaitDialog();
    }

    @Override
    public void hideLoading() {
        hideWaitDialog();
    }

    @Override
    public boolean isActive() {
        return isLive();
    }

    /**
     * 处理道具商店购买成功EventBus事件
     *
     * @param result
     */
    @Subscribe
    public void receivePropsResult(PropsShopBuySuccess result) {
        if (null == result) {
            return;
        }

        if (null == result.user) {
            return;
        }

        int prop1Num = 0;
        int prop2Num = 0;

        tv_diamond_num.setText(getString(R.string.diamond_balance) + result.user.DiamondNum);
        tv_gold_num.setText(getString(R.string.gold_balance) + result.user.GoldNum);
        tv_star_num.setText(getString(R.string.stars_m) + result.user.StarNum);
        List<PropsShopBuySuccess.PropsItemBuySuccess> list = result.list;
        if (null != list && list.size() > 0) {
            for (PropsShopBuySuccess.PropsItemBuySuccess item : list) {
                if (item.PropsID.equals(props1.propID)) {
                    prop1Num = prop1Num + item.Num;
                } else if (item.PropsID.equals(props2.propID)) {
                    prop2Num = prop2Num + item.Num;
                }
            }
        }

        if (0 != prop1Num) {
            String moonGoldColor = getColor(prop1Num, props1.propExpend);
            String moonGoldExpend = "<font color='" + moonGoldColor + "'>" + prop1Num + "/" + "</font>" + props1.propExpend;
            tv_moon_gold_expend.setText(Html.fromHtml(moonGoldExpend));
        }
        if (0 != prop2Num) {
            String moonSilverColor = getColor(prop2Num, props2.propExpend);
            String moonSilverExpend = "<font color='" + moonSilverColor + "'>" + prop2Num + "/" + "</font>" + props2.propExpend;
            tv_moon_silver_expend.setText(Html.fromHtml(moonSilverExpend));
        }
    }

    private String getColor(int total, int expend) {
        String color = "#7ed321";
        if (total < expend) {
            color = "#d0021b";
        }
        return color;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != anim) {
            anim.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SkillDetailFragment.this);
    }

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_user_head)
    HeadPhotoView iv_user_head;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_diamond_num)
    TextView tv_diamond_num;
    @BindView(R.id.tv_gold_num)
    TextView tv_star_num;
    @BindView(R.id.tv_star_num)
    TextView tv_gold_num;
    @BindView(R.id.skill_icon)
    ImageView skill_icon;
    @BindView(R.id.skill_icon_first)
    ImageView skill_icon_first;
    @BindView(R.id.skill_name)
    TextView skill_name;
    @BindView(R.id.skill_name_icon_below)
    TextView skill_name_icon_below;
    @BindView(R.id.ratingBarView)
    RatingBarView ratingBarView;
    @BindView(R.id.tv_current_effect_desc)
    TextView tv_current_effect_desc;
    @BindView(R.id.tv_update_effect_desc)
    TextView tv_update_effect_desc;
    @BindView(R.id.pb_proficiency)
    ProgressBar pb_proficiency;
    @BindView(R.id.tv_proficiency_desc)
    TextView tv_proficiency_desc;
    @BindView(R.id.iv_prop)
    ImageView iv_prop;
    @BindView(R.id.tv_success)
    TextView tv_success;
    @BindView(R.id.tv_moon_gold_expend)
    TextView tv_moon_gold_expend;
    @BindView(R.id.tv_moon_gold_successful)
    TextView tv_moon_gold_successful;
    @BindView(R.id.tv_moon_silver_expend)
    TextView tv_moon_silver_expend;
    @BindView(R.id.tv_diamond_charge)
    TextView tv_diamond_charge;
    @BindView(R.id.tv_star_charge)
    TextView tv_star_charge;
    @BindView(R.id.tv_diamond_successful)
    TextView tv_diamond_successful;
    @BindView(R.id.tv_star_successful)
    TextView tv_star_successful;
    @BindView(R.id.tv_gold_charge)
    TextView tv_gold_charge;
    @BindView(R.id.tv_skill_level)
    TextView tv_skill_level;
    //    @BindView(R.id.tv_base_success) TextView tv_base_success;
//    @BindView(R.id.tv_acer_success) TextView tv_acer_success;
//    @BindView(R.id.tv_ranking) TextView tv_ranking;
//    @BindView(R.id.tv_diamond_success_rate) TextView tv_diamond_success_rate;
//    @BindView(R.id.tv_recharge_upgrade) TextView tv_recharge_upgrade;
    @BindView(R.id.tv_person_grade)
    TextView tv_person_grade;
    @BindView(R.id.moon_gold_rl)
    RelativeLayout moon_gold_rl;
    @BindView(R.id.moon_silver_rl)
    RelativeLayout moon_silver_rl;
    @BindView(R.id.diamond_charge_rl)
    RelativeLayout diamond_charge_rl;
    @BindView(R.id.star_charge_rl)
    RelativeLayout star_charge_rl;
    @BindView(R.id.gold_charge_rl)
    RelativeLayout gold_charge_rl;
    @BindView(R.id.root_view_rl)
    RelativeLayout root_view_rl;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.seat_line)
    View seat_line;
    @BindView(R.id.gv_success_list)
    MyGridView gv_success_list;
}
