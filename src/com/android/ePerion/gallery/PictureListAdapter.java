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

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.e_perion.gallery.R;

// TODO: Auto-generated Javadoc
/**
 * Custom adapter that extends an arrayAdapter of listAttribute in order to display the different items of the custom list: picture icon, 
 * picture name, picture comment and the checkbox.
 * @author Julien Salvi
 */
public class PictureListAdapter extends ArrayAdapter<ListAttribute> {
	
	/** The pic items. */
	private List<ListAttribute> picItems = null;
	
	/** The context. */
	private Activity context;
	
	/**
	 * The custom adapter in order to display the custom view.
	 * @param context The Activity where the adapter is used.
	 * @param objects List of ListAttributes to be displayed in the listView.
	 */
	public PictureListAdapter(Activity context, List<ListAttribute> objects) {
		super(context, R.layout.picture_list, objects);
		this.context = context;
		this.picItems = objects;
	}
	
	/**
	 * Static class which allows to get the different items of the listView.
	 * @author Julien Salvi
	 */
	static class ViewHolder {
        
        /** The com text. */
        protected TextView nameText, comText;
        
        /** The img view. */
        protected ImageView imgView;
        
        /** The checkbox. */
        protected CheckBox checkbox;
    }
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.picture_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.nameText = (TextView) view.findViewById(R.id.picName);
            viewHolder.nameText.setTextColor(Color.WHITE);
            viewHolder.comText = (TextView) view.findViewById(R.id.picComment);
            viewHolder.comText.setTextColor(Color.WHITE);
            viewHolder.imgView = (ImageView) view.findViewById(R.id.pictureItem);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.picCheck);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					ListAttribute elem = (ListAttribute) viewHolder.checkbox.getTag();
					elem.setCheck(buttonView.isChecked());
					if (elem.isCheck() == true) {
						System.out.println("Lat: "+elem.getLat()+" Lng: "+elem.getLng());
					} else {
						System.out.println("Elem unchecked");
					}
				}
			});
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(picItems.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(picItems.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
        holder.nameText.setText(picItems.get(position).getName());
        holder.comText.setText(picItems.get(position).getComment());
        
        holder.imgView.setImageBitmap(resizePictureToIcon(picItems.get(position).getUri()));
        holder.checkbox.setChecked(picItems.get(position).isCheck());
        return view;
	}
	
	/**
	 * Resize the image thanks to its Uri in order to have the corresponding thumbnail (100*100).
	 * @param pictureURI Picture Uri to resize.
	 * @return A resized bitmap. 
	 */
	private Bitmap resizePictureToIcon(Uri pictureURI) {
		String imagePath = getRealPathFromURI(pictureURI);
		// Decode size image
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		//Max size of picture 100*100.
		final int MAX_SIZE = 100;
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
		//Set the new bitmap option with the new scale.
		BitmapFactory.Options newOptions = new BitmapFactory.Options();
		newOptions.inSampleSize = scale;
		return BitmapFactory.decodeFile(imagePath, newOptions);
	}
	
	/**
	 * Retrieve file's physical path from logical path.
	 *
	 * @param contentUri The picture Uri.
	 * @return A String which is the file's physical path.
	 */
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor c = context.getContentResolver().query(contentUri, proj, null, null, null);
		if (c != null) {
			c.moveToFirst();
	        int columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        return c.getString(columnIndex);
		} else {
			return null;
		}
    }
	
	/**
	 * Get the list of listAttribute.
	 * @return A list of ListAttribute.
	 */
	public List<ListAttribute> getListAttribute() {
		return picItems;
	}

}
