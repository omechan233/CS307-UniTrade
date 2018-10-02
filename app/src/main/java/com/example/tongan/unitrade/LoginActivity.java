


package com.example.tongan.unitrade;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via username/password.
*/



public class LoginActivity extends AppCompatActivity  {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    //get user input username & password when login
    public String getEmail() {
        EditText editText = (EditText) findViewById(R.id.email_input);
        String username = editText.getText().toString();
        return username;
    }

    public String getPassword() {
        EditText editText = (EditText) findViewById(R.id.password_input);
        String password = editText.getText().toString();
        return password;
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

    //authentication, check user login info
    public boolean authentication() {
        String email = getEmail();
        String password = getPassword();
        if(isEmailValid(email) && isPasswordValid(password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(LoginActivity.this, "Authentication failed. Invalid Username or Password.",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}

