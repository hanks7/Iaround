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
import net.iaround.tools.StringUtil;
import net.iaround.ui.activity.TitleActivity;

public class EditSignatureActivity extends TitleActivity implements View.OnClickListener {

    private ImageView ivLeft;
    private FrameLayout flLeft;
    private TextView tvTitle;
    private EditText etUserSignature;
    final int maxLen = 14;
    private int selEndIndex;
    private TextView tvCountCurrent;
    /**
     * 判断从哪里跳转过来  0 编辑个人资料  1 创建聊吧
     */
    private int isFrom;
    private TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, null, null, true, 0, null, null);
        setContent(R.layout.activity_edit_signature);

        tvTitle = findView(R.id.tv_title);
        ivLeft = findView(R.id.iv_left);
        flLeft = findView(R.id.fl_left);
        etUserSignature = findView(R.id.et_user_signature);
        tvCountCurrent = (TextView) findViewById(R.id.tv_edit_signnature_count_curr);
        tvSave = (TextView) findViewById(R.id.tv_right);
        tvSave.setText(getResources().getString(R.string.pay_alipay_Ensure));
        tvSave.setVisibility(View.VISIBLE);
        tvSave.setOnClickListener(this);

        etUserSignature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = etUserSignature.getText();
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

                    if (mTextMaxlenght > 140) {

                        // 截取最大的字段

                        String newStr = str.substring(0, i);

                        etUserSignature.setText(newStr);

                        // 得到新字段的长度值

                        editable = etUserSignature.getText();

                        int newLen = editable.length();

                        if (selEndIndex > newLen) {

                            selEndIndex = editable.length();

                        }

                        // 设置新光标所在的位置

                        Selection.setSelection(editable, selEndIndex);

                        Toast.makeText(EditSignatureActivity.this, getResources().getString(R.string.text_too_long), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = etUserSignature.getText().toString();
//                tvCountCurrent.setText(content.length() + "");
                tvCountCurrent.setText(StringUtil.getLengthCN1(content) + "");
            }
        });
    }

    private void initDatas() {
        Intent intent = getIntent();
        String signature = intent.getStringExtra(Constants.INFO_CONTENT);//EDIT_RETURN_INFO
        isFrom = intent.getIntExtra("isFrom", 0);
        if (isFrom == 0) {
            tvTitle.setText(getString(R.string.edit_signature));
            if (!TextUtils.isEmpty(signature) && !signature.equals(getString(R.string.edit_signature_tips))) {
                etUserSignature.setText(signature);
                etUserSignature.setSelection(signature.length());
            }
        } else if (isFrom == 1) {
            tvTitle.setText(R.string.create_chatbar_edit_chatbarcontent_title);
            etUserSignature.setHint(R.string.create_chatbar_edit_chatbarcontent_hint);
            if (!TextUtils.isEmpty(signature) && !signature.equals(getString(R.string.create_chatbar_edit_chatbarcontent_title))) {

                etUserSignature.setText(signature);
                etUserSignature.setSelection(signature.length());
            }
        }

    }

    private void initListeners() {
        flLeft.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
    }

    public static void actionStartForResult(Activity mActivity, String content, int reqCode) {
        Intent startIntent = new Intent(mActivity, EditSignatureActivity.class);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        mActivity.startActivityForResult(startIntent, reqCode);
    }

    public static void actionStartForResult(Activity mActivity, String content, int isFrom, int reqCode) {
        Intent startIntent = new Intent(mActivity, EditSignatureActivity.class);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        startIntent.putExtra("isFrom", isFrom);
        mActivity.startActivityForResult(startIntent, reqCode);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            saveSign();
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
                Intent data = new Intent();
                data.putExtra(Constants.EDIT_RETURN_INFO, etUserSignature.getText().toString());
                setResult(Activity.RESULT_OK, data);
                finish();
                break;
            case R.id.tv_right:
                saveSign();
                break;
        }
    }

    public void saveSign() {
        Intent data = new Intent();
        data.putExtra(Constants.EDIT_RETURN_INFO, etUserSignature.getText().toString());
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
