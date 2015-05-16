package com.pingpong.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sdhalli on 5/6/2015.
 */
public class WifiConnectionStat implements IConnectionStat
{

    final String statsUploadBase = "http://198.199.116.105/ConnectivityStats/stats_upload.php";
    final RestServiceClient restServiceClient = new RestServiceClient(statsUploadBase);
    static  Registration registration = new Registration();
    private int max_number_upload_retry = 4;
    private long upload_retry_buffer_ms = 5000;
    private long max_number_stats = 1000;

    private long stat_id;
    private double rssi;
    private long frequency;
    private String capabilities;
    private String bssid;
    private String ssid;
    private long timestamp;
    private double latitude;
    private double longitude;
    private  boolean isReadyToUpload = false;

    public  boolean IsReadyToUpload()
    {
        return isReadyToUpload;
    }

    public void Upload(String device_unique_id) throws Exception {


        String deviceid = Registration.GetDeviceID(device_unique_id);
        restServiceClient.AddParam("deviceid", deviceid);
        restServiceClient.AddParam("technology", "WiFi");
        restServiceClient.AddParam("rssi", String.valueOf(rssi));
        restServiceClient.AddParam("frequency", String.valueOf(frequency));
        restServiceClient.AddParam("bssid", bssid);
        restServiceClient.AddParam("ssid", ssid);
        restServiceClient.AddParam("timestamp", String.valueOf(timestamp));
        restServiceClient.AddParam("latitude", String.valueOf(latitude));
        restServiceClient.AddParam("longitude", String.valueOf(longitude));
        boolean success = false;
        int num_retry = 0;
        do {
            restServiceClient.Execute(RestServiceClient.RequestMethod.GET);
            String response = restServiceClient.getResponse();
            success = response.split(":")[0].trim().equals("1");
            num_retry++;
            if(num_retry > max_number_upload_retry)
            {
                break;
            }
            if(!success)
            {
                Thread.sleep(upload_retry_buffer_ms);
            }
        }while (!success);

    }

    public void Store(Context context)
    {
        StorageHandler localStorage = new StorageHandler(context);
        SQLiteDatabase readableDatabase = localStorage.getReadableDatabase();
        SQLiteDatabase writableDatabase = localStorage.getWritableDatabase();
        //cursor to get the total count of records in the local storage
        Cursor cursor = readableDatabase.rawQuery("Select count(*) from WIFI_CONNECTIVITY_STATS", null);
        if(cursor != null && cursor.moveToFirst())
        {
            long count = cursor.getLong(0);
            //maintain the count by removing the extra records
            if(count > max_number_stats)
            {
                long num_records_delete = count - max_number_stats;
                cursor = readableDatabase.rawQuery("Select * from WIFI_CONNECTIVITY_STATS order by 1 asc limit "+num_records_delete, null);
                if(cursor != null)
                {
                    while (cursor.moveToNext()) {
                        long stat_id = cursor.getLong(0);
                        this.DeleteConnectionStat(context, stat_id);
                    }
                }
            }
        }
        //insert the stat into local storage
        String insert_sql = "insert into WIFI_CONNECTIVITY_STATS " +
                "(" +
                "rssi, " +
                "frequency, " +
                "capabilities, " +
                "bssid, " +
                "ssid, " +
                "timestamp, " +
                "latitude, " +
                "longitude) " +
                "values(" + this.rssi +
                "," + this.frequency + ",'"
                + this.capabilities + "','"
                + this.bssid + "', '"
                + this.ssid + "', "
                + this.timestamp + ", "
                + this.latitude + ", "
                + this.longitude + ")";
        writableDatabase.execSQL(insert_sql);
    }



    public void Log(double rssi, long frequency, String capabilities, String bssid,
                    String ssid, long timestamp, double latitude,
                    double longitude) throws UnsupportedEncodingException {
        this.rssi = rssi;
        this.frequency = frequency;
        this.capabilities = capabilities;
        this.bssid = bssid.replace(" ", "%20");
        this.ssid = ssid.replace(" ", "%20");
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public long  SetToEarliestConnectionStat(Context context)
    {
        long stat_id = -1;
        StorageHandler localStorage = new StorageHandler(context);
        String select_sql = "Select STAT_ID, rssi, frequency, capabilities, bssid, ssid, timestamp, latitude, longitude from WIFI_CONNECTIVITY_STATS order by STAT_ID ASC limit 1";
        SQLiteDatabase readableDatabase = localStorage.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery(select_sql, null);
        if (cursor != null && cursor.moveToFirst())
        {
            isReadyToUpload = true;
            stat_id = cursor.getLong(0);
            this.stat_id = stat_id;
            this.rssi = cursor.getDouble(1);
            this.frequency = cursor.getLong(2);
            this.capabilities = cursor.getString(3);
            this.bssid = cursor.getString(4);
            this.ssid = cursor.getString(5);
            this.timestamp = cursor.getLong(6);
            this.latitude = cursor.getDouble(7);
            this.longitude = cursor.getDouble(8);
        }
        cursor.close();

        readableDatabase.close();
        return stat_id;
    }

    public void DeleteConnectionStat(Context context, long stat_id)
    {
        StorageHandler localStorage = new StorageHandler(context);
        SQLiteDatabase writableDatabase = localStorage.getWritableDatabase();
        String delete_sql = "Delete from WIFI_CONNECTIVITY_STATS where stat_id = " + stat_id;
        writableDatabase.execSQL(delete_sql);
    }

    public static final String WPA = "WPA";
    public static final String WEP = "WEP";
    public static final String WPA2 = "WPA2";
    public static final String OPEN = "Open";

    public static String security(String cap)
    {
        if (cap.toLowerCase().contains(WEP.toLowerCase()))
        {return WEP ;}

        else if (cap.toLowerCase().contains(WPA2.toLowerCase()))
        {return WPA2;}

        else if (cap.toLowerCase().contains(WPA.toLowerCase()))
        {return WPA;}
        else


            return OPEN;

    }
}