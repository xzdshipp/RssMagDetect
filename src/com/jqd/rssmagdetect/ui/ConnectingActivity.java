package com.jqd.rssmagdetect.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * @author jiangqideng@163.com
 * @date 2016-6-28 下午7:18:51
 * @description 连接wifi热点，没有具体功能的模块，仅供娱乐
 */
public class ConnectingActivity extends Activity {

	private WifiManager wifiManager;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connecting);

		Intent intent = this.getIntent();
		String wifiInfo = intent.getStringExtra("wifiInfo");
		textView = (TextView) findViewById(R.id.connecttedInfo);
		textView.setText(wifiInfo);
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

	}

	public void update(View view) {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		textView.setText(wifiInfo.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connecting, menu);
		return true;
	}

}
