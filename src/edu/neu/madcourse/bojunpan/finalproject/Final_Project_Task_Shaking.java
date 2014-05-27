package edu.neu.madcourse.bojunpan.finalproject;

import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.finalproject.ShakeHandsListener.OnShakeHandsListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.Toast;

public class Final_Project_Task_Shaking extends Activity {
	WebView webView01;
	ShakeHandsListener mShakeHandsListener = null;
	private PopupWindow mPopupWindow;
	private int cnt = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_item01);
		if (!isConnectingToInternet()) {
			show_toast("Internet is not available, try to connect");
		}
		webView01 = (WebView) findViewById(R.id.final_project_view01);
		if (Build.VERSION.SDK_INT >= 11) {
			webView01.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		webView01.getSettings().setJavaScriptEnabled(true);
		webView01.setWebViewClient(new WebViewClient());
		webView01.setWebChromeClient(new MyWebChromeClient());
		webView01.loadUrl("file:///android_asset/final_tutorial_shaking.html");
		webView01.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new initPopupWindow(), 300);
		mShakeHandsListener = new ShakeHandsListener(Final_Project_Task_Shaking.this);
		mShakeHandsListener.setOnShakeHandsListener(new OnShakeHandsListener() {
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				mShakeHandsListener.stop();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						webView01.loadUrl("javascript:setPano2link()");
						mShakeHandsListener.start();
						cnt++;
					}
				}, 100);
				if (cnt >= 3) {
					show_results();
				}
			}
		});

	}

	private void show_results() {
		finish();
		Intent intent = new Intent(this, Final_Project_Task_Shaking_Result.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

	private class initPopupWindow extends TimerTask {
		@Override
		public void run() {

			Message message = new Message();
			message.what = 0;
			mHandler.sendMessage(message);
		}
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				showtask();
				break;
			}
		};
	};

	private void showtask() {
		View popupView = getLayoutInflater().inflate(
				R.layout.final_popup_shaking, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		findViewById(R.id.final_project_view01).post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPopupWindow.showAtLocation(findViewById(R.id.final_project_view01),
						Gravity.CENTER, 0, 0);	
			}
		});
	}

	private boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	private void show_toast(String answer) {
		Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mShakeHandsListener.stop();
	}
}
