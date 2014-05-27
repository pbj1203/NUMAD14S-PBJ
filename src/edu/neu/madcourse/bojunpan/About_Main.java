package edu.neu.madcourse.bojunpan;

import edu.neu.madcourse.bojunpan.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager; 
import android.widget.TextView;

public class About_Main extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.about_main);
	TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	
	String Imei = tm.getDeviceId();
	TextView s = (TextView)findViewById(R.id.imei);
	s.setText("IMEI/MEID numbers: "+Imei);
	}
}