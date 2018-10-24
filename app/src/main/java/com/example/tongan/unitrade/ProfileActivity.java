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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        final TextView comment_view = (TextView) findViewById(R.id.comment_view);
        String commentid = sharedPreferences.getString("commentid",null);

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
        //retrieve comments
//        final ScrollView scrollView = (ScrollView) findViewById(R.id.comment_area);
//        scrollView.removeAllViews();
        Comment ct1=new Comment("guo361@purdue.edu","test1","g",
                '3',"2018/10/23/1132","xu830@purdue.edu");
        Comment ct2=new Comment("an82@purdue.edu","test2","a",
                '5',"2018/10/23/1133","xu830@purdue.edu");
        final ArrayList<Comment> com_test = new ArrayList<Comment>();
        com_test.add(ct1);
        com_test.add(ct2);

        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> my_comments = new ArrayList<String>();
                        my_comments = (List<String>) document.getData().get("my_comments");
                        if (my_comments == null || my_comments.isEmpty()) {
                            System.out.println("Nothing on the list!");
                        } else {
                            for (int i = 0; i < my_comments.size(); i++) {
                                final DocumentReference com_doc = db.collection("comments").document(my_comments.get(i));
                                final int finalI = i;
                                com_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Comment current_com = new Comment();
                                        current_com = documentSnapshot.toObject(Comment.class);
                                        System.out.println(current_com.getSender_name() + current_com.getPosted_time()
                                        + current_com.getContent() + current_com.getReceiver_name());
                                        System.out.println("#################################");
//                                        LinearLayout comment = new LinearLayout(getBaseContext());
//                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 200);
//                                        comment.setLayoutParams(params);
//                                        ImageView imageView = new ImageView(getBaseContext());
//                                        imageView.setImageResource(R.mipmap.poi_test_src);
//                                        params = new LinearLayout.LayoutParams(180, 180);
//                                        params.setMargins(20, 20, 0, 20);
//                                        imageView.setLayoutParams(params);
//                                        comment.addView(imageView);
//                                        TextView tv = new TextView(getBaseContext());
//                                        //todo : the String below is getting information from a hard coding ArrayList<Item>, change it to adapt the actual data retrieved from backend
//                                        String text = "\n" + current_com.getContent() + "\n" + current_com.getPosted_time() + "\n" + current_com.getBuyer_email();
//                                        tv.setText(text);
//                                        comment.addView(tv);
//                                        comment.setOnClickListener(new View.OnClickListener() {
//
//                                            public void onClick(View v) {
//                                                //todo:get the item_id of the selected item and store it into a global variable that can be used in the ItemDetail page(need to know which item to display detail)
//                                                startActivity(new Intent(ProfileActivity.this, ItemDetail.class));
//
//                                            }
//                                        });
//                                        scrollView.addView(comment);
                                    }
                                });
                            }


                        }
                        //result[0] = (String[])document.getData().get("my_items");
                        Log.e(TAG, "my item list found");

                    } else {
                        Log.e(TAG, "my item list not found");
                    }
                }
            });


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
