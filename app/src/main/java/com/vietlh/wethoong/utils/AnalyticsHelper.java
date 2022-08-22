package com.vietlh.wethoong.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

public class AnalyticsHelper {
    private static FirebaseAnalytics mFirebaseAnalytics;

    //these parameters are cross-updated by DeviceInfoCollector. Always make sure that it runs first before sending any analytics event
    public static String idForVendor = "";
    public static String adsId = "";
    public static int dbVersion;

    public static void sendAnalyticEvent(Context context, String eventName, HashMap<String, String> params) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("dbVersion", dbVersion + "");
        if (!idForVendor.isEmpty()) {
            bundle.putString("idforvendor", idForVendor);
        }
        if (!adsId.isEmpty()) {
            bundle.putString("adsid", adsId);
        }
        String payload = eventName + "&/" + "idforvendor" + "*" + bundle.get("idforvendor") + "//" + "adsid" + "*" + bundle.get("adsid") + "//" + "dbVersion" + "*" + bundle.get("dbVersion") + "/";
        if (params != null) {
            for (String key : params.keySet()) {
                bundle.putString(key, params.get(key));
                payload += "/" + key + "*" + params.get(key) + "/";
            }
        }
        mFirebaseAnalytics.logEvent(eventName, bundle);
        System.out.println("+++++ analytics tracking sent: " + payload);
    }
}
