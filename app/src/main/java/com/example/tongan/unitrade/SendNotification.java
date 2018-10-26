package com.example.tongan.unitrade;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.example.tongan.unitrade.objects.Order;

public class SendNotification extends AppCompatActivity {
    private Button itemSold_btn;
    private Button methodChange_btn;

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendnotification);
        //create 2 notification categories in Android System Notification Setting page.
        //may not be used in our app. Can be ignored.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "ItemSold";
            String channelName = "Get Notified when Item is sold";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "methodChange";
            channelName = "Get Notified when trading method changed";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }

        itemSold_btn = (Button) findViewById(R.id.itemSold_notice);
        methodChange_btn = (Button) findViewById(R.id.methodChange_notice);

        itemSold_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: check back-end Notification status
                //if current user's ItemSold Notification status is on, and the item they posted is sold,
                //use following code to push notification.

                //Front-end push notification
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(SendNotification.this, Order.class);
                PendingIntent ma = PendingIntent.getActivity(SendNotification.this,0,intent,0);
                Notification notification = new NotificationCompat.Builder(SendNotification.this, "ItemSold")
                        .setContentTitle("UniTrade:")
                        .setContentText("Your item is sold!")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setAutoCancel(true)
                        .setContentIntent(ma)
                        .build();

                manager.notify(1, notification);

            }
        });


        methodChange_btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                    //Todo: check back-end Notification status
                    //if current user's MethodChange Notification status is on, and if the trading method is changed,
                    //use following code to push notification.

                    //Front-end push notification
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Intent intent = new Intent(SendNotification.this, Order.class);
                    PendingIntent ma = PendingIntent.getActivity(SendNotification.this,0,intent,0);
                    Notification notification = new NotificationCompat.Builder(SendNotification.this, "methodChange")
                            .setContentTitle("UniTrade:")
                            .setContentText("The Trading method is changed!")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setAutoCancel(true)
                            .setContentIntent(ma)
                            .build();

                    manager.notify(1, notification);
            }
        });


    }



}
