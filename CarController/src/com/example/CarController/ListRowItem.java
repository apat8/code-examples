package com.example.bluetoothdemo2;

import android.widget.ProgressBar;

public class ListRowItem {

	public String nameAddress;
	public String pairing;
	ProgressBar pairingProgress;
	int progress;
	
	public ListRowItem(String nameAddress, String pairing){
		this.nameAddress = nameAddress;
		this.pairing = pairing;
	}
	
	public void setPairing(){
		pairing = "Pairing...";
	}
	
	public void setPaired(){
		pairing = "Paired";
	}
	
	public void clear(){
		pairing = "";
	}
	
	public String getPairing(){
		return pairing;
	}
	
	public String getName(){
		return nameAddress;
	}
	public int getProgressVisibility(){
		return progress;
	}
	
	public void setProgressVisibility(int visibility){
		progress = visibility;
	}
	
}
