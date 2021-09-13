package com.example.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    List<Weather> weathers;
    RecyclerView rv;
    Activity activity;

    public CustomAdapter(Context context, List<Weather> weathers, RecyclerView rv, Activity activity){
        this.context = context;
        this.weathers = weathers;
        this.rv = rv;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        Weather w = weathers.get(position);
        holder.txtTempnow.setText(w.getTempNow());
        try {
            String iconUrl = "https://openweathermap.org/img/wn/" + w.getIcon() + "@4x.png";
            Picasso.get().load(iconUrl).into(holder.txtIcon);
        }catch(Exception e){
            Toast.makeText(context, "Erro" + e, Toast.LENGTH_SHORT).show();
        }
        holder.txtCityC.setText(w.getCity() + ", " + w.getCountry());
        holder.txtCondition.setText(w.getCondition());

        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ExpandWeather.class);
            intent.putExtra("city", String.valueOf(w.getCity()));
            intent.putExtra("country", String.valueOf(w.getCountry()));
            intent.putExtra("tempnow", String.valueOf(w.getTempNow()));
            intent.putExtra("tempmax", String.valueOf(w.getTempMax()));
            intent.putExtra("tempmin", String.valueOf(w.getTempMin()));
            intent.putExtra("condition", String.valueOf(w.getCondition()));
            intent.putExtra("sunrise", String.valueOf(w.getSunrise()));
            intent.putExtra("sunset", String.valueOf(w.getSunset()));
            intent.putExtra("wind", String.valueOf(w.getWind()));
            intent.putExtra("pressure", String.valueOf(w.getPressure()));
            intent.putExtra("humidity", String.valueOf(w.getHumidity()));
            intent.putExtra("feels", String.valueOf(w.getFeels()));
            intent.putExtra("icon", String.valueOf(w.getIcon()));
            intent.putExtra("date", String.valueOf(w.getDate()));
            intent.putExtra("weatherid", w.getId());
            activity.startActivityForResult(intent, 1);
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView txtIcon;
        TextView txtCityC;
        TextView txtTempnow;
        TextView txtCondition;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIcon = itemView.findViewById(R.id.imageviewicon);
            txtCityC = itemView.findViewById(R.id.txtcitycountry);
            txtTempnow = itemView.findViewById(R.id.txtTempNow);
            txtCondition = itemView.findViewById(R.id.txtconditionrow);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
