
package net.iaround.ui.activity.im;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.NearContactWorker;
import net.iaround.database.PersonalMessageWorker;
import net.iaround.model.im.NearContact;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CustomDialog;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.comon.JazzyViewPager;
import net.iaround.ui.comon.JazzyViewPager.TransitionEffect;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.friend.bean.BlacklistFilteringBean;
import net.iaround.ui.game.ChatGameUserList;
import net.iaround.ui.group.activity.ReportChatAcitvity;
import net.iaround.ui.space.more.ChatRecordReport;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * 收到搭讪
 */
public class ChatGameActivity extends BaseFragmentActivity implements OnClickListener,
        OnPageChangeListener {
    /*
     * 是否显示banner条保存到sharepreference 的key
     */
    private final static String CHAT_GAME_SETTING_KEY = "not_tips_again";


	private ChatGameUserList mChatGameUserListView; // 横幅VIEW

    private ArrayList<User> mChatGameUsers; // 收到搭讪的用户表
    private TextView mTitleName; // 标题
    private TextView mRightButton;
    private ImageView mBackImage;
    private RelativeLayout mRlDeletelayout;
    private RelativeLayout mRlReplyLayout;
    private TextView mDeletButton;
    private TextView mReplyButton;
    private Button mSetCloseButton;
    private int firthEntryUserSize;
    private String bannerSet;
    private int curpage;

    private String userId;

    private RelativeLayout mAccostSettingLayout;

    private ArrayList<ChatGameFragment> pageChatGame;
    private JazzyViewPager viewPager;

    ChatGameContent chatGamecontent;

    ChatGameSelectedPage chatGameSelectPage;

    private long blacklistFilteringFlag;
    private ArrayList<NearContact> nearContact;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:

                    viewPager.setCurrentItem(msg.arg1);

                    break;
                case 2:
                    if (mChatGameUsers.size() > 1) {
                        mChatGameUsers.remove(viewPager.getCurrentItem());
                        chatGamecontent.notifyDataSetChanged();
                        chatGamecontent.delPageItem(viewPager.getCurrentItem());
                        if (viewPager.getCurrentItem() < mChatGameUsers.size()) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem());
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                        }

                    } else {
                        finish();
                    }

                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bannerSet = SharedPreferenceUtil.getInstance(ChatGameActivity.this).getString(
                CHAT_GAME_SETTING_KEY);
        firthEntryUserSize = 0;
        curpage = 0;

        userId = Common.getInstance().loginUser.getUid() + "";

        initView();


        MessageModel.getInstance()
                .clearReceiveAccostNoneRead(Common.getInstance().loginUser.getUid() + "",
                        mContext);

        blacklistFiltering();
    }

    @Override
    protected void onResume() {

        super.onResume();
        // initData( );
        // // 设置标题
        // String contentRes = ChatGameActivity.this.getResources( ).getString(
        // R.string.receive_accost_title );
        // String content = String.format( contentRes , String.valueOf(
        // mChatGameUsers.size( ) ) );
        // mTitleName.setText( content );
    }


    void initView() {
        setContentView(R.layout.activity_receive_accost);

        mTitleName = (TextView) findViewById(R.id.tv_title);
		mChatGameUserListView = (ChatGameUserList) findViewById( R.id.chatgame_list_view );

        viewPager = (JazzyViewPager) findViewById(R.id.pager);
        viewPager.setTransitionEffect(TransitionEffect.Standard);

        mRlDeletelayout = (RelativeLayout) findViewById(R.id.rl_del);
        mRlReplyLayout = (RelativeLayout) findViewById(R.id.rl_reply);
        mDeletButton = (TextView) findViewById(R.id.delete_btn);
        mReplyButton = (TextView) findViewById(R.id.reply_btn);
        mSetCloseButton = (Button) findViewById(R.id.close_setting_btn);

        mTitleName = (TextView) findViewById(R.id.tv_title);
        mBackImage = (ImageView) findViewById(R.id.iv_left);
        mRightButton = (TextView) findViewById(R.id.tv_right);
        mRightButton.setVisibility(View.VISIBLE);
        mRightButton.setText(getResources().getString(R.string.report));


        mSetCloseButton.setOnClickListener(this);
        mRlReplyLayout.setOnClickListener(this);
        mRlDeletelayout.setOnClickListener(this);
        mDeletButton.setOnClickListener(this);
        mReplyButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);

        mBackImage.setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);

        mAccostSettingLayout = (RelativeLayout) findViewById(R.id.accost_setting_layout);
        mAccostSettingLayout.setOnClickListener(this);


    }


    private void initData() {
        mChatGameUsers = new ArrayList<User>();
        // --------------从数据库读取收到的搭讪数据----------------
        ArrayList<NearContact> nearContact = getRecieveAccost(this, userId + "", 0,
                10000);

        if (nearContact != null) {
            for (NearContact contact : nearContact) {
                User user = contact.getUser();
                mChatGameUsers.add(user);
            }
        }

        sortUserList();


        // --------------从数据库读取搭讪数据结束----------------


        if (mChatGameUsers.size() <= 0) {
            finish();
            return;
        }

        pageChatGame = new ArrayList<ChatGameFragment>();
        for (int i = 0, iMax = mChatGameUsers.size(); i < iMax; i++) {
            pageChatGame.add(null);
        }

        if (mChatGameUsers.size() > 100) {
            ArrayList<User> newUser = new ArrayList<User>();
            for (int i = 0, iMax = 100; i < iMax; i++) {
                newUser.add(i, mChatGameUsers.get(i));
            }
		mChatGameUserListView.setChatGameUserList( newUser );
        } else {
			mChatGameUserListView.setChatGameUserList( mChatGameUsers );
        }
		mChatGameUserListView.setData( ChatGameActivity.this );

        chatGamecontent = new ChatGameContent(getSupportFragmentManager());//jiqiang

        viewPager.setAdapter(chatGamecontent);
        viewPager.setOnPageChangeListener(this);
        if (curpage < mChatGameUsers.size()) {
            viewPager.setCurrentItem(curpage);
        } else {
            viewPager.setCurrentItem(0);
        }

        // 设置标题
        String contentRes = ChatGameActivity.this.getResources().getString(
                R.string.receive_accost_title);
        String content = String.format(contentRes, String.valueOf(mChatGameUsers.size()));
        mTitleName.setText(content);
    }

    /**
     * 黑名单过滤
     */
    private void blacklistFiltering(){

        nearContact = getRecieveAccost(this, userId + "", 0,
                10000);

        if (nearContact != null & nearContact.size() > 0){
            String filter = "";
            for (int i = 0; i < nearContact.size(); i++){
                NearContact user = nearContact.get(i);
                if (i == nearContact.size() - 1){
                    filter += user.getUser().getUid();
                }else{
                    filter +=user.getUser().getUid() + ",";
                }
            }
            Log.d("Other","uids ="+filter);
            blacklistFilteringFlag = FriendHttpProtocol.getBlacklistFiltering(this,filter,this);

        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        if (blacklistFilteringFlag == flag){
            BlacklistFilteringBean bean = GsonUtil.getInstance().getServerBean(result,BlacklistFilteringBean.class);
            if (bean != null) {
                if (bean.isSuccess()) {
                    if (bean.userid != null) {
                        for (BlacklistFilteringBean.UserBlack userBlack : bean.userid) {
                            for (NearContact newFansBean : nearContact) {
                                if (userBlack.status == 1) {
                                    if (newFansBean.getfUid() == userBlack.userid) {
                                        // 删除最近联系人列表
                                        NearContactWorker db = DatabaseFactory.getNearContactWorker(this);
                                        db.deleteRecord(Common.getInstance().loginUser.getUid() + "", ""
                                                + userBlack.userid);

                                        // 删除聊天记录
                                        PersonalMessageWorker chatDb = DatabaseFactory.getChatMessageWorker(this);
                                        chatDb.deleteMsg(Common.getInstance().loginUser.getUid() + "", ""
                                                + userBlack.userid);
                                    }
                                }
                            }
                        }
                    }

                }
                initData();
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);
        ErrorCode.getErrorMessage(this,e);
    }

    public User getUserItem(int index) {
        if(mChatGameUsers == null){
            mChatGameUsers = new ArrayList<>();
        }
        if (index < mChatGameUsers.size()) {
            return mChatGameUsers.get(index);
        }
        return null;
    }


    @Override
    public void onClick(View v) {

        if (v == mDeletButton || v == mRlDeletelayout) {
            // 删除
            if(viewPager == null || mChatGameUsers ==null){
                return;
            }
            if (viewPager.getCurrentItem() < mChatGameUsers.size()) {

                // 删除最近联系人列表
                NearContactWorker db = DatabaseFactory.getNearContactWorker(this);
                db.deleteRecord(Common.getInstance().loginUser.getUid() + "", ""
                        + mChatGameUsers.get(viewPager.getCurrentItem()).getUid());


                // 删除聊天记录
                PersonalMessageWorker chatDb = DatabaseFactory.getChatMessageWorker(this);
                chatDb.deleteMsg(Common.getInstance().loginUser.getUid() + "", ""
                        + mChatGameUsers.get(viewPager.getCurrentItem()).getUid());

                // db.deleteSubgroupRecord(""+mChatGameUsers.get(
                // viewPager.getCurrentItem( )).getUid( ),3);
                handler.sendEmptyMessage(2);
                if (chatGameSelectPage != null) {
                    chatGameSelectPage.onChatGamePageDeleted(viewPager.getCurrentItem());
                }
                String contentRes = ChatGameActivity.this.getResources().getString(
                        R.string.receive_accost_title);
                String content = String.format(contentRes,
                        String.valueOf(mChatGameUsers.size() - 1));
                mTitleName.setText(content);
            }

        } else if (v == mRightButton || v.getId() == R.id.fl_right) {
            // 举报
			int curSelect = viewPager.getCurrentItem( );
            if(null!=mChatGameUsers){
                User user = mChatGameUsers.get( curSelect );
                Intent intent = new Intent( this, ReportChatAcitvity.class );
                intent.putExtra( ReportChatAcitvity.USER_ID_KEY, user.getUid() );
                intent.putExtra( ReportChatAcitvity.REPORT_FROM_KEY, ChatRecordReport.TYPE_PERSON );
                startActivity( intent );
            }
        } else if (v == mBackImage || v.getId() == R.id.fl_left) {
            finish();
        } else if (v == mReplyButton || v == mRlReplyLayout) {
            if(mChatGameUsers == null || viewPager ==null){
                return;
            }
            if (mChatGameUsers.size() <= viewPager.getCurrentItem())return;
            User user = mChatGameUsers.get(viewPager.getCurrentItem());
            if (user != null) {
                // SpaceOther.appendUserData( user) ;
                // SpaceOther.launch( v.getContext( ) , user.getUid( ) ,
                // ProfileEntrance.CHAT_GAME_REPLY );

                ChatPersonal.skipToChatPersonal(mActivity, user,
                        ChatPersonal.REQUEST_SPACEOTHER_JUMP);


            }
        } else if (v == mSetCloseButton) {
            String dialogSet = SharedPreferenceUtil.getInstance(ChatGameActivity.this)
                    .getString(CHAT_GAME_SETTING_KEY);
            if (!CommonFunction.isEmptyOrNullStr(dialogSet)) {
                if (dialogSet == "1") {
                    mAccostSettingLayout.setVisibility(View.GONE);
                    return;
                } else {
                    Long saveTime = Long.parseLong(dialogSet);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(saveTime + Common.getInstance().serverToClientTime);

                    int dayOfSave = cal.get(Calendar.DAY_OF_MONTH);

                    cal.setTimeInMillis(System.currentTimeMillis()
                            + Common.getInstance().serverToClientTime);
                    int dayOfNow = cal.get(Calendar.DAY_OF_MONTH);

                    if (dayOfNow - dayOfSave < 1) {
                        mAccostSettingLayout.setVisibility(View.GONE);
                        return;
                    }

                }
            }

            CustomDialog updateDialog = null;

            updateDialog = new CustomDialog(ChatGameActivity.this, ChatGameActivity.this
                    .getResources().getString(R.string.receive_accost_tips_title),
                    ChatGameActivity.this.getResources().getString(
                            R.string.receive_accost_set_tips), ChatGameActivity.this
                    .getResources()
                    .getString(R.string.receive_accost_not_tips_again),
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            // 不再提醒
                            mAccostSettingLayout.setVisibility(View.GONE);
                            SharedPreferenceUtil.getInstance(ChatGameActivity.this)
                                    .putString(CHAT_GAME_SETTING_KEY, "1");

                        }
                    }, ChatGameActivity.this.getResources().getString(
                    R.string.receive_accost_tips_close), new OnClickListener() {

                @Override
                public void onClick(View v) {

                    // 关闭
                    mAccostSettingLayout.setVisibility(View.GONE);
                    SharedPreferenceUtil.getInstance(ChatGameActivity.this)
                            .putString(CHAT_GAME_SETTING_KEY,
                                    System.currentTimeMillis() + "");
                }
            });


            try {
                updateDialog.show();
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else if (v == mAccostSettingLayout) {
            // 跳转到设置搭讪游戏

            Toast.makeText(ChatGameActivity.this, "跳转到设置搭讪游戏", Toast.LENGTH_LONG).show();
//			Intent intent = new Intent( ChatGameActivity.this , GreetingSettingActivity.class );
//
//			this.startActivity( intent );//jiqiang
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    class ChatGameContent extends FragmentStatePagerAdapter {
        public ChatGameContent(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {

            ChatGameFragment temp = newInstance(arg0, mChatGameUsers.get(arg0).getIcon(),
                    mChatGameUsers.get(arg0).getUid());
            pageChatGame.add(arg0, temp);
            viewPager.setObjectForPosition(pageChatGame.get(arg0), arg0);
            return pageChatGame.get(arg0);

        }

        public void delPageItem(int index) {
            pageChatGame.remove(index);
        }

        @Override
        public int getCount() {

            if (mChatGameUsers.size() > 100) {
                return 100;
            }
            return mChatGameUsers.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {

            pageChatGame.add(position, null);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {

            // return super.getItemPosition( object );
            return POSITION_NONE;
        }
    }


    private ChatGameFragment[] chatgameInstance = new ChatGameFragment[4];

    ChatGameFragment newInstance(int position, String imageUrl, long currentUid) {
        ChatGameFragment newChatGameFragment = new ChatGameFragment();


        Bundle args = new Bundle();
        args.putString("photoId", imageUrl);
        args.putInt("Position", position);
        args.putLong("UserId", currentUid);

        newChatGameFragment.setArguments(args);
        return newChatGameFragment;

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {


    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {


    }

    @Override
    public void onPageSelected(int arg0) {

        if (chatGameSelectPage != null) {
            CommonFunction.log("ChatGame", "onChatGamePageSelected ==" + arg0);
            chatGameSelectPage.onChatGamePageSelected(arg0);
            curpage = arg0;
        }
    }


    public void setChatGamePageChangeListener(ChatGameSelectedPage onPageSelected) {
        chatGameSelectPage = onPageSelected;
    }


    public interface ChatGameSelectedPage {
         void onChatGamePageSelected(int selectIndex);

         void onChatGamePageDeleted(int deletedIndex);
    }


    /**
     * 获取收到搭讪列表
     */
    public ArrayList<NearContact> getRecieveAccost(Context context, String uid,
                                                   int start, int amount) {
        ArrayList<NearContact> list = new ArrayList<NearContact>();
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);

        Cursor cursor = db.selectRecieveAccostPage(uid);
        try {
            cursor.moveToFirst();
            while (!(cursor.isAfterLast())) {
                NearContact n = new NearContact();
                try {
                    int subgroup = cursor.getInt(cursor
                            .getColumnIndex(NearContactWorker.SUBGROUP));
                    if (subgroup == 3) {// 只对发出搭讪类型操作
                        n.setSubGroup(subgroup);
                        String content = cursor.getString(cursor
                                .getColumnIndex(NearContactWorker.LASTCONTENT));
                        User fUser = new User();
                        String noteName = cursor.getString(cursor
                                .getColumnIndex(NearContactWorker.FNOTE));
                        fUser.setNoteName(noteName);
                        fUser.setLastSayTime(cursor.getLong(cursor
                                .getColumnIndex(NearContactWorker.LASTDATETIME)));
                        String userInfo = cursor.getString(cursor
                                .getColumnIndex(NearContactWorker.FUSERINFO));
                        if (!CommonFunction.isEmptyOrNullStr(userInfo)) {
                            JSONObject jsonObj = new JSONObject(userInfo);
                            try {
                                fUser.setUid(jsonObj.optLong("fuid"));
                                fUser.setNickname(CommonFunction.jsonOptString(jsonObj,
                                        "fnickname"));
                                fUser.setIcon(CommonFunction
                                        .jsonOptString(jsonObj, "ficon"));
                                fUser.setViplevel(jsonObj.optInt("fvip"));
                                fUser.setSVip(jsonObj.optInt("svip"));
                                fUser.setSex(jsonObj.optInt("fgender"));
                                fUser.setLat(jsonObj.optInt("flat"));
                                fUser.setLng(jsonObj.optInt("flng"));
                                fUser.setAge(jsonObj.optInt("fage"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            fUser.setUid((cursor.getLong(cursor
                                    .getColumnIndex(NearContactWorker.FUID))));
                            fUser.setNickname(cursor.getString(cursor
                                    .getColumnIndex(NearContactWorker.FNICKNAME)));
                            fUser.setIcon(cursor.getString(cursor
                                    .getColumnIndex(NearContactWorker.FICON)));
                        }
                        n.setUser(fUser);

                        n.setFriendMsg(false);


                        list.add(n);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return list;

    }

    private void sortUserList() {
        Collections.sort(mChatGameUsers, new SortBySERVER_ID());
    }
    class SortBySERVER_ID implements Comparator<User> {
        public int compare(User o1, User o2) {
            User s1 = o1;
            User s2 = o2;
            if (s1.getLastSayTime() < s2.getLastSayTime())
                return 1;
            return -1;
        }
    }

}
