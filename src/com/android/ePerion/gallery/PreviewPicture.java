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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ePerion.gallery.database.Picture;
import com.android.ePerion.gallery.database.PicturesDBAdapter;
import com.android.ePerion.gallery.pictureEffect.PictureColorEffect;
import com.android.e_perion.gallery.R;

/**
 * Picture preview before putting it into the custom gallery.
 * The user will be able to modify the picture by adding color effects: set the picture to black&white, sepia, negative or polaroïd.
 * The modified picture will be stored in the sd card.
 * @author Julien Salvi
 */
public class PreviewPicture extends Activity {
	
	private String imagePath = null;
	private String fileName = null;
	private Bitmap previewPhoto = null;
	private ImageView imageView = null;
	private EditText imageName = null;
	private EditText imageCom = null;
	private TextView nameText = null;
	private TextView comText = null;
	private Uri uriImg = null;
	private File imgFile = null; 
	private Uri tmpUri = null;
	
	private static final int SAVE_TMP_BMP_DIALOG_ID = 0;
	private static final int SAVE_FINAL_BMP_DIALOG_ID = 1;
	private boolean shownTmpBmpDialog = false;
	private boolean shownFinalBmpDialog = false;
	private SaveTmpBmpTask saveTmpBmpTask = null;
	private SaveFinalBmpTask saveFinalBmpTask = null;
	private boolean useTmpFile = false;
	private boolean effects;
	
	private PicturesDBAdapter databasePic = null;
	
	private String picName = null;
	private String picComment = null;
	private String picLocation = null;
	private float picLongitude;
	private float picLatitude;
	private String picDate = null;
	
	private int click = 0;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_picture);

		fileName = getString(R.string.tmp_photo);
		
		if (null != savedInstanceState) {
			this.openPhotoFromSdcard();
		} else {
			this.openPhotoFromSdcard();
		}
		
		//Check the user's preferences:
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		effects = prefs.getBoolean("boxEffects", true);
		
		uriImg = Uri.parse(getIntent().getExtras().getString("imageUri").toString());
		tmpUri = uriImg;
		imgFile = new File(getRealPathFromURI(uriImg));
		
		//Database in order to add picture content for the gallery.
		databasePic = new PicturesDBAdapter(PreviewPicture.this);
		
		@SuppressWarnings("unchecked")
		HashMap<Integer, Object> hashMap = (HashMap<Integer, Object>) getLastNonConfigurationInstance();
		if (null != hashMap) {
			if (hashMap.containsKey(0)) saveTmpBmpTask = (SaveTmpBmpTask) hashMap.get(0);
			if (hashMap.containsKey(1)) saveFinalBmpTask = (SaveFinalBmpTask) hashMap.get(1);
		}
		
		saveTmpBmpTask = new SaveTmpBmpTask(this);
		saveTmpBmpTask.execute(false);
			
		imageView = (ImageView) findViewById(R.id.photo);
		imageView.setImageBitmap(previewPhoto);
		
		imageName = (EditText) findViewById(R.id.photoName);
		imageCom = (EditText) findViewById(R.id.photoComment);
		nameText = (TextView) findViewById(R.id.nameText);
		comText = (TextView) findViewById(R.id.commentText);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}
	
	@Override 
	protected void onRestart() {
		super.onRestart();
		openTmpPhoto();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_preview, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
	    if (!effects) {
	    	menu.getItem(0).setEnabled(false);
	    }
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.item_effect:
			openEffectDialog();
			return true;
		case R.id.item_save_new_pic:
			if (click == 1) {
				saveFinalBmpTask = new SaveFinalBmpTask(this);
				saveFinalBmpTask.execute(false);
			} else {
				saveAndAddToGallery();
			}
			return true;
		case R.id.save_exit_item:
			//Save the picture and go to home page.
			doSaveAndExit();
			return true;
		}
		return false;
	}

	/**
	 * Save the final picture, add the picture in the database and open the gallery view.
	 */
	private void saveAndAddToGallery() {
		
    	//Open the database to add pictures.
    	databasePic.open();
    	//Set the picture attributes.
    	if (checkEditText(imageName)) {
    		picName = imageName.getText().toString();
    	}
    	if (checkEditText(imageCom)) {
    		picComment = imageCom.getText().toString();
    	}
    	picDate = getImageDate(tmpUri);
    	//Toast.makeText(this, "Coords: "+getImageLatitude(uriImg)+", "+getImageLongitude(uriImg), Toast.LENGTH_SHORT).show();
    	if (getImageLatitude(uriImg) == null || getImageLongitude(uriImg) == null
    			|| getImageLatitude(uriImg).equals("0") || getImageLongitude(uriImg).equals("0")) {
    		picLocation = "Unkown";
    		picLongitude = 0;
        	picLatitude = 0;
    	} else {
    		picLongitude = Float.parseFloat(getImageLongitude(uriImg));
        	picLatitude = Float.parseFloat(getImageLatitude(uriImg));
        	picLocation = coordinatesToCountryName(picLatitude, picLongitude);
    	}
    	//Create the new picture to be added in the database.
    	Picture nPic = new Picture(picName, picComment, uriImg.toString(), picLocation, picLatitude, picLongitude, picDate);
    	databasePic.insertPicture(nPic);
    	databasePic.close();
    	
    	Intent galleryIntent = new Intent(this, GalleryView.class);
    	startActivity(galleryIntent);
	}
	
	/**
	 * Get the country name of a given picture with its latitude and longitude.
	 * @param lon Longitude
	 * @param lat Latitude
	 * @return The country name.
	 */
	private String coordinatesToCountryName(float lat, float lon) {
		Geocoder geocoder = new Geocoder(this);
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation((double)lat, (double)lon, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Address obj = addresses.get(0);
		String country = obj.getCountryName();
		return country;
	}
	
	/**
	 * Save the current modified picture and go to the home page.
	 */
	private void doSaveAndExit() {
    	saveFinalBmpTask = new SaveFinalBmpTask(this);
    	saveFinalBmpTask.execute(true);
    }

	/**
	 * Open the photo from the sd card and resize the selected picture in order to fit the screen.
	 */
	private void openPhotoFromSdcard() {
		imagePath = getRealPathFromURI(Uri.parse(getIntent().getExtras().getString("imageUri")));
		// Decode size image
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		final int MAX_SIZE = 1000;
		// Find the correct scale (power of 2)
		int w = options.outWidth;
		int h = options.outHeight;
		int scale = 1;
		while (true) {
			if ((w < MAX_SIZE) || (h < MAX_SIZE)) break;
			w /= 2;
			h /= 2;
			scale *= 2;
		}
		BitmapFactory.Options newOptions = new BitmapFactory.Options();
		newOptions.inSampleSize = scale;
		previewPhoto = BitmapFactory.decodeFile(imagePath, newOptions);
	}
	
	@Override 
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("useTmpFile", useTmpFile);
		super.onSaveInstanceState(outState);
	}
	
	@Override 
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.getString("imageUri") != null)
			uriImg = Uri.parse(savedInstanceState.getString("uriImg").toString());
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (null != saveTmpBmpTask) saveTmpBmpTask.setActivity(null);
		if (null != saveFinalBmpTask) saveFinalBmpTask.setActivity(null);
		HashMap<Integer, Object> hashMap = new HashMap<Integer, Object>(2);
		hashMap.put(0, saveTmpBmpTask);
		hashMap.put(1, saveFinalBmpTask);
		return hashMap;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog saveBmpDialog = new ProgressDialog(this);
		saveBmpDialog.setMessage("Please, wait...");
		saveBmpDialog.setCancelable(true);
		saveBmpDialog.setIndeterminate(true);
		
		switch (id) {
		case SAVE_TMP_BMP_DIALOG_ID:
			shownTmpBmpDialog = true;
			return saveBmpDialog;
		case SAVE_FINAL_BMP_DIALOG_ID:
			shownFinalBmpDialog = true;
			return saveBmpDialog;
		default:
			return super.onCreateDialog(id);
		}
	}
	
	/**
	 * Set a boolean when the async task is complete.
	 */
	private void onTmpTaskCompleted() {
		if (shownTmpBmpDialog) removeDialog(SAVE_TMP_BMP_DIALOG_ID);
		// Temporary bitmap can be used now as it was saved in the internal storage
		useTmpFile = true;
	}
	
	/**
	 * Save and add to the gallery the final picture when the final task is complete. 
	 * @param exitActivity True for finishing the activity.
	 */
	private void onFinalTaskCompleted(boolean exitActivity) {
		if (shownFinalBmpDialog) removeDialog(SAVE_FINAL_BMP_DIALOG_ID);
		if (exitActivity) this.finish();
		else this.saveAndAddToGallery();
	}
    
	/**
	 * Async task in order to store the new modified picture and open the gallery view after the task complete.
	 * @author Julien Salvi
	 */
	private class SaveTmpBmpTask extends AsyncTask<Boolean, Void, Void> {
		private PreviewPicture previewPhotoActivity = null;
		private boolean completed = false;
		
		/**
		 * Construct the async task with the activity.
		 * @param previewPhotoActivity Current activity.
		 */
		public SaveTmpBmpTask(PreviewPicture previewPhotoActivity) {
			this.previewPhotoActivity = previewPhotoActivity;
		}
			
		@Override
		protected void onPreExecute() {
			previewPhotoActivity.showDialog(SAVE_TMP_BMP_DIALOG_ID);
		}
		
		@Override // Separate from UI thread
		protected Void doInBackground(Boolean... png) {
			previewPhotoActivity.saveTmpPhoto(png[0]);
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void unused) {
			completed = true;
			this.notifyActivityTaskCompleted();
		}
		
		/**
		 * Notify the activity when the task is complete.
		 */
		private void notifyActivityTaskCompleted() {
			if (previewPhotoActivity != null) {
				previewPhotoActivity.onTmpTaskCompleted();
			}
		}
		
		/**
		 * 
		 * @param previewPhotoActivity
		 */
		private void setActivity(PreviewPicture previewPhotoActivity) {
			this.previewPhotoActivity = previewPhotoActivity;
			if (completed) {
				this.notifyActivityTaskCompleted();
			}
		}
	}
    
    private void saveTmpPhoto(boolean png) {    	
    	// Save tmp bitmap to internal app storage
		try {
			FileOutputStream fos = openFileOutput(getString(R.string.tmp_photo_i), MODE_PRIVATE);
			if (png)
				// Quality is not lost with PNG format
				previewPhoto.compress(Bitmap.CompressFormat.PNG, 100, fos);
			else
				previewPhoto.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			this.moveTmpFile();
			fos.close();
		} catch (IOException e) {
			// Nothing here
		}
    }
    
    private void moveTmpFile() {
    	try {
    		FileChannel source = openFileInput(getString(R.string.tmp_photo_i)).getChannel();
    		FileChannel dest = openFileOutput(fileName, MODE_PRIVATE).getChannel();  
    		dest.transferFrom(source, 0, source.size());
    		deleteFile(getString(R.string.tmp_photo_i));
    		source.close();
    		dest.close();
    	} catch (FileNotFoundException e) {
    		// Nothing here
    	} catch (IOException e) {
    		// Nothing here
    	}
    }
    
    private void openTmpPhoto() {
    	// Open tmp bitmap from internal storage    	
		ImageView imageView = (ImageView) findViewById(R.id.photo);
		try {
			FileInputStream fis = openFileInput(fileName); 
			if (fis.available() > 0) { 
				byte[] bArray = new byte[fis.available()]; 
				fis.read(bArray); 
				previewPhoto = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);
				imageView.setImageBitmap(previewPhoto);
				fis.close();
			}
		} catch (IOException e) {
			// Nothing here
		}
    }
    
    /**
     * Open the alert dialog menu in order to manage the picture effects.
     */
    private void openEffectDialog() {
    	new AlertDialog.Builder(this)
    	.setTitle("Add Effect")
    	.setItems(R.array.add_effect_menu, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				// Image to Black and White 
				case 0:
					click = 1;
					doBlackAndWhiteEffect();
					break;
				// Image to sapia
				case 1:
					click = 1;
					doSepiaEffect();
					break;
				// Image to Negative colors.
				case 2:
					click = 1;
					doNegativeEffect();
					break;
				// Image to polaroid and text effect as a title.
				case 3:
					click = 1;
					doPolaroidEffect();
					break;
				}
			}
		}).show();
    }
    
    /**
     * Modify the colors' picture to black and white.
     */
    private void doBlackAndWhiteEffect() {
    	previewPhoto = new PictureColorEffect(previewPhoto).blackAndWhite();
    	imageView.setImageBitmap(previewPhoto);
    	saveTmpBmpTask = new SaveTmpBmpTask(this);
    	saveTmpBmpTask.execute(false);
    }
    
    /**
     * Modify the colors' picture to sepia.
     */
    private void doSepiaEffect() {
    	previewPhoto = new PictureColorEffect(previewPhoto.copy(Config.ARGB_8888, true)).sepia();
    	imageView.setImageBitmap(previewPhoto);
    	saveTmpBmpTask = new SaveTmpBmpTask(this);
    	saveTmpBmpTask.execute(false);
    }
    
    /**
     * Modify the colors' picture to negative.
     */
    private void doNegativeEffect() {
    	previewPhoto = new PictureColorEffect(previewPhoto.copy(Config.ARGB_8888, true)).negative();
    	imageView.setImageBitmap(previewPhoto);
    	saveTmpBmpTask = new SaveTmpBmpTask(this);
    	saveTmpBmpTask.execute(false);
    }
    
    /**
     * Do a polaroid effect to the picture and add a custom title to the picture.
     */
    private void doPolaroidEffect() {
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        
        // Set max length
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(20);
        input.setFilters(fArray);
        input.setHint("20 characters max");
        
        alert.setTitle("Would you like to add a title?");
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        		String title = input.getText().toString().trim();
        		previewPhoto = new PictureColorEffect(previewPhoto).polaroid(title, getAssets());
            	imageView.setImageBitmap(previewPhoto);
            	saveTmpBmpTask = new SaveTmpBmpTask(PreviewPicture.this);
            	saveTmpBmpTask.execute(false);
        	}
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        		dialog.cancel();
        		previewPhoto = new PictureColorEffect(previewPhoto).polaroid(null, null);
            	imageView.setImageBitmap(previewPhoto);
            	saveTmpBmpTask = new SaveTmpBmpTask(PreviewPicture.this);
            	saveTmpBmpTask.execute(false);
        	}
        });
        alert.show();
    }
    
    private void saveFinalBmp() {
    	long currentTime = System.currentTimeMillis();
    	String date = new SimpleDateFormat("yyyy-MMdd-HHmmss").format(new Date(currentTime));
    	ContentValues values = new ContentValues(5);
		values.put(MediaStore.Images.Media.TITLE, "eperionPhoto"+date+".jpg");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DATE_ADDED, currentTime);
		values.put(MediaStore.Images.Media.LATITUDE ,getImageLatitude(uriImg));
		values.put(MediaStore.Images.Media.LONGITUDE, getImageLongitude(uriImg));
		
		uriImg = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		try {
			OutputStream outStream = getContentResolver().openOutputStream(uriImg);
			previewPhoto.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.close();
		} catch (IOException e) {
			// Nothing here
		}
    }
    
	// SaveFinalBmpTask executes the long operations in a background thread allowing a UI update
	private class SaveFinalBmpTask extends AsyncTask<Boolean, Void, Boolean> {
		private PreviewPicture previewPhotoActivity = null;
		private boolean completed = false; 
		
		public SaveFinalBmpTask(PreviewPicture previewPhotoActivity) {
			this.previewPhotoActivity = previewPhotoActivity;
		}
		
		@Override
		protected void onPreExecute() {
			previewPhotoActivity.showDialog(SAVE_FINAL_BMP_DIALOG_ID);
		}
		
		@Override // Separate from UI thread
		protected Boolean doInBackground(Boolean... exitActivity) {
			previewPhotoActivity.saveFinalBmp();
			return exitActivity[0];
		}
		
		@Override
		protected void onPostExecute(Boolean exitActivity) {
			completed = true;
			this.notifyActivityTaskCompleted(exitActivity);
		}
		
		private void notifyActivityTaskCompleted(Boolean exitActivity) {
			if (previewPhotoActivity != null) {
				previewPhotoActivity.onFinalTaskCompleted(exitActivity);
			}
		}
		
		private void setActivity(PreviewPicture previewPhotoActivity) {
			this.previewPhotoActivity = previewPhotoActivity;
			if (completed) {
				this.notifyActivityTaskCompleted(false);
			}
		}
	}
	
	/**
	 * Check if the edit text is null.
	 * @param message Edit Text to check.
	 * @return True if something's written, false otherwise.
	 */
	private boolean checkEditText(EditText message) {
		if (message.getText().toString().length() == 0) {
			setAlert("Warning!", "Empty spot ! Write a name or a comment.", false);
			message.requestFocus();
			return false;
		} else
			return true;
	}
    
	/**
	 * Get the real path from the picture URI.
	 * @param contentUri URI of the picture.
	 * @return The picture real path.
	 */
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }
	
	/**
	 * Get the image name from the URI.
	 * @param contentUri URI of the picture.
	 * @return The image name.
	 */
	public String getImageName(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.TITLE };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
		cursor.moveToFirst();
		
		return cursor.getString(index);
	}
	
	/**
	 * Retrieve the taken date for the picture.
	 * @param contentUri URI of the picture.
	 * @return The taken date.
	 */
	public String getImageDate(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATE_TAKEN };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
		cursor.moveToFirst();
		
		String datePicture = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss").format(new Date(Long.parseLong(cursor.getString(index))));
		return datePicture;
	}
	
	/**
	 * Retrieve the longitude of the picture.
	 * @param contentUri URI of the picture.
	 * @return The longitude.
	 */
	public String getImageLongitude(Uri contentUri) {
		String[] proj = {MediaStore.Images.Media.LONGITUDE };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE);
		cursor.moveToFirst();
		
		return cursor.getString(index);
	}
	
	/**
	 * Retrieve the latitude of the picture.
	 * @param contentUri
	 * @return
	 */
	public String getImageLatitude(Uri contentUri) {
		String[] proj = {MediaStore.Images.Media.LATITUDE };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE);
		cursor.moveToFirst();
		
		return cursor.getString(index);
	}
	
	/**
	 * Display an alert dialog.
	 * @param title Title of the alert dialog
	 * @param msg Message that will be be displayed.
	 * @param finishActivity True to finish the activity.
	 */
	private void setAlert(String title, String msg, final boolean finishActivity) {
    	new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(msg)
		.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Finish the activity when "Ok" is pressed
				if (finishActivity) PreviewPicture.this.finish();
			}
		}).show();
    }
}
