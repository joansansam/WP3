package com.example.joan.wp3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import static com.example.joan.wp3.ApiHelper.ACCUWEATHER;
import static com.example.joan.wp3.ApiHelper.DARKSKY;
import static com.example.joan.wp3.ApiHelper.OPENWEATHERMAP;

/**
 * Created by joan.sansa.melsion on 18/04/2018.
 */

public class MainActivity extends AppCompatActivity {

    private String selectedService;
    private Button getGpsButton,callServiceButton;
    private EditText latInput, lonInput;
    private TextView responseTV;

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
                //For test
                /*latInput.setText("41.493639");
                lonInput.setText("2.076364");*/

                new LocationHelper(MainActivity.this);
            }
        });

        final MainActivity activity = MainActivity.this;
        callServiceButton = findViewById(R.id.call_service_btn);
        callServiceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                double lat = Double.valueOf(latInput.getText().toString());
                double lon = Double.valueOf(lonInput.getText().toString());

                ApiHelper apiHelper = new ApiHelper();
                JSONObject response = apiHelper.selectService(activity,selectedService, lat, lon);
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


    }
}
