package com.yinpai.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

/**
 * @description: 日期工具类
 * @author: yangshumin
 * @create: 2019-06-24 15:50
 */
public class DateUtil {


    private final static ThreadLocal<SimpleDateFormat> YYYY_MM_DD_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> YYYY_MM_DD_HH_MM_SS_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> MM_DD_HH_MM_SS_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd HH:mm:ss");
        }
    };

    public static Date getNowToNextDayDate(Integer amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }

    public static Date getThisYearInitDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(YEAR), JANUARY, 1, 0, 0, 0);
        return calendar.getTime();
    }


    public static Date getMonthInitDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(YEAR), calendar.get(MONTH), 1, 0, 0, 0);
        calendar.set(MILLISECOND, 0);
        return calendar.getTime();
    }


    /****************************parse**********************/
    /**
     * yyyy-MM-dd
     *
     * @param dateStr
     * @return
     */
    public static Date parseDD(String dateStr) {
        try {
            return YYYY_MM_DD_FORMAT.get().parse(dateStr);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * MM-dd HH:mm:ss
     *
     * @param dateStr
     * @return
     */
    public static Date parseMMDDSS(String dateStr) {
        try {
            Calendar calendar = getInstance();
            int year = calendar.get(YEAR);
            Date date = MM_DD_HH_MM_SS_FORMAT.get().parse(dateStr);
            calendar.setTime(date);
            calendar.set(YEAR, year);
            return calendar.getTime();
        } catch (Exception e) {
        }
        return null;
    }


    private static Date minDate(Integer year, Integer month, Integer day) {
        if (year == null) {
            return null;
        }
        Calendar instance = new Builder().set(YEAR, year).build();
        if (month == null || month < 1 || month > 12) {
            month = instance.getActualMinimum(MONTH) + 1;
        }
        instance.set(MONTH, month - 1);
        if (day == null || day < 1) {
            day = instance.getActualMinimum(DAY_OF_MONTH);
        }
        instance.set(DAY_OF_MONTH, day);
        return getMinTime(instance.getTime());
    }

    private static Date maxDate(Integer year, Integer month, Integer day) {
        if (year == null) {
            return null;
        }
        Calendar instance = new Builder().set(YEAR, year).build();
        if (month == null || month < 1 || month > 12) {
            month = instance.getActualMaximum(MONTH) + 1;
        }
        instance.set(MONTH, month - 1);
        if (day == null || day < 1) {
            day = instance.getActualMaximum(DAY_OF_MONTH);
        }
        instance.set(DAY_OF_MONTH, day);
        return getMaxTime(instance.getTime());
    }

    /**
     * @param params, 可变参数:年, 月, 日
     * @return
     */
    public static Date getMaxDate(Integer... params) {
        if (params.length > 3 || params.length < 1) {
            return null;
        }
        return maxDate(params[0],
                params.length > 1 ? params[1] : null,
                params.length > 2 ? params[2] : null);
    }

    /**
     * @param params, 可变参数:年, 月, 日
     * @return
     */
    public static Date getMinDate(Integer... params) {
        if (params.length > 3 || params.length < 1) {
            return null;
        }
        return minDate(params[0],
                params.length > 1 ? params[1] : null,
                params.length > 2 ? params[2] : null);
    }

    /**
     * 获取当日最大时间
     */
    public static Date getMaxTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar instance = getInstance();
        instance.setTime(date);
        instance.set(HOUR_OF_DAY, instance.getActualMaximum(HOUR_OF_DAY));
        instance.set(MINUTE, instance.getActualMaximum(MINUTE));
        instance.set(SECOND, instance.getActualMaximum(SECOND));
        instance.set(MILLISECOND, instance.getActualMinimum(MILLISECOND));
        return instance.getTime();
    }

    /**
     * 获取当日最小时间
     */
    public static Date getMinTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar instance = getInstance();
        instance.setTime(date);
        instance.set(HOUR_OF_DAY, instance.getActualMinimum(HOUR_OF_DAY));
        instance.set(MINUTE, instance.getActualMinimum(MINUTE));
        instance.set(SECOND, instance.getActualMinimum(SECOND));
        instance.set(MILLISECOND, instance.getActualMinimum(MILLISECOND));
        return instance.getTime();
    }

    /**
     * 计算日期的月份差
     *
     * @param preDate
     * @param postDate
     * @return
     */
    public static int getMonthDiff(Date preDate, Date postDate) {
        Calendar preCalendar = getInstance();
        preCalendar.setTime(preDate);
        int preYear = preCalendar.get(YEAR);
        int preMonth = preCalendar.get(MONTH);
        Calendar postCalendar = getInstance();
        postCalendar.setTime(postDate);
        int postYear = postCalendar.get(YEAR);
        int postMonth = postCalendar.get(MONTH);

        return (postYear - preYear) * 12 + (postMonth - preMonth);
    }

    /**
     * 计算日期差
     *
     * @param preDate
     * @param postDate
     * @return
     */
    public static int getDayDiff(Date preDate, Date postDate) {
        Calendar cal1 = getInstance();
        cal1.setTime(preDate);

        Calendar cal2 = getInstance();
        cal2.setTime(postDate);
        int preDays = cal1.get(DAY_OF_YEAR);
        int postDays = cal2.get(DAY_OF_YEAR);

        int preYear = cal1.get(YEAR);
        int postYear = cal2.get(YEAR);
        if (preYear != postYear) {
            int timeDistance = 0;
            for (int i = preYear; i < postYear; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    timeDistance += 366;
                } else {
                    timeDistance += 365;
                }
            }
            return timeDistance + (postDays - preDays);
        } else {
            return postDays - preDays;
        }
    }

    /**
     * 给出的日期加任意时间单位
     *
     * @param source
     * @param scale  Calendar.Second/Calendar.Hour
     * @param step
     * @return
     */
    public static Date timeAdd(Date source, int scale, int step) {
        if (source == null) {
            return null;
        }
        if (step == 0) {
            return source;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(source.getTime());
        calendar.add(scale, step);
        return calendar.getTime();
    }


    public static Integer getValue(Date source, int scale) {
        Calendar calendar;
        if (source == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance();
            calendar.setTime(source);
        }
        return calendar.get(scale);
    }

    public static String getMMDDYYHHMMSS(Date source) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        return sdf.format(source);
    }

    public static Date getMMDDYYHHMMSS(String source) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(source);
        return date;
    }

    public static Date getYYMMDDHHMMSS(String source) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String TimeStart = source.replace("-", " ");
        String time = TimeStart.replace(".", "-");
        Date date = format.parse(time);
        return date;
    }

    public static boolean isNow(Date date) {
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String nowDay = sf.format(now);
        String day = sf.format(date);
        return day.equals(nowDay);
    }
}
