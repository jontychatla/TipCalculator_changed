package com.calculator.tipcalculator.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.calculator.tipcalculator.model.FilterData;
import com.calculator.tipcalculator.model.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharatkc on 2/16/14.
 */
public class DbQuery extends DatabaseConnector {

    public DbQuery(Context context) {
        super(context);
    }

    public List<String> getYear() {
        String query = "SELECT DISTINCT(strftime('%Y', t.date)) as year FROM tips t order by strftime('%Y', t.date) desc";
        Cursor cursor = database.rawQuery(query, null);
        List<String> years = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                int year = cursor.getColumnIndex("year");
                years.add(cursor.getString(year));
            } while (cursor.moveToNext());
        }
        return years;
    }

    public List<FilterData> getYearAndMonthTips(String year, String month) {
        String query = "SELECT   sum(t.billamount) as billamount, sum(t.tipamount) as tipamount, sum(t.billamount + t.tipamount) as total from tips t\n" +
                "where strftime('%Y', t.date) = ? and strftime('%m',t.date) = ?";
        Cursor cursor = database.rawQuery(query, new String[]{year, month});
        List<FilterData> list = new ArrayList<FilterData>();
        if (cursor.moveToFirst()) {
            do {
                int billamount = cursor.getColumnIndex("billamount");
                int tipamount = cursor.getColumnIndex("tipamount");
                int total = cursor.getColumnIndex("total");
                FilterData data = new FilterData();
                data.withBillAmount(cursor.getDouble(billamount))
                        .withTipAmount(cursor.getDouble(tipamount))
                        .withTotalAmount(cursor.getDouble(total));
                list.add(data);
            } while (cursor.moveToNext());
        }
        return list;

    }

    public Boolean rowExists() {
        System.out.println("====enter=====");
        String query = "select exists( select * from tips limit 1) as rowexists";
        Cursor cursor = database.rawQuery(query, null);
        int rowexists = cursor.getColumnIndex("rowexists");
        if (cursor.moveToFirst()) {

            if (cursor.isNull(rowexists) || cursor.getInt(rowexists) == 0) {
                System.out.println("exit");
                return false;
            }  else {
                System.out.println("exit");
            return true;
            }
        }
        return false;
    }

    public Tip getMaxBillPerWeek() {
        return null;

    }

//    public Tip getSpendingPerYear(){
//        String query = " SELECT * FROM " + TABLE_NAME;
//        Cursor cursor = database.rawQuery(query, null);
//        if (cursor.moveToFirst()) {
//            do {
//                Tip tip = new Tip();
//                tip.withPlace(cursor.getString(1))
//                        .withDate(cursor.getString(2))
//                        .withBillAmount(cursor.getDouble(3))
//                        .withTipAmount(cursor.getDouble(4));
//                tipsList.add(tip);
//            } while (cursor.moveToNext());
//        }
//        return null;
//    }

    public Tip getSpendingPerMonth() {
        return null;
    }

    public Tip getSpendingPerWeek() {
        return null;
    }


}
