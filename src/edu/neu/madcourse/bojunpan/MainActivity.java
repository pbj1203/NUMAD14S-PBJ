package edu.neu.madcourse.bojunpan;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.communication.Communication_Main;
import edu.neu.madcourse.bojunpan.dictionary.Dictionary;
import edu.neu.madcourse.bojunpan.finalproject.Final_Project_Main;
import edu.neu.madcourse.bojunpan.finalproject.Final_Project_Start_Screen;
import edu.neu.madcourse.bojunpan.finalproject.Final_Project_Tutorial;
import edu.neu.madcourse.bojunpan.sudoku.Sudoku;
import edu.neu.madcourse.bojunpan.trickiestpart.Trickiest_Part_Main;
import edu.neu.madcourse.bojunpan.twoplayergame.TwoPlayerGame_Main;
import edu.neu.madcourse.bojunpan.wordgame.WordGame;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class MainActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		View errorButton = findViewById(R.id.error_button);
		errorButton.setOnClickListener(this);
		View gameButton = findViewById(R.id.game_button);
		gameButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		View dictionaryButton = findViewById(R.id.dictionary_button);
		dictionaryButton.setOnClickListener(this);
		View letris_button = findViewById(R.id.letris_button);
		letris_button.setOnClickListener(this);
		View communication_button = findViewById(R.id.communication_button);
		communication_button.setOnClickListener(this);
		View twoplaygame_button = findViewById(R.id.twoplaygame_button);
		twoplaygame_button.setOnClickListener(this);
		View trickiestpart_button = findViewById(R.id.trickiest_part_button);
		trickiestpart_button.setOnClickListener(this);
		View finalproject_button = findViewById(R.id.final_project_button);
		finalproject_button.setOnClickListener(this);
	}
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.about_button:
			Intent about_button = new Intent(this, About_Main.class);
			startActivity(about_button);
			break;
		case R.id.error_button:
			Intent error_button = new Intent(this, GenerateError.class);
			startActivity(error_button);
			break;
		case R.id.game_button:
			Intent game_button = new Intent(this, Sudoku.class);
			startActivity(game_button);
			break; 
		case R.id.dictionary_button:
			Intent dictionary_button = new Intent(this, Dictionary.class);
			startActivity(dictionary_button);
			break;
		case R.id.letris_button:
			Intent letris_button = new Intent(this, WordGame.class);
			startActivity(letris_button);
			break;
		case R.id.communication_button:
			Intent communication_button = new Intent(this, Communication_Main.class);
			startActivity(communication_button);
			break;
		case R.id.twoplaygame_button:
			Intent twoplaygame_button = new Intent(this, TwoPlayerGame_Main.class);
			startActivity(twoplaygame_button);
			break;
		case R.id.trickiest_part_button:
			Intent trickiestpart_button = new Intent(this, Trickiest_Part_Main.class);
			startActivity(trickiestpart_button);
			break;
		case R.id.final_project_button:
			Intent finalproject_button = new Intent(this, Final_Project_Start_Screen.class);
			startActivity(finalproject_button);
			break;
		case R.id.exit_button:
			finish();
			break;
		}
		
	}
	

}