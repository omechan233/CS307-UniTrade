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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Purchase extends AppCompatActivity {
    private SharedPreferences shared;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private final Functions f = new Functions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String useremail = shared.getString("email","");
        final String itemid = shared.getString("itemid","");
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

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference item_doc = db.collection("items").document(itemid);
                final boolean finalFace_to_face = face_to_face;
                item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Item item = documentSnapshot.toObject(Item.class);
                        if (item.getStatus() == 2) {
                            Toast.makeText(Purchase.this, "Item was already sold" + radioButton.getText(), Toast.LENGTH_LONG).show();
                        }
                        else if(item.getSeller_name().equals(useremail)){
                            Toast.makeText(Purchase.this, "You cannot buy your own item" + radioButton.getText(), Toast.LENGTH_LONG).show();
                        }
                        else if (item.getStatus() == 0) {
                            Toast.makeText(Purchase.this, "Item is not available. " + radioButton.getText(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Timestamp orderTime = Timestamp.now();
                            f.create_order(useremail,item.getSeller_name(), itemid, orderTime, item.getPrice(), item.getTitle(), finalFace_to_face,0);
                            f.changeItemStatusToBought(itemid);
                            Toast.makeText(Purchase.this, "Submit Success! You choose " + radioButton.getText(), Toast.LENGTH_LONG).show();

                            finish();

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
