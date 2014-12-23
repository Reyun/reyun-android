package com.reyun.reyunchannel;

import android.app.Activity;
import android.os.Bundle;

import com.reyun.sdk.ReYunChannel;

public class BaseActivity extends Activity {

	boolean isActive = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReYunChannel.setContext(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		ReYunChannel.setContext(getApplicationContext());
		if (!isActive) {

			// ReYun.postSessionData();
			ReYunChannel.startHeartBeat(getApplicationContext());
			isActive = true;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		ReYunChannel.stopHeartBeat();
		if (ReYunChannel.isAppOnForeground() == false) {
			isActive = false;
		}
	}

}
