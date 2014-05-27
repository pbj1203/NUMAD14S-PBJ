package edu.neu.madcourse.bojunpan.twoplayergame;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.mhealth.api.KeyValueAPI;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.widget.TextView;
import android.widget.Toast;

public class TwoPlayerGame_CountdownTimer extends Activity {
	private TimeCount time;
	private TextView count_down;
	private TextView count_label;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twoplayergame_countdowntimer);
		count_down = (TextView) findViewById(R.id.twoplaygame_count_down_text);
		count_label = (TextView) findViewById(R.id.twoplaygame_count_down_label);
		if (TwoPlayerGame_Global.Asyn_or_Syn == true) {
			count_label.setText("Syn Mode: " + TwoPlayerGame_Global.Username
					+ " & " + TwoPlayerGame_Global.Matched_name
					+ " is coming...");
			time = new TimeCount(8000, 1000);
		} else {
			count_label.setText("Asyn Mode: it's "
					+ TwoPlayerGame_Global.Matched_name + " turn");
			time = new TimeCount(10000, 1000);
		}
		time.start();
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			finish();
			if (checkconnection()) {
				if (TwoPlayerGame_Global.Asyn_or_Syn == true) {
					startSynGame();
				} else {
					startAsynGame();
				}
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			count_down.setText(String.valueOf(millisUntilFinished / 1000));
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

	private void startSynGame() {
		Intent twoPlayerGame_SynGame = new Intent(this,
				TwoPlayerGame_SynGame.class);
		startActivity(twoPlayerGame_SynGame);
	}

	private void startAsynGame() {
		Intent twoPlayerGame_AsynGame = new Intent(this,
				TwoPlayerGame_AsynGame.class);
		startActivity(twoPlayerGame_AsynGame);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
