package net.iaround.ui.activity.editpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Constants;
//import net.iaround.model.database.Lable;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.ui.view.user.FlowLayout;

//import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择标签的页面
 */
public class SelectLableActivity extends TitleActivity implements View.OnClickListener {

    private int lableLogo;
    private int lableColor;
    private int lableSelectedBg;
    private int lableNormalBg;
//    private List<Lable> lableList;
    private ImageView ivLableLogo;
    private TextView tvDesc;
    private TextView tvSelectedNum;
    private ArrayList<String> lableIds;
    private FlowLayout flLables;
    private ImageView ivTitleLeft;
    private FrameLayout flLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, null, getString(R.string.edit_label), true, 0, null, null);
        flLeft = findView(R.id.fl_left);
        ivTitleLeft = findView(R.id.iv_left);
        setContent(R.layout.activity_select_lable);
        ivLableLogo = findView(R.id.iv_lable_logo);
        tvDesc = findView(R.id.tv_desc);
        tvSelectedNum = findView(R.id.tv_selected_num);
        flLables = findView(R.id.fl_lables);
    }

    private void initDatas() {
        Intent intent = getIntent();
        lableIds = intent.getStringArrayListExtra(Constants.LABLE_SELECTED_IDS);
        int lablePosition = intent.getIntExtra(Constants.LABLE_SELECTED_POSITION, 0);
        lableNormalBg = R.drawable.circle_corner_bg;
        switch (lablePosition){
            case Constants.LABLE_SPORT_POSITION:
                lableLogo = R.drawable.edit_sport;
                lableColor = getResColor(R.color.lable_sport_color);
                lableSelectedBg = R.drawable.circle_sport_bg;
//                lableList = DataSupport.where(CommonFunction.getRoundSelectCode(Constants.LABLE_IDS, Constants.LABLE_SPORT_START_INDEX, Constants.LABLE_SPORT_END_INDEX)).find(Lable.class);
                break;
            case Constants.LABLE_TRAVEL_POSITION:
                lableLogo = R.drawable.edit_travel;
                lableColor = getResColor(R.color.lable_travel_color);
                lableSelectedBg = R.drawable.circle_travel_bg;
//                lableList = DataSupport.where(CommonFunction.getRoundSelectCode(Constants.LABLE_IDS, Constants.LABLE_TRAVEL_START_INDEX, Constants.LABLE_TRAVEL_END_INDEX)).find(Lable.class);
                break;
            case Constants.LABLE_ART_POSITION:
                lableLogo = R.drawable.edit_arts;
                lableColor = getResColor(R.color.lable_art_color);
                lableSelectedBg = R.drawable.circle_art_bg;
//                lableList = DataSupport.where(CommonFunction.getRoundSelectCode(Constants.LABLE_IDS, Constants.LABLE_ART_START_INDEX, Constants.LABLE_ART_END_INDEX)).find(Lable.class);
                break;
            case Constants.LABLE_FOOD_POSITION:
                lableLogo = R.drawable.edit_food;
                lableColor = getResColor(R.color.lable_food_color);
                lableSelectedBg = R.drawable.circle_food_bg;
//                lableList = DataSupport.where(CommonFunction.getRoundSelectCode(Constants.LABLE_IDS, Constants.LABLE_FOOD_START_INDEX, Constants.LABLE_FOOD_END_INDEX)).find(Lable.class);
                break;
            case Constants.LABLE_FUN_POSITION:
                lableLogo = R.drawable.edit_fun;
                lableColor = getResColor(R.color.lable_fun_color);
                lableSelectedBg = R.drawable.circle_fun_bg;
//                lableList = DataSupport.where(CommonFunction.getRoundSelectCode(Constants.LABLE_IDS, Constants.LABLE_FUN_START_INDEX, Constants.LABLE_FUN_END_INDEX)).find(Lable.class);
                break;
            case Constants.LABLE_REST_POSITION:
                lableLogo = R.drawable.edit_rest;
                lableColor = getResColor(R.color.lable_rest_color);
                lableSelectedBg = R.drawable.circle_rest_bg;
//                lableList = DataSupport.where(CommonFunction.getRoundSelectCode(Constants.LABLE_IDS, Constants.LABLE_REST_START_INDEX, Constants.LABLE_REST_END_INDEX)).find(Lable.class);
                break;
            case Constants.LABLE_MYLABLE_POSITION:
                lableLogo = R.drawable.edit_lable;
                lableColor = getResColor(R.color.lable_mylable_color);
                lableSelectedBg = R.drawable.circle_mylable_bg;
//                lableList = DataSupport.where(CommonFunction.getRoundSelectCode(Constants.LABLE_IDS, Constants.LABLE_LABLE_START_INDEX, Constants.LABLE_LABLE_END_INDEX)).find(Lable.class);
                break;
        }
        ivLableLogo.setImageResource(lableLogo);
        tvDesc.setTextColor(lableColor);
        tvSelectedNum.setTextColor(lableColor);
        tvSelectedNum.setText("("+ lableIds.size()+"/6)");
//        for(Lable lable : lableList){
//            TextView tvLable = new TextView(this);
//            if(lableIds.contains(lable.getLableId())){
//                tvLable.setBackgroundResource(lableSelectedBg);
//                tvLable.setTextColor(getResColor(R.color.common_white));
//            } else {
//                tvLable.setBackgroundResource(lableNormalBg);
//                tvLable.setTextColor(getResColor(R.color.common_black));
//            }
//            tvLable.setText(lable.getLableValue());
//            tvLable.setTag(lable.getLableId());
//            tvLable.setOnClickListener(this);
//            flLables.addView(tvLable); //yuchao
//        }
    }

    private void initListeners() {
        ivTitleLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
            case R.id.fl_left:
                Intent intent = new Intent();
                intent.putStringArrayListExtra(Constants.LABLE_SELECTED_IDS, lableIds);
                setResult(Activity.RESULT_OK,intent);
                finish();
                break;
            default:
                String id = (String) v.getTag();
                if(lableIds.contains(id)){
                    lableIds.remove(id);
                    v.setBackgroundResource(lableNormalBg);
                    ((TextView)v).setTextColor(getResColor(R.color.common_black));
                } else {
                    if(lableIds.size() >= 6){
                        Toast.makeText(this, getString(R.string.edit_user_lable_remind), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        lableIds.add(id);
                        v.setBackgroundResource(lableSelectedBg);
                        ((TextView)v).setTextColor(getResColor(R.color.common_white));
                    }
                }
                tvSelectedNum.setText("("+ lableIds.size()+"/6)");
        }

    }
}
