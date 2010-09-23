package com.game.timeattack;

import android.database.Cursor;

public class Utils {

	public static int getIntFromCol(Cursor aCursor, String colName) {
		return sToI(getStringFromCol(aCursor, colName));
	}

	public static String getStringFromCol(Cursor cursor, String colName) {
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
}
