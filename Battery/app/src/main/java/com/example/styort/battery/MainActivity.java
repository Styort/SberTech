package com.example.styort.battery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private ImageView batteryAnimatedIV;
    private TextView batteryPercentTV, batteryTechnology, batteryStatusTV;

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int capacity = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            float fPercent = ((float) level / (float) capacity) * 100f;
            int percent = Math.round(fPercent);

            int drawableLevel = percent * 100;
            batteryAnimatedIV.getDrawable().setLevel(drawableLevel);
            batteryPercentTV.setText(getString(R.string.battery_percent_format, percent));
            batteryTechnology.setText(getString(R.string.battery_technology) + technology);
            int state = getBatteryStatus(intent);
            batteryStatusTV.setText(getString(R.string.battery_status) + getResources().getStringArray(R.array.battery_states)[state]);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryAnimatedIV = (ImageView) findViewById(R.id.battery_iv);
        batteryPercentTV = (TextView) findViewById(R.id.battery_percent_tv);
        batteryTechnology = (TextView) findViewById(R.id.battery_technology_tv);
        batteryStatusTV = (TextView) findViewById(R.id.battery_status_tv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Здесь, перед удалением Activity, зачистим аниматор во избежание утечек памяти
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }

    private boolean isBatteryPresent(Intent intent){
        return intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, true);
    }
    private int getBatteryStatus(Intent intent){
        int state = 0;
        if (isBatteryPresent(intent)){
            state = intent.getIntExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_UNKNOWN);
        }
        return state;
    }
}
