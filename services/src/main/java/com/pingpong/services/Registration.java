package com.pingpong.services;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by sdhalli on 5/4/2015.
 */
public class Registration {
    private static final String registration_base = "http://198.199.116.105/ConnectivityStats/registration.php";

    private static String deviceID;

    public static String GetDeviceID(String device_unique_id) throws Exception {

        if(deviceID == null)
        {
            registerDevice(device_unique_id);
        }
        return deviceID;
    }

    private static void registerDevice(String device_unique_id) throws Exception {
        //check if device is registered

        RestServiceClient restServiceClient = new RestServiceClient(registration_base);
        restServiceClient.AddParam("mode", "check");
        restServiceClient.AddParam("ueid", device_unique_id);
        restServiceClient.Execute(RestServiceClient.RequestMethod.GET);
        String registration_check = restServiceClient.getResponse();
        if(registration_check.trim().equals("0"))
        {
            //device is not registered. proceed to registration
            restServiceClient.ClearParams();
            restServiceClient.AddParam("mode", "registration");
            restServiceClient.AddParam("model", Build.MODEL);
            restServiceClient.AddParam("manufacturer", Build.MANUFACTURER);
            restServiceClient.AddParam("ueid", device_unique_id);
            restServiceClient.Execute(RestServiceClient.RequestMethod.GET);
            registration_check = restServiceClient.getResponse();
        }
        if(!registration_check.trim().equals("0")) {
            deviceID = registration_check.split(":")[1].trim();
        }
    }
}
