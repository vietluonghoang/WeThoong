package com.vietlh.wethoong.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.HashMap;

public class AnalyticsHelper {
    private static FirebaseAnalytics mFirebaseAnalytics;
    public static String SCREEN_NAME_TRACUUVANBAN = "TracuuVanban";
    public static String SCREEN_NAME_TRACUUMUCPHAT = "TracuuMucphat";
    public static String SCREEN_NAME_TRACUUBIENBAO = "TracuuBienbao";
    public static String SCREEN_NAME_TRACUUVACHKEDUONG = "TracuuVachkeduong";
    public static String SCREEN_NAME_HUONGDANLUAT = "Huongdanluat";
    public static String SCREEN_NAME_CHUNGTOI = "AboutUs";
    public static String SCREEN_NAME_UNDERCONSTRUCTION = "Underconstruction";
    public static String SCREEN_NAME_UPDATEVERSION = "UpdateVersion";

    private static MixpanelAPI mp;

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

    public static void initMixPanel(Context context, String userID) {
        if (userID == null) {
            userID = GeneralSettings.DEFAULT_MIXPANEL_USER_ID;
        } else {
            userID = userID.trim().toLowerCase();
        }
        // Init Mixpanel
        mp = MixpanelAPI.getInstance(context, GeneralSettings.MIXPANEL_PROJECT_TOKEN, GeneralSettings.TRACK_AUTOMATIC_EVENTS);
        mp.identify(userID, true);
    }

    public static void sendAnalyticEvent(String eventName, HashMap<String, String> params) {
        JSONObject props = new JSONObject();
        if (params != null) {
            for (String key : params.keySet()) {
                try {
                    props.put(key, params.get(key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(GeneralSettings.MIXPANEL_ENABLED) {
            mp.track(eventName, props);
            System.out.println("+++++ MixPanel enabled\n" + eventName + "\n" + props);
        }else {
            System.out.println("+++++ MixPanel disabled\n" + eventName + "\n" + props);
        }
    }
}
