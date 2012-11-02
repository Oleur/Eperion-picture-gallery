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

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.e_perion.gallery.R;

// TODO: Auto-generated Javadoc
/**
 * Display the picture in a alert dialog theme.
 * @author Julien Salvi
 */
public class PictureViewer extends Activity {

	/** The picture view. */
	private ImageView pictureView = null;
	
	/** The uri img. */
	private Uri uriImg = null;
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_viewer);
        
        pictureView = (ImageView) findViewById(R.id.viewerImage);
        uriImg = Uri.parse(getIntent().getExtras().getString("uriViewPic"));
        pictureView.setImageURI(uriImg);
    }
}
