package com.vietlh.wethoong;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.RedirectionHelper;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.util.HashMap;

public class ChungtoiActivity extends AppCompatActivity {
    private ImageView imgFounder;
    private TextView lblFounderFB;
    private TextView lblFounderEmail;
    private UtilsHelper helper;
    private RedirectionHelper redirectionHelper;
    private Button btnAdsOptout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chungtoi);
        helper = new UtilsHelper();
        redirectionHelper = new RedirectionHelper();
        initFounderInfo();
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

    private void initFounderInfo() {
        imgFounder = (ImageView) findViewById(R.id.founderImage);
        lblFounderFB = (TextView) findViewById(R.id.founderFBDetails);
        lblFounderFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, HashMap<String, String>> urlSets = new HashMap<>();
                redirectionHelper.initFBUrlSets(urlSets, 0, getResources().getString(R.string.wethoongFB), getResources().getString(R.string.wethoongFBApp));
                redirectionHelper.initFBUrlSets(urlSets, 1, getResources().getString(R.string.condonghieuluatFB), getResources().getString(R.string.condonghieuluatFBApp));
                redirectionHelper.openFacebook(getApplicationContext(), urlSets);
            }
        });
        lblFounderEmail = (TextView) findViewById(R.id.founderEDetails);
        lblFounderEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectionHelper.openUrlInExternalBrowser(getApplicationContext(), "mailto:" + lblFounderFB.getText().toString());
            }
        });

        Bitmap bitmap = helper.getBitmapFromAssets(this, "partners/vietcat_crop.jpg");
        imgFounder.setImageBitmap(bitmap);
        ViewGroup.LayoutParams parms = imgFounder.getLayoutParams();
        parms.width = (int) (helper.getScreenWidth() * 0.25);
        parms.height = parms.width;
        imgFounder.setLayoutParams(parms);

        btnAdsOptout =(Button)findViewById(R.id.btnAdsOptout);
        btnAdsOptout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCouponScreen();
            }
        });
        if (GeneralSettings.isAdsOptout){
            btnAdsOptout.setEnabled(false);
            btnAdsOptout.setTextColor(getResources().getColor(R.color.grayout));
        }else {
//            Disable the Ads Optout button
//            btnAdsOptout.setEnabled(true);
        }
    }

    private void openCouponScreen() {
        Intent i = new Intent(getApplicationContext(), CouponRedeemActivity.class);
        startActivity(i);
    }
}
