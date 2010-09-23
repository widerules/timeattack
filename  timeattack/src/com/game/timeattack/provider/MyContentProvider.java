package com.game.timeattack.provider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.game.timeattack.Utils;
import com.game.timeattack.provider.TimeAttack.Attack;
import com.game.timeattack.provider.TimeAttack.Fleet;

public class MyContentProvider extends ContentProvider {
	public static final String PROVIDER_NAME = "com.game.timeattack.provider.MyContentProvider";

	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ PROVIDER_NAME);

	private static final String tag = "MyContentProvider";
	private static final String pre = "DBOpenHelper: ";
	private static final String DATABASE_NAME = "timeattackdb";
	private static final int DATABASE_VERSION = 6;

	private static final UriMatcher uriMatcher;

	private static final Map<String, String> sAttackProjectionMap;
	private static final Map<String, String> sFleetProjectionMap;

	private static final int ATTACKS = 1;
	private static final int ATTACK_ID = 2;
	private static final int FLEETS = 3;
	private static final int FLEET_ID = 4;

	private static final String TAG = "MyContentProvider";

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm:ss");

	public static class MyDbOpenHelper extends SQLiteOpenHelper {
		public MyDbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(tag, pre + "Creating a new DB");

			db.execSQL(Attack.TABLE_CREATE);
			db.execSQL(Fleet.TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(tag, pre + "Upgrading from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Attack.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + Fleet.TABLE_NAME);
			onCreate(db);
		}
	}

	private MyDbOpenHelper dbHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case ATTACKS:
			count = db.delete(Attack.TABLE_NAME, selection, selectionArgs);
			break;
		case ATTACK_ID:
			String attack_id = uri.getPathSegments().get(1);
			count = db.delete(Attack.TABLE_NAME, Attack._ID
					+ "="
					+ attack_id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		case FLEETS:
			count = db.delete(Fleet.TABLE_NAME, selection, selectionArgs);
			break;

		case FLEET_ID:
			String fleet_id = uri.getPathSegments().get(1);
			count = db.delete(Fleet.TABLE_NAME, Fleet._ID
					+ "="
					+ fleet_id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ATTACKS:
			return Attack.CONTENT_TYPE;
		case ATTACK_ID:
			return Attack.CONTENT_ITEM_TYPE;
		case FLEETS:
			return Fleet.CONTENT_TYPE;
		case FLEET_ID:
			return Fleet.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		switch (uriMatcher.match(uri)) {
		case ATTACKS:
		case FLEETS:
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		switch (uriMatcher.match(uri)) {
		case ATTACKS:
			Calendar cal = Calendar.getInstance();

			if (values.containsKey(Attack.NAME) == false) {
				values.put(Attack.NAME, "Attack name");
			}
			if (values.containsKey(Attack.YEAR) == false) {
				values.put(Attack.YEAR, "" + Utils.getFromCalendar(cal, "%tY"));
			}
			if (values.containsKey(Attack.MONTH) == false) {
				int month = Utils.sToI(Utils.getFromCalendar(cal, "%tm"));
				values.put(Attack.MONTH, "" + month);
			}
			if (values.containsKey(Attack.DAY) == false) {
				int day = Utils.sToI(Utils.getFromCalendar(cal, "%td"));
				values.put(Attack.DAY, "" + (day + 1));
			}
			if (values.containsKey(Attack.H) == false) {
				values.put(Attack.H, "01");
			}
			if (values.containsKey(Attack.M) == false) {
				values.put(Attack.M, "01");
			}
			break;
		case FLEETS:
			if (values.containsKey(Fleet.GROUP_ID) == false) {
				values.put(Fleet.GROUP_ID, 0);
			}
			if (values.containsKey(Fleet.NAME) == false) {
				values.put(Fleet.NAME, "Fleet name");
			}
			if (values.containsKey(Fleet.H) == false) {
				values.put(Fleet.H, "02");
			}
			if (values.containsKey(Fleet.M) == false) {
				values.put(Fleet.M, "02");
			}
			if (values.containsKey(Fleet.S) == false) {
				values.put(Fleet.S, "02");
			}
			if (values.containsKey(Fleet.DELTA) == false) {
				values.put(Fleet.DELTA, "00");
			}
			if (values.containsKey(Fleet.LAUNCH_TIME) == false) {
				values.put(Fleet.LAUNCH_TIME, " ");
			}
			break;
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = 0;
		switch (uriMatcher.match(uri)) {
		case ATTACKS:
			rowId = db.insert(Attack.TABLE_NAME, Attack.NAME, values);
			break;
		case FLEETS:
			rowId = db.insert(Fleet.TABLE_NAME, Fleet.NAME, values);
			break;
		}

		if (rowId > 0) {
			switch (uriMatcher.match(uri)) {
			case ATTACKS:
				Uri attackUri = ContentUris.withAppendedId(Attack.CONTENT_URI,
						rowId);
				getContext().getContentResolver().notifyChange(attackUri, null);
				return attackUri;
			case FLEETS:
				Uri fleetUri = ContentUris.withAppendedId(Fleet.CONTENT_URI,
						rowId);
				getContext().getContentResolver().notifyChange(fleetUri, null);
				return fleetUri;
			}
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new MyDbOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String lastPathSegment = uri.getLastPathSegment();

		qb.setTables(lastPathSegment);
		switch (uriMatcher.match(uri)) {
		case ATTACKS:
			qb.setProjectionMap(sAttackProjectionMap);
			break;
		case ATTACK_ID:
			qb.setProjectionMap(sAttackProjectionMap);
			qb.appendWhere(Attack._ID + "=" + uri.getPathSegments().get(1));
			break;
		case FLEETS:
			qb.setProjectionMap(sFleetProjectionMap);
			break;
		case FLEET_ID:
			qb.setProjectionMap(sFleetProjectionMap);
			qb.appendWhere(Fleet._ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = qb.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case ATTACKS:
			count = db.update(Attack.TABLE_NAME, values, selection,
					selectionArgs);
			String[] columns = { Attack._ID };
			Cursor cursor = db.query(Attack.TABLE_NAME, columns, selection,
					null, null, null, null);
			cursor.moveToFirst();
			int agroupId = cursor.getInt(cursor
					.getColumnIndexOrThrow(Attack._ID));
			calcGroup(db, agroupId);
			break;
		case ATTACK_ID:
			String attack_id = uri.getPathSegments().get(1);
			count = db.update(Attack.TABLE_NAME, values, Attack._ID
					+ "="
					+ attack_id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		case FLEETS:
			count = db.update(Fleet.TABLE_NAME, values, selection,
					selectionArgs);
			String[] columns1 = { Fleet._ID, Fleet.GROUP_ID };
			Cursor cursor1 = db.query(Fleet.TABLE_NAME, columns1, selection,
					null, null, null, null);
			cursor1.moveToFirst();
			int achildId = cursor1.getInt(cursor1
					.getColumnIndexOrThrow(Fleet._ID));
			int agroupId1 = cursor1.getInt(cursor1
					.getColumnIndexOrThrow(Fleet.GROUP_ID));
			calcChild(db, agroupId1, achildId);
			break;
		case FLEET_ID:
			String fleet_id = uri.getPathSegments().get(1);
			count = db.update(Fleet.TABLE_NAME, values, Fleet._ID
					+ "="
					+ fleet_id
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(PROVIDER_NAME, "attack", ATTACKS);
		uriMatcher.addURI(PROVIDER_NAME, "attack/#", ATTACK_ID);

		uriMatcher.addURI(PROVIDER_NAME, "fleet", FLEETS);
		uriMatcher.addURI(PROVIDER_NAME, "fleet/#", FLEET_ID);

		sAttackProjectionMap = new HashMap<String, String>();
		sAttackProjectionMap.put(Attack._ID, Attack._ID);
		sAttackProjectionMap.put(Attack.NAME, Attack.NAME);
		sAttackProjectionMap.put(Attack.YEAR, Attack.YEAR);
		sAttackProjectionMap.put(Attack.MONTH, Attack.MONTH);
		sAttackProjectionMap.put(Attack.DAY, Attack.DAY);
		sAttackProjectionMap.put(Attack.H, Attack.H);
		sAttackProjectionMap.put(Attack.M, Attack.M);
		sAttackProjectionMap.put(Attack.S, Attack.S);

		sFleetProjectionMap = new HashMap<String, String>();
		sFleetProjectionMap.put(Fleet._ID, Fleet._ID);
		sFleetProjectionMap.put(Fleet.GROUP_ID, Fleet.GROUP_ID);
		sFleetProjectionMap.put(Fleet.NAME, Fleet.NAME);
		sFleetProjectionMap.put(Fleet.H, Fleet.H);
		sFleetProjectionMap.put(Fleet.M, Fleet.M);
		sFleetProjectionMap.put(Fleet.S, Fleet.S);
		sFleetProjectionMap.put(Fleet.DELTA, Fleet.DELTA);
		sFleetProjectionMap.put(Fleet.LAUNCH_TIME, Fleet.LAUNCH_TIME);

	}

	public void calcGroup(SQLiteDatabase aDb, int agroupId) {
		String[] projection = { Fleet._ID };
		String selection = Fleet.GROUP_ID + "=" + agroupId;
		Cursor cursor = aDb.query(Fleet.TABLE_NAME, projection, selection,
				null, null, null, null);
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndexOrThrow(Fleet._ID));
			calcChild(aDb, agroupId, id);
		}
	}

	public void calcChild(SQLiteDatabase aDb, int agroupId, int achildId) {
		String[] projection = { Attack.YEAR, Attack.MONTH, Attack.DAY,
				Attack.H, Attack.M, Attack.S };
		String selection = Attack._ID + "=" + agroupId;
		Cursor cursor = aDb.query(Attack.TABLE_NAME, projection, selection,
				null, null, null, null);
		cursor.moveToFirst();
		int attackYear = Utils.getIntFromCol(cursor, Attack.YEAR);
		int attackMonth = Utils.getIntFromCol(cursor, Attack.MONTH);
		int attackDay = Utils.getIntFromCol(cursor, Attack.DAY);
		int attackH = Utils.getIntFromCol(cursor, Attack.H);
		int attackM = Utils.getIntFromCol(cursor, Attack.M);
		int attackS = Utils.getIntFromCol(cursor, Attack.S);
		String[] projection2 = { Fleet.H, Fleet.M, Fleet.S, Fleet.DELTA };
		String selection2 = Fleet._ID + "=" + achildId;
		Cursor cursor2 = aDb.query(Fleet.TABLE_NAME, projection2, selection2,
				null, null, null, null);
		cursor2.moveToFirst();
		int fleetH = Utils.getIntFromCol(cursor2, Fleet.H);
		int fleetM = Utils.getIntFromCol(cursor2, Fleet.M);
		int fleetS = Utils.getIntFromCol(cursor2, Fleet.S);
		int fleetDelta = Utils.getIntFromCol(cursor2, Fleet.DELTA);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, attackYear);
		cal.set(Calendar.MONTH, attackMonth - 1);
		cal.set(Calendar.DAY_OF_MONTH, attackDay);
		cal.set(Calendar.HOUR_OF_DAY, attackH);
		cal.set(Calendar.MINUTE, attackM);
		cal.set(Calendar.SECOND, attackS);

		Log.d(TAG, "new time to launch:" + attackYear + " " + attackMonth + " "
				+ attackDay);
		// cal.add(Calendar.HOUR_OF_DAY, -hh);
		// cal.add(Calendar.MINUTE, -mm);
		// cal.add(Calendar.SECOND, -ss);
		//
		// cal.add(Calendar.SECOND, -dd);
		cal = Utils.addToCalendar(cal, 0, -fleetH, -fleetM, -fleetS
				- fleetDelta);
		String timeToLaunch = Utils.formatCalendar(cal, "%td") + " "
				+ Utils.formatCalendar(cal, "%tb") + " "
				+ Utils.formatCalendar(cal, "%tr");
		ContentValues values = new ContentValues();
		values.put(Fleet.LAUNCH_TIME, timeToLaunch);
		String where = "_id=" + achildId;
		int update = aDb.update(Fleet.TABLE_NAME, values, where, null);
		Log.d(TAG, "number of lines modified after calculation:" + update);

	}
}
