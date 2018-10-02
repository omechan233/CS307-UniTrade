package com.example.tongan.unitrade;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.example.tongan.unitrade.objects.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//array of option --> ArrayAdpter --> wishlist_listview
//listview{fav_items.xml}


public class MainActivity extends AppCompatActivity{
    /*
    public static void main(String[] args){
       // f1.add_wishlist("TongAn12:03:5909212019", "KennyPiggy");
    }
    */
    private FirebaseAuth mAuth;
    private Functions f1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //f1 = new Functions();

        Button loginBtn = (Button) findViewById(R.id.login_login_btn);
        Button registerBtn = (Button) findViewById(R.id.login_register_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authentication();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        //congifure wishlist
       //populateWishlistview();
        //initView();
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
            h.postDelayed(r, 500); // will be delayed for 0.5 seconds
        }
    }

    private boolean isUserLoggedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    //get user input username & password when login
    private String getEmail() {
        EditText editText = (EditText) findViewById(R.id.login_email_input);
        String email = editText.getText().toString();
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
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(MainActivity.this, "Authentication failed. Invalid Username or Password given.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void populateWishitem() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //get a list of wishitem id
        List<String> wishitem_id = new ArrayList<>();
        List<Item> wishlist = new ArrayList<>();
        wishitem_id = f1.get_itemid_list(currentUser.getUid());
        //use id to get a list of item
        wishlist = f1.get_wanted_item(wishitem_id);
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
}

