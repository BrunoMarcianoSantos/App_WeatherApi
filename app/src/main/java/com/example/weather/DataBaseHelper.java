package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "dbWeather", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_weather (weatherid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "city varchar(70), country varchar(3), tempnow varchar(30), tempmax varchar(40), " +
                "tempmin varchar(40), condition varchar(70), sunrise varchar(20), sunset varchar(20), " +
                "wind varchar(15), pressure varchar(15), humidity varchar(15), feels varchar(30), " +
                "icon varchar(15), current varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbl_weather");
        onCreate(db);
    }

    public void addWeather(Weather weather){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("city", weather.getCity());
        cv.put("country", weather.getCountry());
        cv.put("tempnow", weather.getTempNow());
        cv.put("tempmax", weather.getTempMax());
        cv.put("tempmin", weather.getTempMin());
        cv.put("condition", weather.getCondition());
        cv.put("sunrise", weather.getSunrise());
        cv.put("sunset", weather.getSunset());
        cv.put("wind", weather.getWind());
        cv.put("pressure", weather.getPressure());
        cv.put("humidity", weather.getHumidity());
        cv.put("feels", weather.getFeels());
        cv.put("icon", weather.getIcon());
        cv.put("current", weather.getDate());
        long result = db.insert("tbl_weather", null, cv);
         if(result == -1){
             Toast.makeText(context, "Ops! Algum erro ocorreu, tente novamente!", Toast.LENGTH_SHORT).show();
         }else {
             Toast.makeText(context, "Clima de " + weather.getCity() + " foi salvo", Toast.LENGTH_SHORT).show();
         }
    }

    public List<Weather> returnWeather(){
        List<Weather> weathers = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("tbl_weather", new String[]{"weatherid", "city", "country", "tempNow", "tempMax", "tempMin", "condition",
                "sunrise", "sunset", "wind", "pressure", "humidity", "feels", "icon", "current"},null,
                null, null, null, null );
        while(cursor.moveToNext()){
            Weather cli = new Weather();
            cli.setId(cursor.getInt(0));
            cli.setCity(cursor.getString(1));
            cli.setCountry(cursor.getString(2));
            cli.setTempNow(cursor.getString(3));
            cli.setTempMax(cursor.getString(4));
            cli.setTempMin(cursor.getString(5));
            cli.setCondition(cursor.getString(6));
            cli.setSunrise(cursor.getString(7));
            cli.setSunset(cursor.getString(8));
            cli.setWind(cursor.getString(9));
            cli.setPressure(cursor.getString(10));
            cli.setHumidity(cursor.getString(11));
            cli.setFeels(cursor.getString(12));
            cli.setIcon(cursor.getString(13));
            cli.setDate(cursor.getString(14));
            weathers.add(cli);
        }
        return weathers;
    }

    public void deleteOneRow(int weatherid){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("tbl_weather", "weatherid " + "=" + weatherid, null);
        if(result == -1){
            Toast.makeText(context, "Ops! Algum erro ocorreu, tente novamente!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Clima deletado", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tbl_weather", null, null);
    }
}
