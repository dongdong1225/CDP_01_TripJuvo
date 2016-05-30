package com.knucapstone.tripjuvo.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.font.MaterialDesignIconsTextView;
import com.knucapstone.tripjuvo.gcmService.RegistrationIntentService;
import com.knucapstone.tripjuvo.view.kbv.KenBurnsView;

public class SplashScreensActivity extends Activity {

	public static final String SPLASH_SCREEN_OPTION = "com.csform.android.uiapptemplate.SplashScreensActivity";
	public static final String SPLASH_SCREEN_OPTION_1 = "Fade in + Ken Burns";
	public static final String SPLASH_SCREEN_OPTION_2 = "Down + Ken Burns";
	public static final String SPLASH_SCREEN_OPTION_3 = "Down + fade in + Ken Burns";
	
	private KenBurnsView mKenBurns;
	private MaterialDesignIconsTextView mLogo;
	private TextView welcomeText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
		setContentView(R.layout.activity_splash_screen);


		
		mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
		mLogo = (MaterialDesignIconsTextView) findViewById(R.id.logo);
		welcomeText = (TextView) findViewById(R.id.welcome_text);
		
		String category = SPLASH_SCREEN_OPTION_1;
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(SPLASH_SCREEN_OPTION)) {
			category = extras.getString(SPLASH_SCREEN_OPTION, SPLASH_SCREEN_OPTION_1);
		}
		setAnimation(category);
		new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreensActivity.this, MainActivity.class);
				startActivity(i);
				// close this activity
				finish();
			}
		}, 3000);
	}
	
	/** Animation depends on category.
	 * */
	private void setAnimation(String category) {

			mKenBurns.setImageResource(R.drawable.splash_screen_option_one_2);
			animation1();
			animation3();
	}

	private void animation1() {
		ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
		scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleXAnimation.setDuration(1200);
		ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
		scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleYAnimation.setDuration(1200);
		ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
		alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		alphaAnimation.setDuration(1200);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
		animatorSet.setStartDelay(500);
		animatorSet.start();
	}
	
	private void animation2() {
		mLogo.setAlpha(1.0F);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
		mLogo.startAnimation(anim);
	}
	
	private void animation3() {
		ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
		alphaAnimation.setStartDelay(1700);
		alphaAnimation.setDuration(500);
		alphaAnimation.start();
	}
}
