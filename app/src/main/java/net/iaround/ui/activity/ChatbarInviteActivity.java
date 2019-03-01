package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.contacts.bean.FriendListBean;
import net.iaround.ui.fragment.ChatbarFriendsFragment;
import net.iaround.ui.fragment.ChatbarRecommendFragment;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatbarInviteActivity extends BaseFragmentActivity implements View.OnClickListener,HttpCallBack{

    private long FLAG_INVITE_FRIENDS ;
    private String[] title;
    private MagicIndicator mMagicIndicator;
    private ViewPager viewPager;
    private ContactsAdapter1 contactsAdapter;
    private ImageView seach;
    private ChatbarFriendsFragment tab1Fragement;
    private ChatbarRecommendFragment tab2Fragement;
    private FriendListBean entity;
    private FrameLayout flRight;
    private TextView tvRight;
    private ArrayList<Integer> inviteFriendUser;
    private ArrayList<Integer> inviteRecommendUser;
    private String groupId;

    //用来表示当前在好友页面还是推荐页面
    private int type = 0;//0 好友 1 推荐

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbar_invite);

        inviteFriendUser = new ArrayList<>();
        inviteRecommendUser = new ArrayList<>();
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupid");
        initView();
    }

    public void initView() {
        entity = new FriendListBean();
        viewPager = (ViewPager) findViewById(R.id.vp_contacts_viewPager);
        mMagicIndicator = (MagicIndicator) findViewById(R.id.siv_contacts_indicator1);
        seach = (ImageView) findViewById(R.id.iv_contacts_seach);
        flRight = (FrameLayout) findViewById(R.id.fl_right);
        tvRight = (TextView) findViewById(R.id.tv_right);
        //后退
        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.iv_contacts_back).setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);
        title = getResources().getStringArray(R.array.contacts_chatbar_invite);
        viewPager.setOffscreenPageLimit(2);
        contactsAdapter = new ContactsAdapter1(getSupportFragmentManager());
        viewPager.setAdapter(contactsAdapter);

        initMagicIndicator();

        viewPager.setCurrentItem(0,false);

        flRight.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    type = 0;
                else
                    type = 1;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_contacts_back:
                onBackPressed();
                break;
            case R.id.fl_right:
            case R.id.tv_right:
                // TODO: 2017/8/16 邀请好友
                inviteFriends();
                break;
        }
    }
    /**
     * 邀请好友
     */
    private void inviteFriends() {
        String userIds = "";
        if (type == 0)
        {
            if (inviteFriendUser.size() == 0)
                return;
            for (int i = 0; i < inviteFriendUser.size(); i++) {
                userIds += inviteFriendUser.get(i)+",";
            }
        }else if (type == 1)
        {
            if (inviteRecommendUser.size() == 0)
                return;
            for (int i = 0; i < inviteRecommendUser.size(); i++) {
                userIds += inviteRecommendUser.get(i)+"";
            }
        }
        if (TextUtils.isEmpty(userIds)) {
            finish();
        } else {
            FLAG_INVITE_FRIENDS = GroupHttpProtocol.chatbarInviteChat(this,groupId, userIds,this);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == FLAG_INVITE_FRIENDS)
        {
            if (result == null)
                return;
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("status") == 200)
                {
                    DialogUtil.showOKDialog(ChatbarInviteActivity.this,
                            getResources().getString(R.string.prompt), getResources()
                                    .getString(R.string.group_chat_invite_dialog),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    finish();
                                }
                            });
                }else
                {
                    ErrorCode.showError(ChatbarInviteActivity.this,result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        ErrorCode.toastError(this,e);
    }

    private class ContactsAdapter1 extends FragmentPagerAdapter {

        public ContactsAdapter1(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (tab1Fragement == null) {
                    tab1Fragement = new ChatbarFriendsFragment(new ChatbarFriendsFragment.InviteFriendsListCallback() {
                        @Override
                        public void getInviteFriendList(List<Integer> inviteUsers) {
                            inviteFriendUser.clear();
                            inviteFriendUser.addAll(inviteUsers);
                        }
                    });
                }
                return tab1Fragement;
            } else {
                if (tab2Fragement == null) {
                    tab2Fragement = new ChatbarRecommendFragment(groupId, new ChatbarRecommendFragment.InviteRecommendListCallback() {
                        @Override
                        public void getInviteRecommendList(List<Integer> inviteUsers) {
                            inviteRecommendUser.clear();
                            inviteRecommendUser.addAll(inviteUsers);
                        }
                    });
                }
                return tab2Fragement;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position] ;
        }
    }
}
