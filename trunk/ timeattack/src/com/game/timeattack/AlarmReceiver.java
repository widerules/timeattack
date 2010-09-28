package com.game.timeattack;

import com.game.timeattack.provider.TimeAttack.Fleet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	private static final int ALARM_ID = 1;
	private String mFleetName;
	private int mChildId;
	private NotificationManager notificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Alarme activée", Toast.LENGTH_LONG).show();

		Bundle extras = intent.getExtras();
		if (extras.isEmpty()) {
			return;
		}
		mFleetName = extras.getString("fleetName");
		mChildId = extras.getInt("childId");
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.status;
		CharSequence tickerText = "Alarm fleet : " + mFleetName;
		CharSequence contentTitle = "Alarm";
		CharSequence contentText = "The fleet " + mFleetName
				+ " is about to be launched !";
		Intent notificationIntent = new Intent(context,
				MainExpandableListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.vibrate = new long[] { 0, 200, 100, 200, 100, 200 };
		notificationManager.notify(ALARM_ID + mChildId, notification);

		ContentValues values = new ContentValues();
		values.put(Fleet.ALARM_ACTIVATED, "false");
		context.getContentResolver().update(Fleet.CONTENT_URI, values,
				Fleet._ID + "=" + mChildId, null);

	}

}
