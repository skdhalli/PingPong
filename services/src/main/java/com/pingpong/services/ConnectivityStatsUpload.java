package com.pingpong.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sdhalli on 5/1/2015.
 */
public class ConnectivityStatsUpload extends IntentService {

    long upload_frequency_milliseconds = 5000;
    public ConnectivityStatsUpload() {
        super("ConnectivityStatsUpload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        do {
            try {
                Thread.sleep(upload_frequency_milliseconds);} catch (InterruptedException e) {
                e.printStackTrace();
            }
                SQLLiteHelper helper = new SQLLiteHelper(this.getApplicationContext());
                SQLLiteHelper.ConnectionStat connectionStat = helper.GetEarliestConnectionStat();
            if(connectionStat != null)
            {
                //upload to server
                Log.d("Trace", "Upload stat - "+connectionStat.toString());
            }

        }while (true);


    }
}
