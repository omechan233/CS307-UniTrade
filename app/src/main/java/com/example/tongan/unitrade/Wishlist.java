package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Wishlist extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Button homebtn = (Button) findViewById(R.id.wishlist_home_btn);
        Button profilebtn = (Button) findViewById(R.id.wishlist_profile_btn);

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wishlist.this, HomePageActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wishlist.this, ProfileActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

    }
}
