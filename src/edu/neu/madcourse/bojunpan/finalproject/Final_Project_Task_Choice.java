package edu.neu.madcourse.bojunpan.finalproject;

import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.finalproject.TurnOverListener.OnTurnOverListener;
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

public class Final_Project_Task_Choice extends Activity {
	WebView webView03;
	TurnOverListener mTurnOverListener = null;
	private PopupWindow mPopupWindow;
	private boolean flag = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_item03);
		Final_Project_Global.list_is_touch = false;
		Final_Project_Global.is_tutorial = true;
		if (!isConnectingToInternet()) {
			show_toast("Internet is not available, try to connect");
		}
		webView03 = (WebView) findViewById(R.id.final_project_view03);
		if (Build.VERSION.SDK_INT >= 11) {
			webView03.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		webView03.getSettings().setJavaScriptEnabled(true);
		webView03.setWebViewClient(new WebViewClient());
		webView03.setWebChromeClient(new MyWebChromeClient());
		webView03
				.loadUrl("file:///android_asset/final_tutorial_answerquestions.html");
		webView03.requestFocus();
		mTurnOverListener = new TurnOverListener(Final_Project_Task_Choice.this);
		mTurnOverListener.setOnTurnOverListener(new OnTurnOverListener() {
			@Override
			public void onTurnOver() {
				// TODO Auto-generated method stub
				show_questions();
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

	private void show_toast(String answer) {
		Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
	}

	private void show_questions() {
		Intent intent = new Intent(this, Final_Project_Choose_List.class);
		startActivity(intent);
	}

	private void showtask() {
		View popupView = getLayoutInflater().inflate(
				R.layout.final_popup_turn_over, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		findViewById(R.id.final_project_view03).post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPopupWindow.showAtLocation(findViewById(R.id.final_project_view03),
						Gravity.CENTER, 0, 0);	
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(Final_Project_Global.list_is_touch) {
			finish();
			Intent intent = new Intent(this, Final_Project_Task_Choice_Result.class);
			intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTurnOverListener.stop();
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
}
