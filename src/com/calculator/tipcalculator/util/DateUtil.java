package com.calculator.tipcalculator.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bharatkc on 2/16/14.
 */
public class DateUtil {
    private final static SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat viewFormat = new SimpleDateFormat("MM/dd/yyyy");

    public static String getDbDateString(String date) {
        try {
            Date d = viewFormat.parse(date);
            return dbFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
