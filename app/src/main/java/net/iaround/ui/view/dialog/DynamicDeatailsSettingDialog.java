package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;

/**
 * Title: 动态详情设置弹框样式
 * Author：GH on 2017/1/08 16:09
 * Email：jt_gaohang@163.com
 */
public class DynamicDeatailsSettingDialog extends Dialog {
    public static final int COPY_COMMENT = 1;
    public static final int REPORT = 2;
    public static final int DELETE = 3;
    public static final int OTHERINFO_FOCUS = 4;
    public static final int OTHERINFO_NO_RELATION = 5;

    private FrameLayout llBackground;

    private TextView tvDelete;

    private LinearLayout lyReport;

    private LinearLayout lvOtherNotice;
    private LinearLayout lvMineNotice;
    private LinearLayout lvReportUser;

    private LinearLayout lyComment;
    private TextView tvCopy;
    private TextView tvCommentDelete;

    private TextView tvCancel;

    private ItemOnclick itemOnclick;
    private LinearLayout lyOtherinfoSetting;
    private LinearLayout lvBlacklist;
    private LinearLayout lvRemarks;

    private int seeHimDynamic;
    private int myDynamic;
    private int report;
    private int blackList;
    private int note;

    private ImageView ivSeeHim;
    private ImageView ivMyDynamic;
    private ImageView ivReport;
    private ImageView ivBlackList;
    private ImageView ivNote;

    private TextView tvSeeHim;
    private TextView tvMyDynamic;
    private TextView tvReport;
    private TextView tvBlackList;
    private TextView tvNote;
    private Context context;

    public DynamicDeatailsSettingDialog(Context context, int seeHimDynamic, int myDynamic, int report, int blackList, int note) {
        super(context, R.style.transparent_dialog);
        this.context = context;
        this.seeHimDynamic = seeHimDynamic;
        this.myDynamic = myDynamic;
        this.report = report;
        this.blackList = blackList;
        this.note = note;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_dynamic_details_setting);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        llBackground = (FrameLayout) findViewById(R.id.ly_dynamic_details);
        initView();
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
    }

    public void setCopyComment(boolean isMine,long userId) {

        init(COPY_COMMENT);
        if (userId == Common.getInstance().loginUser.getUid())
        {
            tvCommentDelete.setVisibility(View.VISIBLE);
        }else
        {
            tvDelete.setVisibility(View.GONE);
            if (isMine) {
                tvCommentDelete.setVisibility(View.VISIBLE);
            } else {
                tvCommentDelete.setVisibility(View.GONE);
            }
        }
    }
    public void setCopyComment(boolean isMine) {

        init(COPY_COMMENT);
        if (isMine) {
            tvCommentDelete.setVisibility(View.VISIBLE);
        } else {
            tvCommentDelete.setVisibility(View.GONE);
        }
    }

    private void initView() {
        tvDelete = (TextView) findViewById(R.id.tv_pop_dynamic_details_delete);

        lyReport = (LinearLayout) findViewById(R.id.ly_pop_dynamic_details_report);
        lvOtherNotice = (LinearLayout) findViewById(R.id.ly_pop_dynamic_details_other_notice);
        ivSeeHim = (ImageView) findViewById(R.id.iv_pop_dynamic_details_other_notice);
        tvSeeHim = (TextView) findViewById(R.id.tv_pop_dynamic_details_other_notice);
        if (0 == seeHimDynamic) {
            ivSeeHim.setImageResource(R.drawable.userinfo_more_no_seehim);//userinfo_more_seehim    userinfo_more_seehim
            tvSeeHim.setText(context.getResources().getString(R.string.userinfo_more_no_seehim));
        } else {
            ivSeeHim.setImageResource(R.drawable.userinfo_more_seehim);
            tvSeeHim.setText(context.getResources().getString(R.string.userinfo_more_seehim));
        }
        lvMineNotice = (LinearLayout) findViewById(R.id.ly_pop_dynamic_details_mine_notice);
        ivMyDynamic = (ImageView) findViewById(R.id.iv_pop_dynamic_details_mine_notice);
        tvMyDynamic = (TextView) findViewById(R.id.tv_pop_dynamic_details_mine_notice);
        if (0 == myDynamic) {
            ivMyDynamic.setImageResource(R.drawable.userinfo_more_reject_see_mydynamic);//userinfo_more_see_mydynamic  userinfo_more_see_my_dynamic
            tvMyDynamic.setText(context.getResources().getString(R.string.userinfo_more_reject_hime_see_mydynamic));
        } else {
            ivMyDynamic.setImageResource(R.drawable.userinfo_more_see_mydynamic);
            tvMyDynamic.setText(context.getResources().getString(R.string.userinfo_more_see_my_dynamic));
        }
        lvReportUser = (LinearLayout) findViewById(R.id.ly_pop_dynamic_details_report_user);
        ivReport = (ImageView) findViewById(R.id.iv_report_user);
        tvReport = (TextView) findViewById(R.id.tv_report_user);
        ivReport.setImageResource(R.drawable.userinfo_more_report);


        lyOtherinfoSetting = (LinearLayout) findViewById(R.id.ly_pop_other_info_setting);
        lvBlacklist = (LinearLayout) findViewById(R.id.ly_pop_black_list);
        ivBlackList = (ImageView) findViewById(R.id.iv_black_list);
        tvBlackList = (TextView) findViewById(R.id.tv_black_list);
        if (1 == blackList) {
            ivBlackList.setImageResource(R.drawable.userinfo_more_add_blacklist);//userinfo_more_add_blacklist
            tvBlackList.setText(context.getResources().getString(R.string.userinfo_more__no_blacklist));
        } else {
            ivBlackList.setImageResource(R.drawable.userinfo_more_no_blacklist);
            tvBlackList.setText(context.getResources().getString(R.string.userinfo_more_add_blacklist));
        }
        lvRemarks = (LinearLayout) findViewById(R.id.ly_pop_userinfo_remarks);
        ivNote = (ImageView) findViewById(R.id.iv_userinfo_remarks);
        tvNote = (TextView) findViewById(R.id.tv_userinfo_remarks);
        ivNote.setImageResource(R.drawable.otherinfo_more_remarks);


        lyComment = (LinearLayout) findViewById(R.id.ly_dynamic_details_comment);
        tvCopy = (TextView) findViewById(R.id.tv_pop_dynamic_details_copy);
        tvCommentDelete = (TextView) findViewById(R.id.tv_pop_dynamic_details_comment_delete);

        tvCancel = (TextView) findViewById(R.id.tv_pop_dynamic_details_cancel);
    }

    public void init(int type) {
        if (type == COPY_COMMENT) {
            lyComment.setVisibility(View.VISIBLE);
            lyReport.setVisibility(View.GONE);
            lyOtherinfoSetting.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
        } else if (type == REPORT) {
            lyComment.setVisibility(View.GONE);
            lyReport.setVisibility(View.VISIBLE);
            lyOtherinfoSetting.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
        } else if (type == OTHERINFO_FOCUS) {
            lyComment.setVisibility(View.GONE);
            lyReport.setVisibility(View.VISIBLE);
            lyOtherinfoSetting.setVisibility(View.VISIBLE);
            lvRemarks.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.GONE);
        } else if (type == OTHERINFO_NO_RELATION) {
            lyComment.setVisibility(View.GONE);
            lyReport.setVisibility(View.VISIBLE);
            lyOtherinfoSetting.setVisibility(View.VISIBLE);
            lvRemarks.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
        } else {
            lyComment.setVisibility(View.GONE);
            lyReport.setVisibility(View.GONE);
            lyOtherinfoSetting.setVisibility(View.GONE);
            tvDelete.setVisibility(View.VISIBLE);
        }

        tvDelete.setTag("delete");
        tvDelete.setOnClickListener(listener);

        lvOtherNotice.setTag("other_notice");
        lvOtherNotice.setOnClickListener(listener);

        lvMineNotice.setTag("mine_notice");
        lvMineNotice.setOnClickListener(listener);

        lvReportUser.setTag("report_user");
        lvReportUser.setOnClickListener(listener);

        lvBlacklist.setTag("other_blacklist");
        lvBlacklist.setOnClickListener(listener);

        lvRemarks.setTag("other_remarks");
        lvRemarks.setOnClickListener(listener);

        tvCopy.setTag("copy");
        tvCopy.setOnClickListener(listener);

        tvCommentDelete.setTag("comment_delete");
        tvCommentDelete.setOnClickListener(listener);

        tvCancel.setTag("cancel");
        tvCancel.setOnClickListener(listener);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (itemOnclick == null)
                return;
            /*if (view.getTag().equals("delete")){
                itemOnclick.itemOnclick(view);
            }else if (view.getTag().equals("other_notice")){
                itemOnclick.itemOnclick(view);
            }else if (view.getTag().equals("mine_notice")){
                itemOnclick.itemOnclick(view);
            }else if (view.getTag().equals("report_user")){
                itemOnclick.itemOnclick(view);
            }else if (view.getTag().equals("copy")){
                itemOnclick.itemOnclick(view);
            }else if (view.getTag().equals("comment_delete")){
                itemOnclick.itemOnclick(view);
            }else if (view.getTag().equals("other_blacklist")){
                itemOnclick.itemOnclick(view);
            }else if (view.getTag().equals("other_remarks")){
                itemOnclick.itemOnclick(view);
            }*/
            itemOnclick.itemOnclick(view);
            dismiss();
        }
    };

    public int getSeeHimDynamic() {
        return seeHimDynamic;
    }

    public void setSeeHimDynamic(int seeHimDynamic) {
        this.seeHimDynamic = seeHimDynamic;
        initView();
    }

    public int getMyDynamic() {
        return myDynamic;
    }

    public void setMyDynamic(int myDynamic) {
        this.myDynamic = myDynamic;
        initView();
    }

    public int getBlackList() {
        return blackList;
    }

    public void setBlackList(int blackList) {
        this.blackList = blackList;
        initView();
    }

    public void setItemOnclick(ItemOnclick itemOnclick) {
        this.itemOnclick = itemOnclick;
    }

    public interface ItemOnclick {
        void itemOnclick(View view);
    }


}
