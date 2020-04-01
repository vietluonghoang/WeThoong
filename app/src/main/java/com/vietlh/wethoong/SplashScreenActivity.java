package com.vietlh.wethoong;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vietlh.wethoong.entities.interfaces.CallbackActivity;

public class SplashScreenActivity extends AppCompatActivity implements CallbackActivity {
    private int splashTime = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        System.out.println("===== SplashScreenActivity: onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("===== SplashScreenActivity: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(splashTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });

        System.out.println("===== SplashScreenActivity: onResume");
        System.out.println("===== SplashScreenActivity: onResume ---- Opening Homescreen");

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("===== SplashScreenActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("===== SplashScreenActivity: onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("===== SplashScreenActivity: onRestart");
        finishAffinity();
        System.exit(0);
//        System.out.println("===== SplashScreenActivity: onRestart ---- Opening Homescreen");
//        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//        startActivity(i);
    }

    @Override
    public void triggerCallbackAction(String actionCase) {

    }
}
