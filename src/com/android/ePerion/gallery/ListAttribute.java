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

package com.android.ePerion.gallery;

import android.net.Uri;

// TODO: Auto-generated Javadoc
/**
 * Class to create a list attribute that allows to have picture information for the custom listView and the Google Map.
 * @author Julien Salvi
 */
public class ListAttribute {
	
	/** The name. */
	private String name;
	
	/** The comment. */
	private String comment;
	
	/** The uri. */
	private Uri uri;
	
	/** The id. */
	private int id;
	
	/** The lat. */
	private float lat;
	
	/** The lng. */
	private float lng;
	
	/** The check. */
	private boolean check;
	
	/**
	 * Constructor of the listAttribute thanks to its parameters.
	 * @param n The picture name.
	 * @param c The picture comment.
	 * @param u The picture Uri.
	 * @param id The picture ID.
	 * @param lat The picture latitude.
	 * @param lng The picture longitude.
	 */
	public ListAttribute(String n, String c, Uri u, int id, float lat, float lng) {
		this.setName(n);
		this.setComment(c);
		this.setUri(u);
		this.setId(id);
		this.setCheck(false);
		this.lat = lat;
		this.lng = lng;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the comment.
	 *
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment.
	 *
	 * @param comment the new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public Uri getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 *
	 * @param uri the new uri
	 */
	public void setUri(Uri uri) {
		this.uri = uri;
	}

	/**
	 * Checks if is check.
	 *
	 * @return true, if is check
	 */
	public boolean isCheck() {
		return check;
	}

	/**
	 * Sets the check.
	 *
	 * @param check the new check
	 */
	public void setCheck(boolean check) {
		this.check = check;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the lat.
	 *
	 * @return the lat
	 */
	public float getLat() {
		return lat;
	}

	/**
	 * Sets the lat.
	 *
	 * @param lat the new lat
	 */
	public void setLat(float lat) {
		this.lat = lat;
	}

	/**
	 * Gets the lng.
	 *
	 * @return the lng
	 */
	public float getLng() {
		return lng;
	}

	/**
	 * Sets the lng.
	 *
	 * @param lng the new lng
	 */
	public void setLng(float lng) {
		this.lng = lng;
	}

}
