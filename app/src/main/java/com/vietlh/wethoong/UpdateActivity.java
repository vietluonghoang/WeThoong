package com.vietlh.wethoong;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.RedirectionHelper;
import com.vietlh.wethoong.utils.UtilsHelper;

public class UpdateActivity extends AppCompatActivity {

    private Button btnUpdate;
    private ImageView topLogo;
    private UtilsHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        helper = new UtilsHelper();
        initComponents();
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

    private void initComponents() {
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openStore();
                    }
                }
        );
        topLogo = (ImageView) findViewById(R.id.imgTopLogo);
        fillImage();
    }

    private void openStore() {
        System.out.println("Opening store......");
        final String appPackageName = getPackageName(); // package name of the app
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void fillImage() {
        Bitmap bitmap = helper.getBitmapFromAssets(this, "logos/car_1024x1024_transparent.png");
        topLogo.setImageBitmap(bitmap);
    }
}