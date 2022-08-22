package com.vietlh.wethoong;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.vietlh.wethoong.entities.interfaces.CallbackActivity;
import com.vietlh.wethoong.utils.GeneralSettings;

import com.google.gson.Gson;

import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity implements CallbackActivity {
    private int maxSplashTime = 5000;
    private int totalSplashTime = 0;
    private int interval = 500;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseRemoteConfigSettings configSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        System.out.println("===== SplashScreenActivity: onCreate");

        // Obtain the FirebaseAnalytics instance.
        System.out.println("----- SplashScreenActivity: Initializing Firebase remote config");
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        if (GeneralSettings.isDevMode) {
            System.out.println("----- SplashScreenActivity: building configSettings with dev mode");
            configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(0)
                    .build();
        } else {
            System.out.println("----- SplashScreenActivity: building configSettings with live mode");
            configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();
        }
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
//                            Log.d(TAG, "Config params updated: " + updated);
                            System.out.println("----- SplashScreenActivity: Fetch and activate remote configs succeeded");
                            Toast.makeText(SplashScreenActivity.this, "Settings Fetch and activate succeeded",
                                    Toast.LENGTH_SHORT).show();
                            System.out.println("----- SplashScreenActivity: Update settings from remote configs succeeded");
                        } else {
                            Toast.makeText(SplashScreenActivity.this, "Settings Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        fetchRemoteConfig();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("===== SplashScreenActivity: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("===== SplashScreenActivity: onResume");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (!GeneralSettings.isRemoteConfigFetched && (totalSplashTime < maxSplashTime)) {
                    try {
                        Thread.sleep(interval);
                        totalSplashTime += interval;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });

        System.out.println("===== SplashScreenActivity: onResume ---- Opening Homescreen");

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("===== SplashScreenActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("===== SplashScreenActivity: onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("===== SplashScreenActivity: onRestart");
        finishAffinity();
        System.exit(0);
//        System.out.println("===== SplashScreenActivity: onRestart ---- Opening Homescreen");
//        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//        startActivity(i);
    }

    @Override
    public void triggerCallbackAction(String actionCase) {

    }

    private void fetchRemoteConfig() {
        //update settings from remote config
        System.out.println("----- SplashScreenActivity: update settings from remote config");
        System.out.println("----- SplashScreenActivity: requiredDBVersion: " + (int) mFirebaseRemoteConfig.getLong("requiredDBVersion"));
        GeneralSettings.requiredDBVersion = (int) mFirebaseRemoteConfig.getLong("requiredDBVersion");
        System.out.println("----- SplashScreenActivity: MINIMUM_APP_VERSION_REQUIRED: " + mFirebaseRemoteConfig.getString("minimumAppVersion"));
        GeneralSettings.MINIMUM_APP_VERSION_REQUIRED = mFirebaseRemoteConfig.getString("minimumAppVersion");
        System.out.println("----- SplashScreenActivity: MINIMUM_ADS_INTERVAL: " + (int) mFirebaseRemoteConfig.getLong("minimumAdsInterval"));
        GeneralSettings.MINIMUM_ADS_INTERVAL = (int) mFirebaseRemoteConfig.getLong("minimumAdsInterval");
        System.out.println("----- SplashScreenActivity: ENABLE_INTERSTITIAL_ADS: " + mFirebaseRemoteConfig.getBoolean("enableInterstitialAds"));
        GeneralSettings.ENABLE_INTERSTITIAL_ADS = mFirebaseRemoteConfig.getBoolean("enableInterstitialAds");
        System.out.println("----- SplashScreenActivity: ENABLE_INAPP_NOTIF: " + mFirebaseRemoteConfig.getBoolean("enableInappNotif"));
        GeneralSettings.ENABLE_INAPP_NOTIF = mFirebaseRemoteConfig.getBoolean("enableInappNotif");
        System.out.println("----- SplashScreenActivity: ENABLE_BANNER_ADS: " + mFirebaseRemoteConfig.getBoolean("enableBannerAds"));
        GeneralSettings.ENABLE_BANNER_ADS = mFirebaseRemoteConfig.getBoolean("enableBannerAds");
        System.out.println("----- SplashScreenActivity: isDevMode: " + mFirebaseRemoteConfig.getBoolean("developementMode"));
        GeneralSettings.isDevMode = mFirebaseRemoteConfig.getBoolean("developementMode");
        System.out.println("----- SplashScreenActivity: setDefaultActiveQC41Id: " + mFirebaseRemoteConfig.getLong("defaultActiveQC41Id"));
        GeneralSettings.setDefaultActiveQC41Id((int) mFirebaseRemoteConfig.getLong("defaultActiveQC41Id"));
        System.out.println("----- SplashScreenActivity: setDefaultActiveNDXPId: " + mFirebaseRemoteConfig.getLong("defaultActiveNDXPId"));
        GeneralSettings.setDefaultActiveNDXPId((int) mFirebaseRemoteConfig.getLong("defaultActiveNDXPId"));
        System.out.println("----- SplashScreenActivity: isAdsOptout: " + mFirebaseRemoteConfig.getBoolean("adsOptout"));
        GeneralSettings.isAdsOptout = mFirebaseRemoteConfig.getBoolean("adsOptout");
        GeneralSettings.setTamgiuPhuongtienDieukhoanID(new Gson().fromJson(mFirebaseRemoteConfig.getString("tamgiuPhuongtienDieukhoanID"), new HashMap<Integer, String>().getClass()));
        GeneralSettings.isRemoteConfigFetched = true; //indicate that remote configs are set
    }
}
