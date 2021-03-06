package com.pingpong.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;

import com.pingpong.connectivitystats.IStatsGenerator;
import com.pingpong.connectivitystats.WIFIStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sdhalli on 4/26/2015.
 */
public class ConnectivityStatsStorage extends IntentService {

    public static final String status_ready_indicator = "Connection Stats ready";
    public  static  final String network_stats_filter = "Network Stats";
    public  static final String location_stats_filter = "Location Info";
    final String newline = System.getProperty("line.separator");
    long scan_frequency_milliseconds = 30000;

    public ConnectivityStatsStorage()
    {
        super("ConnectivityStatsStorage");

    }

    public void Change_Broadcast_Frequency(long broadcast_frequency_milliseconds)
    {
        synchronized ((Object)this.scan_frequency_milliseconds) {
            this.scan_frequency_milliseconds = broadcast_frequency_milliseconds;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        StorageHandler storageHandler = new StorageHandler(this.getApplicationContext());
        
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(status_ready_indicator);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        String technology = intent.getStringExtra("Technology");
        IStatsGenerator statsGenerator = null;

        if(this.getTechnologyHash(technology)  == 0)
        {
            //Wifi
            WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            statsGenerator = new WIFIStats(locationManager, wifiManager);
            if(statsGenerator != null)
            {

                JSONObject network_stats_json = null;
                JSONObject location = null;
                JSONArray scannedResults = null;
                do
                {

                    try
                    {
                        Thread.sleep(scan_frequency_milliseconds);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    try {

                        String jsonResult = statsGenerator.GetResultsJSON();
                        network_stats_json = new JSONObject(jsonResult);
                        location = network_stats_json.getJSONObject(WIFIStats.Location_Tag);
                        scannedResults = network_stats_json.getJSONArray(WIFIStats.Scanned_Results_Tag);

                        for (int i = 0; i < scannedResults.length(); i++) {
                            JSONObject scannedResult = scannedResults.getJSONObject(i);
                            String capabilities = scannedResult.getString("Capabilities");

                            if(WifiConnectionStat.security(capabilities).equals(WifiConnectionStat.OPEN))
                            {
                                WifiConnectionStat wifiConnectionStat = new WifiConnectionStat();
                                double rssi = scannedResult.getDouble("RSSI");
                                long frequency = scannedResult.getLong("Frequency");
                                String bssid = scannedResult.getString("BSSID");
                                String ssid = scannedResult.getString("SSID");
                                long timestamp = scannedResult.getLong("Timestamp");
                                double latitude = location.getDouble("Latitude");
                                double longitude = location.getDouble("Longitude");
                                wifiConnectionStat.Log(rssi, frequency, capabilities, bssid,
                                        ssid, timestamp, latitude, longitude);
                                wifiConnectionStat.Store(this.getApplicationContext());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (true);

            }
        }
    }

    private  int getTechnologyHash(String technology)
    {
        int retval = -1;
        if(technology.toUpperCase().equals("WIFI"))
        {
            retval =  0;
        }
        else if(technology.toUpperCase().equals("LTE"))
        {
            retval =  1;
        }
        else if(technology.toUpperCase().equals("CDMA"))
        {
            retval =  2;
        }
        else if(technology.toUpperCase().equals("GSM"))
        {
            retval =  3;
        }
        else if(technology.toUpperCase().equals("UMTS"))
        {
            retval =  4;
        }
        return  retval;
    }

}
