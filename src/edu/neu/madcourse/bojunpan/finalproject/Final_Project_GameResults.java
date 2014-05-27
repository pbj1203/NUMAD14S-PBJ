package edu.neu.madcourse.bojunpan.finalproject;

import java.util.prefs.Preferences;

import edu.neu.madcourse.bojunpan.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.Settings.Global;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Final_Project_GameResults extends Activity implements
		OnClickListener {
	Button button_return;
	Button button_restart;
	TextView current_time;
	TextView best_time;
	private boolean is_finished = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_game_results);
		long cur_time = System.currentTimeMillis();
		long time_diff = cur_time - Final_Project_Global.last_time;
		time_diff += Final_Project_Global.cur_time;
		Final_Project_Global.hour = time_diff / 3600000;
		Final_Project_Global.second = (time_diff % 60000) / 1000;
		Final_Project_Global.min = (time_diff - Final_Project_Global.hour * 3600000) / 60000;
		button_return = (Button) findViewById(R.id.final_project_gameover_return);
		button_return.setOnClickListener(this);
		button_restart = (Button) findViewById(R.id.final_project_gameover_replay);
		button_restart.setOnClickListener(this);
		current_time = (TextView) findViewById(R.id.final_project_gameover_current_time);
		String curtime = "       Current Time:   "
				+ ((Final_Project_Global.hour < 10) ? ("0" + Final_Project_Global.hour)
						: Final_Project_Global.hour)
				+ ":"
				+ ((Final_Project_Global.min < 10) ? ("0" + Final_Project_Global.min)
						: Final_Project_Global.min)
				+ ":"
				+ ((Final_Project_Global.second < 10) ? ("0" + Final_Project_Global.second)
						: Final_Project_Global.second);
		String cur_time_String = ((Final_Project_Global.hour < 10) ? ("0" + Final_Project_Global.hour)
				: Final_Project_Global.hour)
		+ ":"
		+ ((Final_Project_Global.min < 10) ? ("0" + Final_Project_Global.min)
				: Final_Project_Global.min)
		+ ":"
		+ ((Final_Project_Global.second < 10) ? ("0" + Final_Project_Global.second)
				: Final_Project_Global.second);
		SharedPreferences sp;
		String best;
		sp = getSharedPreferences("KJ", MODE_WORLD_READABLE);
		best = sp.getString("BEST", "99:99:99");
		if(best.compareTo(cur_time_String) > 0) {
			best = cur_time_String;
		}
		Editor editor = sp.edit();
        editor.putString("BEST", best);
        editor.commit();
		current_time.setText(curtime);
		best_time = (TextView) findViewById(R.id.final_project_gameover_best_time);
		String besttime = "       Best:                  " + best;
		best_time.setText(besttime);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.final_project_gameover_replay:
			Final_Project_Global.hint_numb = 1;
			Final_Project_Global.cur_time = 0;
			Final_Project_Global.last_time = System.currentTimeMillis();
			finish();
			Final_Project_Global.game_status = -1;
			Intent restart_button = new Intent(this,
					Final_Project_NewGame.class);
			restart_button.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(restart_button);
			break;
		case R.id.final_project_gameover_return:
			finish();
			Intent return_button = new Intent(this, Final_Project_Main.class);
			return_button.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(return_button);
//			is_finished = true;
			break;
		}
	}
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		if(!is_finished){
//		Intent return_button = new Intent(this, Final_Project_Main.class);
//		return_button.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(return_button);
//		}
//	}
}
