package edu.neu.madcourse.bojunpan.finalproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.model.LatLng;

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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.finalproject.OrientationListener.OnOrientationListener;
import edu.neu.madcourse.bojunpan.finalproject.ShakeHandsListener.OnShakeHandsListener;
import edu.neu.madcourse.bojunpan.finalproject.TurnOverListener.OnTurnOverListener;

public class Final_Project_NewGame extends Activity {
	WebView webView;
	ShakeHandsListener mShakeHandsListener = null;
	TurnOverListener mTurnOverListener = null;
	OrientationListener mOrientationListener = null;
	TextView game_status;
	private PopupWindow mPopupWindow;
	private String Lat;
	private String Lng;
	TextView marquee;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.final_project_newgame);
		Final_Project_Global.is_tutorial = false;
		Final_Project_Global.clean();
		if (!isConnectingToInternet()) {
			show_toast("Internet is not available, try to connect");
		}
		init();
		webView = (WebView) findViewById(R.id.final_project_newgame);
		marquee = (TextView) findViewById(R.id.marquee_text);
		marquee.setSelected(true);
		start_game();
		mShakeHandsListener = new ShakeHandsListener(Final_Project_NewGame.this);
		mShakeHandsListener.setOnShakeHandsListener(new OnShakeHandsListener() {
			@Override
			public void onShake() {
				// TODO Auto-generated method stub
				mShakeHandsListener.stop();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:setPano2link()");
						mShakeHandsListener.start();
					}
				}, 500);
			}
		});
		mOrientationListener = new OrientationListener(
				Final_Project_NewGame.this);
		mOrientationListener.start();
		mOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientation() {
						// TODO Auto-generated method stub
						Handle_Orientation();
					}
				});
		mTurnOverListener = new TurnOverListener(Final_Project_NewGame.this);
		mTurnOverListener.setOnTurnOverListener(new OnTurnOverListener() {
			@Override
			public void onTurnOver() {
				// TODO Auto-generated method stub
				Log.d("aaa", "aaaaaa");
				show_questions();
			}
		});
		if (Final_Project_Global.game_status == Final_Project_Global.game_target) {
			show_results();
		}
	}

	@SuppressLint("NewApi")
	private void start_game() {
		Final_Project_Global.generate_random_place();
		Lat = Final_Project_Global.choosed_address.split(",")[0];
		Lng = Final_Project_Global.choosed_address.split(",")[1];
		WriteTxtFile(Final_Project_Global.choosed_address,
				"/sdcard/achievement.txt");
		Log.d(Lat, Lng);
		if (Build.VERSION.SDK_INT >= 11) {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.loadUrl("file:///android_asset/final_game.html");
		webView.requestFocus();
		webView.addJavascriptInterface(Final_Project_NewGame.this,
				"AndroidFunction");
		Timer timer = new Timer();
		timer.schedule(new initPopupWindow(), 300);
	}

	private void Handle_Orientation() {
		if (Final_Project_Global.Yaw != 0 || Final_Project_Global.pitch != 0) {
			final String call = "javascript:handlesensor("
					+ Final_Project_Global.Yaw + ","
					+ Final_Project_Global.pitch + ")";
			webView.loadUrl(call);
		}
	}

	private void show_questions() {
		Intent intent = new Intent(this, Final_Project_Choose_List.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@JavascriptInterface
	public int get_hint_number() {
		return Final_Project_Global.hint_numb;
	}

	@JavascriptInterface
	public void pause_game() {
		Intent intent = new Intent(this, Final_Project_GamePause.class);
		startActivity(intent);
	}

	@JavascriptInterface
	public void game_hint() {
		ArrayList<String> list = new ArrayList<String>();
		int num_maxn = Final_Project_Global.countryAddress.size();
		int pos = (int) (Math.random() * num_maxn);
		while (!list.contains(Final_Project_Global.countryName.get(pos))
				&& !list.contains(Final_Project_Global.choosed_country)
				&& list.size() < 2) {
			list.add(Final_Project_Global.countryName.get(pos));
			pos = (int) (Math.random() * num_maxn);
		}
		list.add(Final_Project_Global.choosed_country);
		Collections.sort(list);
		// if (!list.contains(Final_Project_Global.countryName.get(pos))
		// && !list.contains(Final_Project_Global.choosed_country))
		// list.add(Final_Project_Global.countryName.get(pos));
		// num_maxn = Final_Project_Global.countryAddress.size();
		// pos = (int) (Math.random() * num_maxn);
		// if (!list.contains(Final_Project_Global.countryName.get(pos))
		// && !list.contains(Final_Project_Global.choosed_country))
		// list.add(Final_Project_Global.countryName.get(pos));
		// num_maxn = Final_Project_Global.countryAddress.size();
		// pos = (int) (Math.random() * num_maxn);
		// if (!list.contains(Final_Project_Global.countryName.get(pos))
		// && !list.contains(Final_Project_Global.choosed_country))
		// list.add(Final_Project_Global.countryName.get(pos));
		// num_maxn = Final_Project_Global.countryAddress.size();
		// pos = (int) (Math.random() * num_maxn);
		// if (!list.contains(Final_Project_Global.countryName.get(pos))
		// && !list.contains(Final_Project_Global.choosed_country))
		// list.add(Final_Project_Global.countryName.get(pos));

		show_hint_dialog("You maybe in one of these places: \n" + "      "
				+ list.get(0) + "\n      " + list.get(1) + "\n      "
				+ list.get(2));
		list.clear();
		Final_Project_Global.hint_numb--;
	}

	@JavascriptInterface
	public int GetHint() {
		return Final_Project_Global.hint_numb;
	}

	@JavascriptInterface
	public String GetLat() {
		return Lat;
	}

	@JavascriptInterface
	public String GetLon() {
		return Lng;
	}

	private void show_toast(String answer) {
		Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showtask();
				break;
			}
		};
	};

	private class initPopupWindow extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
		}
	}

	private void show_results() {
		Intent intent = new Intent(this, Final_Project_GameResults.class);
		startActivity(intent);
	}

	private void showtask() {
		View popupView = getLayoutInflater().inflate(R.layout.final_popup_game,
				null);
		game_status = (TextView) popupView
				.findViewById(R.id.final_popup_game_state);
		if (Final_Project_Global.game_status == -1) {
			game_status.setText("Try to guess where you are");
			Final_Project_Global.game_status = 0;
		} else {
			game_status.setText("New Travel "
					+ Final_Project_Global.game_status + "/"
					+ Final_Project_Global.game_target + " finished");
		}
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		findViewById(R.id.final_project_newgame).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPopupWindow.showAtLocation(
						findViewById(R.id.final_project_newgame),
						Gravity.CENTER, 0, 0);
			}
		});
	}

	private void show_hint_dialog(String answer) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.final_game_right_wrong, null);
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				Final_Project_NewGame.this);
		dialog.setView(view);
		dialog.setTitle("Hint")
				.setMessage(answer)
				.setPositiveButton("Got it",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).show();
		// Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
	}

	private void init() {
		if (Final_Project_Global.mode == 0) {
			read_name("country_name.txt");
			read_address("country_address.txt");
		} else if (Final_Project_Global.mode == 1) {
			read_name("state_name.txt");
			read_address("state_address.txt");
		} else {
			read_name("city_name.txt");
			read_address("city_address.txt");
		}

	}

	private void read_name(String filename) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(getResources()
					.getAssets().open(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				Final_Project_Global.countryName.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void read_address(String filename) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(getResources()
					.getAssets().open(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				Final_Project_Global.countryAddress.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	public void newplace() {
		String call = "javascript:initialize("
				+ Final_Project_Global.choosed_address + ")";
		Log.d("Address?", Final_Project_Global.choosed_address);
		webView.loadUrl(call);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTurnOverListener.stop();
		mShakeHandsListener.stop();
		mOrientationListener.stop();
	}

	protected void onResume() {
		super.onResume();
		Final_Project_Music.play(this, R.raw.final_project_music);
		if (!isConnectingToInternet()) {
			show_toast("Internet is not available, try to connect");
		}

	}

	protected void onPause() {
		super.onPause();
		Final_Project_Music.stop(this);
	}
}
