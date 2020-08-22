package com.example.bluetoothdemo2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.MotionEventCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView title;
	ListView list;
	BluetoothAdapter btAdapter;
	Set<BluetoothDevice> devicesArray; 			// to store bonded devices in
	//ArrayAdapter<String> listAdapter;
	//public ArrayList<String> previousDevicesList;
	//ArrayList<String> currentDevicesList;
	BroadcastReceiver receiver;
	boolean firstFoundDevice;
	boolean deviceToRemove;
	boolean discoveryCancelled;
	BluetoothDevice d;
	BluetoothSocket socket;
	boolean connecting;
	AlertDialog alertDialog;
	CustomAdapter listAdapter;
	int indexOfDevice;
	Set<BluetoothDevice> pairedDevices;
	AlertDialog.Builder builder2;
	
	ArrayList<ListRowItem> item;
	
	ArrayList<ListRowItem> previousDevicesList;
	ArrayList<ListRowItem> currentDevicesList;
	
	ProgressBar scanningBar;
	ImageButton buttonRefresh;
	boolean discoveryStarted;
	Switch btEnableSwitch;
	Switch onOff;
	TextView emptyListMessage; 
	boolean ifBtAdapterEnabled;
	boolean btAllowed;
	Drawable refreshImage;
	ProgressBar pairingBar;
	boolean bondingStarted;
	
	RelativeLayout layout;
	
	
	Connection	connectionSocket;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connectionSocket = (Connection)getApplicationContext();
		title = (TextView) findViewById(R.id.deviceTitle);
		list = (ListView) findViewById(R.id.list);
			
		layout = (RelativeLayout) findViewById(R.id.kk);
		
		//previousDevicesList = new ArrayList<String>();
		//currentDevicesList = new ArrayList<String>();
		//listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, previousDevicesList);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		firstFoundDevice = true;
		deviceToRemove = true;
		discoveryCancelled = false;
		discoveryStarted = false;
		
		bondingStarted = false;
		previousDevicesList = new ArrayList<ListRowItem>();
		currentDevicesList = new ArrayList<ListRowItem>();
		
		emptyListMessage = new TextView(getApplicationContext());
		
		
		scanningBar = (ProgressBar) findViewById(R.id.progressBar1);
		listAdapter = new CustomAdapter(this, previousDevicesList);
		builder2 = new AlertDialog.Builder(this);
		buttonRefresh = (ImageButton) findViewById(R.id.refreshBt);
		refreshImage = this.getResources().getDrawable(R.drawable.ic_action_refresh);
		
		btEnableSwitch = (Switch) findViewById(R.id.BluetoothSwitch);
	
		ActionBar action = getActionBar();
		action.setDisplayShowHomeEnabled(false);
		action.setDisplayShowTitleEnabled(false);
		
		emptyListMessage.setText("No Devices Detected");
		emptyListMessage.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		emptyListMessage.setTextColor(Color.GRAY);
		emptyListMessage.setTextSize(20);
		emptyListMessage.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		emptyListMessage.setVisibility(View.GONE);
		((ViewGroup)list.getParent()).addView(emptyListMessage);
		list.setEmptyView(emptyListMessage);
		
		//buttonRefresh.setEnabled(false);
		list.setAdapter(listAdapter);		
		
		btAllowed = false;
		
		//StateListDrawable selector = new StateListDrawable();
		//selector.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(android.R.color.holo_blue_dark));
		//list.setSelector(selector);
		
		receiver = new BroadcastReceiver(){
			
			@TargetApi(19)
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				String action = arg1.getAction();
				
				if(BluetoothDevice.ACTION_FOUND.equals(action)){
					
					BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String name = device.getName() + "\n" + device.getAddress();
					boolean newDevice = true;
					boolean pair = true;
					pairedDevices = btAdapter.getBondedDevices();
					
					if(pairedDevices.size() > 0){
						for(BluetoothDevice pairDevice : pairedDevices){
							String nameAdress = pairDevice.getName() + "\n" + pairDevice.getAddress();
							if(name.equals(nameAdress)){
								pair = false;
								break;
							}
						}
					}
					
					if(firstFoundDevice){
						if(pair){
							previousDevicesList.add(new ListRowItem(name,""));
							currentDevicesList.add(new ListRowItem(name,""));
							
						}
						
						else{
							previousDevicesList.add(new ListRowItem(name,"Paired"));
							currentDevicesList.add(new ListRowItem(name,"Paired"));
						}
						
						firstFoundDevice = false;
						previousDevicesList.get(0).setProgressVisibility(View.INVISIBLE);
						currentDevicesList.get(0).setProgressVisibility(View.INVISIBLE);
					}
					else{
						currentDevicesList.add(new ListRowItem(name,""));
						
						for(int i = 0; i < previousDevicesList.size(); i++){
							if(previousDevicesList.get(i).getName().equals(name)){
								newDevice = false;
								break;
							}
						}
						if(newDevice){
							if(pair){
								previousDevicesList.add(new ListRowItem(name,""));
							}
							
							else{
								previousDevicesList.add(new ListRowItem(name,"Paired"));
							}
							try{
								previousDevicesList.get(previousDevicesList.size()-1).setProgressVisibility(View.INVISIBLE);
							}
							catch(IndexOutOfBoundsException e){
								previousDevicesList.get(0).setProgressVisibility(View.INVISIBLE);
							}
							
						}	
					}
					
					listAdapter.notifyDataSetChanged();
				}
				
				else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
/*	add this*/		scanningBar.setVisibility(4);
					discoveryStarted = false;
					invalidateOptionsMenu();
					
					buttonRefresh.setEnabled(true);
					refreshImage.setAlpha(255);
					
					if(btAllowed){
						buttonRefresh.setEnabled(false);
						refreshImage.setAlpha(130);
						btAllowed = false;
					}
					
						
					if(currentDevicesList.size() == 0){
						if(bondingStarted){
							previousDevicesList.clear();
							bondingStarted = false;
						}
						
					}
					else{
						for(int x = 0; x < previousDevicesList.size(); x++){
							for(int y = 0; y < currentDevicesList.size(); y++){
								deviceToRemove = true;
								if(previousDevicesList.get(x).getName().equals(currentDevicesList.get(y).getName())){
									deviceToRemove = false;
									break;
								}
							}
							if(!deviceToRemove){
								continue;
							}
							else{
								previousDevicesList.remove(x);
								x=0;
							}
						}
					}
		
					currentDevicesList.clear();
					listAdapter.notifyDataSetChanged();
										
					if(discoveryCancelled == true){
						discoveryCancelled = false;
						
					}
					else{
						btAdapter.startDiscovery();
					}

				}
				
				else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
					BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String name = d.getName() + "\n" + d.getAddress();
					
					if(previousDevicesList.size() > 0){
						if(d.getBondState() == BluetoothDevice.BOND_BONDED){
							previousDevicesList.get(indexOfDevice).setPaired();
							previousDevicesList.get(indexOfDevice).setProgressVisibility(View.INVISIBLE);
						}
					
						if(d.getBondState() == BluetoothDevice.BOND_BONDING){
							previousDevicesList.get(indexOfDevice).setPairing();
							previousDevicesList.get(indexOfDevice).setProgressVisibility(View.VISIBLE);
						}
					
						if(d.getBondState() == BluetoothDevice.BOND_NONE){
							previousDevicesList.get(indexOfDevice).clear();
							previousDevicesList.get(indexOfDevice).setProgressVisibility(View.INVISIBLE);
						}
					}
						
					listAdapter.notifyDataSetChanged();
					
				}
				
				else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
					if(connecting){
						alertDialog.dismiss();
						Intent intent = new Intent(MainActivity.this, Controls.class);
						startActivity(intent); // starts new page
						
						connectionSocket.setBluetoothSocket(socket);
						connectionSocket.setControls("Buttons");
					}
				}
				
				else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
					discoveryStarted = true;
					invalidateOptionsMenu();
					scanningBar.setVisibility(1);
					refreshImage.setAlpha(130);
					buttonRefresh.setEnabled(false);
				}
				
			} // end of onReceive method
			
		}; // end of receiver
		
		enableBt();
	
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);
		
		IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter2);
		
		IntentFilter filter3 = new IntentFilter (BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(receiver, filter3);
		
		IntentFilter filter4 = new IntentFilter (BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(receiver, filter4);
		
		IntentFilter filter5 = new IntentFilter (BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(receiver, filter5);
		
		btAdapter.startDiscovery();
		
		buttonRefresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				discoveryCancelled = true;
				btAdapter.startDiscovery();
				
			}
			
		});
		
		
		
	list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@TargetApi(19)
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
							
				indexOfDevice = arg2;
				String name = ((ListRowItem)list.getItemAtPosition(arg2)).getName();
				String[] a = name.split("\n");
				d = btAdapter.getRemoteDevice(a[1]);
				discoveryCancelled = true;
				
				connectionSocket.setDevice(d);
				
				Intent intnt = new Intent(MainActivity.this, Device_status.class);
				startActivity(intnt); // starts new page
				/*
				
				//String devicePairing = ((TextView) arg1.findViewById(android.R.id.text1)).getText().toString();
				//TextView x = (TextView)arg1.findViewById(android.R.id.text1);
				//x.setTextColor(Color.GRAY);
				
				if(d.getBondState() == BluetoothDevice.BOND_NONE){
					bondingStarted = true;
					d.createBond(); // creadBond method automatically cancels discovery		
				}

				else if(d.getBondState() == BluetoothDevice.BOND_BONDED){
					connecting = true;
					//btAdapter.cancelDiscovery();
					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
					// Get a BluetoothSocket for a connection with the given BluetoothDevice
					try {
						socket = d.createRfcommSocketToServiceRecord(uuid);
					} catch (IOException e) {
						e.printStackTrace();
					}
									
					Thread connectionThread  = new Thread(new Runnable() {
						
						@Override
						public void run() {
							// Always cancel discovery because it will slow down a connection
							//btAdapter.cancelDiscovery();
							
							// Make a connection to the BluetoothSocket
							try {
								// This is a blocking call and will only return on a
								// successful connection or an exception
								socket.connect();
							} catch (IOException e) {
								//connection to device failed so close the socket
								connecting = false;
								try {
									socket.close();
								} catch (IOException e2) {
									e2.printStackTrace();
								}
							}
						}
					}); //  end of thread 
				
					connectionThread.start();
				
					connectingDialog();
					
				} // end of bonded if statement
*/
			} // end of onItemClick listener
			
			
		}); // end of onClickListener 
		
		
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				String name = ((ListRowItem) list.getItemAtPosition(arg2)).getName();
				String[] a = name.split("\n");
				d = btAdapter.getRemoteDevice(a[1]);
				
				
				builder2.setMessage("Do you want to unpair device?");
				
				builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						try {
							Method m = d.getClass().getMethod("removeBond", (Class[])null);
							previousDevicesList.get(indexOfDevice).clear();
							try {
								m.invoke(d, (Object[])null);
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						dialog.dismiss();
					}; // end of onClick method
				}); // end of positiveButton method
				
				builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				AlertDialog dialog = builder2.create();
				dialog.show();
				return true;
			}// end of onItemClick
			
		}); // end of long click method
		
		
		
		
		
	} // end of onCreate method

	// checks for bluetooth and enables it
	public void enableBt(){
		if(btAdapter == null){
			String toastText = "No Bluetooth Detected.";
			Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
		}
		else{
			if(!btAdapter.isEnabled()){
				Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enable, 0);
			}
			else{
				ifBtAdapterEnabled = true;
			}
		}
	}// end of enableBt method
	
	public void connectingDialog(){
		if (connecting){
			TextView msg = new TextView(this);
			msg.setText("\nConnecting...\n");
			msg.setTextSize(25);
			msg.setTextColor(Color.parseColor("#0EBFE9"));
			msg.setGravity(Gravity.CENTER);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(msg);
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					connecting = false;
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					dialog.dismiss();
				}
			}); // end of setNegativeButton
			alertDialog = builder.create();
			alertDialog.show();
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// for enabling bluetooth
		if(requestCode == 0){
			if(resultCode == RESULT_CANCELED){
				emptyListMessage.setText("Bluetooth is turned off");
				refreshImage.setAlpha(130);
				buttonRefresh.setEnabled(false);
				scanningBar.setVisibility(4);
				
			}
			if(resultCode == RESULT_OK){
				onOff.setChecked(true);
				if (btAdapter.isDiscovering()){
					btAdapter.cancelDiscovery();
				}
				
				btAdapter.startDiscovery();
			}
		}
	}// end of onActivityResult method
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		
	}// end of onDestroy
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		onOff = (Switch) menu.findItem(R.id.BluetoothSwitch).getActionView().findViewById(R.id.btSwitch);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
			
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		if(ifBtAdapterEnabled){
			onOff.setChecked(true);
			ifBtAdapterEnabled = false;
		}
		
		
		onOff.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					emptyListMessage.setText("No Devices Detected");
					btAdapter.enable();
					discoveryCancelled = true;
					buttonRefresh.setEnabled(true);
					refreshImage.setAlpha(255);
					//buttonRefresh.setAlpha(225);
				}
				else{
					emptyListMessage.setText("Bluetooth is turned off");
					discoveryCancelled = true;
					if(btAdapter.isDiscovering()){
						btAllowed = true;
						btAdapter.cancelDiscovery();
					}
					if(previousDevicesList.size() > 0){
						previousDevicesList.clear();
					}
					btAdapter.disable();
					refreshImage.setAlpha(130);
					buttonRefresh.setEnabled(false);
				}
				
				list.setEmptyView(emptyListMessage);
			}
			
		});
		
	
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		//unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			System.out.println(event.getActionMasked());
			
		}
		else if (event.getAction() == MotionEvent.ACTION_UP){
			System.out.println("FF");
		}
		return false;
	}


	
	

}





