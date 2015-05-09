package com.pingpong.services;

import android.os.Build;

/**
 * Created by sdhalli on 5/4/2015.
 */
public class Registration {
    private static final String registration_base = "http://198.199.116.105/ConnectivityStats/registration.php";

    private static String deviceID;

    public static String GetDeviceID() throws Exception {
        if(deviceID == null)
        {
            registerDevice();
        }
        return deviceID;
    }

    private static void registerDevice() throws Exception {
        //check if device is registered
        RestServiceClient restServiceClient = new RestServiceClient(registration_base);
        restServiceClient.AddParam("mode", "check");
        restServiceClient.AddParam("ueid", android.os.Build.DEVICE);
        restServiceClient.Execute(RestServiceClient.RequestMethod.GET);
        String registration_check = restServiceClient.getResponse();
        if(registration_check == "0")
        {
            //device is not registered. proceed to registration
            restServiceClient.ClearParams();
            restServiceClient.AddParam("mode", "registration");
            restServiceClient.AddParam("model", Build.MODEL);
            restServiceClient.AddParam("manufacturer", Build.MANUFACTURER);
            restServiceClient.AddParam("ueid", Build.DEVICE);
            restServiceClient.Execute(RestServiceClient.RequestMethod.GET);
            registration_check = restServiceClient.getResponse();
        }

        deviceID = registration_check.split(":")[1];
    }
}
