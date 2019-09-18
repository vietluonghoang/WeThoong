package com.vietlh.wethoong;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.vietlh.wethoong.entities.AppConfiguration;
import com.vietlh.wethoong.networking.DeviceInfoCollector;
import com.vietlh.wethoong.networking.MessageContainer;
import com.vietlh.wethoong.networking.NetworkHandler;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.RedirectionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity {
    private DBConnection connection;
    private Button btnTracuuvanban;
    private Button btnTracuumucphat;
    private Button btnTracuubienbao;
    private Button btnTracuuvachkeduong;
    private Button btnChungtoilaai;
    private TextView versionInfo;
    private AdsHelper adsHelper;
    private MessageContainer msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.connection = DBConnection.getInstance(this);
        initComponents();
        MobileAds.initialize(this, "ca-app-pub-1832172217205335~8071107814");
        adsHelper = new AdsHelper();
        adsHelper.updateLastConnectionState();

        sendAnalytics();
        getAppConfigs();
        updateAppConfigs();
        GeneralSettings.LAST_APP_OPEN_TIMESTAMP = System.currentTimeMillis() / 1000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("############ In Onresume now. Checking.... ");
        if (GeneralSettings.isAppClosed) {
            sendAnalytics();
            getAppConfigs();
            GeneralSettings.isAppClosed = false;
        }
        updateAppConfigs();
        if (checkIfNeedToUpdate()){
            openUpdateScreen();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isInForeground = new RedirectionHelper().isAppInForeground(getApplicationContext());
        System.out.println("############ In Onstop - " + isInForeground);
        if (!isInForeground) {
            GeneralSettings.isAppClosed = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("############ In Onpause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("############ In Ondestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("############ In OnRestart");
    }

    private void sendAnalytics() {
        System.out.println("############ Sending analytics now. Checking....");
        final HashMap<String, String> deviceInfo = new HashMap<>();
        new DeviceInfoCollector(getApplicationContext()).execute(deviceInfo);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("############ here1.");
                while (!DeviceInfoCollector.IS_READY) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (deviceInfo != null) {
                    deviceInfo.put("action", "app_open");
                    deviceInfo.put("actiontype", "");
                    deviceInfo.put("actionvalue", "");
                    NetworkHandler net = new NetworkHandler();
                    String analyticsUrl = "https://wethoong-server.herokuapp.com/analytics/";
                    net.sendData(analyticsUrl, NetworkHandler.METHOD_POST, NetworkHandler.CONTENT_TYPE_APPLICATION_JSON, deviceInfo);
                } else {
                    System.out.println("------- Not ready to send analytics");
                }
            }
        });
    }

    private void getAppConfigs() {
        NetworkHandler net = new NetworkHandler();
        String getConfigUrl = "https://wethoong-server.herokuapp.com/getconfig/";
        net.requestData(getConfigUrl, NetworkHandler.MIME_TYPE_APPLICATION_JSON);
        msg = net.getMessages();
    }

    private void updateAppConfigs() {
        if (msg.getValue(MessageContainer.DATA) != null) {
            AppConfiguration appConfig = new AppConfiguration();
            JSONObject json = (JSONObject) msg.getValue(MessageContainer.DATA);
            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                String key = it.next();
                try {
                    switch (key) {
                        case AppConfiguration.ENABLE_BANNER_ADS:
                            appConfig.setEnableBannerAds((String) json.get(key));
                            GeneralSettings.ENABLE_BANNER_ADS = appConfig.isEnableBannerAds();
                            break;
                        case AppConfiguration.ENABLE_INAPP_NOTIF:
                            appConfig.setEnableInappNotif((String) json.get(key));
                            GeneralSettings.ENABLE_INAPP_NOTIF = appConfig.isEnableInappNotif();
                            break;
                        case AppConfiguration.ENABLE_INTERSTITIAL_ADS:
                            appConfig.setEnableInterstitialAds((String) json.get(key));
                            GeneralSettings.ENABLE_INTERSTITIAL_ADS = appConfig.isEnableInterstitialAds();
                            break;
                        case AppConfiguration.MINIMUM_ADS_INTERVAL:
                            appConfig.setMinimumAdsInterval((String) json.get(key));
                            GeneralSettings.MINIMUM_ADS_INTERVAL = appConfig.getMinimumAdsInterval();
                            break;
                        case AppConfiguration.MINIMUM_APP_VERSION:
                            appConfig.setMinimumAppVersion((String) json.get(key));
                            GeneralSettings.MINIMUM_APP_VERSION_REQUIRED = appConfig.getMinimumAppVersion();
                            break;
                        default:
                            System.out.println("Configuration is not valid");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("############ AppConfig....\nminVersion: " + appConfig.getMinimumAppVersion() + "\nminInterval: " + appConfig.getMinimumAdsInterval()
                    + "\nenable banner: " + appConfig.isEnableBannerAds() + "\nenable interstitial: " + appConfig.isEnableInterstitialAds() + "\nenable notif: " + appConfig.isEnableInappNotif());
        }
    }

    private void initComponents() {
        btnTracuuvanban = (Button) findViewById(R.id.btnTracuuvanbanphapluat);
        btnTracuuvanban.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTracuuvanbanScreen();
                    }
                }
        );
        btnTracuumucphat = (Button) findViewById(R.id.btnTracuumucphat);
        btnTracuumucphat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTracuumucphatScreen();
                    }
                }
        );
        btnTracuubienbao = (Button) findViewById(R.id.btnTracuubienbaohieu);
        btnTracuubienbao.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTracuuBienbaoScreen();
                    }
                }
        );
        btnTracuuvachkeduong = (Button) findViewById(R.id.btnTracuuvachkeduong);
        btnTracuuvachkeduong.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTracuuVachkeduongScreen();
                    }
                }
        );
        btnChungtoilaai = (Button) findViewById(R.id.btnChungtoilaai);
        btnChungtoilaai.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChungtoiScreen();
                    }
                }
        );
        versionInfo = (TextView) findViewById(R.id.versionInfo);
        versionInfo.setText(getVersionInfo());
    }

    private String getVersionInfo() {
        String versionName = "";
        long versionCode = 0;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "v." + versionName + "(" + versionCode + ") db." + GeneralSettings.dbVersion;
    }

    private boolean checkIfNeedToUpdate() {
        String versionName = "";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int result = compareVersionNames(versionName, GeneralSettings.MINIMUM_APP_VERSION_REQUIRED);

        if (result == -1){
            return true;
        }
        return false;
    }

    //If result is 0 then version are the same
    //if result is 1 then the old version is newer than the new one
    //if result is -1 then the old version is older than the new one
    private int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }

    private void openTracuuvanbanScreen() {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType", "vanban");
        startActivity(i);
    }

    private void openTracuumucphatScreen() {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType", "mucphat");
        startActivity(i);
    }

    private void openTracuuBienbaoScreen() {
        Intent i = new Intent(getApplicationContext(), BienbaoActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType", "bienbao");
        startActivity(i);
    }

    private void openTracuuVachkeduongScreen() {
        Intent i = new Intent(getApplicationContext(), VachkeduongActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType", "vachkeduong");
        startActivity(i);
    }

    private void openChungtoiScreen() {
        Intent i = new Intent(getApplicationContext(), ChungtoiActivity.class);
        startActivity(i);
    }

    private void openUnderconstructionScreen() {
        Intent i = new Intent(getApplicationContext(), UnderconstructionActivity.class);
        startActivity(i);
    }

    private void openUpdateScreen() {
        Intent i = new Intent(getApplicationContext(), ChungtoiActivity.class);
        startActivity(i);
    }
}
