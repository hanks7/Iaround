package net.iaround.ui.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;

import java.util.ArrayList;


/**
 * Title: 动态详情举报弹框样式
 * Author：GH on 2017/1/08 16:09
 * Email：jt_gaohang@163.com
 */
public class CustomContextDialog extends Dialog {

    private Context mContext;
    private FrameLayout llBackground;
    private LinearLayout llContainer;
    private TextView tvHeader;
    private View dividerView;

    private View.OnClickListener listener;

    private String[] array;
    private int type;
    private boolean isPublishTopic;

    /**
     *
     * @param context
     * @param type 弹出哪个窗口
     */
    public CustomContextDialog(Context context,int type) {
        super(context,R.style.transparent_dialog);
        this.mContext = context;
        this.type = type;
    }

    public CustomContextDialog(Context context,int type,boolean isPublishTopic)
    {
        super(context,R.style.transparent_dialog);
        this.mContext = context;
        this.type = type;
        this.isPublishTopic = isPublishTopic;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_dynamic_details_report);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        dividerView = findViewById(R.id.customDialog_divider);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        llContainer = (LinearLayout) findViewById(R.id.llContainer);
        llBackground = (FrameLayout) findViewById(R.id.llBackground);
        llBackground.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    hide();
                }
                return false;
            }
        });

        //通过判断是否点击话题发表权限设置不同的显示
        if (isPublishTopic && this.type == 5) {
            tvHeader.setVisibility(View.VISIBLE);
            tvHeader.setText(R.string.role_topic_sel_title);
        } else if (!isPublishTopic && this.type == 5) {
            tvHeader.setVisibility(View.VISIBLE);
            tvHeader.setText(R.string.role_talk_sel_title);
        }else if (isPublishTopic && this.type == 8)
        {
            tvHeader.setVisibility(View.VISIBLE);
            tvHeader.setText(R.string.setting_notice_circle_msg_setting);
            dividerView.setVisibility(View.VISIBLE);

        }

        findViewById(R.id.tv_pop_dynamic_details_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        //添加布局
        if (type == 0) {
            array = mContext.getResources().getStringArray(R.array.login_forget_pwd);
        } else if (type == 1) {
            array = mContext.getResources().getStringArray(R.array.dynamic_details_reprot);
        } else if (type == 2) {
            array = mContext.getResources().getStringArray(R.array.group_myself_more);
        } else if (type == 3) {
            array = mContext.getResources().getStringArray(R.array.group_other_more);
        } else if (type == 4) {
            array = mContext.getResources().getStringArray(R.array.group_member_manager);
        } else if (type == 5) {
            array = mContext.getResources().getStringArray(R.array.group_manage);
        } else if (type == 6)
        {
            array = mContext.getResources().getStringArray(R.array.group_search_result_item_click);
        }else if (type == 7)
        {//信息界面:清空消息，标记全部已读
            array = mContext.getResources().getStringArray(R.array.message_fragment);
        }else if (type == 8)
        {//通知界面:圈消息设置
            array = mContext.getResources().getStringArray(R.array.group_message_setting);
        }else if (type == 9)
        {//消息界面圈通知
            array = mContext.getResources().getStringArray(R.array.group_notice);
        }else if (type == 10)
        {//圈子详情页
            array = mContext.getResources().getStringArray(R.array.group_manager_more);
        }else if (type == 11)
        {
            array = mContext.getResources().getStringArray(R.array.group_owner);
        }else if (type == 12)
        {
            array = mContext.getResources().getStringArray(R.array.group_manager);
        }else if (type == 13)
        {
            array = mContext.getResources().getStringArray(R.array.group_manager_search_owner);
        }else if (type == 14)
        {//圈聊中圈主权利
            array = mContext.getResources().getStringArray(R.array.group_role_owner_groupChat);
        }else if (type == 15)
        {//圈聊管理员权利
            array = mContext.getResources().getStringArray(R.array.group_role_manager_groupChat);
        }else if (type == 16)
        {//圈聊中成员
            array = mContext.getResources().getStringArray(R.array.group_role_memeber_groupChat);
        }else if (type == 17)
        {//圈助手
            array = mContext.getResources().getStringArray(R.array.group_helper1);
        }else if (type == 18)
        {//圈助手
            array = mContext.getResources().getStringArray(R.array.group_helper2);
        }else if (type == 19)
        {//圈助手
            array = mContext.getResources().getStringArray(R.array.group_helper3);
        }else if (type == 20)
        {//圈助手
            array = mContext.getResources().getStringArray(R.array.group_helper4);
        }else if (type == 21)
        {//圈话题
            array = mContext.getResources().getStringArray(R.array.group_topic_detail);
        }else if (type == 22)
        {
            array = mContext.getResources().getStringArray(R.array.group_topic_detail_owner);
        }else if (type == 23)
        {
            array = mContext.getResources().getStringArray(R.array.group_topic_detail_manager);
        }else if (type == 24)
        {
            array = mContext.getResources().getStringArray(R.array.group_topic_report);
        }else if (type == 25)
        {//取消置顶
            array = mContext.getResources().getStringArray(R.array.group_topic_detail_cancle_top);
        }else if (type == 26)
        {//取消精华
            array = mContext.getResources().getStringArray(R.array.group_topic_detail_cancle_best);
        }else if (type == 27)
        {
            array = mContext.getResources().getStringArray(R.array.group_topic_copy_review);
        }else if (type == 28)
        {
            array = mContext.getResources().getStringArray(R.array.group_topic_delete_review);
        }else if (type == 29)
        {
            array = mContext.getResources().getStringArray(R.array.group_topic_detail_cancle_both);
        }else if (type == 30)
        {//聊吧资料，粉丝可执行的操作
            array = mContext.getResources().getStringArray(R.array.group_fans_more);
        }else if (type == 31)
        {//聊吧资料，游客可执行的操作
            array = mContext.getResources().getStringArray(R.array.group_visitor_more);
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (String str : array){
            arrayList.add(str);
        }

        init(arrayList);
    }

    public void setListenner( View.OnClickListener litener){
        this.listener = litener;
    }

    public void init(ArrayList<String> list) {
        if(list == null || list.size() <= 0)
        {
            return;
        }

        for(int i = 0; i < list.size(); i++)
        {
                addItemView(list,i);
        }
    }

    private void addItemView(ArrayList<String> list,int i) {
        View view = View.inflate(mContext, R.layout.view_custom_context, null);
//        View view1 = View.inflate(mContext,R.layout.custom_user_info,null);
        TextView tvName = (TextView) view.findViewById(R.id.tv_custom_context_item);
        View line =  view.findViewById(R.id.line_custom_context_item);
//        取消最后一条线
        if (list.size() == i+1){
            line.setVisibility(View.GONE);
        }
        tvName.setText(list.get(i));
        tvName.setTag(i);

        tvName.setOnClickListener(listener);
        llContainer.addView(view);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
