package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;

import java.sql.SQLException;


public class PoiDeleteQuery extends Query
{
	private long mId;


	public PoiDeleteQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(PoiDAO.delete(mId));
		return data;
	}
}
