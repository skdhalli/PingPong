package com.pingpong.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.pingpong.services.ConnectivityStatsBroadcast;
import com.pingpong.services.ConnectivityStatsStorage;
import com.pingpong.services.ConnectivityStatsUpload;
import com.pingpong.services.StorageHandler;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static final String technology = "Wifi";
    final String newline = System.getProperty("line.separator");
    private StorageHandler storageHandler = new StorageHandler(this);
    //private final ConnectivityStatsStorage connectivityStatsStorage = new ConnectivityStatsStorage();
    //private Intent statsBroadCastIntent = new Intent(this, ConnectivityStatsBroadcast.class);
    //private final IntentFilter connectivity_stats_ready_filter = new IntentFilter(ConnectivityStatsBroadcast.status_ready_indicator);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        //Log.d("Trace", "Device ID: "+ telephonyManager.getDeviceId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityStatsStorage connectivityStatsStorage = new ConnectivityStatsStorage(storageHandler);

        IntentFilter connectivity_stats_ready_filter = new IntentFilter(ConnectivityStatsBroadcast.status_ready_indicator);
        connectivity_stats_ready_filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(connectivityStatsStorage, connectivity_stats_ready_filter);

        //Broadcast intent service
        Intent statsBroadCastIntent = new Intent(this, ConnectivityStatsBroadcast.class);
        statsBroadCastIntent.putExtra("Technology", technology);
        startService(statsBroadCastIntent);

        //Upload intent service
        Intent statsUploadIntent = new Intent(this, ConnectivityStatsUpload.class);
        startService(statsUploadIntent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String connection_json = intent.getStringExtra(ConnectivityStatsBroadcast.network_stats_filter);
            SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            String format = s.format(new Date());
            System.getProperty("os.version");

            TextView textView = (TextView)findViewById(R.id.status);
            textView.setText
                    ("Technology: "+technology+newline
                    +"Last updated: "+format+newline
                    +"Network stats: "+connection_json);

        }
    };


}
