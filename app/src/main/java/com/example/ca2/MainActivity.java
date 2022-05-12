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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Boolean isRecording = false;
    // Fill this in order to create the plot at the end
    private Float[] plotNumbers = {};

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
            Toast.makeText(this, "Recording stopped!", Toast.LENGTH_SHORT).show();
            GraphView graph = (GraphView) findViewById(R.id.graph);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });
            graph.addSeries(series);
        } else {
            isRecording = true;
            Log.d("applogs", "Recording started!");
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show();
        }
    }
}