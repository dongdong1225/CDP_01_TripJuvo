package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;

import java.sql.SQLException;


public class PoiDeleteAllQuery extends Query
{
	public PoiDeleteAllQuery()
	{
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(PoiDAO.deleteAll());
		return data;
	}
}
