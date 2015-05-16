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

    long upload_frequency_milliseconds = 100;
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

            IConnectionStat connectionStat = null;
            if(technology.toUpperCase().equals("WIFI"))
            {
                connectionStat = new WifiConnectionStat();
                long stat_id = connectionStat.SetToEarliestConnectionStat(this.getApplicationContext());
                if(stat_id != -1)
                {
                    try
                    {
                        connectionStat.Upload(device_unique_id);
                        connectionStat.DeleteConnectionStat(this.getApplicationContext(), stat_id);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    //Log.d("Trace", "No data to upload");
                }
            }
            //implement for other technologies too



        }while (true);


    }
}
