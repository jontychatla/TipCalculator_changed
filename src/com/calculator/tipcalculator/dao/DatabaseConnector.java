package com.calculator.tipcalculator.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.calculator.tipcalculator.model.Tip;
import com.calculator.tipcalculator.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharatkc on 2/1/14.
 */
public class DatabaseConnector {
    private static final String DATABASE_NAME = "UserTips";
    private static final String TABLE_NAME = "tips";
    protected SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    private static final String TIPS_PLACE_COLUMN = "place";
    private static final String TIPS_DATE_COLUMN = "date";
    private static final String TIPS_BILL_AMOUNT_COLUMN = "billamount";
    private static final String TIPS_TIP_AMOUNT_COLUMN = "tipamount";


    public DatabaseConnector(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open() {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close() {
        if (null != database) {
            database.close();
        }
    }

    public void insertTip(Tip tip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIPS_PLACE_COLUMN, tip.getPlace());
        contentValues.put(TIPS_DATE_COLUMN, DateUtil.getDbDateString(tip.getDate()));
        contentValues.put(TIPS_BILL_AMOUNT_COLUMN, tip.getBillAmount());
        contentValues.put(TIPS_TIP_AMOUNT_COLUMN, tip.getTipAmount());

        open();
        database.insert(TABLE_NAME, null, contentValues);
        close();
    }

    public void updateTip(long id, Tip tip) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIPS_PLACE_COLUMN, tip.getPlace());
        contentValues.put(TIPS_DATE_COLUMN, DateUtil.getDbDateString(tip.getDate()));
        contentValues.put(TIPS_BILL_AMOUNT_COLUMN, tip.getBillAmount());
        contentValues.put(TIPS_TIP_AMOUNT_COLUMN, tip.getTipAmount());

        open();
        database.update(TABLE_NAME, contentValues, "id= " + id, null);
        close();
    }

    public List<Tip> getAllTips() {
        List<Tip> tipsList = new ArrayList<Tip>();
        String query = " SELECT * FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Tip tip = new Tip();
                tip.withPlace(cursor.getString(1))
                        .withDate(cursor.getString(2))
                        .withBillAmount(cursor.getDouble(3))
                        .withTipAmount(cursor.getDouble(4));
                tipsList.add(tip);
            } while (cursor.moveToNext());
        }
        return tipsList;
    }

    public Cursor getOneTip(long id) {
        return database.query(TABLE_NAME, null, "id=" + id, null, null, null, null);
    }

    public void deleteTip(long id) {
        open();
        database.delete(TABLE_NAME, "id=" + id, null);
        close();
    }

}

class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context, String place, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, place, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE table tips " +
                "(id integer primary key autoincrement, " +
                "place TEXT, " +
                "date TEXT, " +
                "billamount REAL, " +
                "tipamount REAL);";
        sqLiteDatabase.execSQL(query);

//         query = "CREATE table ethnic " +
//                "(id integer primary key autoincrement, " +
//                "ethnic TEXT);";
//          sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}