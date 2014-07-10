/**
 * Accelerator Test activity
 * Using accelerometer and orientation sensor to calculate location coordinate
 */
package com.example.peekaroundcorner;

import java.io.IOException;
import java.math.RoundingMode;
import com.example.peekaroundcorner.navigation.User;
import com.example.peekaroundcorner.socket.Client;
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
	private User mUser;
	/*private double accelerator_x=0;
	private double accelerator_y=0;
	private double accelerator_z=0;*/
	
	private Thread readSensorThread;
	private Thread orientationSend;
	private Handler mHandler;
	@SuppressWarnings("deprecation")
	@SuppressLint({ "InlinedApi", "HandlerLeak" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    final Card sensorCard=new Card(this);
	    sensorCard.setImageLayout(Card.ImageLayout.FULL);
	    sensorCard.setText("Accelerator Test");
	    sensorCard.setFootnote("Sensor Start");
    	setContentView(sensorCard.getView());
    	mUser=new User("Gawain");
    	/*
    	 * Create and register for sensors
    	 */
	    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    mAcceleratorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    mOrientationSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	    /*
	     * Initial location object with current coordinates
	     */
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
					Thread.currentThread().sleep(100);
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
	    orientationSend=new Thread(new Runnable(){
			@Override
			public void run() {
				Client c=new Client("192.168.206.205",8000);
    			try {
					c.BuildUpConnection();
					c.messageSend("ORI"+"#"+mUser.userName+"#"+String.valueOf(mUser.orientation));
					c.closeConnection();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	    	
	    });
	    /*
	     * Handler message per 20ms
	     * Transfer sensor data into system coordinate
	     * Calculate current coordinates
	     */
	    mHandler=new Handler(){
	    	public void handleMessage(Message msg){
	    		switch(msg.what){
	    		case 0:
	    			/*
	    			location.updateAcceleration(accelerator_x,accelerator_z,orientation);
	    			location.updateSpeed();
	    			location.updateCoordinate();*/
	    			Log.v("AcceleratorActivity_mHandler_handleMessage", String.valueOf(mUser.orientation));
	    			//sensorCard.setFootnote(String.valueOf(location.speed_x)+"  "+String.valueOf(location.speed_y));
	    			//sensorCard.setFootnote(String.valueOf(orientation));
	    			sensorCard.setFootnote(String.valueOf(mUser.orientation));
	    			setContentView(sensorCard.getView());
	    			Thread send=new Thread(orientationSend);
	    			send.start();
	    			
	    		}
	    	}
	    };
	
	    // TODO Auto-generated method stub
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	/*
	 * Read Sensor data when change
	 * 2 sensor used, need to determine
	 */
	public void onSensorChanged(SensorEvent event) {
		/*if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
			
			DecimalFormat df = new DecimalFormat( "#.0 ");
			df.setRoundingMode(RoundingMode.HALF_UP);  
			if(Math.abs(event.values[0])<=0.15){
				accelerator_x=0;
			}
			else accelerator_x=Double.valueOf(df.format(event.values[0]));
			//accelerator_y=Double.valueOf(df.format(event.values[1]));
			if(Math.abs(event.values[2])<=0.15){
				accelerator_z=0;
			}
			else accelerator_z=Double.valueOf(df.format(event.values[2]));
		}*/
		if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
			/*
			 * Read orientation data
			 */
			mUser.orientation=event.values[0];
		}
	}
	protected void onResume() {
        super.onResume();
        /*
         * Register for 2 sensors
         */
        mSensorManager.registerListener(this, mAcceleratorSensor, SensorManager.SENSOR_DELAY_FASTEST);
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
      * Stop handle message
      */
     mSensorManager.unregisterListener(this); 
     readSensorThread.stop();
    }

}
