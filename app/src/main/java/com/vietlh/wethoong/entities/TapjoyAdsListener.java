package com.vietlh.wethoong.entities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPlacementVideoListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyLog;
import com.vietlh.wethoong.utils.AdsHelper;

import static android.support.constraint.Constraints.TAG;

public class TapjoyAdsListener extends Activity implements TJPlacementListener, TJPlacementVideoListener, TJConnectListener {
    private TJPlacement placement;

    public TapjoyAdsListener(Activity activity, String placementName) {
        Tapjoy.setActivity(activity);
        placement = Tapjoy.getPlacement(placementName, this);
    }

    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {
        TapjoyLog.i(TAG, "onRequestSuccess for placement " + tjPlacement.getName());

        if (!tjPlacement.isContentAvailable()) {
            TapjoyLog.i(TAG, "No content available for placement  " + tjPlacement.getName());
        }
    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
        Log.i(TAG, "Tapjoy send event " + tjPlacement.getName() + " failed with error: " + tjError.message);
    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {
        TapjoyLog.i(TAG, "onContentReady for placement " + tjPlacement.getName());
        if (AdsHelper.isValidToShowIntestitialAds()) {
            tjPlacement.showContent();
        }
    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {
        TapjoyLog.i(TAG, "onContentShow for placement " + tjPlacement.getName());
    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {
        TapjoyLog.i(TAG, "onContentDismiss for placement " + tjPlacement.getName());
    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

    }

    @Override
    public void onClick(TJPlacement tjPlacement) {
        TapjoyLog.i(TAG, "onClick for placement " + tjPlacement.getName());
    }

    @Override
    public void onVideoStart(TJPlacement tjPlacement) {
        Log.i(TAG, "Video has started has started for: " + tjPlacement.getName());
    }

    @Override
    public void onVideoError(TJPlacement tjPlacement, String s) {
        Log.i(TAG, "Video error: " + s + " for " + tjPlacement.getName());
    }

    @Override
    public void onVideoComplete(TJPlacement tjPlacement) {
        Log.i(TAG, "Video has completed for: " + tjPlacement.getName());
    }

    @Override
    public void onConnectSuccess() {
        Log.i(TAG, "Successfully connect to TJ");
        placement.requestContent();
    }

    @Override
    public void onConnectFailure() {
        Log.i(TAG, "Failed connect to TJ");
    }
}
