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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sdhalli on 4/26/2015.
 */
public class ConnectivityStatsBroadcast extends IntentService {

    public static final String status_ready_indicator = "Connection Stats ready";
    public  static  final String network_stats_filter = "Network Stats";
    public  static final String location_stats_filter = "Location Info";
    final String newline = System.getProperty("line.separator");
    long broadcast_frequency_milliseconds = 2000;
    long locationBuffer = 5000;
    long connectivityBuffer = 5000;

    public ConnectivityStatsBroadcast()
    {
        super("ConnectivityManager");

    }

    public void Change_Broadcast_Frequency(long broadcast_frequency_milliseconds)
    {
        synchronized ((Object)this.broadcast_frequency_milliseconds) {
            this.broadcast_frequency_milliseconds = broadcast_frequency_milliseconds;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(status_ready_indicator);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        String technology = intent.getStringExtra("Technology");
        IStatsGenerator statsGenerator = null;

        switch(this.getTechnologyHash(technology))
        {
            case 0:
                WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                statsGenerator = new WIFIStats(locationManager, wifiManager);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }

        if(statsGenerator != null)
        {
            String json = null;
            //repeat this action
            do {
                try {
                    json = statsGenerator.GetResultsJSON();
                    broadcastIntent.putExtra(network_stats_filter, json);
                    sendBroadcast(broadcastIntent);
                    Thread.sleep(broadcast_frequency_milliseconds);
                    }
                catch(Exception e){
                        e.printStackTrace();
                    }
            }while (true);
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
