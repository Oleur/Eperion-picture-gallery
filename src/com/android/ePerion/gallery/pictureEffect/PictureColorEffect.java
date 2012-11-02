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
package com.android.ePerion.gallery.pictureEffect;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;

// TODO: Auto-generated Javadoc
/**
 * Image processing in order to change the colors: black&white, sepia, negative...
 * @author Julien Salvi
 */
public class PictureColorEffect {
	
	/** The bitmap. */
	private Bitmap bitmap;
	
	/** The color matrix. */
	private ColorMatrix colorMatrix;
	
	/** The paint. */
	private Paint paint;
	
	/** The filter. */
	private ColorMatrixColorFilter filter;
	
	/** The canvas. */
	private Canvas canvas;
	
	/**
	 * Constructor of the picture effect class.
	 * @param bitmap Picture to be modified.
	 */
	public PictureColorEffect(Bitmap bitmap) {
		this.bitmap = bitmap;
		colorMatrix = new ColorMatrix();
		paint = new Paint();
		paint.setAntiAlias(true);
	}
	
	/**
	 * Modify the color's picture. Set the colors to black and white.
	 * @return A black and white bitmap.
	 */
	public Bitmap blackAndWhite() {
		//Creation of a new bitmap for the transformation.
		Bitmap blackAndWhiteBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas blackAndWhiteCanvas = new Canvas(blackAndWhiteBitmap);
		
		float[] blackAndWhite = new float[] {
			0.5f, 0.5f, 0.5f, 0, 0,
			0.5f, 0.5f, 0.5f, 0, 0,
			0.5f, 0.5f, 0.5f, 0, 0,
			0, 0, 0, 1, 0
		};

		colorMatrix.set(blackAndWhite);
		filter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(filter);		
		blackAndWhiteCanvas.drawBitmap(bitmap, 0, 0, paint);
		
		return blackAndWhiteBitmap;
	}
	
	/**
	 * Modify the color's picture. Set the color's picture to sepia.
	 * @return A sepia picture.
	 */
	public Bitmap sepia() {
		
		float[] sepia = new float[] {
			0.393f, 0.769f, 0.189f, 0, 0,
			0.349f, 0.686f, 0.168f, 0, 0,
			0.272f, 0.534f, 0.131f, 0, 0,
			0, 0, 0, 1, 0
		};
		
		canvas = new Canvas(this.bitmap);
		colorMatrix.set(sepia);
		filter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(filter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		
		return bitmap;
	}
	
	/**
	 * Modify the color's picture. Set the colors to negative.
	 * @return A negative picture.
	 */
	public Bitmap negative() {
		
		float[] negative = new float[] {
			-1, 0, 0, 0, 255,
			0, -1, 0, 0, 255,
			0, 0, -1, 0, 255,
			0, 0, 0, 1, 0
		};
	
		canvas = new Canvas(this.bitmap);
		colorMatrix.set(negative);
		filter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(filter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
				
		return bitmap;
	}
	
	/**
	 * Modify the color's picture. Set the picture as a polaroid and add a title to the picture..
	 * @param title Polaroid title.
	 * @param asset Asset Manager
	 * @return A polaroid bitmap.
	 */
	public Bitmap polaroid(String title, AssetManager asset) {
		// Put some contrast first
		float[] contrast = new float[] {
			2, 0, 0, 0, -130,
			0, 2, 0, 0, -130,
			0, 0, 2, 0,	-130,
			0, 0, 0, 1, 0
		};
		
		bitmap = getFormattedBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		int offScreenBmpWidth = (int) (w + ((12.5 * w) / 100));
		int offScreenBmpHeight = (int) (h + ((31.25 * h) / 100));
		
		int offset = (int) ((1.25 * w) / 100);
		int offsetBmp = (int) ((6.25 * w) / 100);
		
		// Create an off-screen bitmap where the canvas content will be recorded
		Bitmap polaroidBitmap = Bitmap.createBitmap(offScreenBmpWidth, offScreenBmpHeight, Config.ARGB_8888);	
		Canvas polaroidCanvas = new Canvas(polaroidBitmap);
		// Draw an off-white color rectangle that will contain the photo
		Rect backgroundFrame = new Rect(0, 0, offScreenBmpWidth, offScreenBmpHeight);
		Paint background = new Paint();
		background.setAntiAlias(true);
		background.setARGB(255, 255, 254, 218); // #FFFEDA
		// Draw a bit of shadow on the white borders
		polaroidCanvas.drawRect(backgroundFrame, background);

		colorMatrix.set(contrast);
		filter = new ColorMatrixColorFilter(colorMatrix);
		// Draw a very light shadow between the picture and the white margins
		paint.setShadowLayer((int) (offset / 2), 0, 0, Color.BLACK);
		paint.setColorFilter(filter);
		// Draw the photo on the white rectangle
		polaroidCanvas.drawBitmap(bitmap, offsetBmp, offsetBmp, paint);
		// Overlay titling
		if (title != null && asset != null) {
			Typeface typeface = Typeface.createFromAsset(asset, "angelina.TTF");
			paint.setTypeface(typeface);
			this.computeTextSize(title, polaroidBitmap, bitmap, offsetBmp);
			paint.setTextAlign(Align.CENTER);
			int xText = this.getXText(polaroidBitmap);
			int yText = this.getYText(polaroidBitmap, bitmap, offsetBmp);
			polaroidCanvas.drawText(title, 0, title.length(), xText, yText, paint);
		}
		return polaroidBitmap;
	}

	/**
	 * Compute the text size in the polaroïd picture.
	 * @param title Polaroid title.
	 * @param outterBmp Outter Bitmap.
	 * @param innerBmp Inner bitmap.
	 * @param offsetBmp offset.
	 * @return The text size represented by a float.
	 */
	private float computeTextSize(String title, Bitmap outterBmp,
			Bitmap innerBmp, int offsetBmp) {
		
		int frameHeight = outterBmp.getHeight() - (offsetBmp + innerBmp.getHeight());
		float size = (50 * frameHeight) / 100;
		paint.setTextSize(size);
		
		while (paint.measureText(title) >= outterBmp.getWidth() - (2 * offsetBmp)) {
			size -= 1f;
			paint.setTextSize(size);
		}
		
		return size;
	}

	/**
	 * Get the text width.
	 * @param outterBmp Bitmap.
	 * @return The text width.
	 */
	private int getXText(Bitmap outterBmp) {
		
		return (int) (outterBmp.getWidth() / 2);
	}
	
	/**
	 * Get the text height.
	 * @param outterBmp outter Bitmap.
	 * @param innerBmp Inner Bitmap.
	 * @param offset offset.
	 * @return The text height.
	 */
	private int getYText(Bitmap outterBmp, Bitmap innerBmp, int offset) {
		
		int frameHeight = outterBmp.getHeight() - (offset + innerBmp.getHeight());
		return (int) (offset + innerBmp.getHeight() + (frameHeight / 2));
	}

	/**
	 * Scale the bitmap to the new width and height.
	 * @param bitmap2 Bitmap to format.
	 * @param width New width.
	 * @param height New height.
	 * @return A formated bitmap.
	 */
	private Bitmap getFormattedBitmap(Bitmap bitmap2, int width, int height) {
		// Scale a bitmap conserving scale ratio aspect with a 400 px width
		Matrix mtx = new Matrix();
		mtx.postScale(400f / width, 400f / width);
		return Bitmap.createBitmap(bitmap2, 0, 0, width, height, mtx, true);
	}
	
}