package com.example.tongan.unitrade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tongan.unitrade.Config.Config;
import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class Paypal extends AppCompatActivity {
    private static final String TAG = "Paypal page";
    public static final int PAYPAL_REQUEST_CODE = 7171;
    private SharedPreferences sharedPreferences;
    private final Functions f = new Functions();

    private static PayPalConfiguration config;
    Button btnPayNow;
    EditText edtAmount;
    String amount = "";

    @Override
    protected  void onDestroy(){
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paypal);
        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);
        String temp_email = sharedPreferences.getString("seller_email", "");

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("Buyer's PayPal email is " + temp_email);

        //String abc = new Config(temp_email).getClientID();
        Config configg = new Config(temp_email);
        String abc = configg.getClientID();
        System.out.println("Buyer's PayPal abc is " + abc);

        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(abc);



        //Start PayPal Service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


        btnPayNow = (Button) findViewById(R.id.btnPayNow);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        amount = sharedPreferences.getString("item_price","");
        edtAmount.setText(amount);
        edtAmount.setTextIsSelectable(false);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

    }

    private void processPayment() {
        //Todo: get item price from DB.

        //amount = edtAmount.getText().toString();

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
                "Trading in UniTrade", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        SharedPreferences shared;

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String email = shared.getString("email","");
        final String itemid = shared.getString("itemid","");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null){
                    try{
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );
                        /**
                         * create order
                         */
                        DocumentReference item_doc = db.collection("items").document(itemid);
                        item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                Item item = documentSnapshot.toObject(Item.class);
                                if (item.getStatus() == 2) {
                                    Toast.makeText(Paypal.this, "Item was already sold" , Toast.LENGTH_LONG).show();
                                }
                                else if(item.getSeller_name().equals(email)){
                                    Toast.makeText(Paypal.this, "You cannot buy your own item" , Toast.LENGTH_LONG).show();
                                }
                                else if (item.getStatus() == 0) {
                                    Toast.makeText(Paypal.this, "Item is not available. ", Toast.LENGTH_LONG).show();
                                }
                                else {
                                        Timestamp orderTime = Timestamp.now();
                                        f.create_order(email, item.getSeller_name(), itemid, orderTime, item.getPrice(), item.getTitle(), false, 0,false, item.getItem_image());
                                        f.changeItemStatusToBought(itemid);

                                    /**************************************
                                     * PayPal test page
                                     ***************************************/


                                }
                            }
                        });

                        System.out.println("888888888888888888888888888888888888888888888888888888888888888");

//                        Query query =  db.collection("orders").whereEqualTo("item_ID",itemid);
                        db.collection("orders")
                                .whereEqualTo("item_ID", itemid)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                System.out.println("sssssssssssssssssssss");
                                                //final DocumentReference order_doc = db.collection("orders").document(document.getId());
                                                //order_doc.update("is_paid",true);
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();

        }

    }
}