package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocalWeather extends AppCompatActivity implements FetchAddressTask.OnTaskCompleted,
        LoaderManager.LoaderCallbacks<String> {

    String condflocal, sunalocal, setalocal, tempatualflocal, tempmaxflocal, tempminflocal,
            speedflocal, likeflocal, umidflocal, pressflocal, currentDatelocal, countrylocal,
            namelocal, iconlocal;
    int tempapif;
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
        } else {
            if (city.length() == 0) {
                Toast.makeText(this, "Ops, a cidade não foi encontrada, tente novamente!", Toast.LENGTH_LONG).show();
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
            iconlocal = weather.getString("icon");
            double like = main.getDouble("feels_like");
            int umid = main.getInt("humidity");
            float press = main.getInt("pressure");
            long set = sys.getLong("sunset");
            long suns = sys.getLong("sunrise");
            String speed = winds.getString("speed");
            countrylocal = sys.getString("country");
            namelocal = jsonObject.getString("name");

            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + iconlocal + "@4x.png";
                Picasso.get().load(iconUrl).into(iconimg);
            } catch (Exception e) {
                Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            }

            condflocal = cond.substring(0, 1).toUpperCase() + cond.substring(1).toLowerCase();
            sunalocal = RequestHTTP.unixConvert(suns);
            setalocal = RequestHTTP.unixConvert(set);
            String tempatualapilocal = String.format("%.0f", tempatual);
            tempapif = Integer.parseInt(tempatualapilocal);
            tempatualflocal = String.format("%.0f", tempatual) + "°C";
            tempmaxflocal = "Máxima: " + String.format("%.0f", tempmax) + "°C";
            tempminflocal = "Mínima: " + String.format("%.0f", tempmin) + "°C";
            speedflocal = speed + " M/s";
            likeflocal = String.format("%.0f", like) + "°C";
            umidflocal = umid + "%";
            pressflocal = press + "hPa";
            currentDatelocal = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());

            citycountry.setText(namelocal + ", " + countrylocal);
            temp.setText(tempatualflocal);
            maxtemp.setText(tempmaxflocal);
            mintemp.setText(tempminflocal);
            condition.setText(condflocal);
            wind.setText(speedflocal);
            feels.setText(likeflocal);
            humidity.setText(umidflocal);
            pressure.setText(pressflocal);
            sunrise.setText(sunalocal);
            sunset.setText(setalocal);


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

    public void SaveDbWeatherLocal(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Salvar ?");
        builder.setMessage("Salvar esse clima ?");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            try {
                DataBaseHelper db = new DataBaseHelper(this);
                Weather w = new Weather();
                //w.setId(++increment);
                w.setCity(namelocal.trim());
                w.setCountry(countrylocal.trim());
                w.setTempNow(tempatualflocal.trim());
                w.setTempMax(tempmaxflocal.trim());
                w.setTempMin(tempminflocal.trim());
                w.setCondition(condflocal.trim());
                w.setSunrise(sunalocal.trim());
                w.setSunset(setalocal.trim());
                w.setWind(speedflocal.trim());
                w.setPressure(pressflocal.trim());
                w.setHumidity(umidflocal.trim());
                w.setFeels(likeflocal.trim());
                w.setIcon(iconlocal.trim());
                w.setDate(currentDatelocal.trim());
                db.addWeather(w);
            }catch(Exception e){
                Toast.makeText(this, "Erro" + e, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Não", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    public void SendApiLocal(View view){
        Intent intent = new Intent(this, SendWeather.class);
        intent.putExtra("tempnow", tempapif);
        intent.putExtra("condition", condflocal);
        intent.putExtra("name", namelocal);
        this.startActivityForResult(intent, 1);
    }
}