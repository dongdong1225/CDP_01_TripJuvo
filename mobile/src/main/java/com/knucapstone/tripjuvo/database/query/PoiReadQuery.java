package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;

import java.sql.SQLException;


public class PoiReadQuery extends Query
{
	private long mId;


	public PoiReadQuery(long id)
	{
		mId = id;
	}


	@Override
	public Data<PoiModel> processData() throws SQLException
	{
		Data<PoiModel> data = new Data<>();
		data.setDataObject(PoiDAO.read(mId));
		return data;
	}
}
