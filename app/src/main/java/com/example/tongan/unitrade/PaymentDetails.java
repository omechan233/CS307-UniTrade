package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class PaymentDetails extends AppCompatActivity {

    TextView txtId, txtAmount, txtStatus;
    Button back_to_order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_detail);

        txtId = (TextView)findViewById(R.id.txtId);
        txtAmount = (TextView)findViewById(R.id.txtAmount);
        txtStatus = (TextView)findViewById(R.id.txtStatus);
        back_to_order = (Button) findViewById(R.id.backToOrder);

        //get Intent
        Intent intent = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText("$" + paymentAmount);
            back_to_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PaymentDetails.this, OrderList.class));

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
