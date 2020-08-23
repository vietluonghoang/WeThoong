package com.vietlh.wethoong;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.RedirectionHelper;

public class UnderconstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_underconstruction);
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
}
