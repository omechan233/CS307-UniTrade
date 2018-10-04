package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private Functions f;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        //initialize fields
        mAuth = FirebaseAuth.getInstance();
        f = new Functions();
        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);


        ImageButton homebtn = (ImageButton) findViewById(R.id.profile_back_icon);
        Button wishlistbtn = (Button) findViewById(R.id.profile_wishlist_btn);

        final EditText username_edit = (EditText)findViewById(R.id.input_username);
        username_edit.setFocusable(false);
        username_edit.setTextIsSelectable(false);

        final EditText phone_edit = (EditText)findViewById(R.id.input_phone);
        phone_edit.setFocusable(false);
        phone_edit.setTextIsSelectable(false);

        final EditText address_edit = (EditText)findViewById(R.id.input_address);
        address_edit.setFocusable(false);
        address_edit.setTextIsSelectable(false);

        final EditText email_edit = (EditText)findViewById(R.id.input_email);
        email_edit.setFocusable(false);
        email_edit.setTextIsSelectable(false);

        String email = sharedPreferences.getString("email", null);
        String username = getUserNameFromFirestore(email);
        String phone = "";
        String address = "";
        //todo: get username, email, phone, address from backend, and store it into the variables above
        //todo: during registration set new user's phone, address to empty string
        
        username_edit.setText(username);
        phone_edit.setText(phone);
        address_edit.setText(address);
        email_edit.setText(email);

        TextView edit = (TextView)findViewById(R.id.edit_profile);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView temp = (TextView)findViewById(R.id.edit_profile);
                if(temp.getText().toString().equals("Edit")) {
                    username_edit.setFocusable(true);
                    address_edit.setFocusable(true);
                    email_edit.setFocusable(true);
                    phone_edit.setFocusable(true);
                    username_edit.setTextIsSelectable(true);
                    address_edit.setTextIsSelectable(true);
                    email_edit.setTextIsSelectable(true);
                    phone_edit.setTextIsSelectable(true);
                    String text = "Confirm";
                    temp.setText(text);
                } else {

                    //todo: use xxx_edit.getText().toString() to get updated input and update it into backend

                    username_edit.setFocusable(false);
                    address_edit.setFocusable(false);
                    email_edit.setFocusable(false);
                    phone_edit.setFocusable(false);
                    username_edit.setTextIsSelectable(false);
                    address_edit.setTextIsSelectable(false);
                    email_edit.setTextIsSelectable(false);
                    phone_edit.setTextIsSelectable(false);
                    String text = "Edit";
                    temp.setText(text);
                }
            }
        });

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                startActivity(intent);*/
                finish();
            }
        });

        wishlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Wishlist.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

    }

    private  String getUserNameFromFirestore(String email){
        String ret = "";
        ret = f.get_username_by_email(email);
        return ret;
    }


}
