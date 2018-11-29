package com.example.tongan.unitrade;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Nonnull;

public class OrderDetail extends AppCompatActivity {
    private SharedPreferences shared;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "orderdetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        String order_ID = shared.getString("order_ID", "");
        final String email = shared.getString("email","");

        System.out.println("in order detail, order od is "+ order_ID);

        final TextView itemName = (TextView)findViewById(R.id.item_name_order_detail);
        final TextView price = (TextView)findViewById(R.id.price_order_detail);
        final TextView time = (TextView)findViewById(R.id.time_order_detail);
        final TextView method = (TextView)findViewById(R.id.trade_order_detail);
        final TextView seller = (TextView)findViewById(R.id.seller_order_detail);
        final TextView status = (TextView)findViewById(R.id.status_order_detail);
        final TextView tracking = (TextView) findViewById(R.id.tracking_order_detail);

        //get info from backend here
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference item_doc = db.collection("orders").document(order_ID);
        /**
         * real time method update
         */
        item_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    //Todo: refresh order detail page here
                    final Order current_order = snapshot.toObject(Order.class);

                    String itemName_String = "Item Name : " + current_order.getItem_title();
                    String price_String = "Price : " + current_order.getItem_price();/*+ get price from backend*/;
                    String trackingnumber = "Tracking number" + current_order.getTrackingnumber();

                    Timestamp time_stamp = current_order.getOrder_time();

                    //format post date
                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                    format.setTimeZone(TimeZone.getTimeZone("EDT"));
                    String posted_time = "Posted: " +format.format(time_stamp.toDate());


                    String time_String = "Order Time : " + posted_time; /*+ get time from backend*/;
                    String method_String = "Trade Method : ";


                    if(current_order.getFace_to_face()) {
                        method_String += "Face to Face";
                    }else{
                        method_String += "Online";
                    }
                    String seller_String = "Seller Name : " +current_order.getSeller_email(); /*+ get seller from backend*/
                    String status_String = "Order Status : ";
                    if (current_order.isIs_sold()){
                        status_String += "Closed";
                    }else{
                        status_String += "In progress";
                    }
                    itemName.setText(itemName_String);
                    price.setText(price_String);
                    time.setText(time_String);
                    method.setText(method_String);
                    seller.setText(seller_String);
                    status.setText(status_String);
                    tracking.setText(trackingnumber);
                  

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        /***
         * item sold notification to seller
         */
        //get users sold notification setting
        DocumentReference userDocRef = db.collection("users").document(email);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        //update text boxes with user info from database
                        String notification = doc.get("notification").toString();
                        if (notification.equals("0")) {
                        } else {
                            //get order list from profile
                            final DocumentReference profiles = db.collection("profiles").document(email);

                            profiles.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        List<String> my_order = new ArrayList<String>();
                                        my_order = (List<String>) document.getData().get("my_orders");
                                        if (my_order == null || my_order.isEmpty()) {
                                            System.out.println("Nothing on the list!");
                                        } else {
                                            for (int i = 0; i < my_order.size(); i++) {
                                                //get item from order list
                                                final DocumentReference order_doc = db.collection("orders").document(my_order.get(i));
                                                final int finalI = i;
                                                order_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                        @Nullable FirebaseFirestoreException e) {
                                                        if (e != null) {
                                                            Log.w(TAG, "Listen failed.", e);
                                                            return;
                                                        }

                                                        if (snapshot != null && snapshot.exists()) {
                                                            order_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    Order current_order = new Order();
                                                                    current_order = documentSnapshot.toObject(Order.class);
                                                                    if (current_order.isIs_sold() && current_order.getSoldnotify()!= 1) {
                                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                        Intent intent = new Intent(OrderDetail.this, Order.class);
                                                                        PendingIntent ma = PendingIntent.getActivity(OrderDetail.this, 0, intent, 0);
                                                                        Notification notification = new NotificationCompat.Builder(OrderDetail.this, "ItemSold")
                                                                                .setContentTitle("UniTrade:")
                                                                                .setContentText("Your order " + current_order.getItem_title() + "is confirmed by the seller")
                                                                                .setWhen(System.currentTimeMillis())
                                                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                                .setAutoCancel(true)
                                                                                .setContentIntent(ma)
                                                                                .build();

                                                                        manager.notify(1, notification);
                                                                        order_doc.update("soldnotify",1);
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Log.d(TAG, "Current data: null");
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        //result[0] = (String[])document.getData().get("my_items");
                                        Log.e(TAG, "my order list found");

                                    } else {
                                        Log.e(TAG, "my order list not found");
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "No such document...");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        Button return_button = (Button)findViewById(R.id.order_detail_previous_page);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetail.this, OrderList.class));

            }
        });

        /***********************************************
         * check if current item status is  "is_sold". if yes, then show "write comment" btn. if not, invisible btn.
         ***********************************************/
        final Button write_comment = (Button) findViewById(R.id.write_comment);

        write_comment.setVisibility(View.INVISIBLE);

        final DocumentReference profileDocRef = db.collection("orders").document(order_ID);
        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Order current_order = document.toObject(Order.class);
                    boolean is_sold = current_order.isIs_sold();
                    if (is_sold && !current_order.isCommented()) {
                        write_comment.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        write_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetail.this, CommentPage.class));
            }
        });




        /**********************
         * click "Trade Method Textview" to change the trading method "
         ***********************/


        method.setClickable(true);
        method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pop-up dialog to ask user to choose new trading method.
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrderDetail.this);
                builder.setTitle("Notice:");
                builder.setMessage("Your want to change your trading method to:  ");
                builder.setCancelable(true);

                // user choose "Accepted" button:
                builder.setPositiveButton(
                        "Face to Face",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(OrderDetail.this, "You choose Face to Face!",
                                        Toast.LENGTH_SHORT).show();
                                //Todo: back-end check is "Face to Face" same as the old trading method. if yes, do nothing. if no, update back-end with new trading method.
                                db.collection("orders").document(item_doc.getId())
                                        .update("methodpending", 1);

                            }
                        });

                //user choose "Online Payment" button:
                builder.setNegativeButton(
                        "Online Payment",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(OrderDetail.this, "You choose online Payment!",
                                        Toast.LENGTH_SHORT).show();
                                //Todo: back-end check is "Online Payment" same as the old trading method. if yes, do nothing. if no, update back-end with new trading method.
                                db.collection("orders").document(item_doc.getId())
                                        .update("methodpending", 2);

                            }
                        });
                builder.show();
                //front-end functionality ends here.

            }
        });


    }
}
