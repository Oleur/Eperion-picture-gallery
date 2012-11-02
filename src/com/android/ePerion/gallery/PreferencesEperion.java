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

import com.android.e_perion.gallery.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * User preferences for the ePerion gallery application. Set with the method bellow the API 3.0 without the toolBar.
 * @author Julien Salvi
 */
public class PreferencesEperion extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   addPreferencesFromResource(R.xml.prefs);
	}

}
