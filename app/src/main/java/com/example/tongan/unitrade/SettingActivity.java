package com.example.tongan.unitrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    private String report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageButton wishListBtn = (ImageButton)findViewById(R.id.wishlist_setting_icon);
        wishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, Wishlist.class));
            }
        });

        ImageButton profileBtn = (ImageButton)findViewById(R.id.profile_setting_icon);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
            }
        });

        ImageButton logoutBtn = (ImageButton)findViewById(R.id.logout_setting_icon);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout user from Firebase
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
            }
        });

        ImageButton homePageBtn = (ImageButton)findViewById(R.id.settings_home_page_icon);
        homePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, HomePageActivity.class));
            }
        });
        
        ImageButton orderListBtn = (ImageButton)findViewById(R.id.myOrder_setting_icon);
        orderListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, OrderList.class));
            }
        });

        ImageButton notificationBtn = (ImageButton) findViewById(R.id.noti_setting_icon);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, NotificationPage.class));

            }
        });

        ImageButton reportBtn = (ImageButton) findViewById(R.id.settings_report);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SettingActivity.this);
                View promptView = layoutInflater.inflate(R.layout.report, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingActivity.this);
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.report_box);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                report = editText.getText().toString();
                                //todo : store the report to database

                                //Test the trackingNumber variable get correct input
                                //Toast.makeText(getBaseContext(), report, Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

    }
}
