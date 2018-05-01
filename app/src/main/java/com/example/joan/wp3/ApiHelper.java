package com.example.joan.wp3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by joan.sansa.melsion on 24/04/2018.
 */

public class ApiHelper {

    final static String ACCUWEATHER="Accuweather";
    final static String OPENWEATHERMAP="OpenWeatherMap";

    private JSONObject serviceResponseJson=null;
    private Activity activity;
    private String urlString="";

    public JSONObject selectService(Activity activity, String service, double lat, double lon){
        this.activity = activity;

        if(service.equals(ACCUWEATHER)){
            urlString="http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=TQcwTT9Mw9VMsXPuIKBtgJCjhLZDRh8e&q="+lat+"%2C"+lon+"&details=false";
            serviceCall();

        } else if(service.equals(OPENWEATHERMAP)){
            urlString = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&type=accurate&appid=679b8bcfee89ed57c2bd5ebed2389690";
            serviceCall();
        }

        return serviceResponseJson;
    }

    @SuppressLint("StaticFieldLeak")
    private void serviceCall(){

        new AsyncTask<Void, Void, Void>() {
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
                if(urlString.contains("accuweather") && urlString.contains("locations")){
                    try {
                        String locationKey = serviceResponseJson.getString("Key");
                        urlString="http://dataservice.accuweather.com/currentconditions/v1/"+locationKey+"?apikey=TQcwTT9Mw9VMsXPuIKBtgJCjhLZDRh8e&details=true";
                        serviceCall();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ApiHelper", e.getMessage());
                    }
                } else {
                    updateUI(serviceResponseJson.toString());
                }
            }
        }.execute();
    }

    private void updateUI(String text){
        TextView responseTV = activity.findViewById(R.id.response_tv);
        responseTV.setText(text);
        TextView urlTV = activity.findViewById(R.id.url_tv);
        urlTV.setText(urlString);

        String pressureValue = "0.0";
        try {
            if(urlString.contains("openweathermap")) {
                pressureValue = serviceResponseJson.getJSONObject("main").getString("pressure");
                Toast.makeText(activity.getApplicationContext(), "Pressure="+pressureValue, Toast.LENGTH_SHORT).show();
            }
            else if(urlString.contains("accuweather")){
                pressureValue = serviceResponseJson.getJSONObject("Pressure").getJSONObject("Metric").getString("Value");
                Toast.makeText(activity.getApplicationContext(), "Pressure="+pressureValue, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ApiHelper",e.getMessage());
        }
    }
}
