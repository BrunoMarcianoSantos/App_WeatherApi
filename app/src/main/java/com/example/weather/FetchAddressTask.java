package com.example.weather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String[]> {

    private Context mContext;
    private OnTaskCompleted mListener;


    FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    private final String TAG = FetchAddressTask.class.getSimpleName();

    @Override
    protected String[] doInBackground(Location... params) {
        Geocoder geocoder = new Geocoder(mContext,
                Locale.getDefault());

        Location location = params[0];
        List<Address> addresses = null;
        String[] resultMessage = new String[2];
        resultMessage[0]="";
        resultMessage[1]="";

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),

                    1);

        } catch (IOException ioException) {
            resultMessage[0] = "Serviço indisponível";
            Log.e(TAG, resultMessage[0], ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            resultMessage[0] = "Latitude ou longitude inválida";
            Log.e(TAG, resultMessage[0] + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (resultMessage[0].isEmpty()) {
                resultMessage[0] = "Endereço não encontrado";
                Log.e(TAG, resultMessage[0]);
            }
        } else {
            Address address = addresses.get(0);
            resultMessage[0] = address.getSubAdminArea();
            resultMessage[1] = address.getSubLocality();
        }
        return resultMessage;
    }

    @Override
    protected void onPostExecute(String[] address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(String[] result);
    }
}
