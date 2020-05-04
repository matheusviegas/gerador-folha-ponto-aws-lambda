package br.com.mvsouza.helpers;

import br.com.mvsouza.scraping.bean.ScrapingInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    public enum DateFormat {
        DAY_MONTH_YEAR_SLASH("dd/MM/yyyy"),
        YEAR_MONTH_DAY_SLASH("yyyy/MM/dd"),
        YEAR_MONTH_DAY_DASH("yyyy-MM-dd"),
        MONTH_YEAR_SLASH("MM/yyyy"),
        WEEK_DAY_NAME("EEEE"),
        MONTH_NAME("MMMM");

        private final String format;

        DateFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

    }

    private static SimpleDateFormat buildSimpleDateFormat(DateFormat format) {
        return new SimpleDateFormat(format.getFormat(), new Locale("pt", "BR"));
    }

    public static String format(Calendar calendar, DateFormat format) {
        return buildSimpleDateFormat(format).format(calendar.getTime());
    }

    public static String format(Date date, DateFormat format) {
        return buildSimpleDateFormat(format).format(date);
    }

    public static Date parse(String date, DateFormat format) throws ParseException {
        return buildSimpleDateFormat(format).parse(date);
    }

    public static String getWeekDayName(Calendar cal) {
        return buildSimpleDateFormat(DateFormat.WEEK_DAY_NAME).format(cal.getTime()).replace("feira", "Feira");
    }

    public static String getMonthName(Calendar cal) {
        return buildSimpleDateFormat(DateFormat.MONTH_NAME).format(cal.getTime());
    }

    public static Calendar parseDateReference(String date) throws IllegalArgumentException, ParseException {
        Calendar dateReference = Calendar.getInstance();
        dateReference.setTime(parse(date, DateHelper.DateFormat.MONTH_YEAR_SLASH));

        return dateReference;
    }

}
