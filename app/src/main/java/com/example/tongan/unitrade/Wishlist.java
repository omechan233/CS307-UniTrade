package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Wishlist extends AppCompatActivity {
    private Button Homebtn, Profilebtn;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Homebtn = (Button) findViewById(R.id.Homebtn);
        Profilebtn = (Button) findViewById(R.id.Profilebtn);

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wishlist.this, HomePageActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        Profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wishlist.this, Profilepage.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

    }
}
