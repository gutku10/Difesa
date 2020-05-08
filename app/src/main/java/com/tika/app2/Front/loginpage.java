package com.tika.app2.Front;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tika.app2.Front.Home.HomeNew;
import com.tika.app2.Login.User;
import com.tika.app2.Login.signparent;
import com.tika.app2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class loginpage extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText txtEmail, txtPassword;
    Button btn_login;
    private FirebaseAuth firebaseAuth;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userReference;


    private void getData() {

        String token = firebaseAuth.getCurrentUser().getUid();

        Log.i("TAG", "onComplete: " + token);
        userReference = reference.child("User").child(token);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                SharedPreferences prefs = getSharedPreferences("userData", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("authID", token);

                editor.putString("firstName", user.getFirstName());
                editor.putString("lastName", user.getFirstName());
                editor.putString("dateOfBirth", user.getDateOfBirth());
                editor.putString("address", user.getAddress());
                editor.putString("email", user.getEmail());
                editor.putString("emergencyContact_1", user.getEmergencyPhone1());
                editor.putString("emergencyContact_2", user.getEmergencyPhone2());
                editor.putString("myPhoneNumber", user.getMyPhone());
                editor.putString("firstName", user.getFirstName());
                editor.putString("firstName", user.getFirstName());
                editor.putString("bloodGroup", user.getBloodGroup());
                editor.putString("weight", user.getWeight());
                editor.putString("height", user.getHeight());

                editor.apply();
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    private void getFCMToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    Log.i(TAG, "getFCMToken: " + token);

                    // Log and toast
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("FCM tokens/users/" + mAuth.getCurrentUser().getUid() + "/token");
                    tokenRef.setValue(token);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (firebaseAuth.getCurrentUser() != null) {

            /*try {
                FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            getData();
            getFCMToken();
            startActivity(new Intent(loginpage.this, HomeNew.class));


        }
    }

    boolean checkPermissionAvailable(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    void getPermissions(String[] permissionsArg) {

        ArrayList<String> permissionsToRequest = new ArrayList<>();


        for (String permission : permissionsArg) {
            if (!checkPermissionAvailable(permission))
                permissionsToRequest.add(permission);
        }

        Object[] objArray = permissionsToRequest.toArray();

        if (objArray.length > 0)
            ActivityCompat.requestPermissions(this, Arrays.copyOf(objArray, objArray.length, String[].class), 1);
    }

    void checkPermissionGrantedOrNot(@NonNull int[] grantResults, final String permission, int i, boolean required) {
        if (!(grantResults.length > 0)
                || !(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {

            String permissionName;

            switch (permission) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    permissionName = "Storage";
                    break;
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    permissionName = "Location";
                    break;
                case Manifest.permission.CAMERA:
                    permissionName = "Camera";
                    break;
                case Manifest.permission.CALL_PHONE:
                    permissionName = "Call";
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + permission);
            }

            if (required) {

                AlertDialog.Builder builder = new AlertDialog.Builder(loginpage.this);
                builder.setMessage(permissionName + " permission is required. Please allow to use Difesa.")
                        .setTitle("Required Permission")
                        .setPositiveButton(R.string.grantPermission, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getPermissions(new String[]{permission});
                            }
                        });
                AlertDialog dialog = builder.create();

                dialog.show();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            boolean required = true;

            for (int i = 0; i < permissions.length; i++) {

                switch (permissions[i]) {
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    case Manifest.permission.CALL_PHONE:
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                        required = true;
                        break;
                    case Manifest.permission.CAMERA:
                        required = false;
                        break;
                }
                checkPermissionGrantedOrNot(grantResults, permissions[i], i, required);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);


        getPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION});


        txtEmail = findViewById(R.id.mail);
        txtPassword = findViewById(R.id.passl);
        btn_login = findViewById(R.id.buttonlogin);
        firebaseAuth = FirebaseAuth.getInstance();


        btn_login.setOnClickListener(v -> {

            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

//                Intent intent = getIntent();

            /*if(intent.getBooleanExtra("firstOpen",true)){
                SharedPreferences pref = getSharedPreferences("pref3", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putBoolean("firstStarted", false);
                edt.commit();
            }*/

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(loginpage.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(loginpage.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(loginpage.this, task -> {
                        if (task.isSuccessful()) {

                            try {
                                new Thread(() -> {
                                    try {
                                        Log.i(TAG, "onOptionsItemSelected: Attempting get token");

                                        FirebaseInstanceId.getInstance().getToken("340364969638", "FCM");
                                        Log.i(TAG, "onOptionsItemSelected:  get token - successful");

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }).start();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            finally {

                                Log.i(TAG, "onOptionsItemSelected: setting auoinit to true");
                                FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                                Log.i(TAG, "onOptionsItemSelected:  auoinit set to true");


                            }

                            getData();

                            getFCMToken();


                            startActivity(new Intent(loginpage.this, HomeNew.class));


                        } else {
                            Toast.makeText(loginpage.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    });
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    public void btn_signupForm(View view) {

        startActivity(new Intent(this, signparent.class));
    }

}
