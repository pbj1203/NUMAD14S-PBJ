package edu.neu.madcourse.bojunpan.finalproject;

import edu.neu.madcourse.bojunpan.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Final_Project_Seperate_Icon extends Activity {
	private int count = 0;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.final_project_main);
		preferences = getSharedPreferences("tutorial_count",
				MODE_WORLD_READABLE);
		count = preferences.getInt("tutorial_count", 0);
		if (count == 0) {
			finish();
			Intent start_button = new Intent(this,
					Final_Project_Task_Start_Slogan.class);
			start_button.setFlags(start_button.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(start_button);
		}
		else {
			finish();
			Intent start_button = new Intent(this,
					Final_Project_Main.class);
			start_button.setFlags(start_button.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(start_button);
		}
	}
}
