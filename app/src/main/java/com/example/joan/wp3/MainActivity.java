package com.example.joan.wp3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    private String selectedService;
    private Button getGpsButton;

    //--------------------------------------------------------------------------
    // LIFE CYCLE
    //--------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getGpsButton = findViewById(R.id.get_gps_btn);
        getGpsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //
            }
        });

        Spinner servicesSpinner = findViewById(R.id.services_spinner);
        final String[] services= new String[]{"Accuweather","OpenWeatherMap"};
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
