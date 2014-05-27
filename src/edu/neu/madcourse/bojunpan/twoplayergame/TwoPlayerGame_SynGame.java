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
import edu.neu.madcourse.bojunpan.communication.Communication_Globals;
import edu.neu.madcourse.bojunpan.communication.GcmNotification;
import edu.neu.madcourse.bojunpan.sudoku.Game;
import edu.neu.madcourse.bojunpan.sudoku.Music;
import edu.neu.madcourse.bojunpan.sudoku.PuzzleView;
import edu.neu.mhealth.api.KeyValueAPI;
import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TwoPlayerGame_SynGame extends Activity {
	public ToneGenerator toneGenerator;
	private WeakReference<TwoPlayerGame_SynGame> mainActivityWeakRef;
	private static final int DEFAULT_SIZE = 2 << 24;
	private static final int[] seeds = { 3, 5, 7, 11, 13, 31, 37, 61 };
	private static BitSet bits = new BitSet(DEFAULT_SIZE);
	private static SimpleHash[] func = new SimpleHash[seeds.length];
	public Vibrator vibrator = null;
	private static final String TAG = "WordGame_game";
	private TwoPlayerGame_SynView wordGame_View;
	public String timeString;
	public String scoreString;
	public int time;
	public int score = 0;
	public String matchedscore = "0";
	public int count_x = 0;
	public int count_y = 0;
	public int count = 0;
	public int cur_y[] = new int[15];
	public int cnt = 0;
	public int last_score;
	public boolean visit;
	public boolean flag;
	public String string = "";
	public int judge_cnt = 0;
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
		Log.d(TAG, "onCreate");
		this.mainActivityWeakRef = new WeakReference<TwoPlayerGame_SynGame>(
				this);
		cnt = 0;
		flag = false;
		toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
				ToneGenerator.MAX_VOLUME);
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
		wordGame_View = new TwoPlayerGame_SynView(this);
		setContentView(wordGame_View);
		for (int j = 0; j < wordGame_View.column; j++)
			cur_y[j] = wordGame_View.row;
		wordGame_View.requestFocus();
		getPuzzle();
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
					judge_cnt++;
					if (judge_cnt == 20 && time > 5) {
						sendAlert();
						Log.d("sendAlert", "Alert!!!");
						judge_cnt = 0;
					}
					if (time == 5) {
						Music.stop(TwoPlayerGame_SynGame.this);
						Music.play(TwoPlayerGame_SynGame.this,
								R.raw.last_five_second);
					} else if (time == 0 || flag == true) {
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

	@Override
	protected void onResume() {
		super.onResume();
		if (time > 5)
			Music.play(this, R.raw.wordgame_music);
		else if (time >= 0 && time <= 5)
			Music.play(this, R.raw.last_five_second);
		else
			Music.stop(this);
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
				String reg_device = TwoPlayerGame_Global.regid;
				int nIcon = R.drawable.ic_stat_cloud;
				int nType = TwoPlayerGame_Global.SIMPLE_NOTIFICATION;
				GcmNotification gcmNotification = new GcmNotification();
				Map<String, String> msgParams;
				msgParams = new HashMap<String, String>();
				msgParams.put("data.alertText", "Notification");
				msgParams.put("data.titleText", "Alert");
				msgParams.put("data.contentText", "Please playing actively...");
				msgParams.put("data.nIcon", String.valueOf(nIcon));
				msgParams.put("data.nType", String.valueOf(nType));
				KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD, "alertText",
						"Message Notification");
				KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD, "titleText",
						"Alert");
				KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD, "contentText",
						"Please playing actively...");
				gcmNotification
						.sendNotification(
								msgParams,
								reg_device,
								edu.neu.madcourse.bojunpan.twoplayergame.TwoPlayerGame_SynGame.this);
				msg = "alert coming...";
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(TwoPlayerGame_SynGame.this, msg, Toast.LENGTH_SHORT).show();
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
				TwoPlayerGame_SynGame.this);
		dialog.setView(view);
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
