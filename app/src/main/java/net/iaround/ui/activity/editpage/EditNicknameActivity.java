package net.iaround.ui.activity.editpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.StringUtil;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.utils.eventbus.NoteNameNotifyEvent;

import org.greenrobot.eventbus.EventBus;

public class EditNicknameActivity extends TitleActivity implements View.OnClickListener {

    private ImageView ivLeft;
    private FrameLayout flLeft;
    private TextView tvTitle;
    private EditText etUserNicknam;
    private ImageView ivDelete;
    private TextView tvEditNickName;
    private String nickname;
    private String strContent;
    private TextView tvSaveNickName;
    private TextView tvEditNickNameNum;


    /**
     * 判断是从圈子/个人信息 跳转到修改名称界面
     * 2  编辑他人备注
     * 1  编辑个人资料
     * 0  编辑圈子名称
     */
    private static int FROM_GROUP_EDIT_NAME = 0;
    private static int FROM_USER_EDIT_NAME = 1;
    private int isFrom = 0;
    private long targetUserId;

    //    private TextView tv_hint_out_of_nickNameLength,tv_hint_empty_of_nickName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        Intent intent = getIntent();
        nickname = intent.getStringExtra(Constants.INFO_CONTENT);
        isFrom = intent.getIntExtra("isFrom", FROM_USER_EDIT_NAME);
        targetUserId = intent.getLongExtra("userId", 0);

        setTitle_LCR(false, R.drawable.title_back, null, null, null, true, 0, null, null);
        setContent(R.layout.activity_edit_nickname);

        tvTitle = findView(R.id.tv_title);
        flLeft = findView(R.id.fl_left);
        ivLeft = findView(R.id.iv_left);
        etUserNicknam = findView(R.id.et_user_nickname);
        ivDelete = findView(R.id.iv_delete);
        tvEditNickName = (TextView) findViewById(R.id.tv_edit_nickname_count_current);
        tvSaveNickName = (TextView) findViewById(R.id.tv_right);
        tvSaveNickName.setVisibility(View.VISIBLE);
        tvSaveNickName.setText(getResources().getString(R.string.pay_alipay_Ensure));
        tvSaveNickName.setOnClickListener(this);
        tvEditNickNameNum = (TextView) findViewById(R.id.tv_edit_nickname_count_total);

//        tv_hint_out_of_nickNameLength = (TextView) findViewById(R.id.tv_hint_out_of_nickNameLength);
//        tv_hint_empty_of_nickName = (TextView) findViewById(R.id.tv_hint_empty_of_nickName);

        if (isFrom == FROM_GROUP_EDIT_NAME) {
            etUserNicknam.setHint(getResources().getString(R.string.group_edit_nickname_hint));
            tvTitle.setText(R.string.create_chatbar_edit_chatbarname_title);
        } else if (isFrom == FROM_USER_EDIT_NAME) {
            etUserNicknam.setHint(getResources().getString(R.string.edit_nickname_tips));
            tvTitle.setText(getString(R.string.edit_nickname));
        }
        etUserNicknam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = etUserNicknam.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);

                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，

                //如果是汉字则为两个字符

                for (int i = 0; i < str.length(); i++) {

                    char charAt = str.charAt(i);

                    //32-122包含了空格，大小写字母，数字和一些常用的符号，

                    //如果在这个范围内则算一个字符，

                    //如果不在这个范围比如是汉字的话就是两个字符

                    if (charAt >= 32 && charAt <= 122) {

                        mTextMaxlenght++;

                    } else {

                        mTextMaxlenght += 2;

                    }

                    // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小

                    if (isFrom == 0)
                    {
                        tvEditNickNameNum.setText(""+16);
                        if (mTextMaxlenght > 16) {

                            // 截取最大的字段

                            String newStr = str.substring(0, i);

                            etUserNicknam.setText(newStr);

                            // 得到新字段的长度值

                            editable = etUserNicknam.getText();

                            int newLen = editable.length();

                            if (selEndIndex > newLen) {

                                selEndIndex = editable.length();

                            }

                            // 设置新光标所在的位置

                            Selection.setSelection(editable, selEndIndex);
//                        if (s.length() == 14) {
//                            Toast.makeText(EditNicknameActivity.this, "最大长度为16个字符或8个汉字！", Toast.LENGTH_SHORT).show();
//                        }
                            Toast.makeText(EditNicknameActivity.this, getResources().getString(R.string.text_too_long), Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        tvEditNickNameNum.setText(""+14);
                        if (mTextMaxlenght > 14) {

                            // 截取最大的字段

                            String newStr = str.substring(0, i);

                            etUserNicknam.setText(newStr);

                            // 得到新字段的长度值

                            editable = etUserNicknam.getText();

                            int newLen = editable.length();

                            if (selEndIndex > newLen) {

                                selEndIndex = editable.length();

                            }

                            // 设置新光标所在的位置

                            Selection.setSelection(editable, selEndIndex);
//                        if (s.length() == 14) {
//                            Toast.makeText(EditNicknameActivity.this, "最大长度为16个字符或8个汉字！", Toast.LENGTH_SHORT).show();
//                        }
                            Toast.makeText(EditNicknameActivity.this, getResources().getString(R.string.text_too_long), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = etUserNicknam.getText().toString();
//                tvCountCurrent.setText(content.length() + "");
                tvEditNickName.setText(StringUtil.getLengthCN1(content) + "");
            }
        });

    }

    private void initDatas() {
//        Intent intent = getIntent();
//        String nickname = intent.getStringExtra(Constants.INFO_CONTENT);
//        isFrom = intent.getIntExtra("isFrom", FROM_USER_EDIT_NAME);
//        targetUserId = intent.getLongExtra("userId", 0);
        if (nickname == null)return;
        char[] chars = nickname.toCharArray();
        if (!TextUtils.isEmpty(nickname) && !nickname.equals(getString(R.string.edit_nickname_tips)) && chars.length <= 14) {
            etUserNicknam.setText(nickname);
            etUserNicknam.setSelection(etUserNicknam.getText().toString().length());
        } else if (!TextUtils.isEmpty(nickname) && !nickname.equals(getString(R.string.edit_nickname_tips)) && chars.length > 14) {
            String nickn = nickname.substring(0, 8);
            etUserNicknam.setText(nickn);
            etUserNicknam.setSelection(etUserNicknam.getText().toString().length());
        }
    }

    private void initListeners() {
        ivLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
    }

    public static void actionStartForResult(Activity mActivity, String content, int reqCode) {
        Intent startIntent = new Intent(mActivity, EditNicknameActivity.class);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        startIntent.putExtra("isFrom", 1);
        mActivity.startActivityForResult(startIntent, reqCode);
    }

    public void actionStartForResult(Activity mActivity, String content, long targetUserId, int reqCode, int isFrom) {
        Intent startIntent = new Intent(mActivity, EditNicknameActivity.class);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        startIntent.putExtra("isFrom", isFrom);
        startIntent.putExtra("userId", targetUserId);
        mActivity.startActivityForResult(startIntent, reqCode);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            onBackFinsh();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
            case R.id.fl_left:
                onBackFinsh();
                break;
            case R.id.iv_delete:
                etUserNicknam.setText("");
                break;
            case R.id.tv_right:
                onBackFinsh();
                break;
        }
    }

    /**
     * 回调信息
     */
    private void onBackFinsh() {
        switch (isFrom) {
            case 0://编辑圈子名称
            case 1://编辑个人昵称
                Intent data = new Intent();
                String nickname = etUserNicknam.getText().toString();
                data.putExtra(Constants.EDIT_RETURN_INFO, nickname);
                setResult(Activity.RESULT_OK, data);
                finish();
                break;
            case 2://编辑个人备注
                editUserNotes();
                break;
        }
    }
//        Intent data = new Intent();
//        String nickname = etUserNicknam.getText().toString();
//        data.putExtra(Constants.EDIT_RETURN_INFO, nickname);
//        setResult(Activity.RESULT_OK, data);
//        finish();
//        if (nickname.equals(R.string.edit_nickname_tips) ||nickname.length() > 8)
//        {
//            Toast.makeText(EditNicknameActivity.this, "哥，文字超限制了", Toast.LENGTH_SHORT).show();
//            return;
//        }else {
//
//        }

    /**
     * 修改用户备注名
     */
    private void editUserNotes() {
        strContent = CommonFunction.filterKeyWordAndReplaceEmoji(
                this, etUserNicknam.getText().toString().trim());// 过滤关键字
        strContent = StringUtil.qj2bj(strContent);

        UserHttpProtocol.userNotesSetname(this, targetUserId, strContent,
                new HttpCallBack() {
                    @Override
                    public void onGeneralSuccess(String result, long flag) {
//                        Toast.makeText(EditNicknameActivity.this,getResources().getString(R.string.userinfo_note_success), Toast.LENGTH_LONG).show();
                        EventBus.getDefault().post(new NoteNameNotifyEvent(strContent, targetUserId));
                    }

                    @Override
                    public void onGeneralError(int e, long flag) {
//                        Toast.makeText(EditNicknameActivity.this,getResources().getString(R.string.userinfo_note_failure) , Toast.LENGTH_LONG).show();
                    }
                });
        Intent intent = new Intent();
        intent.putExtra(Constants.EDIT_RETURN_INFO, strContent);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
