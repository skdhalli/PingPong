package com.pingpong.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by sdhalli on 5/5/2015.
 */
public class StorageHandler extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ConnectivityStats.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE WIFI_CONNECTIVITY_STATS(STAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "LATITUDE DOUBLE, LONGITUDE DOUBLE, RSSI DOUBLE, FREQUENCY LONG, CAPABILITIES VARCHAR(512), BSSID VARCHAR(512), SSID VARCHAR(512)," +
            "TIMESTAMP INTEGER);";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS WIFI_CONNECTIVITY_STATS;";


    public StorageHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public void onCreate(SQLiteDatabase db) {
        Log.d("Trace", "OnCreate - Creating tables");
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("Trace", "OnCreate - Created tables");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.d("Trace", "Deleting tables");
        db.execSQL(SQL_DELETE_ENTRIES);
        Log.d("Trace", "Deleted tables");
        Log.d("Trace", "Creating tables");
        onCreate(db);
        Log.d("Trace", "Created tables");

    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int getCount()
    {
        int retval = 0;
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String select_sql = "Select Count(*) from WIFI_CONNECTIVITY_STATS";
        Cursor cursor = readableDatabase.rawQuery(select_sql, null);
        if(cursor != null && cursor.moveToFirst()) {
            retval = cursor.getInt(0);
        }
        if(cursor != null) {
            cursor.close();
        }
        return  retval;
    }


}
