package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.CategoryDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.CategoryModel;

import java.sql.SQLException;


public class CategoryUpdateQuery extends Query
{
	private CategoryModel mCategory;


	public CategoryUpdateQuery(CategoryModel category)
	{
		mCategory = category;
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(CategoryDAO.update(mCategory));
		return data;
	}
}
