package com.thunderunited.meterstand;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Handmatig extends Activity {

	private Button _button;
	private static final String DATA_PATH = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ ScanActivity.class.getPackage().getName() + "/";
	private String DB_NAME = DATA_PATH + "/foto.db";
	private String TABLE_NAME = "handmatig";
	private Spinner spinner1, spinner2;
	private SimpleDateFormat dateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd");
	private String crntDate = dateFormatISO8601.format(new Date());
	public static final String METER_LEVEL = "meter_level";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		String meterValue = null;
		if (bundle != null) {
			meterValue = bundle.getString(METER_LEVEL);
		}
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_handmatig);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		
		if (meterValue != null) {
			EditText meterValueEditText = (EditText)findViewById(R.id.tbValue);
			meterValueEditText.setText(meterValue);
		}
		
		_button = (Button) findViewById(R.id.btnSubmit);
		_button.setOnClickListener(new ButtonClickHandler());
		
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
	
	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			createDatabase();
			updateDatabase();
		}
	}
	
	void createDatabase() {
        SQLiteDatabase myDb = openOrCreateDatabase(DB_NAME,Context.MODE_PRIVATE, null);
        String MySQL = "create table if not exists "
                + TABLE_NAME
                + " (_id INTEGER primary key autoincrement, soortMeter TEXT not null, stand INTEGER not null, soortStand TEXT not null, datum DATETIME);";
        myDb.execSQL(MySQL);
        myDb.close();
    }
	
	void updateDatabase(){
		spinner1 = (Spinner) findViewById(R.id.sprTypeofMeter);
		spinner2 = (Spinner) findViewById(R.id.sprTypeofValue);
		EditText stand = (EditText) findViewById(R.id.tbValue);
		
		if(spinner1.getSelectedItem() == "Soort Meter")
		{
			Toast.makeText(this, "U heeft geen soort meter ingevuld!", Toast.LENGTH_SHORT).show();
		}
		else if(isEmpty(stand) != false)
		{
			Toast.makeText(this, "U heeft geen stand ingevuld!", Toast.LENGTH_SHORT).show();
		}
		else if(spinner2.getSelectedItem() == "Soort Stand")
		{
			Toast.makeText(this, "U heeft geen soort stand ingevuld!", Toast.LENGTH_SHORT).show();
		}
		else{
			SQLiteDatabase myDb = openOrCreateDatabase(DB_NAME,Context.MODE_PRIVATE, null);
			String MySQL = "INSERT INTO " 
				+ TABLE_NAME + 
				"(soortMeter, stand, soortStand, datum) VALUES('" + spinner1.getSelectedItem() + "','" + spinner2.getSelectedItem() + "','" + stand.getText().toString() + "', '" + crntDate + "')";
			myDb.execSQL(MySQL);
	        myDb.close();
	        super.finish();
		}
	}
	private boolean isEmpty(EditText etText) {
	    if (etText.getText().toString().trim().length() > 0) {
	        return false;
	    } else {
	        return true;
	    }
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
