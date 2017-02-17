package com.example.vilaire.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewY;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mTextViewY = (TextView) findViewById(R.id.textView3);

        mSensorManager = (SensorManager)getSystemService(this.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void Position(float iY)
    {

        int direction = (int)((iY+5)*6)+60;
        if(direction > 120)
            direction = 120;
        if(direction < 60)
            direction = 60;
        //Log.i("FCT->Postion", "Position X: " + iX);
        Log.i("FCT->Postion", "Position Y: " + iY + " Direction: " + (iY+5)*6 + "Total: " + direction);
        //Log.i("FCT->Postion", "Position Z: " + iZ);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float y = sensorEvent.values[1];
            Position(y);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop()
    {
        mSensorManager.unregisterListener(mSensorListener);
        super.onStop();
    }
}