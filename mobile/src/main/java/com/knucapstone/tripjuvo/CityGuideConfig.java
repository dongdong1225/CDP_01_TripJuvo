package com.knucapstone.tripjuvo;


public class CityGuideConfig
{
	// file name of the SQLite database, this file should be placed in assets folder
	public static final String DATABASE_NAME = "cityguide.db";

	// database version, should be incremented if database has been changed
	public static final int DATABASE_VERSION = 2;

	// debug logs, value is set via build config in build.gradle
	public static final boolean LOGS = BuildConfig.LOGS;
}
