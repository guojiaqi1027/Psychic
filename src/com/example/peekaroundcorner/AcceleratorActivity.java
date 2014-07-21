/**
 * Accelerator Test activity
 * Using accelerometer and orientation sensor to calculate location coordinate
 */
package com.example.peekaroundcorner;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.Socket;

import com.example.peekaroundcorner.navigation.User;
import com.example.peekaroundcorner.socket.Client;
import com.google.android.glass.app.Card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AcceleratorActivity extends Activity implements SensorEventListener,LocationListener{

	/** Called when the activity is first created. */
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;
	private Sensor mMagneticSensor;
	private Sensor mAccelerometerSensor;
	private Sensor mRotateSensor;
	private User mUser;
	private Client clientSocket;
	private Thread readSensorThread;
	private Thread orientationSend;
	private Thread loginThread;
	private Thread connectThread;
	private Handler mHandler;
	private boolean isInitialed=false;
	private boolean sensorThreadOn=true;
	float[] accelerometerValues = new float[3];  
    float[] magneticFieldValues = new float[3]; 
    float[] mRotationMatrix = new float[16];
    float[] mOrientation = new float[9];
    private Location mLocation;
    private GeomagneticField mGeomagneticField;
    private LocationManager mLocationManager;
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
    	sensorCard.getView().setKeepScreenOn(true);
    	mUser=new User("Gawain");
    	/*
    	 * Create and register for sensors
    	 */
	    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    mMagneticSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    mAccelerometerSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	   mOrientationSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	   mRotateSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	   
	   /*
	    * Create location manager
	    */
	   mLocationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	   mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
					while(sensorThreadOn){
					Thread.currentThread().sleep(200);
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
	    connectThread=new Thread(new Runnable(){
	    	/*
	    	 * Connecting Socket with server
	    	 */
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				clientSocket=new Client("192.168.206.205",8000);
				try {
					clientSocket.BuildUpConnection();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg=new Message();
				msg.what=3;
				mHandler.sendMessage(msg);
				
			}
	    	
	    });
	    connectThread.start();
	    /*
	     * Send orientation data to server
	     */
	    orientationSend=new Thread(new Runnable(){
			@Override
			public void run() {
    			clientSocket.messageSend("ORI"+"#"+String.valueOf(mUser.orientation));
				
			}
	    	
	    });
	    loginThread=new Thread(new Runnable(){
			@Override
			public void run() {
				boolean login=false;
    			try {
    				/*
    				 * Scanning User name until login
    				 */
					while(!login){
					clientSocket.messageSend("LOGIN#"+mUser.userName);
					Log.v("ORIENTATIONACTIVITY_LOGINTHREAD", "LOGIN#"+mUser.userName);
					String result=clientSocket.messageReceive();
					if(result.equals("true")){
						login=true;
					}
					}
					/*
					 * Scanning initial coordinate
					 */
					clientSocket.messageSend("INI#0#0");
					Log.v("ORIENTATIONACTIVITY_LOGINTHREAD", "INI#0#0");
					Message msg=new Message();
					msg.what=1;
					mHandler.sendMessage(msg);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	    	
	    });
	    /*
	     * Handler message per 20ms
	     * Start sending thread, send data to server per 20ms
	     * isInitialed is true after login and initialed
	     */
	    mHandler=new Handler(){
	    	public void handleMessage(Message msg){
	    		switch(msg.what){
	    		case 0:
	    			sensorCard.setFootnote(String.valueOf(mUser.orientation));
	    			setContentView(sensorCard.getView());
	    			if(isInitialed){
	    			Thread send=new Thread(orientationSend);
	    			send.start();
	    			}
	    			break;
	    		case 1:
	    			isInitialed=true;
	    			break;
	    		case 3:
	    			loginThread.start();
	    			readSensorThread.start();
	    			break;
	    			
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
	 */
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Get the current heading from the sensor, then notify the listeners of the
            // change.
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
            SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, mRotationMatrix);
            SensorManager.getOrientation(mRotationMatrix, mOrientation);
            /*
             * Compute head orientation with sensor and location provider
             */
            float mHeading=(float)Math.toDegrees(mOrientation[0]);
            mUser.orientation=((int)(mod(computeTrueNorth(mHeading),360.f)-6)/5)*5;
		}
	}
	public float mod(float a, float b) {
        return (a % b + b) % b;
    }
	/*
	 * Compute real orientation combine with location and sensor
	 */
	private float computeTrueNorth(float heading) {
        if (mGeomagneticField != null) {
            return heading + mGeomagneticField.getDeclination();
        } else {
            return heading;
        }
    }
	protected void onResume() {
        super.onResume();
        /*
         * Register for sensors
         */
        mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
       mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
       mSensorManager.registerListener(this, mRotateSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
     sensorThreadOn=false;
    }
    /*
     * when location changed, update geomagnetic field
     */
	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
        updateGeomagneticField();
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	/*
	 * Get geomagneticfield information when location change
	 */
	public void updateGeomagneticField(){
		mGeomagneticField = new GeomagneticField((float) mLocation.getLatitude(),
                (float) mLocation.getLongitude(), (float) mLocation.getAltitude(),
                mLocation.getTime());
	}

}
