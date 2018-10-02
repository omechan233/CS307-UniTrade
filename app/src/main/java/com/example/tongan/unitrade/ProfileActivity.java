package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ProfileActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        ImageButton homebtn = (ImageButton) findViewById(R.id.profile_home_page_icon);
        Button wishlistbtn = (Button) findViewById(R.id.profile_wishlist_btn);

         homebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                 //intent.putExtra("name", name.getText().toString());
                 startActivity(intent);
             }
         });

         wishlistbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(ProfileActivity.this, Wishlist.class);
                 //intent.putExtra("name", name.getText().toString());
                 startActivity(intent);
             }
         });

    }



}
