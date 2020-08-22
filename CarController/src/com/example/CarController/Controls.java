package com.example.bluetoothdemo2;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.RecognizerIntent;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Controls extends Activity implements SensorEventListener {

	private static final int VOICE_RECOGNIZER = 0;
	BluetoothSocket socket;
	InputStream inStream;
	OutputStream outStream;
	TextView text;
	Connection btSocket;
	Button left, right, up, down;
	Handler h;
	ConnectedThread thread;
	Sensor sensor;
	SensorManager sm;
	Connection controlSelected;
	GLSurfaceView ourSurface;;
	ListView listView;
	DrawerLayout drawerLayout;
	ListView drawerList;
	
	String kk;
	int previousMessage, currentMessage;
	boolean first;
	
	Switch autoSwitch;
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		final int MESSAGE_READ = 1;
		btSocket = (Connection) getApplicationContext();
		socket = btSocket.getBluetoothSokcet();	
		first = true;
		
		kk = "";
		previousMessage = 0;
		currentMessage = 0;
		// handler to convert bytes[] to string
		h = new Handler(){
			public void handleMessage(Message msg){
								
				switch(msg.what){
					case MESSAGE_READ:
						byte[] readBuf = (byte[]) msg.obj;
						String message = new String(readBuf,0, msg.arg1);
						System.out.println(message);
						//setMessageReceived(message);
					/*							 
						if(message.contains("#") == false){
							//System.out.println(message);
							kk= kk + message;
							//System.out.println(kk);
						}
						else{
							//System.out.println(kk);
																										
							kk.toCharArray();
							if(kk.toCharArray().length <=3 && kk.toCharArray().length>0){
								//System.out.println(kk);
								int num = Integer.parseInt(kk);
								System.out.println(num);
								setMessageReceived(kk);
							}
							
							kk = "";
							
							//setMessageReceived(message);
						}
						*/
						break;
				}
			}
		}; // end of handler
				
		thread = new ConnectedThread(socket,h); // creates thread to read and write
		thread.start(); // starts the run method in the ConnectedThread class
		
		if(btSocket.Selected().equals("Buttons")){
			selectedButtons();
		}
		
		else if(btSocket.Selected().equals("Motion")){
			
			selectedMotion();
			
			
		}
		else if(btSocket.Selected().equals("Voice")){
			selectedVoice();
		}
	
				
	} // end of onCreate method

	public void onBackPressed(){
		thread.cancel();
		finish();
		return;
	} // end of onBackPressed;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controls, menu);
		autoSwitch = (Switch) menu.findItem(R.id.autoSwitch).getActionView().findViewById(R.id.AutoSwitch);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final CharSequence[] items = {"Buttons", "Motion", "Voice"};
		if (item.getItemId() == R.id.action_control){
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Control With:");
							
			builder.setSingleChoiceItems(items, btSocket.selectedControls(), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					btSocket.setFalse();
					if("Buttons".equals(items[which])){
						btSocket.setControls("Buttons");
					}
					
					if("Motion".equals(items[which])){
						btSocket.setControls("Motion");
					}
					
					if("Voice".equals(items[which])){
						btSocket.setControls("Voice");
					}
					dialog.cancel();
					Intent intent = getIntent();
					finish();
					startActivity(intent);
					
				}
			});
			
			builder.show();
			
		}
			
		
		
		
		return super.onOptionsItemSelected(item);
		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
				
		autoSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					thread.write("A".getBytes());
				}
				else{
					thread.write("M".getBytes());
				}
			}
			
		});

			return super.onPrepareOptionsMenu(menu);
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	
	} // end of onAccuracyChanged method

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		float x,y;
		
		x = arg0.values[0];
		y = arg0.values[1];
		//System.out.println(x + "     " + y);
		
		if(x > 5){ // backwards
			byte[] bytes = "B".getBytes(); 
			thread.write(bytes);
		}
		
		if(x < -5){ //  forward
			byte[] bytes = "F".getBytes(); 
			thread.write(bytes);
		}

		if(y < -5){ //  Left
			thread.write("L".getBytes());
		}
		if(y > 5){ // Right
			thread.write("R".getBytes());
		}
		if((x > -5) && (x < 5) && (y > -5) && (y < 5)){
			thread.write("S".getBytes());
		}
		
	} // end of onSensorChanged
	
	@SuppressLint("HandlerLeak")
	public void selectedButtons(){
		setContentView(R.layout.activity_controls);
		text = (TextView)findViewById(R.id.textView1);
		left = (Button) findViewById(R.id.leftButton);
		right = (Button) findViewById(R.id.rightButton);
		up = (Button) findViewById(R.id.upButton);
		down = (Button) findViewById(R.id.downButtom);
		
		
		//String[] stringArray = {"f","g","h"};
		//drawerList = (ListView) findViewById(R.id.list_slidermenu);
		//drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		//drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArray));
		
		
		
		
		
		
		
		
		// handler to convert bytes[] to string
	/*	h = new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
					case MESSAGE_READ:
						byte[] readBuf = (byte[]) msg.obj;
						String message = new String(readBuf,0,msg.arg1);
						System.out.print(message);
						text.setText(message);
						break;
				}
			}
		};*/ // end of handler
		
		//thread = new ConnectedThread(socket,h); // creates thread to read and write
		//thread.start(); // starts the run method in the ConnectedThread class
		
		left.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				String l = "L";
				//text.setText(l);
				byte[] lBytes = l.getBytes();
				thread.write(lBytes);

				if(arg1.getAction() == MotionEvent.ACTION_UP){
					thread.write("S".getBytes());
				}

				return false;
			}
			
		});

		right.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				String r = "R";
				//text.setText(r);
				byte[] rBytes = r.getBytes();
				thread.write(rBytes);	
				
				if(arg1.getAction() == MotionEvent.ACTION_UP){
					thread.write("S".getBytes());
				}
				return false;
			}	
		});

		down.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				String b = "B";
				//text.setText(b);
				byte[] bBytes = b.getBytes();
				thread.write(bBytes);	
				
				if(arg1.getAction() == MotionEvent.ACTION_UP){
					thread.write("S".getBytes());
				}
				return false;
			}	
		});
		
		
		up.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				String u = "F";
				byte[] uBytes = u.getBytes();
				if(arg1.getAction() == MotionEvent.ACTION_DOWN){
					thread.write(uBytes);	
				}
				
				if(arg1.getAction() == MotionEvent.ACTION_UP){
					thread.write("S".getBytes());
				}
				
				return false;
			}
			
		});
		
	} // end of selectedButtons method	
	
	public void selectedMotion(){
		setContentView(R.layout.accelerometer);
		text = (TextView) findViewById(R.id.acceleromterText);
		sm=(SensorManager)getSystemService(SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this,sensor, SensorManager.SENSOR_DELAY_NORMAL);
	} // end of selecetedMotion method
	
	public void selectedVoice(){
		setContentView(R.layout.voice);
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command");
		startActivityForResult(intent, VOICE_RECOGNIZER);
	} // end of selectedVoice method
	
	public void setMessageReceived(String message){
		text.setText("");
		text.setText(message);
	} // end of setMessageReveived method
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(sm != null){
			sm.unregisterListener(this);
		}
	} // end of onDestroy method

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	} // end of onPause method

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	} // end of onResume method

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if(requestCode == VOICE_RECOGNIZER && resultCode == RESULT_OK ){
			
			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); 
			for (int i = 0; i < results.size(); i++){
							
				if(results.get(i).equals("forward")){
					thread.write("F".getBytes());
				}
				
				else if(results.get(i).equals("back")){
					thread.write("B".getBytes());
				}
				
				else if(results.get(i).equals("left")){
					thread.write("L".getBytes());
				}
				
				else if(results.get(i).equals("right")){
					thread.write("R".getBytes());
				}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			thread.write("S".getBytes());
			} // end of for loop
			
		} // end of voice recognizer if statement
		
		super.onActivityResult(requestCode, resultCode, data);
	} // end of onActivityResult method

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		/*switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:{
				System.out.println("D" );
				break;
			}
			case MotionEvent.ACTION_UP:{
				System.out.println("d" );
				break;
			}
		
		}
		*/
		if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP){
			System.out.println("d" );
			
			
		}
		
		return super.onTouchEvent(event);
	}
	
	
	
	
	
}
