package com.example.tongan.unitrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class ItemDetail extends AppCompatActivity {
    private String trackingNumber;
    private static final String TAG = "Item detail page";
    SharedPreferences shared;
    FirebaseStorage storage;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String email = shared.getString("email","");
        final String itemid = shared.getString("itemid","");
        final String item_id = itemid;
        storage = FirebaseStorage.getInstance();


        final Button tracking_number = (Button)findViewById(R.id.tracking_number);

        tracking_number.setVisibility(View.INVISIBLE);
        tracking_number.setClickable(false);
        //todo set the shipping information text INVISIBLE

        DocumentReference temp_item = db.collection("items").document(item_id);
        temp_item.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    Item item = doc.toObject(Item.class);
                    //todo check the status of online paying
                    boolean is_paid = true;
                    if(item.getSeller_name().equals(email) && is_paid){
                        tracking_number.setVisibility(View.VISIBLE);
                        tracking_number.setClickable(true);
                        //todo set the shipping information text VISIBLE
                    }
                }
            }
        });
        // AT: I tried to write something to get the shipping address but failed... because i really dont know how to show that text...
        // Bur the code is write here! so feel free to copy things for the back end and display it
//        shipping_address.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LayoutInflater layoutInflater = LayoutInflater.from(ItemDetail.this);
//                View promptView = layoutInflater.inflate(R.layout.information_dialog, null);
//                final EditText ship_address = (EditText)promptView.findViewById(R.id.shipping_add_text);
//                DocumentReference temp_item2 = db.collection("items").document(item_id);
//                temp_item2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        DocumentSnapshot doc = task.getResult();
//                        if(doc.exists()){
//                            Item item = doc.toObject(Item.class);
//                            ship_address.setText(("Name: " + item.getShipping_name() + "\nAddress" + item.getShipping_address() + "\nPhone: " + item.getShipping_phone()));
//                        }
//                    }
//                });
//            }
//        });

        tracking_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(ItemDetail.this);
                View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ItemDetail.this);
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.tracking_number_edittext);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                trackingNumber = editText.getText().toString();
                                System.out.println("tracking:::::::::::::" + trackingNumber);
                                db.collection("items").document(item_id)
                                        .update("trackingnumber", trackingNumber);
                                db.collection("items").document(item_id)
                                       .update("is_shipped", true);
                                Query query =  db.collection("orders").whereEqualTo("item_ID",item_id);
                                db.collection("orders")
                                        .whereEqualTo("item_ID", item_id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        final DocumentReference order_doc = db.collection("orders").document(document.getId());
                                                        order_doc.update("is_shipped",true);
                                                        order_doc.update("trackingnumber",trackingNumber);
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });


                                //Test the trackingNumber variable get correct input
                                Toast.makeText(getBaseContext(), "Your tracking number is " + trackingNumber, Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

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
        final ImageView item_pic = findViewById(R.id.detail_image);
        final TextView seller_name = (TextView) findViewById(R.id.detail_seller);
        final TextView trackingnumber = (TextView) findViewById(R.id.detail_tracking);

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
                String track = item.getTrackingnumber();
                category.setText(cate);
                desc_edit.setText(description);
                time_edit.setText(postText);
                name_edit.setText(item_name);

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

                //set tracking number
                trackingnumber.setText(track);

                //Set Item image to one uploaded with the post, if it exists
                StorageReference storageRef = storage.getReference();
                String picPath = documentSnapshot.getString("item_image");
                if(picPath != null && !picPath.equals("")) {
                    StorageReference picRef = storageRef.child(picPath);

                    try {
                        //TODO: add logic to allow for different file types
                        final File localFile = File.createTempFile("Images", "jpg");
                        if (localFile != null) {
                            picRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    item_pic.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Something went wrong with getting the profile image! Message: " + e.getLocalizedMessage());
                                }
                            });
                        } else {
                            Log.e(TAG, "Improper File Type!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Null pointer found when attempting to retrieve image, message: " + e.getLocalizedMessage());
                    }
                }
                else{ //default image
                    item_pic.setImageResource(R.mipmap.poi_test_src);
                }
            }
        });

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
                String price = price_edit.getText().toString();
                String seller_email = seller_name.getText().toString();
                SharedPreferences.Editor edit = shared.edit();
                edit.putString("item_price", price);
                edit.putString("seller_email",seller_email);
                edit.apply();




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
