package com.knucapstone.tripjuvo.database.query;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;

import java.sql.SQLException;
import java.util.List;


public class PoiReadByCategoryQuery extends Query
{
	private long mCategoryId;
	private long mSkip = -1L;
	private long mTake = -1L;


	public PoiReadByCategoryQuery(long categoryId)
	{
		mCategoryId = categoryId;
	}


	public PoiReadByCategoryQuery(long categoryId, long skip, long take)
	{
		mCategoryId = categoryId;
		mSkip = skip;
		mTake = take;
	}


	@Override
	public Data<List<PoiModel>> processData() throws SQLException
	{
		Data<List<PoiModel>> data = new Data<>();
		data.setDataObject(PoiDAO.readByCategory(mCategoryId, mSkip, mTake));
		return data;
	}
}
