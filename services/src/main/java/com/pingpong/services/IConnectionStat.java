package com.pingpong.services;

import android.content.Context;

/**
 * Created by sdhalli on 5/7/2015.
 */
public interface IConnectionStat
{
    public void Upload(String device_unique_id) throws Exception;
    public  boolean IsReadyToUpload();
    public void Store(Context context);
    public long  SetToEarliestConnectionStat(Context context);
    public void DeleteConnectionStat(Context context, long stat_id);
}
