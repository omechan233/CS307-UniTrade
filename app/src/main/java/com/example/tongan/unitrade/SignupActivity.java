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
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.SignInMethodQueryResult;

import io.opencensus.common.Function;


public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignupActivity";
    private Functions f = new Functions();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //get shared instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        /*********************************
         * AT:
         * Change the authenticate() function return type from bool to int
         * So that the app can show the exact error message
         * when it fails to create a user.
         *
         * Also Create an user when successful.
         *
         * Status code:
         * -1, firebase authentication error
         * 0, success.
         * 1, username invalid
         * 2, email invalid
         * 3, password invalid
         ***********************************/

        Button btnCreateAccount = (Button) findViewById(R.id.register_createAccount_btn);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int auth_result = authentication();
                if (auth_result==0) {
                    int create_user = f.create_user(getUsername(),getEmail(),"",2,"",0,"","");
                    System.out.println("Create User status: "+create_user);
                    Toast.makeText(getBaseContext(),
                            "Success! Check your email for a verification link", Toast.LENGTH_LONG).show();

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

                }

                else if(auth_result==-1){
                    Toast.makeText(getBaseContext(),
                            "Firestore Authentication failed!", Toast.LENGTH_LONG).show();
                }
                else if(auth_result==1){
                    Toast.makeText(getBaseContext(),
                            "Username was invalid!", Toast.LENGTH_LONG).show();
                }
                else if(auth_result==2){
                    Toast.makeText(getBaseContext(),
                            "User email was invalid or may already exist!", Toast.LENGTH_LONG).show();
                }
                else if(auth_result==3){
                    Toast.makeText(getBaseContext(),
                            "User password was invalid!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //check if user is already signed in
        FirebaseUser fUser = mAuth.getCurrentUser();
        if (fUser != null) {
            Toast.makeText(SignupActivity.this, "Sign out first before registering another account!",
                    Toast.LENGTH_SHORT).show();
        }
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



    /*********************************
     * AT:
     * Change the authenticate() function return type from bool to int
     * So that the app can show the exact error message
     * when it fails to create a user.
     *
     * Status code:
     * -1, firebase authentication error
     * 0, success.
     * 1, username invalid
     * 2, email invalid
     * 3, password invalid
     ***********************************/

    public int authentication() {
        //get text field inputs
        String username = getUsername();
        String email = getEmail();
        String password = getPassword();

        if (!isUsernameValid(username)){
            return 1;
        }
        if (!isEmailValid(email)){
            return 2;
        }
        if (!isPasswordValid(password)){
            return 3;
        }

        final int result[] ={0};
          mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            result[0]=0;
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException existsEmail){
                                Log.d(TAG, "onComplete: email exists in authentication");
                                result[0] = 2; //it failed! not sure why task.isSuccessful returned true though...
                            }
                            catch (Exception e){
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                            sendEmailVerification();
                        }
                        else {
                            result[0]=-1;
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException existsEmail){
                                Log.d(TAG, "onComplete: email exists in authentication");
                            }
                            catch (Exception e){
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                        }
                    }
                });

        return result[0];
    }

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

