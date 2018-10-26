package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationPage extends AppCompatActivity {
    SharedPreferences shared;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        shared=getSharedPreferences("app", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        Button notifi_back_btn = (Button) findViewById(R.id.notifi_back);
        final Switch notifi_switch = (Switch) findViewById(R.id.notifi_switch);
        final Switch notifi_itemSold = (Switch) findViewById(R.id.notifi_itemSold_switch);
        final Switch notifi_changeMethod = (Switch) findViewById(R.id.notifi_changeMethod_switch);


        final String email=shared.getString("email","");
        final Functions f = new Functions();



        //use backend function to get NotificationPage number
        //if int == 1, set switch.checked() = true;
        //if int == 0, set switch.checked() = false;
        final DocumentReference userDoc = db.collection("users").document(email);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    int notification = doc.getLong("notification").intValue();
                    if(notification == 0){
                        notifi_switch.setChecked(false);
                    }
                    else{
                        notifi_switch.setChecked(true);
                    }
                }
            }
        });

        notifi_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationPage.this, SettingActivity.class);
                startActivity(intent);
            }
        });



        notifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println(email);
                if (isChecked){
                    f.change_notification(email, 1);
                    Toast.makeText(getBaseContext(), "NotificationPage on!", Toast.LENGTH_LONG).show();

                }
                else {
                    f.change_notification(email, 0);
                    Toast.makeText(getBaseContext(), "NotificationPage off!", Toast.LENGTH_LONG).show();
                }

            }
        });

        notifi_itemSold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo:back-end change the ItemSold Notify status to 1;
                    Toast.makeText(getBaseContext(), "Item Sold NotificationPage on!", Toast.LENGTH_LONG).show();

                }
                else {
                    //Todo:back-end change the ItemSold Notify status to 0;
                    Toast.makeText(getBaseContext(), "Item Sold NotificationPage off!", Toast.LENGTH_LONG).show();


                }
            }
        });

        notifi_changeMethod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo:back-end change the ChangeMethod Notify status to 1;
                    Toast.makeText(getBaseContext(), "ChangeMethod NotificationPage on!", Toast.LENGTH_LONG).show();

                }
                else {
                    //Todo:back-end change the ChangeNotify Notify status to 0;
                    Toast.makeText(getBaseContext(), "ChangeMethod NotificationPage off!", Toast.LENGTH_LONG).show();


                }
            }
        });

        Button send_page = (Button) findViewById(R.id.notify_gotoSendpage);
        send_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationPage.this, SendNotification.class);
                startActivity(intent);
            }
        });




    }
}