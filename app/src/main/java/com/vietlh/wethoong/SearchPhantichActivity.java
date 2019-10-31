package com.vietlh.wethoong;

import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vietlh.wethoong.adapters.ListRecyclerViewAdapter;
import com.vietlh.wethoong.adapters.PhantichListRecyclerViewAdapter;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.entities.Phantich;
import com.vietlh.wethoong.entities.interfaces.CallbackActivity;
import com.vietlh.wethoong.networking.MessageContainer;
import com.vietlh.wethoong.networking.NetworkHandler;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.RedirectionHelper;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchPhantichActivity extends AppCompatActivity implements CallbackActivity {
    private static final String TAG = "SearchPhantichActivity";
    private RecyclerView searchResultRecyclerView;
    private PhantichListRecyclerViewAdapter searchResultListRecyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Phantich> allPhantich;
    private HashMap<String, Phantich> rawPhantichList;
    private Queries queries = new Queries(DBConnection.getInstance(this));
    private ConstraintLayout coverView;
    private LinearLayout loctheoView;
    private Button btnLoctheo;
    private TextView txtLoctheo;
    private EditText tfSearch;
    private LinearLayout adsView;
    private TextView coverMessage;
    private NetworkHandler net;
    private UtilsHelper helper = new UtilsHelper();
    private AdsHelper adsHelper = new AdsHelper();
    private RedirectionHelper redirectionHelper = new RedirectionHelper();

    private final String ACTION_CASE_UPDATE_PHANTICH = "updatePhantich";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_phantich);
        initComponents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isInForeground = new RedirectionHelper().isAppInForeground(getApplicationContext());
        System.out.println("############ SearchPhantichActivity: In Onstop - " + isInForeground);
        if (!isInForeground) {
            GeneralSettings.isAppClosed = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("############ SearchPhantichActivity: In onResume ");
        initAds();
        showCoverLayer(true);
        allPhantich = new ArrayList<>();

        searchResultRecyclerView = (RecyclerView) findViewById(R.id.search_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        searchResultRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        recyclerLayoutManager = new LinearLayoutManager(this);
        searchResultRecyclerView.setLayoutManager(recyclerLayoutManager);

        // specify an adapter (see also next example)
        searchResultListRecyclerAdapter = new PhantichListRecyclerViewAdapter(this, allPhantich);
        searchResultRecyclerView.setAdapter(searchResultListRecyclerAdapter);

        //workaround to make searchResultRecyclerView to match parent width
        ViewGroup.LayoutParams searchResultLayoutParams = searchResultRecyclerView.getLayoutParams();
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        searchResultLayoutParams.width = helper.getScreenWidth() - 40;
        searchResultLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        searchResultRecyclerView.setLayoutParams(searchResultLayoutParams);

        rawPhantichList = new HashMap<>();
        checkPhantichList();
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
    }

    private void initComponents() {
        coverView = findViewById(R.id.coverLayer);
        coverMessage = (TextView) coverView.findViewById(R.id.coverMessage);
        tfSearch = (EditText) findViewById(R.id.txtSearch);
        loctheoView = (LinearLayout) findViewById(R.id.locTheoView);
        if (GeneralSettings.isDevMode) {
            btnLoctheo = (Button) loctheoView.findViewById(R.id.btnLoctheo);
            txtLoctheo = (TextView) loctheoView.findViewById(R.id.lblLoctheo);
            btnLoctheo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAllPhantichData();
                }
            });
        } else {
            loctheoView.setVisibility(View.GONE);
        }

        tfSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateResultList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateResultList(String keyword) {
        HashMap<String, Phantich> rawPhantichList = search(keyword.trim());
        updatePhantichList(rawPhantichList);
        searchResultListRecyclerAdapter.updateView(allPhantich);
    }

    private void updatePhantichList(HashMap<String, Phantich> arrPhantich) {
        allPhantich = new ArrayList<>();
        for (String key : arrPhantich.keySet()) {
            allPhantich.add(arrPhantich.get(key));
        }
        searchResultListRecyclerAdapter.updateView(allPhantich);
    }

    private HashMap<String, Phantich> search(String keyword) {
        if (keyword.length() > 0) {
            return queries.getPhantichByKeyword(keyword.toLowerCase());
        } else {
            return queries.getAllPhantich();
        }
    }

    private void showCoverLayer(Boolean now) {
        if (now) {
            coverView.setVisibility(View.VISIBLE);
        } else {
            coverView.setVisibility(View.GONE);
        }
    }

    private void checkPhantichList() {
        System.out.println("Checking data....");
        if (GeneralSettings.wasConnectedToInternet) {
            coverMessage.setText("Đang xử lý dữ liệu......");
            if (rawPhantichList.size() < 1) {
                getPhantichList();
                getPhantichListFromDatabase();
                System.out.println("Waiting for response....");
                if (allPhantich.size() > 0) {
                    System.out.println("Found local data!");
                    showCoverLayer(false);
                }
            } else {
                System.out.println("Received response....");
                coverMessage.setText("Đang xử lý dữ liệu......");
                showCoverLayer(true);
                System.out.println("Checking data revison....");
                Phantich rawPhantich = null;
                for (String key : rawPhantichList.keySet()) {
                    rawPhantich = rawPhantichList.get(key);
                    if (rawPhantich != null) {
                        break;
                    }
                }
                if (rawPhantich != null) {
                    if ((allPhantich.size() > 0 && (allPhantich.get(0).getRevision() < rawPhantich.getRevision()))
                            || (allPhantich.size() < 1)) {
                        System.out.println("Updating local data....");
                        boolean isSuccess = queries.insertPhantichToDatabase(rawPhantichList); //TO DO: should have a fallback solution for this, in case if it failed to insert data
                        getPhantichListFromDatabase();

                        System.out.println("Done updating data!");
                    } else {
                        System.out.println("No newer data!");
                    }
                    //finish updating data
                    showCoverLayer(false);
                } else {
                    coverMessage.setText("Có lỗi khi xử lý dữ liệu!\nVui lòng thử lại sau.");
                }
            }
        } else {
            coverMessage.setText("Có lỗi kết nối mạng!\nVui lòng kiểm tra lại kết nối và thử lại sau....");
        }
    }

    private void getPhantichList() {
        String target = "https://wethoong-server.herokuapp.com/phantich/getphantich/";
        net = new NetworkHandler(this, target, NetworkHandler.METHOD_GET, NetworkHandler.MIME_TYPE_APPLICATION_JSON, null, ACTION_CASE_UPDATE_PHANTICH);
        net.execute();
    }

    private void initPhantichList() {
        System.out.println("Initializing raw data");
        MessageContainer result = net.getMessages();
        if (result != null) {
            if (result.getValue(MessageContainer.DATA) instanceof HashMap) {
                rawPhantichList = (HashMap<String, Phantich>) result.getValue(MessageContainer.DATA);
            } else {
                System.out.println("Error getting data: " + result.getValue(MessageContainer.DATA) + "");
            }
        } else {
            System.out.println("Null response");
        }
    }

    private void getPhantichListFromDatabase() {
        System.out.println("Get data from local");
        updatePhantichList(queries.getAllPhantich());
    }

    private void removeAllPhantichData() {
        queries.executeUpdateQuery("delete from phantich");
        queries.executeUpdateQuery("delete from phantich_details");
        queries.executeUpdateQuery("delete from tblAppConfigs");
    }

    @Override
    public void triggerCallbackAction(String actionCase) {
        System.out.println("==== actionCase: " + actionCase);
        switch (actionCase) {
            case ACTION_CASE_UPDATE_PHANTICH:
                net.parsePhantichData();
                initPhantichList();
                checkPhantichList();
                break;
            default:
                System.out.println("======= no valid action found ======");
        }
    }
}
