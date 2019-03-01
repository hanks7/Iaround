package net.iaround.ui.view.pipeline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.model.entity.Item;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.worldmsg.WorldMessageActivity;
import net.iaround.utils.SkillHandleUtils;
import net.iaround.utils.ScreenUtils;


/**
 * Created by Ray on 2017/8/17.
 */

public class PipelineWorldMessageView extends LinearLayout implements View.OnClickListener {


    private int defaultMarginTop = 10;//view默认距上的距离
    private ObjectAnimator translationXAnimator;

    private HeadPhotoView hpWorldMessageAvatar;//头像
    private TextView tvWorldMessageName;//名字
    private RichTextView tvWorldMessageInfo;//内容
    public boolean isShow;

    private TextView tvWorkdTitle;//顶部世界消息显示称号
    private TextView tvWorldRecuit;//世界消息显示招募
    private TextView tvWorldRecuitBroadCast;//世界消息招募广播
    private ImageView ivWorldSkillAnimtion;//播放技能动画
    private RelativeLayout rlWorldMessageInfo;//点击进入世界消息列表
    private UserTitleView userTitleView;//称号
//    private CustomChatBarDialog mCustomChatBarDialog;//聊吧弹框
    private LinearLayout messageRootView;


    public PipelineWorldMessageView(Context context) {
        this(context, null);
    }

    public PipelineWorldMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PipelineWorldMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    public void setIsShow(boolean isShowing){
        isShow = isShowing;
    }

    private void initView(final Context context) {
        setVisibility(GONE);
        setOrientation(HORIZONTAL);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);

        LinearLayout welcomeView = (LinearLayout) inflate(getContext(), R.layout.chat_bar_world_meaage_head, null);
        LinearLayout.LayoutParams lpw = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        welcomeView.setLayoutParams(lpw);

        hpWorldMessageAvatar = (HeadPhotoView) welcomeView.findViewById(R.id.hp_world_message_avastar);
        tvWorldMessageName = (TextView) welcomeView.findViewById(R.id.tv_chat_bar_world_message_name);
        tvWorldMessageInfo = (RichTextView) welcomeView.findViewById(R.id.tv_chat_bar_world_message_info);
        tvWorkdTitle = (TextView) welcomeView.findViewById(R.id.tv_chat_bar_workd_title);
        tvWorldRecuit = (TextView) welcomeView.findViewById(R.id.tv_chat_bar_workd_recuit);
        tvWorldRecuitBroadCast = (TextView) welcomeView.findViewById(R.id.chat_bar_world_recuit_broad_cast);
        ivWorldSkillAnimtion = (ImageView) welcomeView.findViewById(R.id.iv_world_message_skill_avastar);
        rlWorldMessageInfo = (RelativeLayout) welcomeView.findViewById(R.id.rl_world_message_info);
        userTitleView = (UserTitleView) welcomeView.findViewById(R.id.user_title_world);
        tvWorldRecuit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.toastMsg(context,"recuit");
            }
        });

        addView(welcomeView);
        messageRootView = welcomeView;
    }

    /**
     * view的位移动画，从屏幕左侧移动到当前屏幕
     *
     * @param duration
     */
    public void translationView(long duration) {
        setVisibility(VISIBLE);
        int width = getWidth();
        int sreenWidth = CommonFunction.getScreenPixWidth(BaseApplication.appContext);
        if (width == 0) {
            width = ScreenUtils.dp2px(210);
        }

        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", sreenWidth, 0);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        if (null == translationXAnimator) {
            translationXAnimator = ObjectAnimator.ofPropertyValuesHolder(this, translationX, alpha);
        }
        translationXAnimator.setDuration(duration);
        translationXAnimator.cancel();
        translationXAnimator.start();
        translationXAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

//                postDelayed(welcomeRunnable,3000);
            }
        });


//        this.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                alphaGoneView(PipelineWelcomeView.this, 1000);
//            }
//        },1000);
    }
    public void setWorldMessageInfo(String worldMessageName){
        tvWorldMessageName.setText(worldMessageName);

    }

    public void setWorldMessageInfo(final Context context, final User user, String messageInfo, Item item, int recuit, int currentRole, int messageType, final WorldMessageRecord contentBean, final String groupId, final String groupName, final OnHeadViewFromWorldClickListener listener){
        if(user != null){

            SpannableString spNameFirst = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, user.getNickname().toString(),
                    16, null);
            tvWorldMessageName.setText(spNameFirst + "");
            if(messageInfo!=null) {
                tvWorldMessageInfo.setText(messageInfo.toString());
            }
            tvWorldMessageInfo.parseIconWorld();
//            hpWorldMessageAvatar.execute(ChatFromType.UNKONW, user, null);
            if(null!=user) {
                hpWorldMessageAvatar.execute(user, 0);
            }

            hpWorldMessageAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  listener.headViewWorldMessageClick(user);
                }
            });

            if(item != null){
//                RankingTitleUtil.getInstance().handleReallyRank(item.key,item.value,tvWorkdTitle);
                userTitleView.setTitleText(item);
            }
            if(recuit == 0){
                tvWorldRecuit.setVisibility(GONE);
            }else {
                if(0 == currentRole || 1 == currentRole){//自己是吧主或者管理员
                    tvWorldRecuit.setVisibility(VISIBLE);
                }else {
                    tvWorldRecuit.setVisibility(GONE);
                }

            }
            rlWorldMessageInfo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorldMessageActivity.class);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra("cuurentInde",0);
                    context.startActivity(intent);

                }
            });
            if(messageType == 31){//招募
                ivWorldSkillAnimtion.setVisibility(GONE);
                tvWorldRecuitBroadCast.setVisibility(VISIBLE);
                tvWorldRecuitBroadCast.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(context != null){
                            final Activity activity = CloseAllActivity.getInstance().getTopActivity();
                            if (activity instanceof GroupChatTopicActivity){
                                if(null!=contentBean&&contentBean.recruitContent.groupid.equals(groupId)){
                                    return;
                                }
                                ((GroupChatTopicActivity) activity).instant.isGroupIn = true;
                                ((GroupChatTopicActivity) activity).instant.finish();
                                if(null!=contentBean) {
                                    Intent intent = new Intent(context, GroupChatTopicActivity.class);
                                    intent.putExtra("id", contentBean.recruitContent.groupid + "");
                                    intent.putExtra("isChat", true);
                                    activity.startActivity(intent);
                                }

                            }
                        }
                    }
                });

            }else if(messageType == 33){
                tvWorldRecuitBroadCast.setVisibility(GONE);
                ivWorldSkillAnimtion.setVisibility(VISIBLE);
                //技能动图
                SkillHandleUtils.playFrame(BaseApplication.appContext,contentBean.skillContent.gif,ivWorldSkillAnimtion);

            }else {
                ivWorldSkillAnimtion.setVisibility(GONE);
                tvWorldRecuitBroadCast.setVisibility(GONE);
                //动画关掉
            }

        }

    }

    public void translationViewRevert(long duration) {
        setVisibility(VISIBLE);
        int width = getWidth();
        int sreenWidth = CommonFunction.getScreenPixWidth(BaseApplication.appContext);
        if (width == 0) {
            width = ScreenUtils.dp2px(210);
        }

        //移动
        ObjectAnimator translationXAnimatorenter = ObjectAnimator.ofFloat(PipelineWorldMessageView.this, "translationX", sreenWidth,0);
        ObjectAnimator translationXAnimatorout = ObjectAnimator.ofFloat(PipelineWorldMessageView.this, "translationX", 0,-sreenWidth);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(translationXAnimatorout);
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    private Runnable welcomeRunnable = new Runnable() {
        @Override
        public void run() {
            alphaGoneView(PipelineWorldMessageView.this, 1000);
        }
    };

    public void removeCallBacks(){
        this.removeCallbacks(welcomeRunnable);
    }


    /**
     * 消失时候的渐变动画
     *
     * @param view
     * @param duration
     */

    private void alphaGoneView(View view, long duration) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        alpha.setDuration(duration);
        alpha.start();
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setVisibility(VISIBLE);
            }
        });
    }

    /**
     * 设置欢迎信息
     */
    public void setWelcomeInfo(User currentUser) {
        refreshView(currentUser);
    }

    /**
     * 刷新view
     */

    private void refreshView(User currentUser) {

    }


    /**
     * 设置当前view在屏幕上下的位置
     *
     * @param heightPosition
     */
    public void setHeightPosition(int heightPosition) {
        if (heightPosition <= 0) {
            heightPosition = defaultMarginTop;
        }
        heightPosition = ScreenUtils.dp2px(heightPosition);
        MarginLayoutParams margin = new MarginLayoutParams(getLayoutParams());
        margin.setMargins(margin.leftMargin, heightPosition, margin.rightMargin, margin.bottomMargin);
        LayoutParams params = new LayoutParams(margin);
        setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_bar_world_recuit_broad_cast:
                //跳转到当前发送招募广播聊吧
                break;
        }

    }
}
