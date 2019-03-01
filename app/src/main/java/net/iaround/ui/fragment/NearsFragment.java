//package net.iaround.ui.fragment;
//
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import net.iaround.BaseApplication;
//import net.iaround.R;
//import net.iaround.conf.Common;
//import net.iaround.conf.Config;
//import net.iaround.statistics.Statistics;
//import net.iaround.tools.CommonFunction;
//import net.iaround.tools.DensityUtils;
//import net.iaround.tools.DisplayUtil;
//import net.iaround.ui.activity.MainFragmentActivity;
//
//
///**
// * 附近数据
// * 作者：gh on 2017/6/10 22:43
// * <p>
// * 邮箱：jt_gaohang@163.com
// */
//public class NearsFragment extends Fragment implements MainFragmentActivity.PagerSelectNear1, MainFragmentActivity.PagerSelectNear {
//
//    private ViewPager viewPager;
//    private ScrollIndicatorView tabLayout;
//    private ImageView ivSeach;
//    private ImageView ivFilter;
//
//    private Near1Fragment tab1Fragement;
//    private DynamicCenterFragment tab2Fragement;
//    private VideoAnchorFragment tab3Fragement;
//
//    //第二次点击的时间
//    long lastPressTime = 0;
//
//    private int index = 0;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_nears, null);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        CommonFunction.log("NearsFragment", "onViewCreated() into, savedInstanceState=" + savedInstanceState);
//        initViews(getView());
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        CommonFunction.log("NearsFragment", "onDestroy() into");
//    }
//
//    private void initViews(View view) {
//        //获取视频开关
//        int videoChatOpen = Config.getVideoChatOpen();
//
//        viewPager = (ViewPager) view.findViewById(R.id.nears_viewPager);
//        tabLayout = (ScrollIndicatorView) view.findViewById(R.id.tb_near_indicator);
//        ivSeach = (ImageView) view.findViewById(R.id.iv_nears_seach);
//        ivFilter = (ImageView) view.findViewById(R.id.iv_near_filter);
//
//        ivSeach.setVisibility(View.INVISIBLE);
//
//        DynamicTitleAdapter adapter = new DynamicTitleAdapter(getChildFragmentManager());
//        tabLayout.setOnTransitionListener(new OnTransitionTextListener().setColorId(getActivity(), R.color.login_btn, R.color.action_title_color));
//
//        ColorBar colorBar = new ColorBar(getActivity(), getResources().getColor(R.color.login_btn), DensityUtils.dp2px(BaseApplication.appContext, 3));
//        colorBar.setWidth(DensityUtils.dp2px(BaseApplication.appContext, 16));
//        tabLayout.setScrollBar(colorBar);
//        if(1 == videoChatOpen) {
//            viewPager.setOffscreenPageLimit(2);
//        }else{
//            viewPager.setOffscreenPageLimit(1);
//        }
//        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(tabLayout, viewPager);
//        indicatorViewPager.setAdapter(adapter);
//        if( 1 == videoChatOpen) {
//            adapter.setData(getResources().getStringArray(R.array.near_title));
//            adapter.notifyDataSetChanged();
//        }else{
//            adapter.setData(getResources().getStringArray(R.array.near_title_short));
//            adapter.notifyDataSetChanged();
//        }
//        ivFilter.setOnClickListener(clickListener);
//        if (videoChatOpen == 0 || Common.getInstance().getDefaultTopOption() == 0){
//            viewPager.setCurrentItem(0);
//        }else{
//            index = Common.getInstance().getDefaultTopOption() - 1;
//            if(index>=0){
//                viewPager.setCurrentItem(index);
//            }
//            if (index == 2){
//                if (Common.getInstance().loginUser.getUserType() == 1){
//                    ivFilter.setBackgroundResource(R.drawable.anchors_certification_icon);
//                }else{
//                    ivFilter.setBackgroundResource(R.drawable.white);
//                    ivFilter.setOnClickListener(null);
//                }
//            }
//        }
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                index = position;
//                ivFilter.setOnClickListener(clickListener);
//                if (position == 0){
//                    //统计
//                    Statistics.onPageClick(Statistics.PAGE_NEARBY);
//                    ivFilter.setBackgroundResource(R.drawable.near_filter);
//                }else if (position == 1){
//                    //统计
//                    Statistics.onPageClick(Statistics.PAGE_DYNAMIC);
//                    ivFilter.setBackgroundResource(R.drawable.near_filter);
//                }else {
//                    if (Common.getInstance().loginUser.getUserType() == 1){
//                        ivFilter.setBackgroundResource(R.drawable.anchors_certification_icon);
//                    }else{
//                        ivFilter.setBackgroundResource(R.drawable.white);
//                        ivFilter.setOnClickListener(null);
//                    }
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }
//
//    private View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.iv_near_filter:
//                    CommonFunction.log("NearsFragment", "iv_near_filter click()");
//                    if (index == 0) {
//                        if (tab1Fragement == null)
//                            tab1Fragement = new Near1Fragment();
//                        tab1Fragement.skipToFilter();
//                    } else if (index == 1){
//                        if (tab2Fragement == null)
//                            tab2Fragement = new DynamicCenterFragment();
//                        tab2Fragement.skipToFilter();
//                    }else {
//                        if (tab3Fragement == null)
//                            tab3Fragement = new VideoAnchorFragment();
//                        if (Common.getInstance().loginUser.getUserType() == 1)
//                            tab3Fragement.skipToFilter();
//                    }
//                    break;
//            }
//        }
//    };
//
//    @Override
//    public void onNearSelected(int pageMode) {
//
//        if (index == 1) {
//            if (tab2Fragement instanceof PagerSelectDynamic) {
//                tab2Fragement.onDynamicSelected(pageMode);
//            }
//        }
//
//    }
//
//    /**
//     * MainFragmentActivity 点击附近按钮时触发
//     */
//    @Override
//    public void onNearSelected() {
//        if (0 == index) {
//            //统计
//            Statistics.onPageClick(Statistics.PAGE_NEARBY);
//        } else if (1 == index) {
//            //统计
//            Statistics.onPageClick(Statistics.PAGE_DYNAMIC);
//        }
//    }
//
//    private class DynamicTitleAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
//
//        private String[] versions = null;
//
//        public DynamicTitleAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//        }
//
//        public String[] getData(){
//            return versions;
//        }
//
//        public void setData(String[] data){
//            versions = data;
//        }
//
//        @Override
//        public int getCount() {
//            if(versions==null){
//                return 0;
//            }
//            return versions.length;
//        }
//
//        @Override
//        public View getViewForTab(int position, View convertView, ViewGroup container) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.title_tab_top, container, false);
//            }
//            TextView textView = (TextView) convertView;
//            textView.setText(versions[position]);
//            int witdh = getTextWidth(textView);
//            int padding = DisplayUtil.dipToPix(BaseApplication.appContext, 3);
//            //因为wrap的布局 字体大小变化会导致textView大小变化产生抖动，这里通过设置textView宽度就避免抖动现象
//            //1.3f是根据上面字体大小变化的倍数1.3f设置
//            textView.setWidth((int) (witdh * 1.3f) + padding);
//
//            return convertView;
//        }
//
//        @Override
//        public Fragment getFragmentForPage(int position) {
//            if (position == 0) {
//                if (tab1Fragement == null) {
//                    tab1Fragement = new Near1Fragment();
//                }
//                return tab1Fragement;
//            } else if (position == 1){
//                if (tab2Fragement == null) {
//                    tab2Fragement = new DynamicCenterFragment();
//                }
//                return tab2Fragement;
//            }else {
//                if (tab3Fragement == null){
//                    tab3Fragement = new VideoAnchorFragment();
//                }
//                return tab3Fragement;
//            }
//        }
//
//        @Override
//        public int getItemPosition(Object object) {
//            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
//            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
//            return PagerAdapter.POSITION_UNCHANGED;
//        }
//
//        private int getTextWidth(TextView textView) {
//            if (textView == null) {
//                return 0;
//            }
//            Rect bounds = new Rect();
//            String text = textView.getText().toString();
//            Paint paint = textView.getPaint();
//            paint.getTextBounds(text, 0, text.length(), bounds);
//            int width = bounds.left + bounds.width();
//            return width;
//        }
//
//    }
//
//    public interface PagerSelectDynamic {
//        void onDynamicSelected(int pageMode);
//    }
//
//}
