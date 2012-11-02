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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.ePerion.gallery.database.Picture;
import com.android.ePerion.gallery.database.PicturesDBAdapter;
import com.android.ePerion.gallery.mapEperion.GoogleMapEperion;
import com.android.e_perion.gallery.R;

/**
 * Class to display the gallery's picture in a custom listView. The user has to click on the item in order to display the picture, 
 * a long click to delete it. For displaying the pictures in the map, the user has to check the item checkbox and click on the map button.
 * Moreover, the user will be able to order the pictures by name, location or date.
 * @author Julien Salvi
 */
public class GalleryView extends Activity implements OnClickListener, OnItemLongClickListener, OnItemClickListener {
	
	private ListView pictureList;
	private ImageButton resetDB;
	private ImageButton openGMap;
	
	private double radius;
	private boolean prefName;
	private boolean prefDate;
	private boolean prefLoc;
	
	private PicturesDBAdapter databasePic;
	private PictureListAdapter myAdapter;
	private ArrayList<Picture> allPictures = null;
	
	private static final int RADIUS_EARTH = 6371;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        
        pictureList = (ListView) findViewById(R.id.listPictureGallery);
               
        databasePic = new PicturesDBAdapter(GalleryView.this);    
        
        //Listener for the reset and map buttons.
        resetDB = (ImageButton) findViewById(R.id.buttonReset);
        openGMap = (ImageButton) findViewById(R.id.mapButton);
        resetDB.setOnClickListener(this);
        openGMap.setOnClickListener(this);
        
        //Check the user's preferences:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefName = prefs.getBoolean("boxOrderName", true);
        prefLoc = prefs.getBoolean("boxOrderLocation", true);
        prefDate = prefs.getBoolean("boxOrderDate", true);
        
        //*************************************
        //********** CUSTOM LIST **************
        //*************************************
        
        List<ListAttribute> listPicItem = new ArrayList<ListAttribute>();
        
        databasePic.open();
        allPictures = databasePic.getAllPictures();
        int sizeDB = allPictures.size();
        for (int i=0; i < sizeDB ;i++) {
        	databasePic.open();
        	Picture pic = allPictures.get(i);
        	databasePic.close();
            listPicItem.add(i, get(pic.getName(), pic.getComment(), Uri.parse(pic.getUri()), 
            		pic.getId(), pic.getLatitude(), pic.getLongitude()));
        }
        databasePic.close();
        
        
        myAdapter = new PictureListAdapter(this, listPicItem);
        pictureList.setAdapter(myAdapter);
        pictureList.setClickable(true);
        pictureList.setOnItemClickListener(this);
        pictureList.setOnItemLongClickListener(this);
    }
    
    /**
     * Get a list attribute thanks to its parameters.
     * @param name Picture name.
     * @param com Picture comment.
     * @param uri Picture uri.
     * @param id Picture ID.
     * @param lat Picture latitude.
     * @param lng Picture longitude.
     * @return A listAttribute item.
     */
    private ListAttribute get(String name, String com, Uri uri, int id, float lat, float lng) {
    	return new ListAttribute(name, com, uri, id, lat, lng);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_gallery_view, menu);
		return true;
	}
    
    @Override
	public boolean onPrepareOptionsMenu (Menu menu) {
	    if (!prefName) {
	    	menu.getItem(0).setEnabled(false);
	    }
	    if (!prefLoc) {
	    	menu.getItem(1).setEnabled(false);
	    }
	    if (!prefDate) {
	    	menu.getItem(2).setEnabled(false);
	    }
	    return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_order_c:
			//Open a dialog menu that allows to order the pictures by countries, cities or a specified perimeter.
			openLocationDialog();
			return true;
		case R.id.menu_order_n:
			//Order by picture name.
			orderByName();
			return true;
		case R.id.menu_order_d:
			//Order by date.
			orderByDate();
			return true;
		}
		return false;
    }

    /**
     * Open a dialog menu that allows to order the pictures by countries, cities or a specified perimeter.
     */
	private void openLocationDialog() {
		//Implement the menu thats allows to order the pictures by their location.
		new AlertDialog.Builder(this)
		.setTitle("Order by Location")
		.setItems(R.array.order_by_location, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0:
					orderByCountry();
					break;
				case 1:
					orderByCity();
					break;
				case 2:
					LayoutInflater factory = LayoutInflater.from(GalleryView.this);
			        final View adView = factory.inflate(R.layout.dialog_radius_layout, null);
			        //Alert Dialog in order to get the wanted radius.
					AlertDialog.Builder ad = new AlertDialog.Builder(GalleryView.this);
						ad.setView(adView);
						ad.setTitle("Radius in km");
						ad.setIcon(android.R.drawable.ic_dialog_info);
						ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								EditText radText = (EditText) adView.findViewById(R.id.rangefield);
								if (radText.getText().toString().equals("")) {
									Toast.makeText(GalleryView.this, "Please, put a number", Toast.LENGTH_SHORT).show();
								} else {
									radius = Double.parseDouble(radText.getText().toString());
									//Lunch the sort in order to get the picture that are in the range with the given radius.
									orderByGivenPerimeter(radius);
								}
							}
						});
						ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// Do nothing.
							}
						});
						ad.show();
					break;
				}
			}
		}).setIcon(R.drawable.wolrd_map_icon)
		.show();
	}

	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonReset:
			setAlert("Reset!", "Are you sure you want to reset the database ?", false);
    		break;
		case R.id.mapButton:
			ArrayList<Integer> listPicMap = new ArrayList<Integer>();
			List<ListAttribute> list = myAdapter.getListAttribute();
			if (list != null) {
				for(ListAttribute att : list) {
					if (att.isCheck()) {
						//Toast.makeText(GalleryView.this, "num: "+String.valueOf(att.getId()), Toast.LENGTH_SHORT).show();
						listPicMap.add(att.getId());
					}
				}
			}
			if (listPicMap != null) {
				Intent mapIntent = new Intent(this, GoogleMapEperion.class);
				mapIntent.putIntegerArrayListExtra("picIds", listPicMap);
				mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mapIntent);
			} else {
				Toast.makeText(GalleryView.this, "No item selected !", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		
	}
	
	/**
	 * Order the database by the picture location.
	 */
	private void orderByCountry() {
		databasePic.open();
		//Get all the pictures order by the country name.
        allPictures = databasePic.getAllPicturesOrderByLocation();
        //List of the attributes to add into the new list.
        List<ListAttribute> listPicItem = new ArrayList<ListAttribute>();
        int sizeDB = allPictures.size();
        for (int i=0; i < sizeDB ;i++) {
        	databasePic.open();
        	Picture pic = allPictures.get(i);
        	databasePic.close();
            listPicItem.add(i, get(pic.getName(), "Country: "+pic.getLocation(), Uri.parse(pic.getUri())
            		, pic.getId(), pic.getLatitude(), pic.getLongitude()));
        }
        
        myAdapter.clear();
        myAdapter = new PictureListAdapter(this, listPicItem);
        pictureList.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        databasePic.close();
	}
	
	/**
	 * Order the pictures by a given location and a radius. It gathers all the pictures that are in the range.
	 * @param radius Radius of the range.
	 */
	private void orderByGivenPerimeter(double radius) {
		//List of checked items.
        List<ListAttribute> listPic = new ArrayList<ListAttribute>();
        List<ListAttribute> list = myAdapter.getListAttribute();
		if (list != null) {
			for(ListAttribute att : list) {
				if (att.isCheck()) {
					listPic.add(att);
				}
			}
		}
		if (listPic.size() == 0 || listPic.size() > 1) {
			Toast.makeText(this, "Only one item has to be selected !", Toast.LENGTH_LONG).show();
		} else {
			databasePic.open();
			//Get all the pictures.
	        allPictures = databasePic.getAllPictures();
			int sizeDB = allPictures.size();
			ListAttribute pictureCenter = listPic.get(0);
			//Initialize a new ArrayList of ListAttribute.
			listPic = new ArrayList<ListAttribute>();
	        for (int i=0; i < sizeDB ;i++) {
	        	databasePic.open();
	        	Picture pic = allPictures.get(i);
	        	databasePic.close();
	        	double dist = computeDistanceGeopoints(pictureCenter.getLat(), pictureCenter.getLng(),
	        			pic.getLatitude(), pic.getLongitude());
	        	if (dist != -1.0 && dist <= radius) {
	        		ListAttribute lAtt = get(pic.getName(), "Country: "+pic.getLocation(), Uri.parse(pic.getUri())
		            		, pic.getId(), pic.getLatitude(), pic.getLongitude());
	        		lAtt.setCheck(true);
	        		listPic.add(lAtt);
	        	}
	        }
	        
	        myAdapter.clear();
	        myAdapter = new PictureListAdapter(this, listPic);
	        pictureList.setAdapter(myAdapter);
	        myAdapter.notifyDataSetChanged();
	        databasePic.close();
		}
	}

	/**
	 * Get the distance in kilometers between two geoPoint thanks to the formula bellow:
	 *          Formula to compute the distance between two GeoPoints                    
	 * dist = arccos(sin(lat1) � sin(lat2) + cos(lat1) � cos(lat2) � cos(lon1 - lon2)) � R
	 * @param lat1 Latitude of the first geoPoint.
	 * @param lng1 Longitude of the first geoPoint.
	 * @param lat2 Latitude of the second geoPoint.
	 * @param lng2 Longitude of the second geoPoint.
	 * @return The distance between two geoPoints.
	 */
	private double computeDistanceGeopoints(float lat1, float lng1, float lat2, float lng2) {
		/* ***********************************************************************************
		 *         Formula to compute the distance between two GeoPoints                     *
		 * dist = arccos(sin(lat1) � sin(lat2) + cos(lat1) � cos(lat2) � cos(lon1 - lon2)) � R
		 *************************************************************************************/
		double dist = -1;
		if (lat2 != 0.0 || lng2 != 0) {
			dist = Math.acos(Math.sin(Math.toRadians( (double)lat1 )) * Math.sin(Math.toRadians( (double)lat2 )) 
					+ (Math.cos(Math.toRadians( (double)lat1 )) * Math.cos(Math.toRadians( (double)lat2 )) 
					* Math.cos(Math.toRadians( (double)(lng1 - lng2) ))))*RADIUS_EARTH;
		}	
		return dist;
	}

	/**
	 * Order the database by the cities where the pictures were taken and update the listView.
	 */
	private void orderByCity() {
		databasePic.open();
		//Get all the pictures order by the country name.
        allPictures = databasePic.getAllPicturesOrderByLocation();
        //List of the attributes to add into the new list.
        List<ListAttribute> listPicItem = new ArrayList<ListAttribute>();
        int sizeDB = allPictures.size();
        for (int i=0; i < sizeDB ;i++) {
        	databasePic.open();
        	Picture pic = allPictures.get(i);
        	databasePic.close();
        	//Get the city name thanks to the latitude/longitude.
        	if (pic.getLongitude() != 0.0 || pic.getLatitude() != 0.0) {
        		String cityName = coordinatesToCityName(pic.getLatitude(), pic.getLongitude());
                listPicItem.add(i, get(pic.getName(), "City: "+cityName, Uri.parse(pic.getUri())
                		, pic.getId(), pic.getLatitude(), pic.getLongitude()));
        	} else {
        		listPicItem.add(i, get(pic.getName(), "City: "+pic.getLocation(), Uri.parse(pic.getUri())
                		, pic.getId(), pic.getLatitude(), pic.getLongitude()));
        	}
        	
        }
        
        myAdapter.clear();
        myAdapter = new PictureListAdapter(this, listPicItem);
        pictureList.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        databasePic.close();
	}
	
	/**
	 * Order the database by the picture names and update te listView.
	 */
	private void orderByName() {
		databasePic.open();
		//Get all the pictures order by the country name.
        allPictures = databasePic.getAllPicturesOrderByName();
        //List of the attributes to add into the new list.
        List<ListAttribute> listPicItem = new ArrayList<ListAttribute>();
        int sizeDB = allPictures.size();
        for (int i=0; i < sizeDB ;i++) {
        	databasePic.open();
        	Picture pic = allPictures.get(i);
        	databasePic.close();
            listPicItem.add(i, get(pic.getName(), pic.getComment(), Uri.parse(pic.getUri())
            		, pic.getId(), pic.getLatitude(), pic.getLongitude()));
        }
        
        myAdapter.clear();
        myAdapter = new PictureListAdapter(this, listPicItem);
        pictureList.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        databasePic.close();
	}
	
	/**
	 * Order the database by the picture dates.
	 */
	private void orderByDate() {
		databasePic.open();
		//Get all the pictures order by the country name.
        allPictures = databasePic.getAllPicturesOrderByDate();
        //List of the attributes to add into the new list.
        List<ListAttribute> listPicItem = new ArrayList<ListAttribute>();
        int sizeDB = allPictures.size();
        for (int i=0; i < sizeDB ;i++) {
        	databasePic.open();
        	Picture pic = allPictures.get(i);
        	databasePic.close();
            listPicItem.add(i, get(pic.getName(), "Date: "+pic.getDate(), Uri.parse(pic.getUri())
            		, pic.getId(), pic.getLatitude(), pic.getLongitude()));
        }
        
        myAdapter.clear();
        myAdapter = new PictureListAdapter(this, listPicItem);
        pictureList.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        databasePic.close();
	}
	
	/**
	 * Get the country name of a given picture with its latitude and longitude.
	 * @param lon Longitude
	 * @param lat Latitude
	 * @return The country name.
	 */
	private String coordinatesToCityName(float lat, float lon) {
		Geocoder geocoder = new Geocoder(this);
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation((double)lat, (double)lon, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Address obj = addresses.get(0);
		String city = obj.getLocality();
		return city;
	}

	/**
	 * Alert dialog to confirm the reset of the database.
	 * @param title Box title.
	 * @param msg Message displayed.
	 * @param finishActivity True to finish the activity, false otherwise.
	 */
	private void setAlert(String title, String msg, final boolean finishActivity) {
    	new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(msg)
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Finish the activity when "Ok" is pressed
				if (finishActivity) GalleryView.this.finish();
			}
		})
		.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				GalleryView.this.deleteDatabase("pictures.db");
	    		Toast.makeText(GalleryView.this, "Database's reset...", Toast.LENGTH_SHORT).show();
			}
		}).show();
    }
	
	/**
	 * Display an alert dialog.
	 * @param title Title of the alert dialog
	 * @param msg Message that will be be displayed.
	 * @param finishAc True to finish the activity.
	 * @param position Picture position.
	 */
	private void setAlertDeletePicture(String title, String msg, final boolean finishAc, final int position) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(msg)
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Finish the activity when "Ok" is pressed
				if (finishAc) GalleryView.this.finish();
			}
		})
		.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deletePicture(position);
	    		Toast.makeText(GalleryView.this, "Picture deleted", Toast.LENGTH_SHORT).show();
			}
		}).show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		ListAttribute att = (ListAttribute) pictureList.getItemAtPosition(position);
		databasePic.open();
		Picture nPic = databasePic.getPicture(att.getId());
		databasePic.close();
		Intent imgViewer = new Intent(this, PictureViewer.class);
		imgViewer.putExtra("uriViewPic", nPic.getUri());
		startActivity(imgViewer);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		setAlertDeletePicture("Delete", "Are you sure you want to delete this picture ?", false, position);
		return true;
	}
	
	/**
	 * Delete the selected picture thanks to its position.
	 * @param position Picture postion in the list.
	 */
	private void deletePicture(int position) {
		ListAttribute att = (ListAttribute) pictureList.getItemAtPosition(position);
		databasePic.open();
		//Remove the selected picture from the gallery but not in the sc card.
		databasePic.removePicture(att.getId());
        //List of the attributes to add into the new list.
        allPictures = databasePic.getAllPictures();
        List<ListAttribute> listPicItem = new ArrayList<ListAttribute>();
        int sizeDB = allPictures.size();
        for (int i=0; i < sizeDB ;i++) {
        	databasePic.open();
        	Picture pic = allPictures.get(i);
        	databasePic.close();
            listPicItem.add(i, get(pic.getName(), pic.getComment(), Uri.parse(pic.getUri()), pic.getId(), 
            		pic.getLatitude(), pic.getLongitude()));
        }
        
        myAdapter.clear();
        myAdapter = new PictureListAdapter(this, listPicItem);
        pictureList.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        databasePic.close();
	}
    
}
