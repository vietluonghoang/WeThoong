package com.vietlh.wethoong;

import android.graphics.Bitmap;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BienbaoActivity extends AppCompatActivity {

    private static final String TAG = "BienbaoActivity";
    private RecyclerView searchResultRecyclerView;
    private ListRecyclerViewAdapter searchResultListRecyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Dieukhoan> allDieukhoan;
    private HashMap<String, Boolean> selectedPlateShapeGroups = new HashMap<>();
    private HashMap<String, Boolean> selectedPlateShapes = new HashMap<>();
    private HashMap<String, Boolean> selectedPlateDetails = new HashMap<>();
    private HashMap<String, String> shapeGroupNamePair = new HashMap() {{
        put("Circle", "Hình tròn");
        put("Rectangle", "Hình chữ nhật");
        put("Arrow", "Hình mũi tên");
        put("Octagon", "Hình bát giác");
        put("Triangle", "Hình tam giác");
        put("Square", "Hình vuông");
        put("Rhombus", "Hình quả trám");
        put("Xshape", "Hình chữ X");
    }};
    private Queries queries = new Queries(DBConnection.getInstance(this));
    private Button btnLoctheo;
    private TextView txtLoctheo;
    private ConstraintLayout lineLoutPlateDetailsSelect;
    private HorizontalScrollView scvPlateShapes;
    private LinearLayout lineLoutPlateShapeItems;
    private ConstraintLayout cloutArrowView;
    private ImageButton btnPlateDetailsArrow;
    private ConstraintLayout cloutCreaturesView;
    private ImageButton btnPlateDetailsCreatures;
    private ConstraintLayout cloutSignsView;
    private ImageButton btnPlateDetailsSigns;
    private ConstraintLayout cloutFiguresView;
    private ImageButton btnPlateDetailsFigures;
    private ConstraintLayout cloutAlphanumericsView;
    private ImageButton btnPlateDetailsAlphanumerics;
    private ConstraintLayout cloutVehiclesView;
    private ImageButton btnPlateDetailsVehicles;
    private ConstraintLayout cloutStructuresView;
    private ImageButton btnPlateDetailsStructures;
    private ConstraintLayout cloutExtrasView;
    private ImageButton btnPlateDetailsExtras;
    private LinearLayout adsView;
    private ArrayList<String> vanbanid = new ArrayList<>();
    private String searchType;
    private UtilsHelper helper = new UtilsHelper();
    private AdsHelper adsHelper = new AdsHelper();
    private RedirectionHelper redirectionHelper = new RedirectionHelper();

    //Filter popup elements
    private AlertDialog.Builder builder;
    AlertDialog alert = null;
    private View customView;
    private LinearLayout lineLoutPlateShapeGroupsSelection;
    private LinearLayout lineLoutVachShapeGroupsSelection;
    private Switch cbPlateShapeRectangle;
    private Switch cbPlateShapeTriangle;
    private Switch cbPlateShapeCircle;
    private Switch cbPlateShapeSquare;
    private Switch cbPlateShapeXshape;
    private Switch cbPlateShapeOctagon;
    private Switch cbPlateShapeRhombic;
    private Switch cbPlateShapeArrow;
    private int colorNormalBtnBg;
    private int colorNormalBtnFg;
    private int colorSelectedBtnBg;
    private int colorSelectedBtnFg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienbao);
        getPassingParameters();
        colorNormalBtnBg = getResources().getColor(R.color.normalBtnBG);
        colorSelectedBtnBg = getResources().getColor(R.color.selectedBtnBG);
        initComponents();
        builder = new AlertDialog.Builder(this);
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

    private void initAds() {
        adsView = findViewById(R.id.adsView);
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
        searchType = (String) getIntent().getStringExtra("searchType");
    }

    private void initComponents() {
        scvPlateShapes = findViewById(R.id.scvFilters);
        lineLoutPlateShapeItems = findViewById(R.id.filterItem);
        btnLoctheo = findViewById(R.id.locTheoView).findViewById(R.id.btnLoctheo);
        txtLoctheo = ((ConstraintLayout) findViewById(R.id.locTheoView)).findViewById(R.id.lblLoctheo);

        initPlateShapeGroups();
        initPlateDetails();
        initSelectedPlateShapes();

        btnLoctheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopUp();
                updateUIFromFilterData();
            }
        });

        searchResultRecyclerView = (RecyclerView) findViewById(R.id.search_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        searchResultRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        recyclerLayoutManager = new LinearLayoutManager(this);
        searchResultRecyclerView.setLayoutManager(recyclerLayoutManager);

        // specify an adapter (see also next example)
        searchResultListRecyclerAdapter = new ListRecyclerViewAdapter(this, allDieukhoan, 0, 50);
        updateResultList("");

        searchResultRecyclerView.setAdapter(searchResultListRecyclerAdapter);

        //workaround to make searchResultRecyclerView to match parent width
        ViewGroup.LayoutParams searchResultLayoutParams = searchResultRecyclerView.getLayoutParams();
        searchResultLayoutParams.width = helper.getScreenWidth();
        searchResultRecyclerView.setLayoutParams(searchResultLayoutParams);

        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                addVanbanidToList(GeneralSettings.getDefaultActiveNDXPId()+"");
                break;
            case GeneralSettings.SEARCH_TYPE_BIENBAO:
                addVanbanidToList(GeneralSettings.getDefaultActiveQC41Id()+"");
            case GeneralSettings.SEARCH_TYPE_VACHKEDUONG:
                addVanbanidToList(GeneralSettings.getDefaultActiveQC41Id()+"");
            default:
                int maxId = GeneralSettings.getMaxVanbanId();
                while (maxId > 0){
                    if (GeneralSettings.getVanbanInfo(maxId,"shortname").length() > 0){
                        addVanbanidToList(maxId + "");
                    }
                    maxId--;
                }
                break;
        }
    }

    private void initPlateShapeGroups() {
        ArrayList<String> groups = queries.getPlateGroups();
        for (String group :
                groups) {
            selectedPlateShapeGroups.put(group, true);
        }
    }

    private void initPlateDetails() {
        lineLoutPlateDetailsSelect = findViewById(R.id.detailsSelectView);
        btnPlateDetailsAlphanumerics = findViewById(R.id.btnAlphanumerics);
        btnPlateDetailsAlphanumerics.setTag("Alphanumerics");
        setButtonBackgroundColor(btnPlateDetailsAlphanumerics, false);
        btnPlateDetailsAlphanumerics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsAlphanumerics);
            }
        });
        cloutAlphanumericsView = findViewById(R.id.alphanumericsView);
        cloutAlphanumericsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsAlphanumerics);
            }
        });
        btnPlateDetailsArrow = findViewById(R.id.btnArrow);
        btnPlateDetailsArrow.setTag("Arrows");
        setButtonBackgroundColor(btnPlateDetailsArrow, false);
        btnPlateDetailsArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsArrow);
            }
        });
        cloutArrowView = findViewById(R.id.arrowView);
        cloutArrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsArrow);
            }
        });
        btnPlateDetailsCreatures = findViewById(R.id.btnCreatures);
        btnPlateDetailsCreatures.setTag("Creatures");
        setButtonBackgroundColor(btnPlateDetailsCreatures, false);
        btnPlateDetailsCreatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsCreatures);
            }
        });
        cloutCreaturesView = findViewById(R.id.creaturesView);
        cloutCreaturesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsCreatures);
            }
        });
        btnPlateDetailsStructures = findViewById(R.id.btnStructures);
        btnPlateDetailsStructures.setTag("Structures");
        setButtonBackgroundColor(btnPlateDetailsStructures, false);
        btnPlateDetailsStructures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsStructures);
            }
        });
        cloutStructuresView = findViewById(R.id.structuresView);
        cloutStructuresView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsStructures);
            }
        });
        btnPlateDetailsFigures = findViewById(R.id.btnFigures);
        btnPlateDetailsFigures.setTag("Figures");
        setButtonBackgroundColor(btnPlateDetailsFigures, false);
        btnPlateDetailsFigures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsFigures);
            }
        });
        cloutFiguresView = findViewById(R.id.figuresView);
        cloutFiguresView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsFigures);
            }
        });
        btnPlateDetailsVehicles = findViewById(R.id.btnVehicles);
        btnPlateDetailsVehicles.setTag("Vehicles");
        setButtonBackgroundColor(btnPlateDetailsVehicles, false);
        btnPlateDetailsVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsVehicles);
            }
        });
        cloutVehiclesView = findViewById(R.id.vehiclesView);
        cloutVehiclesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsVehicles);
            }
        });
        btnPlateDetailsSigns = findViewById(R.id.btnSigns);
        btnPlateDetailsSigns.setTag("Signs");
        setButtonBackgroundColor(btnPlateDetailsSigns, false);
        btnPlateDetailsSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsSigns);
            }
        });
        cloutSignsView = findViewById(R.id.signsView);
        cloutSignsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlateDetailsSelection(btnPlateDetailsSigns);
            }
        });
        btnPlateDetailsExtras = findViewById(R.id.btnExtras);
        btnPlateDetailsExtras.setTag("Extras");
        //make it invisible
//        setButtonBackgroundColor(btnPlateDetailsExtras,false);
        btnPlateDetailsExtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
//                updatePlateDetailsSelection(btnPlateDetailsExtras);
            }
        });
        cloutExtrasView = findViewById(R.id.extrasView);
        cloutExtrasView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                updatePlateDetailsSelection(btnPlateDetailsExtras);
            }
        });
    }

    private void initSelectedPlateShapes() {
        String currentSelectedPlateShape = "";
        boolean isSelectedPlateShapePersisted = false;
        for (String shape :
                selectedPlateShapes.keySet()) {
            if (selectedPlateShapes.get(shape)) {
                currentSelectedPlateShape = shape;
                break;
            }
        }
        lineLoutPlateShapeItems.removeAllViews();
        int desirableHeight = (int) ((float) helper.getScreenHeight() * 0.1);
        int desirablePadding = (int) ((float) desirableHeight * 0.05);

        findViewById(R.id.btnLeftNav).setBackground(helper.getDrawableFromAssets(this, "parts/navigate_left.png"));
        findViewById(R.id.btnRightNav).setBackground(helper.getDrawableFromAssets(this, "parts/navigate_right.png"));

        ArrayList<String> groups = new ArrayList<>();
        for (String group :
                selectedPlateShapeGroups.keySet()) {
            if (selectedPlateShapeGroups.get(group)) {
                groups.add(group);
            }
        }
        ArrayList<String> shapes = queries.getPlateShapeByGroup(groups);
        for (String img : shapes) {
            String imgName = img.replace("\n", "").trim();
            if (imgName.length() < 1) {
                //do nothing
            } else {
                Bitmap image = helper.getBitmapFromAssets(this, "parts/" + imgName + ".png");

                final ImageView imgView = new ImageView(this);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(layoutParams);
                imgView.setTag(imgName);
                imgView.setImageBitmap(helper.scaleImageByHeight(image, desirableHeight));
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setImageBackgroundColor((ImageView) v);
                        updateResultList("");
                    }
                });
                LinearLayout imgWrapper = new LinearLayout(this);
                imgWrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (desirableHeight + (2 * desirablePadding))));
                imgWrapper.setOrientation(LinearLayout.HORIZONTAL);
                imgWrapper.setPadding(desirablePadding, desirablePadding * 2, desirablePadding, desirablePadding * 2);
                imgWrapper.addView(imgView);
                if (imgName.equals(currentSelectedPlateShape)) {
                    selectedPlateShapes.put(imgName, true);
                    imgWrapper.setBackgroundColor(colorSelectedBtnBg);
                    isSelectedPlateShapePersisted = true;
                } else {
                    imgWrapper.setBackgroundColor(colorNormalBtnBg);
                }
                lineLoutPlateShapeItems.addView(imgWrapper);
            }
        }

        if (!isSelectedPlateShapePersisted && isPlateShapeSelected(currentSelectedPlateShape)) {
            selectedPlateShapes.put(currentSelectedPlateShape, false);
        }
    }

    private void updateResultList(String keyword) {
        allDieukhoan = search(keyword.trim());
        searchResultListRecyclerAdapter.updateView(allDieukhoan);
    }

    private ArrayList<Dieukhoan> search(String keyword) {
        if (keyword.length() > 0) {
            switch (searchType) {
                case GeneralSettings.SEARCH_TYPE_VANBAN:
                    return null;
                case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                    return null;
                default:
                    return null;
            }
        } else {
            switch (searchType) {
                case GeneralSettings.SEARCH_TYPE_BIENBAO:
                    ArrayList<String> kw = populatePlateParams();
                    ArrayList<String> groups = new ArrayList<>();
                    for (String group : selectedPlateShapeGroups.keySet()) {
                        if (selectedPlateShapeGroups.get(group)) {
                            groups.add(group);
                        }
                    }
                    ArrayList<Dieukhoan> result = queries.getPlateByParams(kw, groups);
                    Collections.sort(result);
                    return result;
                case GeneralSettings.SEARCH_TYPE_VACHKEDUONG:
                    return null;
                default:
                    return queries.searchChildren(keyword, vanbanid);
            }
        }
    }

    private ArrayList<String> populatePlateParams() {
        ArrayList<String> params = new ArrayList<>();
        for (String shape : selectedPlateShapes.keySet()) {
            if (selectedPlateShapes.get(shape)) {
                params.add(("tblPlateShapes:" + shape).trim());
            }
        }
        for (String detailsGroup : selectedPlateDetails.keySet()) {
            //TO DO: in future, if we support advance search which allows user to select a (or many) figures/signs in each group, the value that appends to 'params' should has the same form of 'plateShape' above (with colon in the midlle of group and figure/sign name)
            if (selectedPlateDetails.get(detailsGroup)) {
                params.add(detailsGroup.trim());
            }
        }
        return params;
    }

    private void showFilterPopUp() {
        LayoutInflater layoutInflater = getLayoutInflater();

        //this is custom dialog
        customView = layoutInflater.inflate(R.layout.popup_filter_bienbao, null);
        lineLoutPlateShapeGroupsSelection = (LinearLayout) customView.findViewById(R.id.PlateShapeSelection);

        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                break;
            case GeneralSettings.SEARCH_TYPE_BIENBAO:
                initPlateShapeFilters();
                updateUIFromFilterData();

                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                break;
            default:
                break;
        }

        Button btnXong = (Button) customView.findViewById(R.id.btnXong);
        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                updateFilterLabel();
                initSelectedPlateShapes();
                updateResultList("");
            }
        });

        builder.setView(customView);
        builder.create();
        alert = builder.show();
    }

    private void initPlateShapeFilters() {
        cbPlateShapeArrow = customView.findViewById(R.id.optionArrowCheckbox);
        cbPlateShapeArrow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeArrow.isChecked()) {
                    selectedPlateShapeGroups.put("Arrow", true);
                } else {
                    selectedPlateShapeGroups.put("Arrow", false);
                }
            }
        });
        cbPlateShapeCircle = customView.findViewById(R.id.optionCircleCheckbox);
        cbPlateShapeCircle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeCircle.isChecked()) {
                    selectedPlateShapeGroups.put("Circle", true);
                } else {
                    selectedPlateShapeGroups.put("Circle", false);
                }
            }
        });
        cbPlateShapeRectangle = customView.findViewById(R.id.optionRectangleCheckbox);
        cbPlateShapeRectangle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeRectangle.isChecked()) {
                    selectedPlateShapeGroups.put("Rectangle", true);
                } else {
                    selectedPlateShapeGroups.put("Rectangle", false);
                }
            }
        });
        cbPlateShapeSquare = customView.findViewById(R.id.optionSquareCheckbox);
        cbPlateShapeSquare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeSquare.isChecked()) {
                    selectedPlateShapeGroups.put("Square", true);
                } else {
                    selectedPlateShapeGroups.put("Square", false);
                }
            }
        });
        cbPlateShapeXshape = customView.findViewById(R.id.optionXshapeCheckbox);
        cbPlateShapeXshape.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeXshape.isChecked()) {
                    selectedPlateShapeGroups.put("Xshape", true);
                } else {
                    selectedPlateShapeGroups.put("Xshape", false);
                }
            }
        });
        cbPlateShapeTriangle = customView.findViewById(R.id.optionTriangleCheckbox);
        cbPlateShapeTriangle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeTriangle.isChecked()) {
                    selectedPlateShapeGroups.put("Triangle", true);
                } else {
                    selectedPlateShapeGroups.put("Triangle", false);
                }
            }
        });
        cbPlateShapeOctagon = customView.findViewById(R.id.optionOctagonCheckbox);
        cbPlateShapeOctagon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeOctagon.isChecked()) {
                    selectedPlateShapeGroups.put("Octagon", true);
                } else {
                    selectedPlateShapeGroups.put("Octagon", false);
                }
            }
        });
        cbPlateShapeRhombic = customView.findViewById(R.id.optionRhombicCheckbox);
        cbPlateShapeRhombic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbPlateShapeRhombic.isChecked()) {
                    selectedPlateShapeGroups.put("Rhombus", true);
                } else {
                    selectedPlateShapeGroups.put("Rhombus", false);
                }
            }
        });
    }

    private void addVanbanidToList(String vbID) {
//        String vbID = GeneralSettings.getVanbanInfo(vanbanKey, "id");
        for (String id : vanbanid) {
            if (vbID.equals(id)) {
                return;
            }
        }
        vanbanid.add(vbID);
        updateFilterLabel();
    }

    private void updateFilterLabel() {
        String loctheo = "";
        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:
                break;
            case GeneralSettings.SEARCH_TYPE_BIENBAO:
                for (String group : selectedPlateShapeGroups.keySet()) {
                    if (selectedPlateShapeGroups.get(group)) {
                        loctheo += shapeGroupNamePair.get(group) + ", ";
                    }
                }
                break;
            default:
                break;
        }
        txtLoctheo.setText(helper.removeLastCharacters(loctheo, 2));
    }

    private void updateUIFromFilterData() {
        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:

                break;
            case GeneralSettings.SEARCH_TYPE_MUCPHAT:

                break;
            case GeneralSettings.SEARCH_TYPE_BIENBAO:
                if (selectedPlateShapeGroups.get("Circle") != null && selectedPlateShapeGroups.get("Circle")) {
                    cbPlateShapeCircle.setChecked(true);
                    ;
                } else {
                    cbPlateShapeCircle.setChecked(false);
                }
                if (selectedPlateShapeGroups.get("Rectangle") != null && selectedPlateShapeGroups.get("Rectangle")) {
                    cbPlateShapeRectangle.setChecked(true);
                } else {
                    cbPlateShapeRectangle.setChecked(false);
                }
                if (selectedPlateShapeGroups.get("Octagon") != null && selectedPlateShapeGroups.get("Octagon")) {
                    cbPlateShapeOctagon.setChecked(true);
                } else {
                    cbPlateShapeOctagon.setChecked(false);
                }
                if (selectedPlateShapeGroups.get("Triangle") != null && selectedPlateShapeGroups.get("Triangle")) {
                    cbPlateShapeTriangle.setChecked(true);
                } else {
                    cbPlateShapeTriangle.setChecked(false);
                }
                if (selectedPlateShapeGroups.get("Xshape") != null && selectedPlateShapeGroups.get("Xshape")) {
                    cbPlateShapeXshape.setChecked(true);
                } else {
                    cbPlateShapeXshape.setChecked(false);
                }
                if (selectedPlateShapeGroups.get("Square") != null && selectedPlateShapeGroups.get("Square")) {
                    cbPlateShapeSquare.setChecked(true);
                } else {
                    cbPlateShapeSquare.setChecked(false);
                }
                if (selectedPlateShapeGroups.get("Arrow") != null && selectedPlateShapeGroups.get("Arrow")) {
                    cbPlateShapeArrow.setChecked(true);
                } else {
                    cbPlateShapeArrow.setChecked(false);
                }
                if (selectedPlateShapeGroups.get("Rhombus") != null && selectedPlateShapeGroups.get("Rhombus")) {
                    cbPlateShapeRhombic.setChecked(true);
                } else {
                    cbPlateShapeRhombic.setChecked(false);
                }
                break;
            default:
                break;
        }
    }

    private void updatePlateDetailsSelection(ImageButton button) {
        String btnName = (String) button.getTag();
        selectedPlateDetails.put(btnName, !isButtonOn(btnName));
        setButtonBackgroundColor(button, isButtonOn(btnName));
        updateResultList("");
    }

    private void setButtonBackgroundColor(ImageButton button, boolean isActive) {
        if (isActive) {
            ((ConstraintLayout) button.getParent()).setBackgroundColor(colorSelectedBtnBg);
        } else {
            ((ConstraintLayout) button.getParent()).setBackgroundColor(colorNormalBtnBg);
        }
    }

    private boolean isButtonOn(String btnName) {
        if (selectedPlateDetails.get(btnName) == null) {
            return false;
        }
        return selectedPlateDetails.get(btnName);
    }

    private boolean isPlateShapeSelected(String imageName) {
        if (selectedPlateShapes.get(imageName) == null) {
            return false;
        }
        return selectedPlateShapes.get(imageName);
    }

    private void setImageBackgroundColor(ImageView imgView) {
        boolean isAlreadyRemoved = false;
        boolean isAlreadySet = false;
        for (int i = 0; i < lineLoutPlateShapeItems.getChildCount(); i++) {
            LinearLayout child = (LinearLayout) lineLoutPlateShapeItems.getChildAt(i);
            if (child.getChildAt(0).equals(imgView)) {
                if (isPlateShapeSelected((String) imgView.getTag())) {
                    child.setBackgroundColor(colorNormalBtnBg);
                    selectedPlateShapes.put((String) imgView.getTag(), false);
                    isAlreadyRemoved = true;
                } else {
                    child.setBackgroundColor(colorSelectedBtnBg);
                    selectedPlateShapes.put((String) imgView.getTag(), true);
                }
                isAlreadySet = true;
            } else {
                child.setBackgroundColor(colorNormalBtnBg);
                if (imgView != null) {
                    if (isPlateShapeSelected((String) ((ImageView) child.getChildAt(0)).getTag())) {
                        isAlreadyRemoved = true;
                    }
                    selectedPlateShapes.put((String) ((ImageView) child.getChildAt(0)).getTag(), false);
                }
            }
            if (isAlreadyRemoved && isAlreadySet) {
                break;
            }
        }
    }
}
