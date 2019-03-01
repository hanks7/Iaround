package net.iaround.ui.seach.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.activity.BaseActivity;

/**
 * 搜索首页
 * Created by gh on 2017/11/3.
 */

public class SearchMainActivity extends BaseActivity implements View.OnClickListener {


    private EditText searchEdit;
    private LinearLayout typeLy;
    private LinearLayout resultLy;
    private TextView userTv;
    private TextView chatbarTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);

        initView();

    }

    private void initView() {

        findViewById(R.id.fl_back).setOnClickListener(this);

        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        titleTv.setText(getString(R.string.search));

        searchEdit = (EditText) findViewById(R.id.et_seach_seach);

        typeLy = (LinearLayout) findViewById(R.id.ly_seach_type);
        resultLy = (LinearLayout) findViewById(R.id.ly_search_result);
        userTv = (TextView) findViewById(R.id.tv_search_user_result);
        chatbarTv = (TextView) findViewById(R.id.tv_search_chatbar_result);

        findViewById(R.id.ly_search_user).setOnClickListener(this);
        findViewById(R.id.ly_search_chabar).setOnClickListener(this);


        userTv.setOnClickListener(this);
        chatbarTv.setOnClickListener(this);

        showSoftInputFromWindow(getActivity());

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    notifyEdit();

                    return true;
                }
                return false;
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                int mTextMaxlenght = 0;
                Editable editable = searchEdit.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);
                for (int i = 0; i < str.length(); i++) {

                    char charAt = str.charAt(i);

                    if (charAt >= 32 && charAt <= 122) {

                        mTextMaxlenght++;

                    } else {

                        mTextMaxlenght += 2;

                    }

                    if (mTextMaxlenght > 14) {

                        // 截取最大的字段

                        String newStr = str.substring(0, i);

                        searchEdit.setText(newStr);

                        // 得到新字段的长度值

                        editable = searchEdit.getText();

                        int newLen = editable.length();

                        if (selEndIndex > newLen) {

                            selEndIndex = editable.length();

                        }

                        // 设置新光标所在的位置
                        Selection.setSelection(editable, selEndIndex);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                notifyEdit();

            }
        });

    }

    /**
     * 时刻监测输入框的状态
     */
    private void notifyEdit() {
        String inputStr = searchEdit.getText().toString();
        if (!TextUtils.isEmpty(inputStr)) {
            typeLy.setVisibility(View.GONE);
            resultLy.setVisibility(View.VISIBLE);
            userTv.setText(Html.fromHtml(getString(R.string.contacts_seach_title)+"<font color='#FF0000'>“" + inputStr + "”</font>"+getString(R.string.contacts_seach_user)));
            chatbarTv.setText(Html.fromHtml(getString(R.string.contacts_seach_title)+"<font color='#FF0000'>“" + inputStr + "”</font>"+getString(R.string.contacts_seach_group)));
        }else{
            typeLy.setVisibility(View.VISIBLE);
            resultLy.setVisibility(View.GONE);
        }
    }


    public static void launch(Activity activity, int reqCode) {
        Intent intent = new Intent(activity, SearchMainActivity.class);
        activity.startActivityForResult(intent, reqCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.ly_search_user:
            case R.id.tv_search_user_result:
                String inputStr = searchEdit.getText().toString();
                Intent intent = new Intent(SearchMainActivity.this, SearchUserActivity.class);
                intent.putExtra("keyword", inputStr);
                startActivity(intent);

                break;
            case R.id.ly_search_chabar:
            case R.id.tv_search_chatbar_result:
                Intent groupIntent = new Intent(SearchMainActivity.this, SearchChatBarActivity.class);
                groupIntent.putExtra("keyword", searchEdit.getText().toString());
                startActivity(groupIntent);

                break;
        }

    }

}
