package com.game.timeattack;

import java.util.Calendar;
import java.util.Formatter;

import android.database.Cursor;

public class Utils {

	public static int getIntFromCol(Cursor cursor, String colName) {
		cursor.moveToFirst();
		return sToI(getStringFromCol(cursor, colName));
	}

	public static String getStringFromCol(Cursor cursor, String colName) {
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndexOrThrow(colName));
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

	public static Calendar addToCalendar(Calendar cal, int days, int hours,
			int minutes, int seconds) {
		cal.add(Calendar.DAY_OF_MONTH, days);
		cal.add(Calendar.HOUR_OF_DAY, hours);
		cal.add(Calendar.MINUTE, minutes);
		cal.add(Calendar.SECOND, seconds);
		return cal;
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
}
