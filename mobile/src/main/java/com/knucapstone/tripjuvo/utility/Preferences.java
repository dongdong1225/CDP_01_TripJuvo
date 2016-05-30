package com.knucapstone.tripjuvo.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.R;


public class Preferences
{
	private SharedPreferences mSharedPreferences;
	private Context mContext;

	
	public Preferences(Context context)
	{
		if(context==null) context = CityGuideApplication.getContext();
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mContext = context;
	}
	
	
	public void clearPreferences()
	{
		Editor editor = mSharedPreferences.edit();
		editor.clear();
		editor.commit();
	}


	// getters


	public int getMapType()
	{
		String key = mContext.getString(R.string.prefs_key_map_type);
		return mSharedPreferences.getInt(key, GoogleMap.MAP_TYPE_NORMAL);
	}


	// setters


	public void setMapType(int mapType)
	{
		String key = mContext.getString(R.string.prefs_key_map_type);
		Editor editor = mSharedPreferences.edit();
		editor.putInt(key, mapType);
		editor.commit();
	}
}
