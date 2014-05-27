package edu.neu.madcourse.bojunpan;
import edu.neu.madcourse.bojunpan.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class GenerateError extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	TextView s = (TextView)findViewById(R.id.generate_error);
	s.setText("I am Error");
	setContentView(R.layout.generate_error);
	}
}