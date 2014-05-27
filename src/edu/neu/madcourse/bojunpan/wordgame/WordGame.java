package edu.neu.madcourse.bojunpan.wordgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.sudoku.Game;
import edu.neu.madcourse.bojunpan.sudoku.Prefs;

public class WordGame extends Activity implements OnClickListener {
	WordGame_Values values;
	Button continueButton;
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!values.continue_button_flag) {
			continueButton.setVisibility(View.INVISIBLE);
			//continueButton.setEnabled(false);
			continueButton.invalidate();
			continueButton.refreshDrawableState();
		} else {
			//continueButton.setEnabled(true);
			continueButton.setVisibility(View.VISIBLE);
			continueButton.invalidate();
			continueButton.refreshDrawableState();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letris_main);
		continueButton = (Button) findViewById(R.id.letris_continue_button);
		continueButton.setOnClickListener(this);
		Log.d("wordgame.java", String.valueOf(values.continue_button_flag));
		View newButton = findViewById(R.id.letris_new_button);
		newButton.setOnClickListener(this);
		View ranklistButton = findViewById(R.id.letris_instruction);
		ranklistButton.setOnClickListener(this);
		View aboutButton = findViewById(R.id.letris_about_button);
		aboutButton.setOnClickListener(this);
		View acknowledgeButton = findViewById(R.id.letris_acknowledge_button);
		acknowledgeButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.letris_exit_button);
		exitButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.letris_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.letris_settings:
			startActivity(new Intent(this, WordGame_Prefs.class));
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.letris_continue_button:
			startGame(WordGame_Game.DIFFICULTY_CONTINUE);
			break;
		case R.id.letris_new_button:
			openNewGameDialog();
			break;
		case R.id.letris_instruction:
			Intent letris_instruction = new Intent(this, WordGame_Instruction.class);
			startActivity(letris_instruction);
			break;
		case R.id.letris_about_button:
			Intent letris_about_button = new Intent(this, WordGame_About.class);
			startActivity(letris_about_button);
			break;
		case R.id.letris_acknowledge_button:
			Intent letris_acknowledge_button = new Intent(this,
					WordGame_Acknowledge.class);
			startActivity(letris_acknowledge_button);
			break;
		case R.id.letris_exit_button:
			finish();
			break;
		}
	}

	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.new_game_title)
				.setItems(R.array.difficulty,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								startGame(i);
							}
						}).show();
	}

	/** Start a new game with the given difficulty level */
	private void startGame(int i) {
		Intent intent = new Intent(this, WordGame_Game.class);
		intent.putExtra(WordGame_Game.KEY_DIFFICULTY, i);
		startActivity(intent);
	}
}
