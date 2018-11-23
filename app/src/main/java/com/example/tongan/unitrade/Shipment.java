package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Shipment extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipment);

        Button next = (Button) findViewById(R.id.next_btn);




        //Todo: save the information in DB.

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name_editText = (EditText) findViewById(R.id.name_editText);
                EditText add1_editText = (EditText) findViewById(R.id.address1_input);
                EditText add2_editText = (EditText) findViewById(R.id.address2_input);
                EditText state_editText = (EditText) findViewById(R.id.state_input);
                EditText zipCode_editText= (EditText) findViewById(R.id.zip_input);
                EditText phoneNumber_editText = (EditText) findViewById(R.id.phone_input);


                final String name = name_editText.getText().toString();
                final String add1 = add1_editText.getText().toString();
                final String add2 = add2_editText.getText().toString();
                final String state = state_editText.getText().toString();
                final String zipCode = zipCode_editText.getText().toString();
                final String phoneNumber = phoneNumber_editText.getText().toString();

                //input check
                if (TextUtils.isEmpty(name)
                        || TextUtils.isEmpty(add1)
                        || TextUtils.isEmpty(add2)
                        || TextUtils.isEmpty(state)
                        || TextUtils.isEmpty(zipCode)
                        || TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(getBaseContext(), "Please fill all blanks!", Toast.LENGTH_LONG).show();

                }
                else {
                    //direct to PayPal API page.
                    startActivity(new Intent(Shipment.this, Paypal.class));
                }







            }
        });
    }
}
