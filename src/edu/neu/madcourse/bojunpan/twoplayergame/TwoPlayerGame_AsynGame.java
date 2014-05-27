package edu.neu.madcourse.bojunpan.twoplayergame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.communication.GcmNotification;
import edu.neu.madcourse.bojunpan.sudoku.Game;
import edu.neu.madcourse.bojunpan.sudoku.Music;
import edu.neu.madcourse.bojunpan.sudoku.PuzzleView;
import edu.neu.mhealth.api.KeyValueAPI;
import android.R.bool;
import android.R.integer;
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
import android.widget.Toast;

public class TwoPlayerGame_AsynGame extends Activity {
	public ToneGenerator toneGenerator;
	private WeakReference<TwoPlayerGame_AsynGame> mainActivityWeakRef;
	private static final int DEFAULT_SIZE = 2 << 24;
	private static final int[] seeds = { 3, 5, 7, 11, 13, 31, 37, 61 };
	private static BitSet bits = new BitSet(DEFAULT_SIZE);
	private static SimpleHash[] func = new SimpleHash[seeds.length];
	public Vibrator vibrator = null;
	public String string = "";
	private static final String TAG = "WordGame_game";
	private TwoPlayerGame_AsynView wordGame_View;
	public String timeString;
	public String scoreString;
	public int time;
	public int score = 0;
	public int roundtime = 10;
	public String matchedscore = "";
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
	public int win_flag = 0;
	public int diff;
	public boolean is_continue;
	private TwoPlayerGame_Global values;

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
		if (!checkconnection()) {
			finish();
		}
		Log.d(TAG, "onCreate");
		this.mainActivityWeakRef = new WeakReference<TwoPlayerGame_AsynGame>(
				this);
		cnt = 0;
		flag = false;
		toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
				ToneGenerator.MAX_VOLUME);
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
		wordGame_View = new TwoPlayerGame_AsynView(this);
		setContentView(wordGame_View);
		for (int j = 0; j < wordGame_View.column; j++)
			cur_y[j] = wordGame_View.row;
		wordGame_View.requestFocus();
		getPuzzle();
		if (!TwoPlayerGame_Global.is_new) {
			continue_button();
		}
		for (int i = 0; i < 1000; i++)
			wordString += generate_alphabet();
		timer.schedule(task, 1000, 1000);
		dropTimer.schedule(drop, 1000, speed);
	}

	public String generate_alphabet() {
		// TODO Auto-generated method stub
		double rnd = Math.random();
		char readomLetter = 0;
		if (rnd <= values.probability[0])
			readomLetter = 'a';
		else {
			for (int i = 1; i < 26; i++)
				if (rnd > values.probability[i - 1]
						&& rnd <= values.probability[i])
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
					time--;
					if (time == 5) {
						Music.stop(TwoPlayerGame_AsynGame.this);
						Music.play(TwoPlayerGame_AsynGame.this,
								R.raw.last_five_second);
					} 
					if (time == 0 || flag) {
						flag = true;
						int mscore = 0;
						if (!matchedscore.contains("Error")
								&& !matchedscore.contains("ERROR")) {
							mscore = Integer.parseInt(matchedscore);
						}
						if (score > mscore) {
							win_flag = 2;
						} else if (score == mscore) {
							win_flag = 1;
						} else {
							win_flag = 0;
						}
						Log.d("?????????", "time over!!");
						showresults();
					}
					else {
						if (time > 0) {
							roundtime--;
						}
						if (roundtime == 5) {
							sendAlert();
						}
						if (roundtime == 0) {
							enter_countdown();
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
					if (count == wordGame_View.column * wordGame_View.row
							|| flag) {
						count = 0;
						int mscore = 0;
						if (!matchedscore.contains("Error")
								&& !matchedscore.contains("ERROR")) {
							mscore = Integer.parseInt(matchedscore);
						}
						if (score > mscore) {
							win_flag = 2;
						} else if (score == mscore) {
							win_flag = 1;
						} else {
							win_flag = 0;
						}
						Log.d("?????????", "grid full!!!");
						showresults();
					} else {
						if (cur_y[count_x] == 0) {
							count_x++;
							if (count_x == wordGame_View.column)
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
			});

		}
	};

	private void enter_countdown() {
		finish();
		Intent twoplayer_countdown = new Intent(this,
				TwoPlayerGame_CountdownTimer.class);
		startActivity(twoplayer_countdown);
	}

	protected void continue_button() {
		wordGame_View.invalidate();
		cnt = getPreferences(MODE_PRIVATE).getInt("cnt", cnt);
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
		visit = getPreferences(MODE_PRIVATE).getBoolean("visit", visit);
		for (int i = 0; i < wordGame_View.column; i++)
			cur_y[i] = getPreferences(MODE_PRIVATE).getInt("cur_y" + i,
					cur_y[i]);
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
		// Save the current puzzle
		TwoPlayerGame_Global.is_new = false;
		getPreferences(MODE_PRIVATE).edit().putInt("cnt", cnt).commit();
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
		getPreferences(MODE_PRIVATE).edit().putBoolean("visit", visit).commit();
		for (int i = 0; i < wordGame_View.column; i++)
			getPreferences(MODE_PRIVATE).edit().putInt("cur_y" + i, cur_y[i])
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

	private void sendAlert() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				String msg = "";
				String reg_device = KeyValueAPI.get(
						TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD,
						TwoPlayerGame_Global.Matched_name);
				int nIcon = R.drawable.ic_stat_cloud;
				int nType = TwoPlayerGame_Global.SIMPLE_NOTIFICATION;
				GcmNotification gcmNotification = new GcmNotification();
				Map<String, String> msgParams;
				msgParams = new HashMap<String, String>();
				msgParams.put("data.alertText", "Notification");
				msgParams.put("data.titleText", "Turn Coming");
				msgParams.put("data.contentText", "Your turns is coming...");
				msgParams.put("data.nIcon", String.valueOf(nIcon));
				msgParams.put("data.nType", String.valueOf(nType));
				KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD, "alertText",
						"Message Notification");
				KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD, "titleText",
						"Take Turn");
				KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD, "contentText",
						"Please playing actively...");
				gcmNotification
						.sendNotification(
								msgParams,
								reg_device,
								edu.neu.madcourse.bojunpan.twoplayergame.TwoPlayerGame_AsynGame.this);
				msg = "opposite turns coming...";
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(TwoPlayerGame_AsynGame.this, msg,
						Toast.LENGTH_SHORT).show();
			}
		}.execute(null, null, null);
	}

	private void getPuzzle() {
		// TODO Auto-generated method stub
		time = 60;
		speed = 500;
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

	public void showresults() {
		Music.stop(this);
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.wordgame_results, null);
		Log.i(TAG, "Come into showresults");
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				TwoPlayerGame_AsynGame.this);
		dialog.setView(view);
		string = (KeyValueAPI
				.get(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD,
						values.Matched_name
								+ "String")
				.contains("Error") ? ""
				: KeyValueAPI
						.get(TwoPlayerGame_Global.BACKUP_USERNAME,
								TwoPlayerGame_Global.BACKUP_PASSWORD,
								values.Matched_name
										+ "String"));
		new Thread() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				KeyValueAPI.put(
						TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD,
						values.Username + "Timeover", "true");
			}
		}.start();
		if (!visit && mainActivityWeakRef.get() != null
				&& !mainActivityWeakRef.get().isFinishing()) {
			visit = true;
			if (win_flag == 2) {
				Log.i(TAG, "win_flag is true");
				dialog.setTitle("You Win!")
						.setMessage(
								"Your wordlist: "
										+ "\n"
										+ wordGame_View.wordstring
										+ TwoPlayerGame_Global.Matched_name
										+ " wordlist: \n"
										+ string)
						.setNegativeButton("Return To Menu",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).show();
			} else if (win_flag == 0) {
				Log.i(TAG, "win_flag is false");
				dialog.setTitle("You Lose!")
						.setMessage(
								"Your wordlist: "
										+ "\n"
										+ wordGame_View.wordstring
										+ TwoPlayerGame_Global.Matched_name
										+ " wordlist: \n"
										+ string)
						.setNegativeButton("Return To Menu",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).show();
			} else {
				dialog.setTitle("Draw!")
						.setMessage(
								"Your wordlist: "
										+ "\n"
										+ wordGame_View.wordstring
										+ TwoPlayerGame_Global.Matched_name
										+ " wordlist: \n"
										+ string)
						.setNegativeButton("Return To Menu",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).show();
			}
		}
	}


	private boolean checkconnection() {
		if (!KeyValueAPI.get(TwoPlayerGame_Global.BACKUP_USERNAME,
				TwoPlayerGame_Global.BACKUP_PASSWORD,
				TwoPlayerGame_Global.Matched_name + "Match").equals(
				TwoPlayerGame_Global.Username)) {
			Toast.makeText(this, "the opposite connection failed",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
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

}
