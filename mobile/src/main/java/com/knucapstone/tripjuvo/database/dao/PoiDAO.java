package com.knucapstone.tripjuvo.database.dao;

import com.knucapstone.tripjuvo.database.DatabaseHelper;
import com.knucapstone.tripjuvo.database.model.PoiModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;


public class PoiDAO extends DAO
{
	public static int create(PoiModel poi) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		return dao.create(poi);
	}
	
	
	public static PoiModel read(long id) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		return dao.queryForId(id);
	}
	
	
	public static List<PoiModel> readAll(long skip, long take) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		List<PoiModel> list;
		if(skip==-1L && take==-1L)
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}


	public static List<PoiModel> readFavorites(long skip, long take) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		List<PoiModel> list;
		if(skip==-1L && take==-1L)
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(PoiModel.COLUMN_FAVORITE, true);
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(PoiModel.COLUMN_FAVORITE, true);
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}


	public static List<PoiModel> readByCategory(long categoryId, long skip, long take) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		List<PoiModel> list;
		if(skip==-1L && take==-1L)
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(PoiModel.COLUMN_CATEGORY_ID, categoryId);
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(PoiModel.COLUMN_CATEGORY_ID, categoryId);
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}

	public static List<PoiModel> readByCity(String city, long skip, long take) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		List<PoiModel> list;
		if(skip==-1L && take==-1L)
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(PoiModel.COLUMN_CITY, city);
			queryBuilder.orderBy(PoiModel.COLUMN_CITY, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq(PoiModel.COLUMN_CITY, city);
			queryBuilder.orderBy(PoiModel.COLUMN_CITY, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}
	
	
	public static int update(PoiModel poi) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		return dao.update(poi);
	}
	
	
	public static int delete(long id) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		return dao.deleteById(id);
	}
	
	
	public static int deleteAll() throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		DeleteBuilder<PoiModel, Long> deleteBuilder = dao.deleteBuilder();
		return dao.delete(deleteBuilder.prepare());
	}


	public static List<PoiModel> search(String query, long skip, long take) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		List<PoiModel> list;
		if(skip==-1L && take==-1L)
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where()
					.like(PoiModel.COLUMN_NAME, "%" + query + "%")
					.or()
					.like(PoiModel.COLUMN_INTRO, "%" + query + "%")
					.or()
					.like(PoiModel.COLUMN_DESCRIPTION, "%" + query + "%")
					.or()
					.like(PoiModel.COLUMN_ADDRESS, "%" + query + "%");
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			list = dao.query(queryBuilder.prepare());
		}
		else
		{
			QueryBuilder<PoiModel, Long> queryBuilder = dao.queryBuilder();
			queryBuilder.where()
					.like(PoiModel.COLUMN_NAME, "%" + query + "%")
					.or()
					.like(PoiModel.COLUMN_INTRO, "%" + query + "%")
					.or()
					.like(PoiModel.COLUMN_DESCRIPTION, "%" + query + "%")
					.or()
					.like(PoiModel.COLUMN_ADDRESS, "%" + query + "%");
			queryBuilder.orderBy(PoiModel.COLUMN_NAME, true);
			queryBuilder.offset(skip).limit(take);
			list = dao.query(queryBuilder.prepare());
		}
		return list;
	}


	public static int refresh(PoiModel poi) throws SQLException
	{
		Dao<PoiModel, Long> dao = getDao();
		return dao.refresh(poi);
	}


	private static Dao<PoiModel, Long> getDao() throws SQLException
	{
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getPoiDao();
	}
}
