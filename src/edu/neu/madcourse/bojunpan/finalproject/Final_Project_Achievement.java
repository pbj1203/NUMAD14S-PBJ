package edu.neu.madcourse.bojunpan.finalproject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.util.EncodingUtils;

import edu.neu.madcourse.bojunpan.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Final_Project_Achievement extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		WebView webView;
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.final_achievement);
		if(!isConnectingToInternet())
		{
			show_toast("Internet is not available, try to connect");
		}
		webView = (WebView) findViewById(R.id.final_project_achievement);
		if (Build.VERSION.SDK_INT >= 11) {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.loadUrl("file:///android_asset/final_achievement.html");
		webView.requestFocus();
		webView.addJavascriptInterface(Final_Project_Achievement.this,
				"AndroidFunction");
	}

	@JavascriptInterface
	public String read_file() {
		String fileName = "/sdcard/achievement.txt";
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			Log.d("length", String.valueOf(length));
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	private void show_toast(String answer) {
		Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
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
}
