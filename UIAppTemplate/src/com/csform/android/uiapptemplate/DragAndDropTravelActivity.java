package com.csform.android.uiapptemplate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.csform.android.uiapptemplate.adapter.DragAndDropTravelAdapter;
import com.csform.android.uiapptemplate.model.DummyModel;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;

import java.util.ArrayList;

public class DragAndDropTravelActivity extends ActionBarActivity {

	private DynamicListView mDynamicListView;
	Intent intent;
	ArrayList<DummyModel> list;

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

//		list.add(new DummyModㅌel(0, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/0.jpg", "Monument walk tour", R.string.fontello_heart_empty));
//		list.add(new DummyModel(1, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/1.jpg", "River walk tour", R.string.fontello_heart_empty));
//		list.add(new DummyModel(2, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/2.jpg", "City walk tour", R.string.fontello_heart_empty));
//		list.add(new DummyModel(3, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/3.jpg", "Park walk tour", R.string.fontello_heart_empty));
//		list.add(new DummyModel(4, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/4.jpg", "Vilage walk tour", R.string.fontello_heart_empty));
//		list.add(new DummyModel(5, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/5.jpg", "Lake walk tour", R.string.fontello_heart_empty));
//		list.add(new DummyModel(6, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/6.jpg", "Castle walk tour", R.string.fontello_heart_empty));
//		list.add(new DummyModel(7, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/7.jpg", "Beach walk tour", R.string.fontello_heart_empty));
		for(int i = 0; i < checkedItemsArrayList.size(); i ++)
		{
			list.add(new DummyModel(i, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/"+i+".jpg", checkedItemsArrayList.get(i), R.string.fontello_heart_empty));
		}

		setUpDragAndDrop();
		Toast.makeText(this, "Long press an item to start dragging",
				Toast.LENGTH_SHORT).show();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Drag and Drop");
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
//		Intent intent = getIntent();
//		ArrayList<String> checkedItemsArrayList = new ArrayList<>();
//		checkedItemsArrayList = intent.getStringArrayListExtra("CHECKED_ITEMS");
//		ArrayList<DummyModel> list = new ArrayList<>();
//
////		list.add(new DummyModel(0, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/0.jpg", "Monument walk tour", R.string.fontello_heart_empty));
////		list.add(new DummyModel(1, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/1.jpg", "River walk tour", R.string.fontello_heart_empty));
////		list.add(new DummyModel(2, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/2.jpg", "City walk tour", R.string.fontello_heart_empty));
////		list.add(new DummyModel(3, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/3.jpg", "Park walk tour", R.string.fontello_heart_empty));
////		list.add(new DummyModel(4, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/4.jpg", "Vilage walk tour", R.string.fontello_heart_empty));
////		list.add(new DummyModel(5, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/5.jpg", "Lake walk tour", R.string.fontello_heart_empty));
////		list.add(new DummyModel(6, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/6.jpg", "Castle walk tour", R.string.fontello_heart_empty));
////		list.add(new DummyModel(7, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/7.jpg", "Beach walk tour", R.string.fontello_heart_empty));
//		for(int i = 0; i < checkedItemsArrayList.size(); i ++)
//		{
//			list.add(new DummyModel(i, "http://pengaja.com/uiapptemplate/newphotos/listviews/draganddrop/travel/"+i+".jpg", checkedItemsArrayList.get(i), R.string.fontello_heart_empty));
//		}

		final DragAndDropTravelAdapter adapter = new DragAndDropTravelAdapter(this, list);


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
	}
}
