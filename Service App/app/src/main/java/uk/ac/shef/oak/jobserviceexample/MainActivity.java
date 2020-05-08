/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.jobserviceexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import uk.ac.shef.oak.jobserviceexample.restarter.RestartServiceBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    Double longitude, latitude;

    PNConfiguration pnConfiguration;
    PubNub pubnub;

    String number, number2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



            Log.i("TAG", "onShake: ############################## " + number);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }








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


                    SharedPreferences prefs = getSharedPreferences("prefs",Context.MODE_PRIVATE);
                    latitude = Double.parseDouble(prefs.getString("latitude","0"));
                    longitude =Double.parseDouble(prefs.getString("longitude","0"));

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number2, null, "Help Me I am at https://maps.google.com/?q="+latitude+","+longitude, null, null);


                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(number));
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

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }
}
