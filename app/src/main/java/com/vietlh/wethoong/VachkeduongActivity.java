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
import java.util.HashMap;

public class VachkeduongActivity extends AppCompatActivity {

    private static final String TAG = "VachkeduongActivity";
    private RecyclerView searchResultRecyclerView;
    private ListRecyclerViewAdapter searchResultListRecyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Dieukhoan> allDieukhoan;
    private HashMap<String, Boolean> selectedVachShapeGroups = new HashMap<>();
    private HashMap<String, Boolean> selectedVachShapes = new HashMap<>();
    private HashMap<String, Boolean> selectedVachDetails = new HashMap<>();
    private HashMap<String, String> shapeGroupNamePair = new HashMap();
    private Queries queries = new Queries(DBConnection.getInstance(this));
    private Button btnLoctheo;
    private TextView txtLoctheo;
    private ConstraintLayout lineLoutVachShapeSelect;
    private ConstraintLayout lineLoutVachDetailsSelect;
    private HorizontalScrollView scvVachShapes;
    private LinearLayout lineLoutVachShapeItems;
    private ConstraintLayout cloutOnroadView;
    private ImageButton btnVachDetailsOnroad;
    private ConstraintLayout cloutSidewalkView;
    private ImageButton btnVachDetailsSidewalk;
    private ConstraintLayout cloutCrossView;
    private ImageButton btnVachDetailsCross;
    private ConstraintLayout cloutObstacleView;
    private ImageButton btnVachDetailsObstacle;
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
    private LinearLayout lineLoutVachShapeGroupsSelection;
    private Switch cbVachShapeYellow;
    private Switch cbVachShapeWhite;
    private Switch cbVachShapeBlack;
    private Switch cbVachShapeRed;
    private Switch cbVachShapeAlphanumeric;
    private Switch cbVachShapeShape;
    private Switch cbVachShapeParallel;
    private Switch cbVachShapeDuo;
    private Switch cbVachShapeSingle;
    private Switch cbVachShapeSign;
    private int colorNormalBtnBg;
    private int colorNormalBtnFg;
    private int colorSelectedBtnBg;
    private int colorSelectedBtnFg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vachkeduong);
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
        searchType = getIntent().getStringExtra("searchType");
    }

    private void initComponents() {
        lineLoutVachShapeSelect = findViewById(R.id.filterView);
        scvVachShapes = findViewById(R.id.scvFilters);
        lineLoutVachShapeItems = findViewById(R.id.filterItem);
        btnLoctheo = findViewById(R.id.locTheoView).findViewById(R.id.btnLoctheo);
        txtLoctheo = findViewById(R.id.locTheoView).findViewById(R.id.lblLoctheo);

        initVachShapeGroups();
        initVachDetails();
        initSelectedVachShapes();

        btnLoctheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopUp();
                updateUIFromFilterData();
            }
        });

        searchResultRecyclerView = findViewById(R.id.search_result);

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
//                addVanbanidToList(GeneralSettings.danhsachvanban[0]);
                addVanbanidToList(GeneralSettings.getDefaultActiveNDXPId()+"");
                break;
            case GeneralSettings.SEARCH_TYPE_BIENBAO:
//                addVanbanidToList(GeneralSettings.danhsachvanban[1]);
                addVanbanidToList(GeneralSettings.getDefaultActiveQC41Id()+"");
                break;
            case GeneralSettings.SEARCH_TYPE_VACHKEDUONG:
//                addVanbanidToList(GeneralSettings.danhsachvanban[1]);
                addVanbanidToList(GeneralSettings.getDefaultActiveQC41Id()+"");
                break;
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

    private void initVachShapeGroups() {
        HashMap<String, String> groups = queries.getVachGroups();
        for (String name :
                groups.keySet()) {
            selectedVachShapeGroups.put(name, true);
            shapeGroupNamePair.put(name, groups.get(name));
        }
    }

    private void initVachDetails() {
        lineLoutVachDetailsSelect = findViewById(R.id.detailsSelectView);
        btnVachDetailsCross = findViewById(R.id.btnCross);
        btnVachDetailsCross.setTag("cross");
        setButtonBackgroundColor(btnVachDetailsCross, false);
        btnVachDetailsCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsCross);
            }
        });
        cloutCrossView = (ConstraintLayout) findViewById(R.id.crossView);
        cloutCrossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsCross);
            }
        });
        btnVachDetailsOnroad = (ImageButton) findViewById(R.id.btnOnroad);
        btnVachDetailsOnroad.setTag("on road");
        setButtonBackgroundColor(btnVachDetailsOnroad, false);
        btnVachDetailsOnroad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsOnroad);
            }
        });
        cloutOnroadView = (ConstraintLayout) findViewById(R.id.onroadView);
        cloutOnroadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsOnroad);
            }
        });
        btnVachDetailsSidewalk = (ImageButton) findViewById(R.id.btnSidewalk);
        btnVachDetailsSidewalk.setTag("sidewalk");
        setButtonBackgroundColor(btnVachDetailsSidewalk, false);
        btnVachDetailsSidewalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsSidewalk);
            }
        });
        cloutSidewalkView = (ConstraintLayout) findViewById(R.id.sidewalkView);
        cloutSidewalkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsSidewalk);
            }
        });
        btnVachDetailsObstacle = (ImageButton) findViewById(R.id.btnObstacle);
        btnVachDetailsObstacle.setTag("obstacle");
        setButtonBackgroundColor(btnVachDetailsObstacle, false);
        btnVachDetailsObstacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsObstacle);
            }
        });
        cloutObstacleView = (ConstraintLayout) findViewById(R.id.obstacleView);
        cloutObstacleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVachDetailsSelection(btnVachDetailsObstacle);
            }
        });
    }

    private void initSelectedVachShapes() {
        String currentSelectedVachShape = "";
        boolean isSelectedVachShapePersisted = false;
        for (String shape :
                selectedVachShapes.keySet()) {
            if (selectedVachShapes.get(shape)) {
                currentSelectedVachShape = shape;
                break;
            }
        }
        lineLoutVachShapeItems.removeAllViews();
        int desirableHeight = (int) ((float) helper.getScreenHeight() * 0.15);
        int desirablePadding = (int) ((float) desirableHeight * 0.05);

        findViewById(R.id.btnLeftNav).setBackground(helper.getDrawableFromAssets(this, "parts/navigate_left.png"));
        findViewById(R.id.btnRightNav).setBackground(helper.getDrawableFromAssets(this, "parts/navigate_right.png"));

        ArrayList<String> groups = new ArrayList<>();
        for (String group :
                selectedVachShapeGroups.keySet()) {
            if (selectedVachShapeGroups.get(group)) {
                groups.add(group);
            }
        }
        ArrayList<String> shapes = queries.getVachShapeByGroup(groups);
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
                if (imgName.equals(currentSelectedVachShape)) {
                    selectedVachShapes.put(imgName, true);
                    imgWrapper.setBackgroundColor(colorSelectedBtnBg);
                    isSelectedVachShapePersisted = true;
                } else {
                    imgWrapper.setBackgroundColor(colorNormalBtnBg);
                }
                lineLoutVachShapeItems.addView(imgWrapper);
            }
        }

        if (!isSelectedVachShapePersisted && isVachShapeSelected(currentSelectedVachShape)) {
            selectedVachShapes.put(currentSelectedVachShape, false);
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
                    return null;
                case GeneralSettings.SEARCH_TYPE_VACHKEDUONG:
                    ArrayList<String> kw = populateVachParams();
                    ArrayList<String> groups = new ArrayList<>();
                    for (String group : selectedVachShapeGroups.keySet()) {
                        if (selectedVachShapeGroups.get(group)) {
                            groups.add(group);
                        }
                    }
                    return queries.getVachByParams(kw, groups);
                default:
                    return queries.searchChildren(keyword, vanbanid);
            }
        }
    }

    private ArrayList<String> populateVachParams() {
        ArrayList<String> params = new ArrayList<>();
        for (String shape : selectedVachShapes.keySet()) {
            if (selectedVachShapes.get(shape)) {
                params.add(("tblVachShapes:" + shape).trim());
            }
        }
        for (String detailsGroup : selectedVachDetails.keySet()) {
            //TO DO: in future, if we support advance search which allows user to select a (or many) figures/signs in each group, the value that appends to 'params' should has the same form of 'plateShape' above (with colon in the midlle of group and figure/sign name)
            if (selectedVachDetails.get(detailsGroup)) {
                params.add("positions:" + detailsGroup.trim());
            }
        }
        return params;
    }

    private void showFilterPopUp() {
        LayoutInflater layoutInflater = getLayoutInflater();

        //this is custom dialog
        customView = layoutInflater.inflate(R.layout.popup_filter_vachke, null);
        lineLoutVachShapeGroupsSelection = (LinearLayout) customView.findViewById(R.id.vachShapeSelection);
//        ViewGroup.LayoutParams hiddenSection;
        switch (searchType) {
            case GeneralSettings.SEARCH_TYPE_VANBAN:
                break;
            case GeneralSettings.SEARCH_TYPE_BIENBAO:
                break;
            case GeneralSettings.SEARCH_TYPE_VACHKEDUONG:
                initVachShapeFilters();
                updateUIFromFilterData();
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
                initSelectedVachShapes();
                updateResultList("");
            }
        });

        builder.setView(customView);
        builder.create();
        alert = builder.show();
    }

    private void initVachShapeFilters() {
        cbVachShapeAlphanumeric = customView.findViewById(R.id.optionAlphanumericCheckbox);
        cbVachShapeAlphanumeric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeAlphanumeric.isChecked()) {
                    selectedVachShapeGroups.put("alphanumeric", true);
                } else {
                    selectedVachShapeGroups.put("alphanumeric", false);
                }
            }
        });
        cbVachShapeBlack = customView.findViewById(R.id.optionBlackCheckbox);
        cbVachShapeBlack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeBlack.isChecked()) {
                    selectedVachShapeGroups.put("black", true);
                } else {
                    selectedVachShapeGroups.put("black", false);
                }
            }
        });
        cbVachShapeDuo = customView.findViewById(R.id.optionDuoCheckbox);
        cbVachShapeDuo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeDuo.isChecked()) {
                    selectedVachShapeGroups.put("duo line", true);
                } else {
                    selectedVachShapeGroups.put("duo line", false);
                }
            }
        });
        cbVachShapeParallel = customView.findViewById(R.id.optionParallelCheckbox);
        cbVachShapeParallel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeParallel.isChecked()) {
                    selectedVachShapeGroups.put("multiple line", true);
                } else {
                    selectedVachShapeGroups.put("multiple line", false);
                }
            }
        });
        cbVachShapeRed = customView.findViewById(R.id.optionRedCheckbox);
        cbVachShapeRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeRed.isChecked()) {
                    selectedVachShapeGroups.put("red", true);
                } else {
                    selectedVachShapeGroups.put("red", false);
                }
            }
        });
        cbVachShapeShape = customView.findViewById(R.id.optionShapeCheckbox);
        cbVachShapeShape.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeShape.isChecked()) {
                    selectedVachShapeGroups.put("shape", true);
                } else {
                    selectedVachShapeGroups.put("shape", false);
                }
            }
        });
        cbVachShapeSign = customView.findViewById(R.id.optionSignCheckbox);
        cbVachShapeSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeSign.isChecked()) {
                    selectedVachShapeGroups.put("sign", true);
                } else {
                    selectedVachShapeGroups.put("sign", false);
                }
            }
        });
        cbVachShapeWhite = customView.findViewById(R.id.optionWhiteCheckbox);
        cbVachShapeWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeWhite.isChecked()) {
                    selectedVachShapeGroups.put("white", true);
                } else {
                    selectedVachShapeGroups.put("white", false);
                }
            }
        });
        cbVachShapeSingle = customView.findViewById(R.id.optionSingleCheckbox);
        cbVachShapeSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeSingle.isChecked()) {
                    selectedVachShapeGroups.put("single line", true);
                } else {
                    selectedVachShapeGroups.put("single line", false);
                }
            }
        });
        cbVachShapeYellow = customView.findViewById(R.id.optionYellowCheckbox);
        cbVachShapeYellow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbVachShapeYellow.isChecked()) {
                    selectedVachShapeGroups.put("yellow", true);
                } else {
                    selectedVachShapeGroups.put("yellow", false);
                }
            }
        });
    }

    private void addVanbanidToList(String vbID) {
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
                break;
            case GeneralSettings.SEARCH_TYPE_VACHKEDUONG:
                for (String group : selectedVachShapeGroups.keySet()) {
                    if (selectedVachShapeGroups.get(group)) {
                        loctheo += shapeGroupNamePair.get(group) + ", ";
                    }
                }
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

                break;
            case GeneralSettings.SEARCH_TYPE_VACHKEDUONG:
                if (selectedVachShapeGroups.get("single line") != null && selectedVachShapeGroups.get("single line")) {
                    cbVachShapeSingle.setChecked(true);
                } else {
                    cbVachShapeSingle.setChecked(false);
                }
                if (selectedVachShapeGroups.get("shape") != null && selectedVachShapeGroups.get("shape")) {
                    cbVachShapeShape.setChecked(true);
                } else {
                    cbVachShapeShape.setChecked(false);
                }
                if (selectedVachShapeGroups.get("multiple line") != null && selectedVachShapeGroups.get("multiple line")) {
                    cbVachShapeParallel.setChecked(true);
                } else {
                    cbVachShapeParallel.setChecked(false);
                }
                if (selectedVachShapeGroups.get("duo line") != null && selectedVachShapeGroups.get("duo line")) {
                    cbVachShapeDuo.setChecked(true);
                } else {
                    cbVachShapeDuo.setChecked(false);
                }
                if (selectedVachShapeGroups.get("sign") != null && selectedVachShapeGroups.get("sign")) {
                    cbVachShapeSign.setChecked(true);
                } else {
                    cbVachShapeSign.setChecked(false);
                }
                if (selectedVachShapeGroups.get("alphanumeric") != null && selectedVachShapeGroups.get("alphanumeric")) {
                    cbVachShapeAlphanumeric.setChecked(true);
                } else {
                    cbVachShapeAlphanumeric.setChecked(false);
                }
                if (selectedVachShapeGroups.get("yellow") != null && selectedVachShapeGroups.get("yellow")) {
                    cbVachShapeYellow.setChecked(true);
                } else {
                    cbVachShapeYellow.setChecked(false);
                }
                if (selectedVachShapeGroups.get("white") != null && selectedVachShapeGroups.get("white")) {
                    cbVachShapeWhite.setChecked(true);
                } else {
                    cbVachShapeWhite.setChecked(false);
                }
                if (selectedVachShapeGroups.get("black") != null && selectedVachShapeGroups.get("black")) {
                    cbVachShapeBlack.setChecked(true);
                } else {
                    cbVachShapeBlack.setChecked(false);
                }
                if (selectedVachShapeGroups.get("red") != null && selectedVachShapeGroups.get("red")) {
                    cbVachShapeRed.setChecked(true);
                } else {
                    cbVachShapeRed.setChecked(false);
                }
                break;
            default:
                break;
        }
    }

    private void updateVachDetailsSelection(ImageButton button) {
        String btnName = (String) button.getTag();
        selectedVachDetails.put(btnName, !isButtonOn(btnName));
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
        if (selectedVachDetails.get(btnName) == null) {
            return false;
        }
        return selectedVachDetails.get(btnName);
    }

    private boolean isVachShapeSelected(String imageName) {
        if (selectedVachShapes.get(imageName) == null) {
            return false;
        }
        return selectedVachShapes.get(imageName);
    }

    private void setImageBackgroundColor(ImageView imgView) {
        boolean isAlreadyRemoved = false;
        boolean isAlreadySet = false;
        for (int i = 0; i < lineLoutVachShapeItems.getChildCount(); i++) {
            LinearLayout child = (LinearLayout) lineLoutVachShapeItems.getChildAt(i);
            if (child.getChildAt(0).equals(imgView)) {
                if (isVachShapeSelected((String) imgView.getTag())) {
                    child.setBackgroundColor(colorNormalBtnBg);
                    selectedVachShapes.put((String) imgView.getTag(), false);
                    isAlreadyRemoved = true;
                } else {
                    child.setBackgroundColor(colorSelectedBtnBg);
                    selectedVachShapes.put((String) imgView.getTag(), true);
                }
                isAlreadySet = true;
            } else {
                child.setBackgroundColor(colorNormalBtnBg);
                if (imgView != null) {
                    if (isVachShapeSelected((String) ((ImageView) child.getChildAt(0)).getTag())) {
                        isAlreadyRemoved = true;
                    }
                    selectedVachShapes.put((String) ((ImageView) child.getChildAt(0)).getTag(), false);
                }
            }
            if (isAlreadyRemoved && isAlreadySet) {
                break;
            }
        }
    }
}
