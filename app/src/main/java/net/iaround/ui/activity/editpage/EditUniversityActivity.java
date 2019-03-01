package net.iaround.ui.activity.editpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.SchEntity;
import net.iaround.model.entity.SchoolEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.ActionBarActivity;
import net.iaround.ui.adapter.JobAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditUniversityActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView ivLeft;
    private FrameLayout flLeft;
    private ListView lvUniversity;

    private String currentUniveristy;
    private ArrayList<SchoolEntity> universities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setContentView(R.layout.activity_edit_job);
        TextView tvTitle = findView(R.id.tv_title);
        ivLeft = findView(R.id.iv_left);
        flLeft = findView(R.id.fl_left);
        lvUniversity = findView(R.id.lv_job);

        tvTitle.setText(getString(R.string.edit_university));
    }

    private void initDatas() {
        Intent intent = getIntent();
        String university = intent.getStringExtra(Constants.INFO_CONTENT);
        String universityGson = CommonFunction.getGsonFromFile(this, Constants.UNIVERSITY_FILE);
        Type type = new TypeToken<ArrayList<SchoolEntity>>() {
        }.getType();
        universities = GsonUtil.getInstance().json2Bean(universityGson, type);
        lvUniversity.setAdapter(new JobAdapter(university, universities));
        lvUniversity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SchEntity schEntity = new SchEntity(universities.get(position).getSchoolID(), universities.get(position).getSchoolName());
                /*currentUniveristy = GsonUtil.getInstance().getStringFromJsonObject(schEntity);
                JobAdapter jobAdapter = (JobAdapter) parent.getAdapter();
                jobAdapter.setCurrentJob(universities.get(position).getSchoolName());
                jobAdapter.notifyDataSetChanged();*/
               currentUniveristy = schEntity.getSchoolName() + "," + schEntity.getSchoolID();
//                currentUniveristy = schEntity.getSchoolName();
                Intent data = new Intent();
                data.putExtra(Constants.EDIT_RETURN_INFO, currentUniveristy);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    private void initListeners() {
        flLeft.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
            case R.id.fl_left:
                Intent data = new Intent();
                data.putExtra(Constants.EDIT_RETURN_INFO, currentUniveristy);
                setResult(Activity.RESULT_OK, data);
                finish();
                break;
        }
    }

    public static void actionStartForResult(Activity mActivity, String content, int reqCode) {
        Intent startIntent = new Intent(mActivity, EditUniversityActivity.class);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        mActivity.startActivityForResult(startIntent, reqCode);
    }


}
