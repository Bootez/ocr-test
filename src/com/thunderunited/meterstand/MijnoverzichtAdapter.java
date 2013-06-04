package com.thunderunited.meterstand;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MijnoverzichtAdapter extends BaseAdapter{

   private static ArrayList<String> arrayResults;
	 
	 private LayoutInflater mInflater;

	 public MijnoverzichtAdapter(Context context, ArrayList<String> results) {
		 results = arrayResults;
		 mInflater = LayoutInflater.from(context);
	 }

	 public int getCount() {
		 return arrayResults.size();
	 }

	 public Object getItem(int position) {
	  return arrayResults.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(int position, View convertView, ViewGroup parent) {
	  ViewHolder holder;
	  if (convertView == null) {
	   convertView = mInflater.inflate(R.layout.custom_row_view, null);
	   holder = new ViewHolder();
	   holder.txtName = (TextView) convertView.findViewById(R.id.soortMeter);
	   //holder.txtCityState = (TextView) convertView.findViewById(R.id.stand);
	   //holder.txtPhone = (TextView) convertView.findViewById(R.id.soortStand);

	   convertView.setTag(holder);
	  } else {
	   holder = (ViewHolder) convertView.getTag();
	  }
	  
//	  holder.txtName.setText(results.get(position).getName());
//	  holder.txtCityState.setText(results.get(position).getCityState());
//	  holder.txtPhone.setText(results.get(position).getPhone());

	  return convertView;
	 }

	 static class ViewHolder {
	  TextView txtName;
	  TextView txtCityState;
	  TextView txtPhone;
	 }
}
