package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class StatisticWeather extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    EditText txtcity;
    TextView txtid, txtdisc, txtconc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_weather);

        txtcity = findViewById(R.id.searchcityapi);
        txtid = findViewById(R.id.txtidapi);
        txtdisc = findViewById(R.id.txtdiscrepancia);
        txtconc = findViewById(R.id.txtconcordancia);

        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    public void SearchApi(View view){
        String city = txtcity.getText().toString();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()
                && city.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", city);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            Toast.makeText(this, "Aguarde...", Toast.LENGTH_SHORT).show();
        } else {
            if (city.length() == 0) {
                Toast.makeText(this, "Digite uma cidade válida!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Verifique a conexão com a internet!", Toast.LENGTH_LONG).show();
            }
        }

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";
        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new RequestHTTP.CarregaApi(this, queryString);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);


            int id = jsonObject.getInt("id");
            String nome = jsonObject.getString("nome");
            int disc = jsonObject.getInt("discrepancias");
            int conc = jsonObject.getInt("concordancias");

            /*txtid.setText(id);*/ txtid.setText(String.valueOf(id));
            /*txtconc.setText(conc);*/ txtconc.setText(String.valueOf(conc));
            /*txtdisc.setText(disc);*/ txtdisc.setText(String.valueOf(disc));
        } catch (Exception e) {
            Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}