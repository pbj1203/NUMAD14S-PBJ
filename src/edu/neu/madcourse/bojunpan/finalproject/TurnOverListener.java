package edu.neu.madcourse.bojunpan.finalproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class TurnOverListener implements SensorEventListener {
	private static final int state_invalid = -1;
	private static final int state_initial = 0;
	private static final int state_first = 1;
	private static final int state_second = 2;
	private static final int state_third = 3;
	private static final int state_forth = 4;
	private static final int state_fifth = 5;
	private static final long timeDifference = 1000000000;
	private static final int UPTATE_INTERVAL_TIME = 70;
	private SensorManager sensorManager;
	private Sensor sensor;
	private OnTurnOverListener onTurnOverListener;
	private Context mContext;
	private long lastUpdateTime;
	private long currUpdateTime;
	private int currState;
	private int prevState;

	public TurnOverListener(Context c) {
		mContext = c;
		start();
	}

	public void start() {
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		if (sensor != null) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}

	}

	public void stop() {
		sensorManager.unregisterListener(this);
	}

	public void setOnTurnOverListener(OnTurnOverListener listener) {
		onTurnOverListener = listener;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values.clone();
		if (currState == state_invalid) {
			if (values[2] > 10 && values[2] < 11 && values[1] > -5 && values[1] < 5) {
				currState = state_initial;
				lastUpdateTime = event.timestamp;
			}
		}
		if (currState == state_initial) {
			if (values[2] < -11 && values[2] > -20 && values[1] > -5 && values[1] < 5) {
				currState = state_first;
				lastUpdateTime = event.timestamp;
			}
		}
		if (currState == state_first) {
			if ((event.timestamp - lastUpdateTime) > timeDifference) {
				currState = state_invalid;
			} else if (values[2] > 11 && values[2] < 20 && values[1] > -5 && values[1] < 5) {
				currState = state_second;
				lastUpdateTime = event.timestamp;
			}
		}
		if (currState == state_second) {
			if ((event.timestamp - lastUpdateTime) > timeDifference) {
				currState = state_invalid;
			} if (values[2] < -11 && values[2] > -20 && values[1] > -5 && values[1] < 5) {
				currState = state_third;
				lastUpdateTime = event.timestamp;
			}
		}

		if (currState == state_third) {
			if ((event.timestamp - lastUpdateTime) > timeDifference) {
				currState = state_invalid;
			} else if (values[2] < 20 && values[2] > 11 && values[1] > -5 && values[1] < 5) {
				currState = state_forth;
				lastUpdateTime = event.timestamp;
			}
		}

		if (currState == state_forth) {
			if ((event.timestamp - lastUpdateTime) > timeDifference) {
				currState = state_invalid;
			} if (values[2] < -11 && values[2] > -20 && values[1] > -5 && values[1] < 5) {
				currState = state_fifth;
				lastUpdateTime = event.timestamp;
			}
		}

		if (currState == state_fifth) {
			onTurnOverListener.onTurnOver();
			currState = state_invalid;
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public interface OnTurnOverListener {
		public void onTurnOver();
	}

}
