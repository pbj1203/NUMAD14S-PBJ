package edu.neu.madcourse.bojunpan.finalproject;

import edu.neu.madcourse.bojunpan.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Final_Project_Task_Compass_Result extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_tutorial_compass_finish);
	}
	public void buttonclick(View v) {
		finish();
		Intent intent = new Intent(this,
				Final_Project_Task_Hint.class);
		startActivity(intent);
	}
}
