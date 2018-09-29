package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

   private Button Homebtn, clickToPost, clickToProfile, logoutBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mAuth = FirebaseAuth.getInstance();

       // Homebtn = (Button) findViewById(R.id.Homebtn);
        clickToPost = (Button) findViewById(R.id.Postbtn);
        clickToProfile = (Button) findViewById(R.id.Profilebtn);

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

        clickToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //direct to profile page and ask backend for current login user's information.
                Toast.makeText(getBaseContext(),
                        "Profile!!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);

            }
        });
    }

    public void btnLogout(View view){
        //sign out from Firebase
        mAuth.signOut();

        //redirect to MainActivity
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // if you are redirecting from a fragment then use getActivity() as the context.
                startActivity(new Intent(HomePageActivity.this, MainActivity.class));
            }
        };
        Handler h = new Handler();
        // The Runnable will be executed after the given delay time
        h.postDelayed(r, 1500); // will be delayed for 1.5 seconds
    }


    public void setClickToPost(Button clickToPost) {
        this.clickToPost = clickToPost;
    }
}
