package com.vietlh.wethoong.networking;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.HashMap;

public class DeviceInfoCollector extends AsyncTask<HashMap<String, String>, Void, HashMap<String, String>> {
    public static boolean IS_READY = false;
    private String idForVendor = "";
    private String adsId = "";
    private String deviceName = "";
    private String osName = "Android";
    private String osVersion = "";
    private String appVersion = "";
    private String appVersionNumber = "";
    private Context context;

    public DeviceInfoCollector(Context context) {
        this.context = context;
    }

    private String getIdForVendor() {
        idForVendor = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return idForVendor;
    }

    private String getAdsId() {
        try {
            System.out.println("############ Getting advertising id....");
            adsId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        return adsId;
    }

    private String getDeviceName() {
        deviceName = android.os.Build.MANUFACTURER + android.os.Build.MODEL;
        return deviceName;
    }

    private String getOsName() {
        return osName;
    }

    private String getOsVersion() {
        osVersion = Build.VERSION.RELEASE;
        return osVersion;
    }

    private String getAppVersion() {
        try {
            appVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            return appVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getAppVersionNumber() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionNumber = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).getLongVersionCode());
            } else {
                appVersionNumber = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            }
            return appVersionNumber;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected HashMap<String, String> doInBackground(HashMap<String, String>... hashMaps) {
        if (hashMaps.length > 0){
            System.out.println("############ Getting device info....");
            hashMaps[0].put("idforvendor", getIdForVendor());
            hashMaps[0].put("adsid", getAdsId());
            hashMaps[0].put("devicename", getDeviceName());
            hashMaps[0].put("osname", getOsName());
            hashMaps[0].put("osversion", getOsVersion());
            hashMaps[0].put("appversion", getAppVersion());
            hashMaps[0].put("appversionnumber", getAppVersionNumber());

            if (idForVendor == "" || adsId == "" || deviceName == "" || osName == "" || osVersion == "" || appVersion == "" || appVersionNumber == "") {
                return null;
            }
            return hashMaps[0];
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        System.out.println("############ onPreExecute....");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        System.out.println("############ onProgressUpdate....");
    }

    @Override
    protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
        super.onPostExecute(stringStringHashMap);
        if (stringStringHashMap != null){
            IS_READY = true;
        }
        System.out.println("############ onPostExecute....\n"+stringStringHashMap);
    }
}
