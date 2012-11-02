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

// TODO: Auto-generated Javadoc
/**
 * Picture class with its attributes: name, comment, uri, location, latitude, longitude and date.
 * @author Julien Salvi
 */
public class Picture {
	
	/** The id. */
	private int id;
	
	/** The name. */
	private String name;
	
	/** The comment. */
	private String comment;
	
	/** The uri. */
	private String uri;
	
	/** The location. */
	private String location;
	
	/** The latitude. */
	private float latitude;
	
	/** The longitude. */
	private float longitude;
	
	/** The date. */
	private String date;
	
	/**
	 * Default constructor.
	 */
	public Picture() {
	}
	
	/**
	 * Constructor thanks to the name, comment, URI, location, latitude, longitude and the date.
	 * @param name Picture name.
	 * @param com Picture comment.
	 * @param uri Picture URI.
	 * @param loc Picture location (country).
	 * @param lat Latitude.
	 * @param lng Longitude.
	 * @param date Taken date.
	 */
	public Picture(String name, String com, String uri, String loc, float lat, float lng, String date) {
		this.name = name;
		this.comment = com;
		this.uri = uri;
		this.location = loc;
		this.latitude = lat;
		this.longitude = lng;
		this.date = date;
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
	public String getUri() {
		return uri;
	}
	
	/**
	 * Sets the uri.
	 *
	 * @param uri the new uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * Sets the location.
	 *
	 * @param loc the new location
	 */
	public void setLocation(String loc) {
		this.location = loc;
	}
	
	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public float getLatitude() {
		return latitude;
	}
	
	/**
	 * Sets the latitude.
	 *
	 * @param latitude the new latitude
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public float getLongitude() {
		return longitude;
	}
	
	/**
	 * Sets the longitude.
	 *
	 * @param longitude the new longitude
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(String date) {
		this.date = date;
	}	

}
