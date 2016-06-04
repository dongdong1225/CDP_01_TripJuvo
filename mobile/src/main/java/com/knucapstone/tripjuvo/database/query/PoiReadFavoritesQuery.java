package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;

import java.sql.SQLException;
import java.util.List;


public class PoiReadFavoritesQuery extends Query
{
	private long mSkip = -1L;
	private long mTake = -1L;


	public PoiReadFavoritesQuery()
	{
	}


	public PoiReadFavoritesQuery(long skip, long take)
	{
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<PoiModel>> processData() throws SQLException
	{
		Data<List<PoiModel>> data = new Data<>();
		data.setDataObject(PoiDAO.readFavorites(mSkip, mTake));
		return data;
	}
}
