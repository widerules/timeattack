package com.game.timeattack;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.game.timeattack.provider.TimeAttack.Attack;
import com.game.timeattack.provider.TimeAttack.Fleet;

public class EditAlarm extends Activity implements OnClickListener {

	final static String TAG = "EditAlarm";
	EditText mH, mM, mS;
	Button cancel, ok;
	private int CODE_OK = 1;
	int groupId, childId;
	Cursor attackCursor, fleetCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_alarm);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras == null) {
			throw new IllegalArgumentException(
					"Need groupId, childId and code to launch");
		}
		groupId = extras.getInt("groupId");
		childId = extras.getInt("childId");
		Uri uri = Uri.withAppendedPath(Attack.CONTENT_URI, "" + groupId);
		attackCursor = getContentResolver().query(uri, null, null, null, null);
		attackCursor.moveToFirst();
		uri = Uri.withAppendedPath(Fleet.CONTENT_URI, "" + childId);
		fleetCursor = getContentResolver().query(uri, null, null, null, null);
		fleetCursor.moveToFirst();
		mH = (EditText) findViewById(R.id.h);
		mM = (EditText) findViewById(R.id.m);
		mS = (EditText) findViewById(R.id.s);

		long alarmTime = Utils.getLongFromCol(fleetCursor, Fleet.ALARM);
		Calendar cal = Calendar.getInstance();
		long launchTime = Utils.getLongFromCol(fleetCursor, Fleet.LAUNCH_TIME);
		cal.setTimeInMillis(launchTime);
		long difference = launchTime - alarmTime;
		Calendar diff = Calendar.getInstance();
		diff.clear();
		diff.setTimeInMillis(difference);
		diff.add(Calendar.HOUR_OF_DAY, -1);
		mH.setText(Utils.getFromCalendar(diff, Utils.HOUR_OF_DAY_24H));
		mM.setText(Utils.getFromCalendar(diff, Utils.MINUTES));
		mS.setText(Utils.getFromCalendar(diff, Utils.SECONDS));
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.cancel:
			setResult(-1, null);
			this.finish();
			break;
		case R.id.ok:
			checkTextValues();
			Calendar cal = Calendar.getInstance();
			long launchTime = Utils.getLongFromCol(fleetCursor,
					Fleet.LAUNCH_TIME);
			cal.setTimeInMillis(launchTime);

			int hours = Utils.sToI(mH.getText().toString());
			int minutes = Utils.sToI(mM.getText().toString());
			int seconds = Utils.sToI(mS.getText().toString());
			Utils.addToCalendar(cal, 0, 0, 0, -hours, -minutes, -seconds);
			ContentValues values = new ContentValues();
			values.put(Fleet.ALARM, cal.getTimeInMillis());
			getContentResolver().update(Fleet.CONTENT_URI, values,
					Fleet._ID + "=" + childId, null);
			setResult(CODE_OK);
			finish();
			break;
		default:
			throw new IllegalArgumentException("Wrong Button");
		}
	}

	private void checkTextValues() {
		Editable h, m, s;
		h = mH.getText();
		m = mM.getText();
		s = mS.getText();
		if (h.length() == 0) {
			h.insert(0, "0");
		}
		if (m.length() == 0) {
			m.insert(0, "0");
		}
		if (s.length() == 0) {
			s.insert(0, "0");
		}
		if (Utils.sToI(m.toString()) > 60) {
			m.clear();
			m.insert(0, "0");
		}
		if (Utils.sToI(s.toString()) > 60) {
			s.clear();
			s.insert(0, "0");
		}
	}
}
