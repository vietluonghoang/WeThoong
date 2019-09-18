package com.vietlh.wethoong.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.vietlh.wethoong.entities.TapjoyAdsListener;

import java.net.InetAddress;
import java.util.Hashtable;

/**
 * Created by vietlh on 4/29/18.
 */

public class AdsHelper {
    public void addBannerViewtoView(AdView banner, ViewGroup toView) {
        banner.setAdSize(AdSize.BANNER);
        if (GeneralSettings.isDevMode) {
            banner.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        } else {
            banner.setAdUnitId("ca-app-pub-1832172217205335/3658533168");
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        banner.setLayoutParams(params);
        toView.addView(banner);
    }

    public void addButtonToView(Button btnFBBanner, ViewGroup toView) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnFBBanner.setLayoutParams(params);
        toView.addView(btnFBBanner);
    }

    public void updateLastConnectionState() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress ipAddr = InetAddress.getByName("google.com");
                    //You can replace it with your name
                    GeneralSettings.wasConnectedToInternet = true;
                } catch (Exception e) {
                    GeneralSettings.wasConnectedToInternet = false;
                }
            }
        }).start();
    }

    public void initTJAds(Activity activity, Context context) {
        String tapjoySDKKey = "zdZi7DfORiyEmKDuQL6cRQECeRpfBk1g76VPDUAEvi9CZxvoMwS3SZ0eDcMe";
        String placementName = "WeThoongPlacement";
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();

        if (GeneralSettings.isDevMode) {
            Tapjoy.setDebugEnabled(true);
            connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");      // remember to turn this off for your production builds!
        } else {
            Tapjoy.setDebugEnabled(false);
            connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "false");
        }

        TapjoyAdsListener tjAdsListener = new TapjoyAdsListener(activity, placementName);
        Tapjoy.connect(context, tapjoySDKKey, connectFlags, tjAdsListener);
    }

    public static boolean isValidToShowIntestitialAds() {
        System.out.println("getLastAppOpenTimestamp: " + GeneralSettings.LAST_APP_OPEN_TIMESTAMP);
        System.out.println("getInterstitialAdsOpenTimes: " + GeneralSettings.INTERSTITIAL_ADS_OPEN_TIME);
        System.out.println("isEnableInterstitialAds: " + GeneralSettings.ENABLE_INTERSTITIAL_ADS);
        System.out.println("minimumAdsIntervalInSeconds: " + GeneralSettings.MINIMUM_ADS_INTERVAL);
        System.out.println("getLastInterstitialAdsOpenTimestamp: " + GeneralSettings.LAST_INTERSTITIAL_ADS_OPEN_TIMESTAMP);

        //If the app was opened more than a day, let reset the opening timestamp and interstitial ads open counter
        if (System.currentTimeMillis() / 1000 - GeneralSettings.LAST_APP_OPEN_TIMESTAMP > 86400) {
            GeneralSettings.LAST_APP_OPEN_TIMESTAMP = System.currentTimeMillis() / 1000;
            GeneralSettings.INTERSTITIAL_ADS_OPEN_TIME = 0;
        }

        //Show interstitial ads just in case:
        //1. Interstitial ads is enabled
        //2. It's long enough since the last time the ads shown (ex: at least 5 mins between shows)
        //3. The more time the ads shown, the longer interval until the next shown (ex: 5 mins for the first show, 10 mins for the second show, 15 mins for the third show and so on...)
        if (GeneralSettings.ENABLE_INTERSTITIAL_ADS
                && (System.currentTimeMillis() / 1000 - GeneralSettings.LAST_APP_OPEN_TIMESTAMP > GeneralSettings.MINIMUM_ADS_INTERVAL * (GeneralSettings.INTERSTITIAL_ADS_OPEN_TIME + 1))
                && (System.currentTimeMillis() / 1000 - GeneralSettings.LAST_INTERSTITIAL_ADS_OPEN_TIMESTAMP > GeneralSettings.MINIMUM_ADS_INTERVAL)) {
            return true;
        }
        return false;
    }
}
