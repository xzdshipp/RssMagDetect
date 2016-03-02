/*2015.5.8 修改
 * 点击下一位置的时候，初始化dataRSSI
 * 
 * 
 * 
 */
/*2015.5.26 修改
 * 去掉wifi，只采集其他各种传感器数据
 */

package com.example.my_wifi;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private Button startButton = null;
	private Button stopButton = null;
	private Button checkButton = null;
	private Button scanButton = null;
	private Button connecttedButton = null;
	private Button previousPositionButton;
	private Button nextPositionButton;
	private TextView positionTextView;
	private TextView dataTextView;
	private ToggleButton toggleButton;
	private TextView textView = null;
	private ListView listView = null;
	private WifiManager wifiManager = null;
	private List<ScanResult> scanResults = null;
	private WifiInfo wifiInfo;
	private boolean startCollect=false;
	private Thread timeThread;
	private boolean cancelThread=false;
	private long timeSinceStart=0;
	private int dataNum=0;
	private long timeOfStartScan=0;
	private int position_index=1;
	
	private SensorManager sensorManager;
	private SensorEventListener mSensorListener;
	private SensorEventListener oSensorListener;
	private SensorEventListener aSensorListener;
	private SensorEventListener gSensorListener;
	private SensorEventListener graSensorListener;
	
	private Sensor msensor;
	private Sensor osensor;
	private Sensor asensor;
	private Sensor gsensor;
	private Sensor grasensor;
	
	private float[] temp_m = new float[3];
	private float[] temp_o = new float[3];
	private float[] temp_a = new float[3];
	private float[] temp_g = new float[3];
	private float[] temp_gra = new float[3];
	private int[][] dataRssi = new int[3][10000];
	private int[][] dataMagnetic = new int[3][10000];
	private int[][] dataOrientation = new int[3][10000];
	private int[][] dataAccelerate = new int[3][10000];
	private int[][] dataGyroscope = new int[3][10000];
	private int[][] dataGravity = new int[3][10000];
	
	private String tempString = null;
	private String[] dataBssid = new String[] {	//长度60，最多存储60个热点的RSS
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
	};
	private String[] dataWifiNameStrings = new String[] {	//长度60，最多存储60个热点的RSS
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
	};
	
//	private ArrayList<String> wifiNames = null;
//	private ArrayList<WifiConfiguration> wifiDevices = null; 
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		startButton = (Button) findViewById(R.id.startButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		checkButton = (Button) findViewById(R.id.checkButton);
		scanButton = (Button) findViewById(R.id.scanButton);
		toggleButton =(ToggleButton) findViewById(R.id.toggleButton1);
		connecttedButton = (Button) findViewById(R.id.connecttedButton);
		nextPositionButton = (Button) findViewById(R.id.nextPositionButton);
		previousPositionButton = (Button) findViewById(R.id.previousPositionButton);
		
		textView = (TextView) findViewById(R.id.textView1);
		positionTextView = (TextView) findViewById(R.id.positionTextView);
		dataTextView = (TextView) findViewById(R.id.dataTextView);
		listView = (ListView) findViewById(R.id.listView1);
		startButton.setOnClickListener(startWiFiListener);
		stopButton.setOnClickListener(stopWiFiListener);
		checkButton.setOnClickListener(checkWiFiListener);
		scanButton.setOnClickListener(scanWiFilClickListener);
		connecttedButton.setOnClickListener(connecttedClickListener);
		nextPositionButton.setOnClickListener(changePositionClickListener);
		previousPositionButton.setOnClickListener(changePositionClickListener);
		
		//下面顺便采集些磁场和方向的数据
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		osensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		grasensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		
		mSensorListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				temp_m[0]=event.values[0];
				temp_m[1]=event.values[1];
				temp_m[2]=event.values[2];
				if (timeSinceStart-timeOfStartScan>=2) {//10ms * n
					timeOfStartScan = timeSinceStart;
					dataMagnetic[0][dataNum]=(int)Math.floor(temp_m[0]*100);dataMagnetic[1][dataNum]=(int)Math.floor(temp_m[1]*100);dataMagnetic[2][dataNum]=(int)Math.floor(temp_m[2]*100);
					dataOrientation[0][dataNum]=(int)Math.floor(temp_o[0]*100);dataOrientation[1][dataNum]=(int)Math.floor(temp_o[1]*100);dataOrientation[2][dataNum]=(int)Math.floor(temp_o[2]*100);
					dataAccelerate[0][dataNum]=(int)Math.floor(temp_a[0]*100);dataAccelerate[1][dataNum]=(int)Math.floor(temp_a[1]*100);dataAccelerate[2][dataNum]=(int)Math.floor(temp_a[2]*100);
					dataGyroscope[0][dataNum]=(int)Math.floor(temp_g[0]*100);dataGyroscope[1][dataNum]=(int)Math.floor(temp_g[1]*100);dataGyroscope[2][dataNum]=(int)Math.floor(temp_g[2]*100);
					dataGravity[0][dataNum]=(int)Math.floor(temp_gra[0]*100);dataGravity[1][dataNum]=(int)Math.floor(temp_gra[1]*100);dataGravity[2][dataNum]=(int)Math.floor(temp_gra[2]*100);
					dataNum++;
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		oSensorListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				temp_o[0]=event.values[0];
				temp_o[1]=event.values[1];
				temp_o[2]=event.values[2];
				if (timeSinceStart-timeOfStartScan>=2) {//10ms * n
					timeOfStartScan = timeSinceStart;
					dataMagnetic[0][dataNum]=(int)Math.floor(temp_m[0]*100);dataMagnetic[1][dataNum]=(int)Math.floor(temp_m[1]*100);dataMagnetic[2][dataNum]=(int)Math.floor(temp_m[2]*100);
					dataOrientation[0][dataNum]=(int)Math.floor(temp_o[0]*100);dataOrientation[1][dataNum]=(int)Math.floor(temp_o[1]*100);dataOrientation[2][dataNum]=(int)Math.floor(temp_o[2]*100);
					dataAccelerate[0][dataNum]=(int)Math.floor(temp_a[0]*100);dataAccelerate[1][dataNum]=(int)Math.floor(temp_a[1]*100);dataAccelerate[2][dataNum]=(int)Math.floor(temp_a[2]*100);
					dataGyroscope[0][dataNum]=(int)Math.floor(temp_g[0]*100);dataGyroscope[1][dataNum]=(int)Math.floor(temp_g[1]*100);dataGyroscope[2][dataNum]=(int)Math.floor(temp_g[2]*100);
					dataGravity[0][dataNum]=(int)Math.floor(temp_gra[0]*100);dataGravity[1][dataNum]=(int)Math.floor(temp_gra[1]*100);dataGravity[2][dataNum]=(int)Math.floor(temp_gra[2]*100);
					dataNum++;
				}
//				dataTextView.setText(String.valueOf(temp_o[0])+" "+String.valueOf(temp_o[1])+" "+String.valueOf(temp_o[2]));
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		aSensorListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				temp_a[0]=event.values[0];
				temp_a[1]=event.values[1];
				temp_a[2]=event.values[2];
				if (timeSinceStart-timeOfStartScan>=2) {//10ms * n
					timeOfStartScan = timeSinceStart;
					dataMagnetic[0][dataNum]=(int)Math.floor(temp_m[0]*100);dataMagnetic[1][dataNum]=(int)Math.floor(temp_m[1]*100);dataMagnetic[2][dataNum]=(int)Math.floor(temp_m[2]*100);
					dataOrientation[0][dataNum]=(int)Math.floor(temp_o[0]*100);dataOrientation[1][dataNum]=(int)Math.floor(temp_o[1]*100);dataOrientation[2][dataNum]=(int)Math.floor(temp_o[2]*100);
					dataAccelerate[0][dataNum]=(int)Math.floor(temp_a[0]*100);dataAccelerate[1][dataNum]=(int)Math.floor(temp_a[1]*100);dataAccelerate[2][dataNum]=(int)Math.floor(temp_a[2]*100);
					dataGyroscope[0][dataNum]=(int)Math.floor(temp_g[0]*100);dataGyroscope[1][dataNum]=(int)Math.floor(temp_g[1]*100);dataGyroscope[2][dataNum]=(int)Math.floor(temp_g[2]*100);
					dataGravity[0][dataNum]=(int)Math.floor(temp_gra[0]*100);dataGravity[1][dataNum]=(int)Math.floor(temp_gra[1]*100);dataGravity[2][dataNum]=(int)Math.floor(temp_gra[2]*100);
					dataNum++;
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		gSensorListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				temp_g[0]=event.values[0];
				temp_g[1]=event.values[1];
				temp_g[2]=event.values[2];
				if (timeSinceStart-timeOfStartScan>=2) {//10ms * n
					timeOfStartScan = timeSinceStart;
					dataMagnetic[0][dataNum]=(int)Math.floor(temp_m[0]*100);dataMagnetic[1][dataNum]=(int)Math.floor(temp_m[1]*100);dataMagnetic[2][dataNum]=(int)Math.floor(temp_m[2]*100);
					dataOrientation[0][dataNum]=(int)Math.floor(temp_o[0]*100);dataOrientation[1][dataNum]=(int)Math.floor(temp_o[1]*100);dataOrientation[2][dataNum]=(int)Math.floor(temp_o[2]*100);
					dataAccelerate[0][dataNum]=(int)Math.floor(temp_a[0]*100);dataAccelerate[1][dataNum]=(int)Math.floor(temp_a[1]*100);dataAccelerate[2][dataNum]=(int)Math.floor(temp_a[2]*100);
					dataGyroscope[0][dataNum]=(int)Math.floor(temp_g[0]*100);dataGyroscope[1][dataNum]=(int)Math.floor(temp_g[1]*100);dataGyroscope[2][dataNum]=(int)Math.floor(temp_g[2]*100);
					dataGravity[0][dataNum]=(int)Math.floor(temp_gra[0]*100);dataGravity[1][dataNum]=(int)Math.floor(temp_gra[1]*100);dataGravity[2][dataNum]=(int)Math.floor(temp_gra[2]*100);
					dataNum++;
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		graSensorListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				temp_gra[0]=event.values[0];
				temp_gra[1]=event.values[1];
				temp_gra[2]=event.values[2];
				if (timeSinceStart-timeOfStartScan>=2) {//10ms * n
					timeOfStartScan = timeSinceStart;
					dataMagnetic[0][dataNum]=(int)Math.floor(temp_m[0]*100);dataMagnetic[1][dataNum]=(int)Math.floor(temp_m[1]*100);dataMagnetic[2][dataNum]=(int)Math.floor(temp_m[2]*100);
					dataOrientation[0][dataNum]=(int)Math.floor(temp_o[0]*100);dataOrientation[1][dataNum]=(int)Math.floor(temp_o[1]*100);dataOrientation[2][dataNum]=(int)Math.floor(temp_o[2]*100);
					dataAccelerate[0][dataNum]=(int)Math.floor(temp_a[0]*100);dataAccelerate[1][dataNum]=(int)Math.floor(temp_a[1]*100);dataAccelerate[2][dataNum]=(int)Math.floor(temp_a[2]*100);
					dataGyroscope[0][dataNum]=(int)Math.floor(temp_g[0]*100);dataGyroscope[1][dataNum]=(int)Math.floor(temp_g[1]*100);dataGyroscope[2][dataNum]=(int)Math.floor(temp_g[2]*100);
					dataGravity[0][dataNum]=(int)Math.floor(temp_gra[0]*100);dataGravity[1][dataNum]=(int)Math.floor(temp_gra[1]*100);dataGravity[2][dataNum]=(int)Math.floor(temp_gra[2]*100);
					dataNum++;
				}
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		
		sensorManager.registerListener(mSensorListener, msensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(oSensorListener, osensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(aSensorListener, asensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(gSensorListener, gsensor, SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(graSensorListener, grasensor, SensorManager.SENSOR_DELAY_FASTEST);
		
		toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				startCollect=isChecked;
				//2015.5.26 删掉wifi相关的代码
				/*
				if (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
					Toast.makeText(MainActivity.this, "正在开启wifi，请稍后...", Toast.LENGTH_SHORT).show();
					if (wifiManager==null) {
						wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
					}
					if (!wifiManager.isWifiEnabled()) {
						wifiManager.setWifiEnabled(true);
					}
					
					while (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
						textView.setText("状态："+wifiManager.getWifiState());
					}
					textView.setText("状态："+wifiManager.getWifiState());
				}
				
				if (wifiManager==null) {
					wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
				}
				*/
				
				if (startCollect) {
					/*
					wifiManager.startScan();
					timeOfStartScan=timeSinceStart;
					registerReceiver(cycleWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
					*/
				}else {
					//unregisterReceiver(cycleWifiReceiver);
					//然后存储数据到文件
					//log只列出前20个热点信息
					//Log.i("BSSID列表", dataBssid[0]+" "+dataBssid[1]+" "+dataBssid[2]+" "+dataBssid[3]+" "+dataBssid[4]+" "+dataBssid[5]+" "+dataBssid[6]+" "+dataBssid[7]+" "+dataBssid[8]+" "+dataBssid[9]+" "+dataBssid[10]+" "+dataBssid[11]+" "+dataBssid[12]+" "+dataBssid[13]+" "+dataBssid[14]+" "+dataBssid[15]+" "+dataBssid[16]+" "+dataBssid[17]+" "+dataBssid[18]+" "+dataBssid[19]);
					try {
						FileOutputStream outStream=MainActivity.this.openFileOutput("dataRssi_at_"+ position_index+".txt",Context.MODE_WORLD_WRITEABLE+Context.MODE_WORLD_READABLE);
			            OutputStream fos = outStream;
			            DataOutputStream dos = new DataOutputStream(fos); 
			            for (int i = 0; i < dataNum; i++) {
			            	/*
							for (int j = 0; j < dataBssid.length; j++) {
								dos.writeInt(dataRssi[j][i]);
							}
							*/
							//加一点数据：三个磁场和三个方向的数据:多了12个int
							dos.writeInt(dataMagnetic[0][i]);dos.writeInt(dataMagnetic[1][i]);dos.writeInt(dataMagnetic[2][i]);
							dos.writeInt(dataOrientation[0][i]);dos.writeInt(dataOrientation[1][i]);dos.writeInt(dataOrientation[2][i]);
							dos.writeInt(dataAccelerate[0][i]);dos.writeInt(dataAccelerate[1][i]);dos.writeInt(dataAccelerate[2][i]);
							dos.writeInt(dataGyroscope[0][i]);dos.writeInt(dataGyroscope[1][i]);dos.writeInt(dataGyroscope[2][i]);
							dos.writeInt(dataGravity[0][i]);dos.writeInt(dataGravity[1][i]);dos.writeInt(dataGravity[2][i]);
						}
			            dos.close();
					}  catch (FileNotFoundException e) {
			            return;
			        }
			        catch (IOException e){
			            return ;
			        }
					
					/*
					try {
						FileOutputStream outStream=MainActivity.this.openFileOutput("dataBssid.txt",Context.MODE_WORLD_WRITEABLE+Context.MODE_WORLD_READABLE);
			            OutputStream fos = outStream;
			            DataOutputStream dos = new DataOutputStream(fos); 
						for (int j = 0; j < dataBssid.length; j++) {
							tempString = j+1+"  BSSID: ";
							dos.write(tempString.getBytes());
							dos.write(dataBssid[j].getBytes());
							dos.write("  SSID:".getBytes());
							dos.write(dataWifiNameStrings[j].getBytes());
							dos.write("\n".getBytes());
						}
			            dos.close();
					}  catch (FileNotFoundException e) {
			            return;
			        }
			        catch (IOException e){
			            return ;
			        }
					*/
					
				}	
				
				
				
				
				
			}
		});
		wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
		textView.setText("状态："+wifiManager.getWifiState());
		
		listView.setOnItemClickListener(wifiClickListener);

		timeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!cancelThread) {
						Thread.sleep(10);//睡10ms
						timeSinceStart++;
//						Log.i("timeofTheday", String.valueOf(timeOfTheDay));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		timeThread.start();
	}
	
	private OnClickListener startWiFiListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (wifiManager==null) {
				wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
			}
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
			textView.setText("状态："+wifiManager.getWifiState());
		}
	};
	private OnClickListener stopWiFiListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (wifiManager==null) {
				wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
			}
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}
			textView.setText("状态："+wifiManager.getWifiState());
		}
	};
	private OnClickListener checkWiFiListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (wifiManager==null) {
				wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
			}
			textView.setText("状态："+wifiManager.getWifiState());
			
		}
	};
	
	private OnClickListener scanWiFilClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			//2015.2.26----注释掉了下边这段，不使用wifi
			/*
			if (startCollect) {
				Toast.makeText(MainActivity.this, "正在循环扫描！", Toast.LENGTH_SHORT).show();
			}else {

				if (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
					Toast.makeText(MainActivity.this, "WiFi未打开，请稍候再试！", Toast.LENGTH_SHORT).show();
				} else {
					if (wifiManager==null) {
						wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
					}
					wifiManager.startScan();
					registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				}
			}
			*/
		}
	};
	
	private OnClickListener connecttedClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			wifiInfo = wifiManager.getConnectionInfo();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ConnectingActivity.class);
			intent.putExtra("wifiInfo", wifiInfo.toString());
			MainActivity.this.startActivity(intent);
		}
	};
	
	private OnClickListener changePositionClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (toggleButton.isChecked()){
				Toast.makeText(MainActivity.this, "请先关闭数据采集再改变位置，ok？", 0).show();
			}else {
				dataNum = 0;//改变位置了，这个置零
				for (int i = 0; i < dataRssi.length; i++) {
					for (int j = 0; j < dataRssi[i].length; j++) {
						dataRssi[i][j] = 0;
					}
				}
				
				if (v.getId()==nextPositionButton.getId()) {
					position_index++;
					positionTextView.setText("当前位置："+ position_index);
				}else if (v.getId()==previousPositionButton.getId()) {
					position_index--;
					positionTextView.setText("当前位置："+ position_index);
				}
			}
		}
	};
	
	//两次返回退出
	private long exitTime = 0;
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
	if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){ 
	if((System.currentTimeMillis()-exitTime) > 2000){ 
	Toast.makeText(getApplicationContext(), "再次点击“返回”退出", Toast.LENGTH_SHORT).show(); 
	exitTime = System.currentTimeMillis(); 
	} else { 
	finish(); 
	System.exit(0); 
	} 
	return true; 
	} 
	return super.onKeyDown(keyCode, event); 
	} 
	
	
	
	private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
//			List<WifiConfiguration> wcList = wifiManager.getConfiguredNetworks();  
			
//	        WifiConfiguration wc = null;  
//	        wifiNames.clear();  
//	        wifiDevices.clear();  
//	        Toast.makeText(MainActivity.this, "len= "+ wcList.size(), Toast.LENGTH_SHORT).show();
//	        for (int i = 0, len = wcList.size(); i < len; i++) {  
//	        	
//	            wc = wcList.get(i);  
//	            if (!wifiNames.contains(wc.SSID)) {  
//	                wifiNames.add(wc.SSID);  
//	                wifiDevices.add(wc);  
//	            }  
//	        }  
			scanResults=wifiManager.getScanResults();
//			Toast.makeText(MainActivity.this, "共扫描到 "+scanResults.size()+"个wifi热点", Toast.LENGTH_SHORT).show();
			if (scanResults!=null) {
				MyAdapter adapter = new MyAdapter(MainActivity.this, scanResults);	
				listView.setAdapter(adapter);
			}
			
		}
	};
	
	private OnItemClickListener wifiClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			ScanResult scanResult = scanResults.get(arg2);
			
			AlertDialog.Builder wifiBuilder = new AlertDialog.Builder(MainActivity.this);
			wifiBuilder.setTitle(scanResult.SSID);
			ViewGroup connectWiFiGroup = 
					(ViewGroup) MainActivity.this.getLayoutInflater().inflate(R.layout.connect_wifi,null);
			TextView msgTextView = (TextView) connectWiFiGroup.getChildAt(0);
			EditText pswEditText = (EditText) connectWiFiGroup.getChildAt(2);
			
			msgTextView.setText(
					"  SSID:\n  "+ scanResult.SSID+"\n\n"+
					"  BSSID:\n  "+scanResult.BSSID+"\n\n"+
					"  frequency:\n  "+scanResult.frequency+" MHz\n\n"+
					"  capabilities:\n  "+scanResult.capabilities+"\n\n"+
					"  level:\n  "+scanResult.level+" dBm\n\n"+
					"  describeContents:\n  "+scanResult.describeContents()+"\n\n"
					);
			
			wifiBuilder.setView(connectWiFiGroup);
			
			wifiBuilder.setPositiveButton("不知道密码哦", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast toast = Toast.makeText(MainActivity.this, "呵呵！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});
			wifiBuilder.setNegativeButton("连接", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast toast = Toast.makeText(MainActivity.this, "请自行前往系统页面连接，谢谢！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});
		
			wifiBuilder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(MainActivity.this, "-.-", Toast.LENGTH_SHORT).show();
				}
			});

		AlertDialog alertDialog = wifiBuilder.create();
		alertDialog.show();
		}
	};

	private final BroadcastReceiver cycleWifiReceiver = new BroadcastReceiver() {
	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			scanResults=wifiManager.getScanResults();
			
//			Toast.makeText(MainActivity.this, "共扫描到 "+scanResults.size()+"个wifi热点", Toast.LENGTH_SHORT).show();
			if (scanResults!=null) {
				MyAdapter adapter = new MyAdapter(MainActivity.this, scanResults);	
				listView.setAdapter(adapter);
			}
			//更新热点列表，只增不减，顺序不变，同时将RSSI记录
			for (int i = 0; i < scanResults.size(); i++) {
				for (int j = 0; j < dataBssid.length; j++) {
					if (dataBssid[j].equals(scanResults.get(i).BSSID)) {
						dataRssi[j][dataNum]=scanResults.get(i).level;
						
						break;
					}
					if (dataBssid[j]=="") {
						dataBssid[j]=scanResults.get(i).BSSID;
						dataWifiNameStrings[j]=scanResults.get(i).SSID;
						dataRssi[j][dataNum]=scanResults.get(i).level;
						break;
					}
				}
				
			}
//			Log.i("BSSID列表", dataBssid[0]+" "+dataBssid[1]+" "+dataBssid[2]+" "+dataBssid[3]+" "+dataBssid[4]+" "+dataBssid[5]+" "+dataBssid[6]+" "+dataBssid[7]+" "+dataBssid[8]+" "+dataBssid[9]+" ");
			Log.i("RSSI列表", dataRssi[0][dataNum]+" "+dataRssi[1][dataNum]+" "+dataRssi[2][dataNum]+" "+dataRssi[3][dataNum]+" "+dataRssi[4][dataNum]+" "+dataRssi[5][dataNum]+" "+dataRssi[6][dataNum]+" "+dataRssi[7][dataNum]+" "+dataRssi[8][dataNum]+" "+dataRssi[9][dataNum]+" "+dataRssi[10][dataNum]+" "+dataRssi[11][dataNum]+" "+dataRssi[12][dataNum]+" "+dataRssi[13][dataNum]+" "+dataRssi[14][dataNum]+" "+dataRssi[15][dataNum]+" "+dataRssi[16][dataNum]+" "+dataRssi[17][dataNum]+" "+dataRssi[18][dataNum]+" "+dataRssi[19][dataNum]);
			
			dataMagnetic[0][dataNum]=(int)Math.floor(temp_m[0]*100);dataMagnetic[1][dataNum]=(int)Math.floor(temp_m[1]*100);dataMagnetic[2][dataNum]=(int)Math.floor(temp_m[2]*100);
			dataOrientation[0][dataNum]=(int)Math.floor(temp_o[0]*100);dataOrientation[1][dataNum]=(int)Math.floor(temp_o[1]*100);dataOrientation[2][dataNum]=(int)Math.floor(temp_o[2]*100);
			dataTextView.setText(String.valueOf(temp_o[0])+" "+String.valueOf(temp_o[1])+" "+String.valueOf(temp_o[2]));
			dataNum++;
			

			while (timeSinceStart-timeOfStartScan<100) {
				//等待
			}
			if (timeSinceStart-timeOfStartScan>=100) {//收到后开始下一次扫描，控制一下时间，每秒一次
				wifiManager.startScan();
				timeOfStartScan=timeSinceStart;
				toggleButton.setText("关闭RSS数据采集"+"("+String.valueOf(dataNum)+")");
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
//	class AsyncMain extends AsyncTask<Void, Void, Void>{
//
//		protected Void doInBackground(Void... params) {
//			// TODO Auto-generated method stub
//			while (startCollect) {
//				publishProgress();
//			}
//			return null;
//
//		}
//
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//		}
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//		}
//
//
//		@Override
//		protected void onProgressUpdate(Void... values) {
//			// TODO Auto-generated method stub
//			
//			
//			super.onProgressUpdate(values);
//		}
//
//	}
	protected void onStop() {
		// TODO Auto-generated method stub
		sensorManager.unregisterListener(mSensorListener);
		sensorManager.unregisterListener(oSensorListener);
		cancelThread=true;
		super.onStop();
	}
	
	
}
	

