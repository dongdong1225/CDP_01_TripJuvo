package com.knucapstone.tripjuvo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.CityGuideConfig;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.adapter.DrawerAdapter;
import com.knucapstone.tripjuvo.database.dao.CategoryDAO;
import com.knucapstone.tripjuvo.database.model.CategoryModel;
import com.knucapstone.tripjuvo.fragment.PoiListFragment;
import com.knucapstone.tripjuvo.listener.OnSearchListener;
import com.knucapstone.tripjuvo.utility.ResourcesUtility;
import com.knucapstone.tripjuvo.view.DrawerDividerItemDecoration;
import com.knucapstone.tripjuvo.view.ScrimInsetsFrameLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CityActivity extends AppCompatActivity implements DrawerAdapter.CategoryViewHolder.OnItemClickListener, OnSearchListener {
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sIState = savedInstanceState;

        setupActionBar();
        setupRecyclerView();
        setupDrawer(savedInstanceState);

        //showGroupCity();
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

        Intent intent = getIntent();
        String cityname;
        cityname = intent.getStringExtra("ThisCity");
        // show initial fragment
        if (savedInstanceState == null) {
            Fragment fragment = PoiListFragment.newInstance(PoiListFragment.SEARCH_BY_CITY,cityname);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_drawer_content, fragment).commitAllowingStateLoss();

            //mDrawerAdapter.setSelected(mDrawerAdapter.getRecyclerPositionByCategory(0));
            setTitle(mCategoryList.get(0).getName());
            //mDrawerLayout.closeDrawer(mDrawerScrimInsetsFrameLayout);
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