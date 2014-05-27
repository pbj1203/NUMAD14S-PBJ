package edu.neu.madcourse.bojunpan.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import edu.neu.madcourse.bojunpan.R;

public class Final_Project_GameMode extends Activity implements OnClickListener{
	Button mode_easy;
	Button mode_hard;
	Button mode_crazy;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_project_main_mode);
		
		mode_easy = (Button)findViewById(R.id.final_project_main_mode_easy);
		mode_easy.setOnClickListener(this);
		mode_hard = (Button)findViewById(R.id.final_project_main_mode_hard);
		mode_hard.setOnClickListener(this);
		mode_crazy = (Button)findViewById(R.id.final_project_main_mode_crazy);
		mode_crazy.setOnClickListener(this);
		Final_Project_Global.cur_time = 0;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.final_project_main_mode_easy:
			Final_Project_Global.last_time = System.currentTimeMillis();
			Final_Project_Global.mode = 0;
			Intent mode_easy_button = new Intent(this, Final_Project_NewGame.class);
			mode_easy_button.setFlags(mode_easy_button.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mode_easy_button);
			break;
		case R.id.final_project_main_mode_hard:
			Final_Project_Global.last_time = System.currentTimeMillis();
			Final_Project_Global.mode = 1;
			Intent mode_hard_button = new Intent(this,Final_Project_NewGame.class);
			mode_hard_button.setFlags(mode_hard_button.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mode_hard_button);
			break;
		case R.id.final_project_main_mode_crazy:
			Final_Project_Global.last_time = System.currentTimeMillis();
			Final_Project_Global.mode = 2;
			Intent mode_crazy_button = new Intent(this,Final_Project_NewGame.class);
			mode_crazy_button.setFlags(mode_crazy_button.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mode_crazy_button);
			break;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Final_Project_Global.game_status = -1;
		Final_Project_Global.hint_numb = 1;
		Final_Project_Music.play(this, R.raw.final_project_music);
	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Final_Project_Music.stop(this);
	}

}
