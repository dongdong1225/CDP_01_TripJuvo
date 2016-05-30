package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.CategoryDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.CategoryModel;

import java.sql.SQLException;
import java.util.List;


public class CategoryReadAllQuery extends Query
{
	private long mSkip = -1L;
	private long mTake = -1L;


	public CategoryReadAllQuery()
	{
	}


	public CategoryReadAllQuery(long skip, long take)
	{
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<CategoryModel>> processData() throws SQLException
	{
		Data<List<CategoryModel>> data = new Data<>();
		data.setDataObject(CategoryDAO.readAll(mSkip, mTake));
		return data;
	}
}
