package com.knucapstone.tripjuvo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.knucapstone.tripjuvo.database.ExpandableData;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.font.RobotoTextView;
import com.knucapstone.tripjuvo.util.ImageUtil;
import com.knucapstone.tripjuvo.view.AnimatedExpandableListView;
import com.knucapstone.tripjuvo.view.AnimatedExpandableListView.AnimatedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExpandableTravelListViewActivity extends AppCompatActivity {

	private AnimatedExpandableListView listView;
	private ExampleAdapter adapter;
	private String city_name;
	private String picture_URL;
	private Activity activity;
	private final int childAdd = 9190;
	ArrayList<ExpandableData> expandableDatalist = new ArrayList<ExpandableData>();
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expandable_list_view_travel);
		Intent intent = new Intent();
		intent = getIntent();
		city_name = new String();
		picture_URL= new String();
		if(intent != null)
		{
			city_name = intent.getStringExtra("CityName");
			picture_URL = intent.getStringExtra("picture_URL");
		}

		activity = new Activity();


		List<GroupItem> items = new ArrayList<GroupItem>();
		items = fillData(items);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("상세 정보");

		adapter = new ExampleAdapter(this);
		adapter.setData(items);

		//Git Test
		//com.knucapstone.tripjuvo;
		RobotoTextView title;
		ImageView imageview;

		listView = (AnimatedExpandableListView) findViewById(R.id.activity_expandable_travel_list_view);

		View headerView = getLayoutInflater().inflate(R.layout.header_expandable_list_view_travel, listView, false);

		title = (RobotoTextView)headerView.findViewById(R.id.title);
		title.setText(city_name);
		imageview = (ImageView)headerView.findViewById(R.id.imageView1);
		Log.i("Pic_URL",picture_URL+"");
		ImageUtil.displayImage(imageview, picture_URL, null);

		listView.addHeaderView(headerView);
		listView.setDividerHeight(0);
		listView.setAdapter(adapter);

		activity = this;

		// In order to show animations, we need to use a custom click handler
		// for our ExpandableListView.
		listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// We call collapseGroupWithAnimation(int) and
				// expandGroupWithAnimation(int) to animate group
				// expansion/collapse.
				if (listView.isGroupExpanded(groupPosition)) {
					listView.collapseGroupWithAnimation(groupPosition);
				} else {
					listView.expandGroupWithAnimation(groupPosition);
				}
				return true;
			}

		});
		listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				int poi_id = 0;
				for(int i =0; i< expandableDatalist.size();i++) {
					if (expandableDatalist.get(i).getPosition() == groupPosition && expandableDatalist.get(i).getChildPosition() == childPosition)
						poi_id = expandableDatalist.get(i).getPoi_id();
				}
				Intent intent;
				Log.i("poiID",""+poi_id);
				if(poi_id == childAdd)
				{
					//intent = PoiDetailActivity.newIntent(activity, 0);
					intent = new Intent(activity, CityActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("ThisCity", city_name);
					intent.putExtra("groupPosition", groupPosition);
					//startActivity(intent);
					startActivityForResult(intent, 0);
				}
				else {
					intent = PoiDetailActivity.newIntent(activity, poi_id);
					intent.putExtra("groupPosition", groupPosition);
					startActivityForResult(intent, 0);
				}
				Log.i("onChildClick", groupPosition + "  " +childPosition+ "  "+id);
				//startActivity(intent);

				return false;
			}

		});

		// Set indicator (arrow) to the right
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// Log.v("width", width + "");
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				50, r.getDisplayMetrics());
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			listView.setIndicatorBounds(width - px, width);
		} else {
			listView.setIndicatorBoundsRelative(width - px, width);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int groupPosition = data.getIntExtra("retVal", -1);
		Log.i("onActivityResult", "groupPosition : " + groupPosition);

		List<GroupItem> items = new ArrayList<GroupItem>();
		items = fillData(items);
		adapter = new ExampleAdapter(this);
		adapter.setData(items);
		listView.setAdapter(adapter);
		listView.expandGroupWithAnimation(groupPosition);

	}

	private static class GroupItem {
		String title;
		int icon;
		List<ChildItem> items = new ArrayList<ChildItem>();
	}

	private static class ChildItem {
		String title;
		int poi_id;
	}

	private static class ChildHolder {
		TextView title;
	}

	private static class GroupHolder {
		TextView title;
		TextView icon;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private List<GroupItem> fillData(List<GroupItem> items) {

		//category_id ->1 = where to eat, 2 = where to sleep, 3 = where to go
		SQLiteDatabase db = openOrCreateDatabase("cityguide.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

		Cursor c_eat= db.rawQuery("SELECT * from pois where favorite = 1 and category_id = 1 and city = '" + city_name + "';", null);
		Cursor c_sleep= db.rawQuery("SELECT * from pois where favorite = 1 and category_id = 2 and city = '" +city_name+ "';",null);
		Cursor c_go= db.rawQuery("SELECT * from pois where favorite = 1 and category_id = 3 and city = '" +city_name+ "';",null);

		c_eat.moveToFirst();
		int cnt_eat = 0;
		c_sleep.moveToFirst();
		int cnt_sleep =0;
		c_go.moveToFirst();
		int cnt_go = 0;
		//fu
		GroupItem item = new GroupItem();
		item.title = "Where to go";
		item.icon = R.string.material_icon_go;
		ChildItem child;

		while(c_go.getCount() > 0) {
			child = new ChildItem();
			child.title = c_go.getString(2);
			child.poi_id = c_go.getInt(0);
			item.items.add(child);
			ExpandableData ExpD = new ExpandableData(c_go.getInt(0),0,cnt_go);
			expandableDatalist.add(ExpD);
			cnt_go++;
			if(c_go.isLast())
				break;
			c_go.moveToNext();
		}
		child = new ChildItem();
		child.title = "+ ADD";
		child.poi_id = childAdd;
		item.items.add(child);
		ExpandableData ExpD = new ExpandableData(child.poi_id,0,cnt_go);
		expandableDatalist.add(ExpD);
		cnt_go++;

		items.add(item);

		item = new GroupItem();
		item.title = "Where to sleep";
		item.icon = R.string.material_icon_sleep;
		while(c_sleep.getCount() > 0) {
			child = new ChildItem();
			child.title = c_sleep.getString(2);
			child.poi_id = c_sleep.getInt(0);
			item.items.add(child);
			ExpD = new ExpandableData(c_sleep.getInt(0),1,cnt_sleep);
			expandableDatalist.add(ExpD);
			cnt_sleep++;
			if(c_sleep.isLast())
				break;
			c_sleep.moveToNext();
		}
		child = new ChildItem();
		child.title = "+ ADD";
		child.poi_id = childAdd;
		item.items.add(child);

		items.add(item);

		item = new GroupItem();
		item.title = "Where to eat";
		item.icon = R.string.material_icon_eat;
		while(c_eat.getCount() > 0) {
			child = new ChildItem();
			child.title = c_eat.getString(2);
			child.poi_id = c_eat.getInt(0);
			item.items.add(child);
			ExpD = new ExpandableData(c_eat.getInt(0),2,cnt_eat);
			expandableDatalist.add(ExpD);
			cnt_eat++;
			if(c_eat.isLast())
				break;
			c_eat.moveToNext();
		}
		child = new ChildItem();
		child.title = "+ ADD";
		child.poi_id = childAdd;
		item.items.add(child);

		items.add(item);

		return items;
	}

	private class ExampleAdapter extends AnimatedExpandableListAdapter {

		private LayoutInflater inflater;

		private List<GroupItem> items;

		public ExampleAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void setData(List<GroupItem> items) {
			this.items = items;
		}

		@Override
		public ChildItem getChild(int groupPosition, int childPosition) {
			Log.i("ChildItemClick","asdf");
			return items.get(groupPosition).items.get(childPosition);

		}
		@Override
		public long getChildId(int groupPosition, int childPosition) {

			Log.i("ChildItemClick", "asdf2");
			return childPosition;
		}

		@Override
		public View getRealChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildHolder holder;
			ChildItem item = getChild(groupPosition, childPosition);
			if (convertView == null) {
				holder = new ChildHolder();
				convertView = inflater.inflate(
						R.layout.list_item_expandable_travel_child, parent,
						false);
				holder.title = (TextView) convertView
						.findViewById(R.id.textTitle);
				convertView.setTag(holder);
			} else {
				holder = (ChildHolder) convertView.getTag();
			}

			holder.title.setText(item.title);

			return convertView;
		}

		@Override
		public int getRealChildrenCount(int groupPosition) {
			return items.get(groupPosition).items.size();
		}

		@Override
		public GroupItem getGroup(int groupPosition) {
			return items.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return items.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder holder;
			GroupItem item = getGroup(groupPosition);
			if (convertView == null) {
				holder = new GroupHolder();
				convertView = inflater.inflate(
						R.layout.list_item_expandable_travel, parent, false);
				holder.title = (TextView) convertView
						.findViewById(R.id.list_item_expandable_travel_textTitle);
				holder.icon = (TextView) convertView
						.findViewById(R.id.list_item_expandable_travel_icon);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}

			holder.title.setText(item.title);
			holder.icon.setText(item.icon);
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}

	}
}
