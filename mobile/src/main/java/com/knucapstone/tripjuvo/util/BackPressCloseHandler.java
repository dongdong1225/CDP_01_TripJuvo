package com.knucapstone.tripjuvo.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.activity.MainActivity;

/**
 * Created by JYM on 2016-06-05.
 */
public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();

            Intent t = new Intent(activity, MainActivity.class);
            activity.startActivity(t);

            activity.moveTaskToBack(true);
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, R.string.double_back_press_app_close, Toast.LENGTH_SHORT);
        toast.show();
    }

}
