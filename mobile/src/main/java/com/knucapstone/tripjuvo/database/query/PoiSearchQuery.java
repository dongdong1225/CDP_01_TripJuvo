package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;

import java.sql.SQLException;
import java.util.List;


public class PoiSearchQuery extends Query
{
	private String mQuery;
	private long mSkip = -1L;
	private long mTake = -1L;


	public PoiSearchQuery(String query)
	{
		mQuery = query;
	}


	public PoiSearchQuery(String query, long skip, long take)
	{
		mQuery = query;
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<PoiModel>> processData() throws SQLException
	{
		Data<List<PoiModel>> data = new Data<>();
		data.setDataObject(PoiDAO.search(mQuery, mSkip, mTake));
		return data;
	}
}
