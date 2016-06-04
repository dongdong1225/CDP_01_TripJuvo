package com.knucapstone.tripjuvo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.fragment.WizardTravelFragment;

public class WizardTravelActivity extends ActionBarActivity {

	private MyPagerAdapter adapter;
	private ViewPager pager;
	private TextView title;
	private TextView text;
	private TextView navigator;
	private TextView button;
	private int currentItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard_travel);

		currentItem = 0;

		pager = (ViewPager) findViewById(R.id.activity_wizard_travel_pager);
		title = (TextView) findViewById(R.id.activity_wizard_travel_title);
		text = (TextView) findViewById(R.id.activity_wizard_travel_text);
		navigator = (TextView) findViewById(R.id.activity_wizard_travel_possition);
		button = (TextView) findViewById(R.id.activity_wizard_travel_button);

		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		pager.setCurrentItem(currentItem);

		setNavigator();
	//	title.setText("Fragment Example " + (currentItem + 1));
	//	text.setText("Text for Fragment Example " + (currentItem + 1) + " " + getString(R.string.lorem_ipsum_short));

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int position) {
				// TODO Auto-generated method stub
				setNavigator();
			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					Toast.makeText(WizardTravelActivity.this, "SIGN IN",
							Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(WizardTravelActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}

	public void setNavigator() {
		String navigation = "";
		for (int i = 0; i < adapter.getCount(); i++) {
			if (i == pager.getCurrentItem()) {
				navigation += getString(R.string.material_icon_point_full)
						+ "  ";
			} else {
				navigation += getString(R.string.material_icon_point_empty)
						+ "  ";
			}
		}
		if(pager.getCurrentItem() == 0) {
			navigator.setText(navigation);
			title.setText("관광지 정보를 쉽게 찾으세요!");
			text.setText("내 주변 관광지에 대한 \n 정보를 쉽게 찾을 수 있습니다.");
		}
		else if(pager.getCurrentItem() == 1)
		{
			navigator.setText(navigation);
			title.setText("모든 예약을 쉽게 할 수 있습니다.");
			text.setText("호텔, 버스, 비행기, 렌트카는 물론 \n투어 예약도 손쉽게 가능 합니다.");
		}
		else
		{
			navigator.setText(navigation);
			title.setText("여행 일정을 코디네이팅 받으세요!");
			text.setText("내가 가고싶은 관광지를 선택하면 여행 경로를 \n만들어 드립니다. 호텔, 교통도 추천 드립니다.");
		}
	}

	public void setCurrentSlidePosition(int position) {
		this.currentItem = position;
	}

	public int getCurrentSlidePosition() {
		return this.currentItem;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		// Disable going back to the MainActivity
		moveTaskToBack(true);
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return WizardTravelFragment.newInstance(position);
			} else if (position == 1) {
				return WizardTravelFragment.newInstance(position);
			} else {
				return WizardTravelFragment.newInstance(position);
			}
		}
	}
}