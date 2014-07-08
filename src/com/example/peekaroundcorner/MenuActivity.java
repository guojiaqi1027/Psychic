package com.example.peekaroundcorner;

import java.util.ArrayList;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuActivity extends Activity {
//public static String cameraId="camera1";
private ArrayList<Card> cardList;
	/**  
	 * Create the main menu
	 * Initial Cardscrollview and Adapter
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    cardList=new ArrayList<Card>();
	    initialCard();
	    CardScrollView cardMenu=new CardScrollView(this);
	    MenuAdapter menuAdapter=new MenuAdapter();
	    cardMenu.setAdapter(menuAdapter);
	    cardMenu.activate();
	    cardMenu.setOnItemClickListener(itemListner);
        setContentView(cardMenu);
	    // TODO Auto-generated method stub
	}
	private OnItemClickListener itemListner=new OnItemClickListener(){
		@Override
		/**
		 * Listener of menu tap
		 * 0 for camera display
		 * 1 for scanner QR code
		 */
	      public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	      {
			switch(position){
			case 0:
			{
				/**
				 * Intent to Camera activity
				 */
				Intent cameraIntent = new Intent();                 
	        	cameraIntent.setClass(MenuActivity.this, CameraActivity.class);                 
		    	startActivity(cameraIntent);                
		    	MenuActivity.this.finish();
		    	Log.v("MenuActivity_OnItemClickListner", "CameraStart");
		    	break;
			}
			case 1:
			{
				/**
				 * Intent to Scanner activity
				 */
				Intent scannerIntent = new Intent();                 
	        	scannerIntent.setClass(MenuActivity.this, ScannerActivity.class);                 
		    	startActivity(scannerIntent);                
		    	MenuActivity.this.finish();
		    	Log.v("MenuActivity_OnItemClickListner", "ScannerStart");
		    	break;
			}
			case 2:
			{
				/**
				 * Intent to Camera activity
				 */
				Intent sensorIntent = new Intent();                 
				sensorIntent.setClass(MenuActivity.this, AcceleratorActivity.class);                 
		    	startActivity(sensorIntent);                
		    	MenuActivity.this.finish();
		    	Log.v("MenuActivity_OnItemClickListner", "SensorActivityStart");
		    	break;
			}
			}
	      }
	};
	/**
	 * Initial the card menu
	 * Add several card selection to menu
	 */
	private void initialCard(){
		Card cameraCard=new Card(this);
		cameraCard.setImageLayout(Card.ImageLayout.FULL);
		cameraCard.setText("Camera");
		cameraCard.setFootnote("Show the default Camera");
		cardList.add(cameraCard);
		Card scannerCard=new Card(this);
		scannerCard.setImageLayout(Card.ImageLayout.FULL);
		scannerCard.setText("Scanner");
		scannerCard.setFootnote("Scan a QR code of Camera");
		cardList.add(scannerCard);
		Card acceleratorCard=new Card(this);
		acceleratorCard.setImageLayout(Card.ImageLayout.FULL);
		acceleratorCard.setText("Accelerator Test");
		acceleratorCard.setFootnote("Scan a QR code of Camera");
		cardList.add(acceleratorCard);
	}

	/**
	 * Extend CardScrollAdapter
	 */
	private class MenuAdapter extends CardScrollAdapter
    {
        @Override
        public int getCount()
        {
            return cardList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return cardList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return cardList.get(position).getView();
        }

        @Override
        public int getPosition(Object o)
        {
            return 0;
        }
    }

}
