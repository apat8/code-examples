package com.example.bluetoothdemo2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Device_status extends Activity {

	Button unPair, pair, connect;
	TextView status, deviceName;
	Connection globalVariables;
	BluetoothDevice selectedDevice;
	BluetoothAdapter btAdapter;
	Set<BluetoothDevice> bondedDevices;
	BroadcastReceiver receiver;
	ProgressBar pairingProgress;
	View divider;
	BluetoothSocket btSocket;
	Thread connectionThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);     
		setContentView(R.layout.activity_device_status);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		globalVariables = (Connection)getApplicationContext();
		unPair = (Button)findViewById(R.id.unpair);
		pair = (Button) findViewById(R.id.pair);
		connect = (Button) findViewById(R.id.connect);
		status = (TextView) findViewById(R.id.status);
		selectedDevice = globalVariables.getDevice();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
	    actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
		
		LayoutParams layout = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View actionBarStatus = inflater.inflate(R.layout.action_bar_device_status, null);
		actionBar.setCustomView(actionBarStatus,layout);
		
		deviceName=(TextView) actionBarStatus.findViewById(R.id.device);
		pairingProgress = (ProgressBar)actionBarStatus.findViewById(R.id.pairingProgressBar);
		divider = (View) actionBarStatus.findViewById(R.id.divider);
		
		deviceName.setText(selectedDevice.getName());
		pairingProgress.setVisibility(ProgressBar.INVISIBLE);
	
		
		bondedDevices = btAdapter.getBondedDevices();
		
		status.setText("Unpaired");
		
		unPair.setEnabled(false);
		connect.setEnabled(false);
		
		if(bondedDevices.size() > 0){
			for(BluetoothDevice paired: bondedDevices){
				if(selectedDevice.equals(paired)){
					status.setText("Paired");
					pair.setEnabled(false);
					unPair.setEnabled(true);
					connect.setEnabled(true);
					break;
				}
				
			}
		}
		
		
		
		pair.setOnClickListener(new OnClickListener(){

			@TargetApi(Build.VERSION_CODES.KITKAT)
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectedDevice.createBond();
				
			}
	
		});
		
		unPair.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Method m;
				try {
					m = selectedDevice.getClass().getMethod("removeBond", (Class[])null);
					m.invoke(selectedDevice, (Object[])null);
				} catch (Exception e){
					
				}						
			}
		});
		
		connect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pairingProgress.setVisibility(ProgressBar.VISIBLE);
				divider.setVisibility(View.INVISIBLE);
				status.setText("Connecting...");
				UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				// Get a BluetoothSocket for a connection with the given BluetoothDevice
				try {
					btSocket = selectedDevice.createRfcommSocketToServiceRecord(uuid);
				} catch (IOException e) {
					e.printStackTrace();
				}
								
				connectionThread  = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// Always cancel discovery because it will slow down a connection
						//btAdapter.cancelDiscovery();
						
						// Make a connection to the BluetoothSocket
						try {
							// This is a blocking call and will only return on a
							// successful connection or an exception
							btSocket.connect();
							
						} catch (IOException e) {
							//connection to device failed so close the socket
							try {
								btSocket.close();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
					}
				}); //  end of thread 
			
				connectionThread.start();
			
			}
			
		});
		
		connect.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				String action = arg1.getAction();
				
				if(action == BluetoothDevice.ACTION_BOND_STATE_CHANGED){
					if (selectedDevice.getBondState() == BluetoothDevice.BOND_BONDED){
						status.setText("Paired");
						pair.setEnabled(false);
						unPair.setEnabled(true);
						connect.setEnabled(true);
						pairingProgress.setVisibility(ProgressBar.INVISIBLE);
						divider.setVisibility(View.VISIBLE);
					}
					if (selectedDevice.getBondState() == BluetoothDevice.BOND_BONDING){
						status.setText("Pairing...");
						pairingProgress.setVisibility(ProgressBar.VISIBLE);
						divider.setVisibility(View.INVISIBLE);
					}
					if(selectedDevice.getBondState() == BluetoothDevice.BOND_NONE){
						status.setText("Unpaired");
						pairingProgress.setVisibility(ProgressBar.INVISIBLE);
						divider.setVisibility(View.VISIBLE);
						pair.setEnabled(true);
						unPair.setEnabled(false);
						connect.setEnabled(false);
					}
				} // end of bond state changed
				
				if(action == BluetoothDevice.ACTION_ACL_CONNECTED){
					pairingProgress.setVisibility(ProgressBar.INVISIBLE);
					divider.setVisibility(View.VISIBLE);
					globalVariables.setBluetoothSocket(btSocket);
					globalVariables.setControls("Buttons");
					Intent intent = new Intent(Device_status.this, Controls.class);
					startActivity(intent); // starts new page
					
				}
				
				if(action == BluetoothDevice.ACTION_ACL_DISCONNECTED){
					finish();
				}
				
				
			} // end of onReceive
			
		}; // end of broadcast receiver
		
		registerIntents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_status, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		try {
			if(btSocket != null)
			btSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
	}

	public void registerIntents(){
		IntentFilter bondingIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(receiver, bondingIntent);
		
		IntentFilter connectedIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(receiver, connectedIntent);
		
		IntentFilter disconnectedIntent = new IntentFilter (BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(receiver, disconnectedIntent);
		
	} // end of registerReveivers

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		this.finish();
		
	}
	
}
