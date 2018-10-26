package com.example.tongan.unitrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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

    }
}
