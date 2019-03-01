package net.iaround.ui.view.pipeline;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.utils.ScreenUtils;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ray on 2017/8/17.
 */

public class PipelineWelcomeView extends LinearLayout implements View.OnClickListener {
    static class CaseUser{
        public User user;
        long time; //显示的时间
        public CaseUser(User u, long t){
            this.user = u;
            this.time = t;
        }
    }

    static class GroupHistory{
        String group; //聊吧ID
        long time; //显示的时间
        public GroupHistory(String g, long t){
            this.group = g;
            this.time = t;
        }
    }

    private Context context;
    private int defaultMarginTop = 10;//view默认距上的距离
    private ObjectAnimator translationXAnimator;

    private HeadPhotoView hpWelcomeAvatar;
    private TextView tvWelcomeName;
    private LinearLayout llWelcome;//根据性别改变背景色

    LinkedBlockingQueue<User> mIncomingUsers = null; //当前用户
    LinkedBlockingQueue<CaseUser> mCacheUsers = null; //已经显示过的用户,大概1分钟
    private boolean mShowing = false; // 正在显示欢迎语
    private String mGroupID = null; //聊吧ID
    private static LinkedBlockingQueue<GroupHistory> mGroupHistory = null; //当前登陆用户登陆过的聊吧

    public PipelineWelcomeView(Context context) {
        this(context, null);
    }

    public PipelineWelcomeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PipelineWelcomeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setVisibility(GONE);
        setOrientation(VERTICAL);
        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        View welcomeView = inflate(getContext(), R.layout.pipeline_welcome_join, null);
        llWelcome = (LinearLayout) welcomeView.findViewById(R.id.ll_pipeline_welcome);
        hpWelcomeAvatar = (HeadPhotoView) welcomeView.findViewById(R.id.iv_chat_bar_welcome_avatar);
        tvWelcomeName = (TextView) welcomeView.findViewById(R.id.tv_chat_bar_welcome);

        addView(welcomeView);

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

        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", sreenWidth, width);
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
                postDelayed(welcomeRunnable,3000);
            }
        });
    }
    private Runnable welcomeRunnable = new Runnable() {
        @Override
        public void run() {
            alphaGoneView(PipelineWelcomeView.this, 1000);
        }
    };

    public void removeCallBacks(){
        this.removeCallbacks(welcomeRunnable);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallBacks();
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
                showNext();
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
        this.context = BaseApplication.appContext;

        //this.iconUrl = currentUser.getIcon();
        //this.nickName = currentUser.getNickname();
        refreshView(currentUser,context);
    }

    /**
     * 刷新view
     */

    private void refreshView(User currentUser,Context context) {
        if(2 == currentUser.getSexIndex()){
            llWelcome.setBackgroundResource(R.drawable.gradient_welcome_girle_bg);
        }else {
            llWelcome.setBackgroundResource(R.drawable.gradient_welcome_boy_bg);
        }

        SpannableString spName;
        if(context != null){
            context = BaseApplication.appContext;
            hpWelcomeAvatar.execute(currentUser,context);
            spName = FaceManager.getInstance(context).parseIconForString(context, currentUser.getNickname(), 0, null);
            tvWelcomeName.setText(spName);
        }

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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(margin);
        setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {

    }


    public void handleWelcomeUser(User user, String group){
        CommonFunction.log("PipelineWelcomeView","handleWelcomeUser() into, group=" + group + ", user=" + user.getUid());
        if(null==user || null== group){
            return;
        }
        if(null!=mGroupID && !group.equals(mGroupID)){
            if(mIncomingUsers!=null){
                mIncomingUsers.clear();
            }
            if(mCacheUsers!=null){
                mCacheUsers.clear();
            }
        }
        if(mGroupID!=group) {
            mGroupID = group;
        }
        if( null == mIncomingUsers ){
            mIncomingUsers = new LinkedBlockingQueue<User>(3);
        }
        if( null == mCacheUsers ){
            mCacheUsers = new LinkedBlockingQueue<CaseUser>(15); // 15* 4 seconds = 60 s
        }
        if( null == mGroupHistory){
            mGroupHistory = new LinkedBlockingQueue<GroupHistory>(10);
        }

        if(false == mIncomingUsers.offer(user)){
            mIncomingUsers.poll();
            mIncomingUsers.offer(user);
        }

        if(mShowing == false) {
            User current = mIncomingUsers.poll();
            if(null!=current) {
                long nowTime = System.currentTimeMillis();
                //用户是否是当前登陆用户且一分钟之内进过聊吧
                if(current.getUid() == Common.getInstance().loginUser.getUid()){
                    Iterator<GroupHistory> historys = mGroupHistory.iterator();
                    while (historys.hasNext()){
                        GroupHistory history = historys.next();
                        if(history.group.equals(group) &&
                                (nowTime - history.time)<=60000 ){
                            return;
                        }
                    }
                    GroupHistory g = new GroupHistory(group, nowTime);
                    if(false == mGroupHistory.offer(g)){
                        mGroupHistory.poll();
                        mGroupHistory.offer(g);
                    }

                }
                //用户是否1分钟之内
                boolean find = false, show = false;
                Iterator<CaseUser> caches = mCacheUsers.iterator();
                while (caches.hasNext()){
                    CaseUser cacheUser = caches.next();
                    if(cacheUser.user.getUid() == current.getUid()){
                        find = true;
                        if((nowTime - cacheUser.time)> 60000 ) {
                            show = true;
                            caches.remove();
                        }
                    }
                }
                if( false == find || (find==true && show == true) ) {
                    mShowing = true;
                    CaseUser newCache = new CaseUser(current, nowTime);
                    if( false == mCacheUsers.offer(newCache) ){
                        mCacheUsers.poll();
                        mCacheUsers.offer(newCache);
                    }
                    CommonFunction.log("PipelineWelcomeView","handleWelcomeUser() show welcome user, user=" + current.getUid());
                    showWelcome(current);
                }
            }
        }
    }

    private void showWelcome(User user){
        this.removeCallBacks();
        this.clearAnimation();
        this.setWelcomeInfo(user);
        this.translationView(300);
    }

    private void showNext(){
        //CommonFunction.log("PipelineWelcomeView","showNext() into");
        User current = mIncomingUsers.poll();
        if(null!=current) {
            long nowTime = System.currentTimeMillis();
            //用户是否是当前登陆用户且一分钟之内进过聊吧
            if(current.getUid() == Common.getInstance().loginUser.getUid()){
                Iterator<GroupHistory> historys = mGroupHistory.iterator();
                while (historys.hasNext()){
                    GroupHistory history = historys.next();
                    if(history.group.equals(mGroupID) &&
                            (nowTime - history.time)<=60000 ){
                        return;
                    }
                }
                GroupHistory g = new GroupHistory(mGroupID, nowTime);
                if(false == mGroupHistory.offer(g)){
                    mGroupHistory.poll();
                    mGroupHistory.offer(g);
                }

            }
            //用户是否1分钟之内
            boolean find = false, show = false;
            Iterator<CaseUser> caches = mCacheUsers.iterator();

            while (caches.hasNext()){
                CaseUser cacheUser = caches.next();
                if(cacheUser.user.getUid() == current.getUid()){
                    find = true;
                    if((nowTime - cacheUser.time)> 60000){
                        show = true;
                        caches.remove();
                    }
                }
            }
            if( find == false || (find==true && show == true) )  {
                CaseUser newCache = new CaseUser(current, nowTime);
                if( false == mCacheUsers.offer(newCache) ){
                    mCacheUsers.poll();
                    mCacheUsers.offer(newCache);
                }
                //CommonFunction.log("PipelineWelcomeView","showNext() show welcome user, user=" + current.getUid());
                showWelcome(current);
            }
        }else{
            mShowing = false; //没有用户可以欢迎
        }
    }

}
