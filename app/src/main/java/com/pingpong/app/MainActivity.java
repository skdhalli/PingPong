package com.pingpong.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.pingpong.services.ConnectivityStatsBroadcast;
import com.pingpong.services.ConnectivityStatsUpload;
import com.pingpong.services.RestServiceClient;

import android.os.Build;
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
    //final String registration_url = "http://198.199.116.105/ConnectivityStats/registration.php";
    public static String DeviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        //Log.d("Trace", "Device ID: "+ telephonyManager.getDeviceId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityStatsBroadcast.status_ready_indicator);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        //dummy - until i finish testing
        registerReceiver(broadcastReceiver, filter);
        //registerReceiver(connectivityStatsUpload, filter);


        Intent intent = new Intent(this, ConnectivityStatsBroadcast.class);
        intent.putExtra("Technology", technology);
        startService(intent);
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

    private ConnectivityStatsUpload connectivityStatsUpload = new ConnectivityStatsUpload();

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
