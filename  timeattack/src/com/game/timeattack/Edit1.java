package com.game.timeattack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.game.timeattack.provider.TimeAttack.Attack;
import com.game.timeattack.provider.TimeAttack.Fleet;

public class Edit1 extends Activity implements OnClickListener {

	private static final int EDITION_GROUP = 1;
	private static final int EDITION_CHILD = 2;
	private static final int EDITION_ADD_GROUP = 3;
	private static final int EDITION_ADD_CHILD = 4;
	private static boolean HIDE_DELTA = false;
	private static boolean HIDE_H_M_S = false;
	private static boolean HIDE_NAME = false;
	private static boolean HIDE_DATE = false;
	final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm:ss");
	final static String TAG = "Edit1";
	EditText mName, mH, mM, mS, mD;
	Button cancel, ok, datePlus, dateMinus;
	TableRow pluses, textValues, minuses, titles;
	TextView date;
	private int CODE_OK = 1;
	int groupId, childId, code, newGroupId, newChildId;
	private int newlyInsertedChildId = -1;
	private int newlyInsertedGroupId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit1);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras == null) {
			throw new IllegalArgumentException(
					"Need groupId, childId and code to launch");
		}
		groupId = extras.getInt("groupId");
		childId = extras.getInt("childId");
		code = extras.getInt("code");
		HIDE_DELTA = extras.getBoolean("HIDE_DELTA");
		HIDE_H_M_S = extras.getBoolean("HIDE_H_M_S");
		HIDE_NAME = extras.getBoolean("HIDE_NAME");
		HIDE_DATE = extras.getBoolean("HIDE_DATE");

		if (!HIDE_NAME || !HIDE_DELTA) {
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
		Cursor cursor = null;
		ContentValues values;
		String d;
		switch (code) {
		case EDITION_ADD_GROUP:
			values = new ContentValues();
			values.put(Attack.NAME, "Attack");
			values.put(Attack.H, "6");
			values.put(Attack.M, "0");
			values.put(Attack.S, "0");
			Uri uri = getContentResolver().insert(Attack.CONTENT_URI, values);
			newGroupId = new Integer((uri.getPathSegments().get(1)));
			Log.d(TAG, "new group ID=" + newGroupId);
			String[] projection3 = { Attack.NAME, Attack.YEAR, Attack.MONTH,
					Attack.DAY, Attack.H, Attack.M, Attack.S };
			String selection3 = Attack._ID + "=" + newGroupId;
			cursor = getContentResolver().query(Attack.CONTENT_URI,
					projection3, selection3, null, null);
			newlyInsertedGroupId = newGroupId;
			break;
		case EDITION_GROUP:
			HIDE_DELTA = true;
			String[] projection2 = { Attack.NAME, Attack.YEAR, Attack.MONTH,
					Attack.DAY, Attack.H, Attack.M, Attack.S };
			String selection2 = Attack._ID + "=" + groupId;
			cursor = getContentResolver().query(Attack.CONTENT_URI,
					projection2, selection2, null, null);
			break;
		case EDITION_ADD_CHILD:
			values = new ContentValues();
			values.put(Fleet.NAME, "New Fleet");
			values.put(Fleet.GROUP_ID, groupId);
			values.put(Fleet.H, "1");
			values.put(Fleet.M, "0");
			values.put(Fleet.S, "0");
			values.put(Fleet.DELTA, "0");
			Log.d(TAG, "add " + " groupID=" + groupId + " child=" + childId);
			Uri insert = getContentResolver().insert(Fleet.CONTENT_URI, values);
			Log.d(TAG, "inserted URI=" + insert);
			newChildId = new Integer((insert.getPathSegments().get(1)));
			String[] projection4 = { Fleet.NAME, Fleet.H, Fleet.M, Fleet.S,
					Fleet.DELTA };
			String selection4 = Fleet._ID + "=" + newChildId;
			cursor = getContentResolver().query(Fleet.CONTENT_URI, projection4,
					selection4, null, null);
			cursor.moveToFirst();
			d = cursor.getString(cursor.getColumnIndexOrThrow(Fleet.DELTA));
			mD = (EditText) findViewById(R.id.d);
			mD.setText(d);
			newlyInsertedChildId = newChildId;
			break;
		case EDITION_CHILD:
			String[] projection = { Fleet.NAME, Fleet.H, Fleet.M, Fleet.S,
					Fleet.DELTA };
			String selection = Fleet._ID + "=" + childId;
			cursor = getContentResolver().query(Fleet.CONTENT_URI, projection,
					selection, null, null);
			cursor.moveToFirst();
			d = cursor.getString(cursor.getColumnIndexOrThrow(Fleet.DELTA));
			mD = (EditText) findViewById(R.id.d);
			int delta = 0;
			try {
				delta = new Integer(d);
			} catch (NumberFormatException e) {
				delta = 0;
			}
			// if (delta < 0) {
			// DELTA_NEGATIVE = true;
			// }
			mD.setText("" + Math.abs(delta));
			break;
		default:
			throw new IllegalArgumentException("Wrong Code");
		}

		String name = Utils.getStringFromCol(cursor, Attack.NAME);
		String h = Utils.getStringFromCol(cursor, Attack.H);
		String m = Utils.getStringFromCol(cursor, Attack.M);
		String s = Utils.getStringFromCol(cursor, Attack.S);
		if (childId == -1) {
			Calendar cal = Calendar.getInstance();
			int year = Utils.getIntFromCol(cursor, Attack.YEAR);
			int month = Utils.getIntFromCol(cursor, Attack.MONTH);
			int day = Utils.getIntFromCol(cursor, Attack.DAY);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);

			date = (TextView) findViewById(R.id.date);
			date.setText(Utils.getFromCalendar(cal, "%tF"));
		}

		Log.d(TAG, "received: code=" + code + " name=" + name + " groupId="
				+ groupId + " childId=" + childId);

		mName = (EditText) findViewById(R.id.name);
		mH = (EditText) findViewById(R.id.h);
		mM = (EditText) findViewById(R.id.m);
		mS = (EditText) findViewById(R.id.s);

		mName.setText(name);
		mH.setText(h);
		mM.setText(m);
		mS.setText(s);

		if (HIDE_NAME) {
			TextView label = (TextView) findViewById(R.id.label_name);
			label.setVisibility(View.GONE);
			mName.setVisibility(View.GONE);
		}
		if (HIDE_DATE) {
			RelativeLayout date = (RelativeLayout) findViewById(R.id.datelayout);
			date.setVisibility(View.GONE);
		}
		titles = (TableRow) findViewById(R.id.titles);
		pluses = (TableRow) findViewById(R.id.pluses);
		textValues = (TableRow) findViewById(R.id.values);
		minuses = (TableRow) findViewById(R.id.minuses);
		for (int i = 0; i < pluses.getChildCount(); i++) {
			pluses.getChildAt(i).setOnClickListener(this);
			minuses.getChildAt(i).setOnClickListener(this);

			if ((HIDE_DELTA && i == 3) || (HIDE_H_M_S && i < 3)) {
				titles.getChildAt(i).setVisibility(View.GONE);
				pluses.getChildAt(i).setVisibility(View.GONE);
				minuses.getChildAt(i).setVisibility(View.GONE);
				textValues.getChildAt(i).setVisibility(View.GONE);
			}
		}
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
		datePlus = (Button) findViewById(R.id.date_inc);
		datePlus.setOnClickListener(this);
		dateMinus = (Button) findViewById(R.id.date_dec);
		dateMinus.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.h_inc:
			changeTime(3600);
			break;
		case R.id.m_inc:
			changeTime(60);
			break;
		case R.id.s_inc:
			changeTime(1);
			break;
		case R.id.d_inc:
			try {
				Integer d = new Integer(mD.getText().toString());
				mD.setText("" + (d + 5));
			} catch (Exception e) {
				mD.setText("0");
			}
			break;
		case R.id.h_dec:
			changeTime(-3600);
			break;
		case R.id.m_dec:
			changeTime(-60);
			break;
		case R.id.s_dec:
			changeTime(-1);
			break;
		case R.id.d_dec:
			try {
				int d = new Integer(mD.getText().toString());
				if (d >= 5) {
					mD.setText("" + (d - 5));
				}
			} catch (NumberFormatException e) {
				mD.setText("0");
			}
			break;
		case R.id.cancel:
			if (newlyInsertedChildId != -1) {
				getContentResolver().delete(Fleet.CONTENT_URI,
						Fleet._ID + "=" + newlyInsertedChildId, null);
			}
			if (newlyInsertedGroupId != -1) {
				getContentResolver().delete(Attack.CONTENT_URI,
						Fleet._ID + "=" + newlyInsertedGroupId, null);
			}
			setResult(-1, null);
			this.finish();
			break;
		case R.id.ok:
			Intent data = getIntent();
			data.putExtra("groupPosition", groupId);
			data.putExtra("childPosition", childId);
			ContentValues values;
			setResult(CODE_OK, data);
			if (code == EDITION_CHILD) {
				values = new ContentValues();
				values.put(Fleet.NAME, mName.getText().toString());
				values.put(Fleet.H, mH.getText().toString());
				values.put(Fleet.M, mM.getText().toString());
				values.put(Fleet.S, mS.getText().toString());
				values.put(Fleet.DELTA, mD.getText().toString());
				int updatedchild = getContentResolver().update(
						Fleet.CONTENT_URI, values, Fleet._ID + "=" + childId,
						null);
				// calcChild(groupId, childId);
				Log.d(TAG, "URI Updated=" + updatedchild);
			} else if (code == EDITION_GROUP) {
				values = new ContentValues();
				values.put(Attack.NAME, mName.getText().toString());
				values.put(Attack.H, mH.getText().toString());
				values.put(Attack.M, mM.getText().toString());
				values.put(Attack.S, mS.getText().toString());
				int updatedgroup = getContentResolver().update(
						Attack.CONTENT_URI, values, Attack._ID + "=" + groupId,
						null);
				Log.d(TAG, "URI Updated=" + updatedgroup);
				// calcGroup(groupId);
			} else if (code == EDITION_ADD_GROUP) {
				values = new ContentValues();
				values.put(Attack.NAME, mName.getText().toString());
				values.put(Attack.H, mH.getText().toString());
				values.put(Attack.M, mM.getText().toString());
				values.put(Attack.S, mS.getText().toString());
				int updatedgroup = getContentResolver().update(
						Attack.CONTENT_URI, values,
						Attack._ID + "=" + newGroupId, null);
				Log.d(TAG, "URI Updated=" + updatedgroup);
			} else if (code == EDITION_ADD_CHILD) {
				values = new ContentValues();
				values.put(Fleet.NAME, mName.getText().toString());
				values.put(Fleet.H, mH.getText().toString());
				values.put(Fleet.M, mM.getText().toString());
				values.put(Fleet.S, mS.getText().toString());
				values.put(Fleet.DELTA, mD.getText().toString());
				int updatedchild = getContentResolver().update(
						Fleet.CONTENT_URI, values,
						Fleet._ID + "=" + newChildId, null);
				Log.d(TAG, "groupId=" + groupId + " newChildId=" + newChildId);
				// calcChild(groupId, newChildId);
				Log.d(TAG, "URI Updated=" + updatedchild);
			}
			finish();
			break;
		case R.id.date_inc:
			date.setText(changeDate(1));
			break;
		case R.id.date_dec:
			date.setText(changeDate(-1));
			break;
		default:
			throw new IllegalArgumentException("Wrong Button");
		}
	}

	private String changeDate(int i) {
		CharSequence dateString = date.getText();
		CharSequence yearS = dateString.subSequence(0, 4);
		CharSequence monthS = dateString.subSequence(5, 7);
		CharSequence dayS = dateString.subSequence(8, 10);
		int year = Utils.sToI((String) yearS);
		int month = Utils.sToI((String) monthS);
		int day = Utils.sToI((String) dayS);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.add(Calendar.DAY_OF_WEEK_IN_MONTH, i);
		return Utils.getFromCalendar(cal, "%tF");
	}

	private void changeTime(int seconds) {
		String h = mH.getText().toString();
		String m = mM.getText().toString();
		String s = mS.getText().toString();
		if (h.equalsIgnoreCase("")) {
			mH.setText("00");
		}
		;
		if (m.equalsIgnoreCase("")) {
			mM.setText("00");
		}
		;
		if (s.equalsIgnoreCase("")) {
			mS.setText("00");
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, new Integer(mH.getText().toString()));
		cal.set(Calendar.MINUTE, new Integer(mM.getText().toString()));
		cal.set(Calendar.SECOND, new Integer(mS.getText().toString()));

		cal.add(Calendar.SECOND, seconds);
		Date time = cal.getTime();
		simpleDateFormat.format(time);
		mH.setText("" + time.getHours());
		mM.setText("" + time.getMinutes());
		mS.setText("" + time.getSeconds());
	}

}
