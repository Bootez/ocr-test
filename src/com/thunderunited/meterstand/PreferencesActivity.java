package com.thunderunited.meterstand;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.activity_preferences);

		String title = getString(R.string.title_activity_preferences);
		setTitle(title);
	}
}
