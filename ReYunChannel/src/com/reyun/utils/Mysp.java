package com.reyun.utils;

import org.json.JSONObject;

import android.R.color;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Mysp {
	private static String RString = "";
	private static long RLong = 0L;

	private static boolean isSuccess = false;

	/**
	 * 获取文件中的值
	 * 
	 * @param context
	 * @param XMLName
	 * @param key
	 * @return
	 */
	public static String GetString(Context context, String XMLName, String key) {

		SharedPreferences mysharedPreferences = context.getSharedPreferences(
				XMLName, context.MODE_PRIVATE);
		RString = mysharedPreferences.getString(key, "unknown");
		return RString;

	}

	public static long GetLong(Context context, String XMLName, String key) {

		SharedPreferences mysharedPreferences = context.getSharedPreferences(
				XMLName, context.MODE_PRIVATE);
		RLong = mysharedPreferences.getLong(key, 0);
		return RLong;

	}

	/**
	 * 添加 值到文件中
	 * 
	 * @param context
	 * @param XMLName
	 * @param Key
	 * @param value
	 * @return
	 */
	public static boolean AddString(Context context, String XMLName,
			String Key, String value) {
		SharedPreferences mysharPreferences = context.getSharedPreferences(
				XMLName, context.MODE_PRIVATE);

		Editor myeditor = mysharPreferences.edit();
		myeditor.clear();
		myeditor.putString(Key, value);
		isSuccess = myeditor.commit();
		return isSuccess;
	}

	public static String GetAppid(Context context) {

		SharedPreferences mysharedPreferences = context.getSharedPreferences(
				"appidXML", context.MODE_PRIVATE);
		RString = mysharedPreferences.getString("appid", "unknown");
		return RString;

	}

	public static RequestParaExd getData(Context context, String what) {

		RequestParaExd params = new RequestParaExd();
		try {
			params.put("appid", GetAppid(context));
			params.put("who", GetString(context, "appIntall", "account"));
			params.put("what", what);
			params.put("when", CommonUtils.getTime(GetLong(context,
					"reyun_interval", "interval")));

			JSONObject contextData = new JSONObject();

			contextData.put("deviceid", CommonUtils.getDeviceId(context));
			contextData.put("channelid",
					GetString(context, "reyun_interval", "channelId"));
			params.put("context", contextData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;

	}

}
