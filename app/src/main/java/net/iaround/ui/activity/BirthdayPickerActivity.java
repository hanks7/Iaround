
package net.iaround.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.view.user.IARDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BirthdayPickerActivity extends TitleActivity {
    private String mBirth;
    private IARDatePicker mDatePicker;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-M-d");
    private TextView mAgeTv, mStartTv;
    private TextView tvSave;
    private int horoscope;
    private int userAge;

    public static void launchForResult(Activity activity, String birth, int requestCode) {
        activity.startActivityForResult(
                new Intent(activity, BirthdayPickerActivity.class).putExtra("birth", birth),
                requestCode);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_LCR(false, R.drawable.title_back, null, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.edit_birthday_title), false, 0, getString(R.string.edit_user_birth_ok), null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSave = findView(R.id.tv_right);
        tvSave.setTextColor(0xFFFF4064);
        setContent(R.layout.iaround_birthday_picker_activity);

        mBirth = getIntent().getStringExtra("birth");
        if (TextUtils.isEmpty(mBirth)) {
            mBirth = "1990-1-1 00:00:00";
        }

        mDatePicker = (IARDatePicker) findViewById(R.id.datePicker);
        mAgeTv = (TextView) findViewById(R.id.age_tv);
        mStartTv = (TextView) findViewById(R.id.start_tv);

		/*
         * ( ( TextView ) actionBar.findView( R.id.title_left_text ) )
		 * .setText( R.string.cancel ); ( ( TextView ) actionBar.findView(
		 * R.id.title_right_text ) ).setText( R.string.ok );
		 */
        ((TextView) findViewById(R.id.age_title)).setText(R.string.edit_user_age);
        ((TextView) findViewById(R.id.start_title)).setText(R.string.edit_user_horoscope);
        ((TextView) findViewById(R.id.tip)).setText(R.string.edit_user_birth_tip);

		
		/*
         * actionBar.findView( R.id.title_left_text ).setOnClickListener(
		 * new OnClickListener( ) {
		 * 
		 * @Override public void onClick( View v ) { finish( ); } } );
		 * 
		 * actionBar.findView( R.id.title_right_text ).setOnClickListener(
		 * new OnClickListener( ) {
		 * 
		 * @Override public void onClick( View v ) { int year =
		 * mDatePicker.getYear( ); int month = mDatePicker.getMonth( ); int day
		 * = mDatePicker.getDayOfMonth( ); Date date = new Date( year - 1900 ,
		 * month , day ); setResult( Activity.RESULT_OK , new Intent(
		 * ).putExtra( "birth" , mDateFormat.format( date ) ) ); finish( ); } }
		 * );
		 */
        tvSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDate();
                Date date = new Date(year - 1900, month, day);
                userAge = calAge(year, month, day);
                Intent intent = new Intent();
                intent.putExtra(Constants.EDIT_RETURN_INFO, mDateFormat.format(date).toString()).putExtra("horoscope", horoscope);
                intent.putExtra(Constants.EDIT_BIRTHDAT_AGE, userAge);
                setResult(Activity.RESULT_OK, intent);
//                setResult(Activity.RESULT_OK,
//                        new Intent().putExtra(Constants.EDIT_RETURN_INFO, mDateFormat.format(date).toString()).putExtra("horoscope", horoscope));
                finish();
            }
        });

        Date dat;
        try {
            dat = mDateFormat.parse(mBirth);
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(dat);
            int birthYear = birthCalendar.get(Calendar.YEAR);
            int birthMonth = birthCalendar.get(Calendar.MONTH);
            int birthDay = birthCalendar.get(Calendar.DAY_OF_MONTH);

            // 设置当前年龄
            mAgeTv.setText(String.valueOf(calAge(birthYear, birthMonth, birthDay)));
            // 设置当前星座
            mStartTv.setText(date2Constellation(birthMonth, birthDay));
            userAge = calAge(birthYear, birthMonth, birthDay);

            final Calendar minDate = TimeFormat.GetRelative2CurrentTime(100, Calendar.YEAR);
            /**
             * 默认最小年龄是16周岁
             */
            final Calendar maxDate = TimeFormat.GetRelative2CurrentTime(16, Calendar.YEAR);
            mDatePicker.setDate(birthYear, birthMonth + 1, birthDay);
            mDatePicker.setMaxYear(maxDate.get(Calendar.YEAR));
            mDatePicker.setMinYear(minDate.get(Calendar.YEAR));
            mDatePicker.setOnDateChangedListener(new IARDatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(IARDatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                    // TODO Auto-generated method stub

                    int minYear = minDate.get(Calendar.YEAR);
                    int minMonth = minDate.get(Calendar.MONTH) + 1;
                    int minDay = minDate.get(Calendar.DAY_OF_MONTH);
                    int maxYear = maxDate.get(Calendar.YEAR);
                    int maxMonth = maxDate.get(Calendar.MONTH) + 1;
                    int maxDay = maxDate.get(Calendar.DAY_OF_MONTH);

                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.set(year, monthOfYear - 1, dayOfMonth);
                    if (newCalendar.after(maxDate)) {
//						mDatePicker.getVisitorList( maxYear , maxMonth , maxDay , this );
                        mDatePicker.resetDate(maxYear, maxMonth, maxDay);
                        year = maxYear;
                        monthOfYear = maxMonth;
                        dayOfMonth = maxDay;
                    } else if (newCalendar.before(minDate)) {
//						mDatePicker.getVisitorList( minYear , minMonth , minDay , this );
                        mDatePicker.resetDate(minYear, minMonth, minDay);
                        year = minYear;
                        monthOfYear = minMonth;
                        dayOfMonth = minDay;
                    }


                    mAgeTv.setText(String.valueOf(calAge(year, monthOfYear - 1, dayOfMonth)));
                    mStartTv.setText(date2Constellation(monthOfYear - 1, dayOfMonth));
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // 禁止编辑DatePicker的输入框 modify by shifengxiong 2014/04/23
        mDatePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int year = mDatePicker.getYear();
            int month = mDatePicker.getMonth();
            int day = mDatePicker.getDate();
            Date date = new Date(year - 1900, month, day);
            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("birth", mDateFormat.format(date)));
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();

//		CommonFunction.hideInputMethod( this , getRootView( ) );
    }

    public int calAge(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        int yearBirth = year;
        int monthBirth = month;
        int dayOfMonthBirth = day;

        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    /**
     * 根据日期获取星座
     * <p>
     * 月份从0开始计算
     *
     * @return
     */
    private String date2Constellation(int month, int day) {
        String[] constellationArr = getResources().getStringArray(R.array.horoscope_date);

        if (day < constellationEdgeDay[month]) {
            month = month + 1;
        } else {
            month += 2;
        }
        if (month <= 12) {
            horoscope = month;
            return constellationArr[month];
        }
        horoscope = 1;
        return constellationArr[1];
    }

    public final int[] constellationEdgeDay =
            {19, 18, 20, 19, 20, 21, 22, 22, 22, 23, 21, 21};
}
