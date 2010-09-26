package com.game.timeattack;

import java.util.Calendar;
import java.util.Formatter;

import com.game.timeattack.provider.TimeAttack.Attack;
import com.game.timeattack.provider.TimeAttack.Fleet;

import android.content.Context;
import android.database.Cursor;

public class Utils {

	public static int getIntFromCol(Cursor cursor, String colName) {
		return sToI(getStringFromCol(cursor, colName));
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
	
	public static String getAttackTime(Context context,int groupId){
		String[] projection = { Attack.YEAR, Attack.MONTH,
				Attack.DAY, Attack.H, Attack.M, Attack.S };
		String selection = Attack._ID + "=" + groupId;
		Cursor attackCursor = context.getContentResolver().query(Attack.CONTENT_URI,
				projection, selection, null, null);
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
		
		return Utils.formatCalendar(calendar, "%tF") + " "
		+ Utils.formatCalendar(calendar, "%tr");
	}
	
	public static String getAttackName(Context context,int groupId){
		Cursor attackCursor = context.getContentResolver().query(Attack.CONTENT_URI,
				new String[] {Attack.NAME}, Attack._ID + "=" + groupId, null, null);
		attackCursor.moveToFirst();
		return Utils.getStringFromCol(attackCursor, Attack.NAME);
	}
	
	public static String getFleetLaunchTimeCal(Context context,int groupId, int childId){
		String[] projection = {Fleet.H,Fleet.M,Fleet.S,Fleet.DELTA};
		Cursor fleetCursor = context.getContentResolver().query(Fleet.CONTENT_URI, projection, Fleet._ID+"="+childId, null, null);
		String[] projection2={Attack.YEAR,Attack.MONTH,Attack.DAY, Attack.H, Attack.M, Attack.S};
		Cursor attackCursor = context.getContentResolver().query(Attack.CONTENT_URI, projection2, Attack._ID+"="+groupId, null, null);
		
		
		
		return null;
	}

//	public static Calendar getFleetLaunchTime(){
//		
//	}
	
}
