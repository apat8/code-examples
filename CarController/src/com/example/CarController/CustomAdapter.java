package com.example.bluetoothdemo2;

import java.util.ArrayList;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<ListRowItem> {
	
	Context context;
	ArrayList<ListRowItem> arrayListItem;
	ProgressBar progress;
	
	public CustomAdapter(Context context, ArrayList<ListRowItem> arrayListItem){
		super(context, R.layout.single_row, arrayListItem);
		
		this.context = context;
		this.arrayListItem = arrayListItem;
			
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int[] colors = new int[] {0x1060015, color.holo_green_dark };
		//0x30ffffff
		View row = inflater.inflate(R.layout.single_row, parent, false);
		
		TextView name = (TextView) row.findViewById(R.id.deviceTitle);
		TextView pairing = (TextView) row.findViewById(R.id.pairingText);
		progress = (ProgressBar) row.findViewById(R.id.pairingProgressBar);
		
		
		name.setText(arrayListItem.get(position).getName());
		pairing.setText(arrayListItem.get(position).getPairing());
		progress.setVisibility(arrayListItem.get(position).getProgressVisibility());
		
		
		name.setTextSize(20);
		pairing.setTextSize(18);
		
		//int colorPos = position % colors.length;
		//row.setBackgroundColor(colors[colorPos]);
		 
		if (position % 2 == 1) {
		    row.setBackgroundResource(R.drawable.selecting_list_item);  
		} else {
		    row.setBackgroundResource(R.drawable.selecting_list_item);  
		}

		
		return row;

	}
	
}
