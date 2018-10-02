package com.example.tongan.unitrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

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

                //get input in edittext
                String itemName = itemName_edit.getText().toString();
                String description = description_edit.getText().toString();
                Double price = -1.0;

                //check if the user input is empty
                if (!itemName.equals("") && !description.equals("") && !price_edit.getText().toString().equals("")) {

                    price = Double.parseDouble(price_edit.getText().toString());
                    //todo : store the itemName, description and price to database here

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
