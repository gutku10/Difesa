package com.tika.app2.Front;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tika.app2.Login.Member;
import com.tika.app2.R;

public class SosActivity extends AppCompatActivity {
    private static final int REQUEST_CALL = 1;
    private EditText mEditTextNumber;
    int i=1;
    FirebaseAuth firebaseAuth;
    FirebaseUser member;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView imageCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        //SessionConfiguration sessionConfiguration=new SessionConfiguration.Builder().setClientId("5JxbR9oiwAp7yqbvU208Am1FCDJOkPe3").setServerToken().setRedirectUri()





        imageCall = findViewById(R.id.image_call);

        imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();

            }
        });

        final MediaPlayer sirenMP = MediaPlayer.create(this,R.raw.siren);
        Button playSiren = (Button) this.findViewById(R.id.siren);
        playSiren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sirenMP.start();

            }

        });
        /*Button ol = (Button) this.findViewById(R.id.olas);
        ol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.olacabs.com/mobile"));
                //startActivity(browserIntent);
                Intent i=getPackageManager().getLaunchIntentForPackage("https://m.uber.com/ul/?client_id=5JxbR9oiwAp7yqbvU208Am1FCDJOkPe3");
                startActivity(i);
            }

        });*/
    }


    private void makePhoneCall(){
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
                        if (number.trim().length() > 0) {

                            if (ContextCompat.checkSelfPermission(SosActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SosActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                            } else {
                                String dial = "tel:" + number;
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                            }

                        } else {
                            Toast.makeText(SosActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                        }


                    }
     //makePhoneCall();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



         //makePhoneCall();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}