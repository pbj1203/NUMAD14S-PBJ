package edu.neu.madcourse.bojunpan.wordgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.sudoku.Game;
import edu.neu.madcourse.bojunpan.sudoku.Music;
import edu.neu.madcourse.bojunpan.sudoku.PuzzleView;
import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard.Row;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WordGame_Game extends Activity {
	public ToneGenerator toneGenerator;
	private WeakReference<WordGame_Game> mainActivityWeakRef;
	private static final int DEFAULT_SIZE = 2 << 24;
	private static final int[] seeds = { 3, 5, 7, 11, 13, 31, 37, 61 };
	private static BitSet bits = new BitSet(DEFAULT_SIZE);
	private static SimpleHash[] func = new SimpleHash[seeds.length];
	public Vibrator vibrator=null;
	private static final String TAG = "WordGame_game";
	public static final String LEVEL = "edu.neu.madcourse.bojunpan.wordgame.level";
	public static final String continueButtonString = "edu.neu.madcourse.bojunpan.wordgame_game.continueButtonString";
	public static final String KEY_DIFFICULTY = "edu.neu.madcourse.bojunpan.wordgame.difficulty";
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	public int diff_choose;
	protected static final int DIFFICULTY_CONTINUE = -1;
	private WordGame_View wordGame_View;
	private WordGame wordGame;
	public String timeString;
	public String levelString;
	public String scoreString;
	public int time;
	public int level = 1;
	public int score = 0;
	public int count_x = 0;
	public int count_y = 0;
	public int count = 0;
	public int cur_y[] = new int[15];
	public int cnt = 0;
	public int last_score;
	public boolean visit;
	public boolean flag;
	Timer timer = new Timer();
	Timer dropTimer = new Timer();
	String wordString = new String();
	String index_name;
	public boolean[][] vis = new boolean[26][26];
	public int index;
	public boolean TONE_FLAG = false;
	private static int speed;
	public boolean win_flag = false;
	public int diff;
	public boolean is_continue;
	private WordGame_Values values;

	class SimpleHash {

		private int cap;
		private int seed;

		public SimpleHash(int cap, int seed) {
			this.cap = cap;
			this.seed = seed;
		}

		public int hash(String value) {
			int result = 0;
			int len = value.length();
			for (int i = 0; i < len; i++) {
				result = seed * result + value.charAt(i);
			}
			return (cap - 1) & result;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		this.mainActivityWeakRef = new WeakReference<WordGame_Game>(this);
		cnt = 0;
		
		flag = false;
		toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
				ToneGenerator.MAX_VOLUME);
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
		wordGame_View = new WordGame_View(this);
		setContentView(wordGame_View);
		for (int j = 0; j < wordGame_View.column; j++)
			cur_y[j] = wordGame_View.row;
		wordGame_View.requestFocus();
		diff = getIntent().getIntExtra(KEY_DIFFICULTY, diff);
		getPuzzle(diff);
		for (int i = 0; i < 1000; i++)
			wordString += generate_alphabet();
		timer.schedule(task, 1000, 1000);
		dropTimer.schedule(drop, 1000, speed);
		values.continue_button_flag = true;
	}

	public String generate_alphabet() {
		// TODO Auto-generated method stub
		double rnd = Math.random();
		char readomLetter = 0;
		if (rnd <= values.probability[diff][0])
			readomLetter = 'a';
		else {
			for (int i = 1; i < 26; i++)
				if (rnd > values.probability[diff][i - 1]
						&& rnd <= values.probability[diff][i])
					readomLetter = (char) (i + 'a');
		}
		return String.valueOf(readomLetter);
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					Log.d("task run", String.valueOf(time));
					if (!wordGame_View.is_stop) {
						time--;
						if (time == 5) {
							Music.stop(WordGame_Game.this);
							Music.play(WordGame_Game.this,
									R.raw.last_five_second);
						}
						if (time == 0) {
							flag = true;
							if (score >= 40 * level) {
								win_flag = true;
							}
							Log.d("?????????", "time over!!");
							showresults();
						}
					}
				}

			});
		}
	};

	TimerTask drop = new TimerTask() {
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					if (!wordGame_View.is_stop) {
						if (count == wordGame_View.column * wordGame_View.row) {
							count = 0;
							flag = true;
							if (score >= 40 * level) {
								win_flag = true;
							}
							Log.d("?????????", "grid full!!!");
							showresults();
						} else {
							if (cur_y[count_x] == 0) {
								count_x++;
								if(count_x == wordGame_View.column)
									count_x = 0;
								return;
							}
							count_y++;
							if (count_y == cur_y[count_x]) {
								count_y = 0;
								cur_y[count_x]--;
								count_x++;
								cnt++;
								count++;
								if (count_x == wordGame_View.column) {
									count_x = 0;
									count_y = 0;
								}
							}

						}
					}
				}
			});

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		if (!wordGame_View.is_stop) {
			if (time > 5)
				Music.play(this, R.raw.wordgame_music);
			else if (time >= 0 && time <= 5)
				Music.play(this, R.raw.last_five_second);
			else
				Music.stop(this);
		}
	}

	protected void continue_button() {
		wordGame_View.invalidate();
		cnt = getPreferences(MODE_PRIVATE).getInt("cnt", cnt);
		level = getPreferences(MODE_PRIVATE).getInt("level", level);
		time = getPreferences(MODE_PRIVATE).getInt("time", time);
		diff = getPreferences(MODE_PRIVATE).getInt("diff", diff);
		speed = getPreferences(MODE_PRIVATE).getInt("speed", speed);
		wordGame_View.row = getPreferences(MODE_PRIVATE).getInt("row",
				wordGame_View.row);
		wordGame_View.column = getPreferences(MODE_PRIVATE).getInt("column",
				wordGame_View.column);
		wordGame_View.is_words = getPreferences(MODE_PRIVATE).getBoolean(
				"is_word", wordGame_View.is_words);
		flag = getPreferences(MODE_PRIVATE).getBoolean("flag", flag);
		win_flag = getPreferences(MODE_PRIVATE)
				.getBoolean("win_flag", win_flag);
		visit = getPreferences(MODE_PRIVATE).getBoolean("visit", visit);
		for (int i = 0; i < wordGame_View.column; i++)
			cur_y[i] = getPreferences(MODE_PRIVATE).getInt("cur_y" + i,
					cur_y[i]);
		win_flag = getPreferences(MODE_PRIVATE)
				.getBoolean("win_flag", win_flag);
		wordString = getPreferences(MODE_PRIVATE).getString("wordstring",
				wordString);
		wordGame_View.gametextString = getPreferences(MODE_PRIVATE).getString(
				"gametextstring", wordGame_View.gametextString);
		wordGame_View.wordstring = getPreferences(MODE_PRIVATE).getString(
				"view_wordstring", wordGame_View.wordstring);
		score = getPreferences(MODE_PRIVATE).getInt("score", score);
		count = getPreferences(MODE_PRIVATE).getInt("count", count);
		count_x = getPreferences(MODE_PRIVATE).getInt("count_x", count_x);
		count_y = getPreferences(MODE_PRIVATE).getInt("count_y", count_y);
		wordGame_View.gametextString = getPreferences(MODE_PRIVATE).getString(
				"gametextstring", wordGame_View.gametextString);
		wordGame_View.count_numb = getPreferences(MODE_PRIVATE).getInt(
				"count_numb", wordGame_View.count_numb);
		for (int i = 0; i < wordGame_View.column; i++)
			for (int j = 0; j < wordGame_View.row; j++) {
				wordGame_View.block[i][j] = (char) (getPreferences(MODE_PRIVATE)
						.getInt("block" + i + j, wordGame_View.block[i][j]));
				wordGame_View.numb[i][j] = getPreferences(MODE_PRIVATE).getInt(
						"numb" + i + j, wordGame_View.numb[i][j]);
				wordGame_View.exist_block[i][j] = getPreferences(MODE_PRIVATE)
						.getBoolean("exist_block" + i + j,
								wordGame_View.exist_block[i][j]);
				wordGame_View.press_flag[i][j] = getPreferences(MODE_PRIVATE)
						.getBoolean("press_flag" + i + j,
								wordGame_View.press_flag[i][j]);
				wordGame_View.choose_block[i][j] = getPreferences(MODE_PRIVATE)
						.getBoolean("choose_block" + i + j,
								wordGame_View.choose_block[i][j]);
			}
		wordGame_View.invalidate();
		if (time > 5)
			Music.play(this, R.raw.wordgame_music);
		else
			Music.play(this, R.raw.last_five_second);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		Music.stop(this);
		wordGame_View.is_stop = true;
		wordGame_View.pauseString = "resume";
		// Save the current puzzle
		getPreferences(MODE_PRIVATE).edit().putInt("cnt", cnt).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("level", level).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("time", time).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("diff", diff).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("speed", speed).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("row", wordGame_View.row)
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt("column", wordGame_View.column).commit();
		getPreferences(MODE_PRIVATE).edit().putBoolean("flag", flag).commit();
		getPreferences(MODE_PRIVATE).edit()
				.putBoolean("is_word", wordGame_View.is_words).commit();
		getPreferences(MODE_PRIVATE).edit().putBoolean("win_flag", win_flag)
				.commit();
		getPreferences(MODE_PRIVATE).edit().putBoolean("visit", visit).commit();
		for (int i = 0; i < wordGame_View.column; i++)
			getPreferences(MODE_PRIVATE).edit().putInt("cur_y" + i, cur_y[i])
					.commit();
		getPreferences(MODE_PRIVATE).edit().putBoolean("win_flag", win_flag)
				.commit();
		getPreferences(MODE_PRIVATE).edit().putString("wordstring", wordString)
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putString("gametextstring", wordGame_View.gametextString)
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putString("view_wordstring", wordGame_View.wordstring)
				.commit();
		getPreferences(MODE_PRIVATE).edit().putInt("score", score).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("count", count).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("count_x", count_x).commit();
		getPreferences(MODE_PRIVATE).edit().putInt("count_y", count_y).commit();
		getPreferences(MODE_PRIVATE).edit()
				.putString("gametextstring", wordGame_View.gametextString)
				.commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt("count_numb", wordGame_View.count_numb).commit();
		for (int i = 0; i < wordGame_View.column; i++)
			for (int j = 0; j < wordGame_View.row; j++) {
				getPreferences(MODE_PRIVATE).edit()
						.putInt("block" + i + j, wordGame_View.block[i][j])
						.commit();
				getPreferences(MODE_PRIVATE).edit()
						.putInt("numb" + i + j, wordGame_View.numb[i][j])
						.commit();
				getPreferences(MODE_PRIVATE)
						.edit()
						.putBoolean("exist_block" + i + j,
								wordGame_View.exist_block[i][j]).commit();
				getPreferences(MODE_PRIVATE)
						.edit()
						.putBoolean("press_flag" + i + j,
								wordGame_View.press_flag[i][j]).commit();
				getPreferences(MODE_PRIVATE)
						.edit()
						.putBoolean("choose_block" + i + j,
								wordGame_View.choose_block[i][j]).commit();
			}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Nothing need to be done here
			Log.d("isRun", "landscape");

		} else {
			// Nothing need to be done here
			Log.d("isRun", "normal");
		}

	}

	private void getPuzzle(int diff) {
		// TODO Auto-generated method stub
		switch (diff) {
		case DIFFICULTY_CONTINUE:
			continue_button();
			break;
		case DIFFICULTY_EASY:
			time = 60;
			speed = 500;
			break;
		case DIFFICULTY_MEDIUM:
			time = 50;
			speed = 450;
			break;
		case DIFFICULTY_HARD:
			time = 45;
			speed = 400;
			break;

		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	public static void addValue(String value) {
		for (SimpleHash f : func)
			bits.set(f.hash(value), true);
	}

	public static void add(String value) {
		if (value != null)
			addValue(value);
	}

	public static boolean contains(String value) {
		if (value == null)
			return false;
		boolean ret = true;
		for (SimpleHash f : func)
			ret = ret && bits.get(f.hash(value));
		return ret;
	}

	private void showresults() {
		Music.stop(this);
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.wordgame_results, null);
		Log.i(TAG, "Come into showresults");
		AlertDialog.Builder dialog = new AlertDialog.Builder(WordGame_Game.this);
		dialog.setView(view);
		if (!visit && mainActivityWeakRef.get() != null
				&& !mainActivityWeakRef.get().isFinishing()) {
			visit = true;
			if (win_flag) {
				Log.i(TAG, "win_flag is true");
				dialog.setTitle("You Win!")
						.setMessage(wordGame_View.wordstring)
						.setPositiveButton("Next Level",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										level++;
										restart_game();
										dialog.cancel();
									}
								})
						.setNegativeButton("Return To Menu",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										values.continue_button_flag = false;
										finish();
									}
								}).show();
			} else {
				Log.i(TAG, "win_flag is false");

				dialog.setTitle("You Lose!")
						.setMessage(wordGame_View.wordstring)
						.setPositiveButton("Retry",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										level = 1;
										restart_game();
										dialog.cancel();
									}
								})
						.setNegativeButton("Return To Menu",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										values.continue_button_flag = false;
										finish();
									}
								}).show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Music.stop(this);
		drop.cancel();
		drop = null;
		dropTimer.purge();
		dropTimer = null;
		task.cancel();
		task = null;
		timer.purge();
		timer = null;
	}

	private void restart_game() {
		Music.play(this, R.raw.wordgame_music);
		values.continue_button_flag = true;
		getPuzzle(diff);
		visit = false;
		flag = false;
		for (int j = 0; j < 9; j++)
			cur_y[j] = wordGame_View.row;
		win_flag = false;
		wordString = "";
		wordGame_View.wordstring = "";
		score = 0;
		for (int i = 0; i < 1000; i++)
			wordString += generate_alphabet();
		count = 0;
		cnt = 0;
		count_x = 0;
		count_y = 0;
		for (int i = 0; i < wordGame_View.column; i++)
			for (int j = 0; j < wordGame_View.row; j++) {
				wordGame_View.exist_block[i][j] = false;
				wordGame_View.block[i][j] = 0;
				wordGame_View.choose_block[i][j] = false;
				wordGame_View.press_flag[i][j] = false;
				wordGame_View.numb[i][j] = 0;
			}
		wordGame_View.count_numb = 0;
		wordGame_View.gametextString = "";
		wordGame_View.invalidate();
	}

}
