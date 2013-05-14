package com.thunderunited.meterstand;

import com.example.test_ocr1.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class Overzicht extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);		 
		setContentView(R.layout.activity_overzicht); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

	}
	
	public void infoClick(View v) {
		startChosenActivity(Information.class);
	}
	
	public void manualClick(View v) {
		startChosenActivity(Handmatig.class);
	}
	
	public void scannenClick(View v) {
		startChosenActivity(ScanActivity.class);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.overzicht, menu);
		return true;
	}
	
	public void startChosenActivity(Class actName) {
		Intent intent = new Intent(this, actName);
		startActivity(intent);
	}

}
