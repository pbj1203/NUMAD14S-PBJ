package edu.neu.madcourse.bojunpan.dictionary;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.wordgame.WordGame_View.readfromfile;

public class Dictionary extends Activity {
	int num = 1;
	String str;
	public ArrayList<String> list = new ArrayList<String>();
	public static ArrayList<String> list_view = new ArrayList<String>();
	public ArrayAdapter<String> arrayAdapter;
	private EditText text;
	private TextView text_list;
	private static final int DEFAULT_SIZE = 2 << 24;
	private static final int[] seeds = { 3, 5, 7, 11, 13, 31, 37, 61 };
	private static BitSet bits = new BitSet(DEFAULT_SIZE);
	private static SimpleHash[] func = new SimpleHash[seeds.length];
	private ToneGenerator toneGenerator;
	int index;
	String index_name;
	boolean[][] vis = new boolean[26][26];

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
		try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);

		toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM,
				ToneGenerator.MAX_VOLUME);

		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
		View gobackButton = findViewById(R.id.go_back);
		text_list = (TextView) findViewById(R.id.text_list);
		gobackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		text = (EditText) findViewById(R.id.edit_text);
		text.addTextChangedListener(textWatcher);
		// text.setEnabled(false);

		Button clearButton;
		clearButton = (Button) findViewById(R.id.button_clear);
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				text.setText("");
				text_list.setText("");
				list_view.clear();
			}
		});
		Button acknowledgeButton = (Button) findViewById(R.id.acknowledge);
		acknowledgeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent l = new Intent(Dictionary.this, Acknowledge.class);
				startActivity(l);
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		String saveString = text_list.getText().toString();
		SharedPreferences sp = this.getSharedPreferences("save",
				Context.MODE_PRIVATE);
		sp.edit().putString("content", saveString).commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		text.setText(str);
		String saveString = this.getSharedPreferences("save",
				Context.MODE_PRIVATE).getString("content", null);
		text_list.setText(saveString);
		super.onResume();
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			if (str.length() == 2) {
				index_name = str.substring(0, 2);
				if (!vis[index_name.charAt(0) - 'a'][index_name
						.charAt(1) - 'a']) {
					vis[index_name.charAt(0) - 'a'][index_name
							.charAt(1) - 'a'] = true;
					new readfromfile().execute();
				}
			}
			if (str.length() >= 3) {
				if (contains(str)) {
					toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
					if (!list_view.contains(str)) {
						list_view.add(str);
						text_list.append(str + "\n");
					}
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			str = text.getText().toString();
			str = str.toLowerCase();
		}
	};

	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Nothing need to be done here

		} else {
			// Nothing need to be done here
		}

	}

	private class readfromfile extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPostExecute(String result) {

		}

		protected String doInBackground(Void... params) {
			InputStreamReader inputStreamReader = null;
			String filename = "wordlist/" + index_name + ".txt";
			try {
				inputStreamReader = new InputStreamReader(getResources()
						.getAssets().open(filename));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			BufferedReader reader = new BufferedReader(inputStreamReader);
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					add(line);
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

			return "";
		}
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

}