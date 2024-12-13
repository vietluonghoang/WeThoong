package com.vietlh.wethoong;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vietlh.wethoong.entities.Phantich;
import com.vietlh.wethoong.entities.interfaces.CallbackActivity;
import com.vietlh.wethoong.networking.DeviceInfoCollectorRunnable;
import com.vietlh.wethoong.networking.NetworkHandler;
import com.vietlh.wethoong.networking.NetworkHandlerRunnable;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.RedirectionHelper;
import com.vietlh.wethoong.utils.SearchFor;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.util.HashMap;

public class DetailsPhantichActivity extends AppCompatActivity implements CallbackActivity {

    private Queries queries = new Queries(DBConnection.getInstance(this));
    private UtilsHelper helper = new UtilsHelper();
    private RedirectionHelper redirectionHelper = new RedirectionHelper();
    private AdsHelper adsHelper = new AdsHelper();
    private SearchFor search = new SearchFor(this);
    private Phantich phantich;
    private String phantichId;
    private HashMap<String, String> deviceInfo = new HashMap<>();

    private ScrollView scrollView;
    private Button btnSource;
    private TextView lblTitle;
    private Button btnBreadscrubs;
    private TextView lblAuthorName;
    private LinearLayout phantichDetailsView;

    private LinearLayout adsView;

    //list of params for callback
    private final String ACTION_CASE_SEND_ANALYTICS = "sendAnalytics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_phantich);

        getPassingParameters();
        initComponents();

        if (!GeneralSettings.wasConnectedToInternet) {
            findViewById(R.id.sourceView).setVisibility(View.GONE);
            findViewById(R.id.authorView).setVisibility(View.GONE);
            lblTitle.setText("Lỗi kết nối mạng!\nHãy kiểm tra lại kết nối và thử mở lại màn hình này!");
        } else {
//            new DeviceInfoCollector(this, getApplicationContext(), ACTION_CASE_SEND_ANALYTICS).execute(deviceInfo);
            new Thread(new DeviceInfoCollectorRunnable(this, getApplicationContext(), ACTION_CASE_SEND_ANALYTICS, deviceInfo)).start();
            phantich = queries.getPhantichById(phantichId).get(phantichId);
            showPhantich();
        }
        initAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adsHelper.updateLastConnectionState(this);
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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        helper.createContextMenu(menu, v, this);
    }

    private void getPassingParameters() {
        phantichId = (String) getIntent().getStringExtra("phantichId");
    }

    private void initComponents() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        btnSource = (Button) findViewById(R.id.btnSource);
        btnSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lblTitle = (TextView) findViewById(R.id.lblTittle);
        btnBreadscrubs = (Button) findViewById(R.id.btnBreadscrubs);
        btnBreadscrubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lblAuthorName = (TextView) findViewById(R.id.lblAuthorName);
//        registerForContextMenu(lblNoidung);
        phantichDetailsView = (LinearLayout) findViewById(R.id.phantichDetailsView);
    }

    private void initAds() {
        adsView = (LinearLayout) findViewById(R.id.adsView);
        adsHelper.updateLastConnectionState(this);
        if (GeneralSettings.wasConnectedToInternet && GeneralSettings.ENABLE_BANNER_ADS) {
            AdView googleAdView = new AdView(this);
            adsHelper.addBannerViewtoView(googleAdView, adsView);
            AdRequest adRequest = new AdRequest.Builder().build();
            googleAdView.loadAd(adRequest);
        } else {
            Button btnFBBanner = new Button(this);
            btnFBBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, HashMap<String, String>> urlSets = new HashMap<>();
                    redirectionHelper.initFBUrlSets(urlSets, 0, getResources().getString(R.string.wethoongFB), getResources().getString(R.string.wethoongFBApp));
                    redirectionHelper.initFBUrlSets(urlSets, 1, getResources().getString(R.string.condonghieuluatFB), getResources().getString(R.string.condonghieuluatFBApp));
                    redirectionHelper.openFacebook(getApplicationContext(), urlSets);

                }
            });
            btnFBBanner.setBackgroundResource(R.drawable.facebook_banner_wethoong);
            adsHelper.addButtonToView(btnFBBanner, adsView);
        }
        if (GeneralSettings.ENABLE_INTERSTITIAL_ADS) {
            adsHelper.initTJAds(this, getApplicationContext());
        }
    }

    private void showPhantich() {
        lblTitle.setText(phantich.getTittle());
        lblAuthorName.setText(phantich.getAuthor());
        btnSource.setText(phantich.getSource());
        btnSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectionHelper.openUrlInExternalBrowser(getApplicationContext(), phantich.getSource());
            }
        });
        HashMap<String, ViewGroup> details = phantich.getContentDetails(getApplicationContext());
        int order = 0;
        int counter = 0;
        HashMap<String, ViewGroup> orderList = new HashMap<>();
        while (order < details.size()) {
            if (details.get(String.valueOf(counter)) != null) {
                orderList.put(String.valueOf(order), details.get(String.valueOf(counter)));
                order += 1;
            }
            counter += 1;
        }

        for (int i = 0; i < orderList.size(); i++) {
            phantichDetailsView.addView(orderList.get(String.valueOf(i)));
        }
    }

    private void sendAnalytics() {
        System.out.println("############ Sending analytics now. Checking....");
        deviceInfo.put("action", "view_phantich");
        deviceInfo.put("actiontype", "view_detail");
        deviceInfo.put("actionvalue", phantichId);
        String analyticsUrl = "https://wethoong-server.herokuapp.com/analytics/";
//        new NetworkHandler(this, analyticsUrl, NetworkHandler.METHOD_POST, NetworkHandler.CONTENT_TYPE_APPLICATION_JSON, NetworkHandler.MIME_TYPE_APPLICATION_JSON, deviceInfo, "").execute();
        //            Replace AsyncTask by Thread
        NetworkHandlerRunnable networkRunnable = new NetworkHandlerRunnable(this, analyticsUrl, NetworkHandler.METHOD_POST, NetworkHandler.CONTENT_TYPE_APPLICATION_JSON, NetworkHandler.MIME_TYPE_APPLICATION_JSON, deviceInfo, "");
        new Thread(networkRunnable).start();
        networkRunnable.updateResult();
    }

    @Override
    public void triggerCallbackAction(String actionCase) {
        switch (actionCase) {
            case ACTION_CASE_SEND_ANALYTICS:
                sendAnalytics();
                break;
            default:
                System.out.println("+++++ no valid action found +++++");
        }
    }
}
