package com.tika.app2.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class GetLocation  {


    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    Activity activity;
    Context context;

    public GetLocation(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;

        getLastLocation(activity, context);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(Activity activity, Context context){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        /*if (checkPermissions()) {
            if (isLocationEnabled()) {*/
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();

                                    editor.putString("latitude",String.valueOf(location.getLatitude()));
                                    editor.putString("longitude",String.valueOf(location.getLongitude()));

                                    editor.apply();
                                    editor.commit();
                                }
                            }
                        }
                );
          /*  } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);*/
            }
        /*} else {
//            requestPermissions();
        }*/


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("latitude",String.valueOf(mLastLocation.getLatitude()));
            editor.putString("longitude",String.valueOf(mLastLocation.getLongitude()));

            editor.apply();
            editor.commit();
        }
    };

    /*private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }*/

    /*private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(activity);
            }
        }
    }*/

    /*@Override
    public void onResume(){
        super.onResume();
//        if (checkPermissions()) {
            getLastLocation(activity, context);
//        }

    }*/
}
