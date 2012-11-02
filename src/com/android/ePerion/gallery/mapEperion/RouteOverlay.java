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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

// TODO: Auto-generated Javadoc
/**
 * Class that extends the Overlay class in order to draw the route the picture coordinates.
 * @author Julien Salvi
 */
public class RouteOverlay extends Overlay {
	
	/** The route geopoints. */
	private ArrayList<GeoPoint> routeGeopoints;

	/**
	 * Constructor of the routeOverlay thanks to a list of geoPoints.
	 * @param allGeoPoints List of geoPoints.
	 */
    public RouteOverlay(ArrayList<GeoPoint> allGeoPoints) {
        super();
        this.routeGeopoints = allGeoPoints;
    }

    /* (non-Javadoc)
     * @see com.google.android.maps.Overlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean, long)
     */
    @Override
    public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
        super.draw(canvas, mv, shadow);
        drawPath(mv, canvas);
        return true;
    }

    /**
     * Draw the route thanks to geoPoints previously collected.
     * @param mv MapView where the route is displayed.
     * @param canvas Canvas in order to draw the route.
     */
    public void drawPath(MapView mv, Canvas canvas) {
        int xPrev = -1, yPrev = -1, xNow = -1, yNow = -1;
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(4);
	paint.setAlpha(100);
        if (routeGeopoints != null)
            for (int i = 0; i < routeGeopoints.size() - 4; i++) {
                GeoPoint gp = routeGeopoints.get(i);
                Point point = new Point();
                mv.getProjection().toPixels(gp, point);
                xNow = point.x;
                yNow = point.y;
                if (xPrev != -1) {
                    canvas.drawLine(xPrev, yPrev, xNow, yNow, paint);
                }
                xPrev = xNow;
                yPrev = yNow;
            }
    }

}
