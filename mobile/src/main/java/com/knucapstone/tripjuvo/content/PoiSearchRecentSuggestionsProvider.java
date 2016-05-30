package com.knucapstone.tripjuvo.content;

import android.content.SearchRecentSuggestionsProvider;


public class PoiSearchRecentSuggestionsProvider extends SearchRecentSuggestionsProvider
{
	//public final static String AUTHORITY = "com.robotemplates.cityguide.content.PoiSearchRecentSuggestionsProvider";
	public final static String AUTHORITY = "com.knucapstone.tripjuvo.content.PoiSearchRecentSuggestionsProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;


	public PoiSearchRecentSuggestionsProvider()
	{
		setupSuggestions(AUTHORITY, MODE);
	}
}
