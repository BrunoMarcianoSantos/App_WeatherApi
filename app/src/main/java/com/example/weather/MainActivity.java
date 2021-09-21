package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CardView cardSearch, cardLocal, cardSensor, cardWeather, cardApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardSearch = findViewById(R.id.cardSearch);
        cardLocal = findViewById(R.id.cardLocal);
        cardSensor = findViewById(R.id.cardSensor);
        cardWeather = findViewById(R.id.cardWeather);
        cardApi = findViewById(R.id.cardApi);

        cardSearch.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SearchWeather.class);
            startActivity(i);
        });

        cardLocal.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LocalWeather.class);
            startActivity(i);
        });

        cardSensor.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SensorWeather.class);
            startActivity(i);
        });

        cardApi.setOnClickListener(v -> Toast("Em desenvolvimento"));

        cardWeather.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, DisplayWeather.class);
            startActivity(i);
        });
    }

    public void Toast (String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}