package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.CategoryDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.CategoryModel;

import java.sql.SQLException;


public class CategoryReadQuery extends Query
{
	private long mId;


	public CategoryReadQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<CategoryModel> processData() throws SQLException
	{
		Data<CategoryModel> data = new Data<>();
		data.setDataObject(CategoryDAO.read(mId));
		return data;
	}
}
