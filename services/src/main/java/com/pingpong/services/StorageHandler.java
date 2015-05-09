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

    public void StoreConnectionStat(IConnectionStat connectionStat)
    {
        String insert_sql = "";
        if(connectionStat instanceof WifiConnectionStat)
        {
            WifiConnectionStat wifiConnectionStat = (WifiConnectionStat)connectionStat;
            insert_sql = "insert into WIFI_CONNECTIVITY_STATS (rssi, frequency, capabilities, bssid, ssid, timestamp, latitude, longitude) " +
                    "values(" + wifiConnectionStat.rssi + "," + wifiConnectionStat.frequency + ",'" + wifiConnectionStat.capabilities + "','" + wifiConnectionStat.bssid + "', '" + wifiConnectionStat.ssid + "', " + wifiConnectionStat.timestamp + ", " + wifiConnectionStat.latitude + ", " + wifiConnectionStat.longitude + ")";
        }
        if(insert_sql != "") {
            SQLiteDatabase writableDatabase = this.getWritableDatabase();
            writableDatabase.execSQL(insert_sql);
            writableDatabase.close();
        }
    }

    public IConnectionStat GetUploadReadyConnectionStat(String technology)
    {
        IConnectionStat result = null;
        if(technology.toUpperCase() == "WIFI") {
            String select_sql = "Select STAT_ID, rssi, frequency, capabilities, bssid, ssid, timestamp, latitude, longitude from WIFI_CONNECTIVITY_STATS order by STAT_ID ASC limit 1";
            SQLiteDatabase writableDatabase = this.getWritableDatabase();
            SQLiteDatabase readableDatabase = this.getReadableDatabase();
            //Log.d("Trace", insert_sql);
            Cursor cursor = readableDatabase.rawQuery(select_sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                WifiConnectionStat retval = new WifiConnectionStat();
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
                result = retval;
                String delete_sql = "Delete from WIFI_CONNECTIVITY_STATS where stat_id = " + stat_id;
                writableDatabase.execSQL(delete_sql);
            }
            cursor.close();
            //String count_sql = "Select * from WIFI_CONNECTIVITY_STATS";
            //cursor = readableDatabase.rawQuery(count_sql, null);
            //int count = cursor.getCount();
            //cursor.close();
            //Log.d("Trace", "Total # of stats while pulling: "+count);

            writableDatabase.close();
            readableDatabase.close();
            //Log.d("Trace", "Executed insert");
        }
        return result;
    }


}
