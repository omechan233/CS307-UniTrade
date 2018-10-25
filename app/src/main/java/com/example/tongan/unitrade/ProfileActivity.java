package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Comment;
import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    private String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Functions f;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        //initialize fields
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
        final String username = "";
        String phone = "";
        String address = "";

        /*
         * Retrieving data from the database to fill in EditText fields. Documents are specific to Firestore,
         * DO NOT CHANGE doc.get("[document]")
         */
        //retrieve username from user document
        DocumentReference userDocRef = db.collection("users").document(email);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        //update text boxes with user info from database
                        username_edit.setText(doc.get("user_name").toString());
                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                    }else{
                        Log.d(TAG, "No such document...");
                    }
                }
                else{
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //retrieve data from profile document
        DocumentReference profileDocRef = db.collection("profiles").document(email);
        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        //update text boxes with user info from database
                        address_edit.setText(doc.get("address").toString());
                        phone_edit.setText(doc.get("phone_number").toString());

                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                    }else{
                        Log.d(TAG, "No such document...");
                    }
                }
                else{
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        //display comments
        final LinearLayout comment_view = (LinearLayout) findViewById(R.id.comment_area);
        //THIS IS A HARD CODING STRING COMMENT ARRAY!
        //todo:get comments from backend
        final String[] comments = new String[] {"comment1", "comment2", "comment3", "4", "5"};

        for (int i = 0; i < comments.length; i++) {
            LinearLayout comment = new LinearLayout(getBaseContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
            comment.setLayoutParams(params);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200);
            TextView tv = new TextView(getBaseContext());
            tv.setLayoutParams(params);
            //todo : Set the text here to actual comment
            String text = "  Username\n    This is a hard coding comment " + (i + 1) + "\n Remember to change here.";
            tv.setText(text);
            tv.setTextColor(Color.parseColor("#000000"));
            comment.addView(tv);
            RatingBar rating = new RatingBar(getBaseContext(), null, android.R.attr.ratingBarStyleSmall);
            rating.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rating.setStepSize(1);
            rating.setClickable(false);
            rating.setNumStars(5);
            //todo : get rating of comment and use setRating()
            rating.setRating(3);
            comment.addView(rating);
            comment_view.addView(comment);
        }

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
                    f.update_profile(email_edit.getText().toString(), phone_edit.getText().toString(), 1, "", "", address_edit.getText().toString());


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

    public String get_username_by_email(String email){
        final String[] result = {""};
        final Task<DocumentSnapshot> task = db.collection("users").document(email).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                result[0] = task.getResult().toString();
                Log.d(TAG, "get_username_by_email: onSuccess: found result: " + result[0], task.getException());
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "get_username_by_email: onFailure: did not find result ", task.getException());
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(result[0].isEmpty())
            System.out.println("OOOOOOOOOOOOF?");
        return result[0];
    }
}
