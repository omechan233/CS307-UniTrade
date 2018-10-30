package com.example.tongan.unitrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CommentPage extends AppCompatActivity {
    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);


    }
}
