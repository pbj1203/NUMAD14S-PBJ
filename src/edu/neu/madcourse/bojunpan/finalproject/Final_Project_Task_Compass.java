package edu.neu.madcourse.bojunpan.finalproject;

import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.finalproject.OrientationListener.OnOrientationListener;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Final_Project_Task_Compass extends Activity {
	WebView webView02;
	OrientationListener mOrientationListener = null;
	private PopupWindow mPopupWindow;
	private int cnt = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_item02);
		webView02 = (WebView) findViewById(R.id.final_project_view02);
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
		webView02
				.loadUrl("file:///android_asset/final_tutorial_orientation.html");
		webView02.requestFocus();
		mOrientationListener = new OrientationListener(Final_Project_Task_Compass.this);
		mOrientationListener.start();
		mOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientation() {
						// TODO Auto-generated method stub
						Handle_Orientation();
					}
				});
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

	private void Handle_Orientation() {
		if (Final_Project_Global.Yaw != 0 || Final_Project_Global.pitch != 0) {
			final String call = "javascript:handlesensor("
					+ Final_Project_Global.Yaw + ","
					+ Final_Project_Global.pitch + ")";
			webView02.loadUrl(call);
		}
	}

	private void showtask() {
		View popupView = getLayoutInflater().inflate(
				R.layout.final_popup_compass, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		findViewById(R.id.final_project_view02).post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPopupWindow.showAtLocation(findViewById(R.id.final_project_view02),
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
					Final_Project_Task_Compass_Result.class);
			startActivity(intent);
		}

		@JavascriptInterface
		public void sendToAndroid(String text) {
			// this is called from JS with passed value
			final Toast uniquet = Toast.makeText(getApplicationContext(), text,
					Toast.LENGTH_LONG);
			LinearLayout toastLayout = (LinearLayout) uniquet.getView();
			TextView toastTV = (TextView) toastLayout.getChildAt(0);
			toastTV.setTextSize(18);
			uniquet.setGravity(Gravity.CENTER, 0, 0);
			uniquet.show();
			new CountDownTimer(4000, 1000)
			{

			    public void onTick(long millisUntilFinished) {uniquet.show();}
			    public void onFinish() {uniquet.show();}

			}.start();
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mOrientationListener.stop();
	}
}
