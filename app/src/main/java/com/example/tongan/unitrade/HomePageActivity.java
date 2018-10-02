package com.example.tongan.unitrade;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class HomePageActivity extends AppCompatActivity {
    public HomePageActivity(){

    }
   private Button Homebtn, clickToPost, clickToSetting;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

       // Homebtn = (Button) findViewById(R.id.Homebtn);
        clickToPost = (Button) findViewById(R.id.Postbtn);
        clickToSetting = (Button) findViewById(R.id.Settingbtn);

      /*  Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, HomePageActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });*/


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
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);

            }
        });









    }


    public void setClickToPost(Button clickToPost) {
        this.clickToPost = clickToPost;
    }
}
