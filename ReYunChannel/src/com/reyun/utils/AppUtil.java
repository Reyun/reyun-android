package com.reyun.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AppUtil {
	private static String m_appid = "";
	private static long interval = 0;

	public static String GetMetaData(Context appContext, String DataName)
			throws NameNotFoundException {
		ApplicationInfo appInfo = appContext.getPackageManager()
				.getApplicationInfo(appContext.getPackageName(),
						PackageManager.GET_META_DATA);
		String msg = appInfo.metaData.getString(DataName);
		return msg;
	}

	// 检测 app 的sdk
	public static boolean checkAppid(Context context) {
		try {
			m_appid = AppUtil.GetMetaData(context, "com.reyun.KEY");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			if (ReYunConst.DebugMode) {
				Log.e(ReYunConst.TAG, "========appidkey is null =====");
			}
		}
		if (m_appid == null) {

			if (ReYunConst.DebugMode) {
				Log.e(ReYunConst.TAG, "========appid is null=====");
			}

			return false;
		} else {
			if (m_appid.length() >= 32 && m_appid.length() <= 64) {

				return true;
			} else {

				if (ReYunConst.DebugMode) {
					Log.e(ReYunConst.TAG, "========appid is uncorrected=====");
				}
				return false;
			}
		}

	}

	/**
	 * 判断对象是否为 null 如果为 null or “” 返回false
	 * 
	 * @param entity
	 * @return
	 */
	public static boolean isEmpty(String entity) {
		return (entity == null || "".equals(entity));
	}

	/**
	 * 获取服务器间隔
	 */

	public static void GetTime(JSONObject responseBody, Context m_context) {

		String serverTime = System.currentTimeMillis() + "";
		try {
			serverTime = responseBody.getString("ts");
		} catch (Exception e) {
			e.printStackTrace();
		}
		long myserverTime = Long.parseLong(serverTime);
		interval = System.currentTimeMillis() - myserverTime;
		SharedPreferences mysharPreferences = m_context.getSharedPreferences(
				"reyun_interval", m_context.MODE_PRIVATE);
		Editor myeditor = mysharPreferences.edit();
		myeditor.clear();
		myeditor.putLong("interval", interval);
		myeditor.commit();

	}

	private static RequestParaExd getUserInstallData(Context context) {
		RequestParaExd params = new RequestParaExd();

		try {
			params.put("appid", AppUtil.GetMetaData(context, "com.reyun.KEY"));
			params.put("what", "install");
			params.put("when", CommonUtils.getTime(interval));
			JSONObject dev = new JSONObject();
			dev.put("deviceid", CommonUtils.getDeviceId(context));
			// dev.put("channelid", m_channelid);
			params.put("context", dev);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return params;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
