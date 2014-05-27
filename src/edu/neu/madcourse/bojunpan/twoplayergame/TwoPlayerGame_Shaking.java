package edu.neu.madcourse.bojunpan.twoplayergame;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.sudoku.Music;
import edu.neu.madcourse.bojunpan.twoplayergame.TwoPlayerGame_ShakeListener.OnShakeListener;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_Instruction;
import edu.neu.mhealth.api.KeyValueAPI;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.LoginFilter.UsernameFilterGeneric;
import android.text.format.Time;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TwoPlayerGame_Shaking extends Activity {

	TwoPlayerGame_ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private MediaPlayer shake_match;
	private MediaPlayer shake_sound_male;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	private String shaking_mode_time = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TwoPlayerGame_Global.Matched_name = "";
		setContentView(R.layout.twoplayergame_shaking);
		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		mShakeListener = new TwoPlayerGame_ShakeListener(this);
		TwoPlayerGame_Global.is_new = true;
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				new Thread() {	
					@Override
					public void run() {
						// TODO Auto-generated method stub
						set_shaking_mode_time();
					}
				}.start();
				startAnim();
				mShakeListener.stop();
				startVibrato();
				startMatchEffect();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						boolean connect_flag = exist_others();
						if (!connect_flag) {
							Toast.makeText(
									getApplicationContext(),
									"Sorry!\nNo Match At The Same Time!\nTry Again!",
									Toast.LENGTH_LONG).show();

						} else if (connect_flag
								&& TwoPlayerGame_Global.Matched_name
										.equals(TwoPlayerGame_Global.Username)) {
							Toast.makeText(
									getApplicationContext(),
									"Sorry!\nTwo player should not be the same name!\nTry Again!",
									Toast.LENGTH_LONG).show();
						} else if (connect_flag
								&& !TwoPlayerGame_Global.Matched_name
										.equals(TwoPlayerGame_Global.Username)) {
							StartSuccessfulEffect();
							Toast.makeText(
									getApplicationContext(),
									"Match Successful!\nMached "
											+ TwoPlayerGame_Global.Matched_name,
									Toast.LENGTH_LONG).show();
							new Thread() {	
								@Override
								public void run() {
									// TODO Auto-generated method stub
									KeyValueAPI.put(
											TwoPlayerGame_Global.BACKUP_USERNAME,
											TwoPlayerGame_Global.BACKUP_PASSWORD,
											TwoPlayerGame_Global.Username + "Match",
											TwoPlayerGame_Global.Matched_name);
								}
							}.start();
							if (TwoPlayerGame_Global.Username
									.compareTo(TwoPlayerGame_Global.Matched_name) < 0) {
								TwoPlayerGame_Global.user_turn = false;
							} else {
								TwoPlayerGame_Global.user_turn = true;
							}
							show_next_activity();
							new Thread() {	
								@Override
								public void run() {
									// TODO Auto-generated method stub
									KeyValueAPI.clearKey(
											TwoPlayerGame_Global.BACKUP_USERNAME,
											TwoPlayerGame_Global.BACKUP_PASSWORD,
											shaking_mode_time);
								}
							}.start();
							return;
						}
						mVibrator.cancel();
						stopMusic();
						mShakeListener.start();
					}
				}, 2000);
			}
		});
	}

	public void startAnim() {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);
	}

	public void startVibrato() {
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
	}

	private void startMatchEffect() {
		shake_sound_male = MediaPlayer.create(getApplicationContext(),
				R.raw.shake_sound_male);
		shake_sound_male.start();

	}

	private void StartSuccessfulEffect() {
		shake_match = MediaPlayer.create(getApplicationContext(),
				R.raw.shake_match);
		shake_match.start();
	}

	private void stopMusic() {
		Music.stop(this);
	}

	private void set_shaking_mode_time() {
		Time t = new Time();
		t.setToNow();
		shaking_mode_time = "";
		if (TwoPlayerGame_Global.Asyn_or_Syn == false) {
			shaking_mode_time += "Asyn";
		} else {
			shaking_mode_time += "Syn";
		}
		shaking_mode_time += String.valueOf(t.year);
		shaking_mode_time += String.valueOf(t.month);
		shaking_mode_time += String.valueOf(t.monthDay);
		shaking_mode_time += String.valueOf(t.hour);
		shaking_mode_time += String.valueOf(t.minute);
		String str = "";
		str = KeyValueAPI.get(TwoPlayerGame_Global.BACKUP_USERNAME,
				TwoPlayerGame_Global.BACKUP_PASSWORD, shaking_mode_time);

		if (str.contains("Error") || str.contains("ERROR")) {
			KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
					TwoPlayerGame_Global.BACKUP_PASSWORD, shaking_mode_time,
					TwoPlayerGame_Global.Username);
		} else {
			str += "|" + TwoPlayerGame_Global.Username;
			KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
					TwoPlayerGame_Global.BACKUP_PASSWORD, shaking_mode_time,
					str);
		}
		Log.d(shaking_mode_time, str);
	}

	private void show_next_activity() {
		finish();
		if (TwoPlayerGame_Global.Asyn_or_Syn == true) {
			Intent twoplayer_countdown = new Intent(this,
					TwoPlayerGame_CountdownTimer.class);
			startActivity(twoplayer_countdown);
		} else {
			if (!TwoPlayerGame_Global.user_turn) {
				Intent twoplayer_countdown = new Intent(this,
						TwoPlayerGame_CountdownTimer.class);
				startActivity(twoplayer_countdown);
			} else {
				Intent twoPlayerGame_AsynGame = new Intent(this,
						TwoPlayerGame_AsynGame.class);
				startActivity(twoPlayerGame_AsynGame);
			}
		}
	}

	public boolean exist_others() {
		String str = "";
		str = KeyValueAPI.get(TwoPlayerGame_Global.BACKUP_USERNAME,
				TwoPlayerGame_Global.BACKUP_PASSWORD, shaking_mode_time);
		if(str.contains("Error") || str.contains("ERROR")) {
			return false;
		}
		str += "|";
		Log.d(TwoPlayerGame_Global.Username, str);
		String tmp = "";
		for (int i = 0; i < str.length(); i++) {
			if (str.toCharArray()[i] == '|') {
				if (tmp.equals(TwoPlayerGame_Global.Username)) {
					tmp = "";
				} else if (!tmp.equals("")
						&& !tmp.equals(TwoPlayerGame_Global.Username)) {
					TwoPlayerGame_Global.Matched_name = tmp;
					break;
				}
			} else {
				tmp += str.toCharArray()[i];
			}
		}
		Log.d(TwoPlayerGame_Global.Username, TwoPlayerGame_Global.Matched_name);
		if (tmp.equals(TwoPlayerGame_Global.Username) || tmp.equals("")
				|| TwoPlayerGame_Global.Matched_name.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
}