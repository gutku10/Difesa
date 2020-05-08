package com.tika.app2.Medical;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import com.tika.app2.R;

public class bcgActivity extends AppCompatActivity{



    TextView title;
    TextView vaccineDescription;
    ImageView vaccineImage;
    String videoId;
    TextView description;
    Toolbar toolbar;

    /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_bcg, container, false);
        vaccineDescription = (TextView)(getActivity().findViewById(R.id.destext));
        vaccineImage = (ImageView)(getActivity().findViewById(R.id.inf1));
        title = (TextView) (getActivity().findViewById(R.id.tittext));
        description= (TextView) (getActivity().findViewById(R.id.destext));



        Bundle mBundle = this.getArguments();

        if(mBundle!=null){
            vaccineDescription.setText(mBundle.getString("Description"));
            vaccineImage.setImageResource(mBundle.getInt("Image"));
            videoId = mBundle.getString("VideoString");
            title.setText(mBundle.getString("Title"));
        }




        return view;


    }

    */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bcg);


        YouTubePlayerSupportFragmentX youTubePlayerFragment = new YouTubePlayerSupportFragmentX();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.youtube_Player,youTubePlayerFragment).commit();

//        getSupportActionBar().setTitle("Self Defense");
        Log.i("TAG", "onPageSelected: setting Self Defense" );




        vaccineDescription = (TextView)(findViewById(R.id.destext));
        vaccineImage = (ImageView)(findViewById(R.id.inf1));
        title = (TextView) (findViewById(R.id.tittext));
        description= (TextView) (findViewById(R.id.destext));


        Intent intent = getIntent();

        String topTitle = intent.getStringExtra("Title");
        vaccineDescription.setText(intent.getStringExtra("Description"));
        vaccineImage.setImageResource(intent.getIntExtra("Image",0));
        title.setText(topTitle);
        videoId = intent.getStringExtra("VideoString");

        youTubePlayerFragment.initialize("AIzaSyDwNM61_0aCyP898VU9-4smqeKLO7IXEPo",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });



    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.activity_bcg, container, false);

        YouTubePlayerSupportFragmentX youTubePlayerFragment = new YouTubePlayerSupportFragmentX();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.youtube_Player,youTubePlayerFragment).commit();


        vaccineDescription = (TextView)(rootView.findViewById(R.id.destext));
        vaccineImage = (ImageView)(rootView.findViewById(R.id.inf1));
        title = (TextView) (rootView.findViewById(R.id.tittext));
        description= (TextView) (rootView.findViewById(R.id.destext));

        Toolbar toolbar = findViewById(R.id.toolbar);



//        AppCompatActivity activity = new AppCompatActivity();





        *//*if(mBundle!=null){
            vaccineDescription.setText(mBundle.getString("Description"));
            vaccineImage.setImageResource(mBundle.getInt("Image"));
            videoId = mBundle.getString("VideoString");
            String topTitle = mBundle.getString("Title");
            title.setText(topTitle);

            toolbar.setTitle(topTitle);
        }*//*
        youTubePlayerFragment.initialize("AIzaSyDwNM61_0aCyP898VU9-4smqeKLO7IXEPo",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });


        *//*initialize("AIzaSyDwNM61_0aCyP898VU9-4smqeKLO7IXEPo",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
*//*


        return rootView;

        *//*playVideo(videoId,youtubePlayerView);*//*



*//*
        Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=VMSPCH4jyFo"));
        startActivity(browserIntent);
        *//*


    }*/
//main
    /*public void playVideo(final String videoId, YouTubePlayerView youTubePlayerView) {
        //initialize youtube player view
        youTubePlayerView.initialize("AIzaSyDwNM61_0aCyP898VU9-4smqeKLO7IXEPo",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }*/
}






