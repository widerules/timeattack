package com.game.timeattack.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class TimeAttack {

	public static final class Attack implements BaseColumns {
		public static final String PROVIDER_NAME = "com.game.timeattack.provider.MyContentProvider/attack";
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ PROVIDER_NAME);

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified ASC";

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * attacks.
		 */
		public static final String CONTENT_TYPE = "timeattack.attack.dir/timeattack.attack";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * attack.
		 */
		public static final String CONTENT_ITEM_TYPE = "timeattack.attack.item/timeattack.attack";

		public static final String TABLE_NAME = "attack";

		public static final String _ID = "_id";
		public static final String NAME = "name";
		public static final String ATTACK_TIME = "attack_time";

		public static final String TABLE_CREATE = "CREATE TABLE '" + TABLE_NAME
				+ "' ('" + _ID
				+ "' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , '" + NAME
				+ "' TEXT, '" + ATTACK_TIME + "' TEXT );";

	}

	public static final class Fleet implements BaseColumns {
		public static final String PROVIDER_NAME = "com.game.timeattack.provider.MyContentProvider/fleet";
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ PROVIDER_NAME);

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "launch_time ASC";

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * attacks.
		 */
		public static final String CONTENT_TYPE = "timeattack.fleet.dir/timeattack.fleet";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * attack.
		 */
		public static final String CONTENT_ITEM_TYPE = "timeattack.fleet.item/timeattack.fleet";

		public static final String TABLE_NAME = "fleet";

		public static final String _ID = "_id";
		public static final String GROUP_ID = "group_id";
		public static final String NAME = "name";
		public static final String H = "h";
		public static final String M = "m";
		public static final String S = "s";
		public static final String DELTA = "delta";
		public static final String ALARM_DELTA = "alarm_delta";
		public static final String ALARM_ACTIVATED = "alarm_activated";
		public static final String LAUNCH_TIME = "launch_time";

		public static final String TABLE_CREATE = "CREATE TABLE '" + TABLE_NAME
				+ "' ('" + _ID
				+ "' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , '"
				+ GROUP_ID + "' INTEGER ,'" + NAME + "' TEXT , '" + H
				+ "' TEXT , '" + M + "' TEXT, '" + S + "' TEXT , '" + DELTA
				+ "' TEXT, '" + ALARM_ACTIVATED + "' TEXT, '" + ALARM_DELTA
				+ "' TEXT, '" + LAUNCH_TIME + "' TEXT );";

	}
}
