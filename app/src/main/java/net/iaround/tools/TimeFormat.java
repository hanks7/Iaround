
package net.iaround.tools;


import android.annotation.SuppressLint;
import android.content.Context;

import com.sina.weibo.sdk.api.share.Base;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-3-12 下午10:21:48
 * @ClassName TimeFormat
 * @Description: 时间规范类, 把CommonFuntion中抽离出来
 * y年、M月、d日、h12小时中的时，H24小时中的时、m分、s秒、S毫秒、E星期、D一年中的第几天、F一年中的第几个星期几
 * w一年中的第几个星期、W一月中的第几个星期、
 */

public class TimeFormat {

    private static int secondUnit = 1000;// 1秒
    private static int minuUnit = 60 * secondUnit; // 1分钟
    private static int hourUnit = 60 * minuUnit; // 1小时
    private static int dayUnit = 24 * hourUnit; // 1天

    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat1
     * @Description: 时间格式化1，参照吴宇提供的Excel文档
     * @Rule:<1m[1分钟前], <1h[N分钟前]，<24H[N小时前],其他[N天前]
     */
    public static String timeFormat1(Context context, Long time) {

        String strTime = "";

        Date d = new Date();
        Long nowTime = Common.getInstance().serverToClientTime + d.getTime(); // 同步服务器时间
        Long deltaTime = nowTime - time;// 间隔时间

        if (deltaTime < 1 * minuUnit)// [1分钟前]
        {
            strTime = "1" + context.getResources().getString(R.string.before_minute);
        } else if (deltaTime < hourUnit) // [N分钟前]
        {
            strTime = deltaTime / minuUnit
                    + context.getResources().getString(R.string.before_minute);
        } else if (deltaTime < dayUnit) // [N小时前]
        {
            strTime = deltaTime / hourUnit
                    + context.getResources().getString(R.string.before_hour);
        } else
        // [N天前]
        {
            strTime = deltaTime / dayUnit
                    + context.getResources().getString(R.string.before_date);
        }

        return strTime;
    }

    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat2
     * @Description: 时间格式化2，参照吴宇提供的Excel文档
     * @Rule: <1m[刚刚来访],<1hour[N分钟前来访],其他[H:mm来访]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat2(Context context, Long time) {


        String strTime = "";

        Date d = new Date();
        Long nowTime = Common.getInstance().serverToClientTime + d.getTime(); // 同步服务器时间
        Long deltaTime = nowTime - time;// 间隔时间


        if (time <= 0 || deltaTime <= 1 * minuUnit)// [刚刚来访]
        {
            strTime = context.getResources().getString(R.string.just_recently_visit);
        } else if (deltaTime < 60 * minuUnit) // [N分钟前来访]
        {
            strTime = deltaTime / minuUnit
                    + context.getResources().getString(R.string.minute_ago_visiting);
        } else {// [H:mm来访]

            String format = context.getResources().getString(
                    R.string.chat_timerecord_today_timeformat);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time)
                    + context.getResources().getString(R.string.visiting);
        }
        return strTime;
    }

    /**
     * @param time
     * @return
     * @throws ParseException 根据生日获取年龄;
     */
    public static int getCurrentAgeByBirthdate(long time)
            throws Exception {
        String yearBirth = convertTimeLong2String(time, Calendar.YEAR);
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy");
            String currentTime = formatDate.format(calendar.getTime());
            Date today = formatDate.parse(currentTime);
            Date brithDay = formatDate.parse(yearBirth);

            return today.getYear() - brithDay.getYear();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 返回星座索引
     * <p>
     * 月份从0开始计算
     *
     * @return
     */
    public static String date3Constellation(long time) {
        int[] constellationEdgeDay = {19, 18, 20, 19, 20, 21, 22, 22, 22, 23, 21, 21};

        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (day < constellationEdgeDay[month]) {
            month = month + 1;
        } else {
            month += 2;
        }
        if (month <= 12) {
            return "" + month;
        }
        return "1";
    }

    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat3
     * @Description: 时间格式化3，参照吴宇提供的Excel文档
     * @Rule:<1m[刚刚],<1h[N分钟前],<24h[h:mm],48h[昨天 h:mm],不跨年[M月D日 h:mm],跨年[Y年M月D日 h:mm]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat3(Context context, Long time) {

        String strTime = "";

        Date d = new Date();
        Long nowTime = Common.getInstance().serverToClientTime + d.getTime(); // 同步服务器时间
        Long deltaTime = nowTime - time;// 间隔时间

        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);

        Calendar today = getToday();
        Calendar yesterday = getYesterday();


        if (deltaTime < 1 * minuUnit)// [刚刚]
        {
            strTime = context.getResources().getString(R.string.just_recently);
        } else if (deltaTime < 1 * hourUnit) // [N分钟前]
        {
            strTime = deltaTime / minuUnit
                    + context.getResources().getString(R.string.minute_ago);
        } else if (current.after(today)) // [H:mm]
        {
            String format = context.getResources().getString(
                    R.string.chat_timerecord_today_timeformat);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        } else if (current.before(today) && current.after(yesterday)) // [昨天
        // h:mm]
        {
            String format = context.getResources().getString(
                    R.string.chat_timerecord_yearstoday_timeformat);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        } else if (current.get(Calendar.YEAR) == today.get(Calendar.YEAR)) // [M月d日
        // h:mm]
        {
            String format = context.getResources().getString(R.string.timeformat_MdHm);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        } else
        // [YYYY年M月d日 h:mm]
        {
            String format = context.getResources().getString(R.string.timeformat_YMdHm);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        }

        return strTime;
    }


    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat4
     * @Description: 时间格式化4，参照吴宇提供的Excel文档
     * @Rule:<1m[刚刚],<1h[N分钟前],<24h[H:mm],48h[昨天 H:mm],不跨年[M-d],跨年[yyyy-M-d]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat4(Context context, Long time) {
        if (context == null) {
            context = BaseApplication.appContext;
        }
        String strTime = "";

        Date d = new Date();
        Long nowTime = Common.getInstance().serverToClientTime + d.getTime(); // 同步服务器时间
        Long deltaTime = nowTime - time;// 间隔时间

//		CommonFunction.log(
//				"sherlock" ,
//				"serverToClientTime : d.getTime : System.currentTimeMillis : deltaTime == "
//						+ Common.getInstance( ).serverToClientTime + " : " + d.getTime( )
//						+ " : " + System.currentTimeMillis( ) + " : " + deltaTime );

        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);

        Calendar today = getToday();
        Calendar yesterday = getYesterday();


        if (deltaTime < 1 * minuUnit)// [刚刚]
        {
            strTime = context.getResources().getString(R.string.just_recently);
        } else if (deltaTime < 1 * hourUnit) // [N分钟前]
        {
            strTime = deltaTime / minuUnit
                    + context.getResources().getString(R.string.minute_ago);
        } else if (current.after(today)) // [H:mm]
        {
            String format = context.getResources().getString(
                    R.string.chat_timerecord_today_timeformat);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        } else if (current.before(today) && current.after(yesterday)) // [昨天 H:mm]
        {
            String format = context.getResources().getString(
                    R.string.chat_timerecord_yearstoday_timeformat);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        } else if (current.get(Calendar.YEAR) == today.get(Calendar.YEAR)) // [M-d]
        {
            SimpleDateFormat formatter = new SimpleDateFormat("M-d");
            strTime = formatter.format(time);
        } else if (current.get(Calendar.YEAR) != today.get(Calendar.YEAR))// [yyyy-M-d]
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
            strTime = formatter.format(time);
        } else {
            CommonFunction.log("current==" + current);
        }

        return strTime;
    }


    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat5
     * @Description: 时间格式化5，参照吴宇提供的Excel文档
     * @Rule:<24h[h:mm],48h[昨天 h:mm],不跨年[M-D h:mm],跨年[YYYY-M-D h:mm]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat5(Context context, Long time) {

        String strTime = "";

        Date d = new Date();
        Long nowTime = Common.getInstance().serverToClientTime + d.getTime(); // 同步服务器时间
        Long deltaTime = nowTime - time;// 间隔时间

        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);

        Calendar today = getToday();
        Calendar yesterday = getYesterday();


        if (current.after(today)) // [H:mm]
        {
            String format = context.getResources().getString(
                    R.string.chat_timerecord_today_timeformat);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        } else if (current.before(today) && current.after(yesterday)) // [昨天 H:mm]
        {
            String format = context.getResources().getString(
                    R.string.chat_timerecord_yearstoday_timeformat);
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            strTime = formatter.format(time);
        } else if (current.get(Calendar.YEAR) == today.get(Calendar.YEAR)) // [M-d h:mm]
        {
            SimpleDateFormat formatter = new SimpleDateFormat("M-d h:mm");
            strTime = formatter.format(time);
        } else {// [yyyy-M-d h:mm]

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d h:mm");
            strTime = formatter.format(time);
        }

        return strTime;
    }

    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat6
     * @Description: 时间格式化6，参照吴宇提供的Excel文档
     * @Rule:[H:mm]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat6(Context context, Long time) {

        String strTime = "";

        String format = context.getResources().getString(
                R.string.chat_timerecord_today_timeformat);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        strTime = formatter.format(time);

        return strTime;
    }

    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat7
     * @Description: 时间格式化7，参照吴宇提供的Excel文档
     * @Rule:[yyyy/M/d]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat7(Context context, Long time) {

        String strTime = "";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d");
        strTime = formatter.format(time);

        return strTime;
    }


    /**
     * 获取时间 小时:分 H:mm
     *
     * @return
     */
    public static String getTimeShort(int time) {

        SimpleDateFormat formatter = new SimpleDateFormat("H:mm");
        Date currentTime = new Date();
        currentTime.setHours(time);
        currentTime.setMinutes(0);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间 分:秒 H:mm
     *
     * @return
     */
    public static String getTimeMinute(int time) {

        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        Date currentTime = new Date();
        currentTime.setHours(time);
        currentTime.setMinutes(0);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间 小时:分;秒 H:mm
     *
     * @return
     */
    public static Date getTime(int time) {
        Date currentTime = new Date();
        currentTime.setHours(time);
        currentTime.setMinutes(0);
        return currentTime;
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 把一个long型时间转成String型时间
     *
     * @param time  时间
     * @param level 参考Calendar
     * @return "yyyy-M-d k:mm:ss" 格式的时间
     */
    public static String convertTimeLong2String(long time, int level) {
        String format = "yyyy-M-d k:mm:ss";
        switch (level) {
            case Calendar.SECOND: {
                format = "yyyy-M-d k:mm:ss";
            }
            case Calendar.MINUTE: {
                format = "yyyy-M-d k:mm";
            }
            break;
            case Calendar.HOUR: {
                format = "yyyy-M-d k";
            }
            break;
            case Calendar.DATE: {
                format = "yyyy-M-d";
            }
            break;
            case Calendar.MONTH: {
                format = "yyyy-M";
            }
            break;
            case Calendar.YEAR: {
                format = "yyyy";
            }
            break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    /**
     * 把一个String型时间转成Long型时间
     *
     * @param time  时间
     * @param level 参考Calendar
     * @return millisecond value
     */
    public static long convertTimeString2Long(String time, int level) {
        String format = "yyyy-M-d H:mm:ss";
        switch (level) {
            case Calendar.MINUTE: {
                format = "yyyy-M-d H:mm";
            }
            break;
            case Calendar.HOUR: {
                format = "yyyy-M-d H";
            }
            break;
            case Calendar.DATE: {
                format = "yyyy-M-d";
            }
            break;
            case Calendar.MONTH: {
                format = "yyyy-M";
            }
            break;
            case Calendar.YEAR: {
                format = "yyyy";
            }
            break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        long second = 0;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            second = date.getTime();
        }
        return second;
    }

    // 获取今天的时间界限
    private static Calendar getToday() {
        Calendar today = Calendar.getInstance(); // 计算今天的界限
        today.set(Calendar.YEAR, today.get(Calendar.YEAR));
        today.set(Calendar.MONTH, today.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        // Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        return today;
    }

    // 获取昨天的时间界限
    private static Calendar getYesterday() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.YEAR, yesterday.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, yesterday.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, yesterday.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        return yesterday;
    }


    /**
     * 计算相对于现在offset的时间
     *
     * @param offset 正数表示今天之前的时间，负数表示今天之后的时间
     * @param level  {@link Calendar} 参考Calendar
     *               Calendar.SECOND Calendar.MINUTE ...
     * @return Calendar
     */
    public static Calendar GetRelative2CurrentTime(int offset, int level) {
        Calendar current = Calendar.getInstance();

        //current.set( Calendar.MINUTE , current.get( Calendar.SECOND ) - offset );
        current.set(level, current.get(level) - offset);

        return current;

    }

    /**
     * 判断传入时间是否在今天以后，包括今天;今天的时间标准为今天的0时0分0秒
     *
     * @param timeInMillis
     * @return
     */
    public static boolean IsAfterTodayTime(long timeInMillis) {
        Calendar today = getToday();
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(timeInMillis);
        return target.after(today);
    }

    /**
     * 获取当前的时间，与同步服务器
     *
     * @return long
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis() + Common.getInstance().serverToClientTime;
    }

    /**
     * 将秒转换为时间格式（00：00）
     *
     * @param s 秒数
     * @return
     */
    public static String secToTime(int s) {
        String time = null;

        int m = s / 60; // 分
        s = s - (m * 60); // 秒

        time = String.format("%02d:%02d", m, s);

        return time;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param lastTime    较小的时间
     * @param currentTime 较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int getDeltaDayNum(long lastTime, long currentTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date smdate = new Date(lastTime);
        Date bdate = new Date(currentTime);
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long betweenDays = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(betweenDays));
    }

    public static int getDeltaMonthNum(long lastTime, long currentTime)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date lastDate = new Date(lastTime);
        Date currentDate = new Date(currentTime);

        lastDate = sdf.parse(sdf.format(lastDate));
        currentDate = sdf.parse(sdf.format(currentDate));

        Calendar lastCalendar = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();

        lastCalendar.setTime(lastDate);
        currentCalendar.setTime(currentDate);

        int result = currentCalendar.get(Calendar.MONTH) - lastCalendar.get(Calendar.MONTH);
        int month = (currentCalendar.get(Calendar.YEAR) - lastCalendar.get(Calendar.YEAR)) * 12;

        return result + month;
    }

    public static int getDeltaMinutes(long lastTime, long currentTime) {
        long betweenDays = (currentTime - lastTime) / (1000 * 60);

        int mins = Integer.parseInt(String.valueOf(betweenDays));
        CommonFunction.log("time", mins);

        return mins;
    }

    /**
     * 判断传入时间是否在今天以后，包括今天;今天的时间标准为今天的0时0分0秒
     *
     * @param timeInMillis
     * @return
     */
    public static boolean isAfterTodayTime(long timeInMillis) {
        Calendar today = getToday();
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(timeInMillis * 1000);
        return target.after(today);
    }

    /**
     * 判断传入时间是否在今天以后，包括今天;今天的时间标准为今天的0时0分0秒
     *
     * @param timeInMillis
     * @return
     */
    public static boolean isAfterYesterdayTime(long timeInMillis) {
        Calendar yesterday = getYesterday();
        Calendar target = Calendar.getInstance();
        target.setTimeInMillis(timeInMillis * 1000);
        return target.after(yesterday);
    }

    /**
     * @param context
     * @param time
     * @return
     * @Title: timeFormat7
     * @Description: 时间格式化7，参照吴宇提供的Excel文档
     * @Rule:[yyyy/M/d]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat8(Context context, Long time) {

        String strTime = "";

        SimpleDateFormat formatter = new SimpleDateFormat("M-d");
        strTime = formatter.format(time);

        return strTime;
    }

    /**
     * @param time
     * @return
     * @throws ParseException 根据生日获取年龄;
     */
    public static int getAgeByBirthdate(long time) {
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(System.currentTimeMillis());
        int yearNow = current.get(Calendar.YEAR);
        int monthNow = current.get(Calendar.MONTH);
        int dayOfMonthNow = current.get(Calendar.DAY_OF_MONTH);

        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(time * 1000);
        int yearBirth = birthday.get(Calendar.YEAR);
        int monthBirth = birthday.get(Calendar.MONTH);
        int dayOfMonthBirth = birthday.get(Calendar.DAY_OF_MONTH);

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
    public static String date2Constellation(Context mContext, long time) {
        int[] constellationEdgeDay = {19, 18, 20, 19, 20, 21, 22, 22, 22, 23, 21, 21};

        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String[] constellationArr = mContext.getResources().getStringArray(R.array.horoscope_date);

        if (day < constellationEdgeDay[month]) {
            month = month + 1;
        } else {
            month += 2;
        }
        if (month <= 12) {
            return constellationArr[month];
        }
        return constellationArr[1];
    }

    /**
     * @return
     * @Title: timeFormat8
     * @Description: 时间格式化8，
     * @Rule: [ 20170817055450 ]
     */
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat8() {

        String strTime = "";
        long time = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        strTime = formatter.format(time);

        return strTime;
    }

    /**
     * 生成年月日整型
     *
     * @return
     */
    public static long getYearMonthDayDate() {

        String strTime = "";
        long time = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        strTime = formatter.format(time);

        return Long.parseLong(strTime);
    }

    /**
     * Android 音乐播放器应用里，读出的音乐时长为 long 类型以秒数为单位，例如：将 234736 转化为分钟和秒应为 03:55 （包含四舍五入）
     *
     * @param second 音乐时长
     * @return
     */
    public static String timeParse(long second) {
        long h = 0;
        long d = 0;
        long s = 0;
        long temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        String hStr = "";
        String dStr = "";
        String sStr = "";

        if (h < 10) {
            hStr = "0" + h;
        } else {
            hStr = "" + h;
        }

        if (d < 10) {
            dStr = "0" + d;
        } else {
            dStr = "" + d;
        }

        if (s < 10) {
            sStr = "0" + s;
        } else {
            sStr = "" + s;
        }

        if (h == 0) {
            return dStr + ":" + sStr;
        }

        return hStr + ":" + dStr + ":" + sStr;
    }

    /*时间秒数转为字符串
     * */
    public static String secondsToString(int seconds) {
        int hour = 0;
        int min = 0;
        int second = 0;
        min = seconds / 60;
        second = seconds % 60;
        if (min >= 60) {
            hour = min / 60;
            min = min % 60;
        }
        String timeStr;
        if (hour > 0) {
            timeStr = String.format("%02d:%02d:%02d", hour, min, second);
        } else {
            timeStr = String.format("%02d:%02d", min, second);
        }
        return timeStr;
    }
}
