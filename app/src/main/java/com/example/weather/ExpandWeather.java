package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;

import com.squareup.picasso.Picasso;

public class ExpandWeather extends AppCompatActivity {

    TextView expandcity, expanddate, expandsunrise, expandsunset, expandwind, expandpressure,
    expandhumidity, expandfeels, expandtempnow, expandtempmax, expandtempmin, expandcondition,
    expandid;
    ImageView expandicon;
    Button dell, share;

    CardView cd;

    String city;
    String country;
    String tempnow;
    String tempmax;
    String tempmin;
    String condition;
    String sunrise;
    String sunset;
    String wind;
    String pressure;
    String humidity;
    String feels;
    String icon;
    String date;
    Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_weather);

        expandcity = findViewById(R.id.expandcity);
        expanddate = findViewById(R.id.expandate);
        expandsunrise = findViewById(R.id.expandsunrise);
        expandsunset = findViewById(R.id.expandsunset);
        expandwind = findViewById(R.id.expandwind);
        expandpressure = findViewById(R.id.expandpressure);
        expandhumidity = findViewById(R.id.expandhumidity);
        expandfeels = findViewById(R.id.expandfeels);
        expandtempnow = findViewById(R.id.expandtempnow);
        expandtempmax = findViewById(R.id.expandtempmax);
        expandtempmin = findViewById(R.id.expandotempmin);
        expandcondition = findViewById(R.id.expandcondition);
        expandid = findViewById(R.id.expandid);
        expandicon = findViewById(R.id.expandicon);
        dell = findViewById(R.id.deleteweather);
        share = findViewById(R.id.shareweather);
        cd = findViewById(R.id.cardexpand);

        extrasWeather();
    }

    public void extrasWeather(){
        if(getIntent().hasExtra("city") && getIntent().hasExtra("country") &&
                getIntent().hasExtra("tempnow") && getIntent().hasExtra("tempmax") &&
                getIntent().hasExtra("tempmin") && getIntent().hasExtra("condition") &&
                getIntent().hasExtra("sunrise") && getIntent().hasExtra("sunset") &&
                getIntent().hasExtra("wind") && getIntent().hasExtra("pressure") &&
                getIntent().hasExtra("humidity") && getIntent().hasExtra("feels") &&
                getIntent().hasExtra("icon") && getIntent().hasExtra("date")
                && getIntent().hasExtra("weatherid")){
            city = getIntent().getStringExtra("city");
            country = getIntent().getStringExtra("country");
            tempnow = getIntent().getStringExtra("tempnow");
            tempmax = getIntent().getStringExtra("tempmax");
            tempmin = getIntent().getStringExtra("tempmin");
            condition = getIntent().getStringExtra("condition");
            sunrise = getIntent().getStringExtra("sunrise");
            sunset = getIntent().getStringExtra("sunset");
            wind = getIntent().getStringExtra("wind");
            pressure = getIntent().getStringExtra("pressure");
            humidity = getIntent().getStringExtra("humidity");
            feels = getIntent().getStringExtra("feels");
            icon = getIntent().getStringExtra("icon");
            date = getIntent().getStringExtra("date");
            id = getIntent().getIntExtra("weatherid", 0);

            expandcity.setText(city + ", " + country);
            expandcondition.setText(condition);
            expanddate.setText(date);
            expandfeels.setText(feels);
            expandhumidity.setText(humidity);
            expandid.setText("Código: " + String.valueOf(id));
            expanddate.setText(date);
            expandpressure.setText(pressure);
            expandsunrise.setText(sunrise);
            expandsunset.setText(sunset);
            expandtempmax.setText(tempmax);
            expandtempmin.setText(tempmin);
            expandtempnow.setText(tempnow);
            expandwind.setText(wind);
            Toast.makeText(ExpandWeather.this, "Qual" + id, Toast.LENGTH_SHORT).show();

            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@4x.png";
                Picasso.get().load(iconUrl).into(expandicon);
            }catch(Exception e){
                Toast.makeText(ExpandWeather.this, "Erro" + e, Toast.LENGTH_SHORT).show();
            }
            Log.d("Clima", city+" "+id+" "+wind);
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteWeather(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apagar " + city + " ?");
        builder.setMessage("Confimar exclusão do clima da cidade " + city + " ?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            DataBaseHelper db = new DataBaseHelper(ExpandWeather.this);
            db.deleteOneRow(id);
            finish();
        });
        builder.setNegativeButton("Não", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    public void shareWeather(View v){
        Bitmap bitmap = takeScreenshot(cd);
        shareIt(bitmap);
    }

    public Bitmap takeScreenshot(View view) {
        View v = view;
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        return bitmap;
    }

    private void shareIt(Bitmap bitmap) {
        String pathofBmp=
                MediaStore.Images.Media.insertImage(this.getContentResolver(),
                        bitmap,"Clima", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Star App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(shareIntent, "Enviar Clima"));
    }
}