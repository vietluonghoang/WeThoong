package com.vietlh.wethoong;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vietlh.wethoong.adapters.ListRecyclerViewAdapter;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.RedirectionHelper;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private RecyclerView searchResultRecyclerView;
    private ListRecyclerViewAdapter searchResultListRecyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Dieukhoan> allDieukhoan;
    private Queries queries = new Queries(DBConnection.getInstance(this));
    private LinearLayout searchView;
    private Button btnLoctheo;
    private TextView txtLoctheo;
    private EditText tfSearch;
    private ImageButton btnMicro;
    private String searchKeyword;
    private LinearLayout adsView;
    private ArrayList<String> vanbanid = new ArrayList<>();
    private HashMap<Integer, Boolean> vanbanState = new HashMap<>();
    private ArrayList<String> phuongtien = new ArrayList<>();
    private HashMap<String, String> mucphat = new HashMap<>();
    private String searchType;
    private UtilsHelper helper = new UtilsHelper();
    private AdsHelper adsHelper = new AdsHelper();
    private RedirectionHelper redirectionHelper = new RedirectionHelper();
    private final int REQ_CODE = 100;

    //Filter popup elements
    private AlertDialog.Builder builder;
    AlertDialog alert = null;
    private View customView;
    private LinearLayout lineLoutLoaivanban;
    private LinearLayout lineLoutLoaivanbanItem;
    private LinearLayout lineLoutPhuongtien;
    private Switch swtPhuongtien;
    private Button btnPhuongtienOto;
    private Button btnPhuongtienXemay;
    private Button btnPhuongtienXedap;
    private Button btnPhuongtienXechuyendung;
    private Button btnPhuongtienTauhoa;
    private Button btnPhuongtienDibo;
    private LinearLayout lineLoutMucphatSelection;
    private Switch swtMucphat;
    private Spinner spMucphatTu;
    private Spinner spMucphatDen;
    private int colorNormalBtnBg;
    private int colorNormalBtnFg;
    private int colorSelectedBtnBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getPassingParameters();
        colorNormalBtnBg = getResources().getColor(R.color.normalBtnBG);
        colorSelectedBtnBg = getResources().getColor(R.color.selectedBtnBG);
        initComponents();
        builder = new AlertDialog.Builder(this);

        searchResultRecyclerView = (RecyclerView) findViewById(R.id.search_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        searchResultRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        recyclerLayoutManager = new LinearLayoutManager(this);
        searchResultRecyclerView.setLayoutManager(recyclerLayoutManager);

        // specify an adapter (see also next example)
        searchResultListRecyclerAdapter = new ListRecyclerViewAdapter(this, allDieukhoan, searchKeyword);
        if (!searchType.equals("lienquan")) {
            updateResultList("");
        } else {
            updateResultListWithRelatedDieukhoan();
        }
        searchResultRecyclerView.setAdapter(searchResultListRecyclerAdapter);

        //workaround to make searchResultRecyclerView to match parent width
        ViewGroup.LayoutParams searchResultLayoutParams = searchResultRecyclerView.getLayoutParams();
        searchResultLayoutParams.width = helper.getScreenWidth();
        searchResultRecyclerView.setLayoutParams(searchResultLayoutParams);
        initAds();

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    updateSearchBarText(result.get(0).toString());
                    System.out.println("=====SR: " + result.get(0));
                }
                break;
            }
        }
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

    private void getPassingParameters() {
        searchType = getIntent().getStringExtra("searchType");
    }

    private void initComponents() {
        searchView = findViewById(R.id.searchView);
        tfSearch = findViewById(R.id.txtSearch);
        btnLoctheo = findViewById(R.id.locTheoView).findViewById(R.id.btnLoctheo);
        txtLoctheo = findViewById(R.id.locTheoView).findViewById(R.id.lblLoctheo);
        btnLoctheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopUp();
                updateUIFromFilterData();
                updateFilterLabel();
            }
        });
        btnMicro = findViewById(R.id.btnMicro);
        btnMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showSpeechRecognizerPopup();
                startSpeechRecognizer();
            }
        });
        int maxId = GeneralSettings.getMaxVanbanId();
        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                initFilterConfig();
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                vanbanState.put(GeneralSettings.getDefaultActiveNDXPId(), true);
                break;
            default:
                maxId = GeneralSettings.getMaxVanbanId();
                while (maxId > 0) {
                    if (GeneralSettings.getVanbanInfo(maxId, "shortname").length() > 0) {
                        vanbanState.put(maxId, true);
                    }
                    maxId--;
                }
                break;
        }

        updateVanbanIdList();
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
        allDieukhoan = search(keyword.trim());
        searchResultListRecyclerAdapter.updateView(allDieukhoan, this.searchKeyword);
    }

    private void updateResultListWithRelatedDieukhoan() {
        searchView.removeAllViews();
        helper.hideSection(searchView);
        String dieukhoanId = (String) getIntent().getStringExtra("dieukhoanId");
        allDieukhoan = new ArrayList<>();

        for (Dieukhoan dk : queries.getAllDirectRelatedDieukhoan(Integer.parseInt(dieukhoanId))) {
            allDieukhoan.add(dk);
        }
        for (Dieukhoan dk : queries.getAllRelativeRelatedDieukhoan(Integer.parseInt(dieukhoanId))) {
            allDieukhoan.add(dk);
        }
        searchResultListRecyclerAdapter.updateView(allDieukhoan);
    }

    private ArrayList<Dieukhoan> search(String keyword) {
        this.searchKeyword = keyword;
        if (keyword.length() > 0) {
            switch (searchType) {
                case GeneralSettings.SEARCH_TYPE_VANBAN:
                    return queries.searchDieukhoan(keyword, vanbanid);
                case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                    return queries.searchDieukhoanByQuery(getBuiltQuery(keyword), vanbanid);
                default:
                    return null;
            }

        } else {
            return queries.searchChildren(keyword, vanbanid);
        }
    }

    public void updateSearchBarText(String keyword) {
        tfSearch.setText(keyword);
    }

    private String getBuiltQuery(String keyword) {
        String query = Queries.getRawQuery();

        String appendString = "";
        for (String k : queries.convertKeywordsForDifferentAccentType(keyword.toLowerCase())) {
            String str = "";
            for (String key : k.split(" ")) {
                str += "dkSearch like '%" + key + "%' and ";
            }
            str = str.substring(0, str.length() - 5);
            appendString += "(" + str + ") or ";
        }
        appendString = "(" + appendString.substring(0, appendString.length() - 4) + ")";

        if (mucphat.get("tu") != null || mucphat.get("den") != null) {
            appendString += getWhereClauseForMucphat(mucphat.get("tu"), mucphat.get("den"));
        }

        if (phuongtien.size() > 0) {
            appendString += getWhereClauseForPhuongtien();
        }
        return query + appendString;
    }

    private String getWhereClauseForMucphat(String tu, String den) {
        int tuInt;
        int denInt;
        try {
            tuInt = Integer.parseInt(tu.replace(".", ""));
        } catch (Exception ex) {
            tuInt = Integer.parseInt(GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId())[0].replace(".", ""));
        }
        try {
            denInt = Integer.parseInt(den.replace(".", ""));
        } catch (Exception ex) {
            denInt = Integer.parseInt(GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId())[GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId()).length - 1].replace(".", ""));
        }
        String inClause = "";
        for (String item : GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId())) {
            int itemInt = Integer.parseInt(item.replace(".", ""));
            if (itemInt >= tuInt && itemInt <= denInt) {
                inClause += "\"" + item + "\",";
            }
        }
        inClause = inClause.substring(0, inClause.length() - 1);

        return " and dkId in (select distinct dieukhoanID from tblMucphat where canhanTu in (" + inClause + ") or canhanDen in (" + inClause + ") or tochucTu in (" + inClause + ") or tochucDen in (" + inClause + "))";
    }

    private String getWhereClauseForPhuongtien() {
        String inClause = "";
        for (String pt : phuongtien) {
            if (pt.equals(GeneralSettings.PHUONGTIEN_OTO)) {
                inClause += "oto = 1 or otoTai = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_XEMAY)) {
                inClause += "moto = 1 or xemaydien = 1 or xeganmay = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_XECHUYENDUNG)) {
                inClause += "maykeo = 1 or xechuyendung = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_TAUHOA)) {
                inClause += "tau = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_XEDAP)) {
                inClause += "xedapmay = 1 or xedapdien = 1 or xethoso = 1 or sucvat = 1 or xichlo = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_DIBO)) {
                inClause += "dibo = 1 or ";
            }
        }

        inClause = inClause.substring(0, inClause.length() - 4);

        return " and dkID in (select distinct dieukhoanID from tblPhuongtien where " + inClause + ")";
    }

    @SuppressLint("WrongViewCast")
    private void showFilterPopUp() {
        LayoutInflater layoutInflater = getLayoutInflater();

//        ViewGroup.LayoutParams hiddenSection;
        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                initVanbanFilters(layoutInflater);
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                initMucphatFilter(layoutInflater);
                break;
            default:

                break;
        }

        Button btnXong = (Button) customView.findViewById(R.id.btnXong);
        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                updateVanbanIdList();
                updateResultList(tfSearch.getText().toString());
            }
        });
        colorNormalBtnFg = btnXong.getCurrentTextColor();

        builder.setView(customView);
        builder.create();
        alert = builder.show();
    }

    private void showSpeechRecognizerPopup() {
        LayoutInflater layoutInflater = getLayoutInflater();
        customView = layoutInflater.inflate(R.layout.popup_speech_recognizer, null);

        Button btnXong = customView.findViewById(R.id.btnXong);
        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        colorNormalBtnFg = btnXong.getCurrentTextColor();

        builder.setView(customView);
        builder.create();
        alert = builder.show();
    }

    private void startSpeechRecognizer() {
        String languageToLoad  = "vi"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Locale defaultLocale = Locale.getDefault();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, defaultLocale);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói từ khoá bạn muốn tra cứu và bấm nút \"Xong\"");
        try {
            startActivityForResult(intent, REQ_CODE);
            Toast.makeText(getApplicationContext(), "Khởi tạo thành công", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Rất tiếc thiết bị của bạn không được hỗ trợ bởi Google :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void initVanbanFilters(LayoutInflater layoutInflater) {
        //this is custom dialog
        customView = layoutInflater.inflate(R.layout.popup_filter_vanban, null);
        lineLoutLoaivanban = customView.findViewById(R.id.Loaivanban);
        lineLoutLoaivanbanItem = lineLoutLoaivanban.findViewById(R.id.filterItem);
    }

    private void initMucphatFilter(LayoutInflater layoutInflater) {
        //this is custom dialog
        customView = layoutInflater.inflate(R.layout.popup_filter_mucphat, null);
        lineLoutPhuongtien = customView.findViewById(R.id.phuongtienLines);
        swtPhuongtien = customView.findViewById(R.id.phuongtienSectionToggleSwitch);
        swtPhuongtien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //reset all phuongtien buttons
                setButtonBackgroundColor(btnPhuongtienOto, false);
                setButtonBackgroundColor(btnPhuongtienTauhoa, false);
                setButtonBackgroundColor(btnPhuongtienXemay, false);
                setButtonBackgroundColor(btnPhuongtienXedap, false);
                setButtonBackgroundColor(btnPhuongtienXechuyendung, false);
                setButtonBackgroundColor(btnPhuongtienDibo, false);

                if (swtPhuongtien.isChecked()) {
                    lineLoutPhuongtien.setVisibility(View.VISIBLE);
                } else {
                    lineLoutPhuongtien.setVisibility(View.GONE);
                    phuongtien.clear();
                }
                updateFilterLabel();
            }
        });

        initPhuongtienFilterButtons();
        initMucphatFilterSpinner();

        if (phuongtien.size() > 0) {
            swtPhuongtien.setChecked(true);
            lineLoutPhuongtien.setVisibility(View.VISIBLE);
        } else {
            swtPhuongtien.setChecked(false);
            lineLoutPhuongtien.setVisibility(View.GONE);
        }

        lineLoutMucphatSelection = customView.findViewById(R.id.mucphatSelectionTuDen);
        swtMucphat = customView.findViewById(R.id.mucphatSectionToggleSwitch);
        swtMucphat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swtMucphat.isChecked()) {
                    lineLoutMucphatSelection.setVisibility(View.VISIBLE);
                } else {
                    lineLoutMucphatSelection.setVisibility(View.GONE);
                    mucphat.remove("tu");
                    mucphat.remove("den");
                    spMucphatDen.setSelection(0);
                    spMucphatTu.setSelection(0);
                }
                updateFilterLabel();
            }
        });

        if (mucphat.isEmpty()) {
            lineLoutMucphatSelection.setVisibility(View.GONE);
            swtMucphat.setChecked(false);
        } else {
            lineLoutMucphatSelection.setVisibility(View.VISIBLE);
            swtMucphat.setChecked(true);
        }
    }

    private void initPhuongtienFilterButtons() {
        btnPhuongtienOto = (Button) customView.findViewById(R.id.btnPhuongtienOto);
        btnPhuongtienOto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienOto);
            }
        });
        btnPhuongtienXemay = (Button) customView.findViewById(R.id.btnPhuongtienXemay);
        btnPhuongtienXemay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienXemay);
            }
        });
        btnPhuongtienXedap = (Button) customView.findViewById(R.id.btnPhuongtienXedap);
        btnPhuongtienXedap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienXedap);
            }
        });
        btnPhuongtienXechuyendung = (Button) customView.findViewById(R.id.btnPhuongtienXechuyendung);
        btnPhuongtienXechuyendung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienXechuyendung);
            }
        });
        btnPhuongtienTauhoa = (Button) customView.findViewById(R.id.btnPhuongtienTauhoa);
        btnPhuongtienTauhoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienTauhoa);
            }
        });
        btnPhuongtienDibo = (Button) customView.findViewById(R.id.btnPhuongtienDibo);
        btnPhuongtienDibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienDibo);
            }
        });
    }

    private void initMucphatFilterSpinner() {
        String[] initMucphatRange = {"-----"};
        // Initialize an empty list
        ArrayList<String> both = new ArrayList<>();

        // Add first array elements to list
        Collections.addAll(both, initMucphatRange);

        // Add another array elements to list
        Collections.addAll(both, GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId()));

        // Convert list to array
        String[] result = both.toArray(new String[both.size()]);

        spMucphatTu = (Spinner) customView.findViewById(R.id.mucphatTuPicker);
        spMucphatDen = (Spinner) customView.findViewById(R.id.mucphatDenPicker);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, result);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spMucphatTu.setAdapter(adapter);
        spMucphatTu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMucphatFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateMucphatFilters();
            }
        });
        spMucphatDen.setAdapter(adapter);
        spMucphatDen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMucphatFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateMucphatFilters();
            }
        });
    }

    private void updatePhuongtienFilters(Button button) {
        if (helper.getButtonBackgroundColor(button) == colorNormalBtnBg) {
            setButtonBackgroundColor(button, true);
            addPhuongtienToList(button);
        } else {
            setButtonBackgroundColor(button, false);
            removePhuongtienFromList(button);
        }
    }

    private void addPhuongtienToList(Button button) {
        String buttonText = button.getText().toString();

        if (buttonText.equals(getResources().getString(R.string.oto))) {
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_OTO.equals(pt)) {
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_OTO);
        }
        if (buttonText.equals(getResources().getString(R.string.xemay_moto))) {
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_XEMAY.equals(pt)) {
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_XEMAY);
        }
        if (buttonText.equals(getResources().getString(R.string.xedap_xethoso))) {
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_XEDAP.equals(pt)) {
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_XEDAP);
        }
        if (buttonText.equals(getResources().getString(R.string.xechuyendung))) {
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_XECHUYENDUNG.equals(pt)) {
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_XECHUYENDUNG);
        }
        if (buttonText.equals(getResources().getString(R.string.tauhoa))) {
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_TAUHOA.equals(pt)) {
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_TAUHOA);
        }
        if (buttonText.equals(getResources().getString(R.string.dibo))) {
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_DIBO.equals(pt)) {
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_DIBO);
        }
        updateFilterLabel();
    }

    private void removePhuongtienFromList(Button button) {
        String buttonText = button.getText().toString();

        if (buttonText.equals(getResources().getString(R.string.oto))) {
            buttonText = GeneralSettings.PHUONGTIEN_OTO;
        }
        if (buttonText.equals(getResources().getString(R.string.xemay_moto))) {
            buttonText = GeneralSettings.PHUONGTIEN_XEMAY;
        }
        if (buttonText.equals(getResources().getString(R.string.xedap_xethoso))) {
            buttonText = GeneralSettings.PHUONGTIEN_XEDAP;
        }
        if (buttonText.equals(getResources().getString(R.string.xechuyendung))) {
            buttonText = GeneralSettings.PHUONGTIEN_XECHUYENDUNG;
        }
        if (buttonText.equals(getResources().getString(R.string.tauhoa))) {
            buttonText = GeneralSettings.PHUONGTIEN_TAUHOA;
        }
        if (buttonText.equals(getResources().getString(R.string.dibo))) {
            buttonText = GeneralSettings.PHUONGTIEN_DIBO;
        }

        if (phuongtien.size() > 0) {
            int idx = 0;
            boolean isFound = false;
            for (String pt : phuongtien) {
                if (pt.equals(buttonText)) {
                    isFound = true;
                    break;
                }
                idx++;
            }
            if (isFound) {
                phuongtien.remove(idx);
            }
        }
        updateFilterLabel();
    }

    private void updateMucphatFilters() {
        int tu = -1;
        int den = -1;
        try {
            mucphat.put("tu", GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId())[spMucphatTu.getSelectedItemPosition() - 1]);
            tu = Integer.parseInt(mucphat.get("tu").replace(".", ""));
        } catch (Exception ex) {
            Log.i(TAG, "mucphat.get('tu') is not valid");
        }
        try {
            mucphat.put("den", GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId())[spMucphatDen.getSelectedItemPosition() - 1]);
            den = Integer.parseInt(mucphat.get("den").replace(".", ""));
        } catch (Exception ex) {
            Log.i(TAG, "mucphat.get('den') is not valid");
        }

        if (tu >= 0 && den >= 0) {
            if (tu > den) {
                String temp = mucphat.get("tu");
                mucphat.put("tu", mucphat.get("den"));
                mucphat.put("den", temp);
                int tuPos = spMucphatTu.getSelectedItemPosition();
                spMucphatTu.setSelection(spMucphatDen.getSelectedItemPosition());
                spMucphatDen.setSelection(tuPos);
            }
        }
        updateFilterLabel();
    }

    private void updateVanbanIdList() {
        vanbanid = new ArrayList<>();
        for (Integer id : vanbanState.keySet()) {
            if (vanbanState.get(id)) {
                vanbanid.add(id + "");
            }
        }
        updateFilterLabel();
    }

    private void updateFilterLabel() {
        String loctheo = "";
        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                for (String vbID : vanbanid) {
                    loctheo += GeneralSettings.getVanbanInfo(Integer.parseInt(vbID), "shortname") + ", ";
                }
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                for (String pt : phuongtien) {
                    loctheo += pt + ", ";
                }
                if (mucphat.get("tu") != null && mucphat.get("den") != null) {
                    loctheo += "từ " + mucphat.get("tu") + " đến " + mucphat.get("den") + ", ";
                } else {
                    if (mucphat.get("tu") != null) {
                        loctheo += "trên " + mucphat.get("tu") + ", ";
                    }
                    if (mucphat.get("den") != null) {
                        loctheo += "dưới " + mucphat.get("den") + ", ";
                    }
                }

                break;
            default:
                break;
        }

        if (loctheo.length() > 2) {
            txtLoctheo.setText(loctheo.substring(0, loctheo.length() - 2));
        } else {
            txtLoctheo.setText(loctheo);
        }
    }

    private void updateUIFromFilterData() {
        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                updateSwitches();
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                for (String pt : phuongtien) {
                    switch (pt) {
                        case GeneralSettings.PHUONGTIEN_DIBO:
                            setButtonBackgroundColor(btnPhuongtienDibo, true);
                            break;
                        case GeneralSettings.PHUONGTIEN_XECHUYENDUNG:
                            setButtonBackgroundColor(btnPhuongtienXechuyendung, true);
                            break;
                        case GeneralSettings.PHUONGTIEN_XEDAP:
                            setButtonBackgroundColor(btnPhuongtienXedap, true);
                            break;
                        case GeneralSettings.PHUONGTIEN_XEMAY:
                            setButtonBackgroundColor(btnPhuongtienXemay, true);
                            break;
                        case GeneralSettings.PHUONGTIEN_TAUHOA:
                            setButtonBackgroundColor(btnPhuongtienTauhoa, true);
                            break;
                        case GeneralSettings.PHUONGTIEN_OTO:
                            setButtonBackgroundColor(btnPhuongtienOto, true);
                            break;
                        default:
                            break;
                    }
                }
                if (mucphat.get("tu") != null) {
                    for (int i = 0; i < GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId()).length; i++) {
                        if (mucphat.get("tu").equals(GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId())[i])) {
                            spMucphatTu.setSelection(i + 1);
                        }
                    }
                }
                if (mucphat.get("den") != null) {
                    for (int i = 0; i < GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId()).length; i++) {
                        if (mucphat.get("den").equals(GeneralSettings.getMucphatRangePerVanban(GeneralSettings.getDefaultActiveNDXPId())[i])) {
                            spMucphatDen.setSelection(i + 1);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setButtonBackgroundColor(Button button, boolean isActive) {
        if (isActive) {
            button.setBackgroundColor(colorSelectedBtnBg);
            button.setTextColor(getResources().getColor(R.color.white));
        } else {
            button.setBackgroundColor(colorNormalBtnBg);
            button.setTextColor(colorNormalBtnFg);
        }
    }

    private LinearLayout generateFilterSwitchItem(int id, String shortname, String fullname, boolean isOn) {
        LinearLayout wrapperView = new LinearLayout(this);
        wrapperView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT));
        wrapperView.setOrientation(LinearLayout.HORIZONTAL);
        wrapperView.setPadding(2, 2, 2, 2);

        LinearLayout wrapperTitleView = new LinearLayout(this);
        wrapperTitleView.setLayoutParams(new LinearLayout.LayoutParams(0
                , LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        wrapperTitleView.setOrientation(LinearLayout.VERTICAL);
        TextView lblVanbanShortname = new TextView(this);
        lblVanbanShortname.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT));
        lblVanbanShortname.setText(shortname);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lblVanbanShortname.setTextAppearance(R.style.textTitle);
        } else {
            lblVanbanShortname.setTextAppearance(this, R.style.textTitle);
        }

        TextView lblVanbanFullname = new TextView(this);
        lblVanbanFullname.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT));
        lblVanbanFullname.setText(fullname);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lblVanbanFullname.setTextAppearance(R.style.textSubtitle);
        } else {
            lblVanbanFullname.setTextAppearance(this, R.style.textSubtitle);
        }
        wrapperTitleView.addView(lblVanbanShortname);
        wrapperTitleView.addView(lblVanbanFullname);

        final Switch swt = new Switch(this);
        swt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.WRAP_CONTENT));
        swt.setGravity(Gravity.RIGHT);
        swt.setChecked(isOn);
        swt.setTag(id);
        swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (swt.isChecked()) {
                    vanbanState.put((Integer) swt.getTag(), true);
                } else {
                    vanbanState.put((Integer) swt.getTag(), false);
                }
            }
        });

        wrapperView.addView(wrapperTitleView);
        wrapperView.addView(swt);

        return wrapperView;
    }

    private void updateSwitches() {
        ArrayList<LinearLayout> selectedVanban = new ArrayList<>();
        ArrayList<LinearLayout> notSelectedVanban = new ArrayList<>();

        int id = GeneralSettings.getMaxVanbanId();
        while (id > 0) {
            if (vanbanState.get(id)) {
                selectedVanban.add(generateFilterSwitchItem(id, GeneralSettings.getVanbanInfo(id, "shortname"), GeneralSettings.getVanbanInfo(id, "fullname"), true));
            } else {
                notSelectedVanban.add(generateFilterSwitchItem(id, GeneralSettings.getVanbanInfo(id, "shortname"), GeneralSettings.getVanbanInfo(id, "fullname"), false));
            }
            id--;
        }
        ArrayList<LinearLayout> allVanban = new ArrayList<>();
        allVanban.addAll(selectedVanban);
        allVanban.addAll(notSelectedVanban);

        for (LinearLayout item : allVanban) {
            lineLoutLoaivanbanItem.addView(item);
        }
    }

    private void initFilterConfig() {
        if (vanbanState.size() < 1) {
            //iterate all Vanban to get active ones
            int id = GeneralSettings.getMaxVanbanId();
            while (id > 0) {
                String valid = GeneralSettings.getVanbanInfo(id, "valid");
                if (valid.length() > 0) {
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("MM/dd/yyyy").parse(valid);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (new Date().after(date) || new Date().equals(date)) {
                        vanbanState.put(id, true);
//                        addVanbanidToList(id + "");
                    } else {
                        vanbanState.put(id, false);
                    }
                }
                id--;
            }
            //iterate active Vanban to deactivate replaced ones
            //TO DO: multiple replacement
            for (int vbId : vanbanState.keySet()) {
                String replaceId = GeneralSettings.getVanbanInfo(vbId, "replace");
                Boolean existed = vanbanState.get(Integer.parseInt(replaceId));
                if (existed != null) {
                    if (vanbanState.get(vbId) && existed) {
                        vanbanState.put(Integer.parseInt(GeneralSettings.getVanbanInfo(vbId, "replace")), false);
                    }
                }
            }
        }
    }
}
