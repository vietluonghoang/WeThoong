package com.vietlh.wethoong;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.vietlh.wethoong.adapters.ListRecyclerViewAdapter;
import com.vietlh.wethoong.entities.BosungKhacphuc;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.AdsHelper;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;
import com.vietlh.wethoong.utils.SearchFor;
import com.vietlh.wethoong.utils.UtilsHelper;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private Queries queries = new Queries(DBConnection.getInstance(this));
    private UtilsHelper helper = new UtilsHelper();
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
    private Button btnXemthem;

    private LinearLayout adsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getPassingParameters();
        initComponents();
        dieukhoan = queries.searchDieukhoanByID(dieukhoanId,vanbanid).get(0);
        vanbanid.add(String.valueOf(dieukhoan.getVanban().getId()));
        updateDetails();

        showDieukhoan();

        if(GeneralSettings.isAdsEnabled) {
            initAds();
        }
    }

    private void getPassingParameters(){
        dieukhoanId = (String) getIntent().getStringExtra("dieukhoanId");
    }

    private void initComponents(){
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        btnXemthemView = (ConstraintLayout)findViewById(R.id.btnXemthemView);

        lblVanban = (TextView)findViewById(R.id.lblVanban);
        lblDieukhoan = (TextView)findViewById(R.id.lblDieukhoan);
        lblNoidung = (TextView)findViewById(R.id.lblNoidung);
        mucphatDetails = (TextView)findViewById(R.id.mucphatDetails);
        phuongtienDetails = (TextView)findViewById(R.id.phuongtienDetails);
        linhvucDetails = (TextView)findViewById(R.id.linhvucDetails);
        doituongDetails = (TextView)findViewById(R.id.doituongDetails);
        hinhphatbosungDetails = (TextView)findViewById(R.id.hinhphatbosungDetails);
        bienphapkhacphucDetails = (TextView)findViewById(R.id.bienphapkhacphucDetails);
        tamgiuDetails = (TextView)findViewById(R.id.tamgiuDetails);
        thamquyenDetails = (TextView)findViewById(R.id.thamquyenDetails);

        btnXemthem = (Button)findViewById(R.id.btnXemthem);
        btnXemthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRelatedDieukhoan();
            }
        });
        btnBreadscrubs = (Button)findViewById(R.id.btnBreadscrubs);
        btnBreadscrubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Message","tapping on: "+ parentDieukhoan.getId());
                Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                //TODO: need to change the hardcode dieukhoanId to something that configurable.
                i.putExtra("dieukhoanId", String.valueOf(parentDieukhoan.getId()));
                startActivity(i);
            }
        });

        extraView = (LinearLayout)findViewById(R.id.extraView);
        mucphatView = (LinearLayout)findViewById(R.id.mucphatView);
        phuongtienView = (LinearLayout)findViewById(R.id.phuongtienView);
        linhvucView = (LinearLayout)findViewById(R.id.linhvucView);
        doituongView = (LinearLayout)findViewById(R.id.doituongView);
        hinhphatbosung = (LinearLayout)findViewById(R.id.hinhphatbosung);
        bienphapkhacphuc = (LinearLayout)findViewById(R.id.bienphapkhacphuc);
        tamgiu = (LinearLayout)findViewById(R.id.tamgiu);
        thamquyen = (LinearLayout)findViewById(R.id.thamquyen);
        minhhoaView = (LinearLayout)findViewById(R.id.minhhoaView);
        childrenDieukhoan = (RelativeLayout)findViewById(R.id.childrenDieukhoan);

        rclChildrenDieukhoan = (RecyclerView) findViewById(R.id.rclChildrenDieukhoan);
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

    private void showRelatedDieukhoan(){
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType","lienquan");
        i.putExtra("dieukhoanId",dieukhoanId);
        startActivity(i);
    }

    private void hideMinhhoaView(Boolean isHidden) {
        if(isHidden){
            helper.hideSection(minhhoaView);
        }else{
            helper.showSection(minhhoaView);
        }
    }

    private void hideExtraInfoView(Boolean isHidden)  {
        if(isHidden){
            helper.hideSection(extraView);
            populateExtraInfoView();
        }else{
            helper.showSection(extraView);
        }
    }

    private void showDieukhoan() {
        lblVanban.setText(dieukhoan.getVanban().getMa());
        lblDieukhoan.setText(dieukhoan.getSo());
        String breadscrubText = search.getAncestersNumber(dieukhoan, vanbanid);
        if (breadscrubText.length() > 0) {
            btnBreadscrubs.setText(breadscrubText);
            btnBreadscrubs.setEnabled(true);
        }else {
            btnBreadscrubs.setVisibility(View.GONE);
        }

        String noidung = "";
        if(dieukhoan.getTieude().length() < 1){
            noidung = dieukhoan.getNoidung();
        }else if (dieukhoan.getNoidung().length() < 1){
            noidung = dieukhoan.getTieude();
        }else {
            noidung = dieukhoan.getTieude() + "\n" + dieukhoan.getNoidung();
        }
        lblNoidung.setText(noidung);

        ArrayList<String> images = dieukhoan.getMinhhoa();

        if(images.size() > 0){
            fillMinhhoaToViewMinhhoa(images);
        }else{
            hideMinhhoaView(true);
        }

        // Enable extra section for details of ND46
        if (String.valueOf(dieukhoan.getVanban().getId()).equals(GeneralSettings.getVanbanInfo(GeneralSettings.danhsachvanban[0],"id"))) {
            hideExtraInfoView(false);
            populateExtraInfoView();
        }else{
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

        if(relatedChildren.size() < 1){
            btnXemthem.setVisibility(View.GONE);
            helper.hideSection(btnXemthemView);
        }

        if(children.size() > 0) {
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
        }else {
            helper.hideSection(childrenDieukhoan);
        }
    }

    private void populateBosungKhacphucView(){
        if (hinhphatbosungList.size() > 0) {
            helper.showSection(hinhphatbosung);
            String bosungDetails = "";
            for (BosungKhacphuc bosung: hinhphatbosungList) {
                bosungDetails += bosung.getNoidung() + "\n";
            }
            hinhphatbosungDetails.setText(bosungDetails);
        } else {
            helper.hideSection(hinhphatbosung);
        }

        if (bienphapkhacphucList.size() > 0) {
            helper.showSection(bienphapkhacphuc);
            String khacphucDetails = "";
            for (BosungKhacphuc khacphuc: bienphapkhacphucList) {
                khacphucDetails += khacphuc.getNoidung() + "\n";
            }
            bienphapkhacphucDetails.setText(khacphucDetails);
        } else {
            helper.hideSection(bienphapkhacphuc);
        }

        if (tamgiuphuongtienList.size() > 0) {
            helper.showSection(tamgiu);
            tamgiuDetails.setText("07 ngày");
        } else {
            helper.hideSection(tamgiu);
        }

        if (thamquyenList.size() > 0) {
            helper.showSection(thamquyen);
            String tqDetails = "";
            for (Dieukhoan tq: thamquyenList) {
                tqDetails += tq.getNoidung() + "\n";
            }
            thamquyenDetails.setText(tqDetails);
        } else {
            helper.hideSection(thamquyen);
        }
    }

    private void populateExtraInfoView(){
        String mpText = getMucphat(String.valueOf(dieukhoan.getId()));
        String ptText = getPhuongtien(String.valueOf(dieukhoan.getId()));
        String lvText = getLinhvuc(String.valueOf(dieukhoan.getId()));
        String dtText = getDoituong(String.valueOf(dieukhoan.getId()));

        if (mpText.length() > 0) {
            helper.showSection(mucphatView);
            mucphatDetails.setText(mpText);
        }else{
            helper.hideSection(mucphatView);
        }
        if (ptText.length() > 0) {
            helper.showSection(phuongtienView);
            phuongtienDetails.setText(ptText);
        }else{
            helper.hideSection(phuongtienView);
        }
        if (lvText.length() > 0) {
            helper.showSection(linhvucView);
            linhvucDetails.setText(lvText);
        }else{
            helper.hideSection(linhvucView);
        }
        if (dtText.length() > 0) {
            helper.showSection(doituongView);
            doituongDetails.setText(dtText);
        }else{
            helper.hideSection(doituongView);
        }
    }

    private void fillMinhhoaToViewMinhhoa(ArrayList<String> images) {
        hideMinhhoaView( false);

        for (String img : images) {
            String imgName = img.replace("\n","").trim();
            if (imgName.length() < 1){

            }else{
                Bitmap image = helper.getBitmapFromAssets(this,"minhhoa/" + imgName);

                ImageView imgView = new ImageView(this);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                imgView.setLayoutParams(layoutParams);

                imgView.setImageBitmap(helper.scaleImage(image,helper.getScreenWidth()));
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
//        String qry = "select distinct dk.id as dkId, dk.so as dkSo, tieude as dkTieude, dk.noidung as dkNoidungString" +
//                 ", minhhoa as dkMinhhoa, cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, vanbanid as vbId" +
//                 ", vb.ten as vbTen, nam as vbNam, ma as vbMa, vb.noidung as vbNoidung, coquanbanhanh as vbCoquanbanhanhId" +
//                 ", cq.ten as cqTen, dk.forSearch as dkSearch from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id " +
//                 "join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id " +
//                 "join tblRelatedDieukhoan as rdk on dk.id = rdk.dieukhoanId where (dkCha = " + GeneralSettings.tamgiuPhuongtienDieukhoanID +
//                 " or dkCha in (select id from tblchitietvanban where cha = " + GeneralSettings.tamgiuPhuongtienDieukhoanID + ") " +
//                 "or dkCha in (select id from tblchitietvanban where cha in (select id from tblchitietvanban where cha = " +
//                 GeneralSettings.tamgiuPhuongtienDieukhoanID + "))) and rdk.relatedDieukhoanID = " + dieukhoanId;
        String qry = "select distinct dk.id as dkId, dk.so as dkSo, tieude as dkTieude, dk.noidung as dkNoidungString" +
                ", minhhoa as dkMinhhoa, cha as dkCha, vb.loai as lvbID, lvb.ten as lvbTen, vb.so as vbSo, vanbanid as vbId" +
                ", vb.ten as vbTen, nam as vbNam, ma as vbMa, vb.noidung as vbNoidung, coquanbanhanh as vbCoquanbanhanhId" +
                ", cq.ten as cqTen, dk.forSearch as dkSearch from tblChitietvanban as dk join tblVanban as vb on dk.vanbanid=vb.id " +
                "join tblLoaivanban as lvb on vb.loai=lvb.id join tblCoquanbanhanh as cq on vb.coquanbanhanh=cq.id " +
                "join tblRelatedDieukhoan as rdk on dk.id = rdk.dieukhoanId where (dkCha = " + GeneralSettings.tamgiuPhuongtienDieukhoanID +
                " or dkCha in (select id from tblchitietvanban where cha = " + GeneralSettings.tamgiuPhuongtienDieukhoanID + ")" +
                " or dkCha in (select id from tblchitietvanban where cha in (select id from tblchitietvanban where cha = " +
                GeneralSettings.tamgiuPhuongtienDieukhoanID + "))) and (rdk.relatedDieukhoanID = " + dieukhoan.getId() +
                " or rdk.relatedDieukhoanID = " + dieukhoan.getCha() + " or rdk.relatedDieukhoanID = (select cha from " +
                "tblchitietvanban where id = " + dieukhoan.getCha() + "))";
        ArrayList<Dieukhoan> tamgiuList = queries.searchDieukhoanByQuery(qry, vanbanid);
        return tamgiuList;
    }

    private String getMucphat(String id){
        return queries.searchMucphatInfo(id);
    }

    private String getPhuongtien(String id){
        return queries.searchPhuongtienInfo(id);
    }

    private String getLinhvuc(String id){
        return queries.searchLinhvucInfo(id);
    }

    private String getDoituong(String id){
        return queries.searchDoituongInfo(id);
    }

    private ArrayList<Dieukhoan> getChildren(String keyword) {
        return queries.searchChildren(keyword.trim(),vanbanid);
    }

    private ArrayList<Dieukhoan> getParent(String keyword){
        return queries.searchDieukhoanByID(keyword, vanbanid);
    }
}
