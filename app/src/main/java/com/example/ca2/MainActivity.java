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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Boolean isRecording = false;
    private ArrayList<Float> plotNumbers = new ArrayList<Float>();

    private Float scale = 0.1F;
    private Float positionX  = 0F;

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
                if (positionX > plotNumbers.size()) {
                    plotNumbers.add(values[2]);
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
                if (values[0] > 0.5f || values[0] < -0.5f){
                    positionX += values[0] * scale;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        Thread newThread = new Thread(() -> {
            while (true) {
                truncateChart();
                plotTheChart();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        });
        newThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(accelerationEventListener, accelerationSensor,SensorManager.SENSOR_DELAY_FASTEST);
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
            plotTheChart();
        } else {
            isRecording = true;
            plotNumbers.clear();
            positionX = 0.0F;
            truncateChart();
            Log.d("applogs", "Recording started!");
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show();
        }
    }

    private void truncateChart() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
    }

    private void plotTheChart() {
        GraphView graph = (GraphView) findViewById(R.id.graph);

        Log.d("applogs", plotNumbers.toString());
        Log.d("applogs:positionX",positionX.toString());

        DataPoint[] dataPoints = new DataPoint[plotNumbers.size()];

        for (int i=0;i< plotNumbers.size();i++) {
            dataPoints[i] = new DataPoint(i, plotNumbers.get(i));
        }

        Log.d("applogs", String.valueOf(dataPoints.length));

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        series.setDrawDataPoints(true);
        graph.getViewport().setXAxisBoundsManual(false);
        graph.addSeries(series);
    }
}