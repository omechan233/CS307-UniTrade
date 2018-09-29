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
//array of option --> ArrayAdpter --> wishlist_listview
//listview{fav_items.xml}


public class MainActivity extends AppCompatActivity{
        //private Button btnLogin, btnRegister,btnCreateAccount;
       // Button btnCreateAccount;
    private FirebaseAuth mAuth;
    private Button createAccountBtn;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAccountBtn = (Button) findViewById(R.id.registerBtn);
/*      createAccountBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //redirect to SignUpActivity
                        startActivity(new Intent(MainActivity.this, SignupActivity.class));
                    }
                };
                Handler h = new Handler();
                //run after delay
                h.postDelayed(r, 1500);
            }
        });
*/
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

    private void populateWishlistview() {
        //create list of items
        //following is a test
        String[] myitems = {"A","B","C","D"};

        //build adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,   //context
                R.layout.wishlistitem,  //layout to use
                myitems);   //items to display
        //configure list view
        ListView wishlist = (ListView) findViewById(R.id.wlistview);
        wishlist.setAdapter(adapter);
    }

   /* public void initView () {
        //login button
        btnLogin = (Button) findViewById(R.id.button1);
        //Register button
        btnRegister = (Button) findViewById(R.id.button2);
        //Create Account button
        btnCreateAccount = (Button) findViewById(R.id.button3);
    }*/

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

    // setContentView(R.layout.activity_signup);

        // initView();
        //Login to SignUp page


        //Login Button
       /* btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity loginActivity = new LoginActivity();
                if (loginActivity.authentication() == true) {
                    //go to home page
                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                    startActivity(intent);
                }
                //if user login failed, pop up message
                else {
                    Toast.makeText(getBaseContext(),
                            "Username or Password isn't correct!", Toast.LENGTH_LONG).show();
                    return;
                }
            }

        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


      /*  public void onClick1 (View view){
            //if user login success
            LoginActivity loginActivity = new LoginActivity();
            if (loginActivity.authentication() == true) {
                //go to home page
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
            //if user login failed, pop up message
            else {
                Toast.makeText(getBaseContext(),
                        "Username or Password isn't correct!", Toast.LENGTH_LONG).show();
                return;
            }
        }
*/

//Register Button
     /*   public void onClick2 (View view){
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            //intent.putExtra("name", name.getText().toString());
            startActivity(intent);
        } */


        //Create Account Button
    /*    public void onClick3 (View view){
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            startActivity(intent);
            // Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            //intent.putExtra("name", name.getText().toString());
            // startActivity(intent);*/
       /*SignupActivity signUpActivity = new SignupActivity();
        EditText text1 = findViewById(R.id.username);
        EditText text2 = findViewById(R.id.email);
        EditText text3 = findViewById(R.id.password);
        String username = text1.getText().toString();
        String email = text2.getText().toString();
        String password = text3.getText().toString();*/
      /*  if (signUpActivity.authentication() == true){
            Toast.makeText(getBaseContext(),
                    "Success!", Toast.LENGTH_LONG).show();
            return;
           // Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
           // startActivity(intent2);

        }
        else {
            Toast.makeText(getBaseContext(),
                    "Input format incorrect!", Toast.LENGTH_LONG).show();

            //username format not correct
            if (!signUpActivity.inputCheck(username) && signUpActivity.emailCheck(email) && signUpActivity.inputCheck(password)){
                Toast.makeText(getBaseContext(),
                        "Username format incorrect!", Toast.LENGTH_LONG).show();
                return ;

            }
            //email format not correct
            else if (signUpActivity.inputCheck(username) && !signUpActivity.emailCheck(email) && signUpActivity.inputCheck(password)){
                Toast.makeText(getBaseContext(),
                        "Email format incorrect!", Toast.LENGTH_LONG).show();
                return ;

            }
            //password format not correct
            else if (signUpActivity.inputCheck(username) && signUpActivity.emailCheck(email) && !signUpActivity.inputCheck(password)){
                Toast.makeText(getBaseContext(),
                        "Password format incorrect!", Toast.LENGTH_LONG).show();
                return ;

            }
            //two or more format not correct

            Toast.makeText(getBaseContext(),
                    "Input format incorrect!", Toast.LENGTH_LONG).show();
            return ;

        }*/


