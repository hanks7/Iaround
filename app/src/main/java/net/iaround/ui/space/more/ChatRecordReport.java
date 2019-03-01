package net.iaround.ui.space.more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.model.type.ChatMessageType;
import net.iaround.share.utils.Hashon;
import net.iaround.tools.im.AudioPlayUtil;
import net.iaround.ui.adapter.ChatReportRecordAdapter;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.group.activity.ReportChatAcitvity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 聊天记录举报页面
 *
 * @author 余勋杰
 */
public class ChatRecordReport extends SuperActivity implements OnClickListener,
        Callback {

    public static final int TYPE_PERSON = 1;
    public static final int TYPE_ROOM = 2;

    private RelativeLayout rlTitle;
    private TextView tvTitle;
    private ImageView tvCancel;
    private ImageView tvComplete;

    private ListView lvRecord;

    private ChatReportRecordAdapter adapter;

    private long mUid;//登录者的用户id
    private long targetUid;//被举报用户的id
    private long targetGroupId;//当reportFrom == TYPE_ROOM时,举报的消息属于哪个圈子的id
    private int reportFrom;//从哪里举报 TYPE_PERSON or TYPE_ROOM

    public Handler handler;

    private final int MSG_LOAD_LIST_WHAT = 1;
    private final int PAGE_SIZE = 24;//每页加载的数量

    private ArrayList<ChatRecord> recordList = new ArrayList<ChatRecord>(); // 显示出来的聊天记录
    private ArrayList<ChatRecord> recordBuffer = new ArrayList<ChatRecord>(); // 缓存的没有显示的聊天记录

    private int curPage; //这个不是标记当前显示的页数，而是从数据库中读取的页数

    private boolean endFlag;
    private boolean reqLock;

    private View moreRl;//更多的View
    private View refPb;//更多View中的progressBar

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.space_report_chat_record);

        initView();
        initFooter();

        mUid = Common.getInstance().loginUser.getUid();
        handler = new Handler(this);

        targetUid = getIntent().getLongExtra(ReportChatAcitvity.USER_ID_KEY, 0);
        reportFrom = getIntent().getIntExtra(ReportChatAcitvity.REPORT_FROM_KEY, ChatRecordReport.TYPE_PERSON);
        targetGroupId = getIntent().getLongExtra(ReportChatAcitvity.GROUP_ID_KEY, 0);

        adapter = new ChatReportRecordAdapter(this, recordList);
        adapter.showCheckBox(true);
        lvRecord.setAdapter(adapter);

        lvRecord.postDelayed(new Runnable() {
            public void run() {
                performNextPage();
            }
        }, 50);
    }

    private void initView() {
        rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
        tvTitle = (TextView) rlTitle.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.report);

        tvCancel = (ImageView) rlTitle.findViewById(R.id.iv_left);
        tvCancel.setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);

        tvComplete = (ImageView) rlTitle.findViewById(R.id.iv_right);
        tvComplete.setImageResource(R.drawable.icon_publish);
        tvComplete.setVisibility(View.VISIBLE);
        tvComplete.setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);

        lvRecord = (ListView) findViewById(R.id.lvRecord);
    }

    public void onStop(Context context) {
        super.onStop();
        AudioPlayUtil.getInstance().releaseRes();
    }

    public void onDestroy(Context context) {
        super.onDestroy();
        AudioPlayUtil.getInstance().releaseRes();
    }

    public void onClick(View v) {
        if (v.equals(tvCancel) || v.getId() == R.id.fl_left) {
            finish();
        } else if (v.equals(tvComplete) || v.getId() == R.id.fl_right) {
            reportRecord();
        }
    }

    // 执行举报
    private void reportRecord() {
        if (adapter == null)
            return;

        ArrayList<ChatRecord> selectList = adapter.getSeletedList();

        if (selectList == null || selectList.size() <= 0) {
            Toast.makeText(this, R.string.select_chat_record,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String content = getReportContent(selectList);

        Intent intent = new Intent();
        intent.putExtra(ReportChatAcitvity.REPORT_CONTENT, content);
        intent.putExtra(ReportChatAcitvity.REPORT_COUNT, adapter.getSelectedCount());
        setResult(RESULT_OK, intent);
        finish();

//        // 发送数据
//         if (adapter.pd != null && adapter.pd.isShowing()) {
//         adapter.pd.dismiss();
//         }
//         adapter.pd = DialogUtil.showProgressDialog(this,
//         R.string.photo_crop_title, R.string.please_wait, null);
//
//         try {
//         // 个人资料不当举报
//         if (!CommonFunction.forbidSay(this)) {
//         long flag = SpaceModel.getInstance(this).reportReq(
//         mActivity, ReportType.SEX_ABUSE_POLITICS,
//         ReportTargetType.CHAT_RECORD,
//         String.valueOf(adapter.targetUid), json,
//         ChatRecordReport.this);
//         if (flag == 1) {
//         if (adapter.pd != null && adapter.pd.isShowing()) {
//         adapter.pd.dismiss();
//         }
//         }
//         }
//         } catch (Throwable t) {
//         CommonFunction.log(t);
//         if (adapter.pd != null && adapter.pd.isShowing()) {
//         adapter.pd.dismiss();
//         }
//         Toast.makeText(mActivity, R.string.network_req_failed,
//         Toast.LENGTH_SHORT).show();
//         }
    }

    // 这个方法被非主线程调用，不可直接执行ui操作
    private void loadNextPage() {
        // 如果缓存中的数据不够一页，则尝试从数据库中取出数据
        while (recordBuffer.size() < PAGE_SIZE) {
            ArrayList<ChatRecord> rawList = null;
            if (reportFrom == ChatRecordReport.TYPE_PERSON) { // 举报私聊
                ChatPersonalModel helper = ChatPersonalModel.getInstance();
                rawList = helper.selectPageRecord(mContext,
                        String.valueOf(mUid), String.valueOf(targetUid),
                        curPage * PAGE_SIZE, PAGE_SIZE);
            } else if (reportFrom == ChatRecordReport.TYPE_ROOM) { // 举报圈子中的发言
                GroupChatListModel helper = GroupChatListModel.getInstance();
                rawList = helper.loadGroupMessage(mContext, mUid,
                        targetGroupId, curPage * PAGE_SIZE, PAGE_SIZE);
            }

            // 如果数据库中没有数据，则没必要继续循环，即便数据不够一页也跳出
            if (rawList == null || rawList.size() <= 0) {
                break;
            }

            // 从数据库中读取的数据可以是任何人的发言，因此需要过滤非指定uid的发言
            for (ChatRecord record : rawList) {
                // 排除问题的消息
                if (isNeedReport(record)) {
                    recordBuffer.add(record);
                }
            }
            curPage++;
        }

        // 如果缓存列表不足一页，肯定是数据库中已经没有更多数据了
        endFlag = recordBuffer.size() < PAGE_SIZE;
        int readSize = (!endFlag) ? PAGE_SIZE : recordBuffer.size();
        // 回到主线程操作数据
        Message msg = new Message();
        msg.what = MSG_LOAD_LIST_WHAT;
        msg.arg1 = readSize;
        handler.sendMessage(msg);
    }

    // 判断该聊天记录需要加入举报列表
    private boolean isNeedReport(ChatRecord record) {
        // 如果该聊天记录是我的,则不需要举报
        if (record.getSendType() == MessageBelongType.SEND || record.getFuid() != targetUid) {
            return false;
        } else {
            // 如果该记录是真心话的问题,不需要举报
            if (record.getType().equals(
                    String.valueOf(ChatRecordViewFactory.ACCOST_GAME_QUE))) {
                return false;
            }// 如果该记录是分享类型,不需要举报
            else if (record.getType().equals(
                    String.valueOf(ChatRecordViewFactory.SHARE))) {
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_LIST_WHAT: {
                // 只取出足够一页（小于或等于）的数据出来显示
                for (int i = 0; i < msg.arg1; i++) {
                    recordList.add(recordBuffer.remove(0));
                }
                adapter.notifyDataSetChanged();
                reqLock = false;

                moreRl.setVisibility(endFlag ? View.GONE : View.VISIBLE);
                refPb.setVisibility(View.GONE);
                break;
            }
        }
        return false;
    }

    private void initFooter() {
        View footer = LayoutInflater.from(this).inflate(R.layout.new_footer, null);
        moreRl = footer.findViewById(R.id.moreRl);
        refPb = footer.findViewById(R.id.refPb);
        moreRl.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!reqLock) {
                    performNextPage();
                }
            }
        });
        lvRecord.addFooterView(footer);
        moreRl.setVisibility(View.GONE);
    }

    public void performNextPage() {
        reqLock = true;
        refPb.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                loadNextPage();
            }
        }.start();
    }

    // 组合为举报的内容
    private String getReportContent(ArrayList<ChatRecord> selectList) {
        HashMap<String, Object> map = new HashMap<String, Object>();

        ArrayList<HashMap<String, Object>> contents = new ArrayList<HashMap<String, Object>>();
        map.put("contents", contents);

        for (ChatRecord record : selectList) {

            HashMap<String, Object> item = new HashMap<String, Object>();

            int type = Integer.valueOf(record.getType());
            item.put("type", type);
            if (type == ChatMessageType.IMAGE || type == ChatMessageType.VIDEO) {
                item.put("content", record.getAttachment());
            } else {
                item.put("content", record.getContent());
            }
            item.put("sendTime", record.getDatetime());

            contents.add(item);
        }

        // 转为string
        String reportContent = (new Hashon()).fromHashMap(map);

        return reportContent;
    }
}
