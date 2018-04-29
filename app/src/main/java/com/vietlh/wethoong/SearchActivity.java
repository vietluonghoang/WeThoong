package com.vietlh.wethoong;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.vietlh.wethoong.adapters.ListRecyclerViewAdapter;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
    private LinearLayout adsView;
    private ArrayList<String> vanbanid = new ArrayList<>();
    private ArrayList<String> phuongtien = new ArrayList<>();
    private HashMap<String,String> mucphat = new HashMap<>();
    private String searchType;
    private UtilsHelper helper = new UtilsHelper();
    private AdsHelper adsHelper = new AdsHelper();

    //Filter popup elements
    private AlertDialog.Builder builder;
    AlertDialog alert = null;
    private View customView;
    private LinearLayout lineLoutLoaivanban;
    private LinearLayout lineLoutMucphat;
    private CheckBox cbQC41;
    private CheckBox cbND46;
    private CheckBox cbTT01;
    private CheckBox cbLGTDB;
    private CheckBox cbLXLVPHC;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getPassingParameters();
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
        searchResultListRecyclerAdapter = new ListRecyclerViewAdapter(this,allDieukhoan);
        if (!searchType.equals("lienquan")) {
            updateResultList("");
        }else {
            updateResultListWithRelatedDieukhoan();
        }
        searchResultRecyclerView.setAdapter(searchResultListRecyclerAdapter);

        //workaround to make searchResultRecyclerView to match parent width
        ViewGroup.LayoutParams searchResultLayoutParams = searchResultRecyclerView.getLayoutParams();
        searchResultLayoutParams.width = helper.getScreenWidth();
        searchResultRecyclerView.setLayoutParams(searchResultLayoutParams);

        if(GeneralSettings.isAdsEnabled) {
            initAds();
        }
    }

    private void initAds(){
        adsView = (LinearLayout) findViewById(R.id.adsView);
        adsHelper.updateLastConnectionState();
        if (GeneralSettings.wasConnectedToInternet) {
            AdView googleAdView = new AdView(this);
            adsHelper.addBannerViewtoView(googleAdView, adsView);
            AdRequest adRequest = new AdRequest.Builder().build();
            googleAdView.loadAd(adRequest);
        }else {
            Button btnFBBanner = new Button(this);
            btnFBBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helper.openUrlInExternalBrowser(getApplicationContext(),getResources().getString(R.string.wethoongFB));
                }
            });
            btnFBBanner.setBackgroundResource(R.drawable.facebook_banner_wethoong);
            adsHelper.addButtonToView(btnFBBanner,adsView);
        }
    }

    private void getPassingParameters(){
        searchType = (String) getIntent().getStringExtra("searchType");
    }

    private void initComponents(){
        searchView = (LinearLayout) findViewById(R.id.searchView);
        tfSearch = (EditText)findViewById(R.id.txtSearch);
        btnLoctheo = (Button)((LinearLayout)findViewById(R.id.locTheoView)).findViewById(R.id.btnLoctheo);
        txtLoctheo = (TextView)((LinearLayout)findViewById(R.id.locTheoView)).findViewById(R.id.lblLoctheo);
        btnLoctheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopUp();
                updateUIFromFilterData();
                updateFilterLabel();
            }
        });
        switch (searchType){
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                for (String key : GeneralSettings.danhsachvanban) {
                    addVanbanidToList(key);
                }
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                addVanbanidToList(GeneralSettings.danhsachvanban[0]);
                break;
            default:
                for (String key : GeneralSettings.danhsachvanban) {
                    addVanbanidToList(key);
                }
                break;
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

    private void updateResultList(String keyword){
        allDieukhoan = search(keyword.trim());
        searchResultListRecyclerAdapter.updateView(allDieukhoan);
    }

    private void updateResultListWithRelatedDieukhoan(){
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

    private ArrayList<Dieukhoan> search(String keyword){
        if (keyword.length() > 0) {
            switch (searchType){
                case GeneralSettings.SEARCH_TYPE_VANBAN:
                    return queries.searchDieukhoan(keyword, vanbanid);
                case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                    return queries.searchDieukhoanByQuery(getBuiltQuery(keyword),vanbanid);
                default:
                    return null;
            }

        } else {
            return queries.searchChildren(keyword, vanbanid);
        }
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
        appendString = "(" + appendString.substring(0,appendString.length() - 4) + ")";

        if (mucphat.get("tu") != null || mucphat.get("den") != null) {
            appendString += getWhereClauseForMucphat(mucphat.get("tu"),mucphat.get("den"));
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
        }catch (Exception ex){
            tuInt = Integer.parseInt(GeneralSettings.mucphatRange[0].replace(".", ""));
        }
        try{
            denInt = Integer.parseInt(den.replace(".", ""));
        }catch (Exception ex){
            denInt = Integer.parseInt(GeneralSettings.mucphatRange[GeneralSettings.mucphatRange.length - 1].replace(".", ""));
        }
        String inClause = "";
        for (String item : GeneralSettings.mucphatRange) {
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

            if (pt.equals(GeneralSettings.PHUONGTIEN_XEMAY))
            {
                inClause += "moto = 1 or xemaydien = 1 or xeganmay = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_XECHUYENDUNG))
            {
                inClause += "maykeo = 1 or xechuyendung = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_TAUHOA))
            {
                inClause += "tau = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_XEDAP))
            {
                inClause += "xedapmay = 1 or xedapdien = 1 or xethoso = 1 or sucvat = 1 or xichlo = 1 or ";
            }

            if (pt.equals(GeneralSettings.PHUONGTIEN_DIBO)) {
                inClause += "dibo = 1 or ";
            }
        }

        inClause = inClause.substring(0, inClause.length() - 4);

        return " and dkID in (select distinct dieukhoanID from tblPhuongtien where " + inClause + ")";
    }

    private void showFilterPopUp() {
        LayoutInflater layoutInflater = getLayoutInflater();

        //this is custom dialog
        customView = layoutInflater.inflate(R.layout.popup_filters, null);

        lineLoutLoaivanban = (LinearLayout)customView.findViewById(R.id.Loaivanban);
        lineLoutMucphat = (LinearLayout)customView.findViewById(R.id.mucphatSection);
//        ViewGroup.LayoutParams hiddenSection;
        switch (searchType){
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                lineLoutMucphat.setVisibility(View.INVISIBLE);
                lineLoutLoaivanban.setVisibility(View.VISIBLE);
                helper.hideSection(lineLoutMucphat);

                initVanbanFilters();

                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                lineLoutMucphat.setVisibility(View.VISIBLE);
                lineLoutLoaivanban.setVisibility(View.INVISIBLE);
                helper.hideSection(lineLoutLoaivanban);

                lineLoutPhuongtien = (LinearLayout)customView.findViewById(R.id.phuongtienLines);
                swtPhuongtien = (Switch)customView.findViewById(R.id.phuongtienSectionToggleSwitch);
                swtPhuongtien.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (swtPhuongtien.isChecked()){
                            lineLoutPhuongtien.setVisibility(View.VISIBLE);
                        }else {
                            lineLoutPhuongtien.setVisibility(View.GONE);

                            //reset all phuongtien buttons
                            setButtonBackgroundColor(btnPhuongtienOto,false);
                            setButtonBackgroundColor(btnPhuongtienTauhoa,false);
                            setButtonBackgroundColor(btnPhuongtienXemay,false);
                            setButtonBackgroundColor(btnPhuongtienXedap,false);
                            setButtonBackgroundColor(btnPhuongtienXechuyendung,false);
                            setButtonBackgroundColor(btnPhuongtienDibo,false);
                            phuongtien.clear();
                        }
                        updateFilterLabel();
                    }
                });

                initPhuongtienFilters();
                initMucphatFilter();

                if (phuongtien.size() > 0){
                    swtPhuongtien.setChecked(true);
                    lineLoutPhuongtien.setVisibility(View.VISIBLE);
                }else {
                    swtPhuongtien.setChecked(false);
                    lineLoutPhuongtien.setVisibility(View.GONE);
                }

                lineLoutMucphatSelection = (LinearLayout)customView.findViewById(R.id.mucphatSelectionTuDen);
                swtMucphat = (Switch)customView.findViewById(R.id.mucphatSectionToggleSwitch);
                swtMucphat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (swtMucphat.isChecked()){
                            lineLoutMucphatSelection.setVisibility(View.VISIBLE);
                        }else {
                            lineLoutMucphatSelection.setVisibility(View.GONE);
                            mucphat.remove("tu");
                            mucphat.remove("den");
                            spMucphatDen.setSelection(0);
                            spMucphatTu.setSelection(0);
                        }
                        updateFilterLabel();
                    }
                });

                if (mucphat.isEmpty()){
                    lineLoutMucphatSelection.setVisibility(View.GONE);
                    swtMucphat.setChecked(false);
                }else {
                    lineLoutMucphatSelection.setVisibility(View.VISIBLE);
                    swtMucphat.setChecked(true);
                }

                break;
            default:

                break;
        }

        Button btnXong = (Button)customView.findViewById(R.id.btnXong);
        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                updateResultList(tfSearch.getText().toString());
            }
        });
        colorNormalBtnBg = helper.getButtonBackgroundColor(btnXong);
        colorNormalBtnFg = btnXong.getCurrentTextColor();

        builder.setView(customView);
        builder.create();
        alert = builder.show();
    }

    private void initVanbanFilters(){
        cbQC41 = (CheckBox)customView.findViewById(R.id.optionQC41Checkbox);
        cbQC41.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbQC41.isChecked()){
                    addVanbanidToList(GeneralSettings.danhsachvanban[1]);
                }else {
                    removeVanbanidFromList(GeneralSettings.danhsachvanban[1]);
                }
            }
        });
        cbND46 = (CheckBox)customView.findViewById(R.id.optionND46Checkbox);
        cbND46.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbND46.isChecked()){
                    addVanbanidToList(GeneralSettings.danhsachvanban[0]);
                }else {
                    removeVanbanidFromList(GeneralSettings.danhsachvanban[0]);
                }
            }
        });
        cbTT01 = (CheckBox)customView.findViewById(R.id.optionTT01Checkbox);
        cbTT01.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbTT01.isChecked()){
                    addVanbanidToList(GeneralSettings.danhsachvanban[2]);
                }else {
                    removeVanbanidFromList(GeneralSettings.danhsachvanban[2]);
                }
            }
        });
        cbLGTDB = (CheckBox)customView.findViewById(R.id.optionLGTCheckbox);
        cbLGTDB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbLGTDB.isChecked()){
                    addVanbanidToList(GeneralSettings.danhsachvanban[3]);
                }else {
                    removeVanbanidFromList(GeneralSettings.danhsachvanban[3]);
                }
            }
        });
        cbLXLVPHC = (CheckBox)customView.findViewById(R.id.optionLXLVPHCCheckbox);
        cbLXLVPHC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbLXLVPHC.isChecked()){
                    addVanbanidToList(GeneralSettings.danhsachvanban[4]);
                }else {
                    removeVanbanidFromList(GeneralSettings.danhsachvanban[4]);
                }
            }
        });
    }

    private void initPhuongtienFilters(){
        btnPhuongtienOto = (Button)customView.findViewById(R.id.btnPhuongtienOto);
        btnPhuongtienOto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienOto);
            }
        });
        btnPhuongtienXemay = (Button)customView.findViewById(R.id.btnPhuongtienXemay);
        btnPhuongtienXemay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienXemay);
            }
        });
        btnPhuongtienXedap = (Button)customView.findViewById(R.id.btnPhuongtienXedap);
        btnPhuongtienXedap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienXedap);
            }
        });
        btnPhuongtienXechuyendung = (Button)customView.findViewById(R.id.btnPhuongtienXechuyendung);
        btnPhuongtienXechuyendung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienXechuyendung);
            }
        });
        btnPhuongtienTauhoa = (Button)customView.findViewById(R.id.btnPhuongtienTauhoa);
        btnPhuongtienTauhoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienTauhoa);
            }
        });
        btnPhuongtienDibo = (Button)customView.findViewById(R.id.btnPhuongtienDibo);
        btnPhuongtienDibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhuongtienFilters(btnPhuongtienDibo);
            }
        });
    }

    private void initMucphatFilter(){
        String[] initMucphatRange = {"-----"};
        // Initialize an empty list
        ArrayList<String> both = new ArrayList<>();

        // Add first array elements to list
        Collections.addAll(both,initMucphatRange);

        // Add another array elements to list
        Collections.addAll(both,GeneralSettings.mucphatRange);

        // Convert list to array
        String[] result = both.toArray(new String[both.size()]);

        spMucphatTu = (Spinner) customView.findViewById(R.id.mucphatTuPicker);
        spMucphatDen = (Spinner) customView.findViewById(R.id.mucphatDenPicker);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,result);

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

    private void updatePhuongtienFilters(Button button){
        if (helper.getButtonBackgroundColor(button) == colorNormalBtnBg){
            setButtonBackgroundColor(button,true);
            addPhuongtienToList(button);
        }else {
            setButtonBackgroundColor(button,false);
            removePhuongtienFromList(button);
        }
    }

    private void addPhuongtienToList(Button button){
        String buttonText = button.getText().toString();

        if(buttonText.equals(getResources().getString(R.string.oto))){
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_OTO.equals(pt)){
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_OTO);
        }
        if(buttonText.equals(getResources().getString(R.string.xemay_moto))){
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_XEMAY.equals(pt)){
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_XEMAY);
        }
        if(buttonText.equals(getResources().getString(R.string.xedap_xethoso))){
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_XEDAP.equals(pt)){
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_XEDAP);
        }
        if(buttonText.equals(getResources().getString(R.string.xechuyendung))){
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_XECHUYENDUNG.equals(pt)){
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_XECHUYENDUNG);
        }
        if(buttonText.equals(getResources().getString(R.string.tauhoa))){
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_TAUHOA.equals(pt)){
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_TAUHOA);
        }
        if(buttonText.equals(getResources().getString(R.string.dibo))){
            for (String pt : phuongtien) {
                if (GeneralSettings.PHUONGTIEN_DIBO.equals(pt)){
                    return;
                }
            }
            phuongtien.add(GeneralSettings.PHUONGTIEN_DIBO);
        }
        updateFilterLabel();
    }

    private void removePhuongtienFromList(Button button) {
        String buttonText = button.getText().toString();

        if(buttonText.equals(getResources().getString(R.string.oto))){
            buttonText = GeneralSettings.PHUONGTIEN_OTO;
        }
        if(buttonText.equals(getResources().getString(R.string.xemay_moto))){
            buttonText = GeneralSettings.PHUONGTIEN_XEMAY;
        }
        if(buttonText.equals(getResources().getString(R.string.xedap_xethoso))){
            buttonText = GeneralSettings.PHUONGTIEN_XEDAP;
        }
        if(buttonText.equals(getResources().getString(R.string.xechuyendung))){
            buttonText = GeneralSettings.PHUONGTIEN_XECHUYENDUNG;
        }
        if(buttonText.equals(getResources().getString(R.string.tauhoa))){
            buttonText = GeneralSettings.PHUONGTIEN_TAUHOA;
        }
        if(buttonText.equals(getResources().getString(R.string.dibo))){
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

    private void updateMucphatFilters(){
        int tu = -1;
        int den = -1;
        try {
            mucphat.put("tu",GeneralSettings.mucphatRange[spMucphatTu.getSelectedItemPosition() - 1]);
            tu = Integer.parseInt(mucphat.get("tu").replace(".", ""));
        }catch (Exception ex){
            Log.i(TAG,"mucphat.get('tu') is not valid");
        }
        try{
            mucphat.put("den",GeneralSettings.mucphatRange[spMucphatDen.getSelectedItemPosition() - 1]);
            den = Integer.parseInt(mucphat.get("den").replace(".", ""));
        }catch (Exception ex){
            Log.i(TAG,"mucphat.get('den') is not valid");
        }

        if (tu >= 0 && den >= 0){
            if (tu > den){
                String temp = mucphat.get("tu");
                mucphat.put("tu",mucphat.get("den"));
                mucphat.put("den",temp);
                int tuPos = spMucphatTu.getSelectedItemPosition();
                spMucphatTu.setSelection(spMucphatDen.getSelectedItemPosition());
                spMucphatDen.setSelection(tuPos);
            }
        }
        updateFilterLabel();
    }

    private void addVanbanidToList(String vanbanKey){
        String vbID = GeneralSettings.getVanbanInfo(vanbanKey,"id");
        for (String id : vanbanid) {
            if (vbID.equals(id)){
                return;
            }
        }
        vanbanid.add(vbID);
        updateFilterLabel();
    }

    private void removeVanbanidFromList(String vanbanKey){
        if (vanbanid.size() > 0) {
            String vbID = GeneralSettings.getVanbanInfo(vanbanKey, "id");
            int idx = 0;
            boolean isFound = false;
            for (String id : vanbanid) {
                if (vbID.equals(id)) {
                    isFound = true;
                    break;
                }
                idx++;
            }
            if (isFound) {
                vanbanid.remove(idx);
            }
        }
        updateFilterLabel();
    }

    private void updateFilterLabel(){
        String loctheo = "";
        switch (searchType){
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                for (String vbID : vanbanid) {
                    for (String vbKey :
                            GeneralSettings.danhsachvanban) {
                        if (vbID.equals(GeneralSettings.getVanbanInfo(vbKey,"id"))){
                            loctheo += GeneralSettings.getVanbanInfo(vbKey,"fullName") + ", ";
                        }
                    }
                }
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                for (String pt : phuongtien) {
                    loctheo += pt + ", ";
                }
                if (mucphat.get("tu") != null && mucphat.get("den") != null){
                    loctheo += "từ " + mucphat.get("tu") + " đến " + mucphat.get("den") + ", ";
                }else {
                    if (mucphat.get("tu") != null){
                        loctheo += "trên " + mucphat.get("tu") + ", ";
                    }
                    if (mucphat.get("den") != null){
                        loctheo += "dưới " + mucphat.get("den") + ", ";
                    }
                }

                break;
            default:
                break;
        }

        if (loctheo.length() > 2) {
            txtLoctheo.setText(loctheo.substring(0, loctheo.length() - 2));
        }else {
            txtLoctheo.setText(loctheo);
        }
    }

    private void updateUIFromFilterData(){
        switch (searchType){
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                ArrayList<String> notListedVanban = new ArrayList<>();
                for (String vbk : GeneralSettings.danhsachvanban) {
                    if (vanbanid.contains(GeneralSettings.getVanbanInfo(vbk, "id"))) {
                        if (vbk.equals(GeneralSettings.danhsachvanban[0])){
                            cbND46.setChecked(true);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[1])){
                            cbQC41.setChecked(true);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[2])){
                            cbTT01.setChecked(true);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[3])){
                            cbLGTDB.setChecked(true);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[4])){
                            cbLXLVPHC.setChecked(true);
                        }
                    }else {
                        if (vbk.equals(GeneralSettings.danhsachvanban[0])){
                            cbND46.setChecked(false);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[1])){
                            cbQC41.setChecked(false);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[2])){
                            cbTT01.setChecked(false);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[3])){
                            cbLGTDB.setChecked(false);
                        }
                        if (vbk.equals(GeneralSettings.danhsachvanban[4])){
                            cbLXLVPHC.setChecked(false);
                        }
                    }
                }
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                for (String pt : phuongtien) {
                    switch (pt){
                        case GeneralSettings.PHUONGTIEN_DIBO:
                            setButtonBackgroundColor(btnPhuongtienDibo,true);
                            break;
                        case GeneralSettings.PHUONGTIEN_XECHUYENDUNG:
                            setButtonBackgroundColor(btnPhuongtienXechuyendung,true);
                            break;
                        case GeneralSettings.PHUONGTIEN_XEDAP:
                            setButtonBackgroundColor(btnPhuongtienXedap,true);
                            break;
                        case GeneralSettings.PHUONGTIEN_XEMAY:
                            setButtonBackgroundColor(btnPhuongtienXemay,true);
                            break;
                        case GeneralSettings.PHUONGTIEN_TAUHOA:
                            setButtonBackgroundColor(btnPhuongtienTauhoa,true);
                            break;
                        case GeneralSettings.PHUONGTIEN_OTO:
                            setButtonBackgroundColor(btnPhuongtienOto,true);
                            break;
                        default:
                            break;
                    }
                }
                if (mucphat.get("tu") != null){
                    for (int i = 0;i < GeneralSettings.mucphatRange.length;i++){
                        if (mucphat.get("tu").equals(GeneralSettings.mucphatRange[i])){
                            spMucphatTu.setSelection(i + 1);
                        }
                    }
                }
                if (mucphat.get("den") != null){
                    for (int i = 0;i < GeneralSettings.mucphatRange.length;i++){
                        if (mucphat.get("den").equals(GeneralSettings.mucphatRange[i])){
                            spMucphatDen.setSelection(i + 1);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setButtonBackgroundColor(Button button, boolean isActive){
        if (isActive){
            button.setBackgroundColor(getResources().getColor(R.color.blue));
            button.setTextColor(getResources().getColor(R.color.white));
        }else {
            button.setBackgroundColor(colorNormalBtnBg);
            button.setTextColor(colorNormalBtnFg);
        }
    }
}
