package edu.scut.luluteam.contextlib.utils;

import java.util.Calendar;

/**
 * Created by luluteam on 2017/12/3.
 */

public class CalendarUtil {
    private static Calendar calendar = Calendar.getInstance();

    public static String getDateTimeString(String date_divider, String date_time_divider) {
        String result = "";
        String timeDivider = ":";
        result = getYear() + date_divider + getMonth() + date_divider + getDayOfMonth() +
                date_time_divider + getHour() + timeDivider + getMinute() + timeDivider + getSecond();
        return result;
    }

    public static int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecond() {
        return calendar.get(Calendar.SECOND);
    }
}
