package pariseight.androidveh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class VehiculeControl extends Activity {

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    byte [] buffer = new byte[16];
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private SensorManager mSensorManager;
    //private VehicleControlView mControlVehiculeView;
    private VehiculeControlView mControlView;
    private int Accel;
    private FeuSignalisation LED_AR_G;
    private FeuSignalisation LED_AR_D;
    private FeuSignalisation LED_AV_G;
    private FeuSignalisation LED_AV_D;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicule_control);

        Intent newInt = getIntent();
        address = newInt.getStringExtra(MainActivity.EXTRA_ADDRESS);

        new ConnectBT().execute();

        buffer[0] = 0 - 128;
        buffer[1] = 0 - 128;
        buffer[2] = 0 - 128;
        buffer[3] = 90 - 128;
        for(int i = 4; i < 16; i++)
        {
            buffer[i] = 0 - 128;
        }

        mControlView = (VehiculeControlView)findViewById(R.id.VehiculeContView);
        mControlView.setVisibility(View.VISIBLE);

        //mControlVehiculeView = (VehicleControlView)findViewById(R.id.VehicleControlView);
        //mControlVehiculeView.setVisibility(View.VISIBLE);
       // mControlVehiculeView.run();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(100);
                        if(mControlView.Warning)
                        {
                            //LED AR GAUCHE
                            buffer[4] = 120 - 128;
                            buffer[8] = 50 - 128;
                            buffer[12] = 0 - 128;
                            //LED AR DROITE
                            buffer[5] = 120 - 128;
                            buffer[9] = 50 - 128;
                            buffer[13] = 0 - 128;
                            //LED AV GAUCHE
                            buffer[6] = 120 - 128;
                            buffer[10] = 50 - 128;
                            buffer[14] = 0 - 128;
                            //LED AV DROITE
                            buffer[7] = 120 - 128;
                            buffer[11] = 50 - 128;
                            buffer[15] = 0 - 128;

                            //BREAK
                            sleep(500);
                            if(mControlView.Phare || mControlView.PleinPhare){
                                if(mControlView.Phare)
                                {
                                    //LED AR GAUCHE
                                    buffer[4] = 128 - 128;
                                    buffer[8] = 0 - 128;
                                    buffer[12] = 0 - 128;
                                    //LED AR DROITE
                                    buffer[5] = 128 - 128;
                                    buffer[9] = 0 - 128;
                                    buffer[13] = 0 - 128;
                                    //LED AV GAUCHE
                                    buffer[6] = 128 - 128;
                                    buffer[10] = 128 - 128;
                                    buffer[14] = 128 - 128;
                                    //LED AV DROITE
                                    buffer[7] = 128 - 128;
                                    buffer[11] = 128 - 128;
                                    buffer[15] = 128 - 128;
                                    sleep(300);
                                }
                                else{
                                    //LED AR GAUCHE
                                    buffer[4] = 128 - 128;
                                    buffer[8] = 0 - 128;
                                    buffer[12] = 0 - 128;
                                    //LED AR DROITE
                                    buffer[5] = 128 - 128;
                                    buffer[9] = 0 - 128;
                                    buffer[13] = 0 - 128;
                                    //LED AV GAUCHE
                                    buffer[6] = 255 - 128;
                                    buffer[10] = 255 - 128;
                                    buffer[14] = 255 - 128;
                                    //LED AV DROITE
                                    buffer[7] = 255 - 128;
                                    buffer[11] = 255 - 128;
                                    buffer[15] = 255 - 128;
                                    sleep(300);
                                }

                            }
                            else{
                                buffer[4] = 0 - 128;
                                buffer[8] = 0 - 128;
                                buffer[12] = 0 - 128;
                                //LED AR DROITE
                                buffer[5] = 0 - 128;
                                buffer[9] = 0 - 128;
                                buffer[13] = 0 - 128;
                                //LED AV GAUCHE
                                buffer[6] = 0 - 128;
                                buffer[10] = 0 - 128;
                                buffer[14] = 0 - 128;
                                //LED AV DROITE
                                buffer[7] = 0 - 128;
                                buffer[11] = 0 - 128;
                                buffer[15] = 0 - 128;
                                sleep(300);
                            }
                        }
                        else if(mControlView.ClignGauche){
                            //LED AR GAUCHE
                            buffer[4] = 120 - 128;
                            buffer[8] = 50 - 128;
                            buffer[12] = 0 - 128;
                            //LED AR DROITE
                            //buffer[5] = 0 - 128;
                            //buffer[9] = 0 - 128;
                            //buffer[13] = 0 - 128;
                            //LED AV GAUCHE
                            buffer[6] = 120 - 128;
                            buffer[10] = 50 - 128;
                            buffer[14] = 0 - 128;
                            //LED AV DROITE
                            //buffer[7] = 0 - 128;
                            //buffer[11] = 0 - 128;
                            //buffer[15] = 0 - 128;

                            //BREAK
                            sleep(500);
                            if(mControlView.Phare || mControlView.PleinPhare){
                                if(mControlView.Phare){
                                    //LED AR GAUCHE
                                    buffer[4] = 128 - 128;
                                    buffer[8] = 0 - 128;
                                    buffer[12] = 0 - 128;
                                    //LED AR DROITE
                                    buffer[5] = 128 - 128;
                                    buffer[9] = 0 - 128;
                                    buffer[13] = 0 - 128;
                                    //LED AV GAUCHE
                                    buffer[6] = 128 - 128;
                                    buffer[10] = 128 - 128;
                                    buffer[14] = 128 - 128;
                                    //LED AV DROITE
                                    buffer[7] = 128 - 128;
                                    buffer[11] = 128 - 128;
                                    buffer[15] = 128 - 128;
                                    sleep(300);
                                }
                                else{
                                    //LED AR GAUCHE
                                    buffer[4] = 128 - 128;
                                    buffer[8] = 0 - 128;
                                    buffer[12] = 0 - 128;
                                    //LED AR DROITE
                                    buffer[5] = 128 - 128;
                                    buffer[9] = 0 - 128;
                                    buffer[13] = 0 - 128;
                                    //LED AV GAUCHE
                                    buffer[6] = 255 - 128;
                                    buffer[10] = 255 - 128;
                                    buffer[14] = 255 - 128;
                                    //LED AV DROITE
                                    buffer[7] = 255 - 128;
                                    buffer[11] = 255 - 128;
                                    buffer[15] = 255 - 128;
                                    sleep(300);
                                }

                            }
                            else {
                                //LED AR GAUCHE
                                buffer[4] = 0 - 128;
                                buffer[8] = 0 - 128;
                                buffer[12] = 0 - 128;
                                //LED AR DROITE
                                //buffer[5] = 0 - 128;
                                //buffer[9] = 0 - 128;
                                //buffer[13] = 0 - 128;
                                //LED AV GAUCHE
                                buffer[6] = 0 - 128;
                                buffer[10] = 0 - 128;
                                buffer[14] = 0 - 128;
                                //LED AV DROITE
                                //buffer[7] = 0 - 128;
                                //buffer[11] = 0 - 128;
                                //buffer[15] = 0 - 128;
                                sleep(300);
                            }
                        }
                        else if(mControlView.ClignDroite){
                            //LED AR GAUCHE
                            //buffer[4] = 0 - 128;
                            //buffer[8] = 0 - 128;
                            //buffer[12] = 0 - 128;
                            //LED AR DROITE
                            buffer[5] = 120 - 128;
                            buffer[9] = 50 - 128;
                            buffer[13] = 0 - 128;
                            //LED AV GAUCHE
                            //buffer[6] = 0 - 128;
                            //buffer[10] = 0 - 128;
                            //buffer[14] = 0 - 128;
                            //LED AV DROITE
                            buffer[7] = 120 - 128;
                            buffer[11] = 50 - 128;
                            buffer[15] = 0 - 128;

                            //BREAK
                            sleep(500);
                            if(mControlView.Phare || mControlView.PleinPhare){
                                if(mControlView.Phare)
                                {
                                    //LED AR GAUCHE
                                    buffer[4] = 128 - 128;
                                    buffer[8] = 0 - 128;
                                    buffer[12] = 0 - 128;
                                    //LED AR DROITE
                                    buffer[5] = 128 - 128;
                                    buffer[9] = 0 - 128;
                                    buffer[13] = 0 - 128;
                                    //LED AV GAUCHE
                                    buffer[6] = 128 - 128;
                                    buffer[10] = 128 - 128;
                                    buffer[14] = 128 - 128;
                                    //LED AV DROITE
                                    buffer[7] = 128 - 128;
                                    buffer[11] = 128 - 128;
                                    buffer[15] = 128 - 128;
                                    sleep(300);
                                }
                                else{
                                    //LED AR GAUCHE
                                    buffer[4] = 128 - 128;
                                    buffer[8] = 0 - 128;
                                    buffer[12] = 0 - 128;
                                    //LED AR DROITE
                                    buffer[5] = 128 - 128;
                                    buffer[9] = 0 - 128;
                                    buffer[13] = 0 - 128;
                                    //LED AV GAUCHE
                                    buffer[6] = 255 - 128;
                                    buffer[10] = 255 - 128;
                                    buffer[14] = 255 - 128;
                                    //LED AV DROITE
                                    buffer[7] = 255 - 128;
                                    buffer[11] = 255 - 128;
                                    buffer[15] = 255 - 128;
                                    sleep(300);
                                }

                            }
                            else{
                                //LED AR GAUCHE
                                //buffer[4] = 0 - 128;
                                //buffer[8] = 0 - 128;
                                //buffer[12] = 0 - 128;
                                //LED AR DROITE
                                buffer[5] = 0 - 128;
                                buffer[9] = 0 - 128;
                                buffer[13] = 0 - 128;
                                //LED AV GAUCHE
                                //buffer[6] = 0 - 128;
                                //buffer[10] = 0 - 128;
                                //buffer[14] = 0 - 128;
                                //LED AV DROITE
                                buffer[7] = 0 - 128;
                                buffer[11] = 0 - 128;
                                buffer[15] = 0 - 128;
                                sleep(300);
                            }

                        }

                        if(mControlView.Phare)
                        {
                            if(mControlView.PleinPhare){
                                //LED AR GAUCHE
                                buffer[4] = 128 - 128;
                                buffer[8] = 0 - 128;
                                buffer[12] = 0 - 128;
                                //LED AR DROITE
                                buffer[5] = 128 - 128;
                                buffer[9] = 0 - 128;
                                buffer[13] = 0 - 128;
                                //LED AV GAUCHE
                                buffer[6] = 255 - 128;
                                buffer[10] = 255 - 128;
                                buffer[14] = 255 - 128;
                                //LED AV DROITE
                                buffer[7] = 255 - 128;
                                buffer[11] = 255 - 128;
                                buffer[15] = 255 - 128;
                            }
                            else{
                                //LED AR GAUCHE
                                buffer[4] = 128 - 128;
                                buffer[8] = 0 - 128;
                                buffer[12] = 0 - 128;
                                //LED AR DROITE
                                buffer[5] = 128 - 128;
                                buffer[9] = 0 - 128;
                                buffer[13] = 0 - 128;
                                //LED AV GAUCHE
                                buffer[6] = 128 - 128;
                                buffer[10] = 128 - 128;
                                buffer[14] = 128 - 128;
                                //LED AV DROITE
                                buffer[7] = 128 - 128;
                                buffer[11] = 128 - 128;
                                buffer[15] = 128 - 128;
                            }

                        }
                        else if (mControlView.PleinPhare == true){
                            //LED AR GAUCHE
                            //buffer[4] = 128 - 128;
                            //buffer[8] = 0 - 128;
                            //buffer[12] = 0 - 128;
                            //LED AR DROITE
                            //buffer[5] = 128 - 128;
                            //buffer[9] = 0 - 128;
                            //buffer[13] = 0 - 128;
                            //LED AV GAUCHE
                            buffer[6] = 255 - 128;
                            buffer[10] = 255 - 128;
                            buffer[14] = 255 - 128;
                            //LED AV DROITE
                            buffer[7] = 255 - 128;
                            buffer[11] = 255 - 128;
                            buffer[15] = 255 - 128;
                        }
                        else{
                            //LED AR GAUCHE
                            buffer[4] = 0 - 128;
                            buffer[8] = 0 - 128;
                            buffer[12] = 0 - 128;
                            //LED AR DROITE
                            buffer[5] = 0 - 128;
                            buffer[9] = 0 - 128;
                            buffer[13] = 0 - 128;
                            //LED AV GAUCHE
                            buffer[6] = 0 - 128;
                            buffer[10] = 0 - 128;
                            buffer[14] = 0 - 128;
                            //LED AV DROITE
                            buffer[7] = 0 - 128;
                            buffer[11] = 0 - 128;
                            buffer[15] = 0 - 128;
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float y = sensorEvent.values[1];
            Direction(y);
            Acceleration();
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

        buffer[3] = (byte) (direction - 128);
        if (BuildConfig.DEBUG) {
            //Log.i("FCT->Direction", "Direction: " + direction + " Buffer: " + buffer[2]);
        }
    }

    public void Acceleration(){
        Accel = (int)mControlView.repereAcceleration;
        if(Accel >= 0 && Accel < 255){
            buffer[0] = 1 - 128;
            buffer[1] = (byte) (Accel - 128);
            buffer[2] = (byte) (Accel - 128);
        }
        if (Accel < 0) {
            Accel *= -1;
            buffer[0] = 0 - 128;
            buffer[1] = (byte) (Accel - 128);
            buffer[2] = (byte) (Accel - 128);
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
    //**********************************************************************************************
    //                                  Gestion du bluetooth
    //**********************************************************************************************
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
}


