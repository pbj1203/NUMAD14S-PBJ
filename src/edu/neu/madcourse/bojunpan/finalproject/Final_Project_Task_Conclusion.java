package edu.neu.madcourse.bojunpan.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import edu.neu.madcourse.bojunpan.R;

public class Final_Project_Task_Conclusion extends Activity{
	private int count = 0;
	SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_project_task3_finish);
	}
	public void buttonclick(View v) {
		finish();
		preferences = getSharedPreferences("tutorial_count",MODE_WORLD_READABLE);
        count = preferences.getInt("tutorial_count", 0);
		Editor editor = preferences.edit();
		editor.putInt("tutorial_count", ++count);
		editor.commit();
		Intent intent = new Intent(this,
				Final_Project_Main.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
