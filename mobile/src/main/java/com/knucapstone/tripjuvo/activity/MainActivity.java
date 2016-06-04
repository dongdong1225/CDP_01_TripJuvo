package com.knucapstone.tripjuvo.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.CityGuideConfig;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.adapter.DrawerAdapter;
import com.knucapstone.tripjuvo.beacon.RecoBackgroundMonitoringService;
import com.knucapstone.tripjuvo.beacon.RecoBackgroundRangingService;
import com.knucapstone.tripjuvo.database.dao.CategoryDAO;
import com.knucapstone.tripjuvo.database.model.CategoryModel;
import com.knucapstone.tripjuvo.fragment.PoiListFragment;
import com.knucapstone.tripjuvo.gcmService.RegistrationIntentService;
import com.knucapstone.tripjuvo.listener.OnSearchListener;
import com.knucapstone.tripjuvo.utility.ResourcesUtility;
import com.knucapstone.tripjuvo.view.DrawerDividerItemDecoration;
import com.knucapstone.tripjuvo.view.ScrimInsetsFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.CategoryViewHolder.OnItemClickListener, OnSearchListener {
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ScrimInsetsFrameLayout mDrawerScrimInsetsFrameLayout;
	private DrawerAdapter mDrawerAdapter;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private List<CategoryModel> mCategoryList;
	private static final String DATABASE_NAME = CityGuideConfig.DATABASE_NAME;
	private static final String DATABASE_PATH = "/data/data/" + CityGuideApplication.getContext().getPackageName() + "/databases/";
	private static final int DATABASE_VERSION = CityGuideConfig.DATABASE_VERSION;

	private phpDown task; //웹서버의 php를 실행하기 위한 인스턴스
	private boolean loadFinish;

	//비콘관련 변수들
	public static final String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E"; //바꾸지 마셈
	public static final boolean SCAN_RECO_ONLY = true; //레코만 읽을거임
	public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
	public static final boolean DISCONTINUOUS_SCAN = false;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_LOCATION = 10;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;

	private int beaconPoi = 0;

	Bundle sIState;
	public static Intent newIntent(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		setupActionBar();
//		setupRecyclerView();
//		setupDrawer(savedInstanceState);
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("COUNT", "main1");

			Intent service = new Intent(this, RegistrationIntentService.class);
			startService(service);
			reset();
		ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifi.isConnected() || mobile.isConnected()) {
			Log.i("NETWORK_STATE", "CONNECTING");
			task = new phpDown();
			task.execute("http://tripjuvo.ivyro.net/insertAllPoi.php");//도메인을 실행하게되면 php가 실행되서 데이터전달이 일어난다.
		}
			Log.i("COUNT", "main2");

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			sIState = savedInstanceState;
			SQLiteDatabase db = openOrCreateDatabase(
					"tripjuvo.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
			db.execSQL("CREATE TABLE IF NOT EXISTS logins " +
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT, checks INTEGER, account TEXT, age INTEGER, " +
					"vehicle TEXT);");

			String sql = "SELECT * from logins where checks = 1;";
			Cursor c = db.rawQuery(sql, null);

			if (c.getCount() == 0) {
				Intent intent = new Intent(this, WizardTravelActivity.class);
				startActivity(intent);
			}
			db.close();

			//사용자가 블루투스를 켜도록 요청합니다.
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			mBluetoothAdapter = mBluetoothManager.getAdapter();

			if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
				Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
			}

			//MOS 이상 권한 요청
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is not granted.");
					this.requestLocationPermission();
				} else {
					Log.i("MainActivity", "The location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is already granted.");
				}
			}

			Log.i("MainActivity", "startMonitoringService");
			Intent monitoringIntent = new Intent(this, RecoBackgroundMonitoringService.class);
			startService(monitoringIntent);

			Log.i("MainActivity", "startRangingService");
			Intent rangingIntent = new Intent(this, RecoBackgroundRangingService.class);
			startService(rangingIntent);

			setupActionBar();
			setupRecyclerView();
			setupDrawer(savedInstanceState);

			saveBeaconMinorValue();
			//showGroupCity();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch(requestCode) {
			case REQUEST_LOCATION : {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					Toast.makeText(MainActivity.this, R.string.location_permission_granted, Toast.LENGTH_LONG).show();
					//Snackbar.make(mLayout, R.string.location_permission_granted, Snackbar.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(MainActivity.this, R.string.location_permission_not_granted, Toast.LENGTH_LONG).show();
					//Snackbar.make(mLayout, R.string.location_permission_not_granted, Snackbar.LENGTH_LONG).show();
				}
			}
			default :
				break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		// analytics
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}


	@Override
	public void onResume() {
		super.onResume();
	}


	@Override
	public void onPause() {
		super.onPause();
	}


	@Override
	public void onStop() {
		super.onStop();

		// analytics
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// open or close the drawer if home button is pressed
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// action bar menu behavior
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfiguration) {
		super.onConfigurationChanged(newConfiguration);
		mDrawerToggle.onConfigurationChanged(newConfiguration);
	}


	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		} else {
			super.onBackPressed();
		}
	}


	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}


	@Override
	public void onItemClick(View view, int position, long id, int viewType) {
		// position
		int categoryPosition = mDrawerAdapter.getCategoryPosition(position);
		selectDrawerItem(categoryPosition);
	}


	@Override
	public void onSearch(String query) {
		Fragment fragment = PoiListFragment.newInstance(query);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container_drawer_content, fragment).commitAllowingStateLoss();

		mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(0));
		setTitle(getString(R.string.title_search) + ": " + query);
	}

	private void requestLocationPermission() {
		if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
			return;
		}

		Snackbar.make(getRecyclerView(), R.string.location_permission_rationale, Snackbar.LENGTH_INDEFINITE)
				.setAction(R.string.ok, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
					}
				})
				.show();
	}

	private boolean isBackgroundMonitoringServiceRunning(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for(ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
			if(RecoBackgroundMonitoringService.class.getName().equals(runningService.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isBackgroundRangingServiceRunning(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for(ActivityManager.RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
			if(RecoBackgroundRangingService.class.getName().equals(runningService.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void setupActionBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayUseLogoEnabled(false);
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);
	}


	private void setupRecyclerView() {
		// reference
		RecyclerView recyclerView = getRecyclerView();

		// set layout manager
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(linearLayoutManager);

		// load categories from database
		loadCategoryList();

		// set adapter
		if (recyclerView.getAdapter() == null) {
			// create adapter
			mDrawerAdapter = new DrawerAdapter(mCategoryList, this);
		} else {
			// refill adapter
			mDrawerAdapter.refill(mCategoryList, this);
		}
		recyclerView.setAdapter(mDrawerAdapter);

		// add decoration
		List<Integer> dividerPositions = new ArrayList<>();
		dividerPositions.add(3);
		RecyclerView.ItemDecoration itemDecoration = new DrawerDividerItemDecoration(
				this,
				null,
				dividerPositions,
				getResources().getDimensionPixelSize(R.dimen.global_spacing_xxs));
		recyclerView.addItemDecoration(itemDecoration);
	}


	private void setupDrawer(Bundle savedInstanceState) {
		mTitle = getTitle();
		mDrawerTitle = getTitle();

		// reference
		mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
		mDrawerScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.activity_main_drawer_scrim_layout);

		// set drawer
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setStatusBarBackgroundColor(ResourcesUtility.getValueOfAttribute(this, R.attr.colorPrimaryDark));
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// show initial fragment
		if (savedInstanceState == null) {
			selectDrawerItem(0);
		}
	}


	private void selectDrawerItem(int position) {

		//Log.i("@@@", Integer.toString(position));
		if(position == 2) // findPathActivity 호출
		{
			Intent intent = new Intent(this, FindPathActivity.class);
			startActivity(intent);
			return;
		}
		Fragment fragment = PoiListFragment.newInstance(mCategoryList.get(position).getId());
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container_drawer_content, fragment).commitAllowingStateLoss();

		mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(position));
		setTitle(mCategoryList.get(position).getName());
		mDrawerLayout.closeDrawer(mDrawerScrimInsetsFrameLayout);
	}


	private void loadCategoryList() {
		try {
			mCategoryList = CategoryDAO.readAll(-1L, -1L);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		CategoryModel all = new CategoryModel();
		all.setId(PoiListFragment.CATEGORY_ID_ALL);
		all.setName(getResources().getString(R.string.drawer_category_all));
		all.setImage("drawable://" + R.drawable.ic_category_all);

		CategoryModel favorites = new CategoryModel();
		favorites.setId(PoiListFragment.CATEGORY_ID_FAVORITES);
		favorites.setName(getResources().getString(R.string.drawer_category_favorites));
		favorites.setImage("drawable://" + R.drawable.ic_category_favorites);

		CategoryModel findPath = new CategoryModel();
		findPath.setId(PoiListFragment.CATEGORY_ID_findPath);
		findPath.setName(getResources().getString(R.string.drawer_category_findPath));
		findPath.setImage("drawable://" + R.drawable.ic_category_findpath);


		mCategoryList.add(0, all);
		mCategoryList.add(1, favorites);
		mCategoryList.add(2, findPath);
	}


	private RecyclerView getRecyclerView() {
		return (RecyclerView) findViewById(R.id.activity_main_drawer_recycler);
	}

	//	public void reset() {
//		try {
//			String sql = "DELETE FROM pois;";
//			SQLiteDatabase db = openOrCreateDatabase(
//					"cityguide.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
//			db.execSQL("CREATE TABLE IF NOT EXISTS pois " +
//					"(id INTEGER, category_id INTEGER, name TEXT PRIMARY KEY, intro INTEGER, " +
//					"description TEXT, image text, link text, latitude DOUBLE, longitude DOUBLE, " +
//					"address text, phone text, email text, favorite SMALLINT, city TEXT);");
//			db.execSQL(sql);
//
//			Log.i("RESET", "SER");
//			Semaphore sem = new Semaphore(1);
//			InsertAllPois th1 = new InsertAllPois(sem);
//			th1.start();
//
//			Cursor c = db.rawQuery("SELECT * FROM favorite", null);
//			int cnt = c.getCount();
//			c.moveToFirst();
//			for (int i = 0; i < cnt; i++) {
//				db.execSQL("UPDATE pois SET favorite = 1 where id = " + c.getInt(0) + ";");
//				c.moveToNext();
//			}
//			db.close();
////			final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
////					R.style.AppTheme_Dark_Dialog);
////			progressDialog.setIndeterminate(true);
////			progressDialog.setMessage("로딩중...");
////			progressDialog.show();
////			new android.os.Handler().postDelayed(
////					new Runnable() {
////						public void run() {
////							// On complete call either onLoginSuccess or onLoginFailed
////							// onLoginFailed();
////							progressDialog.dismiss();
////						}
////					}, 3000);
//
//		}
//		catch (Exception e)
//		{
//			Log.i("RESETERROR",e.getMessage());
//		}
//	}
	public void reset() {
		try {
			Log.i("COUNT", "RESET1");
			String sql = "DELETE FROM pois;";
			SQLiteDatabase db = openOrCreateDatabase(
					"cityguide.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
			db.execSQL("CREATE TABLE IF NOT EXISTS pois " +
					"(id INTEGER, category_id INTEGER, name TEXT PRIMARY KEY, intro INTEGER, " +
					"description TEXT, image text, link text, latitude DOUBLE, longitude DOUBLE, " +
					"address text, phone text, email text, favorite SMALLINT, city TEXT);");
			db.execSQL(sql);
			Log.i("COUNT", "RESET2");

			Cursor c = db.rawQuery("SELECT * FROM favorite", null);
			int cnt = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < cnt; i++) {
				db.execSQL("UPDATE pois SET favorite = 1 where id = " + c.getInt(0) + ";");
				c.moveToNext();
			}
			db.close();
//			final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
//					R.style.AppTheme_Dark_Dialog);
//			progressDialog.setIndeterminate(true);
//			progressDialog.setMessage("로딩중...");
//			progressDialog.show();
//			new android.os.Handler().postDelayed(
//					new Runnable() {
//						public void run() {
//							// On complete call either onLoginSuccess or onLoginFailed
//							// onLoginFailed();
//							progressDialog.dismiss();
//						}
//					}, 3000);

		} catch (Exception e) {
			Log.i("RESETERROR", e.getMessage());
		}
	}

	private class phpDown extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... urls) {
			StringBuilder jsonHtml = new StringBuilder();
			try {
				// 연결 url 설정
				URL url = new URL(urls[0]);
				// 커넥션 객체 생성
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				// 연결되었으면.
				if (conn != null) {
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					// 연결되었음 코드가 리턴되면.
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						for (; ; ) {
							// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
							String line = br.readLine();
							if (line == null) break;
							// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
							jsonHtml.append(line + "\n");
						}
						br.close();
					}
					conn.disconnect();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return jsonHtml.toString();
		}

		protected void onPostExecute(String str) {
			try {
				JSONObject root = new JSONObject(str);
				JSONArray ja = root.getJSONArray("results"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"
				SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
				int cnt = ja.length();
				String update_sql = "UPDATE sqlite_sequence set seq = "+Integer.toString(cnt)+" where name = 'pois';";
				db.execSQL(update_sql);
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					String id = jo.getString("id");
					String category_id = jo.getString("category_id");
					String name = jo.getString("name");
					String intro = jo.getString("intro");
					String description = jo.getString("description");
					String image = jo.getString("image");
					String link = jo.getString("link");
					String latitude = jo.getString("latitude");
					String longitude = jo.getString("longitude");
					String address = jo.getString("address");
					String phone = jo.getString("phone");
					String email = jo.getString("email");
					String city = jo.getString("city");

					Log.i("Latitude", latitude);

					String sql = "INSERT INTO pois (id, category_id, name, intro, description, image, link, " +
							"latitude, longitude, address, phone, email, favorite, city) values (" + id + "," + category_id +
							",'" + name + "','" + intro + "','" + description + "','" + image + "','" + link + "'," + latitude + "," +
							longitude + ",'" + address + "','" + phone + "','" + email + "', 0 ,'" + city + "');";
					try {
						db.execSQL(sql);
					}
					catch (Exception e)
					{
						Log.i("DB","execSQL ERROR");
					}
					try {
						Cursor c = db.rawQuery("SELECT * FROM favorite", null);
						int cnt2 = c.getCount();
						c.moveToFirst();
						for (int j = 0; j < cnt2; j++) {
							db.execSQL("UPDATE pois SET favorite = 1 where id = " + c.getInt(0) + ";");
							c.moveToNext();
						}
					}
					catch (Exception e){
						Log.i("DB","favorit error");
					}
				}
				Log.i("COUNT", "readE");
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			setupActionBar();
			setupRecyclerView();
			setupDrawer(sIState);
		}

	}
	public void saveBeaconMinorValue()
	{
		//비콘 인식시 실행되는 부분
		try
		{
			Log.i("beaconbeacon", 1+"");
			Intent intent = getIntent();
			if(intent.hasExtra("BeaconPoi"))
			{
				beaconPoi = intent.getIntExtra("BeaconPoi", 0);
				Log.i("beaconbeacon", beaconPoi+"");

				Intent poi_datail_intent = PoiDetailActivity.newIntent(this, beaconPoi);
				startActivity(poi_datail_intent);
			}
		}
		catch(Exception e)
		{
			Log.i("beaconbeacon", e+"");
		}
	}

	public String showGroupCity() {
		String cityName = "";
		try {
			Log.i("showGroupCity", 1 + "");
			Intent intent = getIntent();
			if (intent.hasExtra("ThisCity")) {
				cityName = intent.getStringExtra("ThisCity");
				Log.i("showGroupCity", cityName);
			}
		} catch (Exception e) {
			Log.i("beaconbeacon", e + "");
		}

		Fragment fragment = PoiListFragment.newInstance(PoiListFragment.CATEGORY_ID_ALL);
		Bundle bundle = new Bundle();
		bundle.putString("ThisCity", cityName);
		fragment.setArguments(bundle);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container_drawer_content, fragment).commitAllowingStateLoss();

		mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(0));
		setTitle(mCategoryList.get(0).getName());
		mDrawerLayout.closeDrawer(mDrawerScrimInsetsFrameLayout);


		return cityName;
	}
}