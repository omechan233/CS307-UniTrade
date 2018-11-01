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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignupActivity";
    private Functions f = new Functions();

    //Toast Fields
    public static final String INVALID_USERNAME = "Username was invalid! It may already exist...";
    public static final String INVALID_EMAIL = "Email  was invalid";
    public static final String INVALID_PASSWORD = "User password was invalid! Passwords must have at least one capital letter, a digit, and between 6 and 20 characters total";
    public static final String EXISTING_EMAIL = "Email  already exists! Try again with a different email.";
    public static final String SUCCESS = "Success! Check your email for a verification link";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //get shared instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        /**
         * Authentication through Firebase
         *
         * Checks for valid inputs and if email already exists
         */
        Button btnCreateAccount = (Button) findViewById(R.id.register_createAccount_btn);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = getUsername();
                final String email = getEmail();
                final String password = getPassword();

                //check inputs for validity before passing them to firebase
                if(!isEmailValid(email)){
                    Toast.makeText(getBaseContext(), INVALID_EMAIL, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(!isPasswordValid(password)) {
                    Toast.makeText(getBaseContext(), INVALID_PASSWORD, Toast.LENGTH_LONG).show();
                    return;
                }
                else if(!isUsernameValid(username)) {
                    Toast.makeText(getBaseContext(), INVALID_USERNAME, Toast.LENGTH_LONG).show();
                    return;
                }

                //if inputs are valid, proceed with authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendEmailVerification();

                                    int create_user = f.create_user(username,email,"",2,"",0,"","");
                                    System.out.println("Create User status: " +create_user);
                                    Toast.makeText(getBaseContext(), SUCCESS , Toast.LENGTH_LONG).show();

                                    Runnable r = new Runnable() {
                                        @Override
                                        public void run() {
                                            // if you are redirecting from a fragment then use getActivity() as the context.
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                        }
                                    };
                                    Handler h = new Handler();
                                    // The Runnable will be executed after the given delay time
                                    h.postDelayed(r, 1000); // will be delayed for 1.0 second

                                }else{
                                    try{
                                        throw task.getException();
                                    }catch(FirebaseAuthUserCollisionException userExists){
                                        Log.d(TAG, "create new user: email already exists in authentication! Message: " + userExists.getLocalizedMessage());
                                        Toast.makeText(getBaseContext(), EXISTING_EMAIL, Toast.LENGTH_LONG).show();
                                    }catch(NullPointerException e){
                                        Log.d(TAG, "Null pointer when attempting to add new user. Message: " +e.getLocalizedMessage());
                                    }
                                    catch(Exception e){
                                        Log.e(TAG, "General Exception caught. Message: " + e.getLocalizedMessage());
                                    }
                                }
                            }
                        });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //check if user is already signed in
        FirebaseUser fUser = mAuth.getCurrentUser();
        if (fUser != null)
            Toast.makeText(SignupActivity.this, "Sign out first before registering another account!", Toast.LENGTH_SHORT).show();
    }

    private String getEmail() {
        EditText editText = (EditText) findViewById(R.id.register_email_input);
        String email = editText.getText().toString();
        return email;
    }

    private String getPassword() {
        EditText editText = (EditText) findViewById(R.id.register_password_input);
        String password = editText.getText().toString();
        return password;
    }

    private String getUsername() {
        EditText editText = (EditText) findViewById(R.id.register_username_input);
        String username = editText.getText().toString();
        return username;
    }

    private boolean isUsernameValid(String username) {
        //check if is empty
        // AT: Check if the user name is already used
        // Check if username exists is currently unavailable.
        if (TextUtils.isEmpty(username) || f.username_exists(username)) {
            return false;
        }

        // username must be at least 5 characters
        return username.length() > 1;
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

    private boolean isEmailValid(String email) {
        //check if is empty
        if (TextUtils.isEmpty(email) || f.email_exists(email)) {
            return false;
        }
        //This regex was provided by OWASP Validation Regex Repository
        //it will check to make sure email follows a format like so:
        //  email :  example@email.com
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Sends Email Verification through Firebase to the newly-registered user
     */
    private void sendEmailVerification() {
        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            System.out.println("SIGNUP ACTVITIY: No User found!!");
            return;
        }
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignupActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

