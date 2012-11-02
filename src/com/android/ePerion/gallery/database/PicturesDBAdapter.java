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

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// TODO: Auto-generated Javadoc
/**
 * Database adapter which allows retrieve, update or insert picture attributes from the database.
 * @author Julien Salvi
 */
public class PicturesDBAdapter {
	
	/** The Constant BASE_VERSION. */
	private static final int BASE_VERSION = 1;
	
	/** The Constant DB_NAME. */
	private static final String DB_NAME = "pictures.db";
	
	/** The Constant TABLE_PICTURE. */
	private static final String TABLE_PICTURE = "table_picture";
	
	/** The Constant COLUMN_ID. */
	private static final String COLUMN_ID = "id";
	
	/** The Constant COLUMN_ID_ID. */
	private static final int COLUMN_ID_ID = 0;
	
	/** The Constant COLUMN_NAME. */
	private static final String COLUMN_NAME = "name";
	
	/** The Constant COLUMN_NAME_ID. */
	private static final int COLUMN_NAME_ID = 1;
	
	/** The Constant COLUMN_COMMENT. */
	private static final String COLUMN_COMMENT = "comment";
	
	/** The Constant COLUMN_COMMENT_ID. */
	private static final int COLUMN_COMMENT_ID = 2;
	
	/** The Constant COLUMN_URI. */
	private static final String COLUMN_URI = "uri";
	
	/** The Constant COLUMN_URI_ID. */
	private static final int COLUMN_URI_ID = 3;
	
	/** The Constant COLUMN_LOCATION. */
	private static final String COLUMN_LOCATION = "location";
	
	/** The Constant COLUMN_LOCATION_ID. */
	private static final int COLUMN_LOCATION_ID = 4;
	
	/** The Constant COLUMN_LATITUDE. */
	private static final String COLUMN_LATITUDE = "latitude";
	
	/** The Constant COLUMN_LATITUDE_ID. */
	private static final int COLUMN_LATITUDE_ID = 5;
	
	/** The Constant COLUMN_LONGITUDE. */
	private static final String COLUMN_LONGITUDE = "longitude";
	
	/** The Constant COLUMN_LONGITUDE_ID. */
	private static final int COLUMN_LONGITUDE_ID = 6;
	
	/** The Constant COLUMN_DATE. */
	private static final String COLUMN_DATE = "date";
	
	/** The Constant COLUMN_DATE_ID. */
	private static final int COLUMN_DATE_ID = 7;
	
	/** The my database. */
	private SQLiteDatabase myDatabase;
	
	/** The db helper. */
	private PictureDatabaseHelper dbHelper;
	
	/**
	 * Construct the database adapter in the current context.
	 * @param context Activity context.
	 */
	public PicturesDBAdapter(Context context) {
		this.dbHelper = new PictureDatabaseHelper(context, DB_NAME, null, BASE_VERSION);
	}
	
	/**
	 * Open the database.
	 */
	public void open() {
		myDatabase = dbHelper.getWritableDatabase();
	}
	
	/**
	 * Close the database.
	 */
	public void close() {
		myDatabase.close();
	}
	
	/**
	 * Get the current database.
	 * @return A SQLite database.
	 */
	public SQLiteDatabase getDatabase() {
		return this.myDatabase;
	}
	
	/**
	 * Delete all rows which belong to the picture table.
	 */
	public void dropPictureTable() {
		this.myDatabase.delete(TABLE_PICTURE, null, null);
	}
	
	/**
	 * Return a picture based on its ID.
	 * @param id Picture ID.
	 * @return A picture.
	 */
	public Picture getPicture(int id) {
		Cursor c = myDatabase.query(TABLE_PICTURE, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_COMMENT, COLUMN_URI, COLUMN_LOCATION, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_DATE
		}, COLUMN_ID + "=" + id, null, null, null, null);
		return cursorToPicture(c);
	}
	
	/**
	 * Get all the pictures order by the date.
	 * @return An ArrayList of all pictures that belong to the database.
	 */
	public ArrayList<Picture> getAllPicturesOrderByDate() {
		Cursor c = myDatabase.query(TABLE_PICTURE, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_COMMENT, COLUMN_URI, COLUMN_LOCATION, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_DATE
		}, null, null, null, null, COLUMN_DATE);
		return cursorToPictures(c);
	}
	
	/**
	 * Get all the pictures order by the location where the pictures were taken.
	 * @return An ArrayList of all pictures that belong to the database.
	 */
	public ArrayList<Picture> getAllPicturesOrderByLocation() {
		Cursor c = myDatabase.query(TABLE_PICTURE, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_COMMENT, COLUMN_URI, COLUMN_LOCATION, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_DATE
		}, null, null, null, null, COLUMN_LOCATION);
		return cursorToPictures(c);
	}
	
	/**
	 * Get all the pictures order by their name.
	 * @return An ArrayList of all pictures that belong to the database.
	 */
	public ArrayList<Picture> getAllPicturesOrderByName() {
		Cursor c = myDatabase.query(TABLE_PICTURE, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_COMMENT, COLUMN_URI, COLUMN_LOCATION, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_DATE
		}, null, null, null, null, COLUMN_LOCATION);
		return cursorToPictures(c);
	}
	
	/**
	 * Get all the pictures from the database.
	 * @return An ArrayList of all pictures that belong to the database.
	 */
	public ArrayList<Picture> getAllPictures() {
		Cursor c = myDatabase.query(TABLE_PICTURE, new String[] {
				COLUMN_ID, COLUMN_NAME, COLUMN_COMMENT, COLUMN_URI, COLUMN_LOCATION, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_DATE
		}, null, null, null, null, null);
		return cursorToPictures(c);
	}
	
	/**
	 * Insert a new picture in the picture database.
	 *
	 * @param pic the pic
	 */
	public void insertPicture(Picture pic) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, pic.getName());
		values.put(COLUMN_COMMENT, pic.getComment());
		values.put(COLUMN_URI, pic.getUri());
		values.put(COLUMN_LOCATION, pic.getLocation());
		values.put(COLUMN_LATITUDE, pic.getLatitude());
		values.put(COLUMN_LONGITUDE, pic.getLongitude());
		values.put(COLUMN_DATE, pic.getDate());
		myDatabase.insert(TABLE_PICTURE, null, values);
	}
	
	/**
	 * Update a row thanks to its ID and the new picture attributes.
	 * @param id Row ID.
	 * @param pictureToUpdate Picture object with the new attributes.
	 */
	public void updatePicture(int id, Picture pictureToUpdate) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, pictureToUpdate.getName());
		values.put(COLUMN_COMMENT, pictureToUpdate.getComment());
		values.put(COLUMN_URI, pictureToUpdate.getUri());
		values.put(COLUMN_LOCATION, pictureToUpdate.getLocation());
		values.put(COLUMN_LATITUDE, pictureToUpdate.getLatitude());
		values.put(COLUMN_LONGITUDE, pictureToUpdate.getLongitude());
		values.put(COLUMN_DATE, pictureToUpdate.getDate());
		myDatabase.update(TABLE_PICTURE, values, COLUMN_ID + " = " + id, null);
	}

	/**
	 * Remove the selected picture with its ID.
	 * @param id Picture ID.
	 */
	public void removePicture(int id) {
		myDatabase.delete(TABLE_PICTURE, COLUMN_ID + " = " + id, null);
	}
	
	/**
	 * Remove the selected picture from its Uri.
	 *
	 * @param name the name
	 */
	public void removePicture(String name) {
		myDatabase.delete(TABLE_PICTURE, COLUMN_NAME + " = " + name, null);
	}
	
	/**
	 * Convert a cursor to a Picture.
	 * @param c Cursor to choose the picture.
	 * @return A picture.
	 */
	private Picture cursorToPicture(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}
		c.moveToFirst();
		Picture pic = new Picture();
		//Extract values from the cursor
		pic.setId(c.getInt(COLUMN_ID_ID));
		pic.setName(c.getString(COLUMN_NAME_ID));
		pic.setComment(c.getString(COLUMN_COMMENT_ID));
		pic.setUri(c.getString(COLUMN_URI_ID));
		pic.setLocation(c.getString(COLUMN_LOCATION_ID));
		pic.setLatitude(c.getFloat(COLUMN_LATITUDE_ID));
		pic.setLongitude(c.getFloat(COLUMN_LONGITUDE_ID));
		pic.setDate(c.getString(COLUMN_DATE_ID));
		//Close the cursor to free the data.
		c.close();
		return pic;
	}
	
	/**
	 * Convert the cursor to an arrayList of picture.
	 * @param c Cursor to choose the picture.
	 * @return An arrayList of picture.
	 */
	private ArrayList<Picture> cursorToPictures(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}
		
		ArrayList<Picture> messages = new ArrayList<Picture>(c.getCount());
		c.moveToFirst();
		do {
			Picture pic = new Picture();
			pic.setId(c.getInt(COLUMN_ID_ID));
			pic.setName(c.getString(COLUMN_NAME_ID));
			pic.setComment(c.getString(COLUMN_COMMENT_ID));
			pic.setUri(c.getString(COLUMN_URI_ID));
			pic.setLocation(c.getString(COLUMN_LOCATION_ID));
			pic.setLatitude(c.getFloat(COLUMN_LATITUDE_ID));
			pic.setLongitude(c.getFloat(COLUMN_LONGITUDE_ID));
			pic.setDate(c.getString(COLUMN_DATE_ID));
			messages.add(pic);
		} while (c.moveToNext());
		
		c.close();
		return messages;
	}
	
}
