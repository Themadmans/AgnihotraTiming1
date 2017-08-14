package com.example.tomer.agnihotratiming;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tomer on 8/4/2017.
 */

public class DBhelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "AgnihotraDB";
    private static final String DB_TABLE1 = "Location1";
    private static final String DB_TABLE2 = "Location2";
    private static final String DB_TABLE3 = "Location3";
    private static final int DB_VER = 1;
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_SUNRISE = "sunrise";
    private static final String KEY_SUNSET = "sunset";
    private Context mycontext;

    public DBhelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
        mycontext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE1 = "CREATE TABLE " + DB_TABLE1 + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT," + KEY_SUNSET + " TEXT,"
                + KEY_SUNRISE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE1);

        // Create tables again
        onCreate(db);
    }


  public void addDate(Entrydate dateentry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, dateentry.getDate());
        values.put(KEY_SUNRISE, dateentry.getSunrise());
        values.put(KEY_SUNSET, dateentry.getSunset());

        // Inserting Row
        db.insert(DB_TABLE1, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public Entrydate getDate (String datetosearch) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DB_TABLE1, new String[] { KEY_ID,
                        KEY_DATE, KEY_SUNRISE,KEY_SUNSET }, KEY_DATE + "=?",
                new String[] { datetosearch }, null, null, null, null);
        Entrydate entrydate=null;
       if (cursor.getCount()>0) {
            cursor.moveToFirst();
            entrydate = new Entrydate(cursor.getString(1),
                    cursor.getString(2), cursor.getString(3));

        }
        else
        {
            Toast.makeText(mycontext," Oops ! Data not found for date. Try updating database. ", Toast.LENGTH_LONG).show();
        }
        // return contact
        cursor.close();
        db.close();
        return entrydate;
    }

}