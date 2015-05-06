package com.pingpong.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sdhalli on 5/1/2015.
 */
public class ConnectivityStatsUpload extends IntentService {

    long upload_frequency_milliseconds = 100;
    public ConnectivityStatsUpload() {
        super("ConnectivityStatsUpload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        do {
            try {
                Thread.sleep(upload_frequency_milliseconds);}
            catch (InterruptedException e) {
                e.printStackTrace();
            }
                StorageHandler helper = new StorageHandler(this.getApplicationContext());
                ConnectionStat connectionStat = helper.GetUploadReadyConnectionStat();
            if(connectionStat != null)
            {
                //upload to server
                Log.d("Trace", "Upload stat - "+connectionStat.stat_id);
            }
            else
            {
                Log.d("Trace", "No data to upload");
            }

        }while (true);


    }
}
