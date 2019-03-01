package net.iaround.ui.worldmsg;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseFragment;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.model.skill.EventLongPressUser;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.chat.view.WorldMessageBaseView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：zx on 2017/8/22 21:06
 */
public class WorldMessageFragment extends MvpBaseFragment<WorldMessageContract.Presenter> implements WorldMessageContract.View, View.OnLayoutChangeListener{

    @BindView(R.id.message_list)
    PullToRefreshListView message_list;
    @BindView(R.id.editContent)
    EditText editContent;
    @BindView(R.id.btnSend)
    TextView btnSend;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    private ArrayList<WorldMessageRecord> mDataList;
    private ArrayList<WorldMessageRecord> recordHistory = new ArrayList<>();

    private View headView;
    private WorldMessageAdapter adapter;
    private String groupId;
    private String groupName;
    private volatile long timeStamp = 0;
    private boolean isLast = true;


    public static WorldMessageFragment getInstance(Bundle bundle){
        WorldMessageFragment fragment = new WorldMessageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void getBundles() {
        Bundle bundle = getArguments();
        if (null != bundle){
            groupId = bundle.getString("groupId");
            groupName = bundle.getString("groupName");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_world_message_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        getBundles();
        init(view);
        mPresenter = new WorldMessagePresenter(this);
        mPresenter.setSocketCallback();
        EventBus.getDefault().register(WorldMessageFragment.this);
        message_list.post(new Runnable() {
            @Override
            public void run() {
                message_list.setRefreshing(true);
            }
        });
    }

    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    private void init(final View view) {
        rootLayout.addOnLayoutChangeListener(this);
        screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;
        if (null == mDataList){
            mDataList = new ArrayList<>();
        }
        adapter = new WorldMessageAdapter();
        headView = View.inflate(BaseApplication.appContext, R.layout.chat_personal_record_head, null);
        View footerView = View.inflate(BaseApplication.appContext, R.layout.record_footer, null);
        message_list.getRefreshableView().addHeaderView(headView);
        message_list.getRefreshableView().addFooterView(footerView);
        message_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//PULL_FROM_START
        message_list.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        message_list.getRefreshableView().setFastScrollEnabled(false);
        message_list.setOnRefreshListener(mOnRefreshListener);
        message_list.setAdapter(adapter);
        message_list.setVerticalScrollBarEnabled(false);
        message_list.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });
        message_list.getRefreshableView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int count = view.getChildCount();
                switch (scrollState){
                    case SCROLL_STATE_IDLE:
                        for (int i = 0; i < count; i++) {
                            if (view.getChildAt(i) instanceof WorldMessageBaseView){
                                WorldMessageBaseView recordView = (WorldMessageBaseView) view.getChildAt(i);
                                recordView.setScrolled(false);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case SCROLL_STATE_FLING:
                    case SCROLL_STATE_TOUCH_SCROLL:
                        for (int i = 0; i < count; i++) {
                            if (view.getChildAt(i) instanceof WorldMessageBaseView) {
                                WorldMessageBaseView recordView = (WorldMessageBaseView) view.getChildAt(i);
                                recordView.setScrolled(true);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isLast = message_list.getRefreshableView().getLastVisiblePosition() == totalItemCount - 1;
            }
        });
        editContent.addTextChangedListener(textWatcher);


    }


    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            CommonFunction.log("WorldMessageFragment","onPullDownToRefresh this.timeStamp=" + timeStamp);
            mPresenter.getWorldMessageHistory(timeStamp);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

        }
    };


    @Override
    public void updateLastMessage(WorldMessageRecord record) {
        if (null != record){
            mDataList.add(record);
            message_list.post(new Runnable() {
                @Override
                public void run() {
                    adapter.updateLastMessage(mDataList);
                    if (isShowKeyboard || isLast){
                        message_list.getRefreshableView().setSelection(mDataList.size());
                        message_list.getRefreshableView().smoothScrollToPosition(mDataList.size());
                    }
                }
            });
        }


    }
    @Override
    public void updateHistoryMessage(final ArrayList<WorldMessageRecord> recordList, long timeStamp) {
        if(timeStamp>0){
            this.timeStamp = timeStamp;
        }

        CommonFunction.log("WorldMessageFragment","updateHistoryMessage this.timeStamp=" + this.timeStamp);
        if (null != recordList && recordList.size() > 0){
            final int size = recordList.size();
            recordHistory.clear();
            recordHistory.addAll(recordList);
            recordHistory.addAll(mDataList);
            mDataList.clear();
            mDataList.addAll(recordHistory);
            message_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    message_list.onRefreshComplete();
                    message_list.setAdapter(adapter);
                    adapter.updateLastMessage(mDataList);
                    message_list.getRefreshableView().setSelected(true);
                    message_list.getRefreshableView().setSelection(size);
                }
            }, 200);
        }else {
            refreshCompleted();
        }

    }

    @Override
    public void refreshCompleted() {
        message_list.post(new Runnable() {
            @Override
            public void run() {
                message_list.onRefreshComplete();
                //message_list.setRefreshing(false);
            }
        });
    }

    private void clearEditContent() {
        editContent.setText("");
    }

    @OnClick(R.id.btnSend)
    public void onViewClicked() {
        String content = editContent.getText().toString().trim();
        if (TextUtils.isEmpty(content) || CommonFunction.isStringAllSpace(content)){
            CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.input_world_msg));
            return;
        }
        clearEditContent();
        if (!TextUtils.isEmpty(groupName)){
            content = content + "（" + getString(R.string.come_from) + groupName + "）";
        }
        mPresenter.sendWorldMessage(groupId, content);
    }

    /**
     * 监听输入框文字的改变
     */
    protected TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            showSendBtn();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int len = s.toString().length();
            if(len >= 140){
                CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.max_share_content_length_tips));
            }
        }
    };

    private void showSendBtn() {
        String str = editContent.getEditableText().toString().trim();
        boolean isShow = !TextUtils.isEmpty(str);

        if (isShow) {
            btnSend.setVisibility(View.VISIBLE);
        } else {
            btnSend.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPresenter(WorldMessageContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public boolean isActive() {
        return isLive();
    }

    @Override
    public void onDestroy() {
        if (null != mPresenter) {
            mPresenter.clearSocketCallback();
        }
        EventBus.getDefault().unregister(WorldMessageFragment.this);
        super.onDestroy();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //只要控件将view向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){//软键盘弹起
            isShowKeyboard = true;
            message_list.post(new Runnable() {
                @Override
                public void run() {
                    message_list.getRefreshableView().setSelection(mDataList.size());
                    message_list.getRefreshableView().smoothScrollToPosition(mDataList.size());
                }
            });

        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){//软件盘关闭
            isShowKeyboard = false;
        }
    }
    private boolean isShowKeyboard = false;

    @Subscribe
    public void receivePropsResult(EventLongPressUser result){
        if (!isShowKeyboard){
            editContent.setFocusable(true);
            editContent.requestFocus();
            InputMethodManager imm = (InputMethodManager) BaseApplication.appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        SpannableString spContent = FaceManager.getInstance(BaseApplication.appContext)
                .parseIconForString(BaseApplication.appContext, result.getUserName(), 0, null);
        editContent.setText(spContent);
        /*设置光标位置*/
        editContent.setSelection(editContent.getText().length());
    }
}
