package com.knucapstone.tripjuvo.fragment;

import android.Manifest;
import android.animation.Animator;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.TSP.AdjMatrix;
import com.knucapstone.tripjuvo.TSP.Spot;
import com.knucapstone.tripjuvo.activity.DragAndDropTravelActivity;
import com.knucapstone.tripjuvo.activity.MapActivity;
import com.knucapstone.tripjuvo.activity.PoiDetailActivity;
import com.knucapstone.tripjuvo.adapter.PoiListAdapter;
import com.knucapstone.tripjuvo.adapter.SearchSuggestionAdapter;
import com.knucapstone.tripjuvo.content.PoiSearchRecentSuggestionsProvider;
import com.knucapstone.tripjuvo.database.DatabaseCallListener;
import com.knucapstone.tripjuvo.database.DatabaseCallManager;
import com.knucapstone.tripjuvo.database.DatabaseCallTask;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;
import com.knucapstone.tripjuvo.database.query.PoiReadAllQuery;
import com.knucapstone.tripjuvo.database.query.PoiReadByCategoryQuery;
import com.knucapstone.tripjuvo.database.query.PoiReadFavoritesQuery;
import com.knucapstone.tripjuvo.database.query.PoiSearchQuery;
import com.knucapstone.tripjuvo.database.query.Query;
import com.knucapstone.tripjuvo.dialog.AboutDialogFragment;
import com.knucapstone.tripjuvo.geolocation.Geolocation;
import com.knucapstone.tripjuvo.geolocation.GeolocationListener;
import com.knucapstone.tripjuvo.listener.OnSearchListener;
import com.knucapstone.tripjuvo.utility.ImageLoaderUtility;
import com.knucapstone.tripjuvo.utility.LocationUtility;
import com.knucapstone.tripjuvo.utility.Logcat;
import com.knucapstone.tripjuvo.utility.PermissionUtility;
import com.knucapstone.tripjuvo.view.GridSpacingItemDecoration;
import com.knucapstone.tripjuvo.view.StatefulLayout;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class PoiListFragment extends TaskFragment implements DatabaseCallListener, GeolocationListener, PoiListAdapter.PoiViewHolder.OnItemClickListener
{
	public static final long CATEGORY_ID_ALL = -1L;
	public static final long CATEGORY_ID_FAVORITES = -2L;
	public static final long CATEGORY_ID_SEARCH = -3L;
	public static final long CATEGORY_ID_findPath = -4;

	private static final String ARGUMENT_CATEGORY_ID = "category_id";
	private static final String ARGUMENT_SEARCH_QUERY = "search_query";
	private static final String DIALOG_ABOUT = "about";
	private static final long TIMER_DELAY = 60000L; // in milliseconds
	private static final int LAZY_LOADING_TAKE = 128;
	private static final int LAZY_LOADING_OFFSET = 4;

	private boolean mLazyLoading = false;
	private View mRootView;
	private StatefulLayout mStatefulLayout;
	private PoiListAdapter mAdapter;
	private OnSearchListener mSearchListener;
	private ActionMode mActionMode;
	private DatabaseCallManager mDatabaseCallManager = new DatabaseCallManager();
	private Geolocation mGeolocation = null;
	private Location mLocation = null; //내위치
	private Handler mTimerHandler;
	private Runnable mTimerRunnable;
	private long mCategoryId;
	private String mSearchQuery;
	private List<PoiModel> mPoiList = new ArrayList<>();
	private List<Object> mFooterList = new ArrayList<>();
	private phpDown task;
	private ArrayList<Spot> spotArrayList;
	private AdjMatrix adjMatrix;

	private int beaconMinorValue = 0;

	private ArrayList<String> cityList;

	public static PoiListFragment newInstance(long categoryId)
	{
		PoiListFragment fragment = new PoiListFragment();

		// arguments
		Bundle arguments = new Bundle();
		arguments.putLong(ARGUMENT_CATEGORY_ID, categoryId);
		fragment.setArguments(arguments);

		return fragment;
	}


	public static PoiListFragment newInstance(String searchQuery)
	{
		PoiListFragment fragment = new PoiListFragment();

		// arguments
		Bundle arguments = new Bundle();
		arguments.putLong(ARGUMENT_CATEGORY_ID, CATEGORY_ID_SEARCH);
		arguments.putString(ARGUMENT_SEARCH_QUERY, searchQuery);
		fragment.setArguments(arguments);

		return fragment;
	}


	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		// set search listener
		try
		{
			mSearchListener = (OnSearchListener) getActivity();
		}
		catch(ClassCastException e)
		{
			throw new ClassCastException(getActivity().getClass().getName() + " must implement " + OnSearchListener.class.getName());
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		task = new phpDown();
		task.execute("http://tripjuvo.ivyro.net/selectAllFromRegion.php");
		synchronized (this)
		{
			try{
				//task.wait();
				task.get();
			}
			catch (Exception e)
			{
				Log.i("sync err",e.getMessage());

			}
		}

		// handle fragment arguments
		Bundle arguments = getArguments();
		if(arguments != null)
		{
			handleArguments(arguments);
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mRootView = inflater.inflate(R.layout.fragment_poi_list, container, false);
		setupRecyclerView();
		return mRootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// setup stateful layout
		setupStatefulLayout(savedInstanceState);

		showFloatingActionButton2(false);

		// load data
		if (mPoiList == null || mPoiList.isEmpty()) loadData();

		// lazy loading progress
		if(mLazyLoading) showLazyLoadingProgress(true);

		// show toolbar if hidden
		showToolbar(true);

		// check permissions
		PermissionUtility.checkPermissionAll(this);

		// init timer task
		setupTimer();

		if(mCategoryId == CATEGORY_ID_FAVORITES)
		{
			showFloatingActionButton2(true);
			final FloatingActionButton floatingActionButton1 = (FloatingActionButton) getActivity().findViewById(R.id.fab_findPath);
			//find path fab
			floatingActionButton1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPoiList.isEmpty())
						return;

					Intent intent = new Intent(getActivity(), DragAndDropTravelActivity.class);

					cityList = new ArrayList<String>();

					for (int i = 0; i < mPoiList.size(); i++) {
						if(!( cityList.contains(mPoiList.get(i).getCity())) ) {
							cityList.add(mPoiList.get(i).getCity());
						}
					}
					ArrayList<Spot> fovoraitedCity = new ArrayList<Spot>();
					for(String x : cityList) {
						Log.i("Added City : ", x);
						//fovoraitedCity.add(spotArrayList.get(city));
					}
					for(Spot spot : spotArrayList)
					{
						for(String cityname : cityList) {
							if (spot.getProvider().equals(cityname))
							{
								fovoraitedCity.add(spot);
							}
						}
					}
					Log.i("wait for task","Finish");



					//AdjMatrix adjMatrix = new AdjMatrix(spotArrayList);
					ArrayList<Spot> sortedList = doTSP(fovoraitedCity);

					ArrayList<Location> locationArrayList = new ArrayList<>();
					ArrayList<String> pictureArrayList = new ArrayList<>();
					for (int i = 0; i < sortedList.size(); i++) {
						Location temp = new Location(sortedList.get(i).getProvider());
						temp.setLatitude(sortedList.get(i).getLatitude());
						temp.setLongitude(sortedList.get(i).getLongitude());

						locationArrayList.add(temp);
						pictureArrayList.add(sortedList.get(i).getPicture());
					}

					//Intent intent = new Intent(FindPathActivity.this, testActivity.class);
					intent.putExtra("locationArrayList", locationArrayList);
					intent.putExtra("pictureArrayList", pictureArrayList);
					startActivity(intent);

//					else
//					{
//						Intent intent = new Intent(getActivity(), FindPathActivity.class);
//						startActivity(intent);
//					}
					//mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new SearchActionModeCallback());
				}
			});
		}


//		Log.i("beaconbeacon", 1 + "@");
//		beaconMinorValue = getArguments().getInt("BeaconMinorValue", 0);
//		Log.i("beaconbeacon", beaconMinorValue + "@@");
//		if(beaconMinorValue == 16522)
//		{
//			Log.i("beaconbeacon", 2 + "@");
//			// position
//			//int poiPosition = mAdapter.getPoiPosition(1);
//
//			// start activity
////			PoiModel poi = mPoiList.get(poiPosition);
//			PoiModel poi = mPoiList.get(1);
//			startPoiDetailActivity(getView(), poi.getId());
//			Log.i("beaconbeacon", 3 + "@");
//		}
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

		// timer
		startTimer();
	}


	@Override
	public void onPause()
	{
		super.onPause();

		// timer
		stopTimer();

		// stop adapter
		if(mAdapter!=null) mAdapter.stop();

		// stop geolocation
		if(mGeolocation!=null) mGeolocation.stop();
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

		// cancel async tasks
		mDatabaseCallManager.cancelAllTasks();
	}


	@Override
	public void onDetach()
	{
		super.onDetach();
	}


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// save current instance state
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);

		// stateful layout state
		if(mStatefulLayout!=null) mStatefulLayout.saveInstanceState(outState);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// action bar menu
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_poi_list, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// action bar menu behavior
		switch(item.getItemId())
		{
			case R.id.menu_fragment_poi_list_map:
				startMapActivity();
				return true;

			case R.id.menu_rate:
				startWebActivity(getString(R.string.app_store_uri, CityGuideApplication.getContext().getPackageName()));
				return true;

			case R.id.menu_about:
				showAboutDialog();
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
			case PermissionUtility.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
			case PermissionUtility.REQUEST_PERMISSION_ACCESS_LOCATION:
			case PermissionUtility.REQUEST_PERMISSION_ALL:
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
							if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
							{
								synchronized(this)
								{
									ImageLoader.getInstance().destroy();
									ImageLoaderUtility.init(getActivity().getApplicationContext());
								}
							}
							else if(permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) ||
									permission.equals(Manifest.permission.ACCESS_FINE_LOCATION))
							{
								mGeolocation = null;
								mGeolocation = new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), this);
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
	public void onItemClick(View view, int position, long id, int viewType)
	{
		// position
		int poiPosition = mAdapter.getPoiPosition(position);

		// start activity
		PoiModel poi = mPoiList.get(poiPosition);
		startPoiDetailActivity(view, poi.getId());
	}


	@Override
	public void onDatabaseCallRespond(final DatabaseCallTask task, final Data<?> data)
	{
		runTaskCallback(new Runnable() {
			public void run() {
				if (mRootView == null) return; // view was destroyed

				if (task.getQuery().getClass().equals(PoiReadAllQuery.class))
				{
					Logcat.d("PoiReadAllQuery");

					// get data
					Data<List<PoiModel>> poiReadAllData = (Data<List<PoiModel>>) data;
					List<PoiModel> poiList = poiReadAllData.getDataObject();
					Iterator<PoiModel> iterator = poiList.iterator();
					while (iterator.hasNext()) {
						PoiModel poi = iterator.next();
						mPoiList.add(poi);
					}
				}
				else if (task.getQuery().getClass().equals(PoiReadFavoritesQuery.class))
				{
					Logcat.d("PoiReadFavoritesQuery");

					// get data
					Data<List<PoiModel>> poiReadFavoritesData = (Data<List<PoiModel>>) data;
					List<PoiModel> poiList = poiReadFavoritesData.getDataObject();
					Iterator<PoiModel> iterator = poiList.iterator();
					while (iterator.hasNext()) {
						PoiModel poi = iterator.next();
						mPoiList.add(poi);
					}

					//showFloatingActionButton2(true);
				}
				else if (task.getQuery().getClass().equals(PoiSearchQuery.class)) {
					Logcat.d("PoiSearchQuery");

					// get data
					Data<List<PoiModel>> poiSearchData = (Data<List<PoiModel>>) data;
					List<PoiModel> poiList = poiSearchData.getDataObject();
					Iterator<PoiModel> iterator = poiList.iterator();
					while (iterator.hasNext()) {
						PoiModel poi = iterator.next();
						mPoiList.add(poi);
					}
				} else if (task.getQuery().getClass().equals(PoiReadByCategoryQuery.class)) {
					Logcat.d("PoiReadByCategoryQuery");

					// get data
					Data<List<PoiModel>> poiReadByCategoryData = (Data<List<PoiModel>>) data;
					List<PoiModel> poiList = poiReadByCategoryData.getDataObject();
					Iterator<PoiModel> iterator = poiList.iterator();
					while (iterator.hasNext()) {
						PoiModel poi = iterator.next();
						mPoiList.add(poi);
					}
				}

				// calculate distances and sort
				calculatePoiDistances();
				sortPoiByDistance();

				// show content
				showLazyLoadingProgress(false);
				if (mPoiList != null && !mPoiList.isEmpty()) mStatefulLayout.showContent();
				else mStatefulLayout.showEmpty();

				// finish query
				mDatabaseCallManager.finishTask(task);


//				Log.i("beaconbeacon", 1 + "@");
//				beaconMinorValue = getArguments().getInt("BeaconMinorValue", 0);
//				Log.i("beaconbeacon", beaconMinorValue + "@@");
//				if(beaconMinorValue == 16522)
//				{
//					Log.i("beaconbeacon", 2 + "@");
//					// position
//					//int poiPosition = mAdapter.getPoiPosition(1);
//
//					// start activity
////			PoiModel poi = mPoiList.get(poiPosition);
//					PoiModel poi = mPoiList.get(1);
//					startPoiDetailActivity(getView(), poi.getId());
//					Log.i("beaconbeacon", 3 + "@");
//				}
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
				else if(task.getQuery().getClass().equals(PoiReadFavoritesQuery.class))
				{
					Logcat.d("PoiReadFavoritesQuery / exception " + exception.getClass().getSimpleName() + " / " + exception.getMessage());
				}
				else if(task.getQuery().getClass().equals(PoiSearchQuery.class))
				{
					Logcat.d("PoiSearchQuery / exception " + exception.getClass().getSimpleName() + " / " + exception.getMessage());
				}
				else if(task.getQuery().getClass().equals(PoiReadByCategoryQuery.class))
				{
					Logcat.d("PoiReadByCategoryQuery / exception " + exception.getClass().getSimpleName() + " / " + exception.getMessage());
				}

				// hide progress
				showLazyLoadingProgress(false);
				if(mPoiList!=null && !mPoiList.isEmpty()) mStatefulLayout.showContent();
				else mStatefulLayout.showEmpty();

				// handle fail
				handleFail();

				// finish query
				mDatabaseCallManager.finishTask(task);
			}
		});
	}


	@Override
	public void onGeolocationRespond(Geolocation geolocation, final Location location)
	{
		runTaskCallback(new Runnable()
		{
			public void run()
			{
				if(mRootView == null) return; // view was destroyed

				Logcat.d("onGeolocationRespond() = " + location.getProvider() + " / " + location.getLatitude() + " / " + location.getLongitude() + " / " + new Date(location.getTime()).toString());
				mLocation = location;

				// calculate distances and sort
				calculatePoiDistances();
				sortPoiByDistance();
				if(mAdapter!=null && mLocation!=null && mPoiList!=null && !mPoiList.isEmpty()) mAdapter.notifyDataSetChanged();
			}
		});
	}


	@Override
	public void onGeolocationFail(Geolocation geolocation)
	{
		runTaskCallback(new Runnable()
		{
			public void run()
			{
				if(mRootView == null) return; // view was destroyed

				Logcat.d("onGeolocationFail()");
			}
		});
	}


	private void handleFail()
	{
		Toast.makeText(getActivity(), R.string.global_database_fail_toast, Toast.LENGTH_LONG).show();
	}


	private void handleArguments(Bundle arguments)
	{
		mCategoryId = arguments.getLong(ARGUMENT_CATEGORY_ID, CATEGORY_ID_ALL);
		mSearchQuery = arguments.getString(ARGUMENT_SEARCH_QUERY, "");
	}


	private void loadData()
	{
		if(!mDatabaseCallManager.hasRunningTask(PoiReadAllQuery.class) &&
				!mDatabaseCallManager.hasRunningTask(PoiReadFavoritesQuery.class) &&
				!mDatabaseCallManager.hasRunningTask(PoiSearchQuery.class) &&
				!mDatabaseCallManager.hasRunningTask(PoiReadByCategoryQuery.class))
		{
			// show progress
			mStatefulLayout.showProgress();

			// run async task
			Query query;
			if(mCategoryId==CATEGORY_ID_ALL)
			{
				query = new PoiReadAllQuery(0, LAZY_LOADING_TAKE);
			}
			else if(mCategoryId==CATEGORY_ID_FAVORITES)
			{
				query = new PoiReadFavoritesQuery(0, LAZY_LOADING_TAKE);
			}
			else if(mCategoryId==CATEGORY_ID_SEARCH)
			{
				query = new PoiSearchQuery(mSearchQuery, 0, LAZY_LOADING_TAKE);
			}
			else
			{
				query = new PoiReadByCategoryQuery(mCategoryId, 0, LAZY_LOADING_TAKE);
			}
			mDatabaseCallManager.executeTask(query, this);
		}



	}


	private void lazyLoadData()
	{
		// show lazy loading progress
		showLazyLoadingProgress(true);

		// run async task
		Query query;
		if(mCategoryId==CATEGORY_ID_ALL)
		{
			query = new PoiReadAllQuery(mPoiList.size(), LAZY_LOADING_TAKE);
		}
		else if(mCategoryId==CATEGORY_ID_FAVORITES)
		{
			query = new PoiReadFavoritesQuery(mPoiList.size(), LAZY_LOADING_TAKE);
		}
		else if(mCategoryId==CATEGORY_ID_SEARCH)
		{
			query = new PoiSearchQuery(mSearchQuery, mPoiList.size(), LAZY_LOADING_TAKE);
		}
		else
		{
			query = new PoiReadByCategoryQuery(mCategoryId, mPoiList.size(), LAZY_LOADING_TAKE);
		}
		mDatabaseCallManager.executeTask(query, this);
	}


	private void showLazyLoadingProgress(boolean visible)
	{
		if(visible)
		{
			mLazyLoading = true;

			// show footer
			if(mFooterList.size()<=0)
			{
				mFooterList.add(new Object());
				mAdapter.notifyItemInserted(mAdapter.getRecyclerPositionByFooter(0));
			}
		}
		else
		{
			// hide footer
			if(mFooterList.size()>0)
			{
				mFooterList.remove(0);
				mAdapter.notifyItemRemoved(mAdapter.getRecyclerPositionByFooter(0));
			}

			mLazyLoading = false;
		}
	}


	private void showToolbar(boolean visible)
	{
		final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		if(visible)
		{
			toolbar.animate()
					.translationY(0)
					.setDuration(200)
					.setInterpolator(new AccelerateDecelerateInterpolator())
					.setListener(new Animator.AnimatorListener()
					{
						@Override
						public void onAnimationStart(Animator animator)
						{
							toolbar.setVisibility(View.VISIBLE);
							toolbar.setEnabled(false);
						}


						@Override
						public void onAnimationEnd(Animator animator)
						{
							toolbar.setEnabled(true);
						}


						@Override
						public void onAnimationCancel(Animator animator)
						{
						}


						@Override
						public void onAnimationRepeat(Animator animator)
						{
						}
					});
		}
		else
		{
			toolbar.animate()
					.translationY(-toolbar.getBottom())
					.setDuration(200)
					.setInterpolator(new AccelerateDecelerateInterpolator())
					.setListener(new Animator.AnimatorListener()
					{
						@Override
						public void onAnimationStart(Animator animator)
						{
							toolbar.setEnabled(false);
						}


						@Override
						public void onAnimationEnd(Animator animator)
						{
							toolbar.setVisibility(View.GONE);
							toolbar.setEnabled(true);
						}


						@Override
						public void onAnimationCancel(Animator animator)
						{
						}


						@Override
						public void onAnimationRepeat(Animator animator)
						{
						}
					});
		}
	}


	private void showFloatingActionButton(boolean visible)
	{
		final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

		if(visible)
		{
			fab.show();
		}
		else
		{
			fab.hide();
		}
	}

	private void showFloatingActionButton2(boolean visible)
	{
		final FloatingActionButton fab1 = (FloatingActionButton) getActivity().findViewById(R.id.fab_findPath);

		if(visible)
		{
			fab1.show();
		}
		else
		{
			fab1.hide();
		}
	}


	private void bindData()
	{
		// reference
		final RecyclerView recyclerView = getRecyclerView();
		final FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
		//final FloatingActionButton floatingActionButton1 = (FloatingActionButton) getActivity().findViewById(R.id.fab_findPath);

		// content
		if(recyclerView.getAdapter()==null)
		{
			// create adapter
			mAdapter = new PoiListAdapter(mPoiList, mFooterList, this, getGridSpanCount());
		}
		else
		{
			// refill adapter
			mAdapter.refill(mPoiList, mFooterList, this, getGridSpanCount());
		}

		// set fixed size
		recyclerView.setHasFixedSize(false);

		// add decoration
		RecyclerView.ItemDecoration itemDecoration = new GridSpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.fragment_poi_list_recycler_item_padding));
		recyclerView.addItemDecoration(itemDecoration);

		// set animator
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		// set adapter
		recyclerView.setAdapter(mAdapter);

		// lazy loading
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			private static final int THRESHOLD = 100;

			private int mCounter = 0;
			private Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);


			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);

				// reset counter
				if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
					mCounter = 0;
				}

				// disable item animation in adapter
				if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
					mAdapter.setAnimationEnabled(false);
				}
			}


			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
				int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
				int visibleItemCount = layoutManager.getChildCount();
				int totalItemCount = layoutManager.getItemCount();
				int lastVisibleItem = firstVisibleItem + visibleItemCount;

				// lazy loading
				if (totalItemCount - lastVisibleItem <= LAZY_LOADING_OFFSET && mPoiList.size() % LAZY_LOADING_TAKE == 0 && !mPoiList.isEmpty()) {
					if (!mLazyLoading) lazyLoadData();
				}

				// toolbar and FAB animation
				mCounter += dy;
				if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING || recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
					// scroll down
					if (mCounter > THRESHOLD && firstVisibleItem > 0) {
						// hide toolbar
						if (mToolbar.getVisibility() == View.VISIBLE && mToolbar.isEnabled()) {
							showToolbar(false);
						}

						// hide FAB
						showFloatingActionButton(false);
						showFloatingActionButton2(false);

						mCounter = 0;
					}

					// scroll up
					else if (mCounter < -THRESHOLD || firstVisibleItem == 0) {
						// show toolbar
						if (mToolbar.getVisibility() == View.GONE && mToolbar.isEnabled()) {
							showToolbar(true);
						}

						// show FAB
						showFloatingActionButton(true);
						showFloatingActionButton2(false);

						mCounter = 0;
					}
				}
			}
		});

		// floating action button //search fab
		floatingActionButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new SearchActionModeCallback());
			}
		});

		//find path fab
//		floatingActionButton1.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				if(mCategoryId==CATEGORY_ID_FAVORITES)
//				{
//					Intent intent = new Intent(getActivity(), DragAndDropTravelActivity.class);
//
//					ArrayList<Spot> spotArrayList = new ArrayList<>();
//					for(int i=0 ; i<mPoiList.size() ; i++)
//					{
//						spotArrayList.add(new Spot(mPoiList.get(i).getName(),
//								mPoiList.get(i).getLatitude(),
//								mPoiList.get(i).getLongitude(),
//								mPoiList.get(i).getImage()));
//					}
//
//					AdjMatrix adjMatrix = new AdjMatrix(spotArrayList);
//					ArrayList<Spot> sortedList = adjMatrix.makeAdjMatrix();
//
//					ArrayList<Location> locationArrayList = new ArrayList<>();
//					ArrayList<String> pictureArrayList = new ArrayList<>();
//					for(int i=0 ; i<sortedList.size() ; i++)
//					{
//						Location temp = new Location(sortedList.get(i).getProvider());
//						temp.setLatitude(sortedList.get(i).getLatitude());
//						temp.setLongitude(sortedList.get(i).getLongitude());
//
//						locationArrayList.add(temp);
//						pictureArrayList.add(sortedList.get(i).getPicture());
//					}
//
//					//Intent intent = new Intent(FindPathActivity.this, testActivity.class);
//					intent.putExtra("locationArrayList", locationArrayList);
//					intent.putExtra("pictureArrayList", pictureArrayList);
//					startActivity(intent);
//				}
//				else
//				{
//					Intent intent = new Intent(getActivity(), FindPathActivity.class);
//					startActivity(intent);
//				}
//				//mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new SearchActionModeCallback());
//			}
//		});

	}



	private RecyclerView getRecyclerView()
	{
		return mRootView!=null ? (RecyclerView) mRootView.findViewById(R.id.fragment_poi_list_recycler) : null;
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
				Logcat.d("" + (state == null ? "null" : state.toString()));

				// bind data
				if(state == StatefulLayout.State.CONTENT)
				{
					if(mLazyLoading && mAdapter != null)
					{
						mAdapter.notifyDataSetChanged();
					}
					else
					{
						if(mPoiList!=null && !mPoiList.isEmpty()) bindData();
					}
				}

				// floating action button
				showFloatingActionButton(state == StatefulLayout.State.CONTENT);
			}
		});

		// restore state
		mStatefulLayout.restoreInstanceState(savedInstanceState);
	}


	private void setupRecyclerView()
	{
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getGridSpanCount());
		gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
		RecyclerView recyclerView = getRecyclerView();
		recyclerView.setLayoutManager(gridLayoutManager);
	}


	private int getGridSpanCount()
	{
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		float screenWidth  = displayMetrics.widthPixels;
		float cellWidth = getResources().getDimension(R.dimen.fragment_poi_list_recycler_item_size);
		return Math.round(screenWidth / cellWidth);
	}


	private void calculatePoiDistances()
	{
		if(mLocation!=null && mPoiList!=null && !mPoiList.isEmpty())
		{
			for(int i=0; i<mPoiList.size(); i++)
			{
				PoiModel poi = mPoiList.get(i);
				LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
				LatLng poiLocation = new LatLng(poi.getLatitude(), poi.getLongitude());
				int distance = LocationUtility.getDistance(myLocation, poiLocation);
				poi.setDistance(distance);
			}
		}
	}


	private void sortPoiByDistance()
	{
		if(mLocation!=null && mPoiList!=null && !mPoiList.isEmpty())
		{
			Collections.sort(mPoiList);
		}
	}


	private void setupTimer()
	{
		mTimerHandler = new Handler();
		mTimerRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				Logcat.d("timer");

				// check access location permission
				if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
				{
					// start geolocation
					mGeolocation = null;
					mGeolocation = new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), PoiListFragment.this);
				}

				mTimerHandler.postDelayed(this, TIMER_DELAY);
			}
		};
	}


	private void startTimer()
	{
		mTimerHandler.postDelayed(mTimerRunnable, 0);
	}


	private void stopTimer()
	{
		mTimerHandler.removeCallbacks(mTimerRunnable);
	}


	private void showAboutDialog()
	{
		// create and show the dialog
		DialogFragment newFragment = AboutDialogFragment.newInstance();
		newFragment.setTargetFragment(this, 0);
		newFragment.show(getFragmentManager(), DIALOG_ABOUT);
	}


	private void startPoiDetailActivity(View view, long poiId)
	{
		Intent intent = PoiDetailActivity.newIntent(getActivity(), poiId);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight());
			getActivity().startActivity(intent, options.toBundle());
		}
		else
		{
			startActivity(intent);
		}
	}


	private void startMapActivity()
	{
		Intent intent = MapActivity.newIntent(getActivity());
		startActivity(intent);
	}


	private void startWebActivity(String url)
	{
		try
		{
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
		}
		catch(android.content.ActivityNotFoundException e)
		{
			// can't start activity
		}
	}


	private class SearchActionModeCallback implements ActionMode.Callback
	{
		private SearchView mSearchView;
		private SearchSuggestionAdapter mSearchSuggestionAdapter;


		@Override
		public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
		{
			// search view
			mSearchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
			setupSearchView(mSearchView);

			// search menu item
			MenuItem searchMenuItem = menu.add(Menu.NONE, Menu.NONE, 1, getString(R.string.menu_search));
			searchMenuItem.setIcon(R.drawable.ic_menu_search);
			MenuItemCompat.setActionView(searchMenuItem, mSearchView);
			MenuItemCompat.setShowAsAction(searchMenuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

			return true;
		}


		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
		{
			showFloatingActionButton(false);
			showFloatingActionButton2(false);
			return true;
		}


		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
		{
			return false;
		}


		@Override
		public void onDestroyActionMode(ActionMode actionMode)
		{
			showFloatingActionButton(true);
			showFloatingActionButton2(false);
		}


		private void setupSearchView(SearchView searchView)
		{
			// expand action view
			searchView.setIconifiedByDefault(true);
			searchView.setIconified(false);
			searchView.onActionViewExpanded();

			// search hint
			searchView.setQueryHint(getString(R.string.menu_search_hint));

			// text color
			AutoCompleteTextView searchText = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
			searchText.setTextColor(ContextCompat.getColor(getActivity(), R.color.global_text_primary_inverse));
			searchText.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.global_text_secondary_inverse));

			// suggestion listeners
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
			{
				@Override
				public boolean onQueryTextSubmit(String query)
				{
					// listener
					mSearchListener.onSearch(query);

					// save query for suggestion
					SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), PoiSearchRecentSuggestionsProvider.AUTHORITY, PoiSearchRecentSuggestionsProvider.MODE);
					suggestions.saveRecentQuery(query, null);

					// close action mode
					mActionMode.finish();

					return true;
				}

				@Override
				public boolean onQueryTextChange(String query)
				{
					if(query.length()>2)
					{
						updateSearchSuggestion(query);
					}
					return true;
				}
			});
			searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener()
			{
				@Override
				public boolean onSuggestionSelect(int position)
				{
					return false;
				}

				@Override
				public boolean onSuggestionClick(int position)
				{
					// get query
					Cursor cursor = (Cursor) mSearchSuggestionAdapter.getItem(position);
					String title = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));

					// listener
					mSearchListener.onSearch(title);

					// close action mode
					mActionMode.finish();

					return true;
				}
			});
		}


		private void updateSearchSuggestion(String query)
		{
			// cursor
			ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
			String contentUri = "content://" + PoiSearchRecentSuggestionsProvider.AUTHORITY + '/' + SearchManager.SUGGEST_URI_PATH_QUERY;
			Uri uri = Uri.parse(contentUri);
			Cursor cursor = contentResolver.query(uri, null, null, new String[] { query }, null);

			// searchview content
			if(mSearchSuggestionAdapter==null)
			{
				// create adapter
				mSearchSuggestionAdapter = new SearchSuggestionAdapter(getActivity(), cursor);

				// set adapter
				mSearchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
			}
			else
			{
				// refill adapter
				mSearchSuggestionAdapter.refill(getActivity(), cursor);

				// set adapter
				mSearchView.setSuggestionsAdapter(mSearchSuggestionAdapter);
			}
		}
	}

	private class phpDown extends AsyncTask<String, Integer,String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Log.i("wait for task","doInBackground");
			StringBuilder jsonHtml = new StringBuilder();
			try{
				// 연결 url 설정
				URL url = new URL(urls[0]);
				// 커넥션 객체 생성
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				// 연결되었으면.
				if(conn != null){
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					// 연결되었음 코드가 리턴되면.
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
					{
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						for(;;){
							// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
							String line = br.readLine();
							if(line == null) break;
							// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
							jsonHtml.append(line + "\n");
						}
						br.close();
					}
					conn.disconnect();
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
			Log.i("wait for task","doInBackground finish");
			return jsonHtml.toString();
		}

		@Override
		protected void onPostExecute(String str)
		{
			Log.i("wait for task","onPostExecute");
			String cityName;

			spotArrayList = new ArrayList<>();
			Spot spot;

			try{
				JSONObject root = new JSONObject(str);
				JSONArray ja = root.getJSONArray("results"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"

				for(int i=0;i<ja.length();i++){
					JSONObject jo = ja.getJSONObject(i);
					cityName = jo.getString("city");
					spot = new Spot(cityName);
					spot.setLatitude(Double.parseDouble(jo.getString("latitude")));
					spot.setLongitude(Double.parseDouble(jo.getString("longitude")));
					spot.setPicture(jo.getString("picture"));

					//if(cityList.contains(spot.getProvider()))
					//{
					spotArrayList.add(spot);
					//}


					//cityNameList.add(cityName);
				}

			}catch (JSONException e){
				e.printStackTrace();
			}

			//listview.setAdapter(adapter);
			Log.i("wait for task","onPostExecute finish");
		}
	}//end of private class

	private ArrayList<Spot> doTSP(ArrayList<Spot> checkedSpotList)
	{
		adjMatrix = new AdjMatrix(checkedSpotList);
		return adjMatrix.makeAdjMatrix();

	}
}
