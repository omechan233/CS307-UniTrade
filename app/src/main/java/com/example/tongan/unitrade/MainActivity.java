package com.example.tongan.unitrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.opencensus.common.Function;


public class MainActivity extends AppCompatActivity{
    private SharedPreferences shared;
    private FirebaseAuth mAuth;
//    private Button sendEmailLinkBtn;
//    private TextView sendEmailLinkTxt;

    private Functions f1;
    public String Email = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        shared=getSharedPreferences("app", Context.MODE_PRIVATE);



        Button loginBtn = (Button) findViewById(R.id.login_login_btn);
        Button registerBtn = (Button) findViewById(R.id.login_register_btn);
        Button resetPasswd = (Button) findViewById(R.id.login_resetPasswd_btn);
//        sendEmailLinkBtn = (Button) findViewById(R.id.login_sendEmailLink_btn);
//        sendEmailLinkTxt = (TextView) findViewById(R.id.login_emailLink_txt);

        //hide these until we know user has not been verified
//        sendEmailLinkTxt.setVisibility(View.GONE);
//        sendEmailLinkBtn.setVisibility(View.GONE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmailVerified()) { //only authenticate if the email is verified
                    authentication();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        resetPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder reset_pswd_dialog = new AlertDialog.Builder(MainActivity.this);
                reset_pswd_dialog.setTitle("Reset Password");

                //set input
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                reset_pswd_dialog.setView(input);

                reset_pswd_dialog.setPositiveButton("SEND EMAIL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String resetPwdEmail = input.getText().toString();
                        mAuth.sendPasswordResetEmail(resetPwdEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful());{
                                            Toast.makeText(MainActivity.this, "Email was sent to the given address",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

                reset_pswd_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                reset_pswd_dialog.show();
            }
        });

 /*       sendEmailLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    mAuth.getCurrentUser().sendEmailVerification(); //send email verification again

                    Toast.makeText(MainActivity.this, "Another email has been sent to your address!",
                            Toast.LENGTH_SHORT).show();

                    //hide these again
                    sendEmailLinkTxt.setVisibility(View.GONE);
                    sendEmailLinkBtn.setVisibility(View.GONE);
                }
            }
        });
*/
    }

    protected void onStart(){
        super.onStart();
        //check if user is already logged in, if so then forward to HomePage
        if(isUserLoggedIn() && isEmailVerified()){
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // if you are redirecting from a fragment then use getActivity() as the context.
                    startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                }
            };
            Handler h = new Handler();
            // The Runnable will be executed after the given delay time
            h.postDelayed(r, 500); // will be delayed for 0.5 seconds
        }
    }

    private boolean isEmailVerified(){
        mAuth = FirebaseAuth.getInstance(); //reload Instance
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            System.out.println("MAIN: Could not find current user! Assume they already verified after registering...");
            return true;
        }
        currentUser.reload(); //reload user in case they still have the app open when they get verified

        if(currentUser.isEmailVerified()){
            return true;
        }
        else{
            Toast.makeText(MainActivity.this, "Make sure to verify your email before logging in!",
                    Toast.LENGTH_SHORT).show();

            //have these appear
//            sendEmailLinkBtn.setVisibility(View.VISIBLE);
//            sendEmailLinkTxt.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean isUserLoggedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    //get user input username & password when login
    protected String getEmail() {
        EditText editText = (EditText) findViewById(R.id.login_email_input);
        String email = editText.getText().toString();
        this.Email = email;

        //save email in SharedPreferences object
        SharedPreferences.Editor edit = shared.edit();
        edit.putString("email", email);
        edit.apply();


        return email;
    }

    private String getPassword() {
        EditText editText = (EditText) findViewById(R.id.login_password_input);
        String password = editText.getText().toString();
        return password;
    }

    private boolean isEmailValid(String email) {
        //check if is empty
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        //This regex was provided by OWASP Validation Regex Repository
        //it will check to make sure email follows a format like so:
        //  email :  example@email.com
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isPasswordValid(String password){
        //check if is empty
        if (TextUtils.isEmpty(password)){
            return false;
        }

        //password must have a least one capital letter, one digit, and have a length between 6 and 20
        String regex = "^(?=.*[A-Z])(?=.*\\d)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);

        if(!m.find() || password.length() < 6 || password.length() > 20)
            return false;

        return true;
    }

    //authentication, check user login info
    private void authentication() {
        String email = getEmail();
        String password = getPassword();
        if(isEmailValid(email) && isPasswordValid(password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, re-direct to homepage
                                Toast.makeText(MainActivity.this, "Login Successful!",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed. Email or Password is incorrect.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(MainActivity.this, "Cannot Authenticate. Invalid Username or Password given.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}

