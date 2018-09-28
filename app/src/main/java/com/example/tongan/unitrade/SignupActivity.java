package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


    public class SignupActivity extends AppCompatActivity {
        private Button btnCreateAccount;
        private static final String TAG = "SignupActivity";
        private FirebaseAuth mAuth;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);

            //get shared instance of FirebaseAuth
            mAuth = FirebaseAuth.getInstance();

            btnCreateAccount= (Button) findViewById(R.id.button3);
            btnCreateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (authentication()){
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

        @Override
        public void onStart(){
            super.onStart();
            //check if user is already signed in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                Toast.makeText(SignupActivity.this, "Sign out first before registering another account!",
                        Toast.LENGTH_SHORT).show();
            }
        }

        public boolean isUsernameValid(String string){
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

        public boolean isPasswordValid(String password){
            //check if is empty
            if (TextUtils.isEmpty(password)){
                return false;
            }
            //password must have a least one capital letter, one digit, and have a length between 6 and 14
            return (!password.matches("[A-Z]") || !password.matches("[0-9]") || password.length() < 6 || password.length() > 14);
        }

        public boolean isEmailValid(String email){
            //check if is empty
            if (TextUtils.isEmpty(email)){
                return false;
            }
            //This regex was provided by OWASP Validation Regex Repository
            //it will check to make sure email follows a format like so:
            //  email :  example@email.com
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            return email.matches(emailRegex);
        }

        //authentication:
        public boolean authentication(){
            //get user entered information:
            EditText text1 = findViewById(R.id.username);
            EditText text2 = findViewById(R.id.email);
            EditText text3 = findViewById(R.id.password);

            //convert to Strings
            String username = text1.getText().toString();
            String email = text2.getText().toString();
            String password = text3.getText().toString();

            //if all the input format is correct, do authentication with Firebase
            if (isUsernameValid(username) && isEmailValid(email) && isPasswordValid(password)) {
                //send username, email, password to database and return true.
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                return true;
            }
            else {
                Toast.makeText(SignupActivity.this, "Username, Email, or Password was invalid!",
                        Toast.LENGTH_SHORT).show();
              return false;

            }
        }
    }
