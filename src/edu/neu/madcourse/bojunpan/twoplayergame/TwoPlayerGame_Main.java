package edu.neu.madcourse.bojunpan.twoplayergame;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.communication.Communication_Globals;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_About;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_Acknowledge;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_Game;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_Instruction;
import edu.neu.mhealth.api.KeyValueAPI;
import android.R.anim;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TwoPlayerGame_Main extends Activity implements OnClickListener {
	private static final String[] Mode = { "Asynchronous", "Synchronous" };
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private EditText username;
	TwoPlayerGame_Global twoPlayerGame_Global;
	GoogleCloudMessaging gcm;
	String regid;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twoplayergame_main);
		new Thread() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				KeyValueAPI.clear(TwoPlayerGame_Global.BACKUP_USERNAME,
						TwoPlayerGame_Global.BACKUP_PASSWORD);
			}
		}.start();
		spinner = (Spinner) findViewById(R.id.Asyn_or_Syn);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Mode);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		spinner.setVisibility(View.VISIBLE);
		View Login = findViewById(R.id.Twoplayergame_Login);
		Login.setOnClickListener(this);
		View Instruction = findViewById(R.id.Twoplayergame_Instruction);
		Instruction.setOnClickListener(this);
		View Acknowledge = findViewById(R.id.Twoplayergame_Acknowledge);
		Acknowledge.setOnClickListener(this);
		View exitButton = findViewById(R.id.Twoplayergame_Quit);
		exitButton.setOnClickListener(this);
		username = (EditText) findViewById(R.id.Twoplayergame_Username);
		gcm = GoogleCloudMessaging.getInstance(this);
		context = getApplicationContext();
	}

	class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (arg2 == 0) {
				twoPlayerGame_Global.Asyn_or_Syn = false;
			} else {
				twoPlayerGame_Global.Asyn_or_Syn = true;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Twoplayergame_Login:
			twoPlayerGame_Global.Username = username.getText().toString();
			if (twoPlayerGame_Global.Username.equals("")) {
				Toast.makeText(this, "You need to input a username",
						Toast.LENGTH_LONG).show();
				break;
			}
			if (!isConnectingToInternet()) {
				Toast.makeText(this, "Internet is not available",
						Toast.LENGTH_LONG).show();
				break;
			}
			registerInBackground();
			break;
		case R.id.Twoplayergame_Instruction:
			Intent twoplayer_instruction = new Intent(this,
					WordGame_Instruction.class);
			startActivity(twoplayer_instruction);
			break;
		case R.id.Twoplayergame_Acknowledge:
			Intent twoplayer_acknowledge_button = new Intent(this,
					TwoPlayerGame_Acknowledge.class);
			startActivity(twoplayer_acknowledge_button);
			break;
		case R.id.Twoplayergame_Quit:
			finish();
			break;
		}
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

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(twoPlayerGame_Global.GCM_SENDER_ID);
					if (KeyValueAPI.isServerAvailable()) {
						KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
								TwoPlayerGame_Global.BACKUP_PASSWORD, username
										.getText().toString(), regid);
						twoPlayerGame_Global.regid = regid;
						KeyValueAPI.put(TwoPlayerGame_Global.BACKUP_USERNAME,
								TwoPlayerGame_Global.BACKUP_PASSWORD,
								"titleText", "login");
						msg = username.getText().toString()
								+ " has registration ID=" + regid;
					} else {
						msg = "Error :" + "Backup Server is not available";
						return msg;
					}
				} catch (IOException ex) {
					msg = "Network is not available!";
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		}.execute(null, null, null);
	}
}
