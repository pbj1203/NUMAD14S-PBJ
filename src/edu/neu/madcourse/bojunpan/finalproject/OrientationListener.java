package edu.neu.madcourse.bojunpan.finalproject;

import java.util.List;

import edu.neu.madcourse.bojunpan.trickiestpart.Trickiest_Part_Global;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationListener implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor sensor;
	private OnOrientationListener onOrientationListener;
	private Context mContext;

	public OrientationListener(Context c) {
		mContext = c;
		start();
	}

	public void start() {
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size() == 1) {
			sensorManager.registerListener((SensorEventListener) this,
					sensors.get(0), SensorManager.SENSOR_DELAY_UI);
		}
	}

	public void stop() {
		sensorManager.unregisterListener(this);
	}

	public void setOnOrientationListener(OnOrientationListener listener) {
		onOrientationListener = listener;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Final_Project_Global.Yaw = event.values[0];
		Final_Project_Global.pitch = event.values[1];
		onOrientationListener.onOrientation();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public interface OnOrientationListener {
		public void onOrientation();
	}

}
