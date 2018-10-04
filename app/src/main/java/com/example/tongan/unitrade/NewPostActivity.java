package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

public class NewPostActivity extends AppCompatActivity {
    SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        shared=getSharedPreferences("app", Context.MODE_PRIVATE);

        Button cancel = (Button) findViewById(R.id.cancel_btn);
        Button submit = (Button) findViewById(R.id.submit_btn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPostActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText itemName_edit = (EditText) findViewById(R.id.item_name_input);
                EditText price_edit = (EditText) findViewById(R.id.price_input);
                EditText description_edit = (EditText) findViewById(R.id.desc_input);
                EditText category_edit = (EditText) findViewById(R.id.category_input);

                //get input in edittext
                Date currentTime = Calendar.getInstance().getTime();
                String itemName = itemName_edit.getText().toString();
                String description = description_edit.getText().toString();
                //String username = "";
                String postedtime =currentTime.toString();
                String address ="[not implemented yet]";
                String category = category_edit.getText().toString();
                Double price = -1.0;

                // 0 for currently unavailable
                // 1 for available
                // 2 for someone bought it
                int status = 1;

                //get username by email
                Functions f1 = new Functions();
                String email=shared.getString("email","");

                Functions f = new Functions();

                //check if the user input is empty
                if (!itemName.equals("") && !description.equals("") && !price_edit.getText().toString().equals("")) {

                    price = Double.parseDouble(price_edit.getText().toString());

                    int ret = f.create_post(itemName,email,postedtime,price,category,address,description,status);

                    //back to homepage
                    Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewPostActivity.this, HomePageActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getBaseContext(), "Input cannot be empty!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
