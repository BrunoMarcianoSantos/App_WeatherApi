package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocalWeather extends AppCompatActivity implements FetchAddressTask.OnTaskCompleted,
        LoaderManager.LoaderCallbacks<String> {

    TextView condition, temp, mintemp, maxtemp, citycountry,
            sunrise, sunset, wind, pressure, humidity, feels;
    ImageView iconimg;
    ProgressBar pb;
    RelativeLayout rl;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private boolean mTrackingLocation;
    private LocationCallback mLocationCallback;
    private String lastCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_weather);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        citycountry = findViewById(R.id.citycountry);
        pb = findViewById(R.id.loader);
        rl = findViewById(R.id.mainContainer);
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

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mTrackingLocation) {
                    new FetchAddressTask(LocalWeather.this, LocalWeather.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };

        pb.setVisibility(View.VISIBLE);
        rl.setVisibility(View.GONE);
        startTrackingLocation();

    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);

        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(600000);
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this,
                            "Permissão negada!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String[] result) {
        if (mTrackingLocation) {
            lastCity = result[0];
            WeatherGps();
        }
    }

    private void WeatherGps(){
        String city = lastCity;

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
            long set = sys.getLong("sunset");
            long suns = sys.getLong("sunrise");
            String speed = winds.getString("speed");
            String country = sys.getString("country");
            String name = jsonObject.getString("name");

            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@4x.png";
                Picasso.get().load(iconUrl).into(iconimg);
            } catch (Exception e) {
                Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            }

            citycountry.setText(name + ", " + country);
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