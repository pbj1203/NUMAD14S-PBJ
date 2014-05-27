package edu.neu.madcourse.bojunpan.finalproject;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
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
import edu.neu.madcourse.bojunpan.finalproject.Final_Project_Task_NewPlace.IJavascriptHandler;

public class Final_Project_Task_Hint extends Activity {
	WebView webView02;
	private PopupWindow mPopupWindow;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.final_item12);
		webView02 = (WebView) findViewById(R.id.final_project_view12);
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
		webView02.loadUrl("file:///android_asset/final_tutorial_hint.html");
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
		View popupView = getLayoutInflater().inflate(R.layout.final_popup_hint,
				null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		findViewById(R.id.final_project_view12).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPopupWindow.showAtLocation(
						findViewById(R.id.final_project_view12),
						Gravity.CENTER, 0, 0);
			}
		});
	}

	final class IJavascriptHandler {
		IJavascriptHandler() {
		}

		@JavascriptInterface
		public void taskfinish() {
			show_answer("You maybe in one of these contries: \n"
					+ "      New Zealand\n" + "      France\n" + "      Germany");
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

	private void show_answer(String answer) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.final_game_right_wrong, null);
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				Final_Project_Task_Hint.this);
		dialog.setView(view);
		dialog.setTitle("Hint")
				.setMessage(answer)
				.setPositiveButton("Got it",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								finish();
								start_another_activity();
							}
						}).show();
		// Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
	}

	private void start_another_activity() {
		Intent intent = new Intent(this, Final_Project_Task_Hint_Results.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
