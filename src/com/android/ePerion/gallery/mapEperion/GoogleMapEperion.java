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

package com.android.ePerion.gallery.mapEperion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.android.ePerion.gallery.database.Picture;
import com.android.ePerion.gallery.database.PicturesDBAdapter;
import com.android.e_perion.gallery.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

// TODO: Auto-generated Javadoc
/**
 * Class which implements the Google Map in order to visualize the picture, defined as a marker, in the map and see the route
 * between the different markers.
 * @author Julien Salvi
 */
public class GoogleMapEperion extends MapActivity implements LocationListener {
	
	/** The map view. */
	private MapView mapView; 
	
	/** The map controller. */
	private MapController mapController;
	
	/** The coord list. */
	private List<GeoPoint> coordList;
	
	/** The map overlays. */
	private List mapOverlays;
	
	/** The sites. */
	private VisitedSitesOverlay sites;
	
	/** The pic ids. */
	private ArrayList<Integer> picIds;
	
	/** The database pic. */
	private PicturesDBAdapter databasePic;

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map_eperion);
		
		//Activate the zoom.
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		
		mapController = mapView.getController();
		databasePic = new PicturesDBAdapter(GoogleMapEperion.this);
		
		//Getting the picture Ids of the selected pictures.
		picIds = getIntent().getExtras().getIntegerArrayList("picIds");
		coordList = new ArrayList<GeoPoint>();
		//Create the list of geoPoint thanks to the picture Ids.
		databasePic.open();
		for (int i = 0; i < picIds.size(); i++) {
			databasePic.open();
			Picture pic = databasePic.getPicture(picIds.get(i));
			coordList.add(new GeoPoint((int)(pic.getLatitude() * 1E6), (int)(pic.getLongitude() * 1E6)));
			databasePic.close();
		}
		databasePic.close();
		
		//Creation of the marker thanks to the geoPoints.
		mapOverlays = mapView.getOverlays();
		Drawable imgMarker = this.getResources().getDrawable(R.drawable.googlemapmarker);
		sites = new VisitedSitesOverlay(imgMarker, this);

		//Arraylist of geoPoint to display the route on the map.
		ArrayList<GeoPoint> routePoints = new ArrayList<GeoPoint>();
		for (int j = 0; j < coordList.size(); j++) {
			databasePic.open();
			mapController.animateTo(coordList.get(j));
			Picture pic = databasePic.getPicture(picIds.get(j));
			OverlayItem oItem = new OverlayItem(coordList.get(j), pic.getName(), pic.getComment());
			sites.addOverlayItem(oItem);
			mapOverlays.add(sites);
			
			
			//Creation of the driving direction thanks the picture coordinates.
			if ((j+1) <= (picIds.size()-1)) {
				List<GeoPoint> routeTemp = new ArrayList<GeoPoint>();
				Picture p1 = databasePic.getPicture(picIds.get(j));
				Picture p2 = databasePic.getPicture(picIds.get(j+1));
				//Collecting the geoPoint from a source point and a destination point.
				routeTemp = this.getDirections(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
				routePoints.addAll(routeTemp);
			} else {
				Toast.makeText(this, "Driving directions", Toast.LENGTH_SHORT).show();
			}
			databasePic.close();
		}
		//Adding the RouteOverlay to the map to view the route between the different points.
		mapView.getOverlays().add(new RouteOverlay(routePoints));
		
		mapController.setZoom(14);
	}
	
	/**
	 * Get a list of GeoPoint in order to draw the driving direction between two geoPoint.
	 * @param lat1 Latitude of the source geoPoint.
	 * @param lon1 Longitude of the source geoPoint.
	 * @param lat2 Latitude of the destination.
	 * @param lon2 Longitude of the destination.
	 * @return A list of geoPoints.
	 */
	public ArrayList<GeoPoint> getDirections(double lat1, double lon1, double lat2, double lon2) {
		//URL to send to the Google Map API in order to get the route between two points.
        String url = "http://maps.googleapis.com/maps/api/directions/xml?origin=" +lat1 + "," 
        				+ lon1  + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric";
        String tag[] = { "lat", "lng" };
        ArrayList<GeoPoint> list_of_geopoints = new ArrayList<GeoPoint>();
        HttpResponse response = null;
        //Send the HTTP request to the servers.
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            //Parse the XML document, returned by the Google API, to store the geoPoints extracted.
            if (doc != null) {
                NodeList nl1, nl2;
                nl1 = doc.getElementsByTagName(tag[0]);
                nl2 = doc.getElementsByTagName(tag[1]);
                if (nl1.getLength() > 0) {
                    list_of_geopoints = new ArrayList<GeoPoint>();
                    for (int i = 0; i < nl1.getLength(); i++) {
                        Node node1 = nl1.item(i);
                        Node node2 = nl2.item(i);
                        double lat = Double.parseDouble(node1.getTextContent());
                        double lng = Double.parseDouble(node2.getTextContent());
                        list_of_geopoints.add(new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6)));
                    }
                } else {
                    // No points found
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_of_geopoints;
    }


	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
