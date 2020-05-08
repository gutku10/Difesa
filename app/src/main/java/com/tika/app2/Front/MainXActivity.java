package com.tika.app2.Front;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tika.app2.Utils.GetLocation;
import com.tika.app2.Login.Member;
import com.tika.app2.R;
import com.tika.app2.listeners.PictureCapturingListener;
import com.tika.app2.services.APictureCapturingService;
import com.tika.app2.services.PictureCapturingServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * App's Main Activity showing a simple usage of the picture taking service.
 *
 * @author hzitoun (zitoun.hamed@gmail.com)
 */
public class MainXActivity extends AppCompatActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {
    int counter = 0;
    String zap;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    Double latitude, longitude;

    FirebaseAuth firebaseAuth;
    FirebaseUser member;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Member info;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userReference;


    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    private static final int REQUEST_CALL = 1;


    private ImageView uploadBackPhoto;
    private ImageView uploadFrontPhoto;

    //The capture service
    private APictureCapturingService pictureService;
    boolean checkPermissionAvailable(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_x);
        checkPermissions();
        uploadBackPhoto = (ImageView) findViewById(R.id.backIV);
        uploadFrontPhoto = (ImageView) findViewById(R.id.frontIV);

        // getting instance of the Service from PictureCapturingServiceImpl
        pictureService = PictureCapturingServiceImpl.getInstance(this);


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

//                tvShake.setText("Shake Action is just detected!!");

                firebaseAuth = FirebaseAuth.getInstance();

                /*databaseReference = FirebaseDatabase.getInstance().getReference().child("Location");
                info = new Member();

                info.setLa("28.7226794");
                info.setLon("77.1415056");
                databaseReference.push().setValue(info);*/

                String Uid = firebaseAuth.getCurrentUser().getUid();

                databaseReference = FirebaseDatabase.getInstance().getReference("Locations/users/" + Uid);
                Map<String, Double> locationMap = new HashMap<>();
                GetLocation getLocation = new GetLocation(MainXActivity.this, getApplicationContext());
                SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

                locationMap.put("Latitude",Double.parseDouble(prefs.getString("Latitude", "0.0")));
                locationMap.put("Longitude",Double.parseDouble(prefs.getString("Longitude", "0.0")));

                databaseReference.setValue(locationMap);

                databaseReference = FirebaseDatabase.getInstance().getReference("Emergency/users/" + Uid);
                databaseReference.setValue(true);

                makePhoneCall();

//                sendImage();

                if(checkPermissionAvailable(Manifest.permission.CAMERA))
                    pictureService.startCapturing(MainXActivity.this);

                Toast.makeText(MainXActivity.this, "Shaked!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }


    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * We've finished taking pictures from all phone's cameras
     */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("Done capturing all photos!");
            return;
        }
        showToast("No camera detected!");
    }

    /**
     * Displaying the pictures taken.
     */
    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(() -> {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                if (pictureUrl.contains("0_pic.jpg")) {
                    uploadBackPhoto.setImageBitmap(scaled);
                } else if (pictureUrl.contains("1_pic.jpg")) {
                    //      uploadFrontPhoto.setImageBitmap(scaled);
                }
            });
            showToast("Picture saved to " + pictureUrl);


            String toNumber = "+91" + zap; // contains spaces.
            toNumber = toNumber.replace("+", "").replace(" ", "");

            String p = String.valueOf(counter);

            Intent sendIntent = new Intent("android.intent.action.MAIN");

            Uri uri;
            if(checkPermissionAvailable(Manifest.permission.CAMERA)) {
                uri = Uri.parse("/storage/emulated/0/0_pic" + p + ".jpg");
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sendIntent.setType("image/jpg");
            }

            else{
                sendIntent.setType("text/plain");
            }
            counter += 1;
            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");

            GetLocation getLocation = new GetLocation(MainXActivity.this, getApplicationContext());

            SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
            latitude = Double.parseDouble(prefs.getString("latitude", "0"));
            longitude = Double.parseDouble(prefs.getString("longitude", "0"));

            sendIntent.putExtra(Intent.EXTRA_TEXT, "Help Me I am at https://maps.google.com/?q=" + latitude + "," + longitude);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setPackage("com.whatsapp");


            startActivity(sendIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }

    /**
     * checking  permissions at Runtime.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }


    private void makePhoneCall() {
        firebaseAuth = FirebaseAuth.getInstance();
        member = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");


        SharedPreferences prefs = getSharedPreferences("userData", Context.MODE_PRIVATE);

        String number = prefs.getString("emergencyContact_1", "1234567890");
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(MainXActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainXActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        /*firebaseAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(task -> {
            userReference = reference.child(task.getResult().getToken()).child("Emergency Contact 1");
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String number = dataSnapshot.getValue(String.class);

                    if (number.trim().length() > 0) {

                        if (ContextCompat.checkSelfPermission(MainXActivity.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainXActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                        } else {
                            String dial = "tel:" + number;
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        }

                        SharedPreferences prefs = getSharedPreferences("userData", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString("emergencyContact_1", number);
                        editor.apply();
                        editor.commit();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

                ;

                // makePhoneCall();
            });

        });*/
        }
    }
}





/*



















package com.skull1.hackathon1.Front;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.skull1.hackathon1.R;
import com.skull1.hackathon1.listeners.PictureCapturingListener;
import com.skull1.hackathon1.services.APictureCapturingService;
import com.skull1.hackathon1.services.PictureCapturingServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MainXActivity extends AppCompatActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {


    int count=0;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private ImageView uploadBackPhoto;
    private ImageView uploadFrontPhoto;

    //The capture service
    private APictureCapturingService pictureService;

    private TextView tvShake;
    private Button btn;
    private static final int REQUEST_CALL = 1;


    private void sendImage(){

        pictureService.startCapturing(this);



        String toNumber = "+91 8920532416"; // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");

        String p =String.valueOf(count);
        Uri uri = Uri.parse("/storage/emulated/0/0_pic"+p+".jpg");
        count+=1;
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra(Intent.EXTRA_STREAM,uri);
        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT,"Help Me I am at https://maps.google.com/?q=30.35537,76.37016");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("image/jpg");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(sendIntent);

    }




    private void makePhoneCall(){


        String number = "8920532416" ;   //mEditTextNumber.getText().toString();
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(MainXActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainXActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(MainXActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {   //demo link   http://jasonmcreynolds.com/?p=388
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_x);

        tvShake = findViewById(R.id.tvShake);
        btn = findViewById(R.id.btn);

        checkPermissions();
        uploadBackPhoto = (ImageView) findViewById(R.id.backIV);
        uploadFrontPhoto = (ImageView) findViewById(R.id.frontIV);
        final Button btn = (Button) findViewById(R.id.startCaptureBtn);
        // getting instance of the Service from PictureCapturingServiceImpl
        pictureService = PictureCapturingServiceImpl.getInstance(this);





        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                */
/*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 //

                tvShake.setText("Shake Action is just detected!!");

//                makePhoneCall();

                sendImage();


                Toast.makeText(MainXActivity.this, "Shaked!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

   // btn.setOnClickListener(v -> {

      //  showToast("Starting capture!");
       // pictureService.startCapturing(this);
   // });





    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }

    */
/**
 * We've finished taking pictures from all phone's cameras
 * //
 *
 * @Override public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
 * if (picturesTaken != null && !picturesTaken.isEmpty()) {
 * showToast("Done capturing all photos!");
 * return;
 * }
 * showToast("No camera detected!");
 * }
 * <p>
 * <p>
 * Displaying the pictures taken.
 * //
 * @Override public void onCaptureDone(String pictureUrl, byte[] pictureData) {
 * if (pictureData != null && pictureUrl != null) {
 * runOnUiThread(() -> {
 * final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
 * final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
 * final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
 * if (pictureUrl.contains("0_pic.jpg")) {
 * uploadBackPhoto.setImageBitmap(scaled);
 * <p>
 * new Handler().postDelayed(new Runnable() {
 * @Override public void run() {
 * uploadBackPhoto.setImageBitmap(scaled);
 * <p>
 * <p>
 * }
 * }, 1000);
 * <p>
 * } else if (pictureUrl.contains("1_pic.jpg")) {
 * uploadFrontPhoto.setImageBitmap(scaled);
 * }
 * });
 * showToast("Picture saved to " + pictureUrl);
 * }
 * }
 * @Override public void onRequestPermissionsResult(int requestCode,
 * @NonNull String permissions[], @NonNull int[] grantResults) {
 * switch (requestCode) {
 * case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
 * if (!(grantResults.length > 0
 * && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
 * checkPermissions();
 * }
 * }
 * }
 * }
 * <p>
 * <p>
 * checking  permissions at Runtime.
 * //
 * @TargetApi(Build.VERSION_CODES.M) private void checkPermissions() {
 * final List<String> neededPermissions = new ArrayList<>();
 * for (final String permission : requiredPermissions) {
 * if (ContextCompat.checkSelfPermission(getApplicationContext(),
 * permission) != PackageManager.PERMISSION_GRANTED) {
 * neededPermissions.add(permission);
 * }
 * }
 * if (!neededPermissions.isEmpty()) {
 * requestPermissions(neededPermissions.toArray(new String[]{}),
 * MY_PERMISSIONS_REQUEST_ACCESS_CODE);
 * }
 * }
 * <p>
 * }
 * <p>
 * Displaying the pictures taken.
 * //
 * @Override public void onCaptureDone(String pictureUrl, byte[] pictureData) {
 * if (pictureData != null && pictureUrl != null) {
 * runOnUiThread(() -> {
 * final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
 * final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
 * final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
 * if (pictureUrl.contains("0_pic.jpg")) {
 * uploadBackPhoto.setImageBitmap(scaled);
 * <p>
 * new Handler().postDelayed(new Runnable() {
 * @Override public void run() {
 * uploadBackPhoto.setImageBitmap(scaled);
 * <p>
 * <p>
 * }
 * }, 1000);
 * <p>
 * } else if (pictureUrl.contains("1_pic.jpg")) {
 * uploadFrontPhoto.setImageBitmap(scaled);
 * }
 * });
 * showToast("Picture saved to " + pictureUrl);
 * }
 * }
 * @Override public void onRequestPermissionsResult(int requestCode,
 * @NonNull String permissions[], @NonNull int[] grantResults) {
 * switch (requestCode) {
 * case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
 * if (!(grantResults.length > 0
 * && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
 * checkPermissions();
 * }
 * }
 * }
 * }
 * <p>
 * <p>
 * checking  permissions at Runtime.
 * //
 * @TargetApi(Build.VERSION_CODES.M) private void checkPermissions() {
 * final List<String> neededPermissions = new ArrayList<>();
 * for (final String permission : requiredPermissions) {
 * if (ContextCompat.checkSelfPermission(getApplicationContext(),
 * permission) != PackageManager.PERMISSION_GRANTED) {
 * neededPermissions.add(permission);
 * }
 * }
 * if (!neededPermissions.isEmpty()) {
 * requestPermissions(neededPermissions.toArray(new String[]{}),
 * MY_PERMISSIONS_REQUEST_ACCESS_CODE);
 * }
 * }
 * <p>
 * }
 */
/**
 * Displaying the pictures taken.
 //

 @Override public void onCaptureDone(String pictureUrl, byte[] pictureData) {
 if (pictureData != null && pictureUrl != null) {
 runOnUiThread(() -> {
 final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
 final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
 final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
 if (pictureUrl.contains("0_pic.jpg")) {
 uploadBackPhoto.setImageBitmap(scaled);

 new Handler().postDelayed(new Runnable() {
 @Override public void run() {
 uploadBackPhoto.setImageBitmap(scaled);


 }
 }, 1000);

 } else if (pictureUrl.contains("1_pic.jpg")) {
 uploadFrontPhoto.setImageBitmap(scaled);
 }
 });
 showToast("Picture saved to " + pictureUrl);
 }
 }

 @Override public void onRequestPermissionsResult(int requestCode,
 @NonNull String permissions[], @NonNull int[] grantResults) {
 switch (requestCode) {
 case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
 if (!(grantResults.length > 0
 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
 checkPermissions();
 }
 }
 }
 }

 */
/**
 * checking  permissions at Runtime.
 //

 @TargetApi(Build.VERSION_CODES.M) private void checkPermissions() {
 final List<String> neededPermissions = new ArrayList<>();
 for (final String permission : requiredPermissions) {
 if (ContextCompat.checkSelfPermission(getApplicationContext(),
 permission) != PackageManager.PERMISSION_GRANTED) {
 neededPermissions.add(permission);
 }
 }
 if (!neededPermissions.isEmpty()) {
 requestPermissions(neededPermissions.toArray(new String[]{}),
 MY_PERMISSIONS_REQUEST_ACCESS_CODE);
 }
 }

 }*/