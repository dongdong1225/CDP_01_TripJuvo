package com.knucapstone.tripjuvo.gcmService;

/**
 * Created by leedonghee on 16. 5. 27..
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.knucapstone.tripjuvo.activity.NotiRegistration;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken("96405965131", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.e("token",token);
            NotiRegistration notiRegistration = new NotiRegistration(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
