package net.iaround.ui.activity.editpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.SchoolEntity;
import net.iaround.ui.activity.ActionBarActivity;
import net.iaround.ui.adapter.JobAdapter;

import java.util.ArrayList;

public class EditJobActivity extends ActionBarActivity {

    //    private ImageView ivLeft;
    private ListView lvJob;
    private RelativeLayout jobLayout;

    private String currentJob;
    private String[] jobs;
    private ArrayList<SchoolEntity> jobList = new ArrayList<>();
    private FrameLayout flLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
        initViews();
        initDatas();
//        initListeners();
    }

    private void initViews() {
        setActionBarTitle(R.string.edit_job);
        jobLayout = (RelativeLayout) findViewById(R.id.rl_job);
//        ivLeft = findViewById(R.id.iv_left);
        lvJob = (ListView) findViewById(R.id.lv_job);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        flLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initDatas() {
        Intent intent = getIntent();
        final String job = intent.getStringExtra(Constants.INFO_CONTENT);
        jobs = getResStringArr(R.array.job);
        jobList.clear();
        for (int i = 0; i < jobs.length; i++) {
            jobList.add(new SchoolEntity(i, jobs[i]));
        }
        lvJob.setAdapter(new JobAdapter(job, jobList));
        lvJob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentJob = position + "";//+ 1
                JobAdapter jobAdapter = (JobAdapter) parent.getAdapter();
                jobAdapter.setCurrentJob(jobList.get(position).getSchoolName());
                jobAdapter.notifyDataSetChanged();
                getJobForUserInfo(currentJob);
            }
        });
    }

//    private void initListeners() {
//        ivLeft.setOnClickListener(this);
//    }

    private void getJobForUserInfo(String currentJob) {
        Intent data = new Intent();
        data.putExtra(Constants.EDIT_RETURN_INFO, currentJob);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.iv_left:
//                Intent data = new Intent();
//                data.putExtra(Constants.EDIT_RETURN_INFO, currentJob);
//                setResult(Activity.RESULT_OK, data);
//                finish();
//                break;
//        }
//    }

    public static void actionStartForResult(Activity mActivity, String content, int reqCode) {
        Intent startIntent = new Intent(mActivity, EditJobActivity.class);
        startIntent.putExtra(Constants.INFO_CONTENT, content);
        mActivity.startActivityForResult(startIntent, reqCode);
    }


}
