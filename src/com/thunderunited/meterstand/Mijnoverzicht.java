package com.thunderunited.meterstand;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Mijnoverzicht extends Activity {

  private static final String DATA_PATH = Environment
			.getExternalStorageDirectory()
			+ "/"
			+ ScanActivity.class.getPackage().getName() + "/";
	private String DB_NAME = DATA_PATH + "/foto.db";
	private String TABLE_NAME = "handmatig";
	private String _id;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mijnoverzicht);
		listView = (ListView) findViewById(R.id.listView1);
		ArrayList<HashMap<String, String>> searchResults = openDatabase();

		SimpleAdapter arrayAdapter = new SimpleAdapter(this, searchResults,
				R.layout.custom_row_view, new String[] { "soortMeter",
						"soortStand", "stand", "datum" }, new int[] {
						R.id.soortMeter, R.id.soortStand, R.id.stand,
						R.id.datum });
		listView.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mijnoverzicht, menu);
		return true;
	}

	ArrayList<HashMap<String, String>> openDatabase() {
		SQLiteDatabase myDb = openOrCreateDatabase(DB_NAME,
				Context.MODE_PRIVATE, null);

		Cursor c = myDb.rawQuery(
				"SELECT _id, soortMeter, stand, soortStand, datum FROM "
						+ TABLE_NAME, null);

		ArrayList<HashMap<String, String>> mArrayList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		// map.put("soortMeter", "Soort meter");
		// map.put("soortStand", "Soort stand");
		// map.put("stand", "Stand");
		// map.put("datum", "Datum");
		// mArrayList.add(map);

		c.moveToFirst();

		while (!c.isAfterLast()) {
			map = new HashMap<String, String>();
			map.put("soortMeter",
					"Soort meter: "
							+ c.getString(c.getColumnIndex("soortMeter")));
			map.put("soortStand",
					"Soort stand: "
							+ c.getString(c.getColumnIndex("soortStand")));
			map.put("stand", "Stand: " + c.getString(c.getColumnIndex("stand")));
			map.put("datum", "Datum: " + c.getString(c.getColumnIndex("datum")));
			mArrayList.add(map);
			c.moveToNext();
		}

		for (int i = 0; i < mArrayList.size(); i++) {
			Log.e("array: ", mArrayList.get(i).toString());
		}

		myDb.close();
		return mArrayList;
	}
}
