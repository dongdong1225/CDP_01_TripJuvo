package com.knucapstone.tripjuvo.gcmService;

/**
 * Created by leedonghee on 16. 5. 27..
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.activity.SplashScreensActivity;


public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String Text = data.getString("Notice");
        String Title = data.getString("Title");
        Log.i("onMessageReceived",data.toString());

        //큰 아이콘
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_noti);
        //알림 사운드
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //알림 클릭시 이동할 인텐트
        //Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://developers.google.com/cloud-messaging/"));
        Intent intent = new Intent(this, SplashScreensActivity.class);
        //노티피케이션을 생성할때 매개변수는 PendingIntent이므로 Intent를 PendingIntent로 만들어주어야함.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //노티피케이션 빌더 : 위에서 생성한 이미지나 텍스트, 사운드등을 설정해줍니다.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setLargeIcon(bitmap)
                .setContentTitle(Title)
                .setContentText(Text)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //노티피케이션을 생성합니다.
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
