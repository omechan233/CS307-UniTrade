package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderList extends AppCompatActivity {
    private static final String TAG = "OrderList";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences shared;
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);

        Button back = (Button) findViewById(R.id.posted_item_back);
        Button myOrder = (Button) findViewById(R.id.show_my_order);
        final Button myItem = (Button) findViewById(R.id.show_posted_item);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderList.this, SettingActivity.class);
                startActivity(intent);
            }
        });



        myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getBaseContext(), "Order Page", Toast.LENGTH_LONG).show();

                final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.list_area);
                linearLayout.removeAllViews();

                final String user_email = shared.getString("email", "");

                DocumentReference prof_doc = db.collection("profiles").document(user_email);

                prof_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //get my_items list from profile
                                ArrayList<String> my_items = (ArrayList<String>) document.get("my_orders");

                                if (my_items == null || my_items.isEmpty()) { //null check
                                    System.out.println("Nothing in the list!");
                                } else {
                                    for (int i = 0; i < my_items.size(); i++) {
                                        final DocumentReference item_doc = db.collection("orders").document(my_items.get(i));
                                        item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                final Order current_order = documentSnapshot.toObject(Order.class);

                                                final LinearLayout item = new LinearLayout(getBaseContext());
                                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 200);
                                                item.setLayoutParams(params);

                                                //get item's image from storage
                                                StorageReference storageRef = storage.getReference();
                                                String picPath = documentSnapshot.getString("item_image");

                                                StorageReference picRef = null;
                                                if(picPath != null)
                                                    picRef = storageRef.child(picPath);

                                                if(picRef != null){
                                                    try{
                                                        //TODO: add logic to allow for different file types
                                                        final File localFile = File.createTempFile("Images", "jpg");
                                                        if(localFile != null) {
                                                            picRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                    //create and set params of image in parent layout
                                                                    ImageView imageView = new ImageView(getBaseContext());
                                                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
                                                                    params.setMargins(20, 20, 0, 20);
                                                                    imageView.setLayoutParams(params);
                                                                    item.addView(imageView);
                                                                    imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));


                                                                    params = new LinearLayout.LayoutParams(500, 200);
                                                                    params.setMargins(20, 20, 0, 20);
                                                                    TextView tv = new TextView(getBaseContext());
                                                                    tv.setLayoutParams(params);
                                                                    //todo : the String below is getting information from a hard coding ArrayList<Item>, change it to adapt the actual data retrieved from backend
                                                                    String text = "\n" + current_order.getItem_title() + "\n" + current_order.getItem_price();
                                                                    tv.setText(text);
                                                                    item.addView(tv);
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    System.out.println("Something went wrong with getting the profile image! Message: " + e.getLocalizedMessage());
                                                                }
                                                            });
                                                        }else
                                                            Log.e(TAG, "Improper File Type!");
                                                    }catch(IOException e) {
                                                        Log.e(TAG, "IOError attempting to get image from Storage, message: " + e.getLocalizedMessage());
                                                    }
                                                }
                                                else{
                                                    //create and set params of image in parent layout
                                                    ImageView imageView = new ImageView(getBaseContext());
                                                    imageView.setImageResource(R.mipmap.poi_test_src);
                                                    params = new LinearLayout.LayoutParams(180, 180);
                                                    params.setMargins(20, 20, 0, 20);
                                                    imageView.setLayoutParams(params);
                                                    item.addView(imageView);

                                                    //create textview in parent layout
                                                    TextView tv = new TextView(getBaseContext());
                                                    String text = "\n" + current_order.getItem_title() + "\n" + current_order.getItem_price() + "\n" + current_order.getSeller_email() + "\n" + current_order.getOrder_time() + "\n" + current_order.getFace_to_face();
                                                    tv.setText(text);
                                                    params = new LinearLayout.LayoutParams(500, 200);
                                                    item.setLayoutParams(params);
                                                    params.setMargins(20, 20, 0, 20);
                                                    item.addView(tv);
                                                }
                                                final Order finalCurrent_Order = current_order;
                                                item.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        SharedPreferences.Editor edit = shared.edit();
                                                        String order_id = user_email+finalCurrent_Order.getOrder_time().toString();
                                                        System.out.println("current order is "+order_id);
                                                        edit.putString("order_ID", order_id);

                                                        edit.apply();

                                                        startActivity(new Intent(OrderList.this, OrderDetail.class));
                                                    }
                                                });

                                                linearLayout.addView(item);

                                            }
                                        });
                                    }
                                }
                                Log.d(TAG, "my order list found");

                            } else {
                                Log.e(TAG, "my order list not found");
                            }
                        }
                    }
                });

            }
        });


        myItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(getBaseContext(), "Posted Item Page", Toast.LENGTH_LONG).show();

                final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.list_area);
                linearLayout.removeAllViews();


                String email = shared.getString("email", "");
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference user_doc = db.collection("profiles").document(email);

                user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> my_items = (List<String>) document.getData().get("my_items");
                            if (my_items == null || my_items.isEmpty()) {
                                System.out.println("Nothing on the list!");
                            } else {
                                for (int i = 0; i < my_items.size(); i++) {
                                    final DocumentReference item_doc = db.collection("items").document(my_items.get(i));
                                    item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            final Item current_item = documentSnapshot.toObject(Item.class);
                                            final LinearLayout item = new LinearLayout(getBaseContext());

                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 200);
                                            item.setLayoutParams(params);

                                            //get item's image from storage
                                            StorageReference storageRef = storage.getReference();
                                            String picPath = current_item.getItem_image();

                                            StorageReference picRef = null;
                                            if(picPath != null && !picPath.isEmpty())
                                                picRef = storageRef.child(picPath);

                                            if(picRef != null){
                                                try{
                                                    //TODO: add logic to allow for different file types
                                                    final File localFile = File.createTempFile("Images", "jpg");
                                                    if(localFile != null) {
                                                        picRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                //create and set params of image in parent layout
                                                                ImageView imageView = new ImageView(getBaseContext());
                                                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
                                                                params.setMargins(20, 20, 0, 20);
                                                                imageView.setLayoutParams(params);
                                                                item.addView(imageView);
                                                                imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));


                                                                params = new LinearLayout.LayoutParams(180, 180);
                                                                params.setMargins(20, 20, 0, 20);
                                                                TextView tv = new TextView(getBaseContext());
                                                                //todo : the String below is getting information from a hard coding ArrayList<Item>, change it to adapt the actual data retrieved from backend
                                                                String text = "\n" + current_item.getTitle() + "\n" + current_item.getPrice() + "\n" + current_item.getSeller_name();
                                                                tv.setText(text);
                                                                item.addView(tv);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                System.out.println("Something went wrong with getting the profile image! Message: " + e.getLocalizedMessage());
                                                            }
                                                        });
                                                    }else
                                                        Log.e(TAG, "Improper File Type!");
                                                }catch(IOException e) {
                                                    Log.e(TAG, "IOError attempting to get image from Storage, message: " + e.getLocalizedMessage());
                                                }
                                            }
                                            else{
                                                //create and set params of image in parent layout
                                                ImageView imageView = new ImageView(getBaseContext());
                                                imageView.setImageResource(R.mipmap.poi_test_src);
                                                params = new LinearLayout.LayoutParams(180, 180);
                                                params.setMargins(20, 20, 0, 20);
                                                imageView.setLayoutParams(params);
                                                item.addView(imageView);

                                                //create textview in parent layout
                                                TextView tv = new TextView(getBaseContext());
                                                String text = "\n" + current_item.getTitle() + "\n" + current_item.getPrice() + "\n" + current_item.getSeller_name();
                                                tv.setText(text);
                                                item.addView(tv);
                                            }

                                            //Connect each item to it's detail page
                                            item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SharedPreferences.Editor edit = shared.edit();
                                                    edit.putString("itemid", current_item.getid());
                                                    edit.apply();

                                                    startActivity(new Intent(OrderList.this, ItemDetail.class));
                                                }
                                            });
                                            linearLayout.addView(item);
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
            }
        });

        //display my items by default
        myItem.callOnClick();

    }
}
