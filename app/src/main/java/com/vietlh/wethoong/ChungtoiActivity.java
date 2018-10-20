package com.vietlh.wethoong;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vietlh.wethoong.utils.UtilsHelper;

public class ChungtoiActivity extends AppCompatActivity {
    private ImageView imgFounder;
    private TextView lblFounderFB;
    private TextView lblFounderEmail;
    private UtilsHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chungtoi);
        helper = new UtilsHelper();
        initFounderInfo();
    }

    private void initFounderInfo(){
        imgFounder = (ImageView)findViewById(R.id.founderImage);
        lblFounderFB = (TextView)findViewById(R.id.founderFBDetails);
        lblFounderFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.openUrlInExternalBrowser(getApplicationContext(),lblFounderFB.getText().toString());
            }
        });
        lblFounderEmail = (TextView)findViewById(R.id.founderEDetails);
        lblFounderEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.openUrlInExternalBrowser(getApplicationContext(),"mailto:" + lblFounderFB.getText().toString());
            }
        });

        Bitmap bitmap = helper.getBitmapFromAssets(this, "partners/vietcat_crop.jpg");
        imgFounder.setImageBitmap(bitmap);
        ViewGroup.LayoutParams parms = imgFounder.getLayoutParams();
        parms.width = (int)(helper.getScreenWidth() * 0.25);
        parms.height = parms.width;
        imgFounder.setLayoutParams(parms);
    }
}