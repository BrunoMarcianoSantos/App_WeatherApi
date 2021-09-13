package com.example.weather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DisplayWeather extends AppCompatActivity {

    RecyclerView recyclerView;
    CustomAdapter customAdapter;
    DataBaseHelper db;
    RecyclerView.LayoutManager layoutManager;
    List<Weather> weathers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_weather);

        db = new DataBaseHelper(this);
        weathers = db.returnWeather();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        customAdapter = new CustomAdapter(this, weathers, recyclerView, DisplayWeather.this);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

}