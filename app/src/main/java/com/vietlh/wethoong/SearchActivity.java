package com.vietlh.wethoong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vietlh.wethoong.adapters.ListRecyclerViewAdapter;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.Queries;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchResultRecyclerView;
    private ListRecyclerViewAdapter searchResultListRecyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ArrayList<Dieukhoan> allDieukhoan;
    private Queries queries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getPassingParameters();
        initResultList();

        searchResultRecyclerView = (RecyclerView) findViewById(R.id.search_result);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        searchResultRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerLayoutManager = new LinearLayoutManager(this);
        searchResultRecyclerView.setLayoutManager(recyclerLayoutManager);

        // specify an adapter (see also next example)
        searchResultListRecyclerAdapter = new ListRecyclerViewAdapter(this,allDieukhoan);
        searchResultRecyclerView.setAdapter(searchResultListRecyclerAdapter);
    }

    private void getPassingParameters(){
        queries = (Queries)getIntent().getSerializableExtra("queries");
    }

    private void initResultList(){
        allDieukhoan = queries.getAllDieukhoan();
    }
}
