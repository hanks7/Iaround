package net.iaround.ui.activity.editpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.HometownEntity;
import net.iaround.model.entity.ProvinceEntiry;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.TitleActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static net.iaround.R.id.view;

public class EditProvinceActivity extends TitleActivity {

    private static final int PROVINCE_PAGE = 1;
    private static final int CITY_PAGE = 2;
    private ListView lvProvices;
    private ArrayList<ProvinceEntiry> provinceEntiries;
    private ArrayList<ProvinceEntiry.City> cities = new ArrayList<>();
    private ListView lvCity;
    private ImageView ivLeft;
    private FrameLayout flLeft;
    private int currentPage = PROVINCE_PAGE;
    private HometownEntity hometownEntity = new HometownEntity();
    private int index;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
//        setTitle_LCR(false, R.drawable.title_back, null, null, getString(R.string.edit_hometown), true, 0, null, null);

//        setContent(R.layout.activity_edit_province);
        setContentView(R.layout.activity_edit_province);
        flLeft = findView(R.id.fl_left);
        ivLeft = findView(R.id.iv_left);
        lvProvices = findView(R.id.lv_province);
        lvCity = findView(R.id.lv_city);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.edit_hometown));
    }

    private void initDatas() {
        String provinceList = CommonFunction.getGsonFromFile(this, Constants.PROVINCE_FILE);
        Type type = new TypeToken<ArrayList<ProvinceEntiry>>() {
        }.getType();
        provinceEntiries = GsonUtil.getInstance().json2Bean(provinceList, type);
        lvProvices.setAdapter(provinceAdapter);
        lvCity.setAdapter(cityAdapter);
    }

    private void initListeners() {
        flLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == PROVINCE_PAGE) {
                    finish();
                } else {
                    currentPage = PROVINCE_PAGE;
                    lvProvices.setVisibility(View.VISIBLE);
                    lvCity.setVisibility(View.GONE);
                }
            }
        });
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == PROVINCE_PAGE) {
                    finish();
                } else {
                    currentPage = PROVINCE_PAGE;
                    lvProvices.setVisibility(View.VISIBLE);
                    lvCity.setVisibility(View.GONE);
                }
            }
        });
        lvProvices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                hometownEntity.setProvinceID(provinceEntiries.get(position).getProvinceID());
                hometownEntity.setProvince(provinceEntiries.get(position).getTitle());
                currentPage = CITY_PAGE;
                lvProvices.setVisibility(View.GONE);
                Animation animation = animationForListCity();
                lvCity.setAnimation(animation);
                lvCity.setVisibility(View.VISIBLE);
                cities.clear();
                cities.addAll(provinceEntiries.get(position).getCity());
                cityAdapter.notifyDataSetChanged();

            }
        });

        lvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hometownEntity.setCityID(cities.get(position).getCityID());
                hometownEntity.setCity(cities.get(position).getTitle());
//                String hometown = GsonUtil.getInstance().getStringFromJsonObject(hometownEntity);
                //个人感觉还是Json数据用起来方便，可读性高，对添加顺序没有硬性要求，但是服务端说Json传的字段太多，浪费空间

//                String hometown = "1"+","+getResources().getString(R.string.edit_userinfo_china)+","+hometownEntity.getProvince() + "," + hometownEntity.getProvinceID() + "," + hometownEntity.getCity() + "," + hometownEntity.getCityID();
                if (index == provinceEntiries.size() - 1) {
                    String hometown = hometownEntity.getCityID() + ":" + hometownEntity.getCity();
                    Intent data = new Intent();
                    data.putExtra(Constants.EDIT_RETURN_INFO, hometown);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    String hometown = "1" + ":" + getResources().getString(R.string.edit_userinfo_china) + "," + hometownEntity.getCityID() + ":" + hometownEntity.getCity() + "," + hometownEntity.getProvinceID() + ":" + hometownEntity.getProvince();
                    Intent data = new Intent();
                    data.putExtra(Constants.EDIT_RETURN_INFO, hometown);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }

            }
        });
    }

    @NonNull
    private Animation animationForListCity() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        Animation animation = new TranslateAnimation(width, 0, 0, 0);
        animation.setDuration(500);
        return animation;
    }

    private BaseAdapter cityAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            if (cities == null) {
                return 0;
            }
            return cities.size();
        }

        @Override
        public Object getItem(int position) {
            return cities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.item_province_layout, null);
                viewHolder.view = convertView.findViewById(view);
                viewHolder.tvProvice = (TextView) convertView.findViewById(R.id.tv_province);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0 && cities.size() == 1) {
                viewHolder.view.setVisibility(View.GONE);
            }
            viewHolder.tvProvice.setText(cities.get(position).getTitle());
            return convertView;
        }

        class ViewHolder {
            View view;
            TextView tvProvice;
        }
    };

    private BaseAdapter provinceAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            if (provinceEntiries == null) {
                return 0;
            }
            return provinceEntiries.size();
        }

        @Override
        public Object getItem(int position) {
            return provinceEntiries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.item_province_layout, null);
                viewHolder.view = convertView.findViewById(view);
                viewHolder.tvProvice = (TextView) convertView.findViewById(R.id.tv_province);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.view.setVisibility(View.GONE);
            }
            viewHolder.tvProvice.setText(provinceEntiries.get(position).getTitle());
            return convertView;
        }

        class ViewHolder {
            View view;
            TextView tvProvice;
        }
    };

    public static void actionStartForResult(Activity mActivity, int reqCode) {
        Intent startIntent = new Intent(mActivity, EditProvinceActivity.class);
        mActivity.startActivityForResult(startIntent, reqCode);
    }
}
