package com.vietlh.wethoong.entities;

public class AppConfiguration {
    public static final String MINIMUM_APP_VERSION = "minimum_app_version";
    public static final String ENABLE_INAPP_NOTIF = "enable_inapp_notif";
    public static final String ENABLE_BANNER_ADS = "enable_banner_ads";
    public static final String ENABLE_INTERSTITIAL_ADS = "enable_interstitial_ads";
    public static final String MINIMUM_ADS_INTERVAL = "minimum_ads_interval";

    private String minimumAppVersion = "";
    private boolean enableInappNotif = false;
    private boolean enableBannerAds = false;
    private boolean enableInterstitialAds = false;
    private int minimumAdsInterval = 300; //in seconds

    public String getMinimumAppVersion() {
        return minimumAppVersion;
    }

    public void setMinimumAppVersion(String minimumAppVersion) {
        this.minimumAppVersion = minimumAppVersion;
    }

    public boolean isEnableInappNotif() {
        return enableInappNotif;
    }

    public void setEnableInappNotif(boolean enableInappNotif) {
        this.enableInappNotif = enableInappNotif;
    }

    public void setEnableInappNotif(String enableInappNotif) {
        if ("1".equals(enableInappNotif)) {
            this.enableInappNotif = true;
        } else {
            this.enableInappNotif = false;
        }
    }

    public boolean isEnableBannerAds() {
        return enableBannerAds;
    }

    public void setEnableBannerAds(boolean enableBannerAds) {
        this.enableBannerAds = enableBannerAds;
    }

    public void setEnableBannerAds(String enableBannerAds) {
        if ("1".equals(enableBannerAds)) {
            this.enableBannerAds = true;
        } else {
            this.enableBannerAds = false;
        }
    }

    public boolean isEnableInterstitialAds() {
        return enableInterstitialAds;
    }

    public void setEnableInterstitialAds(boolean enableInterstitialAds) {
        this.enableInterstitialAds = enableInterstitialAds;
    }

    public void setEnableInterstitialAds(String enableInterstitialAds) {
        if ("1".equals(enableInterstitialAds)) {
            this.enableInterstitialAds = true;
        } else {
            this.enableInterstitialAds = false;
        }
    }

    public int getMinimumAdsInterval() {
        return minimumAdsInterval;
    }

    public void setMinimumAdsInterval(int minimumAdsInterval) {
        this.minimumAdsInterval = minimumAdsInterval;
    }

    public void setMinimumAdsInterval(String minimumAdsInterval) {
        this.minimumAdsInterval = Integer.parseInt(minimumAdsInterval);
    }
}
