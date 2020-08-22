package com.example.bluetoothdemo2;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Connection extends Application {

	BluetoothSocket btSocket;
	boolean useButtons;
	boolean useMotion;
	boolean useVoice;
	int selected;
	BluetoothDevice newDevice;
	
	public BluetoothSocket getBluetoothSokcet(){
		return btSocket;
		
	}
	
	public void setBluetoothSocket(BluetoothSocket newSocket){
		btSocket = newSocket;
	}
	
	public void setControls(String option){
		if(option.equals("Buttons")){
			useButtons = true;
			selected = 0;
		}
			
		if(option.equals("Motion")){
			useMotion = true;
			selected = 1;
		}
		
		if(option.equals("Voice")){
			useVoice = true;
			selected = 2;
		}	
			
	}
	
	public void setFalse(){
		useButtons = false;
		useMotion= false;
		useVoice = false;
	}
	
	public String Selected(){
		if(useButtons){
			return("Buttons");
		}
		if(useMotion){
			return("Motion");
		}
		if(useVoice){
			return("Voice");
		}
		return null;	
	}
	
	public int selectedControls(){
		return selected;
	}
	
	public void setDevice(BluetoothDevice device){
		newDevice = device;
	}
	
	public BluetoothDevice getDevice(){
		return newDevice;
	}
}
