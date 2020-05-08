package com.tika.app2.Home;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tika.app2.Login.Member;
import com.tika.app2.R;


import java.net.URLEncoder;

public class LocationCapture extends AppCompatActivity {

    public static double longitude;
    public static double latitude;
    double lat;
    double longit;
    FirebaseAuth firebaseAuth;
    FirebaseUser member;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;
    Button xyz;
    String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationcapture);
        xyz=(Button)findViewById(R.id.button_get_loc);
        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLongLong();
        getLastLocation();


        xyz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double x=getLastLocation();
                double y=getLongLong();
                firebaseAuth=FirebaseAuth.getInstance();
                member=firebaseAuth.getCurrentUser();
                databaseReference=FirebaseDatabase.getInstance().getReference().child("Member");
                String key=member.getEmail();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    String xyz;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Member info = snapshot.getValue(Member.class);
                            if (key.equalsIgnoreCase(info.getName())) {
                                xyz = info.getPh().toString();
                                // a[i]=xyz;i=i+1;
                                String number = xyz;   //mEditTextNumber.getText().toString();

                                String toNumber = "+91"+number; // contains spaces.
                                toNumber = toNumber.replace("+", "").replace(" ", "");
                                openWhatsApp(toNumber,"Help Me I am at https://maps.google.com/?q="+x+","+y);
                                Log.i("TAG", "onClick: $$$$$$$$$$$$$$$$$$$"+y+"   "+x);

                            }
//                  makePhoneCall();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                // makePhoneCall();
//                String toNumber = "+91"+"8218521910"; // contains spaces.
//                toNumber = toNumber.replace("+", "").replace(" ", "");
//                openWhatsApp(toNumber,"Help Me I am at https://maps.google.com/?q="+x+",76.3690206");
//                Log.i("TAG", "onClick: $$$$$$$$$$$$$$$$$$$"+y+"   "+x);
//
//





















//                Intent sendIntent = new Intent("android.intent.action.MAIN");
//                //sendIntent.putExtra(Intent.EXTRA_STREAM,uri);
//                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
//                sendIntent.putExtra(Intent.EXTRA_TEXT,"Help Me I am at https://maps.google.com/?q="+x+","+y);
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.setPackage("com.whatsapp");
//                startActivity(sendIntent);
//

            }
        });


    }


    @SuppressLint("MissingPermission")
    public double getLastLocation(){

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");

                                    lat=location.getLatitude();



                                }
                            }
                        }
                );

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
        return lat;
    }

    public double getLongLong(){

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location1 = task.getResult();
                                if (location1 == null) {
                                    requestNewLocationData();
                                } else {
                                    // latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location1.getLongitude()+"");

                                    longit=location1.getLongitude();



                                }
                            }
                        }
                );

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
        return longit;
    }



    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            longitude = Double.valueOf(mLastLocation.getLatitude()+"");
            latitude = Double.valueOf(mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
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
                getLastLocation();
                getLongLong();
            }
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
            getLongLong();
        }

    }
    private void openWhatsApp(String numero,String mensaje){

        try{
            PackageManager packageManager =this.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone="+ numero +"&text=" + URLEncoder.encode(mensaje, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch(Exception e) {
            Log.e("ERROR WHATSAPP",e.toString());
            //KToast.errorToast(getActivity(), getString(R.string.no_whatsapp), Gravity.BOTTOM, KToast.LENGTH_SHORT);
        }

    }
}