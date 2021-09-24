package com.example.weather;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.JsonObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendWeather extends AppCompatActivity {

    String condwapi, namewapi, tempapiatualf, condapif, tempapif;
    int tempapiatual, newmax, newmin;
    TextView condapi, tempapi, minseek, maxseek, tempseek, tempsens;
    MaterialButtonToggleGroup toggleone, toggletwo, togglethree;
    Slider sktempatual, sksensacao;
    SwitchMaterial swtsen;
    LinearLayout ltemp, lsens;
    MaterialButton btnsendapi;
    MaterialCheckBox chkcond, chktemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_weather);

        toggleone = findViewById(R.id.toggleone);
        toggletwo = findViewById(R.id.toggletwo);
        togglethree = findViewById(R.id.togglethree);
        condapi = findViewById(R.id.txtcondapi);
        tempapi = findViewById(R.id.txttempapi);
        sktempatual = findViewById(R.id.seektempapi);
        sksensacao = findViewById(R.id.seeksensacaoapi);
        minseek = findViewById(R.id.txtminseek);
        maxseek = findViewById(R.id.txtmaxseek);
        tempseek = findViewById(R.id.txttempseek);
        swtsen = findViewById(R.id.switchsensacao);
        ltemp = findViewById(R.id.linearseektemp);
        lsens = findViewById(R.id.linearseeksensacao);
        tempsens = findViewById(R.id.txttempsens);
        btnsendapi = findViewById(R.id.btnsendapi);
        chkcond = findViewById(R.id.chkcond);
        chktemp = findViewById(R.id.chktemp);

        if(getIntent().hasExtra("tempnow") && getIntent().hasExtra("condition")
                && getIntent().hasExtra("name")){
            tempapiatual = getIntent().getIntExtra("tempnow", 0);
            condwapi = getIntent().getStringExtra("condition");
            namewapi = getIntent().getStringExtra("name");
        }

        chkcond.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()){
                for (int i = 0; i < toggleone.getChildCount(); i++) {
                    View child = toggleone.getChildAt(i);
                    child.setEnabled(false);
                }
                for (int i = 0; i < toggletwo.getChildCount(); i++) {
                    View child = toggletwo.getChildAt(i);
                    child.setEnabled(false);
                }
                for (int i = 0; i < togglethree.getChildCount(); i++) {
                    View child = togglethree.getChildAt(i);
                    child.setEnabled(false);
                }
                condapi.setText("Correto");
            }else{
                for (int i = 0; i < toggleone.getChildCount(); i++) {
                    View child = toggleone.getChildAt(i);
                    child.setEnabled(true);
                }
                for (int i = 0; i < toggletwo.getChildCount(); i++) {
                    View child = toggletwo.getChildAt(i);
                    child.setEnabled(true);
                }
                for (int i = 0; i < togglethree.getChildCount(); i++) {
                    View child = togglethree.getChildAt(i);
                    child.setEnabled(true);
                }
                condapi.setText(" ");
            }
        });

        chktemp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()){
                sktempatual.setEnabled(false);
                sksensacao.setEnabled(false);
                tempapi.setText("Correto");
            }else{
                sktempatual.setEnabled(true);
                sksensacao.setEnabled(true);
                tempapi.setText(" ");
            }
        });

        //Parte dos botões da condição *********************

        toggleone.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.toggleceulimpo) {
                    toggletwo.clearChecked();
                    togglethree.clearChecked();
                    condapi.setText("Céu limpo");
                }
                if (checkedId == R.id.togglealgumasnuves) {
                    toggletwo.clearChecked();
                    togglethree.clearChecked();
                    condapi.setText("Algumas nuvens");
                }
                if (checkedId == R.id.togglenublado) {
                    toggletwo.clearChecked();
                    togglethree.clearChecked();
                    condapi.setText("Nublado");
                }
            }
        });

        toggletwo.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.togglechuvisco) {
                    toggleone.clearChecked();
                    togglethree.clearChecked();
                    condapi.setText("Chuva leve");
                }
                if (checkedId == R.id.togglechuva) {
                    toggleone.clearChecked();
                    togglethree.clearChecked();
                    condapi.setText("Chuva");
                }
                if (checkedId == R.id.togglechuvaforte) {
                    toggleone.clearChecked();
                    togglethree.clearChecked();
                    condapi.setText("Chuva forte");
                }
            }
        });

        togglethree.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.toggletempestade) {
                    toggleone.clearChecked();
                    toggletwo.clearChecked();
                    condapi.setText("Tempestade");
                }
                if (checkedId == R.id.toggleneve) {
                    toggleone.clearChecked();
                    toggletwo.clearChecked();
                    condapi.setText("Neve");
                }
                if (checkedId == R.id.togglenevoa) {
                    toggleone.clearChecked();
                    toggletwo.clearChecked();
                    condapi.setText("Nevoa");
                }
            }
        });

        //Parte da temperatura e sensação ********************

        swtsen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()){
                tempsens.setText("A sensação do clima está:");
                sktempatual.setVisibility(View.GONE);
                ltemp.setVisibility(View.GONE);
                sksensacao.setVisibility(View.VISIBLE);
                lsens.setVisibility(View.VISIBLE);
            }else{
                tempsens.setText("Temperatura:");
                sktempatual.setVisibility(View.VISIBLE);
                ltemp.setVisibility(View.VISIBLE);
                sksensacao.setVisibility(View.GONE);
                lsens.setVisibility(View.GONE);
            }
        });

        try {
            newmax = tempapiatual + 10;
            newmin = tempapiatual - 10;
            sktempatual.setValue(tempapiatual);
            sktempatual.setValueFrom(newmin);
            sktempatual.setValueTo(newmax);

            minseek.setText(String.format("%d °C", newmin));
            maxseek.setText(String.format("%d °C", newmax));
            tempseek.setText(String.format("%d °C", tempapiatual));

        }catch(Exception e){
            Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show();
        }

        sktempatual.addOnChangeListener((slider, value, fromUser) -> {
            if (value == tempapiatual){
                tempapi.setText("Correto");
            }else {
                tempapi.setText(String.format("%.0f", value));
            }

        });
        sksensacao.addOnChangeListener((slider, value, fromUser) -> {
            if (value == 1){
                tempapi.setText("Muito frio");
            }
            if (value == 2){
                tempapi.setText("Frio");
            }
            if (value == 3){
                tempapi.setText("Normal");
            }
            if (value == 4){
                tempapi.setText("Quente");
            }
            if (value == 5){
                tempapi.setText("Muito quente");
            }
        });

        //Botão enviar *******************

        btnsendapi.setOnClickListener(v -> SendApi());
    }

    /*public void SendApi(){
        tempapiatualf = String.valueOf(tempapiatual);
        condapif = condapi.getText().toString();
        tempapif = tempapi.getText().toString();
        if (condapif.equals("Correto")) {
            try {
                if (tempapif.length() != 0) {
                    String url = "https://slaoq.com";
                    String[] dadoscoletados = {namewapi, condwapi, tempapiatualf, condapif, tempapif, url};
                    new HTTPReqTask().execute(dadoscoletados);
                } else {
                    Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show();
            }
        } else if (tempapif.equals("Correto")){
            try {
                if (condapif.length() != 0) {
                    String url = "https://slaoq.com";
                    String[] dadoscoletados = {namewapi, condwapi, tempapiatualf, condapif, tempapif, url};
                    new HTTPReqTask().execute(dadoscoletados);
                } else {
                    Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                if (condapif.length() != 0 && tempapif.length() != 0) {
                    String url = "https://slaoq.com";
                    String[] dadoscoletados = {namewapi, condwapi, tempapiatualf, condapif, tempapif, url};
                    new HTTPReqTask().execute(dadoscoletados);
                } else {
                    Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show();
            }
        }
    }*/

    public void SendApi(){
        condapif = condapi.getText().toString();
        tempapif = tempapi.getText().toString();
        if (condapif.equals("Correto")) {
            try {
                if (tempapif.length() != 0) {
                    condwapi = null;
                    condapif = null;
                    int tempint = Integer.parseInt(tempapif);
                    MyTaskParams params = new MyTaskParams(tempapiatual, tempapif, namewapi, condwapi, condapif);
                    new HTTPReqTask().execute(params);
                } else {
                    Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show();
            }
        } else if (tempapif.equals("Correto")){
            try {
                if (condapif.length() != 0) {
                    tempapiatual = 0;
                    tempapif = null;
                    //String[] dadoscoletados = {namewapi, condwapi, condapif, tempapif};
                    MyTaskParams params = new MyTaskParams(tempapiatual, tempapif, namewapi, condwapi, condapif);
                    new HTTPReqTask().execute(params);
                } else {
                    Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                if (condapif.length() != 0 && tempapif.length() != 0) {
                    MyTaskParams params = new MyTaskParams(tempapiatual, tempapif, namewapi, condwapi, condapif);
                    new HTTPReqTask().execute(params);
                } else {
                    Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erro: " + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class MyTaskParams
    {
        int tempapi;
        String tempuser;
        String cidade;
        String condapi;
        String conduser;

        MyTaskParams(int tempapi, String tempuser, String cidade, String condapi, String conduser)
        {
            this.tempapi = tempapi;
            this.tempuser = tempuser;
            this.cidade = cidade;
            this.condapi = condapi;
            this.conduser = conduser;
        }
    }

    private class HTTPReqTask extends AsyncTask<MyTaskParams, Void, Void> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(SendWeather.this, "Coletando Dados...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(MyTaskParams... params) {
            HttpURLConnection urlConnection = null;
            String nameapi, condapi, conduser, tempuser; int tempapi;
            nameapi = params[0].cidade;
            condapi = params[1].condapi;
            tempapi = params[2].tempapi;
            conduser = params[3].conduser;
            tempuser = params[4].tempuser;

            try {
                JsonObject postData = new JsonObject();
                postData.addProperty("nome_cidade", nameapi);
                postData.addProperty("cond_api", condapi);
                postData.addProperty("temp_api", tempapi);
                postData.addProperty("cond_user", conduser);
                postData.addProperty("sens_user", tempuser);

                URL url = new URL("http://192.168.15.9:51067/Home/Send");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  201) {
                    throw new IOException("Resposta inválida: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SendWeather.this, "Algum erro aconteceu! " + e, Toast.LENGTH_LONG).show();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Toast.makeText(SendWeather.this, "Dados Coletados", Toast.LENGTH_LONG).show();
        }
    }

}
