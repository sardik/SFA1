package com.ksni.roots.ngsales.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by #roots on 28/10/2015.
 */
public class OperationalCalendar {
    final static String TABLE = "sls_week";
    public String week;
    public String week_name;
    public int year_week;
    public int month_week;
    public int week_int;
    public String from_date;
    public String to_date;
    private SQLiteDatabase db;

    public OperationalCalendar(SQLiteDatabase db){
        this.db = db;
    }

    private boolean isExist(){
        boolean ada = false;
        Cursor cur = db.rawQuery(
                "SELECT * FROM "+TABLE+" WHERE week =?", new String[]{week});
        if (cur.moveToFirst()){
            ada = true;
        }
        cur.close();
        return ada;
    }


    public boolean save() {
        try {

            ContentValues cv = new ContentValues();
            if (isExist()) {
                cv.put("week_name", week_name);
                cv.put("year_week", year_week);
                cv.put("month_week", month_week);
                cv.put("week_int", week_int);
                cv.put("from_date", from_date);
                cv.put("to_date", to_date);
                db.update(TABLE, cv, "week=?", new String[]{week});
            } else {
                cv.put("week", week);
                cv.put("week_name", week_name);
                cv.put("year_week", year_week);
                cv.put("month_week", month_week);
                cv.put("week_int", week_int);
                cv.put("from_date", from_date);
                cv.put("to_date", to_date);
                db.insert(TABLE, null, cv);

            }
            return true;
        } catch (Exception x) {
            return false;
        }
    }

}
