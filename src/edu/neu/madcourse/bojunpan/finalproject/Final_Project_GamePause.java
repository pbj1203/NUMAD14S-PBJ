package edu.neu.madcourse.bojunpan.finalproject;

import edu.neu.madcourse.bojunpan.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class Final_Project_GamePause extends Activity implements OnClickListener{
	Button button_restart;
	Button button_return;
	ImageButton button_music;
	Button button_resume;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_pause_game);
		button_restart = (Button)findViewById(R.id.final_pause_restart);
		button_restart.setOnClickListener(this);
		button_return = (Button)findViewById(R.id.final_pause_return);
		button_return.setOnClickListener(this);
		button_music = (ImageButton)findViewById(R.id.final_pause_music);
		if(Final_Project_Global.music_on_off)
		{
			button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_on));
		}
		else
		{
			button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_off));
		}
		button_music.setOnClickListener(this);
		button_resume = (Button)findViewById(R.id.final_pause_resume);
		button_resume.setOnClickListener(this);
		long cur_time = System.currentTimeMillis();
		long time_diff = cur_time - Final_Project_Global.last_time;
		Final_Project_Global.cur_time += time_diff;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.final_pause_restart:
			Final_Project_Global.cur_time = 0;
			Final_Project_Global.last_time = System.currentTimeMillis();
			Final_Project_Global.hint_numb = 1;
			finish();
			Final_Project_Global.game_status = -1;
			Intent restart_button = new Intent(this, Final_Project_NewGame.class);
			restart_button.setFlags(restart_button.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(restart_button);
			break;
		case R.id.final_pause_return:
			finish();
			Intent return_button = new Intent(this, Final_Project_Main.class);
			return_button.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(return_button);
			break;
		case R.id.final_pause_music:
			if(Final_Project_Global.music_on_off)
			{
				button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_off));
				Final_Project_Global.music_on_off = false;
				Final_Project_Music.stop(this);
			}
			else
			{
				button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_on));
				Final_Project_Global.music_on_off =true;
				Final_Project_Music.play(this, R.raw.final_project_music);
			}
			break;
		case R.id.final_pause_resume:
			Final_Project_Global.last_time = System.currentTimeMillis();
			finish();
			break;
		}
	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Final_Project_Music.stop(this);
	}
	protected void onResume() {
	      super.onResume();
	      button_music = (ImageButton)findViewById(R.id.final_pause_music);
			if(Final_Project_Global.music_on_off)
			{
				button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_on));
			}
			else
			{
				button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_off));
			}
			button_music.setOnClickListener(this);
			Final_Project_Music.play(this, R.raw.final_project_music);
	      
	     
	   }
}
