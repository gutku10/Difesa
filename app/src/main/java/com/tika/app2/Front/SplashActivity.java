package com.tika.app2.Front;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tika.app2.Front.Home.HomeNew;
import com.tika.app2.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT =1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences prefs = getSharedPreferences("pref3",MODE_PRIVATE);
                boolean firstStart = prefs.getBoolean("firstStarted",true);

                if(firstStart==true){

                    Intent homeIntent = new Intent(SplashActivity.this, HomeNew.class);
                    homeIntent.putExtra("firstOpen",true);
                    startActivity(homeIntent);



                    /*
                    Intent loginIntent = new Intent(SplashActivity.this,loginpage.class);
                    startActivity(loginIntent);
*/
                    SharedPreferences prefs2 = getSharedPreferences("pref3", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs2.edit();

                    editor.putBoolean("firstStarted", false);
                    editor.apply();




/*
                    Intent homeIntent = new Intent(SplashActivity.this,InfantFragment.class);
                    startActivity(homeIntent);
                    */

                }

                else {

                    Intent homeIntent = new Intent(SplashActivity.this,HomeNew.class);
                    homeIntent.putExtra("firstOpen",false);
                    Log.i("TAG", " ###################################### "+firstStart);
                    startActivity(homeIntent);


                }

                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
