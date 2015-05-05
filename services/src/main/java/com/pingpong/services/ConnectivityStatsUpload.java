package com.pingpong.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sdhalli on 5/1/2015.
 */
public class ConnectivityStatsUpload extends BroadcastReceiver {




    public ConnectivityStatsUpload()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String network_stats = intent.getStringExtra(ConnectivityStatsBroadcast.network_stats_filter);
        Log.d("Trace", network_stats);
    }

    void upload()
    {

    }
}
