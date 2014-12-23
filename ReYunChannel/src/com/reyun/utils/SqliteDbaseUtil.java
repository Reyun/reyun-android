package com.reyun.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqliteDbaseUtil {

	private Context sqliteContext;
	private ReYunDbHelper dbHelper;
	private static SqliteDbaseUtil mSqliteUtilInstance = null;
	private SQLiteDatabase mSqliteDB = null;
	private final String TAG = "sqlite";
	private ArrayList my_list;

	private SqliteDbaseUtil() {

	}

	private SqliteDbaseUtil(Context context) {

		this.sqliteContext = context;
	}

	public synchronized static SqliteDbaseUtil getInstance(Context context) {
		if (mSqliteUtilInstance == null) {
			mSqliteUtilInstance = new SqliteDbaseUtil(context);
		}
		return mSqliteUtilInstance;
	}

	public boolean openDateBase() {
		if (mSqliteDB == null) {
			dbHelper = new ReYunDbHelper(sqliteContext);
			mSqliteDB = dbHelper.getWritableDatabase();
			mSqliteDB
					.execSQL("CREATE TABLE IF NOT EXISTS "
							+ ReYunSqlConst.TABLE_NAME
							+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, what char, value BLOB);");
		}
		return (mSqliteDB == null) ? false : true;
	}

	public void closeDataBase() {
		if (mSqliteDB != null) {

			mSqliteDB.close();
			mSqliteDB = null;
		}
	}

	public void insertOneRecordToTable(String what, byte[] requestparams) {
		mSqliteDB.beginTransaction();
		ContentValues cValues = new ContentValues();
		cValues.put("what", what);
		cValues.put("value", requestparams);
		mSqliteDB.insert(ReYunSqlConst.TABLE_NAME, null, cValues);
		mSqliteDB.setTransactionSuccessful();
		if (ReYunConst.DebugMode) {
			Log.e(TAG, "====data base insert OneRecord  ==:" + what);
		}
		mSqliteDB.endTransaction();

	}

	private void delOnrRecord(int id) {
		mSqliteDB.beginTransaction();
		String sql = "delete from " + ReYunSqlConst.TABLE_NAME + "where _id ="
				+ id;
		mSqliteDB.execSQL(sql);
		mSqliteDB.setTransactionSuccessful();
		if (ReYunConst.DebugMode) {
			Log.e(TAG, "=== delete OneRecord  == id :" + id);
		}
	}

	public String queryrecordsByCount(int count) {
		Cursor mycursor = null;
		mSqliteDB.beginTransaction();
		try {
			mycursor = mSqliteDB.rawQuery("SELECT * FROM "
					+ ReYunSqlConst.TABLE_NAME + " limit ?",
					new String[] { String.valueOf(count) });
			mSqliteDB.setTransactionSuccessful();
		} catch (Exception e) {
			mSqliteDB.endTransaction();
		} finally {
			mSqliteDB.endTransaction();
		}

		if (ReYunConst.DebugMode) {
			Log.e(TAG,
					"====database queryRecords count is ======"
							+ mycursor.getCount());
		}

		String mydata = null;
		if (mycursor != null) {
			if (mycursor.getCount() > 0) {
				my_list = new ArrayList<String>();
				JSONArray array = new JSONArray();
				while (mycursor.moveToNext()) {

					final int id = mycursor.getInt(mycursor
							.getColumnIndex("_id"));
					String name = mycursor.getString(mycursor
							.getColumnIndex("what"));
					byte[] value = mycursor.getBlob(mycursor
							.getColumnIndex("value"));

					my_list.add(id + "");
					JSONObject object = byteArrayToJsonObj(value);
					CommonUtils.printErrLog(TAG,
							"====query failed record row id is ======" + id);
					array.put(object);
				}
				mydata = array.toString();
			}
			mycursor.close();
		}
		return mydata;
	}

	private JSONObject byteArrayToJsonObj(byte[] bytes) {
		JSONObject jsonObject = null;
		if (bytes != null) {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream), 8 * 1024);
			StringBuffer json_buffer = new StringBuffer();
			String line = "";
			try {
				while ((line = in.readLine()) != null) {
					json_buffer.append(line);

				}
				jsonObject = new JSONObject(json_buffer.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return jsonObject;
	}

	public void delRecordsByCount(int count) {
		mSqliteDB.beginTransaction();

		try {

			if (my_list != null) {
				for (int index = 0; index < my_list.size(); index++) {
					String[] whereargs = new String[] { my_list.get(index) + "" };
					int a = mSqliteDB.delete(ReYunSqlConst.TABLE_NAME, "_id=?",
							whereargs);

				}

			}
			if (ReYunConst.DebugMode) {
				Log.e(TAG, "===delete success :" + count);
			}
			mSqliteDB.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			mSqliteDB.endTransaction();
		}
		if (ReYunConst.DebugMode) {
			Log.e(TAG, "=== delete records count  == count :" + count);
		}

	}

	private Object byteArrayToObject(byte[] bytes) {
		Object obj = null;
		if (bytes == null) {
			return null;
		}

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public int queryrecords() {
		int Cursorcount = -1;
		try {
			Cursor mycursor = null;

			mycursor = mSqliteDB.query(ReYunSqlConst.TABLE_NAME, null, null,
					null, null, null, null);
			if (mycursor != null && !"".equals(mycursor)) {
				Cursorcount = mycursor.getCount();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Cursorcount;
	}

	public void delRecordsByWhat(String[] what) {
		mSqliteDB.beginTransaction();

		try {

			String[] whereargs = new String[what.length];
			for (int index = 0; index < what.length; index++) {
				whereargs[index] = what[index];

				mSqliteDB.delete(ReYunSqlConst.TABLE_NAME, "what=?", whereargs);
			}
			if (ReYunConst.DebugMode) {
				Log.e(TAG, "=== delete records success ==:");
			}
			mSqliteDB.setTransactionSuccessful();
		} catch (Exception e) {
			mSqliteDB.endTransaction();
		} finally {
			mSqliteDB.endTransaction();
		}
		if (ReYunConst.DebugMode) {
			Log.e(TAG, "=== delete records what  ==:");
		}

	}

	/**
	 * 通过id 删除
	 * 
	 */

	public void delRecordsByID(String id) {
		mSqliteDB.beginTransaction();

		try {

			String[] whereargs = new String[] { id };
			mSqliteDB.delete(ReYunSqlConst.TABLE_NAME, "_id=?", whereargs);
			if (ReYunConst.DebugMode) {
				Log.e(TAG, "=== delete records success ==:");
			}
			mSqliteDB.setTransactionSuccessful();
		} catch (Exception e) {
			mSqliteDB.endTransaction();
		} finally {
			mSqliteDB.endTransaction();
		}
		if (ReYunConst.DebugMode) {
			Log.e(TAG, "=== delete records what  ==:");
		}

	}

	/**
	 * 返回发送失败的数据数据
	 */

	public List queryJsonByCount(int count) {
		Cursor mycursor = null;
		List mydatalist = null;
		mSqliteDB.beginTransaction();
		try {
			mycursor = mSqliteDB.rawQuery("SELECT * FROM "
					+ ReYunSqlConst.TABLE_NAME + " limit ?",
					new String[] { String.valueOf(count) });
			mSqliteDB.setTransactionSuccessful();
		} catch (Exception e) {
			mSqliteDB.endTransaction();
		} finally {
			mSqliteDB.endTransaction();
		}

		if (ReYunConst.DebugMode) {
			Log.e(TAG,
					"====database queryRecords count is ======"
							+ mycursor.getCount());
		}

		if (mycursor != null) {

			if (mycursor.getCount() > 0) {
				mydatalist = new ArrayList<Record>();
				while (mycursor.moveToNext()) {
					Record re = new Record();
					re.setId(mycursor.getInt(mycursor.getColumnIndex("_id")));
					re.setName(mycursor.getString(mycursor
							.getColumnIndex("what")));

					JSONObject object = byteArrayToJsonObj(mycursor
							.getBlob(mycursor.getColumnIndex("value")));
					re.setValue(object);
					mydatalist.add(re);
					CommonUtils.printErrLog(
							TAG,
							"====query failed record row id is ======"
									+ re.getId());
				}
			}

		}

		// String mydata = null;
		// if (mycursor != null) {
		// if (mycursor.getCount() > 0) {
		// my_list = new ArrayList<String>();
		// JSONArray array = new JSONArray();
		// while (mycursor.moveToNext()) {
		//
		// final int id = mycursor.getInt(mycursor
		// .getColumnIndex("_id"));
		// String name = mycursor.getString(mycursor
		// .getColumnIndex("what"));
		// byte[] value = mycursor.getBlob(mycursor
		// .getColumnIndex("value"));
		//
		// my_list.add(id + "");
		// JSONObject object = byteArrayToJsonObj(value);
		// CommonUtils.printErrLog(TAG,
		// "====query failed record row id is ======" + id);
		// array.put(object);
		// }
		// mydata = array.toString();
		// }
		// // mSqliteDB.endTransaction();
		// mycursor.close();
		// }
		return mydatalist;
	}
}
