package com.game.timeattack;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.game.timeattack.provider.TimeAttack.Fleet;

public class FleetDetails extends Activity implements OnClickListener,
		OnCheckedChangeListener,
		android.widget.CompoundButton.OnCheckedChangeListener {
	private AlarmManager mAlarmManager;
	private static final int EDITION_CHILD = 2;
	private static final String TAG = "FleetDetails";
	private static final int ALARM_ID = 1;
	int mGroupId, mChildId;
	EditText mName, mDuration, mDelta, mAlarm;
	RadioGroup mRadioGroup;
	RadioButton mBefore, mAfter;
	CheckBox mAlarmCheckBox;
	TextView mLaunchAt;
	Button mOk, mCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.table_fleet_edit_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_fleet_detail_title);
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			throw new IllegalArgumentException(
					"Need groupId and childId to launch");
		}
		mGroupId = extras.getInt("groupId");
		mChildId = extras.getInt("childId");
		mName = (EditText) findViewById(R.id.fleet_name_edittext);
		mDuration = (EditText) findViewById(R.id.travel_duration_edittext);
		mDelta = (EditText) findViewById(R.id.delta_edittext);
		mAlarm = (EditText) findViewById(R.id.alarm_edittext);
		mRadioGroup = (RadioGroup) findViewById(R.id.delta_radio_group);
		mAlarmCheckBox = (CheckBox) findViewById(R.id.alarm_checkbox);
		mLaunchAt = (TextView) findViewById(R.id.launch_at_textview);
		mOk = (Button) findViewById(R.id.ok);
		mCancel = (Button) findViewById(R.id.cancel);
		mBefore = (RadioButton) findViewById(R.id.delta_before);
		mAfter = (RadioButton) findViewById(R.id.delta_after);

		mDuration.setOnClickListener(this);
		mDelta.setOnClickListener(this);
		mName.setOnClickListener(this);
		mAlarm.setOnClickListener(this);
		mRadioGroup.setOnCheckedChangeListener(this);
		mAlarmCheckBox.setOnCheckedChangeListener(this);

		Cursor fleetCursor = getContentResolver().query(Fleet.CONTENT_URI,
				new String[] { Fleet.DELTA, Fleet.ALARM_ACTIVATED },
				Fleet._ID + "=" + mChildId, null, null);
		fleetCursor.moveToFirst();

		int delta = Utils.getIntFromCol(fleetCursor, Fleet.DELTA);
		if (delta < 0) {
			mAfter.setChecked(true);
		}
		Boolean isAlarmActive = new Boolean(Utils.getStringFromCol(fleetCursor,
				Fleet.ALARM_ACTIVATED));
		if (isAlarmActive) {
			mAlarmCheckBox.setChecked(true);
		}

		TextView timeOfTheAttack = (TextView) findViewById(R.id.time_of_the_attack);
		TextView namelabel = (TextView) findViewById(R.id.attack_name);
		String dateAndTime = Utils.getAttackTime(this, mGroupId);
		timeOfTheAttack.setText(dateAndTime);
		String name = Utils.getAttackName(this, mGroupId);
		namelabel.setText(name);
	}

	@Override
	protected void onResume() {
		super.onResume();
		update();
	}

	private void update() {
		String[] projection = { Fleet.NAME, Fleet.H, Fleet.M, Fleet.S,
				Fleet.DELTA, Fleet.LAUNCH_TIME, Fleet.ALARM };
		String selection = Fleet._ID + "=" + mChildId;
		Cursor cursor = getContentResolver().query(Fleet.CONTENT_URI,
				projection, selection, null, null);

		cursor.moveToFirst();
		mName.setText(cursor
				.getString(cursor.getColumnIndexOrThrow(Fleet.NAME)));
		String d = cursor.getString(cursor.getColumnIndexOrThrow(Fleet.DELTA));
		int delta;
		try {
			delta = new Integer(d);
		} catch (Exception e) {
			delta = 0;
		}
		if (delta < 0) {
			mAfter.setChecked(true);
		}
		mDelta.setText("" + Math.abs(delta));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, Utils.getIntFromCol(cursor, Fleet.M));
		cal.set(Calendar.SECOND, Utils.getIntFromCol(cursor, Fleet.S));
		String h = Utils.getStringFromCol(cursor, Fleet.H);
		if (h.length() < 2) {
			if (h.length() == 0) {
				h = "00";
			} else {
				h = "0" + h;
			}
		}
		String duration = h + ":" + Utils.formatCalendar(cal, Utils.MINUTES)
				+ ":" + Utils.formatCalendar(cal, Utils.SECONDS);
		mDuration.setText(duration);

		long launchTime = Utils.getLongFromCol(cursor, Fleet.LAUNCH_TIME);
		cal = Calendar.getInstance();
		cal.setTimeInMillis(launchTime);
		mLaunchAt.setText(Utils.formatCalendar(cal, Utils.DAY_2_DIGITS) + " "
				+ Utils.formatCalendar(cal, Utils.LOCALIZED_MONTH_ABR) + " "
				+ Utils.formatCalendar(cal, Utils.FULL_12H_TIME));
		Calendar alarmCal = Calendar.getInstance();
		long alarmTime = Utils.getLongFromCol(cursor, Fleet.ALARM);
		alarmCal.setTimeInMillis(alarmTime);
		long difference = cal.getTimeInMillis() - alarmCal.getTimeInMillis();
		Calendar diffCal = Calendar.getInstance();
		diffCal.clear();
		diffCal.setTimeInMillis(difference);
		String alarmDelta = Utils.getFromCalendar(diffCal,
				Utils.HOUR_OF_DAY_24H)
				+ ":"
				+ Utils.getFromCalendar(diffCal, Utils.MINUTES)
				+ ":"
				+ Utils.getFromCalendar(diffCal, Utils.SECONDS);
		mAlarm.setText(alarmDelta);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, Edit1.class);
		intent.putExtra("groupId", (int) mGroupId);
		intent.putExtra("childId", (int) mChildId);
		Log.d(TAG, "edited Group=" + mGroupId + " child=" + mChildId);
		switch (v.getId()) {
		case R.id.travel_duration_edittext:
			intent.putExtra("code", EDITION_CHILD);
			intent.putExtra("HIDE_DELTA", true);
			intent.putExtra("HIDE_NAME", true);
			intent.putExtra("HIDE_DATE", true);
			break;
		case R.id.delta_edittext:
			intent.putExtra("code", EDITION_CHILD);
			intent.putExtra("HIDE_H_M_S", true);
			intent.putExtra("HIDE_NAME", true);
			intent.putExtra("HIDE_DATE", true);
			break;
		case R.id.fleet_name_edittext:
			intent.putExtra("code", EDITION_CHILD);
			intent.putExtra("HIDE_DELTA", true);
			intent.putExtra("HIDE_H_M_S", true);
			intent.putExtra("HIDE_DATE", true);
			break;
		case R.id.alarm_edittext:
			intent.setClass(this, EditAlarm.class);
			break;
		default:
			throw new IllegalArgumentException("Button not handled");
		}

		startActivity(intent);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		String[] projection = { Fleet.DELTA };
		String selection = Fleet._ID + "=" + mChildId;
		Cursor cursor = getContentResolver().query(Fleet.CONTENT_URI,
				projection, selection, null, null);
		cursor.moveToFirst();
		String d = cursor.getString(cursor.getColumnIndexOrThrow(Fleet.DELTA));
		int delta = new Integer(d);
		ContentValues values = new ContentValues();
		String where = Fleet._ID + "=" + mChildId;
		switch (checkedId) {
		case R.id.delta_before:
			if (delta < 0) {
				values.put(Fleet.DELTA, -delta);
				getContentResolver().update(Fleet.CONTENT_URI, values, where,
						null);
			}
			break;
		case R.id.delta_after:
			if (delta > 0) {
				values.put(Fleet.DELTA, -delta);
				getContentResolver().update(Fleet.CONTENT_URI, values, where,
						null);
			}
			break;
		default:
			throw new IllegalArgumentException("Radio Button not handled");
		}
		update();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		switch (buttonView.getId()) {
		case R.id.alarm_checkbox:
			if (isChecked) {
				Cursor fleetCursor = getContentResolver().query(
						Fleet.CONTENT_URI,
						new String[] { Fleet.ALARM, Fleet.NAME,
								Fleet.ALARM_ACTIVATED },
						Fleet._ID + "=" + mChildId, null, null);
				fleetCursor.moveToFirst();
				String name = Utils.getStringFromCol(fleetCursor, Fleet.NAME);
				// long when = Utils.getLongFromCol(cursor, Fleet.ALARM);
				long when = System.currentTimeMillis() + 10000;
				Intent intent = new Intent(this, AlarmReceiver.class);
				intent.putExtra("fleetName", name);
				intent.putExtra("childId", mChildId);
				PendingIntent pendingIntent = PendingIntent
						.getBroadcast(getApplicationContext(), ALARM_ID
								+ mChildId, intent, 0);
				mAlarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);

				ContentValues values = new ContentValues();
				values.put(Fleet.ALARM_ACTIVATED, "true");
				getContentResolver().update(Fleet.CONTENT_URI, values,
						Fleet._ID + "=" + mChildId, null);
			} else {
				PendingIntent pendingIntent = PendingIntent
						.getBroadcast(getApplicationContext(), ALARM_ID
								+ mChildId, new Intent(getApplicationContext(),
								AlarmReceiver.class), 0);
				mAlarmManager.cancel(pendingIntent);
				ContentValues values = new ContentValues();
				values.put(Fleet.ALARM_ACTIVATED, "false");
				getContentResolver().update(Fleet.CONTENT_URI, values,
						Fleet._ID + "=" + mChildId, null);
			}
			break;
		default:
			throw new IllegalArgumentException("CheckButton not handled");
		}
	}
}
