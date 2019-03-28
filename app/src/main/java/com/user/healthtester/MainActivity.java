package com.user.healthtester;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Handler;
import android.os.Message;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    private TextView TvSteps;
    private TextView TvSpeeds;
    private TextView TvStatus;
    private StepDetector simpleStepDetector;
    private SensorManager mSensroMgr;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private double timeBetween = 0.0;
    Date dateending = new Date();
    private int timeInterval = 3000;
    private DataManager dataManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parameters.init(MainActivity.this);
        this.setTitle("Smart steps");

        TvSteps = this.findViewById(R.id.tv_steps);
        TvSpeeds = this.findViewById(R.id.tv_speeds);
        TvStatus = this.findViewById(R.id.tv_status);

        // Get an instance of the SensorManager
        mSensroMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Sensor mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accel = mSensroMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);


        Button BtnStart = findViewById(R.id.btn_start);
        Button BtnStop = findViewById(R.id.btn_stop);

        dataManager = new DataManager(MainActivity.this,TvStatus, TvSteps, TvSpeeds,
                mSensroMgr);
        handler.postDelayed(runnable, timeInterval);

        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                mSensroMgr.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                mSensroMgr.unregisterListener(MainActivity.this);
                numSteps = 0;
                timeBetween = 0.0;
                TvSteps.setText(TEXT_NUM_STEPS + numSteps);
                TvSpeeds.setText("Unit Speed is " + timeBetween);
                TvStatus.setText("Status is " + "stop");
            }
        });

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnable, timeInterval);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        numSteps = 0;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        mSensroMgr.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensroMgr.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

    }


    @Override
    public void step(long timeNs) {
        String status = "";
        Date datebeginning = new Date();
        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        timeBetween = (datebeginning.getTime() - dateending.getTime())  / 100.0;

        if (timeBetween <= 1 ) {
            status = "stop";
        } else if (timeBetween >= 11){
            status = "Running";
        } else {
            status = "Walking";
        }
        dateending = new Date();
        TvStatus.setText("Status is " + status);
        TvSpeeds.setText("Unit Speed is " + timeBetween);

        dataManager.sendData( status, numSteps, timeBetween);
    }

}
