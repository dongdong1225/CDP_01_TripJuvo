package com.knucapstone.tripjuvo.database.query;

/**
 * Created by leedonghee on 16. 6. 4..
 */
import android.util.Log;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;

import java.sql.SQLException;
import java.util.List;


public class PoiReadByCity extends Query
{
    private String mcityName;
    private long mSkip = -1L;
    private long mTake = -1L;


    public PoiReadByCity(String cityName)
    {
        mcityName = cityName;
    }


    public PoiReadByCity(String cityName, long skip, long take)
    {
        mcityName = cityName;
        mSkip = skip;
        mTake = take;
    }


    @Override
    public Data<List<PoiModel>> processData() throws SQLException
    {
        Data<List<PoiModel>> data = new Data<>();
        Log.i("Poi:", "mCategoryId : " + mcityName);
        Log.i("Poi:", "mSkip : " + mSkip);
        Log.i("Poi:", "mTake : " + mTake);

        data.setDataObject(PoiDAO.readByCity(mcityName, mSkip, mTake));

        return data;
    }
}
