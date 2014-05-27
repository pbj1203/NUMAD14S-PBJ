package edu.neu.madcourse.bojunpan.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import edu.neu.madcourse.bojunpan.MainActivity;
import edu.neu.madcourse.bojunpan.R;

public class Final_Project_Main extends Activity implements OnClickListener{

	ImageButton button_about;
	ImageButton button_music;
	Button button_start;
	Button button_quit;
	Button button_achievement;
	Button button_tutorial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_project_main);
		button_about = (ImageButton)findViewById(R.id.final_project_main_about);
		button_about.setOnClickListener(this);
		button_achievement = (Button)findViewById(R.id.final_project_main_achievement);
		button_achievement.setOnClickListener(this);
		button_music = (ImageButton)findViewById(R.id.final_project_main_music);
		button_tutorial = (Button)findViewById(R.id.final_project_main_tutorial);
		button_tutorial.setOnClickListener(this);
		if(Final_Project_Global.music_on_off)
		{
			button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_on));
		}
		else
		{
			button_music.setImageDrawable(getResources().getDrawable(R.drawable.final_main_music_off));
		}
		button_music.setOnClickListener(this);
		button_quit = (Button)findViewById(R.id.final_project_main_quit);
		button_quit.setOnClickListener(this);
		button_start = (Button)findViewById(R.id.final_project_main_start);
		button_start.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.final_project_main_about:
			Intent about_button = new Intent(this, Final_Project_About.class);
			startActivity(about_button);
			break;
		case R.id.final_project_main_start:
			Intent start_button = new Intent(this,Final_Project_GameMode.class);
			startActivity(start_button);
			break;
		case R.id.final_project_main_quit:
			finish();
//			Intent quit_button = new Intent(this,MainActivity.class);
//			quit_button.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(quit_button);
			break;
		case R.id.final_project_main_music:
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
		case R.id.final_project_main_achievement:
			Intent achievement_button = new Intent(this, Final_Project_Achievement.class);
			startActivity(achievement_button);
			break;
		case R.id.final_project_main_tutorial:
			//finish();
			Final_Project_Global.is_tutorial = true;
			Intent tutorial_button = new Intent(this, Final_Project_Task_Start_Slogan.class);
			tutorial_button.setFlags(tutorial_button.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(tutorial_button);
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
	      button_music = (ImageButton)findViewById(R.id.final_project_main_music);
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
