package com.example.peekaroundcorner;
/**
 * Scanner activity using ZXing app
 */
import java.util.ArrayList;

import com.example.peekaroundcorner.camera.Camera;
import com.google.android.glass.app.Card;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

public class ScannerActivity extends Activity {
	private ArrayList<Camera> cameraList=null;
	private String playUrl=null;
	private Thread secondDelayThreadCamera;
	private Thread secondDelayThreadMenu;
	private Handler mHandler;
	private AudioManager mAudioManager;
	private GestureDetector mGestureDetector;
	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Card returnCard=new Card(this);
	    returnCard.setText("Swipe Left to Return");
	    returnCard.setImageLayout(Card.ImageLayout.FULL);
	    setContentView(returnCard.getView());
	    mAudioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
	    initialCameraNetwork();
	    mHandler = new Handler() {	
	    	/**
	    	 * Handle the message from threads
	    	 * 0 for no camera thread
	    	 * 1 for camera display thread
	    	 */
		    public void handleMessage(Message msg) {
		    	switch(msg.what){
		    	case 0:
		    		Intent menuIntent = new Intent();                 
		        	menuIntent.setClass(ScannerActivity.this, MenuActivity.class);                 
			    	startActivity(menuIntent);                
			    	ScannerActivity.this.finish();
		    		break;
		    	case 1:
		    		Intent cameraIntent = new Intent();                 
		        	cameraIntent.setClass(ScannerActivity.this, CameraActivity.class);                 
			    	startActivity(cameraIntent);                
			    	ScannerActivity.this.finish();
			    	break;
		    	}
		    }
		    };
		    /**
		     * Jump to menu after 1000ms
		     * No match camera detected
		     */
	    secondDelayThreadMenu=new Thread(new Runnable(){
	    	@SuppressWarnings("static-access")
			@Override

			public void run() {
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
	    /**
	     * Jump to Camera after 1000ms
	     * Matched camera detected
	     */
	    secondDelayThreadCamera=new Thread(new Runnable(){
	    	@SuppressWarnings("static-access")
			@Override

			public void run() {
	    		try {
					Thread.currentThread().sleep(3000);
					Message msg=new Message();
					msg.what=1;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    	
	    });
	    
	    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);	
	    // TODO Auto-generated method stub
	}
	/**
	 * Handle QR Code result
	 * Display result Card and start thread
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		   if (requestCode == 0) {
		      if (resultCode == RESULT_OK) {
		    	 
		         String contents = intent.getStringExtra("SCAN_RESULT");
		         Log.v("onActivityResult_CameraResult_ID", contents);
		         playUrl=searchUrl(contents);
		         Log.v("onActivityResult_CameraResult_URL", playUrl);
		         if(playUrl.equals("noResult")){
		        	Card noCameraFindCard=new Card(this);
		        	noCameraFindCard.setImageLayout(Card.ImageLayout.FULL);
		        	noCameraFindCard.setText("No Camera Found");
		        	noCameraFindCard.setFootnote("App will Return to Menu");
		        	setContentView(noCameraFindCard.getView());
		        	secondDelayThreadMenu.start();
		        	
		        	 
		         }
		         else{
		        	Context mContext=ScannerActivity.this;
		     		SharedPreferences mShared=mContext.getSharedPreferences("URL", MODE_PRIVATE);
		     		Editor mEditor=mShared.edit();
		     		mEditor.putString("playURL", playUrl);
		     		mEditor.commit();
		     		Card cameraCard=new Card(this);
		     		cameraCard.setImageLayout(Card.ImageLayout.FULL);
		     		cameraCard.setText("Now Openning:\n"+contents);
		     		cameraCard.setFootnote("Please wait...");
		        	setContentView(cameraCard.getView());
		        	secondDelayThreadCamera.start();
		         }
		         // Handle successful scan
		      } else if (resultCode == RESULT_CANCELED) {
		         // Handle cancel
		      }
		   }
		}
	public void initialCameraNetwork(){
		/**
		 * Initial the camera network
		 */
		Camera Camera1=new Camera("camera1","rtsp://192.168.206.132:554/axis-media/media.amp?videocodec=h264");
		cameraList=new ArrayList<Camera>();
		cameraList.add(Camera1);
	}
	public String searchUrl(String id){
		/**
		 * Searching for URL after read the QR code
		 */
		for(int i=0;i<cameraList.size();i++){
			if(cameraList.get(i).getId().equals(id)){
				return cameraList.get(i).getURL();
			}
		}
		return "noResult";
	}
	/**
	 * Override Gesture motion
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
    	Log.v("ScannerActivity_GestureDetector", "Gesture Detect");
        if (gesture == Gesture.SWIPE_LEFT) {
        	Log.v("ScannerActivity_GestureDetector", "Swipe_left Detect");
            mAudioManager.playSoundEffect(Sounds.DISMISSED);
            Intent menuIntent = new Intent();                 
            menuIntent.setClass(ScannerActivity.this, MenuActivity.class);                 
	    	startActivity(menuIntent);                
	    	ScannerActivity.this.finish();
            return true;
        } else {
            return false;
        }
    }
};

}
