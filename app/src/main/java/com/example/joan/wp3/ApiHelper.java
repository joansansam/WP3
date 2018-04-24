package com.example.joan.wp3;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

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

    public JSONObject selectService(String service, double lat, double lon){

        if(service.equals(ACCUWEATHER)){
            String url="http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey=TQcwTT9Mw9VMsXPuIKBtgJCjhLZDRh8e&q="+lat+"%2C"+lon+"&details=false";

            //ToDo

        } else if(service.equals(OPENWEATHERMAP)){
            String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=679b8bcfee89ed57c2bd5ebed2389690";
            serviceCall(url);
        }

        return serviceResponseJson;
    }

    @SuppressLint("StaticFieldLeak")
    private void serviceCall(final String urlString){

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

                    serviceResponseJson = new JSONObject(json.toString());

                } catch (Exception e) {
                    Log.e("ApiHelper","Exception "+ e.getMessage());
                    return null;
                }

                return null;
            }

            //ToDo: acabar i millorar l'acces a serviceREsponseJson en acabar l'asynctask i no abans
            /*@Override
            protected void onPostExecute(Void result) {
                onFinished(serviceResponseJson);
            }

            private JSONObject onFinished(JSONObject response){
                return response;
            }*/
        }.execute();
    }
}
