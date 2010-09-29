package com.game.timeattack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.game.timeattack.provider.TimeAttack.Fleet;

public class AlarmReceiver extends BroadcastReceiver {
	private static final int ALARM_ID = 1;
	private String mFleetName;
	private int mChildId;
	private NotificationManager notificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		if (extras.isEmpty()) {
			return;
		}
		mFleetName = extras.getString("fleetName");
		mChildId = extras.getInt("childId");
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.status;
		CharSequence tickerText = context
				.getString(R.string.notification_ticker)
				+ mFleetName;
		CharSequence contentTitle = context
				.getString(R.string.notification_title);
		CharSequence contentText = context
				.getString(R.string.notification_content_start)
				+ mFleetName
				+ context.getString(R.string.notification_content_end);
		Intent notificationIntent = new Intent(context,
				MainExpandableListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.vibrate = new long[] { 0, 200, 100, 200, 100, 200 };
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(ALARM_ID + mChildId, notification);

		ContentValues values = new ContentValues();
		values.put(Fleet.ALARM_ACTIVATED, "false");
		context.getContentResolver().update(Fleet.CONTENT_URI, values,
				Fleet._ID + "=" + mChildId, null);

	}
}
