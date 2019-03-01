package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import net.iaround.R;
import net.iaround.ui.view.DateWheelView;

import java.util.Arrays;

/**
 * Class:自定义时间选择器dialog
 * Author：chaoy
 * Date: 2017/3/9 11:26
 *
 */
public class CustomDateDialog extends Dialog {

    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTv;//标题
    private DateWheelView wv_year;//年选择
    private DateWheelView wv_month;//月选择

    private String titleStr;//标题

    private String yesStr,noStr;

    private String mYear,mMonth;

    private onNoOnClickLstener noOnClickLstener;
    private onYesOnClickListener yesOnClickListener;

    //月份
    private static final String[] PLANETS_MONTH = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
    //年份
    private static final String[] PLANETS_YEAR = new String[]{"2015","2016","2017"};

    /**
     * 备用天数
     * ,"13","14","15","16","17","18","19"
     ,"20","21","22","23","24","25","26","27","28","29","30","31"
     * @param context
     */
    public CustomDateDialog(Context context) {
        super(context);
    }

    public CustomDateDialog(Context context, int theme)
    {
        super(context,theme);
        this.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_date_select,null));
    }

    public void setNoOnClickLstener(String str,onNoOnClickLstener onNoOnClickListenr){
        if (str != null)
        {
            noStr = str;
        }
        this.noOnClickLstener = onNoOnClickListenr;
    }

    public void setYesOnClickListener(String str,onYesOnClickListener onYesOnClickListener)
    {
        if (str != null)
        {
            yesStr = str;
        }
        this.yesOnClickListener = onYesOnClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_select);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

//        Window window = this.getWindow();
//        window.setGravity(Gravity.CENTER);
//        WindowManager m = window.getWindowManager( );
//        Display d = m.getDefaultDisplay( );
//        WindowManager.LayoutParams p = window.getAttributes( ); // 获取对话框当前的参数值
//        p.height = ( int ) ( d.getWidth( ) * 0.8 );
//        p.width = ( int ) ( d.getWidth( ) * 0.8 );
//        window.setAttributes( p );

        //初始化控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initTouchEvent();
    }

    private  void initView(){
        titleTv = (TextView) findViewById(R.id.tv_title);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        wv_year = (DateWheelView) findViewById(R.id.wv_year);
        wv_month = (DateWheelView) findViewById(R.id.wv_month);

        wv_year.setOffset(2);
        wv_year.setItems(Arrays.asList(PLANETS_YEAR));
        wv_year.setSeletion(1);

        wv_month.setOffset(2);
        wv_month.setItems(Arrays.asList(PLANETS_MONTH));
        wv_month.setSeletion(2);

        setmMonth(wv_month.getSeletedItem());
        setmYear(wv_year.getSeletedItem());

        wv_month.setOnWheelViewListener(new DateWheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                setmMonth(item);
            }
        });

        wv_year.setOnWheelViewListener(new DateWheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                super.onSelected(selectedIndex, item);
                setmYear(item);
            }
        });
    }

    /**
     * 动态的为title，button赋值
     */
    private void initData() {
        if (titleStr != null)
        {
            titleTv.setText(titleStr);
        }
        if (yesStr != null)
        {
            yes.setText(yesStr);
        }
        if (noStr != null)
        {
            no.setText(noStr);
        }
    }
    private void initTouchEvent(){
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yesOnClickListener != null)
                {
                    yesOnClickListener.onYesClick();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noOnClickLstener != null)
                {
                    noOnClickLstener.onNoClick();
                }
            }
        });
    }
    //为title赋值
    public void setTitle(String title)
    {
        titleStr = title;
    }

    public interface onNoOnClickLstener{
        void onNoClick();
    }

    public interface onYesOnClickListener{
        void onYesClick();
    }

    public String getmYear() {
        return mYear;
    }

    public void setmYear(String mYear) {
        this.mYear = mYear;
    }

    public String getmMonth() {
        return mMonth;
    }

    public void setmMonth(String mMonth) {
        this.mMonth = mMonth;
    }
}
