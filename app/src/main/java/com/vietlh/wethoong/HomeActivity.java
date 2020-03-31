package com.vietlh.wethoong;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.vietlh.wethoong.entities.AppConfiguration;
import com.vietlh.wethoong.entities.interfaces.CallbackActivity;
import com.vietlh.wethoong.networking.DeviceInfoCollector;
import com.vietlh.wethoong.networking.MessageContainer;
import com.vietlh.wethoong.networking.NetworkHandler;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.RedirectionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity implements CallbackActivity {
    private DBConnection connection;
    private Button btnTracuuvanban;
    private Button btnTracuumucphat;
    private Button btnTracuubienbao;
    private Button btnTracuuvachkeduong;
    private Button btnHuongdanluat;
    private Button btnChungtoilaai;
    private TextView versionInfo;
    private AdsHelper adsHelper;
    private NetworkHandler network;
    private NetworkHandler net;
    private MessageContainer msg;
    private HashMap<String, String> deviceInfo = new HashMap<>();
    private Queries queries = new Queries(DBConnection.getInstance(this));

    //list of params for callback
    private final String ACTION_CASE_SEND_ANALYTICS = "sendAnalytics";
    private final String ACTION_CASE_UPDATE_APP_CONFIG = "updateConfig";
    private final String ACTION_CASE_CHECK_ADS_OPTOUT_STATE = "checkAdsOptoutState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.connection = DBConnection.getInstance(this);
        initComponents();
        MobileAds.initialize(this, "ca-app-pub-1832172217205335~8071107814");
        adsHelper = new AdsHelper();
        adsHelper.updateLastConnectionState(this);
        GeneralSettings.LAST_APP_OPEN_TIMESTAMP = System.currentTimeMillis() / 1000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("############ HomeActivity: In Onresume now. Checking.... ");
        long now = System.currentTimeMillis() / 1000;
        if (GeneralSettings.isAppClosed && (now - GeneralSettings.lastApplicationStateCheckTimestamp > GeneralSettings.defaultApplicationStateCheckInterval)) {
            System.out.println("!@#$%&*(*(&^&^%%$^#%#@$#!$@#%& con me no ==============");
            new DeviceInfoCollector(this, getApplicationContext(), ACTION_CASE_SEND_ANALYTICS).execute(deviceInfo);
            getAppConfigs();
            GeneralSettings.isAppClosed = false;
            GeneralSettings.lastApplicationStateCheckTimestamp = now;
        }
        if (checkIfNeedToUpdate()) {
            openUpdateScreen();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isInForeground = new RedirectionHelper().isAppInForeground(getApplicationContext());
        System.out.println("############ HomeActivity: In Onstop - " + isInForeground);
        if (!isInForeground) {
            GeneralSettings.isAppClosed = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("############ HomeActivity: In Onpause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("############ HomeActivity: In Ondestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("############ HomeActivity: In OnRestart");
    }

    @Override
    public void triggerCallbackAction(String actionCase) {
        switch (actionCase) {
            case ACTION_CASE_SEND_ANALYTICS:
                sendAnalytics();
                break;
            case ACTION_CASE_UPDATE_APP_CONFIG:
                checkAdsOptout();
                updateAppConfigs();
                break;
            case ACTION_CASE_CHECK_ADS_OPTOUT_STATE:
                net.parseResultStatusData();
                checkCodeState();
                break;
            default:
                System.out.println("+++++ no valid action found +++++");
        }
    }

    private void sendAnalytics() {
        System.out.println("############ Sending analytics now. Checking....");
        deviceInfo.put("action", "app_open");
        deviceInfo.put("actiontype", "");
        deviceInfo.put("actionvalue", "");
        deviceInfo.put("dbversion", "" + connection.getCurrentDBVersion());
        String analyticsUrl = "https://wethoong-server.herokuapp.com/analytics/";
        new NetworkHandler(this, analyticsUrl, NetworkHandler.METHOD_POST, NetworkHandler.CONTENT_TYPE_APPLICATION_JSON, NetworkHandler.MIME_TYPE_APPLICATION_JSON, deviceInfo, "").execute();
    }

    private void getAppConfigs() {
        String getConfigUrl = "https://wethoong-server.herokuapp.com/getconfig/";

        network = new NetworkHandler(this, getConfigUrl, NetworkHandler.METHOD_GET, NetworkHandler.MIME_TYPE_APPLICATION_JSON, null, ACTION_CASE_UPDATE_APP_CONFIG);
        network.execute();
    }

    private void updateAppConfigs() {
//        network.parseAppConfigData();
        network.parseAppConfigData();
        msg = network.getMessages();

        if (msg != null && msg.getValue(MessageContainer.DATA) != null) {
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

    private void checkAdsOptout() {
        String valueInDatabase = queries.getAppConfigsFromDatabaseByKey("adsOptout");
        switch (valueInDatabase) {
            case "1":
                System.out.println("adsoptout state set in database: True");
                GeneralSettings.isAdsOptout = true;
            case "0":
                System.out.println("adsoptout state set in database: Failed");
                GeneralSettings.isAdsOptout = false;
            default:
                System.out.println("send request to check adsoptout state");
                String target = "https://wethoong-server.herokuapp.com/hasoptout";
                net = new NetworkHandler(this, target, NetworkHandler.METHOD_POST, NetworkHandler.CONTENT_TYPE_APPLICATION_JSON, NetworkHandler.MIME_TYPE_APPLICATION_JSON, deviceInfo, ACTION_CASE_CHECK_ADS_OPTOUT_STATE);
                net.execute();
        }

    }

    private void checkCodeState() {
        System.out.println("############# check code message.....");
        try {
            HashMap<String, String> message = (HashMap<String, String>) net.getMessages().getValue(MessageContainer.DATA);
            System.out.println("code message: " + message);

            HashMap<String, String> config = new HashMap<>();
            if ("Success".equals(message.get("status"))) {
                System.out.println("updating adsOptout to: '1'");
                config.put(GeneralSettings.APP_CONFIG_KEY_ADSOPTOUT, "1");
                GeneralSettings.isAdsOptout = queries.updateAppConfigsToDatabase(config);
            } else {
                System.out.println("updating adsOptout to: '0'");
                config.put(GeneralSettings.APP_CONFIG_KEY_ADSOPTOUT, "0");
                GeneralSettings.isAdsOptout = false;
                queries.updateAppConfigsToDatabase(config);
            }
        } catch (Exception e) {
            System.out.println("Fail to checkCodeState: " + e.getMessage());
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
        btnHuongdanluat = (Button) findViewById(R.id.btnHuongdanluat);
        btnHuongdanluat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openHuongdanluatScreen();
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
        return "v." + versionName + "(" + versionCode + ") db." + connection.getCurrentDBVersion();
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

        if (result == -1) {
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

    private void openHuongdanluatScreen() {
        Intent i = new Intent(getApplicationContext(), SearchPhantichActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
//        i.putExtra("searchType", "vachkeduong");
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
        Intent i = new Intent(getApplicationContext(), UpdateActivity.class);
        startActivity(i);
    }
}
