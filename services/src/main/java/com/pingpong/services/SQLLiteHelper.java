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
public class SQLLiteHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ConnectivityStats.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE CONNECTIVITY_STATS(STAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "LATITUDE DOUBLE, LONGITUDE DOUBLE, RSSI DOUBLE, FREQUENCY LONG, CAPABILITIES VARCHAR(512), BSSID VARCHAR(512), SSID VARCHAR(512)," +
            "TIMESTAMP INTEGER);";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS CONNECTIVITY_STATS;";

    public SQLLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.d("Trace", "Trying to delete and re-create table ");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        Log.d("Trace", "Database is ready");
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void UploadConnectionStat(double rssi, long frequency, String capabilities, String bssid, String ssid, long timestamp, double latitude, double longitude)
    {
        String insert_sql = "insert into CONNECTIVITY_STATS (rssi, frequency, capabilities, bssid, ssid, timestamp, latitude, longitude) " +
                "values("+rssi+","+frequency+",'"+capabilities+"','"+bssid+"', '"+ssid+"', "+timestamp+", "+latitude+", "+longitude+")";
        SQLiteDatabase conn_db = this.getWritableDatabase();
        conn_db.beginTransaction();
        //Log.d("Trace", insert_sql);
        conn_db.execSQL(insert_sql);
        //Log.d("Trace", "Executed insert");
        conn_db.endTransaction();

    }

    public  ConnectionStat GetEarliestConnectionStat()
    {
        ConnectionStat retval = null;
        String select_sql = "Select STAT_ID, rssi, frequency, capabilities, bssid, ssid, timestamp, latitude, longitude from CONNECTIVITY_STATS order by STAT_ID ASC";
        SQLiteDatabase conn_db = this.getWritableDatabase();
        conn_db.beginTransaction();
        //Log.d("Trace", insert_sql);
        Cursor cursor = conn_db.rawQuery(select_sql, null);
        cursor.moveToFirst();
        //if(cursor.moveToFirst())
        //{
            retval = new ConnectionStat();
            int stat_id = cursor.getInt(0);
            String delete_sql = "Delete from connectivity_stats where stat_id = "+stat_id;
            conn_db.execSQL(delete_sql);
            retval.rssi = cursor.getDouble(1);
            retval.frequency = cursor.getLong(2);
            retval.capabilities = cursor.getString(3);
            retval.bssid = cursor.getString(4);
            retval.ssid = cursor.getString(5);
            retval.timestamp = cursor.getLong(6);
            retval.latitude = cursor.getDouble(7);
            retval.longitude = cursor.getDouble(8);
        //}
        //Log.d("Trace", "Executed insert");
        conn_db.endTransaction();
        return retval;
    }

    public class ConnectionStat
    {
        public double rssi;
        public long frequency;
        public String capabilities;
        public String bssid;
        public String ssid;
        public long timestamp;
        public double latitude;
        public double longitude;
    }
}
