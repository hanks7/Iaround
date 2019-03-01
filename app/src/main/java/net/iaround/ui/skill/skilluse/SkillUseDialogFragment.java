package net.iaround.ui.skill.skilluse;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.skill.SkillOpenBean;
import net.iaround.model.skill.SkillUsedInfoBean;
import net.iaround.model.skill.SkillUsedItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.skill.FullyGridLayoutManager;
import net.iaround.ui.skill.RecycleItemClickListener;
import net.iaround.ui.view.RatingBarView;
import net.iaround.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：zx on 2017/8/14 20:51
 */
public class SkillUseDialogFragment extends DialogFragment implements SkillUseContract.View, RecycleItemClickListener<SkillUsedItemBean>, DialogInterface.OnKeyListener {


    @BindView(R.id.btn_close)
    ImageView btn_close;
    @BindView(R.id.skill_use_recyclerView)
    RecyclerView skillUseRecyclerView;
    @BindView(R.id.progressBar_others)
    ProgressBar progressBarOthers;
    @BindView(R.id.progressBar_self)
    ProgressBar progressBarSelf;
    @BindView(R.id.ratingBarView_others)
    RatingBarView ratingBarView_others;
    @BindView(R.id.ratingBarView_self)
    RatingBarView ratingBarView_self;
    @BindView(R.id.tv_skill_effect)
    TextView tv_skill_effect;
    @BindView(R.id.iv_prop)
    ImageView iv_prop;
    @BindView(R.id.tv_moon_expend)
    TextView tv_moon_expend;
    @BindView(R.id.tv_gold_expend)
    TextView tv_gold_expend;
    @BindView(R.id.tv_diamond_expend)
    TextView tv_diamond_expend;
    @BindView(R.id.tv_star_expend)
    TextView tv_star_expend;
    @BindView(R.id.tv_hit_rate)
    TextView tv_hit_rate;
    @BindView(R.id.btn_use_skill)
    Button btn_use_skill;
    @BindView(R.id.skill_use_ll)
    LinearLayout skill_use_ll;
    @BindView(R.id.props_rl)
    RelativeLayout props_rl;
    @BindView(R.id.gold_rl)
    RelativeLayout gold_rl;
    @BindView(R.id.diamond_rl)
    RelativeLayout diamond_rl;
    @BindView(R.id.star_rl)
    RelativeLayout star_rl;
    private Dialog mWaitDialog = null;

    private int screenWidth;
    private int goldTotalNum = 0;
    private int diamondTotalNum = 0;
    private int starTotalNum = 0;
    private int propTotalNum = 0;
    private int propExpend = 0;
    private int totalHitRate;//当前命中率
    private int selftProgress = 1;//自己progressBar进度值
    private int otherProgress = 1;//他人progressBar进度值
    private float maxLevel = 0;//PK双方最大等级
    private SkillPkTimer pkTimer;

    private TextView tv_selfLevel;
    private TextView tv_otherLevel;
    private PopupWindow selfpopupWindow;
    private PopupWindow ohterpopupWindow;
    private Unbinder unbinder;
    private SkillUseContract.Presenter mPresenter;
    private SkillUseAdapter adapter;
    private String targetUserId;
    private String groupId;
    private String skillId;
    private String selectedSkillPropsId;//当前选中技能下的道具id
    private String propsId;//选择的道具ID
    private String currencyType;//代币类型 2钻石 1金币;
    private int lastSelectExpendRlId = -1;
    private int currentSelectedItem = 0;
    private boolean mTimerDestroy = false;
    private Object mTimerDestroyLock = new Object();


    public static SkillUseDialogFragment getInstance(Bundle bundle) {
        SkillUseDialogFragment fragment = new SkillUseDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = initDialog();
        unbinder = ButterKnife.bind(this, dialog);
        initView();
        getBundles();
        if (null != mPresenter)
            mPresenter.onAttach();
        if (null != mPresenter)
            mPresenter.getSkillUsedInfo(targetUserId);
        return dialog;
    }

    private void getBundles() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            targetUserId = bundle.getString("targetUserId");
            groupId = bundle.getString("groupId");
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_use_skill, R.id.props_rl, R.id.gold_rl, R.id.diamond_rl, R.id.star_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                backSkillDialog();
                dismiss();
                break;
            case R.id.btn_use_skill:
                mPresenter.skillAttack(targetUserId, skillId, groupId, currencyType, propsId);
                break;
            case R.id.props_rl:
                expendSelected(R.id.props_rl);
                break;
            case R.id.gold_rl:
                expendSelected(R.id.gold_rl);
                break;
            case R.id.diamond_rl:
                expendSelected(R.id.diamond_rl);
                break;
            case R.id.star_rl:
                expendSelected(R.id.star_rl);
                break;
        }
    }

    private int openItemPosition;

    /**
     * 条目点击事件
     *
     * @param itemBean
     */
    @Override
    public void onItemClick(int position, SkillUsedItemBean itemBean) {
        if (null != itemBean) {
            switch (itemBean.status) {
                case 0:
                    openItemPosition = position;
                    mPresenter.openSkill(itemBean);
                    break;
                case 1://更新ui
                    currentSelectedItem = position;
                    adapter.setmSelectedItem(position);
                    updateItemUI(itemBean);
                    break;
            }

        }
    }

    private void updateItemUI(SkillUsedItemBean itemBean) {

        skillId = itemBean.skillId;
        totalHitRate = itemBean.hitRate;
        List<SkillUsedItemBean.Props> props = itemBean.props;
        if (null != props && props.size() > 0) {
            GlideUtil.loadImage(BaseApplication.appContext, props.get(0).icon, iv_prop);
            propExpend = itemBean.props.get(0).needNum;
            propTotalNum = itemBean.props.get(0).num;
            selectedSkillPropsId = itemBean.props.get(0).id;
        }

        ratingBarView_self.setStarView(itemBean.mySkillLevel);
        ratingBarView_others.setStarView(itemBean.otherSkillLevel);
        tv_skill_effect.setText(CommonFunction.getLangText(itemBean.skillDesc));

        String color = getColor(propTotalNum, propExpend);
        String propExpends = "<font color='" + color + "'>" + propTotalNum + "/" + "</font>" + propExpend;
        tv_moon_expend.setText(Html.fromHtml(propExpends));

        color = getColor(diamondTotalNum, itemBean.diamondExp);
        String diamondExpend = "<font color='" + color + "'>" + diamondTotalNum + "/" + "</font>" + itemBean.diamondExp;
        tv_diamond_expend.setText(Html.fromHtml(diamondExpend));

        color = getColor(goldTotalNum, itemBean.goldExp);
        String goldExpend = "<font color='" + color + "'>" + goldTotalNum + "/" + "</font>" + itemBean.goldExp;
        tv_gold_expend.setText(Html.fromHtml(goldExpend));

        color = getColor(starTotalNum, itemBean.starExp);
        String starExpend = "<font color='" + color + "'>" + starTotalNum + "/" + "</font>" + itemBean.starExp;
        tv_star_expend.setText(Html.fromHtml(starExpend));

        tv_selfLevel.setText(itemBean.mySkillLevel + getString(R.string.charm_lv));
        tv_otherLevel.setText(itemBean.otherSkillLevel + getString(R.string.charm_lv));

        showProgressBarValue(itemBean.mySkillLevel, itemBean.otherSkillLevel);
        expendSelected(lastSelectExpendRlId);
    }

    private String getColor(int total, int expend) {
        String color = "#7ed321";
        if (total < expend) {
            color = "#d0021b";
        }
        return color;
    }

    /**
     * 首次进入页面更新页面数据
     *
     * @param usedBean
     */
    @Override
    public void showSkillUsedInfo(SkillUsedInfoBean usedBean) {
        if (null == usedBean) {
            return;
        }
        List<SkillUsedItemBean> skillUsedList = usedBean.skillList;
        adapter.updateList(skillUsedList);
        goldTotalNum = usedBean.gold;
        diamondTotalNum = usedBean.diamond;
        starTotalNum = usedBean.star;
        if (null != skillUsedList && skillUsedList.size() > 0) {
            for (SkillUsedItemBean itemBean : skillUsedList) {
                if (1 == itemBean.status) {
                    int indexOf = skillUsedList.indexOf(itemBean);
                    adapter.setmSelectedItem(indexOf);
                    onItemClick(indexOf, itemBean);//默认选中
                    break;
                }
            }
        }
    }

    /**
     * 处理技能攻击结果
     *
     * @param attackResult
     */
    @Override
    public void handleAttackResult(SkillAttackResult attackResult) {
        if (null == attackResult) {
            CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.attack_failure));
            return;
        }
        if (1 == attackResult.isHit) {
            CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.attack_success));
        } else {
            if (attackResult.skillId == 2) {
                CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.attack_success));
            } else {
                CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.attack_failure));
            }
        }
        goldTotalNum = attackResult.goldNum;
        diamondTotalNum = attackResult.diamondNum;
        starTotalNum = attackResult.starNum;
        //技能攻击结束更新页面UI
        onItemClick(currentSelectedItem, adapter.updatePositionView(currentSelectedItem, attackResult));

    }

    @Override
    public void updateSkillOpenStatus(SkillUsedItemBean itemBean, SkillOpenBean.SkillOpenItem skill) {
        itemBean.status = skill.Status;
        itemBean.mySkillLevel = skill.Level;
        onItemClick(openItemPosition, itemBean);
    }

    /**
     * 显示progressBar
     *
     * @param selfLevel
     * @param otherLevel
     */
    private void showProgressBarValue(int selfLevel, int otherLevel) {
        if (null != selfpopupWindow) {
            selfpopupWindow.dismiss();
        }
        if (null != ohterpopupWindow) {
            ohterpopupWindow.dismiss();
        }
        maxLevel = calculateMaxLevel(selfLevel, otherLevel);
        selftProgress = (int) ((selfLevel / maxLevel) * 100);
        otherProgress = (int) ((otherLevel / maxLevel) * 100);
        resizeProgress();
        progressBarSelf.setMax(100);
        progressBarOthers.setMax(100);
        progressBarSelf.setProgress(0);
        progressBarOthers.setProgress(0);
        if (null != pkTimer) {
            pkTimer.cancel();
            pkTimer.start();
        }
    }

    /**
     *
     */
    private void resizeProgress() {
        if (selftProgress > otherProgress) {
            float proportion = (float) selftProgress / otherProgress;
            if (proportion > 5 && proportion < 10) {
                otherProgress = otherProgress * 2;
            } else if (proportion >= 10 && proportion < 20) {
                otherProgress = otherProgress * 5;
            } else if (proportion >= 20 && proportion < 30) {
                otherProgress = otherProgress * 10;
            } else if (proportion >= 30) {
                otherProgress = otherProgress * 15;
            }
        } else if (selftProgress < otherProgress) {
            float proportion = (float) otherProgress / selftProgress;
            if (proportion > 5 && proportion < 10) {
                selftProgress = selftProgress * 2;
            } else if (proportion >= 10 && proportion < 20) {
                selftProgress = selftProgress * 5;
            } else if (proportion >= 20 && proportion < 30) {
                selftProgress = selftProgress * 10;
            } else if (proportion >= 30) {
                selftProgress = selftProgress * 15;
            }
        }
    }

    /**
     * 计算PK双方的最大等级，返回值为双方的最大值*0.8
     *
     * @param selfLevel
     * @param otherLevel
     * @return
     */
    private float calculateMaxLevel(int selfLevel, int otherLevel) {
        if (0 == selfLevel && 0 == otherLevel) {
            return 1;
        }
        if (60 == selfLevel || 60 == otherLevel) {
            return 60;
        }
        if (selfLevel == otherLevel) {
            return (float) (selfLevel / 0.5);
        }

        int max = Math.max(selfLevel, otherLevel);
        if (0 != max) {
            return (float) (max / 0.8);
        }
        return 0;
    }

    /**
     * 显示等级文本
     */
    private void showSkillLevelText() {
        if (!SkillUseDialogFragment.this.isActive()) {
            return;
        }
        float selfRate = selftProgress * 1.0f / progressBarSelf.getMax();
        float otherRate = otherProgress * 1.0f / progressBarOthers.getMax();

        if (null == selfpopupWindow) {
            selfpopupWindow = new PopupWindow(tv_selfLevel, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int selfPosX = (int) ((screenWidth / 2 - ScreenUtils.dp2px(34)) * selfRate) - ScreenUtils.dp2px(26);

        selfpopupWindow.showAsDropDown(progressBarSelf, selfPosX, ScreenUtils.dp2px(3));

        if (null == ohterpopupWindow) {
            ohterpopupWindow = new PopupWindow(tv_otherLevel, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int otherPosX = (int) ((screenWidth / 2 - ScreenUtils.dp2px(35)) * (1 - otherRate));
        ohterpopupWindow.showAsDropDown(progressBarOthers, otherPosX, ScreenUtils.dp2px(3));
    }


    private void expendSelected(int rlId) {
        lastSelectExpendRlId = rlId;
        props_rl.setBackgroundDrawable(R.id.props_rl == rlId ? drawable : null);
        gold_rl.setBackgroundDrawable(R.id.gold_rl == rlId ? drawable : null);
        diamond_rl.setBackgroundDrawable(R.id.diamond_rl == rlId ? drawable : null);
        star_rl.setBackgroundDrawable(R.id.star_rl == rlId ? drawable : null);
        propsId = R.id.props_rl == rlId ? selectedSkillPropsId : "";
        if (R.id.star_rl == rlId) {
            currencyType = "6";
        } else if (R.id.diamond_rl == rlId) {
            currencyType = "2";
        } else if (R.id.gold_rl == rlId) {
            currencyType = "1";
        } else {
            currencyType = "0";
        }
    }

    @Override
    public void setPresenter(SkillUseContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogUtil.getProgressDialog(getActivity(), getString(R.string.dialog_title), getString(R.string.please_wait), null);
        }
        mWaitDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mWaitDialog != null) {
            if (mWaitDialog.isShowing()) {
                mWaitDialog.hide();
            }
        }
    }

    @Override
    public boolean isActive() {
        if (!isAdded()) {
            return false;
        }
        if (getDialog() == null) {
            return false;
        }
        return getDialog().isShowing();
    }

    /**
     * 初始化dialog
     *
     * @return
     */
    private Dialog initDialog() {
        Dialog dialog = new Dialog(getActivity(), R.style.skill_use_dialog_animate);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(R.layout.dialog_use_skill);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false); //外部点击取消
        dialog.setOnKeyListener(this);//返回监听

        //设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; //紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; //宽度持平
        window.setAttributes(lp);

        return dialog;
    }

    private Drawable drawable;

    /**
     * 初始化当前view
     */
    private void initView() {
        drawable = getResources().getDrawable(R.drawable.icon_skill_selected);
        lastSelectExpendRlId = R.id.gold_rl;
        pkTimer = new SkillPkTimer(this, 1000, 10);
        screenWidth = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        tv_selfLevel = (TextView) LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.pop_item_text_view, null);
        tv_otherLevel = (TextView) LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.pop_item_text_view, null);

        Typeface typeface = Typeface.createFromAsset(BaseApplication.appContext.getAssets(), "DINCond-Black.otf");
        tv_hit_rate.setTypeface(typeface);
        tv_moon_expend.setTypeface(typeface);
        tv_gold_expend.setTypeface(typeface);
        tv_diamond_expend.setTypeface(typeface);
        tv_star_expend.setTypeface(typeface);
        tv_selfLevel.setTypeface(typeface);
        tv_otherLevel.setTypeface(typeface);

        adapter = new SkillUseAdapter(this);
        skillUseRecyclerView.setHasFixedSize(true);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(BaseApplication.appContext, 3);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        manager.setSmoothScrollbarEnabled(true);
        manager.canScrollVertically();
        skillUseRecyclerView.setLayoutManager(manager);
        skillUseRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        synchronized (mTimerDestroyLock) {
            if (null != pkTimer) {
                pkTimer.cancel();
                pkTimer = null;
            }
            mTimerDestroy = true;
            CommonFunction.log("SkillUseDialogFragment", "onStop() timer destroy");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        synchronized (mTimerDestroyLock) {
            if (null != pkTimer) {
                pkTimer.cancel();
                pkTimer = null;
            }
            mTimerDestroy = true;
            CommonFunction.log("SkillUseDialogFragment", "onDestroyView() timer destroy");
        }
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
        if (null != mPresenter) {
            mPresenter.onDettach();
            mPresenter = null;
        }
        if (null != selfpopupWindow) {
            selfpopupWindow.dismiss();
            selfpopupWindow = null;
        }
        if (null != ohterpopupWindow) {
            ohterpopupWindow.dismiss();
            ohterpopupWindow = null;
        }
    }

    private void onTimerFinish() {
        synchronized (mTimerDestroyLock) {
            if (mTimerDestroy == true) {
                CommonFunction.log("SkillUseDialogFragment", "SkillPkTimer onFinish() timer destroy");
                return;
            }
            Activity activity = getActivity();
            if (CommonFunction.activityIsDestroyed(activity) == false) {
                progressBarSelf.setProgress(selftProgress);
                progressBarOthers.setProgress(otherProgress);
                tv_hit_rate.setText(totalHitRate + "%");
                if (isDetached() == false) {
                    showSkillLevelText();
                }
            } else {
                CommonFunction.log("SkillUseDialogFragment", "activity null");
            }
        }
    }

    private void onTimerTick(long arg0) {
        synchronized (mTimerDestroyLock) {
            if (mTimerDestroy == true) {
                CommonFunction.log("SkillUseDialogFragment", "SkillPkTimer onTick() timer destroy");
                return;
            }
            Activity activity = getActivity();
            if (CommonFunction.activityIsDestroyed(activity) == false) {
                int value = (int) (arg0 / 10) - 1;
                int self = selftProgress - value;
                int other = otherProgress - value;

                if (self >= 0) {
                    progressBarSelf.setProgress(self);
                }
                if (other >= 0) {
                    progressBarOthers.setProgress(other);
                }

                int hitRate = totalHitRate - value;
                if (hitRate >= 0) {
                    tv_hit_rate.setText(hitRate + "%");
                }
            } else {
                CommonFunction.log("SkillUseDialogFragment", "activity null");
            }
        }
    }

    private static class SkillPkTimer extends CountDownTimer {
        private WeakReference<SkillUseDialogFragment> mSkillUseDialogFragment;

        public SkillPkTimer(SkillUseDialogFragment fragment, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mSkillUseDialogFragment = new WeakReference<SkillUseDialogFragment>(fragment);
        }

        @Override
        public void onFinish() {
            SkillUseDialogFragment fragment = mSkillUseDialogFragment.get();
            if (fragment != null) {
                fragment.onTimerFinish();
            }
        }

        @Override
        public void onTick(long arg0) {
            SkillUseDialogFragment fragment = mSkillUseDialogFragment.get();
            if (fragment != null) {
                fragment.onTimerTick(arg0);
            }
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backSkillDialog();
            dismiss();
            return true;
        } else {
            //这里注意当不是返回键时需将事件扩散，否则无法处理其他点击事件
            return false;
        }
    }

    /**
     * 使用技能返回监测被攻击消息
     */
    private void backSkillDialog() {
        Common.getInstance().setShowDailog(false);
        EventBus.getDefault().post("update_skill_dialog");
    }
}
