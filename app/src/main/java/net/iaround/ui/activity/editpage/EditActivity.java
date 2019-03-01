package net.iaround.ui.activity.editpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.ui.activity.TitleActivity;


/**
 * 使命已经完成，可以光荣牺牲
 */
public class EditActivity extends TitleActivity {

    private TextView tvTitle;
    private EditText etDes;
    private TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, 0, "取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null, false, 0, "保存", null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_edit);
        tvTitle = findView(R.id.tv_title);
        tvSave = findView(R.id.tv_right);
        etDes = findView(R.id.et_des);
    }

    private void initDatas() {
        String title = getIntent().getStringExtra(Constants.INFO_TITLE);
        tvTitle.setText(title);
        String content = getIntent().getStringExtra(Constants.INFO_CONTENT);
        if(!TextUtils.isEmpty(content)){
            etDes.setText(content);
        } else {
            etDes.setHint("请编辑你的内容");
        }
    }

    private void initListeners() {
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(Constants.EDIT_RETURN_INFO, etDes.getText().toString());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    public static void actionStartForResult(Activity mActivity, String title, String content, int reqCode){
        Intent startIntent = new Intent(mActivity, EditActivity.class);
        startIntent.putExtra(Constants.INFO_TITLE, title);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        mActivity.startActivityForResult(startIntent, reqCode);
    }
}
