package com.tika.app2.Front;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tika.app2.Home.aadharupload;
import com.tika.app2.Login.Member;
import com.tika.app2.Login.User;
import com.tika.app2.Medical.Pedometer;
import com.tika.app2.R;

public class ProfileFragment extends Fragment {
    //    PieChart mPieChart;
    Button pdfBtn, steps;
    private TextView weightTextView, heightTextView, bgTextView, emergencyContactTextView_1, nameTextView;
    FirebaseAuth firebaseAuth;
    FirebaseUser member;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView call;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userReference;


    private void getData() {

        String token = firebaseAuth.getCurrentUser().getUid();
        userReference = reference.child("User").child(token);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                SharedPreferences prefs = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();

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

                weightTextView.setText(prefs.getString("weight", "-"));
                heightTextView.setText(prefs.getString("height", "-"));
                bgTextView.setText(prefs.getString("bloodGroup", "-"));
                emergencyContactTextView_1.setText(prefs.getString("emergencyContact_1", "1234567890"));
                nameTextView.setText(prefs.getString("firstName", "User"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile, container, false);
        weightTextView = view.findViewById(R.id.weight_no);
        heightTextView = view.findViewById(R.id.lam);
        bgTextView = view.findViewById(R.id.blood);
        emergencyContactTextView_1 = view.findViewById(R.id.call_profile_number);
        setRetainInstance(true);
        pdfBtn = (Button) view.findViewById(R.id.pdfupload_btn);
        steps = (Button) view.findViewById(R.id.pedo_button);
        call = (ImageView) view.findViewById(R.id.call_button_profile);
        nameTextView = view.findViewById(R.id.prof_name);

        SharedPreferences prefs = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);

        weightTextView.setText(prefs.getString("weight", "-"));
        heightTextView.setText(prefs.getString("height", "-"));
        bgTextView.setText(prefs.getString("bloodGroup", "-"));
        emergencyContactTextView_1.setText(prefs.getString("emergencyContact_1", "1234567890"));
        nameTextView.setText(prefs.getString("firstName", "User"));

        getData();

        /*mPieChart = (PieChart) getActivity().findViewById(R.id.piechart);
        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
*/
//        ImageView callButton =(ImageView) view.findViewById(R.id.call_button_profile);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        Log.i("TAG", "onPageSelected: setting Profile");
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");


        pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), aadharupload.class);
                startActivity(intent);

            }

        });
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Pedometer.class);
                startActivity(intent);

            }

        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getActivity().getPackageManager();
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    TextView e = getActivity().findViewById(R.id.call_profile_number);

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + e.getText().toString()));
                    startActivity(intent);

                }
            }
        });


        return view;
    }

    {
        firebaseAuth = FirebaseAuth.getInstance();
        member = firebaseAuth.getCurrentUser();
        String token = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(token);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}