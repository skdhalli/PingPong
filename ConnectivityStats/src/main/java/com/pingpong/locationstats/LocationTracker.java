package com.pingpong.locationstats;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sdhalli on 5/5/2015.
 */
public class LocationTracker implements LocationListener {

    private LocationManager locationManager;

    public LocationTracker(LocationManager locationManager)
    {
        this.locationManager = locationManager;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public Location GetLastKnownLocation() {
        Location lastKnownLocation = null;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled)
        {
            //Log.d("Trace", "Using GPS mode");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, this);

            if (locationManager != null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //Log.d("Trace", "Latitude - " + lastKnownLocation.getLatitude() + ", Longitude - " + lastKnownLocation.getLongitude());
            }
        }
        else if (isNetworkProviderEnabled)
        {
            //Log.d("Trace", "Using Network mode");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 50, this);
            if (locationManager != null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                //Log.d("Trace", "Latitude - " + lastKnownLocation.getLatitude() + ", Longitude - " + lastKnownLocation.getLongitude());
            }
        }

        return  lastKnownLocation;
    }

}
