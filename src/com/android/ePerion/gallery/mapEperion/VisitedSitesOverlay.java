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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

// TODO: Auto-generated Javadoc
/**
 * Overlay item that acts as a marker on the map. When the user click on the map the picture information will be displayed.
 * @author Julien Salvi
 */
public class VisitedSitesOverlay extends ItemizedOverlay<OverlayItem> {
	
	/** The sites list. */
	private List<OverlayItem> sitesList = new ArrayList<OverlayItem>();
	
	/** The ctx. */
	private Context ctx;

	/**
	 * Constructor of a visited overlay thanks to a drawable marker.
	 * @param marker Drawable marker.
	 */
	public VisitedSitesOverlay(Drawable marker) {
		super(boundCenterBottom(marker));
	}
	
	/**
	 * Constructor of visited overlay thanks to drawable marker and a context.
	 * @param marker Drawable marker.
	 * @param context Context.
	 */
	public VisitedSitesOverlay(Drawable marker, Context context) {
		  super(boundCenterBottom(marker));
		  ctx = context;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int item) {
		// return the item.
		return sitesList.get(item);
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		return sitesList.size();
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(int index) {
		OverlayItem oItem = sitesList.get(index);
		Toast.makeText(ctx, "Name: "+oItem.getTitle()+", Comment: "+oItem.getSnippet(), Toast.LENGTH_SHORT).show();
		return true;
	}
	
	/**
	 * Add an overlay item.
	 *
	 * @param overlay the overlay
	 */
	public void addOverlayItem(OverlayItem overlay) {
		sitesList.add(overlay);
		populate();
    }

}
