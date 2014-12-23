package com.reyun.reyunchannel;

import android.os.Bundle;

import com.reyun.sdk.ReYunChannel;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		ReYunChannel.initWithKeyAndChanelId(getApplicationContext(), "App43_1");
		ReYunChannel.setLoginSuccessBusiness("liwei11121");
//		ReYunChannel.setRegisterWithAccountID("liwei11121");
//		ReYunChannel.setPayment("liushuihao", PaymentType.APPLE,
//				CurrencyType.CNY, 1200);
//		ReYunChannel.setEvent("cesh1i");
//		ReYunChannel.exitSdk();
	}

}
