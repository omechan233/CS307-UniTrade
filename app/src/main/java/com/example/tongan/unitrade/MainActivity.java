package com.example.tongan.unitrade;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
//array of option --> ArrayAdpter --> wishlist_listview
//listview{fav_items.xml}


public class MainActivity extends AppCompatActivity{
    public static void main(String[] args){
        System.out.println(11);
        Functions f1 = new Functions();
       // f1.add_wishlist("TongAn12:03:5909212019", "KennyPiggy");
    }
        //private Button btnLogin, btnRegister,btnCreateAccount;
       // Button btnCreateAccount;
    private FirebaseAuth mAuth;
    private Button createAccountBtn;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        createAccountBtn = (Button) findViewById(R.id.registerBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        //congifure wishlist
       //populateWishlistview();
        //btnCreateAccount = (Button) findViewById(R.id.button3);
        //initView();
       /* btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
fix Button functions, add page redirection
            }
        });*/


    }

    protected void onStart(){
        super.onStart();
        //check if user is already logged in, if so then forward to HomePage
        if(isUserLoggedIn()){
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // if you are redirecting from a fragment then use getActivity() as the context.
                    startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                }
            };
            Handler h = new Handler();
            // The Runnable will be executed after the given delay time
            h.postDelayed(r, 1500); // will be delayed for 1.5 seconds
        }
    }

    private boolean isUserLoggedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }



    private void populateWishlistview() {
        //create list of items
        //following is a test
        List<String> itemname = new ArrayList<>();
        //put itemname into an array list



        //build adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,   //context
                R.layout.wishlistitem,  //layout to use
                itemname);   //items to display
        //configure list view
        ListView wishlist = (ListView) findViewById(R.id.wlistview);
        wishlist.setAdapter(adapter);
    }

    public void btnLogin(View view) {
        LoginActivity loginActivity = new LoginActivity();
        if (loginActivity.authentication()) {
            //go to home page
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            startActivity(intent);
        }
        //if user login failed, pop up message
        else {
            Toast.makeText(getBaseContext(), "Username or Password isn't correct!", Toast.LENGTH_LONG).show();
        }
    }

    public void btnRegister(View view) {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        //intent.putExtra("name", name.getText().toString());
        startActivity(intent);
    }

}

