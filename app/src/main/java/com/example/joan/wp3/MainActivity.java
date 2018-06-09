package com.example.joan.wp3;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.joan.wp3.ApiHelper.ACCUWEATHER;
import static com.example.joan.wp3.ApiHelper.DARKSKY;
import static com.example.joan.wp3.ApiHelper.OPENWEATHERMAP;

/**
 * Created by joan.sansa.melsion on 18/04/2018.
 */

public class MainActivity extends AppCompatActivity {

    private String selectedService;
    private Button getGpsButton,callServiceButton;
    private ToggleButton repetitiveButton;
    private EditText latInput, lonInput;
    private TextView responseTV;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private MainActivity activity;

    //--------------------------------------------------------------------------
    // LIFE CYCLE
    //--------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latInput = findViewById(R.id.lat_input);
        lonInput = findViewById(R.id.lon_input);

        getGpsButton = findViewById(R.id.get_gps_btn);
        getGpsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Obtain GPS position

                new LocationHelper(MainActivity.this);
            }
        });

        activity = MainActivity.this;
        callServiceButton = findViewById(R.id.call_service_btn);
        callServiceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String latString = latInput.getText().toString();
                String lonString = latInput.getText().toString();
                if(latString.equals("")){
                    latString = "41.493639";
                    latInput.setText(latString);
                }
                if(lonString.equals("")){
                    lonString = "2.076364";
                    lonInput.setText(lonString);
                }
                double lat = Double.valueOf(latString);
                double lon = Double.valueOf(lonString);

                ApiHelper apiHelper = new ApiHelper();
                apiHelper.selectService(activity,selectedService, lat, lon);
            }
        });

        Spinner servicesSpinner = findViewById(R.id.services_spinner);
        final String[] services= new String[]{ACCUWEATHER, OPENWEATHERMAP, DARKSKY};
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, services);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicesSpinner.setAdapter(adaptador);
        servicesSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        selectedService = (String)parent.getItemAtPosition(position);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedService = (String)parent.getItemAtPosition(0);
                    }
                });

        repetitiveButton = findViewById(R.id.repetitive_btn);
        repetitiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int intervalInSeconds = 10;
                //int intervalInSeconds = 10*60;

                boolean buttonIsChecked = repetitiveButton.isChecked();

                if(buttonIsChecked) {
                    if (timer != null) {
                        timer.cancel();
                    }

                    timer = new Timer();
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 1000, intervalInSeconds * 1000);
                }
                else{
                    if (timer != null){
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        });

        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("callNumber",0).commit();

    }

    /**
     *http://android-er.blogspot.com/2013/12/example-of-using-timer-and-timertask-on.html
     */
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    String latString = latInput.getText().toString();
                    String lonString = latInput.getText().toString();
                    if(latString.equals("")){
                        latString = "41.493639";
                        latInput.setText(latString);
                    }
                    if(lonString.equals("")){
                        lonString = "2.076364";
                        lonInput.setText(lonString);
                    }
                    double lat = Double.valueOf(latString);
                    double lon = Double.valueOf(lonString);

                    ApiHelper apiHelper = new ApiHelper();
                    SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    int callNumber = prefs.getInt("callNumber",0);
                    if(callNumber<50) {
                        callNumber++;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("callNumber",callNumber).commit();
                        apiHelper.selectService(activity,ACCUWEATHER, lat, lon);
                    }
                    apiHelper.selectService(activity,DARKSKY, lat, lon);
                }});
        }

    }
}
