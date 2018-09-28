package com.example.tongan.unitrade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import butterknife.InjectView;

// package com.sourcey.materiallogindemo;

    public class SignupActivity extends AppCompatActivity {
        /*  private static final String TAG = "SignupActivity";

          @InjectView(R.id.input_name) EditText _nameText;
          @InjectView(R.id.input_email) EditText _emailText;
          @InjectView(R.id.input_password) EditText _passwordText;
          @InjectView(R.id.btn_signup) Button _signupButton;
          @InjectView(R.id.link_login) TextView _loginLink;
      */
        //@InjectView(R.id.button3) Button btnCreateAccount;
        Button btnCreateAccount;
        private static final String TAG = "SignupActivity";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            btnCreateAccount= (Button) findViewById(R.id.button3);
            btnCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (authentication() == true){
                        Toast.makeText(getBaseContext(),
                                "Success!", Toast.LENGTH_LONG).show();
                        Runnable r = new Runnable() {

                            @Override
                            public void run() {
                                // if you are redirecting from a fragment then use getActivity() as the context.
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));

                            }
                        };
                        Handler h = new Handler();
                        // The Runnable will be executed after the given delay time
                        h.postDelayed(r, 1500); // will be delayed for 1.5 seconds


                    }
                    else {
                        Toast.makeText(getBaseContext(),
                                "Input format incorrect!", Toast.LENGTH_LONG).show();
                    }



                }
            });

        }

        //input check

        public boolean inputCheck(String string){
            //check if is empty
            if (TextUtils.isEmpty(string)){
                return false;
            }
            // username, password needs to be at least 1 character adn no more than 8 char.
            else if (string.length() < 1 || string.length() > 8) {
                return false;
            }
            else return true;
        }
        public boolean emailCheck(String email){
            //check if is empty
            if (!email.contains("@")){
                return false;
            }
            else return !TextUtils.isEmpty(email);
        }


        //authentication:
        public boolean authentication(){
            //user entered infomation:
            EditText text1 = findViewById(R.id.username);
            EditText text2 = findViewById(R.id.email);
            EditText text3 = findViewById(R.id.password);
            String username = text1.getText().toString();
            String email = text2.getText().toString();
            String password = text3.getText().toString();
            //if all the input format is correct, do authentication with backend
            if (inputCheck(username) && emailCheck(email) && inputCheck(password)) {
                //send username, email, password to databsase and return true.
                return true;
            }
            else {
              return false;

            }



        }
    }
