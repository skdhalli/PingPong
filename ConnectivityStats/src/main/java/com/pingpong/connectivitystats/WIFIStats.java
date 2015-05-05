package com.pingpong.connectivitystats;

import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.pingpong.locationstats.LocationTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by sdhalli on 4/28/2015.
 */
public class WIFIStats extends LocationTracker implements IStatsGenerator {

    public  WIFIStats(LocationManager locationManager, WifiManager wifiManager)
    {
        super(locationManager);
        this.wifiManager = wifiManager;

    }

    private  WifiManager wifiManager;


    public  boolean IsConnected()
    {
        boolean retval = false;
        int conn_state = wifiManager.getWifiState();
        retval = conn_state == WifiManager.WIFI_STATE_ENABLED;
        return  retval;
    }

    public String GetResultsJSON() throws JSONException {
        String retval = "";
        if(wifiManager != null) {
            if (IsConnected())
            {
                Location lastKnownLocation = GetLastKnownLocation();
                JSONObject json = new JSONObject();
                //Get the location
                JSONObject locationJson = new JSONObject();
                locationJson.put("Latitude", lastKnownLocation.getLatitude());
                locationJson.put("Longitude", lastKnownLocation.getLongitude());
                json.put("Location", locationJson.toString());

                //Sniffing all access points
                List<ScanResult> scanResultList = wifiManager.getScanResults();

                JSONArray scanResults = new JSONArray();
                for(ScanResult scanResult: scanResultList)
                {
                    JSONObject scanResultJson = new JSONObject();
                    scanResultJson.put("Timestamp", scanResult.timestamp);
                    scanResultJson.put("BSSID", scanResult.BSSID);
                    scanResultJson.put("Capabilities", scanResult.capabilities);
                    scanResultJson.put("Frequency", scanResult.frequency);
                    scanResultJson.put("RSSI", scanResult.level);
                    scanResultJson.put("SSID", scanResult.SSID);
                    scanResults.put(scanResultJson);
                }
                json.put("Scanned Results", scanResults);

                  //From Wifi Info of connected AP
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if(wifiInfo != null) {
                   JSONObject connectedStatsJson = new JSONObject();
                   connectedStatsJson.put("RSSI", wifiInfo.getRssi());
                   connectedStatsJson.put("BSSID", wifiInfo.getBSSID());
                   connectedStatsJson.put("Hidden SSID", wifiInfo.getHiddenSSID());
                   connectedStatsJson.put("IP Address", wifiInfo.getIpAddress());
                   connectedStatsJson.put("Link speed", wifiInfo.getLinkSpeed());
                   connectedStatsJson.put("Mac address", wifiInfo.getMacAddress());
                   connectedStatsJson.put("Network ID", wifiInfo.getNetworkId());
                   connectedStatsJson.put("SSID", wifiInfo.getSSID());
                   connectedStatsJson.put("Supplicant State", wifiInfo.getSupplicantState().toString());
                   connectedStatsJson.put("Signal Level", wifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5));
                   connectedStatsJson.put("DHCP Info", wifiManager.getDhcpInfo().toString());
                   json.put("Connected AP Stats", connectedStatsJson);
               }
                retval = json.toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());

                //Log.d("Trace", currentDateandTime+" : "+retval);
            }
            else
            {
                retval = "Not connected";
            }
        }
        return  retval;
    }


}
