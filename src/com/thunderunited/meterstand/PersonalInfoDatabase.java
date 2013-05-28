package com.thunderunited.meterstand;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PersonalInfoDatabase {

	private static final String TAG = PersonalInfoDatabase.class.getName();
	
	private static final String NAME = "name";
	private static final String ADDRESS = "address";
	private static final String POSTCODE = "postcode";
	private static final String HOUSE_NUMBER = "houseNumber";
	private static final String ENERGY_COMPANY = "energyCompany";
	
	private static final String EMPTY_STRING = "";

	public void save(PersonalInfo personalInfo, Context context) {
		SharedPreferences settings = context.getSharedPreferences(TAG
				+ "Preferences", Context.MODE_PRIVATE);
		
		Editor preferencesEditor = settings.edit();
		
		preferencesEditor.putString(NAME, personalInfo.getName());
		preferencesEditor.putString(ADDRESS, personalInfo.getAddress());
		preferencesEditor.putString(POSTCODE, personalInfo.getPostcode());
		preferencesEditor.putString(HOUSE_NUMBER, personalInfo.getHouseNumber());
		preferencesEditor.putString(ENERGY_COMPANY, personalInfo.getEnergyCompany());
		
		preferencesEditor.commit();

	}

	public PersonalInfo get(Context context) {
		
		SharedPreferences settings = context.getSharedPreferences(TAG
				+ "Preferences", Context.MODE_PRIVATE);
		
		String name = settings.getString(NAME, EMPTY_STRING);
		String address = settings.getString(ADDRESS, EMPTY_STRING);
		String postcode = settings.getString(POSTCODE, EMPTY_STRING);
		String houseNumber = settings.getString(HOUSE_NUMBER, EMPTY_STRING);
		String energyCompany = settings.getString(ENERGY_COMPANY, EMPTY_STRING);

		PersonalInfo personalInfo = new PersonalInfo();
		personalInfo.setName(name);
		personalInfo.setAddress(address);
		personalInfo.setPostcode(postcode);
		personalInfo.setHouseNumber(houseNumber);
		personalInfo.setEnergyCompany(energyCompany);
		
		return personalInfo;
	}
}
