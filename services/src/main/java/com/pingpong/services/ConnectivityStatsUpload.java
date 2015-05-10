package com.pingpong.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by sdhalli on 5/1/2015.
 */
public class ConnectivityStatsUpload extends IntentService {

    long upload_frequency_milliseconds = 1000;
    String technology = "";

    public ConnectivityStatsUpload() {
        super("ConnectivityStatsUpload");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        technology = intent.getStringExtra("Technology");
        String device_unique_id = ((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        do {
            try
            {
                Thread.sleep(upload_frequency_milliseconds);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            StorageHandler helper = new StorageHandler(this.getApplicationContext());
            IConnectionStat connectionStat = helper.GetUploadReadyConnectionStat(technology);
            if(connectionStat != null)
            {
                try
                {
                    connectionStat.Upload(device_unique_id);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
                //Log.d("Trace", "No data to upload");
            }

        }while (true);


    }
}
