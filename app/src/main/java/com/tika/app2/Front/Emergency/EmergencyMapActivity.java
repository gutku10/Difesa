package com.tika.app2.Front.Emergency;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tika.app2.R;
import com.tika.app2.Utils.DirectionsJSONParser;
import com.tika.app2.Utils.GetLocation;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EmergencyMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static String TAG = "TAG";

    Handler handler;
    Runnable runnable;

    private GoogleMap mMap;
    ArrayList markerPoints = new ArrayList();
    FirebaseAuth mAuth;

    FusedLocationProviderClient mFusedLocationClient;
    Activity activity;
    Context context;

    private String userIdMain;
    private LatLng latLngMain;
    private LatLng latLngUser;

    Timer timer;


    void showAndGet(LatLng latLngUser, LatLng latLngMain) {

        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(latLngUser);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(options);

        options.position(latLngUser);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options);

        LatLng origin = latLngUser;
        LatLng dest = latLngMain;

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }

    void get(boolean bool) {

//        getLastLocation(EmergencyMapActivity.this, getApplicationContext());

        GetLocation getLocation = new GetLocation(EmergencyMapActivity.this, getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        latLngUser = new LatLng(Double.parseDouble(prefs.getString("latitude", "0.0")), Double.parseDouble(prefs.getString("longitude", "0.0")));

//        userIdMain = prefs.getString("userIdMain","nothing");


        if (bool == true) {
            showAndGet(latLngUser, latLngMain);

            LatLng mid = new LatLng((latLngUser.latitude + latLngMain.latitude) / 2, (latLngUser.longitude + latLngMain.longitude) / 2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mid, 16));

        } else {
//add code that if child removed/ emergency removed, then stop checking otherwise error

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Near/users/" + mAuth.getInstance().getCurrentUser().getUid() + "/" + userIdMain);

            Log.i(TAG, "get: $$$$$$$$$looking new location emergecy data at:" + ref.toString());
            ref.child("Latitude").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double latitude = dataSnapshot.getValue(Double.class);
                    Log.i(TAG, "onDataChange: New latitude emergency=" + latitude.toString());
                    dataSnapshot.getRef().getParent().child("Longitude").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Double longitude = dataSnapshot.getValue(Double.class);

                            latLngMain = new LatLng(latitude, longitude);
                            showAndGet(latLngUser, latLngMain);

                            LatLng mid = new LatLng((latLngUser.latitude + latLngMain.latitude) / 2, (latLngUser.longitude + latLngMain.longitude) / 2);
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mid, 16));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_maps);
        mAuth = FirebaseAuth.getInstance();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // mode options in here
        // then check how to refreah map so that onmapready is called again
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        mMap.clear();

        GetLocation getLocation = new GetLocation(EmergencyMapActivity.this, getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        latLngUser = new LatLng(Double.parseDouble(prefs.getString("latitude", "0.0")), Double.parseDouble(prefs.getString("longitude", "0.0")));

//        getLastLocation(EmergencyMapActivity.this, getApplicationContext());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        latLngMain = new LatLng(bundle.getDouble("Latitude"), bundle.getDouble("Longitude"));
        userIdMain = bundle.getString("UId");
        get(true);

        handler = new Handler();
        runnable = () -> {
            timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //your method
                    get(false);
                }
            }, 0, 5000);
        };
        handler.postDelayed(runnable, 5000);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(EmergencyMapActivity.this, EmergencySelectionActivity.class));
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + "key=" + "AIzaSyDMSXQmnfsN46zorGRNlb0HA89S5_jnRKg";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.i("TAG", "getDirectionsUrl: $$$$$$$$$$$$$$$$$$$$$$$$$$$$" + url);


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /*@SuppressLint("MissingPermission")
    private void getLastLocation(Activity activity, Context context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        *//*if (checkPermissions()) {
            if (isLocationEnabled()) {*//*
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Log.i(TAG, "onComplete: $$$$$$$$$$$ Got user location");
                            latLngUser = new LatLng(location.getLatitude(), location.getLongitude());

                            Log.i(TAG, "onComplete: " + latLngUser.toString());
                        }
                    }
                }
        );


          *//*  } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);*//*
    }*/
    /*@SuppressLint("MissingPermission")
    private void requestNewLocationData () {

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

    }*/

    /*private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i(TAG, "onComplete: $$$$$$$$$$$ Getting user old location");

            latLngUser = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            Log.i(TAG, "onComplete: $$$$$$$$$$$ Got user location" + latLngUser);*/

    @Override
    protected void onDestroy() {

        if(timer!=null) {
            timer.cancel();
        }

        handler.removeCallbacks(runnable);
        super.onDestroy();


    }
}
//    };
//}

