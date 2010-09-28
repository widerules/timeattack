package com.game.timeattack;

import java.util.Calendar;
import java.util.Formatter;

import android.content.Context;
import android.database.Cursor;

import com.game.timeattack.provider.TimeAttack.Attack;

public class Utils {

	public static final String FULL_DATE = "%tF";
	public static final String FULL_12H_TIME = "%tr";
	public static final String FULL_24H_TIME = "%tT";
	public static final String YEAR_4_DIGITS = "%tY";
	public static final String MONTH_2_DIGITS = "%tm";
	public static final String DAY_2_DIGITS = "%td";
	public static final String HOUR_OF_DAY_24H = "%tH";
	public static final String HOUR_OF_DAY_12H = "%tI";
	public static final String MINUTES = "%tM";
	public static final String SECONDS = "%tS";
	public static final String LOCALIZED_MONTH_ABR = "%tb";
	public static final String MILLISECONDS_SINCE_EPOCH = "%tQ";

	public static int getIntFromCol(Cursor cursor, String colName) {
		return sToI(getStringFromCol(cursor, colName));
	}

	public static String getStringFromCol(Cursor cursor, String colName) {
		return cursor.getString(cursor.getColumnIndexOrThrow(colName));
	}

	public static long getLongFromCol(Cursor cursor, String colName) {
		String string = Utils.getStringFromCol(cursor, colName);
		Long l;
		try {
			l = new Long(string);
		} catch (Exception e) {
			l = new Long(0);
		}
		return l;
	}

	public static int sToI(String s) {
		int i = 0;
		try {
			i = new Integer(s);
		} catch (Exception e) {
			i = 0;
		}
		return i;
	}

	public static String checkStringLenght(String s) {
		String tmp = "0";
		if (s.length() == 1) {
			return tmp + s;
		}
		return s;
	}

	/**
	 * 
	 * @param cal
	 *            ,
	 * @param year
	 *            ,
	 * @param month
	 *            ,
	 * @param days
	 *            ,
	 * @param hours
	 *            ,
	 * @param minutes
	 *            ,
	 * @param seconds
	 *            ,
	 * @return a calendar with the parameters added
	 */
	public static void addToCalendar(Calendar cal, int year, int month,
			int days, int hours, int minutes, int seconds) {
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DAY_OF_MONTH, days);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		cal.add(Calendar.MINUTE, minutes);
		cal.add(Calendar.SECOND, seconds);
		// return cal;
	}

	public static String formatCalendar(Calendar cal, String format) {
		Formatter formatter = new Formatter();
		formatter.format(format, cal);
		return formatter.toString();
	}

	public static String getFromCalendar(Calendar cal, String format) {

		Formatter formatter = new Formatter();
		formatter.format(format, cal);

		String tmp;
		try {
			tmp = formatter.toString();
		} catch (Exception e) {
			tmp = "0";
		}
		return tmp;
	}

	public static String getAttackTime(Context context, int groupId) {
		String[] projection = { Attack.YEAR, Attack.MONTH, Attack.DAY,
				Attack.H, Attack.M, Attack.S };
		String selection = Attack._ID + "=" + groupId;
		Cursor attackCursor = context.getContentResolver().query(
				Attack.CONTENT_URI, projection, selection, null, null);
		attackCursor.moveToFirst();
		int year = Utils.getIntFromCol(attackCursor, Attack.YEAR);
		int month = Utils.getIntFromCol(attackCursor, Attack.MONTH);
		int day = Utils.getIntFromCol(attackCursor, Attack.DAY);
		int h = Utils.getIntFromCol(attackCursor, Attack.H);
		int m = Utils.getIntFromCol(attackCursor, Attack.M);
		int s = Utils.getIntFromCol(attackCursor, Attack.S);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, h);
		calendar.set(Calendar.MINUTE, m);
		calendar.set(Calendar.SECOND, s);
		return Utils.formatCalendar(calendar, FULL_DATE) + " "
				+ Utils.formatCalendar(calendar, FULL_12H_TIME);
	}

	public static String getAttackName(Context context, int groupId) {
		Cursor attackCursor = context.getContentResolver().query(
				Attack.CONTENT_URI, new String[] { Attack.NAME },
				Attack._ID + "=" + groupId, null, null);
		attackCursor.moveToFirst();
		return Utils.getStringFromCol(attackCursor, Attack.NAME);
	}

}
