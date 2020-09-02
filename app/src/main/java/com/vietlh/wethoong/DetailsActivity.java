package com.vietlh.wethoong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vietlh.wethoong.adapters.ListRecyclerViewAdapter;
import com.vietlh.wethoong.entities.BosungKhacphuc;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.RedirectionHelper;
import com.vietlh.wethoong.utils.SearchFor;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static com.vietlh.wethoong.R.*;
import static com.vietlh.wethoong.R.color.*;

public class DetailsActivity extends AppCompatActivity {

    private Queries queries = new Queries(DBConnection.getInstance(this));
    private UtilsHelper helper = new UtilsHelper();
    private RedirectionHelper redirectionHelper = new RedirectionHelper();
    private AdsHelper adsHelper = new AdsHelper();
    private SearchFor search = new SearchFor(this);
    private Dieukhoan dieukhoan;
    private Dieukhoan parentDieukhoan;
    private String dieukhoanId;
    private ArrayList<String> vanbanid = new ArrayList<>();
    private ArrayList<Dieukhoan> relatedChildren = new ArrayList<>();
    private ArrayList<Dieukhoan> children = new ArrayList<>();
    private ArrayList<Dieukhoan> tamgiuphuongtienList = new ArrayList<>();
    private ArrayList<Dieukhoan> thamquyenList = new ArrayList<>();
    private ArrayList<BosungKhacphuc> hinhphatbosungList = new ArrayList<>();
    private ArrayList<BosungKhacphuc> bienphapkhacphucList = new ArrayList<>();
    private ListRecyclerViewAdapter searchResultListRecyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private String contentString = "";

    private ScrollView scrollView;
    private TextView lblVanban;
    private TextView lblDieukhoan;
    private Button btnBreadscrubs;
    private TextView lblNoidung;
    private LinearLayout extraView;
    private LinearLayout mucphatView;
    private TextView mucphatDetails;
    private LinearLayout phuongtienView;
    private TextView phuongtienDetails;
    private LinearLayout linhvucView;
    private TextView linhvucDetails;
    private LinearLayout doituongView;
    private TextView doituongDetails;
    private LinearLayout hinhphatbosung;
    private TextView hinhphatbosungDetails;
    private LinearLayout bienphapkhacphuc;
    private TextView bienphapkhacphucDetails;
    private LinearLayout tamgiu;
    private TextView tamgiuDetails;
    private LinearLayout thamquyen;
    private TextView thamquyenDetails;
    private LinearLayout minhhoaView;
    private RelativeLayout childrenDieukhoan;
    private RecyclerView rclChildrenDieukhoan;

    private ConstraintLayout btnXemthemView;
    private ImageButton btnRead;
    private Button btnXemthem;
    private TextToSpeech speak;
    HashMap<String, String> speechParams = new HashMap<String, String>();

    private LinearLayout adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_details);

        getPassingParameters();
        speak = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = speak.setLanguage(new Locale("vi"));

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                        Toast.makeText(getApplicationContext(), "Ngôn ngữ không hỗ trợ! \nKiểm tra lại cài đặt \"Văn bản - Giọng nói\" trong phần cài đặt của thiết bị.", Toast.LENGTH_SHORT).show();
                        btnRead.setVisibility(View.GONE);
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");

                    //I don't know why we need these params but adding these params and passing them to speak() method, it works
                    speechParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                    speak.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {
                            Log.i("TTS", "Speaker OnStart");
                            updateSpeakerButton(false);
                        }

                        @Override
                        public void onDone(String s) {
                            Log.i("TTS", "Speaker OnDone.");
                            updateSpeakerButton(true);
                        }

                        @Override
                        public void onError(String s) {
                            Log.i("TTS", "Speaker OnError.");
                            updateSpeakerButton(false);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Có lỗi khi khởi tạo TTS!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initComponents();
        dieukhoan = queries.searchDieukhoanByID(dieukhoanId, vanbanid).get(0);
        vanbanid.add(String.valueOf(dieukhoan.getVanban().getId()));
        updateDetails();

        showDieukhoan();
        initAds();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop speech if needed
        if (speak.isSpeaking()) {
            speak.stop();
            updateSpeakerButton(true);
        }
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
        dieukhoanId = getIntent().getStringExtra("dieukhoanId");
    }

    private void initComponents() {
        scrollView = findViewById(id.scrollView);
        btnXemthemView = findViewById(id.btnXemthemView);

        lblVanban = findViewById(id.lblVanban);
        lblDieukhoan = findViewById(id.lblDieukhoan);
        lblNoidung = findViewById(id.lblNoidung);
        registerForContextMenu(lblNoidung);
        mucphatDetails = findViewById(id.mucphatDetails);
        phuongtienDetails = findViewById(id.phuongtienDetails);
        linhvucDetails = findViewById(id.linhvucDetails);
        doituongDetails = findViewById(id.doituongDetails);
        hinhphatbosungDetails = findViewById(id.hinhphatbosungDetails);
        registerForContextMenu(hinhphatbosungDetails);
        bienphapkhacphucDetails = findViewById(id.bienphapkhacphucDetails);
        registerForContextMenu(bienphapkhacphucDetails);
        tamgiuDetails = findViewById(id.tamgiuDetails);
        thamquyenDetails = findViewById(id.thamquyenDetails);

        btnXemthem = findViewById(id.btnXemthem);
        btnXemthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRelatedDieukhoan();
            }
        });
        btnRead = findViewById(id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (!speak.isSpeaking()) {
                    int speechStatus = speak.speak(contentString, TextToSpeech.QUEUE_FLUSH, speechParams);

                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.e("TTS", "Error in converting Text to Speech!");
                    } else {
                        Toast.makeText(getApplicationContext(), "Đang đọc....\nHãy kiểm tra loa hoặc tai nghe", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (speak != null) {
                        speak.stop();
                        updateSpeakerButton(true);
                        Toast.makeText(getApplicationContext(), "Đã ngừng đọc...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //speaker button set to off initially
        updateSpeakerButton(true);

        btnBreadscrubs = findViewById(id.btnBreadscrubs);
        btnBreadscrubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Message", "tapping on: " + parentDieukhoan.getId());
                Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                //TODO: need to change the hardcode dieukhoanId to something that configurable.
                i.putExtra("dieukhoanId", String.valueOf(parentDieukhoan.getId()));
                startActivity(i);
            }
        });

        extraView = findViewById(id.extraView);
        mucphatView = findViewById(id.mucphatView);
        phuongtienView = findViewById(id.phuongtienView);
        linhvucView = findViewById(id.linhvucView);
        doituongView = findViewById(id.doituongView);
        hinhphatbosung = findViewById(id.hinhphatbosung);
        bienphapkhacphuc = findViewById(id.bienphapkhacphuc);
        tamgiu = findViewById(id.tamgiu);
        thamquyen = findViewById(id.thamquyen);
        minhhoaView = findViewById(id.minhhoaView);
        childrenDieukhoan = findViewById(id.childrenDieukhoan);

        rclChildrenDieukhoan = findViewById(id.rclChildrenDieukhoan);
    }

    private void initAds() {
        adsView = (LinearLayout) findViewById(id.adsView);
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
                    redirectionHelper.initFBUrlSets(urlSets, 0, getResources().getString(string.wethoongFB), getResources().getString(string.wethoongFBApp));
                    redirectionHelper.initFBUrlSets(urlSets, 1, getResources().getString(string.condonghieuluatFB), getResources().getString(string.condonghieuluatFBApp));
                    redirectionHelper.openFacebook(getApplicationContext(), urlSets);

                }
            });
            btnFBBanner.setBackgroundResource(drawable.facebook_banner_wethoong);
            adsHelper.addButtonToView(btnFBBanner, adsView);
        }
        if (GeneralSettings.ENABLE_INTERSTITIAL_ADS) {
            adsHelper.initTJAds(this, getApplicationContext());
        }
    }

    private void updateSpeakerButton(Boolean onNow) {
        if (onNow) {
            btnRead.setImageResource(drawable.speaker_on);
            btnRead.setBackgroundResource(drawable.rounded_green_button);
        } else {
            btnRead.setImageResource(drawable.speaker_off);
            btnRead.setBackgroundResource(drawable.round_red_button);
        }
    }

    private void updateDetails() {

        for (Dieukhoan dk : getChildren(dieukhoanId)) {
            children.add(dk);
        }

        for (Dieukhoan dk : queries.getAllDirectRelatedDieukhoan(Integer.parseInt(dieukhoanId))) {
            relatedChildren.add(dk);
        }

        for (Dieukhoan dk : queries.getAllRelativeRelatedDieukhoan(Integer.parseInt(dieukhoanId))) {
            relatedChildren.add(dk);
        }

        hinhphatbosungList = queries.getAllHinhphatbosung(Integer.parseInt(dieukhoanId));
        bienphapkhacphucList = queries.getAllBienphapkhacphuc(Integer.parseInt(dieukhoanId));
        tamgiuphuongtienList = getTamgiuPhuongtienList();
        thamquyenList = getThamquyenList();

        for (Dieukhoan parent : getParent(String.valueOf(dieukhoan.getCha()))) {
            parentDieukhoan = parent;
        }
    }

    private void showRelatedDieukhoan() {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType", "lienquan");
        i.putExtra("dieukhoanId", dieukhoanId);
        startActivity(i);
    }

    private void hideMinhhoaView(Boolean isHidden) {
        if (isHidden) {
            helper.hideSection(minhhoaView);
        } else {
            helper.showSection(minhhoaView);
        }
    }

    private void hideExtraInfoView(Boolean isHidden) {
        if (isHidden) {
            helper.hideSection(extraView);
            populateExtraInfoView();
        } else {
            helper.showSection(extraView);
        }
    }

    private void showDieukhoan() {
        lblVanban.setText(GeneralSettings.getVanbanInfo(dieukhoan.getVanban().getId(), "shortname") + " (" + dieukhoan.getVanban().getMa() + ")");
        lblDieukhoan.setText(dieukhoan.getSo());
        String breadscrubText = search.getAncestersNumber(dieukhoan, vanbanid);
        if (breadscrubText.length() > 0) {
            btnBreadscrubs.setText(breadscrubText);
            btnBreadscrubs.setEnabled(true);
        } else {
            btnBreadscrubs.setVisibility(View.GONE);
        }

        String noidung = "";
        if (dieukhoan.getTieude().length() < 1) {
            noidung = dieukhoan.getNoidung();
        } else if (dieukhoan.getNoidung().length() < 1) {
            noidung = dieukhoan.getTieude();
        } else {
            noidung = dieukhoan.getTieude() + "\n" + dieukhoan.getNoidung();
        }
        lblNoidung.setText(noidung);

        //Reverse breadscrub text
        ArrayList<String> aces = new ArrayList<>(Arrays.asList(breadscrubText.split("/")));
        Collections.reverse(aces);

        //insert prefix for dieukhoan
        String pref = "";
        ArrayList<String> edittedAces = new ArrayList<>();
        for (String scrub: aces){
            edittedAces.add(pref + scrub);
            if (scrub.trim().toLowerCase().startsWith("điều")){
                pref = "khoản ";
            }else if(pref.equals("khoản ")){
                pref = "điểm ";
            } else {
                pref = "";
            }
        }
        //update content string
        String so = (dieukhoan.getSo().endsWith(".")) ? dieukhoan.getSo() : dieukhoan.getSo() + ".";
        contentString = TextUtils.join(" ", edittedAces) + "\n" + pref + so + " " + dieukhoan.getTieude() + "\n" + dieukhoan.getNoidung();

        ArrayList<String> images = dieukhoan.getMinhhoa();

        if (images.size() > 0) {
            fillMinhhoaToViewMinhhoa(images);
        } else {
            hideMinhhoaView(true);
        }

        // Enable extra section for details of ND46
        if (dieukhoan.getVanban().getId() == GeneralSettings.getDefaultActiveNDXPId()) {
            hideExtraInfoView(false);
            populateExtraInfoView();
        } else {
            hideExtraInfoView(true);
        }
        if (hinhphatbosungList.size() < 1 && bienphapkhacphucList.size() < 1 && thamquyenList.size() < 1
                && tamgiuphuongtienList.size() < 1) {
            helper.hideSection(hinhphatbosung);
            helper.hideSection(bienphapkhacphuc);
            helper.hideSection(thamquyen);
            helper.hideSection(tamgiu);
        } else {
            populateBosungKhacphucView();
        }

        if (relatedChildren.size() < 1) {
            //just hide the btnXemthem button only
            btnXemthem.setVisibility(View.INVISIBLE);
        }

        if (children.size() > 0) {
            helper.showSection(childrenDieukhoan);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            rclChildrenDieukhoan.setHasFixedSize(true);

            // use a linear layout manager
            recyclerLayoutManager = new LinearLayoutManager(this);
            rclChildrenDieukhoan.setLayoutManager(recyclerLayoutManager);

            // specify an adapter (see also next example)
            searchResultListRecyclerAdapter = new ListRecyclerViewAdapter(this, children, 1);
            rclChildrenDieukhoan.setAdapter(searchResultListRecyclerAdapter);

            //workaround to make searchResultRecyclerView to match parent width
            ViewGroup.LayoutParams searchResultLayoutParams = rclChildrenDieukhoan.getLayoutParams();
            searchResultLayoutParams.width = helper.getScreenWidth();
            rclChildrenDieukhoan.setLayoutParams(searchResultLayoutParams);

            //update content string with all major children info
            for (Dieukhoan cdk : children) {
                String s = (cdk.getSo().endsWith(".")) ? cdk.getSo() : cdk.getSo() + ".";
                contentString += "\n" + s + " " + cdk.getTieude() + "\n" + cdk.getNoidung();
            }

        } else {
            helper.hideSection(childrenDieukhoan);
        }
    }

    private void populateBosungKhacphucView() {
        if (hinhphatbosungList.size() > 0) {
            helper.showSection(hinhphatbosung);
            String bosungDetails = "";
            for (BosungKhacphuc bosung : hinhphatbosungList) {
                bosungDetails += "- " + bosung.getNoidung() + "\n";
            }
            hinhphatbosungDetails.setText(bosungDetails);
            contentString += "\nHình phạt bổ sung: " + bosungDetails;
        } else {
            helper.hideSection(hinhphatbosung);
        }

        if (bienphapkhacphucList.size() > 0) {
            helper.showSection(bienphapkhacphuc);
            String khacphucDetails = "";
            for (BosungKhacphuc khacphuc : bienphapkhacphucList) {
                khacphucDetails += "- " + khacphuc.getNoidung() + "\n";
            }
            bienphapkhacphucDetails.setText(khacphucDetails);
            contentString += "\nBiện pháp khắc phục: " + khacphucDetails;
        } else {
            helper.hideSection(bienphapkhacphuc);
        }

        if (tamgiuphuongtienList.size() > 0) {
            helper.showSection(tamgiu);
            tamgiuDetails.setText("07 ngày");
            contentString += "\nTạm giữ phương tiện: 07 ngày";
        } else {
            helper.hideSection(tamgiu);
        }

        if (thamquyenList.size() > 0) {
            helper.showSection(thamquyen);
            String tqDetails = "";
            for (Dieukhoan tq : thamquyenList) {
                tqDetails += tq.getNoidung() + "\n";
            }
            thamquyenDetails.setText(tqDetails);
        } else {
            helper.hideSection(thamquyen);
        }
    }

    private void populateExtraInfoView() {
        String mpText = getMucphat(String.valueOf(dieukhoan.getId()));
        String ptText = getPhuongtien(String.valueOf(dieukhoan.getId()));
        String lvText = getLinhvuc(String.valueOf(dieukhoan.getId()));
        String dtText = getDoituong(String.valueOf(dieukhoan.getId()));

        if (mpText.length() > 0) {
            helper.showSection(mucphatView);
            mucphatDetails.setText(mpText);
            contentString += "\nMức phạt: " + mpText;
        } else {
            helper.hideSection(mucphatView);
        }
        if (ptText.length() > 0) {
            helper.showSection(phuongtienView);
            phuongtienDetails.setText(ptText);
            contentString += "\nPhương tiện: " + ptText;
        } else {
            helper.hideSection(phuongtienView);
        }
        if (lvText.length() > 0) {
            helper.showSection(linhvucView);
            linhvucDetails.setText(lvText);
            contentString += "\nLĩnh vực: " + lvText;
        } else {
            helper.hideSection(linhvucView);
        }
        if (dtText.length() > 0) {
            helper.showSection(doituongView);
            doituongDetails.setText(dtText);
            contentString += "\nĐối tượng: " + dtText;
        } else {
            helper.hideSection(doituongView);
        }
    }

    private void fillMinhhoaToViewMinhhoa(ArrayList<String> images) {
        hideMinhhoaView(false);

        for (String img : images) {
            String imgName = img.replace("\n", "").trim();
            if (imgName.length() < 1) {

            } else {
                Bitmap image = helper.getBitmapFromAssets(this, "minhhoa/" + imgName);

                ImageView imgView = new ImageView(this);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(layoutParams);

                imgView.setImageBitmap(helper.scaleImage(image, helper.getScreenWidth()));
                minhhoaView.addView(imgView);
            }
        }
    }

    //TODO: Populate thamquyen
    private ArrayList<Dieukhoan> getThamquyenList() {
        ArrayList<Dieukhoan> thamquyen = new ArrayList<>();

        return thamquyen;
    }

    private ArrayList<Dieukhoan> getTamgiuPhuongtienList() {
        String qry = "select distinct dk.id as dkId, dk.so as dkSo, tieude as dkTieude, dk.noidung as dkNoidungString" +
                ", minhhoa as dkMinhhoa, cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, vanbanid as vbId" +
                ", vb.ten as vbTen, nam as vbNam, ma as vbMa, vb.noidung as vbNoidung, coquanbanhanh as vbCoquanbanhanhId" +
                ", cq.ten as cqTen, dk.forSearch as dkSearch from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id " +
                "join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id " +
                "join tblRelatedDieukhoan as rdk on dk.id = rdk.dieukhoanId where (dkCha = " + GeneralSettings.getTamgiuPhuongtienDieukhoanID(dieukhoan.getVanban().getId()) +
                " or dkCha in (select id from tblchitietvanban where cha = " + GeneralSettings.getTamgiuPhuongtienDieukhoanID(dieukhoan.getVanban().getId()) + ")" +
                " or dkCha in (select id from tblchitietvanban where cha in (select id from tblchitietvanban where cha = " +
                GeneralSettings.getTamgiuPhuongtienDieukhoanID(dieukhoan.getVanban().getId()) + "))) and (rdk.relatedDieukhoanID = " + dieukhoan.getId() +
                " or rdk.relatedDieukhoanID = " + dieukhoan.getCha() + " or rdk.relatedDieukhoanID = (select cha from " +
                "tblchitietvanban where id = " + dieukhoan.getCha() + "))";
        ArrayList<Dieukhoan> tamgiuList = queries.searchDieukhoanByQuery(qry, vanbanid);
        return tamgiuList;
    }

    private String getMucphat(String id) {
        return queries.searchMucphatInfo(id);
    }

    private String getPhuongtien(String id) {
        return queries.searchPhuongtienInfo(id);
    }

    private String getLinhvuc(String id) {
        return queries.searchLinhvucInfo(id);
    }

    private String getDoituong(String id) {
        return queries.searchDoituongInfo(id);
    }

    private ArrayList<Dieukhoan> getChildren(String keyword) {
        return queries.searchChildren(keyword.trim(), vanbanid);
    }

    private ArrayList<Dieukhoan> getParent(String keyword) {
        return queries.searchDieukhoanByID(keyword, vanbanid);
    }

    public String getFullText() {
        return contentString;
    }

}
