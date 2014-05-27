package edu.neu.madcourse.bojunpan.finalproject;

import edu.neu.madcourse.bojunpan.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class Final_Project_Start_Screen extends Activity implements
		OnClickListener {
	Button startButton;
	private int tutorial_count = 0;
	SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_project_start_screen);
		startButton = (Button) findViewById(R.id.final_project_start_tutorial);
		startButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.final_project_start_tutorial:
			finish();
			preferences = getSharedPreferences("tutorial_count",MODE_WORLD_READABLE);
			tutorial_count = preferences.getInt("tutorial_count", 0);
	        if(tutorial_count == 0) {
				Intent start_button = new Intent(this, Final_Project_Task_Start_Slogan.class);
				startActivity(start_button);
	        }
	        else {
	        	Intent start_button = new Intent(this, Final_Project_Main.class);
				startActivity(start_button);
	        }
			break;
		}
	}
}