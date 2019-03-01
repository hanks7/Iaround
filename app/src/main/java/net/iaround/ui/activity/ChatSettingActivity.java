package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.entity.type.ChatFromType;
import net.iaround.entity.type.ReportTargetType;
import net.iaround.model.database.FriendModel;
import net.iaround.model.type.ReportType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.ui.activity.editpage.EditNicknameActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.ui.group.activity.ReportChatAcitvity;
import net.iaround.ui.space.more.ChatRecordReport;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.dialog.DialogInterface;
import net.iaround.ui.view.dialog.NormalAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class: 聊天设置
 * Author：gh
 * Date: 2017/1/16 12:27
 * Email：jt_gaohang@163.com
 */
public class ChatSettingActivity extends TitleActivity implements View.OnClickListener, HttpCallBack {

    //标题栏
    private TextView tvTitle;
    private ImageView ivLeft;

    private HeadPhotoView avatar;//头像
    private TextView name;//昵称
    private TextView signture;//个性签名
    private TextView tvRemarks;//备注名称

    private CustomContextDialog reportDialog;
//    private long uid;
    private User user;
    private String nickName;
//    private String icon;
//    private String sign;
//    private int ischat;
//    private String longNote;

    /**
     * 协议请求flag
     */
    private long BACKLIST_FLAG = 100; // 列入黑名单的flag
    private long REPORT_FLAG = 0;//举报

    private long UPDATE_NOTE_FLAG = -1;// 上传新长备注

    private long getNoteFlag;

    private final int reportTargetType = ReportTargetType.CHAT_RECORD;
    private String remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting);

        initView();
        initActionbar();
        initData();

    }

    private void initActionbar() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivLeft = (ImageView) findViewById(R.id.iv_left);

        tvTitle.setText(getResources().getString(R.string.chat_setting_title));
        ivLeft.setImageResource(R.drawable.title_back);

        findViewById(R.id.fl_left).setOnClickListener(this);
        ivLeft.setOnClickListener(this);
    }

    private void initView() {

        avatar = (HeadPhotoView) findViewById(R.id.iv_chat_avatar);
        name = (TextView) findViewById(R.id.tv_chat_name);
        signture = (TextView) findViewById(R.id.tv_chat_signature);
        tvRemarks = (TextView) findViewById(R.id.tv_chat_remarks);

        findViewById(R.id.ly_chat_avatar).setOnClickListener(itemClick);
        findViewById(R.id.ly_chat_remarks).setOnClickListener(itemClick);
        findViewById(R.id.ly_chat_black).setOnClickListener(itemClick);
        findViewById(R.id.ly_chat_report).setOnClickListener(itemClick);
        findViewById(R.id.ly_chat_clear).setOnClickListener(itemClick);

    }

    private void initData() {
        //初始化举报对话框
        reportDialog = new CustomContextDialog(this, 1);
        reportDialog.setListenner(reportClick);

        Intent intent = getIntent();
//        uid = getIntent().getLongExtra("identify", 0);
//        sign = getIntent().getStringExtra("signture");
//        icon = intent.getStringExtra("ficon");
//        nickName = intent.getStringExtra("fnickname");
//        ischat = intent.getIntExtra("ischat", 0);
        user = (User) intent.getSerializableExtra("user");

        nickName = user.getNoteName(true);
        remark = user.getNoteName();
        if (remark != null)
        {
            if (TextUtils.equals("null",remark))
                remark = "";
            name.setText(FaceManager.getInstance(this)
                    .parseIconForString( name,this,nickName,16));
            tvRemarks.setText(remark);
        }else
        {
//            String noteName = user.getNoteName(true);
            name.setText(FaceManager.getInstance(this)
                    .parseIconForString(name,this,nickName,16));
            SpannableString personalRemark = FaceManager.getInstance(this).parseIconForString(
                        tvRemarks,this,remark,16);
            tvRemarks.setText(personalRemark);
        }
//        GlideUtil.loadCircleImage(ChatSettingActivity.this,icon.toString(),avatar);
        avatar.execute(ChatFromType.UNKONW, user, null);
        avatar.clickable(false);
        if (user.getSign() != null && !TextUtils.isEmpty(user.getSign())) {
//            sign = getIntent().getStringExtra("signture");
            SpannableString personalSign = FaceManager.getInstance(mContext)
                    .parseIconForString(signture, mContext,
                            user.getSign(), 13);
            signture.setText(personalSign);
        } else {
            signture.setText(getResString(R.string.signature_empty_tips));
        }

        //拉取后台数据 备注 YC暂时取消
//        getNoteFlag = BusinessHttpProtocol.getLongNote(this, uid, this);

    }

    /**
     * 举报中条目的点击事件
     */
    private View.OnClickListener reportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int type = ReportType.OTHER;
            switch ((int) view.getTag()) {
                case 0:// 色情
                    type = ReportType.SEX;
                    break;
                case 1:// 广告
                    type = ReportType.ADVERTISEMENT;
                    break;
                case 2:// 骚扰
                    type = ReportType.DISTURB;
                    break;
                case 3:// 欺诈
                    type = ReportType.REACTIONARY;
                    break;
            }
            reportDialog.hide();
            showWaitDialog();
            REPORT_FLAG = UserHttpProtocol.systemReport(mContext, type, reportTargetType,
                    String.valueOf(user.getUid()), null, ChatSettingActivity.this);
        }
    };


    /**
     * 查看个人资料，拉黑，备注，举报，清空聊天记录
     * 点击事件处理
     */
    private View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ly_chat_avatar://查看个人资料
                    Intent intent = new Intent(ChatSettingActivity.this, OtherInfoActivity.class);
//                    intent.putExtra(Constants.UID,Long.valueOf(uid));
                    intent.putExtra("user", user);
                    intent.putExtra("chat_set", true);
                    intent.putExtra(Constants.UID, user.getUid());
                    startActivityForResult(intent,204);
                    break;
                case R.id.ly_chat_remarks://备注
//                    String remarks = tvRemarks.getText().toString();
                    if (user.getRelationship() == User.RELATION_FRIEND || user.getRelationship() == User.RELATION_MYSELF
                            || user.getRelationship() == User.RELATION_FOLLOWING) {//好友或自己才可以设置备注
                        EditNicknameActivity editNicknameActivity = new EditNicknameActivity();
                        editNicknameActivity.actionStartForResult(ChatSettingActivity.this, remark, user.getUid(), 201, 2);
                    } else {
                        Toast.makeText(ChatSettingActivity.this, getResString(R.string.other_info_no_friend_remarks), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.ly_chat_black://拉黑
//                    showWaitDialog();
                    String msg = String.format(getString(R.string.add_black_list_comf),
                            user.getNickname());
                    DialogUtil.showOKCancelDialog(ChatSettingActivity.this, getString(R.string.add_black), msg,
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    try {
                                        BACKLIST_FLAG = UserHttpProtocol.userDevilAdd(ChatSettingActivity.this, user.getUid(), ChatSettingActivity.this);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        Toast.makeText(ChatSettingActivity.this, getResString(R.string.network_req_failed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    break;
                case R.id.ly_chat_report://举报
//                    reportDialog.show();
                    new NormalAlertDialog.Builder(ChatSettingActivity.this)
                            .setTitleText(getResString(R.string.prompt))
                            .setContentText(getResString(R.string.sure_black_and_report))
                            .setLeftButtonText(getString(R.string.setting_clear_cached_dialog_left))
                            .setRightButtonText(getString(R.string.register_right_title_btn))
                            .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
                                @Override
                                public void clickLeftButton(NormalAlertDialog dialog, View view) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void clickRightButton(NormalAlertDialog dialog, View view) {
                                    dialog.dismiss();
                                    blackReport(user.getUid());
                                }
                            })
                            .build().show();
                    break;
                case R.id.ly_chat_clear://清空聊天记录
                    new NormalAlertDialog.Builder(ChatSettingActivity.this)
                            .setTitleText(getResString(R.string.menu_clear_history))
                            .setContentText(getResString(R.string.comfirm_to_clear_group_chat_history))
                            .setLeftButtonText(getString(R.string.setting_clear_cached_dialog_left))
                            .setRightButtonText(getString(R.string.setting_clear_cached_dialog_right))
                            .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
                                @Override
                                public void clickLeftButton(NormalAlertDialog dialog, View view) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void clickRightButton(NormalAlertDialog dialog, View view) {
                                    dialog.dismiss();
                                    clearHistory(user.getUid());
                                }
                            })
                            .build().show();

                    break;

            }
        }
    };

    /**
     * Modifier：kevinsu Reason：因为这样如果不想举报了，已经被拉黑，BugFreeID：4741 Date：2014.3.14
     *
     * @param fuid 对方id
     */
    private void blackReport(long fuid) {
        // 举报
        Intent intent = new Intent(this, ReportChatAcitvity.class);
        intent.putExtra(ReportChatAcitvity.USER_ID_KEY, fuid);
        intent.putExtra(ReportChatAcitvity.REPORT_FROM_KEY, ChatRecordReport.TYPE_PERSON);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201 && resultCode == Activity.RESULT_OK) {
            remark = data.getStringExtra(Constants.EDIT_RETURN_INFO);
            if (remark == null || TextUtils.isEmpty(remark) || "null".equals(remark) || "".equals(remark))
            {
                remark = "";
                user.setNoteName(remark);
                tvRemarks.setText(remark);
                if (TextUtils.isEmpty(user.getNickname()) | "null".equals(user.getNickname())){
                    name.setText(""+user.getUid());
                }else{
                    name.setText(FaceManager.getInstance(this).parseIconForString(
                            name,this,user.getNickname(),16));
                }

            }else
            {
                user.setNoteName(remark);
                tvRemarks.setText(FaceManager.getInstance(this).parseIconForString(
                        tvRemarks,this,remark,16));
                name.setText(FaceManager.getInstance(this).parseIconForString(
                        name,this,remark,16));
//                nickName = remark;
            }
//            UPDATE_NOTE_FLAG = BusinessHttpProtocol
//                    .updateLongNote(mContext, uid, remark, this);

//            UPDATE_NOTE_FLAG = UserHttpProtocol.userNotesSetname(mContext, uid, remark,this);

        }else if (requestCode == 204 && resultCode == Activity.RESULT_OK){
            user = (User) data.getSerializableExtra("user");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            data.putExtra(Constants.EDIT_RETURN_INFO, remark);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fl_left || v.getId() == R.id.iv_left) {
            Intent data = new Intent();
            data.putExtra(Constants.EDIT_RETURN_INFO,remark);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
       /* if (flag == UPDATE_NOTE_FLAG) {// 更新长备注
            try {
//                if (!result.contains("remarks"))
//                    return;
                JSONObject obj = new JSONObject(result);
                if (obj.optInt("status") != 200) {
                    CommonFunction.toastMsg(mContext, R.string.note_fail);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else*/ if (flag == BACKLIST_FLAG) { // 成功列入黑名单
            handleAddToBlackListSucc();
        } else if (REPORT_FLAG == flag) {
            if (Constant.isSuccess(result))
                CommonFunction.toastMsg(mContext, R.string.report_return_title);
            else
                ErrorCode.showError(mContext, result);
            setResult(ChatPersonal.RESULT_ADDBLACK);
            finish();
        } else if ((flag == getNoteFlag)) {//备注
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.has("status") && obj.optLong("status") == 200) {
                    //YC 取消获取后台备注数据
//                    longNote = CommonFunction.jsonOptString(obj, "remarks");
//                     if ( !CommonFunction.isEmptyOrNullStr( longNote ) )
//                    tvRemarks.setText(longNote);
//
//                    SpannableString note = FaceManager.getInstance(this).parseIconForString(tvRemarks, this, longNote, 13);
//                    if (note != null) {
//                        tvRemarks.setText(note);
//                    }
//                    FaceManager.getInstance( this ).parseIconForEditText( this ,tvRemarks );
//                    String mContent = tvRemarks.getText( ).toString( );
//                    tvRemarks.setSelection( Math.max( 0 , mContent.length( ) ) );
                } else {
                    CommonFunction.log("sherlock", "error " + result + " ---- "
                            + this.getClass().getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        hideWaitDialog();
        if (flag == UPDATE_NOTE_FLAG) {
            ErrorCode.toastError(mContext, e);
        } else if (REPORT_FLAG == flag) {
            CommonFunction.toastMsg(mContext, R.string.operate_fail);
            finish();
        }
    }

    /**
     * 成功列入黑名单
     */
    private void handleAddToBlackListSucc() {
        CommonFunction.showToast(mContext, getResString(R.string.add_black_suc), 0);

        FriendModel.getInstance(mContext).deleteFollow(user.getUid());
        MessageModel.getInstance().deleteNearContactRecord(mContext, user.getUid());
        UserBufferHelper.getInstance().remove(user.getUid());
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("addblack", ChatPersonal.RESULT_ADDBLACK);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
//        setResult( ChatPersonal.RESULT_ADDBLACK );//yuchao resultCode为-1  通过bundle传递的值判断进行后续判断
        finish();
    }

    // 清空聊天记录
    private void clearHistory(long fuid) {
        // 删除本地私信消息
        ChatPersonalModel.getInstance()
                .deleteRecord(this, String.valueOf(Common.getInstance().loginUser.getUid()), String.valueOf(fuid));
        DatabaseFactory.getNearContactWorker(mContext)
                .deleteRecord(String.valueOf(Common.getInstance().loginUser.getUid()), String.valueOf(fuid));
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("clearList", ChatPersonal.RESULT_CLEAR_LIST);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
//        setResult( ChatPersonal.RESULT_CLEAR_LIST ); //yuchao
        finish();
    }
}
