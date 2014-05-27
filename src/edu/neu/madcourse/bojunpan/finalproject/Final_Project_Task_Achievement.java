package edu.neu.madcourse.bojunpan.finalproject;

import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.EncodingUtils;

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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.Toast;
import edu.neu.madcourse.bojunpan.R;

public class Final_Project_Task_Achievement extends Activity{
	WebView webView02;
	private PopupWindow mPopupWindow;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_item13);
		webView02 = (WebView) findViewById(R.id.final_project_view13);
		if (Build.VERSION.SDK_INT >= 11) {
			webView02.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		if (!isConnectingToInternet()) {
			show_toast("Internet is not available, try to connect");
		}
		webView02.getSettings().setJavaScriptEnabled(true);
		webView02.setWebViewClient(new WebViewClient());
		webView02.setWebChromeClient(new MyWebChromeClient());
		webView02.addJavascriptInterface(Final_Project_Task_Achievement.this,
				"AndroidFunction");
		webView02
				.loadUrl("file:///android_asset/final_tutorial_achievement.html");
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
				R.layout.final_popup_achievements, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		findViewById(R.id.final_project_view13).post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPopupWindow.showAtLocation(findViewById(R.id.final_project_view13),
						Gravity.CENTER, 0, 0);	
			}
		});
	}

	@JavascriptInterface
	public String read_file() {
		String fileName = "/sdcard/achievement.txt";
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			Log.d("length", String.valueOf(length));
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@JavascriptInterface
	public void start_next() {
		finish();
		Intent intent = new Intent(this, Final_Project_Task_Conclusion.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
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
