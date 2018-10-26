package com.example.tongan.unitrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class OrderDetail extends AppCompatActivity {
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String email = shared.getString("email", "");
        final String itemid = shared.getString("itemid", "");
        final String item_id = itemid;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);


    }

}
