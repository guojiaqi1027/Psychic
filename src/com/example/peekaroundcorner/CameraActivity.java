/**
 * Camera Activity
 * Display Camera video stream via rtsp url
 */
package com.example.peekaroundcorner;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.VideoView;
import com.example.peekaroundcorner.R;
import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class CameraActivity extends Activity implements SensorEventListener {
private VideoView rtspVideoView=null;
private MediaController rtspVideoController=null;
private Thread mediaThread=null;
private Thread scannerThread=null;
private Handler mHandler;
private String playUrl;
private AudioManager mAudioManager;
private GestureDetector mGestureDetector;
private SensorManager cameraSensorManager;
private Sensor cameraRotateSensor;
	@SuppressWarnings("deprecation")
	@SuppressLint({ "InlinedApi", "HandlerLeak" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		mAudioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
		cameraSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		cameraRotateSensor=cameraSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		rtspVideoController=new MediaController(this);
		rtspVideoView=(VideoView)findViewById(R.id.rtsp_video);
		rtspVideoController.setAnchorView(rtspVideoView);
		rtspVideoController.setMediaPlayer(rtspVideoView);
		/**
		 * Read current(default) URL from Preference file
		 */
		Context mContext=CameraActivity.this;
 		SharedPreferences mShared=mContext.getSharedPreferences("URL", MODE_PRIVATE);
 		playUrl=mShared.getString("playURL", "rtsp://192.168.206.132:554/axis-media/media.amp?videocodec=h264");
 		Log.v("CameraActivity_onCreate_playUrl", playUrl);
 		mHandler = new Handler() {
 			/**
 			 * Intent to camera activity
 			 */
 			public void handleMessage(Message msg) {
	    	switch(msg.what){
	    	case 0:
	    		Log.v("CameraActivity_mHandler", "Open Scanner Intent");
	    		Intent scannerIntent = new Intent();                 
	        	scannerIntent.setClass(CameraActivity.this, ScannerActivity.class);                 
		    	startActivity(scannerIntent);                
		    	CameraActivity.this.finish();
	    		break;

	    	}
	    }
	    };
		mediaThread=new Thread(new Runnable(){
			@Override
			/**
			 * Thread of camera display
			 */
			public void run() {
				Uri rtspVideo=Uri.parse(playUrl);
				rtspVideoView.setMediaController(rtspVideoController);
				rtspVideoView.setVideoURI(rtspVideo);
				rtspVideoView.start();
			}
	    });
		mediaThread.start();
		scannerThread=new Thread(new Runnable(){
			@SuppressWarnings("static-access")
			@Override

			/**
			 * Thread of intent to scanner activity
			 */
			public void run() {
				Log.v("CameraActivity_intentThreadScanner","ScannerThread start");
	    		try {
					Thread.currentThread().sleep(3000);
					Message msg=new Message();
					msg.what=0;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    	
	    });
		
	
	}
	/**
	 * Override Gesture event
	 * Active Gesture detector
	 */
	 @Override
	    public boolean onGenericMotionEvent(MotionEvent event) {
	        return mGestureDetector.onMotionEvent(event);
	    }
	 /**
	  * Gesture Detect
	  * Using BaseListner detector 
	  * Detect for swipe_left
	  */
	private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
        	Log.v("CameraActivity_GestureDetector", "Gesture Detect");
            if (gesture == Gesture.SWIPE_LEFT) {
            	Log.v("CameraActivity_GestureDetector", "Swipe_up Detect");
                mAudioManager.playSoundEffect(Sounds.DISMISSED);
                Intent menuIntent = new Intent();                 
                menuIntent.setClass(CameraActivity.this, MenuActivity.class);                 
		    	startActivity(menuIntent);                
		    	CameraActivity.this.finish();
                return true;
            } else {
                return false;
            }
        }
    };
    protected void onResume() {
        super.onResume();
        cameraSensorManager.registerListener(this, cameraRotateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
     // TODO Auto-generated method stub
     super.onPause();
     cameraSensorManager.unregisterListener(this); 
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    /**
     * Sensor data catch
     * When head up, display the card and run the intent thread
     * Detect angle is -120(30)
     */
	public void onSensorChanged(SensorEvent event){
		float x=event.values[1];
		if(x<=-120){
			/**
			 * Stop sensor manager to avoid open other thread
			 */
			cameraSensorManager.unregisterListener(this); 
			Log.v("CameraActivity_onSensorChanged","Start Scanner");
			Card scannerCard=new Card(this);
			/**
			 * Display the Intent card
			 */
			scannerCard.setImageLayout(Card.ImageLayout.FULL);
			scannerCard.setText("Now Openning Scanner");
			scannerCard.setFootnote("Please wait...");
        	setContentView(scannerCard.getView());
        	scannerThread.start();
		}
	}
}

