package edu.neu.madcourse.bojunpan.finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_Game;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard.Key;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Final_Project_Choose_List extends Activity {
	TextView textView;
	private ListView areaList;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_choose_list);
		Log.d("answer?", Final_Project_Global.choosed_country);
		textView = (TextView) findViewById(R.id.final_guess_area);
		if(Final_Project_Global.is_tutorial) {
			textView.setText("Choose Country");
		}
		else {
			textView.setText(Final_Project_Global.choose_mode[Final_Project_Global.mode]);
		}
		areaList = (ListView) findViewById(R.id.final_list_area);
		if (!isConnectingToInternet()) {
			show_toast("Internet is not available, try to connect");
		}
		if (Final_Project_Global.is_tutorial) {
			read_name("country_name.txt");
		}
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				Final_Project_Global.countryName);
		areaList.setAdapter(adapter);
		areaList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String key = Final_Project_Global.countryName.get(position);
				if (Final_Project_Global.is_tutorial == true) {
					if(key.equals("France")) {
						Final_Project_Global.list_is_touch = true;
						show_tutorial_answer("You are right! The country is " + "France.");
					}
					else {
						show_tutorial_wrong_answer("Wrong answer! Please Try again.");
					}
					return;
				}
				if (key.equals(Final_Project_Global.choosed_country)) {
					show_answer("You are right! The country is " + Final_Project_Global.choosed_country);
					Final_Project_Global.game_status += 1;
				} else {
					show_answer("Wrong answer! Correct answer: " + Final_Project_Global.choosed_country);
				}
				
			}
		});
	}

	private void start_another_activity() {
		Intent intent = new Intent(this, Final_Project_NewGame.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void show_tutorial_answer(String answer) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.final_game_right_wrong, null);
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				Final_Project_Choose_List.this);
		dialog.setView(view);
		dialog.setTitle("Guess Results")
				.setMessage(answer)
				.setPositiveButton("Continue Travel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								finish();
							}
						}).show();
	}
	private void show_tutorial_wrong_answer(String answer) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.final_game_right_wrong, null);
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				Final_Project_Choose_List.this);
		dialog.setView(view);
		dialog.setTitle("Guess Results")
				.setMessage(answer)
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								finish();
							}
						}).show();
	}
	
	private void show_answer(String answer) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.final_game_right_wrong, null);
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				Final_Project_Choose_List.this);
		dialog.setView(view);
		dialog.setTitle("Guess Results")
				.setMessage(answer)
				.setPositiveButton("Continue Travel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								finish();
								start_another_activity();
							}
						}).show();
		// Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
	}

	private void read_name(String filename) {
		Final_Project_Global.countryName.clear();
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(getResources()
					.getAssets().open(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				Final_Project_Global.countryName.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Final_Project_Music.stop(this);
	}

	protected void onResume() {
		super.onResume();
		if (!Final_Project_Global.is_tutorial) {
			Final_Project_Music.play(this, R.raw.final_project_music);
		}
	}
}