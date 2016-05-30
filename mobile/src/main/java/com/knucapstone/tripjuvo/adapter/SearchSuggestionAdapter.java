package com.knucapstone.tripjuvo.adapter;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knucapstone.tripjuvo.R;


public class SearchSuggestionAdapter extends CursorAdapter
{
	public SearchSuggestionAdapter(Context context, Cursor cursor)
	{
		super(context, cursor, 0);
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.search_suggestion_item, parent, false);
		return view;
	}


	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		// reference
		LinearLayout root = (LinearLayout) view;
		TextView titleTextView = (TextView) root.findViewById(R.id.search_suggestion_item_title);

		// content
		final int index = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
		titleTextView.setText(cursor.getString(index));
	}
	
	
	public void refill(Context context, Cursor cursor)
	{
		changeCursor(cursor);
	}
}
