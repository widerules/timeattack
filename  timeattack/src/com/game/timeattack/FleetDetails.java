package com.game.timeattack;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.game.timeattack.provider.TimeAttack.Fleet;

public class FleetDetails extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private static final int EDITION_CHILD = 2;
	private static final String TAG = "FleetDetails";
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
		mRadioGroup.setOnCheckedChangeListener(this);

		Cursor fleetCursor = getContentResolver().query(Fleet.CONTENT_URI,
				new String[] { Fleet.DELTA }, Fleet._ID + "=" + mChildId, null,
				null);
		fleetCursor.moveToFirst();

		int delta = Utils.getIntFromCol(fleetCursor, Fleet.DELTA);
		if (delta < 0) {
			mAfter.setChecked(true);
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
				Fleet.DELTA, Fleet.LAUNCH_TIME };
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
		cal.setLenient(true);
		// cal.set(Calendar.HOUR_OF_DAY, Utils.getIntFromCol(cursor, Fleet.H));
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

		long time = Utils.getLongFromCol(cursor, Fleet.LAUNCH_TIME);
		cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		mLaunchAt.setText(Utils.formatCalendar(cal, Utils.DAY_2_DIGITS) + " "
				+ Utils.formatCalendar(cal, Utils.LOCALIZED_MONTH_ABR) + " "
				+ Utils.formatCalendar(cal, Utils.FULL_12H_TIME));
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, Edit1.class);
		intent.putExtra("groupId", (int) mGroupId);
		intent.putExtra("childId", (int) mChildId);
		intent.putExtra("code", EDITION_CHILD);
		Log.d(TAG, "edited Group=" + mGroupId + " child=" + mChildId);
		switch (v.getId()) {
		case R.id.travel_duration_edittext:
			intent.putExtra("HIDE_DELTA", true);
			intent.putExtra("HIDE_NAME", true);
			intent.putExtra("HIDE_DATE", true);
			break;
		case R.id.delta_edittext:
			intent.putExtra("HIDE_H_M_S", true);
			intent.putExtra("HIDE_NAME", true);
			intent.putExtra("HIDE_DATE", true);
			break;
		case R.id.fleet_name_edittext:
			intent.putExtra("HIDE_DELTA", true);
			intent.putExtra("HIDE_H_M_S", true);
			intent.putExtra("HIDE_DATE", true);
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
				// calcChild();
			}
			break;
		case R.id.delta_after:
			if (delta > 0) {
				values.put(Fleet.DELTA, -delta);
				getContentResolver().update(Fleet.CONTENT_URI, values, where,
						null);
				// calcChild();
			}
			break;
		default:
			throw new IllegalArgumentException("Radio Button not handled");
		}
		update();
	}

	// public void calcChild() {
	// Calendar cal = Calendar.getInstance();
	// String[] projection = { Attack.H, Attack.M, Attack.S };
	// String selection = Attack._ID + "=" + mGroupId;
	// Cursor cursor = getContentResolver().query(Attack.CONTENT_URI,
	// projection, selection, null, null);
	// cursor.moveToFirst();
	// int h1 = new Integer(cursor.getString(cursor
	// .getColumnIndexOrThrow(Attack.H)));
	// int h2 = new Integer(cursor.getString(cursor
	// .getColumnIndexOrThrow(Attack.M)));
	// int h3 = new Integer(cursor.getString(cursor
	// .getColumnIndexOrThrow(Attack.S)));
	// String[] projection2 = { Fleet.H, Fleet.M, Fleet.S, Fleet.DELTA };
	// String selection2 = Fleet._ID + "=" + mChildId;
	// Cursor cursor2 = getContentResolver().query(Fleet.CONTENT_URI,
	// projection2, selection2, null, null);
	// cursor2.moveToFirst();
	// String h = cursor2.getString(cursor2.getColumnIndexOrThrow(Fleet.H));
	// String m = cursor2.getString(cursor2.getColumnIndexOrThrow(Fleet.M));
	// String s = cursor2.getString(cursor2.getColumnIndexOrThrow(Fleet.S));
	// String d = cursor2
	// .getString(cursor2.getColumnIndexOrThrow(Fleet.DELTA));
	// int hh = new Integer(h);
	// int mm = new Integer(m);
	// int ss = new Integer(s);
	// int dd = new Integer(d);
	// // if (DELTA_NEGATIVE) {
	// // dd = -dd;
	// // }
	//
	// cal.set(Calendar.HOUR_OF_DAY, h1);
	// cal.set(Calendar.MINUTE, h2);
	// cal.set(Calendar.SECOND, h3);
	//
	// cal.add(Calendar.HOUR_OF_DAY, -hh);
	// cal.add(Calendar.MINUTE, -mm);
	// cal.add(Calendar.SECOND, -ss);
	//
	// cal.add(Calendar.SECOND, -dd);
	//
	// Date time = cal.getTime();
	// String timeToLaunch = simpleDateFormat.format(time);
	// ContentValues values = new ContentValues();
	// values.put(Fleet.LAUNCH_TIME, timeToLaunch);
	// String where = "_id=" + mChildId;
	// int update = getContentResolver().update(Fleet.CONTENT_URI, values,
	// where, null);
	// Log.d(TAG, "number of lines modified after calculation:" + update);
	//
	// }
}
