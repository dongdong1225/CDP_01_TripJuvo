package com.knucapstone.tripjuvo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.adapter.DragAndDropTravelAdapter;
import com.knucapstone.tripjuvo.model.DummyModel;
import com.knucapstone.tripjuvo.model.TravelDummyModel;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;

import java.util.ArrayList;

public class DragAndDropTravelActivity extends AppCompatActivity {

	private DynamicListView mDynamicListView;
	Intent intent;
	ArrayList<DummyModel> list;
	ArrayList<Location> locationArrayList;
	ArrayList<String> pictureArrayList;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_views);

		mDynamicListView = (DynamicListView) findViewById(R.id.dynamic_listview);
		mDynamicListView.setDividerHeight(0);

		intent = getIntent();
		ArrayList<String> checkedItemsArrayList = new ArrayList<>();
		checkedItemsArrayList = intent.getStringArrayListExtra("CHECKED_ITEMS");
		list = new ArrayList<>();

		setUpDragAndDrop();
		Toast.makeText(this, "Long press an item to start dragging",
				Toast.LENGTH_SHORT).show();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("여행 추천 경로");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpDragAndDrop() {
		Intent intent = getIntent();
		locationArrayList = new ArrayList<>();
		pictureArrayList= new ArrayList<>();
		if(intent != null)
		{
			locationArrayList = getIntent().getExtras().getParcelableArrayList("locationArrayList");
			pictureArrayList = intent.getStringArrayListExtra("pictureArrayList");
		}
		ArrayList<TravelDummyModel> list = new ArrayList<>();

		for(int i = 0; i < locationArrayList.size(); i ++)
		{
			list.add(new TravelDummyModel(i, pictureArrayList.get(i), locationArrayList.get(i).getProvider(), R.string.fontello_heart_empty,"10m","100"));
		}

		//final
		DragAndDropTravelAdapter adapter = new DragAndDropTravelAdapter(this, list);

		mDynamicListView.setAdapter(adapter);
		mDynamicListView.enableDragAndDrop();
		TouchViewDraggableManager tvdm = new TouchViewDraggableManager(R.id.drag_and_drop_travel_icon);
		mDynamicListView.setDraggableManager(new TouchViewDraggableManager(R.id.icon));
		mDynamicListView.setDraggableManager(tvdm);

		mDynamicListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
										   View view, int position, long id) {
				mDynamicListView.startDragging(position);
				return true;
			}
		});

		mDynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				DragAndDropTravelAdapter adapter = (DragAndDropTravelAdapter) mDynamicListView.getAdapter();
				TravelDummyModel item = (TravelDummyModel) adapter.getItem(position);

				Log.i("clicked", item.getText() + "  " + position + "");
				String CityName = item.getText();

				Intent intent = new Intent(DragAndDropTravelActivity.this, ExpandableTravelListViewActivity.class);
				intent.putExtra("CityName", CityName);
				intent.putExtra("picture_URL", item.getImageURL());
				startActivity(intent);
			}
		});
	}
}
