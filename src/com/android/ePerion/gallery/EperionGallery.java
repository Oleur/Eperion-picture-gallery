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
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.e_perion.gallery.R;

/**
 * Class that allows to pick a picture up from your gallery. 
 * The user is also able to view directly the ePerion gallery by clicking on gallery button. 
 * @author Julien Salvi
 */
public class EperionGallery extends Activity implements OnClickListener {
	
	private static final int SELECT_PIC_REQUEST = 1001;
	
	private Button openGallery = null;
	private Button pickPicture = null;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //Retrieve the buttons from the view.
        openGallery = (Button) findViewById(R.id.openGalleryButton);
        pickPicture = (Button) findViewById(R.id.pickPicture);
        
        pickPicture.setOnClickListener(this);
        openGallery.setOnClickListener(this);
    }
    
    @Override
    public void onResume() {
     super.onResume();
     
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_gallery, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.menu_settings:
				//Open the preferences.
				Intent intent = new Intent(EperionGallery.this, PreferencesEperion.class);
				startActivity(intent);
				return true;
		}
		return false;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
    	case R.id.openGalleryButton:
    		if (isWritableExternalStorageState(R.id.openGalleryButton)) {
    			openEperionGallery();
    		}
    		break;
    	case R.id.pickPicture:
    		if (isWritableExternalStorageState(R.id.pickPicture)) {
    			selectPhotoGallery();
    		}
    		break;
		}
	}

	/**
	 * Open the ePrion gallery intent.
	 */
	private void openEperionGallery() {
		Intent ePrionGalleryIntent = new Intent(this, GalleryView.class);
		startActivity(ePrionGalleryIntent);
	}

	/**
	 * Check if the external storage is writable. 
	 * @param button_ID Button ID.
	 * @return True if the external storage is writable, false if not.
	 */
	 private boolean isWritableExternalStorageState(int button_ID) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
        	return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
        	Toast.makeText(this, "SD Card is not writable", Toast.LENGTH_LONG).show();
        	return false;
        } else {
        	return false;
        }
    }
	 
	/**
	 * Allow the user to go to his gallery to pick up a picture.
	 */
	private void selectPhotoGallery() {
		Intent selectPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
		selectPhotoIntent.setType("image/*");
		startActivityForResult(Intent.createChooser(selectPhotoIntent, "Select Photo"), SELECT_PIC_REQUEST);
	}
	
	/**
	 * If a picture is selected from the gallery, the preview photo intent is going open with data put in extra.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == SELECT_PIC_REQUEST) {
    		if (resultCode == RESULT_OK) {
    			Intent previewPhotoIntent = new Intent(this, PreviewPicture.class);
    			previewPhotoIntent.putExtra("imageUri", data.getData().toString());
    			previewPhotoIntent.putExtra("saveTmpBmp", true);
    			startActivity(previewPhotoIntent);
    		}
    	}
    }
}
