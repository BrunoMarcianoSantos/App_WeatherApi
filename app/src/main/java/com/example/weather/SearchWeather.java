package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchWeather extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    EditText searchcity;
    TextView condition, temp, mintemp, maxtemp,
    sunrise, sunset, wind, pressure, humidity, feels;
    ImageView iconimg;
    ProgressBar pb;
    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_weather);

        pb = findViewById(R.id.loader);
        rl = findViewById(R.id.mainContainer);
        searchcity = findViewById(R.id.searchcity);
        condition = findViewById(R.id.condition);
        temp = findViewById(R.id.temp);
        mintemp = findViewById(R.id.temp_min);
        maxtemp = findViewById(R.id.temp_max);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        wind = findViewById(R.id.wind);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        feels = findViewById(R.id.feels);
        iconimg = findViewById(R.id.weathericon);

        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    public void Search(View view){
        String city = searchcity.getText().toString();

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
            //nmTempmax.setText(R.string.loading);
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
        pb.setVisibility(View.VISIBLE);
        rl.setVisibility(View.GONE);
        String queryString = "";
        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new RequestHTTP.CarregaClima(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("weather");


            JSONObject weather = itemsArray.getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject winds = jsonObject.getJSONObject("wind");
            JSONObject sys = jsonObject.getJSONObject("sys");


            double tempatual = main.getDouble("temp");
            double tempmax = main.getDouble("temp_max");
            double tempmin = main.getDouble("temp_min");
            String cond = weather.getString("description");
            String icon = weather.getString("icon");
            double like = main.getDouble("feels_like");
            int umid = main.getInt("humidity");
            float press = main.getInt("pressure");
            String zone = jsonObject.getString("timezone");
            long set = sys.getLong("sunset");
            long suns = sys.getLong("sunrise");
            String speed = winds.getString("speed");

            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@4x.png";
                Picasso.get().load(iconUrl).into(iconimg);
            }catch(Exception e){
                Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            }

            temp.setText(String.format("%.0f", tempatual) + "°C");
            maxtemp.setText("Máxima: " + String.format("%.0f", tempmax) + "°C");
            mintemp.setText("Mínima: " + String.format("%.0f", tempmin) + "°C");
            String condf = cond.substring(0, 1).toUpperCase() + cond.substring(1).toLowerCase();
            condition.setText(condf);
            wind.setText(speed + " M/s");
            feels.setText(String.format("%.0f", like) + "°C");
            humidity.setText(umid + "%");
            pressure.setText(press + "hPa");
            String suna = RequestHTTP.unixConvert(suns);
            sunrise.setText(suna);
            String seta = RequestHTTP.unixConvert(set);
            sunset.setText(seta);

            pb.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}