package com.knucapstone.tripjuvo.database.dao;

import com.knucapstone.tripjuvo.database.DatabaseHelper;
import com.knucapstone.tripjuvo.utility.Logcat;

import java.sql.SQLException;


public class DAO
{
	public static void printDatabaseInfo()
	{
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		try
		{
			Logcat.d("%d categories", databaseHelper.getCategoryDao().countOf());
			Logcat.d("%d pois", databaseHelper.getPoiDao().countOf());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
