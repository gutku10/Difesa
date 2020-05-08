/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.jobserviceexample;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonElement;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.message_actions.PNMessageAction;
import com.pubnub.api.models.consumer.objects_api.space.PNSpace;
import com.pubnub.api.models.consumer.objects_api.user.PNUser;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import uk.ac.shef.oak.jobserviceexample.Shake.ShakeDetector;
import uk.ac.shef.oak.jobserviceexample.listeners.PictureCapturingListener;
import uk.ac.shef.oak.jobserviceexample.services.APictureCapturingService;
import uk.ac.shef.oak.jobserviceexample.services.PictureCapturingServiceImpl;
import uk.ac.shef.oak.jobserviceexample.utilities.Notification;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Service extends android.app.Service implements SensorEventListener, PictureCapturingListener {
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service";
    private static Service mCurrentService;
    private int counter = 0;

    private static final float SHAKE_THRESHOLD_GRAVITY = 6F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector shakeDetector;
    private long mShakeTimestamp;
    private int mShakeCount;

    double latitude;
    double longitude;

    String number;
    String number2;

    PNConfiguration pnConfiguration;
    PubNub pubnub;

    FusedLocationProviderClient mFusedLocationClient;



    public static final String ACCOUNT_SID =
            "AC68429e765e5445a868afe0b854dc8f42";
    public static final String AUTH_TOKEN =
            "fd111f9a8e91ca11dc980e1cf67afe32";


    private void getLastLocation(){
        mFusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            SharedPreferences prefs = getSharedPreferences("prefs",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();

                            editor.putString("latitude",String.valueOf(location.getLatitude()));
                            editor.putString("longitude",String.valueOf(location.getLongitude()));

                            editor.apply();
                            editor.commit();

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Log.i(TAG, "message:#################################### capture" + latitude + " " + longitude);

                        }
                    }
                }
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            SharedPreferences prefs = getSharedPreferences("prefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("latitude",String.valueOf(mLastLocation.getLatitude()));
            editor.putString("longitude",String.valueOf(mLastLocation.getLongitude()));

            editor.apply();
            editor.commit();

            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    };

    public Service() {
        super();
    }

    void makeWhatsapp() throws IOException {
        {
            Intent i = new Intent(Intent.ACTION_VIEW);


            String url = null;
            try {
                url = "https://api.whatsapp.com/send?phone=" + "+91" +number + "&text=" + URLEncoder.encode("Help Me I am at https://maps.google.com/?q="+latitude+","+ longitude, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            i.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        mCurrentService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");
        counter = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        startTimer();


        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Service notification", "This is the service's notification", R.drawable.ic_sleep));
                Log.i(TAG, "restarting foreground successful");
                startTimer();

            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
        pubnub.unsubscribe();
    }


    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Difesa/number.txt";
        FileInputStream fis = null;
        byte[] buffer;
        try {
            fis = new FileInputStream(filepath);
            int length = (int) new File(filepath).length();
            buffer = new byte[length];
            fis.read(buffer, 0, length);

            number = new String(buffer, StandardCharsets.UTF_8);
            number2 = number.substring(3);

            SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("number", String.valueOf(number));
            editor.apply();
            editor.commit();



            Log.i(TAG, "onShake: ############################## " + number);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Starting timer");

        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        Log.i(TAG, "Scheduling...");
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //



        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-9301af48-556e-11ea-b828-26d2a984a2e5");
        pnConfiguration.setSecure(true);
        pnConfiguration.setUuid("apppubnub");
        pubnub = new PubNub(pnConfiguration);

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getOperation() != null) {
                    switch (status.getOperation()) {
                        // let's combine unsubscribe and subscribe handling for ease of use
                        case PNSubscribeOperation:
                        case PNUnsubscribeOperation:
                            // note: subscribe statuses never have traditional
                            // errors, they just have categories to represent the
                            // different issues or successes that occur as part of subscribe
                            switch (status.getCategory()) {
                                case PNConnectedCategory:
                                    // this is expected for a subscribe, this means there is no error or issue whatsoever
                                case PNReconnectedCategory:
                                    // this usually occurs if subscribe temporarily fails but reconnects. This means
                                    // there was an error but there is no longer any issue
                                case PNDisconnectedCategory:
                                    // this is the expected category for an unsubscribe. This means there
                                    // was no error in unsubscribing from everything
                                case PNUnexpectedDisconnectCategory:
                                    // this is usually an issue with the internet connection, this is an error, handle appropriately
                                case PNAccessDeniedCategory:
                                    // this means that PAM does allow this client to subscribe to this
                                    // channel and channel group configuration. This is another explicit error
                                default:
                                    // More errors can be directly specified by creating explicit cases for other
                                    // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                            }

                        case PNHeartbeatOperation:
                            // heartbeat operations can in fact have errors, so it is important to check first for an error.
                            // For more information on how to configure heartbeat notifications through the status
                            // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                            if (status.isError()) {
                                // There was an error with the heartbeat operation, handle here
                            } else {
                                // heartbeat operation was successful
                            }
                        default: {
                            // Encountered unknown status type
                        }
                    }
                } else {
                    // After a reconnection see status.getCategory()
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                String messagePublisher = message.getPublisher();
                System.out.println("Message publisher: " + messagePublisher);
                System.out.println("Message Payload:$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + message.getMessage());
                System.out.println("Message Subscription: " + message.getSubscription());
                System.out.println("Message Channel: " + message.getChannel());
                System.out.println("Message timetoken: " + message.getTimetoken());

                if(message.getMessage().toString().equals("\"Abhinav\"")) {
                    System.out.println("Message Payload:####################################calling  " + message.getMessage());


                    getLastLocation();

                    SmsManager smsManager = SmsManager.getDefault();

                    Log.i(TAG, "message:####################################" + latitude + " " + longitude);
                    smsManager.sendTextMessage(number2, null, "Help Me I am at https://maps.google.com/?q="+latitude+","+longitude, null, null);



                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(number));
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }








            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }

            @Override
            public void signal(PubNub pubnub, PNSignalResult signal) {
                System.out.println("Signal publisher: " + signal.getPublisher());
                System.out.println("Signal payload: " + signal.getMessage());
                System.out.println("Signal subscription: " + signal.getSubscription());
                System.out.println("Signal channel: " + signal.getChannel());
                System.out.println("Signal timetoken: " + signal.getTimetoken());
            }

            @Override
            public void user(PubNub pubnub, PNUserResult pnUserResult) {
                // for Objects, this will trigger when:
                // . user updated
                // . user deleted
                PNUser pnUser = pnUserResult.getUser(); // the user for which the event applies to
                pnUserResult.getEvent(); // the event name
            }

            @Override
            public void space(PubNub pubnub, PNSpaceResult pnSpaceResult) {
                // for Objects, this will trigger when:
                // . space updated
                // . space deleted
                PNSpace pnSpace = pnSpaceResult.getSpace(); // the space for which the event applies to
                pnSpaceResult.getEvent(); // the event name
            }

            @Override
            public void membership(PubNub pubnub, PNMembershipResult pnMembershipResult) {
                // for Objects, this will trigger when:
                // . user added to a space
                // . user removed from a space
                // . membership updated on a space
                JsonElement data = pnMembershipResult.getData(); // membership data for which the event applies to
                pnMembershipResult.getEvent(); // the event name
            }

            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult pnActionResult) {
                PNMessageAction pnMessageAction = pnActionResult.getMessageAction();
                System.out.println("Message action type: " + pnMessageAction.getType());
                System.out.println("Message action value: " + pnMessageAction.getValue());
                System.out.println("Message action uuid: " + pnMessageAction.getUuid());
                System.out.println("Message action actionTimetoken: " + pnMessageAction.getActionTimetoken());
                System.out.println("Message action messageTimetoken: " + pnMessageAction.getMessageTimetoken());

                System.out.println("Message action subscription: " + pnActionResult.getSubscription());
                System.out.println("Message action channel: " + pnActionResult.getChannel());
                System.out.println("Message action timetoken: " + pnActionResult.getTimetoken());
            }
        });

        SubscribeCallback subscribeCallback = new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // internet got lost, do some magic and call reconnect when ready
                    pubnub.reconnect();
                } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                    // do some magic and call reconnect when ready
                    pubnub.reconnect();
                } else {
                    Log.e("TAG",status.toString());
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }

            @Override
            public void signal(PubNub pubnub, PNSignalResult pnSignalResult) {

            }

            @Override
            public void user(PubNub pubnub, PNUserResult pnUserResult) {

            }

            @Override
            public void space(PubNub pubnub, PNSpaceResult pnSpaceResult) {

            }

            @Override
            public void membership(PubNub pubnub, PNMembershipResult pnMembershipResult) {

            }

            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult pnMessageActionResult) {

            }
        };

        pubnub.addListener(subscribeCallback);

        pubnub.subscribe()
                .channels(Arrays.asList("Help_Me")) // subscribe to channels
                .execute();
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        Log.i(TAG, "initialising TimerTask");
        final APictureCapturingService pictureService = PictureCapturingServiceImpl.getInstance(Service.this);


        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onShake(int count) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                getLastLocation();


                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Difesa/number.txt";
                FileInputStream fis = null;
                byte[] buffer;
                try {
                    fis = new FileInputStream(filepath);
                    int length = (int) new File(filepath).length();
                    buffer = new byte[length];
                    fis.read(buffer, 0, length);

                    number = new String(buffer, StandardCharsets.UTF_8);
                    number2 = number.substring(3);

                    Log.i(TAG, "onShake: ############################## " + number);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number2, null, "Help Me I am at https://maps.google.com/?q=28.7226794,77.1415056", null, null);

                        }
                    }, 2000); //Time in milisecond


                    Log.i(TAG, "onShake:$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + number);
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if(fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Log.i(TAG, "onShake:$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ tel:+91"+number2);
                        intent.setData(Uri.parse(number));
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }, 2000); //Time in milisecond






                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.skull1.hackathon1");
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        } else {
                        }

                    }
                }, 2000); //Time in milisecond
                /*try {
                    makeWhatsapp();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/



                //pictureService.startCapturing(Service.this);





                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }

            }
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(shakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        timerTask = new TimerTask() {
            @SuppressLint("MissingPermission")
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));



                /*if (counter%15 == 0){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:+911234567890"));
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);




                }*/

            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static Service getmCurrentService() {
        return mCurrentService;
    }

    public static void setmCurrentService(Service mCurrentService) {
        Service.mCurrentService = mCurrentService;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }

            // reset the shake count after 3 seconds of no shakes
            if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                mShakeCount = 0;
            }
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }


            mShakeTimestamp = now;
            mShakeCount++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {

    }

    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {

    }


}
