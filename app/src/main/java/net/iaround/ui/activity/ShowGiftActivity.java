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
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.store.StoreMineGiftActivityNew;

import java.util.ArrayList;


public class ShowGiftActivity extends ActionBarActivity {

    private TextView tvTitle;
    private GridView gvHeadPics;
    private TextView tvEmpty;
    private ArrayList<String> gifts;
    private int isFrom;//1 个人  2 他人
    private TextView tvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_loginuser_gift);
        initViews();
        initDatas();
        initListener();

    }

    private void initViews() {
        findViewById(R.id.iv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvRight = (TextView) findViewById(R.id.tv_right);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //YC
//                StoreMineGiftActivity.launchMineGiftToFollow(mContext, Common.getInstance().loginUser);
                Intent intent = new Intent(ShowGiftActivity.this,StoreMineGiftActivityNew.class);
                startActivity(intent);
            }
        });
        tvTitle = findView(R.id.tv_title);
        tvTitle.setText(getResources().getString(R.string.gift));
        gvHeadPics = findView(R.id.gv_headpics);
        tvEmpty = findView(R.id.empty);

    }

    private void initDatas() {
        gifts = getIntent().getStringArrayListExtra(Constants.GIFT_LIST);
        isFrom = getIntent().getIntExtra("isFrom", 0);
        if (gifts != null) {
            gvHeadPics.setAdapter(new MyAdapter(gifts));
        }


        if (gifts == null) {
            if (isFrom == 1) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(getString(R.string.no_content_gift_get));
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(getResources().getString(R.string.private_gift));
            } else if (isFrom == 2) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(getString(R.string.space_sent_his_first_gift));
            }
        } else {
            if (isFrom == 1) {
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(getResources().getString(R.string.private_gift));
            }
        }
    }

    private void initListener() {

    }

    public static void startAction(Context context, ArrayList<String> gifts, int isFrom) {
        Intent intent = new Intent(context, ShowGiftActivity.class);
        intent.putStringArrayListExtra(Constants.GIFT_LIST, gifts);
        intent.putExtra("isFrom", isFrom);
        context.startActivity(intent);
    }

    public static void startAction(Context context, ArrayList<String> gifts) {
        Intent intent = new Intent(context, ShowGiftActivity.class);
        intent.putStringArrayListExtra(Constants.GIFT_LIST, gifts);
        context.startActivity(intent);
    }

    class MyAdapter extends BaseAdapter {

        private ArrayList<String> gifts;

        public MyAdapter(ArrayList<String> gifts) {
            this.gifts = gifts;
        }

        @Override
        public int getCount() {
            if (gifts == null) {
                return 0;
            }
            return gifts.size();
        }

        @Override
        public Object getItem(int position) {
            return gifts.get(position);
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
//            ImageViewUtil.getDefault().loadImage(gifts.get(position), imageView, R.drawable.userinfo_default_pic, R.drawable.default_pitcure_small_angle);
            GlideUtil.loadImage(BaseApplication.appContext, gifts.get(position), imageView, R.drawable.userinfo_default_pic, R.drawable.default_pitcure_small_angle);
            return imageView;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }
}
