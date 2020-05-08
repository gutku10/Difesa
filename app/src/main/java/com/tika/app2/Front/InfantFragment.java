package com.tika.app2.Front;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tika.app2.Adapter.MyAdapter;
import com.tika.app2.R;
import com.tika.app2.Selections.InfantDetails;

import java.util.ArrayList;
import java.util.List;

public class InfantFragment extends Fragment {
    RecyclerView mRecyclerView;
    List<InfantDetails> myInfantList;
    InfantDetails mInfantDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_infant);

        View rootView = inflater.inflate(R.layout.activity_infant, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        myInfantList = new ArrayList<>();

        mInfantDetails = new InfantDetails("Hammer strike","Using your car keys is one of the easiest ways to defend yourself.","Easy",R.drawable.pic1,"VMSPCH4jyFo","In this martial arts technique, martial arts students will swing their clenched fist downwards to the target (i.e. an attacker’s nose).\nThis strike can be safer for a student’s hand because they are hitting a target with the “padded” bottom of a clenched fist. Thus there is a lower chance that a student will break their hand or knuckles with this strike.\nAll stretches and exercises should be supervised by a trained martial arts instructor in order to prevent injuries and to ensure the proper technique is utilized. ");
        myInfantList.add(mInfantDetails);
        mInfantDetails = new InfantDetails("Groin kick","making your escape possible.","Easy",R.drawable.pic2,"iPATx16rdeg","In self defense, the groin kick is considered as one of the most effective means of fighting off a man and rendering him helpless. Although the effect is generally temporary, it can buy you enough time to escape or even disarm your attacker.");
        myInfantList.add(mInfantDetails);
        mInfantDetails = new InfantDetails("Heel palm strike","This move can cause damage to the nose or throat.","Medium",R.drawable.pic3,"YtWGobxLV88","The heel of the palm is the area of the palm at the point where the forearm bones (radius and ulna) end, and large bones of the hand (metacarpals) begin. It’s the part of the hand that bears the most weight when you perform a bench press or a handstand.\n A Palm Heel Strike is delivered in a straight forward thrusting motion, with the hand flexed backwards, and the fingers pointing upward. The fingers should not make contact with your target during the strike; only the palm should touch the target.");
        myInfantList.add(mInfantDetails);
        mInfantDetails = new InfantDetails("Elbow strike","This may cause your attacker to loosen their grip, allowing you to run.","Hard",R.drawable.pic4,"RBzFpCN4D34","An elbow strike (commonly referred to as simply an \"elbow\") is a strike with the point of the elbow, the part of the forearm nearest to the elbow, or the part of the upper arm nearest to the elbow. Elbows can be thrown sideways similarly to a hook, upwards similarly to an uppercut, downwards with the point of the elbow, diagonally or in direct movement and in several other ways like during a jump etc.");


        myInfantList.add(mInfantDetails);

        MyAdapter myAdapter = new MyAdapter(getActivity(),myInfantList);
        mRecyclerView.setAdapter(myAdapter);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Self Defense");

        return rootView;

    }


}
