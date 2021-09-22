package com.example.weather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsWeather extends AppCompatActivity {

    Button btnweathers, btnapi;
    private DataBaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_weather);

        btnweathers = findViewById(R.id.deleteweathers);
        btnapi = findViewById(R.id.deleteapi);
    }

    public void deleteWeathers(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Tudo?");
        builder.setMessage("Confimar exclusão de todos os climas salvos?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            dbh.deleteAll();
            finish();
        });
        builder.setNegativeButton("Não", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    public void deleteApi(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Tudo?");
        builder.setMessage("Confimar exclusão dos dados sobre a diferença do clima?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            dellLogic();
            finish();
        });
        builder.setNegativeButton("Não", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    public void dellLogic(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            Uri climaURI = Uri.parse("http://192.168.15.9:58838/Local/getLocal?Deleteall").buildUpon()
                    .build();
            URL requestURL = new URL(climaURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String linha;

            while ((linha = reader.readLine()) != null) {
                builder.append(linha);
                builder.append("\n");
            }
            bookJSONString = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}