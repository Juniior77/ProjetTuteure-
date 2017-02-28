package com.ParisEight.ProjetTuteure;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.nio.Buffer;
import java.util.EventListener;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class VehiculeControl extends ActionBarActivity {


    // Button btnOn, btnOff, btnDis;
    ImageButton Discnt;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    byte[] buffer = new byte[4];
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private SensorManager mSensorManager;
    private boolean isRunning = true;
    private int width, height;
    ImageView imgRectAcc, imgPoiAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the VehiculeControl
        setContentView(R.layout.activity_led_control);

        //call the widgets
        Discnt = (ImageButton) findViewById(R.id.discnt);

        //Gestion de la taille de l'écran
        Display mDisplay = getWindowManager().getDefaultDisplay();
        Point mPoint = new Point();
        mDisplay.getSize(mPoint);

        height = mPoint.y;
        width = mPoint.x;

        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        Discnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });

        buffer[0] = 0 - 128;
        buffer[1] = 0 - 128;
        buffer[2] = 0 - 128;
        buffer[3] = 90 - 128;

    }

    public boolean onTouchEvent(final MotionEvent event) {

        float y;
        y = event.getY();
        Log.i("FCT->onTouchEvent", "Valeur y: " + y + " width: " + width + " height: " + height);

        int ptZero = (height + 82) / 2;
        Log.i("FCT->onTouchEvent", "PtZero: " + ptZero);
        double ValeurDbl = (ptZero - y) / 2;
        int Valeur = (int) ValeurDbl;

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Valeur > 0) {
                Log.i("FCT->MAV", "Le vehicule est en mode marche avant !!!!!!!!!!!!!!!!!!!!!!!" + " La valeur vaut: " + Valeur);
                buffer[0] = 1 - 128;
                buffer[1] = (byte) (Valeur - 128);
                buffer[2] = (byte) (Valeur - 128);
                //Log.i("FCT->Direction", " Buffer: " + buffer[2]);
            } else if (Valeur < 0) {
                Log.i("FCT->MAR", "Le vehicule est en mode marche arriere !!!!!!!!!!!!!!!!!!!!!!!" + " La valeur vaut: " + Valeur);
                buffer[0] = 0 - 128;
                buffer[1] = (byte) (Valeur - 128);
                buffer[2] = (byte) (Valeur - 128);
                //Log.i("FCT->Direction", " Buffer: " + buffer[2]);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            buffer[1] = 0 - 128;
            buffer[2] = 0 - 128;
        }
        return super.onTouchEvent(event);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float y = sensorEvent.values[1];
            Direction(y);
            if (isBtConnected) {
                try {
                    btSocket.getOutputStream().write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void Direction(float y) {
        int direction = (int) ((y + 5) * 6) + 60;
        if (direction > 120)
            direction = 120;
        if (direction < 60)
            direction = 60;

        buffer[3] = (byte) (direction & 0xFF);
        if (BuildConfig.DEBUG) {
            Log.i("FCT->Direction", "Direction: " + direction + " Buffer: " + buffer[2]);
        }
    }

    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(VehiculeControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onStop();
    }
}
