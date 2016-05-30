package com.knucapstone.tripjuvo;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.knucapstone.tripjuvo.utility.ImageLoaderUtility;


public class CityGuideApplication extends Application
{
	private static CityGuideApplication sInstance;

	private Tracker mTracker;


	public static Context getContext()
	{
		return sInstance;
	}


	public CityGuideApplication()
	{
		sInstance = this;
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		
		// force AsyncTask to be initialized in the main thread due to the bug:
		// http://stackoverflow.com/questions/4280330/onpostexecute-not-being-called-in-asynctask-handler-runtime-exception
		try
		{
			Class.forName("android.os.AsyncTask");
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		// init image caching
		ImageLoaderUtility.init(getApplicationContext());
	}
}
