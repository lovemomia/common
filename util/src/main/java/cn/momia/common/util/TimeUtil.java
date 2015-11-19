package cn.momia.common.util;

import com.alibaba.fastjson.util.TypeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtil {
    public static class TimeUnit {
        public static final int MONTH = 1;
        public static final int QUARTER = 2;
        public static final int YEAR = 3;
    }

    public static final DateFormat STANDARD_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
    private static final String[] AM_PM = { "上午", "下午" };

    public static List<Date> castToDates(List<String> timeStrs) {
        List<Date> times = new ArrayList<Date>();
        for (String timeStr : timeStrs) {
            Date time = castToDate(timeStr);
            if (time != null) times.add(time);
        }

        return times;
    }

    public static Date castToDate(String timeStr) {
        try {
            return TypeUtils.castToDate(timeStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return WEEK_DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static boolean isSameDay(Date day1, Date day2) {
        return SHORT_DATE_FORMAT.format(day1).equals(SHORT_DATE_FORMAT.format(day2));
    }

    public static String getAmPm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return AM_PM[calendar.get(Calendar.AM_PM)];
    }

    public static float getAge(Date birthday) {
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        calendar.setTime(birthday);
        int yearBorn = calendar.get(Calendar.YEAR);
        int monthBorn = calendar.get(Calendar.MONTH);

        int year = yearNow - yearBorn;
        if (year >= 1) return year;

        int month = monthNow - monthBorn;
        if (month > 0 && month < 1) month = 1;
        return month / 12.0F;
    }

    public static String formatAge(Date birthday) {
        float age = getAge(birthday);

        if (age <= 0) {
            return "未出生";
        } else if (age > 0 && age < 1) {
            int month = (int) (age * 12);
            if (month == 0) month = 1;
            return month + "个月";
        } else {
            return ((int) age) + "岁";
        }
    }

    public static boolean isAdult(Date birthday) {
        return getAge(birthday) >= 18;
    }

    public static boolean isChild(Date birthday) {
        return getAge(birthday) < 18;
    }

    public static Date add(Date startTime, int time, int timeUnit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        switch (timeUnit) {
            case TimeUnit.MONTH:
                calendar.add(Calendar.MONTH, time);
                break;
            case TimeUnit.QUARTER:
                calendar.add(Calendar.MONTH, time * 3);
                break;
            case TimeUnit.YEAR:
                calendar.add(Calendar.YEAR, time);
                break;
            default: break;
        }

        return calendar.getTime();
    }
}
