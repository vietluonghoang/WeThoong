package com.vietlh.wethoong.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;

public class RedirectionHelper {

    public HashMap<String, HashMap<String, String>> initFBUrlSets(HashMap<String, HashMap<String, String>> sets, int order, String fbUrl, String fbId) {
        HashMap<String, String> urlSet = new HashMap<>();
        urlSet.put("fbUrl", fbUrl);
        urlSet.put("fbId", fbId);
        sets.put("set" + order, urlSet);
        return sets;
    }

    //method to get the right URL to use in the intent
    private String getFacebookPageURL(Context context, String fbUrl, String fbId) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + fbUrl;
            } else { //older versions of fb app
                return "fb://profile/" + fbId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return fbUrl; //normal web url
        }
    }

    public void openUrlInExternalBrowser(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("mailto:") && !url.startsWith("fb://")) {
            url = "http://" + url;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public void openFacebook(Context context, HashMap<String, HashMap<String, String>> urlSets) {
        int randomNumber = new UtilsHelper().getRandomNumberInRange(0, urlSets.keySet().size() - 1);
        String fbUrl = urlSets.get("set" + randomNumber).get("fbUrl");
        String fbId = urlSets.get("set" + randomNumber).get("fbId");

        try {
            String url = getFacebookPageURL(context, fbUrl, fbId);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (Exception ex) {
            openUrlInExternalBrowser(context, fbUrl);
        }
    }

    public boolean isAppInForeground(@NonNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            if (runningAppProcess.processName.equals(context.getPackageName()) &&
                    runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
