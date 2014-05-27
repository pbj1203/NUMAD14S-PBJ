package edu.neu.madcourse.bojunpan.finalproject;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;

import edu.neu.madcourse.bojunpan.About_Main;
import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.trickiestpart.ShakeListener;
import edu.neu.madcourse.bojunpan.trickiestpart.Trickiest_Part_Global;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorEventListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.madcourse.bojunpan.finalproject.ShakeHandsListener.OnShakeHandsListener;
import edu.neu.madcourse.bojunpan.finalproject.OrientationListener.OnOrientationListener;
import edu.neu.madcourse.bojunpan.finalproject.TurnOverListener.OnTurnOverListener;

public class Final_Project_Tutorial extends Activity {
	private ViewPager viewPager;
	private ViewGroup pageNumGroup;
	WebView webView01;
	WebView webView02;
	WebView webView03;
	View v1;
	View v2;
	View v3;
	View v4;
	Button tutorial_start;
	Button tutorial_main;
	ShakeHandsListener mShakeHandsListener = null;
	TurnOverListener mTurnOverListener = null;
	OrientationListener mOrientationListener = null;
	private ArrayList<View> pageViews;
	private ImageView[] imageViews;
	private ImageView imageView;
	private int current_page = 0;
	private PopupWindow mPopupWindow;
	private int count = 0;
	SharedPreferences preferences;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		preferences = getSharedPreferences("count",MODE_WORLD_READABLE);
//        count = preferences.getInt("count", 0);
		count = 0;
        if(count == 0) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
    		setContentView(R.layout.final_project_tutorial);
    		if(!isConnectingToInternet())
    		{
    			show_toast("Internet is not available, try to connect");
    		}
    		Final_Project_Global.game_status = -1;
    		Final_Project_Global.is_tutorial = true;
    		viewPager = (ViewPager) this.findViewById(R.id.final_guidePages);
    		pageNumGroup = (ViewGroup) this.findViewById(R.id.final_pageNumGroup);
    		LayoutInflater inflater = getLayoutInflater();
    		pageViews = new ArrayList<View>();
    		pageViews.add(inflater.inflate(R.layout.final_item05, null));
    		pageViews.add(inflater.inflate(R.layout.final_item06, null));
    		pageViews.add(inflater.inflate(R.layout.final_item08, null));
    		pageViews.add(inflater.inflate(R.layout.final_item09, null));
    		v1 = inflater.inflate(R.layout.final_item01, null);
    		webView01 = (WebView) v1.findViewById(R.id.final_project_view01);
    		if (Build.VERSION.SDK_INT >= 11) {
    			webView01.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    		}
    		webView01.getSettings().setJavaScriptEnabled(true);
    		webView01.setWebViewClient(new WebViewClient());
    		webView01.setWebChromeClient(new MyWebChromeClient());
    		webView01.loadUrl("file:///android_asset/final_tutorial_shaking.html");
    		webView01.requestFocus();
    		mShakeHandsListener = new ShakeHandsListener(
    				Final_Project_Tutorial.this);
    		mShakeHandsListener.setOnShakeHandsListener(new OnShakeHandsListener() {
    			@Override
    			public void onShake() {
    				// TODO Auto-generated method stub
    				if (current_page == 4) {
    					mShakeHandsListener.stop();
    					new Handler().postDelayed(new Runnable() {
    						@Override
    						public void run() {
    							webView01.loadUrl("javascript:setPano2link()");
    							mShakeHandsListener.start();
    							Toast.makeText(Final_Project_Tutorial.this,
    									"Task1 Finish!", Toast.LENGTH_SHORT).show();
    						}
    					}, 100);
    				}
    			}
    		});
    		pageViews.add(v1);
    		v2 = inflater.inflate(R.layout.final_item02, null);
    		webView02 = (WebView) v2.findViewById(R.id.final_project_view02);
    		if (Build.VERSION.SDK_INT >= 11) {
    			webView02.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    		}
    		webView02.getSettings().setJavaScriptEnabled(true);
    		webView02.setWebViewClient(new WebViewClient());
    		webView02.setWebChromeClient(new MyWebChromeClient());
    		webView02.addJavascriptInterface(new IJavascriptHandler(), "cpjs");
    		webView02
    				.loadUrl("file:///android_asset/final_tutorial_orientation.html");
    		webView02.requestFocus();
    		mOrientationListener = new OrientationListener(
    				Final_Project_Tutorial.this);
    		mOrientationListener.start();
    		mOrientationListener
    				.setOnOrientationListener(new OnOrientationListener() {
    					@Override
    					public void onOrientation() {
    						// TODO Auto-generated method stub
    						if (current_page == 5) {
    							Handle_Orientation();
    						}
    					}
    				});
    		pageViews.add(v2);
    		v3 = inflater.inflate(R.layout.final_item03, null);    		
    		webView03 = (WebView) v3.findViewById(R.id.final_project_view03);
    		if (Build.VERSION.SDK_INT >= 11) {
    			webView03.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    		}
    		webView03.getSettings().setJavaScriptEnabled(true);
    		webView03.setWebViewClient(new WebViewClient());
    		webView03.setWebChromeClient(new MyWebChromeClient());
    		webView03
    				.loadUrl("file:///android_asset/final_tutorial_answerquestions.html");
    		webView03.requestFocus();
    		mTurnOverListener = new TurnOverListener(Final_Project_Tutorial.this);
    		mTurnOverListener.setOnTurnOverListener(new OnTurnOverListener() {
    			@Override
    			public void onTurnOver() {
    				// TODO Auto-generated method stub
    				if (current_page == 6) {
    					show_questions();
    					Toast.makeText(Final_Project_Tutorial.this,
    							"Task3 Finish!", Toast.LENGTH_SHORT).show();
    				}
    			}
    		});
    		pageViews.add(v3);
    		v4 = inflater.inflate(R.layout.final_item04, null);
    		tutorial_main = (Button) v4.findViewById(R.id.final_tutorial_main_menu);
    		tutorial_start = (Button) v4.findViewById(R.id.final_tutorial_start);
    		pageViews.add(v4);
    		imageViews = new ImageView[pageViews.size()];
    		Log.d("PageView.size", String.valueOf(pageViews.size()));
    		for (int i = 0; i < pageViews.size(); i++) {
    			imageView = new ImageView(Final_Project_Tutorial.this);
    			imageView.setLayoutParams(new LayoutParams(20, 20));
    			imageView.setPadding(20, 0, 20, 0);
    			imageViews[i] = imageView;
    			if (i == 0) {
    				imageViews[i]
    						.setBackgroundResource(R.drawable.page_indicator_focused);
    			} else {
    				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
    			}
    			pageNumGroup.addView(imageViews[i]);
    			if(!isConnectingToInternet())
        		{
        			show_toast("Internet is not available, try to connect");
        		}
    		}
    		viewPager.setAdapter(new GuidePageAdapter());
    		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
//			Editor editor = preferences.edit();
//	        editor.putInt("count", ++count);
//	        editor.commit();
		}
        else 
        {
        	Intent finalproject_button = new Intent(this, Final_Project_Main.class);
			startActivity(finalproject_button);
			this.finish();
        }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class GuidePageAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container).addView(pageViews.get(position));
			return pageViews.get(position);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	private void Handle_Orientation() {
		if (Final_Project_Global.Yaw != 0 || Final_Project_Global.pitch != 0) {
			final String call = "javascript:handlesensor("
					+ Final_Project_Global.Yaw + ","
					+ Final_Project_Global.pitch + ")";
			webView02.loadUrl(call);
		}
	}
	
	private void show_questions() {
		if(!isConnectingToInternet())
		{
			show_toast("Internet is not available, try to connect");
		}
		Intent intent = new Intent(this, Final_Project_Choose_List.class);
		startActivity(intent);
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			current_page = arg0;
			Log.d("current page", String.valueOf(current_page));
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0]
						.setBackgroundResource(R.drawable.page_indicator_focused);

				if (arg0 != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.page_indicator);
				}
			}
			Timer timer = new Timer();
			timer.schedule(new initPopupWindow(), 100);
		}
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4:
				showtask1();
				break;
			case 5:
				showtask2();
				break;
			case 6:
				showtask3();
				break;
			}
		};
	};

	private class initPopupWindow extends TimerTask {
		@Override
		public void run() {

			Message message = new Message();
			message.what = current_page;
			mHandler.sendMessage(message);
		}
	}

	private void showtask1() {
		View popupView = getLayoutInflater().inflate(
				R.layout.final_popup_task1, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		mPopupWindow.showAtLocation(findViewById(R.id.final_project_view01),
				Gravity.CENTER, 0, 0);
	}

	private void showtask2() {
		View popupView = getLayoutInflater().inflate(
				R.layout.final_popup_task2, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		mPopupWindow.showAtLocation(findViewById(R.id.final_project_view02),
				Gravity.CENTER, 0, 0);
	}

	private void showtask3() {
		View popupView = getLayoutInflater().inflate(
				R.layout.final_popup_task3, null);
		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		mPopupWindow.showAtLocation(findViewById(R.id.final_project_view03),
				Gravity.CENTER, 0, 0);
	}

	public void start_click(View v) {
		Final_Project_Global.last_time = System.currentTimeMillis();
		Intent mainintent = new Intent(this, Final_Project_Main.class);
		startActivity(mainintent);
		Intent startintent = new Intent(this, Final_Project_NewGame.class);
		startActivity(startintent);
		finish();
	}

	public void mainmenu_click(View v) {
		Intent intent = new Intent(this, Final_Project_Main.class);
		startActivity(intent);
		finish();
	}

	final class IJavascriptHandler {
		IJavascriptHandler() {
		}
		@JavascriptInterface
		public void sendToAndroid(String text) {
			// this is called from JS with passed value
			Toast t = Toast.makeText(getApplicationContext(), text, 2000);
			t.show();
		}
	}
	private void show_toast(String answer) {
		Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTurnOverListener.stop();
		mShakeHandsListener.stop();
		mOrientationListener.stop();
	}
}
