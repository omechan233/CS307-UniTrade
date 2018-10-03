package com.example.tongan.unitrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ItemDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        desc_edit.setTextIsSelectable(false);
        name_edit.setTextIsSelectable(false);
        price_edit.setTextIsSelectable(false);
        desc_edit.setFocusable(false);
        name_edit.setFocusable(false);
        price_edit.setFocusable(false);

        String item_name = "TEST NAME";
        final Double price = -999.0;
        String description = "TEST INFORMATION";
        String seller = "TEST SELLER";
        final String item_id = "TongAn12:03:5909212019";

        //todo : get above values from backend and store into variables

        desc_edit.setText(description);
        name_edit.setText(item_name);
        TextView seller_name = (TextView) findViewById(R.id.detail_seller);
        seller = "Seller : " + seller;
        seller_name.setText(seller);
        String temp = price + "";
        price_edit.setText(temp);

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

                    //todo : use textView.getText().toString() to get new information from user input and update database

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
                                f.delete_post(item_id);
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
                    //get current user
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Functions f = new Functions();
                        String userid = user.getUid();
                        f.add_wishlist(item_id, userid);
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
                    //get current user
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Functions f = new Functions();
                        String userid = user.getUid();
                        f.delete_wishlist(item_id, userid);
                        Toast.makeText(getBaseContext(), "item removed!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // No user is signed in
                        Toast.makeText(getBaseContext(), "please signin first!", Toast.LENGTH_LONG).show();
                    }

                    String add = "+wishList";
                    wishListBtn.setText(add);
                }
            }
        });

    }

}
