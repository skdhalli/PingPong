package com.pingpong.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pingpong.connectivitystats.WIFIStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sdhalli on 5/5/2015.
 */
public class ConnectivityStatsStorage extends BroadcastReceiver
{
    public ConnectivityStatsStorage(StorageHandler storageHandler)
    {
        this.storageHandler = storageHandler;
    }

    StorageHandler storageHandler;
    static StorageHandler helper;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if(helper == null)
            {
                helper = new StorageHandler(context);
            }
            JSONObject network_stats_json = new JSONObject(intent.getStringExtra(ConnectivityStatsBroadcast.network_stats_filter));
            JSONObject location = network_stats_json.getJSONObject(WIFIStats.Location_Tag);
            JSONArray scannedResults = network_stats_json.getJSONArray(WIFIStats.Scanned_Results_Tag);
            for(int i =0;i<scannedResults.length();i++)
            {
                JSONObject scannedResult = scannedResults.getJSONObject(i);
                ConnectionStat connectionStat = new ConnectionStat();
                connectionStat.rssi = scannedResult.getDouble("RSSI");
                connectionStat.frequency = scannedResult.getLong("Frequency");
                connectionStat.capabilities = scannedResult.getString("Capabilities");
                connectionStat.bssid = scannedResult.getString("BSSID");
                connectionStat.ssid = scannedResult.getString("SSID");
                connectionStat.timestamp = scannedResult.getLong("Timestamp");
                connectionStat.latitude = location.getDouble("Latitude");
                connectionStat.longitude = location.getDouble("Longitude");

                helper.StoreConnectionStat(connectionStat);
                //Log.d("Trace", "Writting to sqllite db");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
