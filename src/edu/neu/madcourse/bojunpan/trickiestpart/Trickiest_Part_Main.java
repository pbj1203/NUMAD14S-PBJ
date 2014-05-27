package edu.neu.madcourse.bojunpan.trickiestpart;

import java.io.IOException;
import java.util.List;

import edu.neu.madcourse.bojunpan.trickiestpart.ShakeListener.OnShakeListener;
import edu.neu.madcourse.bojunpan.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Trickiest_Part_Main extends Activity implements
		SensorEventListener {
	ShakeListener mShakeListener = null;
	private SensorManager sensorManager;
	WebView webView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trickiestpart_main);
		webView = (WebView) findViewById(R.id.trickiest_part_view);
		if (Build.VERSION.SDK_INT >= 11) {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new WebChromeClient());
		webView.loadUrl("file:///android_asset/trickiest_part_streetview.html");
		webView.requestFocus();
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				mShakeListener.stop();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:setPano2link()");
						mShakeListener.start();
					}
				}, 100);
			}
		});
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	}

	private void registerSensor() {
		List<Sensor> sensors = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size() == 1) {
			sensorManager.registerListener((SensorEventListener) this,
					sensors.get(0), SensorManager.SENSOR_DELAY_UI);
		}
	}

	private void unregisterSensor() {
		sensorManager.unregisterListener(this);
	}

	@Override
	protected void onPause() {
		unregisterSensor();
		super.onPause();
	}

	@Override
	protected void onResume() {
		registerSensor();
		super.onResume();
	}

	private void handledata() {
		if (Trickiest_Part_Global.Yaw != 0 || Trickiest_Part_Global.pitch != 0) {
			final String call = "javascript:handlesensor("
					+ Trickiest_Part_Global.Yaw + ","
					+ Trickiest_Part_Global.pitch + ")";
			webView.loadUrl(call);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		Trickiest_Part_Global.Yaw = event.values[0];
		Trickiest_Part_Global.pitch = event.values[1];
		handledata();
	}

}
