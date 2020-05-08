package com.tika.app2.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tika.app2.Front.loginpage;
import com.tika.app2.R;

public class signparent extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText emailEditText, lastNameTextView, passwordEditText, addressTextView, dateOfBorthEditText, emergencyContactTextView_1, myPhoneTextView, heightEditText, weightEditText, bgroup, firstNameTextView, emergencyContactTextView_2;
    Button registerButton;
    private FirebaseAuth mAuth;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signparent);


        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.pass);
        dateOfBorthEditText = findViewById(R.id.dob);
        registerButton = findViewById(R.id.regt);
        heightEditText = findViewById(R.id.height);
        weightEditText = findViewById(R.id.weight);
        bgroup = findViewById(R.id.b_group);
        emergencyContactTextView_1 = findViewById(R.id.phone_num);
        emergencyContactTextView_2 = findViewById(R.id.phone_num22);
        myPhoneTextView = findViewById(R.id.phone_num2);
        firstNameTextView = findViewById(R.id.firstname);
        lastNameTextView = findViewById(R.id.lastname);
        addressTextView = findViewById(R.id.address_signup);
        mAuth = FirebaseAuth.getInstance();

        reff = FirebaseDatabase.getInstance().getReference().child("User");
        registerButton.setOnClickListener(v -> {

            String email = emailEditText.getText().toString().trim();
            String address = addressTextView.getText().toString().trim();
            String dob = dateOfBorthEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String emergencyContact_1 = emergencyContactTextView_1.getText().toString().trim();
            String emergencyContact_2 = emergencyContactTextView_2.getText().toString().trim();
            String myPhoneNumber = myPhoneTextView.getText().toString().trim();
            String weight = (weightEditText.getText().toString().trim());
            String height = (heightEditText.getText().toString().trim());
            String bg = bgroup.getText().toString().trim();
            String firstName = firstNameTextView.getText().toString().trim();
            String lastName = lastNameTextView.getText().toString().trim();

            User user = new User(firstName, lastName, height, weight, bg, emergencyContact_1, emergencyContact_2, myPhoneNumber, "19", dob, address, email);


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(signparent.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(signparent.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(signparent.this, task -> {
                        if (task.isSuccessful()) {
                            String token = mAuth.getCurrentUser().getUid();

                            /*try{
//                                FirebaseMessaging.getInstance().setAutoInitEnabled(true);

                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }*/

                            reff.child(token).setValue(user)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {


                                            Log.i(TAG, "onCreate: User Data stored successfully");
                                        } else
                                            Toast.makeText(signparent.this, "Faied to sign up.", Toast.LENGTH_LONG).show();
                                    });

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Near/users").child(mAuth.getCurrentUser().getUid());

                            ref.child("dummy").setValue(true);

                            mAuth = FirebaseAuth.getInstance();

                            new Handler().postDelayed(() -> {
                                if (mAuth.getCurrentUser() != null) {
                                    mAuth.signOut();
                                }
                                startActivity(new Intent(signparent.this, loginpage.class));
                            }, 1000);


                            /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("FCM tokens/"+"users/"+mAuth.getCurrentUser().getUid());
                                    tokenRef.setValue(task.getResult().getToken()).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.i(TAG, "onCreate: User FCM Token stored successfully");
                                        }
                                    });
                                }
                            });
*/


                        } else {
                            Toast.makeText(signparent.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    });


        });


    }
}
