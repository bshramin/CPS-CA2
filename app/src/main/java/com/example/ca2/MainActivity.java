package com.example.ca2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Boolean isRecording = false;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;

    private Sensor accelerationSensor;
    private SensorEventListener accelerationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscopeSensor == null) {
            Toast.makeText(this, "This device has no Gyroscope!", Toast.LENGTH_LONG).show();
            Log.d("applogs", "This device has no gyroscope sensor");
            finish();
        }

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (!isRecording) {
                    return;
                }
                float[] values = sensorEvent.values;
                Log.d("applogs-gyro", Arrays.toString(values));
                if (values[2] > 0.5f) {
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if (values[2] < -0.5f) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerationSensor == null) {
            Toast.makeText(this, "This device has no accelerometer!", Toast.LENGTH_LONG).show();
            Log.d("applogs", "This device has no accelerometer sensor");
            finish();
        }

        accelerationEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (!isRecording) {
                    return;
                }
                float[] values = sensorEvent.values;
                Log.d("applogs-acce", Arrays.toString(values));
                if (values[0] > 0.5f) {
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if (values[0] < -0.5f) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(accelerationEventListener, accelerationSensor,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeEventListener);
        sensorManager.unregisterListener(accelerationEventListener);
    }

    // Start stop recording button
    public void onClick(View view) {
        if (isRecording) {
            isRecording = false;
            Log.d("applogs", "Recording stopped!");
        } else {
            isRecording = true;
            Log.d("applogs", "Recording started!");
        }
    }
}