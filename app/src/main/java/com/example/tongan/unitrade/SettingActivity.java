package com.example.tongan.unitrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Report;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
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

                final EditText report_text = (EditText) promptView.findViewById(R.id.report_box);
                final Spinner report_spinner = (Spinner) promptView.findViewById(R.id.report_spinner);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String report = report_text.getText().toString();
                                SharedPreferences shared = getSharedPreferences("app", Context.MODE_PRIVATE);
                                String email = shared.getString("email", null);
                                Timestamp reportTime = Timestamp.now();
                                String category = report_spinner.getSelectedItem().toString();

                                if(email == null) {
                                    System.out.println("ERROR getting info");
                                    Toast.makeText(SettingActivity.this, "Error Sending Report, Please try again!", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                                else if(report.isEmpty()){
                                    Toast.makeText(SettingActivity.this, "Please provide input before sending a report", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                                else { //send report
                                    Functions f = new Functions();
                                    f.addReport(new Report(email, reportTime, report, category));
                                    Toast.makeText(SettingActivity.this, "Report Sent! We will address your concerns ASAP, thank you!", Toast.LENGTH_LONG).show();
                                }
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
