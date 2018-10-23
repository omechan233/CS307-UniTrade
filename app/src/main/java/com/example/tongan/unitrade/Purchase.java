package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Purchase extends AppCompatActivity {
    SharedPreferences shared;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String useremail = shared.getString("email","");
        final String itemid = shared.getString("itemid","");
        final String orderid = shared.getString("orderid","");
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


                boolean face_to_face = false;

                if (radioButton.getText().equals("Face-to-Face")) {
                    face_to_face = true;
                }

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference item_doc = db.collection("items").document(itemid);
                final boolean finalFace_to_face = face_to_face;
                item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Item item = documentSnapshot.toObject(Item.class);
                        Date date = new Date();
                        String time = date.toGMTString();
                        Functions f = new Functions();
                        f.create_order(useremail,itemid, time, item.getPrice() ,item.getTitle(), finalFace_to_face);

                        DocumentReference order_doc = db.collection("orders").document(orderid);

                        if(finalFace_to_face){
                            Toast.makeText(Purchase.this, "Submit Success! You choose "+ radioButton.getText(),Toast.LENGTH_LONG).show();
                            f.changeOrderTypeFTF(orderid);
                        }else{
                            Toast.makeText(Purchase.this, "Submit Success! You choose "+radioButton.getText(), Toast.LENGTH_LONG).show();
                            f.changeOrderTypeOnline(orderid);
                        }
                    }
                });
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
