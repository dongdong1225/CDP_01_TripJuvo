package com.knucapstone.tripjuvo.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.knucapstone.tripjuvo.CityGuideConfig;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.activity.MapActivity;
import com.knucapstone.tripjuvo.activity.PoiDetailActivity;
import com.knucapstone.tripjuvo.database.DatabaseCallListener;
import com.knucapstone.tripjuvo.database.DatabaseCallManager;
import com.knucapstone.tripjuvo.database.DatabaseCallTask;
import com.knucapstone.tripjuvo.database.dao.CategoryDAO;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.CategoryModel;
import com.knucapstone.tripjuvo.database.model.PoiModel;
import com.knucapstone.tripjuvo.database.query.PoiReadAllQuery;
import com.knucapstone.tripjuvo.database.query.Query;
import com.knucapstone.tripjuvo.graphics.BitmapScaler;
import com.knucapstone.tripjuvo.utility.Logcat;
import com.knucapstone.tripjuvo.utility.NetworkUtility;
import com.knucapstone.tripjuvo.utility.PermissionUtility;
import com.knucapstone.tripjuvo.utility.Preferences;
import com.knucapstone.tripjuvo.utility.Version;
import com.knucapstone.tripjuvo.view.StatefulLayout;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MapFragment extends TaskFragment implements DatabaseCallListener
{
	private static final int MAP_ZOOM = 14;

	private View mRootView;
	private StatefulLayout mStatefulLayout;
	private MapView mMapView;
	private DatabaseCallManager mDatabaseCallManager = new DatabaseCallManager();
	private List<PoiModel> mPoiList = new ArrayList<>();
	private ClusterManager<PoiModel> mClusterManager;
	private Map<Long, BitmapDescriptor> mBitmapDescriptorMap = new HashMap<>();
	private long mPoiId = -1L;
	private double mPoiLatitude = 0.0;
	private double mPoiLongitude = 0.0;
	
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		// handle intent extras
		Bundle extras = getActivity().getIntent().getExtras();
		if(extras != null)
		{
			handleExtras(extras);
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mRootView = inflater.inflate(R.layout.fragment_map, container, false);
		initMap();
		mMapView = (MapView) mRootView.findViewById(R.id.fragment_map_mapview);
		mMapView.onCreate(savedInstanceState);
		return mRootView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// setup map
		setupMap();
		setupClusterManager();

		// setup stateful layout
		setupStatefulLayout(savedInstanceState);

		// load data
		if(mPoiList==null || mPoiList.isEmpty()) loadData();

		// check permissions
		PermissionUtility.checkPermissionAccessLocation(this);
	}
	
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();

		// map
		if(mMapView!=null) mMapView.onResume();
	}
	
	
	@Override
	public void onPause()
	{
		super.onPause();

		// map
		if(mMapView!=null) mMapView.onPause();
	}
	
	
	@Override
	public void onStop()
	{
		super.onStop();
	}
	
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		mRootView = null;
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// map
		if(mMapView!=null) mMapView.onDestroy();

		// cancel async tasks
		mDatabaseCallManager.cancelAllTasks();
	}
	
	
	@Override
	public void onDetach()
	{
		super.onDetach();
	}


	@Override
	public void onLowMemory()
	{
		super.onLowMemory();

		// map
		if(mMapView!=null) mMapView.onLowMemory();
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// save current instance state
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);

		// stateful layout state
		if(mStatefulLayout!=null) mStatefulLayout.saveInstanceState(outState);

		// map
		if(mMapView!=null) mMapView.onSaveInstanceState(outState);
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// action bar menu
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_map, menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// action bar menu behavior
		switch(item.getItemId())
		{
			case R.id.menu_fragment_map_layers_normal:
				setMapType(GoogleMap.MAP_TYPE_NORMAL);
				return true;

			case R.id.menu_fragment_map_layers_satellite:
				setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				return true;

			case R.id.menu_fragment_map_layers_hybrid:
				setMapType(GoogleMap.MAP_TYPE_HYBRID);
				return true;

			case R.id.menu_fragment_map_layers_terrain:
				setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		switch(requestCode)
		{
			case PermissionUtility.REQUEST_PERMISSION_ACCESS_LOCATION:
			{
				// if request is cancelled, the result arrays are empty
				if(grantResults.length > 0)
				{
					for(int i=0; i<grantResults.length; i++)
					{
						if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
						{
							// permission granted
							String permission = permissions[i];
							if(permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) ||
									permission.equals(Manifest.permission.ACCESS_FINE_LOCATION))
							{
								// do nothing
							}
						}
						else
						{
							// permission denied
						}
					}
				}
				else
				{
					// all permissions denied
				}
				break;
			}
		}
	}


	@Override
	public void onDatabaseCallRespond(final DatabaseCallTask task, final Data<?> data)
	{
		runTaskCallback(new Runnable()
		{
			public void run()
			{
				if(mRootView == null) return; // view was destroyed

				if(task.getQuery().getClass().equals(PoiReadAllQuery.class))
				{
					Logcat.d("PoiReadAllQuery");

					// get data
					Data<List<PoiModel>> poiReadAllData = (Data<List<PoiModel>>) data;
					List<PoiModel> poiList = poiReadAllData.getDataObject();
					mPoiList.clear();
					Iterator<PoiModel> iterator = poiList.iterator();
					while(iterator.hasNext())
					{
						PoiModel poi = iterator.next();
						mPoiList.add(poi);
					}
				}

				// hide progress and bind data
				if(mPoiList!=null && !mPoiList.isEmpty()) mStatefulLayout.showContent();
				else mStatefulLayout.showEmpty();

				// finish query
				mDatabaseCallManager.finishTask(task);
			}
		});
	}


	@Override
	public void onDatabaseCallFail(final DatabaseCallTask task, final Exception exception)
	{
		runTaskCallback(new Runnable()
		{
			public void run()
			{
				if(mRootView == null) return; // view was destroyed

				if(task.getQuery().getClass().equals(PoiReadAllQuery.class))
				{
					Logcat.d("PoiReadAllQuery / exception " + exception.getClass().getSimpleName() + " / " + exception.getMessage());
				}

				// hide progress
				if(mPoiList!=null && !mPoiList.isEmpty()) mStatefulLayout.showContent();
				else mStatefulLayout.showEmpty();

				// handle fail
				handleFail();

				// finish query
				mDatabaseCallManager.finishTask(task);
			}
		});
	}


	private void handleFail()
	{
		Toast.makeText(getActivity(), R.string.global_database_fail_toast, Toast.LENGTH_LONG).show();
	}


	private void handleExtras(Bundle extras)
	{
		if(extras.containsKey(MapActivity.EXTRA_POI_ID))
		{
			mPoiId = extras.getLong(MapActivity.EXTRA_POI_ID);
		}
		if(extras.containsKey(MapActivity.EXTRA_POI_LATITUDE))
		{
			mPoiLatitude = extras.getDouble(MapActivity.EXTRA_POI_LATITUDE);
		}
		if(extras.containsKey(MapActivity.EXTRA_POI_LONGITUDE))
		{
			mPoiLongitude = extras.getDouble(MapActivity.EXTRA_POI_LONGITUDE);
		}
	}
	
	
	private void loadData()
	{
		if(!mDatabaseCallManager.hasRunningTask(PoiReadAllQuery.class))
		{
			// show progress
			mStatefulLayout.showProgress();

			// run async task
			Query query = new PoiReadAllQuery();
			mDatabaseCallManager.executeTask(query, this);
		}
	}


	private void bindData()
	{
		// reference
		final GoogleMap map = ((MapView) mRootView.findViewById(R.id.fragment_map_mapview)).getMap();

		// map
		if(map!=null)
		{
			// add pois
			map.clear();
			mClusterManager.clearItems();
			for(PoiModel poi : mPoiList)
			{
				mClusterManager.addItem(poi);
			}
			mClusterManager.cluster();
		}

	}


	private void initMap()
	{
		if(!Version.isSupportedOpenGlEs2(getActivity()))
		{
			Toast.makeText(getActivity(), R.string.global_map_fail_toast, Toast.LENGTH_LONG).show();
		}

		try
		{
			MapsInitializer.initialize(getActivity());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	private void setupStatefulLayout(Bundle savedInstanceState)
	{
		// reference
		mStatefulLayout = (StatefulLayout) mRootView;

		// state change listener
		mStatefulLayout.setOnStateChangeListener(new StatefulLayout.OnStateChangeListener()
		{
			@Override
			public void onStateChange(View v, StatefulLayout.State state)
			{
				Logcat.d("" + (state==null ? "null" : state.toString()));

				// bind data
				if(state==StatefulLayout.State.CONTENT)
				{
					if(mPoiList!=null && !mPoiList.isEmpty()) bindData();
				}
			}
		});

		// restore state
		mStatefulLayout.restoreInstanceState(savedInstanceState);
	}


	private void setupMap()
	{
		// reference
		GoogleMap map = ((MapView) mRootView.findViewById(R.id.fragment_map_mapview)).getMap();

		// settings
		if(map!=null)
		{
			Preferences preferences = new Preferences(getActivity());

			map.setMapType(preferences.getMapType());

			// check access location permission
			if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
			{
				map.setMyLocationEnabled(true);
			}

			UiSettings settings = map.getUiSettings();
			settings.setAllGesturesEnabled(true);
			settings.setMyLocationButtonEnabled(true);
			settings.setZoomControlsEnabled(true);

			LatLng latLng = null;
			if(mPoiLatitude==0.0 && mPoiLongitude==0.0 &&
					ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
			{
				LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
				Location location = getLastKnownLocation(locationManager);
				if(location!=null) latLng = new LatLng(location.getLatitude(), location.getLongitude());
			}
			else
			{
				latLng = new LatLng(mPoiLatitude, mPoiLongitude);
			}

			if(latLng != null)
			{
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(latLng)
						.zoom(MAP_ZOOM)
						.bearing(0)
						.tilt(0)
						.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
		}
	}


	private void setupClusterManager()
	{
		// reference
		GoogleMap map = ((MapView) mRootView.findViewById(R.id.fragment_map_mapview)).getMap();

		// clustering
		if(map!=null)
		{
			mClusterManager = new ClusterManager<>(getActivity(), map);
			mClusterManager.setRenderer(new DefaultClusterRenderer<PoiModel>(getActivity(), map, mClusterManager)
			{
				@Override
				protected void onBeforeClusterItemRendered(PoiModel poi, MarkerOptions markerOptions)
				{
					CategoryModel category = poi.getCategory();
					BitmapDescriptor bitmapDescriptor = loadBitmapDescriptor(category);

					markerOptions.title(poi.getName());
					markerOptions.icon(bitmapDescriptor);

					super.onBeforeClusterItemRendered(poi, markerOptions);
				}
			});
			mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<PoiModel>()
			{
				@Override
				public void onClusterItemInfoWindowClick(PoiModel poiModel)
				{
					startPoiDetailActivity(poiModel.getId());
				}
			});
			map.setOnCameraChangeListener(mClusterManager);
			map.setOnInfoWindowClickListener(mClusterManager);
		}
	}


	private Location getLastKnownLocation(LocationManager locationManager)
	{
		Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		long timeNet = 0L;
		long timeGps = 0L;

		if(locationNet!=null)
		{
			timeNet = locationNet.getTime();
		}

		if(locationGps!=null)
		{
			timeGps = locationGps.getTime();
		}

		if(timeNet>timeGps) return locationNet;
		else return locationGps;
	}


	private void setMapType(int type)
	{
		GoogleMap map = ((MapView) mRootView.findViewById(R.id.fragment_map_mapview)).getMap();
		if(map!=null)
		{
			map.setMapType(type);

			Preferences preferences = new Preferences(getActivity());
			preferences.setMapType(type);
		}
	}


	private BitmapDescriptor loadBitmapDescriptor(CategoryModel category)
	{
		BitmapDescriptor bitmapDescriptor = mBitmapDescriptorMap.get(category.getId());
		if(bitmapDescriptor==null)
		{
			try
			{
				CategoryDAO.refresh(category);
				bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(loadMarkerBitmap(category.getMarker()));
			}
			catch(SQLException | IOException | IllegalArgumentException e)
			{
				bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(getColorAccentHue());
			}
			mBitmapDescriptorMap.put(category.getId(), bitmapDescriptor);
		}
		return bitmapDescriptor;
	}


	private Bitmap loadMarkerBitmap(String path) throws IOException, IllegalArgumentException
	{
		int size = getActivity().getResources().getDimensionPixelSize(R.dimen.fragment_map_marker_size);
		InputStream inputStream = getActivity().getAssets().open(path);
		Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
		Bitmap bitmap = BitmapScaler.scaleToFill(originalBitmap, size, size);
		if(originalBitmap!=bitmap) originalBitmap.recycle();
		inputStream.close();
		return bitmap;
	}


	private float getColorAccentHue()
	{
		// get accent color
		TypedValue typedValue = new TypedValue();
		getActivity().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
		int markerColor = typedValue.data;

		// get hue
		float[] hsv = new float[3];
		Color.colorToHSV(markerColor, hsv);
		return hsv[0];
	}


	private void startPoiDetailActivity(long poiId)
	{
		Intent intent = PoiDetailActivity.newIntent(getActivity(), poiId);
		startActivity(intent);
	}
}
