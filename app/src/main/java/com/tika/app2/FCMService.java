package com.tika.app2;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(mAuth!=null) {

            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("FCM tokens/" + "users/" + mAuth.getCurrentUser().getUid());
            tokenRef.setValue(s);
        }
    }
}
