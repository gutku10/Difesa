package com.tika.app2.Front;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.tika.app2.R;

public class SplashScrActivity extends AppCompatActivity {

    private ImageView logoSplash, chmaraTech, logoWhite;
    private Animation anim1, anim2, anim3;
    private static int SPLASH_TIME_OUT =1200;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*Intent intent = getIntent();
        if(intent!=null) {

            Bundle bundle = intent.getExtras();
            String emergencyUid = bundle.getString("Uid");
            SharedPreferences prefs = getSharedPreferences("emergency", Context.MODE_PRIVATE);
            prefs.edit().putString("Uid", emergencyUid).apply();


        }*/

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_scr);
        init();


        logoSplash.startAnimation(anim1);
        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                logoSplash.startAnimation(anim2);
                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        finish();






                               /* SharedPreferences prefs = getSharedPreferences("pref3",MODE_PRIVATE);
                                boolean firstStart = prefs.getBoolean("firstStarted",true);

                                if(firstStart==true){*/

                                    Intent loginIntent = new Intent(SplashScrActivity.this,loginpage.class);
                                    loginIntent.putExtra("firstOpen",true);
                                    startActivity(loginIntent);



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

//                                }

                                /*else {

                                    Intent homeIntent = new Intent(SplashScrActivity.this,HomeNew.class);
                                    homeIntent.putExtra("firstOpen",false);
                                    Log.i("TAG", " ###################################### "+firstStart);
                                    startActivity(homeIntent);


                                }*/




                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
//                logoSplash.startAnimation(anim2);
//                logoSplash.setVisibility(View.GONE);

//                logoWhite.startAnimation(anim3);
//                chmaraTech.startAnimation(anim3);
/*
                anim3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        logoWhite.setVisibility(View.VISIBLE);
//                        chmaraTech.setVisibility(View.VISIBLE);

                        finish();
                        startActivity(new Intent(SplashScrActivity.this,HomeNew.class));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
*/


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




    }


    private void init(){

        logoSplash = findViewById(R.id.ivLogoSplash);
        logoWhite = findViewById(R.id.ivLogoSplash);
//        chmaraTech = findViewById(R.id.ivCHTtext);
        anim1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        anim2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout);
        anim3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein);
    }


}
