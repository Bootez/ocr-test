package com.thunderunited.meterstand;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PersonalInfoDatabase {

	private static final String TAG = PersonalInfoDatabase.class.getName();
	
	
	private static final String EMPTY_STRING = "";

	public void save(PersonalInfo personalInfo, Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG
				+ "Preferences", Context.MODE_PRIVATE);
		
		Editor preferencesEditor = settings.edit();
		
		String nameKey = context.getString(R.string.preferences_key_name);
		String addressKey = context.getString(R.string.preferences_key_address);
		String postcodeKey = context.getString(R.string.preferences_key_postcode);
		String houseNumberKey = context.getString(R.string.preferences_key_house_number);
		String energyCompanyKey = context.getString(R.string.preferences_key_energy_company);
		
		
		preferencesEditor.putString(nameKey, personalInfo.getName());
		preferencesEditor.putString(addressKey, personalInfo.getAddress());
		preferencesEditor.putString(postcodeKey, personalInfo.getPostcode());
		preferencesEditor.putString(houseNumberKey, personalInfo.getHouseNumber());
		preferencesEditor.putString(energyCompanyKey, personalInfo.getEnergyCompany());
		
		preferencesEditor.commit();

	}

	public PersonalInfo get(Context context) {
		
		SharedPreferences settings = context.getSharedPreferences(TAG
				+ "Preferences", Context.MODE_PRIVATE);
		
		String nameKey = context.getString(R.string.preferences_key_name);
		String addressKey = context.getString(R.string.preferences_key_address);
		String postcodeKey = context.getString(R.string.preferences_key_postcode);
		String houseNumberKey = context.getString(R.string.preferences_key_house_number);
		String energyCompanyKey = context.getString(R.string.preferences_key_energy_company);
		
		String name = settings.getString(nameKey, EMPTY_STRING);
		String address = settings.getString(addressKey, EMPTY_STRING);
		String postcode = settings.getString(postcodeKey, EMPTY_STRING);
		String houseNumber = settings.getString(houseNumberKey, EMPTY_STRING);
		String energyCompany = settings.getString(energyCompanyKey, EMPTY_STRING);

		PersonalInfo personalInfo = new PersonalInfo();
		personalInfo.setName(name);
		personalInfo.setAddress(address);
		personalInfo.setPostcode(postcode);
		personalInfo.setHouseNumber(houseNumber);
		personalInfo.setEnergyCompany(energyCompany);
		
		return personalInfo;
	}
}
