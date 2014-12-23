package com.reyun.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.R.dimen;
import android.R.string;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class CommonUtils {
	/**
	 * checkpermissions
	 * 
	 * @param context
	 * @param permission
	 * @param true of false
	 * 
	 */

	public static boolean checkPermissions(Context context, String permission) {
		PackageManager localPackageManager = context.getPackageManager();
		return localPackageManager.checkPermission(permission,
				context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Get the current time format yyyy-MM-dd HH：mm：ss
	 * 
	 * @return
	 * 
	 */

	public static String getTime(long interval) {
		Date date = new Date(System.currentTimeMillis() - interval);
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return localSimpleDateFormat.format(date);

	}

	/**
	 * Get the timezone
	 * 
	 * @return
	 */
	public static String getTimeZone() {
		int time_zone = TimeZone.getDefault().getRawOffset() / 1000 / 3600;
		if (time_zone > 0)
			return "+" + time_zone;
		else
			return time_zone + "";

	}

	/**
	 * Get current activity's name
	 * 
	 * @param context
	 * @return
	 */

	public static String getActivityName(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		if (checkPermissions(context, "android.permission.GET_TASKS")) {
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			return cn.getShortClassName();
		} else {
			if (ReYunConst.DebugMode) {
				Log.e("lost permission", "android.permission.GET_TASKS");
			}
			return null;

		}

	}

	/**
	 * Get PackageName
	 * 
	 * @param context
	 * @return
	 */

	public static String getPackageName(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		if (checkPermissions(context, "android.permission.GET_TASKS")) {
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			return cn.getPackageName();
		} else {
			if (ReYunConst.DebugMode) {
				Log.e("lost permission", "android.permission.GET_TASKS");
			}
		}
		return null;

	}

	/**
	 * check phone state is readied ;
	 * 
	 * @param context
	 * @return
	 */

	public static boolean checkPhoneState(Context context) {
		PackageManager packageManager = context.getPackageManager();
		if (packageManager
				.checkPermission("android.permission.READ_PHONE_STATE",
						context.getPackageName()) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * get sdk number
	 * 
	 * @param paramContext
	 * @return
	 */

	public static String getSdkVersion(Context context) {
		String osVersion = "";
		if (checkPhoneState(context)) {
			osVersion = android.os.Build.VERSION.RELEASE;
			if (ReYunConst.DebugMode) {
				Log.e("android_osVersion", "OsVersion" + osVersion);
			}
			return osVersion;
		} else {
			if (ReYunConst.DebugMode) {
				Log.e("android_osVersion", "OsVersion get failed");
			}

			return null;
		}

	}

	/**
	 * Get the version number of the current program
	 * 
	 * @param ontext
	 * @return
	 */
	public static String getCurVersion(Context context) {
		String curversion = "";

		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			curversion = pi.versionName;
			if (curversion == null || curversion.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			if (ReYunConst.DebugMode) {
				Log.e("VersionInfo", "NameNotFoundException", e);
			}
		}
		return curversion;
	}

	/**
	 * Get deviceid
	 * 
	 * @param context
	 *            add <user-permission android:name="READ_PHOME_STATE"/>
	 * @return
	 */
	public static String getDeviceId(Context context) {
		if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
			String deviceid = "";
			if (checkPhoneState(context)) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(context.TELEPHONY_SERVICE);

				deviceid = tm.getDeviceId();
				if (deviceid == null) {
					deviceid = Settings.Secure.getString(
							context.getContentResolver(),
							Settings.Secure.ANDROID_ID);
				}
			}
			if (deviceid != null) {
				if (ReYunConst.DebugMode) {
					printLog("commonUtil", "deviceId:" + deviceid);
				}
				return deviceid;
			} else {
				if (ReYunConst.DebugMode) {
					Log.e("commonUtil", "deviceId is null");
				}
			}
			return "unknown";
		} else {
			if (ReYunConst.DebugMode) {
				Log.e("lost permisssioin",
						"lost----->android.permission.READ_PHONE_STATE");
			}
		}
		return "unknown";
	}

	// Get operatorName

	public static String getOperatorName(Context context) {

		if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
			String op = "unknown";
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			op = tm.getSimOperatorName();
			if (op != null) {
				if (op.equals("")) {
					op = "unkown";
				}
				if (ReYunConst.DebugMode) {
					printLog("commonUtil", "op:" + op);
				}
				return op;
			} else {
				if (ReYunConst.DebugMode) {
					Log.e("commonUtil", "deviceid is null");
				}
				return "unknown";
			}

		} else {
			if (ReYunConst.DebugMode) {
				Log.e("lost permissioin",
						"lost------>android.permission.READ_PHONE_STATE");
				Toast.makeText(
						context,
						"getOperatorName lost----->android.permission.READ_PHONE_STATE",
						Toast.LENGTH_LONG);
			}

			return "unknown";
		}

	}

	public static String getPhoneResolution(Context context) {
		String resolution = "unknown";
		WindowManager manager = (WindowManager) context
				.getSystemService(context.WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(displayMetrics);
		resolution = displayMetrics.widthPixels + "*"
				+ displayMetrics.heightPixels;
		return resolution;
	}

	/**
	 * To determine whether it contains a gyroscope
	 * 
	 * @return
	 */
	public static boolean isHaveGravity(Context context) {
		SensorManager manager = (SensorManager) context
				.getSystemService(context.SENSOR_SERVICE);
		if (manager == null) {
			return false;

		}
		return true;
	}

	/**
	 * Get the current networking
	 * 
	 * @param context
	 * @return WIFI or MOBILE
	 */
	private static String getNetworkType(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		int type = manager.getNetworkType();
		String typeString = "UNKOWN";
		if (type == TelephonyManager.NETWORK_TYPE_CDMA) {
			typeString = "CDMA";
			typeString = "2G";
		} else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
			typeString = "EDGE";
			typeString = "2G";
		} else if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
			typeString = "EVDO_0";
			typeString = "3G"; // ����3G
		} else if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
			typeString = "EVDO_A";
			typeString = "3G"; // ���� 3G
		} else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
			typeString = "GPRS";
			typeString = "2G"; // 2G ����
		} else if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
			typeString = "HSDPA";
			typeString = "3G"; // ��ͨ3G
		} else if (type == TelephonyManager.NETWORK_TYPE_HSPA) {
			typeString = "HSPA";
		} else if (type == TelephonyManager.NETWORK_TYPE_HSUPA) {
			typeString = "HSUPA";
		} else if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
			typeString = "UMTS";
			typeString = "3G"; // ��ͨ 3G
		} else if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
			typeString = "UNKOWN";
		}
		return typeString;
	}

	/**
	 * Determine the current network type
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkTypeWifi(Context context) {
		if (checkPermissions(context, "android.permission.INTERNET")) {
			ConnectivityManager cManager = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()
					&& info.getTypeName().equals("WIFI")) {
				return true;

			} else {
				if (ReYunConst.DebugMode) {
					Log.e("error", "Network not wifi");
				}
				return false;
			}
		} else {
			if (ReYunConst.DebugMode) {
				Log.e(" lost  permission",
						"lost----> android.permission.INTERNET");
			}
			return false;
		}

	}

	public static String getConnectType(Context context) {
		if (checkPermissions(context, "android.permission.INTERNET")) {
			ConnectivityManager cManager = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			NetworkInfo mwifi = cManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mMobile = cManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mwifi != null && mwifi.isAvailable() && mwifi.isConnected()) {
				return "WIFI";
			} else if (mMobile != null && mMobile.isAvailable()
					&& mMobile.isConnected()) {
				String connectType = getNetworkType(context);
				return connectType;
			} else {
				return "unknown";
			}
		} else {
			if (ReYunConst.DebugMode) {
				Log.e(" lost  permission",
						"lost----> android.permission.INTERNET");
			}
		}
		return "unknown";
	}

	/**
	 * check whether wap connect
	 */
	public static boolean isWapConnected(Context context) {
		if (checkPermissions(context, "android.permission.INTERNET")) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			NetworkInfo mMoblie = manager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMoblie != null && mMoblie.isAvailable()
					&& mMoblie.isConnected()) {
				String proxyHost = android.net.Proxy.getDefaultHost();
				if (proxyHost != null && !proxyHost.equals("")) {
					return true;
				}
			}
		} else {
			if (ReYunConst.DebugMode) {
				Log.e(" lost  permission",
						"lost----> android.permission.INTERNET");
			}
		}
		return false;
	}

	/**
	 * set the output log
	 * 
	 * @param tag
	 * @param log
	 */

	public static void printLog(String tag, String log) {
		if (ReYunConst.DebugMode == true) {
			Log.d(tag, log);
		}
	}

	public static void printErrLog(String tag, String log) {
		if (ReYunConst.DebugMode == true) {
			Log.e(tag, log);
		}
	}

}
