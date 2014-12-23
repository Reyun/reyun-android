package com.reyun.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.AsyncHttpClient;
import com.loopj.AsyncHttpResponseHandler;
import com.loopj.RequestParams;

public class httpnetwork {
	private static final String TAG = "HTTP_NETWORK";
	private final static String SUFFIX = "receive/track/";
	private static AsyncHttpClient client = new AsyncHttpClient();


	public static void get(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {

		if (CommonUtils.isWapConnected(context)) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int proxyPort = android.net.Proxy.getDefaultPort();
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			client.setTimeout(ReYunConst.WAP_REQUEST_TIMEOUT);
			client.getHttpClient().getParams()
					.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
		client.get(ReYunConst.BASE_URL + url, params, responseHandler);

	}

	public static void post(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if (CommonUtils.isWapConnected(context)) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int proxyPort = android.net.Proxy.getDefaultPort();
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			client.setTimeout(ReYunConst.REQUEST_TIMEOUT);
			client.getHttpClient().getParams()
					.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
		client.post(getAbsoluteUrl(url), params, responseHandler);

		CommonUtils.printLog(TAG, "=======request url is:"
				+ getAbsoluteUrl(url));
		if (params != null) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						params.getEntity().getContent()));
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = in.readLine()) != null) {
					buffer.append(line);

				}
				CommonUtils.printLog(TAG, "=======request params is ======"
						+ buffer.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			CommonUtils.printLog(TAG, "=======request params is null ======");
	}

	public static void postJson(Context context, String url,
			JSONObject jsonEntity, AsyncHttpResponseHandler responseHandler) {
		if (CommonUtils.isWapConnected(context) == true) {

			/** ============ code for wap connect setting =========== */
			String proxyHost = android.net.Proxy.getDefaultHost();
			int proxyPort = android.net.Proxy.getDefaultPort();
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			client.setTimeout(ReYunConst.REQUEST_TIMEOUT);
			client.getHttpClient().getParams()
					.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
		StringEntity entity = null;
		try {
			entity = new StringEntity((jsonEntity == null ? null
					: jsonEntity.toString()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.post(context, getAbsoluteUrl(url), entity, "application/json",
				responseHandler);
		CommonUtils.printLog(TAG, "=======request url is:"
				+ getAbsoluteUrl(url));
		if (jsonEntity != null) {

			CommonUtils.printLog(TAG, "=======request params is ======"
					+ jsonEntity.toString());
		} else
			CommonUtils.printLog(TAG, "=======request params is null ======");
	}

	public static void postBatchJson(Context context, String url,
			JSONObject jsonEntity, AsyncHttpResponseHandler responseHandler) {
		if (CommonUtils.isWapConnected(context) == true) {

			/** ============ code for wap connect setting =========== */
			String proxyHost = android.net.Proxy.getDefaultHost();
			int proxyPort = android.net.Proxy.getDefaultPort();
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			client.setTimeout(ReYunConst.REQUEST_TIMEOUT);
			client.getHttpClient().getParams()
					.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
		StringEntity entity = null;
		try {
			entity = new StringEntity((jsonEntity == null ? null
					: jsonEntity.toString()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.post(context, ReYunConst.BASE_URL + url, entity,
				"application/json", responseHandler);
		CommonUtils.printLog(TAG, "=======request url is:"
				+ ReYunConst.BASE_URL + url);
		if (jsonEntity != null) {

			CommonUtils.printLog(TAG, "=======request params is ======"
					+ jsonEntity.toString());
		} else
			CommonUtils.printLog(TAG, "=======request params is null ======");
	}

	private static String getAbsoluteUrl(String url) {
		return ReYunConst.BASE_URL + SUFFIX + url;
	}

}
