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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class Final_Project_PictureTutorial extends Activity {
	private ViewPager viewPager;
	private ViewGroup pageNumGroup;
	private ArrayList<View> pageViews;
	private ImageView[] imageViews;
	private ImageView imageView;
	private Button start_task;
	private int count = 0;
	private int current_page = 0;
	View v;
	SharedPreferences preferences;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getSharedPreferences("count",MODE_WORLD_READABLE);
        count = preferences.getInt("count", 0);
		count = 0;
        if(count == 0) {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
    		setContentView(R.layout.final_project_tutorial);
    		Final_Project_Global.game_status = -1;
    		Final_Project_Global.is_tutorial = true;
    		viewPager = (ViewPager) this.findViewById(R.id.final_guidePages);
    		pageNumGroup = (ViewGroup) this.findViewById(R.id.final_pageNumGroup);
    		LayoutInflater inflater = getLayoutInflater();
    		pageViews = new ArrayList<View>();
    		pageViews.add(inflater.inflate(R.layout.final_item05, null));
    		pageViews.add(inflater.inflate(R.layout.final_item08, null));
    		pageViews.add(inflater.inflate(R.layout.final_item09, null));
    		pageViews.add(inflater.inflate(R.layout.final_item10, null));
    		v = inflater.inflate(R.layout.final_project_tutorial_to_task, null);
    		start_task = (Button)v.findViewById(R.id.final_project_task_start_button);
    		pageViews.add(v);
//    		v = inflater.inflate(R.layout.final_item01, null);
//    		start_task = (Button) v.findViewById(R.id.final_project_start_task);
//    		pageViews.add(v);
    		imageViews = new ImageView[pageViews.size()];
    		Log.d("PageView.size", String.valueOf(pageViews.size()));
    		for (int i = 0; i < pageViews.size(); i++) {
    			imageView = new ImageView(Final_Project_PictureTutorial.this);
    			imageView.setLayoutParams(new LayoutParams(20, 20));
    			imageView.setPadding(20, 0, 20, 0);
    			imageViews[i] = imageView;
    			if (i == 0) {
    				imageViews[i]
    						.setBackgroundResource(R.drawable.page_indicator_focused2);
    			} else {
    				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
    			}
    			pageNumGroup.addView(imageViews[i]);
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
						.setBackgroundResource(R.drawable.page_indicator_focused2);

				if (arg0 != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.page_indicator);
				}
			}
		}
	}
	public void buttonclick(View v) {
		finish();
		Intent intent = new Intent(this,
				Final_Project_Task_Shaking.class);
		startActivity(intent);
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
