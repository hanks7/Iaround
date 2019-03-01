package net.iaround.ui.worldmsg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.skill.skillmsg.SkillMessageFragment;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class WorldMessageActivity extends BaseActivity {

    @BindView(R.id.tb_detailed_indicator)
    MagicIndicator mMagicIndicator;

    @BindView(R.id.vp_detailed_viewPager)
    ViewPager viewPager;

    private String[] title;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_indector_title);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private Bundle getBundles() {
        return getIntent().getExtras();
    }

    private void initView() {
        title = BaseApplication.appContext.getResources().getStringArray(R.array.world_msg_title);
        WordMessageAdapter adapter = new WordMessageAdapter(getSupportFragmentManager(), getBundles());
        viewPager.setAdapter(adapter);
        initMagicIndicator();
        //gh 进入历史消息选中页面
        int index = 0;
        if(getIntent()!= null){
            index = getIntent().getIntExtra("cuurentInde",0);
        }
        //gh 默认切换第二项
        viewPager.setCurrentItem(index);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideIputKeyboard(WorldMessageActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initMagicIndicator() {
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(BaseApplication.appContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return title.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setPaintBold();
                clipPagerTitleView.setText(title[index]);
                clipPagerTitleView.setTextColor(Color.parseColor("#333333"));
                clipPagerTitleView.setClipColor(Color.parseColor("#FF4064"));
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(Color.parseColor("#FF4064"));
                return linePagerIndicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, viewPager);
    }

    public  void hideIputKeyboard(final Context context) {
        final Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                    boolean isOpen = manager.isActive();//isOpen若返回true，则表示输入法打开
                    View v = getCurrentFocus();
                    if(v!=null){
                        manager.hideSoftInputFromWindow(v.getWindowToken(),0);
                        manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.fl_detailed_back)
    public void onBack(){
        finish();
    }

    public class WordMessageAdapter extends FragmentPagerAdapter{
        private Bundle bundle;

        public WordMessageAdapter(FragmentManager fm, Bundle bundles) {
            super(fm);
            this.bundle = bundles;

        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return WorldMessageFragment.getInstance(bundle);
            } else {
                return SkillMessageFragment.getInstance(bundle);
            }
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position] ;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            hideIputKeyboard(WorldMessageActivity.this);
        }
        return super.onTouchEvent(event);
    }
}
