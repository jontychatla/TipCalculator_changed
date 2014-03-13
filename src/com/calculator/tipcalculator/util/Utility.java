package com.calculator.tipcalculator.util;

import java.util.HashMap;

/**
 * Created by bharatkc on 2/17/14.
 */
public class Utility {
    private static HashMap<String, String> yearMap = new HashMap<String, String>();

    static {
        yearMap.put("Jan", "01");
        yearMap.put("Feb", "02");
        yearMap.put("Mar", "03");
        yearMap.put("Apr", "04");
        yearMap.put("May", "05");
        yearMap.put("Jun", "06");
        yearMap.put("Jul", "07");
        yearMap.put("Aug", "08");
        yearMap.put("Sep", "09");
        yearMap.put("Oct", "10");
        yearMap.put("Nov", "11");
        yearMap.put("Dec", "12");
    }

    public static String getMonthNumber(String key) {
        return yearMap.get(key);
    }


    public static String getFormattedMoneyString(double value) {
        return String.format("%.2f", value);
    }

}
