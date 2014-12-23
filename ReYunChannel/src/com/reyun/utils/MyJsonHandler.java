package com.reyun.utils;

import org.json.JSONObject;

import android.content.Context;

import com.loopj.JsonHttpResponseHandler;
import com.reyun.sdk.ReYunChannel;

public class MyJsonHandler extends JsonHttpResponseHandler {

	private final static String TAG = "reyunsdk";
	private Context mContext;
	private String mwhat;

	private RequestParaExd mydata;
	private String mid;

	public MyJsonHandler(String what, Context context, RequestParaExd data) {
		mContext = context;
		mwhat = what;
		mydata = data;
	}

	public MyJsonHandler(String what, Context context, RequestParaExd data,
			String id) {
		mContext = context;
		mwhat = what;
		mydata = data;
		mid = id;

	}

	@Override
	public void onFailure(Throwable arg0, JSONObject arg1) {
		super.onFailure(arg0, arg1);
		if (mwhat != null && !"".equals(mwhat)) {

			if ("GetTime".equals(mwhat)) {
				CommonUtils.printLog(
						TAG,
						"==============SEND faill ========== gettime :"
								+ arg1.toString());
			}
			if ("install".equals(mwhat)) {
				CommonUtils.printLog(
						TAG,
						"==============SEND faill ========== install :"
								+ arg1.toString());
				ReYunChannel.addRecordToDbase("install", mydata);
			}
			if ("startup".equals(mwhat)) {
				CommonUtils.printLog(
						TAG,
						"==============SEND faill ========== startup :"
								+ arg1.toString());

				ReYunChannel.addRecordToDbase("startup", mydata);
			}

			if ("register".equals(mwhat)) {
				CommonUtils.printLog(
						TAG,
						"==============SEND faill ========== register :"
								+ arg1.toString());
				ReYunChannel.addRecordToDbase("register", mydata);
			}

			if ("login".equals(mwhat)) {
				CommonUtils.printLog(
						TAG,
						"==============SEND faill ========== login :"
								+ arg1.toString());
				ReYunChannel.addRecordToDbase("login", mydata);

			}
			if ("payment".equals(mwhat)) {
				CommonUtils.printLog(
						TAG,
						"==============SEND faill ========== payment :"
								+ arg1.toString());
				ReYunChannel.addRecordToDbase("payment", mydata);
			}
		}

	}

	@Override
	public void onSuccess(int arg0, JSONObject responseBody) {
		super.onSuccess(arg0, responseBody);
		if (mwhat != null && !"".equals(mwhat)) {
			if ("GetTime".equals(mwhat)) {
				AppUtil.GetTime(responseBody, mContext);
				CommonUtils.printErrLog(TAG,
						"==============SEND SUCCESS ========== Gettime ");
				if (mid != null && !"".equals(mid)) {
					SqliteDbaseUtil.getInstance(mContext).delRecordsByID(mid);
				}
			}
			if ("install".equals(mwhat)) {
				CommonUtils.printLog(TAG,
						"==============SEND SUCCESS ========== install :"
								+ responseBody.toString());
				Mysp.AddString(mContext, "appIntall", "isAppIntall", "intalled");
				if (mid != null && !"".equals(mid)) {
					SqliteDbaseUtil.getInstance(mContext).delRecordsByID(mid);
				}
			}
			if ("startup".equals(mwhat)) {
				CommonUtils.printLog(TAG,
						"==============SEND SUCCESS ========== startup "
								+ responseBody.toString());
				if (mid != null && !"".equals(mid)) {
					SqliteDbaseUtil.getInstance(mContext).delRecordsByID(mid);
				}
			}

			if ("register".equals(mwhat)) {
				CommonUtils.printLog(TAG,
						"==============SEND SUCCESS ========== register :"
								+ responseBody.toString());
				if (mid != null && !"".equals(mid)) {
					SqliteDbaseUtil.getInstance(mContext).delRecordsByID(mid);
				}
			}
			if ("login".equals(mwhat)) {
				CommonUtils.printLog(TAG,
						"==============SEND SUCCESS ========== login :"
								+ responseBody.toString());
				if (mid != null && !"".equals(mid)) {
					SqliteDbaseUtil.getInstance(mContext).delRecordsByID(mid);
				}
			}

			if ("payment".equals(mwhat)) {
				CommonUtils.printLog(TAG,
						"==============SEND SUCCESS ========== payment"
								+ responseBody.toString());
				if (mid != null && !"".equals(mid)) {
					SqliteDbaseUtil.getInstance(mContext).delRecordsByID(mid);
				}
			}

		}

	}

}
