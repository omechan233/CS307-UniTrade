package com.example.tongan.unitrade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Purchase extends AppCompatActivity {
    SharedPreferences shared;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_purchase);


        super.onCreate(savedInstanceState);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        Button submit = (Button) findViewById(R.id.purchase_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId = radioGroup.getCheckedRadioButtonId();
                System.out.println(selectId);
                radioButton = (RadioButton) findViewById(selectId);
                if (radioButton.getText().equals("Face-to-Face")){
                    Toast.makeText(Purchase.this, "Submit Success! You choose "+ radioButton.getText(),Toast.LENGTH_LONG).show();
                    //Todo: store current user's payment method (face-to-face)in database

                }else {
                    Toast.makeText(Purchase.this, "Submit Success! You choose "+radioButton.getText(), Toast.LENGTH_LONG).show();
                    //Todo: store current user's payment method (Online)in database

                }
            }
        });

        Button gotoOrder = (Button) findViewById(R.id.purchase_GoToOrder);
        gotoOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Purchase.this, OrderList.class));

            }
        });













    }
}
