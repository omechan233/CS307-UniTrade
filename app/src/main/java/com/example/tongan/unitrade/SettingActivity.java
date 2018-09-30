package com.example.tongan.unitrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageButton wishListBtn = (ImageButton)findViewById(R.id.wishlist_setting_icon);
        wishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, Wishlist.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        ImageButton profileBtn = (ImageButton)findViewById(R.id.profile_setting_icon);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, Profilepage.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        ImageButton logoutBtn = (ImageButton)findViewById(R.id.logout_setting_icon);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        ImageButton homePageBtn = (ImageButton)findViewById(R.id.home_page_icon);
        homePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HomePageActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

    }
}
