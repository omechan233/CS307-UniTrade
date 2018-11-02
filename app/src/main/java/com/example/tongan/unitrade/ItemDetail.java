package com.example.tongan.unitrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;
import com.example.tongan.unitrade.objects.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class ItemDetail extends AppCompatActivity {
    private static final String TAG = "Item detail page";
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
        time_edit.setTextIsSelectable(false);
        time_edit.setFocusable(false);


        desc_edit.setTextIsSelectable(false);
        name_edit.setTextIsSelectable(false);
        price_edit.setTextIsSelectable(false);
        desc_edit.setFocusable(false);
        name_edit.setFocusable(false);
        price_edit.setFocusable(false);

        final TextView category = (TextView)findViewById(R.id.item_category_detail);

        final TextView editBtn = (TextView) findViewById(R.id.detail_edit);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference item_doc = db.collection("items").document(itemid);
        item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Item item = documentSnapshot.toObject(Item.class);
                String item_name = item.getTitle();

                int availability = item.getStatus();
                if (availability == 2){
                    item_name += " (SOLD)";
                }

                Double price = item.getPrice();
                String description = item.getDescription();
                String seller = item.getSeller_name();
                final String item_id = itemid;
                Timestamp time = item.getPostTime();

                //format post date
                SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                format.setTimeZone(TimeZone.getTimeZone("EDT"));
                String postText = "Posted: " +format.format(item.getPostTime().toDate());

                // if current login user is not the seller of this item
                // set the edit item detail button invisible.
                if (!seller.equals(email)){
                    editBtn.setVisibility(View.INVISIBLE);
                }
                else{
                    System.out.println("This item is sold by yourself!!!");
                }

                String cate = item.getCategory();
                category.setText(cate);
                desc_edit.setText(description);
                time_edit.setText(postText);
                name_edit.setText(item_name);
                TextView seller_name = (TextView) findViewById(R.id.detail_seller);
                seller = "Seller : " + seller;
                seller_name.setText(seller);
                //ps: frontend here is pretty simple........ just set an onclicklistener.......)*/
                final String profile_email = item.getSeller_name();
                seller_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor edit = shared.edit();
                        edit.putString("profile_email", profile_email);
                        edit.apply();
                        startActivity(new Intent(ItemDetail.this, ProfileActivity.class));
                    }
                });
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


        desc_edit.setText(description);
        name_edit.setText(item_name);
        TextView seller_name = (TextView) findViewById(R.id.detail_seller);
        seller = "Seller : " + seller;
        seller_name.setText(seller);
        String temp = price + "";
        price_edit.setText(temp);
        */

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

                                Functions f = new Functions();
                                f.delete_post(item_id,email);
                                Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ItemDetail.this, OrderList.class));

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

        final String add = "+WishList";
        final String remove = "-WishList";
        wishListBtn.setText(add);

        DocumentReference profileDocRef = db.collection("profiles").document(email);
        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> my_wishlist = (List<String>) document.getData().get("wish_list");
                    if (my_wishlist == null || my_wishlist.isEmpty()) {
                        System.out.println("Nothing on the list!");
                        // button to be add to wishlist
                    }
                    else {
                        if(my_wishlist.contains(item_id)){
                            System.out.println("my wish list does contain this item");
                            wishListBtn.setText(remove);
                        }
                    }
                }
            }
        });

        wishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wishListBtn.getText().toString().equals(add)) {
                    // User is signed in
                    Functions f = new Functions();
                    f.add_wishlist(item_id, email);
                    Toast.makeText(getBaseContext(), "item is added to wishlist!", Toast.LENGTH_LONG).show();
                    wishListBtn.setText(remove);
                    finish();
                } else {
                    Functions f = new Functions();
                    f.delete_wishlist(item_id, email);
                    Toast.makeText(getBaseContext(), "item removed!", Toast.LENGTH_LONG).show();
                    wishListBtn.setText(add);
                    finish();
                }
            }
        });


        Button item_details_buy = (Button) findViewById(R.id.Item_Details_Buy);
        item_details_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AT: Buy success should not show up until its finished
                //Toast.makeText(getBaseContext(), "Buy Success!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ItemDetail.this, Purchase.class);
                startActivity(intent);
            }
        });


        /********************************************************
         * seller confirm payment after an item was sold by others
         ******************************************************/
        final Button confirm_btn = (Button) findViewById(R.id.confirm_btn);
        confirm_btn.setVisibility(View.INVISIBLE);
        //get item status from back-end;
        Boolean item_sold = true;
        //if item status is sold, set confirm btn visible by seller.

        final Query query = db.collection("orders").whereEqualTo("item_ID", itemid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        if (document.exists()) {
                            Order order= document.toObject(Order.class);
                            if(!order.isIs_sold()){
                                confirm_btn.setVisibility(View.VISIBLE);
                            }
                            return;
                        } else {
                            // display empty list
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                if (document.exists()) {
                                    Order order= document.toObject(Order.class);

                                    DocumentReference profiles_doc = db.collection("orders").document(order.getOrder_ID());
                                    profiles_doc.update("is_sold", true);
                                    confirm_btn.setVisibility(View.INVISIBLE);
                                    return;
                                } else {
                                    // display empty list
                                    Log.d(TAG, "No such document");
                                }
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
                Toast.makeText(getBaseContext(), "You confirm this payment!", Toast.LENGTH_LONG).show();
            }
        });












    }

}
