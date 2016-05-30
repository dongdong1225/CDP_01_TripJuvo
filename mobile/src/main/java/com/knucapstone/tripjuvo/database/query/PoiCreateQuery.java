package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;

import java.sql.SQLException;


public class PoiCreateQuery extends Query
{
	private PoiModel mPoi;
	
	
	public PoiCreateQuery(PoiModel poi)
	{
		mPoi = poi;
	}
	
	
	@Override
	public Data<Integer> processData() throws SQLException
	{
		Data<Integer> data = new Data<>();
		data.setDataObject(PoiDAO.create(mPoi));
		return data;
	}
}
