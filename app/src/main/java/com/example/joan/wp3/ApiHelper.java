package com.example.joan.wp3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by joan.sansa.melsion on 24/04/2018.
 */

public class ApiHelper {

    final static String ACCUWEATHER="Accuweather";
    final static String OPENWEATHERMAP="OpenWeatherMap";
    final static String DARKSKY="DarkSky";

    private JSONObject serviceResponseJson=null;
    private Activity activity;
    private String urlString="";

    public void selectService(Activity activity, String service, double lat, double lon){
        this.activity = activity;

        switch (service) {
            case ACCUWEATHER:
                urlString = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=TQcwTT9Mw9VMsXPuIKBtgJCjhLZDRh8e&q=" + lat + "%2C" + lon + "&details=false";
                serviceCall();

                break;
            case OPENWEATHERMAP:
                urlString = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&type=accurate&appid=679b8bcfee89ed57c2bd5ebed2389690";
                serviceCall();
                break;
            case DARKSKY:
                urlString = "https://api.darksky.net/forecast/4f237579222e7fd80fa327b8c57c532d/" + lat + "," + lon;
                serviceCall();
                break;
        }
    }

    private void serviceCall(){
        new AsyncWork().execute();
    }

    private class AsyncWork extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp = "";

                while((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();

                String jsonString = json.toString();
                //Beautify json in order to construct the JSONObject correctly
                if(urlString.contains("currentconditions")){
                    jsonString = json.toString().replace("[","").replace("]","");
                }
                serviceResponseJson = new JSONObject(jsonString);

            } catch (Exception e) {
                Log.e("ApiHelper","Exception "+ e.getMessage());
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(serviceResponseJson != null) {
                if (urlString.contains("accuweather") && urlString.contains("locations")) {
                    try {
                        String locationKey = serviceResponseJson.getString("Key");
                        urlString = "http://dataservice.accuweather.com/currentconditions/v1/" + locationKey + "?apikey=TQcwTT9Mw9VMsXPuIKBtgJCjhLZDRh8e&details=true";
                        serviceCall();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ApiHelper", e.getMessage());
                    }
                } else {
                    updateUI(serviceResponseJson);
                }
            }
        }
    }

    private void updateUI(JSONObject serviceResponseJson){
        TextView responseTV = activity.findViewById(R.id.response_tv);

        String timestamp = DateFormat.format("dd/MM/yyyy-HH:mm:ss", new java.util.Date()).toString();

        String pressureValue = "0.0";
        try {
            if(urlString.contains("openweathermap")) {
                pressureValue = serviceResponseJson.getJSONObject("main").getString("pressure");
            }
            else if(urlString.contains("accuweather")){
                pressureValue = serviceResponseJson.getJSONObject("Pressure").getJSONObject("Metric").getString("Value");
                String temperature = serviceResponseJson.getJSONObject("Temperature").getJSONObject("Metric").getString("Value");
                responseTV.append("\n"+timestamp+" Accuweather: "+pressureValue+" - "+temperature);
            }
            else if (urlString.contains("darksky")){
                pressureValue = serviceResponseJson.getJSONObject("currently").getString("pressure");
                String temperatureString = serviceResponseJson.getJSONObject("currently").getString("temperature");
                double convertedTemp= 5*(Double.valueOf(temperatureString)-32)/9;
                String temperature = String.format(Locale.ENGLISH, "%.2f", convertedTemp);
                responseTV.append("\n"+timestamp+" Darksky: "+pressureValue+" - "+temperature);
            }
            //Toast.makeText(activity.getApplicationContext(), "Pressure="+pressureValue, Toast.LENGTH_SHORT).show();

            //Save logs
            String logs = responseTV.getText().toString();
            SharedPreferences prefs = activity.getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("logs",logs).commit();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ApiHelper",e.getMessage());
        }
    }
}
