package com.reyun.sdk;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.loopj.JsonHttpResponseHandler;
import com.reyun.reyunchannel.BaseActivity;
import com.reyun.utils.AppUtil;
import com.reyun.utils.CommonUtils;
import com.reyun.utils.MyJsonHandler;
import com.reyun.utils.Mysp;
import com.reyun.utils.ReYunConst;
import com.reyun.utils.Record;
import com.reyun.utils.RequestParaExd;
import com.reyun.utils.SqliteDbaseUtil;
import com.reyun.utils.httpnetwork;

public class ReYunChannel {
	private static long interval = 0;
	private static String m_appid = null;
	private static Context m_context;
	private static String m_channelid = "_default_";
	private final static String TAG = "reyunsdk";

	private static ScreenObserver mScreenObserver;

	private static CatchHomeBtnThread m_catchHomeBtnThread = null;

	private volatile static boolean isSdkExit = false;

	private static HomeBtnBroadcastReceiver my_homeBtnReceiver = null;
	private static TimerTask my_timerTask = null;

	private static Timer m_heartBeatTimer = new Timer(true);

	private final static int HEART_BEAT_TIME = 5 * 60 * 1000;

	// private final static int HEART_BEAT_TIME = 60 * 1000;

	/**
	 * ��ֵ application ������
	 * 
	 * @param appContext
	 */
	public static void setContext(Context appContext) {
		m_context = appContext;
	}

	/**
	 * ��ֵ����
	 * 
	 */
	public enum PaymentType {
		UNIONPAY, APPLE, FREE;
	}

	/**
	 * �й� CNY ��� HKD �ձ� JPY ���ô� CAD ���� KRW Ӣ�� GBP ̨�� TWD ���� USD ŷԪ EUR Խ�� VND ����
	 * BRL
	 */
	public enum CurrencyType {
		CNY, HKD, JPY, CAD, KRW, GBP, TWD, USD, EUR, VND, BRL

	}

	/**
	 * ��ʼ�� ����ʱ����app ������ڴ�����
	 * 
	 * @throws JSONException
	 * @throws NameNotFoundException
	 */

	public static void initWithKeyAndChanelId(final Context appContext,
			String channelId) {
		m_channelid = channelId;// ��������
		m_context = appContext;// ������
		if (AppUtil.isEmpty(m_channelid)) {
			m_channelid = "unknown";
		}
		if (AppUtil.checkAppid(appContext) == false) {
			return;
		}
		Mysp.AddString(appContext, "reyun_interval", "channelId", channelId);
		interval = Mysp.GetLong(appContext, "reyun_interval", "interval");

		/**
		 * ��ȡ������ʱ����
		 * 
		 */
		httpnetwork.get(m_context, "receive/gettime", null, new MyJsonHandler(
				"GetTime", appContext, null));

		/**
		 * ��װ�¼��ϴ�
		 */
		String value = Mysp.GetString(appContext, "appIntall", "isAppIntall");

		if (value.equals("unknown")) {
			CommonUtils.printLog(TAG, "============new intall event=========");
			final RequestParaExd mydata = Mysp.getData(appContext, "install");

			httpnetwork.postJson(m_context, "install", mydata,
					new MyJsonHandler("install", appContext, mydata));
		}

		/**
		 * ÿ�������¼��ϴ�
		 */

		// devictype,os,op,network,resolution
		// ���ͣ� ����ϵͳ �� ��Ӫ�̣�����״̬�� �ֱ�·

		final RequestParaExd mydata = Mysp.getData(m_context, "Startup");

		if (AppUtil.isNetworkAvailable(m_context)) {

			httpnetwork.postJson(m_context, "startup", mydata,
					new MyJsonHandler("startup", appContext, mydata));
		} else {
			ReYunChannel.addRecordToDbase("startup", mydata);
		}

	}

	/**
	 * ����ʱ���� ע��ɹ������
	 * 
	 * @param account
	 *            �û��ʺ�
	 * @return
	 * @throws NameNotFoundException
	 */
	public static void setRegisterWithAccountID(String account) {
		if (AppUtil.checkAppid(m_context) == false) {
			return;
		}
		if (account == null || "".equals(account)
				|| account.trim().length() == 0) {
			CommonUtils.printErrLog(TAG,
					"accountid is incorrect,cancle send....");
			return;
		}

		Mysp.AddString(m_context, "reyun_regInfo", "accountid", account);

		final RequestParaExd mydata = getUserRegisterData(account, m_context);

		if (AppUtil.isNetworkAvailable(m_context)) {
			httpnetwork.postJson(m_context, "register", mydata,
					new MyJsonHandler("register", m_context, mydata));
		} else {
			ReYunChannel.addRecordToDbase("register", mydata);
		}

	}

	/**
	 * ����ʱ���� ��¼�ɹ��� account ��Ϊnull ���Ϊnull �Ļ����ο����
	 * 
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public static void setLoginSuccessBusiness(String account) {

		Mysp.AddString(m_context, "appIntall", "account", account);

		if (AppUtil.checkAppid(m_context) == false) {
			return;
		}

		String strAccountid = "";
		if (account == null || "".equals(account)) {
			strAccountid = "visitors";
		} else {
			strAccountid = account;
		}

		final RequestParaExd mydata = Mysp.getData(m_context, "loggedin");

		if (AppUtil.isNetworkAvailable(m_context)) {

			httpnetwork.postJson(m_context, "loggedin", mydata,
					new MyJsonHandler("login", m_context, mydata));
		} else {
			ReYunChannel.addRecordToDbase("loggedin", mydata);
		}

		/**
		 * �������� home �����¼�
		 */
		if (Integer.parseInt(Build.VERSION.SDK) >= 14) {
			m_catchHomeBtnThread = new CatchHomeBtnThread();
			m_catchHomeBtnThread.setDaemon(true);
			m_catchHomeBtnThread.start();
		} else {
			sdkListenerHomeBtn();
		}

		mScreenObserver = new ScreenObserver(m_context);
		mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {

			@Override
			public void onScreenUnlock() {
				// TODO Auto-generated method stub
				CommonUtils.printLog(TAG, "=======onScreenUnlock======");
				if (isAppOnForeground()) {
					startHeartBeat(m_context);
				}
			}

			@Override
			public void onScreenOn() {
				// TODO Auto-generated method stub
				CommonUtils.printLog(TAG, "=======onScreenOn======");
			}

			@Override
			public void onScreenOff() {
				// TODO Auto-generated method stub
				CommonUtils.printLog(TAG, "=======onScreenOff======");
				if (isAppOnForeground()) {
					stopHeartBeat();
				}
			}
		});

		startHeartBeat(m_context);
	}

	/**
	 * ����ʱ�� ���û���ֵ���
	 * 
	 * @param transactionid
	 *            ��ˮ��
	 * @param paymenttype
	 *            ֧������
	 * @param currencytype
	 *            ��������
	 * @param currencyamount
	 *            ��ʵ���ҵĽ��
	 * @throws NameNotFoundException
	 */
	public static void setPayment(String transactionid,
			PaymentType paymenttype, CurrencyType currencytype,
			float currencyamount) {

		if (AppUtil.checkAppid(m_context) == false) {

			return;
		}

		if (transactionid == null || transactionid.trim().length() == 0) {

			return;
		}
		if (currencytype == null && "".equals(currencytype)) {
			currencytype = CurrencyType.CNY;
		}

		final RequestParaExd mydata = getUserPaymentData(transactionid,
				paymenttype, currencytype, currencyamount, m_context);

		if (AppUtil.isNetworkAvailable(m_context)) {

			httpnetwork.postJson(m_context, "payment", mydata,
					new MyJsonHandler("payment", m_context, mydata));
		} else {
			ReYunChannel.addRecordToDbase("payment", mydata);
		}

	}

	/**
	 * 
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */

	public static void setEvent(final String eventName) {

		if (AppUtil.checkAppid(m_context) == false) {
			return;
		}

		if (eventName == null || eventName.trim().length() == 0) {
			return;
		}

		if (isExist(eventName, m_context) == false) {
			return;
		}

		final RequestParaExd mydata = Mysp.getData(m_context, eventName);

		JsonHttpResponseHandler myJsonRespHandler = new JsonHttpResponseHandler() {

			@Override
			public void onFailure(Throwable exception, String responseBody) {
				// TODO Auto-generated method stub
				super.onFailure(exception, responseBody);

				CommonUtils.printErrLog(TAG,
						"==============SEND FAILED ========== eventName :"
								+ eventName);
				addRecordToDbase("userEvent", mydata);
			}

			@Override
			public void onSuccess(int arg0, JSONObject arg1) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1);

				CommonUtils.printLog(TAG,
						"==============SEND SUCCESS ========== eventName :"
								+ eventName + ":" + arg1.toString());
			}
		};

		if (AppUtil.isNetworkAvailable(m_context)) {
			httpnetwork.postJson(m_context, "event", mydata, myJsonRespHandler);
		} else {
			ReYunChannel.addRecordToDbase("event", mydata);

		}

	}

	/*
	 * ==========================================================================
	 * ====================================================================
	 */

	private static RequestParaExd getUserPaymentData(String transactionId,
			PaymentType paymentType, CurrencyType currencyType,
			float currencyAmount, final Context context) {

		SharedPreferences myaccountid = m_context.getSharedPreferences(
				"reyun_regInfo", m_context.MODE_PRIVATE);
		String accountid = myaccountid.getString("accountid", "unknown");

		RequestParaExd params = new RequestParaExd();

		try {
			params.put("appid", m_appid);
			params.put("who", "" + accountid);
			params.put("what", "payment");
			params.put("when", CommonUtils.getTime(interval));

			JSONObject contextData = new JSONObject();
			contextData.put("deviceid", CommonUtils.getDeviceId(context));
			contextData.put("channelid", m_channelid);
			contextData.put("virtualcoinamount", 0);
			contextData.put("transactionid", transactionId);
			contextData.put("paymenttype", paymentType.name());
			contextData.put("currencytype", currencyType);
			contextData.put("currencyamount", currencyAmount + "");

			params.put("context", contextData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	/**
	 * ע��ɹ�������
	 */

	private static RequestParaExd getUserRegisterData(String accound,
			Context context) {
		RequestParaExd params = new RequestParaExd();
		try {
			params.put("appid", m_appid);
			params.put("who", "" + accound);
			params.put("what", "register");
			params.put("when", CommonUtils.getTime(interval));
			JSONObject contextData = new JSONObject();

			contextData.put("deviceid", CommonUtils.getDeviceId(context));
			contextData.put("channelid",
					Mysp.GetString(context, "reyun_interval", "channelId"));
			params.put("context", contextData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	public static void addRecordToDbase(String what, RequestParaExd record) {
		byte[] byteDataArr = jsonObjToByteArray(record);
		SqliteDbaseUtil.getInstance(m_context).openDateBase();
		SqliteDbaseUtil.getInstance(m_context).insertOneRecordToTable(what,
				byteDataArr);

	}

	/**
	 * json ת��Ϊbyte[]
	 * 
	 * @param obj
	 * @return
	 */
	private static byte[] jsonObjToByteArray(JSONObject obj) {
		byte[] bytes = null;
		if (obj != null) {
			try {
				bytes = obj.toString().getBytes("utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	/**
	 * ���� ����
	 */

	interface ScreenStateListener {
		public void onScreenOn();

		public void onScreenOff();

		public void onScreenUnlock();
	}

	static class ScreenObserver {

		private Context mContext;
		private ScreenBroadcastReceiver mScreenReceiver;
		private ScreenStateListener mScreenStateListener;

		public ScreenObserver(Context context) {
			mContext = context;
			mScreenReceiver = new ScreenBroadcastReceiver();
		}

		/*
		 * screen״̬�㲥������
		 */

		private class ScreenBroadcastReceiver extends BroadcastReceiver {
			private String action = null;

			@Override
			public void onReceive(Context context, Intent intent) {
				action = intent.getAction();
				if (Intent.ACTION_SCREEN_ON.equals(action)) {
					mScreenStateListener.onScreenOn();
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					mScreenStateListener.onScreenOff();
				} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
					mScreenStateListener.onScreenUnlock();
				}
			}
		}

		/**
		 * @param listener
		 */
		public void requestScreenStateUpdate(ScreenStateListener listener) {
			mScreenStateListener = listener;
			startScreenBroadcastReceiver();

		}

		/**
		 * ֹͣscreen״̬����
		 */
		public void stopScreenStateUpdate() {// ȡ��ע��
			mContext.unregisterReceiver(mScreenReceiver);
		}

		/**
		 * ����screen״̬�㲥������
		 */
		private void startScreenBroadcastReceiver() {// ������Ļע��
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			mContext.registerReceiver(mScreenReceiver, filter);
		}

	};

	/**
	 * android 4.0 ϵͳ���� ���� home ����
	 */
	static class CatchHomeBtnThread extends Thread {

		private volatile boolean isThreadRun = true;

		@Override
		public void run() {

			synchronized (this) {

				// Process mLogcatProc = null;
				// BufferedReader reader = null;
				// String line;
				if (CommonUtils.checkPermissions(m_context,
						"android.permission.GET_TASKS") == false) {

					if (ReYunConst.DebugMode) {
						Log.e(TAG,
								"======== lost permission android.permission.GET_TASKS =========");
					}

				} else {
					while (isThreadRun) {

						try {
							sleep(500);

							if (!isAppOnForeground() && !isSdkExit) {
								// ���ͼ���home ����Ϣ
								myhandler
										.sendMessage(myhandler.obtainMessage());

								try {
									wait();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									break;
								}
							}

						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							isThreadRun = false;
							break;
						}

					}
				}

			}
		}

		public void close() {

			this.isThreadRun = false;
		}

	};

	/**
	 * �жϻ�Ƿ��ڿ�����Ļ�� �ϲ�
	 */
	public static boolean isAppOnForeground() {

		if (m_context == null) {
			return false;
		}
		ActivityManager pActivityManager = (ActivityManager) m_context
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (pActivityManager == null) {
			return false;
		}
		List<RunningAppProcessInfo> appProcesses = pActivityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName == null) {

				if (ReYunConst.DebugMode == true) {

					Log.e(TAG, "appProcess.processName is null!");
				}
				return false;
			}
			if (m_context == null) {

				if (ReYunConst.DebugMode == true) {

					Log.e(TAG, "=====m_context is null!====");
				}
				return false;
			}
			// if (appProcess.processName.equals(m_context.getPackageName())
			// && appProcess.importance ==
			// RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
			// return true;
			// }
			if (appProcess.processName.equals(m_context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

					return true;
				} else {
					return false;
				}
			}
		}

		return false;
	}

	private static Handler myhandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			CommonUtils.printLog(TAG, "4.0 Home is Pressed+++++++++++++++++");

			stopHeartBeat();
		}
	};

	/**
	 * android 4.0 ���������� ���� home button ֮�󣬱��浱ǰ��Ϸ����ʱ�� session.��resume ʱ��ⷢ������
	 */
	private static class HomeBtnBroadcastReceiver extends BroadcastReceiver {

		private String action = null;
		final String SYSTEM_HOME_KEY = "homekey";
		final String SYSTEM_RECENT_APPS = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			String dlg = Intent.ACTION_CLOSE_SYSTEM_DIALOGS;
			boolean isequal = dlg.equals(action);
			if (isequal && isAppOnForeground()) {

				String reason = intent.getStringExtra("reason");

				if (reason != null) {

					if (reason.equals(SYSTEM_HOME_KEY)) {

						CommonUtils.printLog(TAG,
								"=========== pressed home button ===========");

						stopHeartBeat();

					} else if (reason.equals(SYSTEM_RECENT_APPS)) {

						CommonUtils
								.printLog(TAG,
										"=========== long pressed home button ===========");

					}

				}

			}
		}
	}

	/**
	 * ����ֹͣ
	 */
	public static void stopHeartBeat() {
		CommonUtils.printLog(TAG, "=============ͣ������===========");
		if (m_heartBeatTimer != null) {
			m_heartBeatTimer.cancel();
			m_heartBeatTimer = null;
		}

		if (my_timerTask != null) {
			my_timerTask.cancel();
			my_timerTask = null;
		}
	}

	/**
	 * ����ʱ����app �����˳�֮ǰ����
	 */
	public static void exitSdk() {

		SqliteDbaseUtil.getInstance(m_context).closeDataBase();// �ر����ݿ�
		if (mScreenObserver != null)// ��������
			mScreenObserver.stopScreenStateUpdate();

		if (Integer.parseInt(Build.VERSION.SDK) < 14
				&& my_homeBtnReceiver != null) {

			m_context.unregisterReceiver(my_homeBtnReceiver);
		} else {
			if (m_catchHomeBtnThread != null) {

				try {
					m_catchHomeBtnThread.close();
					m_catchHomeBtnThread.interrupt();
					isSdkExit = true;
					Thread.sleep(0);
					m_catchHomeBtnThread = null;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		if (m_heartBeatTimer != null) {
			m_heartBeatTimer.cancel();
			m_heartBeatTimer = null;
		}

		if (m_appid != null) {
			m_appid = null;
		}
		my_homeBtnReceiver = null;
		m_context = null;
	}

	/**
	 * ���� home ����
	 */
	private static void sdkListenerHomeBtn() {

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		my_homeBtnReceiver = new HomeBtnBroadcastReceiver();
		m_context.registerReceiver(my_homeBtnReceiver, filter);
	}

	/*
	 * �ж��Ƿ���� �¼�����
	 */

	private static boolean isExist(String EventName, Context context) {

		SharedPreferences mSharedPreferences;
		mSharedPreferences = context.getSharedPreferences("event",
				Context.MODE_PRIVATE);
		String content = mSharedPreferences.getString("content", "unknown");

		if (!"unknown".equals(content)) {
			String[] contentsize = content.split("-");
			if (!content.contains(EventName)) {
				if (contentsize.length < 10) {
					content = content + "-" + EventName;
					mSharedPreferences = context.getSharedPreferences("event",
							Context.MODE_PRIVATE);
					Editor myeditor = mSharedPreferences.edit();
					myeditor.clear();
					myeditor.putString("content", content);
					myeditor.commit();

					return true;
				} else {

					if (ReYunConst.DebugMode) {
						Log.e(TAG, "========No more than 10 events=====");
					}
					return false;
				}
			}
		} else {
			mSharedPreferences = context.getSharedPreferences("event",
					Context.MODE_PRIVATE);
			Editor myeditor = mSharedPreferences.edit();
			myeditor.clear();
			myeditor.putString("content", EventName);
			myeditor.commit();
		}
		return true;

	}

	/**
	 * ����ʧ�ܵ�����
	 */

	private static void sendFailureRecord(final int record_count) {

		CommonUtils.printLog("TAG", "����ʧ�ܵ�����");
		SqliteDbaseUtil.getInstance(m_context).openDateBase();
		/**
		 * �ɵķ�ʽ����cache ����
		 */
		// SqliteDbaseUtil.getInstance(m_context).queryAndSendAllRecordFromDb();

		/**
		 * �µķ�ʽ����cache ����
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String mydata = SqliteDbaseUtil.getInstance(m_context)
						.queryrecordsByCount(record_count);
				if (mydata != null && !"".equals(mydata)) {

					mydbhandler.sendMessage(mydbhandler.obtainMessage(1,
							record_count, 0, mydata));
				}

			}
		}).start();
	}

	private static Handler mydbhandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			String mydata = (String) msg.obj;
			final int record_count = msg.arg1;
			if (mydata == null) {
				return;
			}

			RequestParaExd params = new RequestParaExd();
			try {
				params.put("from", "track");
				params.put("data", new JSONArray(mydata));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JsonHttpResponseHandler myJsonRespHandler = new JsonHttpResponseHandler() {

				@Override
				public void onFailure(Throwable exception, String responseBody) {
					// TODO Auto-generated method stub
					super.onFailure(exception, responseBody);
					CommonUtils
							.printLog(TAG,
									"############sendFailureRecord  failure ############ ");
				}

				@Override
				public void onSuccess(int arg0, JSONObject arg1) {
					// TODO Auto-generated method stub
					super.onSuccess(arg0, arg1);
					try {
						if (!arg1.isNull("status")
								&& arg1.getInt("status") == 0)

							SqliteDbaseUtil.getInstance(m_context)
									.delRecordsByCount(record_count);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					CommonUtils.printLog(TAG,
							"==============sendFailureRecord  SUCCESS =========="
									+ arg1.toString());
				}
			};

			httpnetwork.postBatchJson(m_context, "receive/batch", params,
					myJsonRespHandler);
		}
	};

	/**
	 * �������� �����û��������Ҫ����
	 */

	/**
	 * �����������
	 */
	public static void startHeartBeat(Context context) {
		m_context = context;

		// stopHeartBeat();

		if (m_heartBeatTimer == null) {
			m_heartBeatTimer = new Timer(true);
		} else {
			m_heartBeatTimer.cancel();
			m_heartBeatTimer = new Timer(true);
		}

		if (my_timerTask == null) {

			my_timerTask = new TimerTask() {

				@Override
				public void run() {
					CommonUtils.printLog(TAG, "=============��===========");
					// TODO Auto-generated method stub
					/**
					 * �������ݿ�ʧ�ܵ�����
					 */

					SqliteDbaseUtil.getInstance(m_context).openDateBase();
					int count = SqliteDbaseUtil.getInstance(m_context)
							.queryrecords();
					if (count != -1) {
						if (count > 1000) {

							String content = Mysp.GetString(m_context, "event",
									"content");
							String[] contentsize = content.split("-");
							SqliteDbaseUtil.getInstance(m_context)
									.delRecordsByWhat(contentsize);
						}
					}
					sendFailureRecord(10);
				}
			};
		}

		if (m_heartBeatTimer != null && my_timerTask != null) {

			m_heartBeatTimer.schedule(my_timerTask, 1000, HEART_BEAT_TIME);
		}
	}

}
