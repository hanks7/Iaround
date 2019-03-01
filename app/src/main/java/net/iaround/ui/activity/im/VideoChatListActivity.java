
package net.iaround.ui.activity.im;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.AnchorsCertificationProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.im.proto.Iavchat;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.VideoDetailsActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.friend.bean.VideoChatBean;
import net.iaround.ui.friend.bean.VideoChatBean.VideoChatItem;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;

/**
 * 视频会话列表
 **/
public class VideoChatListActivity extends BaseActivity implements OnClickListener, HttpCallBack {
    private LinearLayout mEmptyView;
    private PullToRefreshListView listView;

    private VideoChatListAdapter adapter;
    private ArrayList<VideoChatItem> mDatas;

    private int mCurrentPage = 1;
    private int mTotalPage = 1;
    private final int mPageSize = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_list);

        initView();
        setListener();
        initData();
    }

    /**
     * 初始化View
     */
    private void initView() {

        ((TextView) findViewById(R.id.tv_title)).setText(R.string.video_details_video_chat);

        mEmptyView = (LinearLayout) findViewById(R.id.empty_friend);

        listView = (PullToRefreshListView) findViewById(R.id.pull_video_chat_list);
    }

    /**
     * 设置监听
     */
    private void setListener() {
        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(this);
        mEmptyView.setOnClickListener(this);

        listView.setMode(PullToRefreshBase.Mode.BOTH);

        listView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        listView.getRefreshableView().setDividerHeight(0);
        listView.getRefreshableView().setFadingEdgeLength(-1);
        listView.getRefreshableView().setSelector(R.drawable.transparent);
        listView.getRefreshableView().setFastScrollEnabled(false);
        
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 1;
                requestPageData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (mCurrentPage < mTotalPage) {
                    mCurrentPage = mCurrentPage + 1;
                    requestPageData();
                } else {
                    listView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                            CommonFunction.toastMsg(VideoChatListActivity.this, R.string.no_more_data);

                        }
                    }, 200);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mDatas = new ArrayList<>();
        if (adapter == null)
            adapter = new VideoChatListAdapter(VideoChatListActivity.this, mDatas);

        listView.setAdapter(adapter);

        requestPageData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left: {
                finish();
            }
            break;
            case R.id.empty_friend:

                requestPageData();
                break;
        }
    }

    /**
     * 请求数据
     */
    public void requestPageData() {
        AnchorsCertificationProtocol.getVideoHistoryList(BaseApplication.appContext, mCurrentPage,mPageSize,this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        VideoChatBean bean = GsonUtil.getInstance().getServerBean(result,VideoChatBean.class);
        listView.onRefreshComplete();
        if (bean!= null && bean.isSuccess()){
            mEmptyView.setVisibility(View.GONE);
            refershData(bean);

        }else{
            listView.onRefreshComplete();
            if (mDatas.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {

                ErrorCode.showError(VideoChatListActivity.this,result);
                CommonFunction.toastMsg(mContext, R.string.e_104);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        listView.onRefreshComplete();
        if (mDatas.isEmpty()){
            mEmptyView.setVisibility(View.VISIBLE);
        }
        ErrorCode.getErrorMessage(VideoChatListActivity.this,e);
    }

    /**
     * 刷新布局
     * @param bean
     */
    private void refershData(VideoChatBean bean){
        mCurrentPage = bean.pageno;
        int total = bean.amount;
        mTotalPage = total / mPageSize;
        if (total % mPageSize > 0) {
            mTotalPage++;
        }
        if (mCurrentPage <= 1) {
            if (mDatas == null) {
                mDatas = new ArrayList<>();
            } else {
                mDatas.clear();
            }
        }
        if(bean.list != null ){
            mDatas.addAll(bean.list);
        }

        if (mDatas.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        listView.onRefreshComplete();
        adapter.notifyDataSetChanged();
    }

    class VideoChatListAdapter extends BaseAdapter {
        Context context;
        public ArrayList<VideoChatItem> mList;
        LayoutInflater inflater;

        public VideoChatListAdapter(Context context, ArrayList<VideoChatItem> list) {
            this.mList = list;
            this.context = context;
            mContext = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            if (mList != null) {
                return mList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater
                        .inflate(R.layout.message_contact_item, parent, false);

                holder = new ViewHolder();
                holder.icon = (HeadPhotoView) convertView.findViewById(R.id.friend_icon);
                holder.tvNotice = (TextView) convertView.findViewById(R.id.tv_notice);
                holder.name = (TextView) convertView.findViewById(R.id.userName);
                holder.time = (TextView) convertView.findViewById(R.id.onlineTag);
                holder.chat_status = (TextView) convertView.findViewById(R.id.chat_status);
                holder.num = (TextView) convertView.findViewById(R.id.chat_num_status);

                holder.tvNotice.setTextColor(mContext.getResources().getColor(R.color.gray));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final VideoChatItem msg = (VideoChatItem) getItem(position);
            if (msg != null) {
                // 发送状态
                holder.chat_status.setVisibility(View.GONE);

                // 时间
                holder.time.setText(
                        TimeFormat.timeFormat4(mContext, ((long)msg.end_time * 1000)));

                // 昵称
                holder.name.setText(FaceManager.getInstance(mContext).parseIconForString(
                        holder.name, mContext, msg.nickname, 20));

                // 头像

                final User user = new User();
                user.setSVip(msg.svip);
                user.setViplevel(msg.viplevel);
                user.setUid(msg.otheruid);
                user.setIcon(msg.icon);
                user.setNoteName(msg.note);

                holder.icon.execute(ChatFromType.UNKONW, user, null);

                if (msg.video_finish_type == Iavchat.STATE_CALLER_CLOSE | msg.video_finish_type == Iavchat.STATE_CALLEE_CLOSE){
                    String notes = mContext.getString(R.string.private_video_chat_talk_time) +" "+ TimeFormat.timeParse(msg.video_time);
                    holder.tvNotice.setText(getSpannableTextColor(notes,""+TimeFormat.timeParse(msg.video_time)));
                }else{
                    String notes = mContext.getString(R.string.video_chat_unconnected);
                    holder.tvNotice.setText(getSpannableTextColor(notes,notes));
                }

                holder.num.setVisibility(View.GONE);

                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (msg.otheruid <= 0) { // 用户不存在
                            CommonFunction.showToast(mContext,
                                    mContext.getString(R.string.none_user), 0);
                            return;
                        }

                        // 是否是主播
                        if (msg.is_anchor == 1){

                            Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                            intent.putExtra(VideoDetailsActivity.KEY_VIDEO_UID,msg.otheruid);
                            startActivity(intent);

                        }else {
                            Intent intent = new Intent(VideoChatListActivity.this, OtherInfoActivity.class);
                            intent.putExtra(Constants.UID, msg.otheruid);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }

                    }
                });

            }
            return convertView;
        }

    }

    /**
     * 单独设置内部字体颜色
     * @param text
     * @param keyworld
     * @return
     */
    public SpannableStringBuilder getSpannableTextColor(String text, String keyworld){
        SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder(text);
        if(text.contains(keyworld)){
            int spanStartIndex=text.indexOf(keyworld);
            int spacEndIndex=spanStartIndex+keyworld.length();
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4064")),spanStartIndex,spacEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return spannableStringBuilder;
    }


    static class ViewHolder {
        public HeadPhotoView icon;
        public TextView name;
        public TextView time;
        public TextView tvNotice;
        public TextView chat_status;
        public TextView num;
    }

}
