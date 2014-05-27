package edu.neu.madcourse.bojunpan.wordgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.sudoku.Music;
import edu.neu.madcourse.bojunpan.sudoku.Prefs;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.provider.SyncStateContract.Constants;
import android.provider.UserDictionary.Words;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug.FlagToString;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WordGame_View extends View {
	private static final String TAG = "WordGame";
	private float width; // width of one tile
	private float height; // height of one tile
	private int selX; // X index of selection
	private int selY; // Y index of selection
	private final Rect selRect = new Rect();
	private final Rect curRect = new Rect();
	private final Rect buttonRect = new Rect();
	private final WordGame_Game game;
	public ArrayList<String> wordlist = new ArrayList<String>();
	public ArrayList<String> list = new ArrayList<String>();
	public int row;
	public int column;
	public String timeString;
	public String levelString;
	public String scoreString;
	public String gametextString = "";
	public String quitString;
	public String hintString;
	public String pauseString = "pause";
	public int margin_time_x;
	public int margin_score_x;
	public int title_height;
	public int buttom_height;
	public int title_y;
	public int buttom_y;
	public int time_x;
	public int level_x;
	public int score_x;
	public int text_x;
	public int quit_x;
	public int hint_x;
	public int pause_x;
	public int margin_text;
	public int margin_pause;
	public int margin_hint;
	public boolean exist_block[][] = new boolean[15][15];
	public final Rect pos_block[][] = new Rect[15][15];
	public char block[][] = new char[15][15];
	public boolean choose_block[][] = new boolean[15][15];
	public int button_choose;
	public boolean press_flag[][] = new boolean[15][15];
	public int count_numb;
	public int numb[][] = new int[15][15];
	public boolean is_words;
	private int canvas_width;
	private int canvas_height;
	public String wordstring = "";
	public boolean is_stop = false;
	private WordGame_Values values;

	public WordGame_View(Context context) {
		super(context);
		row = WordGame_Prefs.getRow(context).charAt(0) - '0';
		column = WordGame_Prefs.getColumn(context).charAt(0) - '0';
		this.game = (WordGame_Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the background...
		canvas_height = getHeight();
		canvas_width = getWidth();
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.white));
		paint.setTextSize(20);
		Paint paint_black = new Paint();
		paint_black.setColor(getResources().getColor(R.color.black));
		paint_black.setTextSize(25);
		timeString = "Time: " + game.time;
		levelString = "Level " + game.level + " (Target:" + game.level * 40
				+ ")";
		scoreString = "Score: " + game.score;
		quitString = "Quit";
		hintString = "Hint";
		margin_time_x = canvas_width / 4;
		margin_score_x = canvas_width - canvas_width / 4;
		margin_text = canvas_width / 2;
		margin_hint = margin_text + canvas_width / 6;
		margin_pause = margin_hint + canvas_width / 6;
		title_height = canvas_height / 8;
		buttom_height = canvas_height - title_height;
		title_y = (int) (title_height / 2.0 + paint.measureText("a") / 2.0);
		time_x = (int) margin_time_x / 2
				- (int) (paint.measureText(timeString) / 2.0);
		level_x = (int) ((margin_score_x + margin_time_x) / 2.0 - paint
				.measureText(levelString) / 2.0);
		score_x = (int) ((canvas_width + margin_score_x) / 2.0 - paint
				.measureText(scoreString) / 2.0);
		buttom_y = (int) ((canvas_height + buttom_height) / 2.0 + paint
				.measureText("a") / 2.0);
		text_x = (int) ((margin_text) / 2.0 - paint.measureText(scoreString) / 2.0);
		hint_x = (int) ((margin_hint + margin_text) / 2.0 - paint
				.measureText(hintString) / 2.0);
		pause_x = (int) ((margin_pause + margin_hint) / 2.0 - paint
				.measureText(pauseString) / 2.0);
		quit_x = (int) ((margin_pause + canvas_width) / 2.0 - paint
				.measureText(quitString) / 2.0);
		width = canvas_width / column;
		height = (buttom_height - title_height) / row;
		Rect titleRect = new Rect(0, 0, canvas_width, title_height);
		Rect backgroundRect = new Rect(0, title_height, canvas_width,
				buttom_height);
		Rect buttomRect = new Rect(0, buttom_height, canvas_width,
				canvas_height);
		// Draw the board..
		Paint titlegroundPaint = new Paint();
		titlegroundPaint.setColor(getResources().getColor(R.color.puzzle_dark));
		canvas.drawRect(titleRect, titlegroundPaint);
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(backgroundRect, background);
		Paint buttomPaint = new Paint();
		buttomPaint.setColor(getResources().getColor(R.color.puzzle_dark));
		canvas.drawRect(buttomRect, buttomPaint);
		// Draw the words
		canvas.drawText(timeString, time_x, title_y, paint);
		canvas.drawText(levelString, level_x, title_y, paint);
		canvas.drawText(scoreString, score_x, title_y, paint);
		canvas.drawText(gametextString, text_x, buttom_y, paint);
		canvas.drawText(quitString, quit_x, buttom_y, paint);
		canvas.drawText(hintString, hint_x, buttom_y, paint);
		canvas.drawText(pauseString, pause_x, buttom_y, paint);
		// Define colors for the grid lines
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		canvas.drawLine(margin_time_x, 0, margin_time_x, title_height, light);
		canvas.drawLine(margin_time_x + 1, 0, margin_time_x, title_height,
				hilite);
		canvas.drawLine(margin_score_x, 0, margin_score_x, title_height, light);
		canvas.drawLine(margin_score_x + 1, 0, margin_score_x, title_height,
				hilite);
		for (int i = 0; i < row; i++) {
			canvas.drawLine(0, title_height + i * height, canvas_width,
					title_height + i * height, light);
			canvas.drawLine(0, title_height + i * height + 1, canvas_width,
					title_height + i * height, hilite);
		}
		for (int i = 0; i < column; i++) {
			canvas.drawLine(i * width, title_height, i * width, buttom_height,
					light);
			canvas.drawLine(i * width + 1, title_height, i * width + 1,
					buttom_height, hilite);
		}
		canvas.drawLine(margin_text, buttom_height, margin_text, canvas_height,
				light);
		canvas.drawLine(margin_text + 1, buttom_height, margin_text,
				canvas_height, hilite);
		canvas.drawLine(margin_pause, buttom_height, margin_pause,
				canvas_height, light);
		canvas.drawLine(margin_pause + 1, buttom_height, margin_pause,
				canvas_height, hilite);
		canvas.drawLine(margin_hint, buttom_height, margin_hint, canvas_height,
				light);
		canvas.drawLine(margin_hint + 1, buttom_height, margin_hint,
				canvas_height, hilite);
		if (game.cur_y[game.count_x] != 0) {
			exist_block[game.count_x][game.count_y] = true;
			block[game.count_x][game.count_y] = game.wordString.toCharArray()[game.cnt];
		}
		;
		for (int i = 0; i < column; i++)
			for (int j = 0; j < row; j++) {
				if (exist_block[i][j] == true) {
					if (i == game.count_x && j < game.count_y) {
						exist_block[i][j] = false;
						block[i][j] = 0;
					}
				}
			}
		for (int i = 0; i < column; i++)
			for (int j = 0; j < row; j++)
				if (exist_block[i][j] == true) {
					canvas.drawRect(getRectArea(i, j), paint);
					canvas.drawText(
							String.valueOf(block[i][j]),
							getRectArea(i, j).centerX()
									- paint_black.measureText("a") / 2,
							getRectArea(i, j).centerY()
									+ paint_black.measureText("a") / 2,
							paint_black);
				}
		for (int i = 0; i < column; i++)
			for (int j = 0; j < row; j++) {
				if (press_flag[i][j] == true) {
					if (i == game.count_x && j < game.count_y) {
						press_flag[i][j] = false;
						press_flag[i][game.count_y] = true;
						numb[i][game.count_y] = numb[i][j];
						numb[i][j] = 0;
					}
				}
			}
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		for (int i = 0; i < column; i++)
			for (int j = 0; j < row; j++) {
				if (press_flag[i][j] == true)
					canvas.drawRect(getRectArea(i, j), selected);
			}
		if(!game.flag)
			canvas.drawRect(buttonRect, selected);
		Paint Red = new Paint();
		Red.setColor(getResources().getColor(R.color.red));
		Paint Green = new Paint();
		Green.setColor(getResources().getColor(R.color.green));
		if (is_words == true) {
			canvas.drawRect(0, buttom_height, margin_text, canvas_height, Green);
			canvas.drawText(gametextString, text_x, buttom_y, paint);
		} else {
			canvas.drawRect(0, buttom_height, margin_text, canvas_height,
					titlegroundPaint);
			canvas.drawText(gametextString, text_x, buttom_y, paint);
		}
		if (!game.flag)
			invalidate();
	}

	private void getcurRect(int x, int y) {
		if (x >= 0 && x < column && y >= 0 && y < row)
			curRect.set((int) (x * width), (int) (y * height + title_height),
					(int) (x * width + width),
					(int) (y * height + title_height + height));

	}

	private Rect getRectArea(int x, int y) {
		return new Rect((int) (x * width), (int) (y * height + title_height),
				(int) (x * width + width),
				(int) (y * height + title_height + height));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / column;
		height = (title_height + buttom_height) / row;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		// if (game.flag)
		// return super.onTouchEvent(event);
		if (event.getY() >= title_height && event.getY() <= buttom_height) {
			select((int) (event.getX() / width),
					(int) ((event.getY() - title_height) / height));
			Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
			game.TONE_FLAG = false;
			is_words = false;
			if (gametextString.length() == 2) {
				game.index_name = gametextString.substring(0, 2);
				Log.d("index_name", game.index_name.charAt(0) + "    "+ game.index_name
						.charAt(1));
				if (!game.vis[game.index_name.charAt(0) - 'a'][game.index_name
						.charAt(1) - 'a']) {
					game.vis[game.index_name.charAt(0) - 'a'][game.index_name
							.charAt(1) - 'a'] = true;
					new readfromfile().execute();
				}
			}
			if (gametextString.length() >= 3) {
				if (list.contains(gametextString)) {
					if (!game.TONE_FLAG) {
						game.toneGenerator
								.startTone(ToneGenerator.TONE_PROP_BEEP);
						game.TONE_FLAG = true;
					}
					is_words = true;
				} else
					is_words = false;
			}
		}
		if (event.getY() >= buttom_height && event.getY() <= canvas_height) {
			Log.d("Getheight", "getHeight= " + canvas_height);
			if (event.getX() >= 0 && event.getX() <= margin_text) {
				button_choose = 1;
				if (is_words) {
					is_words = false;
					if (!wordlist.contains(gametextString)) {
						wordlist.add(gametextString);
						wordstring += gametextString + "\n";
					}
					game.score += gametextString.length() * 10;
					game.count -= gametextString.length();
					Log.d("game.count", String.valueOf(game.count));
					for (int i = 0; i < column; i++)
						for (int j = 0; j < row; j++) {
							if (press_flag[i][j]) {
								exist_block[i][j] = false;
								block[i][j] = 0;
								game.cur_y[i]++;
								for (int k = j; k >= 1; k--) {
									exist_block[i][k] = exist_block[i][k - 1];
									block[i][k] = block[i][k - 1];
								}
							}
						}
				}
				for (int i = 0; i < column; i++)
					for (int j = 0; j < row; j++) {
						press_flag[i][j] = false;
						numb[i][j] = 0;
					}
				gametextString = "";
				count_numb = 0;
				buttonRect.set(0, buttom_height, margin_text, canvas_height);
			} else if (event.getX() > margin_text
					&& event.getX() <= margin_hint) {
				button_choose = 2;
				if (game.count <= 2)
					Toast.makeText(this.game, "No Hint Available",
							Toast.LENGTH_SHORT).show();
				else {
					int idx;
					int rnd;
					boolean vis_rnd[] = new boolean[100];
					idx = (int) (Math.random() * 30);
					while (idx == 30)
						idx = (int) (Math.random() * 30);
					String str = values.THREE_WORDS_STRING[idx];
					String add_str = "";
					for (int i = 0; i < game.count - str.length(); i++)
						add_str += game.generate_alphabet();
					str += add_str;
					for (int i = 0; i < str.length(); i++)
						vis_rnd[i] = false;
					count_numb = 0;
					gametextString = "";
					for (int i = 0; i < column; i++)
						for (int j = 0; j < row; j++) {
							press_flag[i][j] = false;
							numb[i][j] = 0;
						}
					for (int i = 0; i < column; i++)
						for (int j = 0; j < row; j++) {
							if (i == game.count_x && j == game.count_y)
								continue;
							if (count_numb == 3)
								break;
							if (exist_block[i][j]) {
								rnd = (int) (Math.random() * str.length());
								while (rnd == str.length() || vis_rnd[rnd]) {
									rnd = (int) (Math.random() * str.length());
								}
								vis_rnd[rnd] = true;
								Log.d("rnd", String.valueOf(rnd));
								block[i][j] = str.toCharArray()[rnd];
								if (rnd >= 0 && rnd < 3) {
									press_flag[i][j] = true;
									numb[i][j] = rnd;
									gametextString += str.toCharArray()[count_numb];
									count_numb++;
								}
							}
						}
					is_words = true;
					buttonRect.set(margin_text, buttom_height, margin_hint,
							canvas_height);
				}
			} else if (event.getX() > margin_hint
					&& event.getX() <= margin_pause) {
				button_choose = 3;
				buttonRect.set(margin_hint, buttom_height, margin_pause,
						canvas_height);
				if (is_stop) {
					Log.i(TAG, "Start");
				} else {
					Log.i(TAG, "Stop");
				}
				is_stop = !is_stop;
				if (is_stop) {
					pauseString = "resume";
					game.flag = true;
					Music.stop(this.game);
				} else {
					pauseString = "pause";
					game.flag = false;
					if (game.time > 5)
						Music.play(this.game, R.raw.wordgame_music);
					else
						Music.play(this.game, R.raw.last_five_second);
					invalidate();
				}
			} else {
				button_choose = 4;
				buttonRect.set(margin_pause, buttom_height, canvas_width,
						canvas_height);
				game.finish();
			}
			invalidate(buttonRect);
		}

		return true;
	}

	private void select(int x, int y) {
		if (!game.flag)
			invalidate(selRect);
		selX = Math.min(Math.max(x, 0), column - 1);
		selY = Math.min(Math.max(y, 0), row - 1);
		getRect(selX, selY, selRect);
		if ((selX == game.count_x && selY == game.count_y)
				|| exist_block[selX][selY]) {
			game.vibrator.vibrate(new long[]{80 ,50,50,100,50}, -1);
			if (!press_flag[selX][selY]) {
				press_flag[selX][selY] = true;
				numb[selX][selY] = count_numb++;
				if (selX == game.count_x && selY < game.cur_y[selX] - 1) {
					gametextString += game.wordString.toCharArray()[game.cnt];
				} else {
					gametextString += block[selX][selY];
				}
			} else {
				if (gametextString.length() == 1)
					gametextString = "";
				else {
					int len = gametextString.length();
					gametextString = gametextString.substring(0,
							numb[selX][selY])
							+ gametextString.substring(numb[selX][selY] + 1,
									len);

				}
				for (int i = 0; i < column; i++)
					for (int j = 0; j < row; j++)
						if (numb[i][j] > numb[selX][selY])
							numb[i][j]--;
				numb[selX][selY] = 0;
				count_numb--;
				press_flag[selX][selY] = false;
			}

		}
		if (!game.flag)
			invalidate(selRect);
	}

	private void getRect(int x, int y, Rect rect) {
		if (x >= 0 && x < column && y >= 0 && y < row)
			rect.set((int) (x * width), (int) (y * height + title_height),
					(int) (x * width + width),
					(int) (y * height + title_height + height));
	}

	public class readfromfile extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(String result) {

		}

		protected String doInBackground(Void... params) {
			InputStreamReader inputStreamReader = null;
			String filename = "wordlist/" + game.index_name + ".txt";
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
					list.add(line);
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
}
