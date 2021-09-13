package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageButton;
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
import java.util.Locale;

public class SearchWeather extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    String condf, suna, seta, tempatualf, tempmaxf, tempminf, speedf, likef, umidf, pressf,
            currentDate, country, name, icon;
    EditText searchcity;
    ImageButton searchweather;
    TextView condition, temp, mintemp, maxtemp,
    sunrise, sunset, wind, pressure, humidity, feels;
    ImageView iconimg, save, api;
    ProgressBar pb;
    RelativeLayout rl;
    static int increment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_weather);

        pb = findViewById(R.id.loader);
        rl = findViewById(R.id.mainContainer);
        searchcity = findViewById(R.id.searchcity);
        searchweather = findViewById(R.id.searchweather);
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
        save = findViewById(R.id.saveweather);
        api = findViewById(R.id.apiweather);

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

    @SuppressLint("DefaultLocale")
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
            icon = weather.getString("icon");
            double like = main.getDouble("feels_like");
            int umid = main.getInt("humidity");
            float press = main.getInt("pressure");
            long set = sys.getLong("sunset");
            long suns = sys.getLong("sunrise");
            String speed = winds.getString("speed");
            country = sys.getString("country");
            name = jsonObject.getString("name");

            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@4x.png";
                Picasso.get().load(iconUrl).into(iconimg);
            }catch(Exception e){
                Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            }
            condf = cond.substring(0, 1).toUpperCase() + cond.substring(1).toLowerCase();
            suna = RequestHTTP.unixConvert(suns);
            seta = RequestHTTP.unixConvert(set);
            tempatualf = String.format("%.0f", tempatual) + "°C";
            tempmaxf = "Máxima: " + String.format("%.0f", tempmax) + "°C";
            tempminf = "Mínima: " + String.format("%.0f", tempmin) + "°C";
            speedf = speed + " M/s";
            likef = String.format("%.0f", like) + "°C";
            umidf = umid + "%";
            pressf = press + "hPa";
            currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());

            temp.setText(tempatualf);
            maxtemp.setText(tempmaxf);
            mintemp.setText(tempminf);
            condition.setText(condf);
            wind.setText(speedf);
            feels.setText(likef);
            humidity.setText(umidf);
            pressure.setText(pressf);
            sunrise.setText(suna);
            sunset.setText(seta);


            pb.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);
            searchcity.setVisibility(View.GONE);
            searchweather.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
            api.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public void SaveDbWeather(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Salvar ?");
        builder.setMessage("Salvar esse clima ?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            try {
                DataBaseHelper db = new DataBaseHelper(this);
                Weather w = new Weather();
                //w.setId(++increment);
                w.setCity(name.trim());
                w.setCountry(country.trim());
                w.setTempNow(tempatualf.trim());
                w.setTempMax(tempmaxf.trim());
                w.setTempMin(tempminf.trim());
                w.setCondition(condf.trim());
                w.setSunrise(suna.trim());
                w.setSunset(seta.trim());
                w.setWind(speedf.trim());
                w.setPressure(pressf.trim());
                w.setHumidity(umidf.trim());
                w.setFeels(likef.trim());
                w.setIcon(icon.trim());
                w.setDate(currentDate.trim());
                db.addWeather(w);
            }catch(Exception e){
                Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Não", (dialogInterface, i) -> {
        });
        builder.create().show();

    }

    public void SendApi(View view){
        Toast.makeText(this, "Em desenvolvimento", Toast.LENGTH_LONG).show();
    }
}