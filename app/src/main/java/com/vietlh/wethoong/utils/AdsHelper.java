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
    public void addBannerViewtoView(AdView banner, ViewGroup toView){
        banner.setAdSize(AdSize.BANNER);
        if(GeneralSettings.isDevMode) {
            banner.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        }else {
            banner.setAdUnitId("ca-app-pub-1832172217205335/3658533168");
        }

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        banner.setLayoutParams(params);
        toView.addView(banner);
    }

    public void addButtonToView(Button btnFBBanner, ViewGroup toView){
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
}
