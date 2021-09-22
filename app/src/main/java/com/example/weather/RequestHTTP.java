package com.example.weather;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class RequestHTTP {

    private static final String LOG_TAG = RequestHTTP.class.getSimpleName();
    private static final String CLIMA_URL = "https://api.openweathermap.org/data/2.5/weather?";
    private static final String QUERY = "q";
    private static final String UNITS = "units";
    private static final String LANG = "lang";
    private static final String KEY = "appid";

    public static String buscaClima(String query) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            Uri climaURI = Uri.parse(CLIMA_URL).buildUpon()
                    .appendQueryParameter(QUERY, query)
                    .appendQueryParameter(UNITS, "metric")
                    .appendQueryParameter(LANG, "pt_br")
                    .appendQueryParameter(KEY, "8e7deb9f1c6d658eb040ee0e7fb272de")
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
            if (builder.length() == 0) {
                return null;
            }
            bookJSONString = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
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
        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }

    public static String buscaApi(String query) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            Uri climaURI = Uri.parse("http://192.168.15.9:58838/Local/getLocal?").buildUpon()
                    .appendQueryParameter("nome", query)
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
            if (builder.length() == 0) {
                return null;
            }
            bookJSONString = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
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
        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }

    public static class CarregaClima extends AsyncTaskLoader<String> {
        private String mQueryString;

        CarregaClima(Context context, String queryString) {
            super(context);
            mQueryString = queryString;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public String loadInBackground() {
            return buscaClima(mQueryString);
        }
    }

    public static class CarregaApi extends AsyncTaskLoader<String> {
        private String mQueryString;

        CarregaApi(Context context, String queryString) {
            super(context);
            mQueryString = queryString;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public String loadInBackground() {
            return buscaApi(mQueryString);
        }
    }

    public static String unixConvert(long unix){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime(unix*1000);
        return dateFormat.format(date);
    }
}
