/**
* Copyright 2012 Julien Salvi
*
* This file is part of E-Perion project.
*
* Mercury is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Mercury is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Mercury. If not, see <http://www.gnu.org/licenses/>.
*/
package com.android.ePerion.gallery.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: Auto-generated Javadoc
/**
 * Class which helps you to create the table which belong to the picture database.
 * @author Julien Salvi
 */
public class PictureDatabaseHelper extends SQLiteOpenHelper {
	
	/** The Constant TABLE_PICTURE. */
	private static final String TABLE_PICTURE = "table_picture";
	
	/** The Constant COLUMN_ID. */
	private static final String COLUMN_ID = "id";
	
	/** The Constant COLUMN_NAME. */
	private static final String COLUMN_NAME = "name";
	
	/** The Constant COLUMN_COMMENT. */
	private static final String COLUMN_COMMENT = "comment";
	
	/** The Constant COLUMN_URI. */
	private static final String COLUMN_URI = "uri";
	
	/** The Constant COLUMN_LOCATION. */
	private static final String COLUMN_LOCATION = "location";
	
	/** The Constant COLUMN_LATITUDE. */
	private static final String COLUMN_LATITUDE = "latitude";
	
	/** The Constant COLUMN_LONGITUDE. */
	private static final String COLUMN_LONGITUDE = "longitude";
	
	/** The Constant COLUMN_DATE. */
	private static final String COLUMN_DATE = "date";
	
	/** The Constant REQUEST_CREATION_DB. */
	private static final String REQUEST_CREATION_DB = "CREATE TABLE "
			+ TABLE_PICTURE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_COMMENT + " TEXT, " + COLUMN_URI + " TEXT NOT NULL, "
			+ COLUMN_LOCATION + " TEXT NOT NULL, " + COLUMN_LATITUDE + " REAL, "
			+ COLUMN_LONGITUDE + " REAL, " + COLUMN_DATE + " TEXT NOT NULL);";

	/**
	 * Constructs a new database helper object thanks to the current context, the database name,
	 * the cursor factory and the database version.
	 * @param context The context.
	 * @param name Database name.
	 * @param factory Cursor factory.
	 * @param version database version.
	 */
	public PictureDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(REQUEST_CREATION_DB);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + REQUEST_CREATION_DB + ";");
		onCreate(db);
	}

}
