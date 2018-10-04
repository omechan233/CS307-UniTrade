package com.example.tongan.unitrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ItemDetail extends AppCompatActivity {
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String email = shared.getString("email","");
        final String itemid = shared.getString("itemid","");
        final String item_id = itemid;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        Button back = (Button) findViewById(R.id.detail_previous_page);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set initial contents
        final EditText desc_edit = (EditText) findViewById(R.id.description_content);
        final EditText name_edit = (EditText) findViewById(R.id.detail_item_name);
        final EditText price_edit = (EditText) findViewById(R.id.detail_price);
        final EditText time_edit = (EditText) findViewById(R.id.detail_posttime);

        desc_edit.setTextIsSelectable(false);
        name_edit.setTextIsSelectable(false);
        price_edit.setTextIsSelectable(false);
        desc_edit.setFocusable(false);
        name_edit.setFocusable(false);
        price_edit.setFocusable(false);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference item_doc = db.collection("items").document(itemid);
        item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Item item = documentSnapshot.toObject(Item.class);
                String item_name = item.getTitle();
                final Double price = item.getPrice();
                String description = item.getDescription();
                String seller = item.getSeller_name();
                final String item_id = itemid;
                String time = item.getPosted_time();

                desc_edit.setText(description);
                time_edit.setText(time);
                name_edit.setText(item_name);
                TextView seller_name = (TextView) findViewById(R.id.detail_seller);
                seller = "Seller : " + seller;
                seller_name.setText(seller);
                String temp = price + "";
                price_edit.setText(temp);



            }
        });

        /*
        String item_name = "TEST NAME";
        final Double price = -999.0;
        String description = "TEST INFORMATION";
        String seller = "TEST SELLER";
        final String item_id = "TongAn12:03:5909212019";
        //final String user_id ="guo361@purdue.edu";

        //todo : get above values from backend and store into variables

        desc_edit.setText(description);
        name_edit.setText(item_name);
        TextView seller_name = (TextView) findViewById(R.id.detail_seller);
        seller = "Seller : " + seller;
        seller_name.setText(seller);
        String temp = price + "";
        price_edit.setText(temp);
        */

        final TextView editBtn = (TextView) findViewById(R.id.detail_edit);
        final TextView delBtn = (TextView) findViewById(R.id.detail_delete);
        delBtn.setClickable(false);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp2 = editBtn.getText().toString();
                if (temp2.equals("Edit")) {
                    String SUBMIT_TEXT = "Submit";
                    desc_edit.setBackgroundResource(R.drawable.message_box);
                    name_edit.setBackgroundResource(R.drawable.message_box);
                    price_edit.setBackgroundResource(R.drawable.message_box);
                    desc_edit.setBackgroundResource(R.drawable.message_box);
                    editBtn.setText(SUBMIT_TEXT);
                    delBtn.setClickable(true);
                    delBtn.setVisibility(View.VISIBLE);
                    desc_edit.setTextIsSelectable(true);
                    name_edit.setTextIsSelectable(true);
                    price_edit.setTextIsSelectable(true);
                    desc_edit.setFocusable(true);
                    name_edit.setFocusable(true);
                    price_edit.setFocusable(true);
                } else {
                    final DocumentReference item_doc = db.collection("items").document(item_id);
                    item_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists()){
                                Item item = doc.toObject(Item.class);
                                double price = Double.parseDouble(price_edit.getText().toString());
                                Functions f = new Functions();
                                item.setDescription(desc_edit.getText().toString());
                                item.setPrice(price);
                                item.setTitle(name_edit.getText().toString());
                                f.delete_post(item_id,email);
                                db.collection("items").document(item.getid()).set(item);

                                final DocumentReference user_doc = db.collection("profiles").document(email);
                                user_doc.update("my_items", FieldValue.arrayUnion(item.getid()));
                            }
                        }
                    });

                    String EDIT_TEXT = "Edit";
                    desc_edit.setBackgroundResource(0);
                    name_edit.setBackgroundResource(0);
                    price_edit.setBackgroundResource(0);
                    desc_edit.setBackgroundResource(0);
                    editBtn.setText(EDIT_TEXT);
                    delBtn.setVisibility(View.INVISIBLE);
                    delBtn.setClickable(false);
                    desc_edit.setTextIsSelectable(false);
                    name_edit.setTextIsSelectable(false);
                    price_edit.setTextIsSelectable(false);
                    desc_edit.setFocusable(false);
                    name_edit.setFocusable(false);
                    price_edit.setFocusable(false);
                }
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ItemDetail.this)
                        .setTitle("Delete Post")
                        .setMessage("Are you sure to delete this post?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //todo : delete post function
                                Functions f = new Functions();
                                f.delete_post(item_id,email);
                                Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .show();
            }
        });

        final Button wishListBtn = (Button) findViewById(R.id.detail_wishlist);
        wishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wishListBtn.getText().toString().equals("+wishList")) {

                    //todo : add to wishList
                    if (email != null) {
                        // User is signed in
                        Functions f = new Functions();
                        f.add_wishlist(item_id, email);
                        Toast.makeText(getBaseContext(), "item is added to wishlist!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // No user is signed in
                        Toast.makeText(getBaseContext(), "please signin first!", Toast.LENGTH_LONG).show();
                    }


                    String remove = "-wishList";
                    wishListBtn.setText(remove);
                } else {

                    //todo : delete from wishList
                    Functions f = new Functions();
                    f.delete_wishlist(item_id, email);
                    Toast.makeText(getBaseContext(), "item removed!", Toast.LENGTH_LONG).show();
                    finish();
                    //get current user
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (user != null) {
//                        // User is signed in
//                        Functions f = new Functions();
//                        String userid = user.getUid();
//                        f.delete_wishlist(item_id, userid);
//                        Toast.makeText(getBaseContext(), "item removed!", Toast.LENGTH_LONG).show();
//                        finish();
//                    } else {
//                        // No user is signed in
//                        Toast.makeText(getBaseContext(), "please signin first!", Toast.LENGTH_LONG).show();
//                    }

                    String add = "+wishList";
                    wishListBtn.setText(add);
                }
            }
        });

    }

}
