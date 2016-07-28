package com.jqd.rssmagdetect.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.jqd.rssmagdetect.file.FileManager;
import com.jqd.rssmagdetect.ui.MainActivity;
import com.jqd.rssmagdetect.ui.WiFiListAdapter;
import com.jqd.rssmagdetect.util.GlobalPara;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-28 下午7:17:43
 * @description 管理wifi，循环扫描和记录wifi模块获取的数据
 */
public class WiFiDataManager {
	private WifiManager wifiManager;
	public List<ScanResult> scanResults = null;
	private volatile static WiFiDataManager wiFiDataManager = null;
	public MainActivity activity;

	public ArrayList<HashMap<Integer, Integer>> dataRssi = new ArrayList<HashMap<Integer, Integer>>(); // 每行代表一个Wifi热点，对应一个map，map的第一个值是数据的index，第二个值是rssi
	public HashMap<String, Integer> dataBssid = new HashMap<String, Integer>();
	public ArrayList<String> dataWifiNames = new ArrayList<String>();
	public int dataCount = 0;

	public static WiFiDataManager getInstance() {
		if (wiFiDataManager == null) {
			synchronized (WiFiDataManager.class) {
				if (wiFiDataManager == null) {
					wiFiDataManager = new WiFiDataManager();
				}
			}
		}
		return wiFiDataManager;
	}

	public void init(MainActivity activity) {
		this.activity = activity;
		if (wifiManager == null) {
			wifiManager = (WifiManager) activity
					.getSystemService(Context.WIFI_SERVICE);
		}
		if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			Toast.makeText(activity, "正在开启wifi，请稍后...", Toast.LENGTH_SHORT)
					.show();
			if (wifiManager == null) {
				wifiManager = (WifiManager) activity
						.getSystemService(Context.WIFI_SERVICE);
			}
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
		}
	}

	public void startCollecting(MainActivity activity) {
		wifiManager.startScan();
		GlobalPara.getInstance().timeOfStartScan = GlobalPara.getInstance().timeSinceStart;
		activity.registerReceiver(cycleWifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	private final BroadcastReceiver cycleWifiReceiver = new BroadcastReceiver() {
		@SuppressLint("UseSparseArrays")
		@Override
		public void onReceive(Context context, Intent intent) {
			scanResults = wifiManager.getScanResults();
			if (scanResults != null) {
				WiFiListAdapter adapter = new WiFiListAdapter(activity,
						scanResults);
				activity.listView.setAdapter(adapter);
			}
			// 更新热点列表，只增不减，顺序不变，同时将RSSI记录下来
			for (int i = 0; i < scanResults.size(); i++) {
				if (!dataBssid.containsKey(scanResults.get(i).BSSID)) { // 新增一个wifi热点
					dataBssid.put(scanResults.get(i).BSSID, dataBssid.size());
					dataWifiNames.add(scanResults.get(i).SSID);
					HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
					tmp.put(dataCount, scanResults.get(i).level);
					dataRssi.add(tmp);
				} else { // wifi热点已存在
					dataRssi.get(dataBssid.get(scanResults.get(i).BSSID)).put(
							dataCount, scanResults.get(i).level);
				}
			}
			dataCount++;

			while (GlobalPara.getInstance().timeSinceStart
					- GlobalPara.getInstance().timeOfStartScan < 50) {
				// 等待，可以用来控制时间，，1 * 10ms, 正常的手机wifi扫描一次大约得一秒了
			}
			GlobalPara.getInstance().timeOfStartScan = GlobalPara.getInstance().timeSinceStart;

			// 收到后开始下一次扫描，控制一下时间，每秒最多两次
			wifiManager.startScan();
			activity.toggleButton.setText("关闭RSS数据采集" + "("
					+ String.valueOf(dataCount) + ")");
		}
	};

	public void endCollecting(MainActivity activity) {
		activity.unregisterReceiver(cycleWifiReceiver); // 取消监听
		SensorsDataManager.getInstance().updateSensorsData(); // 保持传感器和wifi数据的个数同步
		// 然后存储数据到文件
		new FileManager().saveData();

	}

}
