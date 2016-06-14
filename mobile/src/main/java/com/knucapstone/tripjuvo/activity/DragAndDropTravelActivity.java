package com.knucapstone.tripjuvo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.adapter.DragAndDropTravelAdapter;
import com.knucapstone.tripjuvo.database.model.PoiModel;
import com.knucapstone.tripjuvo.fragment.PoiListFragment;
import com.knucapstone.tripjuvo.geolocation.Geolocation;
import com.knucapstone.tripjuvo.geolocation.GeolocationListener;
import com.knucapstone.tripjuvo.model.DummyModel;
import com.knucapstone.tripjuvo.model.TravelDummyModel;
import com.knucapstone.tripjuvo.utility.ImageLoaderUtility;
import com.knucapstone.tripjuvo.utility.LocationUtility;
import com.knucapstone.tripjuvo.utility.Logcat;
import com.knucapstone.tripjuvo.utility.PermissionUtility;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;

public class DragAndDropTravelActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

	private DynamicListView mDynamicListView;
	Intent intent;
	ArrayList<DummyModel> list;
	ArrayList<Location> locationArrayList;
	ArrayList<String> pictureArrayList;

	private LatLng curPoint;
	private double curLati = 0;
	private double curLongi = 0;
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_views);

		//getCurrentPostion();
		// Create an instance of GoogleAPIClient.
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
		}

		mDynamicListView = (DynamicListView) findViewById(R.id.dynamic_listview);
		mDynamicListView.setDividerHeight(0);

		intent = getIntent();
		ArrayList<String> checkedItemsArrayList = new ArrayList<>();
		checkedItemsArrayList = intent.getStringArrayListExtra("CHECKED_ITEMS");
		list = new ArrayList<>();

//		synchronized (this)
//		{
//			try {
//				this.wait();
//			}
//			catch (Exception e)
//			{
//
//			}
//		}
		setUpDragAndDrop();
		Toast.makeText(this, "Long press an item to start dragging",
				Toast.LENGTH_SHORT).show();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("여행 추천 경로");
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}

	@Override
	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
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

		curPoint = new LatLng(curLati, curLongi);
		Log.i("curPoint", curPoint.latitude+" @@ " + curPoint.longitude);

		for(int i = 0; i < locationArrayList.size(); i ++)
		{
			list.add(new TravelDummyModel(i, pictureArrayList.get(i),
											locationArrayList.get(i).getProvider(),
											R.string.fontello_heart_empty,
											calculatePoiDistances(i)+"",
											"100"));
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

	private int calculatePoiDistances(int index)
	{
		int distance = 0;
//		if(curPoint!=null)
//		{
//			for(int i=0; i<locationArrayList.size(); i++)
//			{
//				LatLng cityLocation = new LatLng(locationArrayList.get(index).getLatitude(), locationArrayList.get(index).getLongitude());
//				Log.i("!@#$", locationArrayList.get(index).getLatitude() +"");
//				distance = LocationUtility.getDistance(curPoint, cityLocation);
//				Log.i("distance", distance+"");
//			}
//		}
		LatLng cityLocation = new LatLng(locationArrayList.get(index).getLatitude(), locationArrayList.get(index).getLongitude());
		Log.i("!@#$", locationArrayList.get(index).getLatitude() +"");
		distance = LocationUtility.getDistance(curPoint, cityLocation);
		Log.i("distance", distance+"");

		return distance;
	}

	public void getCurrentPostion()
	{
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				//showNewLocation(location);
				//curPoint = new LatLng(location.getLatitude(), location.getLongitude());
				curLati = location.getLatitude();
				curLongi = location.getLongitude();
				String msg = "Lati : " + curLati + "\nLongi : "+ curLongi;
				Log.i("myLocation", msg);
			}
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			public void onProviderEnabled(String provider) {
			}
			public void onProviderDisabled(String provider) {
			}
		} ;

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	}

	@Override
	public void onConnected(Bundle bundle) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		if (mLastLocation != null) {
//			mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//			mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
			curLati = mLastLocation.getLatitude();
			curLongi = mLastLocation.getLongitude();
			Log.i("onConnected", "Lati : " + curLati + "@@" + "Longi : " + curLongi);
			curPoint = new LatLng(curLati, curLongi);
			Log.i("onConnected", curPoint.latitude+" @@ " + curPoint.longitude);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

}
