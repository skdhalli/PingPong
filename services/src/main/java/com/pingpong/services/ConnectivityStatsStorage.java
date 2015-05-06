package com.pingpong.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pingpong.connectivitystats.WIFIStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sdhalli on 5/5/2015.
 */
public class ConnectivityStatsStorage extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            SQLLiteHelper helper = new SQLLiteHelper(context);
            JSONObject network_stats_json = new JSONObject(intent.getStringExtra(ConnectivityStatsBroadcast.network_stats_filter));
            JSONObject location = network_stats_json.getJSONObject(WIFIStats.Location_Tag);
            JSONArray scannedResults = network_stats_json.getJSONArray(WIFIStats.Scanned_Results_Tag);
            for(int i =0;i<scannedResults.length();i++)
            {
                JSONObject scannedResult = scannedResults.getJSONObject(i);
                double rssi = scannedResult.getDouble("RSSI");
                long frequency = scannedResult.getLong("Frequency");
                String capabilities = scannedResult.getString("Capabilities");
                String BSSID = scannedResult.getString("BSSID");
                String SSID = scannedResult.getString("SSID");
                long Timestamp = scannedResult.getLong("Timestamp");
                double latitude = location.getDouble("Latitude");
                double longitude = location.getDouble("Longitude");
                helper.UploadConnectionStat(rssi, frequency, capabilities, BSSID, SSID, Timestamp, latitude, longitude);
                //Log.d("Trace", "Writting to sqllite db");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
