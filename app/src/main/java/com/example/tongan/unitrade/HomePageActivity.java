package com.example.tongan.unitrade;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_homepage);

       //get Firebase instance
       mAuth = FirebaseAuth.getInstance();

       Button clickToPost = (Button) findViewById(R.id.home_post_btn);
       Button clickToSetting = (Button) findViewById(R.id.home_settings_btn);
       Button logoutBtn = (Button) findViewById(R.id.home_logout_btn);

       clickToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //direct to post item page and ask user to enter items information.
               Toast.makeText(getBaseContext(),
                       "Post!!!", Toast.LENGTH_LONG).show();


        }
        });

       clickToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //direct to profile page and ask backend for current login user's information.
                Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
                startActivity(intent);

            }
        });

       logoutBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //sign out user from Firebase
               mAuth.signOut();
               //direct user back to login page
               startActivity(new Intent(HomePageActivity.this, MainActivity.class));
           }
       });
    }
}
