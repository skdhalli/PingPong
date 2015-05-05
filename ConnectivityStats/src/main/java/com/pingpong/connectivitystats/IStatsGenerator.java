package com.pingpong.connectivitystats;

import android.location.LocationManager;

import org.json.JSONException;

/**
 * Created by sdhalli on 4/28/2015.
 */
public interface IStatsGenerator {
    public String GetResultsJSON() throws JSONException;
    public  boolean IsConnected();
}
