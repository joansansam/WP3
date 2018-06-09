package com.example.joan.wp3;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by joan.sansa.melsion on 03/05/2018.
 */
public class LocationHelper {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location location;
    private double latitude, longitude;
    private Activity activity;

    /**
     * Start location services
     */
    public LocationHelper(Activity activity){
        this.activity = activity;
        //Check for location permissions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        createLocationRequest();
        createLocationCallback();

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void createLocationCallback(){
        //Aqui no entrará nunca si no se tiene la ubicacion activada
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                location = locationResult.getLastLocation();

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                mFusedLocationClient.removeLocationUpdates(mLocationCallback);

                updateUI(latitude, longitude);
            }
        };
    }

    private void updateUI(double lat, double lon){
        EditText latTV = activity.findViewById(R.id.lat_input);
        EditText lonTV = activity.findViewById(R.id.lon_input);

        latTV.setText(String.valueOf(lat));
        lonTV.setText(String.valueOf(lon));

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}
