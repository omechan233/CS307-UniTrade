package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {

    private Button Homebtn, Wishlistbtn,Settingbtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

         Homebtn = (Button) findViewById(R.id.Homebtn);
         Wishlistbtn = (Button) findViewById(R.id.Wishlistbtn);
         Settingbtn = (Button) findViewById(R.id.Settingbtn);

         Homebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                 //intent.putExtra("name", name.getText().toString());
                 startActivity(intent);
             }
         });

         Wishlistbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(ProfileActivity.this, Wishlist.class);
                 //intent.putExtra("name", name.getText().toString());
                 startActivity(intent);
             }
         });

    }



}
