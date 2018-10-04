package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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


        ImageButton homebtn = (ImageButton) findViewById(R.id.profile_home_page_icon);
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
        String username = "";

        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
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
                Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
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
