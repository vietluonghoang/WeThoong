package com.vietlh.wethoong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.Queries;

public class HomeActivity extends AppCompatActivity {
    private DBConnection connection;
    private Button btnTracuuvanban;
    private Button btnTracuumucphat;
    private Button btnTracuubienbao;
    private Button btnTracuuvachkeduong;
    private Button btnChungtoilaai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.connection = DBConnection.getInstance(this);
        initComponents();
    }

    private void initComponents(){
        btnTracuuvanban = (Button) findViewById(R.id.btnTracuuvanbanphapluat);
        btnTracuuvanban.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTracuuvanbanScreen();
                    }
                }
        );
        btnTracuumucphat = (Button) findViewById(R.id.btnTracuumucphat);
        btnTracuumucphat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTracuumucphatScreen();
                    }
                }
        );
        btnTracuubienbao = (Button) findViewById(R.id.btnTracuubienbaohieu);
        btnTracuubienbao.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openUnderconstructionScreen();
                    }
                }
        );
        btnTracuuvachkeduong = (Button) findViewById(R.id.btnTracuuvachkeduong);
        btnTracuuvachkeduong.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openUnderconstructionScreen();
                    }
                }
        );
        btnChungtoilaai = (Button) findViewById(R.id.btnChungtoilaai);
        btnChungtoilaai.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
    }
    
    private void openTracuuvanbanScreen() {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType","vanban");
        startActivity(i);
    }

    private void openTracuumucphatScreen() {
        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
        //TODO: need to change the hardcode searchType to something that configurable.
        i.putExtra("searchType","mucphat");
        startActivity(i);
    }

    private void openChungtoiScreen() {
        Intent i = new Intent(getApplicationContext(), ChungtoiActivity.class);
        startActivity(i);
    }

    private void openUnderconstructionScreen() {
        Intent i = new Intent(getApplicationContext(), UnderconstructionActivity.class);
        startActivity(i);
    }
}
