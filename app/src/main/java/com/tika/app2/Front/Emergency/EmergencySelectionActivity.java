package com.tika.app2.Front.Emergency;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tika.app2.Adapter.EmergencyAdapter;
import com.tika.app2.Front.Home.HomeNew;
import com.tika.app2.R;
import com.tika.app2.Utils.EmergencyLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmergencySelectionActivity extends AppCompatActivity {

    ChildEventListener listener;
    ValueEventListener listener2;

    SharedPreferences prefs;
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        startActivity(new Intent(EmergencySelectionActivity.this, HomeNew.class));
    }

    private int count = 0;

    private static final String TAG = "TAG";
    FirebaseAuth mAuth;
    String Uid;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Near/users");


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Map<String, LatLng> locations;
    Map<String, String> names;
    Map<String, String> near;
    Map<String, Integer> ages;

    private BiMap<String, Integer> UidIndex;

    Map<String, Boolean> emergency;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_selection_layout);

        try {

            prefs = getSharedPreferences("emergency", Context.MODE_PRIVATE);

            mAuth = FirebaseAuth.getInstance();
            Uid = mAuth.getCurrentUser().getUid();
            ref = ref.child(Uid);


            locations = new HashMap<>();
            ages = new HashMap<>();
            names = new HashMap<>();
            near = new HashMap<>();

            UidIndex = HashBiMap.create();

            emergency = new HashMap<>();


            //add all textview stuff here


            recyclerView = findViewById(R.id.emergencySelectionRecyclerView);
            recyclerView.setHasFixedSize(false);

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            mAdapter = new EmergencyAdapter(locations, names, ages, near, UidIndex, this);
            recyclerView.setAdapter(mAdapter);
            listener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //add new listview item here


                    Log.i(TAG, "onChildAdded: $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Child added");

                    String emergencyUId = dataSnapshot.getKey();

                    emergency.put(emergencyUId, true);

                    Map<String, Double> location = (HashMap) dataSnapshot.getValue();

                    listener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            new Handler().postDelayed(() -> {

                                if(emergency.get(dataSnapshot.getRef().getParent().getKey()) == true) {
                                    Double latitude = dataSnapshot.getValue(Double.class);
                                    dataSnapshot.getRef().getParent().child("Longitude").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Double longitude = dataSnapshot.getValue(Double.class);
                                            locations.put(emergencyUId, new LatLng(latitude, longitude));


                                            Log.i(TAG, "onChildAdded:$$$$$$$$$$$$$$ " + locations.toString());


                                            DatabaseReference emergencyRef = FirebaseDatabase.getInstance().getReference("User/" + emergencyUId);
                                            emergencyRef.child("First Name").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    names.put(emergencyUId, dataSnapshot.getValue(String.class));
                                                    Log.i(TAG, "onChildAdded: names " + names.toString());

                                                    emergencyRef.child("Age").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            ages.put(emergencyUId, dataSnapshot.getValue(Integer.class));
                                                            Log.i(TAG, "onChildAdded: ages" + ages.toString());

                                                            Log.i(TAG, "onDataChange: prev count: " + count);
                                                            getNewIndex(emergencyUId);
                                                            Log.i(TAG, "onDataChange: new count: " + count);

                                                            Log.i(TAG, "onDataChange:$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + UidIndex);

                                                            mAdapter = new EmergencyAdapter(locations, names, ages, near, UidIndex, EmergencySelectionActivity.this);
                                                            recyclerView.swapAdapter(mAdapter, true);
                                                            //notify updates here


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

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }, 1000);



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    dataSnapshot.getRef().child("Latitude").addValueEventListener(listener2);


                /*Log.i(TAG, "onChildAdded:locations " + locations.toString());


                Log.i("TAG", "onChildAdded: " + dataSnapshot.getValue().getClass().getName().toString());
                Log.i("TAG", "onChildAdded: " + dataSnapshot.getKey());

                Log.i("TAG", "onChildAdded: " + dataSnapshot.getValue().toString());*/
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //update 'near' field of an item here


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                    Log.i(TAG, "onChildRemoved: $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Child removed");

                    //delete item from listview here

                    Log.i(TAG, "onChildRemoved: " + dataSnapshot.getValue());

                    String emergencyUId = dataSnapshot.getKey();
                    emergency.put(emergencyUId, false);
                    dataSnapshot.getRef().removeEventListener(listener2);
                    deleteIndex(emergencyUId);

                    mAdapter = new EmergencyAdapter(locations, names, ages, near, UidIndex, EmergencySelectionActivity.this);
                    recyclerView.swapAdapter(mAdapter, true);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            ref.addChildEventListener(listener);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getNewIndex(String uId) {

        UidIndex.put(uId, count);
        count+=1;


    }

    private void deleteIndex(String uId) {
        Log.i(TAG, "deleteIndex: " + uId+ "," + UidIndex.toString()+"count: "+ count);


        BiMap<Integer, String> indexUId = UidIndex.inverse();
        int pos = UidIndex.get(uId);
        UidIndex.remove(uId);
        names.remove(uId);
        ages.remove(uId);
        locations.remove(uId);
        near.remove(uId);

        BiMap<String, Integer> newBiMap = HashBiMap.create();
        for(int i = 0; i<=count-1;i+=1){

            if(i==pos){
                continue;
            }
            else if(i<pos) {
                newBiMap.put(indexUId.get(i), i);
            }
            else {
                newBiMap.put(indexUId.get(i), i-1);
            }
        }
        Log.i(TAG, "deleteIndex: NewMUiIndex" + newBiMap);
        count -= 1;
        UidIndex = newBiMap;
    }
}
