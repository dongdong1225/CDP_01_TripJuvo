package com.knucapstone.tripjuvo.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.CityGuideConfig;

/**
 * Created by aassw on 2016-05-23.
 */
public class FavoriteUpdate {
    private static final int DATABASE_VERSION = CityGuideConfig.DATABASE_VERSION;
    private static final String PREFS_KEY_DATABASE_VERSION = "database_version";
    private static final String DATABASE_PATH = "/data/data/" + CityGuideApplication.getContext().getPackageName() + "/databases/";
    private static final String DATABASE_NAME = CityGuideConfig.DATABASE_NAME;

    public void insert(long p_Id) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME ,null,DATABASE_VERSION);
        db.execSQL("CREATE TABLE IF NOT EXISTS favorite " +
                "(poi_id INTEGER PRIMARY KEY, region TEXT);");

        String search = "Select * from pois where id = "+Long.toString(p_Id)+";";
        Cursor c = db.rawQuery(search,null);
        c.moveToFirst();
        String sql = "insert into favorite(poi_id, region) values ("+ Long.toString(p_Id)+",'"+c.getString(13)+"');";
        db.execSQL(sql);
        db.close();
    }
    public void delete(long p_Id) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME ,null,DATABASE_VERSION);
        db.execSQL("CREATE TABLE IF NOT EXISTS favorite " +
                "(poi_id INTEGER PRIMARY KEY,region TEXT);");
        String sql = "DELETE FROM favorite where poi_id = "+ Long.toString(p_Id)+";";
        db.execSQL(sql);
        db.close();
    }
}