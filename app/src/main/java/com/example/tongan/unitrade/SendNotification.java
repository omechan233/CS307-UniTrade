package com.example.tongan.unitrade;
import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.tongan.unitrade.objects.Order;

public class SendNotification extends AppCompatActivity {
    private Button itemSold_btn;
    private Button methodChange_btn;
    private Button insideAppNotice_btn;

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
        insideAppNotice_btn = (Button) findViewById(R.id.insideApp_notice);



        /**********************************************************************/
        // Following code is for pushing a system notification, to notify user "Your posted item is sold."
        /********************************************************************/


        itemSold_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Todo: check back-end Notification status
                //if current user's ItemSold Notification status is on, and the item they posted is sold,
                //use following code to push notification.

                //Todo: front-end functionality starts here, combine them with back-end.
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
                //front-end functionality ends here.

            }
        });

        /**********************************************************************/
        // Following code is for pushing a system notification, to notify user "Your trading method is changed."
        /********************************************************************/
        methodChange_btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                    //Todo: check back-end Notification status
                    //if current user's MethodChange Notification status is on, and if the trading method is changed,
                    //use following code to push notification.

                    //Todo: front-end functionality starts here, combine them with back-end.
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Intent intent = new Intent(SendNotification.this, Order.class);
                    PendingIntent ma = PendingIntent.getActivity(SendNotification.this,0,intent,0);
                    Notification notification = new NotificationCompat.Builder(SendNotification.this, "methodChange")
                            .setContentTitle("UniTrade:")
                            .setContentText("Your item's trading method is changed!")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setAutoCancel(true)
                            .setContentIntent(ma)
                            .build();

                    manager.notify(1, notification);
                //front-end functionality ends here.
            }
        });

        /**********************************************************************/
        // Following code is for pushing a pop-up dialog window to notify users inside the App.
        /********************************************************************/

        insideAppNotice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Todo: front-end functionality starts here, combine them with back-end.

                AlertDialog.Builder builder = new AlertDialog.Builder(SendNotification.this);
                builder.setTitle("Notice:");
                builder.setMessage("Your trading method is changed!");
                builder.setCancelable(true);

                // user choose "Accepted" button:
                builder.setPositiveButton(
                        "Accept",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SendNotification.this, "You accepted!",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

                //user choose "Declined" button:
                builder.setNegativeButton(
                        "Decline",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SendNotification.this, "You declined!",
                                        Toast.LENGTH_SHORT).show();
                                // Todo: user decline this method change request, then back-end should change the order's trading method to the other one.
                                // Todo: then call the front-end pop-up dialog function (the code is following) again to notify user that request was declined by current user.

                            }
                        });
                builder.show();
                //front-end functionality ends here.

            }
        });



        /**********************************************************************/
        // Following code is for pushing a pop-up dialog window to notify users that request was declined by current user inside the App.
        /********************************************************************/
        Button decline_notice_btn = (Button) findViewById(R.id.decline_notice);
        decline_notice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: front-end functionality starts here, combine them with back-end.

                AlertDialog.Builder builder = new AlertDialog.Builder(SendNotification.this);
                builder.setTitle("Notice:");
                builder.setMessage("Your trading method request was declined by the user!");
                builder.setCancelable(true);

                // user choose "Accepted" button:
                builder.setPositiveButton(
                        "Fine!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SendNotification.this, "Fine!",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                builder.show();

                //front-end function ends here.

            }
        });



    }



}
