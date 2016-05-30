package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.CategoryDAO;
import com.knucapstone.tripjuvo.database.data.Data;

import java.sql.SQLException;


public class CategoryDeleteAllQuery extends Query
{
	public CategoryDeleteAllQuery()
	{
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(CategoryDAO.deleteAll());
		return data;
	}
}
