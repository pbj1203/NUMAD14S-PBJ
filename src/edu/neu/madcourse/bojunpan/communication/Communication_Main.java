package edu.neu.madcourse.bojunpan.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.internal.r;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.neu.mhealth.api.KeyValueAPI;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_About;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.inputmethodservice.Keyboard.Key;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Sampler.Value;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.Touch;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Communication_Main extends Activity implements OnClickListener {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_ALERT_TEXT = "alertText";
	public static final String PROPERTY_TITLE_TEXT = "titleText";
	public static final String PROPERTY_CONTENT_TEXT = "contentText";
	public static final String PROPERTY_NTYPE = "nType";
	public String IMEI;
	ConnectionDetector connectionDetector;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	static final String TAG = "GCM_Communication";
	TextView mDisplay;
	EditText mMessage;
	EditText mUsername;
	TextView mSenderIndex;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;
	String regid;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.communication_main);
		mDisplay = (TextView) findViewById(R.id.communication_display);
		mMessage = (EditText) findViewById(R.id.communication_edit_message);
		mUsername = (EditText) findViewById(R.id.communication_edit_username);
		mSenderIndex = (TextView) findViewById(R.id.communication_sender_index);
		gcm = GoogleCloudMessaging.getInstance(this);
		context = getApplicationContext();
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		IMEI = tm.getDeviceId();
		mSenderIndex.setText("IMEI:" + IMEI + "\n");
		regid = getRegistrationId(context);
		if (!TextUtils.isEmpty(regid)) {
			mDisplay.setText("Device registered, registration ID=" + regid);
		}
		Communication_Globals.is_front = true;
		Log.d("is_front:",String.valueOf(Communication_Globals.is_front));
		if (Communication_Globals.is_client == true) {
			String contentText = KeyValueAPI.get(
					Communication_Globals.BACKUP_USERNAME,
					Communication_Globals.BACKUP_PASSWORD, "contentText");
			String sIMEI = KeyValueAPI.get(
					Communication_Globals.BACKUP_USERNAME,
					Communication_Globals.BACKUP_PASSWORD, "Sender_IMEI");
			mMessage.setText(contentText);
			mUsername.setText("Messeage from: " + sIMEI);
			Communication_Globals.is_client = false;
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("OnStart","is_front = true");
		Communication_Globals.is_front = true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("OnStop","is_front = false");
		Communication_Globals.is_front = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("OnDestroy","is_front = true");
		Communication_Globals.is_front = true;
	}

	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		Log.i(TAG, String.valueOf(registeredVersion));
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(Communication_Main.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, "alertText",
							"Register Notification");
					KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, "titleText",
							"Register");
					KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD,
							"contentText", "Registering Successful!");
					regid = gcm.register(Communication_Globals.GCM_SENDER_ID);
					int total = 0;
					if (KeyValueAPI.isServerAvailable()) {
						if (!KeyValueAPI.get(
								Communication_Globals.BACKUP_USERNAME,
								Communication_Globals.BACKUP_PASSWORD, "total")
								.contains("Error")
								&& !KeyValueAPI.get(
										Communication_Globals.BACKUP_USERNAME,
										Communication_Globals.BACKUP_PASSWORD,
										"total").contains("ERROR")) {
							total = Integer.parseInt(KeyValueAPI.get(
									Communication_Globals.BACKUP_USERNAME,
									Communication_Globals.BACKUP_PASSWORD,
									"total"));
						}
						String getString;
						getString = KeyValueAPI.get(
								Communication_Globals.BACKUP_USERNAME,
								Communication_Globals.BACKUP_PASSWORD, IMEI);
						if (getString.contains("Error")
								|| getString.contains("ERROR")) {
							KeyValueAPI.put(
									Communication_Globals.BACKUP_USERNAME,
									Communication_Globals.BACKUP_PASSWORD,
									"total", String.valueOf(total + 1));
							KeyValueAPI.put(
									Communication_Globals.BACKUP_USERNAME,
									Communication_Globals.BACKUP_PASSWORD,
									"regid" + String.valueOf(total + 1), regid);
							KeyValueAPI.put(
									Communication_Globals.BACKUP_USERNAME,
									Communication_Globals.BACKUP_PASSWORD,
									IMEI, regid);
						}
						msg = "Device registered, registration ID=" + regid;
					} else {
						msg = "Error :" + "Backup Server is not available";
						return msg;
					}
					sendRegistrationIdToBackend();
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Network is not available!";
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				mDisplay.setText(msg + "\n");
			}
		}.execute(null, null, null);
	}

	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	public void onClick(final View view) {
		Log.d("click", "am I click?");
		if (view == findViewById(R.id.communication_send)) {
			String message = ((EditText) findViewById(R.id.communication_edit_message))
					.getText().toString();
			if (message != "") {
				sendMessage(message);
			} else {
				Toast.makeText(context, "Sending Context Empty!",
						Toast.LENGTH_LONG).show();
			}
		} else if (view == findViewById(R.id.communication_clear)) {
			mMessage.setText("");
			mUsername.setText("");
		} else if (view == findViewById(R.id.communication_unregistor_button)) {
			if (!TextUtils.isEmpty(regid))
				unregister();
		} else if (view == findViewById(R.id.communication_registor_button)) {
			if (checkPlayServices()) {
				regid = getRegistrationId(context);
				if (TextUtils.isEmpty(regid)) {
					registerInBackground();
				}
			}
		} else if (view == findViewById(R.id.communication_acknowledge)) {
			Intent communication_acknowledge = new Intent(this,
					Communication_Acknowledge.class);
			startActivity(communication_acknowledge);
		}

	}

	private void unregister() {
		Log.d(Communication_Globals.TAG, "UNREGISTER USERID: " + regid);
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					msg = "Sent unregistration";
					KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, "alertText",
							"Notification");
					KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, "titleText",
							"Unregister");
					KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD,
							"contentText", "Unregistering Successful!");
					KeyValueAPI.clearKey(Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, IMEI);
					gcm.unregister();
				} catch (IOException ex) {
					msg = "Network not available!";
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				removeRegistrationId(getApplicationContext());
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				mDisplay.setText(regid);
				mMessage.setText("");
			}
		}.execute();
	}

	private void removeRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(Communication_Globals.TAG, "Removig regId on app version "
				+ appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PROPERTY_REG_ID);
		editor.commit();
		regid = null;
	}

	@SuppressLint("NewApi")
	private void sendMessage(final String message) {
		if (regid == null || regid.equals("")) {
			Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (message.isEmpty()) {
			Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();
			return;
		}

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				if (!isConnectingToInternet()) {
					return "Network is not available";
				}
				int total = 0;
				if (!KeyValueAPI.get(Communication_Globals.BACKUP_USERNAME,
						Communication_Globals.BACKUP_PASSWORD, "total")
						.contains("Error")
						&& !KeyValueAPI.get(
								Communication_Globals.BACKUP_USERNAME,
								Communication_Globals.BACKUP_PASSWORD, "total")
								.contains("ERROR")) {
					try {
						total = Integer.parseInt(KeyValueAPI.get(
								Communication_Globals.BACKUP_USERNAME,
								Communication_Globals.BACKUP_PASSWORD, "total"));
					} catch (NumberFormatException e) {
						msg = "Backup Server Error!";
						return msg;
					}
					
				} else {
					msg = KeyValueAPI.get(
							Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, "total");
					return msg;
				}
				String reg_device = regid;
				int nIcon = R.drawable.ic_stat_cloud;
				int nType = Communication_Globals.SIMPLE_NOTIFICATION;
				Map<String, String> msgParams;
				msgParams = new HashMap<String, String>();
				msgParams.put("data.alertText", "Notification");
				msgParams.put("data.titleText", "Notification Title");
				msgParams.put("data.contentText", message);
				msgParams.put("data.nIcon", String.valueOf(nIcon));
				msgParams.put("data.nType", String.valueOf(nType));
				KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
						Communication_Globals.BACKUP_PASSWORD, "alertText",
						"Message Notification");
				KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
						Communication_Globals.BACKUP_PASSWORD, "titleText",
						"Sending Message");
				KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
						Communication_Globals.BACKUP_PASSWORD, "contentText",
						message);
				KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
						Communication_Globals.BACKUP_PASSWORD, "Sender_IMEI",
						IMEI);
				KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
						Communication_Globals.BACKUP_PASSWORD, "nIcon",
						String.valueOf(nIcon));
				KeyValueAPI.put(Communication_Globals.BACKUP_USERNAME,
						Communication_Globals.BACKUP_PASSWORD, "nType",
						String.valueOf(nType));
				GcmNotification gcmNotification = new GcmNotification();
				if (mUsername.getText().toString().isEmpty()) {
					reg_device = KeyValueAPI.get(
							Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, IMEI);
					gcmNotification
							.sendNotification(
									msgParams,
									reg_device,
									edu.neu.madcourse.bojunpan.communication.Communication_Main.this);
				} else {
					reg_device = KeyValueAPI.get(
							Communication_Globals.BACKUP_USERNAME,
							Communication_Globals.BACKUP_PASSWORD, mUsername
									.getText().toString());
					if(reg_device.contains("No"))
						return "IMEI not found!!";
					gcmNotification
							.sendNotification(
									msgParams,
									reg_device,
									edu.neu.madcourse.bojunpan.communication.Communication_Main.this);
				}
				msg = "sending information...";
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}.execute(null, null, null);
	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}
}
