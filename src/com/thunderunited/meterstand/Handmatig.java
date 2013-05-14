package com.thunderunited.meterstand;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Handmatig extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_handmatig);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);

		// Type Meter lijst
		List<String> typeofMeterList = new ArrayList<String>();
		typeofMeterList.add("Soort Meter");
		typeofMeterList.add("Particulier");
		typeofMeterList.add("Business");

		// Vullen van Dropdown type Meter
		fillSpinner((Spinner) findViewById(R.id.sprTypeofMeter),
				typeofMeterList);

		// Type Meterstand
		List<String> typeofValueList = new ArrayList<String>();
		typeofValueList.add("Soort Stand");
		typeofValueList.add("MHz");
		typeofValueList.add("KHz");

		// Vullen van Dropdown type Meterstand
		fillSpinner((Spinner) findViewById(R.id.sprTypeofValue),
				typeofValueList);
	}

	public void fillSpinner(Spinner id, List<String> spin) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spin);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner Items = id;
		Items.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.handmatig, menu);
		return true;
	}

}
