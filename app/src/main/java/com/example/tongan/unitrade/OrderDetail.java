package com.example.tongan.unitrade;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        TextView itemName = (TextView)findViewById(R.id.item_name_order_detail);
        TextView price = (TextView)findViewById(R.id.price_order_detail);
        TextView time = (TextView)findViewById(R.id.time_order_detail);
        TextView method = (TextView)findViewById(R.id.trade_order_detail);
        TextView seller = (TextView)findViewById(R.id.seller_order_detail);
        TextView status = (TextView)findViewById(R.id.status_order_detail);

        //get info from backend here
        String itemName_String = "Item Name : " /*+ get name from backend*/;
        String price_String = "Price : " /*+ get price from backend*/;
        String time_String = "Order Time : " /*+ get time from backend*/;
        String method_String = "Trade Method : " /*+ get method from backend*/;
        String seller_String = "Seller Name : " /*+ get seller from backend*/;
        String status_String = "Order Status : " /*+ get status from backend*/;

        itemName.setText(itemName_String);
        price.setText(price_String);
        time.setText(time_String);
        method.setText(method_String);
        seller.setText(seller_String);
        status.setText(status_String);

        Button return_button = (Button)findViewById(R.id.order_detail_previous_page);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
