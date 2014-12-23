package com.reyun.Application;

import com.reyun.utils.AppUtil;
import com.reyun.utils.Mysp;
import com.reyun.utils.ReYunConst;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class MyApp extends Application {
	private String appk = "";
	private boolean ishasapk = false;

	@Override
	public void onCreate() {
		super.onCreate();
		ReYunConst.DebugMode = true;
		try {
			appk = AppUtil
					.GetMetaData(getApplicationContext(), "com.reyun.KEY");
			if (appk != null && !"".equals(appk)) {
				if (ReYunConst.DebugMode) {
					Log.e(ReYunConst.TAG, "=====appk ====" + appk);
				}
				ishasapk = AppUtil.checkAppid(getApplicationContext());
				if (ishasapk) {
					// 保存 appk 到appidXML 文件中
					Mysp.AddString(getApplicationContext(), "appidXML", "appid",
							appk);

				} else {
					if (ReYunConst.DebugMode) {
						Log.e(ReYunConst.TAG,
								"=====appk  isn't a valid key ====");
					}
				}

			} else {

				if (ReYunConst.DebugMode) {
					Log.e(ReYunConst.TAG, "=====appk  NOT NULL ====");
				}
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// not found meta_data
			if (ReYunConst.DebugMode) {
				Log.e(ReYunConst.TAG, "=====not found meta_data ====");
			}

		}

	}

}
