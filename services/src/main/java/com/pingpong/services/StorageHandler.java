package com.pingpong.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by sdhalli on 5/5/2015.
 */
public class StorageHandler extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ConnectivityStats.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE CONNECTIVITY_STATS(STAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "LATITUDE DOUBLE, LONGITUDE DOUBLE, RSSI DOUBLE, FREQUENCY LONG, CAPABILITIES VARCHAR(512), BSSID VARCHAR(512), SSID VARCHAR(512)," +
            "TIMESTAMP INTEGER);";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS CONNECTIVITY_STATS;";


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

    public void StoreConnectionStat(ConnectionStat connectionStat)
    {
        String insert_sql = "insert into CONNECTIVITY_STATS (rssi, frequency, capabilities, bssid, ssid, timestamp, latitude, longitude) " +
                "values("+connectionStat.rssi+","+connectionStat.frequency+",'"+connectionStat.capabilities+"','"+connectionStat.bssid+"', '"+connectionStat.ssid+"', "+connectionStat.timestamp+", "+connectionStat.latitude+", "+connectionStat.longitude+")";
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        //Log.d("Trace", insert_sql);
        writableDatabase.execSQL(insert_sql);
        Log.d("Trace", "Executed insert command.");
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        String select_sql = "Select * from Connectivity_Stats";
        Cursor cursor = readableDatabase.rawQuery(select_sql, null);
        int count = cursor.getCount();
        cursor.close();
        Log.d("Trace", "Total # of stats while pushing: "+count);
        writableDatabase.close();
    }

    public  ConnectionStat GetUploadReadyConnectionStat()
    {
        ConnectionStat retval = null;
        String select_sql = "Select STAT_ID, rssi, frequency, capabilities, bssid, ssid, timestamp, latitude, longitude from CONNECTIVITY_STATS order by STAT_ID ASC limit 1";
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        //Log.d("Trace", insert_sql);
        Cursor cursor = readableDatabase.rawQuery(select_sql, null);
        if(cursor != null && cursor.moveToFirst())
        {
            retval = new ConnectionStat();
            int stat_id = cursor.getInt(0);
            retval.stat_id = stat_id;
            retval.rssi = cursor.getDouble(1);
            retval.frequency = cursor.getLong(2);
            retval.capabilities = cursor.getString(3);
            retval.bssid = cursor.getString(4);
            retval.ssid = cursor.getString(5);
            retval.timestamp = cursor.getLong(6);
            retval.latitude = cursor.getDouble(7);
            retval.longitude = cursor.getDouble(8);

            String delete_sql = "Delete from connectivity_stats where stat_id = "+stat_id;
            writableDatabase.execSQL(delete_sql);
        }
        cursor.close();
        String count_sql = "Select * from Connectivity_Stats";
        cursor = readableDatabase.rawQuery(count_sql, null);
        int count = cursor.getCount();
        cursor.close();
        Log.d("Trace", "Total # of stats while pulling: "+count);

        writableDatabase.close();
        readableDatabase.close();
        //Log.d("Trace", "Executed insert");

        return retval;
    }


}
