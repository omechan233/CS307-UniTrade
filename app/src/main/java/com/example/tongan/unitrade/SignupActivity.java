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
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private static final String TAG = "SignupActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //get shared instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        btnCreateAccount = (Button) findViewById(R.id.createAccountButton);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authentication()) {
                    Toast.makeText(getBaseContext(),
                            "Success!", Toast.LENGTH_LONG).show();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            // if you are redirecting from a fragment then use getActivity() as the context.
                            startActivity(new Intent(SignupActivity.this, HomePageActivity.class));
                        }
                    };
                    Handler h = new Handler();
                    // The Runnable will be executed after the given delay time
                    h.postDelayed(r, 1500); // will be delayed for 1.5 seconds
                } else {
                    Toast.makeText(getBaseContext(),
                            "Username, Email, or Password was invalid!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //check if user is already signed in
        fUser = mAuth.getCurrentUser();
        if (fUser != null) {
            Toast.makeText(SignupActivity.this, "Sign out first before registering another account!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isUsernameValid(String username) {
        //check if is empty
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        // username, password needs to be at least 1 character adn no more than 8 char.
        return (username.length() < 1 || username.length() > 8);

    }

    private boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password))
            return false;

        //password must have a least one capital letter, one digit, and have a length between 6 and 14
        return (!password.matches("[A-Z]") || !password.matches("[0-9]") || password.length() < 6 || password.length() > 14);

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


    //authentication:
    public boolean authentication() {
        //get user entered information:
        EditText text1 = findViewById(R.id.username_input);
        EditText text2 = findViewById(R.id.email_input);
        EditText text3 = findViewById(R.id.password_input);

        //convert to strings we can use
        String username = text1.getText().toString();
        String email = text2.getText().toString();
        String password = text3.getText().toString();

        //if all the input format is correct, do authentication with backend
        if (isUsernameValid(username) && isEmailValid(email) && isPasswordValid(password)) {
            //send username, email, password to database and return true.
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                fUser = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });
            return true;
        }
        else return false;
    }
}

