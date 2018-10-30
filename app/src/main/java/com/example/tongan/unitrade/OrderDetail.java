package com.example.tongan.unitrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class OrderDetail extends AppCompatActivity {
    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        String order_ID = shared.getString("order_ID", "");

        System.out.println("in order detail, order od is "+ order_ID);

        final TextView itemName = (TextView)findViewById(R.id.item_name_order_detail);
        final TextView price = (TextView)findViewById(R.id.price_order_detail);
        final TextView time = (TextView)findViewById(R.id.time_order_detail);
        final TextView method = (TextView)findViewById(R.id.trade_order_detail);
        final TextView seller = (TextView)findViewById(R.id.seller_order_detail);
        final TextView status = (TextView)findViewById(R.id.status_order_detail);

        //get info from backend here
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference item_doc = db.collection("orders").document(order_ID);
        item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Order current_order = documentSnapshot.toObject(Order.class);
                String itemName_String = "Item Name : " + current_order.getItem_price();
                String price_String = "Price : " + current_order.getItem_price();/*+ get price from backend*/;

                Timestamp time_stamp = current_order.getOrder_time();

                //format post date
                SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                format.setTimeZone(TimeZone.getTimeZone("EDT"));
                String posted_time = "Posted: " +format.format(time_stamp.toDate());


                String time_String = "Order Time : " + posted_time; /*+ get time from backend*/;
                String method_String = "Trade Method : ";


                if(current_order.getFace_to_face()) {
                    method_String += "Face to face";
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
            }
        });

        Button return_button = (Button)findViewById(R.id.order_detail_previous_page);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button write_comment = (Button) findViewById(R.id.write_comment);
        write_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderDetail.this, CommentPage.class));

            }
        });

    }
}
