package edu.neu.madcourse.bojunpan.communication;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import edu.neu.madcourse.bojunpan.R;
import edu.neu.madcourse.bojunpan.twoplayergame.TwoPlayerGame_AsynGame;
import edu.neu.madcourse.bojunpan.twoplayergame.TwoPlayerGame_Shaking;
import edu.neu.madcourse.bojunpan.twoplayergame.TwoPlayerGame_SynGame;
import edu.neu.mhealth.api.KeyValueAPI;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	private ToneGenerator toneGenerator;
	NotificationCompat.Builder builder;
	static final String TAG = "GCM_Communication";
	MediaPlayer mediaPlayer;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "  I'm reciving data....");
		String alertText = "";
		String titleText = "";
		String contentText = "";
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		Log.d(String.valueOf(extras.size()), extras.toString());
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			alertText = KeyValueAPI.get(Communication_Globals.BACKUP_USERNAME,
					Communication_Globals.BACKUP_PASSWORD, "alertText");
			titleText = KeyValueAPI.get(Communication_Globals.BACKUP_USERNAME,
					Communication_Globals.BACKUP_PASSWORD, "titleText");
			contentText = KeyValueAPI.get(
					Communication_Globals.BACKUP_USERNAME,
					Communication_Globals.BACKUP_PASSWORD, "contentText");
			sendNotification(alertText, titleText, contentText);
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	public void sendNotification(String alertText, String titleText,
			String contentText) {
		Log.d(TAG, "???sendNotification???");
		if (titleText.equals("Register") || titleText.equals("Unregister")
				|| titleText.equals("Sending Message")) {
			mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Communication_Globals.is_client = true;

			Log.d("Is in Front???",
					String.valueOf(Communication_Globals.is_front));
			Intent notificationIntent;
			notificationIntent = new Intent(
					this,
					edu.neu.madcourse.bojunpan.communication.Communication_Main.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.d("titleText", titleText);
			if (Communication_Globals.is_front && titleText.contains("Send")) {
				startActivity(notificationIntent);
				mediaPlayer = MediaPlayer.create(this,
						Settings.System.DEFAULT_NOTIFICATION_URI);
				mediaPlayer.start();
			} else {
				Intent i = new Intent(this, Communication_Main.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent intent = PendingIntent.getActivity(this, 0, i,
						PendingIntent.FLAG_UPDATE_CURRENT);
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this)
						.setSmallIcon(R.drawable.ic_stat_cloud)
						.setContentTitle(titleText)
						.setStyle(
								new NotificationCompat.BigTextStyle()
										.bigText(contentText))
						.setContentText(contentText).setTicker(alertText)
						.setAutoCancel(true)
						.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
				mBuilder.setContentIntent(intent);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		} else if (titleText.equals("login")) {
			Intent twoplayer_login = new Intent(this,
					TwoPlayerGame_Shaking.class);
			twoplayer_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(twoplayer_login);
		}
		else if(titleText.equals("Alert")) {
			mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent i = new Intent(this, TwoPlayerGame_SynGame.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent intent = PendingIntent.getActivity(this, 0, i,
					PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this)
					.setSmallIcon(R.drawable.ic_stat_cloud)
					.setContentTitle(titleText)
					.setStyle(
							new NotificationCompat.BigTextStyle()
									.bigText(contentText))
					.setContentText(contentText).setTicker(alertText)
					.setAutoCancel(true)
					.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
			mBuilder.setContentIntent(intent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}
		else if(titleText.equals("take turn")) {
			mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent i = new Intent(this, TwoPlayerGame_AsynGame.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent intent = PendingIntent.getActivity(this, 0, i,
					PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this)
					.setSmallIcon(R.drawable.ic_stat_cloud)
					.setContentTitle(titleText)
					.setStyle(
							new NotificationCompat.BigTextStyle()
									.bigText(contentText))
					.setContentText(contentText).setTicker(alertText)
					.setAutoCancel(true)
					.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
			mBuilder.setContentIntent(intent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}
	}

}