package net.iaround.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.SchoolEntity;

import java.util.ArrayList;

/**
 * @author：liush on 2016/12/29 20:42
 */
public class JobAdapter extends BaseAdapter {

    private String currentJob;
    private ArrayList<SchoolEntity> jobs;

    public JobAdapter(String currentJob,ArrayList<SchoolEntity> jobs) {
        this.currentJob = currentJob;
        this.jobs = jobs;
    }

    public void setCurrentJob(String job){
        this.currentJob = job;
    }

    @Override
    public int getCount() {
        if(jobs==null){
            return 0;
        }
        return jobs.size();
    }

    @Override
    public Object getItem(int position) {
        return jobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.item_job_layout, null);
            viewHolder.view = convertView.findViewById(R.id.view);
            viewHolder.tvJob = (TextView) convertView.findViewById(R.id.tv_job);
            viewHolder.ivSelected = (ImageView) convertView.findViewById(R.id.iv_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position == 0){
            viewHolder.view.setVisibility(View.GONE);
        }
        viewHolder.tvJob.setText(jobs.get(position).getSchoolName());
        if(jobs.get(position).getSchoolName().equals(currentJob)){
            viewHolder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivSelected.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        View view;
        TextView tvJob;
        ImageView ivSelected;
    }

}
