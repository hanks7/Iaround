package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.tools.glide.GlideUtil;

import java.util.ArrayList;

public class ShowHeadPicActivity extends TitleActivity {

    private TextView tvTitle;
    private GridView gvHeadPics;
    private ArrayList<String> picsThum;
    private ArrayList<String> pics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListener();

    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null, true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_show_head_pic);
        tvTitle = findView(R.id.tv_title);
        gvHeadPics = findView(R.id.gv_headpics);
    }

    private void initDatas() {
        picsThum = getIntent().getStringArrayListExtra(Constants.HEAD_PIC_THUM);
        pics = getIntent().getStringArrayListExtra(Constants.HEAD_PIC);
        gvHeadPics.setAdapter(new MyAdapter(picsThum, pics));
        tvTitle.setText(getString(R.string.show_head_title_left) + picsThum.size() + getString(R.string.show_head_title_right));
    }

    private void initListener() {

    }

    public static void startAction(Context context, ArrayList<String> picsThum, ArrayList<String> pics){
        Intent intent = new Intent(context, ShowHeadPicActivity.class);
        intent.putStringArrayListExtra(Constants.HEAD_PIC_THUM, picsThum);
        intent.putStringArrayListExtra(Constants.HEAD_PIC, pics);
        context.startActivity(intent);
    }

    class MyAdapter extends BaseAdapter{

        private ArrayList<String> picsThum;
        private ArrayList<String> pics;

        public MyAdapter(ArrayList<String> picsThum, ArrayList<String> pics) {
            this.picsThum = picsThum;
            this.pics = pics;
        }

        @Override
        public int getCount() {
            if(picsThum == null){
                return 0;
            }
            return picsThum.size();
        }

        @Override
        public Object getItem(int position) {
            return picsThum.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view = View.inflate(parent.getContext(), R.layout.userinfo_gift_img, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_pic);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            ImageViewUtil.getDefault().loadImage(picsThum.get(position), imageView, R.drawable.userinfo_default_pic, R.drawable.default_pitcure_small_angle);
            GlideUtil.loadImage(BaseApplication.appContext,picsThum.get(position), imageView, R.drawable.userinfo_default_pic, R.drawable.default_pitcure_small_angle);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PictureDetailsActivity.launch(parent.getContext(), pics, position);
                }
            });
            return imageView;
        }
    }
}
