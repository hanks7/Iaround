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

public class EditCompanyActivity extends TitleActivity implements View.OnClickListener {

    private ImageView ivLeft;
    private FrameLayout flLeft;
    private EditText etUserCompany;
    private ImageView ivDelete;
    private TextView tvEditCompany;
    private TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, null, getString(R.string.edit_company), true, 0, null, null);
        setContent(R.layout.activity_edit_company);
        flLeft = findView(R.id.fl_left);
        ivLeft = findView(R.id.iv_left);
        etUserCompany = findView(R.id.et_user_signature);
        ivDelete = findView(R.id.iv_delete);
        tvEditCompany = (TextView) findViewById(R.id.tv_edit_company_count_curr);
        tvSave = (TextView) findViewById(R.id.tv_right);
        tvSave.setVisibility(View.VISIBLE);
        tvSave.setText(getResources().getString(R.string.pay_alipay_Ensure));
        etUserCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = etUserCompany.getText();
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

                    if (mTextMaxlenght > 30) {

                        // 截取最大的字段

                        String newStr = str.substring(0, i);

                        etUserCompany.setText(newStr);

                        // 得到新字段的长度值

                        editable = etUserCompany.getText();

                        int newLen = editable.length();

                        if (selEndIndex > newLen) {

                            selEndIndex = editable.length();

                        }

                        // 设置新光标所在的位置

                        Selection.setSelection(editable, selEndIndex);

                        Toast.makeText(EditCompanyActivity.this,getResources().getString(R.string.text_too_long), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = etUserCompany.getText().toString();
                tvEditCompany.setText(StringUtil.getLengthCN1(content) + "");
            }
        });

    }

    private void initDatas() {
        Intent intent = getIntent();
        String company = intent.getStringExtra(Constants.INFO_CONTENT);
        if (!TextUtils.isEmpty(company) && !company.equals(getString(R.string.edit_company_empty_tips))) {
            etUserCompany.setText(company);
            etUserCompany.setSelection(company.length());
        }
    }

    private void initListeners() {
        ivLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    public static void actionStartForResult(Activity mActivity, String content, int reqCode) {
        Intent startIntent = new Intent(mActivity, EditCompanyActivity.class);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        mActivity.startActivityForResult(startIntent, reqCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
            case R.id.fl_left:
                back();
                break;
            case R.id.iv_delete:
                etUserCompany.setText("");
                break;
            case R.id.tv_right:
                back();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            back();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void back() {
        Intent data = new Intent();
        data.putExtra(Constants.EDIT_RETURN_INFO, etUserCompany.getText().toString());
        setResult(Activity.RESULT_OK, data);
        finish();
    }

}
