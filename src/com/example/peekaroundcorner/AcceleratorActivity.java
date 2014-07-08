/**
 * Accelerator Test activity
 */
package com.example.peekaroundcorner;

import com.example.peekaroundcorner.navigation.Location;
import com.google.android.glass.app.Card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AcceleratorActivity extends Activity implements SensorEventListener{

	/** Called when the activity is first created. */
	private SensorManager mSensorManager;
	private Sensor mAcceleratorSensor;
	private Sensor mOrientationSensor;
	private double accelerator_x=0;
	private double accelerator_y=0;
	private double accelerator_z=0;
	private float orientation=0;
	
	private Thread readSensorThread;
	private Handler mHandler;
	@SuppressLint({ "InlinedApi", "HandlerLeak" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    final Card sensorCard=new Card(this);
	    sensorCard.setImageLayout(Card.ImageLayout.FULL);
	    sensorCard.setText("Accelerator Test");
	    sensorCard.setFootnote("Sensor Start");
    	setContentView(sensorCard.getView());
    	/*
    	 * Create and register for sensors
    	 */
	    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    mAcceleratorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    mOrientationSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    /*
	     * Initial location object with current coordinates
	     */
	    final Location location=new Location(0,0);
	    readSensorThread=new Thread(new Runnable(){
	    	/*
	    	 * Read sensor data per 20ms
	    	 */
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(true){
					Thread.currentThread().sleep(20);
					Message msg=new Message();
					msg.what=0;
					mHandler.sendMessage(msg);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	    	
	    });
	    readSensorThread.start();
	    /*
	     * Handler message per 20ms
	     * Transfer sensor data into system coordinate
	     * Calculate current coordinates
	     */
	    mHandler=new Handler(){
	    	public void handleMessage(Message msg){
	    		switch(msg.what){
	    		case 0:
	    			location.updateAcceleration(accelerator_x,accelerator_z,orientation);
	    			location.updateSpeed();
	    			location.updateCoordinate();
	    			Log.v("AcceleratorActivity_mHandler_handleMessage", String.valueOf(accelerator_x)+" "+String.valueOf(accelerator_y)+" "+String.valueOf(accelerator_z));
	    			sensorCard.setFootnote(String.valueOf(location.speed_x)+"  "+String.valueOf(location.speed_y));
	    			//sensorCard.setFootnote(String.valueOf(orientation));
	    			setContentView(sensorCard.getView());
	    		}
	    	}
	    };
	
	    // TODO Auto-generated method stub
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	/*
	 * Read Sensor data when change
	 * 2 sensor used, need to determine
	 */
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
			/*
			 * Read Accelerometer data
			 */
			accelerator_x=event.values[0];
			accelerator_y=event.values[1];
			accelerator_z=event.values[2];
		}
		else if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
			/*
			 * Read orientation data
			 */
			orientation=event.values[0];
		}
	}
	protected void onResume() {
        super.onResume();
        /*
         * Register for 2 sensors
         */
        mSensorManager.registerListener(this, mAcceleratorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.v("AcceleratorActivity_onResume", "Sensor_Start");
    }
    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
     // TODO Auto-generated method stub
     super.onPause();
     /*
      * Unregister sensors
      */
     mSensorManager.unregisterListener(this); 
     readSensorThread.stop();
    }

}
