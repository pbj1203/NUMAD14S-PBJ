package edu.neu.madcourse.bojunpan.finalproject;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.bojunpan.R;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Final_Project_Task_NewPlace extends Activity {
	WebView webView02;
	private PopupWindow mPopupWindow;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.final_item11);
		WriteTxtFile("48.856006,2.2980720000000474",
				"/sdcard/achievement.txt");
		webView02 = (WebView) findViewById(R.id.final_project_view11);
		if (Build.VERSION.SDK_INT >= 11) {
			webView02.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		if (!isConnectingToInternet()) {
			show_toast("Internet is not available, try to connect");
		}
		webView02.getSettings().setJavaScriptEnabled(true);
		webView02.setWebViewClient(new WebViewClient());
		webView02.setWebChromeClient(new MyWebChromeClient());
		webView02.addJavascriptInterface(new IJavascriptHandler(), "cpjs");
		webView02.loadUrl("file:///android_asset/final_tutorial_newplace.html");
		webView02.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new initPopupWindow(), 300);
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
				R.layout.final_popup_start_guess, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		findViewById(R.id.final_project_view11).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPopupWindow.showAtLocation(
						findViewById(R.id.final_project_view11),
						Gravity.CENTER, 0, 0);
			}
		});
	}

	final class IJavascriptHandler {
		IJavascriptHandler() {
		}

		@JavascriptInterface
		public void taskfinish() {
			finish();
			Intent intent = new Intent(getApplicationContext(),
					Final_Project_Task_Choice.class);
			startActivity(intent);
		}
	}

	private void WriteTxtFile(String strcontent, String strFilePath) {
		String strContent = strcontent + "\n";
		try {
			File file = new File(strFilePath);
			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + strFilePath);
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			Log.e("TestFile", "Error on write File.");
		}
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
		Toast.makeText(this, answer, Toast.LENGTH_LONG).show();
	}
}
