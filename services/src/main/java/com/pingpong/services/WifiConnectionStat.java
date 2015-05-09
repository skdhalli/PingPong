package com.pingpong.services;

/**
 * Created by sdhalli on 5/6/2015.
 */
public class WifiConnectionStat implements IConnectionStat
{

    final String statsUploadBase = "http://198.199.116.105/ConnectivityStats/stats_upload.php";
    final RestServiceClient restServiceClient = new RestServiceClient(statsUploadBase);
    static  Registration registration = new Registration();
    private int max_number_upload_retry = 4;

    public int stat_id;
    public double rssi;
    public long frequency;
    public String capabilities;
    public String bssid;
    public String ssid;
    public long timestamp;
    public double latitude;
    public double longitude;

    public void Upload() throws Exception {

        String deviceid = Registration.GetDeviceID();
        restServiceClient.AddParam("deviceid", deviceid);
        restServiceClient.AddParam("technology", "WiFi");
        restServiceClient.AddParam("rssi", String.valueOf(rssi));
        restServiceClient.AddParam("frequency", String.valueOf(frequency));
        restServiceClient.AddParam("capabilities", capabilities);
        restServiceClient.AddParam("bssid", bssid);
        restServiceClient.AddParam("ssid", ssid);
        restServiceClient.AddParam("timestamp", String.valueOf(timestamp));
        restServiceClient.AddParam("latitude", String.valueOf(latitude));
        restServiceClient.AddParam("longitude", String.valueOf(longitude));
        boolean success = false;
        int num_retry = 0;
        do {
            restServiceClient.Execute(RestServiceClient.RequestMethod.GET);
            String response = restServiceClient.getResponse();
            success = response.split(":")[0] == "1";
            num_retry++;
            if(num_retry > max_number_upload_retry)
            {
                break;
            }
            if(!success)
            {
                Thread.sleep(5000);
            }
        }while (!success);

    }
}