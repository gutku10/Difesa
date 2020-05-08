package com.tika.app2.Front;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.tika.app2.Front.Home.HomeNew;
import com.tika.app2.Utils.GetLocation;
import com.tika.app2.R;


public class DashboardFragment extends Fragment implements View.OnClickListener {

    View view;
    Button  news,maps,sms,hardware,login,defence,sos,olaButton,fir;
    ImageView profilePic,ola_top;
    BottomNavigationView navigation;
    Toolbar toolbar;

    Double latitude,longitude;

    ViewPager pager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        news = (Button) view.findViewById(R.id.dashnews);
        maps = (Button) view.findViewById(R.id.dashmap);
        //sms = (Button) view.findViewById(R.id.dashsms);
        sos = (Button) view.findViewById(R.id.dashsos);
        //login = (Button) view.findViewById(R.id.dashlogin);
        defence = (Button) view.findViewById(R.id.dashfight);
        profilePic  = view.findViewById(R.id.profilepic);
        fir = (Button) view.findViewById(R.id.fir_button);
        ola_top = (ImageView) view.findViewById(R.id.ola_dash_top);
//        olaButton = (Button) view.findViewById(R.id.ola_dash_card);






//        Toolbar toolbar = view.findViewById(R.id.toolbar);
        Log.i("TAG", "onPageSelected: setting DashBoard" );
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("DashBoard");


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeNew)getActivity()).getViewPager().setCurrentItem(2);            }
        });

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeNew)getActivity()).getViewPager().setCurrentItem(5);
                /*Fragment fragment = new NewsActivity();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null)
                        .commit();*/

            }
        });
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent h= new Intent(getActivity(), MapActivity.class);
                startActivity(h);

            }
        });
        /*login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent h= new Intent(getActivity(), loginpage.class);
                startActivity(h);

            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent h= new Intent(getActivity(), MapActivity.class);
                startActivity(h);

            }
        });*/
        defence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeNew)getActivity()).getViewPager().setCurrentItem(4);

                /*Fragment fragment = new InfantFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null)
                    .commit();*/

                /*Intent h= new Intent(getActivity(), InfantFragment.class);
                startActivity(h);*/

            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h= new Intent(getActivity(), SosActivity.class);
                startActivity(h);

            }
        });

        /*fir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent= new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalpolice.gov.in/ncr/State_Selection.aspx"));
                startActivity(browserIntent);

            }
        });*/

        ola_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetLocation getLocation = new GetLocation(getActivity(),getContext());

                SharedPreferences prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                latitude = Double.parseDouble(prefs.getString("latitude","0"));
                longitude = Double.parseDouble(prefs.getString("longitude","0"));
                Intent browserIntent= new Intent(Intent.ACTION_VIEW, Uri.parse("http://book.olacabs.com/?lat="+latitude+"&lng="+longitude+"&category=compact&utm_source=12343&drop_lat=30.356270&drop_lng=76.391291&dsw=yes"));
                startActivity(browserIntent);

            }
        });
//
//        olaButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent browserIntent= new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.olacabs.com/mobile"));
//                startActivity(browserIntent);
//
//            }
//        });


        return view;
    }










    @Override
    public void onClick(View v) {

    }
}