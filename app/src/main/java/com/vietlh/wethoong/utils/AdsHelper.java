package com.vietlh.wethoong.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.net.InetAddress;

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
            //                  ca-app-pub-1832172217205335/3658533168
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
}
