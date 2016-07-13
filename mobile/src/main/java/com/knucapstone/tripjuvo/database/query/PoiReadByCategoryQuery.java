package com.knucapstone.tripjuvo.database.query;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.knucapstone.tripjuvo.database.dao.PoiDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;
import com.knucapstone.tripjuvo.hotelAPI.HotelDataJsonParser;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


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
		Log.i("Poi:", "mCategoryId : " + mCategoryId);
		Log.i("Poi:", "mSkip : " + mSkip);
		Log.i("Poi:", "mTake : " + mTake);
		if(mCategoryId == 2)
		{
			HotelDataJsonParser dataParser = new HotelDataJsonParser();
			//dataParser.task.onPostExecute("");
			dataParser.start();
			try {
				synchronized (dataParser.task) {
					dataParser.task.get();
					while (dataParser.task.getStatus() != AsyncTask.Status.FINISHED);
				}
			}
			catch (Exception e)
			{
				Log.i("PoiReady",e.getMessage());

			}
			//dataParser.waitForReady();
			List<PoiModel> test = new List<PoiModel>() {
				@Override
				public void add(int location, PoiModel object) {

				}

				@Override
				public boolean add(PoiModel object) {
					return false;
				}

				@Override
				public boolean addAll(int location, Collection<? extends PoiModel> collection) {
					return false;
				}

				@Override
				public boolean addAll(Collection<? extends PoiModel> collection) {
					return false;
				}

				@Override
				public void clear() {

				}

				@Override
				public boolean contains(Object object) {
					return false;
				}

				@Override
				public boolean containsAll(Collection<?> collection) {
					return false;
				}

				@Override
				public PoiModel get(int location) {
					return null;
				}

				@Override
				public int indexOf(Object object) {
					return 0;
				}

				@Override
				public boolean isEmpty() {
					return false;
				}

				@NonNull
				@Override
				public Iterator<PoiModel> iterator() {
					return null;
				}

				@Override
				public int lastIndexOf(Object object) {
					return 0;
				}

				@Override
				public ListIterator<PoiModel> listIterator() {
					return null;
				}

				@NonNull
				@Override
				public ListIterator<PoiModel> listIterator(int location) {
					return null;
				}

				@Override
				public PoiModel remove(int location) {
					return null;
				}

				@Override
				public boolean remove(Object object) {
					return false;
				}

				@Override
				public boolean removeAll(Collection<?> collection) {
					return false;
				}

				@Override
				public boolean retainAll(Collection<?> collection) {
					return false;
				}

				@Override
				public PoiModel set(int location, PoiModel object) {
					return null;
				}

				@Override
				public int size() {
					return 0;
				}

				@NonNull
				@Override
				public List<PoiModel> subList(int start, int end) {
					return null;
				}

				@NonNull
				@Override
				public Object[] toArray() {
					return new Object[0];
				}

				@NonNull
				@Override
				public <T> T[] toArray(T[] array) {
					return null;
				}
			};
			test=dataParser.getPoiDateaList();
			Log.i("size", test.size() + " " + test.get(0).getName());
			//data.setDataObject(dataParser.getPoiDateaList());
			data.setDataObject(PoiDAO.readByCategory(mCategoryId, mSkip, mTake));
		}
		else{
			data.setDataObject(PoiDAO.readByCategory(mCategoryId, mSkip, mTake));
		}

		return data;
	}
}
